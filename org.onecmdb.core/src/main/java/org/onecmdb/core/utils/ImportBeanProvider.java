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
package org.onecmdb.core.utils;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;

public class ImportBeanProvider {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private ISession session;
	private IBeanProvider provider;
	private IBeanProvider baseProvider;
	BeanScope scope = new BeanScope();
	
	private boolean validation;

	
	
	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public void setSession(ISession session) {
		this.session = session;
	}
	
	public void setProvider(IBeanProvider provider) {
		this.provider = provider;
	}
	
	public void setBaseProvider(IBeanProvider provider) {
		this.baseProvider = provider;
	}
		
	public List<IRFC> compare() {
		if (this.session == null) {
			throw new IllegalArgumentException("No session is set!");
		}
		if (this.provider == null) {
			throw new IllegalArgumentException("No provider is set!");
		}
		
		
		scope.setBeanProvider(this.provider);
		scope.setRemoteBeanProvider(getRemoteBeanProvider());
		scope.setBaseBeanProvider(this.baseProvider);
		scope.setValidation(this.validation);
		scope.process();
			
			
		List<IRFC> rfcs = scope.getRFCs();
		return(rfcs);
	}
	
	public IRfcResult processProvider() {
		List<IRFC> rfcs = compare();
		
		Set<String> unresolved = scope.getUnresolvedAliases();
		if (unresolved.size() > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("Bean Provider have unresolved items [");
			log.error("Initial providers have unresolved items");
			boolean first = true;
			for (String alias : unresolved) {
				log.error(" " + alias);
				
				if (!first) {
					buf.append(", ");
				}
				first = false;
				
				buf.append(alias);
			}
			buf.append("]");
			RfcResult result = new RfcResult();
			result.setRejectCause(buf.toString());
			result.setRejected(true);
			return(result);			
		}
	
		if (rfcs == null || rfcs.size() == 0) {
			log.info("No rfcs found...");
			RfcResult result = new RfcResult();
			result.setCiAdded(0);
			result.setCiModified(0);
			result.setCiDeleted(0);
			result.setStart(new Date());
			result.setStop(new Date());
			return(result);
		}
			
			
		// Need to send the RFC to CCB.
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		tx.setRfc(rfcs);
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			log.error("Can't setup model scope: "
					+ result.getRejectCause());
		}
		return(result);
	}

	private IBeanProvider getRemoteBeanProvider() {
		// Set up to compare with Already populated...
		IModelService modelService = (IModelService) this.session
		.getService(IModelService.class);
		OneCmdbBeanProvider oneCmdbBeanProvider = new OneCmdbBeanProvider();
		oneCmdbBeanProvider.setModelService(modelService);
		return(oneCmdbBeanProvider);
	}
}
