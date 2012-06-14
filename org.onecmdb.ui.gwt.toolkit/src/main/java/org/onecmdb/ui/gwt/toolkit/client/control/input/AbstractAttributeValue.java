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


public class AbstractAttributeValue {
	protected GWT_AttributeBean aBean;
	protected GWT_CiBean bean;
	protected AttributeControl ctrl;
	private Object widget;

	public AbstractAttributeValue() {
	}
	
	public AbstractAttributeValue(AttributeControl ctrl, GWT_CiBean bean, GWT_AttributeBean aBean) {
		this.aBean = aBean;
		this.bean = bean;
		this.ctrl = ctrl;
	}

	
	public AttributeControl getCtrl() {
		return ctrl;
	}

	public void setCtrl(AttributeControl ctrl) {
		this.ctrl = ctrl;
	}

	public String getLabel() {
		return(aBean.getDisplayName());
	}

	public String getType() {
		return(aBean.getType());
	}
	
	public boolean isComplex() {
		return(aBean.isComplexType());
	}

	public boolean isMultiValued() {
		return(false);
	}

	public String getDisplayName() {
		return(aBean.getDisplayName());
	}
	
	public String getAlias() {
		return(aBean.getAlias());
	}
	
	public String getDescription() {
		return(aBean.getDescription());
	}
	
	public Object getWidget() {
		return widget;
	}

	public void setWidget(Object widget) {
		this.widget = widget;
	}
	
	/**
	 * Only single simple attribute values are sort-able.
	 * @return
	 */
	public boolean isSortable() {
		if (true) {
			return(true);
		}
		if (aBean == null) {
			return(false);
		}
		
		if (true) {
			return(true);
		}
		if (aBean.isComplexType()) {
				return(false);
		}
		
		if (isMultiValued()) {
			return(false);
		}
		
		return(true);
	}

}
