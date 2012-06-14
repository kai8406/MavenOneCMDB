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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.utils.xpath.IOneCMDBContentGenerator;

public class UpdateCommand extends AbstractPathCommand {
	// Specific arguments to Update Command.
	private String inputAttributes;
	
	// TODO: use spring to do this.
	private HashMap<String, IOneCMDBContentGenerator> generatorMap = new HashMap<String, IOneCMDBContentGenerator>();
	private HashMap<String, String> attributeMap = null;
	private List<String> attributeOrder = new ArrayList<String>();

	private ICmdbTransaction tx;
	
	public UpdateCommand(IOneCmdbContext context) {
		super(context);
	}
	
	public String getInputAttributes() {
		return inputAttributes;
	}
	public void setInputAttributes(String outputAttributes) {
		this.inputAttributes = outputAttributes;
		
		// Reset settings.
		this.attributeMap = null;
	}
	
	private void parseInputAttributes() {
		if (this.attributeMap != null) {
			return;
		}
		this.attributeMap = new HashMap<String, String>();
		String list = getInputAttributes(); 
		
		if (list == null || list.equals("")) {
			return;
		}
		
		// How do we handle this in the easiest way?
		// The deleimiter is space but it can be inside a 
		// an attribute condition! Need some Tool...
		
		String attributes[] = list.split(";");
		log.debug("attributes=" + attributes.length);
		for (String a : attributes) {
			String split2[] = a.split("=", 2);
			if (split2.length != 2) {
				throw new IllegalArgumentException("Wrong fromat on inputAttributes '" + a + "' must be of format variable=value;var2=value2;..");
			}
			String attributeName = split2[0];
			String attributeValue = split2[1];
			String value = attributeMap.get(attributeName);
			if (value != null) {
				throw new IllegalArgumentException("Can't set multiple values to '" + attributeName + "'");
			}
			attributeMap.put(attributeName, attributeValue);
			attributeOrder.add(attributeName);
		}
	}
	
	protected String[] getInputAttributeNameAsArray() {
		// Parse if not already done
		parseInputAttributes();
		return(attributeOrder.toArray(new String[0]));
	}

	protected Object getValues(String outputAttribute) {
		// Parse if not already done
		parseInputAttributes();
		
		// Fetch the values strings, un parsed if expression.
		String value = attributeMap.get(outputAttribute);
		if (value == null) {
			return(null);
		}
		
		Object parsedValue = null;
			
		// Check if it's an expression
		if (value.startsWith("[")) {
			if (!value.endsWith("]")) {
				throw new IllegalArgumentException("ERROR: Value expression must be ended with a ']' ");
			}
			String path = value.substring(1);
			path = path.substring(0, path.length()-1);
				
			JXPathContext context = getXPathContext();
			Iterator<Pointer> pointers = (Iterator<Pointer>)context.iteratePointers(path);
			while(pointers.hasNext()) {
				Pointer pointer = pointers.next();
				parsedValue = pointer.getValue();
				// Migth warn here if there exists multiple.
			}
		} else {
				// No need to parse this, will be a String representaion of the value.
				parsedValue = value;
		}
			
		return(parsedValue);
		
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
	
	protected void setupTX() {
		// Setup the tx.
		ISession session = getCurrentSession();
		if (session == null) {
			throw new IllegalAccessError("Not logged in.");
		}
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		
		// The TX is stored in the session.
		this.tx = ccb.getTx(session);
		getDataContext().put("tx", this.tx);
	
	}
	
	protected void processTX() {
		ISession session = getCurrentSession();
		if (session == null) {
			throw new IllegalAccessError("Not logged in.");
		}
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		
		// Commit the tx.	
		if (this.tx == null) {
			throw new IllegalArgumentException("No tx setup!");
		}
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			throw new IllegalAccessError("Rejected:" + result.getRejectCause());
		}
		
	}
	
	/**
	 * Transfer the content to the stream.
	 * Execute the update.
	 * 
	 * @param out
	 */
	public void transfer(OutputStream out) {
		
		String[] inputAttributes = getInputAttributeNameAsArray();
		
		setupTX();
		
		Iterator<Pointer> iter = getPathPointers();
		
		// Check that we have found the root object.
		if (!iter.hasNext()) {
			throw new IllegalArgumentException("No object matching path " + getPath() + " found!");
		}
		
		while(iter.hasNext()) {
			Pointer p = (Pointer)iter.next();
			
			JXPathContext context = getRelativeContext(p);
			
			for (String outputAttribute : inputAttributes) {
				
				/*
				Iterator<Pointer> outputAttrPointersIter = context.iteratePointers(outputAttribute);
				while(outputAttrPointersIter.hasNext()) {
					Pointer vPointer = outputAttrPointersIter.next();
					Object values = getValues(outputAttribute);
					//NodePointer nP = (NodePointer)vPointer;
					//nP.getImmediateValuePointer().setValue(values);
					vPointer.setValue(values);
				}
				*/	
				Object values = getValues(outputAttribute);
				if (values == null) {
					String valueExpr = attributeMap.get(outputAttribute);
					throw new IllegalArgumentException("No value matching expr " + valueExpr + " found!");
				}
				context.setValue(outputAttribute, values);
			}
		}
		
		// Process the TX.
		processTX();
		
	}
}
