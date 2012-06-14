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

import org.dom4j.DocumentException;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationCollectionModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationTypeModel;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.utils.xml.XML2GraphQuery;

public class GroupHandler {

	
	private ICIMDR mdr;
	private String token;

	public GroupHandler(ICIMDR mdr, String token) {
		this.mdr = mdr;
		this.token = token;
	}
	
	public RelationCollectionModel getRelationCollection(CIModel ci, GroupDescription desc) throws DocumentException {
		RelationCollectionModel model = new RelationCollectionModel();
		
		String xmlQuery = desc.getQuery();
		XML2GraphQuery parser = new XML2GraphQuery();
		//parser.setAttributeMap(params);
		GraphQuery q = parser.parse(xmlQuery);


		String relId = ci.get(CIModel.SELECTOR_ID);

		ItemSelector selector = null;
		if (relId == null) {
			// Fetch Primary selector.
			selector = q.fetchPrimarySelectors();
		} else {
			selector = (ItemSelector) q.findSelector(relId);
		}
		for (ItemRelationSelector relSel : q.getItemRelationSelector()) {
			if (relSel.getTarget().equals(selector.getId())) {
				String target = q.findSelector(relSel.getTarget()).getTemplateAlias();
				String source = q.findSelector(relSel.getSource()).getTemplateAlias();
				String selId = relSel.getSource();
				model.addRelationType(getRelationType(ci, selId, relSel, source, target, false));
			}
			if (relSel.getSource().equals(selector.getId())) {
				String target = q.findSelector(relSel.getTarget()).getTemplateAlias();
				String source = q.findSelector(relSel.getSource()).getTemplateAlias();
				String selId = relSel.getTarget();
				
				model.addRelationType(getRelationType(ci, selId, relSel, source, target, true));
			}
		}
		return(model);
	}

	private RelationTypeModel getRelationType(CIModel ci, String selId, ItemRelationSelector relSel, String sourceType, String targetType, boolean outbound) {
		
		RelationTypeModel relType = new RelationTypeModel();
		relType.setInstance(ci);
		relType.setAttributeAlias(relSel.getSourceAttribute());
		relType.setId(selId);
		relType.setOutbound(outbound);
		
		relType.setRefType(getCIModel(relSel.getTemplateAlias()));
		relType.setSourceType(getCIModel(sourceType));
		relType.setTargetType(getCIModel(targetType));
		
		return(relType);
	}

	private CIModel getCIModel(String alias) {
		CIModel model = new CIModel();
		model.setTemplate(true);
		model.setAlias(alias);
		return(model);
	}	

}
