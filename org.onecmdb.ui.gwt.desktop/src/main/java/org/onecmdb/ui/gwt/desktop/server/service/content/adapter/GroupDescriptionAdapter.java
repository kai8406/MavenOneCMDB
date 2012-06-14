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
package org.onecmdb.ui.gwt.desktop.server.service.content.adapter;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.xml.XmlParser;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.IContentDataAware;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;
import org.onecmdb.utils.xml.XML2GraphQuery;
import org.onecmdb.utils.xml.XMLUtils;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GroupDescriptionAdapter implements IAdaptable, IContentDataAware {

	private static final String GROUP_GRAPHQUERY = "GraphQuery";
	private static final String GROUP_PRESENTAION = "Presentation";
	private static final String GROUP_PRESENTAION_TABLE = "Table";
	private static final String GROUP_PRESENTAION_GRAPH = "Graph";
	private static final String GROUP_PRESENTAION_TREE = "Tree";
	private static final String GROUP_LIFECYCLE = "Lifecycle";
	private static final String GROUP_LIFECYCLE_CREATE = "Create";
	private static final String GROUP_LIFECYCLE_DELETE = "Delete";
	private static final String GROUP_LIFECYCLE_IMPORT = "Import";
	
	private GroupDescription model;

	public GroupDescriptionAdapter() {
	}
	
	public Object getAdapter(Class clazz) {
		if (clazz.isInstance(this.model)) {
			return(model);
		}
		return(null);
	}

	public void setContentData(ContentData data)  {
		try {
			load(data);
		} catch (Throwable e) {
			throw new IllegalArgumentException("Adapter GroupDescriptionAdataper failed for data " + data.getPath(), e);
		}
	}
	
	protected void load(ContentData data) throws Exception {
		InputStream in = null;
		try {
			in = ContentParserFactory.get().getInputStream(data);
			SAXReader reader = new SAXReader();
			Document document = reader.read(in);
			
			Element el = document.getRootElement();
			
			this.model = updateModel(el, null);
			this.model.setPath(data.getPath());
				
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					// Ignore...
				}
			}
		}
	}

	private GroupDescription updateModel(Element el, Object object) throws DocumentException {
		GroupDescription desc = new GroupDescription();
		
		// Handle name
		desc.setName(XMLUtils.getAttributeValue(null, el, "name", true));
		desc.setIcon(XMLUtils.getAttributeValue(null, el, "icon", false));
			
		// Handle GRAPH QUERY..
		Element queryEL = el.element(GROUP_GRAPHQUERY);
		handleQuery(desc, queryEL);
		
		// Handle Presenations...
		Element presentaionEl = el.element(GROUP_PRESENTAION);
		handlePresentation(desc, presentaionEl);
		
		// Handle Lifecycle...
		handleLifecycle(desc, el);
			
		
		return(desc);
	}
	private void handleQuery(GroupDescription desc, Element queryEL) throws DocumentException {
		if (queryEL == null) {
			desc.appendErrorMessage("Missing GraphQuery element");
		}
		String xml = queryEL.asXML();
		String xmlQuery = desc.getQuery();
		XML2GraphQuery parser = new XML2GraphQuery();
		//parser.setAttributeMap(params);
		GraphQuery q = parser.parse(xml);
		ItemSelector sel = q.fetchPrimarySelectors();
		if (sel == null) {
			throw new IllegalArgumentException("Group - GraphQuery has no primary selector!");
		}
		desc.setPrimaryTemplate(sel.getTemplateAlias());
		desc.setQuery(xml);
	}
	
	private void handlePresentation(GroupDescription desc, Element e) {
		
		if (e == null) {
			return;
		}
		for (Element child : (List<Element>)e.elements()) {
			
		
		 
		if (child.getName().equals(GROUP_PRESENTAION_TABLE)) {
			Element table = child;	
			GXTModelContentAdapter mAdapter = new GXTModelContentAdapter();
			try {
				BaseModel tableModel = mAdapter.updateModel(table, null);
				tableModel.set("xml", table.asXML());
				tableModel.set("type", "TABLE");
				desc.addPresentaion(tableModel);
				
			} catch (Exception e1) {
				e1.printStackTrace();
				desc.appendErrorMessage("Error in Table Model:" + e1.getMessage() );
			}
		}
		
		if (child.getName().equals(GROUP_PRESENTAION_TREE)) {
			Element tree = child;
			GXTModelContentAdapter mAdapter = new GXTModelContentAdapter();
			try {
				BaseModel treeModel = mAdapter.updateModel(tree, null);
				treeModel.set("type", "TREE");
				desc.addPresentaion(treeModel);
				treeModel.set("xml", tree.asXML());
			} catch (Exception e1) {
				e1.printStackTrace();
				desc.appendErrorMessage("Error in Tree Model:" + e1.getMessage() );
			}
		}
		
		if (child.getName().equals(GROUP_PRESENTAION_GRAPH)) {
			Element graph = child;
			GXTModelContentAdapter mAdapter = new GXTModelContentAdapter();
			try {
				BaseModel graphModel = mAdapter.updateModel(graph, null);
				desc.addPresentaion(graphModel);
				graphModel.set("type", "GRAPH");
				graphModel.set("xml", graph.asXML());
			} catch (Exception e1) {
				e1.printStackTrace();
				desc.appendErrorMessage("Error in Table Model:" + e1.getMessage() );
			}
		}
		}
	}

	private void handleLifecycle(GroupDescription desc, Element e) {
		Element el = e.element(GROUP_LIFECYCLE);
		if (el == null) {
			return;
		}
		for (Element child : (List<Element>)el.elements()) {
			if (child.getName().equals(GROUP_LIFECYCLE_CREATE)) {
				Element instances = child.element(XmlParser.INSTANCES_ELEMENT.getName());
				GXTModelContentAdapter mAdapter = new GXTModelContentAdapter();
				try {
					BaseModel createModel = mAdapter.updateModel(child, null);
					if (instances != null) {
						XmlParser parser = new XmlParser();
						List<CiBean> beans = parser.parseInstances(instances);
					}
					desc.addCreateModel(createModel);
				} catch (Exception e1) {
					e1.printStackTrace();
					desc.appendErrorMessage("Error in Create Model:" + e1.getMessage() );
				}
			}
		}
	}

	public static void main(String argv[]) {
		ContentFile f = new ContentFile(argv[0]);
		GroupDescriptionAdapter ad = new GroupDescriptionAdapter();
		try {
			ad.load(f);
			GroupDescription desc = (GroupDescription) ad.getAdapter(GroupDescription.class);
			//desc.getTableColumnModel("platform", null);
			System.out.println("DESC=" + desc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
