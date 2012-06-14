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

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;

import com.extjs.gxt.ui.client.data.TreeModel;

public abstract class GWT_TreeModel extends ModelItem implements TreeModel {
	List<TreeModel> children = new ArrayList<TreeModel>();
	TreeModel parent = null;
	
	
	public void add(TreeModel child) {
		children.add(child);
		child.setParent(this);
	}

	public TreeModel getChild(int index) {
		if (index > (children.size()-1)) {
			return(null);
		}
		return(children.get(index));
	}

	public int getChildCount() {
		return(getChildren().size());
	}

	public List getChildren() {
		return(children);
	}

	public TreeModel getParent() {
		return(parent);
	}

	public int indexOf(TreeModel child) {
		return(children.indexOf(child));
	}

	public void insert(TreeModel child, int index) {
		children.add(index, child);
	}

	public boolean isLeaf() {
		return(getChildCount() == 0);
	}

	public void remove(TreeModel child) {
		children.remove(child);
	}

	public void removeAll() {
		children.clear();
	}

	public void setParent(TreeModel parent) {
		this.parent = parent;
	}
	
	public void printTree() {
		System.out.println(toString() + "[" + getChildCount() + "]");
		for (GWT_TreeModel child : (List<GWT_TreeModel>)getChildren()) {
			System.out.print("-->");
			child.printTree();
		}
	}

}
