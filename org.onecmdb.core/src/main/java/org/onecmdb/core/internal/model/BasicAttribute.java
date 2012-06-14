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
import java.util.Date;


import org.onecmdb.core.ICi;
import org.onecmdb.core.IType;
import org.onecmdb.core.ITypeSelector;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.reference.ConnectionItem;

/**
 * The basic attribute, from where other attribute types must be based on
 */
public class BasicAttribute extends AbstractAttribute {

	/**
	 * <p>
	 * An attribute always keep a notion of a value, note that the value is
	 * updated by a <em> value resolver</em>
	 * </p>
	 * 
	 * <p>
	 * Note the internal representation of a value is stored as a String!
	 * </p>
	 */
	private String value;
	private Long longValue;
	private Date dateValue;
	private boolean complexValue;
	
	// {{{ spring satisfication
	/*
	 * public BasicAttribute(Object initialValue) { super(); this.value =
	 * initialValue; }
	 */

	public boolean isComplexValue() {
		return complexValue;
	}

	public void setComplexValue(boolean complexValue) {
		this.complexValue = complexValue;
	}

	public BasicAttribute() {
		super();
	}

	// initiate the update policy
	public void initialize() {

	}

	// }}}

	/**
	 * Return the the current value of for this attribute. Note, we actually
	 * return a copy of the value, to make sure no ``unsanctioned'' modifications
	 * will occur once returned, and makes sure the attribute is immutable, which
	 * eases concurrency.
	 */
	public IValue getValue() {
		// break out into a separate thread/process run from a separate
		// location
		if (value == null || getValueType() == null) {
			return (null);
		}
		IValue iValue = getValueType().parseString(value);
		if (this.getReferenceTypeName() != null) {

			if (iValue instanceof ICi) {
				ConnectionItem item = new ConnectionItem(this.daoReader,
						(ICi) iValue);
				iValue = item.getTarget();
			}
		}
		return (iValue);

	}

	public boolean isComplex(){
		return false;
	}
	
	@Override
	public boolean isNullValue() {
		return(this.value == null);		
	}

	/**
	 * Set a new value on this attribute, using a default <em>by reference</em>
	 * strategy. The Object value will be converted to a String representation
	 * using the <em>type<em>.  
	 * @param newValue
	 */
	public void setValue(IValue newValue) {
		this.value = newValue.getAsString();
	}

	@Override
	public InputStream getInputStream() {

		/*
		 * Wrap the current value into a stream, which may leverage the caller,
		 * in cases the caller is not interested in the whole as part.
		 */

		InputStream is = null;
		// is = ObjectConverter.convertValueToInputStream(this.value,
		// getType());

		return is;
	}

	public Object getAdapter(Class clazz) {
		return (null);
	}

	@Override
	protected String toString(int level, int maxLevel) {
		StringBuffer sb = new StringBuffer();

		/*
		 * if ( !(v instanceof String) && !(v instanceof Number) ) sval = "<binary>";
		 * else { sval = v.toString(); }
		 */
		/*
		 * if (sval.length() > 40 ) { sval = sval.substring(0, 40) + "..."; }
		 */
		IType type = getValueType();
		String typeName = "";
		if (type == null) {
			typeName = "undefined";
		} else {
			typeName = type.getAlias();
		}
		sb.append(getTab(level) + "<" + getAlias()); 
		sb.append(" id=\"" + getId() + "\"");
		sb.append(" name=\"" + getDisplayName() + "\"");
		sb.append(" type=\"" + typeName + "\""); 
		sb.append(">");
		String sval = null;
		IValue v = getValue();
		if (v != null) {
			if (v instanceof ConfigurationItem) {
				sb.append(((ConfigurationItem) v).toString(level + 1, maxLevel));
			} else {
				sval = v.getAsString();

				if (sval == null) {
					sval = "null";
				}
				sb.append(sval);
			}
		} else {
			sb.append("null");
		}
		sb.append("</" + getAlias() +">");
		sb.append(toStringAttributes(level + 1, maxLevel));
		return (sb.toString());
	}

	public ICi getReferencedCi() {
		ICi ref = getReference();
		if (ref == null) {
			return (null);
		}
		ConnectionItem item = new ConnectionItem(daoReader, ref);
		ICi ci = item.getTarget();
		return (ci);
	}

	public ICi getReference() {
		if (this.value == null) {
			return (null);
		}
		if (this.referenceType == null) {
			return (null);
		}

		ItemId id = ObjectConverter.convertUniqueNameToItemId(daoReader,
				this.value);
		ICi ci = daoReader.findById(id);
		return (ci);
	}


    
    
	/**
	 * {{{ Hibernate setter/Getters!!! Persistent state in BasicAttribute.
	 * valueAsString : String
	 * 
	 */
	/**
	 * Should Only be used by hibernate!
	 */
	public String getValueAsString() {
		return (this.value);
	}

	/**
	 * Should Only be used by hibernate!
	 */
	public void setValueAsString(String value) {
		this.value = value;
	}
	

	public Date getValueAsDate() {
		return dateValue;
	}

	public void setValueAsDate(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Long getValueAsLong() {
		return this.longValue;
	}

	public void setValueAsLong(Long longValue) {
		this.longValue = longValue;
	}

	public boolean isDerived() {
		return(this.getDerivedFromId() != null);
	}
	
	
	
	/*
	 * Hibernate }}}
	 */
}
