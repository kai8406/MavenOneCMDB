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
package org.onecmdb.ui.gwt.toolkit.client.view.popup;

import org.onecmdb.ui.gwt.toolkit.client.view.dnd.DragControl;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DragablePopup extends PopupPanel {
	
	private Widget content;
	private String title;

	public DragablePopup(String title, boolean autoClose) {
		super(autoClose);
		setStyleName("one-select-popup");
		this.title = (title == null ? "" : title);
	}
	
	public void setContent(Widget content) {
		this.content = content;
		load();
	}
	
	public void load() {
		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel header = new HorizontalPanel();
		header.setStyleName("one-select-popup-header");
		header.setWidth("100%");
		Label headerLabel = new Label(title);
		Image close = new Image("images/eclipse/close.gif");
		header.add(headerLabel);
		header.add(close);
		
		header.setCellHorizontalAlignment(headerLabel, HorizontalPanel.ALIGN_LEFT);
		header.setCellHorizontalAlignment(close, HorizontalPanel.ALIGN_RIGHT);
		header.setCellVerticalAlignment(close, HorizontalPanel.ALIGN_MIDDLE);
		
		// Add drag control.
		new DragControl(this, headerLabel);
		
		close.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				hide();	
			}
		});
		panel.add(header);
		panel.add(content);
		
		panel.setCellVerticalAlignment(header, VerticalPanel.ALIGN_TOP);
		panel.setCellVerticalAlignment(content, VerticalPanel.ALIGN_TOP);
		panel.setCellHeight(content, "100%");
		setWidget(panel);
	}
}
