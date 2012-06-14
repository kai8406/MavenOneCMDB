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
package org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.query;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescriptionConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.GroupPropertyEditor;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TemplateQueryTableEditor extends LayoutContainer {
	
	private GroupDescriptionConfig model;
	private CMDBPermissions permission;
	private Grid<BaseModel> grid;

	public TemplateQueryTableEditor(CMDBPermissions perm, GroupDescriptionConfig model) {
		this.model = model;
		this.permission = perm;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}

	public void initUI() {
		List<ColumnConfig> cols = new ArrayList<ColumnConfig>();
		CheckBoxSelectionModel<BaseModel> sm = null;
		if (permission.isDeletable()) {
			sm = new CheckBoxSelectionModel<BaseModel>();
			cols.add(sm.getColumn());
		}
		//CMDBPermissions perm = new CMDBPermissions();
		//perm.setCurrentState(CMDBPermissions.PermissionState.EDIT);
	
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		cfg.setId("id");
		cfg.setType("xs:string");
		cfg.setName("ID");
		cols.add(EditorFactory.getColumnConfig(cfg, false, permission));
	
		cfg = new AttributeColumnConfig();
		cfg.setId("template");
		cfg.setType("Ci");
		cfg.setComplex(true);
		cfg.setSelectTemplates(true);
		cfg.setName("Template");
		cfg.setSelectTemplates(true);
		cfg.setComplex(true);
		cols.add(EditorFactory.getColumnConfig(cfg, false, permission));
	
	
		cfg = new AttributeColumnConfig();
		cfg.setId("primary");
		cfg.setType("xs:boolean");
		cfg.setName("primary");
		cfg.setHidden(false);
	
		cols.add(EditorFactory.getColumnConfig(cfg, false, permission));

		ColumnModel cm = new ColumnModel(cols);
	
		// Create proxy..
		RpcProxy<ListLoadConfig, ListLoadResult<BaseModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<BaseModel>>() {

			@Override
			protected void load(ListLoadConfig loadConfig,
					AsyncCallback<ListLoadResult<BaseModel>> callback) {
				List<BaseModel> base = buildModel();
				callback.onSuccess(new BaseListLoadResult<BaseModel>(base));
			}
		};
	
		// Create Loader...
		final BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>> loader = new BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>>(proxy);
	
		// Create Store...
		final ListStore<BaseModel> store = new ListStore<BaseModel>(loader);
		//store.setMonitorChanges(true);
		
		if (permission.isEditable()) {
			// Create editor grid.
			grid = new EditorGrid<BaseModel>(store, cm);
		} else {
			grid = new Grid<BaseModel>(store, cm);
		}
	
		//grid.setSelectionModel(sm);
		
		// Add plugins.
		for (ColumnConfig c : cols) {
			if (c instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)c);
			}
		}
		
		ToolBar bar = new ToolBar();
		if (permission.isEditable()) {
			final TextToolItem add = new TextToolItem("Add", "add-icon");
			add.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					((EditorGrid)grid).stopEditing();
					BaseModel m = new BaseModel();
					m.set(GroupDescriptionConfig.TEMPLATE_QUERY_PRIMARY, false);
					model.addQueryTemplate(m);
					store.insert(m, 0);
					((EditorGrid)grid).startEditing(0, 1);
				}
			});
			final TextToolItem undo = new TextToolItem("Undo", "undo-icon");
			undo.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					// Reject from store.
					store.rejectChanges();
					
					// Update model.
					model.setQueryTemplates(store.getModels());
				}
			});

			bar.add(add);
			bar.add(undo);
		}
		if (permission.isDeletable()) {
			final TextToolItem remove = new TextToolItem("Delete", "delete-icon");
			bar.add(remove);
		}
		
		setLayout(new FitLayout());
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.setTopComponent(bar);
		cp.add(grid);
		add(cp);
	}

	private List<BaseModel> buildModel() {
		return(this.model.getQueryTemplates());
	}
}
