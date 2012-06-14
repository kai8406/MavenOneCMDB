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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.onecmdb.core.utils.transform.ANameObject;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.IInstanceSelector;
import org.onecmdb.core.utils.transform.excel.ExcelDataSource;

public class CSVRow extends ANameObject implements IInstance { 
	
	private String line;
	private String columns[];
	private HashMap<String, Integer> headerMap;
	private DataSet dataSet;
	private long row;
	private String colTextDelimiter;
	private String template;
	private String delimiter;
	private boolean autoCreate = true;
	

	public CSVRow(DataSet dataSet, String columns[], long row) {
		this.columns = columns;
		this.row = row;
		this.dataSet = dataSet;
	}
	
	public CSVRow(DataSet dataSet, String line, String delimiter, long row) {
		this.line = line;		
		this.dataSet = dataSet;
		this.row = row;
		this.delimiter = delimiter;
		this.columns = line.split("\\" + delimiter);
	
	}
	
	public CSVRow(DataSet dataSet, String line, String delimiter, long row, String colTextDel) {
		this.line = line;		
		this.dataSet = dataSet;
		this.row = row;
		this.colTextDelimiter = colTextDel;
		this.delimiter = delimiter;
	
		//this.columns = line.split("\\" + delimiter);
		this.columns = parseColumns(line);
	}
	
	protected String[] parseColumns(String l) {
		String split[] = l.split("\\" + delimiter);
		// Need to check that all column starts with colTextDelimiter....
		if (true) {
			return(split);
		}
		List<String> splitList = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			// Check for empty value.
			if (split[i].length() == 0) {
				splitList.add(split[i]);
				continue;
			}
			if (colTextDelimiter == null || colTextDelimiter.length() == 0) {
				splitList.add(split[i]);
				continue;
			}
			// Check for correct value, no delimiters inside text..
			if (split[i].startsWith(colTextDelimiter) && split[i].endsWith(colTextDelimiter)) {
				splitList.add(split[i]);
				continue;
			}
			// Start of a split.
			if (split[i].startsWith(colTextDelimiter)) {
				String value = split[i];
				// Serach forward..
				for (int j = i+1; j < split.length; j++) {
					value += delimiter + split[j];
					
					if (split[j].endsWith(colTextDelimiter)) {
						i = j-1;
						break;
					}
				}
				splitList.add(value);
			}
			
		}
		return(splitList.toArray(new String[0]));
		
	}

	private int getColumnIndex(String name) {
		if (headerMap != null) {
			Integer col = headerMap.get(name);
			if (col == null) {
				return(-1);
			}
			return(col);
		}
		/*
		if (this.getDataSet().getDataSource() instanceof CSVDataSource) {
			int index  = ((CSVDataSource)this.getDataSet().getDataSource()).getHeaderIndex(name);
			return(index);
		}
		
		if (this.getDataSet().getDataSource() instanceof ExcelDataSource) {
			int index  = ((ExcelDataSource)this.getDataSet().getDataSource()).getHeaderIndex(name);
			return(index);
		}
		*/
		return(-1);
	}

	
	public List<String> getColumnNames() {
		if (this.getDataSet().getDataSource() instanceof CSVDataSource) {
			String line = ((CSVDataSource)this.getDataSet().getDataSource()).getHeaderData();
			String[] headers = parseColumns(line);
			List<String> array = Arrays.asList(headers);
			return(array);
		}
		
		if (this.getDataSet().getDataSource() instanceof ExcelDataSource) {
			String headers[] = ((ExcelDataSource)this.getDataSet().getDataSource()).getHeaderData();
			List<String> array = Arrays.asList(headers);
			return(array);
		}
			
		return(new ArrayList<String>());
	}
	
	public DataSet getDataSet() {
		return(this.dataSet);
	}

	public String getLocalID() {
		return("" + row);
	}

	public IInstanceSelector getInstanceSelector() {
		return(getDataSet().getInstanceSelector());
	}
	
	public String getLine() {
		return(this.line);
	}
	public String getCol(String name) {
		int index = getColumnIndex(name);
		if (index < 0) {
			getColumnIndex(name);
			throw new IllegalArgumentException("Column Name '" + name + "' is not found in source. [" + getColumnNames() + "]");
		}
		return(getCol(index+1));
	}	
	

	public String getCol(int index) {
		if (this.columns.length < index) {
			return("");
			//return("<Index[" + index + "] not valid, row=[" + row + "]>");
		}
		return(this.columns[index-1]);
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;			
	}
	
	public String getTemplate() {
		return(this.template);
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	
	@Override
	public IInstance clone() throws CloneNotSupportedException {
		return ((IInstance)super.clone());
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void reset() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public String[] getColumns() {
		return(this.columns);
	}

	public String getColTextDelimiter() {
		return(this.colTextDelimiter);
	}

	public boolean isAutoCreate() {
		return(autoCreate );
	}
	
	public void setAutoCreate(boolean value) {
		this.autoCreate = value;
	}

	public void setHeaderMap(HashMap<String, Integer> headerMap) {
		this.headerMap = headerMap;
		
	}

	
	
}
