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
package org.onecmdb.ui.gwt.toolkit.client.view.screen.login;


import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OneCMDBLoginScreen extends LoginScreen {

	
	
	public OneCMDBLoginScreen() {
		super();
		setTitleText("Login To OneCMDB application server");
	}


	public void checkIfLogedIn() {
		AsyncCallback callback = new AsyncCallback() {

			public void onFailure(Throwable caught) {
				// Is not logedIn...
				// setErrorText("ERROR: " + caught);
			}

			public void onSuccess(Object result) {
				// Is authenticated but have no account!!!
				if (result == null) {
					getBaseEntryScreen().setMainScreen(null);
					return;
				}
		
				if (result instanceof GWT_CiBean) {
					getBaseEntryScreen().setMainScreen((GWT_CiBean)result);					
				}
			
				
			}
			
		};
		
		if (OneCMDBSession.isAuthenticated()) {
			OneCMDBConnector.getInstance().getAuthAccount(OneCMDBSession.getAuthToken(), callback);
		}
   }

	
	protected void checkLogin() {
		AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                showLoading(false);
                // clear the loginscreen
                if (result instanceof String) {
                	OneCMDBSession.setAuthToken((String)result, rememberMe.isChecked());
                	checkIfLogedIn();
                } else {
                	setErrorText("Invalid Username or password");
                }
            }

            public void onFailure(Throwable ex) {
                showLoading(false);
                setErrorText("Unable to Login: " + ex.getMessage());
            }
        };
        
        setErrorText("");
        
        showLoading(true);
        
        OneCMDBConnector.getInstance().auth(txtLogin.getText(), txtPassword.getText(),
        			callback);
	}

	
	
	
}
