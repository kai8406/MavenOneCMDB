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
package org.onecmdb.ui.gwt.desktop.client.widget.multi;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.google.gwt.user.client.Element;

public class MultiValueMenuItem  extends Item {

	  protected MultiValueGrid grid;

	  /**
	   * Creates a new menu item.
	 * @param perm 
	   */
	  public MultiValueMenuItem(AttributeColumnConfig config, CMDBPermissions perm) {
	    hideOnClick = true;
	    grid = new MultiValueGrid(config);
	    grid.setPermissions(perm);
	    grid.addListener(Events.Select, new Listener<ComponentEvent>() {
	      public void handleEvent(ComponentEvent ce) {
	        parentMenu.fireEvent(Events.Select, ce);
	        parentMenu.hide(true);
	      }
	    });
	    grid.addListener(Events.Hide, new Listener<ComponentEvent>() {
		      public void handleEvent(ComponentEvent ce) {
		        parentMenu.fireEvent(Events.Hide, ce);
		        //parentMenu.hide(true);
		      }
		    });
	  }
	  
	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	    grid.render(target, index);
	    setElement(grid.getElement());
	  }

	  @Override
	  protected void handleClick(ComponentEvent be) {
	    grid.onComponentEvent((ComponentEvent) be);
	  }

	public MultiValueGrid getGrid() {
		return(grid);
	}


}
