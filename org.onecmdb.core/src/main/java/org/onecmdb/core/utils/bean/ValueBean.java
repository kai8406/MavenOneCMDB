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
 * <code>ValueBean</code> represent an attribute value that is connected to a <code>CiBean</code>
 * 
 */
public class ValueBean {
	// The atribute alias name
	private String alias;

	// The acctual value, can be complex meaning it's an alias. 
	private String value;
	
	// Indicates that the value is complex, it's a reference.
	private boolean complexValue;
	
	// The value as another CiBean.
	private CiBean beanValue;

	// The back end id, Read-Only.
	private Long id;
	
	// Last Modified Time.
	private Date lastModified;

	public ValueBean() {
	}
	
	
	public ValueBean(String alias, String value, boolean complex) {
		setAlias(alias);
		setValue(value);
		setComplexValue(complex);
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isComplexValue() {
		return complexValue;
	}

	public void setComplexValue(boolean complexValue) {
		this.complexValue = complexValue;
		
	}

	
	public Long getId() {
		return(this.id);
	}


	public void setId(Long id) {
		this.id = id;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(CiBean bean) {
		this.beanValue = bean;
	}

	public CiBean getValueBean() {
		return(this.beanValue);
	}
	
	public void setValueBean(CiBean value) {
		this.beanValue = value;
	}

	public Date getLastModified() {
		return lastModified;
	}


	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}


	public String toXML(int level) {
		StringBuffer buf = new StringBuffer();

		// Value
		if (!this.isComplexValue()) {
			buf.append("\n");
			buf.append("\t\t<" + XmlParser.SET_SIMPLE_VALUE_ELEMENT.getName()
					+ " " + XmlParser.ATT_ALIAS_ATTR.getName() + "=\""
					+ this.getAlias() + "\"");
			
			if (this.getId() != null) {
					buf.append(" " + XmlParser.ID_ATTR.getName() + "=\"" +
							this.getId() + "\"");				
			}
			
			if (this.getLastModified() != null) {
				buf.append(" " + XmlParser.LAST_MODIFIED_ATTR.getName()
						+ "=\"" + CiBean.toXmlDateTime(this.getLastModified()) + "\"");
			}
		
			buf.append(">");
			buf.append(CiBean.toXmlString(this.getValue()));
			buf.append("</" + XmlParser.SET_SIMPLE_VALUE_ELEMENT.getName()
					+ ">");

		} else {
			buf.append("\n");
			buf.append("\t\t<" + XmlParser.SET_COMPLEX_VALUE_ELEMENT.getName()
					+ " " + XmlParser.ATT_ALIAS_ATTR.getName() + "=\""
					+ this.getAlias() + "\"");
			if (this.getId() != null) {
				buf.append(" " + XmlParser.ID_ATTR.getName() + "=\"" +
						this.getId() + "\"");				
			}
				
			buf.append(">");

			buf.append("<" + XmlParser.REF_ELEMENT.getName() + " "
					+ XmlParser.ALIAS_ATTR.getName() + "=\""
					+ this.getValue() + "\"/>");
			buf.append("</" + XmlParser.SET_COMPLEX_VALUE_ELEMENT.getName()
					+ ">");
		}
		return (buf.toString());
	}

	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("alias=" + this.alias + ", v=" + this.value + ", complex=" + this.isComplexValue());
		return(bf.toString());
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((alias == null) ? 0 : alias.hashCode());
		result = PRIME * result + (complexValue ? 1231 : 1237);
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ValueBean other = (ValueBean) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias)) {
			return false;
		}
		if (complexValue != other.complexValue) {
			return false;
		}
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value)) {
			return false;
		}

		return true;
	}


	public ValueBean copy() {
		ValueBean copy = new ValueBean();
		copy.setId(this.getId());
		copy.setAlias(this.getAlias());
		copy.setValue(this.getValue());
		copy.setComplexValue(this.isComplexValue());
		return(copy);
	}


	public boolean hasEmptyValue() {
		if (this.value == null) {
			return(true);
		}
		if (this.value.length() == 0) {
			return(true);
		}
		return(false);
	}
	
	
}
