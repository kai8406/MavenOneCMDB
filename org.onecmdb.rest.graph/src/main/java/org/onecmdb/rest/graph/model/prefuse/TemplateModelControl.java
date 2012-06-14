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

import java.util.HashMap;
import java.util.Map;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.rest.graph.io.OneCMDBConnection;
import org.onecmdb.rest.graph.utils.applet.AppletLogger;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;

public class TemplateModelControl {
	
	private static Map<String, TemplateModelControl> models = new HashMap<String, TemplateModelControl>();
	private String root;
	private org.onecmdb.core.utils.graph.result.Graph templateResult;
	private int totalTemplateCount;
	private long loadTemplateTime;
	private Graph templateGraph;
	private Map<String, CiBean> beanMap = new HashMap<String, CiBean>();
	private Map<String, Node> nodeMap = new HashMap<String, Node>();

	public TemplateModelControl(String root) {
		this.root = root;
	}
		
	
	public void resetData() {
		beanMap.clear();
		nodeMap.clear();
		templateGraph = null;
		templateResult = null;
	}
	
	public String toString() {
		return("TemplateModelControl[" + hashCode() + "]: BeanCache Size=" + beanMap.size());
	}
	public static void reset() {
		for (TemplateModelControl tModel : models.values()) {			
			tModel.resetData();
			System.out.println("Reset:" + tModel.toString());
		}
		models.clear();
	}

	public static void reload() {
		for (TemplateModelControl tModel : models.values()) {
			tModel.update();
		}
	}

	public static TemplateModelControl get(String root) {
		TemplateModelControl templateModel = models.get(root);
		if (templateModel == null) {
			templateModel = new TemplateModelControl(root);
			templateModel.update();
			models.put(root, templateModel);
		}
		System.out.println("Request TemplateModelControl for " + root + ": " + templateModel);
		return(templateModel);
	}
	

	
	private void update() {
		ItemOffspringSelector ci = new ItemOffspringSelector("ci", root);
		ci.setMatchTemplate(true);
		ci.setPrimary(true);
		
		
		GraphQuery q = new GraphQuery();
		q.addSelector(ci);
		
		AppletLogger.showMessage("Query CMDB (RESTFull)...");
		long start = System.currentTimeMillis();
		org.onecmdb.core.utils.graph.result.Graph result = OneCMDBConnection.instance().query(q);
		long stop = System.currentTimeMillis();
		
		result.buildMap();
		
		totalTemplateCount = result.fetchAllNodeOffsprings().size() + 1;
		loadTemplateTime = stop-start;
		
		Template c = result.fetchNode(ci.getId());
		
		templateGraph = new prefuse.data.Graph();
		setupModel(templateGraph);		
		
		if (c == null || c.getOffsprings() == null || c.getOffsprings().size() == 0) {
			
			Node notFound = templateGraph.addNode();
			notFound.set("alias", root);
			notFound.set("name", "Empty Model for [" + root + "]");
		} else {
			for (CiBean ciBean : c.getOffsprings()) {
				beanMap.put(ciBean.getAlias(), ciBean);
			}
			beanMap.put(c.getTemplate().getAlias(), c.getTemplate());
			// Add this first...
			getNode(root);
			AppletLogger.showMessage("Found " + c.getOffsprings().size() + " templates...");
			for (CiBean ciBean : c.getOffsprings()) {
				Node parent = getNode(ciBean.getDerivedFrom());
				Node child = getNode(ciBean.getAlias());
				Edge edge = templateGraph.addEdge(parent, child);
			}
		}
	}

	private void setupModel(Graph g) {
		g.addColumn("alias", String.class);
		g.addColumn("type", String.class);
		g.addColumn("mark", Boolean.class);
		g.addColumn("name", String.class);
		g.addColumn("checked", boolean.class);
		
		// On Edge
		g.addColumn("springCoefficient", float.class);
		g.addColumn("springLength", float.class);
		
		// On Node
		g.addColumn("massValue", float.class);
		g.addColumn("image", String.class);
	}


	private Node getNode(String alias) {
		Node n = nodeMap.get(alias);
		if (n == null) {
			n = templateGraph.addNode();
			CiBean bean = beanMap.get(alias);
			n.set("alias", bean.getAlias());
			n.set("name", bean.getDisplayName());
			nodeMap.put(alias, n);
		}
		return(n);
	}

	public Map<String, CiBean> getBeanMap() {
		return(beanMap);
	}	

	public CiBean getBean(String alias) {
		return(beanMap.get(alias));
	}

	public Graph getTemplateGraph() {
		return(templateGraph);
	}

	public String getTemplateInfoAsHTML() {
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		b.append("OneCMDB contains " + totalTemplateCount + " templates<br>");
		b.append("<hr size=\"1\">");
		b.append("Loaded templates in " + loadTemplateTime + "ms<br>");
		b.append("</html>");
		return(b.toString());
	}

}
