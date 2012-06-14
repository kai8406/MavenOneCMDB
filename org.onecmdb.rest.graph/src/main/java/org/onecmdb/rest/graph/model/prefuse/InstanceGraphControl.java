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
package org.onecmdb.rest.graph.model.prefuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.rest.graph.io.OneCMDBConnection;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.visual.VisualItem;

public class InstanceGraphControl {
	GraphQuery q = new GraphQuery();
	
	prefuse.data.Graph g = new prefuse.data.Graph(true);

	private Graph result;

	private boolean excludeRelation = true;

	private prefuse.data.Graph queryGraph = new prefuse.data.Graph(true);

	private HashMap<String, Node> queryGraphNodeMap = new HashMap<String, Node>();

	private Integer maxSize = 100;

	private String group;

	private HashMap<String, Boolean> visualMap = new HashMap<String, Boolean>();
	private HashMap<String, Aggregate> aggregateMap = new HashMap<String, Aggregate>();
	private HashMap<String, Node> nodeMap = new HashMap<String, Node>();

	private HashMap<String, String> searchMap = new HashMap<String, String>();

	private long loadTime;
	

	class Aggregate {
		HashSet<String> members = new HashSet<String>();
		
		
		public Aggregate() {
			super();
		}

		public void addMember(String m) {
			members.add(m);
		}

		public Set<String> getMembers() {
			return(members);
		}

		public String getId() {
			return("Aggr-" + this.hashCode());
		}
	}
	
	
	public InstanceGraphControl() {
		// Add Model
		g.addColumn("alias", String.class);
		g.addColumn("type", String.class);
		g.addColumn("mark", Boolean.class);
		g.addColumn("name", String.class);
		g.addColumn("aggregate", boolean.class);
		
		// On Edge
		g.addColumn("springCoefficient", float.class);
		g.addColumn("springLength", float.class);
		g.addColumn("visible", boolean.class);
	
		// On Node
		g.addColumn("massValue", float.class);
		g.addColumn("image", String.class);
		
		
		// Add Model
		queryGraph.addColumn("alias", String.class);
		queryGraph.addColumn("type", String.class);
		queryGraph.addColumn("mark", Boolean.class);
		queryGraph.addColumn("name", String.class);
		queryGraph.addColumn("id", String.class);
		queryGraph.addColumn("image", String.class);
		
	}

	public prefuse.data.Graph getGraph() {
		return(g);
	}
	
	protected ItemSelector addItem(String id, String alias, boolean primary, boolean instance) {
		ItemSelector selector = q.findSelector(id);
		if (selector == null) {
			if (instance) {
				selector = new ItemAliasSelector(id, "Root");
				((ItemAliasSelector)selector).setAlias(alias);
			} else {
				selector = new ItemOffspringSelector(id, alias);
				((ItemOffspringSelector)selector).setMatchTemplate(Boolean.FALSE);
				((ItemOffspringSelector)selector).setLimitToChild(false);
			}
			selector.setPrimary(primary);
			
			q.addSelector(selector);
			
		
			
			// Update query Graph.
			//CiBean template = OneCMDBConnection.instance().getBeanFromAlias(alias);
			Node n = queryGraph.addNode();
			n.setString("id",id);
			n.setString("name", alias);
			n.setString("alias", alias);
			queryGraphNodeMap.put(id, n);
		}
		
		
		return(selector);
	}
	
	private ItemConstraint getSecurityConstraint() {
		if (group == null || group.length() == 0) {
			return(null);
		}
		String groups[] = group.split("\\|");
		if (groups.length == 1) {
			ItemSecurityConstraint security = new ItemSecurityConstraint();
			security.setGroupName(this.group);
			return(security);
		}
		ItemOrGroupConstraint or = new ItemOrGroupConstraint();

		for (int i = 0; i < groups.length; i++) {
			ItemSecurityConstraint security = new ItemSecurityConstraint();
			security.setGroupName(groups[i]);
			or.add(security);
		}
		return(or);
	}
	
	
	public void setSecurityGroup(String alias) {
		this.group = alias;
	}
	
