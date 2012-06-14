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
package org.onecmdb.ui.gwt.desktop.client.service.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class SaveItem extends ModelItem {

	public boolean saveInstances() {
		return(getBoolean("saveInstances", false));
	}
	
	private boolean getBoolean(String prop, boolean defaultValue) {
		Object o = get(prop);
		if (o == null) {
			return(defaultValue);
		}
		if (o instanceof Boolean) {
			return((Boolean)o);
		}
		if (o instanceof String) {
			return(Boolean.parseBoolean((String)o));
		}
		return(defaultValue);
	}

	public void setSaveInstances(boolean save) {
		set("saveInstances", "" + save);
	}

	public boolean saveTemplates() {
		return(getBoolean("saveTemplates", true));
	}
	
	public void setSaveTemplates(boolean save) {
		set("saveTemplates", "" + save);
	}

	public String getAlias() {
		return(get("alias"));
	}
	public void setAlias(String alias) {
		set("alias", alias);
	}

	public void setAllChildren(boolean save) {
		set("allChildren", "" + save);
	}
	
	public boolean isAllChildren() {
		return(getBoolean("allChildren", true));
	}

	@Override
	public ModelItem copy() {
		SaveItem copy = new SaveItem();
		super.copy(copy);
		return(copy);
	}

	public void setCI(CIModel ci) {
		set("ci", ci);
	}
	
	public CIModel getCI() {
		return(get("ci"));
	}
	
	
}
