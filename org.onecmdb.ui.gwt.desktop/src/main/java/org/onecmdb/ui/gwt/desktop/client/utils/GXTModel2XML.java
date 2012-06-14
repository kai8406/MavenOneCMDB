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

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GXTModel2XML {
	
	public static String toXML(String rootName, BaseModel model) {
		StringBuffer b = new StringBuffer();
		b.append("<" + rootName +">");
		b.append("\n");
		b.append(toCompactXML(model, 1));
		b.append("\n");
		b.append("</" + rootName +">"); 
		
		return(b.toString());
	}
	
	
	public static String toCompactXML(BaseModel model, int level) {
		StringBuffer b = new StringBuffer();
		// Add attribute values.
		for (String key :  model.getPropertyNames()) {
			Object value = model.get(key);
			b.append(toXML(key, value, level));
		}
		return(b.toString());
	}
	
	public static String toAttributes(BaseModel model) {
		StringBuffer b = new StringBuffer();
		
		// Add attribute values.
		for (String key :  model.getPropertyNames()) {
			Object value = model.get(key);
			if (value instanceof List) {
				
			} else if (value instanceof BaseModel) {
				
			} else {
				b.append(" " + key + "=\"" + value + "\"");
			}
		}
		return(b.toString());
	}
	
	public static String toXML(String key, Object value, int level) {
		StringBuffer b = new StringBuffer();
		
		if (value instanceof List) {
			List values = (List)value;
			for (int i = 0; i < values.size(); i++) {
				Object v = values.get(i);
				b.append(toXML(key, v, level+1));
				if (i < (values.size()-1)) {
					b.append("\n");
				}
			}
		} else if (value instanceof BaseModel) {
			BaseModel mvalue = (BaseModel)value;
			b.append(getTab(level) + "<" + key + toAttributes(mvalue));
			String inner = toCompactXML(mvalue, level+1);
			if (inner.length() == 0) {
				b.append("/>");
			} else {
				b.append(">");
				b.append("\n");
				b.append(inner);
				b.append("\n");
				b.append(getTab(level) + "</" + key + ">");
			}
		} else {
			b.append(getTab(level) + "<" + key + ">" + value + "</" + key + ">");
			b.append("\n");
		}
		return(b.toString());
	}
	
	public static String toXML(BaseModel model, int level) {
		StringBuffer b = new StringBuffer();
		for (String key :  model.getPropertyNames()) {
			Object value = model.get(key);
			
			if (value instanceof List) {
				List values = (List)value;
				for (int i = 0; i < values.size(); i++) {
					Object v = values.get(0);
					b.append(getTab(level) + "<" + key + ">");					
					if (v instanceof BaseModel) {
						b.append("\n");
						b.append(getTab(level+1) + toXML((BaseModel)v, level+2));
						b.append("\n");
					} else {
						b.append(model.get(key));
					}
					b.append(getTab(level) + "</" + key + ">");
					b.append("\n");
				}
				b.append("\n");
			} else {
				b.append(getTab(level) + "<" + key + ">");
				if (value instanceof BaseModel) {
					b.append("\n");
					b.append(getTab(level+1) + toXML((BaseModel)value, level+2));
					b.append("\n");
					b.append(getTab(level+1) + "</" + key + ">");
				} else {
					b.append(model.get(key));
					b.append("</" + key + ">");
				}
				
				b.append("\n");
			}
		}
		return(b.toString());
	}

	private static String getTab(int level) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < level; i++) {
			b.append("  ");
		}
		return(b.toString());
	}
}
