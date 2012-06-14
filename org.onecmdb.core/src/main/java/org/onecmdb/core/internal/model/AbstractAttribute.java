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
package org.onecmdb.core.internal.model;

import java.io.InputStream;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IType;
import org.onecmdb.core.ITypeSelector;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueSelector;

/**
 * 
 * An attribute is always an offspring from "Attribute", which must have a name,
 * and a <em>provider</em> from which its value is fetched.
 */
public abstract class AbstractAttribute extends ConfigurationItem implements
		IAttribute {

	// {{{ IAdaptable

	/*
	 * @Override public Object getAdapter(Class type) { if
	 * (type.equals(IExtensibleAttribute.class)) { return new
	 * IExtensibleAttribute() { public IAttribute createOffspring(String name,
	 * IType valueType, IValueProvider initializer) { BasicAttribute attr = new
	 * BasicAttribute(initializer.fetchValueContent()); attr.setName(name);
	 * attr.setType(valueType); AbstractAttribute.this.addAttribute(attr);
	 * return attr; }
	 *  };
	 *  } if (type.equals(IModifiableAttribute.class)) { return new
	 * IModifiableAttribute() { public void setValue(Object value) {
	 * BasicAttribute ba = (BasicAttribute) AbstractAttribute.this;
	 * ba.setValue(value); } }; }
	 * 
	 * 
	 * return super.getAdapter(type); }
	 */
	// }}}

	// {{{ ---| Spring BEAN Framework |---

	public AbstractAttribute() {
		super();

	}

	@Override
	public Set<ICi> getOffsprings() {
		// TODO Auto-generated method stub
		return daoReader.getAttributeOffsprings(this.getId());
	}


	private String type;

	public final void setType(IType type) {
		this.type = type.getUniqueName();
	}

	protected String referenceType;

	public final void setReferenceType(IType type) {
		if (type != null) {
			this.referenceType = type.getUniqueName();
		}
	}

	private ItemId owner;

	public void setOwner(ICi ci) {
		this.owner = ci.getId();
	}

	public ICi getOwner() {
		if (this.owner == null) {
			return (null);
		}
		ICi ciOwner = getDaoReader().findById(this.owner);
		return (ciOwner);
	}

	/*
	 * Why does this nned this when ConfigurationItem has it.
	 */
	/*
	 * private String displayName; public final void setDisplayName(String
	 * expression) { this.displayName = expression; }
	 */

	// }}}
	/**
	 * Declare how many attributes that a offspring can have.
	 */
	private int maxOccurs = 1;

	/**
	 * Used to declare how many attributes that a offspring should get, by
	 * default.
	 */
	private int minOccurs = 0;

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}


	// }}}

	public abstract IValue getValue();

	public abstract InputStream getInputStream();

	public IType getValueType() {
		return (ObjectConverter.convertStringToType(this.daoReader, this.type));
	}

	public IType getReferenceType() {
		return (ObjectConverter.convertStringToType(this.daoReader,
				this.referenceType));
	}


	/**
	 * <p>
	 * Creates a string representation of this attribute, by concatenating its
	 * name, its type, and the value (in case it is printable), according to:
	 * </p>
	 * <blockqoute> <code><i>name</i>=<i>type</i>:<i>value</i>
	 * </blockqoute>
	 */
	public final String toString() {
		String sval;
		Object v = getValue();
		if (!(v instanceof String) && !(v instanceof Number))
			sval = "<binary>";
		else {
			sval = v.toString();
		}
		if (sval.length() > 40) {
			sval = sval.substring(0, 40) + "...";
		}
		return getAlias() + "=[" + getValueType() + "] " + sval;
	}

	/**
	 * {{{ Hibernate setter/Getters!!! Persistent state in AbstartcAttribute.
	 * name : String ownerId : Long typeName : String
	 * 
	 */
	// Name is ok.
	public Long getOwnerId() {
		return (ObjectConverter.convertItemIdToLong(this.owner));
	}

	public void setOwnerId(Long id) {
		this.owner = ObjectConverter.convertLongToItemId(id);
	}

	public String getTypeName() {
		return (this.type);
	}

	public void setTypeName(String type) {
		this.type = type;
	}

	public String getReferenceTypeName() {
		return (this.referenceType);
	}

	public void setReferenceTypeName(String type) {
		this.referenceType = type;
	}

	/*
	 * Hibernate }}}
	 */

    
  
	@Override
    public final String getIcon() {
        IValue value= this.getValue();
        if (value != null) {
            return value.getIcon();
        } else {
            IType type = getValueType();
            if (type != null) {
            	return type.getIcon();
            }
        }
        return(null);
    }
    
    @Override
    public IValueSelector getValueSelector() {
        IValueSelector selector = new AttrbuteValueSelector(this);
        return (selector);
    }

    public ITypeSelector getTypeSelector() {
        ITypeSelector selector = new InstanceTypeSelector(this);
        return (selector);
    }


}
