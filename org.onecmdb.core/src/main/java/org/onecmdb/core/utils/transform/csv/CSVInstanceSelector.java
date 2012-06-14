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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.transform.AInstanceSelector;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IColumnAware;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.IDataSetMatcher;
import org.onecmdb.core.utils.transform.excel.ExcelDataSource;

public class CSVInstanceSelector extends AInstanceSelector {
	private String delimiter = ";";
	private IDataSetMatcher excludeFilter[];
	private IDataSetMatcher includeFilter[];
	private Log log = LogFactory.getLog(this.getClass());
	private int templateCol = -1; 
	
	public CSVInstanceSelector() {
	}
	
	
	public CSVInstanceSelector(String template, String del) {
		setTemplate(template);
		setDelimiter(del);
	}
	
	public CSVInstanceSelector(String template, String del, IDataSetMatcher exclude[], IDataSetMatcher include[]) {
		setTemplate(template);
		setDelimiter(del);
		this.excludeFilter = exclude;
		this.includeFilter = include;
	}
	
	
	public int getTemplateCol() {
		return templateCol;
	}

	public void setTemplateCol(int col) {
		this.templateCol = col;
	}
	
	public void setTemplateColString(String col) {
		this.templateCol  = Integer.parseInt(col);
	}
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public List<IInstance> getInstances(DataSet ds) throws IOException {
		List<IInstance> rows = new ArrayList<IInstance>();
		if (ds.getDataSource() instanceof ExcelDataSource) {
			ExcelDataSource excel = (ExcelDataSource)ds.getDataSource();
			long index = excel.getHeaderLines();
			for (String rowData[] : excel.getRows()) {
				index++;
				if (!isEmptyRow(rowData)) {
					CSVRow row = new CSVRow(ds, rowData, index);
					row.setHeaderMap(excel.getHeaderMap());
					row.setAutoCreate(isAutoCreate());
					if (callFilter(row)) {
						log.debug("Line filtered ok: [" + index + "]" + rowData);
						
						row.setTemplate(findTemplate(row));
						
						rows.add(row);	
					}
				}	
			}
		}
		if (ds.getDataSource() instanceof CSVDataSource) {
			CSVDataSource csv = (CSVDataSource)ds.getDataSource();
			String colDelimiter = csv.getColDelimiter();
			if (colDelimiter == null) {
				colDelimiter = getDelimiter();
			}
			long index = csv.getHeaderLines();
			for (String line : csv.load()) {
				index++;
				if (!isEmptyLine(line, colDelimiter)) {
					CSVRow row = new CSVRow(ds, line, colDelimiter, index, csv.getTextDelimiter());
					row.setHeaderMap(csv.getHeaderMap());
					row.setAutoCreate(isAutoCreate());
					if (callFilter(row)) {
						log.debug("Line filtered ok: [" + index + "]" + line);
						
						row.setTemplate(findTemplate(row));
						
						rows.add(row);	
					}
				}
			}
		}
		
		if (ds.getDataSource() instanceof CSVRow) {
			CSVRow row = (CSVRow)ds.getDataSource();
			row.setTemplate(findTemplate(row));
			rows.add(row);
		}
		return(rows);
	}


	private boolean isEmptyRow(String[] row) {
		if (row == null) {
			return(true);
		}
		boolean empty = true;
		for (String v : row) {
			if (v != null && v.length() > 0) {
				empty = false;
				break;
			}
		}
		return(empty);
	}


	private String findTemplate(CSVRow row) {
		String temp = null;
		if (templateCol >= 0) {
			temp = row.getCol(templateCol);
			if (temp != null) {
				String colDel = row.getColTextDelimiter();
				if (colDel != null) {
					if (temp.startsWith(colDel) && temp.endsWith(colDel)) {
						// Remove colDel.
						int len = colDel.length();
						if (temp.length() > (len*2)) {
							temp = temp.substring(len, temp.length()-len);
						} else {
							temp = null;
						}
					}
				}
			}
		}
		if (temp == null) {
			temp = getTemplate();
			log.debug("Selector " + getTemplate() + " use default Template");
		}
		log.debug("Selector " + getName() + ":" + getTemplate() + " templateCol=" + templateCol + " generated template " + temp);
		return(temp);
	}


	private boolean callFilter(CSVRow row) throws IOException {
		if (this.excludeFilter != null) {
			for (int i = 0; i < this.excludeFilter.length; i++) {
				if (this.excludeFilter[i].match(row)) {
					return(false);
				}
			}
		}
		if (this.includeFilter != null) {
			for (int i = 0; i < this.includeFilter.length; i++) {
				if (this.includeFilter[i].match(row)) {
					return(true);
				}
			}
			return(false);
		}
		return(true);
	}


	private boolean isEmptyLine(String line, String delimiter) {
		String values[] = line.split("\\" + delimiter);
		for (String value : values) {
			if (value != null && value.length() > 0) {
				return(false);
			}
		}
		return(true);
	}


}
