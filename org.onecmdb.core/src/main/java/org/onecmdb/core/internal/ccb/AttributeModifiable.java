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
package org.onecmdb.core.internal.ccb;

import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeReferenceType;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeType;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyMaxOccurs;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyMinOccurs;

public class AttributeModifiable extends CiModifiable implements
		IAttributeModifiable {
	
	public AttributeModifiable() {
		super();
	}
	
	public void setValue(IValue value) {
		RFCModifyAttributeValue rfc = new RFCModifyAttributeValue();
		if (value instanceof ICi) {
			rfc.setNewValueAsAlias(((ICi)value).getAlias());
		} else {
			rfc.setNewValue(value != null ? value.getAsString() : null);
		}
		add(rfc);
	}

	public void setValueAsString(String stringValue) {
		RFCModifyAttributeValue rfc = new RFCModifyAttributeValue();
		rfc.setNewValue(stringValue);
		add(rfc);
	}

	public void setValueType(IType type) {
		RFCModifyAttributeType rfc = new RFCModifyAttributeType();
		rfc.setNewType(type);
		add(rfc);
	}

	public void setValueTypeAlias(String alias) {
		RFCModifyAttributeType rfc = new RFCModifyAttributeType();
		rfc.setNewTypeAlias(alias);
		add(rfc);
	}

	public void setReferenceType(IType type) {
		RFCModifyAttributeReferenceType rfc = new RFCModifyAttributeReferenceType();
		rfc.setNewReferenceType(type);
		add(rfc);
	}

	public void setReferenceTypeAlias(String alias) {
		RFCModifyAttributeReferenceType rfc = new RFCModifyAttributeReferenceType();
		rfc.setNewReferenceTypeAlias(alias);
		add(rfc);
	}

	public void setMaxOccurs(int maxOccurs) {
		RFCModifyMaxOccurs rfc = new RFCModifyMaxOccurs();
		rfc.setNewMaxOccurs(maxOccurs);
		add(rfc);
	}

	public void setMinOccurs(int minOccurs) {
		RFCModifyMinOccurs rfc = new RFCModifyMinOccurs();
		rfc.setNewMinOccurs(minOccurs);
		add(rfc);
	}

}
