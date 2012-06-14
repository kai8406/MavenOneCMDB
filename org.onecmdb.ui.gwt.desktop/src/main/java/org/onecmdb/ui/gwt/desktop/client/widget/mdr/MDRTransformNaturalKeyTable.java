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
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MDRTransformNaturalKeyTable extends LayoutContainer {

	private TransformConfig transformCfg;
	private BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>> loader;
	private EditorGrid<BaseModel> grid;
	private HashMap<String, BaseModel> countMap;

	public MDRTransformNaturalKeyTable(TransformConfig cfg) {
		this.transformCfg = cfg;
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}



	protected void initUI() {
		List<ColumnConfig> cols = new ArrayList<ColumnConfig>();
		cols.add(new RowNumberer());
		
		CMDBPermissions perm = new CMDBPermissions();
		perm.setCurrentState(CMDBPermissions.PermissionState.EDIT);
		
		/*
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		cfg.setId("selector");
		cfg.setType("xs:string");
		cfg.setName("Data Source Column");
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		*/
		
		AttributeColumnConfig  cfg = new AttributeColumnConfig();
		cfg.setId("dataSetName");
		cfg.setType("xs:string");
		cfg.setName("Data Set name");
		cfg.setHidden(true);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		
		cfg = new AttributeColumnConfig();
		cfg.setId("template");
		cfg.setType("Ci");
		cfg.setName("CMDB Template");
		cfg.setSelectTemplates(true);
		cfg.setComplex(true);
		cfg.setEditable(false);
		cfg.setWidth(140);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("attribute");
		cfg.setType("xs:attribute");
		cfg.setName("CMDB Attribute");
		cfg.setWidth(100);
		cfg.setCIProperty("template");
		cfg.setAttributeFilter("simple");
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));

		cfg = new AttributeColumnConfig();
		cfg.setId("refType");
		cfg.setName("Reference");
		cfg.setWidth(75);
		cfg.setType("xs:template");
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("targetTemplate");
		cfg.setType("xs:template");
		cfg.setName("Target Template");
		cfg.setWidth(100);
		cfg.setSelectTemplates(true);
		cfg.setComplex(true);
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
	
		
		cfg = new AttributeColumnConfig();
		cfg.setId("naturalKey");
		cfg.setType("xs:boolean");
		cfg.setName("Natural Key");
		cfg.setWidth(75);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));

		cfg = new AttributeColumnConfig();
		cfg.setId("instanceCount");
		cfg.setType("xs:integer");
		cfg.setName("Number of instances");
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));

		cfg = new AttributeColumnConfig();
		cfg.setId("matchedCount");
		cfg.setType("xs:integer");
		cfg.setName("Found in CMDB");
		cfg.setEditable(false);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
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
		loader = new BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>>(proxy);
		
		// Create Store...
		ListStore<BaseModel> store = new ListStore<BaseModel>(loader);
		//store.setMonitorChanges(true);
		
		grid = new EditorGrid<BaseModel>(store, cm);
		
		grid.setLoadMask(true);
		
		// Add plugins.
		for (ColumnConfig c : cols) {
			if (c instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)c);
			}
		}
		setLayout(new FitLayout());
		ToolBar bar = new ToolBar();
		TextToolItem item = new TextToolItem("Calculate Instances", "mdr-config-open-icon");
		item.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				calculateInstances();
			}
		});
		bar.add(item);
		
		
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		cp.setTopComponent(bar);
		cp.add(grid);
		add(cp);
		
		update();
	}
	
	
	protected void calculateInstances() {
		final MessageBox info = MessageBox.progress("Progress", "Calculate instances", "calculate...");
		ModelServiceFactory.get().calculateMDRInstances(CMDBSession.get().getToken(), this.transformCfg, new CMDBAsyncCallback<List<BaseModel>>() {

			@Override
			public void onFailure(Throwable t) {
				info.close();
				super.onFailure(t);
			}
			
			@Override
			public void onSuccess(List<BaseModel> arg0) {
				info.close();
				updateInstanceCount(arg0);
			}
			
		});
	}



	protected void updateInstanceCount(List<BaseModel> arg0) {
		countMap = new HashMap<String, BaseModel>();
		for (BaseModel m : arg0) {
			String ds = m.get("ds");
			countMap.put(ds, m);
		}
		if (loader != null) {
			loader.load();
		}
	}


	protected void updateModel(BaseModel m) {
		if (m == null) {
			return;
		}
		//HashMap<String, AttributeSelectorModel> map = buildSelectorMap();

		String selector = m.get("selector");
		String dsName = m.get("dataSetName");
		CIModel template = m.get("template");
		AttributeModel attribute = m.get("attribute");
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
		AttributeSelectorModel asm = null;
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				if (as.getAttribute() != null && as.getAttribute().equals(attribute)) {
					asm = as;
					break;
				}
			}
		}
		if (asm == null) {
			return;
		}
		// Update Natural key.
		asm.setNaturalKey(naturalKey);
		
		this.transformCfg.informChange();
		
	}	
	
	protected void updateModel(List<BaseModel> input) {
		for (BaseModel m : input) {
			updateModel(m);
		}
	}
	
	protected HashMap<String, AttributeSelectorModel> buildSelectorMap() {
		HashMap<String, AttributeSelectorModel> map = new HashMap<String, AttributeSelectorModel>();
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				/*
				if (as.getAttribute() != null) {
					if (as.getAttribute().isComplex()) {
						continue;
					}
				}
				*/
				String selector = as.getSelector();
				if (selector != null) {
					map.put(selector, as);
				}
			}
		}
		return(map);
	}
	
	protected List<BaseModel> buildModel() {
		HashMap<String, AttributeSelectorModel> map = buildSelectorMap();
		List<BaseModel> base = new ArrayList<BaseModel>();
		System.out.println("DataSets=" + transformCfg.getTransformModel().getDataSets().size());
		for (DataSetModel ds : transformCfg.getTransformModel().getDataSets()) {
			System.out.println("Add DS:" + ds.getName());
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				BaseModel m = new BaseModel();
				m.set("attribute", as.getAttribute());
				m.set("template", ds.getTemplate());
				m.set("naturalKey", as.isNaturalKey());
				m.set("selector", as.getSelector());
				m.set("dataSetName", ds.getName());
				if (as.getAttribute().isComplex()) {
					DataSetModel tDs = transformCfg.getTransformModel().getDataSet(as.getSelector());
					if (tDs != null) {
						m.set("targetTemplate", tDs.getTemplate());
					}
					m.set("refType", as.getAttribute().getRefType());
				}
				if (countMap != null) {
					BaseModel model = countMap.get(ds.getTemplate().getAlias());
					if (model != null) {
						m.set("instanceCount", model.get("count"));
						m.set("matchedCount", model.get("foundCount"));
					}
				}
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
		System.out.println("Return attributes=" + base.size());
		return(base);
	}

	public void update() {
		if (loader != null) {
			loader.load();
		}
	}

}
