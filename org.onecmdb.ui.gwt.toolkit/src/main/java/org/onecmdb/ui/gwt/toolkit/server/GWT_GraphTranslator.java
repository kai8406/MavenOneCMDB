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
package org.onecmdb.ui.gwt.toolkit.server;

import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RFCTargetConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRFCSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemTransactionSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.wsdl.TransactionBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_AttributeValueConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_GraphQuery;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemAliasSelector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemAndGroupConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemGroupConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemOffspringSelector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemOrGroupConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemRFCSelector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemRelationSelector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemSecurityConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemSelector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemTransactionSelector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_PageInfo;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_RFCTargetConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_RelationConstraint;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result.GWT_Graph;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result.GWT_Template;

public class GWT_GraphTranslator {
	HashMap<GWT_ItemSelector, ItemSelector> selectorMap = new HashMap<GWT_ItemSelector, ItemSelector>();
	
	public GraphQuery convert(GWT_GraphQuery fromQ) {
		if (fromQ == null) {
			return(null);
		}
		GraphQuery toQ = new GraphQuery(); 
		for (GWT_ItemSelector selector : (List<GWT_ItemSelector>)fromQ.getSelectors()) {
			toQ.addSelector(convert(selector));
		}
		return(toQ);
	}

	public GWT_Graph convert(Graph fromG) {
		if (fromG == null) {
			return(null);
		}
		GWT_Graph toG = new GWT_Graph();
		toG.setAlias(fromG.getAlias());
		for (Template t : fromG.getNodes()) {
			toG.addNodes(convert(t));
		}
		for (Template t : fromG.getEdges()) {
			toG.addEdges(convert(t));
		}
		return(toG);
		
	}

	

	/**
	 *  ItemSelector Convert methods.
	 * 	 
	 */
	
	private ItemSelector convert(GWT_ItemSelector fromS) {
		if (fromS == null) {
			return(null);
		}
		ItemSelector result = selectorMap.get(fromS);
		if (result != null) {
			return(result);
		}
		if (fromS instanceof GWT_ItemOffspringSelector) {
			result = convert((GWT_ItemOffspringSelector)fromS);
		}
		if (fromS instanceof GWT_ItemAliasSelector) {
			result = convert((GWT_ItemAliasSelector)fromS);
		}
		if (fromS instanceof GWT_ItemRelationSelector) {
			result = convert((GWT_ItemRelationSelector)fromS);
		}
		if (fromS instanceof GWT_ItemTransactionSelector) {
			result = convert((GWT_ItemTransactionSelector)fromS);
		}
		if (fromS instanceof GWT_ItemRFCSelector) {
			result = convert((GWT_ItemRFCSelector)fromS);
		}
		
		if (result == null) {
			throw new IllegalArgumentException("Not a valid selction object " + fromS.getClass().getName());
		}
		
		
		return(result);
	}
	
	private void convert(GWT_ItemSelector fromS, ItemSelector toS) {
		selectorMap.put(fromS, toS);
		toS.setId(fromS.getId());
		toS.setTemplateAlias(fromS.getTemplateAlias());
		toS.applyConstraint(convert(fromS.getConstraint()));
		toS.setPrimary(fromS.isPrimary());
		toS.setExcludeRelations(fromS.getExcludeRelations());
		toS.setPageInfo(convert(fromS.getPageInfo()));
	}
	
	private PageInfo convert(GWT_PageInfo from) {
		if (from == null) {
			return(null);
		}
		PageInfo to = new PageInfo();
		to.setFirstResult(from.getFirstResult());
		to.setMaxResult(from.getMaxResult());
		return(to);
	}

	private ItemTransactionSelector convert(GWT_ItemTransactionSelector fromS) {
		if (fromS == null) {
			return(null);
		}
		ItemTransactionSelector toS = new ItemTransactionSelector();
		convert(fromS, toS);
		
		return(toS);
	}
	
	private ItemRFCSelector convert(GWT_ItemRFCSelector fromS) {
		if (fromS == null) {
			return(null);
		}
		ItemRFCSelector toS = new ItemRFCSelector();
		toS.setTxId(fromS.getTxId());
		convert(fromS, toS);
		
		return(toS);
	}


	private ItemAliasSelector convert(GWT_ItemAliasSelector fromS) {
		if (fromS == null) {
			return(null);
		}
		ItemAliasSelector toS = new ItemAliasSelector();
		convert(fromS, toS);
		toS.setAlias(fromS.getAlias());
		
		return(toS);
	}

