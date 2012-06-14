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
package org.onecmdb.core.internal.model.primitivetypes;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.onecmdb.core.ErrorObject;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueSelector;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.utils.HashCodeUtil;

/**
 * <p>
 * Types considered <em>primitive</em> are derived from this class.
 * </p>
 * <p>
 * <b>Note:</b> Class implents both {@link org.onecmdb.core.IType} and
 * {@link org.onecmdb.core.IValue}
 * </p>
 * 
 * @author nogun
 * 
 */
public abstract class SimpleType implements IType, IValue {

	/**
	 * A simple type's value always returns <code>null</code> as its
	 * identifier, in contrast to to a comple type's value, which returns the id
	 * where the value is <em>stored</em>.
	 * 
	 * @return
	 */
	public final ItemId getId() {
		return null;
	}
    
    
    

	/** actual data hold by this type */
	private Object payload;
    
    /** 
     * Initially a simple type is a template. Whenever a value is 
     * returned/created the sinple type is no longer a template.
     * @see #parseString(String)
     */
    private boolean blueprint = true;

	protected final void setPayload(Object payload) {
		this.payload = payload;
	}

    public final boolean isBlueprint() {
        return blueprint;
    }
    
    public final boolean isComplex(){
    	return false;
    }
    
	/** actual data hold by this type */
	protected final Object getPayload() {
		return this.payload;
	}

	public final String getIcon() {
        return getAlias().replaceAll(":", "").toLowerCase();
	}

	public IValue parseInputStream(InputStream in) {
		throw new IllegalArgumentException(
				"parseInputStream() not supported by data type:"
						+ this.getUniqueName());
	}

	public OutputStream asOutputStream() {
		throw new IllegalArgumentException(
				"asOutputStream() not supported by data type:"
						+ this.getUniqueName());
	}

	// {{{ Spingified
	public void setValueAsString(String value) {
		parseString(value);
	}

	// }}}

	public final String getAlias() {
		return (getUniqueName());
	}

	public String getDisplayName() {
        return isBlueprint() ? getAlias() : getAsString();
        
	}

	public final String getAsString() {
		payload = getPayload();
		if (payload != null) {
		    try {
		        return payload.toString();
		    } catch (IllegalStateException e) {}
		}
		return "";
	}

	public final Object getAsJavaObject() {
		return(getPayload());
	}
	
	public final int hashCode() {
		int h = HashCodeUtil.SEED;
		h = HashCodeUtil.hash(h, getPayload());
		return h;
	}

	/**
	 * Compare two types for equality, by comparing the value represneted by
	 * this type
	 */
	public final boolean equals(Object o) {
        
        Object payload = getPayload();
        if (payload != null && payload.equals(o)) {
            return true;
        }
        
		if (o == null || !getClass().equals(o.getClass()))
			return false;

        SimpleType other = (SimpleType) o;
        if (isBlueprint()) {
            // the value is irrelevant. we have the same class, therefore
            // the objects are equal, i.e. they represent the same blueprint.
            return true;
        }
            
        

		Object thisPayload = getPayload();
		Object otherPayload = other.getPayload();

		if (thisPayload != null && otherPayload != null) {
			return thisPayload.equals(otherPayload);
		} else if ( thisPayload != null && otherPayload == null) {
            return false;
        } else if (thisPayload == null && otherPayload != null) {
            return false;
		}
		// both are null!
		return true;
	}

	public IValueSelector getValueSelector() {
		// TODO Auto-generated method stub

		IValueSelector sel = new InfiniteValueSelector();
		return sel;

	}

    
    
    /** 
     * Parses a string retruning a value represented by by the string 
     */
    public final IValue parseString(String s) {
        SimpleType v = doParseString(s);
        if (v != null) {
            v.blueprint = false;
        }
        return (IValue) v;
    }
    /**
     * Do the actual transformation from a string into a simple <em>value</em>.
     * 
     * @param s The string to be parsed. If null is passed, then the
     * <em>null</em> value is requested for.
     * 
     * @param s A string to transform
     * @return A new simple value represented by the string passed
     */
    protected abstract SimpleType doParseString(String s);


    /**
     * Constructs a the value, representing the <code>null</code>, or
     * <em>empty</em> value for this type, by using {@link #parseString}
     * with a <code>null</code> argument.
     *
     */
    public IValue getNullValue() {
        return parseString(null);
    }

    
    
    public ErrorObject validate(IValue v) {
		return null;
	}

	public IValue fromValue(IValue value) {
		return parseString(value != null ? value.getAsString() : null);

	}

	public String toString() {
		return "<" + getValueType().getAlias() + "> " + getAsString();
	}


    public boolean isNullValue() {
        return getPayload() == null;
    }

	public Set<IType> getAllOffspringTypes() {
		HashSet<IType> types = new HashSet<IType>();
		types.add(this);
		return(types);
	}

    
    public IPath<IType> getOffspringPath() {
        return new Path<IType>(this);
    }
    
}
