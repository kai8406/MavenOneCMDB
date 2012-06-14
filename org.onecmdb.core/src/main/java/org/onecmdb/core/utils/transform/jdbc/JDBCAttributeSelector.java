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

import org.onecmdb.core.utils.transform.AAttributeSelector;
import org.onecmdb.core.utils.transform.IAttributeValue;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.TextAttributeValue;

public class JDBCAttributeSelector extends AAttributeSelector {

	private String colName;
	private int col;
	
	public JDBCAttributeSelector() {
	}
	
	public JDBCAttributeSelector(String aName, int col, boolean naturalKey) {
		super.setName(aName);
		super.setNaturalKey(naturalKey);
		setCol(col);
	}
	public JDBCAttributeSelector(String aName, int col, boolean naturalKey, String defaultValue) {
		super.setName(aName);
		super.setNaturalKey(naturalKey);
		super.setDefaultValue(defaultValue);
		setCol(col);
	}

	
	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	public void setColString(String col) {
		this.col = Integer.parseInt(col);
	}

	public IAttributeValue getAttribute(IInstance instance) throws IOException {
		if (instance instanceof JDBCRow) {
			JDBCRow row = (JDBCRow)instance;
			Object data = null;
			if (colName != null) {
				data = row.getCol(colName);
			} else {
				data = row.getCol(col);
			} 
			String text = null;
			if (data != null) {
				text = data.toString();
			}
			TextAttributeValue tav = new TextAttributeValue(this, text);
			tav.setDefaultValue(getDefaultValue());
			return(tav);
		}
		return(null);
	}

}
