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

import com.extjs.gxt.ui.client.data.BaseModel;

public class BaseModelInspection {
	public static String toString(int n, BaseModel m) {
		StringBuffer buf = new StringBuffer();
		buf.append("BaseModel {\n");
		for (String key : m.getPropertyNames()) {
			Object value = m.get(key);
			if (value instanceof BaseModel) {
				buf.append(getTab(n) + key + "=" + toString(n+1, (BaseModel)value));
			} else {
				buf.append(getTab(n) + key +"=" + value);
			}
			buf.append("\n");
		}
		buf.append(getTab(n) + "}" + "\n");
		return(buf.toString());
	}
	
	public static String getTab(int n) {
		String tab = "";
		for (int i = 0; i < n; i++) {
			tab += tab + "  ";
		}
		return(tab);
	}
}
