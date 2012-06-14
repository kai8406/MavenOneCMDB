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
package org.onecmdb.core.example.simple;

import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;

public class UpdateModel {

	public void createInstance() {
		ISession session = new Setup().getSession("user", "passwd");
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		IModelService modelSvc = (IModelService)session.getService(IModelService.class);
		
		// Find a template ci.
		ICi ci = modelSvc.findCi(new Path<String>("IP")); 
		
		// Create a Instance from a IP Template.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ci);
			ICiModifiable ipTemplate = rootTemplate.createOffspring();
			
			// NOTE: This method should be rename to setTemplate(boolean);
			ipTemplate.setIsBlueprint(false);
			
			// Set the Attribute IpAddress, is a simple attribute
			ipTemplate.setDerivedAttributeValue("ipAddress", 
					0, SimpleTypeFactory.STRING.parseString("192.168.1.1"));
		}
		// All modification's are asyncronic in nature.
		ITicket ticket = ccb.submitTx(tx);
		
		// Wait for completion. 
		IRfcResult result = ccb.waitForTx(ticket);
		
		// Check for result.
		if (result.isRejected()) {
			System.out.println("Request was rejected, cause " + result.getRejectCause());
			return;
		}
		// Issue: The result contains no information about the newly created ICi. 
		// This is a problem due to the transaction can 
		// contain more than one modification.		
		System.out.println("Instance added ok.");
	}
}
