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
package org.onecmdb.core.internal.ccb.workers;

import org.onecmdb.core.IAuthorizationService;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IPolicyService;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.IRfcWorker;
import org.onecmdb.core.internal.ccb.rfc.RFCNewRootCi;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.storage.IDaoReader;

public class SimpleCreateRootRfcWorker implements IRfcWorker {

	private IDaoReader reader;
	
	
	public void setPolicyService(IPolicyService service) {
		// TODO:
	}

	public void setDaoReader(IDaoReader reader) {
		this.reader = reader;
	}

	public boolean handleRfc(IRFC rfc) {
		if (rfc instanceof RFCNewRootCi) {
			return (true);
		}
		return (false);
	}

	public IRfcResult perform(IRFC rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		try {
			if (rfc instanceof RFCNewRootCi) {
				return (performCreateRootCi((RFCNewRootCi) rfc, scope));
			}
			result.setRejectCause("TODO:");
			return (result);
		} finally {	
			
		}
	}

	private IRfcResult performCreateRootCi(RFCNewRootCi rfc, IObjectScope scope) {
		// Will not use any for now policy...
		RfcResult result = new RfcResult();
		
		IAuthorizationService auth = (IAuthorizationService) scope.getSession().getService(IAuthorizationService.class);
		Long gid = null;
		if (auth != null) {
			ICi group = auth.getGroup(rfc.getGroup());
			if (group != null) {
				gid = group.getId().asLong();
			}
		}
		
		// Create the root ConfigurationItem.
		ConfigurationItem root = new ConfigurationItem();
		root.setTemplatePath("/" + root.getId().asLong());
		root.setDaoReader(reader);
		root.setGid(gid);
		
		scope.addNewICi(root);

		rfc.setTarget(root);
		// scope.mapRfcToCi(rfc, root);

		return (result);
	}

}
