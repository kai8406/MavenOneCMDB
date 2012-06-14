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
package org.onecmdb.ui.gwt.desktop.server.service.change;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.IContentDataAware;

public class OneCMDBWebServiceMDR implements ICIMDR, IContentDataAware {

	private IOneCMDBWebService service;
	private String id = "OneCMDB";

	public OneCMDBWebServiceMDR() {
	}
	
	public OneCMDBWebServiceMDR(IOneCMDBWebService service) {
		this.service = service;
	}
	public List<CiBean> getCIs(String token, List<String> aliases) {
		GraphQuery q = new GraphQuery();
		ItemAliasSelector alias = new ItemAliasSelector("alias", "Ci");
		alias.setAliases(aliases);
		alias.setPrimary(true);
	
		q.addSelector(alias);
		
		Graph g = query(token, q);
		
		Template t = g.fetchNode(alias.getId());
		if (t == null || t.getOffsprings() == null) {
			return(null);
		}
		return(t.getOffsprings());
	}
	
	public CiBean getCI(String token, String aliasName) {
		GraphQuery q = new GraphQuery();
		ItemAliasSelector alias = new ItemAliasSelector("alias", "Ci");
		alias.setAlias(aliasName);
		alias.setPrimary(true);
		
		q.addSelector(alias);
		
		Graph g = query(token, q);
		
		Template t = g.fetchNode(alias.getId());
		if (t == null || t.getOffsprings() == null) {
			return(null);
		}
		if (t.getOffsprings().size() == 1) {
			return(t.getOffsprings().get(0));
		}
		return(null);
	}

	public Graph query(String token, GraphQuery question) {
		try {
			return(this.service.queryGraph(token, question));
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalArgumentException("Can't query cmdb web service: " + t.getMessage(), t);
		}
	}


	public String getID() {
		return(this.id );
	}

	public void setContentData(ContentData data) {
		Properties p = (Properties) ContentParserFactory.get().getAdaptor(data, Properties.class);
		try {
			service = OneCMDBServiceFactory.getWebService(p.getProperty("oneCMDBwsdl"));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed CMDB URL<" + p.getProperty("oneCMDBwsdl") + ">", e);
		}
		this.id = data.getPath();
	}
	
	public IRfcResult update(String token, CiBean[] localBeans,
			CiBean[] baseBeans) {
		return(service.update(token, localBeans, baseBeans));
	}

	public List<CiBean> getCI(String token) {
		GraphQuery q = new GraphQuery();
		
		ItemOffspringSelector alias = new ItemOffspringSelector("alias", "Root");
		alias.setPrimary(true);
		
		q.addSelector(alias);
		
		Graph g = query(token, q);
		
		Template t = g.fetchNode(alias.getId());
		List<CiBean> beans = new ArrayList<CiBean>();
		
		if (t == null) { 
			return(null);
		}
		if (t.getTemplate() != null) {
			beans.add(t.getTemplate());
		}
		if (t.getOffsprings() != null) {
			for (CiBean bean : t.getOffsprings()) {
				beans.add(bean);
			}
		}
		return(beans);
	}

}
