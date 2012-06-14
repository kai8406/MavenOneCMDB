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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;
import org.onecmdb.core.utils.xml.XmlGenerator;

public class ExportModelProcess extends WorkflowProcess {
	
	
	private String exportFile;
	private boolean templates;
	private boolean instances;
	private String rootAlias;
	private volatile boolean terminate = false;
	
	public String getExportFile() {
		return exportFile;
	}
	
	public void setExportFile(String exportFile) {
		this.exportFile = exportFile;		
	}
	
	

	public boolean isInstances() {
		return instances;
	}

	public void setInstances(boolean instances) {
		this.instances = instances;
	}

	public String getRootAlias() {
		return rootAlias;
	}

	public void setRootAlias(String rootAlias) {
		this.rootAlias = rootAlias;
	}

	public boolean isTemplates() {
		return templates;
	}

	public void setTemplates(boolean templates) {
		this.templates = templates;
	}

	@Override
	public void run() throws Throwable {	
		ISession session = (ISession) getRelevantData().get("session");
		
		// get access to the CI service from onecmdb backend
		IModelService onecmdbModel = (IModelService) session.getService(IModelService.class);
		
		// the bean provider
		OneCmdbBeanProvider onecmdbProvider = new OneCmdbBeanProvider();
		onecmdbProvider.setModelService(onecmdbModel);				
		
		// Validate export file.
		String exportFile = getExportFile();
		if (exportFile == null) {
			throw new IllegalArgumentException("attribute exportFile can't be null!");
		}
		File f = new File(exportFile);
		File parent = f.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
			
		// Generate Xml file.
		XmlGenerator gen = new XmlGenerator();
		gen.setOutput(getExportFile());
		
		// Filter out which beans to use.
		ICi ci = onecmdbModel.findCi(new Path<String>(rootAlias));
		if (ci == null) {
			throw new IllegalArgumentException("No ci found with alias '" + rootAlias+ "', check attribute rootAlias!");
		}
		if (!templates && !instances) {
			throw new IllegalArgumentException("Specify if instnaces and/or templates should be exported.");
		}
			
		List<CiBean> beans = new ArrayList<CiBean>();
		populateBeans(ci, templates, instances, onecmdbProvider, beans);
		
		gen.setBeans(beans);
		
		if (terminate) {
			return;
		}
		try {
			gen.process();
		} catch (IOException e) {
			log.error("Cannot export model to XML: " + e.getMessage());
		}
		
	}

	private void populateBeans(ICi ci, boolean exportTemplates, boolean exportInstances, OneCmdbBeanProvider onecmdbProvider, List<CiBean> beans) {
		if (terminate) {
			return;
		}
		if (ci.isBlueprint() && exportTemplates) {
			CiBean bean = onecmdbProvider.convertCiToBean(ci);
			beans.add(bean);
		}
		
		if (!ci.isBlueprint() && exportInstances) {
			CiBean bean = onecmdbProvider.convertCiToBean(ci);
			beans.add(bean);
		}
		for (ICi offspringCi : ci.getOffsprings()) {
			populateBeans(offspringCi, exportTemplates, exportInstances, onecmdbProvider, beans);
		}
	}

	@Override
	public void interrupt() {
		this.terminate  = true;
	}

}
