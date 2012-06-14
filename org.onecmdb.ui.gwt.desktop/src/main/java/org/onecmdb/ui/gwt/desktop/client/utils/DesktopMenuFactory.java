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
package org.onecmdb.ui.gwt.desktop.client.utils;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopMenuItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;

public class DesktopMenuFactory {
	
	public static Item getMenuItem(final CMDBDesktopMenuItem desktopItem) {
		if (desktopItem.isSeparator()) {
			return(new SeparatorMenuItem());
		}
		MenuItem item = new MenuItem();
		item.setText(desktopItem.getText());
		item.setIconStyle(desktopItem.getIconStyle());
		String toolTip = desktopItem.getTooltip();
		if (toolTip != null && toolTip.length() > 0) {
			item.setToolTip(toolTip);
		}
		if (desktopItem.getWindowItem() != null) {
			item.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					Dispatcher.get().dispatch(new AppEvent<CMDBDesktopMenuItem>(CMDBEvents.DESKTOP_MENU_SELECTED, desktopItem));
				}
			});
			if (desktopItem.getWindowItem().isOpenAtStartup()) {
				Dispatcher.get().dispatch(new AppEvent<CMDBDesktopMenuItem>(CMDBEvents.DESKTOP_MENU_SELECTED, desktopItem));
			}
		}
		
		// Check if submenus.
		if (desktopItem.getMenuItem()!= null && desktopItem.getMenuItem().size() > 0) {
			Menu subMenu = new Menu();
			List items = desktopItem.getMenuItem();
			for (CMDBDesktopMenuItem dItem : desktopItem.getMenuItem()) {
				subMenu.add(getMenuItem(dItem));
			}
			item.setSubMenu(subMenu);
		}
		
		return(item);
	}

	public static void updateShortcuts(Desktop desktop, final CMDBDesktopMenuItem desktopItem) {
		if (desktopItem.getWindowItem() != null) {
			CMDBDesktopWindowItem windowItem = desktopItem.getWindowItem();
		
			Object o = windowItem.get("shortcut");
			if (o instanceof BaseModel) {
				BaseModel shortcut = windowItem.get("shortcut");
			
				Shortcut s = new Shortcut();
				String sText = shortcut.get("text", "[Not specified!}");
				String style = shortcut.get("style", "shortcuts-default");
				String tooltip = shortcut.get("tooltip");
					
				s.setText(sText);
				s.setId(style);
				if (tooltip != null && tooltip.length() > 0) {
					s.setToolTip(tooltip);
				}
				s.addSelectionListener(new SelectionListener<ComponentEvent>() {
					@Override
					public void componentSelected(ComponentEvent ce) {
						Dispatcher.get().dispatch(new AppEvent<CMDBDesktopMenuItem>(CMDBEvents.DESKTOP_MENU_SELECTED, desktopItem));
					}
				});
				desktop.addShortcut(s);
			}
		}
		// Check if submenus.
		if (desktopItem.getMenuItem()!= null && desktopItem.getMenuItem().size() > 0) {
			for (CMDBDesktopMenuItem childItem : desktopItem.getMenuItem()) {
				updateShortcuts(desktop, childItem);
			}
		}
	}
}