	public boolean removeItem(String id) {
		ItemSelector selector = q.findSelector(id);
		if (selector == null) {
			return(false);
		}
		
		// Update Graph.
		if (selector instanceof ItemRelationSelector) {
			
			Iterator iter = queryGraph.edges();
			while (iter.hasNext()) {
				Edge e = (Edge)iter.next();
				if (e.getString("id").equals(id)) {
					queryGraph.removeEdge(e);
				}
			}
		} else {
			Iterator iter = queryGraph.nodes();
			while (iter.hasNext()) {
				Node n = (Node)iter.next();
				if (n.getString("id").equals(id)) {
					queryGraph.removeNode(n);
				}
			}
			queryGraphNodeMap.remove(id);
		}
		// Remove Selector, will also remove reference selectors.
		return(q.removeSelector(selector));
	}
	
	public void setRoot(String template, String instanceAlias, boolean primary,
			boolean visable) {
		if (instanceAlias == null) {
			addNode(template, primary, visable);
		} else {
			addItem(instanceAlias, instanceAlias, primary, true);
			visualMap.put(instanceAlias, visable);
			System.out.println("AddInstance alias=" + instanceAlias + ", visable=" + visable);
		}
	}

	
	public void addNode(String alias, boolean primary, boolean visable) {
		addItem(alias, alias, primary, false);
		visualMap.put(alias, visable);
		System.out.println("AddNode alias=" + alias + ", visable=" + visable);
	}
	
	public void removeNode(String alias) {
		removeItem(alias);
		
		/*
		// Remove all relations.
		List<ItemRelationSelector> rels = new ArrayList<ItemRelationSelector>(q.fetchRelationSelectors());
		for (ItemRelationSelector rel : rels) {
			if (rel.getSource().equals(alias)) {
				q.removeSelector(rel);
			}
			if (rel.getTarget().equals(alias)) {
				q.removeSelector(rel);
			}
		}
		*/
	}

	
	public ItemRelationSelector addRelation(String sourceId, String refType, String targetId) {
		String relId = sourceId + "2" + targetId;
		
		ItemSelector source = q.findSelector(sourceId);
		if (source == null) {
			throw new IllegalArgumentException("Source " + sourceId +" item is missing");
		}
		ItemSelector target = q.findSelector(targetId);
		if (target == null) {
			throw new IllegalArgumentException("target " + sourceId +" item is missing");
		}
		
		ItemSelector relation = q.findSelector(relId);
		if (relation == null) {
			relation = new ItemRelationSelector(relId, refType, target.getId(), source.getId());
			q.addSelector(relation);
			if (excludeRelation) {
				((ItemRelationSelector)relation).setMandatory(false);
			}
			// Add relation...
			Node sNode = queryGraphNodeMap.get(sourceId);
			Node tNode = queryGraphNodeMap.get(targetId);
			if (sNode != null && tNode != null) {
				Edge e = queryGraph.addEdge(sNode, tNode);
				e.setString("alias", refType);
				e.setString("id", relId);
				
			}
		}
		return((ItemRelationSelector)relation);
	}
	
	public boolean removeRelation(String sourceId, String refType, String targetId) {
		String relId = sourceId + "2" + targetId;
		ItemSelector relation = q.findSelector(relId);
		if (relation != null) {
			q.removeSelector(relation);
			return(true);
		}
		return(false);
	}	
	
	
	public void update() {
		// Update security constraint.
		ItemConstraint security = getSecurityConstraint();
		List<ItemSelector> sels = q.fetchSelectors();
		if (sels != null) {
			for (ItemSelector sel : sels) {
				if (sel instanceof ItemRelationSelector) {
					ItemRelationSelector rel = (ItemRelationSelector)sel;
					rel.setMandatory(!excludeRelation);
				}
				if (sel.isPrimary()) {
					sel.applyConstraint(security);
				}
				String search = searchMap.get(sel.getId());
				if (search == null || search.length() == 0) {
					sel.applyConstraint(null);
				} else {		
					AttributeValueConstraint aCon = getValueConstrain(search);
					sel.applyConstraint(aCon);
				}
				sel.setPageInfo(new PageInfo(0, maxSize ));
			}
		}
		loadTime = 0;
		if (sels != null && sels.size() > 0) {
			if (q.fetchPrimarySelectors() == null) {
				q.fetchSelectors().get(0).setPrimary(true);
			}
			long start = System.currentTimeMillis();
			result = OneCMDBConnection.instance().query(q);
			long stop = System.currentTimeMillis();
			loadTime = stop-start;
			System.out.println(result.toString());
			result.buildMap();
		} else {
			result = new Graph();
			result.buildMap();
		}
	}
	
