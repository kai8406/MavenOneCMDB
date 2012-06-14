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
package org.onecmdb.rest.graph.applet;

import java.applet.AppletContext;

import javax.swing.JApplet;

import org.onecmdb.rest.graph.io.OneCMDBConnection;
import org.onecmdb.rest.graph.main.MainInstanceBrowser;
import org.onecmdb.rest.graph.model.prefuse.TemplateModelControl;
import org.onecmdb.rest.graph.utils.applet.AppletLogger;
import org.onecmdb.rest.graph.utils.applet.AppletProperties;

import prefuse.activity.ActivityManager;

public class InstanceViewApplet extends JApplet {

	private MainInstanceBrowser mainFrame;
	private String template;
	private String alias;

	@Override
	public void destroy() {
		AppletControl.destroy();
		/*
		// Stop Prefuse....
		ActivityManager.stopThread();
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		*/
		super.destroy();
	}

	@Override
	public AppletContext getAppletContext() {
		// TODO Auto-generated method stub
		return super.getAppletContext();
	}

	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return super.getParameter(name);
	}

	@Override
	public String[][] getParameterInfo() {
		return(new String[][] {
				{"token","string","An authenticated token to OneCMDB"},
				{"url","string","The URL to oneCMDB WSDL"},
				{"rootCI","string","The root CI alias of the template tree"},
				{"template", "string","The template to list instnaces of"},
				{"iconURL", "string", "The url to fetch images."},
				{"graphBackgroundColor", "string", "The backgroud color in hex"},
		});
	}

	@Override
	public void init() {
		super.init();
		
		AppletControl.init();
		try {
		// Setup initial connectrion.
		OneCMDBConnection con = new OneCMDBConnection();
		String token = getParameter("token");
		String url = getParameter("url");
		
		AppletLogger.showMessage("CMDB REST URL: " + url);
		con.setToken(token);
		con.setUrl(url);
		
		
			con.setup();
		con.setIconURL(getParameter("iconURL"));
		OneCMDBConnection.setInstance(con);

		String rootCI = getParameter("rootCI");
		if (rootCI == null || rootCI.length() == 0) {
			rootCI = "Ci";
		}
		template = getParameter("template");
		if (template == null || template.length() == 0) {
			template = "Ci";
		}
		
		alias = getParameter("alias");
		
		TemplateModelControl.reset();
		
		AppletProperties.set("graphBackgroundColor", getParameter("graphBackgroundColor"));
		System.out.println("Root CI: " + rootCI);
		AppletLogger.showMessage("Root CI :" + rootCI);
		mainFrame = new MainInstanceBrowser(rootCI);
		} catch (Throwable t) {
			destroy();
			IllegalArgumentException e = new IllegalArgumentException("Setup CMDB Connection: " + t.getMessage());
			e.initCause(t);
			throw e;
		}

	}
	
	public void stop() {
		AppletControl.stop();
	}

	public void start() {
		AppletControl.start();
		getContentPane().add(mainFrame);
		mainFrame.setInstanceTemplate(template, alias);
	}
	
	
}
