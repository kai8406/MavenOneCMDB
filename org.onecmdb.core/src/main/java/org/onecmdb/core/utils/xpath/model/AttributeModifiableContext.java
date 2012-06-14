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
import java.util.Set;

import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.primitivetypes.StringType;

/**
 * Created when a new attribute is created.
 * <br>
 * create path /template/<i>template-name</i>/attribute/<i>new-attribute-name</i>
 *
 */
public class AttributeModifiableContext extends AbstractCacheContext {

	private IAttributeModifiable attributeModifier;

	public AttributeModifiableContext(Map<String, Object> context, IAttributeModifiable attributeMod) {
		super(context);
		this.attributeModifier = attributeMod;
	}

	@Override
	public String[] getNewProperties() {
		return(new String[] {
			"policy"	
		});
	}

	@Override
	public Object getNewProperty(String propertyName) {
		if (propertyName.equals("policy")) {
			return(this);
		}
		return(null);
	}

	public void setProperty(String propertyName, Object value) {
		log.debug("SetProperty(" + propertyName + ", " + value);
		if (propertyName.equals("type")) {
			IType type = null;
			if (value instanceof String) {
				ISession session = (ISession) context.get("session");
				if (session == null) {
					throw new IllegalArgumentException("No Session found!");
				}
				IModelService model = (IModelService)session.getService(IModelService.class);
				
				// Check for builtin types.
				Set<IType> builtIn = model.getAllBuiltInTypes();				
				for (IType t : builtIn) {
					if (t.getAlias().equals((String)value)) {
						type = t;
						break;
					}
				}
				
				// Check if it's found as a ICi.
				if (type == null) {
					type = model.findCi(new Path<String>((String)value));
				}
				
			} else {
				if (value instanceof TemplateContext) {
					type = ((TemplateContext)value).getICi();					
				}
			}
			
			if (type == null) {
				throw new IllegalArgumentException("Type + " + value.toString() + " found!");
			}
			
			
			if (!type.isBlueprint()) {
				throw new IllegalArgumentException("Type '" + type.getAlias() + "' must be a template!");
			}
		
			attributeModifier.setValueType(type);
			return;
		}
		
		// Short-cut to policy/maxOccurs
		if (propertyName.equals("maxOccurs")) {
			if (value == null) {
				return;
			}
			String strValue = value.toString();
			int maxOccurs = 0;
			if (strValue.equals("unbound")) {
				maxOccurs = -1;
			} else {
				maxOccurs = Integer.parseInt(strValue);
			}
			attributeModifier.setMaxOccurs(maxOccurs);
			return;
		}
		
		// Short-cut to policy/minOccurs
		if (propertyName.equals("minOccurs")) {
			if (value == null) {
				return;
			}
			String strValue = value.toString();
			int minOccurs = 0;
			if (strValue.equals("unbound")) {
				throw new IllegalArgumentException("Unbound value is not valid on minOccurs!");
			} else {
				minOccurs = Integer.parseInt(strValue);
			}
			attributeModifier.setMinOccurs(minOccurs);
			return;
		}
		
		if (propertyName.equals("defaultValue")) {
			IValue iValue = null;
			if (value instanceof String) {
				// Use String to convert the input to IValue.
				// The lowwer layer will validate the string.
				StringType t = new StringType();
				iValue = t.parseString((String)value);
			} else if (value instanceof InstanceContext) {
				InstanceContext bean = (InstanceContext)value;
				iValue = (IValue) bean.getCi();
			}
			attributeModifier.setValue(iValue);
			return;
		}
	}
}
