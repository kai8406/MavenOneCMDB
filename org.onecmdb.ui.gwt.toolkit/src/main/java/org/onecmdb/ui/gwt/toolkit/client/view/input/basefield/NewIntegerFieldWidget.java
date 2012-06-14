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

import org.gwtiger.client.widget.field.NumericTextFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;

import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class NewIntegerFieldWidget extends NumericTextFieldWidget implements KeyboardListener, IBaseField {

	
	private AttributeValue value;

	public NewIntegerFieldWidget(AttributeValue value) {
		super(value.getLabel());
		this.value = value;
		setText(value.getStringValue());
		setRequired(value.getCtrl().isRequiered());
		addKeyboardListener(this);
		if (value.getCtrl().isReadonly()) {
			baseField.setStyleName("mdv-form-input-readonly");
			((TextBoxBase)baseField).setReadOnly(true);
			setRequired(false);
		}
	}

	public void clear() {
		super.clear();
	}

	public boolean validate() {
		setRequired(value.getCtrl().isRequiered());
		return(super.validate());
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
		this.value.setValue(getText());
		validate();
	}
	

}
