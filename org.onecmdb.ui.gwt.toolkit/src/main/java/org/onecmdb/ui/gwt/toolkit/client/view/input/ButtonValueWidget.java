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
package org.onecmdb.ui.gwt.toolkit.client.view.input;

import org.gwtiger.client.widget.field.Validate;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.IActionCallback;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.IActionWidget;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

public class ButtonValueWidget extends Composite implements Validate,IActionWidget {

	private IActionCallback actionCallback;

	public ButtonValueWidget(final String actionName, String label, final Widget left) {
		HorizontalPanel panel = new HorizontalPanel();
		Button b = new Button(label);
		b.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (actionCallback != null) {
					actionCallback.onAction(actionName, left);
				}
			}
			
		});
		panel.add(b);
		panel.add(left);
		initWidget(panel);
	}
	
	public void addKeyboardListener(KeyboardListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setActionCallback(IActionCallback callback) {
		this.actionCallback = callback;
		
	}

}
