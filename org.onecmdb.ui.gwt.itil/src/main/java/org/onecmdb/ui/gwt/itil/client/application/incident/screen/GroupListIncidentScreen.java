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
package org.onecmdb.ui.gwt.itil.client.application.incident.screen;

import org.onecmdb.ui.gwt.toolkit.client.control.table.CIReferenceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.LabelCounter;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GroupListIncidentScreen extends OneCMDBBaseScreen {
	
	private LabelCounter newIncidentLabel;
	private LabelCounter uiIncidentLabel;
	private LabelCounter problemIncidentLabel;
	private LabelCounter knownErrorIncidentLabel;
	private LabelCounter closedIncidentLabel;
	private TabPanel tab;

	public GroupListIncidentScreen() {
		this.setTitleText("List Incidents by Status");
		if (tab == null) {
			tab = new TabPanel();

			CIReferenceTableControl refNewControl = new CIReferenceTableControl("incidentStatus_New", "<$template{ITIL_Incident}");
			CIReferenceTableControl refUiControl = new CIReferenceTableControl("incidentStatus_UI",  "<$template{ITIL_Incident}");
			CIReferenceTableControl refProblemControl = new CIReferenceTableControl("incidentStatus_Problem", "<$template{ITIL_Incident}");
			CIReferenceTableControl refKnownErrorControl = new CIReferenceTableControl("incidentStatus_KnownError", "<$template{ITIL_Incident}");
			CIReferenceTableControl refClosedControl = new CIReferenceTableControl("incidentStatus_Closed", "<$template{ITIL_Incident}");

			newIncidentLabel = new LabelCounter("New", refNewControl);
			uiIncidentLabel = new LabelCounter("Open", refUiControl);
			problemIncidentLabel = new LabelCounter("Problem", refProblemControl);
			knownErrorIncidentLabel = new LabelCounter("Known Error", refKnownErrorControl);
			closedIncidentLabel = new LabelCounter("Closed", refClosedControl);

			tab.add(new ListIncidentScreen(refNewControl), newIncidentLabel);
			tab.add(new ListIncidentScreen(refUiControl), uiIncidentLabel);
			tab.add(new ListIncidentScreen(refProblemControl), problemIncidentLabel);
			tab.add(new ListIncidentScreen(refKnownErrorControl), knownErrorIncidentLabel);
			tab.add(new ListIncidentScreen(refClosedControl), closedIncidentLabel);
			tab.selectTab(0);
			tab.setWidth("100%");
			dockPanel.add(tab, DockPanel.CENTER);
			tab.setHeight("100%");
			dockPanel.setCellVerticalAlignment(tab, VerticalPanel.ALIGN_TOP);
			dockPanel.setCellHeight(tab, "100%");
		
			tab.addTabListener(new TabListener() {

				public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
					return true;
				}

				public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
					Widget selected = tab.getWidget(tabIndex);
					if (selected instanceof ListIncidentScreen) {
						((ListIncidentScreen)selected).load();
					}
				}
			});
			initWidget(dockPanel);
		}
	}
	
	public void load() {
		// Update all counters...
		newIncidentLabel.update();
		uiIncidentLabel.update();
		problemIncidentLabel.update();
		knownErrorIncidentLabel.update();
		closedIncidentLabel.update();
	
		int selectedTab = tab.getTabBar().getSelectedTab();
		if (selectedTab >= 0) {
			Widget selected = tab.getWidget(selectedTab);
			if (selected instanceof ListIncidentScreen) {
				((ListIncidentScreen)selected).load();
			}
		}
		
	}
}
