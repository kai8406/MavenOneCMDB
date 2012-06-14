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

import org.onecmdb.core.IType;

public class RFCModifyAttributeType extends RFC {

	String oldTypeAlias;

	String newTypeAlias;

	public void setNewType(IType type) {
		if (type == null) {
			this.newTypeAlias = null;
		} else {
			this.newTypeAlias = type.getAlias();
		}
	}

	public void setOldType(IType type) {
		this.oldTypeAlias = type.getAlias();
	}

	public String getNewTypeAlias() {
		return newTypeAlias;
	}

	public void setNewTypeAlias(String newTypeAlias) {
		this.newTypeAlias = newTypeAlias;
	}

	public String getOldTypeAlias() {
		return oldTypeAlias;
	}

	public void setOldTypeAlias(String oldTypeAlias) {
		this.oldTypeAlias = oldTypeAlias;
	}

	/**
	 * {{{ Hibernate setter/Getters!!! Should/Could be done with Hibernate
	 * PropertyAccessor's
	 * 
	 */
	public RFCModifyAttributeType() {
	}

	public String getSummary() {
		return("Modify type on '" + getTargetInfo() + " to '" + getNewTypeAlias() + "'");
	}

	public String toString() {
		return ("RFC - ModifyAttributeType <newType=" + this.newTypeAlias + ">  - " + super.toString());

	}

}
