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
package org.onecmdb.core.utils.xpath.generator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.xpath.IOneCMDBContentGenerator;
import org.onecmdb.core.utils.xpath.commands.QueryCommand;
import org.onecmdb.core.utils.xpath.model.IDynamicHandler;

/**
 * Generate Semicolon seperated list (CSV) for a query command.
 *
 */
public class CSVContentGenerator implements IOneCMDBContentGenerator {
	private QueryCommand cmd;
	private String delimiter = ";";
	private Log log = LogFactory.getLog(this.getClass());
	
	public CSVContentGenerator() {		
	}
	
	public void setCommand(QueryCommand cmd) {
		this.cmd = cmd;
	}

	public String getContentType() {
		return("text/csv");
	}
	
	public void transfer(OutputStream out) {
		log.debug("Debug Query path <" + cmd.getPath() + ">");
		PrintWriter text = new PrintWriter(new OutputStreamWriter(out), false);
		Iterator<Pointer> iter = cmd.getPathPointers();
		
		// Need to peek inside the iter.
		Pointer p = null;
		if (iter.hasNext()) {
			p = (Pointer)iter.next();
		}
		
		boolean first = true;
		
		String[] outputAttributes = cmd.getOutputAttributeAsArray();
		
		if (outputAttributes.length == 1 && outputAttributes[0].equals("*")) {
			// Expand all.
			if (p != null ) {
				Object v = p.getValue();
				if (v instanceof IDynamicHandler) {
					IDynamicHandler dynBean = (IDynamicHandler)v;
					outputAttributes = dynBean.getProperties();
				}
			}
		}
			
		if (outputAttributes.length == 0) {
			out(text, cmd.getPath());
		} else {
			for (int i = 0; i < outputAttributes.length; i++) { 
				String outAttr = outputAttributes[i];
				text.print(cmd.getPath() + "/" + outAttr);
				if (i < (outputAttributes.length-1)) {
					out(text, delimiter);
				}
			}
		}
		
		outln(text, "");
		if (p != null) {
			do {
				if (p == null) {
					p = iter.next();
				}
				if (outputAttributes.length == 0) {
					out(text, p.getValue().toString());
				} else {
					for (int i = 0; i < outputAttributes.length; i++) {
						String outputAttribute = outputAttributes[i];
						if (false) {
						JXPathContext context = cmd.getRelativeContext(p);
						
						Object o = context.getValue(outputAttribute);
						if (o != null) {
							if (o instanceof List) {
								if (((List)o).size() == 1) {
									o = ((List)o).get(0);
								}
							}
							out(text, o.toString());
						}
						}
						if (true) {
						Iterator<Pointer> outputAttrPointersIter = cmd.getRelativePointers(p, outputAttribute);
						while(outputAttrPointersIter.hasNext()) {
							Pointer outputPointer = outputAttrPointersIter.next();
							String value = outputPointer.getValue().toString(); 					
							out(text, value);
							if (outputAttrPointersIter.hasNext()) {
								out(text, ",");
							}
						}
						}
						if (i < (outputAttributes.length-1)) {
							out(text, delimiter);
						}
						
					}
				}
				outln(text, "");
				p = null;
			} while(iter.hasNext());
		}
		text.flush();
	}

	private void out(PrintWriter w, String text) {
		w.print(text);
		//System.out.print(text);
	}
	
	private void outln(PrintWriter w, String text) {
		w.println(text);
		//System.out.println(text);
	
	}
	

}
