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
package org.onecmdb.ui.gwt.toolkit.client.view.screen;


import org.onecmdb.ui.gwt.toolkit.client.IOneCMDBGWTServiceAsync;
import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;

public class OneCMDBBaseScreen extends BaseScreen {

	private OneCMDBApplication baseEntry;

	public OneCMDBBaseScreen() {
		super();
		//setHeaderStyle("one-screen-header");
		setTitleStyle("one-screen-header-title");
	}
	
	public void setBaseEntryScreen(OneCMDBApplication screen) {
		this.baseEntry = screen;
	}
	
	public OneCMDBApplication getBaseEntryScreen() {
		return(this.baseEntry);
	}
	
	protected void setHeaderStyle(String style) {
		// Need to fetch the header panel from dockpanel.
		for (int i = 0; i < dockPanel.getWidgetCount(); i++) {
			Widget w = dockPanel.getWidget(i);
			DockLayoutConstant direction = dockPanel.getWidgetDirection(w);
			if (direction.equals(DockPanel.NORTH)) {
				w.setStyleName(style);
			}
		}
	}
	
	public IOneCMDBGWTServiceAsync getService() {
		return(OneCMDBConnector.getInstance());
	}

	public boolean isScrollable() {
		return(true);
	}

	public boolean isRightPanel() {
		return(true);
	}
}
