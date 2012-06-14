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
package org.onecmdb.core.tests.performance;

import java.util.Set;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.tests.profiler.Profiler;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

public class TestGraphQueryPerformance extends AbstractOneCmdbTestCase {

	@Override
	protected String getDatasourceResource() {
		return("org/onecmdb/core/tests/resources/datasource/postgresql-update-datasource.xml");
	}
	
	
	public void testGraphQueryCI() {
		IModelService svc = (IModelService) session.getService(IModelService.class);
		OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
		impl.setOneCmdb(cmdbContext);
		
		String token;
		try {
			token = impl.auth("admin", "123");
			GraphQuery q = new GraphQuery();
			/*
			Profiler.setProfileFile("d:/tmp/debug/profile.txt");
			Profiler.useProfiler(true);
			*/
			//ItemAliasSelector sel = new ItemAliasSelector("primary", "Ci");
			//sel.setAlias("NAGIOS_I_host_pae-034-bigben-cn-PA");
			ItemOffspringSelector sel = new ItemOffspringSelector("primary", "NAGIOS_Host");
			sel.setMatchTemplate(false);
			sel.setPrimary(true);

			q.addSelector(sel);

			
			impl.queryGraph(token, q);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}
