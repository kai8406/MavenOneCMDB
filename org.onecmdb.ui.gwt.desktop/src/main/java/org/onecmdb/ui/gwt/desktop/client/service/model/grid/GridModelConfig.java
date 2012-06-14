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
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class GridModelConfig  extends BaseModel implements Serializable {

	private transient List<ColumnConfig> cols;
	//private List<GridColumnFilter> gridColumnFilter;
	//private List<AttributeColumnConfig> columnConfig;
	//	private String autoExpandColumnId;
	private boolean supportAddRow;
	private ContentData mdr;
	//private String query;
	private String name;
	private CIModel model;
	
	// Need this for serialization....
	private List<GridColumnFilter> gridColumnFilter;
	private List<AttributeColumnConfig> columnConfig;


	public List<ColumnConfig> getColumns() {
		return(cols);
	}
	public void setColumns(List<ColumnConfig> cols) {
		this.cols = cols;
	}

	public String getQuery() {
		return(get("query"));
	}
	
	public void setQuery(String query) {
		set("query", query);
	}

	public List<GridColumnFilter> getGridColumnFilter() {
		return(get("gridColumnFilter"));
	}
	public void setGridColumnFilter(List<GridColumnFilter> gridColumnFilter) {
		set("gridColumnFilter", gridColumnFilter);
	}
	
	public List<AttributeColumnConfig> getColumnConfig() {
		return(get("ColumnConfig"));
	}
	
	public void setColumnConfig(List<AttributeColumnConfig> columnConfig) {
		this.set("ColumnConfig", columnConfig);
	}
	
	public String getAutoExpandColumnId() {
		return(get("autoExpandColumnId"));
	}
	
	public void setAutoExpandColumnId(String autoExpandColumnId) {
		set("autoExpandColumnId", autoExpandColumnId);
	}
	
	public boolean isSupportAddRow() {
		return supportAddRow;
	}
	public void setSupportAddRow(boolean supportAddRow) {
		this.supportAddRow = supportAddRow;
	}
	public ContentData getMDR() {
		return mdr;
	}
	public void setMDR(ContentData mdr) {
		this.mdr = mdr;
	}
	
	public void setNewCIModel(String name, CIModel model) {
		this.name = name;
		this.model = model;
	}
	public CIModel getNewModel() {
		return(this.model);
	}
	
	public CIModelCollection createNewInstance() {
		CIModelCollection col = createNewInstance(this.model);
		return(col);
	}
	
	public CIModelCollection createNewInstance(CIModel template) {
		CIModelCollection col = new CIModelCollection();
		CIModel newModel = template.newInstance();
		col.addCIModel(name, newModel);
		col.setNewCollection(true);
		return(col);
	}


	public void setEditable(boolean value) {
		if (cols == null) {
			return;
		}
		for (ColumnConfig config : cols) {
			CellEditor editor = config.getEditor();
			if (editor != null) {
				editor.getField().setReadOnly(value);
			}
		}
	}
	
	public List<ColumnConfig> getColumnConfigs(ContentData mdr, CMDBPermissions perm) {
		cols = new ArrayList<ColumnConfig>();
		for (AttributeColumnConfig config : getColumnConfig()) {
			config.setMDR(mdr);
			ColumnConfig column = EditorFactory.getColumnConfig(config, false, perm);
			cols.add(column);
		}
		return(cols);
		
	}

}
