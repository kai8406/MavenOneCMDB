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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;

/**
 * Not used any more. Delete.
 */
public class AttributeValueCollectionContext extends AbstractCacheContext implements ICmdbObjectFactory {
	
	private ICi ci;

	public AttributeValueCollectionContext(Map<String, Object> context, ICi ci, String attAlias) {
		super(context);
		this.ci = ci;
	}
	
	@Override
	public String[] getNewProperties() {
		Set<String> aliases = new HashSet<String>();
		Set<IAttribute> attributes = this.ci.getAttributeDefinitions();
		for (IAttribute attribute : attributes) {
			aliases.add(attribute.getAlias());
		}
		return(aliases.toArray(new String[0]));
	}

	@Override
	public Object getNewProperty(String propertyName) {
		IAttribute attribute = this.ci.getAttributeDefinitionWithAlias(propertyName);
		if (attribute == null) {
			return(null);
		}
		return(new AttributeContext(this.context, attribute, this.ci));
	}

	public void setProperty(String propertyName, Object value) {
		log.debug(this.ci.getAlias() + "::" + 
				this.getClass().getSimpleName() + 
				"::setProperty(" + propertyName + ")");
		
	}
	public void newObject(String alias) {
		log.debug(this.ci.getAlias() + "::" + 
				this.getClass().getSimpleName() + 
				"::newObject(" + alias + ")");	
		ICmdbTransaction tx = (ICmdbTransaction) context.get("tx");
		if (tx == null) {
			throw new IllegalArgumentException("No transaction setup!");
		}
		
		// Modify the template by creating a new instance offspring.
		ICiModifiable templateMod = tx.getTemplate(this.ci);
		
		IAttributeModifiable attributeMod = templateMod.createAttribute(alias, SimpleTypeFactory.STRING, null, 1, 1, null);
		
		updateProperty(alias, new AttributeModifiableContext(this.context, attributeMod));

	}

}
