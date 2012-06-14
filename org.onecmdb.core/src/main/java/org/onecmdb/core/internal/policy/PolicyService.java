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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributePolicy;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPolicyService;
import org.onecmdb.core.IPolicyTrigger;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.SchemaService;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.tests.profiler.Profiler;

public class PolicyService extends SchemaService implements IPolicyService {

	private String rootPolicyAlias;
	private String rootPolicyTriggerAlias = "PolicyTrigger";
	private String rootCiPolicyAlias = "CiPolicy";
	private String rootAttributePolicyAlias = "AttributePolicy";
	private String rootEventPolicyAlias = "EventPolicy";

	private IModelService modelService;


	private HashMap<ICi, IPolicyTrigger> policyMap = new HashMap<ICi, IPolicyTrigger>();
	private PolicyTrigger defaultPolicyTrigger;
	
	private Log log;
	
	public void setModelService(IModelService service) {
		this.modelService = service;
	}

	public void setRootAlias(String rootAlias) {
		this.rootPolicyAlias = rootAlias;
	}
	
	
	/**
	 * How should we retrive the poilyc for a Ci fast?
	 * The ci can be an instance/template (ci or attribute)
	 * Say that we have the policy connected to the template of the ci,
	 * then for an modification of a value, it need to
	 * 1) Fetch owner
	 * 2) Fetch derivedFrom
	 * 3) Query for policy references targeting this ci.
	 * 
	 * ---> Expensive, this has to be performed on every rfc sent in.... 
	 * 
	 */
	public IPolicyTrigger getPolicy(ICi ci) {
		Profiler.start("getPolicy(" + (ci == null ? "null" : ci.getId()) + ")");
		try {
		if (ci == null) {
			return(getDefaultPolicyTrigger());
		}
		if (policyMap.isEmpty()) {
			return(getDefaultPolicyTrigger());
		}
		
		
		ICi localCi = ci;
		if (ci instanceof IAttribute) {
			localCi = ((IAttribute)ci).getOwner();
			if (localCi == null) {
				return(getDefaultPolicyTrigger());
			}
		}
		
		ICi templateCi = localCi;
		
		if (!localCi.isBlueprint()) {
			templateCi = localCi.getDerivedFrom();
		}
		
		IPolicyTrigger policy = null;
		if (templateCi != null) {
			// Look in map...
			policy = policyMap.get(templateCi);
		}
		
		if (policy == null) {
			policy = getDefaultPolicyTrigger();
		}
		return (policy);
		} finally {
			Profiler.stop();
		}
	}

	private IPolicyTrigger getDefaultPolicyTrigger() {
		return(this.defaultPolicyTrigger);
	}

	private void updateDefaultPolicyTrigger() {
		this.defaultPolicyTrigger = new PolicyTrigger(getRootPolicyTrigger());

		CiPolicy ciPolicy = new CiPolicy(getRootCiPolicy());		
		this.defaultPolicyTrigger.setCiPolicy(ciPolicy);
	
		AttributePolicy aPolicy = new AttributePolicy(getRootAttributePolicy());
		List<IAttributePolicy> aPolecies = new ArrayList<IAttributePolicy>();
		aPolecies.add(aPolicy);
		this.defaultPolicyTrigger.setAttributePolicy(aPolecies);
	}
	
	public void init() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		
		setupSchema();
		
		
		// Validate...
		if (getRootPolicy() == null) {
			log.fatal("No root Policy found");
			throw new IllegalArgumentException("No Root Policy found");
		}
		if (getRootPolicyTrigger() == null) {
			log.fatal("No root Policy Trigger found");
			throw new IllegalArgumentException("No Root Policy found");
		}
		
		if (getRootCiPolicy() == null) {
			log.fatal("No root Ci Policy found");
			throw new IllegalArgumentException("No Root Ci Policy found");
		}
		
		if (getRootAttributePolicy() == null) {
			log.fatal("No root Attribute Policy found");
			throw new IllegalArgumentException("No root attribute policy found");
		}
		if (getRootEventPolicy() == null) {
			log.fatal("No root event policy found");
			throw new IllegalArgumentException("No root event policy found");
		}
		
		updatePolicyTriggers();
	}

	
	public void updatePolicyTriggers() {
		updateDefaultPolicyTrigger();
		
		updatePolicyTrigger(getRootPolicyTrigger());
	}
	
	public void updatePolicyTrigger(ICi policy) {
		if (policy == null) {
			return;
		}
		
		if (!modelService.isOffspringOf(getRootPolicyTrigger(), policy)) {
			return;
		}
		
		if (policy.isBlueprint()) {
			for (ICi offspringPolicy : policy.getOffsprings()) {
				updatePolicyTrigger(offspringPolicy);
			}
		} else {
			List<IAttribute> targets = policy.getAttributesWithAlias(POLICY_FOR_ATT);
			for (IAttribute target : targets) {
				IValue value = target.getValue();
				if (value instanceof ICi) {
					policyMap.put((ICi)value, new PolicyTrigger(policy));
				}
			}
		}
	}
	
	public void close() {
		// TODO Auto-generated method stub
	}

	
	/**
	 * Root getters.
	 */
	
	public ICi getRootPolicy() {
		Path<String> path = new Path<String>(this.rootPolicyAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}
	public ICi getRootPolicyTrigger() {
		Path<String> path = new Path<String>(this.rootPolicyTriggerAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}
	public ICi getRootCiPolicy() {
		Path<String> path = new Path<String>(this.rootCiPolicyAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}
	public ICi getRootAttributePolicy() {
		Path<String> path = new Path<String>(this.rootAttributePolicyAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}
	public ICi getRootEventPolicy() {
		Path<String> path = new Path<String>(this.rootEventPolicyAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}

}
