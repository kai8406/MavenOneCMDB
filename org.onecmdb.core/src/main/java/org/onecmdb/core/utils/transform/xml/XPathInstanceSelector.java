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
package org.onecmdb.core.utils.transform.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Node;
import org.onecmdb.core.utils.transform.AInstanceSelector;
import org.onecmdb.core.utils.transform.ANameObject;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.EmptyAttributeValue;
import org.onecmdb.core.utils.transform.IAttributeSelector;
import org.onecmdb.core.utils.transform.IAttributeValue;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.IInstanceSelector;
import org.onecmdb.core.utils.transform.TextAttributeValue;

public class XPathInstanceSelector extends AInstanceSelector  {
	private String xpath;
	private String templatePath = null;
	
	public XPathInstanceSelector() {
	}
	
	public XPathInstanceSelector(String name, String template, String xpath) {
		setName(name);
		setTemplate(template);
		setXpath(xpath);
	}
	public void setTemplatePath(String xpath) {
		this.templatePath  = xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	public String getXpath() {
		return(this.xpath);
	}

	public List<IInstance> getInstances(DataSet ds) throws IOException {
		if (ds.getDataSource() instanceof XMLDataSource) {
			List<IInstance> rows = new ArrayList<IInstance>();
			for (Node node : ((XMLDataSource)ds.getDataSource()).getNodes() ) {
				List<Node> selectedNodes = node.selectNodes(getXpath());
				for (Node selectedNode : selectedNodes) {
					
					XMLRow row = new XMLRow(ds, selectedNode);
					row.setAutoCreate(isAutoCreate());
					String temp = null;
					if (templatePath != null) {
						Node n = row.getNode();
						
						Object sNode = n.selectObject(templatePath);
						
						if (sNode instanceof Node) {
							temp = ((Node)sNode).getText();
						} else if (sNode instanceof String) {
							temp = (String)sNode;							
						}
					}
					if (temp == null) {
						temp = getTemplate();
					}
					row.setTemplate(temp);
					
					rows.add(row);
				}
			}
			return(rows);
		}
		throw new IllegalArgumentException("XPath selector must operate on an XML data source");
	}

	

	
	
	
}
