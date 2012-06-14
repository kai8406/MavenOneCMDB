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

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code>ValueBean</code> represent an attribute value that is connected to a <code>CiBean</code>
 * 
 */
public class GWT_ValueBean implements IsSerializable {
	// The atribute alias name
	private String alias;

	// The acctual value, can be complex meaning it's an alias. 
	private String value;
	
	// Indicates that the value is complex, it's a reference.
	private boolean complexValue;
	
	// The value as another CiBean.
	private GWT_CiBean beanValue;

	// The back end id, Read-Only.
	private String idStr;

	private GWT_AttributeBean definition;
	
	// Create Date.
	private Date createDate;
	
	// Last Modified Time.
	private Date lastModified;


	public GWT_ValueBean() {
	}
	
	
	public GWT_ValueBean(String alias, String value, boolean complex) {
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

	public GWT_AttributeBean getDefinition() {
		return(this.definition);
	}
	
	public void setDefinition(GWT_AttributeBean aBean) {
		this.definition = aBean;
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


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(GWT_CiBean bean) {
		this.beanValue = bean;
	}

	public GWT_CiBean getValueBean() {
		return(this.beanValue);
	}
	
	public void setValueBean(GWT_CiBean value) {
		this.beanValue = value;
	}


	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("alias=" + this.alias + ", v=" + this.value + ", complex=" + this.isComplexValue());
		return(bf.toString());
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((alias == null) ? 0 : alias.hashCode());
		result = PRIME * result + (complexValue ? 1231 : 1237);
		
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GWT_ValueBean)) {
			return(false);
		}
		/*
		if (getClass() != obj.getClass()) {
			return false;
		}
		*/
		final GWT_ValueBean other = (GWT_ValueBean) obj;
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


	public GWT_ValueBean copy() {
		GWT_ValueBean copy = new GWT_ValueBean();
		copy.setIdAsString(this.getIdAsString());
		copy.setAlias(this.getAlias());
		copy.setValue(this.getValue());
		copy.setComplexValue(this.isComplexValue());
		return(copy);
	}


	public boolean hasValue() {
		if (value == null) {
			return(false);
		}
		if (value.length() == 0) {
			return(false);
		}
		return(true);
	}
	
	
}
