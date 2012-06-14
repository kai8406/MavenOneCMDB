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
package org.onecmdb.ui.gwt.desktop.server.service.model;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;

public class Graph2GridTransform {
	
	
	public List<CIModelCollection> transform(ICIMDR mdr, String token, GraphQuery query, Graph graph) {
		List<CIModelCollection> result = new ArrayList<CIModelCollection>();
		
		Transform transform = new Transform();
		graph.buildMap();
		transform.setCache(graph);
		
		for (ItemSelector sel : query.fetchSelectors()) {
			if (sel.isExcludedInResultSet()) {
				continue;
			}
			if (sel instanceof ItemRelationSelector) {
				continue;
			}
			if (!sel.isPrimary()) {
				continue;
			}
			if (sel.getId().equals("cache_serach")) {
				continue;
			}
			Template templ = graph.fetchNode(sel.getId());
			if (templ == null) {
				continue;
			}
			if (templ.getOffsprings() == null) {
				continue;
			}
			CiBean template = null;
			for (CiBean bean : templ.getOffsprings()) {
				CIModelCollection col = new CIModelCollection();
				if (bean.getDerivedFrom().equals(templ.getTemplate().getAlias())) {
					template = templ.getTemplate();
				} else if (template != null) {
					if (!template.getAlias().equals(bean.getDerivedFrom())) {
						template = null;
					}
				}
				if (template == null) {
					template = transform.getCI(mdr, token, bean.getDerivedFrom());
				}
				CIModel model = transform.convert(mdr, token, template, bean);
				col.addCIModel(sel.getId(), model);
				result.add(col);
			}
		}
		System.out.println("Tranform Cache:  hits=" + transform.cacheHit + ", misses=" + transform.cacheMiss);
		return(result);
	}
	
}
