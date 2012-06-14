/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.core.tests.export;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.profiler.Profiler;
import org.onecmdb.core.utils.OnecmdbUtils;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.transform.export.CSVExportSet;
import org.onecmdb.core.utils.transform.export.CSVExporter;
import org.onecmdb.core.utils.transform.export.ColumnSelector;
import org.onecmdb.core.utils.transform.export.InstanceSelector;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.xpath.commands.AuthCommand;
import org.onecmdb.core.utils.xpath.commands.ExportCommand;

public class TestExport extends AbstractOneCmdbTestCase {
	
	public TestExport() {
		super();
	}

	@Override
	protected String getDatasourceResource() {
		return("org/onecmdb/core/tests/resources/datasource/mysql-172-update-datasource.xml");
		
	}
	
	public void xTestExportModel() throws Exception {
		AuthCommand authCmd = new AuthCommand(getCmdbContext());
		authCmd.setPwd("123");
		authCmd.setUser("admin");
		String token = authCmd.getToken();
		
		ExportCommand cmd = new ExportCommand(getCmdbContext());
		cmd.setAuth(token);
		cmd.setName("CSVExportSet-1");
		cmd.transfer(System.out);
	}
	
	public void xtestQuery() {
		QueryCriteria crit = new QueryCriteria();
		crit.setText("sos02r");
		crit.setTextMatchValue(true);
		crit.setOffspringOfAlias("NetworkNode");
		IModelService mSvc = (IModelService) session.getService(IModelService.class);
		QueryResult result = mSvc.query(crit);
		if (result.size() == 1) {
			ICi ci = (ICi) result.get(0);
			System.out.println("Found CI:" + ci);
			OnecmdbUtils util = new OnecmdbUtils(session);
			QueryCriteria crit2 = new QueryCriteria();
			crit2.setOffspringDepth(new Integer(-1));
			long start = System.currentTimeMillis();
			result = util.evaluate(ci, "<$template{Ci}", crit2, true);
			long stop = System.currentTimeMillis();
			long dt1 = (stop-start);
			System.out.println("count1=" + result.getTotalHits() +"time=" + dt1 + "ms");
			start = stop;
			result = util.evaluate(ci, "<$template{*}", null, true);
			stop = System.currentTimeMillis();
			long dt2 = (stop-start);
			System.out.println("count2=" + result.getTotalHits() +"time=" + dt2 + "ms");
				
	
		}
	}
	
	public void xtestIFAliasExport() {
		CSVExportSet set = new CSVExportSet();
		set.setDelimiter("|");
		InstanceSelector sel = new InstanceSelector();
		sel.setTemplate("NetworkIF");
		set.setInstanceSelector(sel);
		
		ArrayList colList = new ArrayList();
		colList.add(new ColumnSelector("IF Alias", ">$attr{ifAlias}", "AAA"));
		colList.add(new ColumnSelector("Node" , ">$attr{system}|>$attr{entityName}", "ABB"));
		
		
		
		set.setColumnSelector(colList);
		//set.setColumnSelector(Collections.EMPTY_LIST);
		CSVExporter export = new CSVExporter(session);
		Profiler.useProfiler(true);
		export.toOutputStream(System.out, set);
	}

	
	public void xtestAnslutningsExport() {
		CSVExportSet set = new CSVExportSet();
		set.setDelimiter("|");
		InstanceSelector sel = new InstanceSelector();
		sel.setTemplate("Anslutning");
		set.setInstanceSelector(sel);
		
		ArrayList colList = new ArrayList();
		colList.add(new ColumnSelector("Datum", ">$attr{datum}", "AAA"));
		colList.add(new ColumnSelector("Brukar ID" , ">$attr{brukareID}", "ABB"));
		colList.add(new ColumnSelector("Anslutning" , ">$attr{anslTyp}", "ACC"));
		/*
		colList.add(new ColumnSelector("Org" , ">$attr{usedBy}", "ADD"));
		colList.add(new ColumnSelector("Node" , ">$attr{node}", "AEE"));
		*/
		
		
		set.setColumnSelector(colList);
		//set.setColumnSelector(Collections.EMPTY_LIST);
		CSVExporter export = new CSVExporter(session);
		Profiler.useProfiler(true);
		export.toOutputStream(System.out, set);
	}

