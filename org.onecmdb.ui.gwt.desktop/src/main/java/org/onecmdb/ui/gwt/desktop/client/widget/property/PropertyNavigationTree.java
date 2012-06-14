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
package org.onecmdb.ui.gwt.desktop.client.widget.property;

import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;

import prefuse.action.layout.graph.TreeLayout;

public class PropertyNavigationTree extends LayoutContainer {
	
	private TreeModel root;
	private String property;

	public PropertyNavigationTree(TreeModel root, String property) {
		this.root = root;
		this.property = property;
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	
		initUI();
	}



	public void initUI() {
		TreeStore<TreeModel> store = new TreeStore<TreeModel>();  
		store.add(root, true);  
		
		final Tree tree = new Tree();
			
		TreeBinder<TreeModel> binder = new TreeBinder<TreeModel>(tree, store);
		binder.setDisplayProperty(property);
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		cp.add(tree);
		
		add(cp);
		
		layout();
	}
	
	
}
