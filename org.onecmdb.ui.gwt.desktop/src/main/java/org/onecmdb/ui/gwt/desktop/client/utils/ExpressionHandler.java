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
package org.onecmdb.ui.gwt.desktop.client.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.core.client.GWT;

public class ExpressionHandler {

	
	public static String replaceURL(String value) {
		if (value == null) {
			return("");
		}
		String vTemp = value;
		
		value = value.replace("baseURL:", GWT.getModuleBaseURL());
		value = value.replace("token:", CMDBSession.get().getToken());
		value = value.replace("rootURL:", getRootURL(GWT.getModuleBaseURL()));
		value = value.replace("contentURL:", CMDBSession.get().getContentRepositoryURL());
		value = value.replace("exportURL:", CMDBSession.get().getExportURL());
		return(value); 
	}
	
	public static String replace(String value) {
		if (value == null) {
			return("");
		}
		String vTemp = value;
		
		value = value.replace("${baseURL}", GWT.getModuleBaseURL());
		value = value.replace("${rootURL}", getRootURL(GWT.getModuleBaseURL()));
		value = value.replace("${token}", CMDBSession.get().getToken());
		value = value.replace("${contentURL}", CMDBSession.get().getContentRepositoryURL());
		value = value.replace("${exportURL}", CMDBSession.get().getExportURL());
		
		
		return(value); 
	}

	private static String rootURL;

	private static String getRootURL(String moduleBaseURL) {
		if (rootURL != null) {
			return(rootURL);
		}
		int index = 0;
		String str = moduleBaseURL;
		for (int i = 0; i < 2; i++) {
			int idx = str.indexOf("/");
			if (idx >= 0) {
				index += idx+1;
				str = str.substring(index+1);
			} else {
				index = -1;
				break;
			}
		}
		if (index < 0) {
			index = moduleBaseURL.indexOf(GWT.getModuleName());
		}
		if (index > 0) {
			moduleBaseURL = moduleBaseURL.substring(0, index);
		}
		rootURL  = moduleBaseURL;
		return(moduleBaseURL);
	}

	
	public static BaseModel replace(BaseModel m, Map<String, List<String>> map) {
		if (map == null || m == null) {
			return(m);
		}
		for (String key : m.getPropertyNames()) {
			Object value = m.get(key);
			if (value instanceof String) {
				String replacedValue = replace((String)value, map);
				m.set(key, replacedValue);
			}
			if (value instanceof BaseModel) {
				replace((BaseModel)value, map);
			}
		}
		
		return(m);
	}


	/**
	 * Replace {nnn} with value in map where key == nnn
	 * @param value
	 * @param map
	 * @return
	 */
	private static String replace(String text, Map<String, List<String>> map) {
		if (map == null) {
			return(text);
		}
		if (!text.contains("{")) {
			return(text);
		}
		String newString = text;
		for (Object key : map.keySet()) {
			List<String> values = map.get(key);
			if (values != null && values.size() > 0) {
				String value = values.get(0);
				newString = newString.replace("{" + key + "}", value);
			}
		}
		return(newString);
	}
	
}
