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
package org.onecmdb.utils.xml;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

public class XMLUtils {
	/**
	 * XML helper functions
	 */
	public static String getElementValue(Map<String, String> attrMap, Element sel, String elementName, boolean requiered) {
		
		Element el = sel.element(elementName);
		if (el == null) {
			if (requiered) {
				throw new IllegalArgumentException("Element <" + elementName + "> is missing in <" + 
						sel.getName() + "> [" + sel.getPath() + "]");
			}
			return(null);
		}
		String text = el.getTextTrim();
		if (requiered && (text == null || text.length() == 0)) {
			throw new IllegalArgumentException("Element <" + elementName + "> has no value in <" + 
					sel.getName() + "> [" + sel.getPath() + "]");
		}
		text = subsituteAttr(attrMap, text);
		return(text);
	}
	
	public static String subsituteAttr(Map<String, String> attrMap, String text) {
		if (attrMap == null) {
			return(text);
		}
		if (!text.contains("{")) {
			return(text);
		}
		String newString = text;
		for (Object key : attrMap.keySet()) {
			String value = (String) attrMap.get(key);
			newString = newString.replace("{" + key + "}", value);
		}
		return(newString);
	}

	public static String getAttributeValue(Map<String, String> attrMap, Element sel,
			String attributeName, boolean requiered) {
		
		Attribute el = sel.attribute(attributeName);
		if (el == null) {
			if (requiered) {
				throw new IllegalArgumentException("Attribute <" + attributeName + "> is missing in <" + 
						sel.getName() + "> [" + sel.getPath() + "]");
			}
			return(null);
		}
		String text = el.getText();
		if (requiered && (text == null || text.length() == 0)) {
			throw new IllegalArgumentException("Element <" + attributeName + "> has no value in <" + 
					sel.getName() + "> [" + sel.getPath() + "]");
		}
		text = subsituteAttr(attrMap, text);
		return(text);
	}

}
