/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.ui.gwt.desktop.client.window.content;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.widget.ContentViewerWidget;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Element;

public class CMDBContentBrowserWidget extends CMDBAbstractWidget {

	public static final String ID = "cmdb-content-browser";

	public CMDBContentBrowserWidget(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FillLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		String path = item.getParams().get("path");
		ContentFolder folder = new ContentFolder();
		if (path != null) {
			folder.setPath(path);
		}
		ContentViewerWidget widget = new ContentViewerWidget(folder);
		String rd = (String)item.getParams().get("readonly", "false");
		boolean readOnly = Boolean.parseBoolean(rd);
		widget.setReadonly(readOnly);
		add(widget);
		
		layout();
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Content Browser Widget");
		desc.setDescription("A Widget that browse content on the CMDB server. Edit content is also supported.");
		desc.addParameter("<li>readonly - true if content is readonly.<li>");
		desc.addParameterEntry("readonly", "false", false, "Support edit of content or not.");
		desc.addParameterEntry("path", null, false, "Path in the repository.");
		
		return(desc);	
	}
}
