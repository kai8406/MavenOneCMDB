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
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.rfc.RFC;
import org.onecmdb.core.internal.ccb.rfc.RFCAddAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDisplayNameExpression;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyIsTemplate;
import org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCNewCi;

public class CiModifiable extends RFC implements ICiModifiable {

	public CiModifiable() {
	}

	public ICiModifiable createOffspring() {
		RFCNewCi rfc = new RFCNewCi();
		add(rfc);
		return (rfc);
	}

	public void setDerivedAttributeValue(String alias, int index, IValue value) {
		RFCModifyDerivedAttributeValue rfc = new RFCModifyDerivedAttributeValue();
		rfc.setAlias(alias);
		if (value != null) {
			if (value instanceof ICi) {
				rfc.setValueAsAlias(((ICi)value).getAlias());
			} else {
				rfc.setValue(value.getAsString());
			}
		} else {
			rfc.setValue(null);
		}
		rfc.setIndex(index);
		add(rfc);
	}

	public IAttributeModifiable createAttribute(String alias, IType type,
			IType refType, int minOccurs, int maxOccurs, IValue value) {
		RFCNewAttribute rfc = new RFCNewAttribute();
		rfc.setAlias(alias);
		rfc.setValueType(type);
		if (refType != null) {
			rfc.setReferenceType(refType);
		}
		rfc.setMinOccurs(minOccurs);
		rfc.setMaxOccurs(maxOccurs);
		if (value != null) {
			rfc.setValue(value);
		}
		add(rfc);
		return (rfc);
	}

	public void setIsBlueprint(boolean value) {
		RFCModifyIsTemplate rfc = new RFCModifyIsTemplate();
		rfc.setNewTemplate(value);
		add(rfc);
	}

	public void setDisplayNameExpression(String name) {
		RFCModifyDisplayNameExpression rfc = new RFCModifyDisplayNameExpression();
		rfc.setNewDisplayNameExpression(name);
		add(rfc);
	}

	public void setAlias(String name) {
		RFCModifyAlias rfc = new RFCModifyAlias();
		rfc.setNewAlias(name);
		add(rfc);
	}

	public IAttributeModifiable addAttribute(String alias) {
		RFCAddAttribute rfc = new RFCAddAttribute();
		rfc.setAlias(alias);
		add(rfc);
		return(rfc);
	}

	public void setDescription(String description) {
		RFCModifyDescription rfc = new RFCModifyDescription();
		rfc.setDescription(description);
		add(rfc);
	}

	public void delete() {
		RFCDestroy rfc = new RFCDestroy();
		add(rfc);
	}
}
