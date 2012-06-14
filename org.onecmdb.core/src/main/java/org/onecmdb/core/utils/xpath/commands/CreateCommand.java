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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.onecmdb.core.IOneCmdbContext;

/**
 * Create Command implementation.
 *
 */
public class CreateCommand extends UpdateCommand {
	
	public CreateCommand(IOneCmdbContext context) {
		super(context);
	}

	@Override
	public void transfer(OutputStream out) {
		this.context.put("create", true);
		
		JXPathContext xPathContext = getXPathContext();
		xPathContext.setLenient(true);
		Object o = xPathContext.getValue(getPath());
		if (o != null) {
			throw new IllegalArgumentException("Path '" + getPath() + "' exists.");
		}
		setupTX();
		
		Pointer p = xPathContext.createPath(getPath());
		
		JXPathContext relContext = getRelativeContext(p);
		
		for (String outputAttribute : getInputAttributeNameAsArray()) {
			
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
			relContext.setValue(outputAttribute, values);
		}
		
		processTX();
	}
	
	
	
	

}
