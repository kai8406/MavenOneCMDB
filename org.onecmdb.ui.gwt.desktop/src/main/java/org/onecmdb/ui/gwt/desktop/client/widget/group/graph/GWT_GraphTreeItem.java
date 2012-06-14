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

public class GWT_GraphTreeItem extends GWT_TreeModel {

	private static final String SELECTOR_ID = "id";
	private static final String QUERY = "query";

	@Override
	public ModelItem copy() {
		return(null);
	}

	public void setSelector(GWT_ItemSelector current) {
		set(SELECTOR_ID, current.getId());
	}

	public String getPath() {
		if (getParent() == null) {
			return("/" + getItemSelector().getId());
		}
		GWT_GraphTreeItem item = (GWT_GraphTreeItem) getParent();
		return(item.getPath() + "/" + getItemSelector().getId());
	}
	public boolean hasParentSelector(GWT_ItemSelector current) {
		if (this.getItemSelector().getId().equals(current.getId())) {
			return(true);
		}
		if (getParent() == null) {
			return(false);
		}
		return(((GWT_GraphTreeItem)getParent()).hasParentSelector(current));
	}

	public GWT_ItemSelector getItemSelector() {
		String id = get(SELECTOR_ID);
		GWT_ItemSelector selector = getGraphQuery().getSelectors().get(id);
		return(selector);
	}

	private GWT_GraphQuery getGraphQuery() {
		GWT_GraphQuery query = get(QUERY);
		if (query != null) {
			return(query);
		}
		
		if (getParent() == null) {
			return(null);
		}
		return(((GWT_GraphTreeItem)getParent()).getGraphQuery());
	}

	
	public void setGraphQuery(GWT_GraphQuery query) {
		set(QUERY, query);
	}
	
	public String toString() {
		return("TreeItem {" + getPath() +  "} [" + getItemSelector().toString() + "]");
	}

}
