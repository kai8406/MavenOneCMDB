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
package org.onecmdb.ui.gwt.desktop.client.widget.grid;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.spring.config.ServiceFactoryBean;
import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.control.GridModelConfigLoader;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.PermissionMenu;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBWidgetFactory;
import org.onecmdb.ui.gwt.desktop.client.window.misc.CMDBAppletWidget;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class CIPropertyPanel extends LayoutContainer {

	private ContentData mdr;
	private CIModelCollection localData;
	private CIModelCollection baseData;
	
	protected GridModelConfig gridModelConfig;
	private CIPropertyGrid currentGrid;
	//private boolean readonly;
	private CMDBPermissions permissions;
	private boolean modelChanged = false;
	private String rootCI;
	private ContentPanel cp;
	
	/**
	 * For now we can only handle one ci in the collection.
	 * @param mdr
	 * @param data
	 */
	public CIPropertyPanel(ContentData mdr, CIModelCollection data, String rootCI) {
		this.mdr = mdr;
		this.localData = data;
		this.baseData = data.copy();
		//this.readonly = readonly;
		this.rootCI = rootCI;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		loadData(new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				ExceptionErrorDialog.showError("Problem loading grid config", arg0);
			}

			public void onSuccess(GridModelConfig arg0) {
				gridModelConfig = arg0;
				initUI();
			}
		});
	}

	protected void loadData(AsyncCallback<GridModelConfig> callback) {
		CIModel m = localData.getCIModels().get(0);
		
		// Load GridConfig for this model.
		ContentData gridData = new ContentData();
		if (m.isTemplate()) {
			gridData.set("template", m.getAlias());
		} else {
			gridData.set("template", m.getDerivedFrom());
		}
		
		new GridModelConfigLoader(mdr, gridData, permissions).load(callback);
		/*		
				new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				ExceptionErrorDialog.showError("Problem loading grid config", arg0);
			}

			public void onSuccess(GridModelConfig arg0) {
				gridModelConfig = arg0;
				initUI();
			}
		});
		*/
	}
	
	private void updateModel(final ContentPanel cp, final CIModel model) {
		DeferredCommand.addCommand(new Command() {

			public void execute() {
				localData = new CIModelCollection();
				localData.addCIModel("offspring", model);
				baseData = localData.copy();
				redraw();
				/*
				cp.remove(currentGrid);
				currentGrid = new CIPropertyGrid(gridModelConfig, localData);
				currentGrid.setPermissions(permissions);
				cp.add(currentGrid);
				cp.layout();
				setHeader(getParent(), "Properties for " + model.getDisplayName());
				*/
			}
			
		});
	}

	
	protected void setHeader(Widget parent, String header) {
		if (parent == null) {
			return;
		}
		if (parent instanceof Window) {
			((Window)parent).setHeading(header);
			return;
		}
		setHeader(parent.getParent(), header);
	}

	protected void initUI() {
		currentGrid = new CIPropertyGrid(gridModelConfig, localData);
		currentGrid.setPermissions(permissions);
		
		setLayout(new FitLayout());
		TabPanel panel = new TabPanel();
		TabItem item = new TabItem("Properties");
		item.setLayout(new FitLayout());
		cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		
		
		
		/*
		
		
		String defaultText = "Read Only";
		String defaultIconStyle = "lock-icon";
		boolean defaultReadonly = true;
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
			defaultText = "Read Only";
			defaultIconStyle = "lock-icon";
			defaultReadonly = true;
		}
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
			defaultText = "Edit Allowed";
			defaultIconStyle = "unlock-icon";
			defaultReadonly = false;
		}
		
		final TextToolItem mode = new TextToolItem(defaultText);  
		mode.setIconStyle(defaultIconStyle);  
		   
		Menu menu = new Menu();
		
		if (permissions == null || permissions.isReadonly()) {
			CheckMenuItem r = new CheckMenuItem("Read Only");
			r.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					mode.setIconStyle("lock-icon");
					mode.setText("Read Only");
					permissions.setCurrentState(CMDBPermissions.PermissionState.READONLY);
					redraw();
				}

			});
			r.setGroup("radios");  
			r.setChecked(defaultReadonly); 
			menu.add(r);  
		}
		if (permissions == null || permissions.isEditable()) {
			CheckMenuItem r = new CheckMenuItem("Edit Allowed");  
			r.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					mode.setText("Edit Allowed");
					mode.setIconStyle("unlock-icon");
					permissions.setCurrentState(CMDBPermissions.PermissionState.EDIT);
					redraw();
				}

			});
			r.setGroup("radios"); 
			r.setChecked(!defaultReadonly);
			menu.add(r);  
		}
		mode.setMenu(menu);
		toolBar.add(mode);
		toolBar.add(new SeparatorToolItem());  
		*/
		/*
		TextToolItem close = new TextToolItem("Close", "close-icon");
		close.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (getParent() instanceof Window) {
					((Window)getParent()).close();
				}
				
			}
		});
		*/
		cp.setTopComponent(getToolBar());
		
		cp.add(currentGrid);
		item.add(cp);
		
		panel.add(item);
		ToolBar bottomBar = new ToolBar();
		bottomBar.add(new FillToolItem());
		bottomBar.add(new CloseTextToolItem(this));
		
		
		item = new TabItem("Graph");
		item.setLayout(new FitLayout());
		item.add(new CMDBAppletWidget(getGraphDesktopItem()));
		panel.add(item);
		
		item = new TabItem("Change Log");
		item.setLayout(new FitLayout());
		item.add(new CIChangeLogGrid(mdr, localData.getCIModels().get(0)));
		panel.add(item);
		
		ContentPanel center = new ContentPanel();
		center.setHeaderVisible(false);
		center.setLayout(new FitLayout());
		center.setBottomComponent(bottomBar);
		center.add(panel);
		add(center);
		layout();
	}
		
	protected void redraw() {
		cp.remove(currentGrid);
		
		loadData(new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				ExceptionErrorDialog.showError("Problem loading grid config", arg0);
			}

			public void onSuccess(GridModelConfig arg0) {
				gridModelConfig = arg0;
				currentGrid = new CIPropertyGrid(gridModelConfig, localData);
				currentGrid.setPermissions(permissions);
				
				cp.add(currentGrid);
				cp.layout();
			}
		});
		
	}

	private ToolBar getToolBar() {
		ToolBar toolBar = new ToolBar();
		
		
		
		
			final TextToolItem save = new TextToolItem("Save", "save-icon");

			save.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					final MessageBox saveInfo = MessageBox.wait("Progress",  
							"Saving your data, please wait...", "Saving..."); 


					List<CIModelCollection> local = new ArrayList<CIModelCollection>();
					List<CIModelCollection> base = new ArrayList<CIModelCollection>();

					local.add(localData);
					base.add(baseData);

					ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

						public void onFailure(Throwable caught) {
							// Error.
							saveInfo.close();
							ExceptionErrorDialog.showError("Can't Save", caught);
						}

						public void onSuccess(StoreResult result) {
							saveInfo.close();
							// saved
							if (result.isRejected()) {
								MessageBox.alert("Save Failed", result.getRejectCause(), new Listener<WindowEvent>() {
									public void handleEvent(WindowEvent be) {

									}
								});
								return;
							} else {
								modelChanged = true;
								currentGrid.commit();
								
								// Reload CI:
								
								CIModel m = localData.getCIModels().get(0);
								List<String> aliases = new ArrayList<String>();
								aliases.add(m.getAlias());
								baseData = localData.copy();
								ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new CMDBAsyncCallback<List<CIModel>>() {

									@Override
									public void onSuccess(List<CIModel> arg0) {
										if (arg0.size() == 1) {
											updateModel(cp, arg0.get(0));
										}
									}
								});
								
							}
						}
					});
				}
			});
	
			toolBar.add(new SeparatorToolItem());  
			final TextToolItem undo = new TextToolItem("Undo", "restore-icon");
			undo.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					currentGrid.restore();
				}

			});
	
		
			PermissionMenu menu = new PermissionMenu(permissions, PermissionMenu.READONLY_MASK|PermissionMenu.EDIT_MASK);
	
			menu.addListener(CMDBEvents.PERMISSION_CHANGED, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					if (!(be.source instanceof Integer)) {
						return;
					}
					save.setVisible(permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT));
					undo.setVisible(permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT));
					
					redraw();
				}
				
			});

		/**
		 * Populate the toolbar.
		 */	
		toolBar.add(menu);
		toolBar.add(new FillToolItem());
		toolBar.add(undo);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(save);
		return(toolBar);
	}

	private CMDBDesktopWindowItem getGraphDesktopItem() {
		CIModel model = localData.getCIModels().get(0);
		
		
		BaseModel applet = new BaseModel();
		applet.set("code", "org.onecmdb.rest.graph.utils.applet.AppletLaunch.class");
		applet.set("archive", "onecmdb/content/Content/applet/onecmdb-applet.jar,onecmdb/content/Content/applet/onecmdb-applet-dependencies.jar");
		
		BaseModel appletParams = new BaseModel();
		applet.set("param", appletParams);
		
		
		appletParams.set("url", "${baseURL}/onecmdb/query");
		
		if (model.isTemplate()) {
			appletParams.set("rootCI", rootCI);
			appletParams.set("alias",  model.getAlias());

			appletParams.set("appletlaunch.callcode", "org.onecmdb.rest.graph.applet.TemplateReferenceViewApplet");
			
		} else {
			appletParams.set("rootCI", rootCI);
			appletParams.set("template", model.getDerivedFrom());
			appletParams.set("alias",  model.getAlias());

			appletParams.set("appletlaunch.callcode", "org.onecmdb.rest.graph.applet.InstanceViewApplet");
		}
		appletParams.set("appletlaunch.color.background", "FFFFFF");
		appletParams.set("appletlaunch.splash", "onecmdb/content/Content/applet/applet-loading.gif");
		appletParams.set("appletlaunch.splasherror", "onecmdb/content/Content/applet/applet-error.gif");
		appletParams.set("appletlaunch.version", "1.5.0");
		appletParams.set("iconURL", "${baseURL}/onecmdb/icon");
		appletParams.set("graphBackgroundColor", "0x454545");
		
		CMDBDesktopWindowItem graph = new CMDBDesktopWindowItem();
		graph.setParams(applet);
		return(graph);
	}

	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions == null ? null : permissions.copy();
	}

	public boolean isModelChanged() {
		return(this.modelChanged);
	}

}
