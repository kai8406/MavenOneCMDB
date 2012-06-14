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
package org.onecmdb.ui.gwt.desktop.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class BorderLayoutTest extends LayoutContainer  {

	
	public BorderLayoutTest() {
		 setLayout(new BorderLayout());  
		       
		 setLayoutOnChange(true);
		 
		     LayoutContainer north = new LayoutContainer();  
		     
		     ContentPanel west = new ContentPanel();  
		     ContentPanel center = new ContentPanel();  
		     ContentPanel east = new ContentPanel();  
		     LayoutContainer south = new LayoutContainer();  
		       
		     BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 50);  
		     northData.setCollapsible(false);  
		     //northData.setFloatable(false);  
		     //northData.setSplit(false);  
		     northData.setMargins(new Margins(5, 5, 0, 5));  
		       
		     BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
		     westData.setSplit(true);  
		     westData.setCollapsible(true);  
		     westData.setMargins(new Margins(5));  
		       
		     BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		     centerData.setMargins(new Margins(5, 0, 5, 0));  
		       
		       
		     BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 200);  
		     eastData.setSplit(true);  
		     eastData.setCollapsible(true);  
		     eastData.setMargins(new Margins(5));  
		       
		     BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 50);  
		     //southData.setSplit(false);  
		     southData.setCollapsible(false);  
		     //southData.setFloatable(false);  
		     southData.setMargins(new Margins(0, 5, 5, 5));  
		       
		     add(north, northData);  
		     add(west, westData);  
		     add(center, centerData);  
		     add(east, eastData);  
		     add(south, southData);  
		}
}
