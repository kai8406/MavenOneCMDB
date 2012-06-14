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
package org.onecmdb.ui.gwt.desktop.client.widget.group.graph;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class GWT_XMLQueryGraphParser {
	
	private static final Object GRAPH_QUERY = "GraphQuery";
	private static final Object ITEM_OFFSPRING_SELECTOR = "ItemOffspringSelector";
	private static final String ID_ATTR = "id";
	private static final String TEMPLATE_ATTR = "template";
	private static final String PRIMARY_ATTR = "primary";
	private static final Object ITEM_RELATION_SELECTOR = "ItemRelationSelector";
	private static final String TARGET_NODE = "target";
	private static final String SOURCE_NODE = "source";

	public static GWT_GraphQuery parse(String xml) {
		GWT_GraphQuery q = null;
		Document doc = XMLParser.parse(xml);
		NodeList list = doc.getChildNodes();
		
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item.getNodeName().equals(GRAPH_QUERY)) {
				q = new GWT_GraphQuery();
				parseSelectors(q, item);
			}
		}
		
		return(q);
	}

	private static void parseSelectors(GWT_GraphQuery q, Node parent) {
		NodeList list = parent.getChildNodes();
		
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item.getNodeName().equals(ITEM_OFFSPRING_SELECTOR)) {
				String id = getAttributeValue(item, ID_ATTR); 
				String template = getAttributeValue(item, TEMPLATE_ATTR);
				String primary = getAttributeValue(item, PRIMARY_ATTR);
				GWT_ItemOffspringSelector sel = new GWT_ItemOffspringSelector();
				sel.setId(id);
				sel.setTemplate(template);
				sel.setPrimary(primary == null ? false : (primary.equalsIgnoreCase("true")));
				q.addSelector(sel);
			}
			if (item.getNodeName().equals(ITEM_RELATION_SELECTOR)) {
				String id = getAttributeValue(item, ID_ATTR); 
				String template = getAttributeValue(item, TEMPLATE_ATTR);
				String target = getNodeValue(item, TARGET_NODE);
				String source = getNodeValue(item, SOURCE_NODE);
					
				GWT_ItemRelationSelector sel = new GWT_ItemRelationSelector();
				sel.setId(id);
				sel.setTemplate(template);
				sel.setTarget(target);
				sel.setSource(source);
				q.addSelector(sel);
			}
			
		}
	}
	
	private static String getNodeValue(Node n, String name) {
		NodeList list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item.getNodeName().equals(name)) {
				return(item.getChildNodes().item(0).getNodeValue());
			}
		}
		return(null);
	}

	private static String getAttributeValue(Node n, String name) {
		NamedNodeMap attrs = n.getAttributes();
		Node item = attrs.getNamedItem(name);
		if (item == null) {
			return(null);
		}
		return(item.getNodeValue());
	}
	
	public static void main(String argv[]) {
		
	}
}
