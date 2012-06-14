/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.modeller.client.control.transform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NewTransformControl extends AttributeValueInputControl {
	
	private static final String DATA_SOURCE_TYPE_XPATH = "XPATH";
	private static final String DATA_SOURCE_TYPE_CSV = "CSV";
	private static final String DATA_SOURCE_TYPE_JDBC = "JDBC";
	
	// Template for each type.
	private static final String XPATH_COMPLEX_ATTRIBUTE_SELECTOR = "XPathComplexAttributeSelector";
	private static final String XPATH_ATTRIBUTE_SELECTOR = "XPathAttributeSelector";
	private static final String XPATH_INSTANCE_SELECTOR = "XPathInstanceSelector";
	private static final String CSV_COMPLEX_ATTRIBUTE_SELECTOR = "CSVComplexAttributeSelector";
	private static final String CSV_ATTRIBUTE_SELECTOR = "CSVAttributeSelector";
	private static final String CSV_INSTANCE_SELECTOR = "CSVInstanceSelector";
	
	
	protected AttributeValue alias = new AttributeValue("Alias", "xs:string", false, true, false);
	protected AttributeValue name = new AttributeValue("Name", "xs:string", false, true, false);
	protected AttributeValue description = new AttributeValue("Description", "xs:string", false, false, false);

	protected AttributeValue template = new AttributeValue("Template", "Root", true, true, false);
	protected AttributeValue dataSourceType = new AttributeValue("Data Source Type", "xs:string", false, true, false);

	public NewTransformControl() {
		TextAttributeControl dataSourceCtrl = new TextAttributeControl("DataSourceType", 
				false, 
				false, 
				TextAttributeControl.TEXT_ENUM_TYPE,
				new Integer(1),
				getDataSourceTypes()
		);
		
		TextAttributeControl descCtrl = new TextAttributeControl("description", 
				false, 
				false, 
				TextAttributeControl.TEXT_AREA_TYPE,
				new Integer(5),
				null); 
		
		AttributeControl templateCtrl = new AttributeControl();
		templateCtrl.setSelectTemplate(true);
			
		template.setCtrl(templateCtrl);
		dataSourceType.setCtrl(dataSourceCtrl);
		
		description.setCtrl(descCtrl);
		
		addAttributeValue(alias);
		addAttributeValue(name);
		addAttributeValue(description);
		addAttributeValue(template);
		addAttributeValue(dataSourceType);
		
	}

	private List getDataSourceTypes() {
		List types = new ArrayList();
		types.add(DATA_SOURCE_TYPE_XPATH);
		types.add(DATA_SOURCE_TYPE_CSV);
		types.add(DATA_SOURCE_TYPE_JDBC);
		
		return(types);
	}
	
	/**
	 * Save a new DataSet....
	 * 
	 *
	 */
	public void commit(AsyncCallback callback) {
		try {
			GWT_CiBean templateCI = template.getValueAsCI();
			if (templateCI == null) {
				throw new Exception("Template is not loaded/found!");
			}
			List beans = new ArrayList();
			// 1) Create a new DataSet.
			GWT_CiBean ds = new GWT_CiBean("DataSet", dataSourceType.getStringValue() + "-" + alias.getStringValue(), false);
			ds.setDescription(description.getStringValue());
			ds.addAttributeValue(new GWT_ValueBean("name", name.getStringValue(), false));
			beans.add(ds);
			
			// 2) Create a InstanceSelector for the correct type.
			GWT_CiBean instanceSelector = new GWT_CiBean(getInstanceSelectorType(), alias.getStringValue() + "-" + templateCI.getAlias(), false);
			instanceSelector.addAttributeValue(new GWT_ValueBean("template", templateCI.getAlias(), false));
			beans.add(instanceSelector);
			ds.addAttributeValue(new GWT_ValueBean("instanceSelector", instanceSelector.getAlias(), true));
			
			// 3) Create AttributeSelectors for all attributes.
			for (Iterator iter = templateCI.getAttributes().iterator(); iter.hasNext();) {
				GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
				GWT_CiBean attributeSelector = new GWT_CiBean(getAttributeSelector(aBean), alias.getStringValue() + "-" + aBean.getAlias(), false);
				attributeSelector.addAttributeValue(new GWT_ValueBean("name", aBean.getAlias(), false));  
				beans.add(attributeSelector);
				ds.addAttributeValue(new GWT_ValueBean("attributeSelector", attributeSelector.getAlias(), true));
			}
			
			// Do commit to onecmdb.
			OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(), 
					(GWT_CiBean[])beans.toArray(new GWT_CiBean[0]), 
					null, 
					callback);
			
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	private String getAttributeSelector(GWT_AttributeBean aBean) throws Exception {
		if (DATA_SOURCE_TYPE_XPATH.equals(dataSourceType.getStringValue())) {
			if (aBean.isComplexType()) {
				return(XPATH_COMPLEX_ATTRIBUTE_SELECTOR);
			}
			return(XPATH_ATTRIBUTE_SELECTOR);
		}
		if (DATA_SOURCE_TYPE_CSV.equals(dataSourceType.getStringValue())) {
			if (aBean.isComplexType()) {
				return(CSV_COMPLEX_ATTRIBUTE_SELECTOR);
			}
			return(CSV_ATTRIBUTE_SELECTOR);
			
		}
		if (DATA_SOURCE_TYPE_JDBC.equals(dataSourceType.getStringValue())) {
			// Not supported yet!
		}
		
		
		throw new Exception(dataSourceType.getStringValue() + " is not supported!");
	}

	private String getInstanceSelectorType() throws Exception {
		if (DATA_SOURCE_TYPE_XPATH.equals(dataSourceType.getStringValue())) {
			return(XPATH_INSTANCE_SELECTOR);
						
		}
		if (DATA_SOURCE_TYPE_CSV.equals(dataSourceType.getStringValue())) {
			return(CSV_INSTANCE_SELECTOR);
					
		}
		if (DATA_SOURCE_TYPE_JDBC.equals(dataSourceType.getStringValue())) {
			// Not supported yet!
		}
		
		
		throw new Exception(dataSourceType.getStringValue() + " is not supported!");
		
	}

}
