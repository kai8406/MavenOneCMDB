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
package org.onecmdb.ui.gwt.desktop.client.window.instance;

import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ColumnFilter;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableCIInstanceGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CIInstanceEditableReferenceTree;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CIInstanceEditableReferenceTreeV2;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateBrowser;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;
import org.onecmdb.ui.gwt.desktop.client.window.misc.CMDBAppletWidget;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SplitToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToggleToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;

public class CMDBInstanceOverviewWidget extends CMDBAbstractWidget {

	public static final String ID = "cmdb-model-instance-overview";
	
	private ContentFile mdr;
	List<String> roots = null;

	private TabItem graphItem;

	private TabItem treeItem;

	private TabItem tableItem;

	/*
	protected boolean readonly;
	protected boolean deletable;
	protected boolean classify;
	*/

	protected CIModel currentSelectedModel;

	private TabPanel folder;

	protected HashMap<CIModel, List<ModelItem>> moveTable = new HashMap<CIModel, List<ModelItem>>();

	private BaseModel treeConfig;
	private BaseModel graphConfig;
	private BaseModel tableConfig;

	private String currentTemplatePath;

	private ContentPanel center;




	
	public CMDBInstanceOverviewWidget(CMDBDesktopWindowItem item) {
		super(item);
	}

	

	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// Handle input params.
		BaseModel param = item.getParams();
		
		mdr = new ContentFile();
		String mdrConf = param.get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		Object o = param.get("rootCI");
		if (o instanceof List) {
			roots = param.get("rootCI");
		}
		String searchRootCi = param.get("searchRefRootCi", "Ci");
		mdr.set("rootCi", searchRootCi);
		
