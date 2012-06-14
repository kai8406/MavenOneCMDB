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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.internal.model.primitivetypes.DateTimeType;
import org.onecmdb.core.utils.xml.XmlParser;

/**
 * <code>CiBean</code> contains data about a CI.
 * 
 * The CiBean can produce XML snippet of it's self. 
 *
 */
public class CiBean {
	// Alias name of this CI
	private String alias;

	// Alias name of derived from template.
	private String derivedFrom;
	
	// The Display name expression, not evaluated.
	private String displayNameExpression;

	// The evaluated display name.
	private String displayName;

	// Internal maps of alias to objects.
	private HashMap<String, List<ValueBean>> valueMap;
	private HashMap<String, AttributeBean> attributeMap = new HashMap<String, AttributeBean>();
	
	// All(derived and local) attributes for this ci.  
	private List<AttributeBean> attributes = new ArrayList<AttributeBean>();
	
	// All attribute values for this ci.
	private List<ValueBean> attributeValues = new ArrayList<ValueBean>();

	// Is this CI a template or not.
	private boolean template;

	// Description
	private String description;
	
	// The backend id of this ci.
	private Long id;

	// The securrity group this ci belongs to.
	private String group;
	
	// Create Date.
	private Date createDate;
	
	// Last Modified Time.
	private Date lastModified;
	/**
	 * Basic constructor.
	 *
	 */
	public CiBean() {
	}
	
	/**
	 * Help constructor, to minimize code lines.
	 * 
	 * @param derivedFrom
	 * @param alias
	 * @param template
	 */
	public CiBean(String derivedFrom, String alias, boolean template) {
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

	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		if (createDate == null) {
			this.createDate = null;
		} else {
			// Need to convert this to a date class for XFire to handle it correct.
			this.createDate = new Date(createDate.getTime());
		}
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		if (lastModified == null) {
			this.lastModified = null;
		} else {
			// Need to convert this to a date class for XFire to handle it correct.
			this.lastModified = new Date(lastModified.getTime());
		}
	}

	public boolean removeAttribute(String alias) {
		AttributeBean aBean = this.attributeMap.get(alias);
		if (aBean != null) {
			removeAttribute(aBean);
			return(true);
		}
		return(false);
	}
	
	public void removeAttribute(AttributeBean aBean) {
		if (aBean != null) {
			this.attributes.remove(aBean);
			this.attributeMap.remove(alias);
		}
	}
	
	public void addAttribute(AttributeBean aBean) {
		this.attributes.add(aBean);
		this.attributeMap.put(aBean.getAlias(), aBean);
	}

	public void setAttributes(List<AttributeBean> attributes) {
		for (AttributeBean aBean : attributes) {
			addAttribute(aBean);
		}
	}

