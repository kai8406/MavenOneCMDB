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

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ColumnFilter;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.utils.CIModelUtils;
import org.onecmdb.ui.gwt.desktop.client.utils.PermissionMenu;
import org.onecmdb.ui.gwt.desktop.client.widget.CompareGridWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableCIInstanceGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.INewInstance;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.InstanceList;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRStartWindow;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class MDRDetailWindow extends CMDBAbstractWidget {
	public static final String ID = "cmdb-mdr-view";
	private ContentPanel center;
	private ContentFile mdr;
	private LayoutContainer configPanel;
	private ContentPanel historyPanel;
	private TextToolItem startTool;
	private TextToolItem configTool;
	//private TextToolItem historyTool;
	
	private List selectedItems;
	private TextToolItem compare;
	
	protected CIModel selectedHistoryModel;
	protected CIModel selectedMDRConfig;
	protected CIModel selectedMDR;
	private EditableCIInstanceGrid historyGrid;
	private InstanceList list;
	
	public MDRDetailWindow(CMDBDesktopWindowItem item) {
		super(item);
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setLayout(new BorderLayout());
		mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		
		String mdrTableName = (String)item.getParams().get("mdrTable");
		ContentFile mdrTableFile = new ContentFile();

		if (mdrTableName == null) {
			mdrTableFile.set("template", "MDR_Repository");
		}  else {
			mdrTableFile.setPath(mdrTableName);
		}
		
		// Check if we have excludePattern
		List<String> excludes = item.getParams().get("excludeMDR");
		List<String> includes = item.getParams().get("includeMDR");
			
		// Left panel is holding the defined MDR(s)
		ContentPanel left = new ContentPanel();
		left.setHeading("Available MDRs");
		left.setLayout(new FitLayout());
		list = new InstanceList(mdr, mdrTableFile, "Name");
		list.setPermissions(permissions);
		if (excludes != null) {
			list.setFilterPatterns(excludes);
			list.setFilterExclude(true);
		}
		if (includes != null) {
			list.setFilterPatterns(includes);
			list.setFilterExclude(false);
		}
		
		
		list.setSelectionListener(getConfigSelectionListener());
		left.add(list);
		left.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				reloadMDRs();
			}
		}));
		ToolBar top = new ToolBar();
		TextToolItem add = new TextToolItem("Add MDR", "add-icon");
		add.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				new AddMDRDialog(permissions, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						reloadMDRs();
					}
				}).show();
			}
			
		});
		top.add(new FillToolItem());
		top.add(add);
		left.setTopComponent(top);
		
		center = new ContentPanel();
		center.setHeading("Available configurations of ");
		center.setLayout(new BorderLayout());
		
		startTool = new TextToolItem("Execute", "mdr-config-open-icon");
		startTool.setToolTip("Excute selected MDR configs");
		startTool.setEnabled(false);
		startTool.addSelectionListener(getStartSelectionListener());

		
		configTool = new TextToolItem("Configure", "config-icon");
		configTool.setToolTip("Configure MDR");
		configTool.setEnabled(false);
		configTool.addSelectionListener(getConfigureSelectionListener());

		PermissionMenu permMenu = new PermissionMenu(permissions, PermissionMenu.READONLY_MASK|PermissionMenu.EDIT_MASK|PermissionMenu.DELETE_MASK);
		
			/*
		historyTool = new TextToolItem("Show History", "history-icon");
		historyTool.setEnabled(false);
		historyTool.addSelectionListener(getHistorySelectionListener());
		*/
		ToolBar bar = new ToolBar();
		bar.add(startTool);
		bar.add(configTool);
		bar.add(new FillToolItem());
		bar.add(permMenu);
			//bar.add(historyTool);
		center.setTopComponent(bar);

		BorderLayoutData centerCenterData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerCenterData.setMargins(new Margins(5, 0, 5, 0));  
	   
	    BorderLayoutData centerSouthData = new BorderLayoutData(LayoutRegion.SOUTH, 200);  
	    centerSouthData.setSplit(true);  
	    centerSouthData.setCollapsible(true);  
	    centerSouthData.setMargins(new Margins(5));  
	
		configPanel = new LayoutContainer();
		configPanel.setLayout(new FitLayout());
		
		ToolBar historyBar = new ToolBar();
		historyBar.add(new FillToolItem());
		compare = new TextToolItem("Open", "compare-icon");
		compare.setToolTip("Compare this data to the <br>previous data comitted to cmdb");
		compare.setEnabled(false);
		compare.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (selectedHistoryModel == null) {
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
						reloadHistory();
					}
					
				});
				w.setCloseAction(CloseAction.CLOSE);
				w.show();
				
				widget.setModels(selectedMDR, selectedMDRConfig, selectedHistoryModel);
			}
			
		});
		historyBar.add(compare);
		/*
		TextToolItem close = new TextToolItem("Close", "close-icon");
		close.setToolTip("Close this window");
		close.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (getParent() instanceof Window) {
					((Window)getParent()).close();
				}
			}
			
		});
		*/
		historyBar.add(new CloseTextToolItem(this));
		
		historyPanel = new ContentPanel();
		historyPanel.setLayout(new FitLayout());
		historyPanel.setBottomComponent(historyBar);
		
		center.add(configPanel, centerCenterData);
		center.add(historyPanel, centerSouthData);
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	 
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
		
		add(left, westData);
		add(center, centerData);
		layout();
	
		permMenu.addListener(CMDBEvents.PERMISSION_CHANGED, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				reloadConfigPanel();
			}
			
		});

	}



	private SelectionListener getConfigureSelectionListener() {
		return(new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				if (selectedItems == null || selectedItems.size() == 0) {
					return;
				}
				
				// Open Window...
				MDRConfigureWindow config = new MDRConfigureWindow(permissions, selectedMDR, selectedMDRConfig);
				Window w = WindowFactory.getWindow("MDR Configuration", config);
				w.setSize(800, 600);
				w.show();
				w.layout();
				w.toFront();
			}
			
		});
	}



	protected void reloadMDRs() {
		list.getLoader().load();		
	}



	private SelectionListener getStartSelectionListener() {
		return(new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				if (selectedItems == null || selectedItems.size() == 0) {
					return;
				}
				MDRStartWindow start = new MDRStartWindow(selectedMDR, selectedItems);
				start.setCloseAction(CloseAction.CLOSE);
				start.addListener(Events.Close, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						reloadHistory();
					}
				});
				start.show();
				
			}
			
		});
	}


	private void reloadHistory() {
		if (historyGrid != null) {
			historyGrid.reload();
		}
	}
	
	private Listener<SelectionChangedEvent> getConfigSelectionListener() {
		return(new Listener<SelectionChangedEvent>() {

			public void handleEvent(SelectionChangedEvent be) {
				
				List selected = be.getSelection();
				CIModelCollection col = (CIModelCollection) selected.get(0);
				selectedMDR = col.getCIModels().get(0);
				
				reloadConfigPanel();
				
			}

			
		});
	}
	
	private void reloadConfigPanel() {
		center.setHeading("Available configurations for MDR " + selectedMDR.getDisplayName());
		
		ContentData cd = new ContentData();
		String configTemplate = selectedMDR.getValueAsString("configAlias");
		cd.set("template", configTemplate);
		
		EditableCIInstanceGrid grid = new EditableCIInstanceGrid(mdr, cd, "Config entries for ");
		grid.setPermissions(permissions);
		grid.setRootCI("Root");
		BaseModel tableConfig = item.getParams().get("mdrConfigTable");
		if (tableConfig != null) {
			grid.setColumnFilter(new ColumnFilter((BaseModel)tableConfig.get("columnFilter")));
		}
		grid.setNewInstanceCallback(new INewInstance() {

			public CIModelCollection createInstance(GridModelConfig ctrl) {
				CIModelCollection model = ctrl.createNewInstance();
				// Connect config to mdr.
				CIModel config = model.getCIModels().get(0);
				String derivedFrom = selectedMDR.getValueAsString("configAlias");
				
				if (derivedFrom != null && derivedFrom.length() > 0) {
					config.setDerivedFrom(derivedFrom);
				}
				CIModelUtils.updateModel(config, "mdrRepository", selectedMDR.getAlias(), true);
				return(model);
			}
			
		});
		grid.setHeaderVisible(false);
		grid.setSelectable(true);
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setQuery(getConfigQuery(selectedMDR));
		grid.setSelectionListener(new Listener<SelectionChangedEvent>() {


			public void handleEvent(SelectionChangedEvent be) {
				if (be.getSelection().size() == 1) {
					CIModelCollection col = (CIModelCollection) be.getSelection().get(0);
					CIModel config = col.getCIModels().get(0);
					selectedItems = new ArrayList();
					selectedItems.add(config);
					boolean redraw = true;
					if (config.equals(selectedMDRConfig)) {
						redraw = false;
					}
					selectedMDRConfig = config;
					if (redraw) {
						showHistory(selectedMDRConfig);
					}
					//historyTool.setEnabled(true);
					startTool.setEnabled(true);
					configTool.setEnabled(true);
				} else if (be.getSelection().size() > 0) {
					hideHistory();
					configTool.setEnabled(false);
					startTool.setEnabled(true);
					selectedItems = be.getSelection();
					selectedMDRConfig = null;
					//historyTool.setEnabled(false);
				} else {
					hideHistory();
					selectedItems = null;
					startTool.setEnabled(false);
					selectedMDRConfig = null;
					//historyTool.setEnabled(false);
				}
			}

		
			
		});
		
		configPanel.removeAll();
		configPanel.add(grid);
		configPanel.layout();

	}
	private String getConfigQuery(CIModel selectedMDR) {
		StringBuffer b = new StringBuffer();
		String configTemplate = selectedMDR.getValueAsString("configAlias");
		b.append("<?xml version=\"1.0\" ?>");
		b.append("<GraphQuery>");
		b.append("<ItemOffspringSelector id=\"offspring\" template=\"" + configTemplate + "\" primary=\"true\">");
		b.append("</ItemOffspringSelector>");
		b.append("<ItemAliasSelector id=\"mdr\" template=\"MDR_Repository\" >");
		b.append("<alias>" + selectedMDR.getAlias() + "</alias>");
		b.append("<excludeInResult>true</excludeInResult>");
		b.append("</ItemAliasSelector>");
		b.append("<ItemRelationSelector id=\"config2mdr\" template=\"Reference\">");
		b.append("<target>mdr</target>");
		b.append("<source>offspring</source>");
		b.append("</ItemRelationSelector>");
		b.append("</GraphQuery>");
		
		return(b.toString());
	}

	private String getHistoryQuery(CIModel selectedConfig) {
		StringBuffer b = new StringBuffer();
		b.append("<?xml version=\"1.0\" ?>");
		b.append("<GraphQuery>");
		b.append("<ItemOffspringSelector id=\"offspring\" template=\"" + MDRHistoryState.getHistoryTemplate() +"\" primary=\"true\">");
		b.append("</ItemOffspringSelector>");
		b.append("<ItemAliasSelector id=\"mdr\" template=\"MDR_ConfigEntry\" >");
		b.append("<alias>" + selectedConfig.getAlias() + "</alias>");
		b.append("<excludeInResult>true</excludeInResult>");
		b.append("</ItemAliasSelector>");
		b.append("<ItemRelationSelector id=\"config2mdr\" template=\"Reference\">");
		b.append("<target>mdr</target>");
		b.append("<source>offspring</source>");
		b.append("</ItemRelationSelector>");
		b.append("</GraphQuery>");
		
		return(b.toString());
	}

	private void hideHistory() {
		this.selectedMDRConfig = null;
		historyPanel.removeAll();
		historyPanel.layout();
	}
	
	private void showHistory(CIModel mdrConfig) {
		this.selectedMDRConfig = mdrConfig;
		ContentData cd = new ContentData();
		cd.set("template", MDRHistoryState.getHistoryTemplate());
		historyGrid = new EditableCIInstanceGrid(mdr, cd, "Config entries for ");
		historyGrid.setRootCI("Root");
		historyGrid.setPermissions(new CMDBPermissions(CMDBPermissions.PermissionState.READONLY));
		historyGrid.setQuery(getHistoryQuery(mdrConfig));
		BaseModel tableConfig = item.getParams().get("mdrHistoryTable");
		if (tableConfig != null) {
			historyGrid.setColumnFilter(new ColumnFilter((BaseModel)tableConfig.get("columnFilter")));
		}
		historyGrid.setHeaderVisible(false);
		historyGrid.setSelectable(true);
		historyGrid.setSelectionMode(SelectionMode.SINGLE);
		historyGrid.setSelectionListener(new Listener<SelectionChangedEvent>() {


			public void handleEvent(SelectionChangedEvent be) {
				if (be.getSelection().size() == 1) {
					compare.setEnabled(true);
					CIModelCollection col = (CIModelCollection) be.getSelection().get(0);
					selectedHistoryModel = col.getCIModels().get(0);
				} else {
					selectedHistoryModel = null;
					compare.setEnabled(false);
				}
			}
			
		});
		//grid.setContextMenuItem(getHistoryMenuItem());
		historyPanel.removeAll();
		historyPanel.setHeading("Execution result for " + mdrConfig.getDisplayName());
		historyPanel.add(historyGrid);
		historyPanel.layout();
		
	}
	private SelectionListener<ToolBarEvent> getHistorySelectionListener() {
		return(new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				showHistory(selectedMDRConfig);
				/*
				ContentData cd = new ContentData();
				cd.set("template", MDRHistoryState.getHistoryTemplate());
				final EditableCIInstanceGrid grid = new EditableCIInstanceGrid(mdr, cd, "Config entries for ");
				grid.setReadonly(true);
				grid.setHeaderVisible(false);
				grid.setSelectable(true);
				grid.setSelectionMode(SelectionMode.SINGLE);
				grid.setSelectionListener(new Listener<SelectionEvent>() {


					public void handleEvent(SelectionEvent be) {
						System.out.println("Selected ...");
						if (be.selection.size() == 1) {
							compare.setEnabled(true);
							CIModelCollection col = (CIModelCollection) be.selection.get(0);
							selectedHistoryModel = col.getCIModels().get(0);
						} else {
							selectedHistoryModel = null;
							startTool.setEnabled(false);
						}
					}
					
				});
				//grid.setContextMenuItem(getHistoryMenuItem());
				
				historyPanel.removeAll();
				
				historyPanel.add(grid);
				historyPanel.layout();
				*/
			}
			
		});
	}

	
	


	


	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB MDR View");
		desc.setDescription("A Widget that views all defined MDR(s). It's also possiable to start importing from MDR.");
		desc.addParameter("<li>excludeMDR - Exclude MDR names, must be simpleList=true</li>");
		desc.addParameter("<li>includeMDR - Include MDR names, must be simpleList=true</li>");
		return(desc);	
	}

}
