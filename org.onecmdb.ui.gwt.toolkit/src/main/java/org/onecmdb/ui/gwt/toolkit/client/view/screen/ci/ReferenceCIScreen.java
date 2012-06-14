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
package org.onecmdb.ui.gwt.toolkit.client.view.screen.ci;

import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InstanceInboundReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InstanceReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.CITreeWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.ReverseTreeTable;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReferenceCIScreen extends OneCMDBBaseScreen {

	
	private HorizontalPanel relationPanel;
	private GWT_CiBean bean;

	public ReferenceCIScreen() {
		setTitleText("References for");
		relationPanel = new HorizontalPanel();
		relationPanel.setHeight("100%");
		dockPanel.add(relationPanel, DockPanel.NORTH);
		dockPanel.setCellHeight(relationPanel, "100%");
		initWidget(dockPanel);
	}

	public void load(String objectType, Long objectId) {
		setErrorText("");
		OneCMDBConnector.getCIFromAlias(objectType, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("Load Error: " + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					bean = (GWT_CiBean)result;
					update();
				}
			}
		});
	}
	
	protected void update() {
		setTitleWidget(new CIIconDisplayNameWidget(bean));
		relationPanel.clear();
		InstanceReferenceTreeControl outboundTreeControl = new InstanceReferenceTreeControl();
		outboundTreeControl.setClickListener(getReferenceClickListener());
		outboundTreeControl.setRootInstance(bean);
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
		VerticalPanel outboundPanel = new VerticalPanel();
		outboundPanel.setHeight("100%");
		outboundPanel.setStyleName("mdv-form");
		outboundPanel.add(outboundLabel);
		outboundPanel.add(outboundTreeWidget);
		outboundPanel.setCellHeight(outboundTreeWidget, "100%");
		outboundPanel.setCellVerticalAlignment(outboundTreeWidget, VerticalPanel.ALIGN_TOP);
	
		
		
		Label inboundLabel = new Label("Inbound References");
		inboundLabel.setStyleName("one-screen-header-title");
		
		InstanceInboundReferenceTreeControl inboundTreeControl = new InstanceInboundReferenceTreeControl(bean);
		inboundTreeControl.setClickListener(getReferenceClickListener());
		inboundTreeControl.setHideRoot(true);
		inboundTreeControl.setShowSearch(false);
		
		ReverseTreeTable inboundTreeWidget = new ReverseTreeTable(inboundTreeControl);
		
	
		VerticalPanel inboundPanel = new VerticalPanel();
		inboundPanel.setHeight("100%");
		inboundPanel.setStyleName("mdv-form");
		inboundPanel.add(inboundLabel);
		inboundPanel.add(inboundTreeWidget);
		inboundPanel.setCellHeight(inboundTreeWidget, "100%");
		inboundPanel.setCellVerticalAlignment(inboundTreeWidget, VerticalPanel.ALIGN_TOP);
		
		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.setHeight("100%");
		centerPanel.setStyleName("mdv-form");
		Label centerLabel = new Label("CI");
		centerLabel.setStyleName("one-screen-header-title");
		centerPanel.add(centerLabel);
		CIIconDisplayNameWidget widget = new CIIconDisplayNameWidget(bean, getCenterClickListener());
		centerPanel.add(widget);
		centerPanel.setCellHeight(widget, "100%");
		centerPanel.setCellVerticalAlignment(widget, VerticalPanel.ALIGN_TOP);
		
		relationPanel.add(inboundPanel);
		relationPanel.add(centerPanel);
		relationPanel.add(outboundPanel);
	
	}

	private ClickListener getCenterClickListener() {
		return(new ClickListener() {

			public void onClick(Widget sender) {
				if (sender instanceof CIWidget) {
					GWT_CiBean bean = ((CIWidget)sender).getCI();
					if (bean != null) {
						getBaseEntryScreen().showScreen(OneCMDBApplication.VIEW_CI_SCREEN, bean.getAlias(), new Long(0));
					}
				}
			}
			
		});
	}

	private ClickListener getReferenceClickListener() {
		return(new ClickListener() {

			public void onClick(Widget sender) {
				if (sender instanceof CIWidget) {
					GWT_CiBean bean = ((CIWidget)sender).getCI();
					if (bean != null) {
						getBaseEntryScreen().showScreen(OneCMDBApplication.REFERENCE_CI_SCREEN, bean.getAlias(), new Long(0));
					}
				}
			}
			
		});
	}
	
}