		treeConfig = getBaseModel(param, "tree");
		tableConfig = getBaseModel(param, "table");
		graphConfig = getBaseModel(param, "graph");
		
		
		initUI();
	}
	
	
	
	





	protected void initUI() {
		
		folder = new TabPanel();  
		/*
		folder.setAutoHeight(true);  
		folder.setAutoWidth(true);
		*/
		if (tableConfig != null && "true".equals(tableConfig.get("visible"))) {
			tableItem = new TabItem("Table");
			tableItem.setLayout(new FitLayout());
			//tableItem.setLayoutOnChange(true);
			
			folder.add(tableItem);
		}
		if (treeConfig != null && "true".equals(treeConfig.get("visible"))) {
			
			treeItem = new TabItem("Tree");
			treeItem.setLayout(new FitLayout());
			//treeItem.setLayoutOnChange(true);
			folder.add(treeItem);
		}
		if (graphConfig != null && "true".equals(graphConfig.get("visible"))) {
			graphItem = new TabItem("Graph");
			graphItem.setLayout(new FitLayout());
			//graphItem.setLayoutOnChange(true);
			folder.add(graphItem);
		}
		
		
		
		setLayout(new FitLayout());
		final CITemplateBrowser browser = new CITemplateBrowser(mdr, roots);
		browser.setReadonly(true);
		
		//final CIInstanceBrowser center = new CIInstanceBrowser(mdr);
		center = new ContentPanel();
		center.setHeading("Instances");
		center.setLayout(new FitLayout());
		center.setLayoutOnChange(true);
		center.add(folder);
		
		browser.setSelectionListsner(new Listener<TreeEvent>() {

			public void handleEvent(TreeEvent te) {  
				TreeItem selectedItem = te.tree.getSelectedItem();  
				if (selectedItem != null) {  
					if (selectedItem.getModel() instanceof CIModel) {
						currentTemplatePath = getTemplatePath(selectedItem);
						currentSelectedModel = (CIModel)selectedItem.getModel();
						redraw();						
					}					
					//Info.display("Selection Changed", "The '{0}' item was selected", item.getText());  
				}
			}

			private String getTemplatePath(TreeItem item) {
				if (item == null) {
					return("/");
				}
				if (!(item.getModel() instanceof CIModel)) {
					return("/");
				}
				CIModel model = (CIModel) item.getModel();
				/*
				if (item.getParentItem() == null) {
					return("/" + model.getAlias());
				}
				*/
				return(getTemplatePath(item.getParentItem())  + model.getAlias() + "/");
			}  
		});  

		folder.addListener(Events.Select, new Listener<TabPanelEvent>() {

			public void handleEvent(TabPanelEvent be) {
				be.item.layout();
			}
			
		});
		ToolBar toolBar = new ToolBar();
		
		String defaultText = "Read Only";
		String defaultIconStyle = "lock-icon";
		permissions.setCurrentState(CMDBPermissions.PermissionState.READONLY);
		//readonly = true;
		if (permissions != null) {
			if ("readonly".equals(permissions.getDefault())) {
				// Allready Default....
			}
			if ("editable".equals(permissions.getDefault())) {
				defaultText = "Edit Allowed";
				defaultIconStyle = "unlock-icon";
				permissions.setCurrentState(CMDBPermissions.PermissionState.EDIT);
				/*
				readonly = false;
				deletable = false;
				classify = false;
				*/
				browser.setMoveTable(null);
			}
			if ("deletable".equals(permissions.getDefault())) {
				defaultText = "Delete Allowed";
				defaultIconStyle = "unlock-delete-icon";
				permissions.setCurrentState(CMDBPermissions.PermissionState.DELETE);
				/*	
				readonly = false;
				deletable = true;
				classify = false;
				*/
				browser.setMoveTable(null);
			}
			if ("classify".equals(permissions.getDefault())) {
				defaultText = "Classify Allowed";
				defaultIconStyle = "classify-icon";
				permissions.setCurrentState(CMDBPermissions.PermissionState.CLASSIFY);
				/*
				readonly = true;
				deletable = false;
				classify = true;
				*/
				moveTable.clear();
				browser.setMoveTable(moveTable);
			}
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
					/*
					readonly = true;
					deletable = false;
					classify = false;
					*/
					browser.setMoveTable(null);
					redraw();
				}

			});
			r.setGroup("radios");  
			r.setChecked(true); 

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
					/*
					readonly = false;
					deletable = false;
					classify = false;
					*/
					browser.setMoveTable(null);
					redraw();
				}

			});
			r.setGroup("radios");  
			menu.add(r);  
		}
		if (permissions == null || permissions.isDeletable()) {
			CheckMenuItem r = new CheckMenuItem("Delete Allowed");  
			r.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					mode.setText("Delete Allowed");
					mode.setIconStyle("unlock-delete-icon");
					permissions.setCurrentState(CMDBPermissions.PermissionState.DELETE);
					/*
					readonly = false;
					deletable = true;
					classify = false;
					*/
					browser.setMoveTable(null);
					redraw();
				}

			});
			r.setGroup("radios");  
			menu.add(r);  
		}
		if (permissions == null || permissions.isClassify()) {
			CheckMenuItem r = new CheckMenuItem("Classify Allowed");  
			r.addSelectionListener(new SelectionListener<ComponentEvent>() {
	
				@Override
				public void componentSelected(ComponentEvent ce) {
					mode.setText("Classify Allowed");
					mode.setIconStyle("classify-icon");
					permissions.setCurrentState(CMDBPermissions.PermissionState.CLASSIFY);
					/*
					readonly = true;
					deletable = false;
					classify = true;
					*/
					moveTable.clear();
					browser.setMoveTable(moveTable);
					redraw();
				}
				
			});
			r.setGroup("radios");  
			menu.add(r);  
		}
		
		mode.setMenu(menu);
		toolBar.add(new FillToolItem());
		toolBar.add(mode);  
		
		ContentPanel left = new ContentPanel();
		left.setScrollMode(Scroll.AUTO);
		left.setHeading("Template Hierarchy");
		left.setLayout(new FitLayout());
		left.setLayoutOnChange(true);
		
		left.setTopComponent(toolBar);
		left.add(browser);
		
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
	    ContentPanel cp = new ContentPanel();
	    cp.setLayout(new BorderLayout());
	    cp.setHeaderVisible(false);
	    
	    
	    ToolBar bottom = new ToolBar();
		bottom.add(new FillToolItem());
		bottom.add(new CloseTextToolItem(this));
		cp.setBottomComponent(bottom);
		
		cp.add(left, westData);
		cp.add(center, centerData);
		
		add(cp);
		
		layout();

		
		
	}





	protected void redraw() {
			if (currentSelectedModel == null) {
				return;
			}
			final CIModel model = currentSelectedModel;
			// Add Table.
			
			// Update header text.
			center.setHeading("Instances of " + model.getAlias());
			if (tableItem != null) {
				ContentData cd = new ContentData();
				cd.set("template", model.getAlias());
				EditableCIInstanceGrid grid = new EditableCIInstanceGrid(mdr, cd, model.getNameAndIcon());
				if (roots.contains("Root")) {
					grid.setRootCI("Root");
				}
				/*
				grid.setReadonly(readonly);
				grid.setDeletable(deletable);
				grid.setClassify(classify);
				*/
				grid.setPermissions(permissions);
				String pageSize = item.getParams().get("pageSize");
				if (pageSize == null) {
					pageSize = CMDBSession.get().getConfig().get(Config.DEFAULT_PAGE_SIZE);
				}
				if (pageSize != null) {
					try {
						int pages = Integer.parseInt(pageSize);
						grid.setPageSize(pages);
					} catch (Throwable e) {
					}
				}

				// Need to fetch column filters.
				BaseModel columnFilter = findTableColumnFilter(model);
				grid.setColumnFilter(new ColumnFilter(columnFilter));
				tableItem.removeAll();
				tableItem.add(grid);

				if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.CLASSIFY)) {
					grid.setMoveTable(moveTable);
					folder.setSelection(tableItem);
				} else {
					grid.setMoveTable(null);
				}
			}
			if (treeItem != null) {
				// Add Tree
				treeItem.removeAll();
				CIInstanceEditableReferenceTreeV2 tree = new CIInstanceEditableReferenceTreeV2(mdr, model);
				/*
				tree.setReadonly(readonly);
				tree.setDeletable(deletable);
				*/
				tree.setPermissions(permissions);
				treeItem.add(tree);
			}
			
			// Add Graph.
			if (graphItem != null) {
				graphItem.removeAll();
				CMDBDesktopWindowItem appletItem = new CMDBDesktopWindowItem();
				BaseModel m = item.getParams().get("graph");
				if (m != null) {
					BaseModel appletPar = m.get("param");
					if (appletPar != null) {
						appletPar.set("template", model.getAlias());
					}
					appletItem.set("params", m);
					graphItem.add(new CMDBAppletWidget(appletItem));
					//graphItem.layout();
				}
			}
			
			if (folder.getSelectedItem() != null) {
				folder.getSelectedItem().layout();
			}
	}





	protected BaseModel findTableColumnFilter(CIModel model) {
		if (tableConfig == null) {
			return(null);
		}
		Object o = tableConfig.get("ColumnFilter");
		if (o instanceof List) {
			List<BaseModel> list = (List<BaseModel>)o;
			for (BaseModel m : list) {
				if (model.getAlias().equals(m.get("alias"))) {
					return(m);
				}
			}
			
			// Check if parent exists.
			if (currentTemplatePath != null) {
				for (BaseModel m : list) {
					String alias = m.get("alias");
					if (alias != null) {
						if (currentTemplatePath.contains("/" + alias + "/")) {
							return(m);
						}
					}
				}
			}
			
		}
		
		return(null);
	}





	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB Instance Overview");
		desc.setDescription("A Widget that displays CI's instances in table/tree and graphs");
		desc.addParameter("<li>permissions - permissions on this widget (readonly/editable/deletable/classify)<li>");
		desc.addParameter("<li>mdrConf - path to configuration for the MDR. Default is to OneCMDB.<li>");
		desc.addParameter("<li>rootCI - [List] Root CI's to display.<li>");
		
		return(desc);	
	}

}
