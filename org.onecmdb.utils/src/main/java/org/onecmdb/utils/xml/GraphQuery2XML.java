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
package org.onecmdb.utils.xml;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemIdConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemNotConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemTransactionSelector;

public class GraphQuery2XML {
	public static String toXML(GraphQuery query, int tab) {
		StringBuffer b = new StringBuffer();
		b.append(getTab(tab));
		b.append("<" + XML2GraphQuery.ELEMENT_ROOT + ">");
		b.append("\n");
		for (ItemSelector sel : query.fetchSelectors()) {
			b.append(toXML(sel, tab+1));
		}
		b.append("\n");
		b.append(getTab(tab));
		b.append("</" + XML2GraphQuery.ELEMENT_ROOT + ">");
		b.append("\n");
		return(b.toString());
	}
	
	public static String toXML(ItemSelector selector, int tab) {
		StringBuffer b = new StringBuffer();
		b.append(getTab(tab));
		b.append("<" + selector.getClass().getSimpleName());
		b.append(" id=\"" + selector.getId() + "\"");
		b.append(" primary=\"" + selector.isPrimary() + "\"");
		b.append(" template=\"" + selector.getTemplateAlias() + "\"");
		
		b.append(">");
		b.append("\n");
		if (selector instanceof ItemTransactionSelector) {
			b.append(getTab(tab+1));
			b.append("<txid>" + ((ItemTransactionSelector)selector).getTxId() + "</txid>");
			b.append("\n");
		}
		
		if (selector instanceof ItemAliasSelector) {
			b.append(getTab(tab+1));
			b.append("<alias>" + ((ItemAliasSelector)selector).getAlias() + "</alias>");
			b.append("\n");
		}
		if (selector instanceof ItemRelationSelector) {
			ItemRelationSelector relSel = (ItemRelationSelector)selector;
			b.append(getTab(tab+1));
			b.append("<source>" + ((ItemRelationSelector)selector).getSource() + "</source>");
			b.append("\n");
			
			b.append(getTab(tab+1));
			b.append("<target>" + ((ItemRelationSelector)selector).getTarget() + "</target>");
			b.append("\n");
			b.append("<mandatory>" + ((ItemRelationSelector)selector).isMandatory() + "</mandatory>");
			b.append("\n");
		}
		if (selector instanceof ItemOffspringSelector) {
			ItemOffspringSelector oSel = (ItemOffspringSelector)selector;
			
				b.append(getTab(tab+1));
				b.append("<limitToChild>");
				b.append(oSel.isLimitToChild());
				b.append("</limitToChild>");
				b.append("\n");
				
				b.append(getTab(tab+1));
				b.append("<matchTemplate>");
				b.append(oSel.getMatchTemplate());
				b.append("</matchTemplate>");
				b.append("\n");
			
		}
		
		if (selector.fetchConstraint() != null) {
			b.append(getTab(tab+1));
			b.append("<constraint>");
			b.append("\n");
			b.append(toXML(selector.fetchConstraint(), tab+2));
			b.append(getTab(tab+1));
			b.append("</constraint>");
			b.append("\n");
		}
		if (selector.getPageInfo() != null) {
			b.append(getTab(tab+1));
			b.append("<pageInfo>");
			b.append("\n");
			String max = "" + selector.getPageInfo().getMaxResult();
			if (max != null) {
				b.append(getTab(tab+2));
				b.append("<maxResult>" + max + "</maxResult>");
				b.append("\n");
			}
			String first = "" + selector.getPageInfo().getFirstResult();
			if (first != null) {
				b.append(getTab(tab+2));
				b.append("<firstResult>" + first + "</firstResult>");
				b.append("\n");
			}
		
			b.append(getTab(tab+1));
			b.append("</pageInfo>");
			b.append("\n");

		}
		if (selector.getExcludeRelations() != null) {
			for (String exclude : selector.getExcludeRelations()) {
				b.append(getTab(tab+1));
				b.append("<excludeRelation id=\"" + exclude + "\"/>");
				b.append("\n");
			}
		}
		b.append(getTab(tab));
		b.append("</" + selector.getClass().getSimpleName() + ">");
		b.append("\n");
		return(b.toString());
	}

	public static String toXML(ItemConstraint constraint, int tab) {
		StringBuffer b = new StringBuffer();
		if (constraint == null) {
			return(b.toString());
		}
		b.append(getTab(tab));
		b.append("<" + constraint.getClass().getSimpleName() + ">");
		b.append("\n");
		if (constraint instanceof ItemGroupConstraint) {
			for (ItemConstraint con : ((ItemGroupConstraint)constraint).fetchConstraints()) {
				b.append(toXML(con, tab+1));
			}
		}
		
		if (constraint instanceof ItemNotConstraint) {
			ItemNotConstraint con = (ItemNotConstraint)constraint;
			b.append(toXML(con.fetchConstraint(), tab+1));
		}
		
		if (constraint instanceof ItemIdConstraint) {
			ItemIdConstraint con = (ItemIdConstraint)constraint;
			if (con.getAlias() != null) {
				b.append(getTab(tab+1));
				b.append("<alias>" + con.getAlias() + "</alias>");
				b.append("\n");
			}
			if (con.getId() != null) {
				b.append(getTab(tab+1));
				b.append("<id>" + con.getId() + "</id>");
				b.append("\n");
			}
		}
		if (constraint instanceof ItemSecurityConstraint) {
			ItemSecurityConstraint con = (ItemSecurityConstraint)constraint;
			if (con.getGid() != null) {
				b.append(getTab(tab+1));
				b.append("<gid>" + con.getGid() + "</gid>");
				b.append("\n");
			}
			if (con.getGroupName() != null) {
				b.append(getTab(tab+1));
				b.append("<groupName>" + con.getGroupName() + "</groupName>");
				b.append("\n");
			}
		}
		
		
		if (constraint instanceof AttributeValueConstraint) {
			AttributeValueConstraint con = (AttributeValueConstraint)constraint;
			b.append(getTab(tab+1));
			b.append("<operation>" + AttributeValueConstraint.getOperation(con.getOperation()) + "</operation>");
			b.append("\n");
			
			b.append(getTab(tab+1));
			if (con.getAlias() != null) {
				b.append("<alias>" + con.getAlias() + "</alias>");
				b.append("\n");
			}
			if (con.getValue() != null) {
				b.append(getTab(tab+1));
				b.append("<value>" + con.getValue() + "</value>");
				b.append("\n");
			}
		}
		
		if (constraint instanceof RelationConstraint) {
			RelationConstraint con = (RelationConstraint)constraint;
			b.append(getTab(tab+1));
			b.append("<direction>" + con.getDirection() + "</direction>");
			b.append("\n");
		
			b.append(getTab(tab+1));
			b.append("<selector>" + con.getSelector() + "</selector>");
			b.append("\n");
		}
		
		b.append(getTab(tab));
		b.append("</" + constraint.getClass().getSimpleName() + ">");
		b.append("\n");
		return(b.toString());
	}

	public static String getTab(int index) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < index; i++) {
			b.append("\t");
		}
		return(b.toString());
	}
}
