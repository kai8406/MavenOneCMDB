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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.dynamic.DynamicPointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.xpath.IOneCMDBContentGenerator;
import org.onecmdb.core.utils.xpath.commands.QueryCommand;

/**
 * Debug content generator. Will show the real XPath result.
 *
 */
public class DebugContentGenerator implements IOneCMDBContentGenerator {
	private QueryCommand cmd;
	private Log log = LogFactory.getLog(this.getClass());
	
	public DebugContentGenerator() {		
	}
	
	public void setCommand(QueryCommand cmd) {
		this.cmd = cmd;
	}

	public String getContentType() {
		return("text/plain");
	}

	public void transfer(OutputStream out) {
		log.debug("Debug Query path <" + cmd.getPath() + ">");
		PrintWriter text = new PrintWriter(new OutputStreamWriter(out), true);
		
		Double count = (Double) cmd.getXPathContext().getValue("count(" + cmd.getPath() + ")");
		Iterator<Pointer> iter = cmd.getPathPointers();
		out(text, cmd.getPath() + "[" + count +"]");
		boolean first = true;
		while(iter.hasNext()) {
			Pointer p = (Pointer)iter.next();
			
			log.debug(p.asPath());
			
			out(text, "\t" + p.asPath() + "=" + p.getNode());
			String[] outputAttributes = cmd.getOutputAttributeAsArray();
			for (String outputAttribute : outputAttributes) {
				Iterator<Pointer> outputAttrPointersIter = cmd.getRelativePointers(p, outputAttribute);
				while(outputAttrPointersIter.hasNext()) {
					Pointer outputPointer = outputAttrPointersIter.next();
					// Try to create some path.
					out(text, "\t\t" + outputPointer.asPath() + "=" + outputPointer.getNode());
				}
			}
		}
	}
	
	private void out(PrintWriter w, String text) {
		w.println(text);
		log.debug(text);
	}
}
