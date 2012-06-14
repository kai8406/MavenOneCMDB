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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.AttributeComparator;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;

public class DefaultAttributeFilter implements IAttributeFilter {
	
	protected List controls = null;
	protected boolean readonly = false;
	private ClickListener clickListener;
	
	public DefaultAttributeFilter() {
		System.out.println("New AttributeFilter");
	}
	
	public void setClickListener(ClickListener listener) {
		this.clickListener = listener;
	}
	
	public void setIsReadonly(boolean value) {
		System.out.println("Set READONLY:" + value);
		this.readonly = value;
	}
	
	/**
	 * Specifies a ordered list of <code>AttributeControl</code< objects.
	 * Each <code>AttributeControl</code> is responsible for rendering and 
	 * handle changes on the attribute. 
	 *  
	 * @param control
	 */
	public void setAttributeControl(List control) {
		this.controls  = control;
	}
	
	public void filterAttributes(GWT_CiBean template, GWT_CiBean bean, AsyncCallback callback) {
		List sorted = filterAttributes(template, bean);
		callback.onSuccess(sorted);
	}

	public List filterAttributes(GWT_CiBean template, GWT_CiBean bean) {
		// If not set return all attributes.
		if (this.controls == null) {
			System.out.println("Filter Attributes:" + this.readonly);
			AttributeControl attributeControl = new AttributeControl();
			attributeControl.setClickListener(this.clickListener);
			attributeControl.setReadonly(this.readonly);
			List result = new ArrayList();
			List sortedAttributes = new ArrayList(template.getAttributes());
			Collections.sort(sortedAttributes, new AttributeComparator());
			for (Iterator iter = sortedAttributes.iterator(); iter.hasNext();) {
				GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
				
				
				result.add(attributeControl.allocAttributeValue(bean, aBean));
			}
			// Add template attribute first....
			if (true) {
				AttributeControl readOnlyAttributeControl = new AttributeControl();
				readOnlyAttributeControl.setReadonly(true);
				AbstractAttributeValue displayName = readOnlyAttributeControl.allocAttributeValue(null, null);
				if (displayName instanceof AttributeValue) {
					AttributeValue aDisplay = (AttributeValue)displayName;
					aDisplay.getAttributeBean().setDisplayName("Display Name");
					aDisplay.getValueBean().setValue(bean.getDisplayName());
					result.add(0, aDisplay);
				}
				
				
				TextAttributeControl descCtrl = new TextAttributeControl("description", 
						this.readonly, 
						false, 
						TextAttributeControl.TEXT_AREA_TYPE,
						new Integer(5),
						null); 
				CIDescriptionValue description = new CIDescriptionValue(descCtrl, bean);
				result.add(1, description);
			}
			return(result);
		}
		
		HashMap map = new HashMap();
		for (Iterator iter = template.getAttributes().iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			map.put(aBean.getAlias(), aBean);	
		}
		
		List sorted = new ArrayList();
		for (int i = 0; i < controls.size(); i++) {
			AttributeControl ctrl = (AttributeControl)controls.get(i);
			String alias = ctrl.getAttrAlias();
			
			GWT_AttributeBean aBean = null;
			GWT_CiBean currentBean = bean;
			
			if (alias.startsWith("$")) {
				sorted.add(new ReferenceAttributeValue(ctrl, currentBean));
			} else {
				 aBean = (GWT_AttributeBean) map.get(alias);
				 sorted.add(ctrl.allocAttributeValue(currentBean, aBean));
			}
			
			
			/*
			if (aBean != null) {
				sorted.add(getAttributeValue(bean, aBean, ctrl));
			} else {
				// Dummy field.
				sorted.add(getDummyAttributeValue(ctrl));
			}
			*/
		}
		return(sorted);
	}




	public void setSimpleAttributeControl(String[] orders) {
		List ctrls = new ArrayList();
		for (int i = 0; i < orders.length; i++) {
			ctrls.add(new AttributeControl(orders[i], false, false));
		}
		setAttributeControl(ctrls);
	}
	
}
