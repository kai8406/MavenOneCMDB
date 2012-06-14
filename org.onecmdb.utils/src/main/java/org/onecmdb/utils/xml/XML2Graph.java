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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.xml.XmlParser;

public class XML2Graph {
	
	
	private XmlParser parser = new XmlParser();

	public Graph fromXML(InputStream in) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(in);
		List<CiBean> beans = new ArrayList<CiBean>();
		Element root = document.getRootElement();
		
		if (!root.getName().equals(Graph2XML.ELEMENT_ROOT)) {
			throw new IllegalArgumentException("Not a correct header element, found " + root.getName() + " requiers " + Graph2XML.ELEMENT_ROOT);
		}
		
		Graph result = new Graph();
		for (Element eSel : (List<Element>)root.elements()) {
			if (eSel.getName().equals(Graph2XML.ELEMENT_NODES)) {
				handleTemplates(result, eSel);
			}
			if (eSel.getName().equals(Graph2XML.ELEMENT_EDGES)) {
				handleTemplates(result, eSel);
			}
		}
		return(result);
	}

	private void handleTemplates(Graph result, Element sel) {
		for (Element el : (List<Element>)sel.elements()) {
			if (el.getName().equals(Graph2XML.ELEMENT_NODE)) {
				Template t = getTemplate(el);
				result.addNodes(t);
			}
			if (el.getName().equals(Graph2XML.ELEMENT_EDGE)) {
				Template t = getTemplate(el);
				result.addEdges(t);
			}
		}
	}

	private Template getTemplate(Element sel) {
		Template t = new Template();
		t.setId(XMLUtils.getAttributeValue(null, sel, "id", true));
		
		Element tEl = sel.element("template");
		if (tEl != null) {
			t.setTemplate(getBean(tEl));
		}
		
		Element offEl = sel.element(Graph2XML.ELEMENT_OFFSPRINGS);
		if (offEl != null) {
			String count = XMLUtils.getAttributeValue(null, offEl, "totalCount", false);
			if (count != null) {
				t.setTotalCount(Integer.parseInt(count));
			}
			for (Element beanEl : (List<Element>)offEl.elements()) {
				t.addOffspring(getBean(beanEl));
			}
		}
		return(t);
	}
	
	private CiBean getBean(Element el) {
		if (el.getName().equals("template")) {
			CiBean bean = parser.parseBlueprint(el);
			return(bean);
		}
		CiBean bean = parser.parseInstance(el);
		return(bean);
	}
	
	
}
