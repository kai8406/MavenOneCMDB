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

import org.gwtiger.client.widget.field.TextFieldWidget;

import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NewMaskTextFieldWidget extends TextFieldWidget {
	private String mask;

	public NewMaskTextFieldWidget(String labelText, String textMask) {
		super(labelText);
		mask = textMask;

		textBox.addKeyboardListener(new KeyboardListenerAdapter() {
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				String text = ((TextBox) sender).getText();

				int length = text.length();
				if ((keyCode == (char) KEY_BACKSPACE) && length > 0) {
					//((TextBox) sender).setText(text.substring(0, length - 1));
					setText(text.substring(0, length - 1));
				}
				if ((keyCode == (char) KEY_TAB || keyCode == (char) KEY_ENTER))
					return;
				if (length >= mask.length()) {
					((TextBox) sender).cancelKey();
					return;
				}
				int nextIndex = mask.indexOf('#', length);

				String currentMask = "";
				if (nextIndex >= 0)
					currentMask = mask.substring(length, nextIndex);
				else
					currentMask = mask.substring(length);
				String newText;
				if (Character.isDigit(keyCode)) {
					if (!currentMask.equals("#"))
						newText = text + currentMask + String.valueOf(keyCode);
					else
						newText = text + keyCode;
					//((TextBox) sender).setText(newText);
					setText(newText);
					((TextBox) sender).setCursorPos(newText.length());
				}
				((TextBox) sender).cancelKey();
			}

		});
	}

	public NewMaskTextFieldWidget(String labelText, String width, String textMask) {
		this(labelText, textMask);
		textBox.setWidth(width);
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getText() {
		String origtext = super.getText();
		String strip = "";
		try {
			for (int i = 0; i < origtext.length(); i++) {
				if (Character.isDigit(origtext.charAt(i)))
					strip = strip.concat(String.valueOf(origtext.charAt(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strip;
	}

	public void setText(String text) {
		// It's ok to set a value, validate should reject it!
		/*
		String fmt = "";
		int textcnt = 0;
		if (text == null || text.length() == 0) {
			super.setText(text);
			return;
		}

		try {
			for (int i = 0; i < mask.length(); i++) {
				if (mask.charAt(i) == '#') {
					fmt = fmt.concat(text.substring(textcnt, textcnt + 1));
					textcnt++;
				} else
					fmt = fmt.concat(mask.substring(i, i + 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		super.setText(text);
	}

	public boolean validate() {
		if (super.validate())
			if (super.getText().length() == mask.length()
					|| super.getText().length() == 0) {
				showError(false);
				return true;
			} else {
				showError("'" + getLabel() + "' is not complete", true);
				return false;
			}
		else
			return false;
	}


}
