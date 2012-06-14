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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * <code>CiBean</code> contains data about a CI.
 * 
 * The CiBean can produce XML snippet of it's self. 
 *
 */
public class GWT_CiBean implements IsSerializable, Serializable {
	// Alias name of this CI
	private String alias;

	// Alias name of derived from template.
	private String derivedFrom;
	
	// The Display name expression, not evaluated.
	private String displayNameExpression;

	// The evaluated display name.
	private String displayName;

	// Internal maps of alias to objects.
	// <String, List<GWT_ValueBean>>
	/**
	 * @gwt.typeArgs <java.lang.String, java.util.List<org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean>>
	 */
	private HashMap valueMap;
	
	//<String, GWT_AttributeBean>
	/**
	 * @gwt.typeArgs <java.lang.String, org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean>
	 */
	private HashMap attributeMap = new HashMap();
	
	// All(derived and local) attributes for this ci.
	//<GWT_AttributeBean>
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean>
	 */
	private List attributes = new ArrayList();
	
	// All attribute values for this ci.
	//<GWT_ValueBean>
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean>
	 */
	private List attributeValues = new ArrayList();

	// Is this CI a template or not.
	private boolean template;

	// Description
	private String description;
	
	// The backend id of this ci.
	private String idStr;

	// The group alias for this ci.
	private String group;
	
	// Create Date.
	private Date createDate;
	
	// Last Modified Time.
	private Date lastModified;

	/**
	 * Basic constructor.
	 *
	 */
	public GWT_CiBean() {
	}
	
	/**
	 * Help constructor, to minimize code lines.
	 * 
	 * @param derivedFrom
	 * @param alias
	 * @param template
	 */
	public GWT_CiBean(String derivedFrom, String alias, boolean template) {
		this.setDerivedFrom(derivedFrom);
		this.setAlias(alias);
		this.setTemplate(template);
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setDescription(String d) {
		this.description = d;
	}

	public String getDescription() {
		return (this.description);
	}

	public boolean removeAttribute(String alias) {
		GWT_AttributeBean aBean = (GWT_AttributeBean)this.attributeMap.get(alias);
		if (aBean != null) {
			this.attributes.remove(aBean);
			this.attributeMap.remove(alias);
			return(true);
		}
		return(false);
	}
	
	public void removeAttributeValues() {
		this.attributeValues.clear();		
		if (this.valueMap != null) {
			this.valueMap.clear();
		}
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

	public void addAttribute(GWT_AttributeBean aBean) {
		this.attributes.add(aBean);
		this.attributeMap.put(aBean.getAlias(), aBean);
		aBean.setParentCI(this);
	}

	/**
	 * 
	 * @param attributes List<GWT_AttributeBean>
	 */
	public void setAttributes(List attributes) {
		for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean) iter.next();
			addAttribute(aBean);
		}
	}
	
	/**
	 * 
	 * @return List<GWT_AttributeBean>
	 */
	public List getAttributes() {
		return (new ArrayList(this.attributes));
	}
	
	
	public GWT_AttributeBean getAttribute(String alias) {
		return (GWT_AttributeBean) (this.attributeMap.get(alias));
	}

	public String getDerivedFrom() {
		return derivedFrom;
	}

	public void setDerivedFrom(String derivedFrom) {
		this.derivedFrom = derivedFrom;
	}

	public String getDisplayNameExpression() {
		return displayNameExpression;
	}

	public void setDisplayNameExpression(String displayNameExpression) {
		this.displayNameExpression = displayNameExpression;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return(this.displayName);
	}
	public String toString() {
		return (alias + " extends " + derivedFrom);
	}

	public void setTemplate(boolean template) {
		this.template = template;

	}

	public boolean isTemplate() {
		return (this.template);
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
	
	

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * 
	 * @param beans List<GWT_ValueBean>
	 */
	public void setAttributeValues(List beans) {
		for (Iterator iter = beans.iterator(); iter.hasNext();) {
			GWT_ValueBean vBean = (GWT_ValueBean) iter.next();
			addAttributeValue(vBean);
		}
	}
	
	/**
	 * 
	 * @return List<GWT_ValueBean>
	 */ 
	public List getAttributeValues() {
		return(this.attributeValues);
	}

	public void addAttributeValue(GWT_ValueBean vBean) {
		if (valueMap == null) {
			valueMap = new HashMap();
		}
		this.attributeValues.add(vBean);
		String alias = vBean.getAlias();
		if (alias == null) {
			throw new IllegalArgumentException(
					"Alias on attribute can not be null!");
		}
		// List<GWT_ValueBean>
		List list = (List) valueMap.get(alias);
		if (list == null) {
			list = new ArrayList();
			valueMap.put(alias, list);
		}
		list.add(vBean);
	}

	public String[] fetchAttributeValueAliases() {
		if (valueMap == null) {
			return (new String[0]);
		}
		// <String>
		List result = new ArrayList();
		result.addAll(valueMap.keySet());
		return (String[]) (result.toArray(new String[0]));
	}

	public void updateAttributeValues(String alias, List values) {
		valueMap.put(alias, values);
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			GWT_ValueBean vBean = (GWT_ValueBean) iter.next();
			if (!this.attributeValues.contains(vBean)) {
				this.attributeValues.add(vBean);
			}
		}
	}
	
