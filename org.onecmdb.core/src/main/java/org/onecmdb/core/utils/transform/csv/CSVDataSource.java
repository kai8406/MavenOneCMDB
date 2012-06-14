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
package org.onecmdb.core.utils.transform.csv;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.transform.IDataSource;

public class CSVDataSource implements IDataSource {

	
	List<URL> urls = new ArrayList<URL>();
	private long headerLines = 2;
	private List<String> headers = new ArrayList<String>();
	private boolean loaded;
	private ArrayList<String> lines;
	private String textDelimiter;
	private String colDelimiter;
	private HashMap<String, Integer> headerMap = new HashMap<String, Integer>();
	
	private Log log = LogFactory.getLog(this.getClass());
	private String rootPath;
	private int headerRow = -1; 

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getColDelimiter() {
		return(this.colDelimiter);
	}
	
	
	
	public void setColDelimiter(String colDelimiter) {
		this.colDelimiter = colDelimiter;
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
	
	public synchronized List<String> load() throws IOException {
		if (loaded) {
			return(lines);
		}
		
		lines = new ArrayList<String>();
		int lineIndex = 0;
		for (URL url : urls) {
			URL nUrl = url;
			if (rootPath != null) {
				nUrl = new URL(nUrl.getProtocol(), nUrl.getHost(), nUrl.getPort(), rootPath + "/" + nUrl.getFile());
			}
			InputStream in = nUrl.openStream();
			try {
				LineNumberReader lin = new LineNumberReader(new InputStreamReader(in));
				boolean eof = false;
				while (!eof) {
					
					String line = lin.readLine();
					if (line == null) {
						eof = true;
						continue;
					}
					// Check if line is not terminated due to nl in fields.
					line = handleEndOfLine(lin, line);
					lineIndex++;
					if (lineIndex < headerLines) {
						log.debug("Add Header:" + line);
						headers.add(line);
						continue;
					}
					log.debug("Add Line:[" + lineIndex + "]" + line);
					lines.add(line);
				}
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
		
		String header = getHeaderData();
		if (headers != null) {
			String headers[] = header.split(getColDelimiter());
			for (int i = 0; i < headers.length; i++) {
				headerMap.put(headers[i], i);
			}
		}
		return(lines);
	}
	
	private String handleEndOfLine(LineNumberReader lin, String line) throws IOException {
		if (this.textDelimiter == null || this.textDelimiter.length() == 0) {
			return(line);
		}
		int count = 0;
		for (char c : line.toCharArray()) {
			if (c == this.textDelimiter.charAt(0)) {
				count++;
			}
		}
		// If we have a even number of textDel then we are fine.
		if ((count % 2) == 0) {
			return(line);
		}
		
		// Else read next line.
		String nextLine = lin.readLine();
		if (nextLine == null) {
			return(line);
		}
		line = line + nextLine;
		
		return(handleEndOfLine(lin,line));
	}

	public String getHeaderData() {
		String h = "";
		if (this.headerRow > 0 && this.headerRow < this.headers.size()) {
			h = this.headers.get(this.headerRow);
		} else if (this.headerLines > 0 && (this.headers.size() > this.headerLines)) {
			 h = this.headers.get((int)this.headerLines-1);
		} else {
			if (this.headers.size() > 0) {
				h = this.headers.get(0);
			}
		}
		return(h);
	}
	
	public void close() throws IOException {
		// Already closed.
	}

	public void reset() throws IOException {
		// Opens every time.
		
	}

	public int getHeaderIndex(String name) {
		Integer i = headerMap.get(name);
		if (i == null) {
			return(-1);
		}
		return(i);
	}

	public HashMap<String, Integer> getHeaderMap() {
		return(headerMap);
	}

	public void setHeaderRow(int row) {
		this.headerRow = row;
	}

	
}
