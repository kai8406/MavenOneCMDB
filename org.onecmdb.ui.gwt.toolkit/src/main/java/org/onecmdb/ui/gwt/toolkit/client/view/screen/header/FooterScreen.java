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
package org.onecmdb.ui.gwt.toolkit.client.view.screen.header;

import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FooterScreen extends OneCMDBBaseScreen {
	
	public FooterScreen() {
		HorizontalPanel panel = new HorizontalPanel();
		HTML label = new HTML("OneCMDB 1.4.0 Beta &copy; Lokomo Systems <<a href='javascript:;'>about</a>>");
		label.setTitle("Show about screen");
		label.setStyleName("onecmdb-footer-label");
		panel.add(label);
		panel.setCellHorizontalAlignment(label, HorizontalPanel.ALIGN_RIGHT);
		panel.setStyleName("onecmdb-footer");
		label.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBApplication.SHOW_STATIC_CONTENT, 
						"about.html", 
						new Long(0));
			}
			
		});
		initWidget(panel);
	}
	
	public boolean isScrollable() {
		return(false);
	}

	public boolean isRightPanel() {
		return(false);
	}

}
