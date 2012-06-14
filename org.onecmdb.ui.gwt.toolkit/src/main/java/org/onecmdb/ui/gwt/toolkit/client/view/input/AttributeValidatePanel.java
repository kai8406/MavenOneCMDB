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

import java.util.Iterator;
import java.util.List;

import org.gwtiger.client.widget.panel.ButtonPanel;
import org.gwtiger.client.widget.panel.ValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeValueControl;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListenerCollection;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO: Merge this with CIValueInputPanel... 
 *
 */
public class AttributeValidatePanel extends ValidatePanel {

	
	private IAttributeValueControl ctrl;
	private LoadListenerCollection loadListenerCollection = new LoadListenerCollection();

	public AttributeValidatePanel(IAttributeValueControl ctrl) {
		this.ctrl = ctrl;
	}

	public void addLoadListener(LoadListener listener) {
		loadListenerCollection.add(listener);
	}
	
	public void load() {
		loadListenerCollection.fireOnLoadStart(this);
		this.ctrl.getAttributes(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				loadListenerCollection.fireOnLoadFailure(this, caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof List) {
					removeChildren();
					for (Iterator iter = ((List)result).iterator(); iter.hasNext();) {
						Widget inputWidget = ctrl.getAttributeRender().getWidget(iter.next());
						if (inputWidget != null) {
							Widget w = (Widget)inputWidget;
							add(w);
						}
					}
					loadListenerCollection.fireOnLoadComplete(this);
				}				
			}
		});
	}

	private void removeChildren() {
		Iterator it = iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
		/*  
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget child = getWidget(i);
			remove(child);
		}
		*/
	}

	public void setButtonPanel(ButtonPanel panel) {
		super.add(panel);
		
	}
}
