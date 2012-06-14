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
package org.onecmdb.ui.gwt.desktop.client.widget;

import com.google.gwt.user.client.ui.HTML;

public class AppletFrame extends HTML {

	public AppletFrame() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<applet archive=\"onecmdb-applet.jar, onecmdb-applet-dependencies.jar\"");
		buf.append("code=\"org.onecmdb.ml.graph.applet.TemplateViewApplet.class\" width=\"100%\" height=\"100%\">");
		buf.append("<param name=\"url\" value=\"http://localhost:8888/onecmdb/webservice/onecmdb\">");
		buf.append("<param name=\"token\" value=\"\">");
	
		buf.append(" <hr>");
		buf.append("If you were using a Java-enabled browser such as HotJava,");
		buf.append("you would see dancing text instead of this paragraph.");
		buf.append("<hr>");
		buf.append("</applet>");
		
		setHTML(buf.toString());
	}
}
