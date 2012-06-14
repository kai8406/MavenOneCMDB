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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.IContentDataAware;

public class CIMDRBeanProvider implements ICIMDR, IContentDataAware  {
	IBeanProvider provider = null;
	private String id;
	
	public CIMDRBeanProvider() {
	}
	
	
	public String getID() {
		return(this.id);
	}


	protected IBeanProvider getProvider() {
		return provider;
	}


	protected void setProvider(IBeanProvider provider) {
		this.provider = provider;
	}


	public Graph query(String token, GraphQuery question) {
		Graph g = new Graph();
		for (ItemSelector sel : question.fetchOrderdItemSelectors()) {
			if (sel instanceof ItemOffspringSelector) {
				ItemOffspringSelector offsprings = (ItemOffspringSelector)sel;
				if (offsprings.getMatchTemplate()) {
					// Get all templates...
					Template t = new Template();
					t.setId(sel.getId());
					g.addNodes(t);
					for (CiBean bean : provider.getBeans()) {
						if (bean.isTemplate()) {
							t.addOffspring(bean);
						}
					}
					
				} else {
					// Get all instances...
					Template t = new Template();
					t.setId(sel.getId());
					g.addNodes(t);
					for (CiBean bean : provider.getBeans()) {
						if (!bean.isTemplate()) {
							t.addOffspring(bean);
						}
					}
				}
				
			}
		}
		return(g);
	}


	public CiBean getCI(String token, String alias) {
		return(provider.getBean(alias));
	}

	public void setContentData(ContentData data)  {
		XmlParser parser = new XmlParser();
		parser.setURL(ContentParserFactory.get().getURL(data).toExternalForm());
		setProvider(parser);
		
		this.id = (String) data.getPath();
	}


	public IRfcResult update(String token, CiBean[] localBeans,
			CiBean[] baseBeans) {
		RfcResult result = new RfcResult();
		result.setRejectCause("Not supported!");
		return(result);
	}


	public List<CiBean> getCI(String token) {
		return(this.provider.getBeans());
	}


	public List<CiBean> getCIs(String token, List<String> aliases) {
		List<CiBean> beans = new ArrayList<CiBean>();
		for (String alias : aliases) {
			CiBean bean = this.provider.getBean(alias);
			if (bean != null) {
				beans.add(bean);
			}
		}
		return(beans);
	}

	

}
