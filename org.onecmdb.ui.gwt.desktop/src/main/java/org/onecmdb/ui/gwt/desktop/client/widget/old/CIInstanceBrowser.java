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
package org.onecmdb.ui.gwt.desktop.client.widget.old;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.utils.LoaderProxy;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeTableBinder;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumn;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumnModel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIInstanceBrowser extends LayoutContainer {

	
	private BaseTreeLoader loader;
	private ContentData mdr;
	private ContentPanel center;
	private CIModel root;
	private PagingToolBar toolBar;
	private LoaderProxy lP;
	private LoadConfigModelItem cfg;
	private TreeStore<? extends ModelData> store;

	public CIInstanceBrowser(ContentData mdr) {
		this.mdr = mdr;
	}
	
	public CIInstanceBrowser(ContentData mdr, CIModel model) {
		this.mdr = mdr;
		setRoot(model);
		start();
	}
	
	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}


	public void init() {
		setLayout(new BorderLayout());
		
		ContentPanel east = new ContentPanel();
		east.setLayout(new FitLayout());
		final LayoutContainer editPanel = new LayoutContainer();
		editPanel.setLayout(new FitLayout());
		east.add(editPanel);
		
		List<TreeTableColumn> columns = new ArrayList<TreeTableColumn>();
		TreeTableColumn column = new TreeTableColumn("name", "Name", 200);
		columns.add(column);

		column = new TreeTableColumn("value", "Value", 200);
		columns.add(column);
	
		TreeTableColumnModel cm = new TreeTableColumnModel(columns);

		final TreeTable table = new TreeTable(cm);
		table.setAnimate(false);
		table.addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				InfoConfig c = new InfoConfig("Event", "Selected Items{0}" + table.getSelectedItems().size());
				Info.display(c);
				List<TreeItem> items = table.getSelectedItems();
				List<CIModel> models = new ArrayList<CIModel>();
				/*
				for (TreeItem item : items) {
					if (item.getModel() instanceof CIModel) {
						models.add((CIModel)item.getModel());
					}
					if (item.getModel() instanceof AttributeModel) {
						AttributeModel aModel = (AttributeModel) item.getModel(); 
						if (aModel.getValues().size() == 1) {
							CIModel ci = aModel.getValues().get(0).getCIModel();
							if (ci != null) {
								models.add(ci);
							}
						}
					}
					
					if (item.getModel() instanceof ValueModel) {
						ValueModel vModel = (ValueModel)item.getModel();
						CIModel ci = vModel.getCIModel();
						if (ci != null) {
							models.add(ci);
						}
					}
				}
				
				if (models.size() == 0) {
					return;
				}
				updateInputForm(editPanel, models);
				*/
			}
		});
		
		//table.setItemIconStyle("icon-page"); 

		RpcProxy<? extends ModelData, List<? extends ModelData>> proxy = new RpcProxy<ModelData, List<? extends ModelData>>() {

			@Override
			protected void load(final ModelData loadConfig,
					final AsyncCallback<List<? extends ModelData>> callback) {
				
				if (loadConfig instanceof ModelItem) {
					/*
					((ModelItem)loadConfig).getChildren(mdr, new AsyncCallback<List<? extends ModelData>>() {

						public void onFailure(Throwable arg0) {
							callback.onFailure(arg0);
							
						}

						public void onSuccess(List<? extends ModelData> arg0) {
							List<ModelItem> items = new ArrayList<ModelItem>();
							for (ModelData d : arg0) {
								if (d instanceof AttributeModel) {
									if (!((AttributeModel)d).isComplex()) {
										continue;	
									}
								}
								items.add((ModelItem)d);
							}
							callback.onSuccess(items);
						}
						
					});
					*/
				}
			}
		};

		/*
	// Convert Object to Model Objects!
	DataReader dr = new DataReader<ModelData, List<CIModel>>() {
		public List<CIModel> read(ModelData model, Object data) {
			// TODO Auto-generated method stub
			if (data instanceof List) {
				return (List<CIModel>) (data);
			}
			return null;
		}
	};
	*/
	
	 	loader = new BaseTreeLoader(proxy) {
			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof ModelItem) {
					//return(((ModelItem)parent).hasChildren());
				}
				return(false);
			}
			
			@Override
			public boolean loadChildren(ModelData parent) {
				// Need to convert the parent (CIModel) to a 
				// Tree Load config.
				// TODO Auto-generated method stub
				return super.loadChildren(parent);
			}
		};


		
		// trees store
		store = new TreeStore(loader);
		store.setMonitorChanges(true);
		
		
		
		store.setStoreSorter(new StoreSorter<ModelData>() {

			@Override
			public int compare(Store store, ModelData m1, ModelData m2, String property) {
				return super.compare(store, m1, m2, property);
			}
		});
		
		
		
		final TreeTableBinder<? extends ModelData> binder = new TreeTableBinder(table, store);
		binder.setDisplayProperty("name");
		
		/*
		  
		 binder.setStringProvider(new BaseModelStringProvider<ModelData>() {

		});
		*/	 
		
		binder.setIconProvider(new ModelStringProvider<ModelData>() {  
			
			public String getStringValue(ModelData model, String property) {
				if (model instanceof ModelItem) {
					ModelItem item = (ModelItem)model;
					String icon = null;
					if (icon == null) {
						return(null);
					}
					return(CMDBSession.get().getContentRepositoryURL() + "/" + icon);
				}
				return(null);
			} 
			 
		});
		
		//table.setSize(700, 300);
		
		
		
		//editPanel.add(editForm);
		toolBar = new PagingToolBar(50);
		lP = new LoaderProxy(null);
		lP.setBaseTreeLoader(loader);
	    toolBar.bind(lP);
		
		center = new ContentPanel();
		center.setHeading("Instanaces(s)");
		center.setLayout(new RowLayout());
		center.add(toolBar, new RowData(1,-1));
		center.add(table, new RowData(1,1));
		
		center.layout();
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 200);  
	    eastData.setSplit(true);  
	    eastData.setCollapsible(true);  
	    eastData.setMargins(new Margins(5));  
	    
	    //east.collapse();
	    
		add(center, centerData);
		add(east, eastData);
		
		layout();
	}

	public void setRoot(CIModel root) {
		cfg = new LoadConfigModelItem(root);
		cfg.setLimit(lP.getLimit());
		lP.useLoadConfig(cfg);
	}

	public void start() {
		// Create 
		lP.load();
	}
	
	protected boolean updateInputForm(LayoutContainer container, List<CIModel> model) {
		final FormPanel editForm = new FormPanel(); 
		editForm.setWidth("100%");
		editForm.setHeight("100%");
		
		editForm.setScrollMode(Scroll.AUTO);
		editForm.setFrame(true);  
		editForm.setFieldWidth(60);  
		editForm.setLabelWidth(100);  
		editForm.setButtonAlign(HorizontalAlignment.CENTER);
		if (model.size() == 0) {
			editForm.setHeading("Can't edit!!!!");
		} else if (model.size() > 1) {
			editForm.setHeading("Edit Multiple CI");
		} else {
			editForm.setHeading("Edit CI " + model.get(0).getDisplayName());
		}
		editForm.setIconStyle("icon-form");  		  
		editForm.setStyleAttribute("padding", "0");  
		     
		if (model.size() > 0) {
			CIModel m = model.get(0);
			/*
			m.getChildren(mdr, new AsyncCallback<List<? extends ModelData>>() {

				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(List<? extends ModelData> arg0) {
					for (AttributeModel a : (List<AttributeModel>)arg0) {
						// TODO: Switch on type, etc...
						if (a.isComplex()) {
							continue;
						}
						
						List<ValueModel> values = a.getValues();
						if (values.size() == 0) {
							editForm.add(getField(a, null));  
						} else if (values.size() == 1) {
							editForm.add(getField(a, values.get(0)));  
						} else {
							for (ValueModel v : values) {
								editForm.add(getField(a, v));  
							}
						}
					}
					
					editForm.layout();
				}
			});
			*/
		}
		
		container.removeAll();
		container.add(editForm);
		container.layout();
		
		return(true);
	}
	
	protected Field getField(AttributeModel aModel, final ValueModel v) {
		TextField text = new TextField(); 
		
		text.setFieldLabel(aModel.getDisplayName());  
		if (v != null) {
			text.setValue(v.getValue());
		} else {
			text.setEmptyText("Edit...");
		}
		text.setAllowBlank(true);  
		text.setMinLength(4); 
		text.setAutoWidth(true);
		text.addListener(Events.Change, new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				v.setUpdateValue((String)be.value);
				Info.display("ChangeEvent", "OldValue {0} - NewValue{0} ", (String)be.oldValue, (String)be.value);
			}

			
		});
		return(text);
	}

}
