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
package org.onecmdb.core.internal.job.workflow;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;

public abstract class WorkflowProcess {
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected WorkflowParameter in;
	protected WorkflowRelevantData data;
	protected WorkflowParameter out = new WorkflowParameter();

	
	public void updateAttribute(String alias, String value) {
		ISession session = (ISession)data.get("session");
		if (session == null) {
			log.error("No 'session' set in relevant data");
			return;
		}
		ICi process = (ICi)in.get("process");
		if (process == null) {
			log.error("No 'process' set in relevant data");
			return;
		}
		List<IAttribute> attributes = process.getAttributesWithAlias(alias);
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		for (IAttribute a: attributes) {
			IAttributeModifiable am = tx.getAttributeTemplate(a);
			am.setValue(SimpleTypeFactory.STRING.parseString(value));
		}
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			log.error("Update attribute '" + alias + "' in process '" + process.getAlias() + "' was rejected: " + result.getRejectCause());
		}
	}
	
	public void updateProgressPercentage(int value) {
		String alias = "progressPercentage";
		ISession session = (ISession)data.get("session");
		if (session == null) {
			log.error("No 'session' set in relevant data");
			return;
		}
		ICi process = (ICi)in.get("process");
		if (process == null) {
			log.error("No 'process' set in relevant data");
			return;
		}
		List<IAttribute> attributes = process.getAttributesWithAlias("progressPercentage");
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		for (IAttribute a: attributes) {
			IAttributeModifiable am = tx.getAttributeTemplate(a);
			am.setValue(SimpleTypeFactory.INTEGER.parseString("" + value));
		}
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			log.error("Update attribute '" + alias + "' in process '" + process.getAlias() + "' was rejected: " + result.getRejectCause());
		}
	}

	public void setInParameter(WorkflowParameter param) {
		this.in = param;
	}

	public void setRelevantData(WorkflowRelevantData data) {
		this.data = data;
	}

	public WorkflowRelevantData getRelevantData() {
		return (this.data);
	}

	public WorkflowParameter getOutParameter() {
		return(this.out);
	}
	
	public void setState(String state) {
		log.info(this.toString() + "setState(" + state + ")");
	}
	
	public void setStatus(String state) {
		log.info(this.toString() + "setStatus(" + state + ")");
	}
	
	public void setJavaClass(String clazz) {
		log.info(this.toString() + "setJavaClass(" + clazz +")");
	}
	
	/**
	 * Actually run the process.
	 * 
	 * TODO: Return a JobStatus.
	 * 
	 * @throws Throwable
	 */
	public abstract void run() throws Throwable;

	public abstract void interrupt();
}
