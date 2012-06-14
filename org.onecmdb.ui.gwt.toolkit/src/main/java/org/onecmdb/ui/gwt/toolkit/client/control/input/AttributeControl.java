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

import org.onecmdb.ui.gwt.toolkit.client.control.listener.IEventListener;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class AttributeControl {

	public static final AttributeControl DEFAULT = new AttributeControl();
	
	private String attrAlias;
	private boolean readonly = false;
	private boolean requiered = false;
	private AbstractAttributeValue attributeValue;
	private IEventListener eventListener;
	
	private ClickListener clickListener;

	private boolean selectTemplate;
	
	
	public AttributeControl() {
	}

	public AttributeControl(String alias, boolean readonly, boolean requiered) {
		this.attrAlias = alias;
		this.readonly = readonly;
		this.requiered = requiered;
	}
	 
	
	public AbstractAttributeValue getAttributeValue() {
		return attributeValue;
	}

	public AbstractAttributeValue allocAttributeValue(GWT_CiBean bean, GWT_AttributeBean aBean) {
		if (aBean != null) {
			this.attributeValue = getAttributeValue(bean, aBean);
		} else {
			this.attributeValue = getDummyAttributeValue();
			
		}
		return(this.attributeValue);
	}

	
	public IEventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(IEventListener eventListener) {
		this.eventListener = eventListener;
	}

	public String getAttrAlias() {
		return attrAlias;
	}
	
	public void setAttrAlias(String attrAlias) {
		this.attrAlias = attrAlias;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isRequiered() {
		return requiered;
	}

	public void setRequiered(boolean requiered) {
		this.requiered = requiered;
	}
	
	protected AbstractAttributeValue getDummyAttributeValue() {
		GWT_CiBean bean = new GWT_CiBean();
		GWT_AttributeBean aBean = new GWT_AttributeBean();
		aBean.setAlias(getAttrAlias());
		aBean.setDisplayName(getAttrAlias());
		aBean.setType("xs:string");
		aBean.setMaxOccurs("1");
		aBean.setMinOccurs("1");
		GWT_ValueBean value = new GWT_ValueBean();
		value.setAlias(getAttrAlias());
		value.setComplexValue(false);
		return(new AttributeValue(this, bean, aBean, value));
	}


	protected AbstractAttributeValue getAttributeValue(GWT_CiBean bean, GWT_AttributeBean aBean) {
		
		if (isMultiple(aBean)) {
			return(new MultipleAttributeValue(this, bean, aBean));
		} else {
			return(new AttributeValue(this, bean, aBean, bean.fetchAttributeValueBean(aBean.getAlias(), 0)));
		}
	}

	private boolean isMultiple(GWT_AttributeBean aBean) {
		return(! "1".equals(aBean.getMaxOccurs()));
	}

	public ClickListener getClickListener() {
		return(this.clickListener);
	}
	
	public void setClickListener(ClickListener listener) {
		this.clickListener = listener;
	}

	public void onEvent(Object listener, Object sender) {
		if (this.eventListener != null) {
			this.eventListener.onEvent(listener, sender);
		}
		
	}

	public void setSelectTemplate(boolean value) {
		this.selectTemplate = value;
	}
	public boolean isSelectTemplate() {
		return(this.selectTemplate);
	}
}
