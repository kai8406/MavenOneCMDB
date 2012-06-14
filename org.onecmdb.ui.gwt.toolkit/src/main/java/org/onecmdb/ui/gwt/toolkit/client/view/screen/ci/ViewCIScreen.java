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
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIAttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InstanceInboundReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InstanceReferenceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.CITreeWidget;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class ViewCIScreen extends EditCIScreen {

	private VerticalPanel relationPanel;
	
	public ViewCIScreen() {
		setTitleText("View CI");
	}
	
	protected CellPanel getMainPanel() {
		return(new HorizontalPanel());
	}
	
	
	protected CIAttributeValueInputControl getControl() {
		//if (this.control == null) {
		this.control = new CIAttributeValueInputControl(templateAlias, isNew());
		return(this.control);
	}
	
	
	
	public IAttributeFilter getAttributeFilter() {
		DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
		aFilter.setIsReadonly(true);
		return(aFilter);	
	}

	protected Widget getButtonPanel() {
		// Add edit button.
		VerticalPanel actionPanel = new VerticalPanel();
		
		/*
		relationPanel = new VerticalPanel();
		relationPanel.setStyleName("mdv-form");
		*/
		
		//HorizontalPanel hPanel = new HorizontalPanel();
		
		HTML edit = new HTML("[<a href='javascript:;'>edit</a>]");
		edit.setTitle("Edit this instance");
		HTML delete = new HTML("[<a href='javascript:;'>delete</a>]");
		delete.setTitle("Delete this instance");
		HTML move = new HTML("[<a href='javascript:;'>classify</a>]");
		move.setTitle("Organize this instance.\nThis means that the CI can be moved to another template");
		HTML reference = new HTML("[<a href='javascript:;'>show references</a>]");
		reference.setTitle("Show inbound/outbound reference for this CI.");
		
		edit.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBApplication.EDIT_CI_SCREEN, 
						control.getBase().getAlias(),
						new Long(0)); 		
			}
			
		});	
		move.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBApplication.MOVE_CI_SCREEN, 
						control.getBase().getAlias(),
						new Long(0)); 		
			}
			
		});	

		delete.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (Window.confirm("Delete " + control.getBase().getDisplayName()  +"\nAre you sure?")) {
					control.delete(new AsyncCallback() {

						public void onFailure(Throwable caught) {
							setErrorText("ERROR: " + caught);
							
						}

						public void onSuccess(Object result) {
							History.back();
						}
					});
				}
			}
		});	
		reference.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					getBaseEntryScreen().showScreen(OneCMDBApplication.REFERENCE_CI_SCREEN, 
							control.getBase().getAlias(),
							new Long(0)); 		
				}
		});	
		
		actionPanel.add(edit);
		actionPanel.add(delete);
		actionPanel.add(move);
		actionPanel.add(reference);
		return(actionPanel);
	}

	
	protected boolean isNew() {
		return(false);
	}
	
	public void onLoadComplete(Object sender) {
		super.onLoadComplete(sender);
		setTitleText("View");
		//setTitleWidget(new CIIconDisplayNameWidget(control.getBase()));
		
		if (true) {
			return;
		}
		relationPanel.clear();
		InstanceReferenceTreeControl outboundTreeControl = new InstanceReferenceTreeControl();
		outboundTreeControl.setRootInstance(this.control.getBase());
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
		relationPanel.add(outboundLabel);
		relationPanel.add(outboundTreeWidget);
	
		Label inboundLabel = new Label("Inbound References");
		inboundLabel.setStyleName("one-screen-header-title");
		
		relationPanel.add(inboundLabel);
		InstanceInboundReferenceTreeControl inboundTreeControl = new InstanceInboundReferenceTreeControl(this.control.getBase());
		inboundTreeControl.setHideRoot(true);
		inboundTreeControl.setShowSearch(false);
		CITreeWidget inboundTreeWidget = new CITreeWidget(inboundTreeControl);
		relationPanel.add(inboundTreeWidget);
	}

	

	
	
}
