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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_RelationConstraint;


/**
 * <code>Graph</code> is the result from a graph query.
 * The graph is organized so the nodes and edges are group
 * by the templates that are derived from.
 * 
 */
public class GWT_Graph extends GWT_NamedItem {
	private static final long serialVersionUID = 1L;
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result.GWT_Template>
	 */
	private List templates = new ArrayList();
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result.GWT_Template>
	 */
	private List edges = new ArrayList();
	
	private transient HashMap sourceMap = new HashMap();
	private transient HashMap targetMap = new HashMap();
	private transient HashMap nodeMap = new HashMap();
	private transient HashMap templateIdMap = new HashMap();
	
	public void addNodes(GWT_Template t) {
		templates.add(t);
	}
	
	public void addEdges(GWT_Template t) {
		edges.add(t);
	}

	public GWT_Template fetchNode(String id) {
		for (Iterator iter = templates.iterator(); iter.hasNext(); ) {
			GWT_Template t = (GWT_Template)iter.next();
			if (t.getId().equals(id)) {
				return(t);
			}
		}
		return(null);
	}

	public GWT_CiBean findAlias(String alias) {
		return (GWT_CiBean) (nodeMap.get(alias));
	}
		
	public void buildMap() {
		for (Iterator iter = templates.iterator(); iter.hasNext(); ) {
			GWT_Template template = (GWT_Template) iter.next();
			templateIdMap.put(template.getAlias(), template.getId());
			for (Iterator off = template.getOffsprings().iterator(); off.hasNext();) {
				GWT_CiBean b = (GWT_CiBean) off.next();
				nodeMap.put(b.getAlias(), b);
				templateIdMap.put(b.getAlias(), template.getId());
			}
		}
		
		for (Iterator iter = edges.iterator(); iter.hasNext(); ) {
			GWT_Template t = (GWT_Template) iter.next();
			for (Iterator rels = t.getOffsprings().iterator(); rels.hasNext(); ) {
				GWT_CiBean b = (GWT_CiBean) rels.next();
				
				String sAlias = getValue(b, "source");
				String tAlias = getValue(b, "target");
				
				GWT_CiBean target = (GWT_CiBean) nodeMap.get(tAlias);
				GWT_CiBean source = (GWT_CiBean) nodeMap.get(tAlias);
				
				// Build map...
				updateRelation(targetMap, target, source);
				updateRelation(sourceMap, source, target);
			}
		}
	}
	
	private void updateRelation(HashMap map, GWT_CiBean target, GWT_CiBean source) {
		List targetList = (List) map.get(target.getAlias());
		if (targetList == null) {
			targetList = new ArrayList();
			map.put(target.getAlias(), targetList);
		}
		String sId = (String) templateIdMap.get(source.getAlias());
		GWT_Template relationTemplate = null;
		for (Iterator templates = targetList.iterator(); templates.hasNext();) {
			relationTemplate = (GWT_Template) templates.next();
			if (relationTemplate.getId().equals(sId)) {
				break;
			}
			relationTemplate = null;
		}
		if (relationTemplate == null) {
			relationTemplate = new GWT_Template();
			relationTemplate.setId(sId);
			targetList.add(relationTemplate);
		}
		relationTemplate.addOffspring(source);
	}
	
	private String getValue(GWT_CiBean b, String alias) {
		GWT_ValueBean vBean = b.fetchAttributeValueBean(alias,0);
		if (vBean == null) {
			return(null);
		}
		return(vBean.getValue());
	}

	public GWT_Template fetchReference(GWT_CiBean bean, int direction, String refId) {
		List temaplates = Collections.EMPTY_LIST;
		if (direction == GWT_RelationConstraint.SOURCE) {
			templates = (List) sourceMap.get(bean.getAlias());
		} else {
			templates = (List) targetMap.get(bean.getAlias());
		}
		for (Iterator iter = templates.iterator(); iter.hasNext(); ) {
			GWT_Template t = (GWT_Template)iter.next();
			if (t.getId().equals(refId)) {
				return(t);
			}
		}
		return(null);
	}
	
	public List getNodes() {
		return(this.templates);
	}
}
