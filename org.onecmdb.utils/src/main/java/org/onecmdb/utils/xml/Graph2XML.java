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

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;

public class Graph2XML {
		
	
	public static final String ELEMENT_ROOT = "OneCMDBGraph";
	public static final String ELEMENT_NODES = "Nodes";
	public static final String ELEMENT_EDGES = "Edges";
	public static final String ELEMENT_OFFSPRINGS = "Offsprings";
	public static final Object ELEMENT_EDGE = "Edge";
	public static final Object ELEMENT_NODE = "Node";
	
	
	public static String toXML(Graph result, int tab) {
		StringBuffer b = new StringBuffer();
		b.append(getTab(tab));
		b.append("<" + Graph2XML.ELEMENT_ROOT + ">");
		b.append("\n");
	
		b.append(getTab(tab+1));
		b.append("<" + Graph2XML.ELEMENT_NODES + ">");
		b.append("\n");
		
		for (Template t : result.getNodes()) {
			b.append(getTab(tab+1) + "<Node id=\"" + t.getId() + "\" type=\"" + t.getTemplate().getAlias() + "\">");
			b.append("\n");
			b.append(toXML(t, tab+2));
			b.append(getTab(tab+1) + "</Node>");
			b.append("\n");
		}
		b.append(getTab(tab+1));
		b.append("</" + Graph2XML.ELEMENT_NODES + ">");
		b.append("\n");
		
		b.append(getTab(tab+1));
		b.append("<" + Graph2XML.ELEMENT_EDGES + ">");
		b.append("\n");
		for (Template t : result.getEdges()) {
				b.append(getTab(tab+1) + "<Edge id=\"" + t.getId() + "\" type=\"" + t.getTemplate().getAlias() + "\">");
				b.append("\n");
				b.append(toXML(t, tab+2));
				b.append(getTab(tab+1) + "</Edge>");
				b.append("\n");
		}
		b.append(getTab(tab+1));
		b.append("</" + Graph2XML.ELEMENT_EDGES + ">");
		b.append("\n");
		
		b.append("\n");
		b.append(getTab(tab));
		b.append("</" + Graph2XML.ELEMENT_ROOT + ">");
		b.append("\n");
		
		return(b.toString());
	}
	
	private static String toXML(Template t, int i) {
		StringBuffer b = new StringBuffer();

		if (t.getTemplate() != null) {
			b.append(t.getTemplate().toXML(i+1));
			b.append("\n");
		}
		b.append(getTab(i) + "<" + ELEMENT_OFFSPRINGS + " totalCount=\"" + t.getTotalCount() + "\">");
		b.append("\n");
		if (t.getOffsprings() != null) {
			for (CiBean bean : t.getOffsprings()) {
				b.append(bean.toXML(i+2));
				b.append("\n");
			}
		}
		b.append(getTab(i) + "</" + ELEMENT_OFFSPRINGS + ">");
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
