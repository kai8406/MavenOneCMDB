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
package org.onecmdb.utils.test;

import java.net.MalformedURLException;

import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;

public class TestGraphQuery {

		public static void main(String argv[]) {
			try {
				new TestGraphQuery().testGraphQueryAttribute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void testGraphQuery() throws Exception {
			// Create wsdl handler.
			IOneCMDBWebService service = OneCMDBServiceFactory.getWebService("http://localhost:8080/webservice/onecmdb");
			String token = service.auth("admin", "123");
			
			
			GraphQuery query = new GraphQuery();
			
			// Select Company instances.
			ItemOffspringSelector company = new ItemOffspringSelector("company", "Company");
			company.setPrimary(true);
			
			// Select System instances.
			ItemOffspringSelector system = new ItemOffspringSelector("system", "System");
	
			// Select the PointsTo reference instnaces between system and company.
			ItemRelationSelector system2company = new ItemRelationSelector(
					"system2compnay", // Id of the selector 
					"PointsTo",  // Template to fetch instnaces.
					company.getId(), // Target instance 
					system.getId()); // Source instance
			
			// Add selectors to query.
			query.addSelector(company);
			query.addSelector(system);
			query.addSelector(system2company);
			
			// Do the query..
			Graph result = service.queryGraph(token, query);
			// Will internal connect references to enable later
			// useage of 
			result.buildMap();
			
			// The result is expressed as a Graph where Nodes are all CI instances 
			// and Edge are all Reference instances.
			Template companies = result.fetchNode(company.getId());
			
			// List all companies and belonging systems
			for (CiBean comp : companies.getOffsprings()) {
				System.out.println("Company=" + comp.getDisplayName());
				Template systems = result.fetchReference(comp, RelationConstraint.TARGET, system2company.getId());
				for (CiBean sys : systems.getOffsprings()) {
					System.out.println("\tSystem=" + sys.getDisplayName());
				}
			}
			
			/**
			 * Will procude:
			 * 	Company=Sun
			 *		System=Bravo 212.100.105.110
			 *	Company=Dell Computers
			 *		System=Jsmith Desktop 212.100.95.23
			 *		System=Charlie 212.100.120.003
			 *		System=Echo 212.100.103.145
			 *		System=Dgould Desktop 212.100.23.76
			 *		System=Alpha 212.100.105.105
			 *		System=Delta 212.100.220.134
			 *
			 */
		}
		
		public void testGraphQueryAttribute() throws Exception {
			// Create wsdl handler.
			IOneCMDBWebService service = OneCMDBServiceFactory.getWebService("http://localhost:8080/webservice/onecmdb");
			String token = service.auth("admin", "123");
			
			
			GraphQuery query = new GraphQuery();
			
			// Select Company instances.
			ItemOffspringSelector company = new ItemOffspringSelector("company", "Company");
			company.setPrimary(true);
			
			// Add constraint on the the instances.
			AttributeValueConstraint aConstraint = new AttributeValueConstraint();
			aConstraint.setAlias("Name");
			aConstraint.setOperation(AttributeValueConstraint.LIKE);
			aConstraint.setValue("%Dell%");
			company.applyConstraint(aConstraint);
			
			// Select System instances.
			ItemOffspringSelector system = new ItemOffspringSelector("system", "System");
	
			// Select the PointsTo reference instnaces between system and company.
			ItemRelationSelector system2company = new ItemRelationSelector(
					"system2compnay", // Id of the selector 
					"PointsTo",  // Template to fetch instnaces.
					company.getId(), // Target instance 
					system.getId()); // Source instance
			
			
			// Add pageing to the company..
			PageInfo info = new PageInfo();
			info.setMaxResult(10);
			info.setFirstResult(0);
			company.setPageInfo(info);
			
			// Add selectors to query.
			query.addSelector(company);
			query.addSelector(system);
			query.addSelector(system2company);
			
			// Do the query..
			Graph result = service.queryGraph(token, query);
			// Will internal connect references to enable later
			// useage of 
			result.buildMap();
			
			// The result is expressed as a Graph where Nodes are all CI instances 
			// and Edge are all Reference instances.
			Template companies = result.fetchNode(company.getId());
			
			// List all companies and belonging systems
			System.out.println("Found " + companies.getTotalCount() + " Companies");
			for (CiBean comp : companies.getOffsprings()) {
				System.out.println("Company=" + comp.getDisplayName());
				Template systems = result.fetchReference(comp, RelationConstraint.TARGET, system2company.getId());
				for (CiBean sys : systems.getOffsprings()) {
					System.out.println("\tSystem=" + sys.getDisplayName());
				}
			}
			
			/**
			 * Will produce:
			 * 	Found 1 Companies
			 * 	Company=Dell Computers
			 *		System=Jsmith Desktop 212.100.95.23
			 *		System=Charlie 212.100.120.003
			 *		System=Echo 212.100.103.145
			 *		System=Dgould Desktop 212.100.23.76
			 *		System=Alpha 212.100.105.105
			 *		System=Delta 212.100.220.134
			 */
		}
}