	private ItemOffspringSelector convert(GWT_ItemOffspringSelector fromS) {
		if (fromS == null) {
			return(null);
		}
		ItemOffspringSelector toS = new ItemOffspringSelector();
		toS.setMatchTemplate(fromS.getMatchTemplate());
		convert(fromS, toS);
		
		return(toS);
	}

	private ItemRelationSelector convert(GWT_ItemRelationSelector fromS) {
		if (fromS == null) {
			return(null);
		}
		ItemRelationSelector toS = new ItemRelationSelector();
		convert(fromS, toS);
		toS.setTarget(fromS.getTarget());
		toS.setSource(fromS.getSource());
		toS.setMandatory(fromS.isMandatory());
		return(toS);
	}
	
	/**
	 *  ItemConstratint Convert methods.
	 * 	 
	 */
	
	private ItemConstraint convert(GWT_ItemConstraint fromC) {
		if (fromC == null) {
			return(null);
		}
		if (fromC instanceof GWT_ItemAndGroupConstraint) {
			return(convert((GWT_ItemAndGroupConstraint)fromC));
		}
		if (fromC instanceof GWT_ItemOrGroupConstraint) {
			return(convert((GWT_ItemOrGroupConstraint)fromC));
		}
		if (fromC instanceof GWT_AttributeValueConstraint) {
			return(convert((GWT_AttributeValueConstraint)fromC));
		}
		if (fromC instanceof GWT_RelationConstraint) {
			return(convert((GWT_RelationConstraint)fromC));
		}
		if (fromC instanceof GWT_ItemSecurityConstraint) {
			return(convert((GWT_ItemSecurityConstraint)fromC));
		}
		if (fromC instanceof GWT_RFCTargetConstraint) {
			return(convert((GWT_RFCTargetConstraint)fromC));
		}
	
		
		throw new IllegalArgumentException("Can't convert abstract " + fromC.getClass().getName() + " class");
	}
	
	
	
	private void convert(ItemGroupConstraint toC, GWT_ItemGroupConstraint fromC) {
		for (GWT_ItemConstraint con : (List<GWT_ItemConstraint>)fromC.getConstraints()) {
			toC.add(convert(con));
		}
	}
	
	private ItemAndGroupConstraint convert(GWT_ItemAndGroupConstraint fromC) {
		ItemAndGroupConstraint toC = new ItemAndGroupConstraint();
		convert(toC, fromC);
		return(toC);
	}
	
	private RFCTargetConstraint convert(GWT_RFCTargetConstraint fromC) {
		RFCTargetConstraint toC = new RFCTargetConstraint();
		if (fromC.getLongId() != null) {
			toC.setLongId(Long.valueOf(fromC.getLongId()));
		}
		return(toC);
	}
	
	private ItemSecurityConstraint convert(GWT_ItemSecurityConstraint fromC) {
		ItemSecurityConstraint toC = new ItemSecurityConstraint();
		toC.setGid(fromC.getGid());
		toC.setGroupName(fromC.getGroupName());
		return(toC);
	}
	
	private ItemOrGroupConstraint convert(GWT_ItemOrGroupConstraint fromC) {
		ItemOrGroupConstraint toC = new ItemOrGroupConstraint();
		convert(toC, fromC);
		return(toC);
	}
	
	private AttributeValueConstraint convert(GWT_AttributeValueConstraint fromC) {
		AttributeValueConstraint toC = new AttributeValueConstraint();
		toC.setAlias(fromC.getAlias());
		toC.setOperation(fromC.getOperation());
		toC.setValue(fromC.getValue());
		toC.setValueType(fromC.getValueType());
		return(toC);
	}
	
	private RelationConstraint convert(GWT_RelationConstraint fromC) {
		RelationConstraint toC = new RelationConstraint();
		toC.setDirection(fromC.getDirection());
		toC.setSelector(fromC.getSelector());
		return(toC);
	}


	/**
	 *  Graph result converts...
	 * 	 
	 */
	
	private GWT_Template convert(Template fromT) {
		GWT_Translator tr = new GWT_Translator();
		
		GWT_Template toT = new GWT_Template();
		toT.setAlias(fromT.getAlias());
		toT.setTemplate(tr.convert(fromT.getTemplate()));
		toT.setTotalCount(fromT.getTotalCount());
		toT.setId(fromT.getId());
		for (CiBean offspring : fromT.getOffsprings()) {
			toT.addOffspring(tr.convert(offspring));
		}
		
		for (RFCBean rfc : fromT.getRFC()) {
			toT.addRFC(tr.convert(rfc));
		}
		
		for (TransactionBean tx : fromT.getTransactions()) {
			toT.addTransaction(tr.convert(tx));
		}
		
		
		return(toT);
	}
	
	
	
	
	
	
	

}
