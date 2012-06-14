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
package org.onecmdb.core.tests.jobs;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.IJobService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestJobScheduling extends TestCase {
	private ISession session;


	private ICi rootCronTrigger;
	private ICi rootRfcTrigger;
	private ICi rootManualTrigger;
	private ICi rootIntervallTrigger;
	
	private ICi rootProcess;


	private IJobService jobsvc;


	private ICi ciRoot;
	

	public void setUp() {
		// Resource res = new
		// ClassPathResource("org/onecmdb/core/example/application.xml");
		//		 Resource res = new
		// ClassPathResource("org/onecmdb/core/example/application.xml");
		String[] resources = {
				"core-onecmdb.xml", 
				"org/onecmdb/core/tests/hsql-inproc-datasource.xml",
				"org/onecmdb/core/tests/jobs/test-provider.xml"				
		};
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				resources);
		

		final IOneCmdbContext cmdb = (IOneCmdbContext)appContext
				.getBean("onecmdb");

		session = cmdb.createSession();
		IModelService modelsvc = (IModelService) session
				.getService(IModelService.class);

		// well known name is ``root''
		ICi root = modelsvc.getRoot();
		assertNotNull(root);
		
		ICiService cisvc = (ICiService) session.getService(ICiService.class);
		// well known name is ``CI''
		ciRoot = cisvc.getRootCi();
		assertNotNull(ciRoot);

		jobsvc = (IJobService) session.getService(IJobService.class);
		assertNotNull(jobsvc);
		
		// well known name is ``CI''
		rootCronTrigger = jobsvc.getRootCronTrigger();
		assertNotNull(rootCronTrigger);
		
		rootManualTrigger = jobsvc.getRootManualTrigger();
		assertNotNull(rootManualTrigger);
		
		rootIntervallTrigger = jobsvc.getRootIntervallTrigger();
		assertNotNull(rootIntervallTrigger);
			
		rootProcess = jobsvc.getRootProcess();
		assertNotNull(rootProcess);
	
		
	}
	
	public void testManaualTrigger() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		ICi ci = utils.createInstance(ciRoot, "CiWithManualTrigger");
		IAttribute a1 = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1 ,1);
		
		ICi trigger = utils.createInstance(rootManualTrigger, "ManualTriggerInstance");
		
		ICi process = utils.createInstance(rootProcess, "Test1");
		utils.setValue(process, "javaClass", SimpleTypeFactory.STRING.parseString(ManualProcessTest.class.getName()), false);
		ManualProcessTest.tester = this;
		
		// add process to trigger.
		utils.setValue(trigger, "process", (IValue)process, false);
		
		manualCalled = 0;
		
		System.out.println("Process:" + process);
		// Add trigger to schedulare.
		jobsvc.reschedualeTrigger(session, trigger);
		System.out.println("Process:" + process);
			
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Process:" + process);
		
		Assert.assertEquals(1, manualCalled);
		
		manualCalled = 0;
		manualCanceled = 0;
		// What should happend here, jobs should be interuppted.
		for (int i = 0; i < 10; i++) {
			// Add trigger to schedulare.
			jobsvc.reschedualeTrigger(session, trigger);
			// To give time for job to start and be canceled.
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Canceled " + manualCanceled);
		System.out.println("Called " + manualCalled);
		Assert.assertTrue(manualCanceled > 0);
		
		
	}
	
	private int manualCalled = 0;
	private int manualCanceled = 0;
	
	public void manualCanceled() {
		synchronized(this) {
			manualCanceled++;
			this.notifyAll();
		}
	}

	public void manualCalled() {
		synchronized(this) {
			manualCalled++;
			this.notifyAll();
		}
	}

	
	public void testCronSchedulare() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		ICi trigger = utils.createInstance(rootCronTrigger, null);
		utils.setValue(trigger, "cronExpression", SimpleTypeFactory.STRING.parseString("*/10 * * * * ?"), false);
			
		ICi process = utils.createInstance(rootProcess, "Test1");
		utils.setValue(process, "javaClass", SimpleTypeFactory.STRING.parseString(CronProcessTest.class.getName()), false);
		
		// add process to trigger.
		utils.setValue(trigger, "process", (IValue)process, false);
		
		CronProcessTest.sem = this;
		
		jobsvc.reschedualeTrigger(session, trigger);
		for (int i = 0; i < 5; i++) {
			long start = System.currentTimeMillis();
			synchronized (this) {
				try {
					this.wait(20000);
				} catch (InterruptedException e) {
					Assert.fail("Din't wake up in time....");
				}
			}
			long stop = System.currentTimeMillis();
			long dt = stop-start;
			System.out.println("Woke up after " + dt + "ms");
			Assert.assertTrue(dt < 15000);			
		}
		
	}

	public void testCronCancel() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		ICi trigger = utils.createInstance(rootCronTrigger, null);
		utils.setValue(trigger, "cronExpression", SimpleTypeFactory.STRING.parseString("*/10 * * * * ?"), false);
			
		ICi process = utils.createInstance(rootProcess, "Test1");
		utils.setValue(process, "javaClass", SimpleTypeFactory.STRING.parseString(CronProcessTest.class.getName()), false);
		
		// add process to trigger.
		utils.setValue(trigger, "process", (IValue)process, false);
		
		CronProcessTest.sem = this;		
			
		jobsvc.reschedualeTrigger(session, trigger);
		for (int i = 0; i < 5; i++) {
			long start = System.currentTimeMillis();
			synchronized (this) {
				try {
					this.wait(20000);
				} catch (InterruptedException e) {
					Assert.fail("Din't wake up in time....");
				}
			}
			long stop = System.currentTimeMillis();
			long dt = stop-start;
			System.out.println("Woke up after " + dt + "ms");
			Assert.assertTrue(dt < 15000);			
		}
		
	}

	public void manualTerminate() {
		
		
	}
	
	
	
}
