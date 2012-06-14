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
package org.onecmdb.ui.gwt.desktop.client.widget.group;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.CITreeModel;
import org.onecmdb.ui.gwt.desktop.client.widget.group.lifecycle.CreateGroupWidget;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GroupNavigationTree extends LayoutContainer {
	private ContentData mdr;
	private BaseTreeLoader<CITreeModel> loader;
	private SelectionChangedListener<CITreeModel> selectionChange;
	
	/**
	 * Tree  |  Property...
	 */
	public GroupNavigationTree(ContentData mdr) {
		this.mdr = mdr;
	}
	
	
	
	public SelectionChangedListener<CITreeModel> getSelectionChange() {
		return selectionChange;
	}



	public void setSelectionChange(SelectionChangedListener<CITreeModel> selectionChange) {
		this.selectionChange = selectionChange;
	}



	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}



	protected void initUI() {
		// Create Tree proxy...
		RpcProxy<CITreeModel, List<CITreeModel>> proxy = new RpcProxy<CITreeModel, List<CITreeModel>>() {
			@Override 
			protected void load(CITreeModel parent, final AsyncCallback<List<CITreeModel>> callback){
				
				if (parent == null || parent.isInGroup()) {
					// Call server-side.
					ModelServiceFactory.get().loadGroup(CMDBSession.get().getToken(), mdr, parent, new CMDBAsyncCallback<List<CITreeModel>>() {

						
						@Override
						public void onFailure(Throwable t) {
							super.onFailure(t);
							callback.onSuccess(new ArrayList<CITreeModel>());
						}

						@Override
						public void onSuccess(List<CITreeModel> arg0) {
							callback.onSuccess(arg0);
						}
						
					});
					return;
				}
				
				callback.onSuccess(parent.getChildren());
				return;
				
			}
		};
			  
			   
		// tree loader  
		loader = new BaseTreeLoader<CITreeModel>(proxy) {  
			@Override  
			public boolean hasChildren(CITreeModel parent) {  
				if (parent == null) {
					return(true);
				}
				if (parent.isFolder()) {
					return(true);
				}
				return (false);  
			}
			
		};  

		  
		// Create tree data store.
	    TreeStore<CIModel> store = new TreeStore<CIModel>(loader);  
		  
		
		// Create tree
		Tree tree = new Tree();  
		tree.setSelectionMode(SelectionMode.MULTI);
		 
		
	
		// Create binder
		TreeBinder<CITreeModel> binder = new TreeBinder<CITreeModel>(tree, store); 
		binder.setAutoLoad(true);
		binder.setCaching(false);

		tree.setContextMenu(getGroupContextMenu(tree));
		if (selectionChange != null) {
			binder.addSelectionChangedListener(selectionChange);
		}
		
		binder.setDisplayProperty(CITreeModel.NAME);  
		binder.setIconProvider(new ModelStringProvider<CITreeModel>() {  

			public String getStringValue(CITreeModel model, String property) {  
				
				String path = model.get(CITreeModel.ICON_PATH);
				return(path);
			}  
		});  
		
		loader.load(null);
		
		add(tree);
		layout(); 
		 
	}


	private Menu getGroupContextMenu(final Tree tree) {
		final Menu menu = new Menu();
		
		MenuItem edit = new MenuItem("Edit", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				CITreeModel model = (CITreeModel) tree.getSelectedItem().getModel();
				GroupDescription desc = model.get(CITreeModel.GROUP_DESCRIPTION);
				if (desc != null) {
					// OPen Dialog...
					Dialog d = new Dialog();
					d.setLayout(new FitLayout());
					d.add(new GroupEditor(desc));
					d.setSize(600, 400);
					d.show();
				}
			}
			
		});
		menu.add(edit);


		menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				CITreeModel model = (CITreeModel) tree.getSelectedItem().getModel();
				final GroupDescription desc = model.get(CITreeModel.GROUP_DESCRIPTION);
				if (desc != null) {
					List<BaseModel> create = desc.getCreates();
					if (create == null || create.size() == 0) {
						return;
					}
					MenuItem mCreate = new MenuItem("Create");
					Menu subMenu = new Menu();
					mCreate.setSubMenu(subMenu);
					for (BaseModel m : create) {
						final MenuItem item = new MenuItem((String)m.get("name"));
						item.setData("model", m);
						item.addSelectionListener(new SelectionListener<ComponentEvent>() {

							@Override
							public void componentSelected(ComponentEvent ce) {
								Dialog d = new Dialog();
								d.setLayout(new FitLayout());
								d.add(new CreateGroupWidget(desc, (BaseModel)item.getData("model")));
								d.setSize(600, 400);
								d.show();
							}
							
						});
						subMenu.add(item);
					}
					menu.add(mCreate);
				}
				
			}
			
		});
		return(menu);
	}

	

	public void setPermission(CMDBPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

}
