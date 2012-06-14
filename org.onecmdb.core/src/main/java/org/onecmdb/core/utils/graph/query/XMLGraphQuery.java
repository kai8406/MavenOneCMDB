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
package org.onecmdb.core.utils.graph.query;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;

public class XMLGraphQuery {
	private Document document;
	private HashMap<String, ItemSelector> selectorMap = new HashMap<String, ItemSelector>();

	public String toXML(GraphQuery g) {
	
		return("");
	}
	
	public GraphQuery fromXML(String gq) throws DocumentException {
		SAXReader reader = new SAXReader();
		document = reader.read(new ByteArrayInputStream(gq.getBytes()));
		Element root = document.getRootElement();
		if (!root.getName().equals("GraphQuery")) {
			throw new IllegalArgumentException("Not a valid GrapgQuery docuemnt");
		}
		GraphQuery query = new GraphQuery();
		for (Iterator iter = root.elementIterator(); iter.hasNext();) {
			Element el = (Element) iter.next();
			ItemSelector selector = parseXMLSelector(el);
			query.addSelector(selector);		 
		}
		return(query);
		
	}
	
	private ItemSelector getSelector(String id) {
		Element el = document.getRootElement().elementByID(id);
		return(parseXMLSelector(el));
	}
	
	private ItemSelector parseXMLSelector(Element el) {
		String id = el.attributeValue("id");
		ItemSelector selector = selectorMap .get(id);
		if (selector != null) {
			return(selector);
		}
		if (el.getName().equals("ItemAliasSelector")) {
			ItemAliasSelector aSelector = new ItemAliasSelector();
			aSelector.setId(id);
			selectorMap.put(id, aSelector);
			
			Element alias = el.element("alias");
			aSelector.setAlias(alias.getTextTrim());
			selector = aSelector;
		} else if (el.getName().equals("ItemRelationSelector")) {
			ItemRelationSelector rSelector = new ItemRelationSelector();
			rSelector.setId(id);
			selectorMap.put(id, rSelector);
			
			Element source = el.element("source");
			Element target = el.element("target");
			
			rSelector.setSource(source.getTextTrim());
			
			rSelector.setTarget(target.getTextTrim());
			
			selector = rSelector;
		} else if (el.getName().equals("ItemOffspringSelector")) {
			ItemOffspringSelector oSelector = new ItemOffspringSelector();
			oSelector.setId(id);
			selectorMap.put(id, oSelector);
		
			Element template = el.element("template");
			oSelector.setTemplateAlias(template.getTextTrim());
		}
		
		ItemConstraint constraint = parseXMLConstraint(el.element("constraint"));
		selector.applyConstraint(constraint);
		return(null);
	}

	private ItemConstraint parseXMLConstraint(Element element) {
		ItemConstraint con = null;
		if (element.getName().equals("ItemAndGroupConstraint")) {
			con = new ItemAndGroupConstraint();
		} else if (element.getName().equals("ItemOrGroupConstraint")) {
			con = new ItemOrGroupConstraint();
		} else if (element.getName().equals("AttributeValueConstraint")) {
			AttributeValueConstraint aCon = new AttributeValueConstraint();
			int op = aCon.getOperation(element.element("operation").getTextTrim());
			aCon.setAlias(element.element("alias").getTextTrim());
			aCon.setOperation(op);
			aCon.setValue(element.element("alias").getTextTrim());
			//aCon.setValueType();
			con = aCon;
		} else if (element.getName().equals("RelationConstraint")) {
			RelationConstraint rCon = new RelationConstraint();
			rCon.setDirection(RelationConstraint.SOURCE);
			
			con = rCon;
		} 
			
		if (con instanceof ItemGroupConstraint) {
			for (Iterator iter = element.elementIterator(); iter.hasNext();) {
				Element el = (Element) iter.next();
				ItemConstraint nCon = parseXMLConstraint(el);
				((ItemGroupConstraint)con).add(nCon);		 
			}
		}
		return(con);
	}
	
	private String getElementText(Element el, String name) {
		Element s = el.element(name);
		if (s == null) {
			throw new IllegalArgumentException("Element "  + el.getName() + " missing element " + name);
		}
		return(s.getTextTrim());
	}
}
