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
package org.onecmdb.ui.gwt.desktop.client.window.mdr;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.help.HelpInfo;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;

public class AddMDRDialog {
	private CMDBPermissions perm;
	private Listener<BaseEvent> closeListener;
	private Dialog dialog;

	public AddMDRDialog(CMDBPermissions perm, Listener<BaseEvent> configCloseListener) {
		this.perm = perm;
		this.closeListener = configCloseListener;
	}
	
	
	public void show() {
		dialog = new Dialog();
		initUI();
	}



	
	protected void initUI() {
		FormData formData = new FormData("-20");  
		
		FormPanel form = new FormPanel();
		form.setHeaderVisible(false);
		form.setSize(300, 70);
		form.setLabelWidth(125);
		form.setFieldWidth(125);
		
		FormBinding binding = new FormBinding(form);
		
		TextField<String> name = new TextField<String>();
		name.setFieldLabel("MDR Name");
		name.setName("name");
		name.setId("name");
		form.add(name, formData);
	
		name = new TextField<String>();
		name.setFieldLabel("MDR Config Name");
		name.setName("cfgName");
		name.setId("cfgName");
		form.add(name, formData);
		
		final BaseModel data = new BaseModel();
		binding.autoBind();
		binding.bind(data);
		
		dialog.getButtonBar().removeAll();
		dialog.addButton(new Button("Create", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				dialog.hide();
				addMDR(data);
			}
			
		}));
			
		
		dialog.addButton(new Button("Cancel", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				dialog.hide();
			}
			
		}));
		
		dialog.setSize(350, 150);
		dialog.setLayout(new RowLayout());
		HelpInfo.show("help/mdr/help-mdr-create.html");
		/*
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setUrl("help/help-mdr-create.html");
		cp.setStyleName("property-panel-background");
		*/
		dialog.add(form, new RowData(1, 1));
		//dialog.add(cp, new RowData(1, 1));
		dialog.show();
	}

	protected void addMDR(BaseModel data) {
		final MessageBox info = MessageBox.progress("Progress",  
	             "Create MDR '" + data.get("name") + "', please wait...", "Creating..."); 
		ModelServiceFactory.get().createMDR(CMDBSession.get().getToken(), data, new CMDBAsyncCallback<BaseModel>() {

			
			@Override
			public void onFailure(Throwable t) {
				info.close();
				super.onFailure(t);
			}

			@Override
			public void onSuccess(BaseModel arg0) {
				info.close();
				// Open Wizard...
				CIModel mdr = arg0.get("mdr");
				CIModel mdrCfg = arg0.get("mdrCfg");
				
				MDRConfigureWindow widget = new MDRConfigureWindow(perm, mdr, mdrCfg);
				Window w = WindowFactory.getWindow("Configure MDR " + mdr.getValueAsString("name") + "/" + mdrCfg.getValueAsString("name"), widget);
				w.setSize(800, 600);
				/*
				Window w = new Window();
				w.setSize(600, 400);
				w.setLayout(new FitLayout());
				w.add(widget);
				*/
				if (closeListener != null) {
					w.addListener(Events.Close, closeListener);
				}
				w.setSize(800, 600);
				w.show();
				w.layout();
				w.setCloseAction(CloseAction.CLOSE);
				w.toFront();
		
				
			}
			
		});
	}

}
