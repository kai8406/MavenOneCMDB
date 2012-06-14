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
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.InstanceSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.widget.form.InputFormWidget;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MDRTransformConfigurator extends LayoutContainer {

	
	private TransformModel model;
	private GridModelConfig sourceGrid;
	private BaseModel currentParent;
	private BaseModel currentModel;
	private ContentPanel rightPanel;
	private TransformConfig cfg;

	public MDRTransformConfigurator(TransformConfig cfg) {
		this.cfg = cfg;
		this.model = cfg.getTransformModel();
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}

	protected void initUI() {
		setLayout(new BorderLayout());
		
		ContentPanel left = new ContentPanel();
		rightPanel = new ContentPanel();
		left.setLayout(new FitLayout());
		
		left.setScrollMode(Scroll.AUTO);
		
		rightPanel.setLayout(new FitLayout());
		
		ContentPanel south = new ContentPanel();
		south.setHeading("DataSource Preview");
		south.setLayout(new FitLayout());
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	 
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	    
	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 200);  
	    southData.setSplit(true);  
	    southData.setCollapsible(true);  
	    southData.setMargins(new Margins(5));  
		
	    
	    // Add preview panel...
	    PreviewTableWidget preview = new PreviewTableWidget(cfg);
	    preview.addListener(CMDBEvents.MDR_GRID_AVAILIABLE, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				if (be.source instanceof GridModelConfig) {
					setSourceGridConfig((GridModelConfig)be.source);
				}
			}
	    	
	    });
	    south.add(preview);
	    // Left is the tree...
		add(left, westData);
		add(rightPanel, centerData);
		add(south, southData);
		
		ToolBar bar = new ToolBar();
		TextToolItem item = new TextToolItem("Add DataSet");
		item.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				DataSetModel ds = new DataSetModel();
				model.addDataSet(ds);
			}
			
		});
		bar.add(item);
		
		left.setTopComponent(bar);
		
		// Layout left all datasources.
		List<DataSetModel> dataSet = model.getDataSets();
		
		// Create proxy..
		RpcProxy<BaseModel, List<? extends BaseModel>> proxy = new RpcProxy<BaseModel, List<? extends BaseModel>>() {

			@Override
			protected void load(BaseModel loadConfig,
					AsyncCallback<List<? extends BaseModel>> callback) {
				if (loadConfig == null) {
					List list = new ArrayList();
					list.add(model);
					callback.onSuccess(list);
					return;
				}
				
				if (loadConfig instanceof TransformModel) {
					callback.onSuccess(model.getDataSets());
					return;
				}
				
				if (loadConfig instanceof DataSetModel) {
					DataSetModel ds = (DataSetModel)loadConfig;
					callback.onSuccess(ds.getChildren());
								
				}
			}
			
		};
		
		// Add Loader.
		BaseTreeLoader loader = new BaseTreeLoader(proxy) {

			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof TransformModel) {
					return(true);
				}
				if (parent instanceof DataSetModel) {
					return(true);
				}
				return(false);
			}
		};
		
		// Create store
		TreeStore<BaseModel> store = new TreeStore<BaseModel>(loader);
		store.setMonitorChanges(true);
		
		// Create tree
		final Tree tree = new Tree();
		tree.setContextMenu(getMenu(tree));
		
			// Bind tree with store.
		final TreeBinder<BaseModel> binder = new TreeBinder<BaseModel>(tree, store) {

			@Override
			protected String getIconValue(ModelData model, String property) {
				if (model instanceof TransformModel) {
					return("images/mdr/16-circle-blue_16x16.png");
				}
				if (model instanceof DataSetModel) {
					return("images/mdr/outline_co_16x16.gif");
				}
				if (model instanceof InstanceSelectorModel) {
					return("images/mdr/interface_16x16.gif");
				}
				if (model instanceof AttributeSelectorModel) {
					if (((AttributeSelectorModel)model).isNaturalKey()) {
						return("images/mdr/attributeKey_obj_16x16.gif");
					}
					return("images/mdr/attribute_obj_16x16.gif");
				}
				return(null);
			
			}

			@Override
			protected String getTextValue(ModelData model, String property) {
				String text = null;
				if (model instanceof TransformModel) {
					text = model.get("name");
				} 
				if (model instanceof DataSetModel) {
					DataSetModel ds = (DataSetModel)model;
					String name = ds.getName();
					CIModel ci = ds.getTemplate();
					if (ci != null) {
						text = name + "[" + ci.getAlias() + "]";
					}
				}
				if (model instanceof AttributeSelectorModel) {
					AttributeSelectorModel as = (AttributeSelectorModel)model;
					if (as.getAttribute() != null) {
						String name = as.getAttribute().getAlias();
						text = name;
						if (as.getSelector() != null) {
							if (as.getAttribute().isComplex()) {
								text = name + "-->" + as.getSelector();
							} else {
								text = name + "=" + as.getSelector();
							}
						}
					}
				}
				if (text == null) {
					text = "<empty>";
				}
				return(text);
			}
			
		};
		binder.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
	
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				// Update center panel...
				
				BaseModel selModel = (BaseModel)se.getSelectedItem();
				TreeItem item = (TreeItem) binder.findItem(selModel);
				BaseModel parent = null;
				if (item != null) {
					if (item.getParentItem() != null) {
						parent = (BaseModel) item.getParentItem().getModel();
					}
					
				}
				updatePropertyPanel(parent, selModel);
			}
		});

		left.add(tree);
		loader.load();
		
	}

	protected void updatePropertyPanel(BaseModel parent, BaseModel selModel) {
		currentParent = parent;
		currentModel = selModel;
		if (currentModel == null) {
			return;
		}
		InputFormWidget form = new InputFormWidget(selModel, getModelConfig(parent, selModel));
		rightPanel.removeAll();
		rightPanel.add(form);
		rightPanel.layout();
	}

	private List<AttributeColumnConfig> getModelConfig(BaseModel parent, BaseModel selModel) {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		// TODO: Add different for different inputs...
		if (selModel instanceof TransformModel) {
			AttributeColumnConfig cfg = new AttributeColumnConfig();
			cfg.setName("Name");
			cfg.setId("name");
			cfg.setType("xs:string");
			list.add(cfg);
		}
		if (selModel instanceof DataSetModel) {
			
			AttributeColumnConfig cfg = new AttributeColumnConfig();
			cfg.setName("Name");
			cfg.setId("name");
			cfg.setType("xs:string");
			list.add(cfg);
			
			cfg = new AttributeColumnConfig();
			cfg.setType("xs:boolean");
			cfg.setId("primary");
			cfg.setName("Primary");
			
			cfg = new AttributeColumnConfig();
			cfg.setType("Ci");
			cfg.setSelectTemplates(true);
			cfg.setComplex(true);
			cfg.setId("template");
			cfg.setName("Template");
			
			list.add(cfg);
		}
		
		if (selModel instanceof AttributeSelectorModel) {
			
			AttributeColumnConfig cfg = new AttributeColumnConfig();
			cfg.setType("xs:boolean");
			cfg.setId("naturalKey");
			cfg.setName("Key");
			list.add(cfg);
			
			cfg = new AttributeColumnConfig();
			cfg.setType("xs:attribute");
			cfg.setId("attribute");
			cfg.setName("Attribute");
			cfg.set("baseModel", parent);
			cfg.set("ciProperty", "template");
			cfg.set("complex", getComplexAttributeConfig());
			cfg.set("simple", getSimpleAttributeConfig());
			list.add(cfg);
			
			
			/*
			cfg = new AttributeColumnConfig();
			cfg.setType("xs:radiogroup");
			cfg.setId("attributeType");
			cfg.setName("Type");
			cfg.addRadio("complex");
			cfg.addRadio("simple");
			cfg.set("complex", getComplexAttributeConfig());
			cfg.set("simple", getSimpleAttributeConfig());
			list.add(cfg);
			*/
		}
		
		return(list);
	}

	private List<AttributeColumnConfig> getSimpleAttributeConfig() {
		// Column Selector 
		List<AttributeColumnConfig> selList = new ArrayList<AttributeColumnConfig>();
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		cfg.setName("Col Selector");
		cfg.setId("selector");
		List<String> values = new ArrayList<String>();
		if (sourceGrid != null) {
			for (AttributeColumnConfig config : sourceGrid.getColumnConfig()) {
				values.add(config.getId());
			}
		} else {
			values.add("Do Preview Source...");
		}
		cfg.setType("xs:enum");
		cfg.setEnumValues(values);
		selList.add(cfg);

		// Default value...
		List<AttributeColumnConfig> valueList = new ArrayList<AttributeColumnConfig>();
		cfg = new AttributeColumnConfig();
		cfg.setName("Value");
		cfg.setId("value");
		cfg.setType("xs:string");
		valueList.add(cfg);
		
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		cfg = new AttributeColumnConfig();
		cfg = new AttributeColumnConfig();
		cfg.setType("xs:radiogroup");
		cfg.setId("selectorType");
		cfg.setName("Type");
		cfg.addRadio("value");
		cfg.addRadio("selector");
		cfg.set("value", valueList);
		cfg.set("selector", selList);
		list.add(cfg);
		
		return(list);
	}

	private List<AttributeColumnConfig> getComplexAttributeConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		AttributeColumnConfig cfg = new AttributeColumnConfig();
		cfg.setName("DataSet");
		cfg.setId("selector");
		cfg.setType("xs:enum");
		List<String> values = new ArrayList<String>();
		for (DataSetModel ds : model.getDataSets()) {
			values.add(ds.getName());
		}
		cfg.setEnumValues(values);
		list.add(cfg);
		return(list);
	}

	private Menu getMenu(final Tree tree) {
		Menu menu = new Menu();
		MenuItem item = new MenuItem("Add Attribute Selector");
		item.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (tree.getSelectedItem().getModel() instanceof DataSetModel) {
					DataSetModel ds = (DataSetModel)tree.getSelectedItem().getModel();
					ds.addAttributeSelector(new AttributeSelectorModel());
				}
				
			}
			
		});
		menu.add(item);
		
		MenuItem deleteAS = new MenuItem("Delete Attribute Selector");
		deleteAS.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (tree.getSelectedItem().getModel() instanceof AttributeSelectorModel) {
					AttributeSelectorModel as = (AttributeSelectorModel)tree.getSelectedItem().getModel();
					as.getParent().removeAttributeSelector(as);
				}
				
			}
			
		});
		menu.add(deleteAS);
		
		MenuItem deleteDS = new MenuItem("Delete Data Set");
			
		
		
		return(menu);
	}

	public void setSourceGridConfig(GridModelConfig source) {
		this.sourceGrid = source;
		updatePropertyPanel(currentParent, currentModel);
	}
	
	
}
