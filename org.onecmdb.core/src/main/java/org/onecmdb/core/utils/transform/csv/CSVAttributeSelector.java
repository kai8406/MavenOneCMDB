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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.transform.AAttributeSelector;
import org.onecmdb.core.utils.transform.ANameObject;
import org.onecmdb.core.utils.transform.IAttributeSelector;
import org.onecmdb.core.utils.transform.IAttributeValue;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.IValueTransform;
import org.onecmdb.core.utils.transform.TextAttributeValue;

public class CSVAttributeSelector extends AAttributeSelector {
	private int colIndex;
	private String colName;
	private String colDel;
	private Log log = LogFactory.getLog(this.getClass()); 
	
	public CSVAttributeSelector() {
	}
	
	public CSVAttributeSelector(String name, int col, boolean naturalKey) {
		setColIndex(col);
		setName(name);
		setNaturalKey(naturalKey);
	}

	public CSVAttributeSelector(String name, IValueTransform transform, int col, String colDel, boolean naturalKey) {
		this(name, col, naturalKey);
		setColDelimiter(colDel);
		setTransform(transform);
	}

	public CSVAttributeSelector(String name, int col, String colDel, boolean naturalKey) {
		this(name, col, naturalKey);
		setColDelimiter(colDel);
	}
	
	
	public void setColDelimiter(String colDel) {
		this.colDel = colDel;
	}
	
	public int getColIndex() {
		return colIndex;
	}
	
	public void setColIndexStr(String col) {
		this.colIndex = Integer.parseInt(col);
	}

	public void setColIndex(int col) {
		this.colIndex = col;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public IAttributeValue getAttribute(IInstance instance) throws IOException {
		if (instance instanceof CSVRow) {
			CSVRow csv = (CSVRow)instance;
			String text = null;
			if (colDel == null) {
				colDel = csv.getColTextDelimiter();
			}
			try {
				if (colName != null) {
					text = csv.getCol(colName);
				} else {
					text = csv.getCol(colIndex);
				}
				if (colDel != null) {
					if (text.startsWith(colDel)) {
						text = text.substring(colDel.length());
					}
					if (text.endsWith(colDel)) {
						text = text.substring(0, text.length()-colDel.length());
					}
					/*
					if (text.startsWith(colDel) && text.endsWith(colDel)) {
						// Remove colDel.
						int len = colDel.length();
						if (text.length() > (len*2)) {
							text = text.substring(len, text.length()-len);
						} else {
							text = "";
						}
					}
					*/
				}
			} catch (Throwable e) {
				log.error("Exception in attribute selector name=" + getName() + ", colName=" + colName + ", colIndex=" + getColIndex(), e);
				throw new IOException(e.getMessage());
			}
			if (text != null) {
				text = text.trim();
			}
			
			// System.out.println("[" + instance.getLocalID() +"]" + getName() + "=" + text);
			
			if (getTransform() != null) {
				text = getTransform().transform(this, text);
			}
			return(new TextAttributeValue(this, text));
		}
		return(null);
	}
}
