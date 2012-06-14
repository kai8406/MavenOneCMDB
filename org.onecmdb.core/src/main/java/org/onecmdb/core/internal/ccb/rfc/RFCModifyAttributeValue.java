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

import java.util.Date;

public class RFCModifyAttributeValue extends RFC {

	private String oldValue;

	private String newValue;
	
	private Long longValue;
	private Date dateValue;
	
	private String newValueAsAlias;

	public String getNewValueAsAlias() {
		return (this.newValueAsAlias);
	}

	public void setNewValueAsAlias(String alias) {
		this.newValueAsAlias = alias;
	}

	public void setNewValue(String value) {
		this.newValue = value;
	}

	public void setOldValue(String value) {
		this.oldValue = value;
	}

	public String getNewValue() {
		return (this.newValue);
	}

	public String getOldValue() {
		return (this.oldValue);
	}

	
	public String getSummary() {
		return ("Modify value on '" + getTargetInfo() + "' to '" + 
				(this.newValue != null ? this.newValue : this.newValueAsAlias)  
				+ "' from '" + this.oldValue + "'");
	}

	/**
	 * {{{ Hibernate setter/Getters!!! Should/Could be done with Hibernate
	 * PropertyAccessor's
	 * 
	 */
	public RFCModifyAttributeValue() {
	}

	/*
	 * Hibernate }}}
	 */
	
	public String toString() {
		return ("RFC - ModifyAttributeValue <newValue=" + this.newValue + ", newAliasValue=" + this.newValueAsAlias + ">  - " + super.toString());

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
