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
package org.onecmdb.core.utils.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICi;
import org.onecmdb.core.utils.bean.CiBean;

public class XmlGenerator {

	private String output;

	List<CiBean> blueprintBeans = new ArrayList<CiBean>();

	List<CiBean> instanceBeans = new ArrayList<CiBean>();

	HashMap<String, Integer> statistics = new HashMap<String, Integer>();
	
	private ICi ci;
	private Log log = LogFactory.getLog(this.getClass()); 

	private boolean compactMode = false;

	public void setOutput(String file) {
		this.output = file;
	}

	
	public void setBeans(List<CiBean> beans) {
		for (CiBean bean : beans) {
			if (bean.isTemplate()) {
				blueprintBeans.add(bean);
			} else {
				instanceBeans.add(bean);
				
				// Add to stat
				Integer count = statistics.get(bean.getDerivedFrom());
				if (count == null) {
					count = new Integer(0);
				}
				count = count + 1;
				statistics.put(bean.getDerivedFrom(), count);
			}
		}
	}

	public void setICi(ICi ci) {
		this.ci = ci;
	}

	public void process() throws IOException {
		if (this.ci != null) {
			// processCi(ci);
		}
		if (this.output.equals("-")) {
			transfer(System.out);
		} else {
			generateXml(new File(output));
		}
		
		log.info("Templates: " + blueprintBeans.size());
		log.info("Instances: " + instanceBeans.size());
		for (String key : statistics.keySet()) {
			log.info("\t" + key + ": " + statistics.get(key));
		}
	}
	
	public void generateXml(File file) throws IOException {
		
		OutputStream out = new FileOutputStream(file);
		
		try {
			transfer(out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
			
	public void transfer(OutputStream out) throws IOException {
		OutputStreamWriter outw = new OutputStreamWriter(out, "UTF-8");
		PrintWriter output = new PrintWriter(outw, true);
		transfer(output);
	}
	
	public void transfer(PrintWriter output) throws IOException {
		
		output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		output.println("<" + XmlParser.ROOT_ELEMENT.getName() + ">");
		output.println("<" + XmlParser.TEMPLATES_ELEMENT.getName() + ">");
		
		for (CiBean bean : blueprintBeans) {
			output.println(bean.toXML(1, compactMode));
		}
		output.println("</" + XmlParser.TEMPLATES_ELEMENT.getName() + ">");
		
		output.println("<" + XmlParser.INSTANCES_ELEMENT.getName() + ">");
		for (CiBean bean : instanceBeans) {
			output.println(bean.toXML(1, compactMode));
		}
		output.println("</" + XmlParser.INSTANCES_ELEMENT.getName() + ">");
		output.println("</" + XmlParser.ROOT_ELEMENT.getName() + ">");
		
	}


	public void setCompactMode(boolean compactMode) {
		this.compactMode  = compactMode;
		
	}
}
