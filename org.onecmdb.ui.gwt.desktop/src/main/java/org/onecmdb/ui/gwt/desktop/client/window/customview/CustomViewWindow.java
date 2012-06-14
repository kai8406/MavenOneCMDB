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
package org.onecmdb.ui.gwt.desktop.client.window.customview;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.widget.customview.CustomViewSelectWidget;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class CustomViewWindow extends CMDBAbstractWidget {
	
	/**
	 * 
		<menuitem text="Custom Views" iconStyle="menuitem-icon-folder" asList="true">
		<menuitem text="Default" asList="true">
			<widget id="cmdb-custom-view">
				<heading>OneCMDB - Custom Views</heading>
				<params>
					<customDefinition>CustomViews/defaultCustomView.xml</customDefinition>
				</params>
			</widget>
		</menuitem>		
	</menuitem>
	 */
	
	
	public static final String ID = "cmdb-custom-view";

	public CustomViewWindow(CMDBDesktopWindowItem item) {
		super(item);
	}
	

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		setLayout(new FitLayout());
		
		String customDef = item.getParams().get("customDefinition");
		String root = item.getParams().get("rootElement");
		String record = item.getParams().get("recordElement");
		
		CustomViewSelectWidget sel = new CustomViewSelectWidget(root, record);
		sel.setCustomFile(customDef);
		
		add(sel);
		layout();
	}







	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Open Custom Views");
		desc.setDescription("Show a list of custom views that an be opened.");
		desc.addParameter("customDefinition - The xml definitions containing all views.");
		desc.addParameter("rootElement - The root element name in the XML.");
		desc.addParameter("recordElement - The element to fetch iterate through.");
		
		return(desc);
	}

}
