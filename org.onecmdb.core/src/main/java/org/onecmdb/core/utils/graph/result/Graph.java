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
package org.onecmdb.core.utils.graph.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.wsdl.RFCBean;

/**
 * <code>Graph</code> is the result from a graph query.
 * The graph is organized so the nodes and edges are group
 * by the templates that are derived from.
 * 
 */
public class Graph extends NamedItem {
	private static final long serialVersionUID = 1L;
	
	private transient HashMap<String, HashMap<String, Template>> edgeSources = new HashMap<String, HashMap<String, Template>>();
	private transient HashMap<String, HashMap<String, Template>> edgeTargets = new HashMap<String, HashMap<String, Template>>();
	private transient HashMap<String, CiBean> nodeMap = new HashMap<String, CiBean>();
	private transient HashMap<String, CiBean> edgeMap = new HashMap<String, CiBean>();
	private transient HashMap<String, String> templateIdMap = new HashMap<String, String>();
	private transient HashMap<Long, List<RFCBean>> rfcMap = new HashMap<Long, List<RFCBean>>();
	
	private List<Template> nodes = new ArrayList<Template>();
	private List<Template> edges = new ArrayList<Template>();
	
	private boolean mapBuild = false;
	
	public void addNodes(Template t) {
		this.nodes.add(t);
	}
	
	public void addEdges(Template t) {
		this.edges.add(t);
	}

	public List<Template> getNodes() {
		return(this.nodes);
	}

	public List<Template> getEdges() {
		return(this.edges);
	}


	public void setNodes(List<Template> templates) {
		this.nodes = templates;
	}

	public void setEdges(List<Template> edges) {
		this.edges = edges;
	}

	public Template fetchNode(String id) {
		for (Template t : this.nodes) {
			if (t.getId().equals(id)) {
				return(t);
			}
		}
		return(null);
	}

	public Template fetchEdge(String id) {
		for (Template t : this.edges) {
			if (t.getId().equals(id)) {
				return(t);
			}
		}
		return(null);
	}

		
	public CiBean findOffspringAlias(String alias) {
		return(this.nodeMap.get(alias));
	}
	
	public CiBean findEdgeBean(String alias) {
		return(this.edgeMap.get(alias));
	}

	
	public Collection<CiBean> fetchAllNodeOffsprings() {
		return(this.nodeMap.values());
	}
	
	public void buildMap() {
		for (Template template : this.nodes) {
			templateIdMap.put(template.getAlias(), template.getId());
			for (CiBean offspring : template.getOffsprings()) {
				nodeMap.put(offspring.getAlias(), offspring);
				templateIdMap.put(offspring.getAlias(), template.getId());
			}
			for (RFCBean rfc : template.getRFC()) {
				// Build rfcs map.
				Long id = rfc.getTargetCIId();
				if (id != null) {
					List<RFCBean> beans = rfcMap.get(id);
					if (beans == null) {
						beans = new ArrayList<RFCBean>();
						rfcMap.put(id, beans);
					}
					beans.add(rfc);
				}
			}
			
		}
		
		for (Template edge : this.edges) {
			HashMap<String, Template> targetMap = new HashMap<String, Template>();
			HashMap<String, Template> sourceMap = new HashMap<String, Template>();
			
			for (CiBean edgeBean : edge.getOffsprings()) {
				edgeMap.put(edgeBean.getAlias(), edgeBean);
				String sAlias = getValue(edgeBean, "source");
				String tAlias = getValue(edgeBean, "target");
				
				
				CiBean target = nodeMap.get(tAlias);
				CiBean source = nodeMap.get(sAlias);
				
				if (target == null || source == null) {
					continue;
				}
				Template sources = targetMap.get(target.getAlias());
				if (sources == null) {
					sources = new Template();
					sources.setId(templateIdMap.get(source.getAlias()));
					sources.setTemplate(fetchNode(sources.getId()).getTemplate());
					targetMap.put(target.getAlias(), sources);
				}
				sources.addOffspring(source);
			
				Template targets = sourceMap.get(source.getAlias());
				if (targets == null) {
					targets = new Template();
					targets.setId(templateIdMap.get(target.getAlias()));
					targets.setTemplate(fetchNode(targets.getId()).getTemplate());
					
					sourceMap.put(source.getAlias(), targets);
				}
				targets.addOffspring(target);
			}
			edgeSources.put(edge.getId(), sourceMap);
			edgeTargets.put(edge.getId(), targetMap);
			
		}
		mapBuild = true;
	}
	
	
	private String getValue(CiBean b, String alias) {
		ValueBean vBean = b.fetchAttributeValueBean(alias,0);
		if (vBean == null) {
			return(null);
		}
		return(vBean.getValue());
	}
	/**
	 * The direction indicates if the input bean is a source or a target.<br>
	 * The values are defined as follow
	 * <pre>
	 * RelationConstraint.SOURCE
	 * RelationConstraint.TARGET
	 * </pre> 
	 * @param bean
	 * @param direction
	 * @param refId
	 * @return
	 */
	public Template fetchReference(CiBean bean, int direction, String refId) {
		if (bean == null) {
			return(new Template());
		}
		
		if (!mapBuild) {
			buildMap();
		}
		
		HashMap<String, Template> map = null;
		
		if (direction == RelationConstraint.SOURCE) {
			map = edgeSources.get(refId);
		} else {
			map = edgeTargets.get(refId);
		}
		if (map == null) {
			return(new Template());
		}
		Template t = map.get(bean.getAlias());
		if (t == null) {
			t = new Template();
		}
		return(t);
	}

	public List<RFCBean> fetchRFCs(CiBean bean) {
		List<RFCBean> beans = rfcMap.get(bean.getId());
		if (beans == null) {
			return(Collections.EMPTY_LIST);
		}
		return(beans);
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Graph:\n");
		
		for (Template t : getNodes()) {
			b.append("\tNode<" + t.getId() + ">{" + 
					"offsprings=" + t.getOffsprings().size() + 
					", rfcs=" + t.getRFC().size() + 
					", txs=" + t.getTransactions().size() + 
					"} totalCount=" + t.getTotalCount() + 
					"\n");
		}
		
		for (Template t : getEdges()) {
			b.append("\tEdge<" + t.getId() + ">{" + 
					"offsprings=" + t.getOffsprings().size() + 
					", rfcs=" + t.getRFC().size() + 
					", txs=" + t.getTransactions().size() +
					"} totalCount=" + t.getTotalCount() +
					"\n");
		}
		return(b.toString());
	}

	

}
