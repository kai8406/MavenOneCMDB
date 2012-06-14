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
package org.onecmdb.ui.gwt.desktop.client.service.model;



public class ValueModel extends ModelItem  {
	
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	
	public static final String VALUE_VALUE = "value";
	public static final String VALUE_DISPLAYNAME = "displayValue";
	public static final String VALUE_ID = "id";
	public static final String VALUE_ALIAS = "alias";
	public static final String VALUE_ISCOMPLEX = "isComplex";
	//private CIModel ci;

	public ValueModel() {
		super();
	}

	public <X> X set(String name, X value) {
	
		if (name.equals(VALUE_VALUE)) {
			if (value instanceof CIModel) {
				//System.out.println("ValueModel:set(" + name + ", CIModel:" + value);
				super.set(VALUE_VALUE, ((CIModel)value).getAlias());
				super.set(VALUE_DISPLAYNAME, ((CIModel)value).getDisplayName());
				super.set(CIModel.CI_ICON_PATH, ((CIModel)value).get(CIModel.CI_ICON_PATH));
				return(value);
			}
			super.set(VALUE_DISPLAYNAME, value);
		}
		if (name.equals("this")) {
			if (value instanceof ValueModel) {
				super.set(VALUE_VALUE, ((ValueModel)value).getValue());
				super.set(VALUE_DISPLAYNAME, ((ValueModel)value).getValueDisplayName());
				super.set(CIModel.CI_ICON_PATH, ((ValueModel)value).get(CIModel.CI_ICON_PATH));
				
				return(value);
				//return(set("value", (ValueModel).getValue()));
			} else {
				return(set("value", value));
			}
		}
		//System.out.println("ValueModel:set(" + name + "," + value);
		return(super.set(name, value));
	}
	
	
	@Override
	public <X> X get(String property) {
		if (property.equals("this")) {
			return((X)this);
		}
		return (X)super.get(property);
	}

	public void setValue(String value) {
		set(VALUE_VALUE, value);
	}
	
	public String getValue() {
		return(get(VALUE_VALUE));
	}
	
	/**
	 * To avoid recursion...
	 */
	@Override
	 public String toString() {
		 //return(getValueDisplayName());
		 return(getAlias() + "[" + hashCode() + "]=" + getValue());
	 }


	
	public void setUpdateValue(String value2) {
		setValue(value2);
		//getParent().propagatePropertyChanged("value", getValue(), value2);
	}

	public void setValueDisplayName(String displayName) {
		set(VALUE_DISPLAYNAME, displayName);
	}
	
	public String getValueDisplayName() {
		return(get(VALUE_DISPLAYNAME)); 
	}

	
	public void setIdAsString(String id) {
		set(VALUE_ID, id);
	}
	
	public String getIdAsString() {
		return(get(VALUE_ID));
	}

	public void setAlias(String alias) {
		set(VALUE_ALIAS, alias);
	}
	

	public String getAlias() {
		return(get(VALUE_ALIAS));
	}
	
	public ValueModel copy() {
		ValueModel vModel = new ValueModel();
		copy(vModel);
		return(vModel);
	}


	public void setIsComplex(boolean complexValue) {
		set(VALUE_ISCOMPLEX, complexValue);
	}

	public boolean isComplex() {
		return((Boolean)get(VALUE_ISCOMPLEX));
	}

	
	
	/*	
	public void setParent(CIModel model) {
		//this.ci = model;
	}

	public CIModel getParent() {
		return(null);
		//return(this.ci);
	}
	*/
	
}
