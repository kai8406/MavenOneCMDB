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

import java.util.List;

import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.job.workflow.WorkflowParameter;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.XmlParser;

public class UrlImportProcess extends WorkflowProcess {
	private List<String> importUrls;
	
	public void setImportUrl(List<String> urls) {
		this.importUrls = urls;
	}
	
	@Override
	public void run() throws Throwable {
		XmlParser xmlProvider = new XmlParser();
		xmlProvider.setURLs(importUrls);
		
		// Process beans
		ProcessBeanProvider importBeans = new ProcessBeanProvider();
		WorkflowParameter par = new WorkflowParameter();
		par.put("provider", xmlProvider);
		par.put("validation", "false");
		importBeans.setInParameter(par);
		importBeans.setRelevantData(data);
		importBeans.run();
		
		
		// Commit
		BeanScope scope = (BeanScope) importBeans.getOutParameter().get("scope");
		List<IRFC> rfcs = scope.getRFCs();
		
		
		CommitRfcProcess commit = new CommitRfcProcess();
		WorkflowParameter par1 = new WorkflowParameter();
		System.out.println(rfcs.size() + " Rfc's generated ");
		par1.put("rfcs", rfcs);
		commit.setInParameter(par1);
		commit.setRelevantData(data);
		commit.run();
		String ok = (String)commit.getOutParameter().get("ok");
		String cause = (String)commit.getOutParameter().get("cause");
		if (ok.equals("false")) {
			throw new IllegalArgumentException("Error Importing, cause " + cause);
		}
		
	}

	@Override
	public void interrupt() {
		
	}
	

}
