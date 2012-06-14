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
import org.onecmdb.ui.gwt.modeller.client.model.TemplateCache;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.BaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ListCIScreen;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TemplateViewScreen extends OneCMDBBaseScreen implements TabListener {

	
	private static final int REF_NAME_COL = 0;
	private static final int REF_CLASS_COL = 0;
	private static final int REF_ATTR_COL = 0;
	
	ListAttributeScreen attributeScreen = new ListAttributeScreen();
	TemplateReferenceScreen referenceScreen = new TemplateReferenceScreen();
	ListCIScreen instancesScreen = new ListCIScreen();
	
	
	private TabPanel tab;
	private String objectType;
	private Long objectId;
	private VerticalPanel infoPanel;
	private VerticalPanel vPanel;
	private DisclosurePanel attrDisc;
	private DisclosurePanel refDisc;
	private DisclosurePanel instancesDisc;
	private DisclosurePanel infoDisc;
	
	public TemplateViewScreen() {
		super();
		/*
		if (true) {
			styleTwo();
			return;
		}
		*/
		vPanel = new VerticalPanel();
		/*
		HorizontalPanel editPanel = new HorizontalPanel();
		HTML edit = new HTML("[<a href='javascript:;'>edit</a>]");
		edit.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBModelCreator.EDIT_TEMPLATE_SCREEN, objectType, new Long(0));
				
			}
			
		});
		editPanel.add(edit);
		editPanel.setCellHorizontalAlignment(edit, HorizontalPanel.ALIGN_RIGHT);
		editPanel.setWidth("100%");
		*/
		infoPanel = new VerticalPanel();
		infoPanel.setStyleName("mdv-form");
		infoPanel.setWidth("100%");
		
		attributeScreen.setBaseEntryScreen(OneCMDBModelCreator.get());
		referenceScreen.setBaseEntryScreen(OneCMDBModelCreator.get());
		instancesScreen.setBaseEntryScreen(OneCMDBModelCreator.get());
		tab = new TabPanel();
		tab.add(infoPanel, "Info");
		tab.add(attributeScreen, "Attributes");
		tab.add(referenceScreen, "References");
		tab.add(instancesScreen, "Instances");
		instancesScreen.setNewSupport(true);
		tab.addTabListener(this);
		tab.selectTab(1);
		
		//vPanel.add(infoPanel);
		vPanel.add(tab);
		
		dockPanel.add(vPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		dockPanel.setSize("100%", "100%");
		//ScrollPanel p = new ScrollPanel(dockPanel);
		//p.setAlwaysShowScrollBars(true);
		initWidget(dockPanel);
	}
	
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
		return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
		Widget selected = tab.getWidget(tabIndex);
		if (selected instanceof BaseScreen) {
			((BaseScreen)selected).load(objectType, objectId);
		}
	}

	
	public void load(final String objectType, final Long objectId) {
		setErrorText("");
		this.objectType = objectType;
		this.objectId = objectId;
		
		if (tab != null) {
			tab.selectTab(tab.getTabBar().getSelectedTab());
		}
		
		TemplateCache.load(objectType, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR:" + caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					GWT_CiBean bean = (GWT_CiBean)result;
					if (bean.isTemplate()) {
						setTitleText("Template");
					} else {
						setTitleText("Instance");
					}
					
					setTitleWidget(new CIIconDisplayNameWidget(bean));
						
					
					infoPanel.clear();
					HorizontalPanel editPanel = new HorizontalPanel();
					HTML edit = new HTML("[<a href='javascript:;'>edit</a>]");
					edit.setTitle("Edit this template.");
					edit.addClickListener(new ClickListener() {

						public void onClick(Widget sender) {
							getBaseEntryScreen().showScreen(OneCMDBModelCreator.EDIT_TEMPLATE_SCREEN, objectType, new Long(0));

						}

					});
					infoPanel.add(edit);
					infoPanel.setCellHorizontalAlignment(edit, HorizontalPanel.ALIGN_RIGHT);
				
					infoPanel.add(getField("ID", "" + bean.getId()));
					infoPanel.add(getField("Alias", bean.getAlias()));
					infoPanel.add(getField("Description", bean.getDescription()));
					infoPanel.add(getField("Display Name Expr.", bean.getDisplayNameExpression()));
					
					infoPanel.setWidth("100%");
				}
			}

			private Widget getField(String l, String value) {
				HorizontalPanel hPanel = new HorizontalPanel();
				Label label = new Label(l, true);
				label.setStyleName("mdv-form-label");
				
				Label valueLabel = new Label(value, true);
				valueLabel.setStyleName("mdv-form-input");
				
				hPanel.add(label);
				hPanel.add(valueLabel);
				
				return(hPanel);
			
			}
			
		});
	}

	
}
