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
import org.onecmdb.core.utils.transform.ANameObject;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IAttributeSelector;
import org.onecmdb.core.utils.transform.IAttributeValue;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.IInstanceSelector;

public class XMLRow extends XMLDataSource implements IInstance {
	private Node node;
	private DataSet dataSet;
	private String template;
	private boolean autoCreate = true;
	
	public XMLRow(DataSet dataSet, Node node) {
		this.node = node;
		this.dataSet = dataSet;
		setName(node.getUniquePath());
	}
	
	public Node getNode() {
		return(this.node);
	}
	
	
	
	public String toString() {
		return("XMLRow[name=" + getName() + ",path=" + node.getPath() + "]");
	}


	public DataSet getDataSet() {
		return(this.dataSet);
	}
	
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	@Override
	public List<Node> getNodes() throws IOException {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(getNode());
		return(nodes);
	}

	public IInstanceSelector getInstanceSelector() {
		return(getDataSet().getInstanceSelector());
	}

	@Override
	public IInstance clone() throws CloneNotSupportedException {
		return ((IInstance)super.clone());
	}

	public String getLocalID() {
		return(node.getPath());
	}

	public String getTemplate() {
		return(this.template);
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isAutoCreate() {
		return(autoCreate);
	}
	
	public void setAutoCreate(boolean value) {
		this.autoCreate = value;
	}

}
