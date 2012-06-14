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
package org.onecmdb.ui.gwt.desktop.client.window.mdr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.browser.CloseWindowListener;
import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyGroupingView;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyXMLReader;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.PermissionMenu;
import org.onecmdb.ui.gwt.desktop.client.widget.CompareGridWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.help.HelpInfo;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRStartWindow;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.XmlReader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class MDROverview extends LayoutContainer {
	
	private ContentFile mdr;
	private BaseListLoader loader;
	private CMDBPermissions perm;

	public MDROverview(ContentFile mdr, CMDBPermissions perm) {
		this.mdr = mdr;
		this.perm = perm;
	}

	@Override
	protected void onRender(Element parent, int index) {
		// TODO Auto-generated method stub
		super.onRender(parent, index);
		
		initUI();
	}

	public void initUI() {
		 setLayout(new FitLayout());  
	   
		 ContentPanel panel = new ContentPanel();
		 panel.setLayout(new FitLayout());
		 panel.setHeaderVisible(false);
		 panel.setLayoutOnChange(true);
		 
	     List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
	     
	     ColumnConfig column = new ColumnConfig("mdr", "MDR", 100);
	     column.setHidden(false);
	     columns.add(column);
		
	     columns.add(new ColumnConfig("name", "Config Name", 260));  
		   
	     
	     column = new ColumnConfig("status", "Status", 100);
	     column.setRenderer(new GridCellRenderer<ModelData>() {

			public String render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store) {
				
				
				String value = model.get(property);
				String style = "#000000";
				if (value == null || value.length() == 0) {
					value = "Not Executed";
					style = "#000000";
				} else {
					if (value.equalsIgnoreCase("failed")) {
						style = "#ff0000";
					}
					if (value.equalsIgnoreCase("committed")) {
						style = "#33FF00";
					}
					if (value.equalsIgnoreCase("executing")) {
						style = "#FF6600";
					}
					if (value.equalsIgnoreCase("ready")) {
						style = "#33CC66";
					}
				}
				
				return("<span width='100%' style='color:" + style + "'>" + value + "</span>");
			}
			
	     });
		 
	     columns.add(column);
		 
		 columns.add(new ColumnConfig("date", "Last Modified", 180));
		 columns.add(new ColumnConfig("added", "Added Objects", 100));
		 columns.add(new ColumnConfig("deleted", "Deleted Objects", 100));
		 columns.add(new ColumnConfig("modified", "Modified Objects", 100));
			 
		 // create the column model  
	     final ColumnModel cm = new ColumnModel(columns);  
	   
	     // defines the xml structure  
	     ModelType type = new ModelType();  
	     type.root = "Tree";  
	     type.recordName = "config";
	     
	     type.addField("name", "name");  
	     
	     type.addField("mdr", "config2mdr/mdr/name");  
	     type.addField("status" , "history2config/history/status");  
	     type.addField("date", "history2config/history/@modifyDate");
	     type.addField("added", "history2config/history/added");
	     type.addField("deleted", "history2config/history/deleted");
	     type.addField("modified", "history2config/history/modified");
	     type.addField("mdrAlias", "config2mdr/mdr/@alias");
	     type.addField("configAlias", "@alias");
	     type.addField("historyAlias", "history2config/history/@alias");
	     
	     // use a http proxy to get the data  
	     /*
	     String sourceURL = CMDBSession.get().getExportURL() + "?name=MDR/mdr-status.cfg";
	     
	     RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, sourceURL);  
	     
	     HttpProxy proxy = new HttpProxy(builder);  
	    
	     // need a loader, proxy, and reader  
	     */
	     RpcProxy proxy = new RpcProxy<BaseListLoadConfig, BaseListLoadResult<BaseModel>>() {

			@Override
			protected void load(BaseListLoadConfig loadConfig,
					AsyncCallback<BaseListLoadResult<BaseModel>> callback) {
				ModelServiceFactory.get().loadMDROverview(CMDBSession.get().getToken(),
						mdr,
						loadConfig,
						callback
						);
			}
	     };  
	     
	     loader = new BaseListLoader(proxy); 
	     GroupingStore<BaseModel> store = new GroupingStore<BaseModel>(loader);  
	     store.groupBy("mdr");
		 store.applyFilters("mdr");
		 store.addFilter(new StoreFilter<BaseModel>() {
				public boolean select(Store<BaseModel> store,
						BaseModel parent, BaseModel item,
						String property) {
					String value = item.get(property);
					if (value == null) {
						return(true);
					}
					if (value.equalsIgnoreCase("models")) {
						return(false);
					}
					return(true);
				}

			});

	    	     
	     final Grid grid = new Grid<BaseModel>(store, cm);  
	     grid.setBorders(true);  
		 grid.setLoadMask(true);
	     MyGroupingView view = new MyGroupingView();
	     view.setForceFit(false);
	     view.setGroupRenderer(new GridGroupRenderer() {
	    	 public String render(GroupColumnData data) {
	    		 String f = cm.getColumnById(data.field).getHeader();
	    		 String l = data.models.size() == 1 ? "Item" : "Items";
	    		 return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
	    	 }
	     });
	     grid.setView(view);
	     
	    
	     ToolBar toolBar = new ToolBar();
	     
	     TextToolItem addMDR = new TextToolItem("Add", "add-icon");
	     addMDR.setToolTip("Add a new MDR");
	     addMDR.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					new AddMDRDialog(perm, new Listener<BaseEvent>() {

						public void handleEvent(BaseEvent be) {
							reload();
						}
					}).show();
				}
		    	 
		     });
	     
	     TextToolItem configMDR = new TextToolItem("Configure", "config-icon");
	     configMDR.setToolTip("Configure a MDR configuration");
	     configMDR.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					ModelData item = grid.getSelectionModel().getSelectedItem();
					if (item == null) {
						return;
					}
					configureMDR(item);
				}
		    	 
		     });
	     
	     
	     TextToolItem reload = new TextToolItem("Reload", "refresh-icon");
	     reload.setToolTip("Reload MDR repository definitions");
	     reload.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				reload();
			}
	    	 
	     });
	     
	     TextToolItem execute = new TextToolItem("Execute", "mdr-config-open-icon");
		 execute.setToolTip("Excute selected MDR configs");
		 execute.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				HelpInfo.show("help/mdr/help-mdr-execute.html");

				ModelData item = grid.getSelectionModel().getSelectedItem();
				if (item == null) {
					return;
				}
				List<String> aliases = new ArrayList<String>();
				final String mdrAlias = item.get("mdrAlias");
				final String configAlias = item.get("configAlias");
				
				if (mdrAlias == null || configAlias == null) {
						Info.display("Error", 
								"MDR Aliases MDR={0}, Config={1}", 
								mdrAlias, 
								configAlias);
						return;
				}
				aliases.add(mdrAlias);
				aliases.add(configAlias);
				ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new CMDBAsyncCallback<List<CIModel>>() {

					@Override
					public void onSuccess(List<CIModel> arg0) {
						if (arg0.size() == 2) {
							CIModel selMDR = findAlias(arg0, mdrAlias);
							CIModel selCfg = findAlias(arg0, configAlias);
							
							if (selMDR == null || selCfg == null) {
								Info.display("ErrorLoad", 
										"Load MDR Ojects MDR={0}, Config={1}", 
										selMDR == null ? "empty" : selMDR.toString(), 
										selCfg == null ? "empty" : selCfg.toString());
								return;
							}
							List<ModelData> configs = new ArrayList<ModelData>();
							configs.add(selCfg);
							MDRStartWindow start = new MDRStartWindow(selMDR, configs);
							start.setCloseAction(CloseAction.CLOSE);
							start.addListener(Events.Close, new Listener<BaseEvent>() {

								public void handleEvent(BaseEvent be) {
									reload();
								}
							});
							start.show();
						}
					}
				});
			}
		 });
	     TextToolItem open = new TextToolItem("Open", "run-icon");
	     open = new TextToolItem("Open", "compare-icon");
		 open.setToolTip("Compare this data to the <br>previous data comitted to cmdb");
		 open.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				ModelData item = grid.getSelectionModel().getSelectedItem();
				if (item == null) {
					return;
				}
				startOpen(item);
				
			}
			 
		 });
		 
		 toolBar.add(addMDR);
		 toolBar.add(configMDR);
		 toolBar.add(new SeparatorToolItem());
		 toolBar.add(execute);
		 toolBar.add(open);
		 toolBar.add(new SeparatorToolItem());
		 toolBar.add(reload);
		 //toolBar.add(new FillToolItem());
		 //toolBar.add(new PermissionMenu(perm, PermissionMenu.READONLY_MASK|PermissionMenu.EDIT_MASK|PermissionMenu.DELETE_MASK));
	     panel.setTopComponent(toolBar);
	     
	     ToolBar bottomBar = new ToolBar();
	     bottomBar.add(new FillToolItem());
	     bottomBar.add(new CloseTextToolItem(this));
	     panel.setBottomComponent(bottomBar);
	     panel.add(grid);
	     add(panel);
	     layout();
	  	 reload();
	}
	
	
	protected void reload() {
		if (this.loader != null) {
			HelpInfo.show("help/mdr/help-mdr.html");
			loader.load();
		}
	}
	
	protected void configureMDR(ModelData item) {
		List<String> aliases = new ArrayList<String>();
		final String mdrAlias = item.get("mdrAlias");
		final String configAlias = item.get("configAlias");
		
		if (mdrAlias == null || configAlias == null) {
				Info.display("Error", 
						"MDR Aliases MDR={0}, Config={1}", 
						mdrAlias, 
						configAlias);
				return;
		}
		aliases.add(mdrAlias);
		aliases.add(configAlias);
		ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new CMDBAsyncCallback<List<CIModel>>() {

			@Override
			public void onSuccess(List<CIModel> arg0) {
				if (arg0.size() == 2) {
					CIModel selMDR = findAlias(arg0, mdrAlias);
					CIModel selCfg = findAlias(arg0, configAlias);
					
					if (selMDR == null || selCfg == null) {
						Info.display("ErrorLoad", 
								"Load MDR Ojects MDR={0}, Config={1}", 
								selMDR == null ? "empty" : selMDR.toString(), 
								selCfg == null ? "empty" : selCfg.toString());
						return;
					}
					MDRConfigureWindow widget = new MDRConfigureWindow(perm, selMDR, selCfg);
					Window w = WindowFactory.getWindow("Configure MDR " + selMDR.getValueAsString("name") + "/" + selCfg.getValueAsString("name"), widget);
					WindowFactory.handleWindowSize(null, w, 800, 600);
					//w.setSize(800, 600);
					/*
					Window w = new Window();
					w.setSize(600, 400);
					w.setLayout(new FitLayout());
					w.add(widget);
					*/
					
					w.addListener(Events.Close, new Listener<BaseEvent>() {

						public void handleEvent(BaseEvent be) {
							reload();
						}
						
					});
					w.setCloseAction(CloseAction.CLOSE);
					w.show();

				}
			}
		});
	}
 	
	protected void startOpen(ModelData item) {
		
		List<String> aliases = new ArrayList<String>();
		final String mdrAlias = item.get("mdrAlias");
		final String configAlias = item.get("configAlias");
		final String historyAlias = item.get("historyAlias");
		if (mdrAlias == null || configAlias == null || historyAlias == null) {
			MessageBox.alert("Alert", "No Execute result found, need to ececute first", null);
			return;
		}
		aliases.add(mdrAlias);
		aliases.add(configAlias);
		aliases.add(historyAlias);
		ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new CMDBAsyncCallback<List<CIModel>>() {

			@Override
			public void onSuccess(List<CIModel> arg0) {
				
				CIModel selectedMDR = findAlias(arg0, mdrAlias);
				CIModel selectedMDRConfig = findAlias(arg0, configAlias);
				CIModel selectedHistoryModel = findAlias(arg0, historyAlias);
				
				if (selectedMDR == null || selectedMDRConfig == null || selectedHistoryModel == null) {
					return;
				}
				
				CompareGridWidget widget = new CompareGridWidget();
				List<ContentData> files = new ArrayList<ContentData>();
				ValueModel value = selectedHistoryModel.getValue("files");
				if (value instanceof ValueListModel) {
					for (ValueModel vModel : ((ValueListModel)value).getValues()) {
						if (vModel.getValue() != null) {
							ContentFile file = new ContentFile(vModel.getValue());
							files.add(file);
						}	
					}
				} else {
					if (value.getValue() != null) {
						ContentFile file = new ContentFile(value.getValue());
						files.add(file);
					}
				}
				Window w = WindowFactory.getWindow("Compare selected data with previously imported data", widget);
				/*
				Window w = new Window();
				w.setSize(600, 400);
				w.setLayout(new FitLayout());
				w.add(widget);
				*/
				
				w.addListener(Events.Close, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						reload();
					}
					
				});
				
				HelpInfo.show("help/mdr/help-mdr-execute-result.html");
				
				w.setCloseAction(CloseAction.CLOSE);
				w.show();
				
				
				widget.setModels(selectedMDR, selectedMDRConfig, selectedHistoryModel);		
				
			}

			
		});
		
	}
	
	private CIModel findAlias(List<CIModel> arg0, String mdrAlias) {
		for (CIModel m : arg0) {
			if (m.getAlias().equals(mdrAlias)) {
				return(m);
			}
		}
		return(null);
	}

}
