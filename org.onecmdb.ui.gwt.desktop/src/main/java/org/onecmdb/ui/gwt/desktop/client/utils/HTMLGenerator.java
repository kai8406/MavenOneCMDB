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

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;

public class HTMLGenerator {

	public static String toHTML(StoreResult result) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<br/><br/><p><b>Commit Result</b><br>");
		buffer.append("<b>" + result.getAdded() + "</b> added objects (references included)<br/>");
		buffer.append("<b>" + result.getModfied() +"</b> modified objects (references included)<br/>");
		buffer.append("<b>" + result.getDelted() + "</b> deleted objects (references included)<br/>");
		buffer.append("Start " + CMDBSession.get().getDateTimeFormat().format(result.getStart()) + "<br/>");
		buffer.append("Stop " +  CMDBSession.get().getDateTimeFormat().format(result.getStop()) + "<br/>");
		/*
		buffer.append("<table>");
		buffer.append(toRow("Start", result.getStart()));
		buffer.append(toRow("Stop", result.getStop()));
		buffer.append(toRow("Added", result.getAdded()));
		buffer.append(toRow("Modified", result.getModfied()));
		buffer.append(toRow("Deleted", result.getDelted()));
		buffer.append("<table></p>");
		*/
		return(buffer.toString());
	}
	
	protected static String toRow(String col1, Object col2) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<tr>");
		
		buffer.append("<td>");
		buffer.append(col1);
		buffer.append("</td>");
		
		buffer.append("<td>");
		buffer.append("<b>");
		buffer.append(col2);
		buffer.append("</b>");
		buffer.append("</td>");
		buffer.append("</tr>");
		
		return(buffer.toString());
	}
}
