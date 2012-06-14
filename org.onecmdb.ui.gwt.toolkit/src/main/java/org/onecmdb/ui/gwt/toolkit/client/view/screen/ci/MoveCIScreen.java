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

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIAttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InheritanceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.DragablePopup;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.CITreeWidget;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MoveCIScreen extends OneCMDBBaseScreen {
	private GWT_CiBean ci;
	private GWT_CiBean newTemplate;
	private GWT_CiBean template;
	
	private HorizontalPanel hPanel;
	
	public MoveCIScreen() {
		super();
		hPanel = new HorizontalPanel();
		dockPanel.add(hPanel, DockPanel.NORTH);
		initWidget(dockPanel);
		setTitleText("Classify");
	}
	
	public void load(String objectType, Long objectId) {
		OneCMDBConnector.getCIFromAlias(objectType, new AsyncCallback() {


			public void onFailure(Throwable caught) {
				setErrorText("ERROR:" + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					ci = (GWT_CiBean)result;
					OneCMDBConnector.getCIFromAlias(ci.getDerivedFrom(), new AsyncCallback() {

						public void onFailure(Throwable caught) {
							setErrorText("ERROR:" + caught);
							
						}

						public void onSuccess(Object result) {
							if (result instanceof GWT_CiBean) {
								template = (GWT_CiBean)result;
								update();
							}
								
						}
					});
				}
			}
		});
	}

	protected void update() {
		setTitleWidget(new CIIconDisplayNameWidget(template));
		hPanel.clear();
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setStyleName("mdv-form");
		vPanel.add(new HTML("<h3>Classify CI <i>" + ci.getDisplayName() + "</i><h3>"));
		HorizontalPanel currentPanel = new HorizontalPanel();
		currentPanel.add(new Label("Current Template: "));
		currentPanel.add(new CIIconDisplayNameWidget(template));
		vPanel.add(currentPanel);
		
		final HorizontalPanel newPanel = new HorizontalPanel();
		final HTML newTemplateHTML = new HTML("<a href='javascript:;'>Change to Template </a>"); 
		newPanel.add(newTemplateHTML);
		newTemplateHTML.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				final DragablePopup popup = new DragablePopup("Select template", false);
				InheritanceTreeControl control = new InheritanceTreeControl(ci.getDerivedFrom());
				control.setFilterInstances(Boolean.TRUE);
				control.setTreeListener(new TreeListener() {

					public void onTreeItemSelected(TreeItem item) {
						if (item.getUserObject() instanceof GWT_CiBean) {
							newPanel.clear();
							newTemplate = (GWT_CiBean)item.getUserObject();
							
							newPanel.add(newTemplateHTML);
							newPanel.add(new CIIconDisplayNameWidget(newTemplate));
							popup.hide();
						}
					}

					public void onTreeItemStateChanged(TreeItem item) {
						// TODO Auto-generated method stub
						
					}
					
				});
				
				CITreeWidget templateTreeWidget = new CITreeWidget(control);
				popup.setContent(templateTreeWidget);
				int left = sender.getAbsoluteLeft() + sender.getOffsetWidth();
				int top = sender.getAbsoluteTop() + sender.getOffsetHeight();
				
				popup.setPopupPosition(left, top);
				popup.show();
			}
			
		});
		vPanel.add(newPanel);
		
		HorizontalPanel bpanel = new HorizontalPanel();
		Button cancel = new Button("CANCEL");
		cancel.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				History.back();
			}
		});
		Button ok = new Button("OK");
		ok.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				CIAttributeValueInputControl control = new CIAttributeValueInputControl(ci);
				control.getLocal().setDerivedFrom(newTemplate.getAlias());
				control.commit(new AsyncCallback() {

					public void onFailure(Throwable caught) {
						setErrorText("ERROR: " + caught);
						
					}

					public void onSuccess(Object result) {
						History.back();
					}
				});
			}
		});
		bpanel.add(ok);
		bpanel.add(cancel);
		
		vPanel.add(bpanel);
		
		hPanel.add(vPanel);
		
		
		
		
	}
	
	

}
