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
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;
import org.onecmdb.ui.gwt.toolkit.client.control.input.MultiWordStartSuggestOracle;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;

import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NewSuggestTestField extends BaseFieldLabelWidget implements KeyboardListener, IBaseField {

	private SuggestBox suggestBox;
	private AttributeValue value;
	

	public NewSuggestTestField(AttributeValue v) {
		super(v.getLabel());
		this.value = v;
		
		if (value.getCtrl().isReadonly()) {
			TextBox readOnlyBox = new TextBox();
			addField(readOnlyBox);
			baseField.setStyleName("mdv-form-input-readonly");			
			setRequired(false);
		} else {
			MultiWordStartSuggestOracle oracle = new MultiWordStartSuggestOracle();
			if (this.value.getCtrl() instanceof TextAttributeControl) {
				TextAttributeControl tCtrl =  (TextAttributeControl)this.value.getCtrl();
				List values = tCtrl.getAvailableValues();
				if (values != null) {
					for (Iterator iter = values.iterator(); iter.hasNext();) {
						Object suggestValue = iter.next();
						oracle.addSuggestion(suggestValue.toString());
					}
				}
			}
			suggestBox = new SuggestBox(oracle);
			suggestBox.setText(value.getStringValue());
			suggestBox.addKeyboardListener(this);
			setRequired(value.getCtrl().isRequiered());
			addField(suggestBox);
		}
	}

	public void clear() {
		if (suggestBox != null) { 
			suggestBox.setText("");
		}
	}

	public boolean validate() {
		setRequired(value.getCtrl().isRequiered());
		if (suggestBox != null) {
			if (this.isRequired()) {
				if (suggestBox.getText().length() == 0) {
					showError("'" + getLabel() + "' is required", true);
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
		this.value.setValue(suggestBox.getText());
		validate();
	}
	
	

}
