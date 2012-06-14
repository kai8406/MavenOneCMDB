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
package org.onecmdb.ui.gwt.desktop.client.widget.group.table;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.fixes.combo.IValueComponent;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.PageSizePagingToolBar;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class GroupTableWidget extends LayoutContainer implements IValueComponent<ModelData>{

	
	private GroupDescription desc;
	private String id;
	private CMDBPermissions permissions;
	private ModelData value;
	private String name;

	public GroupTableWidget(String name, String id, GroupDescription desc) {
		this.desc = desc;
		this.id = id;
		this.name = name;
	}
	
	public CMDBPermissions getPermission() {
		return permissions;
	}

	public void setPermission(CMDBPermissions permission) {
		this.permissions = permission;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}

	public void initUI() {
		
		// Create Loader.
		BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<GroupCollection>> loader = new BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<GroupCollection>>(desc.getProxy(id, (GroupCollection)getValue()));
		
		// Create Store
		ListStore<GroupCollection> store = new ListStore<GroupCollection>(loader);
		store.setMonitorChanges(true);
		
		// Create editor grid.
		ColumnModel model = desc.getTableColumnModel(name, id, permissions);
		EditorGrid<GroupCollection> grid = new EditorGrid<GroupCollection>(store, model);
		for (int i = 0; i < model.getColumnCount(); i++) {
			ColumnConfig cfg = model.getColumn(i);
			if (cfg instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)cfg);
			}
		}
		
		grid.setBorders(true);
		grid.setLoadMask(true);
		
		
		// Fill the component with the grid.
		setLayout(new FitLayout());
		
		// Add this for scrollbars!
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.add(grid);
		PagingToolBar paging = new PageSizePagingToolBar(50);
		paging.bind(loader);
		panel.setBottomComponent(paging);
		
		add(panel);
	
		
		
		loader.load();
	}

	


	public ModelData getValue() {
		return(this.value);
	}

	public void setValue(ModelData value) {
		System.out.println("setModelData:" + value);
		this.value = value;
	}




}


