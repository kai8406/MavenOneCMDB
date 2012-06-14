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
package org.onecmdb.ui.gwt.modeller.client.view.screen;


import org.onecmdb.ui.gwt.modeller.client.OneCMDBModelCreator;
import org.onecmdb.ui.gwt.modeller.client.control.ModelInheritanceTreeControl;
import org.onecmdb.ui.gwt.modeller.client.model.TemplateCache;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.CITreeWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class TemplateBrowserScreen extends OneCMDBBaseScreen implements TreeListener, ChangeListener {
	
	VerticalPanel left = new VerticalPanel();
	VerticalPanel center = new VerticalPanel();
	private GWT_CiBean currentItem = null;
	private CITreeWidget treeWidget;
	private Label title;
	
	public TemplateBrowserScreen() {
		super();
		left.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		initWidget(left);
		TemplateCache.addChangeListener(this);
	}

	
	public void load() {
		if (this.currentItem != null) {
			String alias = this.currentItem.getAlias();
			this.currentItem = null;
			load(alias, null);
		} else {
			// Create a template Tree.
			ModelInheritanceTreeControl control = new ModelInheritanceTreeControl();
			control.setFilterInstances(Boolean.TRUE);
			control.setRootState(true);
			control.setTreeListener(this);
			treeWidget = new CITreeWidget(control);
			
			left.add(treeWidget);
			left.setCellHeight(treeWidget, "100%");
			left.setCellVerticalAlignment(treeWidget, VerticalPanel.ALIGN_TOP);
			
		}
	}

	
	public void load(String objectType, Long objectId) {
		if (currentItem != null) {
			if (currentItem.getAlias().equals(objectType)) {
				loadCenter(currentItem);
				return;
			}
		}
		OneCMDBConnector.getCIFromAlias(objectType, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR:" + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					loadCenter((GWT_CiBean)result);
					return;
				}
			}
		});
	}


	protected void loadCenter(GWT_CiBean bean) {
		if (bean.isTemplate()) {
			TemplateCache.add(bean.getAlias(), bean);
			OneCMDBModelCreator.get().showScreen(OneCMDBModelCreator.TEMPLATE_VIEW_SCREEN, bean.getAlias(), new Long(0));
		}
	}


	public void onTreeItemSelected(TreeItem item) {
		Object data = item.getUserObject();
		if (data instanceof GWT_CiBean) {
			currentItem  = (GWT_CiBean)data;
			load(currentItem.getAlias(), new Long(0));
		}
	}


	public void onTreeItemStateChanged(TreeItem item) {
	}


	public void onChange(Widget sender) {
		if (treeWidget != null) {
			treeWidget.reload();
		}
	}
	
	
	
}
