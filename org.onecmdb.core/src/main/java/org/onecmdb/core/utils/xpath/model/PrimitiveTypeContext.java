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

import java.util.Map;

import org.onecmdb.core.IType;

/**
 * Dynamic Object wrapper for all primitive (built-in) types.
 * <br>
 * <br>Path /primitive/<i>type-alias</i> 
 *
 */
public class PrimitiveTypeContext extends AbstractCacheContext  {

	private IType type;

	public PrimitiveTypeContext(Map<String, Object> context, IType type) {
		super(context);
		this.type = type;
	}
	
	@Override
	public String[] getNewProperties() {
		// What can we do on the ICi, might just return that,
		// But we migth need more controll on how the objects
		// are passed up, manly for doing the setProperty().
		String properties[] = {
		"alias",
		"displayName",
		"icon",
		"description"
		};
		
		return(properties);
	}

	@Override
	public Object getNewProperty(String propertyName) {
		if (propertyName.equals("alias")) {
			return(this.type.getAlias());
		}
	
		if (propertyName.equals("displayName")) {
			return(this.type.getDisplayName());
		}
		
		if (propertyName.equals("icon")) {
			return(this.type.getIcon());
		}
		if (propertyName.equals("description")) {
			return(this.type.getDescription());
		}
			
		return(null);
	}

	public void setProperty(String propertyName, Object value) {		
	}

	public String toString() {
		return(this.type.getAlias());
	}

}
