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

public class RFCModifyAttributeReferenceType extends RFC {
	private String newReferenceTypeAlias;

	private String oldReferenceTypeAlias;

	public String getNewReferenceTypeAlias() {
		return newReferenceTypeAlias;
	}

	public void setNewReferenceTypeAlias(String referenceTypeAlias) {
		this.newReferenceTypeAlias = referenceTypeAlias;
	}

	public void setNewReferenceType(IType type) {
		if (type == null) {
			return;
		}
		this.newReferenceTypeAlias = type.getAlias();
	}

	public String getOldReferenceTypeAlias() {
		return oldReferenceTypeAlias;
	}

	public void setOldReferenceTypeAlias(String oldReferenceTypeAlias) {
		this.oldReferenceTypeAlias = oldReferenceTypeAlias;
	}

	public String getSummary() {
		return("Modify reference type on '" + getTargetInfo() + "' to '" + getNewReferenceTypeAlias() + "");
	}
	
	public String toString() {
		return ("RFC - ModifyAttributeRefType <newRefType=" + this.newReferenceTypeAlias + ">  - " + super.toString());

	}

}
