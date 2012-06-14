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

import java.util.HashMap;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopMenuItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBNotFoundWidget;
import org.onecmdb.ui.gwt.desktop.client.window.DesktopWidgetFactory;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class WindowFactory {

	private static HashMap<String, Window> singletonMap = new HashMap<String, Window>();



	public static Window getWindow(String header, Component data) {
		 	final Window w = new Window();
		    w.setCloseAction(CloseAction.CLOSE);
		    w.setMinimizable(true);
		    w.setMaximizable(true);
		    w.setIconStyle("property-icon");
		    w.setHeading(header);
		    w.setWidth(600);
		    w.setHeight(400);
		    w.setLayout(new FitLayout());
		    w.add(data);
		    CMDBSession.get().getDesktop().addWindow(w);
		    return(w);
	}
	
	
	/*
	public static Window allocWindow(CMDBDesktopWindowItem item) {
		if (item.isSingleton()) {
			Window w = singletonMap.get(item.getID());
			if (w != null) {
				return(w);
			}
		}
		final Window w = new Window();
	    w.setCloseAction(CloseAction.CLOSE);
	    w.setMinimizable(true);
	    w.setMaximizable(true);
	    w.setIconStyle(item.getIconStyle());
	    w.setHeading(item.getHeading());
	    w.setWidth(item.getWidth());
	    w.setHeight(item.getHeight());
	    w.setLayout(new FitLayout());
	    w.getHeader().addTool(new ToolButton("x-tool-help"));
	    
	    Widget widget = DesktopWidgetFactory.get().createWidget(item);
	    if (widget == null) {
	    	w.add(new CMDBNotFoundWidget(item));
	    } else {
	    	w.add(widget);
	    }
	    if (item.isSingleton()) {
	    	singletonMap.put(item.getID(), w);
	    }
	    return(w);
	}
	*/


	public static Window showWindow(Desktop desktop, final CMDBDesktopWindowItem item) {
		
		Config config = CMDBSession.get().getConfig();
		String defaultWidth = config.get(Config.DEFAULT_WINDOW_WIDTH);
		String defaultHeight = config.get(Config.DEFAULT_WINDOW_HEIGHT);
		
		
		if (item.isSingleton()) { 
			Window w = singletonMap.get(item.getID());
			if (w != null) {
				if (!w.isVisible()) {
					w.setVisible(true);
				}
				return(w);
				//w.close();
				//w = null;
				/*
				desktop.addWindow(w);
				w.setSize(defaultWidth, defaultHeight);
				w.show();
				return(w);
				*/
			}
		}
		
		
		final Window w = new Window();
	    w.setCloseAction(CloseAction.CLOSE);
	    
	    w.setMinimizable(item.isMinimizable());
	    w.setMaximizable(item.isMaximizable());
	    w.setIconStyle(item.getIconStyle());
	    w.setHeading(item.getHeading());
	    
	    if (item.getX() != null && item.getY() != null) {
	    	  w.setPagePosition(Integer.parseInt(item.getX()), Integer.parseInt(item.getY()));
	    }
	    
	    int width = 0;
	    int height = 0;
	    if (defaultWidth != null && !item.hasWidth()) {
	    	width = convertSize(defaultWidth);
	    	//w.setWidth(defaultWidth);
	    } else {
	    	width = convertSize(item.getWidth());
	    	//w.setWidth(item.getWidth());
	    }
	    if (defaultHeight != null && !item.hasHeight()) {
	    	height = convertSize(defaultHeight);
	    	//w.setHeight(defaultHeight);
	    } else {
	    	//w.setHeight(item.getHeight());
	    	height = convertSize(item.getHeight());
	    }
	    
	    // Check that the window is not extending the view port.
	    handleWindowSize(item, w, width, height);
	      
	   
	    w.setLayout(new FitLayout());
	    w.setOnEsc(false);
	    
	    if (item.getHelp() != null) {
	    	  w.getHeader().addTool(new ToolButton("x-tool-help", new SelectionListener<ComponentEvent>() {
	    		@Override
				public void componentSelected(ComponentEvent ce) {
	    			com.google.gwt.user.client.Window.open(item.getHelp(), "OneCMDB_Help", "");
	    			/*  
	    			final Window window = new Window();  
		    		  window.setSize(600, 600);  
		    		  window.setPlain(true);  
		    		  window.setHeading("Help - " + item.getHeading());  
		    		  window.setLayout(new FitLayout());
		    		  Frame f = new Frame(item.getHelp());
				  window.add(f);
	    		  window.show();
	    		  */
				}
	    	  }));
	    }
	    
	    // Do this before adding the widget, 
	    // problem with applet loading, before window is showing.
	    desktop.addWindow(w);
	    w.show();
	    
	    Widget widget = DesktopWidgetFactory.get().createWidget(item);
	    if (widget == null) {
	    	w.add(new CMDBNotFoundWidget(item));
	    } else {
	    	w.add(widget);
	    }
	    if (item.isSingleton()) {
	    	singletonMap.put(item.getID(), w);
	    }
	    return(w);
	}

	/**
	 * Convert px into size.
	 * @param height
	 * @return
	 */
	private static int convertSize(String px) {
		if (px == null) {
			return(500);
		}
		if (!px.endsWith("px")) {
			return(500);
		}
		try {
		int i = Integer.parseInt(px.substring(0, px.length()-2));
		return(i);
		} catch(Throwable e) {
			return(500);
		}
	}


	public static void handleWindowSize(CMDBDesktopWindowItem item, Window window, int w, int h) {
		Size viewPortSize = XDOM.getViewportSize();
		int x = 0;
		int y = 0;
		if (item == null || item.getX() == null) {
			// Center x
			x = (viewPortSize.width - w) / 2;
		} else {
			x = Integer.parseInt(item.getX());
		}
		if (item == null || item.getY() == null) {
			y = (viewPortSize.height - h) / 2;
		} else {
			y = Integer.parseInt(item.getY());
		}
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		int outsideX = (x + w) - viewPortSize.width;
		if (outsideX > 0) {
			w = w - outsideX;
		}
		window.setWidth(w);
		
		// Take the startbar intoaccount.
		int outsideY = (y + h) - (viewPortSize.height - 50);
		if (outsideY > 0) {
			h = h - outsideY;
		}
		window.setHeight(h);
		
	}
	
	
}
