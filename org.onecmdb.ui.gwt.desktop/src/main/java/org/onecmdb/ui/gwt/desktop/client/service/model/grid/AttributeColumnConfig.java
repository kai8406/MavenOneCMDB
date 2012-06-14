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
package org.onecmdb.ui.gwt.desktop.client.service.model.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;

import com.extjs.gxt.ui.client.data.BaseModel;

public class AttributeColumnConfig extends BaseModel implements Serializable {
	private String id;
	private String name;
	private String type;
	private String refType;
	private boolean complex;
	private String iconPath;
	private int maxOccurs;
	private int width = 60;
	private boolean editable = false;
	private ContentData mdr;
	private boolean selectTemplates = false;
	private boolean hidden = false;
	
	protected int getInt(String key, int def) {
		Object o = get(key);
	
		if (o == null) {
			return(def);
		}
	
		if (o instanceof Integer) {
			return(((Integer)o).intValue());
		}
		try {
			Integer value = Integer.parseInt(o.toString());
			return(value.intValue());
		} catch (Throwable t) {
			return(def);
		}
	}
	
	protected boolean getBoolean(String key, boolean def) {
		Object o = get(key);
	
		if (o == null) {
			return(def);
		}
	
		if (o instanceof Boolean) {
			return(((Boolean)o).booleanValue());
		}
		try {
			Boolean value = Boolean.parseBoolean(o.toString());
			return(value.booleanValue());
		} catch (Throwable t) {
			return(def);
		}
	}

	public String getId() {
		return(get("id"));
	}
	public void setId(String id) {
		set("id", id);
	}
	public String getName() {
		return(get("name"));
	}
	public void setName(String name) {
		set("name", name);
	}
	public String getType() {
		return(get("type"));
	}
	public void setType(String type) {
		set("type", type);
	}
	public String getRefType() {
		return (get("refType"));
	}
	public void setRefType(String refType) {
		set("refType", refType);
	}
	public boolean isComplex() {
		return(get("complex", false));
	}
	public void setComplex(boolean complex) {
		set("complex", complex);
	}
	public int getMaxOccurs() {
		return(getInt("maxOccurs", 1));
	}
	public void setMaxOccurs(int maxOccurs) {
		set("maxOccurs", maxOccurs);
	}
	public int getWidth() {
		return(getInt("width", 140));
	}
	public void setWidth(int width) {
		set("width", width);;
	}
	public boolean isEditable() {
		return(getBoolean("editable", true));
	}
	public void setEditable(boolean editable) {
		set("editable", editable);
	}
	
	public boolean isHidden() {
		return(getBoolean("hidden", false));
	}

	public void setHidden(boolean hidden) {
		set("hidden", hidden);
	}

	public ContentData getMDR() {
		return(get("mdr", CMDBSession.get().getDefaultCMDB_MDR()));
	}

	public void setMDR(ContentData mdr) {
		set("mdr", mdr);
	}
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public boolean isSelectTemplates() {
		return selectTemplates;
	}
	public void setSelectTemplates(boolean selectTemplates) {
		this.selectTemplates = selectTemplates;
	}

	public boolean isInternal() {
		// For now check if id contaions "internal_"
		return(getId().contains(CIModel.INTERNAL_PREFIX));
	}
	
	public void setDescription(String desc) {
		set("description", desc);
	}
	
	public String getDescription() {
		return(get("description", ""));
	}

	public void setAlias(String alias) {
		set("alias", alias);
	}
	public String getAlias() {
		return(get("alias", ""));
	}

	public void setSearchable(boolean b) {
		set("searchable", b);
	}
	
	public boolean isSearchable() {
		return(getBoolean("searchable",  true));
		
	}

	public GroupDescription getGroupDescription() {
		return(get("groupDescription"));
	}
	public void setGroupDescription(GroupDescription desc) {
		set("groupDescription", desc);
	}
	

	public boolean isReference() {
		return(getBoolean("reference", false));
	}
	
	public void setReference(boolean value) {
		set("reference", value);
	}

	public List<String> getEnumValues() {
		return(get("enum", new ArrayList<String>()));
	}
	
	/**
	 * The base model must contains:
	 * key
	 * value
	 * @param values
	 */
	public void setEnumValues(List<String> values) {
		set("enum", values);
	}

	public void addRadio(String v) {
		List<String> types = get("radio");
		if (types == null) {
			types = new ArrayList<String>();
			set("radio", types);
		}
		types.add(v);
	}
	public List<String> getRadios() {
		return(get("radio", new ArrayList<String>()));
	}

	public String getContentRoot() {
		return(get("contentRoot"));
	}
	public void setContentRoot(String root) {
		set("contentRoot", root);
	}

	public void setComboValues(List<? extends BaseModel> values) {
		set("comboValues", values);
	}
	public List<BaseModel> getComboValues() {
		return(get("comboValues", new ArrayList()));
	}
	public void setComboProperty(String id) {
		set("comboProperty", id);
	}
	public String getComboProperty() {
		return(get("comboProperty"));
	}

	public String getCIProperty() {
		return(get("ciProperty"));
	}
	
	public void setCIProperty(String v) {
		set("ciProperty", v);
	}

	/**
	 * Filter attributes in a template.
	 * Allowed values are: all,simple,complex
	 *  
	 * @return
	 */
	public String getAttributeFilter() {
		return(get("attributeFilter"));
	}
	
	public void setAttributeFilter(String v) {
		set("attributeFilter", v);
	}

	public String getTooltip() {
		return(get("tooltip"));
	}
	
	public void setTooltip(String msg) {
		set("tooltip", msg);
	}

	public void setEditTemplate(boolean editTemplate) {
		set("editTemplate", editTemplate);
	}
	public boolean isEditTemplate() {
		return(get("editTemplate", false));
	}

	
	
	
	
	

	
}
