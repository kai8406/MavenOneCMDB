/*
 * Copyright 2007 Aditya Kapur <addy AT gwtiger.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onecmdb.ui.gwt.toolkit.client.view.screen.login;


import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Addy
 * 
 */
public class LogoutScreen extends OneCMDBBaseScreen implements ClickListener {
    Button btnLogin = new Button("Login");

    public LogoutScreen() {
		dockPanel.add(new Label("Thank you for using OneCMDB"),
			DockPanel.NORTH);
		btnLogin.addClickListener(this);
		dockPanel.add(btnLogin, DockPanel.NORTH);
		initWidget(dockPanel);
    }

    public void onClick(Widget sender) {
    	if (sender == btnLogin) {
    		// When Login button is clicked, show the login screen
    		getBaseEntryScreen().setLoginScreen();
    	}
    }
    
    public boolean isScrollable() {
    	  return(false);
    }

    public boolean isRightPanel() {
		return(false);
	}

      

}
