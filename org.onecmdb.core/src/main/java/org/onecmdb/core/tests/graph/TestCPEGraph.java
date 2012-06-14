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
package org.onecmdb.core.tests.graph;

import java.net.MalformedURLException;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

public class TestCPEGraph extends AbstractOneCmdbTestCase {
	private IOneCMDBWebService cmdbService;
	private String token;

	
	
	@Override
	protected String getDatasourceResource() {
		return("org/onecmdb/core/tests/graph/mysql-update-cygate-datasource.xml");
		//return(OneCMDBTestConfig.MYSQL_UPDATE_DATASOURCE);
		//return(OneCMDBTestConfig.SQLSERVER2005_CREATE_DROP_DATASOURCE);
		//return(OneCMDBTestConfig.HSQL_SERVER_CREATE_DROP_DATASOURCE);
	}

	public void setUp() {
		if (false) {
			Service serviceModel = new ObjectServiceFactory().create(IOneCMDBWebService.class);
			try {
				String remoteURL = "http://localhost:8888/onecmdb/webservice/onecmdb";
				cmdbService = (IOneCMDBWebService) new XFireProxyFactory().create(serviceModel, remoteURL);
			} catch (MalformedURLException e) {				
				e.printStackTrace();
				fail("Can't connect to remote WebService ");
			}
		} else {
			super.setUp();

			OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
			impl.setOneCmdb(getCmdbContext());
			cmdbService = impl;
		}
		try {
			//token = cmdbService.auth("niklas", "G8sigåt1");
			//token = cmdbService.auth("support", "1Qaz1qaz");
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testDirektConnection() {
		ItemOffspringSelector connection = new ItemOffspringSelector("direkt", "DirektNetworkConnection");
		ItemOffspringSelector ifs = new ItemOffspringSelector("ifs", "NetworkIF");
		ItemOffspringSelector nodes = new ItemOffspringSelector("nodes", "NetworkNode");
		
		ItemRelationSelector c2i = new ItemRelationSelector("c2i", "ConnectedTo", ifs.getId(), connection.getId());
		ItemRelationSelector i2n = new ItemRelationSelector("i2n", "BelongsTo", nodes.getId(), ifs.getId());
		
		nodes.setPrimary(true);
		ifs.setExcludedInResultSet(true);
		connection.setExcludedInResultSet(true);
		c2i.setExcludedInResultSet(true);
		i2n.setExcludedInResultSet(true);
		
		
		GraphQuery q = new GraphQuery();
		q.addSelector(connection);
		q.addSelector(ifs);
		q.addSelector(nodes);
		q.addSelector(c2i);
		q.addSelector(i2n);
		
		Graph result = cmdbService.queryGraph(token, q);
		System.out.println(result.toString());
	}
		
}
