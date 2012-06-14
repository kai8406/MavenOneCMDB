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
package org.onecmdb.ui.gwt.desktop.client.fixes.combo;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.google.gwt.user.client.Element;

/**
 * Encapsulat any component to rendered inside a menu item.
 * The component must fire Events.Select when it's ready or Events.Hide if it's 
 * canceled.
 * 
 * @author niklas
 *
 */
public class AdaptableMenuItem extends Item {

	  protected Component comp;

	  /**
	   * Creates a new menu item.
	   */
	  public AdaptableMenuItem(Component comp) {
	    hideOnClick = true;
	    this.comp = comp;
	    comp.addListener(Events.Select, new Listener<ComponentEvent>() {
	      public void handleEvent(ComponentEvent ce) {
	        parentMenu.fireEvent(Events.Select, ce);
	        parentMenu.hide(true);
	      }
	    });
	    comp.addListener(Events.Hide, new Listener<ComponentEvent>() {
		      public void handleEvent(ComponentEvent ce) {
		        parentMenu.fireEvent(Events.Hide, ce);
		        parentMenu.hide(true);
		      }
		    });
	  }
	  
	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	    comp.setWidth("600px");
	    comp.setHeight("400px");
	    
	    comp.render(target, index);
	    setElement(comp.getElement());
	  }

	  @Override
	  protected void handleClick(ComponentEvent be) {
	    comp.onComponentEvent((ComponentEvent) be);
	  }

	public Component getComponent() {
		return(comp);
	}


}
