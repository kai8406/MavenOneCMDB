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
package org.onecmdb.ui.gwt.toolkit.client.control.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.MultipleAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SelectMultipleDataSourceControl extends SelectInheritanceDataSourceControl {

	private MultipleAttributeValue mValue;
	private HashMap valueMap = new HashMap();
	
	public SelectMultipleDataSourceControl(MultipleAttributeValue mValue) {
		super(mValue.getType());
		this.mValue = mValue;
		for (Iterator iter = this.mValue.getAttributeValues().iterator(); iter.hasNext();) {
			AttributeValue aValue = (AttributeValue) iter.next();
			valueMap.put(aValue.getStringValue(), aValue);
		}
	}
	
	protected boolean isMultiSelect() {
		return true;
	}

	public Widget getWidget(Object data) {
		if (!(data instanceof GWT_CiBean)) {
			return(new Label("getChildCount(Object data): Not a correct data object!"));
		}
		final GWT_CiBean bean = (GWT_CiBean)data;
		HorizontalPanel hpanel =  new HorizontalPanel();
		
		if (selectInstances() && (!bean.isTemplate())) {
			final CheckBox cb = new CheckBox();
			cb.setChecked(isChecked(bean));
			hpanel.add(cb);
			cb.addClickListener(new ClickListener() {
	
				public void onClick(Widget sender) {
					addChecked(bean, cb.isChecked());
				}
	
			});
		}
		hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
		Label label = new Label(bean.getDisplayName());
		hpanel.add(label);
	
		return(hpanel);
	}

	protected boolean selectInstances() {
		return(true);
	}

	private boolean isChecked(GWT_CiBean bean) {
		return(valueMap.containsKey(bean.getAlias()));
	}
	
	private void addChecked(GWT_CiBean bean, boolean selected) {
		if (selected) {
			if (!valueMap.containsKey(bean.getAlias())) {
				AttributeValue aValue = mValue.newAttributeValue(bean.getAlias());
				aValue.setValueAsCI(bean);
				valueMap.put(bean.getAlias(), aValue);
			}
		} else {
			valueMap.remove(bean.getAlias());
		}
		
	}
	
	public List getSelection() {
		List values = new ArrayList();
		values.addAll(valueMap.values());
		return(values);
	}


}