	public void xtestSortOrder() {
		
		QueryCriteria<ICi> crit = new QueryCriteria<ICi>();
		crit.setOffspringOfAlias("Anslutning");
		crit.setOffspringDepth(new Integer(-1));
		
		crit.setOrderAttAlias("brukareID");
		crit.setOrderAscending(true);
	
		crit.setFirstResult(new Integer(0));
		crit.setMaxResult(10);
		long time1 = System.currentTimeMillis();
		QueryResult<ICi> result = ((IModelService)session.getService(IModelService.class)).query(crit);
		long time2 = System.currentTimeMillis();
		
		String remoteURL = "http://192.168.1.15:8080/webservice/onecmdb";
		
			Service serviceModel = new ObjectServiceFactory().create(IOneCMDBWebService.class);
	
			try {
				IOneCMDBWebService cmdbService = (IOneCMDBWebService)
				    new XFireProxyFactory().create(serviceModel, remoteURL);

				String auth = cmdbService.auth("admin", "123");
				long time3 = System.currentTimeMillis();
				CiBean beans[] = cmdbService.search(auth, crit);
				long time4 = System.currentTimeMillis();
			
				
				System.out.println("DB_CALL(" + (time1-time1) + "ms) WEB SERVICE(" + (time4-time3) +")");
			} catch (MalformedURLException e) {				
				e.printStackTrace();
				fail("Can't connect to remote WebService ");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	}

	
	public void testQueryPerfomance() {
		
		getNetworkConnection(session, "11687", null);
		
		if (true) {
			return;
		}
		IModelService mSvc = (IModelService)session.getService(IModelService.class);
		
		long t1 = System.currentTimeMillis();
		ICi ci = mSvc.findCi(new Path<String>("WANAccess"));
		long t2 = System.currentTimeMillis();
		System.out.println("Query for CI:" + (t2-t1) + "ms");
		
		QueryCriteria<ICi> crit = new QueryCriteria<ICi>();
		crit.setOffspringOfAlias("Brukare");
		crit.setText("MBO1");
		crit.setMatchAttributeAlias("brukarId");
		crit.setTextMatchValue(true);
					
		
		long time1 = System.currentTimeMillis();
		QueryResult<ICi> result = mSvc.query(crit);
		long time2 = System.currentTimeMillis();
		
		System.out.println("Query for attribute:" + (time2-time1) + "ms, size=" + result.size());
		
		OnecmdbUtils utils = new OnecmdbUtils(session);
		QueryCriteria crit2 = new QueryCriteria();
		//crit2.setOffspringDepth(new Integer(1));
		long time3 = System.currentTimeMillis();
		QueryResult res = utils.evaluate(result.get(0), "<$template{WANAccess}", crit2, false);
		long time4 = System.currentTimeMillis();
		System.out.println("Query for relation:" + (time4-time3) + "ms, size=" + result.size());
			
		/*
		String remoteURL = "http://192.168.1.15:8080/webservice/onecmdb";
		
			Service serviceModel = new ObjectServiceFactory().create(IOneCMDBWebService.class);
	
			try {
				IOneCMDBWebService cmdbService = (IOneCMDBWebService)
				    new XFireProxyFactory().create(serviceModel, remoteURL);

				String auth = cmdbService.auth("admin", "123");
				long time3 = System.currentTimeMillis();
				CiBean beans[] = cmdbService.search(auth, crit);
				long time4 = System.currentTimeMillis();
			
				
				System.out.println("DB_CALL(" + (time1-time1) + "ms) WEB SERVICE(" + (time4-time3) +")");
			} catch (MalformedURLException e) {				
				e.printStackTrace();
				fail("Can't connect to remote WebService ");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			*/
	}
	
	
	/**
	 * Find a network connection for a specific interface.
	 * If the interface is null, return all network connection
	 * attached attached.
	 */
	public List<ICi> getNetworkConnection(ISession session, String nodeEntityId, String ifId) {
		long start = System.currentTimeMillis();
		
		IModelService modelsvc = (IModelService) session
				.getService(IModelService.class);
		
		List<ICi> resultCon = new ArrayList<ICi>();
		if (ifId == null) { 
			QueryCriteria<ICi> crit = new QueryCriteria<ICi>();
			crit.setOffspringOfAlias("NetworkNode");
			crit.setText(nodeEntityId);
			crit.setTextMatchValue(true);
			crit.setMatchAttributeAlias("entityId");
			long t1 = System.currentTimeMillis();
			QueryResult<ICi> result = modelsvc.query(crit);
			long t2 = System.currentTimeMillis();
					
			System.out.println("Query NetworkNode " + nodeEntityId + " found " + result.size() + ", time=" + (t2-t1));
			// Should only return 1.
			OnecmdbUtils utils = new OnecmdbUtils(session);
			for (ICi node : result) {
				t1 = System.currentTimeMillis();
				QueryResult<ICi> ifs = utils.evaluate(node, "<$template{NetworkIF}", null, false);
				t2 = System.currentTimeMillis();
				System.out.println("Query NetworkIF for node" + nodeEntityId + " found " + ifs.size() + ", time=" + (t2-t1));
					
				for (ICi netIf: ifs) {
					resultCon.addAll(resolveConnection(session, netIf));
				}
			}
		} else {
			QueryCriteria<ICi> crit = new QueryCriteria<ICi>();
			crit.setOffspringOfAlias("NetworkIF");
			crit.setText(ifId);
			crit.setTextMatchValue(true);
			crit.setMatchAttributeAlias("ipAddress");
			QueryResult<ICi> result = modelsvc.query(crit);
			for (ICi netIf : result) {
				resultCon.addAll(resolveConnection(session, netIf));
			}
		}
		long stop = System.currentTimeMillis();
		System.out.println("Query NetworkConnnection for node=" + nodeEntityId + ", if=" + ifId + ", found " + resultCon.size() + ", time=" + (stop-start) + "ms");
		return(resultCon);
	}
	
	private List<ICi> resolveConnection(ISession session, ICi ifs) {
		List<ICi> result = new ArrayList<ICi>();
		OnecmdbUtils utils = new OnecmdbUtils(session);
		QueryCriteria nCrit = new QueryCriteria();
		nCrit.setOffspringDepth(new Integer(-1));
		
		// Query for Network connection...
		long t1 = System.currentTimeMillis();
		QueryResult<ICi> connections = utils.evaluate(ifs, "<$template{NetworkConnection}", nCrit, false);
		long t2 = System.currentTimeMillis();
		
		System.out.println("Query NetworkConnection for " + ifs.getDisplayName() + " found " + connections.size() + ", time=" + (t2-t1));
		for (ICi connCI : connections) {
			result.add(connCI);
			// Query for WanAccess
			t1= System.currentTimeMillis();
			QueryResult<ICi> access = utils.evaluate(connCI, "<$template{WANAccess}", null, false);
			t2 = System.currentTimeMillis();
			System.out.println("Query WAnAccess found " + access.size() + ", time=" + (t2-t1));
			for (ICi accCI : access) {
					
				// Query for Brukare...
				QueryResult<ICi> brukare = utils.evaluate(accCI, ">$attr{brukare}", null, false);
				for (ICi brukareCI : brukare) {
				}
			}
		}
		return(result);
	}


}
