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


public class AttributeModel extends ModelItem {
	
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	public AttributeModel() {
		set("remove", false);
	}

	/**
	 * To avoid recursion...
	 */

	/*
	private boolean complex;
	private String displayName;
	private String alias;
	private String description;
	private int maxOccur;
	private int minOccur;
	private String icon;
	private boolean derived;
	private CIModel parent;
	private CIModel ci;
	*/
	

	public boolean isDerived() {
		return((Boolean)get("derived", false));
	}

	public void setDerived(boolean derived) {
		set("derived", derived);
	}

	public String getAlias() {
		return(get("alias"));
	}

	public void setAlias(String alias) {
		set("alias", alias);
	}

	public String getDescription() {
		return(get("description"));
	}

	public void setDescription(String description) {
		set("description", description);
	}

	public String getMaxOccur() {
		return(get("maxOccur"));
	}

	public void setMaxOccur(String maxOccur) {
		set("maxOccur", maxOccur);
	}

	public String getMinOccur() {
		return(get("minOccur"));
	}

	public void setMinOccur(String minOccur) {
		set("minOccur", minOccur);
	}

	public void setDisplayName(String displayName) {
		set("name", displayName);;
	}

	public String getDisplayName() {
		return(get("name"));
	}

	public boolean isComplex() {
		return(get("complex", false));
	}
	
	public void setComplex(boolean b) {
		set("complex", b);
	}


	public void setIdAsString(String id) {
		set("id", id);
	}
	
	public String getIdAsString() {
		return(get("id"));
	}
	
	
	public void setSimpleType(String type) {
		set("simpleType", type);
	}
	public String getSimpleType() {
		return(get("simpleType"));
	}
	
	public void setComplexType(CIModel type) {
		set("complexType", type);
	}
	public CIModel getComplexType() {
		return(get("complexType"));
	}
	
	public void setRefType(CIModel type) {
		set("refType", type);
	}
	public CIModel getRefType() {
		return(get("refType"));
	}
	/*
	public void setParent(CIModel model) {
		this.ci = model;
	}

	public CIModel getParent() {
		return(this.ci);
	}
	*/

	@Override
	public AttributeModel copy() {
		AttributeModel a = new AttributeModel();
		copy(a);
		return(a);
	}

	public boolean isRemove() {
		Object remove = get("remove");
		if (remove instanceof Boolean) {
			return((Boolean)remove);
		}
		if (remove instanceof String) {
			return("true".equalsIgnoreCase((String)remove));
		}
		return(false);
	}
}

	
