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
package org.onecmdb.ui.gwt.desktop.server.service.model.mdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;

public class AutoResolveRelation {

	private ICIMDR mdr;
	private String token;
	HashMap<String, CIModel> map = new HashMap<String, CIModel>();
	Transform tr = new Transform();

	public AutoResolveRelation(ICIMDR mdr, String token) {
		this.mdr = mdr;
		this.token = token;
	}
	
	public TransformModel autoResolve(TransformModel model) {
		// Need all templates....
		
		for (DataSetModel ds : model.getDataSets()) {
			resolveOutgoingRelation(ds, model);
		}
		
		return(model);
	}

	private void resolveOutgoingRelation(DataSetModel ds, TransformModel model) {
		// Find outgoing relations...
		if (ds.getTemplate() == null) {
			return;
		}
		for (AttributeModel am : ds.getTemplate().getAttributes()) {
			if (!am.isComplex()) {
				continue;
			}
			CIModel toType = am.getComplexType();
			if (toType == null) {
				continue;
			}
			List<DataSetModel> matched = matchTemplate(model, toType);
			// Update model...
			for (DataSetModel relDS : matched) {
				AttributeSelectorModel sel = new AttributeSelectorModel();
				sel.setAttribute(am);
				sel.setSelector(relDS.getName());
				System.out.println("Relation: " + ds.getName() + "-->" + relDS.getName());
				// Don't connect to ourselfs..
				if (ds.getName().equals(relDS.getName())) {
					continue;
				}
				// Check that we don't alread have this.
				if (notASNotExists(ds, sel)) {
					ds.addAttributeSelector(sel);
				}
			}
		}
	}
	
	private boolean notASNotExists(DataSetModel ds, AttributeSelectorModel sel) {
		for (AttributeSelectorModel v : ds.getAttributeSelector()) {
			if (v.getAttribute().getAlias().equals(sel.getAttribute().getAlias())) {
				return(false);
			}
		}
		return(true);
	}

	private List<DataSetModel> matchTemplate(TransformModel model, CIModel target) {
		List<DataSetModel> list = new ArrayList<DataSetModel>();
		for (DataSetModel ds : model.getDataSets()) {
			if (isInherited(target, ds.getTemplate())) {
				list.add(ds);
			}
		}
		return(list);
	}
	
	private boolean isInherited(CIModel model, CIModel parent) {
		
		if (parent == null || model == null) {
			return(false);
		}
		if (parent.getAlias().equals(model.getAlias())) {
			return(true);
		}
		String parentName = parent.getDerivedFrom();
		// Find parent...
		if (parentName == null) {
			return(false);
		}
		CIModel p = map.get(parentName);
		if (p == null) {
			CiBean bean = mdr.getCI(token, parentName);
		
			p = tr.convert(mdr, token, bean, bean);
			map.put(p.getAlias(), p);
		}
		return(isInherited(model,p));
	}
	
	protected String getDerivedPath(CIModel ci) {
		if (ci.getDerivedFrom() == null) {
			return("/" + ci.getAlias() + "/");
		}
		CiBean parent = mdr.getCI(token, ci.getDerivedFrom());
		Transform tr = new Transform();
		CIModel p = tr.convert(mdr, token, parent, parent);
		return(getDerivedPath(p) + "" + ci.getAlias() + "/");
	}
}
