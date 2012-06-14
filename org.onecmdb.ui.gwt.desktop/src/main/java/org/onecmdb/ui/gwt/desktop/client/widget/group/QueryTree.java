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
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.CITreeModel;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QueryTree extends LayoutContainer {
	private ContentData mdr;
	private BaseTreeLoader<CITreeModel> loader;
	private SelectionChangedListener<CITreeModel> selectionChange;
	
	/**
	 * Tree  |  Property...
	 */
	public QueryTree(ContentData mdr) {
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

		if (selectionChange != null) {
			binder.addSelectionChangedListener(selectionChange);
		}
		
		binder.setDisplayProperty(CITreeModel.NAME);  
		binder.setIconProvider(new ModelStringProvider<CITreeModel>() {  

			public String getStringValue(CITreeModel model, String property) {  
				return(null);
				/*
				String path = model.get(CITreeModel.ICON_PATH);
				return(path);
				*/
			}  
		});  
		
		loader.load(null);
		
		add(tree);
		layout(); 
		 
	}


	public void setPermission(CMDBPermissions permissions) {
		// TODO Auto-generated method stub
		
	}

}
