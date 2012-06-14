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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.job.workflow.CronTrigger;
import org.onecmdb.core.internal.job.workflow.DirectoryChangeTrigger;
import org.onecmdb.core.internal.job.workflow.IntervallTrigger;
import org.onecmdb.core.internal.job.workflow.ManualTrigger;
import org.onecmdb.core.internal.job.workflow.Trigger;
import org.onecmdb.core.utils.ClassInjector;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

public class JobSchedulare {

	Log log = LogFactory.getLog(this.getClass());

	private Scheduler sched;
	private HashMap<String, Thread>  eventThreads = new HashMap<String, Thread>();
	
	public JobSchedulare() {
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		
		try {
			sched = schedFact.getScheduler();
			sched.start();
			log.info("Quartz Scheduler Started");
		} catch (SchedulerException e) {
			log.fatal("Can't start Quartz Schedulare - " + e.toString(), e);
		}
	}
	
	public void cancel(ICi trigger) {
			// Interuppt job.
			log.info("Interuppt job " + trigger.getAlias());
			Thread t = eventThreads.get(trigger.getAlias());
			if (t != null) {
				t.interrupt();
				eventThreads.remove(trigger.getAlias());
			} else {
				try {
					sched.interrupt(trigger.getAlias(), null);

					log.info("Delete job " + trigger.getAlias());
					sched.deleteJob(trigger.getAlias(), null);

				} catch (SchedulerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public void scheduale(ISession session, ICi trigger, ICi process) {

		if (session == null) {
			log.error("Can't scheduale trigger, need to specify session");
			return;
		}
		if (trigger == null) {
			log.error("Can't scheduale trigger, need to specify trigger");
			return;
		}
	
		Object triggerInstance = getTrigger(trigger);
		
		if (triggerInstance == null) {
			log.error("Can't scheduale, trigger not found");
			return;
		}
		
		// ??? the name of this jobDetail!
		JobDetail jobDetail = new JobDetail(trigger.getAlias(),
                 null,
                 JobRunner.class);
		
		JobDataMap map = new JobDataMap();
		List<ICi> processes = null;
		if (process != null) {
			processes = new ArrayList<ICi>();
			processes.add(process);
		}
		
		map.put("session", session);
		map.put("processes", processes);
		map.put("trigger", trigger);
		
		
		
		jobDetail.setJobDataMap(map);
		
		log.info("Schedule trigger " + trigger.getAlias());
		if (triggerInstance instanceof IEventTrigger) {
			final IEventTrigger eTrigger = (IEventTrigger)triggerInstance;
			eTrigger.setJobDetail(jobDetail);
			Thread t = new Thread(eTrigger) {

				@Override
				public void interrupt() {
					eTrigger.interrupt();
					
					super.interrupt();
				}
				
			};
			eventThreads.put(trigger.getAlias(), t);
			t.start();
			return;
		}
		
		if (triggerInstance instanceof org.quartz.Trigger)  {
			org.quartz.Trigger qTrigger = (org.quartz.Trigger)triggerInstance;
			
			// Check what this trigger is about.
			
			try {
				sched.scheduleJob(jobDetail, qTrigger);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown() {
		if (this.sched != null) {
			log.info("Shutdown Quartz Schedulare");
			try {
				this.sched.shutdown();
				log.info("Shutdown Quartz Schedulare Completed");
			} catch (SchedulerException e) {
				log.error("Error Shutdown Quartz Schedulare : " + e.toString(), e);
			}
		}
	}
	
	private Object getTrigger(ICi ci) {
		// Convert ci to object.
		ClassInjector converter = new ClassInjector();
		//CiToJavaObject converter = new CiToJavaObject();
		converter.addAliasToClass("JobManualTrigger", ManualTrigger.class.getName());
		converter.addAliasToClass("JobIntervallTrigger", IntervallTrigger.class.getName());
		converter.addAliasToClass("JobCronTrigger", CronTrigger.class.getName());
		
		
		final Trigger t = (Trigger) converter.toBeanObject(ci);
		
		if (t instanceof CronTrigger) {
			String expression = ((CronTrigger)t).getCronExpression();
			org.quartz.CronTrigger cron = new org.quartz.CronTrigger();
			try {
				cron.setCronExpression(expression);
			} catch (ParseException e) {
				log.error("Cron trigger '" + ci.getAlias() +"'s expression '" + expression + "' not correct: " + t.toString(), e);
				return(null);
			}
			cron.setName(ci.getAlias());
			return(cron);
		}
	
		if (t instanceof IntervallTrigger) {
			IntervallTrigger intervallT = (IntervallTrigger)t;
			SimpleTrigger simple = new SimpleTrigger();
			
			
			simple.setRepeatInterval(intervallT.getRepeateIntervall());
			simple.setRepeatCount((int) intervallT.getRepeateCount());
			simple.setStartTime(new Date(System.currentTimeMillis() + intervallT.getStartDelay()));
			simple.setName(ci.getAlias());	
			return(simple);
		}
		
		if (t instanceof ManualTrigger) {
			SimpleTrigger simple = new SimpleTrigger();
			simple.setRepeatCount(0);
			simple.setRepeatInterval(0L);
			simple.setStartTime(new Date());
			
			simple.setName(ci.getAlias());	
			return(simple);
		}
		if (t instanceof DirectoryChangeTrigger) {
			return(t);
		}
		return(null);
		
	}
	
}
