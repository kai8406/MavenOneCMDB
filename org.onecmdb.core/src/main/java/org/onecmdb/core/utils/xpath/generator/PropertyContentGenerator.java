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
import org.onecmdb.core.utils.xpath.model.InstanceContext;
import org.onecmdb.core.utils.xpath.model.TemplateContext;

/**
 * Generate property type format of a query.
 *
 */
public class PropertyContentGenerator implements IOneCMDBContentGenerator {
	private QueryCommand cmd;
	private Log log = LogFactory.getLog(this.getClass());
	
	public PropertyContentGenerator() {		
	}
	
	public void setCommand(QueryCommand cmd) {
		this.cmd = cmd;
	}

	public String getContentType() {
		return("text/plain");
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
		if (p != null) {
			do {
				if (p == null) {
					p = iter.next();
				}
				
				Object rootObject = p.getValue();
				
				if (!(rootObject instanceof InstanceContext || rootObject instanceof TemplateContext)) {
					throw new IllegalArgumentException("Illegal path '" +  cmd.getPath() + "'. Must point to a ICi.");
				}
					
				if (outputAttributes.length == 0) {
					outln(text, getPropertyName(p, null) + "=" +  p.getValue().toString());
				} else {
					for (int i = 0; i < outputAttributes.length; i++) {
						String outputAttribute = outputAttributes[i];
						
						JXPathContext context = cmd.getRelativeContext(p);
						if (true) {
							Iterator iterPointer = context.iteratePointers(outputAttribute);													
							int count = 0;
							String uniqueStr = null;
							while(iterPointer.hasNext()) {
								Pointer valuePointer = (Pointer) iterPointer.next();
								Object o = valuePointer.getValue();
								if (iterPointer.hasNext() || uniqueStr != null) {
									// More than one.
									uniqueStr = "[" + count + "]";
								}
								count++;
								String property = getPropertyName(rootObject, outputAttribute);
								if (uniqueStr != null) {
									property = property + uniqueStr;
								}
								if (o instanceof List) {
									int index = 0;
									for (Object v : (List)o) {
										outln(text,  property + "[" + index + "]=" + v.toString());
										index++;
									}
								} else {
									outln(text, property + "=" + (o == null ? "" : o.toString()));
								}
					
							}
						}
					}
				}
				p = null;				
			} while(iter.hasNext());
		}
		text.flush();
	}

	private String getPropertyName(Object pathObject, String attr) {
		String pathProperty = null;
		if (pathObject instanceof TemplateContext) {
			String templateAlias = (String)((TemplateContext)pathObject).getProperty("alias");
			pathProperty = "template." + templateAlias;
		} else if (pathObject instanceof InstanceContext) {
			TemplateContext parent =  (TemplateContext)((InstanceContext)pathObject).getProperty("derivedFrom");
			String templateAlias = (String)parent.getProperty("alias");
			String instanceAlias = (String)((InstanceContext)pathObject).getProperty("alias");
			pathProperty = templateAlias + "." + instanceAlias;
		}
		
		String attrProperty = "";
		if (attr != null) {
			attrProperty = "." + attr.replace('/', '.');
		}
		return(pathProperty + attrProperty);
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
