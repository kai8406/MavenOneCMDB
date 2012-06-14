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
package org.onecmdb.core.internal.job.workflow.sample;

import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;

public class ProcessBeanProvider extends WorkflowProcess {
	boolean validation = true;
	
	public void setValidation(String value) {
		if (value == null) {
			return;
		}
		this.validation = Boolean.parseBoolean(value);
	}
	
	public void run() {
		IBeanProvider localProvider = (IBeanProvider) in.get("provider");
		if (localProvider == null) {
			out.put("cause", "No 'provider' found in input");
			out.put("ok", "false");
			return;
		}
		
		ISession session = (ISession) data.get("session");
		
		if (session == null) {
			out.put("cause", "No 'session' found in relevant data");
			out.put("ok", "false");
			return;
		}
		
		setValidation((String)in.get("validation"));
		
		BeanScope scope = new BeanScope();
		
		scope.setValidation(this.validation);
		
		scope.setBeanProvider(localProvider);

		// Set remote Bean also..
		IModelService modelService = (IModelService) session
				.getService(IModelService.class);
		OneCmdbBeanProvider remoteBeanProvider = new OneCmdbBeanProvider();
		remoteBeanProvider.setModelService(modelService);

		scope.setRemoteBeanProvider(remoteBeanProvider);
		
		scope.process();
		
		out.put("scope", scope);
		out.put("rfcs", scope.getRFCs());
		out.put("ok", "true");
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

}
