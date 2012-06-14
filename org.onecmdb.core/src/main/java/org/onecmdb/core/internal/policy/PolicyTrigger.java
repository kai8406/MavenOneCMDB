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
package org.onecmdb.core.internal.policy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributePolicy;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiPolicy;
import org.onecmdb.core.IEventPolicy;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IPolicyTrigger;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.utils.ClassInjector;

/**
 * Responsable to trigger the actuall policy implementations.
 *  
 */
public class PolicyTrigger extends ConfigurationItem implements IPolicyTrigger {
	private Log log = LogFactory.getLog(this.getClass());
	
	private ICiPolicy ciPolicy;
	private List<IAttributePolicy> attributePolicy = new ArrayList<IAttributePolicy>();
	private List<IEventPolicy> eventPolicy = new ArrayList<IEventPolicy>();
	
	public PolicyTrigger(ICi policy) {
		super.copy((ConfigurationItem)policy);
		
		ClassInjector inject = new ClassInjector();
		inject.addAliasToClass("CiPolicy", CiPolicy.class.getName());
		inject.addAliasToClass("AttributePolicy", AttributePolicy.class.getName());
		inject.addAliasToClass("EventPolicy", EventPolicy.class.getName());
				
		inject.injectAttributes(this, policy);
	}

	public IRfcResult runValidation(IObjectScope scope, IRFC rfc, ICi ci) {
		IRfcResult result = new RfcResult();
		
		log.debug("runValidationOn");	
		
		if (ci instanceof IAttribute) {
			for (IAttributePolicy aPolicy : getAttributePolicies()) {
				result = aPolicy.runValidation(scope, rfc, (IAttribute)ci);
				if (result.isRejected()) {
					return(result);
				}
			}
		} else {
			if (ciPolicy != null) {
				result = ciPolicy.runValidation(scope, rfc, ci);
				if (result.isRejected()) {
					return(result);
				}
			}
		}	
		for (IEventPolicy eventPolicy : getEventPolicies()) {
			if (eventPolicy != null) {
				result = eventPolicy.onEvent(scope, rfc, ci);
			}
		}
		return(result);
	}

	public List<IEventPolicy> getEventPolicies() {
		return(this.eventPolicy);
	}

	public ICiPolicy getCiPolicies() {
		return(this.ciPolicy);
	}
	
	public List<IAttributePolicy> getAttributePolicies() {
		return(this.attributePolicy);
	}

	public void setAttributePolicy(List<IAttributePolicy> attributePolicy) {
		this.attributePolicy = attributePolicy;
	}

	public void setCiPolicy(ICiPolicy ciPolicy) {
		this.ciPolicy = ciPolicy;
	}

	public void setEventPolicy(List<IEventPolicy> eventPolicy) {
		this.eventPolicy = eventPolicy;
	}
	
	
	
	
	
	
}
