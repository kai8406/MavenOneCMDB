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

import org.onecmdb.core.internal.ccb.AttributeModifiable;

/**
 * Add a new derived attribute to the Ci.
 * 
 * @author niklas
 * 
 */
public class RFCAddAttribute extends AttributeModifiable {

	private String alias;
	private Long derivedAttributeId;
	
	public RFCAddAttribute() {
	}

	
	public Long getDerivedAttributeId() {
		return derivedAttributeId;
	}


	public void setDerivedAttributeId(Long derivedAttributeId) {
		this.derivedAttributeId = derivedAttributeId;
	}


	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return (this.alias);
	}

	public String getSummary() {
		return("Add attribute '" + getTargetInfo() + "'");
	}
	
	public String toString() {
		return ("RFC - AddAttribute <alias=" + alias + "> <derivedId=" + this.derivedAttributeId +"> - " + super.toString());
	}

}
