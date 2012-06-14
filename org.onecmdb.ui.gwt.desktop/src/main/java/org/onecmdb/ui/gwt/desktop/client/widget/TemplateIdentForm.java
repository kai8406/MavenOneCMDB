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

import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;

import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class TemplateIdentForm  extends LayoutContainer {
	
	private CIModel model;

	public TemplateIdentForm(CIModel model) {
		this.model = model;
		setLayout(new RowLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		LayoutContainer input = new LayoutContainer(); 
		FormLayout layout = new FormLayout();  
		layout.setLabelAlign(LabelAlign.LEFT);  
		input.setLayout(layout);  

	
		TextField<String> alias = new TextField<String>();  
		alias.setFieldLabel("Alias");  
		input.add(alias);  
	
		TextField<String> dispExpr = new TextField<String>();  
		dispExpr.setFieldLabel("Display Name Expression");  
		input.add(dispExpr);  

		LayoutContainer inputDesc = new LayoutContainer(); 
		
		layout = new FormLayout();  
		layout.setLabelAlign(LabelAlign.TOP);  
		inputDesc.setLayout(layout);
		
		TextArea a = new TextArea();  
		a.setFieldLabel("Description");  
		inputDesc.add(a);
	
		LayoutContainer info = new LayoutContainer(); 
		info.setLayout(new FlowLayout());
		info.add(new HTML("<img src=http://localhost/onecmdb/icons/CiscoRouter.png/><br>" + "<br><b>ID:</b>" + "0128301283018" + "<b>Display Name</b>" + "Anslutning ."));
	
		info.add(info, new RowData(1, -1));
		info.add(input, new RowData(1, -1));
		info.add(inputDesc, new RowData(1, 1));
	}
}
