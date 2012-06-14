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
package org.onecmdb.ui.gwt.itil.client.application.asset.screen;

import org.onecmdb.ui.gwt.toolkit.client.control.table.CIReferenceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.LabelCounter;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListHardwareByTypeScreen extends OneCMDBBaseScreen {
	private LabelCounter unkownHwLabel;
	private LabelCounter switchHwLabel;
	private LabelCounter desktopHwLabel;
	private LabelCounter routerHwLabel;
	private LabelCounter portableHwLabel;
	private LabelCounter serverHwLabel;
	private LabelCounter printerHwLabel;
	
	private TabPanel tab;

	public  ListHardwareByTypeScreen() {
		this.setTitleText("List Incident(s) by Status");
		if (tab == null) {
			tab = new TabPanel();

			CIReferenceTableControl unknownHwControl = new CIReferenceTableControl("UnknownHwType", "<$template{Hardware}");
			CIReferenceTableControl switchHwControl = new CIReferenceTableControl("SwitchHwType",  "<$template{Hardware}");
			CIReferenceTableControl desktopHwControl = new CIReferenceTableControl("DesktopHwType", "<$template{Hardware}");
			CIReferenceTableControl routerHwControl = new CIReferenceTableControl("RouterHwType", "<$template{Hardware}");
			CIReferenceTableControl portableHwControl = new CIReferenceTableControl("PortableHwType", "<$template{Hardware}");
			CIReferenceTableControl serverHwControl = new CIReferenceTableControl("ServerHwType", "<$template{Hardware}");
			CIReferenceTableControl printerHwControl = new CIReferenceTableControl("PrinterHwType", "<$template{Hardware}");
				
			unkownHwLabel = new LabelCounter("Unknown", unknownHwControl);
			switchHwLabel = new LabelCounter("Switch", switchHwControl);
			desktopHwLabel = new LabelCounter("Desktop", desktopHwControl);
			routerHwLabel = new LabelCounter("Router", routerHwControl);
			portableHwLabel = new LabelCounter("Portable", portableHwControl);
			serverHwLabel = new LabelCounter("Server", serverHwControl);
			printerHwLabel = new LabelCounter("Printer", printerHwControl);
				
			

			tab.add(new ListHardwareScreen(serverHwControl), serverHwLabel);
			tab.add(new ListHardwareScreen(switchHwControl), switchHwLabel);
			tab.add(new ListHardwareScreen(desktopHwControl), desktopHwLabel);
			tab.add(new ListHardwareScreen(routerHwControl), routerHwLabel);
			tab.add(new ListHardwareScreen(portableHwControl), portableHwLabel);
			tab.add(new ListHardwareScreen(printerHwControl), printerHwLabel);
			tab.add(new ListHardwareScreen(unknownHwControl), unkownHwLabel);
				tab.selectTab(0);
			dockPanel.add(tab, DockPanel.CENTER);
			dockPanel.setCellVerticalAlignment(tab, VerticalPanel.ALIGN_TOP);		
			tab.addTabListener(new TabListener() {

				public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
					return true;
				}

				public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
					Widget selected = tab.getWidget(tabIndex);
					if (selected instanceof ListHardwareScreen) {
						((ListHardwareScreen)selected).load();
					}
				}
			});
			initWidget(dockPanel);
		}
	}
	
	public void load() {
		// Update all counters...
		unkownHwLabel.update();
		switchHwLabel.update();
		desktopHwLabel.update();
		routerHwLabel.update();
		portableHwLabel.update();
		serverHwLabel.update();
		printerHwLabel.update();
	
		int selectedTab = tab.getTabBar().getSelectedTab();
		if (selectedTab >= 0) {
			Widget selected = tab.getWidget(selectedTab);
			if (selected instanceof ListHardwareScreen) {
				((ListHardwareScreen)selected).load();
			}
		}
		
	}

}
