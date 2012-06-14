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
package org.onecmdb.ui.gwt.desktop.client.window;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class WidgetDescription extends BaseModel {
	/*
	private String id;
	private String name;
	private String description;
	private String parameters;
	*/
	
	public String getId() {
		return(get("id"));
	}
	
	public void setId(String id) {
		set("id", id);
	}
	
	public String getName() {
		return(get("name"));
	}
	
	public void setName(String name) {
		set("name", name);
	}
	public String getDescription() {
		return(get("description"));
	}
	public void setDescription(String description) {
		set("description", description);
	}
	
	public String getParameters() {
		return(get("parameters"));
	}
	
	public void setParameters(String parameters) {
		set("parameters", parameters);
	}
	
	public void addParameterEntry(String key, String defaultValue, boolean list, String desc) {
		WidgetParameterEntry entry = new WidgetParameterEntry(key, defaultValue, list, desc);
		addParameterEntry(entry);
	}
	
	public void addParameterEntry(WidgetParameterEntry entry) {
		
		List<WidgetParameterEntry> entries = get("parameterEntry");
		if (entries == null) {
			entries = new ArrayList<WidgetParameterEntry>();
			set("parameterEntry", entries);
		}
		entries.add(entry);
	}
	
	public List<WidgetParameterEntry> getParameterEntries() {
		List<WidgetParameterEntry> entries = get("parameterEntry");
		if (entries == null) {
			return(new ArrayList<WidgetParameterEntry>());
		}
		return(entries);
	}

	public void addParameter(String string) {
		String param = getParameters();
		if (param == null) {
			param = "";
		}
		param += " " + string;
		setParameters(param);
	}
	
	
	public void setFactoryName(String name) {
		set("factoryName", name);
	}

	public String getFactoryName() {
		return(get("factoryName"));
	}
}
