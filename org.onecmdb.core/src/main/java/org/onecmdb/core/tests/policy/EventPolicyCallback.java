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
package org.onecmdb.core.tests.policy;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IEventPolicyCallback;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.workers.RfcResult;

public class EventPolicyCallback implements IEventPolicyCallback {

	public static final String VALUE_NOT_ALLOWED = "ValueNotAllowed";

	public IRfcResult onPolicyEvent(IObjectScope scope, IRFC rfc, ICi ci) {
		RfcResult result = new RfcResult();
		if (rfc instanceof RFCModifyAttributeValue) {
			RFCModifyAttributeValue modValue = (RFCModifyAttributeValue)rfc;
			String newValue = modValue.getNewValue();
			if (newValue != null && newValue.equals(VALUE_NOT_ALLOWED)) {
				result.setRejectCause("POLICY-DENY: Value <" + VALUE_NOT_ALLOWED + "> s not allowed!!!");
			}
		}
		System.out.println("OnEVENT....");
		return(result);
	}
}