	public List<AttributeBean> getAttributes() {
		List<AttributeBean> copy = new ArrayList<AttributeBean>(this.attributes);
		return (copy);
	}
	
	
	public AttributeBean getAttribute(String alias) {
		return(this.attributeMap.get(alias));
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

	
	public void setAttributeValues(List<ValueBean> beans) {
		for (ValueBean vBean : beans) {
			addAttributeValue(vBean);
		}
	}
	
	public List<ValueBean> getAttributeValues() {
		List<ValueBean> copy = new ArrayList<ValueBean>(this.attributeValues);
		return (copy);
	}

	public void removeAttributeValue(ValueBean bean) {
		this.attributeValues.remove(bean);
		List<ValueBean> valueMap = this.valueMap.get(bean.getAlias());
		if (valueMap != null) {
			valueMap.remove(bean);
		}
		
	}
	
	public void addAttributeValue(ValueBean vBean) {
		if (valueMap == null) {
			valueMap = new HashMap<String, List<ValueBean>>();
		}
		this.attributeValues.add(vBean);
		String alias = vBean.getAlias();
		if (alias == null) {
			throw new IllegalArgumentException(
					"Alias on attribute can not be null!");
		}
		List<ValueBean> list = valueMap.get(alias);
		if (list == null) {
			list = new ArrayList<ValueBean>();
			valueMap.put(alias, list);
		}
		list.add(vBean);
	}

	public String[] fetchAttributeValueAliases() {
		if (valueMap == null) {
			return (new String[0]);
		}
		List<String> result = new ArrayList<String>();
		result.addAll(valueMap.keySet());
		return (result.toArray(new String[0]));
	}

	public List<ValueBean> fetchAttributeValueBeans(String alias) {
		if (valueMap == null) {
			return (Collections.EMPTY_LIST);
		}
		List<ValueBean> list = valueMap.get(alias);
		if (list == null) {
			return (Collections.EMPTY_LIST);
		}
		// Clone
		
		List<ValueBean> copy = new ArrayList<ValueBean>(list);
		return (copy);
	}

	public ValueBean fetchAttributeValueBean(String name, int index) {
		List<ValueBean> list = fetchAttributeValueBeans(name);
		if (list == null) {
			return (null);
		}
		if (list.size() <= index) {
			return (null);
		}
		return (list.get(index));
	}

	public List<String> fetchSortedAttributeValueAliases() {
		//List<String> aliasSet = getAttributeValueAliases();
		//String[] aStr = aliasSet.toArray(new String[0]);
		String[] aStr = fetchAttributeValueAliases();
		Arrays.sort(aStr);
		return ((List<String>) Arrays.asList(aStr));
	}

	public String toXML(int level) {
		return(toXML(level, false));
	}
	
	public String toXML(int level, boolean compact) {
		if (template) {
			return (toXMLAsTemplate(level, compact));
		}
		return (toXMLAsInstance(level));
	}

	private String toXMLAsInstance(int level) {
		StringBuffer buf = new StringBuffer();

		buf.append(XmlParser.getTab(level) + "<" + this.getDerivedFrom()
				+ " alias=\"" + this.getAlias() + "\"");
		
		if (this.getId() != null) {
			buf.append(" " + XmlParser.ID_ATTR.getName() + "=\"" +
					this.getId() + "\"");				
		}
		
		
		if (this.getDisplayNameExpression() != null) {
			buf.append(" " + XmlParser.INSTANCE_NAME_EXPRESSION_ATTR.getName()
					+ "=\"" + toXmlString(this.getDisplayNameExpression()) + "\"");
		}
		
		if (this.getGroup() != null) {
			buf.append(" " + XmlParser.GROUP_ATTR.getName()
					+ "=\"" + toXmlString(this.getGroup()) + "\"");
		}
		if (this.getCreateDate() != null) {
			buf.append(" " + XmlParser.CREATE_DATE_ATTR.getName()
					+ "=\"" + toXmlDateTime(this.getCreateDate()) + "\"");
		}
		if (this.getLastModified() != null) {
			buf.append(" " + XmlParser.LAST_MODIFIED_ATTR.getName()
					+ "=\"" + toXmlDateTime(this.getLastModified()) + "\"");
		}
		if (this.getDisplayName() != null) {
			buf.append(" displayValue"  
					+ "=\"" + toXmlString(this.getDisplayName()) + "\"");
		}
		
		
		buf.append(">");		
		buf.append("\n");
		
		if (this.getDisplayName() != null) {
			buf.append(XmlParser.getTab(level +1) + toXmlString(this.getDisplayName()));
			buf.append("\n");
		}
		
		if (this.getDescription() != null) {
			buf.append(XmlParser.getTab(level + 1) + "<" + XmlParser.DESCRIPTION_ELEMENT.getName() + ">");
			buf.append(this.getDescription());
			buf.append("</" + XmlParser.DESCRIPTION_ELEMENT.getName() + ">");
			buf.append("\n");
		}
		
		for (String name : this.fetchAttributeValueAliases()) {
			for (ValueBean vBean : this.fetchAttributeValueBeans(name)) {
				
				if (vBean.getValue() == null) {
					continue;
				}
				buf.append(XmlParser.getTab(level + 1) + "<" + name);
				if (vBean.getId() != null) {
					buf.append(" " + XmlParser.ID_ATTR.getName() + "=\"" + vBean.getId() + "\"");
				}
				buf.append(">");
				if (vBean.getValueBean() != null) {
					buf.append("\n");
					buf.append(vBean.getValueBean().toXML(level+2));
					buf.append("\n");
					buf.append(XmlParser.getTab(level + 1) + "</" + name + ">");
				} else {
					if (!vBean.isComplexValue()) {
						buf.append(toXmlString(vBean.getValue()));
					} else {
						buf.append("<" + XmlParser.REF_ELEMENT.getName() + " "
								+ XmlParser.ALIAS_ATTR.getName() + "=\""
								+ vBean.getValue() + "\"/>");
					}  
				}
				buf.append("</" + name + ">");
				
				buf.append("\n");
			}
		}
		buf
				.append(XmlParser.getTab(level) + "</" + this.getDerivedFrom()
						+ ">");
		return (buf.toString());
	}
	private String toXMLAsTemplate(int level) {
		return(toXMLAsTemplate(level, false));
	}
	private String toXMLAsTemplate(int level, boolean compact) {

		StringBuffer buf = new StringBuffer();

		buf.append(XmlParser.getTab(level) + "<"
				+ XmlParser.TEMPLATE_ELEMENT.getName() + " "
				+ XmlParser.ALIAS_ATTR.getName() + "=\"" + this.getAlias()
				+ "\"");
		if (this.getId() != null) {
			buf.append(" " + XmlParser.ID_ATTR.getName() + "=\"" +
					this.getId() + "\"");				
		}
		if (this.getDisplayNameExpression() != null) {
			buf.append(" " + XmlParser.INSTANCE_NAME_EXPRESSION_ATTR.getName()
					+ "=\"" + toXmlString(this.getDisplayNameExpression()) + "\"");
		}
		if (this.getGroup() != null) {
			buf.append(" " + XmlParser.GROUP_ATTR.getName()
					+ "=\"" + toXmlString(this.getGroup()) + "\"");
		}
		if (this.getCreateDate() != null) {
			buf.append(" " + XmlParser.CREATE_DATE_ATTR.getName()
					+ "=\"" + toXmlDateTime(this.getCreateDate()) + "\"");
		}
		if (this.getLastModified() != null) {
			buf.append(" " + XmlParser.LAST_MODIFIED_ATTR.getName()
					+ "=\"" + toXmlDateTime(this.getLastModified()) + "\"");
		}


		buf.append(">");
		buf.append("\n");
		
		// Derived from.
		String derivedFrom = this.getDerivedFrom(); 
		if (derivedFrom != null) {
			buf.append(XmlParser.getTab(level + 1) + "<"
					+ XmlParser.DERIVED_FROM_ELEMENT.getName() + ">");
			buf.append("\n");
			buf.append(XmlParser.getTab(level + 2) + "<"
					+ XmlParser.REF_ELEMENT.getName() + " ");
			
			buf.append(XmlParser.ALIAS_ATTR.getName() + "=\""
					+  (derivedFrom == null ? "" : derivedFrom) + "\"/>");
			buf.append("\n");
			buf.append(XmlParser.getTab(level + 1) + "</"
					+ XmlParser.DERIVED_FROM_ELEMENT.getName() + ">");
			buf.append("\n");
		}
		
		if (this.description != null) {
			buf.append(XmlParser.getTab(level + 1) + "<"
					+ XmlParser.DESCRIPTION_ELEMENT.getName() + ">");
			//buf.append("\n");
			//buf.append(XmlParser.getTab(level + 2) + toXmlString(this.description));
			buf.append(toXmlString(this.description));
			//buf.append("\n");
			buf.append("</"
					+ XmlParser.DESCRIPTION_ELEMENT.getName() + ">");			
		}

		for (AttributeBean aBean : this.getAttributes()) {
			//if (!aBean.isDerived()) {
				buf.append(aBean.toXML(level + 1, compact));
			//}
		}

		for (String name : this.fetchSortedAttributeValueAliases()) {
			for (ValueBean vBean : this.fetchAttributeValueBeans(name)) {
				if (vBean.getValue() != null) {
					buf.append(vBean.toXML(level + 1));
				}
			}
		}
		buf.append("\n");
		buf.append(XmlParser.getTab(level) + "</"
				+ XmlParser.TEMPLATE_ELEMENT.getName() + ">");
		return (buf.toString());
	}



	
	public static String toXmlDateTime(Date d) {
		if (d == null) {
			return(null);
		}
		String str = DateTimeType.parseDate(d);
		return(str);
	}
	
	public static String toXmlString(String s) {
		if (s == null) {
			return(null);
		}
		s = s.trim();
	    StringBuffer sb = new StringBuffer();
	    int len = s.length();
	    for (int i = 0; i < len; i++) {
	      char c = s.charAt(i);
	      switch (c) {
	      default:
	        sb.append(c);
	        break;
	      case '<':
	        sb.append("&lt;");
	        break;
	      case '>':
	        sb.append("&gt;");
	        break;
	      case '&':
	        sb.append("&amp;");
	        break;
	      case '"':
	        sb.append("&quot;");
	        break;
	      case '\'':
	        sb.append("&apos;");
	        break;
	      }
	    }
	    return(sb.toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CiBean) {
			return (((CiBean)obj).hashCode() == this.hashCode());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		if (this.alias == null) {
			return(super.hashCode());
		}
		return(this.alias.hashCode());
	}

	public CiBean copy() {
		CiBean copy = new CiBean();
		copy.setAlias(this.getAlias());
		copy.setDerivedFrom(this.getDerivedFrom());
		copy.setDescription(this.getDescription());
		copy.setTemplate(this.isTemplate());
		copy.setDisplayNameExpression(this.getDisplayNameExpression());
		copy.setDisplayName(this.getDisplayName());
		copy.setId(this.getId());
		copy.setCreateDate(this.getCreateDate());
		copy.setLastModified(this.getLastModified());
		
		for (AttributeBean aBean : getAttributes()) {
			copy.addAttribute(aBean.copy());
		}
		for (ValueBean vBean : getAttributeValues()) {
			copy.addAttributeValue(vBean.copy());
		}
		return(copy);
	}

	public void clearAttributes() {
		for (AttributeBean aBean : getAttributes()) {
			removeAttribute(aBean);
		}
		
	}

	public void clearAttributeValues() {
		for (ValueBean vBean : getAttributeValues()) {
			removeAttributeValue(vBean);
		}
	}

	public String toStringValue(String alias) {
		List<ValueBean> values = fetchAttributeValueBeans(alias);
		if (values.size() == 0) {
			return("");
		}
		if (values.size() == 1) {
			String value = values.get(0).getValue();
			if (value == null) {
				return("");
			}
			return(value);
		}
		StringBuffer b = new StringBuffer();
		b.append("[");
		boolean first = true;
		for (ValueBean v : values) {
			if (first) {
				first = false;
			} else {
				b.append(", ");
			}
			b.append(v.getValue() == null ? "" : v.getValue());
		}
		b.append("]");
		return(b.toString());
		
	}

}
