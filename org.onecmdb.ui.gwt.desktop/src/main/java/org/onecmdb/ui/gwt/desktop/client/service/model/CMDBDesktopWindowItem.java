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

import org.onecmdb.ui.gwt.desktop.client.utils.ExpressionHandler;

import com.extjs.gxt.ui.client.data.BaseModel;

public class CMDBDesktopWindowItem extends BaseModel {

	private BaseModel serilizableBaseModel;
	
	public String getIconStyle() {
		return(get("iconStyle"));
	}

	public String getHeading() {
		return(get("heading", ""));
	}
	public void setHeading(String heading) {
		set("heading", heading);
	}
	
	public boolean hasWidth() {
		return(get("width") != null);
	}
	public boolean hasHeight() {
		return(get("height") != null);
	}
	
	public void setWidth(String w) {
		set("width", w);
	}
	
	public String getWidth() {
		return(get("width", "800px"));
	}

	public String getHeight() {
		return(get("height", "600px"));
	}

	public String getX() {
		return(get("x"));
	}
	public String getY() {
		return(get("y"));
	}
	
	public boolean isOpenAtStartup() {
		String value = get("openAtStartup");
		if (value == null) {
			return(false);
		}
		return("true".equals(value));
		
	}


	public boolean isSingleton() {
		String s = get("singleton");
		if (s == null) {
			return(false);
		}
		return(Boolean.parseBoolean(s));
	}

	public void setParams(BaseModel params) {
		set("params", params);
	}
	
	public BaseModel getParams() {
		Object o = get("params");
		if (o instanceof BaseModel) {
			return(ExpressionHandler.replace((BaseModel)o, CMDBSession.get().getURLValues()));
		}
		// Always return an object, to avoid null-pointer's 
		BaseModel params = new BaseModel();
		set("params", params);
		return(params);
	}

	public String getID() {
		return(get("id"));
	}
	
	public void setID(String id) {
		set("id", id);
	}

	public String getHelp() {
		return(get("help"));
	}
	public void setHelp(String url) {
		set("help", url);
	}

	public boolean isMinimizable() {
		String s = get("minimizable");
		if (s == null) {
			return(true);
		}
		return(Boolean.parseBoolean(s));
	}

	public boolean isMaximizable() {
		String s = get("maximizable");
		if (s == null) {
			return(true);
		}
		return(Boolean.parseBoolean(s));
	}
	
}
