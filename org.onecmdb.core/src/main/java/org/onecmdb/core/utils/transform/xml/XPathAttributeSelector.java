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

import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.onecmdb.core.utils.transform.AAttributeSelector;
import org.onecmdb.core.utils.transform.EmptyAttributeValue;
import org.onecmdb.core.utils.transform.IAttributeValue;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.TextAttributeValue;

public class XPathAttributeSelector extends AAttributeSelector  {
	private String xpath;
	
	
	public XPathAttributeSelector(String name, String xpath, boolean naturalKey) {
		setName(name);
		setXpath(xpath);
		setNaturalKey(naturalKey);
	}
	
	public XPathAttributeSelector() {
	}
	
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	public String getXpath() {
		return(this.xpath);
	}

	public IAttributeValue getAttribute(IInstance row) {
		if (row instanceof XMLRow) {
			Node node = ((XMLRow)row).getNode();
			Node selectedNode = null;
			try {
				selectedNode = node.selectSingleNode(getXpath());
			} catch (InvalidXPathException e) {
				throw new IllegalArgumentException("Invalid XPath '" + getXpath() + "' in attribute '" + getName() + "'", e);
			}
			if (selectedNode == null) {
				return(new EmptyAttributeValue(this));
				//throw new IllegalArgumentException("Column '" + getXPath() + "' not found in row '" + node.getPath() +"'");
			}
			TextAttributeValue col = new TextAttributeValue(this, selectedNode.getText());
			return(col);
		}
		return(null);
	}

	@Override
	public String toString() {
		return("XPathAttributeSelector[name=" + getName() + ", xpath=" + getXpath() + "]");
	}

	
	
	
}
