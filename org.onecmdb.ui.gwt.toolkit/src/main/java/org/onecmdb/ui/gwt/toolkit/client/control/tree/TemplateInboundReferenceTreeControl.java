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

public class TemplateInboundReferenceTreeControl extends ReferenceTreeControl {
	
	private GWT_CiBean template;

	public void setRootTemplate(GWT_CiBean bean) {
		this.template = bean;
	}
	
	public void getChildCount(Object o, final AsyncCallback callback) {
		
		if (o instanceof GWT_CiBean) {
			final GWT_CiBean bean = (GWT_CiBean)o;
			// Find inbound reference types.
			GWT_QueryCriteria crit = new GWT_QueryCriteria();
			crit.setMatchType(bean.getAlias());
			crit.setMatchCiTemplates(true);
			
			OneCMDBConnector.getInstance().searchCount(
					OneCMDBSession.getAuthToken(), 
					crit,
					callback);
		}
	}

	public void getChildren(final Object o, Integer firstItem, final AsyncCallback callback) {
		// 
		if (o instanceof GWT_CiBean) {
			final GWT_CiBean bean = (GWT_CiBean)o;
			// Find inbound reference types.
			GWT_QueryCriteria crit = new GWT_QueryCriteria();
			crit.setMatchType(bean.getAlias());
			crit.setMatchCiTemplates(true);
			crit.setMatchCiPath("/Root/Ci");
			
			OneCMDBConnector.getInstance().search(
					OneCMDBSession.getAuthToken(), 
					crit, callback);
		}
	}

	private GWT_QueryCriteria getCriteria() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getRootObject(AsyncCallback callback) {
		callback.onSuccess(this.template);
	}
	
	
	public Widget getWidget(Object data) {
		if (data instanceof GWT_CiBean) {
			GWT_CiBean bean = (GWT_CiBean)data;
			HorizontalPanel hpanel =  new HorizontalPanel();
			hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
			hpanel.add(new CIDisplayNameWidget(bean));
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
