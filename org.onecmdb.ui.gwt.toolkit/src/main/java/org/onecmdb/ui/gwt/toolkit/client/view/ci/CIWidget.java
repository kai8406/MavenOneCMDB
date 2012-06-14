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
package org.onecmdb.ui.gwt.toolkit.client.view.ci;

import java.util.HashMap;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class CIWidget extends Composite {
	
	private static HashMap beanCache = new HashMap();
	
	protected HorizontalPanel vPanel;
	protected String alias;
	private LoadListener loadListener = null;
	
	protected CIWidget() {
		vPanel = new HorizontalPanel();
		vPanel.add(new Label("Loading..."));
		initWidget(vPanel);
	}
	
	public void setLoadListener(LoadListener l) {
		this.loadListener = l;
		
	}
	
	public CIWidget(final String alias) {
		this();
		this.alias = alias;
	}
	
	public GWT_CiBean getCI() {
		return((GWT_CiBean)beanCache.get(alias));
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void update() {
		if (alias == null || alias.length() == 0) {
			loadAndFireEvent(null);
			return;
		}
		if (OneCMDBUtils.getSimpleTypesAsList().contains(alias)) {
			GWT_CiBean bean = new GWT_CiBean("SimpleType", alias, true);
			GWT_ValueBean icon = new GWT_ValueBean("icon", alias.replaceFirst(":", ""), false);
			bean.addAttributeValue(icon);
			bean.setDisplayName(alias);
			loadAndFireEvent(bean);
			return;
		}
		GWT_CiBean bean = (GWT_CiBean) beanCache.get(alias);
		
		if (bean != null) {
			loadAndFireEvent(bean);
			return;
		}
		
		OneCMDBConnector.getCIFromAlias(alias, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				vPanel.clear();
				vPanel.add(new Label("ERROR: " + caught.getMessage()));
			}

			public void onSuccess(Object result) {
				
				if (result instanceof GWT_CiBean) {
					GWT_CiBean bean = (GWT_CiBean)result;
					beanCache.put(bean.getAlias(), bean);
					loadAndFireEvent(bean);
					return;
				}
				
				onFailure(new Exception("Illegal value type or null<" + alias + ">"));
			}

		});
	}
	
	public CIWidget(GWT_CiBean bean) {
		this();
		if (bean != null) {
			this.alias = bean.getAlias();
			beanCache.put(bean.getAlias(), bean);
		}
	}
	
	public abstract void load(GWT_CiBean bean);
	
	private void loadAndFireEvent(GWT_CiBean bean) {
		if (loadListener != null) {
			loadListener.onLoadComplete(bean);
		}
		load(bean);
	}

}
