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
package org.onecmdb.ui.gwt.desktop.client.window.mdr;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.widget.help.HelpInfo;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class MDRViewWindow extends CMDBAbstractWidget {
	public static final String ID = "cmdb-mdr-view";
	private MDRDetailWindow detail;
		
	public MDRViewWindow(CMDBDesktopWindowItem item) {
		super(item);
		detail = new MDRDetailWindow(item);
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		ContentFile mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		super.onRender(parent, index);
		setLayout(new FitLayout());
	
		TabPanel tab = new TabPanel();
		TabItem overviewItem = new TabItem("MDR Overview");
		overviewItem.setLayout(new FitLayout());
		overviewItem.add(new MDROverview(mdr, this.permissions));
		
		TabItem detailTab = new TabItem("MDR Details");
		detailTab.setLayout(new FitLayout());
		detailTab.add(detail);
		
		tab.add(overviewItem);
		tab.add(detailTab);
		
		add(tab);
		layout();
		
		if (getParent() instanceof Window) {
			 ((Window)getParent()).addListener(Events.BeforeClose, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					HelpInfo.abort();
				}
			 });
		 }
	}



	@Override
	public WidgetDescription getDescription() {
		return(detail.getDescription());
	}
}
