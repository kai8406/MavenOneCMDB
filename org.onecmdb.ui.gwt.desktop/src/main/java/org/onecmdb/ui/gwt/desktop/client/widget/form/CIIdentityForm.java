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
package org.onecmdb.ui.gwt.desktop.client.widget.form;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.desktop.Stock;
import org.onecmdb.ui.gwt.desktop.client.desktop.TestData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;

public class CIIdentityForm extends LayoutContainer {
	
	private CIModel model;
	private CMDBPermissions permissions;


	public CIIdentityForm(CIModel model) {
		this.model = model;
	}
	

	@Override
	protected void onRender(Element parent, int index) {
		// TODO Auto-generated method stub
		super.onRender(parent, index);
		initUI();
	}


	private void initUI() {
		// ID. (R)
		// DisplayName (R)
		// DerivedFrom Path. (R)
		// CreateDate (R)
		// LastModififed (R)
		
		// Alias (R/W)
		// DisplayNameExpr (R/W)
		// GID (R/W)
		// Description (R/W)
		setLayout(new FlowLayout());
		/*
		Label label = new Label("DerivedPath: Ci/Test/Test2/Test3");
		label.setHeight("20px");
		add(label);
		*/
		setStyleName("ci-id-panel");
			
		FormPanel form2 = new FormPanel(); 
		
		form2.setFrame(false);  
		form2.setHeaderVisible(false);
		//form2.setHeading("Identification");  
		form2.setLayout(new FlowLayout()); 
		//form2.setCollapsible(true);  
		form2.setSize(-1, 180);
		form2.setLabelWidth(60);
		form2.setFieldWidth(100);
		
		LayoutContainer main = new LayoutContainer();  
		main.setLayout(new ColumnLayout());
		main.setSize(700, 180);
		
		LayoutContainer left = new LayoutContainer();  
		FormLayout layout = new FormLayout();  
		layout.setLabelAlign(LabelAlign.LEFT);  
		layout.setDefaultWidth(180);
		left.setLayout(layout); 
		
		
		FormLayout rightLayout = new FormLayout();  
		rightLayout.setLabelAlign(LabelAlign.LEFT);
		rightLayout.setDefaultWidth(150);
		left.setLayout(layout); 
		
		LayoutContainer right = new LayoutContainer();  
		right.setLayout(rightLayout); 
		
		getInternalModifyFieldSet(left);
		getInternalReadOnlyFieldSet(right);
		left.layout();
		main.add(left, new ColumnData(.5));
		main.add(right, new ColumnData(.5));
		
		form2.add(main);
		/*
		form2.setButtonAlign(HorizontalAlignment.LEFT);
		form2.addButton(new Button("Cancel"));  
		form2.addButton(new Button("Submit"));  
		*/
		add(form2);
		
		layout();
	}

	protected void getInternalModifyFieldSet(LayoutContainer c) {
		/*
		FieldSet fieldSet = new FieldSet();  
		fieldSet.setHeading("CI Identification");  
		fieldSet.setCheckboxToggle(false);  
		FormLayout layout = new FormLayout();  
	    layout.setLabelWidth(75);  
		layout.setPadding(4);  
		fieldSet.setLayout(layout);  
		*/
		Listener update = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				model.set(be.field.getId(), be.value);
			}
		};
		Field<String> alias = null;
		if (isAllowEdit()) {
			alias = new TextField<String>();
		} else {
			alias = new LabelField();
		}
		alias.setFieldLabel("Alias");
		alias.setId(CIModel.CI_ALIAS);
		alias.setWidth(100);
		alias.setValue(model.getAlias());
		alias.addListener(Events.Change, update);

		
		Field<String> dispExpr = null;
		if (isAllowEdit()) {	
			dispExpr = new TextField<String>();
		} else {
			dispExpr = new LabelField();
		}
		dispExpr.setFieldLabel("Display Name<br>Expression");
		dispExpr.setId(CIModel.CI_DISPLAYNAMEEXPR);
		dispExpr.setValue(model.getDisplayNameExpression());
		dispExpr.addListener(Events.Change, update);
			
		
		/*
		TextField<String> gid = new TextField<String>();
		gid.setFieldLabel("GID");
		gid.setId("gid");
		*/
		
		
		TextArea desc = new TextArea();
		desc.setEnabled(isAllowEdit());
		desc.setFieldLabel("Description");
		desc.setId(CIModel.CI_DESCRIPTION);
		desc.setValue(model.getDescription());
		desc.addListener(Events.Change, update);
		c.add(alias);
		c.add(dispExpr);
		//c.add(gid);
		c.add(desc);
	}

	
	private boolean isAllowEdit() {
		
		if (permissions != null) {
			return(permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT));
		}
		return false;
	}


	protected void getInternalReadOnlyFieldSet(LayoutContainer c) {
		/*
		FieldSet fieldSet = new FieldSet();  
		fieldSet.setHeading("User Information");  
		fieldSet.setCheckboxToggle(false);  
		FormLayout layout = new FormLayout();  
	    layout.setLabelWidth(75);  
		layout.setPadding(4);  
		fieldSet.setLayout(layout);  
		*/
		LabelField id = new LabelField();
		id.setFieldLabel("ID:");
		id.setText(model.getIdAsString());
		
		LabelField dispName = new LabelField();
		dispName.setFieldLabel("DisplayName:");
		dispName.setText(model.getDisplayName());
		
		
		LabelField lastMod= new LabelField();
		lastMod.setFieldLabel("Last Modified:");
		
		if (model.getLastModifiedDate() != null) {
			String d = CMDBSession.get().getDateTimeFormat().format(model.getLastModifiedDate());
			lastMod.setText(d);
		}
		
		LabelField createDate = new LabelField();
		createDate.setFieldLabel("Created:");
		if (model.getCreateDate() != null) {
			String d = CMDBSession.get().getDateTimeFormat().format(model.getCreateDate());
			createDate.setText(d);
		}
		c.add(id);
		c.add(dispName);
		c.add(lastMod);
		c.add(createDate);
	}


	public void setPermission(CMDBPermissions permission) {
		this.permissions = permission;
		
	}
	
}
