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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;

public class ValidateHorizontalPanel extends HorizontalPanel implements Validate {
	
	private Object validate;

	public ValidateHorizontalPanel(Object widget) {
		super();
		this.validate = widget;
	}
	
	/**
	 * Need special handling here, since Validate have a clear method!
	 */
	public void clear() {
		if (validate instanceof Validate) {
			((Validate)validate).clear();
		}
	}
	
	public void clearWidgets() {
		super.clear();
	}
	
	public void addKeyboardListener(KeyboardListener listener) {
		if (validate instanceof Validate) {
			((Validate)validate).addKeyboardListener(listener);
		}
	}

	public boolean validate() {
		if (validate instanceof Validate) {
			return(((Validate)validate).validate());
		}
		return(true);
	}
}
