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
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.handler.QueryHandler;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemIdConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RFCTargetConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRFCSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemTransactionSelector;
import org.onecmdb.core.utils.graph.query.selector.RFCItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.TransactionRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.wsdl.TransactionBean;
import org.onecmdb.core.utils.xml.XmlParser;

public class GraphTest extends AbstractOneCmdbTestCase {
	
	
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
		if (true) {
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
	
	public void xtestImport() {
		List<String> result = importModel(new String[] {
				"file:D:/home/niklas/prjs/cygate/cygate-cmdb/Distribution/cygate/model/Cygate_CI_Template.xml",
				"classpath:models/OneCMDB_Reference_Templates.xml",
				"file:D:/home/niklas/prjs/cygate/cygate-cmdb/Distribution/cygate/model/CygateModel.xml",
				
				"file:D:/home/niklas/prjs/cygate/cygate-cmdb/DataSources/SuperOffice/result/SO_utdrag.xml",
				"file:D:/home/niklas/prjs/cygate/cygate-cmdb/DataSources/Netcool/result/SLL-Node-IF-JDBC.xml",
				"file:D:/home/niklas/prjs/cygate/cygate-cmdb/DataSources/XLS/result/WANAccess-Connection-Mapped-JDBC.xml"
							
		});
		System.out.println("=======================================================");
		for (String msg : result) {
			System.out.println(msg);
		}
		System.out.println("=======================================================");
	}
	
	public void xtestImportModified() {
		List<String> result = importModel(new String[] {
				"file:D:/home/niklas/eclipse-ws/org.onecmdb.model.cygate/src/resource/CygateAnslStatus.xml",
				"file:D:/home/niklas/eclipse-ws/org.onecmdb.model.cygate/src/resource/CygateModel.xml",
				"file:D:/home/niklas/eclipse-ws/org.onecmdb.model.cygate/src/resource/CygateSecurityModel.xml",
		});
		System.out.println("=======================================================");
		for (String msg : result) {
			System.out.println(msg);
		}
		System.out.println("=======================================================");
	}
	
	public void xtestRFCQuery4() {
		GraphQuery q = new GraphQuery();
		
		
		ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
		brukare.setPageInfo(new PageInfo(0, 5));
		brukare.setPrimary(true);
	
		q.addSelector(brukare);
		
		Graph result = cmdbService.queryGraph(token, q);
		
		Template t = result.fetchNode("brukare");
		for (CiBean bean : t.getOffsprings()) {
			System.out.println("BEAN: " + bean.toString());
			
			// Fetch RFC for an attribute....
			ValueBean v = bean.fetchAttributeValueBean("name", 0);
			
			ItemRFCSelector rfc = new ItemRFCSelector("rfc", "Any");
			rfc.applyConstraint(new RFCTargetConstraint(v.getId()));
			GraphQuery rfcq = new GraphQuery();
			rfc.setPrimary(true);
			rfcq.addSelector(rfc);
			
			Graph r2 = cmdbService.queryGraph(token, rfcq);
			Template rfcs = r2.fetchNode("rfc");
			for (RFCBean r : rfcs.getRFC()) {
				System.out.println("\t" + r.toString());
			}
		}
	
	}

	public void xtestTransactionQuery1() {
		ItemTransactionSelector trans = new ItemTransactionSelector("trans", null);
		trans.setPrimary(true);
		trans.setPageInfo(new PageInfo(0, 1));
		
		GraphQuery q1 = new GraphQuery();
		q1.addSelector(trans);
		Graph result = cmdbService.queryGraph(token, q1);
		Template t = result.fetchNode("trans");
		TransactionBean tx = t.getTransactions().get(0);
		
		GraphQuery q = new GraphQuery();
			
		ItemOffspringSelector offspring = new ItemOffspringSelector("ci", null);
		offspring.setPrimary(true);
		
		ItemTransactionSelector t2 = new ItemTransactionSelector("trans", null);
		t2.setTxId(tx.getId());
		
		ItemRFCSelector rfc = new ItemRFCSelector("rfc", null);
	
		rfc.setTxId(tx.getId());
		
		RFCItemRelationSelector b2rfc = new RFCItemRelationSelector("b2rfc", RFCItemRelationSelector.META_RELATION_RFC, offspring.getId(), rfc.getId());
		TransactionRelationSelector rfc2trans = new TransactionRelationSelector("rfc2trans", TransactionRelationSelector.META_RELATION_TRANSACTION, t2.getId(), rfc.getId());

		
		q.addSelector(offspring);
		q.addSelector(rfc);
		//q.addSelector(t2);
		q.addSelector(b2rfc);
		//q.addSelector(rfc2trans);
		
		Graph r2 = cmdbService.queryGraph(token, q);
		
		System.out.println("Result:" + result.toString());
		
	}
	public void xtestRFCQuery3() {
		CiBean bBean = null;
		{
			GraphQuery q = new GraphQuery();
			
			ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
			
			brukare.setPageInfo(new PageInfo(0, 1));
			brukare.setPrimary(true);
			q.addSelector(brukare);
			Graph result = cmdbService.queryGraph(token, q);
			Template t = result.fetchNode("brukare");
			bBean = t.getOffsprings().get(0);
		}
		
		{
			GraphQuery q = new GraphQuery();
			ValueBean vName = bBean.fetchAttributeValueBean("name", 0);
			ItemRFCSelector rfc = new ItemRFCSelector("rfc-brukare", ItemRFCSelector.RFC_MODIFY_VALUE_TYPE);
			rfc.setPageInfo(new PageInfo(0, 20));
			rfc.applyConstraint(new RFCTargetConstraint(vName.getId()));
			rfc.setPrimary(true);
			q.addSelector(rfc);
		
			Graph result = cmdbService.queryGraph(token, q);

			System.out.println(result);

			Template t = result.fetchNode("rfc-brukare");
			for (RFCBean bean : t.getRFC()) {
				System.out.println("RFCS: " + bean.toString());
			}
		}
	}
	
	public void testIdConstraint() {
		ItemOrGroupConstraint or = new ItemOrGroupConstraint();
		
		{
			GraphQuery q = new GraphQuery();
			ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
			brukare.setPageInfo(new PageInfo(0, 5));
			brukare.setPrimary(true);
			
			q.addSelector(brukare);
			Graph result = cmdbService.queryGraph(token, q);
			
			Template t = result.fetchNode("brukare");
			for (CiBean b : t.getOffsprings()) {
				ItemIdConstraint idCon = new ItemIdConstraint();
				idCon.setId(b.getId());
				or.add(idCon);
			}
		}
		{
			GraphQuery q = new GraphQuery();
			ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
			brukare.applyConstraint(or);
			brukare.setPrimary(true);
			q.addSelector(brukare);
			Graph result = cmdbService.queryGraph(token, q);
			
			System.out.println(result.toString());
		}
		
	}
	
	public void xtestRFCQuery2() {
		GraphQuery q = new GraphQuery();
		
		ItemAliasSelector brukare = new ItemAliasSelector("brukare", "Brukare");
		brukare.setAlias("Brukare-1226815730");
		
		brukare.setPageInfo(new PageInfo(0, 5));
		brukare.setPrimary(true);
	
		ItemRFCSelector rfc = new ItemRFCSelector("rfc-brukare", ItemRFCSelector.TEMPLATE_RFC);
		rfc.setPageInfo(new PageInfo(0, 20));
		
		RFCItemRelationSelector b2rfc = new RFCItemRelationSelector("b2rfc", RFCItemRelationSelector.META_RELATION_RFC, brukare.getId(), rfc.getId());
		
		q.addSelector(brukare);
		q.addSelector(rfc);
		q.addSelector(b2rfc);
		
		Graph result = cmdbService.queryGraph(token, q);
		
		Template t = result.fetchNode("rfc-brukare");
		for (RFCBean bean : t.getRFC()) {
			System.out.println("RFCS: " + bean.toString());
		}
	
	}
	public void xtestNextGraph() {
		GraphQuery query = new GraphQuery();
		
		
		ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
		ItemOffspringSelector access = new ItemOffspringSelector("access", "WANAccess");
		ItemOffspringSelector conn = new ItemOffspringSelector("connection", "NetworkConnection");
		
			
		ItemRelationSelector a2b = new ItemRelationSelector("a2b", "Reference", brukare.getId(), access.getId());
		ItemRelationSelector a2c = new ItemRelationSelector("a2c", "Reference", conn.getId(), access.getId());
	
		ItemAliasSelector status = new ItemAliasSelector("connectionStatus", "NetworkConnectionStatus");
		status.setAlias("Production");
		ItemRelationSelector c2g = new ItemRelationSelector("c2g", "PointsTo", status.getId(), conn.getId());
		
		
		ItemSecurityConstraint security = new ItemSecurityConstraint();
		security.setGroupName("SLLNetSecurityGroup");
		access.applyConstraint(security);
		brukare.applyConstraint(security);
		//conn.applyConstraint(security);
		
		
		
		AttributeValueConstraint vConstraint = new 
		AttributeValueConstraint("name", 
				AttributeValueConstraint.LIKE, "A%");
		
		
		access.setPrimary(true);
		access.setPageInfo(new PageInfo(0, 10));
		a2b.setMandatory(false);
		
		query.addSelector(access);
		query.addSelector(brukare);
		query.addSelector(conn);
		query.addSelector(a2b);
		query.addSelector(a2c);
		//query.addSelector(status);
		//query.addSelector(c2g);
		
		QueryHandler handler = new QueryHandler(session);
		Graph result = handler.execute3(query);
		
		//Graph result = cmdbService.queryGraph(token, query);
		System.out.println(result.toString());	
	}
	public void xtestRFCQuery() {
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
		brukare.setPageInfo(new PageInfo(0, 5));
		brukare.setPrimary(true);
		
		
		ItemRFCSelector rfc = new ItemRFCSelector("rfc-brukare", ItemRFCSelector.TEMPLATE_RFC);
		rfc.setPageInfo(new PageInfo(0, 20));
		
		RFCItemRelationSelector b2rfc = new RFCItemRelationSelector("b2rfc", RFCItemRelationSelector.META_RELATION_RFC, brukare.getId(), rfc.getId());
		
		q.addSelector(brukare);
		q.addSelector(rfc);
		q.addSelector(b2rfc);
		
		Graph result = cmdbService.queryGraph(token, q);
		
		result.buildMap();
		
		Template t = result.fetchNode("brukare");
		for (CiBean bean : t.getOffsprings()) {
			List<RFCBean> rfcs = result.fetchRFCs(bean);
			System.out.println("BEAN:" + bean);
			System.out.println("RFCS: " + rfcs.size());
		}
		
	}
	
	public void xtestLatestRFCQuery() {
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
		
		ItemRFCSelector rfc = new ItemRFCSelector("rfc-brukare", ItemRFCSelector.TEMPLATE_RFC);
		rfc.setPageInfo(new PageInfo(0, 20));
		rfc.setPrimary(true);
		RFCItemRelationSelector b2rfc = new RFCItemRelationSelector("b2rfc", RFCItemRelationSelector.META_RELATION_RFC, brukare.getId(), rfc.getId());
		
		q.addSelector(brukare);
		q.addSelector(rfc);
		q.addSelector(b2rfc);
		
		Graph result = cmdbService.queryGraph(token, q);
		
		result.buildMap();
		
		Template t = result.fetchNode("rfc-brukare");
		for (RFCBean bean : t.getRFC()) {
			System.out.println("RFCS: " + bean.toString());
		}
		Template t2 = result.fetchNode("brukare");
		for (CiBean bean : t2.getOffsprings()) {
			System.out.println("Bean: " + bean.toString());
		}
		
		
	}
	
	public void xtestNextQuery() {
	
		GraphQuery q = new GraphQuery();
		
		
		ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
		ItemOffspringSelector access = new ItemOffspringSelector("access", "WANAccess");
		ItemOffspringSelector conn = new ItemOffspringSelector("ansl", "NetworkConnection");
		
		ItemRelationSelector a2b = new ItemRelationSelector("a2b", "Reference", brukare.getId(), access.getId());
		ItemRelationSelector a2c = new ItemRelationSelector("a2c", "Reference", conn.getId(), access.getId());
		
		
		brukare.applyConstraint(new AttributeValueConstraint("name", AttributeValueConstraint.LIKE, "A%"));
		
		//conn.applyConstraint(new AttributeValueConstraint("node", AttributeValueConstraint.LIKE, "%sos%"));
		ItemAliasSelector status = new ItemAliasSelector("connectionStatus", "NetworkConnectionStatus");
		status.setAlias("Production");
		
		ItemRelationSelector c2g = new ItemRelationSelector("c2g", "PointsTo", status.getId(), conn.getId());
	
		//q.addSelector(c2g);
		//q.addSelector(status);
		
		access.setPageInfo(new PageInfo(0, 25));
		access.setPrimary(true);
		q.addSelector(access);
		
		q.addSelector(brukare);
		
		q.addSelector(conn);
		
		q.addSelector(a2b);
		
		q.addSelector(a2c);
		
		Graph result = cmdbService.queryGraph(token, q);
		/*
		QueryHandler handler = new QueryHandler(session);
		Graph result = handler.execute2(q);
		result.buildMap();
		*/
		result.buildMap();
		Template aTemplate = result.fetchNode("access");
		for (CiBean a : aTemplate.getOffsprings()) {
			Template con = result.fetchReference(a, RelationConstraint.SOURCE, "a2c");
			Template bruk = result.fetchReference(a, RelationConstraint.SOURCE, "a2b");
			
			System.out.println(aTemplate.getId() + ":" + a.getAlias());
			if (con != null) {
				System.out.println("\t" + con.getId() + ":" +  showAlias(con.getOffsprings()) + "[" +con.getOffsprings().size() +"]" );
			} else {
				System.out.println("NO Connection");
			}
			if (bruk != null) {
				System.out.println("\t" + bruk.getId() + ":" + showAlias(bruk.getOffsprings()) + "[" + bruk.getOffsprings().size() + "]");
			} else {
				System.out.println("NO BRUK");
			}
		}
	}
	
	private String showAlias(List<CiBean> beans) {
		StringBuffer b = new StringBuffer();
		b.append("{");
		for (CiBean bean : beans) {
			b.append(bean.getAlias() + ", ");
		}
		b.append("}");
		return(b.toString());
	}
	/**
	 * Query brukare where name starts with a.
	 * Return a Tree of this type.
	 * 	WANAccess
	 * 		--> Brukare.
	 * 		--> Anslutning.
	 */
	public void xtestSimpleGraph() {
		
		//org.acegisecurity.providers.ldap.LdapAuthenticationProvider
		ItemOffspringSelector brukare = new ItemOffspringSelector("brukare", "Brukare");
		ItemOffspringSelector access = new ItemOffspringSelector("access", "WANAccess");
		ItemOffspringSelector conn = new ItemOffspringSelector("ansl", "NetworkConnection");
		
		ItemRelationSelector a2b = new ItemRelationSelector("a2b", "Reference", brukare.getId(), access.getId());
		ItemRelationSelector a2c = new ItemRelationSelector("a2c", "Reference", conn.getId(), access.getId());
		
		
			ItemAndGroupConstraint bConstraint = new ItemAndGroupConstraint();
			bConstraint.add(new AttributeValueConstraint("name", AttributeValueConstraint.LIKE, "A%"));
			brukare.applyConstraint(bConstraint);

			ItemAndGroupConstraint con1 = new ItemAndGroupConstraint();
			con1.add(new RelationConstraint(RelationConstraint.SOURCE, a2b.getId()));
			access.applyConstraint(con1);

			ItemAndGroupConstraint con2 = new ItemAndGroupConstraint();
			con2.add(new RelationConstraint(RelationConstraint.TARGET, a2b.getId()));
			conn.applyConstraint(con2);
		
		
		
		GraphQuery query = new GraphQuery();
		access.setPageInfo(new PageInfo(0, 20));
		
		query.addSelector(access);
		
		query.addSelector(brukare);
		query.addSelector(a2b);
		query.addSelector(conn);
		query.addSelector(a2c);
		
		IModelService mservice = (IModelService)session.getService(IModelService.class);
		QueryHandler handler = new QueryHandler(session);
		long start = System.currentTimeMillis();
		Graph result = null;//handler.execute(query);
		result.buildMap();
		for (Template node: result.getNodes()) {
			System.out.println("Template id=" + node.getId() + ", count " + node.getOffsprings().size() + "(" + node.getTotalCount() +")");
		}
		for (Template node: result.getEdges()) {
			System.out.println("Edge id=" + node.getId() + ", count " + node.getOffsprings().size() + "(" + node.getTotalCount() +")");
		}
		
		Template t = result.fetchNode("access");
		for (CiBean b : t.getOffsprings()) {
			Template brukResult = result.fetchReference(b, RelationConstraint.SOURCE, "b2a");
			Template anslResult = result.fetchReference(b, RelationConstraint.SOURCE, "a2c");
			System.out.print(t.getId() + ":" + b.toString());
			System.out.print(";" + brukResult.getId() + ":" +  brukResult.getOffsprings().size());
			System.out.println(";" + anslResult.getId() + ":" + anslResult.getOffsprings().size());
			
		}
		long stop = System.currentTimeMillis();
		System.out.println("Query: " + (stop-start) + "ms");
		
	}

	
	private List<String> importModel(String urls[]) {
		List<String> report = new ArrayList<String>();
		for (String url : urls) {
			long start = System.currentTimeMillis();
			XmlParser parser = new XmlParser();
			parser.addURL(url);
			CiBean beans[] = parser.getBeans().toArray(new CiBean[0]);
			IRfcResult result = cmdbService.update(token, beans, null);
			long stop = System.currentTimeMillis();
			
			Assert.assertEquals(null, result.getRejectCause());
			long dt = stop-start;
			long msPci = dt / beans.length;
			String msg = "IMPORT: [" + url + "] ci's [" + beans.length + "] time=" + dt + "ms, " + msPci + "ms/ci";
			report.add(msg);
			System.out.println(msg);
		}
		return(report);
	}
	
	private void deleteModel() {
		CiBean bruk = new CiBean("Ci", "Brukare", true);
		CiBean access = new CiBean("Ci", "WANAccess", true);
		CiBean conn = new CiBean("Ci", "NetworkConnection", true);
		
		cmdbService.update(token, null, new CiBean[] {bruk, access, conn});
		
	}
	
	private void createModel() {
		
		List beans = new ArrayList();
		
		CiBean bruk = new CiBean("Ci", "Brukare", true);
		bruk.addAttribute(new AttributeBean("name", "xs:string",null, false));
		
		CiBean access = new CiBean("Ci", "WANAccess", true);
		access.addAttribute(new AttributeBean("brukare", "Brukare", "Reference", true));
		access.addAttribute(new AttributeBean("networkConnection", "NetworkConnection", "Reference", true));
			
		
		CiBean conn = new CiBean("Ci", "NetworkConnection", true);
		conn.addAttribute(new AttributeBean("lanIF", "xs:string", null, false));
		
		
		beans.add(bruk);
		beans.add(access);
		beans.add(conn);
		
		for (int i = 0; i < 5000; i++) {
			CiBean ib = new CiBean("Brukare", "b-" + i, false);
			CiBean ia = new CiBean("WANAccess", "a-" + i, false);
			CiBean ic = new CiBean("NetworkConnection", "c-" + i, false);
			
			ia.addAttributeValue(new ValueBean("brukare", ib.getAlias(), true));
			ia.addAttributeValue(new ValueBean("networkConnection", ic.getAlias(), true));
			Character a = new Character('A');
			int c = a.charValue();
			int offset = (i % 25);
			System.out.println("c=" + c + "offset=" + offset);
			Character t = new Character((char)(c + offset));
			String name =  t.toString() + "Brukare XXXX";
			System.out.println("Name=" + name);
			ib.addAttributeValue(new ValueBean("name",name, false));
		
			beans.add(ib);
			beans.add(ia);
			beans.add(ic);
			
		}
		
		cmdbService.update(token, (CiBean[])beans.toArray(new CiBean[0]), null); 
		
	}
	

}