	public void queryCMDB(GraphQuery q) {
		long start = System.currentTimeMillis();
		result = OneCMDBConnection.instance().query(q);
		long stop = System.currentTimeMillis();
		loadTime = stop-start;
		System.out.println(result.toString());
		result.buildMap();
	}
	
	private AttributeValueConstraint getValueConstrain(String search) {
		String alias = null;
		int op = AttributeValueConstraint.LIKE;
		search = search.trim();
		if (search.contains("==")) {
			String split[] = search.split("==");
			alias = split[0];
			search = split[1];
		}
		
		if (search.startsWith("\"") && search.endsWith("\"")) {
			op = AttributeValueConstraint.EQUALS;
			search=search.replace('"',' ');
			search = search.trim();
		} else {
			if (search.contains("*")) {
				search = search.replace("*", "%");
			} else {
				search = "%" + search + "%";
			}
		}
		
		AttributeValueConstraint aCon = new AttributeValueConstraint();
		aCon.setOperation(op);
		aCon.setValue(search);
		aCon.setAlias(alias);
		
		return(aCon);
	}

	public Graph getResult() {
		return(this.result);
	}
	
	public void updateGraph() {
		HashMap<String, CiBean> nodeBeans = new HashMap<String, CiBean>();
		HashMap<String, CiBean> edgeBeans = new HashMap<String, CiBean>();
			
		for (Template t : result.getNodes()) {
			for (CiBean bean : t.getOffsprings()) {
				nodeBeans.put(bean.getAlias(), bean);
			}
		}
		for (Template t : result.getEdges()) {
			for (CiBean bean : t.getOffsprings()) {
				edgeBeans.put(bean.getAlias(), bean);
			}
		}
		
		// Syncronize the graph from concurrrent modifications.
		synchronized(g) {
		
			//HashMap<String, Node> nodeMap = new HashMap<String, Node>();
			// handle update of nodes.
			List<Node> removeNode = new ArrayList<Node>();
			
			Iterator nIter = g.getNodes().tuples();
			while (nIter.hasNext()) {
				Object o = nIter.next();
				if (o instanceof Tuple) {
					Tuple t = (Tuple) o;
				
					if (t.canGetString("alias")) {
						String alias = t.getString("alias");
						if (result.findOffspringAlias(alias) == null) {
							System.out.println("Remove Node " + alias);
							Node n = g.getNode(t.getRow());
							System.out.println("\t" + n);
							removeNode.add(n);
							nodeMap.remove(alias);
						} else {
							//nodeMap.put(alias, g.getNode(t.getRow()));
							if (nodeBeans.containsKey(alias)) {
								nodeBeans.remove(alias);
							}

						}
					}
				}
			}
			
			List<Edge> removeEdge = new ArrayList<Edge>();
			Iterator eIter = g.getEdges().tuples();
			while (eIter.hasNext()) {
				Object o = eIter.next();
				if (o instanceof Tuple) {
					Tuple t = (Tuple)o;
					if (t.canGetString("alias")) {
						String alias = t.getString("alias");
						if (result.findEdgeBean(alias) == null) {
							System.out.println("Remove Edge " + alias);
							Edge e = g.getEdge(t.getRow());
							System.out.println("\t" + e);
							removeEdge.add(e);
						}
						if (edgeBeans.containsKey(alias)) {
							edgeBeans.remove(alias);
						}
					}
				}
			}
			
			
			// Start update the graph
			// remove first.
			for (Edge e : removeEdge) {
				g.removeEdge(e);
				
			}
			for (Node n : removeNode) {
				g.removeNode(n);
			}
			
			if (false) {
			// Handle aggregates.
			for (CiBean bean : edgeBeans.values()) {
				String sourceAlias = getSource(bean);
				if (sourceAlias == null) {
					continue;
				}
				String targetAlias = getTarget(bean);
				if (targetAlias == null) {
					continue;
				}
				CiBean target = result.findOffspringAlias(targetAlias);
				CiBean source = result.findOffspringAlias(sourceAlias);
				
				boolean targetVisible = visualMap.get(target.getDerivedFrom());
				boolean sourceVisible = visualMap.get(source.getDerivedFrom());
				
			
				if (!targetVisible && !sourceVisible) {
					Aggregate sourceAggregate = getAggregate(sourceAlias);
					Aggregate targetAggregate = getAggregate(targetAlias);
					// Create aggregated group.
					if (sourceAggregate == null && targetAggregate == null) {
						Aggregate a = new Aggregate();
						a.addMember(sourceAlias);
						a.addMember(targetAlias);
						aggregateMap.put(sourceAlias, a);
						aggregateMap.put(targetAlias, a);
					} else if (sourceAggregate == null && targetAggregate != null) {
						targetAggregate.addMember(sourceAlias);
					} else if (sourceAggregate != null && targetAggregate == null) {
						sourceAggregate.addMember(targetAlias);
					} else {
						// Combine aggreagtes.
						combineAggregate(sourceAggregate, targetAggregate);
					}
				} else if (!sourceVisible) {
					// pointing to aggregate group.
					Aggregate a = new Aggregate();
					a.addMember(sourceAlias);
					aggregateMap.put(sourceAlias, a);
				} else if (!targetVisible) {
					Aggregate a = new Aggregate();
					a.addMember(targetAlias);
					aggregateMap.put(targetAlias, a);
				}
			}
			}
			
			// Add all nodes without releations.
			for (CiBean bean : nodeBeans.values()) {
				System.out.println("Add Node: " + bean.getAlias());
				Node n = addNode(bean);
			}
			for (CiBean bean : edgeBeans.values()) {
				String source = getSource(bean);
				if (source == null) {
					continue;
				}
				String target = getTarget(bean);
					
				if (target == null) {
					continue;
				}
				System.out.println("Add Edge: " + source + "-->" + target);
				Node sourceNode = addNode(result.findOffspringAlias(source));
				Node targetNode = addNode(result.findOffspringAlias(target));
				Edge edge = addEdge(sourceNode, bean, targetNode);
				handleLength(edge, result.findOffspringAlias(source), bean, result.findOffspringAlias(target));
			
			}
			
			/*
			for (CiBean bean : nodeBeans.values()) {
				Node n = g.addNode();
				n.set("alias", bean.getAlias());
				n.set("type", bean.getDerivedFrom());
				String displayName = bean.getDisplayName();
				if (displayName.length() > 20) {
					displayName = displayName.substring(0, 20) + "...";
				}
				n.set("name", displayName);
				n.set("image", "http://localhost:8080/icons/generate?iconid=" + getValue(bean, "icon"));
				nodeMap.put(bean.getAlias(), n);
			}
			
			for (CiBean bean : edgeBeans.values()) {
				String sourceAlias = getSource(bean);
				String targetAlias = getTarget(bean);
				Node source = nodeMap.get(sourceAlias);
				Node target = nodeMap.get(targetAlias);
				if (source == null) {
					continue;
				}
				if (target == null) {
					continue;
				}
				Edge e = g.addEdge(source, target);
				e.set("alias", bean.getAlias());
				e.set("type", bean.getDerivedFrom());
				e.set("name", bean.getDerivedFrom());
			}
			*/
		} // End of synchronized graph modification.	
	}

