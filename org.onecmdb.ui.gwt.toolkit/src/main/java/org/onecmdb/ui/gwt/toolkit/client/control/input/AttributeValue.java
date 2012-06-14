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

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;


public class AttributeValue extends AbstractAttributeValue {
	private GWT_ValueBean value;
	private GWT_CiBean ciValue;
	

	
	public AttributeValue(String label, String type, boolean complex, boolean requiered) {
		this.ctrl = new AttributeControl();
		this.ctrl.setRequiered(requiered);
		this.bean = new GWT_CiBean();
		this.aBean = new GWT_AttributeBean(label, type, null, complex);
		this.aBean.setDisplayName(label);
		this.value = new GWT_ValueBean(label, null, complex);
	}
	
	public AttributeValue(String label, String type, boolean complex, boolean requiered, boolean readonly) {
		this(label, type, complex, requiered);
		this.ctrl.setReadonly(readonly);
	}
	
	public AttributeValue(AttributeControl ctrl, GWT_CiBean bean, GWT_AttributeBean aBean, GWT_ValueBean vBean) {
		super(ctrl, bean, aBean);
		this.value = vBean;
	}

	public String toString() {
		if (this.value == null) {
			return("<empty>");
		}
		return(this.value.getValue());
	}

	
	/*
	public GWT_AttributeBean getAttributeBean() {
		return(this.aBean);
	}
	*/

	public GWT_ValueBean getValueBean() {
		return(this.value);
	}
	
	public GWT_AttributeBean getAttributeBean() {
		return(this.aBean);
	}
	
	public void setValue(String value) {
		if (this.value == null) {
			// Allocate a new value.
			this.value = new GWT_ValueBean();
			this.value.setComplexValue(aBean.isComplexType());
			this.value.setAlias(aBean.getAlias());
			this.bean.addAttributeValue(this.value);
		}
		this.value.setValue(value);
	}

	public String getStringValue() {
		if (this.value == null) {
			return(null);
		}
		return(this.value.getValue());
	}

	public void setValueAsCI(GWT_CiBean bean) {
		this.ciValue = bean;
		if (bean == null) {
			setValue(null);
		} else {
			setValue(bean.getAlias());
		}
	}
	
	public GWT_CiBean getValueAsCI() {
		return(this.ciValue);
	}

	public boolean isNullValue() {
		if (this.value == null) {
			return(true);
		}
		if (this.value.getValue() == null) {
			return(true);
		}
		if (this.value.getValue().length() == 0) {
			return(true);
		}
		return(false);
	}

	

	
}
