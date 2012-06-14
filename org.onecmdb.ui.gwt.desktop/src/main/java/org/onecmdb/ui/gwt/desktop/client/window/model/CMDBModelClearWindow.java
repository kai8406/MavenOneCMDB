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
package org.onecmdb.ui.gwt.desktop.client.window.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.startup.SetAllPropertiesRule;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.SaveDeleteRequest;
import org.onecmdb.ui.gwt.desktop.client.service.model.SaveItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.SaveResponse;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.CIModelBrowser;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateBrowser;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CMDBModelClearWindow extends CMDBAbstractWidget {
	public static final String ID = "cmdb-model-delete";
	private GroupingStore<SaveItem> templateStore;
	private GroupingStore<SaveItem> referenceStore;
	private EditorGrid<SaveItem> refGrid;
	private EditorGrid<SaveItem> templateGrid;
	private ContentFile mdr;
	
	public CMDBModelClearWindow(CMDBDesktopWindowItem item) {
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
		
		List<String> roots = null;
		Object o = item.getParams().get("rootCI");
		if (o instanceof List) {
			roots = item.getParams().get("rootCI");
		}
		CITemplateBrowser browser = new CITemplateBrowser(mdr, roots);
		browser.setReadonly(true);
		browser.setCheckable(true, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				if (be instanceof TreeEvent) {
					TreeEvent te = (TreeEvent)be;
					if (te.item.isChecked()) {
						addItem(te.item);
					} else {
						removeItem(te.item);
					}
				}
				
			}
			
		});
		ContentPanel cp = new ContentPanel();
		cp.setHeading("Select Template");
		cp.add(browser);
		cp.setLayout(new FitLayout());
		cp.setScrollMode(Scroll.AUTO);
		
		BorderLayoutData left = new BorderLayoutData(LayoutRegion.WEST, 200);
		left.setSplit(true);
		left.setCollapsible(true);
		left.setMargins(new Margins(5));
		
		
		BorderLayoutData center = new BorderLayoutData(LayoutRegion.CENTER);
		ContentPanel centerPanel = new ContentPanel();
		
		centerPanel.addButton(new Button("Delete", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				doDelete(true);
			}
		}));
	
		centerPanel.setLayout(new RowLayout());
		
		// Create Template Editable Grid.
		templateStore = new GroupingStore<SaveItem>();  
		templateStore.clearGrouping();
		templateStore.setMonitorChanges(true);
		
		templateGrid = getGridModel(mdr, templateStore, true);
		ContentPanel templCp = new ContentPanel();
		templCp.setLayout(new FitLayout());
		templCp.setHeading("Template(s)/Instanace(s)");
		templCp.add(templateGrid);
		
		centerPanel.add(templCp, new RowData(1,0.6));
			
		// Create Reference Editable Grid.
		referenceStore = new GroupingStore<SaveItem>();  
		referenceStore.clearGrouping();
		referenceStore.setMonitorChanges(true);
		refGrid = getGridModel(mdr, referenceStore, false);
		ContentPanel refCp = new ContentPanel();
		refCp.setLayout(new FitLayout());
		refCp.setHeading("Reference(s)");
		refCp.add(refGrid);
		centerPanel.add(refCp, new RowData(1,0.4));
		
		add(cp, left);
		add(centerPanel, center);
		layout();
		
	}


	protected void doDelete(final boolean verify) {
		String message = "Verify deleting your data, please wait...";
		String prgText = "Verifing..."; 
		if (!verify) {
			message = "Deleting your data, please wait...";
			prgText = "Deleting..."; 
		}
		final MessageBox saveInfo = MessageBox.wait("Progress",  
	             message, prgText); 
		SaveDeleteRequest req = new SaveDeleteRequest();
		req.setTemplates(templateStore.getModels());
		req.setReferences(referenceStore.getModels());
		req.setVerify(verify);
		ModelServiceFactory.get().delete(CMDBSession.get().getToken(), mdr, req, new AsyncCallback<SaveResponse>() {

			public void onFailure(Throwable caught) {
				saveInfo.close();
				ExceptionErrorDialog.showError("Can't delete!", caught);
			}

			public void onSuccess(SaveResponse result) {
				saveInfo.close();
				if (verify) {
					MessageBox.confirm("Confirm", "Delete " + 
							result.getTemplateCIs() + 
							" templates and " + 
							result.getInstanceCIs() + 
							" instances!" + 
							"<br>Are you sure?", new Listener<WindowEvent>() {
						
						public void handleEvent(WindowEvent ce) {
							Dialog dialog = (Dialog) ce.component;  
							Button btn = dialog.getButtonPressed();  
							if (btn.getItemId().equals(Dialog.YES)) {
								doDelete(false);
							}
						}
					}); 
				} else {
					
					if (result.isFailed()) {
						MessageBox.alert("Can't delete", result.getFailedCause(), null).show();
					} else {
						MessageBox.alert("Model Deleted", "CI's delete." + 
							"<br>" + result.getTemplateCIs() + " Templates deleted" + 
							"<br>" + result.getInstanceCIs() + " Instances deleted", null).show();
					}
				}
			}
		});
		
	}

	private EditorGrid<SaveItem> getGridModel(ContentData mdr, ListStore<SaveItem> store, boolean saveInstances) {
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();  

		config.add(EditorFactory.getColumn(mdr, "ci", "Template", 100, false, "xs:string", 1, false, false));
		if (saveInstances) {
			config.add(EditorFactory.getColumn(mdr, "saveInstances", "Instance(s)", 60, true, "xs:boolean", 1, false, false));
		}
		config.add(EditorFactory.getColumn(mdr, "saveTemplates", "Template(s)", 60, true, "xs:boolean", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "allChildren", "All Children", 60, true, "xs:boolean", 1, false, false));
	

		final ColumnModel cm = new ColumnModel(config);  

		GroupingView view = new GroupingView();  
		view.setForceFit(true);  

		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) {  
				String f = cm.getColumnById(data.field).getHeader();  
				String l = data.models.size() == 1 ? "Item" : "Items";  
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		});  

		EditorGrid<SaveItem> grid = new EditorGrid<SaveItem>(store, cm);  
		grid.setView(view);  
		grid.setBorders(true);
		for (ColumnConfig cfg : config) {
			if (cfg instanceof CheckColumnConfig) {
				grid.addPlugin((ComponentPlugin)cfg);
			}
		}
		store.setStoreSorter(null);
		return(grid);
	}

	protected void removeItem(TreeItem item) {
		CIModel ci = null;
		if (item.getModel() instanceof CIModel) {
			ci = (CIModel)item.getModel();
		}
		if (ci == null) {
			return;
		}
		for (SaveItem saveItem : templateStore.getModels()) {
			if (ci.getAlias().equals(saveItem.getAlias())) { 
				templateStore.remove(saveItem);
				break;
			}
		}
		for (SaveItem saveItem : referenceStore.getModels()) {
			if (ci.getAlias().equals(saveItem.getAlias())) { 
				referenceStore.remove(saveItem);
				break;
			}
		}
	
	}

	protected void addItem(TreeItem item) {
		CIModel ci = null;
		if (item.getModel() instanceof CIModel) {
			ci = (CIModel)item.getModel();
		}
		if (ci == null) {
			return;
		}
		
		boolean isCI = true;
		if (hasParent(item, "CIReference")) {
			isCI = false;
		}
		
		// TODO: Check if reference or Ci.
		SaveItem saveItem = new SaveItem();
		saveItem.setAlias(ci.getAlias());
		saveItem.setCI(ci);
		saveItem.setAllChildren(false);
		saveItem.setSaveTemplates(true);
		saveItem.setSaveInstances(false);
		
		if (isCI) {
			templateGrid.stopEditing();
			templateStore.insert(saveItem, 0);
		} else {
			refGrid.stopEditing();
			referenceStore.insert(saveItem, 0);
		}
	
	}
	
	private boolean hasParent(TreeItem item, String alias) {
		if (item == null) {
			return(false);
		}
		if (item.getModel() instanceof CIModel) {
			if (((CIModel)item.getModel()).getAlias().equals(alias)) {
				return(true);
			}
		}
		return(hasParent(item.getParentItem(), alias));
	}


	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB Cleanup Model");
		desc.setDescription("Remove template/instances from OneCMDB.");
		desc.addParameter("<li>mdrConf - path to configuration for the MDR. Default is OneCMDB<li>");
		desc.addParameter("<li>rootCI - [List] Root CI's to display.<li>");	
		
		return(desc);
	}

}
