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
package org.onecmdb.ui.gwt.toolkit.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberInputStream;
import java.io.LineNumberReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.jasper.compiler.SmapStratum.LineInfo;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.logicalcobwebs.asm.tree.LineNumberNode;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.BeanClassInjector;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.TransformEngine;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.WSDLBeanProvider;
import org.onecmdb.ui.gwt.toolkit.client.IOneCMDBGWTService;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RBACSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.Relation;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_GraphQuery;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result.GWT_Graph;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class OneCMDBGWTService extends RemoteServiceServlet implements IOneCMDBGWTService {
	
	private String oneCMDB_Web_URL = null;
	//private IOneCMDBWebService cmdbWebService = null;
	HashMap<String, String> clientURLMap = new HashMap<String, String>();
	HashMap<String, IOneCMDBWebService> webServiceMap= new HashMap<String, IOneCMDBWebService>();
	
	public String getCurrentOneCMDB_WSDL() {
		String url = clientURLMap.get(getClientUniqueKey());
		if (url == null) {
			return(getDefaultOneCMDBServiceURL());
		}
		return(url);
	}
	

	public void setCurrentOneCMDB_WSDL(String url) throws Exception {
		if (url == null) {
			throw new Exception("OneCMDB WSDL URL can not be null!");
		}
		// Don't need to pollute the hashmap.
		if (url.equals(getDefaultOneCMDBServiceURL())) {
			return;
		}
		
		// Validate URL...
		IOneCMDBWebService service = createWebService(url);
		webServiceMap.put(url, service);
		clientURLMap.put(getClientUniqueKey(), url);
	}
	
	private String getClientUniqueKey() {
		// Should use some cookie or something unique per browser!
		HttpServletRequest req = getThreadLocalRequest();
		String key = req.getRemoteAddr();
		return(key);
	}
	
	public void setDefaultOneCMDBServiceURL(String url) {
		this.oneCMDB_Web_URL = url;
	}
	
	public String getDefaultOneCMDBServiceURL() {
		if (oneCMDB_Web_URL == null) {
			String host = null;
			try {
				ClassLoader cl = this.getClass().getClassLoader();
				LineNumberReader lin = new LineNumberReader(
						new InputStreamReader(
								cl.getResourceAsStream("defaultOneCMDB_WSDL_Host.txt")));
					host = lin.readLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (host != null && host.length() > 0) {
				oneCMDB_Web_URL = "http://" + host + "/webservice/onecmdb";
			} else {
				oneCMDB_Web_URL = "http://localhost:8080/webservice/onecmdb";
			}
					
		}
		return(oneCMDB_Web_URL);
	}
	protected IOneCMDBWebService createWebService(String url) throws Exception {
		Service serviceModel = new ObjectServiceFactory().create(IOneCMDBWebService.class);
		
		IOneCMDBWebService cmdbWebService = (IOneCMDBWebService)
		    	new XFireProxyFactory().create(serviceModel, url);
		
		return(cmdbWebService);		
	}
	
	protected IOneCMDBWebService getWebService() throws Exception {
		String clientKey = getClientUniqueKey();
		String url = clientURLMap.get(clientKey);
		if (url == null) {
			url = getDefaultOneCMDBServiceURL();
		}
		IOneCMDBWebService cmdbWebService = webServiceMap.get(url);
		if (cmdbWebService == null) {
			cmdbWebService = createWebService(url);
			webServiceMap.put(url, cmdbWebService);
		}
		return(cmdbWebService);
	}
	
	public String auth(String username, String pwd) throws Exception {
		try {
			String auth = getWebService().auth(username, pwd);
			return(auth);
		} catch (Exception e) {
			return(null);
		}
	}
	
	public void logout(String authToken) throws Exception {
		getWebService().logout(authToken);
	}

	public GWT_CiBean[] evalRelation(String auth, GWT_CiBean gwtSource, String relationPath, GWT_QueryCriteria gwtCrit) throws Exception {
		
		// Transform input
		CiBean source = GWT_Translator.convert(gwtSource);
		QueryCriteria crit = GWT_Translator.convert(gwtCrit);
		
		// Make call
		CiBean bean[] = getWebService().evalRelation(auth, source, relationPath, crit);
		
		// Transform result
		GWT_CiBean[] result = GWT_Translator.convert(bean);
		return(result);
	}

	public int evalRelationCount(String auth, GWT_CiBean gwtRoot, String relationPath, GWT_QueryCriteria gwtCrit) throws Exception {
		CiBean root = GWT_Translator.convert(gwtRoot);
		QueryCriteria crit = GWT_Translator.convert(gwtCrit);
		
		int count = getWebService().evalRelationCount(auth, root, relationPath, crit);
		
		return(count);
	}

	public String[] findRelation(String auth, GWT_CiBean gwtSource, GWT_CiBean gwtTarget) throws Exception {
		CiBean source = GWT_Translator.convert(gwtSource);
		CiBean target = GWT_Translator.convert(gwtTarget);
		
		String[] result = getWebService().findRelation(auth, source, target);
		
		return(result);
	}


	public GWT_CiBean[] query(String auth, String xPath, String attributes) throws Exception {
		CiBean beans[] = getWebService().query(auth, xPath, attributes);
		GWT_CiBean gwtBeans[] = GWT_Translator.convert(beans);
		return(gwtBeans);
	}

	public GWT_CiBean[] search(String auth, GWT_QueryCriteria gwtCriteria) throws Exception {
		long start = System.currentTimeMillis();
		QueryCriteria crit = GWT_Translator.convert(gwtCriteria);
		CiBean beans[] = getWebService().search(auth, crit);
		GWT_CiBean gwtBeans[] = GWT_Translator.convert(beans);
		
		long stop = System.currentTimeMillis();
		System.out.println("Search[" + (stop-start) + "ms][" + gwtBeans.length + "] " + gwtCriteria);
		
		return(gwtBeans);
	}
	

	public int searchCount(String auth, GWT_QueryCriteria gwtCriteria) throws Exception {
		QueryCriteria crit = GWT_Translator.convert(gwtCriteria);
		int count = getWebService().searchCount(auth, crit);
		return(count);
		
	}

	public GWT_RfcResult update(String auth, GWT_CiBean[] gwtLocalBeans, GWT_CiBean[] gwtBaseBeans) throws Exception {
		CiBean localBeans[] = GWT_Translator.convert(gwtLocalBeans);
		CiBean baseBeans[] = GWT_Translator.convert(gwtBaseBeans);
		
		IRfcResult result = getWebService().update(auth, localBeans, baseBeans);
		
		GWT_RfcResult gwtResult = GWT_Translator.convert(result);
		
		return(gwtResult);
	}


	public String newInstanceAlias(String token, String templateAlias) throws Exception {
		String alias = getWebService().newInstanceAlias(token, templateAlias);
		return(alias);
	}

	public GWT_CiBean getAuthAccount(String token) throws Exception {
		CiBean bean = getWebService().getAuthAccount(token);
		GWT_CiBean gwtBean = GWT_Translator.convert(bean);
		return(gwtBean);
	}


	public GWT_RBACSession getRBACSession(String token) throws Exception {
		RBACSession rbac = getWebService().getRBACSession(token);
		GWT_RBACSession gwtRBAC = GWT_Translator.convert(rbac);
		return(gwtRBAC);
	}


	public GWT_CiBean[] transform(String token, String dsAlias, String dataSourceAlias) throws Exception {
		WSDLBeanProvider provider = new WSDLBeanProvider(getWebService(), token);
		CiBean dsBean = provider.getBean(dsAlias);
		if (dsBean == null) {
			throw new Exception("Data Set name <" + dsAlias + "> not found!");
		}
		CiBean dataSourceBean = provider.getBean(dataSourceAlias);
		if (dataSourceBean == null) {
			throw new Exception("Data Source name <" + dataSourceAlias + "> not found!");
		}
		
		long start = System.currentTimeMillis();
		BeanClassInjector injector = new BeanClassInjector();
		injector.setBeanProvider(provider);
		Object dsObject = injector.beanToObject(dsBean);
		long t1 = System.currentTimeMillis();
			
		injector.setBeanProvider(provider);
		Object dataSourceObject = injector.beanToObject(dataSourceBean);
		
		long t2 = System.currentTimeMillis();
		
		if (dsObject instanceof DataSet && dataSourceObject instanceof IDataSource) {
			IDataSource dataSource = null;
			try {
				dataSource = (IDataSource) dataSourceObject;
				DataSet dataSet = (DataSet)dsObject;
				dataSet.setDataSource(dataSource);
				TransformEngine engine = new TransformEngine();
				IBeanProvider result = engine.transform(provider, dataSet);
				long t3 = System.currentTimeMillis();
				// Convert all beans!!!!!!
				GWT_CiBean gwtBeans[] = new GWT_CiBean[result.getBeans().size()];
				int i = 0;
				for (CiBean bean : result.getBeans()) {
					gwtBeans[i] = GWT_Translator.convert(bean);
					i++;
				}
				long t4 = System.currentTimeMillis();
				System.out.println("TIMES: " 
						+ (t1-start) + "ms, "  
						+ (t2-t1) + "ms," 
						+ (t3-t2) + "ms," 
						+ (t4-t3) + "ms,"
						+ (t4-t3) + "ms"
						+ "total=" + (t4-start) + "ms"
						);
							
				return(gwtBeans);
			} finally {
				if (dataSource != null) {
					try {
						dataSource.close();
					} catch (IOException e) {
						// Silently ignore.
					}
			
				}
			}
		}
		throw new Exception("Illegal argument!");
	}

	
	public GWT_Graph queryGraph(String token, GWT_GraphQuery q) throws Exception {
		System.out.println("Query:....");
		GWT_Graph result = new GWT_Graph();
		// Convert objects...
		try {
			GWT_GraphTranslator tr = new GWT_GraphTranslator();
			GraphQuery query = tr.convert(q); 
			long start = System.currentTimeMillis();
			Graph g = getWebService().queryGraph(token, query);
			long stop = System.currentTimeMillis();
			System.out.println("GWT-Server:queryGraph " + (stop-start) + "ms");
			//Convert Graph to GWT_Graph...
			result = tr.convert(g);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return(result);
	}


	public GWT_RfcResult startJob(String token, GWT_CiBean bean)
			throws Exception {
		System.out.println("startJob:....");
		// Convert objects...
		GWT_RfcResult result = null;
		try {
			GWT_Translator tr = new GWT_Translator();
			CiBean job = tr.convert(bean); 
			long start = System.currentTimeMillis();
			IRfcResult r = getWebService().startJob(token,job);
			long stop = System.currentTimeMillis();
			System.out.println("startJob " + (stop-start) + "ms");
			//Convert Graph to GWT_Graph...
			result = tr.convert(r);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return(result);
	}
	
	public GWT_RfcResult cancelJob(String token, GWT_CiBean bean)
	throws Exception {
		System.out.println("startJob:....");
		//	Convert objects...
		GWT_RfcResult result = null;
		try {
			GWT_Translator tr = new GWT_Translator();
			CiBean job = tr.convert(bean); 
			long start = System.currentTimeMillis();
			IRfcResult r = getWebService().cancelJob(token,job);
			long stop = System.currentTimeMillis();
			System.out.println("startJob " + (stop-start) + "ms");
			//Convert Graph to GWT_Graph...
			result = tr.convert(r);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return(result);
	}

	private CiBean getAlias(String token, String alias) throws Exception {
		if (alias == null || alias.length() == 0) {
			return(null);
		}
	
		QueryCriteria crit = new QueryCriteria();
		crit.setCiAlias(alias);
		CiBean[] beans = getWebService().search(token, crit);
		if (beans == null) {
			return(null);
		}
		if (beans.length == 0) {
			return(null);
		}
		return(beans[0]);
	}
	private CiBean findAlias(Graph graph, String token, String alias) throws Exception {
		CiBean bean = graph.findOffspringAlias(alias);
		if (bean != null) {
			return(bean);
		}
		return(getAlias(token, alias));
	}
	
	public Relation[] getRelations(String authToken, String source, String root) throws Exception {
		// Query all templates.
		GraphQuery query = new GraphQuery();
		ItemOffspringSelector sel = new ItemOffspringSelector("sel", root);
		sel.setMatchTemplate(true);
		sel.setPrimary(true);
		query.addSelector(sel);
		
		Graph graph = getWebService().queryGraph(authToken, query);
		graph.buildMap();
		CiBean sourceBean = findAlias(graph, authToken, source);
	
		List<Relation> relations = new ArrayList<Relation>();
		if (sourceBean != null) {
			
			GWT_Translator tr = new GWT_Translator();
	
			for (AttributeBean aBean : sourceBean.getAttributes()) {
				if (aBean.isComplexType()) {
					CiBean target = graph.findOffspringAlias(aBean.getType());
					CiBean relation = getAlias(authToken, aBean.getRefType());
					Relation rel = new Relation();
					rel.setCenter(tr.convert(sourceBean));
					rel.setReferred(tr.convert(target));
					rel.setRelationType(tr.convert(relation));
					rel.setAttribute(tr.convert(aBean));
					rel.setDirection(Relation.CENTER_SOURCE);
					relations.add(rel);
				}
			}
			
			// Loop through all offsprings and check if it matches.
			for (CiBean node : graph.fetchAllNodeOffsprings()) {
				if (node.equals(sourceBean)) {
					continue;
				}
				for (AttributeBean aBean : node.getAttributes()) {
					if (aBean.isDerived()) {
						continue;
					}
					if (aBean.isComplexType()) {
						if (isDerived(graph, sourceBean, aBean.getType())) {
							CiBean relation = getAlias(authToken, aBean.getRefType());
							Relation rel = new Relation();
							rel.setReferred(tr.convert(node));
							rel.setCenter(tr.convert(sourceBean));
							rel.setRelationType(tr.convert(relation));
							rel.setAttribute(tr.convert(aBean));
							rel.setDirection(Relation.CENTER_TARGET);
							relations.add(rel);
						}
					}
				}
			}
		}
		return(relations.toArray(new Relation[0]));
	}


	


	private boolean isDerived(Graph graph, CiBean source, String type) {
		if (source == null) {
			return(false);
		}
		if (source.getAlias().equals(type)) {
			return(true);
		}
		if (source.getDerivedFrom() != null) {
			if (source.getDerivedFrom().equals(type)) {
				return(true);
			}
			return(isDerived(graph, graph.findOffspringAlias(source.getDerivedFrom()), type));
		}
		return(false);
	}

}
