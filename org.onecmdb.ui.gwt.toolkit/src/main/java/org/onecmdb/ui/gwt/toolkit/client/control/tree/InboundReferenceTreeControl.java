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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InboundReferenceTreeControl extends InheritanceTreeControl {

	private GWT_CiBean target;

	public InboundReferenceTreeControl(String rootAlias, GWT_CiBean target) {
		super(rootAlias);
		setFilterInstances(Boolean.TRUE);
		this.target = target;
		this.setMaxResult(null);
	}
	
	public Widget getWidget(Object data) {
		if (!(data instanceof GWT_CiBean)) {
			return(new Label("getChildCount(Object data): Not a correct data object!"));
		}
		GWT_CiBean bean = (GWT_CiBean)data;
		final HorizontalPanel hpanel =  new HorizontalPanel();
		hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
		hpanel.add(new CIDisplayNameWidget(bean));
		getReferenceCount(bean, new AsyncCallback() {

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Object result) {
				if (result instanceof Integer) {
					HTML instances = new HTML("[" + result + "]");
					instances.setStyleName("onecmdb-tree-counter-decoration");
					instances.setTitle("Total number of instances for this template");
					hpanel.add(instances);
				}
			}
			
		});
		return(hpanel);
	}

	private void getReferenceCount(GWT_CiBean bean, AsyncCallback callback) {
		GWT_QueryCriteria crit = new GWT_QueryCriteria();
		crit.setMatchCiInstances(true);
		crit.setOffspringDepth(new Integer(-1));
		OneCMDBConnector.getInstance().evalRelationCount(OneCMDBSession.getAuthToken(), 
				target, "<$template{" + bean.getAlias() + "}", crit, callback);
		
		
	}

	
	
	
}
