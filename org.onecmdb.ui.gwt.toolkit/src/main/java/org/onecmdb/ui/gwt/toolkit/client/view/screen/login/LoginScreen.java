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

import org.gwtiger.client.widget.field.PasswordFieldWidget;
import org.gwtiger.client.widget.field.TextFieldWidget;
import org.gwtiger.client.widget.panel.ButtonCallback;
import org.gwtiger.client.widget.panel.ButtonPanel;
import org.gwtiger.client.widget.panel.ValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;


import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Addy
 * 
 */
public abstract class LoginScreen extends OneCMDBBaseScreen implements ButtonCallback {
    private static final String USERID_COOKIE = "GWTigerUserid";

    protected ValidatePanel vp = new ValidatePanel();
    
    protected TextFieldWidget txtLogin = new TextFieldWidget("Login");

    protected PasswordFieldWidget txtPassword = new PasswordFieldWidget(
            "Password");

    protected CheckBox rememberMe = new CheckBox("Remember Me");

    public LoginScreen() {
        VerticalPanel vPanel = new VerticalPanel();
        txtPassword.setConvertToUpper(false);
        txtLogin.setConvertToUpper(false);
        
    	txtLogin.setRequired(true);
        vp.add(txtLogin);
        vp.add(txtPassword);
        
        addExtraInputFields(vp);
        
        rememberMe.setChecked(true);
        rememberMe.setStyleName("one-remember-me");
        vp.add(rememberMe);
        vp.setCellHorizontalAlignment(rememberMe, HorizontalPanel.ALIGN_RIGHT);
        
        ButtonPanel btnPanel = new ButtonPanel();
        btnPanel.addSaveButton("Login");
        btnPanel.addClearButton();
        btnPanel.setCallback(this);
        vp.add(btnPanel);
        vp.setCellHorizontalAlignment(btnPanel, HorizontalPanel.ALIGN_LEFT);

        setTitleText("Please Login");
        

        //dockPanel.add(vp, DockPanel.NORTH);
        vPanel.add(vp);
        vPanel.setCellHorizontalAlignment(vp, HorizontalPanel.ALIGN_CENTER);
        vPanel.setCellVerticalAlignment(vp, HorizontalPanel.ALIGN_MIDDLE);
        vPanel.setStyleName("mdv-form");
        
        dockPanel.add(vPanel, NORTH);
        dockPanel.setCellHeight(vPanel, "100%");
        initWidget(dockPanel);

    }

    protected void addExtraInputFields(ValidatePanel vp2) {
	}

	public boolean isScrollable() {
  	  return(false);
    }
	
    public boolean isRightPanel() {
		return(false);
	}

    
    public boolean validate() {
        return vp.validate();
    }

    public void clear() {
        setErrorText("");
        vp.clear();
    }

    public void save() {
        checkLogin();
    }

    protected abstract void checkLogin();
    public abstract void checkIfLogedIn();

}
