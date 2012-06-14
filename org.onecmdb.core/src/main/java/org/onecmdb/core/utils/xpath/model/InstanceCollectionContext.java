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
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.internal.model.Path;

/**
 * Dynamic wrapper for all instances of an template.
 * <br>The instances are <i>direct</i> offspring of this template.
 * <br>
 * <br>Path /instance/<i>template-alias</i>  
 *
 */
public class InstanceCollectionContext extends AbstractCacheContext implements ICmdbObjectFactory {
	
	private ICi template;
	private HashMap<String, ICi> ciMap = new HashMap<String, ICi>();

	public InstanceCollectionContext(Map<String, Object> context, ICi template) {
		super(context);
		this.template = template;
	}
	
	@Override
	public String[] getNewProperties() {
		Set<ICi> cis = this.template.getOffsprings();
		List<String> aliases = new ArrayList<String>();
		for (ICi ci : cis) {
			if (!ci.isBlueprint()) {
				String alias = ci.getAlias();
				aliases.add(alias);
				ciMap.put(alias, ci);
			}
		}
		return((String[])aliases.toArray(new String[0]));
	}

	@Override
	public Object getNewProperty(String propertyName) {
		ICi ci = ciMap.get(propertyName);
		if (ciMap.size() == 0) {
			getNewProperties();
			ci = ciMap.get(propertyName);
		}
		if (ci == null) {
			return(null);
		}
		return(new InstanceContext(this.context, ci));
	}

	public void setProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		
	}

	public void newObject(String alias) {
		ICmdbTransaction tx = (ICmdbTransaction) context.get("tx");
		if (tx == null) {
			throw new IllegalArgumentException("No transaction setup!");
		}
		
		// Modify the template by creating a new instance offspring.
		ICiModifiable templateMod = tx.getTemplate(this.template);
		
		ICiModifiable instanceMod = templateMod.createOffspring();
		instanceMod.setIsBlueprint(false);
		instanceMod.setAlias(alias);
		
		updateProperty(alias, new InstanceModifiableContext(this.context, instanceMod));
	}

}