	private void handleLength(Edge edge, CiBean source, CiBean reference, CiBean target) {
		/*
		if (reference.getDerivedFrom().equals("BelongsTo")) {
			edge.setFloat("springLength", 1);
			edge.setFloat("springCoefficient", 1e-4f);
			edge.setBoolean("visible", false);
		}
		*/
	}

	private void combineAggregate(Aggregate sourceAggregate,
			Aggregate targetAggregate) {
		for (String member: targetAggregate.getMembers()) {
			sourceAggregate.addMember(member);
			aggregateMap.put(member, sourceAggregate);
		}
	}

	private Aggregate getAggregate(String alias) {
		Aggregate a = aggregateMap.get(alias);
		return(a);
	}

	private Edge addEdge(Node source, CiBean bean, Node target) {
		System.out.println("Add Edge: " + source + "-->" + target);
		Edge e = g.addEdge(source, target);
		e.set("alias", bean.getAlias());
		e.set("type", bean.getDerivedFrom());
		e.set("name", bean.getDerivedFrom());
		return(e);
	}

	private Node addNode(CiBean ci) {
		Node n = nodeMap.get(ci.getAlias());
		if (n != null) {
			return(n);
		}
		Aggregate a = getAggregate(ci.getAlias());
		if (a != null) {
			n = nodeMap.get(a.getId());
			if (n != null) {
				return(n);
			}
			n = g.addNode();
			n.set("aggregate", true);
			n.set("name", "Aggregate [" + a.getMembers().size()+ "]");
			nodeMap.put(a.getId(), n);
			return(n);
		}
		n = g.addNode();
		n.set("alias", ci.getAlias());
		n.set("type", ci.getDerivedFrom());
		String displayName = ci.getDisplayName();
		if (displayName.length() > 20) {
			displayName = displayName.substring(0, 20) + "...";
		}
		
		n.set("name", displayName);
		String icon = "";
		if (getValue(ci, "icon") != null) {
			icon = "&icon=" + getValue(ci, "icon");
		}
		n.set("image", OneCMDBConnection.instance().getIconURL() + "?type=" + ci.getDerivedFrom() + icon);
		
		nodeMap.put(ci.getAlias(), n);
		return(n);
	}

