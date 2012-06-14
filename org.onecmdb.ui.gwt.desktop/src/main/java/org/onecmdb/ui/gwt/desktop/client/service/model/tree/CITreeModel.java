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
package org.onecmdb.ui.gwt.desktop.client.service.model.tree;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;

public class CITreeModel extends ModelItem {

	public static final String TREEMODEL_CHILDREN = "children";
	public static final String CIMODEL = "ciModel";
	public static final String IS_FOLDER = "isFolder";
	public static final String PATH = "path";
	public static final String GROUP_DESCRIPTION = "groupDescription";
	public static final String NAME = "name";
	public static final String ICON_PATH = "icon";

	@Override
	public ModelItem copy() {
		CITreeModel model = new CITreeModel();
		return(model.copy());
	}

	public boolean hasChildren() {
		return(getChildren().size() > 0);
	}
	
	public void setCIModel(CIModel data) {
		set(CIMODEL, data);
	}
	
	public CIModel getCIModel() {
		return(get(CIMODEL));
	}



	public List<CITreeModel> getChildren() {
		return(get(TREEMODEL_CHILDREN, new ArrayList<CITreeModel>()));
	}
	

	public void setChildren(List<CITreeModel> children) {
		set(TREEMODEL_CHILDREN, children);
	}


	public void addChild(CITreeModel m) {
		List<CITreeModel> children = getChildren();
		children.add(m);
		setChildren(children);
	}

	public void setName(String name) {
		set(NAME, name);
	}
	
	public String getName() {
		return(get(NAME));
	}

	
	private boolean isGroupRoot() {
		return(getGroupDescription() != null);
	}

	public boolean isFolder() {
		return(get(IS_FOLDER, false));
	}

	public void setFolder(boolean b) {
		set(IS_FOLDER, b);
		
	}

	public void setPath(String path) {
		set(PATH, path);
	}

	public String getPath() {
		return(get(PATH));
	}

	public boolean isInGroup() {
		return(isFolder() || isGroupRoot());
	}

	public void setGroupDescription(GroupDescription group) {
		set(GROUP_DESCRIPTION, group);
		
	}
	public GroupDescription getGroupDescription() {
		return(get(GROUP_DESCRIPTION));
	}

}
