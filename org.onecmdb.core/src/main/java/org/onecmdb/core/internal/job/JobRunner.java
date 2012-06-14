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
package org.onecmdb.core.internal.job;

import java.util.ArrayList;
import java.util.Date;
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
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.job.workflow.WorkflowParameter;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.internal.job.workflow.WorkflowRelevantData;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.utils.ClassInjector;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;

public class JobRunner implements StatefulJob, InterruptableJob {

	private static final String ATTR_STATE = "state";
	private static final String ATTR_STATUS = "status";

	private static final String STATE_RUNNING = "RUNNING";
	private static final String STATE_IDLE = "IDLE";
	
	Log log = LogFactory.getLog(this.getClass());
	private List<ICi> processes;
	private ISession session;
	private ICi trigger;
	WorkflowParameter in = new WorkflowParameter();
	
	private List<WorkflowProcess> processInstances = new ArrayList<WorkflowProcess>();
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Execute : " + arg0);
		try {
			JobDataMap map = arg0.getJobDetail().getJobDataMap();

			processes = (List<ICi>) map.get("processes");
			session = (ISession) map.get("session");
			trigger = (ICi) map.get("trigger");

			if (processes == null) {
				processes = new ArrayList<ICi>();
				List<IAttribute> attributes = trigger.getAttributesWithAlias("process");
				for (IAttribute a : attributes) {
					IValue value = a.getValue();
					if (value instanceof ICi) {
						processes.add((ICi)value);
					}
				}
			}
			start();
		} catch (Throwable t) {
			// Catch this else the job is not schedualed again.
			log.error("Job " + arg0.toString() + " throw exception: ", t);
		}
	}
	
	public void setSession(ISession session) {
		this.session = session;
	}
	
	public void setProcess(List<ICi> processes) {
		this.processes = processes;
	}
	
	public void setTrigger(ICi trigger) {
		this.trigger = trigger;
	}
	
	public void setInParameter(WorkflowParameter parameter) {
		in.putAll(parameter);
	}
	
	public void start() {
		handleRunAs();
		
		if (this.processes == null || this.session == null) {
			log.error("Need session and ci to run job.");
			return;
		}
		
		if (trigger != null) {
			updateAttribute(trigger, "start", new Date().toString());
		}
		try {
			// For now we onlye support sequential executions of many processes.
			for (ICi process : processes) {
				try {
					updateAttribute(process, "start", new Date().toString());
					updateAttribute(process, ATTR_STATE, STATE_RUNNING);
					updateAttribute(process, ATTR_STATUS, "Started at " + new Date());
					
					// Convert Ci to a process.
					ClassInjector converter = new ClassInjector();
					
					List<IAttribute> javaClasses = process.getAttributesWithAlias("javaClass");
					if (javaClasses.size() != 1) {
						updateAttribute(process, ATTR_STATUS, "FAILED: Need to specify attribute with alias 'javaClass' in process " + process.getAlias());
						return;
					}
					IAttribute classAttribute = javaClasses.get(0);
					IValue javaClassValue = classAttribute.getValue();
					if (javaClassValue == null) {
						updateAttribute(process, ATTR_STATUS, "FAILED: Need to specify attribute with alias 'javaClass' in process " + process.getAlias());
						return;
					}
					String javaClass = javaClassValue.getAsString();
					
					converter.addAliasToClass(process.getAlias(), javaClass);
					
					final WorkflowProcess p = (WorkflowProcess) converter.toBeanObject(process);
					
					if (p == null) {
						updateAttribute(process, ATTR_STATUS, "FAILED: Can't instaciate WorkflowProcess class " + javaClass);
						return;
					}
					processInstances.add(p);
					// Input paramters.				
					in.put("process", process);		
					p.setInParameter(in);
					
					// Set Relevant Data
					WorkflowRelevantData data = new WorkflowRelevantData();
					data.put("session", session);
					p.setRelevantData(data);
					p.run();
					updateAttribute(process, ATTR_STATUS, "Completed at " + new Date());
				} catch (Throwable t) {
					log.error("Process '" + process.getAlias() + "' exception:" + t, t);
					updateAttribute(process, ATTR_STATUS, "FAILED: Exception encountered " + t.toString());
				} finally {
					updateAttribute(process, ATTR_STATE, STATE_IDLE);
					updateAttribute(process, "stop", new Date().toString());
					
					processInstances.clear();
				}
			}
		} finally {
			
			if (trigger != null) {
				updateAttribute(trigger, "stop", new Date().toString());
			}
			
		}
	}
	
	/**
	 * Setup session, to run as.
	 * If no user/password given then use initial session.
	 */
	private void handleRunAs() {
		String user = getValueAsString(trigger, "user");
		String password = getValueAsString(trigger, "password");
		if (user != null && password != null) {
			// try to login.
			this.session = this.session.newSession();
			this.session.getAuthentication().setPassword(password);
			this.session.getAuthentication().setUsername(user);
		}
		this.session.login();
	}

	private String getValueAsString(ICi ci, String alias) {
		List<IAttribute> attrs = ci.getAttributesWithAlias(alias);
		if (attrs.size() == 0) {
			return(null);
		}
		if (attrs.size() > 1) {
			return(null);
		}
		IAttribute a = attrs.get(0);
		IValue value = a.getValue();
		if (value == null) {
			return(null);
		}
		Object object = value.getAsJavaObject();
		if (object == null) {
			return(null);
		}
		return(object.toString());
	}
	private void updateAttribute(ICi process, String alias, String value) {
		log.info("UPDATE " + alias + "=" + value);
		List<IAttribute> attributes = process.getAttributesWithAlias(alias);
		if (attributes.size() == 0) {
			log.warn("Attribute alias '" + alias + "' don't exists in process '" + process.getAlias() +"'");
			return;
		}
		
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

	public void interrupt() throws UnableToInterruptJobException {
		// Interuppt all jobs.
		for (WorkflowProcess process : processInstances) {
			process.interrupt();
		}
	}

}
