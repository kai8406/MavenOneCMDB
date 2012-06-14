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
package org.onecmdb.utils.group;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;

public class CIPackage {
	
	public CIPackage() {
	}
	
	public GraphQuery getQuery() {
		return(null);
	}
	
	
	
	public Graph createNew() {
		Graph g = new Graph();
		for (ItemOffspringSelector sel : getQuery().getItemOffspringSelector()) {
			Template t = new Template();
			t.setId(sel.getId());
			for (int i = 0; i <  getOccurence(sel); i++) {
				CiBean bean = new CiBean();
				bean.setDerivedFrom(sel.getTemplateAlias());
				bean.setTemplate(false);
				t.addOffspring(bean);
			}
			g.addNodes(t);
		}
		for (ItemRelationSelector rel : getQuery().getItemRelationSelector()) {
			Template relation = new Template();
			relation.setId(rel.getId());
			g.addEdges(relation);
			Template target = g.fetchNode(rel.getTarget());
			Template source = g.fetchNode(rel.getSource());
			String sAttr = rel.getSourceAttribute();
			for (CiBean tBean : target.getOffsprings()) {
				for (CiBean sBean : source.getOffsprings()) {
					ValueBean v = new ValueBean(sAttr, tBean.getAlias(),true);
					sBean.addAttributeValue(v);
					
					ValueBean targetValue = new ValueBean("target", tBean.getAlias(), true);
					ValueBean sourceValue = new ValueBean("source", sBean.getAlias(), true);
					CiBean relBean = new CiBean();
					relBean.setDerivedFrom(rel.getTemplateAlias());
					relBean.setTemplate(false);
					relBean.addAttributeValue(targetValue);
					relBean.addAttributeValue(sourceValue);
					relation.addOffspring(relBean);
				}
			}
		}
		return(g);
	}

	private int getOccurence(ItemOffspringSelector sel) {
		return(1);
	}
}
