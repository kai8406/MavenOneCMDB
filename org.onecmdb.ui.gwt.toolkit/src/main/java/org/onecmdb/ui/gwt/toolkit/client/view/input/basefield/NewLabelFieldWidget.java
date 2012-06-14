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
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;

import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class NewLabelFieldWidget extends BaseFieldLabelWidget implements IBaseField {

	private AttributeValue value;

	public NewLabelFieldWidget(AttributeValue value) {
		super(value.getDisplayName());
		this.value = value;
		Label label = new Label(value.getStringValue());
		addField(label);
		label.setTitle(value.getStringValue());
		baseField.setStyleName("mdv-form-input-readonly");
	}

	public void clear() {
	}

	public boolean validate() {
		return true;
	}

	public Widget getBaseField() {
		return(this.baseField);
	}

	public void addKeyboardListener(KeyboardListener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
