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
package org.onecmdb.ui.gwt.toolkit.client.control.tree;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIDisplayNameWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InstanceInboundReferenceTreeControl extends A_GWT_TreeDataSourceControl {

	private GWT_CiBean target;

	public InstanceInboundReferenceTreeControl(GWT_CiBean target) {
		this.target = target;
	}
	
	public void getChildCount(Object o, AsyncCallback callback) {
		if (o instanceof GWT_CiBean) {
			// Find inbound reference types.
			OneCMDBConnector.getInstance().evalRelationCount(
					OneCMDBSession.getAuthToken(), 
					(GWT_CiBean)o, 
					"<$referenceTemplate", 
					getCriteria(),
					callback);
		}
		
		if (o instanceof Reference) {
			Reference r = (Reference)o;
			// Find inbound reference types.
			OneCMDBConnector.getInstance().evalRelationCount(
					OneCMDBSession.getAuthToken(), 
					(GWT_CiBean)r.getTarget(), 
					"<$referenceSource{" + r.getReference().getAlias() +"}", 
					getCriteria(),
					callback);
			
		}
	}

	public void getChildren(final Object o, Integer firstItem, final AsyncCallback callback) {
		// 
		if (o instanceof GWT_CiBean) {
			final GWT_CiBean bean = (GWT_CiBean)o;
			GWT_QueryCriteria crit = getCriteria();
			if (firstItem != null) {
				crit.setFirstResult(firstItem);
			}
			
			// Find inbound reference types.
			OneCMDBConnector.getInstance().evalRelation(
					OneCMDBSession.getAuthToken(), 
					bean, 
					"<$referenceTemplate", 
					crit,
					new AsyncCallback() {

						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						public void onSuccess(Object result) {
							
							if (result instanceof GWT_CiBean[]) {
								GWT_CiBean list[] = (GWT_CiBean[])result;
								Reference refs[] = new Reference[list.length];
								for (int i = 0; i< list.length; i++) {
									refs[i] = new Reference(bean, list[i]);
								}
								callback.onSuccess(refs);
								return;
							}
							callback.onFailure(new Exception("Unknown return type!"));
						}
					});
		}
		
		if (o instanceof Reference) {
			Reference r = (Reference)o;
			// Find inbound reference types.
			GWT_QueryCriteria crit = getCriteria();
			if (firstItem != null) {
				crit.setFirstResult(firstItem);
			}
			OneCMDBConnector.getInstance().evalRelation(
					OneCMDBSession.getAuthToken(), 
					r.getTarget(), 
					"<$referenceSource{" + r.getReference().getAlias() +"}", 
					crit,
					callback);
			
		}
	}

	private GWT_QueryCriteria getCriteria() {
		return(new GWT_QueryCriteria());
	}

	public void getRootObject(AsyncCallback callback) {
		callback.onSuccess(this.target);
	}

	public Widget getWidget(Object data) {
		if (data instanceof GWT_CiBean) {
			GWT_CiBean bean = (GWT_CiBean)data;
			HorizontalPanel hpanel =  new HorizontalPanel();
			hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
			hpanel.add(new CIDisplayNameWidget(bean, getClickListener()));
			return(hpanel);
		}
		if (data instanceof Reference) {
			Reference ref = (Reference)data;
			HorizontalPanel hpanel =  new HorizontalPanel();
			hpanel.add(new Image(OneCMDBUtils.getIconForCI(ref.getReference())));
			hpanel.add(new CIDisplayNameWidget(ref.getReference()));
			return(hpanel);
			
		}
		return(new Label("<unknown>"));
	}

	class Reference {
		GWT_CiBean ref;
		private GWT_CiBean target;
		
		public Reference(GWT_CiBean target, GWT_CiBean reference) {
			this.ref = reference;
			this.target = target;
		}

		public GWT_CiBean getReference() {
			return(this.ref);
		}

		public GWT_CiBean getTarget() {
			return(this.target);
		}
	}

}
