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

import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ExceptionErrorDialog {
	
	public static void showError(String title, String msg) {
		MessageBox.alert(title, msg, null);
	}

	
	public static void showError(String title, Throwable t) {
		showError(title, t, null);
	}
	
	public static void showError(String title, Throwable t, Listener<WindowEvent> callback) {
		/*
		MessageBox box = MessageBox.prompt(title, t.getMessage(), true);
		box.getTextArea().setValue(t.toString());
		*/
		if (t instanceof CMDBRPCException) {
			showRPCException((CMDBRPCException)t, callback);
		} else {
			MessageBox.alert(title, t.getMessage() + "<br>" + t.toString(), callback);
		}
	}
	
	
	public static void showRPCException(CMDBRPCException e, Listener<WindowEvent> callback) {
		Dialog d = new Dialog();
		d.setLayout(new FitLayout());
		
		//FormPanel form = new FormPanel();
		LayoutContainer area = new LayoutContainer();  
		area.setStyleAttribute("padding", "0 10px 5px 10px");  
		//area.setWidth(450);  
		FormLayout layout = new FormLayout();  
		layout.setLabelAlign(LabelAlign.TOP);
		layout.setDefaultWidth(380);
		area.setLayout(layout);
		
		d.setHeading(e.getHeader());
		
		LabelField field = new LabelField("<b>Received an error from server.<b>");
		
		TextArea stackTrace = new TextArea();
		stackTrace.setFieldLabel("Stacktrace");
		stackTrace.setValue(e.getRemoteStackTrace());
		stackTrace.setReadOnly(true);
		stackTrace.setHeight(200);
		
		TextArea info = new TextArea();
		info.setFieldLabel("Error");
		info.setHeight(60);
		info.setReadOnly(true);
		info.setValue(e.getMessage());
			
		area.add(field);
		area.add(info);
		area.add(stackTrace);
		d.add(area);
		d.setSize(430, 430);
		d.layout();
		d.setHideOnButtonClick(true);
		d.show();
		
		
		/*
		MessageBox box = MessageBox.prompt(title, t.getMessage(), true);
		box.getTextArea().setValue(t.toString());
		*/
		//MessageBox.alert(e.getHeader(), e.getMessage() + "<br>" + e.getRemoteStackTrace(), callback);  
	}

	
}
