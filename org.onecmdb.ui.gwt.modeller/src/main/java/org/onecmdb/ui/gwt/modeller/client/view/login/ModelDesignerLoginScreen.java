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
package org.onecmdb.ui.gwt.modeller.client.view.login;

import org.gwtiger.client.widget.field.TextFieldWidget;
import org.gwtiger.client.widget.panel.ValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.login.OneCMDBLoginScreen;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ModelDesignerLoginScreen extends OneCMDBLoginScreen {
	protected TextFieldWidget oneCMDBWSDL;

	public ModelDesignerLoginScreen() {
		super();
		setTitleText("Login To OneCMDB Model Designer");
	}
	
	
	protected void addExtraInputFields(ValidatePanel vp2) {
		super.addExtraInputFields(vp2);
		oneCMDBWSDL = new TextFieldWidget("OneCMDB Server URL");
		oneCMDBWSDL.setConvertToUpper(false);
		vp2.add(oneCMDBWSDL);
	}






	public void checkIfLogedIn() {
		// Update current oneCMDB WSDl.
		OneCMDBConnector.getInstance().getCurrentOneCMDB_WSDL(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("Problem connecting to oneCMDB GWT Servlet: " + caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof String) {
					oneCMDBWSDL.setText((String)result);
				}
				ModelDesignerLoginScreen.super.checkIfLogedIn();
			}
		});
	}

	public void save() {
		OneCMDBConnector.getInstance().setCurrentOneCMDB_WSDL(oneCMDBWSDL.getText(), new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("Problem setting OneCMDB WSDL URL:" + caught);
			}

			public void onSuccess(Object result) {
				String webService = oneCMDBWSDL.getText();
				// Need to extract the host part...
				String split[] = webService.split("/webservice/onecmdb");
				if (split.length > 0) {
					OneCMDBSession.setOneCMDBURL(split[0]);
				}
				
				ModelDesignerLoginScreen.super.save();
			}
		});		
	}
	
	
	
	

	

}
