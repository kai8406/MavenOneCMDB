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
package org.onecmdb.ui.gwt.desktop.server.service.model.group;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModelList;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.ListModelItem;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;

import com.extjs.gxt.ui.client.data.BaseModel;

import sun.rmi.log.ReliableLog;

public class GroupTransform {

	private boolean deepTree = false;

	public List<GroupCollection> generateGroupData(ICIMDR mdr, String token, GraphQuery query, Graph result) {
		String id = query.fetchPrimarySelectors().getId();
		Template t = result.fetchNode(id);
		Transform transform = new Transform();
		transform.setCache(result);
	
		ListModelItem<GroupCollection> rows = populateRows(transform, mdr, token, query, result, t, "");
		//System.out.println(dumpModel(0, rows));
		
		return(rows.toList());
	}
	
	public ListModelItem<GroupCollection> populateRows(Transform transform, ICIMDR mdr, String token, GraphQuery query, Graph result, Template t, String path) { 
		ListModelItem<GroupCollection> rows = new ListModelItem<GroupCollection>();
	
		// start with primary.
		for (CiBean bean : t.getOffsprings()) {
			GroupCollection row = new GroupCollection();
			CIModel model = transform.convert(mdr, token, t.getTemplate(), bean);
			row.set(t.getId(), model);
			
			rows.add(row);
			
			// Populate references...
			for (ItemRelationSelector rel : query.fetchRelationSelectors()) {
				if (path.contains("/" + rel.getId())) {
					continue;
				}
				Template refs = null;
				if (rel.getSource().equals(t.getId())) {
					refs = result.fetchReference(bean, RelationConstraint.SOURCE, rel.getId());
				} else if (rel.getTarget().equals(t.getId())) {
					refs = result.fetchReference(bean, RelationConstraint.TARGET, rel.getId());
				}
				if (refs != null) {
					ListModelItem<GroupCollection> childRows = populateRows(transform, mdr, token, query,  result, refs, path + "/" + rel.getId());
					row.set(rel.getId(), childRows);
				}
			}
		}
		return(rows);
	}

	private String dumpModel(int i, BaseModel data) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(getTab(i) + data.getClass().getName());
		buf.append("\n");
		for (String name : data.getPropertyNames()) {
			Object value = data.get(name);
			if (value instanceof BaseModel) {
				buf.append(getTab(i+1) + name + "=" + dumpModel((i+1), (BaseModel)value));
			} else {
				buf.append(getTab(i+1) + name + "=" + value);
			}
			buf.append("\n");
		}
		return(buf.toString());
	}

	private String getTab(int k) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < k; i++) {
			b.append("  ");
		}
		return(b.toString());
	}
}