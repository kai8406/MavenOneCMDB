/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.core.utils.transform.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.transform.IDataSource;

public class ExcelDataSource implements IDataSource {

	
	List<URL> urls = new ArrayList<URL>();
	private long headerLines = 2;
	private boolean loaded;
	private ArrayList<String[]> lines = new ArrayList<String[]>();
	private String textDelimiter;
	private String colDelimiter;
	private String rootPath;
	
	private Log log = LogFactory.getLog(this.getClass());
	private String sheet;
	private ArrayList<String[]> headers = new ArrayList<String[]>();
	private HashMap<String, Integer> headerMap = new HashMap<String, Integer>();
	private int headerRow = -1;
	private int columns = 0;
	

	public String getColDelimiter() {
		return(this.colDelimiter);
	}
	
	
	
	public void setColDelimiter(String colDelimiter) {
		this.colDelimiter = colDelimiter;
	}



	public String getRootPath() {
		return rootPath;
	}



	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}



	public String getTextDelimiter() {
		return textDelimiter;
	}

	public void setTextDelimiter(String textDelimiter) {
		this.textDelimiter = textDelimiter;
	}

	public void setURLs(List<URL> urls) {
		for (URL url : urls) {
			this.addURL(url);
		}
	}
	
	public void setUrl(URI sourceURI) {
		try {
			urls.add(sourceURI.toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addURL(URI sourceURI) {
		try {
			urls.add(sourceURI.toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addURL(URL sourceURL) {
		urls.add(sourceURL);
	}
	
	public void setHeaderLines(long n) {
		this.headerLines = n;
	}
	
	public long getHeaderLines()  {
		return(this.headerLines);
	}
	
	
	public synchronized List<String[]> load() throws IOException {
		if (loaded) {
			return(lines);
		}
		int lineIndex = 0;
		for (URL url : urls) {
			// Remove Options.
			String spec = url.toExternalForm();
			int index = spec.indexOf("?");
			if (index > 0) {
				spec = spec.substring(0, index);
			}
			URL nUrl = new URL(spec);
			if (rootPath != null) {
				nUrl = new URL(nUrl.getProtocol(), nUrl.getHost(), nUrl.getPort(), rootPath + "/" + nUrl.getFile());
			}
			InputStream in = nUrl.openStream();
			Workbook workbook = null;
			try {
				workbook = Workbook.getWorkbook(in);
			String query = url.getQuery();
			Sheet sheet = getSheet(workbook, query, nUrl.toExternalForm());
			
			log.info("Excel[" + url + "] using sheet " + sheet.getName());
			columns  = sheet.getColumns();
				for (int row = 0; row < sheet.getRows(); row++) {
					String rowData[] = new String[sheet.getColumns()];
					for (int col = 0; col < sheet.getColumns(); col++) {
						Cell cell = sheet.getCell(col, row);
						String text = cell.getContents();
						rowData[col] = text;
					}
					if (row < headerLines) {
						headers.add(rowData);
					} else {
						lines.add(rowData);
					}
				}
			} catch (BiffException e1) {
				e1.printStackTrace();
				throw new IOException("Problem open Execl file '" + url + "' : " + e1.getMessage());
			} catch (IOException de) {
				IOException e = new IOException("Parse error in <" + url.toExternalForm() + ">, ");
				e.initCause(de);
				throw e;
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
		loaded = true;
		// Update header name mapping...
		String headers[] = getHeaderData();
		for (int i = 0; i < headers.length; i++) {
			headerMap.put(headers[i], i);
		}
		return(lines);
	}
	
	
	private Sheet getSheet(Workbook workbook, String query, String url) {
		Sheet selectedSheet = null;
		String sheetName = "0";
		if (this.sheet != null) {
			sheetName = this.sheet;
			selectedSheet = workbook.getSheet(sheetName);
			if (selectedSheet == null) {
				// Try to convert to number.
				try {
					int sheetN = Integer.parseInt(this.sheet);
					selectedSheet = workbook.getSheet(sheetN);
				} catch (Throwable t) {
					// Ignore...
				}
			}
		}
		if (selectedSheet == null) {
			//selectedSheet = workbook.getSheet(0);
			if (query != null) {
				String options[] = query.split("&");
				// Default is first sheet.
				if (options.length > 0) {
					for (String option : options) {
						if (option.startsWith("sheet=")) {
							sheetName = option.substring("sheet=".length());
							selectedSheet = workbook.getSheet(sheetName);
						}
					}
				}
			}
		}
		if (selectedSheet == null) {
			String names[] = workbook.getSheetNames();
			StringBuffer availSheets = new StringBuffer();
			for (String name : names) {
				if (availSheets.length() > 0) {
					availSheets.append(", ");
				}
				availSheets.append(name);
				
			}
			throw new IllegalArgumentException("Sheet " + sheetName + " don't match [" + availSheets.toString() + "] in excel file " + url);
		}
		return(selectedSheet);	
	}



	/*
	public String getHeaderData() {
		return(this.headers.get(this.headers.size()-1));
	}
	*/
	
	public void close() throws IOException {
		// Already closed.
	}

	public void reset() throws IOException {
		// Opens every time.
		
	}



	public List<String[]> getRows() throws IOException {
		return(load());
	}



	public void setSheet(String sheet) {
		this.sheet = sheet;
	}



	public String[] getHeaderData() {
		String[] h = new String[columns];
		if (this.headerRow > 0 && this.headerRow < this.headers.size()) {
			h = this.headers.get(this.headerRow);
		} else if (this.headerLines > 0 && (this.headers.size() > this.headerLines)) {
			 h = this.headers.get((int)this.headerLines-1);
		} else {
			if (this.headers.size() > 0) {
				h = this.headers.get(0);
			}
		}
		for (int i = 0; i < h.length; i++) {
			if (h[i] == null || h[i].length() == 0) {
				h[i] = "empty-" + i;
			}
		}
		return(h);
	}


	
	public HashMap<String, Integer> getHeaderMap() {
		return headerMap;
	}
	
	public int getHeaderIndex(String name) {
		Integer i = headerMap.get(name);
		if (i == null) {
			return(0);
		}
		return(i);
	}



	public void setHeaderRow(int row) {
		this.headerRow  = row;
		
	}

	
}
