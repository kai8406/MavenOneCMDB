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

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.job.workflow.WorkflowParameter;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.XmlParser;

public class DirectoryImportProcess extends WorkflowProcess {
	private List<String> importPaths;
	private Log log = LogFactory.getLog(this.getClass());
	private volatile boolean terminate = false;
	
	public void setImportPath(List<String> paths) {
		this.importPaths = paths;
	}
	
	@Override
	public void run() throws Throwable {
		this.terminate = false;
		for (String path : importPaths) {
			
			File pathFile = new File(path);
			if (!pathFile.isDirectory()) {
				log.warn("Path '" + path + "' is not a directory, will skip.");
				continue;
			}
			log.info("Process directory '" + path + "'");
			File files[] = pathFile.listFiles();
			for (File f : files) {
				if (terminate) {
					throw new InterruptedException("Iport operation interrupted"); 
				}
				log.info("Process file '" + f.getName() +"' in directory '" + path + "'");
				XmlParser xmlProvider = new XmlParser();
				xmlProvider.setURL(f.toURL().toExternalForm());
				
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
			}
		}
		
	}

	@Override
	public void interrupt() {
		this.terminate = true;
	}
	

}
