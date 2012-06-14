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

import org.onecmdb.ui.gwt.toolkit.client.control.select.SelectInheritanceDataSourceControl;
import org.onecmdb.ui.gwt.toolkit.client.control.select.SelectMultipleDataSourceControl;
import org.onecmdb.ui.gwt.toolkit.client.view.dnd.DragControl;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.CITreeWidget;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectCIPopup extends PopupPanel {

	

	private SelectInheritanceDataSourceControl control;
	private String headerText;

	public SelectCIPopup(String header, SelectInheritanceDataSourceControl control) {
		super(false);
		this.headerText = header;
		this.control = control;
		
		load();	
	
	}
	
	/*
	public SelectCIPopup(String header, String type) {
		this(header, new SelectInheritanceDataSourceControl(type));
	}
	*/
	
	protected void load() {
		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel header = new HorizontalPanel();
		header.setStyleName("one-select-popup-header");
		header.setWidth("100%");
		Label headerLabel = new Label(headerText);
		Image close = new Image("images/eclipse/close.gif");
		header.add(headerLabel);
		if (control instanceof SelectMultipleDataSourceControl) {
			HTML submit = new HTML("[<a href='javascript:;'>save</a>]");
			submit.setStyleName("one-submit-label");
			submit.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					control.getSelectListener().onSelect(((SelectMultipleDataSourceControl)control).getSelection());
				}
			});
			header.add(submit);
			header.setCellHorizontalAlignment(submit, HorizontalPanel.ALIGN_RIGHT);
		}
		
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
		
		setStyleName("one-select-popup");
		
		control.setRootState(true);
		CITreeWidget templateTreeWidget = new CITreeWidget(control);
		
		templateTreeWidget.setSize("100%", "100%");
		ScrollPanel content = new ScrollPanel(templateTreeWidget);
		content.setHeight("300px");
		panel.add(header);
		panel.add(content);
		
		//panel.setCellVerticalAlignment(header, VerticalPanel.ALIGN_TOP);
		panel.setCellHeight(content, "100%");
		panel.setCellWidth(content, "100%");
		panel.setCellVerticalAlignment(content, VerticalPanel.ALIGN_TOP);
		setWidget(panel);

	}
	
	/*
	public void setSelectListener(ISelectListener callback) {
		this.control.setSelectListener(callback);
	}
	*/
}
