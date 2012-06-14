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
package org.onecmdb.ui.gwt.itil.client.application.problem.screen;

import org.onecmdb.ui.gwt.toolkit.client.control.table.CIReferenceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.LabelCounter;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GroupListProblemScreen extends OneCMDBBaseScreen {
	
	private LabelCounter newProblemIncidentLabel;
	private LabelCounter problemIncidentLabel;
	private LabelCounter knownErrorIncidentLabel;
	private TabPanel tab;

	public GroupListProblemScreen() {
		this.setTitleText("List Problems by Status");
		if (tab == null) {
			tab = new TabPanel();

			CIReferenceTableControl refNewProblemControl = new CIReferenceTableControl("problemStatus_New", "<$template{ITIL_Problem}");
			CIReferenceTableControl refProblemControl = new CIReferenceTableControl("problemStatus_Problem", "<$template{ITIL_Problem}");
			CIReferenceTableControl refKnownErrorControl = new CIReferenceTableControl("problemStatus_KnownError", "<$template{ITIL_Problem}");

			newProblemIncidentLabel = new LabelCounter("New", refNewProblemControl);
			problemIncidentLabel = new LabelCounter("Problem", refProblemControl);
			knownErrorIncidentLabel = new LabelCounter("Known Error", refKnownErrorControl);
		
			tab.add(new ListProblemScreen(refNewProblemControl), newProblemIncidentLabel);
			tab.add(new ListProblemScreen(refProblemControl), problemIncidentLabel);
			tab.add(new ListProblemScreen(refKnownErrorControl), knownErrorIncidentLabel);
			tab.selectTab(0);
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
					if (selected instanceof ListProblemScreen) {
						((ListProblemScreen)selected).load();
					}
				}
			});
			initWidget(dockPanel);
		}
	}
	
	public void load() {
		// Update all counters...
		newProblemIncidentLabel.update();
		problemIncidentLabel.update();
		knownErrorIncidentLabel.update();
		
	
		int selectedTab = tab.getTabBar().getSelectedTab();
		if (selectedTab >= 0) {
			Widget selected = tab.getWidget(selectedTab);
			if (selected instanceof ListProblemScreen) {
				((ListProblemScreen)selected).load();
			}
		}
		
	}
}
