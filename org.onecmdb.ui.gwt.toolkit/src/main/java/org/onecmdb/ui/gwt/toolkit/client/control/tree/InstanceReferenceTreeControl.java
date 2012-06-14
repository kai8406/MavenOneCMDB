/*
 * OneCMDB, an open source configuration management project.
 * as indicated by the @authors tag. See the copyright.txt in the
 * Copyright 2007, Lokomo Systems AB, and individual contributors
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
package org.onecmdb.ui.gwt.toolkit.client.control.tree;

import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

import com.google.gwt.user.client.rpc.AsyncCallback;


public class InstanceReferenceTreeControl extends ReferenceTreeControl {
	protected GWT_CiBean instance;
	
	public InstanceReferenceTreeControl() {
	}
	
	
	protected void addReferenceValues(List to, GWT_CiBean bean, GWT_AttributeBean aBean) {
		if (!aBean.isComplexType()) {
			return;
		}
		List values = bean.fetchAttributeValueBeans(aBean.getAlias());
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			GWT_ValueBean value = (GWT_ValueBean) iter.next();
			if (value.getValue() != null && value.getValue().length() > 0) {
				to.add(value.getValue());
			}
		}
	}

	public void setRootInstance(GWT_CiBean bean) {
		this.instance = bean;
	}
	
	
	public void getRootObject(final AsyncCallback callback) {
		// ...
		if (this.instance == null) {
			callback.onFailure(new Exception("No instance specified!"));
			return;
		}
		
		if (this.root == null) {
			OneCMDBConnector.getCIFromAlias(this.instance.getDerivedFrom(), new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						root = (GWT_CiBean)result;
						callback.onSuccess(instance);
					}
					
				}
				
			});
			return;
		}
		callback.onSuccess(instance);
	}


	
	
	
	
}
