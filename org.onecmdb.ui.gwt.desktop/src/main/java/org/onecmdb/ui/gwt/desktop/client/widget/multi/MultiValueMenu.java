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
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;

import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.menu.Menu;

public class MultiValueMenu extends Menu {

	  /**
	   * The internal date picker.
	   */
	  protected MultiValueGrid grid;
	
	  private MultiValueMenuItem item;

	  public MultiValueMenu(AttributeColumnConfig config, CMDBPermissions perm) {
	    item = new MultiValueMenuItem(config, perm);
	    grid = item.getGrid();
	    add(item);
	    baseStyle = "x-date-menu";
	    setAutoHeight(true);
	  }

	  
	 
	  /**
	   * Returns the date picker.
	   * 
	   * @return the date picker
	   */
	  public MultiValueGrid getMultiValueGrid() {
	    return(item.getGrid());
	  }

	  @Override
	  protected void doAttachChildren() {
	    super.doAttachChildren();
	    ComponentHelper.doAttach(item.getGrid());
	  }

	  @Override
	  protected void doDetachChildren() {
	    super.doDetachChildren();
	    ComponentHelper.doDetach(item.getGrid());
	  }
}
