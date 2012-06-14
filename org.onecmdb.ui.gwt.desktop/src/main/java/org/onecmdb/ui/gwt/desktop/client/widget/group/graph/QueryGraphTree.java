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
package org.onecmdb.ui.gwt.desktop.client.widget.group.graph;

import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;

import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.google.gwt.user.client.Element;

public class QueryGraphTree extends LayoutContainer {
	
	
	private GWT_GraphQuery query;
	private TreeStore<ModelItem> store;
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}


	protected void initUI() {		
		setLayout(new FitLayout());
			
		store = new TreeStore<ModelItem>();
		Tree tree = new Tree();
		TreeBinder<ModelItem> binder = new TreeBinder<ModelItem>(tree, store);
		add(tree);
	}
	
	

	public void setGraphTreeItem(GWT_GraphTreeItem item) {
		store.removeAll();
		store.add(item, true);
	}
}
