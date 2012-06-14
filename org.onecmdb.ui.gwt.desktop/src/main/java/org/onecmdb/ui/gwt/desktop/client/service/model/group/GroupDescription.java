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
package org.onecmdb.ui.gwt.desktop.client.service.model.group;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.group.table.GroupProxy;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class GroupDescription extends BaseModel {
	
	public static final String QUERY = "query";
	public static final String NAME = "name";
	private static final String PRIMARY_TEMPLATE = "primaryTemplate";

	public String getQuery() {
		return(get(QUERY));
	}

	public String getName() {
		return(get(NAME));		
	}
	
	public void setName(String name) {
		set(NAME, name);		
	}
	

	public void setQuery(String query) {
		set(QUERY, query);
	}

	public void setPrimaryTemplate(String templateAlias) {
		set(PRIMARY_TEMPLATE, templateAlias);
		
	}

	public String getPrimaryTemplate() {
		return(get(PRIMARY_TEMPLATE));
	}

	public DataProxy<BasePagingLoadConfig, BasePagingLoadResult<GroupCollection>> getProxy(String id, GroupCollection scope) {
		return(new GroupProxy(id, scope, this));
	}
	

	protected List<ColumnConfig> getColumnConfigs(String name, String id, CMDBPermissions perm) {
		BaseModel tableModel = getPresentation(name);
		List<BaseModel> tableViews = tableModel.get("TableView");	
		for (BaseModel m : tableViews) {
			if (id.equals(m.get("id"))) {
				return(getColumnConfigs(name, m, perm));
			}
		}
		return(null);
	}
	
	private BaseModel getPresentation(String name) {
		for (BaseModel view : getPresentations()) {
			if (name.equals((String)view.get("name"))) {
				return(view);
			}
		}
		return(null);
	}

	private List<ColumnConfig> getColumnConfigs(String tableName, BaseModel m,
			CMDBPermissions perm) {
		List<BaseModel> columns = m.get("Column");
		
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		
		for (BaseModel col : columns) {
			AttributeColumnConfig config = new AttributeColumnConfig();
			config.set("tableName", tableName);
			config.setId((String)col.get("object") + "." + (String)col.get("render"));
			config.set("object", (String)col.get("object"));
			config.setType((String)col.get("type"));
			config.setRefType((String)col.get("refType"));
			String max = (String)col.get("maxOccurs");
			config.setMaxOccurs(1);
			if (max != null) {
				try {
					int maxInt = (Integer)Integer.parseInt(max);
					config.setMaxOccurs(maxInt);
				} catch (Throwable t) {
					// Ignore...
				}
			}
			config.setName((String)col.get("label"));
			config.setReference("true".equals(col.get("reference")));
			
			config.setGroupDescription(this);
			ColumnConfig c1 = EditorFactory.getColumnConfig(config , false, perm);
			columnConfigs.add(c1);
		}
		return(columnConfigs);
	}

	public ColumnModel getTableColumnModel(String name, String id, CMDBPermissions perm) {
		List<ColumnConfig> cols = getColumnConfigs(name, id, perm);
		ColumnModel m = new ColumnModel(cols);
		return(m);
		/*
		AttributeColumnConfig config = new AttributeColumnConfig();
		config.setId("platform.value_name");
		config.setType("xs:string");
		config.setMaxOccurs(1);
		config.setName("Platform name");
		List<ColumnConfig> cols = new ArrayList<ColumnConfig>();
		ColumnConfig c1 = EditorFactory.getColumnConfig(config , false, perm);
		cols.add(c1);
		
		config = new AttributeColumnConfig();
		config.setId("cpu");
		config.setType("CPU(s)");
		config.setReference(true);
		config.setName("CPU");
		c1 = EditorFactory.getColumnConfig(config , false, perm);
		cols.add(c1);
		
		config = new AttributeColumnConfig();
		config.setId("os.value_name");
		config.setType("xs:string");
		config.setComplex(false);
		config.setMaxOccurs(1);
		config.setName("OS Name");
		c1 = EditorFactory.getColumnConfig(config , false, perm);
		cols.add(c1);
		
		ColumnModel m = new ColumnModel(cols);
		return(m);
	
		*/
	}

	public ContentData getMDR() {
		return(get("mdr"));
	}

	public void setMDR(ContentData mdr) {
		set("mdr", mdr);
	}
	public void appendErrorMessage(String msg) {
		String message = get("error", "");
		set("error", msg + "\n" + message);
	}
	public String getError() {
		return(get("error"));
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nGroupName=" + getName() + "\n");
		buf.append("\tErrors: " + getError() + "\n");
		buf.append("\tQuery: " + getQuery() + "\n");
		buf.append("\tPresenation:" + "\n");
		List<BaseModel> views = getPresentations();
		if (views != null) {
			for (BaseModel view : views) {
				buf.append("\t\t" + view.get("type") + ":" + view + "\n");
			}
		}
		buf.append("\tLifeCycle" + "\n");	
		
		
		return(buf.toString());
	}

	public void addPresentaion(BaseModel presentation) {
		List<BaseModel> list= get("Presentations");
		if (list == null) {
			list = new ArrayList<BaseModel>();
			set("Presentations", list);
		}
		list.add(presentation);
	}
	

	public List<BaseModel> getPresentations() {
		return(get("Presentations"));
	}

	public String getPath() {
		return(get("path"));
	}
	
	public void setPath(String path) {
		set("path", path);
	}

	public boolean isSupportAddRow() {
		return false;
	}

	public void setIcon(String icon) {
		set("icon", icon);
	}
	
	public String getIcon() {
		return(get("icon"));
	}

	public void addCreateModel(BaseModel createModel) {
		List<BaseModel> list = get("Create");
		if (list == null) {
			list = new ArrayList<BaseModel>();
			set("Create", list);
		}
		list.add(createModel);
	}
	
	public List<BaseModel> getCreates() {
		return(get("Create"));
	}
	
	
}
