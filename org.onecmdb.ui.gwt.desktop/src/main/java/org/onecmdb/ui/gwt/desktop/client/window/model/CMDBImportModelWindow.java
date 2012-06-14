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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.model.AliasFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.utils.CIModelUtils;
import org.onecmdb.ui.gwt.desktop.client.widget.CompareGridWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.ContentSelectorWidget;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DataList;
import com.extjs.gxt.ui.client.widget.DataListItem;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolItem;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class CMDBImportModelWindow extends CMDBAbstractWidget {

	public static final String ID = "cmdb-model-import";

	private CIModel mdrRepository;
	private CIModel mdrConfig;

	private ContentSelectorWidget storedTree;

	//private DataList storeList;
	//private CIModel mdrConfigBase;
	
	public CMDBImportModelWindow(CMDBDesktopWindowItem item) {
		super(item);
	}
	
	public ContentFile getMDR() {
		ContentFile mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		return(mdr);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// Load modelMDR and modelConfig...
		ModelServiceFactory.get().loadModelMDRInfo(CMDBSession.get().getToken(), item.getParams(), new CMDBAsyncCallback<BaseModel>() {
			public void onSuccess(BaseModel result) {
				mdrRepository = result.get("mdr");
				mdrConfig = result.get("mdrConfig");
				//mdrConfigBase = mdrConfig.copy();
				init();
			}
		});
	}
	
	protected void init() {
		setLayout(new RowLayout());
		ContentFolder root = new ContentFolder();
		root.setPath((String)item.getParams().get("modelRoot"));
	
		final ContentSelectorWidget browser = new ContentSelectorWidget(root);
		/*
		ValueListModel contentFiles = (ValueListModel) mdrConfig.getValue("modelFiles");
		if (contentFiles != null) {
			List<ContentData> selected = new ArrayList<ContentData>();
			for (ValueModel vModel : contentFiles.getValues()) {
				if (vModel.getValue() == null) {
					continue;
				}
				ContentFile data = new ContentFile();
				data.setPath(vModel.getValue());
				selected.add(data);
			}
			
			//browser.setSelected(selected);
		}
		*/
		
		browser.setSelected(new ArrayList<ContentData>());
		
		/*
		final CompareGridWidget compare = new CompareGridWidget();
		compare.setRejectEnabled(false);
		compare.setDeleteEnabled(false);
		*/
		
		ContentPanel browserCp = new ContentPanel();
		browserCp.setHeading("Select Models to import");
		browserCp.setLayoutOnChange(true);
		browserCp.setLayout(new FitLayout());
		browserCp.setScrollMode(Scroll.AUTO);
		
		browserCp.add(browser);
		
		ContentPanel storedModels = new ContentPanel();
		storedModels.setHeading("Imported Models");
		storedTree = new ContentSelectorWidget(root);
		
		
		//storeList = new DataList();
		//storeList.setScrollMode(Scroll.AUTO);
		//storeList.setSelectionMode(SelectionMode.MULTI);
		storedModels.setLayout(new FitLayout());
		storedModels.add(storedTree);
		
		updateStoredDataList();
		
		Menu undoMenu = new Menu();
		MenuItem undoImport = new MenuItem("Remove", "reject-icon");
		undoImport.setToolTip("Remove the selected imported Model File from the CMDB");
		undoImport.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				List<TreeItem> items = storedTree.getTree().getSelectedItems();
				if (items.size() == 0) {
					return;
				}
				List<String> result = getCurrentModels();
				for (TreeItem item : items) {
					if (!(item.getModel() instanceof ContentFile)) {
						continue;
					}
					ContentData data = (ContentData)item.getModel();
					result.remove(data.getPath());
				}
				openCompareWindow(result, true);
			}
		});
		undoMenu.add(undoImport);
		
		storedTree.setContextMenu(undoMenu);
		
		storedModels.getHeader().addTool(new ToolButton("x-tool-plus", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				storedTree.expandAll();
			}
		}));
		storedModels.getHeader().addTool(new ToolButton("x-tool-minus", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				storedTree.collapseAll();
			}
		}));
	
		browserCp.getHeader().addTool(new ToolButton("x-tool-plus", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				browser.expandAll();
			}
		}));
		browserCp.getHeader().addTool(new ToolButton("x-tool-minus", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				browser.collapseAll();
			}
		}));
		
		/*	
		ContentPanel compareCp = new ContentPanel();
		compareCp.setHeading("Status of selected models");
		compareCp.setScrollMode(Scroll.AUTO);
		compareCp.setLayout(new FitLayout());
		
		compareCp.add(compare);
		
		*/
		/*
		BorderLayoutData west = new BorderLayoutData(LayoutRegion.WEST, 300);
		west.setSplit(true);
		west.setCollapsible(true);
		west.setMargins(new Margins(5));
		*/
		/*
		TextToolItem cancel = new TextToolItem("Close", "cancel-icon");
		cancel.setToolTip("Close this window");
		cancel.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (getParent() instanceof Window) {
					((Window)getParent()).close();
				}
			}
			
		});
		*/
		ToolBar deleteBar = new ToolBar();
		TextToolItem deleteTool = new TextToolItem("Remove", "delete-icon");
		deleteTool.setToolTip("Remove the selected imported Model File from the CMDB");

		deleteTool.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				List<TreeItem> items = storedTree.getTree().getSelectedItems();
				if (items.size() == 0) {
					return;
				}
				List<String> result = getCurrentModels();
				for (TreeItem item : items) {
					if (!(item.getModel() instanceof ContentFile)) {
						continue;
					}
					ContentData data = (ContentData)item.getModel();
					result.remove(data.getPath());
				}
				openCompareWindow(result, true);
			}
		});
		deleteBar.add(new FillToolItem());
		deleteBar.add(deleteTool);
		deleteBar.add(new SeparatorToolItem());
		deleteBar.add(new CloseTextToolItem(this));
		ToolBar bar = new ToolBar();
		
		TextToolItem openTool = new TextToolItem("Open", "compare-icon");
		openTool.setToolTip("Open and verify the selected model files");
		openTool.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				List<ContentData> items = browser.getSelected();
				/*
				if (items.size() == 0) {
					MessageBox.info("Info", "Nothing selected", null);
					return;
				}
				*/
				List<String> result = new ArrayList<String>();
				for (ContentData item : items) {
					if (item instanceof ContentFile) {
						result.add(item.getPath());
					}
				}
				// Compare if we have them already.
				ValueModel files = mdrConfig.getValue("modelFiles");
				
				if (files instanceof ValueListModel) {
					for (ValueModel v : ((ValueListModel)files).getValues()) {
						if (v.getValue() == null || v.getValue().length() == 0) {
							continue;
						}
						if (!result.contains(v.getValue())) {
							result.add(v.getValue());
						}
					}
				}
				
				openCompareWindow(result, false);
				
					
				//compare.setModels(mdrRepository, mdrConfig, history);
			}
		 });

		
		
		//browserCp.getHeader().addTool(compareTool);
		bar.add(new FillToolItem());
		bar.add(openTool);
		bar.add(new SeparatorToolItem());
		//bar.add(cancel);
		bar.add(new FillToolItem());
		
		StringBuffer b = new StringBuffer();
		b.append("<p>");
		
		
		

		b.append("<b>Info</b></br>Select Model Files that shall be imported to the CMDB.<br/>");
		b.append("The selected Model Files will be compared to the current Models in the CMDB before the new Models are committed to the CMDB<br/>");
		b.append("Warning: If you remove an imported Model, all instances derived from Templates in this Model will also be removed (deleted).<br/>");
		b.append("</p>");
		b.append("<p>");
		b.append("<i>Note: All imported models will be checked for changes</i><br>");
		b.append("</p>");
		HTML html = new HTML(b.toString());
		html.setStyleName("property-panel-background");
		add(html, new RowData(1,-1));
		ContentPanel center = new ContentPanel();
		center.setLayout(new BorderLayout());
		center.setHeaderVisible(false);
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	   
	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 450);  
	    eastData.setSplit(true);  
	    eastData.setCollapsible(false);  
	    eastData.setMargins(new Margins(5));  
	
	    
	    //center.setBottomComponent(bar);
	    
	    browserCp.setBottomComponent(bar);
	    storedModels.setBottomComponent(deleteBar);
	    
	    center.add(browserCp, centerData);
	    center.add(storedModels, eastData);
	    
		add(center, new RowData(1,1));
		//add(compareCp, new BorderLayoutData(LayoutRegion.CENTER));
		layout();
	}

	
	private void updateStoredDataList() {
		
		Set<ContentData> includeSet = new HashSet<ContentData>();
		for (String file : getCurrentModels()) {
			ContentData data = new ContentData();
			data.setPath(file);
			includeSet.add(data);
			appendParent(data.getParent(), includeSet);
		}
		
		storedTree.setIncludeSet(includeSet);
		storedTree.reload();
	}


	protected List<String> getCurrentModels() {
		//storeList.removeAll();
		ValueModel files = mdrConfig.getValue("modelFiles");
		
		List<String> result = new ArrayList<String>();
		if (files instanceof ValueListModel) {
			for (ValueModel v : ((ValueListModel)files).getValues()) {
				if (v.getValue() == null || v.getValue().length() == 0) {
					continue;
				}
				result.add(v.getValue());
			}
		}
		return(result);
	
	}

	
	private void appendParent(ContentData parent, Set<ContentData> includeSet) {
		if (parent == null) {
			return;
		}
		includeSet.add(parent);
		appendParent(parent.getParent(), includeSet);
	}

	protected void openCompareWindow(final List<String> result, boolean delete) {
		final CIModel history = new CIModel();
		history.setDerivedFrom(MDRHistoryState.getHistoryTemplate());
		CIModelUtils.updateModel(history, "files", result);
		CIModelUtils.updateModel(history, "mdrConfigEntry", mdrConfig.getAlias(), true);
		history.setAlias(AliasFactory.generateAlias(MDRHistoryState.getHistoryTemplate()));
		
		CompareGridWidget compare = new CompareGridWidget();
		compare.addListener(CMDBEvents.COMMIT_EVENT, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				final MessageBox info = MessageBox.wait("Update", "Updating", "Wait...");
				
				ModelServiceFactory.get().loadModelMDRInfo(CMDBSession.get().getToken(), item.getParams(), new CMDBAsyncCallback<BaseModel>() {
					@Override
					public void onFailure(Throwable t) {
						info.close();
						super.onFailure(t);
					}

					public void onSuccess(BaseModel m) {
						mdrRepository = m.get("mdr");
						mdrConfig = m.get("mdrConfig");
						
						CIModel mdrConfigBase = mdrConfig.copy();
						
						ValueListModel list = new ValueListModel();
						list.setAlias("modelFiles");
						
						for (String value : result) {
							ValueModel v = new ValueModel();
							v.setValue(value);
							v.setIsComplex(false);
							v.setAlias("modelFiles");
							list.addValue(v);
						}
						
						mdrConfig.setValue("modelFiles", list);
						List<ModelItem> local = new ArrayList<ModelItem>();
						List<ModelItem> base = new ArrayList<ModelItem>();
						local.add(mdrConfig);
						base.add(mdrConfigBase);
						// Update CMDB.
						ModelServiceFactory.get().store(getMDR(), CMDBSession.get().getToken(), local, base, new CMDBAsyncCallback<StoreResult>() {
							
							
							@Override
							public void onFailure(Throwable t) {
								info.close();
								super.onFailure(t);
							}

							@Override
							public void onSuccess(StoreResult arg0) {
								info.close();
								if (arg0.isRejected()) {
									MessageBox.alert("Error", "Can't store information about committed models, " + arg0.getRejectCause(), null);
									return;
								}
								updateStoredDataList();
							}

							
						});
						
					}
				});
				
			}
			
		});
		String title = "Compare selected models with previously imported models";
		if (delete) {
			title = "Remove previously imported models";
		}
		Window w = WindowFactory.getWindow(title, compare);
		w.show();
		
		compare.setModels(mdrRepository, mdrConfig, history);
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Open OneCMDB Models");
		desc.setDescription("Open OneCMDB model data. The model can contain template/instances/references");
		desc.addParameter("modelRoot - Path to where models are keept");
		return(desc);
	}
	

}
