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

import org.onecmdb.ui.gwt.itil.client.ITILApplication;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmNewIncidentScreen extends OneCMDBBaseScreen implements ClickListener {

	private Button newIncident = new Button("New Incident");
	private Button listIncident = new Button("List Incidents");
	private Button editIncident = new Button("Edit Incident");
	private String objectType;
	private VerticalPanel vPanel;
	
	
	public ConfirmNewIncidentScreen() {
		super();
		setTitleText("New Incident Confirmation");
		vPanel = new VerticalPanel();
		vPanel.setHeight("100%");
		dockPanel.add(vPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		initWidget(this.dockPanel);
		
	}

	public void load(String objectType, Long objectId) {
		setErrorText("");
		setLoadingText("");
		this.objectType = objectType;
		
		vPanel.clear();
		vPanel.add(new HTML("Incident <i>" + objectType + "</i> successfully created."));
		
		HorizontalPanel bPanel = new HorizontalPanel();
		bPanel.add(newIncident);
		bPanel.add(listIncident);
		bPanel.add(editIncident);
		newIncident.addClickListener(this);
		listIncident.addClickListener(this);
		editIncident.addClickListener(this);
		
		vPanel.add(bPanel);
		
	}

	public void onClick(Widget sender) {
		if (sender.equals(newIncident)) {
			ITILApplication.get().showScreen(ITILApplication.NEW_INCDIENT_SCREEN);
			return;
		}
		
		if (sender.equals(listIncident)) {
			ITILApplication.get().showScreen(ITILApplication.LIST_INCDIENT_SCREEN);
			return;
		}
		if (sender.equals(editIncident)) {
			ITILApplication.get().showScreen(ITILApplication.EDIT_INCDIENT_SCREEN, objectType, new Long(0));
			return;
		}
		
	}

}
