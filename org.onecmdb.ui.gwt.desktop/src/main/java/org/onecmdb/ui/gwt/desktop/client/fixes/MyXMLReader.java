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
package org.onecmdb.ui.gwt.desktop.client.fixes;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.XmlReader;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class MyXMLReader<C> extends XmlReader<C> {

	private ModelType modelType;
	
	public MyXMLReader(ModelType modelType) {
		super(modelType);
		this.modelType = modelType;
	}


	 public ListLoadResult read(C loadConfig, Object data) {
		    Document doc = XMLParser.parse((String) data);

		    NodeList list = doc.getElementsByTagName(modelType.recordName);
		    ArrayList<ModelData> records = new ArrayList<ModelData>();
		    for (int i = 0; i < list.getLength(); i++) {
		      Node node = list.item(i);
		      Element elem = (Element) node;
		      ModelData model = newModelInstance();
		      for (int j = 0; j < modelType.getFieldCount(); j++) {
		        DataField field = modelType.getField(j);
		        String map = field.map != null ? field.map : field.name;
		        // Fix to allow not existsing elements.
		        try {
		        	String v = getValue(elem, map);
		        	
		        	model.set(field.name, v);
		        } catch (Throwable t) {
		        	// Ignore.
		        }
		      }
		      records.add(model);
		    }

		    int totalCount = records.size();

		    Node root = doc.getElementsByTagName(modelType.root).item(0);
		    if (root != null && modelType.totalName != null) {
		      Node totalNode = root.getAttributes().getNamedItem(modelType.totalName);
		      if (totalNode != null) {
		        String sTot = totalNode.getNodeValue();
		        totalCount = Integer.parseInt(sTot);
		      }
		    }
		    ListLoadResult result = newLoadResult(loadConfig, records);
		    if (result instanceof PagingLoadResult) {
		      PagingLoadResult r = (PagingLoadResult) result;
		      r.setTotalLength(totalCount);
		    }
		    return result;
		  }

	
}
