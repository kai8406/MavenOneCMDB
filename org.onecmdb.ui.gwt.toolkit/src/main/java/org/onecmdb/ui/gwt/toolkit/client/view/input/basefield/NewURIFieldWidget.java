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
package org.onecmdb.ui.gwt.toolkit.client.view.input.basefield;

import org.gwtiger.client.widget.field.BaseFieldLabelWidget;
import org.gwtiger.client.widget.field.MaskTextFieldWidget;
import org.gwtiger.client.widget.field.TextFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class NewURIFieldWidget extends BaseFieldLabelWidget implements KeyboardListener, IBaseField {

	
	private AttributeValue value;
	private TextBox box;

	public NewURIFieldWidget(AttributeValue v) {
		super(v.getLabel());
		this.value = v;
		if (value.getCtrl().isReadonly()) {
			HTML html = new HTML();
			if (!this.value.isNullValue()) {
				html.setHTML("<a href='javascript:;'>" + this.value.getStringValue() +"</a>");
			}
			html.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					Window.open(value.getStringValue(), "_blank", "");
					
				}
				
			});
			
			html.setWordWrap(true);
			html.setTitle(this.value.getStringValue());
			addField(html);
			html.setStyleName("mdv-form-input-readonly");			
			setRequired(false);
			
		} else {
			box = new TextBox();
			box.setText(value.getStringValue());
			box.addKeyboardListener(this);
			setRequired(value.getCtrl().isRequiered());
			addField(box);
		}
		
	}

	public void clear() {
		if (box != null) { 
			box.setText("");
		}
	}

	public boolean validate() {
		setRequired(value.getCtrl().isRequiered());
		if (box != null) {
			if (this.isRequired()) {
				if (box.getText().length() == 0) {
					showError("'" + getLabel() + "' is required", true);
					return false;
				}
				// Check uri consistency
				// Check for protocol://host
				String value = box.getText();
				String parts[] = value.split("://");
				if (parts.length != 2) {
					showError("'" + getLabel() + "' is invalid!", true);
					return false;
				}
			}
		}
		showError(false);
		return true;
	}

	public void addKeyboardListener(KeyboardListener listener) {
	}

	public Widget getBaseField() {
		return(this.baseField);
	}
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		// TODO Auto-generated method stub
		
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// TODO Auto-generated method stub
		
	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		// Update value.
		this.value.setValue(box.getText());
		validate();
	}
	

}
