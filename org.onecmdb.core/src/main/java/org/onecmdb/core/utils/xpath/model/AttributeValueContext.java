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
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.StringType;

/**
 * Dynamic wrapper for an attribute value.
 * <br>
 * Path /instance/<i>template</i>/<i>instance-alias</i>/<i>attribute-alias</i>
 * 
 */
public class AttributeValueContext extends AbstractCacheContext implements ICmdbObjectFactory,ICmdbObjectDestruction {

	private IAttribute attribute;
	private InstanceContext refCi;
	private IValue iValue;
	private ICi ci;
	private String attAlias;
	
	public AttributeValueContext(Map<String, Object> context, IAttribute value) {
		super(context);
		this.attribute = value;
	}
	
	// Used to create new.
	public AttributeValueContext(Map<String, Object> context, ICi ci, String attAlias) {
		super(context);
		this.ci = ci;
		this.attAlias = attAlias;
	}

	@Override
	public String[] getNewProperties() {
		IValue v = getIValue();
		if (v instanceof ICi) {
			if (this.refCi == null) {
				this.refCi = new InstanceContext(this.context, (ICi)v);
			}
			return(this.refCi.getProperties());
		}
		String ret[] = new String[2];
		ret[0] = "asString";
		ret[1] = "displayName";
		return(ret);	
	}

	@Override
	public Object getNewProperty(String propertyName) {
		IValue v = getIValue();
		
		if (v == null) {
			return(null);
		}
		
		if (v instanceof ICi) {
			if (this.refCi == null) {
				this.refCi = new InstanceContext(this.context, (ICi)v);
			}
			if (propertyName.equals("iValue")) {
				return(this.refCi);
			}
			return(this.refCi.getProperty(propertyName));
		}
		if (propertyName.equals("asString")) {
			return(v.getAsString());
		}
		if (propertyName.equals("displayName")) {
			return(v.getDisplayName());
		}
		if (propertyName.equals("iValue")) {
			return(this);
		}
		return(null);
		
	}

	public void setProperty(String propertyName, Object value) {
		// The transaction is setup by the command.
		ICmdbTransaction tx = (ICmdbTransaction)this.context.get("tx");
		if (tx == null) {
			throw new IllegalArgumentException("No transaction found!");
		}
		
		// Modify Attribute value.
		IAttributeModifiable valueMod = tx.getAttributeTemplate(attribute);
		AttributeValueModifiableContext modContext = new AttributeValueModifiableContext(this.context, valueMod);
		modContext.setProperty(propertyName, value);
	}
	
	public String toString() {
		IValue iValue = getIValue();

		if (iValue == null) {
			return(null);
		}
		return(iValue.getDisplayName());
	}
	
	public IValue getIValue() {
		if (this.attribute == null) {
			return(null);
		}
		if (this.iValue == null) {			
			this.iValue = this.attribute.getValue();
		}
		return(this.iValue);
	}

	public void newObject(String alias) {
		// Add a new Attribute.
		// The transaction is setup by the command.
		ICmdbTransaction tx = (ICmdbTransaction)this.context.get("tx");
		if (tx == null) {
			throw new IllegalArgumentException("No transaction found!");
		}
		if (this.attribute != null) {
			this.ci = this.attribute.getOwner();
			this.attAlias = this.attribute.getAlias();
		}
		
		// Modify Attribute value.
		ICiModifiable ciMod = tx.getTemplate(this.ci);
		IAttributeModifiable aMod = ciMod.addAttribute(this.attAlias);
		AttributeValueModifiableContext modContext = new AttributeValueModifiableContext(this.context, aMod);
		
		updateProperty(alias, modContext);
	}

	public void destory() {
		if (this.attribute != null) {
			ICmdbTransaction tx = (ICmdbTransaction)this.context.get("tx");
			if (tx == null) {
				throw new IllegalArgumentException("No transaction found!");
			}
			// Check if root.
			IAttributeModifiable attrMod = tx.getAttributeTemplate(this.attribute);
			if (this.attribute.getDerivedFrom() == null) {
				attrMod.setValue(null);
			} else {
				attrMod.delete();
			}
		}
	}

}
