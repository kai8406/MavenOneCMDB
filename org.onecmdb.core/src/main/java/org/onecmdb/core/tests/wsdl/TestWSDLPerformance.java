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
package org.onecmdb.core.tests.wsdl;

import java.awt.BorderLayout;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import junit.framework.Assert;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.Version;
import org.onecmdb.core.example.MaxMinAvg;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.tests.OneCMDBTestSuiteAllDB;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

	
public class TestWSDLPerformance extends AbstractOneCmdbTestCase {
	
	
	
	public class ProducerControl {
		private String name;
		private String derivedAlias;
		private String token;
		private Report report;
		private int totalCreateSize;
		private int batchCreateSize;
		private IOneCMDBWebService cmdbService;
		
		public int getBatchCreateSize() {
			return batchCreateSize;
		}
		public void setBatchCreateSize(int batchCreateSize) {
			this.batchCreateSize = batchCreateSize;
		}
		public IOneCMDBWebService getCmdbService() {
			return cmdbService;
		}
		public void setCmdbService(IOneCMDBWebService cmdbService) {
			this.cmdbService = cmdbService;
		}
		public String getDerivedAlias() {
			return derivedAlias;
		}
		public void setDerivedAlias(String derivedAlias) {
			this.derivedAlias = derivedAlias;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Report getReport() {
			return report;
		}
		public void setReport(Report report) {
			this.report = report;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public int getTotalCreateSize() {
			return totalCreateSize;
		}
		public void setTotalCreateSize(int totalCreateSize) {
			this.totalCreateSize = totalCreateSize;
		}
	}
	
	public class Report {
		private MaxMinAvg created = new MaxMinAvg();
		private MaxMinAvg failed = new MaxMinAvg();
		private int totalFailed = 0;
		private int totalCreated = 0;
		private long start;
		private String title;
		private int totalCount;
		
		public Report() {
			this.start = System.currentTimeMillis();
		}
		public void addPost(IRfcResult result, int count, long dt) {
			if (result.isRejected()) {
				synchronized(failed) {
					failed.addValue(dt);
					totalFailed += count;
				}
			} else {
				synchronized(created) {
					created.addValue((double)dt/(double)count);
					totalCreated += count;
				}
			}
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getResult() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("===========================================================");
			buffer.append("\n");
			buffer.append("Title: " + this.title);
			buffer.append("\n");
			buffer.append("Core Version: " + Version.VERSION_STRING);
			buffer.append("\n");
			buffer.append("Date: " + new Date());
			buffer.append("\n");
			buffer.append("Total time:" + (System.currentTimeMillis() - this.start) + "ms");
			buffer.append("\n");
			buffer.append("Added: " + totalCreated + " in " + created.getAvg() + "[ms/ci] min=" + created.getMin() + "[ms/ci]  max=" + created.getMax() + "[ms/ci]");
			buffer.append("\n");
			buffer.append("Failed: " + totalFailed + " in " + failed.getAvg() + "ms/ci total=" + failed.getTotal() + "ms");
			buffer.append("\n");
			buffer.append("===========================================================");
			buffer.append("\n");
			return(buffer.toString());
		}
		
		public String progress() {
			return(totalCreated + "[" + totalCount + "] - " + created.getAvg() + "ms/ci");
		}
		public void setTotalCount(int i) {
			this.totalCount = i;
		}
	}
	
	public class MyThreadGroup {

		
		private List<Thread> threads = new ArrayList<Thread>();

		public void addThread(Thread t) {
			threads.add(t);
		}
		/**
		 * Start all threads.
		 *
		 */
		public void start() {
			for (Thread t: threads) {
				t.start();
			}
		}
		
		/**
		 * Join all threads.
		 */
		public void join() {
			for (Thread t: threads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public class Producer implements Runnable {
		private ProducerControl ctrl;

		public Producer(ProducerControl ctrl) {
			this.ctrl = ctrl;
		}
		
		public void run() {
			
			for (int i = 0; i < ctrl.getTotalCreateSize(); i++) {
				List<CiBean> beans = new ArrayList<CiBean>();
				for (int j = 0; j < ctrl.getBatchCreateSize(); j++) {
					CiBean bean = new CiBean();
					bean.setAlias(ctrl.getName() + "-" + i + "." + j);
					bean.setDerivedFrom(ctrl.getDerivedAlias());
					beans.add(bean);
				}
				long start = System.currentTimeMillis();
				IRfcResult result = ctrl.getCmdbService().update(ctrl.getToken(), beans.toArray(new CiBean[0]), null);
				long stop = System.currentTimeMillis();
				ctrl.getReport().addPost(result, beans.size(), (stop-start));
			}
		}
	}
	
	public class Consumer implements Runnable {
		public void run() {
			
		}
	}


	private static final int CONCURRENT_THREADS = 10;
	private static final int CREATE_COUNT = 50;
	private static final int BATCH_CREATE_COUNT = 10;
	
	private IOneCMDBWebService cmdbService;
	private String token;
	private boolean isRunning = true;
	private Report report;
	
	private boolean useRemote = false;
	private String remoteURL = null;
	
	public TestWSDLPerformance() {
		super();
	}
	
	public TestWSDLPerformance(OneCMDBTestConfig config) {
		super(config);
	}
	

	public void setRemoteURL(String url) {
		if (url == null) {
			useRemote = false;
			remoteURL = url;
		} else {
			useRemote = true;
			remoteURL = url;
		}
	}
	
	public void setUp() {
		if (useRemote) {
			Service serviceModel = new ObjectServiceFactory().create(IOneCMDBWebService.class);
			
			try {
				cmdbService = (IOneCMDBWebService)(new XFireProxyFactory().create(serviceModel, remoteURL));
			} catch (MalformedURLException e) {				
				e.printStackTrace();
				fail("Can't connect to remote WebService ");
			}
		} else {
			super.setUp();

			// Create IWebService interface.
			// Directly without going through the XFire!
			OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
			impl.setOneCmdb(getCmdbContext());
			cmdbService = impl;
		}
		// Authenticate.
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login: " + e);
		}
		this.report = new Report();
		this.report.setTitle(getConfig().getPerformanceReportTitle());
	}
	
	public void testWriteConcurrency() {
		String templateAlias = "ConcurrentTest";
		
		// Create typical base CI.
		CiBean template = new CiBean();
		template.setDerivedFrom("Ci");
		template.setAlias(templateAlias);
		template.setTemplate(true);
		
		// Add 10 simple attributes.
		for (int i = 0; i < 10; i++) {
			AttributeBean a = new AttributeBean();
			a.setAlias("a" + i);
			a.setType("xs:string");
			template.addAttribute(a);
		}
		IRfcResult result = cmdbService.update(token, new CiBean[] {template}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		// Start a progressmeter.
		new Thread(new ProgressReporter(this)).start();
		
		// Create 10 Writers, will create 100 each 10 at a time.
		MyThreadGroup tg = new MyThreadGroup();
		report.setTotalCount(CONCURRENT_THREADS * BATCH_CREATE_COUNT * CREATE_COUNT);
		for (int i = 0; i < CONCURRENT_THREADS; i++) {
			ProducerControl pCtrl = new ProducerControl();
			pCtrl.setCmdbService(cmdbService);
			pCtrl.setToken(token);
			pCtrl.setBatchCreateSize(BATCH_CREATE_COUNT);
			pCtrl.setTotalCreateSize(CREATE_COUNT);
			pCtrl.setReport(report);
			pCtrl.setDerivedAlias(templateAlias);
			pCtrl.setName("Instance-Test-" + i);
			tg.addThread(new Thread(new Producer(pCtrl)));
		}
		
		tg.start();
		tg.join();
		
		setFinished();
		
		PrintStream ps = getConfig().getReportPrinter();
		if (ps != null) {
			ps.print(report.getResult());
		}
	}
	
	public void setFinished() {
		this.isRunning = false;
	}
	
	
	public static void main(String argv[]) {
		JFrame frame = new JFrame();
		final JTextArea area = new JTextArea();
		frame.getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
		frame.setSize(500, 500);
		final TestWSDLPerformance test = new TestWSDLPerformance();
		
		// Start a test thread.
		new Thread(new Runnable() {
			public void run() {
				// Test SQLServer 2005.
				test.setRemoteURL("http://192.168.1.15:8080/webservice/OneCMDB");
				test.setUp();
				test.getReport().setTitle("Using Remote Backend");
				test.testWriteConcurrency();
				area.append(test.getReport().getResult());
				test.tearDown();
				
				test.setRemoteURL(null);
				
				// Test SQLServer 2005.
				test.setDatasourceResource(OneCMDBTestConfig.SQLSERVER2005_CREATE_DROP_DATASOURCE);
				test.setUp();
				test.getReport().setTitle("Using SQLServer 2005 Backend");
				test.testWriteConcurrency();
				area.append(test.getReport().getResult());
				test.tearDown();
				
				
				// Test with Oracle.
				// Start with HSQLDB 
				test.setDatasourceResource(OneCMDBTestConfig.ORACLE_10_SERVER_CREATE_DROP_DATASOURCE);
				test.setUp();
				test.getReport().setTitle("Using Oracle 10gExpress Backend");
				test.testWriteConcurrency();
				area.append(test.getReport().getResult());
				test.tearDown();
				
				
				// Start with HSQLDB 
				test.setDatasourceResource(OneCMDBTestConfig.HSQL_SERVER_CREATE_DROP_DATASOURCE);
				test.setUp();
				test.getReport().setTitle("Using HSQL-DB Backend");
				test.testWriteConcurrency();
				area.append(test.getReport().getResult());
				test.tearDown();
				
				// Run with MYSQL
				test.setDatasourceResource(OneCMDBTestConfig.MYSQL_CREATE_DROP_DATASOURCE);
				test.setUp();
				test.getReport().setTitle("Using MySQL Backend");
				test.testWriteConcurrency();
				area.append(test.getReport().getResult());
				test.tearDown();
				
				test.setFinished();
			}
		}).start();
	
		// Start a report thread.
		new Thread(new Runnable() {
			public void run() {
				while (test.isRunning()) {
					Report r = test.getReport();
					if (r != null) {
						area.append(r.progress());
						area.append("\n");
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		frame.setVisible(true);
		
	}

	

	public Report getReport() {	
		return(this.report);
	}

	public boolean isRunning() {
		return(this.isRunning);
	}
	
	class ProgressReporter implements Runnable {
			private TestWSDLPerformance test;

			public ProgressReporter(TestWSDLPerformance test) {
				this.test = test;
			}
			
			public void run() {
				while (test.isRunning()) {
					Report r = test.getReport();
					if (r != null) {
						PrintStream ps = this.test.getConfig().getReportPrinter();
						ps.println(r.progress());
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
	}
}
