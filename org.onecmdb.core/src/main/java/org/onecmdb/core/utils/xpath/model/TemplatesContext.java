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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.model.Path;

/**
 * Dynamic Object Wrapper for <i>all</i> templates.
 * <br>
 * <br>Path /template
 *
 */
public class TemplatesContext extends AbstractCacheContext {

	private HashMap<String, IType> ciMap = new HashMap<String, IType>();
	private ISession session;
	private boolean collection;
	
	public TemplatesContext(Map<String, Object> context, ISession session, boolean collection) {
		super(context);
		this.session = session;
		this.collection = collection;
	}
	
	
	public String[] getNewProperties() {
		// Collection return all template
		if (this.collection) {
			return(new String[] {"template"});
		}
		
		// Specific template through it's alias.
		IModelService mService = (IModelService)session.getService(IModelService.class);
		Set<IType> types = mService.getAllComplexTypes(null);
		
		for (IType type : types) {
			ciMap.put(type.getAlias(), type);
		}
		Set<String> properties = ciMap.keySet();
		return(properties.toArray(new String[0]));
	}

	public Object getNewProperty(String propertyName) {
		// Collection return  
		if (this.collection) {
			IModelService mService = (IModelService)session.getService(IModelService.class);
			Set<IType> types = mService.getAllComplexTypes(null);
			List<TemplateContext> result = new ArrayList<TemplateContext>();
			for (IType type : types) {
				result.add(new TemplateContext(this.context, (ICi)type));
			}
			return(result);
		}
		
		// Specific type.
		IType ci = ciMap.get(propertyName);
		if (ci == null) {
			IModelService mService = (IModelService)session.getService(IModelService.class);
			ci = mService.findCi(new Path<String>(propertyName));
		}
		if (ci == null) {
			return(null);
		}
		return(new TemplateContext(this.context, (ICi)ci));
	}

	public void setProperty(String propertyName, Object value) {
		throw new IllegalArgumentException("Can't set value on Template, yet!");
	}

}
