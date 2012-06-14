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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;

/**
 * The Dynamic handler for an attribute (definition).
 * <br>
 * Returned from /template/<i>template-name</i>/attribute/<i>attribute-alias</i>
 *
 */
public class AttributeContext extends AbstractCacheContext implements ICmdbObjectDestruction {

	private IAttribute attribute;
	private ICi ci;
	
	public AttributeContext(Map<String, Object> context, IAttribute a, ICi ci) {
		super(context);
		this.attribute = a;
		this.ci = ci;
	}
	
	@Override
	public String[] getNewProperties() {
		String properties[] = new String[9];
		
		properties[0] = "id";
		properties[1] = "alias";
		properties[2] = "displayName";
		properties[3] = "type";
		properties[4] = "refType";
		properties[5] = "owner";
		properties[6] = "policy";
		properties[7] = "meta";
		properties[8] = "defaultValue";
		
		
		
		return(properties);
	}

	@Override
	public Object getNewProperty(String propertyName) {
		if (propertyName.equals("id")) {
			return(this.attribute.getId());
		}
		if (propertyName.equals("alias")) {
			return(this.attribute.getAlias());
		}
		if (propertyName.equals("displayName")) {
			return(this.attribute.getDisplayName());
		}
		if (propertyName.equals("type")) {
			IType type = this.attribute.getValueType();
			if (type instanceof ICi) {
				return(new TemplateContext(this.context, (ICi)type));
			}
			return(type.getAlias());
		}
		if (propertyName.equals("refType")) {
			ICi refType = (ICi)this.attribute.getReferenceType();
			if (refType == null) {
				return(null);
			}
			return(new TemplateContext(this.context, refType));
		}
		if (propertyName.equals("owner")) {
			ICi owner = this.attribute.getOwner();
			if (owner == null) {
				return(null);
			}
			return(new TemplateContext(this.context, owner));
		}
		if (propertyName.equals("policy")) {
			return(new AttributePolicyContext(this.context, this.attribute));
		}
		
		if (propertyName.equals("defaultValue")) {
			List<IAttribute> attributes = this.ci.getAttributesWithAlias(this.attribute.getAlias());
			List<Object> values = new ArrayList<Object>();
			
			// Used for creating if no attribute exists.
			if (attributes.size() == 0 && this.context.get("create") != null) {
				values.add(new AttributeValueContext(this.context, this.ci, this.attribute.getAlias()));
			}
			
			for (IAttribute attribute : attributes) {
				values.add(new AttributeValueContext(this.context, attribute));
				/*
				IValue v = attribute.getValue();
				if (v != null) {
					if (v instanceof ICi) {
						values.add(new InstanceContext(this.context, (ICi)v));
					} else {
						values.add(v.getDisplayName());
					}
				}
				*/
			}
			return(values);
		}
		return(null);
	}

	public void setProperty(String propertyName, Object value) {
		log.debug("setProperty(" + propertyName + ", " + value);
		ICmdbTransaction tx = (ICmdbTransaction) this.context.get("tx");
		if (tx == null) {
			throw new IllegalAccessError("No transaction setup!");
		}
		
		// Check the attribute for this ci.
		if (this.attribute.getOwner().equals(this.ci)) {
			IAttributeModifiable attributeMod = tx.getAttributeTemplate(this.attribute);
			AttributeModifiableContext modContext = new AttributeModifiableContext(this.context, attributeMod);
			modContext.setProperty(propertyName, value);
		}
	}
	
	public void destory() {
		if (this.attribute != null) {
			ICmdbTransaction tx = (ICmdbTransaction)this.context.get("tx");
			if (tx == null) {
				throw new IllegalArgumentException("No transaction found!");
			}
			if (!this.attribute.getOwner().equals(this.ci)) {
				throw new IllegalArgumentException("Can't delete attribute definition on offsprings");
			}
			IAttributeModifiable attrMod = tx.getAttributeTemplate(this.attribute);
			attrMod.delete();
	
		}
	}

	
	public String toString() {
		return(this.attribute.getAlias());
	}

	
	
}
