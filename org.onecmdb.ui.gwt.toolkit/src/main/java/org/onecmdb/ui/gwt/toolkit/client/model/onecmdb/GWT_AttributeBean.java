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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code>GWT_AttributeBean</code> holds data for an attribute definition for a CI.<br>
 * It's always contained inside a <code>CiBean</code> and does not have<br>
 * a reference to the owner <code>CiBean</code>.<br>
 * <br>
 * The Attribute bean can produce XML snippet for itself.
 *
 */
public class GWT_AttributeBean implements IsSerializable {
	// The alias for this attribute.
	private String alias;

	// The displayName for this attribute.
	private String displayName;

	// Is the type a complex type, pointer to another CI template
	private boolean complexType;
	
	// The type alias 
	private String type;

	// The reference type alias, can be null if no reference type is requeired.
	private String refType;

	// The max occurrence of this attribute.
	private String maxOccurs;

	// The min occurrence of this attribute.
	private String minOccurs;
	
	// The description for the attribute.
	private String description;

	// The back end id, Read-Only.
	private String idStr;
	
	// If this attribute is defined on this template, Read-Only.
	private boolean derived;

	private GWT_CiBean parent;
	
	// Create Date.
	private Date createDate;
	
	// Last Modified Time.
	private Date lastModified;

	/**
	 * Basic constructor
	 *  
	 */
	public GWT_AttributeBean() {		
	}
	
	/**
	 * Help constructor to minimize code lines.
	 * @param alias
	 * @param type
	 * @param refType
	 * @param complex
	 */
	public GWT_AttributeBean(String alias, String type, String refType, boolean complex) {
		setAlias(alias);
		setType(type);
		setRefType(refType);
		setComplexType(complex);
	}
	
	public void setDescription(String d) {
		this.description = d;
	}

	public String getDescription() {
		return (this.description);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public String getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return (displayName + "<" + type + ">[" + minOccurs + ".." + maxOccurs + " <<"
				+ refType + ">>]");
	}

	public void setParentCI(GWT_CiBean bean) {
		this.parent = bean;
	}
	
	public GWT_CiBean getParentCI() {
		return(this.parent);
	}
	
	public int fetchMaxOccursAsInt() {
		if (maxOccurs == null) {
			return (1);
		}
		if (maxOccurs.equals("unbound")) {
			return (-1);
		}
		int value = Integer.parseInt(maxOccurs);
		return (value);
	}

	public int fetchMinOccursAsInt() {
		if (minOccurs == null) {
			return (1);
		}
		int value = Integer.parseInt(minOccurs);
		return (value);
	}

	public boolean isComplexType() {
		return (this.complexType);
	}

	public void setComplexType(boolean value) {
		this.complexType = value;
	}

	public void setId(Long id) {
		if (id == null) {
			this.idStr = null;
			return;
		}
		this.idStr = id.toString();
	}
	
	public Long getId() {
		if (this.idStr == null) {
			return(null);
		}
		return(new Long(this.idStr));
	}
	
	public void setIdAsString(String id) {
		this.idStr = id;
	}
	
	public String getIdAsString() {
		return(this.idStr);	
	}

	
 	public void setDerived(boolean b) {
		this.derived = b;
	}
	
	public boolean isDerived() {
		return(this.derived);
	}

	public List getValues() {
		List values = Collections.EMPTY_LIST;
		if (getParentCI() != null) {
			values = getParentCI().fetchAttributeValueBeans(this.getAlias());
		}
		return(values);
	}
	
	public GWT_AttributeBean copy() {
		GWT_AttributeBean copy = new GWT_AttributeBean();
		copy.setAlias(this.getAlias());
		copy.setIdAsString(getIdAsString());
		copy.setDerived(this.isDerived());
		copy.setType(this.getType());
		copy.setRefType(this.getRefType());
		copy.setMaxOccurs(this.getMaxOccurs());
		copy.setMinOccurs(this.getMinOccurs());
		copy.setComplexType(this.isComplexType());
		copy.setDescription(this.getDescription());
		copy.setDisplayName(this.getDisplayName());
		return(copy);
	}

	public boolean isMultiValued() {
		return(!getMaxOccurs().equals("1"));
	}
}
