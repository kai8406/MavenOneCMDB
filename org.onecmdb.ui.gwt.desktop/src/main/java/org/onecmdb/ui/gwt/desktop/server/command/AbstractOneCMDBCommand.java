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
package org.onecmdb.ui.gwt.desktop.server.command;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import org.jboss.cache.ConfigureException;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.CMDBWebServiceFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;

public abstract class AbstractOneCMDBCommand {
	private IOneCmdbContext onecmdb;
	private IOneCMDBWebService service;
	private String serviceURL = null;
	private String user = "admin";
	private String pwd = "123";
	private String token;
	private ICIMDR cimdr;

	public abstract void transfer(OutputStream out) throws Throwable;
	public abstract String getContentType();
	
	
	protected ICIMDR getCIMDR() {
		if (this.cimdr == null) {
			this.cimdr = (ICIMDR) CMDBWebServiceFactory.get().getOneCMDBCIMDR();
		}
		return(this.cimdr);
	}

	public IOneCMDBWebService getService() throws Exception {
		if (this.service == null) {
			if (this.onecmdb != null) {
				OneCMDBWebServiceImpl service = new OneCMDBWebServiceImpl();
				service.setOneCmdb(onecmdb);
				this.service = service;
			} else if (this.serviceURL != null) {
				this.service = OneCMDBServiceFactory.getWebService(getServiceURL());
			} else {
				this.service = CMDBWebServiceFactory.get().getOneCMDBWebService();
			}
		}
		return(this.service);
	}

	public String getServiceURL() {
		return(this.serviceURL);
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setService(IOneCMDBWebService service) {
		this.service = service;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() throws Exception {
		if (this.token == null) {
			this.token = getService().auth(getUser(), getPwd());
		}
		return token;
	}
	
	public void setContext(IOneCmdbContext onecmdb) {
		this.onecmdb = onecmdb;
	}

	
	
	// Helper functions.
	public CiBean getCI(String template, String alias) throws Exception {
		GraphQuery q = new GraphQuery();
		ItemAliasSelector aliasSel = new ItemAliasSelector("alias", template);
		aliasSel.setPrimary(true);
		aliasSel.setAlias(alias);
		q.addSelector(aliasSel);
		
		Graph result = getService().queryGraph(getToken(), q);
		
		result.buildMap();
		
		Collection<CiBean> beans = result.fetchAllNodeOffsprings();
		if (beans.size() == 1) {
			return(beans.iterator().next());
		}
		return(null);
	}
	
	public Collection<CiBean> queryCI(String template, String attrAlias, String attrValue) throws Exception {
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector offSel = new ItemOffspringSelector("alias", template);
		offSel.setPrimary(true);
		offSel.setMatchTemplate(false);
		offSel.setLimitToChild(false);
		AttributeValueConstraint aConstraint = new AttributeValueConstraint();
		aConstraint.setAlias(attrAlias);
		aConstraint.setValue(attrValue);
		aConstraint.setOperation(AttributeValueConstraint.EQUALS);
		offSel.applyConstraint(aConstraint);
		
		q.addSelector(offSel);
		
		Graph result = getService().queryGraph(getToken(), q);
		result.buildMap();
		Collection<CiBean> beans = result.fetchAllNodeOffsprings();
		return(beans);
	}
	
	// Helper main starter...
	public static void run(AbstractOneCMDBCommand cmd, String argv[]) {
		
	}
}
