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

import java.io.IOException;

public class DefaultAttributeValueSelector extends AAttributeSelector {
	
	private String value;
	private boolean complex;
	private String dataSet;

	public DefaultAttributeValueSelector() {
		super();
	}
	
	public DefaultAttributeValueSelector(String name, boolean naturalKey, String value) {
		setName(name);
		setNaturalKey(naturalKey);
		this.value = value;
	}
	
	public DefaultAttributeValueSelector(String name, boolean naturalKey, String value, boolean complex) {
		setName(name);
		setNaturalKey(naturalKey);
		this.value = value;
		this.complex = complex;
	}
	
	public IAttributeValue getAttribute(IInstance row) throws IOException {
		return(new TextAttributeValue(this, this.value, this.complex));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isComplex() {
		return complex;
	}

	public void setComplex(boolean complex) {
		this.complex = complex;
	}
}
