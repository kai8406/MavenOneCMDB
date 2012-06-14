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
package org.onecmdb.core.internal.ccb.rfc;

import java.sql.Date;

public class RFCModifyDerivedAttributeValue extends RFC {

	private String alias;

	private String value;

	private int index;

	private String valueAsAlias;

	private Long longValue;
	private Date dateValue;

	public void modify(String alias, String value) {
		this.setAlias(alias);
		this.setValue(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueAsAlias() {
		return valueAsAlias;
	}

	public void setValueAsAlias(String valueAsAlias) {
		this.valueAsAlias = valueAsAlias;
	}

	public void setAlias(String attributeAlias) {
		this.alias = attributeAlias;
	}

	public String getAlias() {
		return (this.alias);
	}

	public String getValue() {
		return value;
	}

	// {{{
	// Hibernate
	public RFCModifyDerivedAttributeValue() {
		super();
	}

	// }}}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return (this.index);
	}
	public String getSummary() {
		return("Modify derived attribute '" + getAlias() + 
				"'[" + this.index + "]' " +
				"to '" + (this.value == null ? this.valueAsAlias : this.value) +"' " +
				"on '" + getTargetInfo() +"'");
	}

	public String toString() {

		String valueString = "";
		if (this.valueAsAlias != null) {
			valueString = "valueAsAlias=" + this.valueAsAlias;
		} else {
			valueString = "value=" + this.value;
		}

		return ("RFC - ModifyDerivedAttributeValue alias=" + alias + ", " + 
				valueString + ", index=" + this.getIndex() + " " + super
				.toString());
	}

	public void setValueAsLong(Long l) {
		this.longValue = l;
	}
	
	public Date getValueAsDate() {
		return(this.dateValue);
		
	}
	public Long getValueAsLong() {
		return(this.longValue);
		
	}
	
	public void setValueAsDate(Date d) {		
		this.dateValue = d;
	}
}
