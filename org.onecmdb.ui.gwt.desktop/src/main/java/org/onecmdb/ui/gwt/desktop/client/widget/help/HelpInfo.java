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
package org.onecmdb.ui.gwt.desktop.client.widget.help;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.user.client.ui.RootPanel;

public class HelpInfo extends Window {
	
	
	private static HelpInfo helpPanel;
	
	public static void abort() {
		if (CMDBSession.get().getUserPreference().hideQuickHelp()) {
			return;
		}
		HelpInfo panel = getHelpPanel();
		panel.show();
		panel.close(null);
	}
	public static void show(String url) {
		if (CMDBSession.get().getUserPreference().hideQuickHelp()) {
			return;
		}
		HelpInfo panel = getHelpPanel();
		panel.setUrl(url);
		if (!panel.isVisible()) {
			Object mini = panel.getData("minimize");
			if (mini instanceof Boolean) {
				if ((Boolean)mini) {
					return;
				}
			}
			panel.show();
		}
	}

	private static HelpInfo getHelpPanel() {
		if (helpPanel == null) {
			helpPanel = new HelpInfo();
		}
		Desktop desktop = CMDBSession.get().getDesktop();
		boolean found = false;
		for (Window w : desktop.getWindows()) {
			if (w.equals(helpPanel)) {
				found = true;
			}
		}
		if (!found) {
			desktop.addWindow(helpPanel);
			
		}
		return(helpPanel);
	}
	
	protected HelpInfo() {
		super();
		//baseStyle = "x-info";
		setLayoutOnChange(true);
		/*
		setShadow(true);
		
		getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				setVisible(false);
				//el().fadeOut(FxConfig.NONE);
				HelpInfo.helpPanel = null;
			}
			
		}));
		*/ 
		setHeading("OneCMDB Quick Help");  
		//setVisible(true);
		
		setClosable(true);
		setMinimizable(true);
		
		// Position the help...
		Size s = XDOM.getViewportSize();
	    int width = 500;
	    int height = 200;
		int top = s.height - (height+30);
		//int left = 30; // Alight to left; 
		int left = (s.width - (width + 30)); // Alight to right;
		setWidth(width); 
		setHeight(height);
		setPosition(left, top);
		//RootPanel.get().add(helpPanel);

	}
	/*
	protected Point position() {
		    Size s = XDOM.getViewportSize();
		    int left = s.width - config.width - 10 + XDOM.getBodyScrollLeft();
		    int top = s.height - config.height - 10 - (level * (config.height + 10))
		        + XDOM.getBodyScrollTop();
		    return new Point(left, top);
		  }
	*/
	 public void onShowInfo() {
		 	/*
		    RootPanel.get().add(this);
		    el().makePositionable(true);
		    setVisible(true);
		    //el().fadeIn(FxConfig.NONE);  
		    */
	 }

}
