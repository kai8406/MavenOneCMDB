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

import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
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
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MDRTransformSelectorTable extends LayoutContainer {

	private TransformConfig transformCfg;
	private BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>> loader;
	private GridModelConfig sourceGrid;

	public MDRTransformSelectorTable(TransformConfig cfg) {
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
		
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		cfg.setId("selector");
		cfg.setType("xs:string");
		cfg.setName("Data Source Column");
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("ref");
		cfg.setType("xs:string");
		cfg.setName("");
		cfg.setHidden(false);
		cfg.setEditable(false);
		cfg.setWidth(30);
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
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
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));
		
		cfg = new AttributeColumnConfig();
		cfg.setId("attribute");
		cfg.setType("xs:attribute");
		cfg.setName("CMDB Attribute");
		cfg.setCIProperty("template");
		cfg.setAttributeFilter("simple");
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));

		
		cfg = new AttributeColumnConfig();
		cfg.setId("naturalKey");
		cfg.setType("xs:boolean");
		cfg.setName("Natural Key");
		cfg.setHidden(true);
		
		cols.add(EditorFactory.getColumnConfig(cfg, false, perm));

		ColumnModel cm = new ColumnModel(cols);
		
		// Create proxy..
		RpcProxy<ListLoadConfig, ListLoadResult<BaseModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<BaseModel>>() {

			@Override
			protected void load(ListLoadConfig loadConfig,
					AsyncCallback<ListLoadResult<BaseModel>> callback) {
				if (sourceGrid != null) {
					List<BaseModel> base = buildModel();
					callback.onSuccess(new BaseListLoadResult<BaseModel>(base));
				}
			}
			
		};
		
		// Create Loader...
		loader = new BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>>(proxy);
		
		// Create Store...
		ListStore<BaseModel> store = new ListStore<BaseModel>(loader);
		
		EditorGrid<BaseModel> grid = new EditorGrid<BaseModel>(store, cm);
		
		// Add plugins.
		for (ColumnConfig c : cols) {
			if (c instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)c);
			}
		}
		setLayout(new FitLayout());
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		ToolBar bar = new ToolBar();
		bar.add(new FillToolItem());
		cp.setTopComponent(bar);
		cp.add(grid);
		add(cp);
	}
	
	
	protected void updateModel(BaseModel m) {
		if (m == null) {
			return;
		}
		HashMap<String, AttributeSelectorModel> map = buildSelectorMap();

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
		AttributeSelectorModel as = map.get(selector);
		
		// Check that it's completed.
		if (template == null || attribute == null) {
			if (as != null) {
				DataSetModel ds = as.getParent();
				ds.removeAttributeSelector(as);
				if (ds.getAttributeSelector().size() == 0) {
					this.transformCfg.getTransformModel().removeDataSet(ds);
				}
			}
			return;
		}
		// Check if we have reallocate it.
		if (as != null) {
			if (!as.getParent().getTemplate().equals(template)) {
				DataSetModel ds = as.getParent();
				ds.removeAttributeSelector(as);
				if (ds.getAttributeSelector().size() == 0)  {
					this.transformCfg.getTransformModel().removeDataSet(ds);
				}
				dsName = null;
				as = null;
			}
		}
		if (as == null) {
			// Create new.
			as = new AttributeSelectorModel();
			as.setSelector(selector);
			as.setUseSelectorName(true);
			// Update or create new dataset...
			if (dsName == null) {
				dsName = template.getAlias();
			}
			DataSetModel ds = transformCfg.getTransformModel().getDataSet(dsName);
			if (ds == null) {
				ds = new DataSetModel();
				ds.setName(dsName);
				m.set("dataSetName", dsName);
				transformCfg.getTransformModel().addDataSet(ds);
			}
			ds.addAttributeSelector(as);
		}
		
		// Update it...
		as.getParent().setTemplate(template);
		as.setAttribute(attribute);
		as.setNaturalKey(naturalKey);
		as.setSelectorType(AttributeSelectorModel.AS_TYPE_SELECTOR);
		
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
				if (as.getAttribute() != null) {
					if (as.getAttribute().isComplex()) {
						continue;
					}
				}
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
		for (AttributeColumnConfig cfg : sourceGrid.getColumnConfig()) {
			BaseModel m = new BaseModel();
			m.set("selector", cfg.getId());
			m.set("ref", "<a style='background-image:url(images/mdr/reference16.gif);background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>");
			AttributeSelectorModel as = map.get(cfg.getId());
			if (as != null) {
				m.set("dataSetName", as.getParent().getName());
				m.set("template", as.getParent().getTemplate());
				m.set("attribute", as.getAttribute());
				m.set("naturalKey", as.isNaturalKey());
			}
			base.add(m);
			m.addChangeListener(new ChangeListener() {

				public void modelChanged(ChangeEvent event) {
					System.out.println("Model Changed=" + event);
					if (event instanceof PropertyChangeEvent) {
						if (!((PropertyChangeEvent)event).getName().equals("dataSetName")) {
							updateModel((BaseModel)event.source);
						}
					}
					
				}
				
			});
		}
		return(base);
	}
	
	public void setSourceGridConfig(GridModelConfig source) {
		this.sourceGrid = source;
		update();
	}
	
	public void update() {
		if (loader != null) {
			loader.load();
		}
	}

}
