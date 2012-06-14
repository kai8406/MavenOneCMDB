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



import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.ISelectListener;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InheritanceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.NullCIBean;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SelectInheritanceDataSourceControl extends InheritanceTreeControl {
	
	private ISelectListener callback;
	private boolean selectInstances;
	private boolean requiered;


	public SelectInheritanceDataSourceControl(String rootAlias) {
		super(rootAlias);
	}
	
	public void setSelectListener(ISelectListener callback) {
		this.callback = callback;
	}

	public void setSelectInstances(boolean value) {
		this.selectInstances = value;
	}
	
	public ISelectListener getSelectListener() {
		return(this.callback);
	}
	
	
	public boolean isRequiered() {
		return requiered;
	}

	public void setRequiered(boolean requiered) {
		this.requiered = requiered;
	}

	public void getRootObject(final AsyncCallback callback) {
		if (isMultiSelect()) {
			super.getRootObject(callback);
			return;
		}
		if (isRequiered()) {
			super.getRootObject(callback);
			return;
		}
		// Else show empty
		super.getRootObject(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			public void onSuccess(Object result) {
				NullCIBean nullBean = new NullCIBean();
				nullBean.setTemplate(selectInstances);
			
				if (result instanceof GWT_CiBean[]) {
					Object resultList[] = (Object[])result;
					Object newResultList[] = new Object[resultList.length + 1];
					newResultList[0] = nullBean;
					
					for (int i = 0; i < resultList.length; i++) {
						if (i == 0) {
							nullBean.addAttributeValue(((GWT_CiBean)resultList[i]).fetchAttributeValueBean("icon", 0));
						}
						newResultList[i+1] = resultList[i];
					}
					callback.onSuccess(newResultList);
					return;
					
				}
				if (result instanceof GWT_CiBean) {
					nullBean.addAttributeValue(((GWT_CiBean)result).fetchAttributeValueBean("icon", 0));
					Object newResult[] = new Object[2];
					newResult[0] = nullBean;
					newResult[1] = result;
					callback.onSuccess(newResult);
					return;
				}
				
			}
			
		});
	}

	
	
	protected boolean isMultiSelect() {
		return false;
	}

	/**
	 * Override widget to add check box button.
	 */
	public Widget getWidget(Object data) {
		if (!(data instanceof GWT_CiBean)) {
			return(new Label("getChildCount(Object data): Not a correct data object!"));
		}
		final GWT_CiBean bean = (GWT_CiBean)data;
		HorizontalPanel hpanel =  new HorizontalPanel();
		hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
		Label label = new Label(bean.getDisplayName());
		hpanel.add(label);
		//final Image popup = new Image("images/select_me.gif");
		//hpanel.add(popup);
		label.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				if (callback != null) {
					if (selectInstances != bean.isTemplate()) {
						return;
					}
					callback.onSelect(bean);
				}
			}
		});
		return(hpanel);
	}

	public void onSelect(Object selected) {
		// TODO Auto-generated method stub
		
	}

	

}