	private String getValue(CiBean bean, String alias) {
		ValueBean vBean = bean.fetchAttributeValueBean(alias, 0);
		if (vBean == null) {
			return(null);
		}
		if (vBean.hasEmptyValue()) {
			return(null);
		}
		return(vBean.getValue());
	}

	private String getSource(CiBean bean) {
		return(getValue(bean, "source"));
	}
	
	private String getTarget(CiBean bean) {
		return(getValue(bean, "target"));
	}

	public void setExclude(boolean selected) {
		this.excludeRelation = selected;
		
	}
	
	public List<CiBean> graphBeanTemplate() {
		List<CiBean> beans = new ArrayList<CiBean>();
		if (true) {
			return(beans);
		}
		CiBean graph = new CiBean();
		graph.setAlias("GraphQuery");
		AttributeBean selA = new AttributeBean("Selector(s)", "selector", "ItemSelector", "ComposedOf", true);
		selA.setMaxOccurs("unbound");
		selA.setMinOccurs("0");
		graph.addAttribute(selA);
		beans.add(graph);

		/*
		CiBean itemConstraint = new CiBean();
		itemConstraint.setAlias("ItemSelector");
		itemConstraint.addAttribute(new AttributeBean("template", "xs:string", null, false));
		
		beans.add(itemSelector);
		*/
		
		
		CiBean itemSelector = new CiBean();
		itemSelector.setAlias("ItemSelector");
		itemSelector.addAttribute(new AttributeBean("template", "xs:string", null, false));
		
		beans.add(itemSelector);
		
		CiBean itemOffspringSelector = new CiBean();
		itemOffspringSelector.setDerivedFrom("ItemSelector");
		itemOffspringSelector.setAlias("ItemOffspringSelector");
		itemOffspringSelector.addAttribute(new AttributeBean("Template", "template", "xs:string", null, false));
		beans.add(itemOffspringSelector);
		
		CiBean itemRelationSelector = new CiBean();
		itemRelationSelector.setAlias("ItemRelationSelector");
		itemRelationSelector.setDerivedFrom("ItemSelector");
		itemRelationSelector.addAttribute(new AttributeBean("Template", "template", "xs:string", null, false));
		itemRelationSelector.addAttribute(new AttributeBean("Source", "source", "ItemSelector", "PointsTo", true));
		itemRelationSelector.addAttribute(new AttributeBean("Target", "target", "ItemSelector", "PointsTo", true));
		beans.add(itemRelationSelector);
		
		return(beans);
	}
	
