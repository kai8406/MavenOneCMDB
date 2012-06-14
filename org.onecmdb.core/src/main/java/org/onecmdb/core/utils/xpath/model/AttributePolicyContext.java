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

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.StringType;

/**
 * Dynamic handler for attribute policy, max/minOccurs.
 * <br>
 * Path /template/<i>template-alias</i>/attribute/<i>attribute-alias</i>
 * /policy
 * 
 *
 */
public class AttributePolicyContext extends AbstractCacheContext {

	private IAttribute attribute;

	public AttributePolicyContext(Map<String, Object> context, IAttribute a) {
		super(context);
		this.attribute = a;
	}
	
	@Override
	public String[] getNewProperties() {
		String p[] = new String[2];
		p[0] = "minOccurs";
		p[1] = "maxOccurs";
		return(p);
	}

	@Override
	public Object getNewProperty(String propertyName) {
		if (propertyName.equals("minOccurs")) {
			return(this.attribute.getMinOccurs());
		}
		if (propertyName.equals("maxOccurs")) {
			return(this.attribute.getMaxOccurs());
		}
		
		return null;
	}

	public void setProperty(String propertyName, Object value) {
		
		// The transaction is setup by the command.
		ICmdbTransaction tx = (ICmdbTransaction)this.context.get("tx");
		if (tx == null) {
			throw new IllegalArgumentException("No transaction found!");
		}
		
		// Modify Attribute value.
		IAttributeModifiable attributeMod = tx.getAttributeTemplate(attribute);
		
		AttributeModifiableContext modContext = new AttributeModifiableContext(this.context, attributeMod);
		modContext.setProperty(propertyName, value);
	}
	
	public String toString() {
		return("[" + this.attribute.getMinOccurs() + ".." + this.attribute.getMaxOccurs() + "]");
	}

}
