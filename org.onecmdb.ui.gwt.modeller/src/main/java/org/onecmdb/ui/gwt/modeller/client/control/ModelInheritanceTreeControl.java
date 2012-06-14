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
package org.onecmdb.ui.gwt.modeller.client.control;


import org.onecmdb.ui.gwt.modeller.client.OneCMDBModelCreator;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InheritanceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class ModelInheritanceTreeControl extends InheritanceTreeControl {
	
	public ModelInheritanceTreeControl() {
		super();
		// Always show all.
		setMaxResult(null);
	}
	
	public Widget getWidget(final Object data) {
		HorizontalPanel hpanel = (HorizontalPanel) super.getWidget(data);
		if (data instanceof GWT_CiBean) {
			final GWT_CiBean bean = (GWT_CiBean)data;
		
			final Image popup = new Image("images/eclipse/tree_menu2.gif");
			hpanel.add(popup);
			hpanel.setCellVerticalAlignment(popup, VerticalPanel.ALIGN_MIDDLE);
			popup.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					System.out.println("Menu popup...");
					final PopupPanel p = new PopupPanel(true);
					//p.setStyleName("popup-menu");
					Command newInstance = new Command() {
						public void execute() {
							p.hide();
							OneCMDBModelCreator.get().showScreen(OneCMDBModelCreator.NEW_INSTANCE_SCREEN, bean.getAlias(), new Long(0));
						}
					};
					Command newTemplate = new Command() {
						public void execute() {
							p.hide();
							OneCMDBModelCreator.get().showScreen(OneCMDBModelCreator.NEW_TEMPLATE_SCREEN, bean.getAlias(), new Long(0));
						}
					};
					Command delete = new Command() {
						public void execute() {
							p.hide();
							delete(bean);
						}
					};

					// Make some sub-menus that we will cascade from the top menu.
					MenuBar fooMenu = new MenuBar(true);
					if (bean.isTemplate()) {
						fooMenu.addItem("New Instance", newInstance);
						fooMenu.addItem("New Template", newTemplate);
					}
					fooMenu.addItem("Delete", delete);


					p.setPopupPosition(popup.getAbsoluteLeft(), popup.getAbsoluteTop());
					p.setWidget(fooMenu);
					p.show();
				}

			});
		}
		return(hpanel);
	}
	
	protected void delete(final GWT_CiBean bean) {
		if (Window.confirm("Delete CI " + bean.getDisplayName() + "?")) {
			// Call update of attribute.
			OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(),
					null,
					new GWT_CiBean[] {bean},
					new AsyncCallback() {

						public void onFailure(Throwable caught) {
							Window.alert("ERROR: " + caught);
						}

						public void onSuccess(Object result) {
							if (result instanceof GWT_RfcResult) {
								GWT_RfcResult rfcResult = (GWT_RfcResult)result;
								if (!rfcResult.isRejected()) {
									Window.alert(bean.getDisplayName() + " deleted!");
									// Fire reload.
									return;
								}
								Window.alert("ERROR: " + rfcResult.getRejectCause());
								return;
							}
							Window.alert("ERROR: Unknown result object!");
						}
					}
			);
		}
	}


	public boolean showSearch() {
		return(false);
	}

	
	
	
}
