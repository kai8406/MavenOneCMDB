/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.ui.gwt.desktop.client.widget;

import org.onecmdb.ui.gwt.desktop.client.Version;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;

public class LoginWidget {
	private static String user = null;
	
	public static void login(boolean lock, final AsyncCallback<CMDBSession> callback) {
		String cookieUser = Cookies.getCookie("auth_username");
		if (cookieUser != null) {
			user = cookieUser;
		}
		final Dialog login = new Dialog();  
		login.setClosable(false);
		login.setButtons("");
		login.setBodyBorder(true);  
		login.setInsetBorder(true);  
		//login.setButtons(Dialog.OK);  
		login.setIconStyle("icon-app-side");
		if (lock) {
			login.setHeading("OneCMDB Desktop - Locked");
		} else {
			login.setHeading("Login - OneCMDB Desktop");
		}
		//<br><a style='font-size:8px'><i>V" + Version.VERSION_STRING + "</i></a></br>");  
		login.setWidth(240);  
		login.setHeight(140);  
		login.setHideOnButtonClick(true);  
		
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(60);
		panel.setFieldWidth(100);
		panel.setHeaderVisible(false);
		
		final TextField username = new TextField(); 
		username.setFieldLabel("Username");
		username.setEmptyText("username");
		if (user != null) {
			username.setValue(user);
		}
		if (lock) {
			username.setEnabled(false);
		}
		panel.add(username);
		
		final TextField password = new TextField(); 
		password.setFieldLabel("Password");
		password.setPassword(true);
		password.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() == KeyboardListener.KEY_ENTER) {
					doLogin(login, (String)username.getValue(), (String)password.getValue(), callback);
				}
			}
			
		});
		panel.add(password);
		
		login.setLayout(new RowLayout());
		login.add(panel, new RowData(1,1));
		HTML version = new HTML();
		version.setText("Version " + Version.VERSION_STRING);
		version.setStyleName("cmdb-version-text");
		login.add(version, new RowData(1, -1));
		login.addButton(new Button("Login", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				doLogin(login, (String)username.getValue(), (String)password.getValue(), callback);
			}
		}));
		login.setModal(true);
		login.show();
	}
	
	protected static void doLogin(final Dialog login, String username, String pwd, final AsyncCallback<CMDBSession> callback) {
		user = username;
		final MessageBox progress = MessageBox.wait("Login",  
	             "Authentication, please wait...", "Authenticating...");
		
		ModelServiceFactory.get().autenticate(username, pwd, new AsyncCallback<CMDBSession>() {

			public void onFailure(Throwable arg0) {
				progress.close();
				login.close();
				callback.onFailure(arg0);
			}

			public void onSuccess(CMDBSession arg0) {
				progress.close();
				login.close();
				callback.onSuccess(arg0);
			}
			
		});
		
	}
}
