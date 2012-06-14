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
package org.onecmdb.core.utils.xpath.model;

import org.apache.commons.jxpath.DynamicPropertyHandler;

/**
 * Wrapper class fro DynamicPropertyHandler to IDynamicHandler.
 *
 */
public class OneCMDBContextHandler implements DynamicPropertyHandler {

	public Object getProperty(Object object, String propertyName) {
		if (object instanceof IDynamicHandler) {
			Object o = ((IDynamicHandler)object).getProperty(propertyName);
			return(o);
		}
		return null;
	}

	public String[] getPropertyNames(Object object) {
		if (object instanceof IDynamicHandler) {
			String p[] = ((IDynamicHandler)object).getProperties();
			// Avoid null pointer.
			if (p == null) {
				p = new String[0];
			}
			return(p);
		}
		return(new String[0]);
	}

	public void setProperty(Object object, String propertyName, Object value) {
		if (object instanceof IDynamicHandler) {
			((IDynamicHandler)object).setProperty(propertyName, value);
		}
	}
	
}
