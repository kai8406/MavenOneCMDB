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

import java.util.Arrays;

import org.gwtiger.client.widget.panel.ButtonPanel;
import org.onecmdb.ui.gwt.itil.client.ITILApplication;
import org.onecmdb.ui.gwt.itil.client.application.incident.control.NewIncidentAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeLoaderControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIAttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIDescriptionControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.NewCIScreen;

import com.google.gwt.user.client.ui.Widget;

public class NewIncidentScreen extends NewCIScreen {
	
	private static DefaultAttributeFilter defFilter = new DefaultAttributeFilter();
	
	public NewIncidentScreen() {
		super();
		setTitleText("New Incident");
		defFilter.setAttributeControl(Arrays.asList(getCtrls()));
		
	}
	
	public void load() {
		super.load("ITIL_Incident", null);
	}

	protected CIAttributeValueInputControl getControl() {
		return(new NewIncidentAttributeControl(templateAlias, isNew()));
	}

	
	protected void onCommitFailure(Throwable caught) {
		System.out.println("Commit FAILED! " + caught);
		setErrorText("Saved FAILED! " + caught.getMessage());
		showError(true);
	}

	protected void onCommitSuccess(Object result) {
		if (result instanceof GWT_CiBean) {
			ITILApplication.get().showScreen(ITILApplication.CONFIRM_NEW_INCDIENT_SCREEN, ((GWT_CiBean)result).getAlias(), new Long(0));
		}
	}
	
	public void onLoadComplete(Object sender) {
	}
	
	public void save() {
		super.save();
	}

	/*
	public void clear() {
		//ITILApplication.get().showScreen(ITILApplication.NEW_INCDIENT_SCREEN);
	}
	*/

	private AttributeControl[] getCtrls() {

		AttributeControl[] ctrls = new AttributeControl[] {
			new AttributeControl("title", false, true),
			new CIDescriptionControl(true),
			//new TextAttributeControl("opDescription", false, false, TextAttributeControl.TEXT_AREA_TYPE, new Integer(5), null),  
			new AttributeControl("affectedCIs", false, false),
			new AttributeControl("priority", false, true),
			new AttributeControl("status", true, false),
			new AttributeControl("reportedBy", false, true),
			new AttributeLoaderControl("ticketIssuer", true, true, loadTickIssuer())
		};
		return(ctrls);
	}
	

	public IAttributeFilter getAttributeFilter() {
		return(defFilter);
	}

	
	protected Widget getButtonPanel() {
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Save");
		bPanel.addClearButton("Clear form");
		bPanel.setCallback(this);
		return(bPanel);
	}
}
