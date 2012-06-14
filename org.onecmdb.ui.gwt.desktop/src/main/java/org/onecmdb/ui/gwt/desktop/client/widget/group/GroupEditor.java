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

import java.util.List;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class GroupEditor extends LayoutContainer {

	private GroupDescription description;

	public GroupEditor(GroupDescription desc) {
		this.description = desc;
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	
		initUI();
	}



	public void initUI() {
		setLayout(new BorderLayout());
		
		// ContentPanels
		final ContentPanel center = new ContentPanel();
		center.setLayout(new FitLayout());
		ContentPanel west = new ContentPanel();
		
		
		// Create a tree.
		TreeStore<BaseModel> store = new TreeStore<BaseModel>();
		Tree tree = new Tree();
		TreeBinder binder = new TreeBinder(tree, store);
		binder.setDisplayProperty("name");
		
		tree.addListener(Events.SelectionChange, new Listener<TreeEvent>() {
		      public void handleEvent(TreeEvent te) {
		        TreeItem item = te.tree.getSelectedItem();
		        if (item != null) {
		          BaseModel model = (BaseModel) item.getModel();
		          Object obj = model.get("object");
		          if (obj != null) {
		        	  Widget edit = getEditor(obj);
		        	  center.removeAll();
		        	  center.add(edit);
		        	  center.layout();
		          }
		        }
		      }
		    });
		
		west.add(tree);
		
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
		
		add(west, westData);
		add(center, centerData);
		
		layout();
	
		populateTree(store);

	}



	protected Widget getEditor(Object obj) {
		TextArea area = new TextArea();
		area.setValue(obj.toString());
		return(area);
	}



	public void populateTree(TreeStore<BaseModel> store, BaseModel parent, BaseModel child) {
		
		if (parent == null) {
			store.add(child, false);
		} else {
			store.add(parent, child, false);
		}
		BaseModel model = child.get("model");
		for (String name : model.getPropertyNames()) {
			Object value = model.get(name);
			populateItem(store, child, name, value);
		}
	}
	public void populateItem(TreeStore<BaseModel> store, BaseModel parent, String tag, Object value) {
		if (value instanceof List) {
			BaseModel list = new BaseModel();
			list.set("name", tag);
			store.add(parent, list, false);
			for (Object o : (List)value) {
				populateItem(store, list, tag, o);
			}
			return;
		}
		if (value instanceof BaseModel) {
			BaseModel child = new BaseModel();
			child.set("name", tag);
			child.set("model", value);
			populateTree(store, parent, child);
		} else {
			BaseModel leaf = new BaseModel();
			leaf.set("name", tag);
			leaf.set("object", value);
			store.add(parent, leaf, false);
		}

	}
	
	private void populateTree(TreeStore<BaseModel> store) {
		
		if (true) {
			BaseModel root = new BaseModel();
			root.set("name", description.getName());
			root.set("model", description);
			populateTree(store, null, root);
			return;
		}
		
		
		BaseModel group = new BaseModel();
		group.set("name", description.getName());		
		store.add(group, false);
			
		BaseModel query = new BaseModel();
		query.set("name", "Query");
		store.add(group, query, false);
	
		BaseModel presentaion = new BaseModel();
		presentaion.set("name", "Presentation");
		store.add(group, presentaion, false);
		
		for (BaseModel view : description.getPresentations()) {
			BaseModel viewModel = new BaseModel();
			viewModel.set("name", (String)view.get("name"));
			store.add(presentaion, viewModel, false);
		}
		
		BaseModel lifecycle = new BaseModel();
		lifecycle.set("name", "LifeCycle");
		store.add(group, lifecycle, false);
		
		BaseModel create = new BaseModel();
		create.set("name", "Create");
		store.add(lifecycle, create, false);

		BaseModel delete = new BaseModel();
		delete.set("name", "Delete");
		store.add(lifecycle, delete, false);

		BaseModel imp = new BaseModel();
		imp.set("name", "Import");
		store.add(lifecycle, imp, false);
		
		BaseModel source = new BaseModel();
		source.set("name", "Source");
		store.add(imp, source, false);
		
		BaseModel transform = new BaseModel();
		transform.set("name", "Transform");
		store.add(imp, transform, false);
		
	}
}
