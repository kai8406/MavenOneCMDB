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
package org.onecmdb.ui.gwt.toolkit.client.control.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;


public class MultipleAttributeValue extends AbstractAttributeValue {

	List attributeValues = new ArrayList();

	public MultipleAttributeValue(AttributeControl ctrl, GWT_CiBean bean, GWT_AttributeBean aBean, List values) {
		super(ctrl, bean, aBean);
		addValues(values);
	}
	
	public MultipleAttributeValue(AttributeControl ctrl, GWT_CiBean bean, GWT_AttributeBean aBean) {
		super(ctrl, bean, aBean);
		List values = bean.fetchAttributeValueBeans(aBean.getAlias());
		addValues(values);
	}
	
	protected void addValues(List values) {
		if (values != null) {
			for (Iterator iter = values.iterator(); iter.hasNext(); ) {
				GWT_ValueBean vBean = (GWT_ValueBean) iter.next(); 
				attributeValues.add(new AttributeValue(ctrl, bean, aBean, vBean));
			}
		}
	
	}
	
	
	public String toString() {
		if (attributeValues == null) {
			return("<empty>");
		}
		if (attributeValues.size() == 0) {
			return("[0]");
		}
		return("[" + attributeValues.size() + "] - " + ((GWT_ValueBean)(attributeValues.get(0))).toString());
	}

	public boolean isMultiValued() {
		return(true);
	}

	public List getAttributeValues() {
		return(this.attributeValues);
	}
	
	public AttributeValue newAttributeValue(String value) {
		GWT_ValueBean vBean = new GWT_ValueBean();
		vBean.setAlias(aBean.getAlias());
		vBean.setComplexValue(aBean.isComplexType());
		vBean.setValue(value);
	
		return(new AttributeValue(ctrl, bean, aBean, vBean));
	}

	public void setAttributeValues(List list) {
		bean.removeAttributeValues(aBean.getAlias());
		this.attributeValues.clear();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			AttributeValue aValue = (AttributeValue)iter.next();
			addAttributeValue(aValue);
		}
	}
	
	public void addAttributeValue(AttributeValue aValue) {
		this.attributeValues.add(aValue);
		this.bean.addAttributeValue(aValue.getValueBean());
	}
	
	public void removeAttributeValue(AttributeValue value) {
		this.bean.removeAttributeValue(value.getValueBean());
		this.attributeValues.remove(value);
	}

	public void removeAttributeValues() {
		List values = new ArrayList(this.attributeValues);
		for (Iterator iter = values.iterator(); iter.hasNext(); ) {
			AttributeValue aValue = (AttributeValue) iter.next();
			removeAttributeValue(aValue);
		}
		
	}
}
