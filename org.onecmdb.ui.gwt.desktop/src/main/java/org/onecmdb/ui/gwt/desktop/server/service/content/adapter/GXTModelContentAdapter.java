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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ojb.broker.util.factory.ConfigurableFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopMenuItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopShortcut;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.UserPreference;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.GXTModel2XML;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.IContentDataAware;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GXTModelContentAdapter implements IAdaptable, IContentDataAware {

	private Properties classMap;
	private BaseModel model;

	public GXTModelContentAdapter() {
		classMap = new Properties();
		classMap.setProperty("OneCMDBDesktopConfig", CMDBDesktopConfig.class.getName());
		classMap.setProperty("menuitem", CMDBDesktopMenuItem.class.getName());
		classMap.setProperty("shortcut", CMDBDesktopShortcut.class.getName());
		classMap.setProperty("widget", CMDBDesktopWindowItem.class.getName());
		
		classMap.setProperty("GridModelConfig", GridModelConfig.class.getName());
		classMap.setProperty("ColumnConfig", AttributeColumnConfig.class.getName());
		classMap.setProperty("UserPreference", UserPreference.class.getName());
	}
	
	public void setContentData(ContentData data)  {
		try {
			load(data);
		} catch (Throwable e) {
			throw new IllegalArgumentException("Adapter GXTModelContentAdataper failed for data " + data.getPath(), e);
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

	public BaseModel updateModel(Element el, BaseModel parent) throws Exception {
	
		//System.out.println(el.getName());
		String className = classMap.getProperty(el.getName());
		if (className == null) {
			className = ConfigurationFactory.get("gxtadapter." + el.getName() + ".class");
			if (className == null) {
				className = BaseModel.class.getName();
			}
			//throw new IllegalArgumentException(el.getName() + " has no class definition");
		}
		Class clazz = Class.forName(className);
		Object object = clazz.newInstance();
		if (!(object instanceof BaseModel)) {
			throw new IllegalArgumentException(el.getName() + " class " + className + " is not implementing BaseModel");
		}
		
		BaseModel model = (BaseModel)object;
		if (parent == null) {
			parent = model;
		}
		model.set("tag", el.getName());
		for (Attribute a : (List<Attribute>)el.attributes()) {
			updateModelValue(model, a.getName(), a.getText(), false);
		}
		for (Element e : (List<Element>)el.elements()) {
			boolean asList = false;
			Attribute a = e.attribute("asList");
			if (a != null) {
				asList = true;
			}
			boolean simpleList = false;
			a = e.attribute("asSimpleList");
			if (a != null) {
				asList = true;
				simpleList = true;
			}
			
			if (isSimpleElement(e) || simpleList) {
				updateModelValue(model, e.getName(), e.getTextTrim(), asList);
			} else {
				updateModelValue(model, e.getName(), updateModel(e, model), asList);
			}
		}
		return(model);
	}

	private boolean isSimpleElement(Element e) {
		if (e.elements().size() > 0) {
			return(false);
		}
		if (e.attributeCount() > 0) {
			return(false);
		}
		return true;
	}

	private void updateModelValue(BaseModel m, String name, Object text, boolean asList) {
		//System.out.println("Update<" + m + ">( " + asList + ")" + name + "=" + text);
		if (asList) {
			Object o = m.get(name);
			if (o == null || !(o instanceof List)) {
				o = new ArrayList();
				m.set(name, o);
			}
			((List)o).add(text);
		} else {
			m.set(name, text);
		}
	}

	public Object getAdapter(Class clazz) {
		if (clazz.isInstance(this.model)) {
			return(model);
		}
		return(null);
	}
	
	public static void main(String argv[]) {
		GXTModelContentAdapter ad = new GXTModelContentAdapter();
		ContentData d = new ContentData();
		d.setPath(argv[0]);
			
		ad.setContentData(d);
		
		Object config =  ad.getAdapter( GridModelConfig.class);
		System.out.println(GXTModel2XML.toXML(config.getClass().getSimpleName(), config, 0));
		System.out.println("=========================================");
		
		/*
		List<CMDBDesktopMenuItem> items = config.getMenuItems();
		for (CMDBDesktopMenuItem item : items) {
			dumpMenuItem(item);
		}
		*/
	}
	
	public static void dumpMenuItem(CMDBDesktopMenuItem item) {
		item.getWindowItem();
	}

}
