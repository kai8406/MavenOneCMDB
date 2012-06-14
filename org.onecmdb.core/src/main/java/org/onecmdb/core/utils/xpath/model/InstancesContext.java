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
import org.onecmdb.core.internal.model.Path;

/**
 * Dynamic wrapper for all instances.  
 * <br> 
 * <br>Path /instances 
 *
 */
public class InstancesContext extends AbstractCacheContext {
	
	private ISession session;
	private HashMap<String, ICi> ciMap = new HashMap<String, ICi>();
	private boolean collection;
	
	public InstancesContext(Map<String, Object> context, ISession session, boolean collection) {
		super(context);
		this.session = session;
		this.collection = collection;
	}
	
	public String[] getNewProperties() {
			
		IModelService mService = (IModelService)session.getService(IModelService.class);
		Set<ICi> cis = mService.getAllTemplates(null);
		for (ICi ci : cis) {
			ciMap.put(ci.getAlias(), ci);
		}
		Set<String> properties = ciMap.keySet();
		return(properties.toArray(new String[0]));
	}

	public Object getNewProperty(String propertyName) {
		ICi ci = ciMap.get(propertyName);
		if (ci == null) {
			IModelService mService = (IModelService)session.getService(IModelService.class);
			ci = mService.findCi(new Path<String>(propertyName));
			if (ci == null) {
				return(null);
			}
		}
		
		if (this.collection) {
			long start = System.currentTimeMillis();
			Set<ICi> offsprings = ci.getOffsprings();
			List<InstanceContext> values = new ArrayList<InstanceContext>();
			for (ICi offspring : offsprings) {
				if (!offspring.isBlueprint()) {
					values.add(new InstanceContext(this.context, offspring));
				}
			}
			log.debug("Get Instances of " + ci.getAlias() +":" + (System.currentTimeMillis() - start) + "ms");
			return(values);
		}
		return(new InstanceCollectionContext(this.context, ci));
	}

	public void setProperty(String propertyName, Object value) {
		System.out.println("SetValue(" + propertyName +"," + value +")");
		throw new IllegalArgumentException("Can't set value on this.");
	}

}
