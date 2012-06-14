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
package org.onecmdb.core.utils.transform;


public class TextAttributeValue extends AAttributeValue  {
	
	private String text;
	private boolean complex;
	private String defaultValue;

	public TextAttributeValue(IAttributeSelector selector, String value) {
		super(selector);
		this.text = value;
	}
	
	public TextAttributeValue(IAttributeSelector selector, String value, boolean complex) {
		super(selector);
		this.text = value;
		this.complex = complex;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public DataSet getDataSet() {
		return null;
	}

	public String getText() {
		if (this.text == null || this.text.length() == 0) {
			// Check if we have a default value.
			return(this.defaultValue);
		}
		return(this.text);
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isPrimitive() {
		return(true);
	}

	public boolean isComplex() {
		return(complex);
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
