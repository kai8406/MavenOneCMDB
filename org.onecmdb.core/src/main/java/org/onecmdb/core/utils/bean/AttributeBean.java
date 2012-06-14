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
package org.onecmdb.core.utils.bean;

import java.util.Date;

import org.onecmdb.core.utils.xml.XmlParser;

/**
 * <code>AttributeBean</code> holds data for an attribute definition for a CI.<br>
 * It's always contained inside a <code>CiBean</code> and does not have<br>
 * a reference to the owner <code>CiBean</code>.<br>
 * <br>
 * The Attribute bean can produce XML snippet for itself.
 *
 */
public class AttributeBean {
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
	private Long id;

	// If this attribute is defined on this template, Read-Only.
	private boolean derived;
	
	// Create Date.
	private Date createDate;
	
	// Last Modified Time.
	private Date lastModified;

	/**
	 * Basic constructor
	 *  
	 */
	public AttributeBean() {		
	}
	
	/**
	 * Help constructor to minimize code lines.
	 * @param alias
	 * @param type
	 * @param refType
	 * @param complex
	 */
	public AttributeBean(String alias, String type, String refType, boolean complex) {
		setAlias(alias);
		setType(type);
		setRefType(refType);
		setComplexType(complex);
	}
	
	public AttributeBean(String displayName, String alias, String type, String refType, boolean complex) {
		setDisplayName(displayName);
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

	public String toString() {
		return (alias + "<" + type + ">[" + minOccurs + ".." + maxOccurs + " <<"
				+ refType + ">>]");
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
		// TODO Auto-generated method stub
		return (this.complexType);
	}

	public void setComplexType(boolean value) {
		this.complexType = value;
	}

	public String toXML(int level) {
		return(toXML(level, false));
	}
	
	public String toXML(int level, boolean compact) {
		StringBuffer buf = new StringBuffer();
		// Compact mode don't show derived attributes.
		if (compact && this.isDerived()) {
			return(buf.toString());
		}
		buf.append("\n");
		
		// Attributes
		buf.append(XmlParser.getTab(level) + "<"
				+ XmlParser.ATTRIBUTE_ELEMENT.getName());
		buf.append(" " + XmlParser.ATT_ALIAS_ATTR.getName() + "=\""
				+ this.getAlias() + "\"");
		if (this.getDisplayName() != null) {
			buf.append(" " + XmlParser.NAME_ATTR.getName() + "=\""
					+ this.getDisplayName() + "\"");
		}
		
		buf.append(" " + XmlParser.ATTR_DERIVED.getName() + "=\"" +
				this.isDerived() + "\"");

		if (this.getId() != null) {
			buf.append(" " + XmlParser.ID_ATTR.getName() + "=\"" +
					this.getId() + "\"");
		}
		
		if (this.getCreateDate() != null) {
			buf.append(" " + XmlParser.CREATE_DATE_ATTR.getName()
					+ "=\"" + CiBean.toXmlDateTime(this.getCreateDate()) + "\"");
		}
		
		if (this.getLastModified() != null) {
			buf.append(" " + XmlParser.LAST_MODIFIED_ATTR.getName()
					+ "=\"" + CiBean.toXmlDateTime(this.getLastModified()) + "\"");
		}
		
		buf.append(">");

		buf.append("\n");

		if (this.description != null) {
			buf.append(XmlParser.getTab(level + 1) + "<"
					+ XmlParser.DESCRIPTION_ELEMENT.getName() + ">");
			buf.append(CiBean.toXmlString(this.description));
			buf.append("</" + XmlParser.DESCRIPTION_ELEMENT.getName() + ">");
			buf.append("\n");
		}
		// Type
		if (this.isComplexType()) {
			buf.append(XmlParser.getTab(level + 1) + "<"
					+ XmlParser.COMPLEX_TYPE_ELEMENT.getName() + ">");
			buf.append("\n");
			buf.append(XmlParser.getTab(level + 2) + "<"
					+ XmlParser.REF_ELEMENT.getName() + " ");
			buf.append(XmlParser.ALIAS_ATTR.getName() + "=\"" + this.getType()
					+ "\"/>");

			buf.append("\n");
			if (this.getRefType() != null) {
				buf.append(XmlParser.getTab(level + 2) + "<"
						+ XmlParser.REF_TYPE_ELEMENT.getName() + ">");
				buf.append("<" + XmlParser.REF_ELEMENT.getName() + " ");
				buf.append(XmlParser.ALIAS_ATTR.getName() + "=\""
						+ this.getRefType() + "\"/>");
				buf.append("</" + XmlParser.REF_TYPE_ELEMENT.getName() + ">");
				buf.append("\n");
			}
			buf.append(XmlParser.getTab(level + 1) + "</"
					+ XmlParser.COMPLEX_TYPE_ELEMENT.getName() + ">");
			buf.append("\n");
		} else {
			buf.append(XmlParser.getTab(level + 1) + "<"
					+ XmlParser.SIMPLE_TYPE_ELEMENT.getName() + ">"
					+ this.getType() + "</"
					+ XmlParser.SIMPLE_TYPE_ELEMENT.getName() + ">");
			buf.append("\n");
		}

		// Policy
		if (this.getMaxOccurs() != null || this.getMinOccurs() != null) {
			buf.append(XmlParser.getTab(level + 1) + "<"
					+ XmlParser.POLICY_ELEMENT.getName() + ">");
			buf.append("\n");

			if (this.getMaxOccurs() != null) {
				buf.append(XmlParser.getTab(level + 2) + "<"
						+ XmlParser.MAX_OCCURS_ELEMENT.getName() + ">"
						+ this.getMaxOccurs() + "</"
						+ XmlParser.MAX_OCCURS_ELEMENT.getName() + ">");
				buf.append("\n");
			}
			if (this.getMinOccurs() != null) {
				buf.append(XmlParser.getTab(level + 2) + "<"
						+ XmlParser.MIN_OCCURS_ELEMENT.getName() + ">"
						+ this.getMinOccurs() + "</"
						+ XmlParser.MIN_OCCURS_ELEMENT.getName() + ">");
				buf.append("\n");
			}
			buf.append(XmlParser.getTab(level + 1) + "</"
					+ XmlParser.POLICY_ELEMENT.getName() + ">");
			buf.append("\n");
		}

		// End Attributes
		buf.append(XmlParser.getTab(level));
		buf.append("</" + XmlParser.ATTRIBUTE_ELEMENT.getName() + ">");

		return (buf.toString());
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return(this.id);
	}

	public void setIdAsString(String id) {
		if (id == null) {
			return;
		}
		this.id = Long.parseLong(id);
	}
	
	public String getIdAsString() {
		if (this.id == null) {
			return(null);
		}
		return(this.id.toString());
	}
	
 	public void setDerived(boolean b) {
		this.derived = b;
	}
	
	public boolean isDerived() {
		return(this.derived);
	}

	public AttributeBean copy() {
		AttributeBean copy = new AttributeBean();
		copy.setAlias(this.getAlias());
		copy.setId(getId());
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
}
