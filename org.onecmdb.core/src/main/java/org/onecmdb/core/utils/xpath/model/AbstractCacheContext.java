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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class to all dynamic handlers.
 * <br>
 * Caches all object from real implementations.
 */
public abstract class AbstractCacheContext implements IDynamicHandler {
	protected List<String> properties;
	protected HashMap<String, Object> propertyValueMap = new HashMap<String, Object>();
	protected Map<String, Object> context;
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected AbstractCacheContext(Map<String, Object> context) {
		this.context = context;
	}
	
	
	public String[] getProperties() {
		if (this.properties == null) {
			this.properties = new ArrayList<String>();
			Collections.addAll(this.properties, getNewProperties());
		}
		return(this.properties.toArray(new String[0]));
	}

	public Object getProperty(String propertyName) {
		Object result = propertyValueMap.get(propertyName);
		if (result != null)  {
			return(result);
		}
		result = getNewProperty(propertyName);
		propertyValueMap.put(propertyName, result);
		return(result);
	}

	protected void updateProperty(String name, Object property) {
		this.propertyValueMap.put(name, property);
		if (this.properties == null) {
			getProperties();
		}
		this.properties.add(name);
	}
	
	public abstract String[] getNewProperties();
	public abstract Object getNewProperty(String propertyName);
}
