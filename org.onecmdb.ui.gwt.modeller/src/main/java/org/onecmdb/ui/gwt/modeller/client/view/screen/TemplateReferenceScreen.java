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

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InstanceInboundReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InstanceReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.ReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.TemplateInboundReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.CITreeWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TemplateReferenceScreen extends OneCMDBBaseScreen {

	private GWT_CiBean currentItem;
	private VerticalPanel vPanel;
	
	public TemplateReferenceScreen() {
		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		dockPanel.add(vPanel, DockPanel.CENTER);
		initWidget(dockPanel);
	}

	public void setRoot(GWT_CiBean root) {
		this.currentItem = root;
	}
	
	public void load() {
		if (currentItem == null) {
			setErrorText("ERROR: No CI set!");
			return;
		}
		vPanel.clear();
		
		ReferenceTreeControl outboundTreeControl = new ReferenceTreeControl();
		outboundTreeControl.setRoot(currentItem);
		outboundTreeControl.setHideRoot(true);
		CITreeWidget outboundTreeWidget = new CITreeWidget(outboundTreeControl);
		
		Label outboundLabel = new Label("Outbound References");
		outboundLabel.setStyleName("one-screen-header-title");
		/*
		HTML click = new HTML("<a href ='javascript:;'>inbound</a>");
		click.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBApplication.REFERENCE_CI_SCREEN, control.getBase().getAlias(), new Long(0));
			}
		});
		*/
		vPanel.add(outboundLabel);
		vPanel.add(outboundTreeWidget);
	
		Label inboundLabel = new Label("Inbound References");
		inboundLabel.setStyleName("one-screen-header-title");
		
		vPanel.add(inboundLabel);
		TemplateInboundReferenceTreeControl inboundTreeControl = new TemplateInboundReferenceTreeControl();
		inboundTreeControl.setRootTemplate(currentItem);
		inboundTreeControl.setHideRoot(true);
		inboundTreeControl.setShowSearch(false);
		CITreeWidget inboundTreeWidget = new CITreeWidget(inboundTreeControl);
		vPanel.add(inboundTreeWidget);
		/*
		relationPanel.add(inboundTreeWidget);
		// Create a template Tree.
		ReferenceTreeControl control = new ReferenceTreeControl();
		control.setRoot(currentItem);
		
		CITreeWidget treeWidget = new CITreeWidget(control);
		vPanel.clear();
		vPanel.add(treeWidget);
		*/
	}
	
	public void load(String objectType, Long objectId) {
		if (currentItem != null) {
			if (currentItem.getAlias().equals(objectType)) {
				//load();
				return;
			}
		}
		OneCMDBConnector.getCIFromAlias(objectType, new AsyncCallback() {
	
			public void onFailure(Throwable caught) {
				setErrorText("ERROR:" + caught);
			}
	
			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					currentItem = (GWT_CiBean)result;
					load();
					return;
				}
			}
		});
	}
}
