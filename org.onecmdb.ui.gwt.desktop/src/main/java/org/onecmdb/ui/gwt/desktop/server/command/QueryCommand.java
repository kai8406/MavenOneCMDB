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
package org.onecmdb.ui.gwt.desktop.server.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;
import org.onecmdb.ui.gwt.desktop.server.transform.OneCMDBTransform;



public class QueryCommand extends AbstractOneCMDBCommand {

	private String contentType = "text/xml";
	private String query;
	private String xslt;
	private String style;
	

	Log log = LogFactory.getLog(this.getClass());
	private Properties attrMap;
	
	// Self test..
	public static void main(String argv[]) {
		try {
			QueryCommand cmd = new QueryCommand();
			cmd.setQuery(argv[0]);
			cmd.setStyle(argv[1]);
			System.out.println("ContentType: " + cmd.getContentType());
			cmd.transfer(System.out);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getXslt() {
		return xslt;
	}

	public void setXslt(String xslt) {
		this.xslt = xslt;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return(contentType);
	}

	public void transfer(OutputStream out) throws Throwable {
		
		OneCMDBTransform transform = new OneCMDBTransform();
		transform.setAttrMap(attrMap);
		transform.setService(getService());
		transform.setQuery(query);
		if (xslt != null) {
			transform.setXsltData(xslt);
		}
		if (style != null) {
			transform.setXmlStyle(style);
		}
		transform.setToken(getToken());
		transform.process(new PrintWriter(new OutputStreamWriter(out, "UTF-8")));
	}


	public Properties getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Properties attrMap) {
		this.attrMap = attrMap;
	}

	
}
