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
package org.onecmdb.core.utils.xpath.commands;

import java.io.OutputStream;
import java.util.HashMap;

import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.utils.xpath.IOneCMDBContentGenerator;
import org.onecmdb.core.utils.xpath.generator.CSVContentGenerator;
import org.onecmdb.core.utils.xpath.generator.DebugContentGenerator;
import org.onecmdb.core.utils.xpath.generator.PropertyContentGenerator;
import org.onecmdb.core.utils.xpath.generator.XMLContentGenerator;

/**
 * Query Command implementation.
 *
 */
public class QueryCommand extends AbstractPathCommand {
	
	// Arguments to Query Command.
	// NOTE! The AbstractPathCommand requiers more.
	private String outputAttributes;
	private String outputFormat;
	
	// TODO: use spring to do this.
	private HashMap<String, IOneCMDBContentGenerator> generatorMap = new HashMap<String, IOneCMDBContentGenerator>();
	
	public QueryCommand(IOneCmdbContext context) {
		super(context);
		
		generatorMap.put("debug", new DebugContentGenerator());
		generatorMap.put("xml", new XMLContentGenerator());
		generatorMap.put("csv", new CSVContentGenerator());
		generatorMap.put("property", new PropertyContentGenerator());
		
	}
	
	
	public String getOutputAttributes() {
		return outputAttributes;
	}
	public void setOutputAttributes(String outputAttributes) {
		this.outputAttributes = outputAttributes;
	}
	public String getOutputFormat() {
		return outputFormat;
	}
	public void setOutputFormat(String outputFromat) {
		this.outputFormat = outputFromat;
	}


	/**
	 * How the content should ibe interperted.
	 * 
	 * For now it's always plain text.
	 * @return
	 */
	public String getContentType() {
		return("text/plain");
	}
	
	/**
	 * Transfer the content to the stream.
	 * 
	 * @param out
	 */
	public void transfer(OutputStream out) {
		// lookup the Content Generator.
		String key = getOutputFormat();
		
		IOneCMDBContentGenerator generator = null;
		generator = generatorMap.get(key);
		if (generator == null) {
			
			// Default for now is Debug Generator
			generator = new DebugContentGenerator();
		}
		
		// Set the command to my self.
		generator.setCommand(this);
		
		// Let the gtenerator do the job.
		generator.transfer(out);
	}
	
	

	public String[] getOutputAttributeAsArray() {
		String list = getOutputAttributes(); 
	
		if (list == null || list.equals("")) {
			return(new String[0]);
		}
		
		// How do we handle this in the easiest way?
		// The deleimiter is space but it can be inside a 
		// an attribute condition! Need some Tool...
		String split[] = list.split(" ");
		
		return(split);
	}


}
