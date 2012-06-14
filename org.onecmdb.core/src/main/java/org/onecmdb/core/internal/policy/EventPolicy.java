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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IEventPolicy;
import org.onecmdb.core.IEventPolicyCallback;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.utils.ClassInjector;
import org.onecmdb.core.utils.OneCMDBClassLoader;



public class EventPolicy extends ConfigurationItem implements  IEventPolicy {

	Log log = LogFactory.getLog(this.getClass());
	
	public EventPolicy(ICi ci) {
		super.copy((ConfigurationItem)ci);
		ClassInjector inject = new ClassInjector();
		inject.injectAttributes(this, ci);
	}

	public IRfcResult onEvent(IObjectScope scope, IRFC rfc, ICi ci) {
		log.info("FIRE-EVENT on RFC:" + rfc);
		log.info("attributePattern:" + this.attributePattern);
		log.info("onRfc:" + this.onRfc);
		log.info("callbackClass:" + this.callbackClass);
		
		IRfcResult result = new RfcResult();
		Object instance = OneCMDBClassLoader.newInstance(this.callbackClass, callbackClasspath);
		if (instance instanceof IEventPolicyCallback) {
			result = ((IEventPolicyCallback)instance).onPolicyEvent(scope, rfc, ci);
		}
		return(result);		
	}
	
	private String attributePattern;
	private String onRfc;
	private String callbackClass;
	private List<String> callbackClasspath;

	public String getAttributePattern() {
		return attributePattern;
	}

	public void setAttributePattern(String attributePattern) {
		this.attributePattern = attributePattern;
	}

	public String getCallbackClass() {
		return callbackClass;
	}

	public void setCallbackClass(String callbackClass) {
		this.callbackClass = callbackClass;
	}

	public List<String> getCallbackClasspath() {
		return callbackClasspath;
	}

	public void setCallbackClasspath(List<String> callbackClasspath) {
		this.callbackClasspath = callbackClasspath;
	}

	public String getOnRfc() {
		return onRfc;
	}

	public void setOnRfc(String onRfc) {
		this.onRfc = onRfc;
	}
}
