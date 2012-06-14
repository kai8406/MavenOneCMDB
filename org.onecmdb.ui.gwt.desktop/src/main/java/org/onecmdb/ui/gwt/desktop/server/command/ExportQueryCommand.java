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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;
import org.onecmdb.ui.gwt.desktop.server.transform.OneCMDBTransform;
import org.onecmdb.utils.transform.ICMDBTransform;

import com.sun.corba.se.pept.encoding.InputObject;



public class ExportQueryCommand extends AbstractOneCMDBCommand {

	private String name;
	private String root = null;
	private Properties properties;

	Log log = LogFactory.getLog(this.getClass());
	private Properties attrMap;
	private String encoding = "UTF-8";
	
	
	// Self test..
	public static void main(String argv[]) {
		try {
			ExportQueryCommand cmd = new ExportQueryCommand();
			cmd.setRoot(argv[0]);
			cmd.setName(argv[1]);
			System.out.println("ContentType: " + cmd.getContentType());
			cmd.transfer(System.out);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	

	public String getEncoding() {
		return encoding;
	}



	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}



	public String getContentType() {
		if (name == null) {
			throw new IllegalArgumentException("name attribute need to be specified in the URL!");
		}
		String contentType = "text/plain";
		if (properties == null) {
			InputStream in = null;
			properties = new Properties();
			File pFile = new File(getReportRoot(), name);
			try {
				in = new FileInputStream(pFile);
				properties.load(in);
			} catch (IOException e) {
				log.warn("Property file '" + pFile.getPath() + "' not found!", e);
				throw new IllegalArgumentException("Property file '" + pFile.getPath() + "' not found!");
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
						// Silently ignore..
					}
				}
			}
		}
		
		if (properties.containsKey("contentType")) {
			contentType = properties.getProperty("contentType");
		}
		return(contentType);
	}
	
	private String getReportRoot() {
		String reportHome = ConfigurationFactory.get(Config.REPORT_HOME);
		return(getRoot() + "/" + reportHome);
	}



	public void setName(String name) throws Exception {
		log.info("setName(" + name + ")");
		this.name = name;
	}

	public String getRoot() {
		return(this.root);
	}
	
	
	public String getName() {
		return name;
	}

	public void setRoot(String root) {
		log.info("Set ExportHandler Root to <" + root + ">");
		this.root = root;
	}


	private void process(String javaClass, OutputStream out) throws Exception {
		ICMDBTransform transform = getInstance(javaClass);
		transform.setProperties(attrMap);
		if (transform.supportWriter()) {
			transform.process(new PrintWriter(new OutputStreamWriter(out, encoding)));
		} else {
			transform.process(out);
		}
	}



	private ICMDBTransform getInstance(String javaClass) throws Exception {
		Class cl = Class.forName(javaClass);
		ICMDBTransform tr = (ICMDBTransform) cl.newInstance();
		return(tr);
	}



	public Properties getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Properties attrMap) {
		this.attrMap = attrMap;
	}



	public void transfer(OutputStream out) throws Throwable {
		if (properties == null) {
			return;
		}
		String queryFile = properties.getProperty("queryFile");
		String xslt = properties.getProperty("xsltFile");
		String xmlStyle = properties.getProperty("xmlStyle");
		String javaClass = properties.getProperty("javaTransformClass");
		
		if (javaClass != null) {
			process(javaClass, out);
			return;
		}
		File qFile = new File(getRoot(), queryFile);
		if (!qFile.exists()) {
			throw new IllegalArgumentException("Query File '" + queryFile + "' is not found!");
		}
		
		log.info("Transfer(" + queryFile + ", " + xslt + ")");
		
		
		
		OneCMDBTransform transform = new OneCMDBTransform();
		transform.setAttrMap(attrMap);
		transform.setService(getService());
		transform.setQueryFile("file:///" + getRoot() + "/" + queryFile);
		if (xslt != null) {
			transform.setXSLT("file:///" + getRoot() + "/" + xslt);
		}
		if (xmlStyle != null) {
			transform.setXmlStyle(xmlStyle);
		}
		transform.setToken(getToken());
		transform.process(new PrintWriter(new OutputStreamWriter(out, encoding)));		
	}

	
}
