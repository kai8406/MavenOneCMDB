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
package org.onecmdb.core.utils.xpath.model;

import java.util.Map;

import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.StringType;

/**
 * Dynamic wrapper for a newly created attribute value.
 * 
 *
 */
public class AttributeValueModifiableContext extends AbstractCacheContext {

	private IAttributeModifiable attributeModifier;

	public AttributeValueModifiableContext(Map<String, Object> context, IAttributeModifiable attributeMod) {
		super(context);
		this.attributeModifier = attributeMod;
	}

	@Override
	public String[] getNewProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getNewProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProperty(String propertyName, Object value) {
		IValue iValue = null;
		if (value instanceof String) {
			StringType t = new StringType();
			iValue = t.parseString((String)value);
		} else if (value instanceof InstanceContext) {
			InstanceContext bean = (InstanceContext)value;
			iValue = (IValue) bean.getCi();
		}
		attributeModifier.setValue(iValue);
	}
	
	


}
