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
package org.onecmdb.core.utils.transform.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.onecmdb.core.utils.transform.ANameObject;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IInstance;

public class JDBCRow extends ANameObject implements IInstance {

	private DataSet ds;
	private String localId;
	private List<String> columnNames = new ArrayList<String>();
	private HashMap<String, Object> dataMap = new HashMap<String, Object>();
	private Vector<Object> dataVector = new Vector<Object>();
	private String template;
	private boolean autoCreate = true;
	
	public DataSet getDataSet() {
		return(this.ds);
	}

	public void setLocalId(String id) {
		this.localId = id;
	}
	
	public String getLocalID() {
		return(localId);
	}

	public void setDataSet(DataSet dataSet) {
		this.ds = dataSet;
	}

	public void close() throws IOException {
	}

	public void reset() throws IOException {
	}
	
	@Override
	public IInstance clone() throws CloneNotSupportedException {
		return((IInstance)super.clone());
	}
	public List<String> getColumnNames() {
		return(columnNames);
	}
	public Object getCol(String name) {
		return(dataMap.get(name));
	}
	
	public Object getCol(int offset) {
		if (dataVector.size() <= offset) {
			return(null);
		}
		return(dataVector.get(offset));
	}
	
	public void addCol(int col, Object data) {
		//dataMap.put(name, data);
		if (dataVector.size() <= col) {
			dataVector.setSize(col+1);
		}
		dataVector.set(col, data);
	}
	
	public void addCol(int col, String name, Object data) {
		addCol(col, data);
		if (name != null) {
			columnNames.add(name);
			dataMap.put(name, data);
		}
	}

	public String getTemplate() {
		return(this.template);
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public boolean isAutoCreate() {
		return(autoCreate );
	}
	
	public void setAutoCreate(boolean value) {
		this.autoCreate = value;
	}



}
