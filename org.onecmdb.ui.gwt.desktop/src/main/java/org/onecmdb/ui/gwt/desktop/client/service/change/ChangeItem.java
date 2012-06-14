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
package org.onecmdb.ui.gwt.desktop.client.service.change;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;

public class ChangeItem extends BaseModel {
	/*
	public static final String STATUS_NO_TEMPLATE = "Template Missing";
	public static final String STATUS_NO_ATTRIBUTE = "Attribute Missing";
	*/
	public static final String STATUS_ERROR_DUPLICATED = "WARNING: duplicated";
	public static final String STATUS_ERROR_MISSING_TEMPLATE = "ERROR: template missing";
	public static final String STATUS_ERROR_MISSING_INSTANCE = "ERROR: instance missing";
	public static final String STATUS_ERROR_PARENT_MISSMATCH = "ERROR: Derived from missmatched";
	public static final String STATUS_ERROR_MISSING_ATTRIBUTE = "ERROR: Attribute is not defined";
	
	public static final String STATUS_DELETE = "Delete";
	public static final String STATUS_NEW = "New";
	public static final String STATUS_MODIFIED = "Modify";
	public static final String STATUS_EQUALS = "Equals";
	private static final String STATUS = "status";
	private List<ChangeItem> children;
	private ChangeItem parent;
	
	

	@Override
	public <X> X get(String property) {
		//System.out.println("CHANGE_ITEM:GET - " + property + "=" + super.get(property));
		return (X)super.get(property);
		
	}
	
	public void setStatus(String status) {
		set(STATUS, status);
	}
	
	public String getStatus() {
		return((String)get(STATUS));
	}
	
	public void addChild(ChangeItem item) {
		item.setParent(this);
		if (children == null) {
			children = new ArrayList<ChangeItem>();
			set("children", children);
		}
		children.add(item);
	}

	private void setParent(ChangeItem parent) {
		this.parent = parent;
	}

	public List<? extends ChangeItem> getChildren() {
		if (children == null) {
			return(new ArrayList<ChangeItem>());
		}
		return(children);
	}
	
	public String toString() {
		return("ChangeItem");
	}
}
