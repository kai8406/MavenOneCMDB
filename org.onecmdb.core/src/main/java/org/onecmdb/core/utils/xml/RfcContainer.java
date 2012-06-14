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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;

public class RfcContainer {
	private List<IRFC> newTemplates = new ArrayList<IRFC>();
	private List<IRFC> newAttributes = new ArrayList<IRFC>();
	private List<IRFC> newTemplateValues = new ArrayList<IRFC>();
	private List<IRFC> newInstances = new ArrayList<IRFC>();
	private List<IRFC> newInstanceValues = new ArrayList<IRFC>();
	private List<IRFC> destroys = new ArrayList<IRFC>();
	private List<IRFC> templateModifiers = new ArrayList<IRFC>();
	private List<IRFC> instanceModifiers = new ArrayList<IRFC>();
	

	public void addNewTemplate(IRFC rfc) {
		newTemplates.add(rfc);
	}
	
	public void addNewInstance(IRFC rfc) {
		newInstances.add(rfc);
	}
	
	public void addNewAttribute(IRFC rfc) {
		newAttributes.add(rfc);
	}
	
	/**
	 * Need to add value's setting in reverse order, else proagattion of
	 * value will not be ok!
	 * @param rfcs
	 */
	public void addNewTemplateValues(List<IRFC> rfcs) {
		for (int i = rfcs.size()-1; i >= 0; i--) {
			newTemplateValues.add(0, rfcs.get(i));
		}
	}

	public void addNewInstanceValues(List<IRFC> rfcs) {
		for (int i = rfcs.size()-1; i >= 0; i--) {
			newInstanceValues.add(0, rfcs.get(i));
		}
	}
	
	public void addDestory(RFCDestroy destoryRfc) {
		destroys.add(destoryRfc);
	}

	
	public void addTemplateModify(IRFC modify) {
		this.templateModifiers.add(modify);
	}
	 
	public void addInstanceModify(IRFC modify) {
		this.instanceModifiers.add(modify);
	}

	public List<IRFC> getOrderedRfcs() {
		ArrayList<IRFC> allRfcs = new ArrayList<IRFC>();
		
		// Delete first.
		allRfcs.addAll(destroys);
		
		allRfcs.addAll(newTemplates);
		allRfcs.addAll(newInstances);
		allRfcs.addAll(newAttributes);
		// Revert order here to handle propagtion of
		// of valus correctly
		// Add in reverted order, can be called more than once
		// so we can't use Collections.revert().
		// Collections.reverse(newInstanceValues);
		// Collections.reverse(newTemplateValues);
		allRfcs.addAll(newTemplateValues);
		allRfcs.addAll(templateModifiers);
		
		
		allRfcs.addAll(newInstanceValues);
		
		/*
		for (int i = (newInstanceValues.size()-1) ; i >= 0; i--) {
			allRfcs.add(newInstanceValues.get(i));
		}
		for (int i = (newTemplateValues.size()-1) ; i >= 0; i--) {
			allRfcs.add(newTemplateValues.get(i));
		}
		*/
		allRfcs.addAll(instanceModifiers);
		
		return(allRfcs);
	}


	
}
