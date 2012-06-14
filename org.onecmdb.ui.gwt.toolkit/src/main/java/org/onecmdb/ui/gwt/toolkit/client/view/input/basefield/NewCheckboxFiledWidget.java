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

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class NewCheckboxFiledWidget extends BaseFieldLabelWidget {

	
	public NewCheckboxFiledWidget(final AttributeValue value) {
		super(value.getLabel());
		
		final CheckBox box = new CheckBox();
		if (value.getStringValue() == null) {
			value.setValue("false");
		}
		box.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (box.isChecked()) {
					value.setValue("true");
				} else {
					value.setValue("false");
				}
				value.getCtrl().onEvent(this, sender);
			}
			
		});

		box.setChecked(value.getStringValue().equals("true"));
		addField(box);
		if (value.getCtrl().isReadonly()) {
			baseField.setStyleName("mdv-form-input-readonly");
			((CheckBox)baseField).setEnabled(false);
			setRequired(false);
		}
	}
	
	public void clear() {
	}

	public boolean validate() {
		return(true);
	}

	public void addKeyboardListener(KeyboardListener listener) {
	}

}