	public boolean removeAttributeValues(String alias) {
		List list = (List) valueMap.get(alias);
		if (list == null) {
			return(false);
		}
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			GWT_ValueBean vBean = (GWT_ValueBean) iter.next();
			this.attributeValues.remove(vBean);
		}
		valueMap.remove(alias);
		return(true);
	}

	
	
	/**
	 * 
	 * @param alias
	 * @return List<GWT_ValueBean>
	 */
	public List fetchAttributeValueBeans(String alias) {
		if (valueMap == null) {
			return (new ArrayList());
		}
		// <GWT_ValueBean>
		List list = (List) valueMap.get(alias);
		if (list == null) {
			return (new ArrayList());
		}
		// Clone array.
		List result = new ArrayList(list);
		return (result);
	}

	public GWT_ValueBean fetchAttributeValueBean(String name, int index) {
		//<GWT_ValueBean>
		List list = fetchAttributeValueBeans(name);
		if (list == null) {
			return (null);
		}
		if (list.size() <= index) {
			return (null);
		}
		return ((GWT_ValueBean)list.get(index));
	}

	public List fetchSortedAttributeValueAliases() {
		//List<String> aliasSet = getAttributeValueAliases();
		//String[] aStr = aliasSet.toArray(new String[0]);
		String[] aStr = fetchAttributeValueAliases();
		Arrays.sort(aStr);
		return ((List) Arrays.asList(aStr));
	}

	

	public boolean equals(Object obj) {
		if (obj instanceof GWT_CiBean) {
			return (((GWT_CiBean)obj).hashCode() == this.hashCode());
		}
		return super.equals(obj);
	}

	public int hashCode() {
		if (this.alias == null) {
			return(super.hashCode());
		}
		return(this.alias.hashCode());
	}

	public GWT_CiBean copy() {
		GWT_CiBean copy = new GWT_CiBean();
		copy.setAlias(this.getAlias());
		copy.setDerivedFrom(this.getDerivedFrom());
		copy.setDescription(this.getDescription());
		copy.setTemplate(this.isTemplate());
		copy.setDisplayNameExpression(this.getDisplayNameExpression());
		copy.setDisplayName(this.getDisplayName());
		copy.setIdAsString(this.getIdAsString());
		copy.setGroup(this.getGroup());
		for (Iterator iter = getAttributes().iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			copy.addAttribute(aBean.copy());
		}
		for (Iterator iter = getAttributeValues().iterator(); iter.hasNext(); ) {
			GWT_ValueBean vBean = (GWT_ValueBean) iter.next();
			copy.addAttributeValue(vBean.copy());
		}
		return(copy);
	}

	public void removeAttributeValue(GWT_ValueBean vBean) {
		this.attributeValues.remove(vBean);
		String alias = vBean.getAlias();
		if (alias == null) {
			throw new IllegalArgumentException(
					"Alias on attribute can not be null!");
		}
		// List<GWT_ValueBean>
		List list = (List) valueMap.get(alias);
		if (list != null) {
			list.remove(vBean);
		}
	}

	
	public void removeAttributes() {
		attributeMap.clear();
		attributes.clear();
		
	}

	public String toStringValue(String alias) {
		List values = fetchAttributeValueBeans(alias);
		if (values.size() == 0) {
			return("");
		}
		if (values.size() == 1) {
			return(((GWT_ValueBean)values.get(0)).getValue());
		}
		StringBuffer b = new StringBuffer();
		b.append("[");
		boolean first = true;
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			GWT_ValueBean v = (GWT_ValueBean) iter.next();
			if (first) {
				first = false;
			} else {
				b.append(", ");
			}
			b.append(v.getValue());
		}
		b.append("]");
		return(b.toString());
		
	}
	
	
}
