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

import java.util.List;
import java.util.Map;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.StringType;

public class InstanceModifiableContext extends AbstractCacheContext {

	private ICiModifiable instanceModifier;

	public InstanceModifiableContext(Map<String, Object> context, ICiModifiable instanceMod) {
		super(context);
		this.instanceModifier = instanceMod;
	}

	@Override
	public String[] getNewProperties() {
		if (this.properties == null) {
			return(new String[0]);
		}
		return(properties.toArray(new String[0]));
	}

	@Override
	public Object getNewProperty(String propertyName) {
		return(propertyValueMap.get(propertyName));
	}

	public void setProperty(String propertyName, Object value) {
		log.debug("SetProperty(" + propertyName + ", " + value);
		if (propertyName.equals("alias")) {
			instanceModifier.setAlias((String)value);
			return;
		}
		if (propertyName.equals("displayNameExpression")) {
			instanceModifier.setDisplayNameExpression((String)value);
			return;
		}
		
		if (propertyName.equals("description")) {
			instanceModifier.setDescription((String)value);
			return;
		}
		
		// Can set values here on single valued attributes, else a specific [] needs to be applied.
		IValue iValue = null;
		if (value instanceof String) {
			// Use String to convert the input to IValue.
			// The lowwer layer will validate the string.
			StringType t = new StringType();
			iValue = t.parseString((String)value);
		} else if (value instanceof InstanceContext) {
			InstanceContext bean = (InstanceContext)value;
			iValue = (IValue) bean.getCi();
			updateProperty(propertyName, bean);
		}
		
		// Will reject of attribute name don't exist.
		instanceModifier.setDerivedAttributeValue(propertyName, 0, iValue);
		
	}
}
