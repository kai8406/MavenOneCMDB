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
package org.onecmdb.ui.gwt.desktop.client.widget.mdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.form.InputFormWidget;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
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
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MDRTransformRelationTable extends LayoutContainer {
	
	private TransformConfig transformCfg;
	private BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>> loader;
	private ListStore<BaseModel> store;
	private Grid<BaseModel> grid;

	public MDRTransformRelationTable(TransformConfig cfg) {
		this.transformCfg = cfg;
	}
	

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}
	
	public void initUI() {
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		List<ColumnConfig> cols = new ArrayList<ColumnConfig>();
		
		// Add checkbox...
		CheckBoxSelectionModel<BaseModel> sm = new CheckBoxSelectionModel<BaseModel>();  
		sm.setSelectionMode(SelectionMode.MULTI);
		cols.add(sm.getColumn());
		
		CMDBPermissions perm = new CMDBPermissions();
		perm.setCurrentState(CMDBPermissions.PermissionState.EDIT);
		
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		cfg.setId("sourceTemplate");
		cfg.setType("Ci");
		cfg.setName("Source Template");
		cfg.setSelectTemplates(true);
		cfg.setComplex(true);
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("sourceAttribute");
		cfg.setType("xs:attribute");
		cfg.setName("Source Attribute");
		cfg.setCIProperty("template");
		cfg.setAttributeFilter("complex");
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("sourceAttribute.refType");
		cfg.setName("Reference");
		cfg.setType("xs:template");
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("naturalKey");
		cfg.setType("xs:boolean");
		cfg.setName("Natural Key");
		cfg.setHidden(true);
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));

		cfg = new AttributeColumnConfig();
		cfg.setId("targetTemplate");
		cfg.setType("xs:template");
		cfg.setName("Target Template");
		cfg.setSelectTemplates(true);
		cfg.setComplex(true);
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		
	
		ColumnModel cm = new ColumnModel(cols);
		
		// Create proxy..
		RpcProxy<ListLoadConfig, ListLoadResult<BaseModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<BaseModel>>() {

			@Override
			protected void load(ListLoadConfig loadConfig,
					AsyncCallback<ListLoadResult<BaseModel>> callback) {
				List<BaseModel> base = loadModel();
				callback.onSuccess(new BaseListLoadResult<BaseModel>(base));
			}
		};
		
		// Create Loader...
		loader = new BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>>(proxy);
		
		// Create Store...
		store = new ListStore<BaseModel>(loader);
		
		grid = new Grid<BaseModel>(store, cm);
		
		
		// Add plugins.
		for (ColumnConfig c : cols) {
			if (c instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)c);
			}
		}
		grid.setSelectionModel(sm);
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		ToolBar top = new ToolBar();
		TextToolItem autoUpdate = new TextToolItem("Update", new SelectionListener<ToolBarEvent>() {
			@Override
			public void componentSelected(ToolBarEvent ce) {
				autoReslove();
			}
		});
		autoUpdate.setIconStyle("refresh-icon");
		/*
		ToolItem add = new TextToolItem("Add", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				addRelation();
			}
		});
		*/
		top.add(autoUpdate);
		TextToolItem delete = new TextToolItem("Delete", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				deleteSelected();
			}

			
		});
		delete.setIconStyle("delete-icon");
		top.add(delete);
		
		cp.setTopComponent(top);
		
		cp.add(grid);
		
		setLayout(new FitLayout());
		add(cp);
	
		update();
	
	}
	
	public void update() {
		if (hasReferences()) {
			loader.load();
		} else {
			autoReslove();
		}		
	}
	
	protected boolean hasReferences() {
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				if (as.getAttribute().isComplex()) {
					return(true);
				}
			}
		}
		return(false);
	}	
	protected void deleteSelected() {
		final List<BaseModel> items = grid.getSelectionModel().getSelectedItems();
		final MessageBox confirm = MessageBox.confirm("Delete", "Delete " + items.size() + " default values", new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				 Button btn = be.buttonClicked;  
				 if (btn.getItemId().equals(Dialog.YES)) {
					 // Remove items...
					 for (BaseModel item : items) {
						 store.remove(item);
						 removeModel(item);
					 }
				 }
				 
			}			
		});
	}

	protected void autoReslove() {
		final MessageBox info = MessageBox.progress("Resolve References", "Please wait", "resolving...");
		ModelServiceFactory.get().autoResolveTransformRelations(CMDBSession.get().getToken(), transformCfg.getTransformModel(), new CMDBAsyncCallback<TransformModel>() {
			
			@Override
			public void onFailure(Throwable t) {
				info.close();
				super.onFailure(t);
			}

			public void onSuccess(TransformModel result) {
				info.close();
				transformCfg.setTransformModel(result);
				transformCfg.informChange();
				loader.load();
			}
			
		});
		
	}


	protected void addRelation() {
		Dialog d = new Dialog();
		d.setSize(400, 400);
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		
		cfg.setId("sourceTemplate");
		cfg.setName("Source Template");
		cfg.setType("xs:combo");
		cfg.setComboValues(transformCfg.getTransformModel().getDataSets());
		cfg.setComboProperty("template.alias");
		list.add(cfg);
		
		cfg = new AttributeColumnConfig();
		cfg.setId("sourceAttribute.refType");
		cfg.setName("Reference");
		cfg.setType("xs:template");
		cfg.setEditable(false);
		list.add(cfg);
		
		cfg = new AttributeColumnConfig();
		cfg.setId("sourceAttribute");
		cfg.setName("Source Attribute");
		cfg.setType("xs:attribute");
		list.add(cfg);
		
		cfg = new AttributeColumnConfig();
		cfg.setId("targetTemplate");
		cfg.setName("Target Template");
		cfg.setType("xs:combo");
		cfg.setComboValues(transformCfg.getTransformModel().getDataSets());
		cfg.setComboProperty("template.alias");
		list.add(cfg);
		BaseModel m = new BaseModel();
		InputFormWidget input = new InputFormWidget(m, list);
		d.add(input);
		d.show();
	}

	protected void updateModel(BaseModel m) {
		if (m == null) {
			return;
		}
		CIModel template = m.get("sourceTemplate");
		AttributeModel attribute = m.get("sourceAttribute");
		Object o = m.get("naturalKey", false);
		boolean naturalKey = false;
		if (o instanceof String) {
			naturalKey = "true".equalsIgnoreCase(o.toString());
		} else if (o instanceof Boolean) {
			naturalKey = (Boolean)o;
		}
		// Check that it's completed.
		if (template == null || attribute == null) {
			return;
		}
		// Find Corret Attribute Selector...
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			if (!ds.getTemplate().getAlias().equals(template.getAlias())) {
				continue;
			}
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				if (as.getAttribute().getAlias().equals(attribute.getAlias())) {
					// Found, update natrual key....
					as.setNaturalKey(naturalKey);
				}
			}
		}
		this.transformCfg.informChange();
		
	}
	
	protected void removeModel(BaseModel m) {
		if (m == null) {
			return;
		}
		CIModel template = m.get("sourceTemplate");
		AttributeModel attribute = m.get("sourceAttribute");
		Object o = m.get("naturalKey", false);
		boolean naturalKey = false;
		if (o instanceof String) {
			naturalKey = "true".equalsIgnoreCase(o.toString());
		} else if (o instanceof Boolean) {
			naturalKey = (Boolean)o;
		}
		// Check that it's completed.
		if (template == null || attribute == null) {
			return;
		}
	
		// Find Corret Attribute Selector...
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			if (!ds.getTemplate().getAlias().equals(template.getAlias())) {
				continue;
			}
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				if (as.getAttribute().getAlias().equals(attribute.getAlias())) {
					// Found, update natrual key....
					ds.removeAttributeSelector(as);
				}
			}
		}
		this.transformCfg.informChange();
	}
	
	protected List<BaseModel> loadModel() {
		List<BaseModel> base = new ArrayList<BaseModel>();
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				if (as.getAttribute().isComplex()) {
					BaseModel m = new BaseModel();
					m.set("sourceAttribute", as.getAttribute());
					m.set("sourceTemplate", ds.getTemplate());
					m.set("naturalKey", as.isNaturalKey());
					DataSetModel tDs = transformCfg.getTransformModel().getDataSet(as.getSelector());
					if (tDs == null) {
						m.set("targetTemplate", as.getSelector());
					} else {
						m.set("targetTemplate", tDs.getTemplate());
					}
					m.set("targetSelector", as.getSelector());
					base.add(m);
					
					m.addChangeListener(new ChangeListener() {

						public void modelChanged(ChangeEvent event) {
							System.out.println("Model Changed=" + event);
							if (event instanceof PropertyChangeEvent) {
								updateModel((BaseModel)event.source);
							}
							
						}
						
					});
				}
			}
		}
		return(base);
	}	
	
	protected HashMap<String, AttributeSelectorModel> buildSelectorMap() {
		HashMap<String, AttributeSelectorModel> map = new HashMap<String, AttributeSelectorModel>();
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				if (as.getAttribute().isComplex()) {
					continue;
				}
				String selector = as.getSelector();
				if (selector != null) {
					map.put(selector, as);
				}
			}
		}
		return(map);
	}

}
