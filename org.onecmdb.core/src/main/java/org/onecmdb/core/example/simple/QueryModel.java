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
package org.onecmdb.core.example.simple;

import java.util.Set;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;

public class QueryModel {

	/**
	 * Use the IModelService and get the Root Ci, 
	 * and print all offsprings of the root object.
	 */
	public void dumpOffsprings() {
		IModelService modelsvc = new Setup().getModelService();
		
		// Fetch the root  Model Object.
		ICi root = modelsvc.getRoot();

		// Fetch it's offsprings.
		Set<ICi> offsprings = root.getOffsprings();

		// Print all offsprings of the root ci.
		System.out.println(root.getDisplayName());
		for (ICi ci: offsprings) {
			System.out.println("\t--> " + ci.getDisplayName());
		}
	}
	
	
	public void useFindAlias() {
		IModelService modelsvc = new Setup().getModelService();
		
		// Find a Ci with a specific alias name. No expression can be used.
		ICi ci = modelsvc.findCi(new Path<String>("IP"));
		if (ci == null) {
			System.out.println("No ci with alias name IP found!");
			return;
		}
		
		// Query offsprings of the IP ci with paging.
		QueryCriteria criteria = new QueryCriteria();
		// Set the template id.
		criteria.setOffspringOfId(ci.getId().asLong() + "");
		// Set First 10.
		criteria.setFirstResult(0);
		criteria.setMaxResult(10);
		QueryResult<ICi> result = modelsvc.query(criteria);
	
		// Set next page.
		criteria.setFirstResult(10);
		criteria.setMaxResult(10);
		result = modelsvc.query(criteria);
	}
	
	public void useQuery() {
		IModelService modelsvc = new Setup().getModelService();
		ICi ci = modelsvc.findCi(new Path<String>("IP"));
		if (ci == null) {
			System.out.println("No ci with alias name IP found!");
			return;
		}
		
		
		
	}
}