	public List<CiBean> graphToBean() {
		List<CiBean> beans = new ArrayList<CiBean>();
		CiBean graph = new CiBean();
		graph.setAlias("GraphQuery-1");
		graph.setDerivedFrom("GraphQuery");
		graph.setDisplayName(graph.getAlias());
		
		beans.add(graph);
		
		for (ItemSelector sel : q.fetchSelectors()) {
			CiBean selBean = itemSelectorToBean(sel);
			if (selBean != null) {
				beans.add(selBean);
				graph.addAttributeValue(new ValueBean("selector", selBean.getAlias(), true));
			}
		}
		return(beans);
	}
	
	private CiBean itemSelectorToBean(ItemSelector sel) {
		
		if (sel instanceof ItemOffspringSelector) {
			CiBean offspringSel = new CiBean();
			offspringSel.setAlias("ItemSelector-" + sel.getId());
			offspringSel.setDisplayName(sel.getId());
			offspringSel.setDerivedFrom("ItemOffspringSelector");
			offspringSel.addAttributeValue(new ValueBean("template", sel.getTemplateAlias(), false));
			return(offspringSel);
		}
		
		if (sel instanceof ItemRelationSelector) {
			ItemRelationSelector rel = (ItemRelationSelector)sel;
			CiBean offspringSel = new CiBean();
			offspringSel.setAlias("ItemSelector-" + sel.getId());
			offspringSel.setDisplayName(sel.getId());
			offspringSel.setDerivedFrom("ItemRelationSelector");
			offspringSel.addAttributeValue(new ValueBean("template", sel.getTemplateAlias(), false));
			offspringSel.addAttributeValue(new ValueBean("source", "ItemSelector-" + rel.getSource(), true));
			offspringSel.addAttributeValue(new ValueBean("target", "ItemSelector-" + rel.getTarget(), true));
			return(offspringSel);
		}
		return(null);
		
	}

	/**
	 * Return the graph of the query
	 * 
	 * @return
	 */
	public prefuse.data.Graph getQueryGraph() {
		return(this.queryGraph);
	}

	public void reset() {
		List<ItemSelector> sels = new ArrayList<ItemSelector>(q.fetchSelectors());
		for (ItemSelector sel : sels) {
			q.removeSelector(sel);
		}
		update();
		visualMap.clear();
		
	}

	public String getResultAsHTML() {
		StringBuffer b = new StringBuffer();
		for (Template t : result.getNodes()) {
			int size = 0;
			if (t.getOffsprings() != null) {
				size = t.getOffsprings().size();
			}
			String of = "";
			if (t.getTotalCount() != size) {
				of = " (" + t.getTotalCount() + ")";
			}
			b.append(t.getTemplate().getAlias() + " " + size + of + " instances visible<br>");
		}
		b.append("<b>Total</b> " + result.fetchAllNodeOffsprings().size() + " instances visible (Loaded in " + loadTime + "ms)<br>");
		
		return(b.toString());
	}

	public void setSearch(String id, String text) {
		searchMap.put(id, text);
	}

	public String getSearch(String id) {
		String search = searchMap.get(id);
		if (search == null) {
			return("");
		}
		return(search);
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}


}
