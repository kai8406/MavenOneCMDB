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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;

public class CIMDRCollection implements ICIMDR {
	private List<ICIMDR> mdrs = new ArrayList<ICIMDR>();
	private String id;
	
	public CIMDRCollection(String id) {
		this.id = id;
	}
	
	public void add(ICIMDR mdr) {
		this.mdrs.add(mdr); 
	}
	
	public List<ICIMDR> getMDRs() {
		return(mdrs);
	}
	
 	public CiBean getCI(String token, String alias) {
 		// Handle multiple beans...
 		CiBean bean = null;
 		for (ICIMDR mdr : mdrs) {
 			CiBean foundBean = mdr.getCI(token, alias);
 			if (foundBean == null) {
 				continue;
 			}
 			if (bean != null) {
 				merge(bean, foundBean);
 			} else {
 				bean = foundBean;
 			}
 		}
 		return(bean);
 	}

 	private void merge(CiBean to, CiBean from) {
 	
		for (AttributeBean aBean : from.getAttributes()) {
			if (to.getAttribute(aBean.getAlias()) == null) {
				to.addAttribute(aBean);
			}
		}
		
		
		for (ValueBean vBean : from.getAttributeValues()) {
			if (to.fetchAttributeValueBeans(vBean.getAlias()).size() == 0) {
				to.addAttributeValue(vBean);
			}
		}
	}

	public Graph query(String token, GraphQuery question) {
 		Graph result = new Graph();
 		for (ICIMDR mdr : mdrs) {
 			Graph mdrResult = mdr.query(token, question);
 			updateGraph(result, mdrResult);
 		}
 		return(result);
	}

	private void updateGraph(Graph result, Graph mdrResult) {
		for (Template t : mdrResult.getNodes()) {
			Template r = result.fetchNode(t.getId());
			if (r == null) {
				result.addNodes(t);
			} else {
				for (CiBean b : t.getOffsprings()) {
					int index = r.getOffsprings().indexOf(b);
					if (index >= 0) {
						CiBean exists = r.getOffsprings().get(index);
						merge(exists, b);
					} else {
						r.addOffspring(b);
					}
				}
			}
		}
		for (Template t : mdrResult.getEdges()) {
			Template r = result.fetchEdge(t.getId());
			if (r == null) {
				result.addEdges(t);
			} else {
				for (CiBean b : t.getOffsprings()) {
					int index = r.getOffsprings().indexOf(b);
					if (index >= 0) {
						CiBean exists = r.getOffsprings().get(index);
						merge(exists, b);
					} else {
						r.addOffspring(b);
					}
				}
			}
		}
	}

	public String getID() {
		return(this.id);
		
	}
	
	public IRfcResult update(String token, CiBean[] localBeans,
			CiBean[] baseBeans) {
		IRfcResult result = null;
		for (ICIMDR mdr : mdrs) {
 			result = mdr.update(token, localBeans, baseBeans);
 			if (result.isRejected()) {
 				return(result); 			
 			}
		}
		return(result);
 	}

	public List<CiBean> getCI(String token) {
		HashMap<String, CiBean> map = new HashMap<String, CiBean>();
		//List<CiBean> beans = new ArrayList<CiBean>();
		for (ICIMDR mdr : mdrs) {
			List<CiBean> beans = mdr.getCI(token);
			for (CiBean bean : beans) {
				CiBean added = map.get(bean.getAlias());
				if (added != null) {
					merge(added, bean);
				} else {
					map.put(bean.getAlias(), bean);
				}
			}
		}
		return(new ArrayList<CiBean>(map.values()));
	}

	public List<CiBean> getCIs(String token, List<String> aliases) {
		// Handle multiple beans...
 		List<CiBean> allBeans = new ArrayList<CiBean>();
		for (ICIMDR mdr : mdrs) {
 			List<CiBean> beans = mdr.getCIs(token, aliases);
 			for (CiBean bean : beans) {
 				int index = allBeans.indexOf(bean);
 				if (index < 0) {
 					allBeans.add(bean);
 				} else {
 					CiBean oldBean = allBeans.get(index);
 					merge(oldBean, bean);
 				}
 			}
 		}
 		return(allBeans);
	}

	

}
