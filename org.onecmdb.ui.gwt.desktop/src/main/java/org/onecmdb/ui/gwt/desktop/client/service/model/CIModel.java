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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CIModel extends ModelItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<AttributeModel> attributes = new ArrayList<AttributeModel>();
	List<ValueModel> values = new ArrayList<ValueModel>();
	ValueListModel valueList;
	
	
	public static final String CI_ATTRIBUTES = "internal_attributes";
	public static final String CI_DERIVEDFROM = "internal_derivedFrom";
	public static final String CI_ID = "internal_id";
	public static final String CI_ALIAS = "internal_alias";
	public static final String CI_DESCRIPTION = "internal_description";
	public static final String CI_DISPLAYNAME = "internal_displayname";
	public static final String CI_DISPLAYNAMEEXPR = "internal_displayNameExpr";
	public static final String CI_ISTEMPLATE = "internal_isTemplate";
	public static final String CI_LASTMODIFIED = "internal_lastModified";
	public static final String CI_CREATED = "internal_created";
	

	public static final String CI_TOTAL_INSTANCE_COUNT = "statistic_totalInstanceCount";
	public static final String CI_TEMPLATE_CHILD_COUNT = "statistic_templateChildCount";
	public static final String CI_INSTANCE_CHILD_COUNT = "statistic_instanceCount";
	
	public static final String CI_ICON_PATH = "display_iconPath";

	public static final String VALUE_PREFIX = "value_";
	public static final String ATTRBUTE_PREFIX = "attribute_";
	public static final String INTERNAL_PREFIX = "internal_";

	public static final String CI_NAME_AND_ICON = "nameAndIcon";

	public static final String SELECTOR_ID = "selector_id";


	
	private static List<AttributeModel> emptyAttributes = new ArrayList<AttributeModel>();
	
	@Override
	public int hashCode() {
		String id = getIdAsString();
		if (id != null) {
			return(id.hashCode());
		}
		String alias = getAlias();
		if (alias != null) {
			return(alias.hashCode());
		}
		return(super.hashCode());
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return(false);
		}
		if (!(obj instanceof CIModel)) {
			return(false);
		}
		return(this.hashCode() == obj.hashCode());
	}


	@Override
	public <X> X get(String property) {
		if (property.equals(CI_NAME_AND_ICON)) {
			return((X)getNameAndIcon());
		}
		/*
		if ("name".equals(property)) {
			return(super.get("derivedFrom"));
		} 
		if ("value".equals(property)) {
			return((X)getDisplayName());
		} 
		*/
		// TODO Auto-generated method stub
		X value =  (X)super.get(property);
		
		/*
		if (value instanceof ValueModel) {
			System.out.println("COPY Value....");
			value = (X) ((ValueModel)value).copy();
		}
		*/
		return(value);
	}
	
	@Override
	public <X> X set(String name, X value) {
		//System.out.println("SET " + name + " to " + value);
		if (name.startsWith(VALUE_PREFIX)) {
			String valueAlias = name.substring(VALUE_PREFIX.length()); 
			Object v = get(name);
			// Check for if a value has been added then, then it should be 
			// removed if we set it null again. Typical when the base CI needs to
			// revert all changes.
			if (v instanceof ValueModel) {
				if (((ValueModel)v).getIdAsString() == null && value == null) {
					// Remove this attribute...
					super.remove(name);
					return(null);
				}
			}
			/*
			if (value == null) {
				super.set(name, null);
				return(value);
			}
			*/
			if (v == null) {
				if (value instanceof ValueListModel) {
					v = new ValueListModel();
				} else {
					v = new ValueModel();
				}
				((ValueModel)v).setAlias(valueAlias);
			}			
			if (v instanceof ValueModel) {
				ValueModel vm = (ValueModel)v;
				
				vm = vm.copy();
				if (value == null) {
					vm.setValue(null);
					vm.setValueDisplayName("");
					vm.set(CIModel.CI_ICON_PATH, null);
				}  else if (value instanceof CIModel) {
					vm.setValue(((CIModel)value).getAlias());
					vm.setValueDisplayName(((CIModel)value).getDisplayName());
					vm.set(CIModel.CI_ICON_PATH, ((CIModel)value).get(CIModel.CI_ICON_PATH));
					vm.setIsComplex(true);
				} else if (value instanceof String) {
					vm.setValue((String)value);
					vm.setValueDisplayName((String)value);
					vm.setIsComplex(false);
				} else if (value instanceof ValueModel) {
					vm = (ValueModel)value;
				}
				setValue(valueAlias, vm);
				return(value);
			}
		}
		
		return super.set(name, value);
	}


	public void addAttribute(AttributeModel a) {
		super.set(ATTRBUTE_PREFIX + a.getAlias(), a);
	}
	
	public void removeAttribute(AttributeModel a) {
		super.remove(ATTRBUTE_PREFIX + a.getAlias());
	}

	public void setValue(String alias, ValueModel v) {
		//values.add(v);
		super.set(VALUE_PREFIX + alias, v);
	}
	
	public ValueModel getValue(String alias) {
		//values.add(v);
		return(super.get(VALUE_PREFIX + alias));
	}
	
	public void setAttribute(List<AttributeModel> attrs) {
		for (AttributeModel a : attrs) {
			addAttribute(a);
		}
	}
	
	public String getNameAndIcon() {
		String text = "";
		
		if (isTemplate()) {
			text = getAlias();
		} else {
			text = getDisplayName();
		}
		
		if (text == null || text.length() == 0) {
			text = "[" + getAlias() + "]";
		}
		String url = get(CIModel.CI_ICON_PATH);
		if (url != null) {
			url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
			text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
		}
		return(text);
	}
	/**
	 * To avoid recursion...
	 */
	@Override
	 public String toString() {
		 return("CI - " + getAlias());
	 }

	public String getDisplayName() {
		String name = get(CI_DISPLAYNAME);
		if (isTemplate() && (name == null || name.length() == 0)) {
			return(getAlias());
		}
		return(name);
	}

	public void setDisplayName(String displayName) {
		super.set(CI_DISPLAYNAME, displayName);
	}

	public String getDerivedFrom() {
		return(get(CI_DERIVEDFROM));
	}

	public void setDerivedFrom(String derivedFrom) {
		super.set(CI_DERIVEDFROM, derivedFrom);
	}

	public void setAlias(String alias) {
		super.set(CI_ALIAS, alias);
	}

	public String getAlias() {
		return(get(CI_ALIAS));
	}


	public void setDescription(String description) {
		super.set(CI_DESCRIPTION, description);
	}
	public String getDescription() {
		return(get(CI_DESCRIPTION));
	}





	public void setTemplate(boolean template) {
		super.set(CI_ISTEMPLATE, template);
	}
	
	public boolean isTemplate() {
		return(get(CI_ISTEMPLATE, false));
	}

	public void setDisplayNameExpression(String displayNameExpression) {
		super.set(CI_DISPLAYNAMEEXPR, displayNameExpression);
	}
	
	public String getDisplayNameExpression() {
		return(get(CI_DISPLAYNAMEEXPR));
	}


	public CIModel newInstance() {
		CIModel model = new CIModel();
		model.setDerivedFrom(getAlias());
		model.setAlias(getAlias() + System.currentTimeMillis());
		for (ValueModel vModel : getValues()) {
			AttributeModel aModel = getAttribute(vModel.getAlias());
			if (aModel != null) {
				/*
				if (aModel.getMinOccur().equals("0") && aModel.getMaxOccur().equals("1")) {
					continue;
				}
				*/
			}
			ValueModel copy = vModel.copy();
			if ((copy instanceof ValueListModel)) {
				ArrayList<ValueModel> list = new ArrayList<ValueModel>();
				list.addAll(((ValueListModel)copy).getValues());
				for (ValueModel v : list) {
					if (v.getValue() == null || v.getValue().length() == 0) {
						((ValueListModel)copy).removeValue(v);
					}
				}
				//((ValueListModel)copy).getValues().clear();
			}
			model.setValue((String)vModel.get(ValueModel.VALUE_ALIAS), copy);
		}
		model.setTemplate(false);
		return(model);
	}


	public <X> X setProperty(String name, X value) {
		return(super.set(name, value));
	}
	public <X> X getProperty(String name) {
		return((X)super.get(name));
	}


	@Override
	public CIModel copy() {
		CIModel model = new CIModel();
		copy(model);
		return(model);
	}

	public AttributeModel getAttribute(String alias) {
		return(get(ATTRBUTE_PREFIX + alias));
	}

	public List<AttributeModel> getAttributes() {
		List<AttributeModel> attr = new ArrayList<AttributeModel>();
		for (String key : getPropertyNames()) {
			if (key.startsWith(ATTRBUTE_PREFIX)) {
				attr.add((AttributeModel)get(key));
			}
		}
		return(attr);
	}


	public List<ValueModel> getValues() {
		List<ValueModel> values = new ArrayList<ValueModel>();
		for (String key : getPropertyNames()) {
			if (key.startsWith(VALUE_PREFIX)) {
				values.add((ValueModel)get(key));
			}
		}
		return(values);
	}

	public void removeValue(ValueModel vm) {
		map.remove(VALUE_PREFIX + vm.getAlias());
	}


	public void setIdAsString(String id) {
		set(CI_ID, id);
	}
	
	public String getIdAsString() {
		return(get(CI_ID));
	}

	public Date getLastModifiedDate() {
		return(get(CI_LASTMODIFIED));
	}
	public void setLastModifiedDate(Date date) {
		set(CI_LASTMODIFIED, date);
	}
	
	public Date getCreateDate() {
		return(get(CI_CREATED));
	}
	
	public void setCreateDate(Date date) {
		set(CI_CREATED, date);
	}


	public String getValueAsString(String name) {
		ValueModel v = getValue(name);
		if (v == null) {
			return("");
		}
		return(v.getValue());
		
	}






	

}
