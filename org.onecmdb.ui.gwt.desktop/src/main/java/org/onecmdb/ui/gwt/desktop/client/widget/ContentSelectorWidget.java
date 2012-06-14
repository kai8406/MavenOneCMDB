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
package org.onecmdb.ui.gwt.desktop.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.PreviewTableWidget;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
import com.extjs.gxt.ui.client.widget.tree.Tree.CheckNodes;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ContentSelectorWidget extends LayoutContainer {
	
	
	private Tree tree;
	private ContentData rootFolder;
	private Listener<BaseEvent> dbClick;
	private Listener<TreeEvent> selectionListener;
	private String heading;
	private List<ContentData> selected;
	private Set<ContentData> includeSet;
	private BaseTreeLoader loader;
	
	public ContentSelectorWidget() {
		this(new ContentFolder());
	}
	
	public ContentSelectorWidget(ContentData root) {
		this.rootFolder = root;
	}
	
	public ContentData getRoot() {
		return(this.rootFolder);
	}
	public Set<ContentData> getIncludeSet() {
		return includeSet;
	}

	public void setIncludeSet(Set<ContentData> includeSet) {
		this.includeSet = includeSet;
	}

	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}
	
	public void init() {
		setLayout(new FitLayout());
		setLayoutOnChange(true);
		setScrollMode(Scroll.AUTO);
		
		
		
		  RpcProxy<? extends ContentData, List<? extends ContentData>> proxy = new RpcProxy<ContentData, List<? extends ContentData>>() {

			@Override 
			protected void load(ContentData loadConfig, AsyncCallback<List<? extends ContentData>> callback){
				loadConfig.getChildren(callback, includeSet);
			}
		  };
			  
			   
		  // tree loader  
		 loader = new BaseTreeLoader(proxy) {  
			  @Override  
			  public boolean hasChildren(ModelData parent) {  
				  return (parent instanceof ContentFolder);  
			  }  
		  };  

		  
		  // trees store  
		  TreeStore<? extends ContentData> store = new TreeStore<ContentData>(loader);  
		  
		  store.setStoreSorter(new StoreSorter<ContentData>() {  

			  @Override  
			  public int compare(Store store, ContentData m1, ContentData m2, String property) {  
				  boolean m1Folder = m1 instanceof ContentFolder;  
				  boolean m2Folder = m2 instanceof ContentFolder;  

				  if (m1Folder && !m2Folder) {  
					  return -1;  
				  } else if (!m1Folder && m2Folder) {  
					  return 1;  
				  }  

				  return super.compare(store, m1, m2, property);  
			  }  
		  });  

		 tree = new Tree();  
		 tree.setSelectionMode(SelectionMode.MULTI);
		 
		 if (selected != null) {
			 tree.setCheckable(true);
			 tree.setCheckNodes(CheckNodes.LEAF);
			 tree.setCheckStyle(CheckCascade.NONE);
			 tree.addListener(Events.Add, new Listener<TreeEvent>() {

				 public void handleEvent(TreeEvent be) {
					 TreeItem item = be.item;
					 if (item.getModel() instanceof ContentData) {
						 ContentData data = (ContentData) item.getModel();
						 for (ContentData sel : selected) {
							 if (sel == null) {
								 continue;
							 }
							 if (sel.getPath().equals(data.getPath())) {
								 item.setChecked(true);
							 }
						 }
					 }
				 }
			 });
			 tree.addListener(Events.CheckChange, new Listener<TreeEvent>() {

				 public void handleEvent(TreeEvent be) {
					 TreeItem item = be.item;
					 if (item == null) {
						 return;
					 }
					 ContentData data = (ContentData)item.getModel();
					 if (data instanceof ContentFile) {
						 if (item.isChecked()) {
							 // Add to list...
							 if (!selected.contains(data)) {
								 selected.add(data);
							 }
						 } else {
							 // Remove from list.
							 selected.remove((ContentData)item.getModel());
						 }
					}
				 }
			 });
		 }
		 if (getContextMenu() != null) {
			 tree.setContextMenu(getContextMenu());
		 }
		 if (getDbClickListener() != null) {
			 tree.addListener(Events.OnDoubleClick, getDbClickListener());
		 }
		 if (getSelectionListener() != null) {
			 tree.addListener(Events.SelectionChange, getSelectionListener());
		 } else {
			 
			 tree.addListener(Events.SelectionChange, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					List<ContentData> list = new ArrayList<ContentData>();
					
					for (TreeItem item : tree.getSelectedItems()) {
						if (item.getModel() instanceof ContentData) {
							list.add((ContentData)item.getModel());
						}
					}
					setSelected(list);
				}
				 
			 });
		 }
		 
		 TreeBinder<? extends ContentData> binder = new TreeBinder<ContentData>(tree, store); 
		 //binder.setAutoLoad(true);
		 binder.setCaching(false);
		 
		 binder.setDisplayProperty("name");  
		 binder.setIconProvider(new ModelStringProvider<ContentData>() {  

				public String getStringValue(ContentData model, String property) {  
					String name = model.getName();
					   if (name == null) {
						   return(null);
					   }
					   
					   if (name.endsWith(".gif")) {
						   return(CMDBSession.get().getContentRepositoryURL() + "/" + model.getPath());
					   }
					   if (name.endsWith(".gif")) {
						   return(CMDBSession.get().getContentRepositoryURL() + "/" + model.getPath());
					   }
					   if (name.endsWith(".png")) {
						   return(CMDBSession.get().getContentRepositoryURL() + "/" + model.getPath());
					   }
					   if (model instanceof ContentFile) {
						   return(GWT.getModuleBaseURL() + "/images/file_obj.gif");
					   }
					   return(null);
				}  
			});  
		 	
		 loader.load(this.rootFolder);
		
		 ContentPanel cp = new ContentPanel();
		 cp.setLayout(new FitLayout());
		 cp.setScrollMode(Scroll.AUTO);
		 //cp.setHeaderVisible(false);
		 cp.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					reload();
				}
				
			}));
		 cp.add(tree);
		 add(cp);
		 
		 layout();
		 
		}

	
	private Listener<TreeEvent> getSelectionListener() {
		return(this.selectionListener);
	}
	
	public void setSelectionListener(Listener<TreeEvent> listener) {
		this.selectionListener = listener;
	}

	private Listener<BaseEvent> getDbClickListener() {
		return(this.dbClick);
	}
	
	public void setDbClickListener(Listener<BaseEvent> listener) {
		this.dbClick = listener;
	}
	
	public void setContextMenu(Menu context) {
		super.setContextMenu(context);
	}

	
	/*
	public List<TreeItem> getChecked() {
		if (tree == null) {
			return(new ArrayList<TreeItem>());
		}
		return(tree.getChecked());
	}
	*/
	
	public List<ContentData> getSelected() {
		return(this.selected);
	}
	
	public void setSelected(List<ContentData> selected) {
		this.selected = new ArrayList<ContentData>(selected);
	}

	public void expandAll() {
		tree.expandAll();
	}
	
	public void collapseAll() {
		tree.collapseAll();
	}
	
	public Tree getTree() {
		return(tree);
	}

	public BaseTreeLoader getLoader() {
		return(loader);
	}
	
	public void reload() {
		if (getLoader() != null) {
			// reload all Folders.
			getLoader().load(rootFolder);
		}
	}
	
	
}
