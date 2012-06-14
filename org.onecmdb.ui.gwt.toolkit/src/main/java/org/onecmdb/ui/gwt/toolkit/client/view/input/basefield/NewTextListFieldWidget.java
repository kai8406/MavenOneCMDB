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

import java.util.Iterator;
import java.util.List;

import org.gwtiger.client.widget.field.BaseFieldLabelWidget;
import org.gwtiger.client.widget.field.ListBoxFieldWidget;
import org.gwtiger.client.widget.field.MaskTextFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;


public class NewTextListFieldWidget extends ListBoxFieldWidget implements  IBaseField, ChangeListener {

	
	private AttributeValue value;

	public NewTextListFieldWidget(AttributeValue value) {
		super(value.getLabel());
		this.value = value;
		this.value.setWidget(this);
		
		field.addChangeListener(this);
		if (this.value.getCtrl() instanceof TextAttributeControl) {
			TextAttributeControl tCtrl =  (TextAttributeControl)this.value.getCtrl();
			List values = tCtrl.getAvailableValues();
			if (values != null) {
				for (Iterator iter = values.iterator(); iter.hasNext();) {
					Object enumValue = iter.next();
					addItem(enumValue.toString());
				}
				if (value.isNullValue()) {
					if (values.size() > 0) {
						setSelectedValue(values.get(0).toString());
						this.value.setValue(values.get(0).toString());
					}
				} else {
					setSelectedValue(value.getStringValue());
				}
		}
	
		}
		
		setRequired(value.getCtrl().isRequiered());
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

	public void addKeyboardListener(KeyboardListener listener) {
	}

	public Widget getBaseField() {
		return(this.baseField);
	}
	

	public void onChange(Widget sender) {
		String enumValue = getSelectedValue();
		value.setValue(enumValue);
		validate();
	}

}
