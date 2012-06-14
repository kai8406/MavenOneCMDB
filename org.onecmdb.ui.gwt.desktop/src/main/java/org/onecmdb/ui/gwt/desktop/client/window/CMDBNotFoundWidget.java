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
package org.onecmdb.ui.gwt.desktop.client.window;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.utils.GXTModel2XML;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class CMDBNotFoundWidget extends CMDBAbstractWidget {
	
	

	public static final String ID = "cmdb-widget-id-not-found";

	public CMDBNotFoundWidget(CMDBDesktopWindowItem item) {
		super(item);
		
		setLayout(new FitLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		HTML html = new HTML();
		html.setText(getInfo());
		add(html);
		layout();
	}

	private String getInfo() {
		StringBuffer b = new StringBuffer();
		b.append("Widget id not found!\n\n");
		b.append("ID:  " + item.getID() +"\n\n");	
		b.append("XML Definition:\n");
		b.append(GXTModel2XML.toXML("widget", item));
		return(b.toString());
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Widget ID Not Found");
		desc.setDescription("Window to show if a widget id is mot found");
		desc.addParameter("None");
		return(desc);
	}
}
