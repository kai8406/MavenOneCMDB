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
package org.onecmdb.ui.gwt.toolkit.client.view.ci;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.TooltipPopup;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CIDisplayNameWidget extends CIWidget {

	private ClickListener listener;
	
	public CIDisplayNameWidget() {
		super();
	}
	
	public CIDisplayNameWidget(String type, ClickListener listener) {
		super(type);
		this.listener = listener;
		update();
	}
	
	public CIDisplayNameWidget(String type) {
		super(type);
		update();
	}
	
	public CIDisplayNameWidget(GWT_CiBean bean) {
		super(bean);
		update();
	}

	public CIDisplayNameWidget(GWT_CiBean bean, ClickListener clickListener) {
		super(bean);
		this.listener = clickListener;
		update();
	}

	public void load(GWT_CiBean bean) {
		vPanel.clear();
		if (bean != null) {
			String displayName = bean.getDisplayName();
			if ((displayName == null || displayName.length() == 0)) {
				displayName = "[" + bean.getAlias() + "]";
			}
			Widget l = null;
			if (this.listener != null) {
				HTML h = new HTML("<a href='javascript:;'>" + displayName +"</a>", false);
				// Replace sender....
				h.addClickListener(new ClickListener() {

					public void onClick(Widget sender) {
						listener.onClick(CIDisplayNameWidget.this);
					}
					
				});
				l = h;
			} else {
				l = new Label(displayName, false);
			}
			vPanel.add(l);
			// Add title.
			new TooltipPopup(l, getTitle(bean));
			
			vPanel.setCellHorizontalAlignment(l, HorizontalPanel.ALIGN_LEFT);
		} else {
			// Add null value...
			vPanel.add(new Label(""));
		}
	}

	public ClickListener getListener() {
		return listener;
	}

	public void setListener(ClickListener listener) {
		this.listener = listener;
	}

	private String getTitle(GWT_CiBean bean) {
		StringBuffer b = new StringBuffer();
		b.append("<b>Alias</b><br/>");
		b.append(bean.getAlias());		
		b.append("<br/><br/>");
		b.append("<b>Description</b><br/>");
		b.append(bean.getDescription());
		return(b.toString());
	}

}
