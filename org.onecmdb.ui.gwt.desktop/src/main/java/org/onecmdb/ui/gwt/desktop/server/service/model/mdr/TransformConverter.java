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
package org.onecmdb.ui.gwt.desktop.server.service.model.mdr;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;
import org.onecmdb.utils.xml.XMLUtils;

public class TransformConverter {

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final String TRANSFORM_HEAD = "<onecmdb-transform";
	private static final String TRANSFORM_FOOTER = "</onecmdb-transform>";

	public static String toXML(String sourceType, TransformModel transform) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(XML_HEADER);
		buffer.append("\n");
		buffer.append(TRANSFORM_HEAD + " name=\"" + transform.getName() + "\">");
		buffer.append("\n");
	
		for (DataSetModel ds : transform.getDataSets()) {
			CIModel model = ds.getTemplate();
			if (model == null) {
				continue;
			}
			// Don't add emtpy dataset's
			if (ds.getAttributeSelector() == null || ds.getAttributeSelector().size() == 0) {
				continue;
			}
			String name = ds.getName();
			buffer.append("\t<DataSet name=\"" + name + "\">");
			buffer.append("\n");
			buffer.append("\t\t<InstanceSelector template=\"" + model.getAlias() + "\"/>");
			buffer.append("\n");
			
			for (AttributeSelectorModel as : ds.getAttributeSelector()) {
				AttributeModel a = as.getAttribute();
				if (a == null) {
					continue;
				}
				as.isNaturalKey();
				if (a.isComplex()) {
					buffer.append("\t\t\t<ComplexAttributeSelector name=\"" + a.getAlias() + "\" naturalKey=\"" + as.isNaturalKey() + "\" dataSet=\"" + as.getSelector() + "\"/>");
				} else {
					String type = as.getSelectorType();
					if (AttributeSelectorModel.AS_TYPE_VALUE.equals(type)) {
						buffer.append("\t\t\t<DefaultAttributeSelector name=\"" + a.getAlias() + "\" naturalKey=\"" + as.isNaturalKey() + "\" value=\"" + as.getValue() + "\"/>");
					} else {
						String selector = "selector";
						if (as.isUseSelectorName()) {
							 selector = "selectorName";
							
						}
						buffer.append("\t\t\t<AttributeSelector name=\"" + a.getAlias() + "\" naturalKey=\"" + as.isNaturalKey() + "\" " + selector +"=\"" + as.getSelector() + "\"/>");
					}
				}
				buffer.append("\n");
			}
			
			buffer.append("\t</DataSet>");
			buffer.append("\n");
			/*
			<AttributeSelector name="rxSpeed" naturalKey="false" selector="5" />

			<ComplexAttributeSelector name="partOf" naturalKey="true" dataSet="platform"/>
			*/
		}
		buffer.append(TRANSFORM_FOOTER);
		buffer.append("\n");
		
		
		return(buffer.toString());
		
	}

	public static TransformModel fromXML(String token, ICIMDR mdr, String content) throws DocumentException {
		TransformModel model = new TransformModel();
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(content));
	
		Element root = document.getRootElement();
		String name = XMLUtils.getAttributeValue(null, root, "name", false);
		if (name != null && name.length() > 0) {
			model.setName(name);
		}
		for (Element el : (List<Element>)root.elements()) {
			if (el.getName().equals("DataSet")) {
				DataSetModel ds = parseDataSet(token, mdr, el);
				if (ds != null) {
					model.addDataSet(ds);
				}
			}
		}
		
		return(model);
	}

	private static DataSetModel parseDataSet(String token, ICIMDR mdr, Element root) {
		DataSetModel ds = new DataSetModel();
		String name = XMLUtils.getAttributeValue(null, root, "name", false);
		ds.setName(name);
		// Handle Instance Selector
		Element ins = root.element("InstanceSelector");
		if (ins == null) {
			return(ds);
		}
		String alias = XMLUtils.getAttributeValue(null, ins, "template", false);
		CiBean template = mdr.getCI(token, alias);
		// Convert to CIModel....
		if (template == null) {
			return(ds);
		}
		Transform t = new Transform();
		CIModel ci = t.convert(mdr, token, template, template);
		ds.setTemplate(ci);
		for (Element el : (List<Element>)root.elements()) {
			
			if (el.getName().equals("AttributeSelector")) {
				AttributeSelectorModel as = getASModel(el, ci);
				String selector = XMLUtils.getAttributeValue(null, el, "selector", false);
				as.setUseSelectorName(false);
				if (selector == null) {
					selector = XMLUtils.getAttributeValue(null, el, "selectorName", false);	
					as.setUseSelectorName(true);
				}
				as.setSelector(selector);
				as.setSelectorType(AttributeSelectorModel.AS_TYPE_SELECTOR);
				ds.addAttributeSelector(as);
			}
			if (el.getName().equals("ComplexAttributeSelector")) {
				AttributeSelectorModel as = getASModel(el, ci);
				String dataSet = XMLUtils.getAttributeValue(null, el, "dataSet", false);
				as.setSelector(dataSet);
				ds.addAttributeSelector(as);
			}
			if (el.getName().equals("DefaultAttributeSelector")) {
				AttributeSelectorModel as = getASModel(el, ci);
				String value = XMLUtils.getAttributeValue(null, el, "value", false);
				as.setValue(value);
				as.setSelectorType(AttributeSelectorModel.AS_TYPE_VALUE);
				ds.addAttributeSelector(as);
			}
		}	
		return(ds);
	}
	
	private static AttributeSelectorModel getASModel(Element el, CIModel ci) {
		AttributeSelectorModel as = new AttributeSelectorModel();
		
		String naturalKey = XMLUtils.getAttributeValue(null, el, "naturalKey", false);
		String attAlias = XMLUtils.getAttributeValue(null, el, "name", false);
		AttributeModel at = ci.getAttribute(attAlias);
		
		as.setNaturalKey("true".equals(naturalKey));
		as.setAttribute(at);
		
		return(as);
	}

}
