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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BaseFieldContainer extends Composite implements Validate,IActionWidget,IActionCallback {
	VerticalPanel vPanel = new VerticalPanel();
	private IActionCallback actionCallback;
	
	public BaseFieldContainer() {
		initWidget(vPanel);
	}
	
	public void addBaseField(Widget w) {
		vPanel.add(w);
		if (w instanceof IActionWidget) {
			((IActionWidget)w).setActionCallback(this);
		}
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

	public void onAction(String action, Object arg) {
		if (this.actionCallback != null) {
			this.actionCallback.onAction(action, arg);
		}
	}


}
