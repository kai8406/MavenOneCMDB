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
package org.onecmdb.ui.gwt.desktop.server.command;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.ui.gwt.desktop.client.service.model.AliasFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRStartWindow;
import org.onecmdb.ui.gwt.desktop.server.command.exec.ExecResult;
import org.onecmdb.ui.gwt.desktop.server.command.exec.JavaExec;
import org.onecmdb.ui.gwt.desktop.server.command.exec.MDRExecThread;
import org.onecmdb.ui.gwt.desktop.server.command.exec.StreamHandler;





public class ExecCommand extends AbstractOneCMDBCommand {

	private String root = null;

	Log log = LogFactory.getLog(this.getClass());
	
	private Properties attrMap;
	private String config;
	private String mdr;
	private String verbose;
	private String history = MDRHistoryState.getHistoryTemplate();
	// Valid commands are [START,LISTEN,STOP]
	private String cmd;
	private String procid;

	private CiBean historyBean;

	private Throwable execError;
	
	private static HashMap<String, MDRExecThread> threadMap = new HashMap<String, MDRExecThread>();
	
	/**
	 * Thread handling...
	 * 
	 */
	public void unregister(MDRExecThread execThread) {
		System.out.println("unregister " + execThread.getCmd().getThreadKey());
		
		synchronized(threadMap) {
			threadMap.remove(execThread.getCmd().getThreadKey());
		}
	}

	public void register(MDRExecThread execThread) {
		System.out.println("register " + execThread.getCmd().getThreadKey());
		
		synchronized(threadMap) {
			threadMap.put(getThreadKey(), execThread);
		}
	}
	
	// Self test..
	public static void main(String argv[]) {
		try {
			ExecCommand cmd = new ExecCommand();
			AbstractOneCMDBCommand.run(cmd, argv);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getVerbose() {
		return verbose;
	}

	public void setVerbose(String verbose) {
		this.verbose = verbose;
	}

	public void setConfig(String config)  {
		this.config = config;
	}
	
	public String getConfig() {
		return(this.config);
	}
	
	
	public String getMdr() {
		return mdr;
	}

	public void setMdr(String mdr) {
		this.mdr = mdr;
	}

	public void setHistory(String history)  {
		this.history = history;
	}
	
	public String getHistory() {
		return(this.history);
	}


	

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getContentType() {
		String contentType = "text/plain";
		return(contentType);
	}
	
	
	public String getRoot() {
		return(this.root);
	}
	
	

	public void setRoot(String root) {
		log.info("Set ExecHandler Root to <" + root + ">");
		this.root = root;
	}

	public void transfer(OutputStream out) throws Throwable {
		final PrintWriter pw = new PrintWriter(out);
		
		// Initial validation.
		if (mdr == null || config == null || mdr.length() == 0 || config.length() == 0) {
			throw new IllegalArgumentException("mdr and config arguments must be set!");
		}
		if (cmd.equalsIgnoreCase("start")) {
			// do start a process..
			doStart(pw);
			return;
		} else if (cmd.equalsIgnoreCase("stop")) {
			// do stop a process
			doStop(pw);
			return;
		} else if (cmd.equalsIgnoreCase("listen")) {
			// do listen on a process.
			doListen(pw);
			return;
		} else if (cmd.equalsIgnoreCase("list")) {
			doListRunning(pw);
			return;
		} else if (cmd.equalsIgnoreCase("status")) {
			doStatus(pw);
			return;
		}
				
				
		throw new IllegalArgumentException("cmd '" + cmd + "' is not valid, use start|stop|listen|list");
	}
	
	private void doListRunning(PrintWriter pw) {
		synchronized(threadMap) {
			for (String key : threadMap.keySet()) {
				MDRExecThread thread = threadMap.get(key);
				pw.println(key);
				if (isVerbose()) {
					pw.println("Started:" + thread.getStart());
					pw.println("Runned:" + ((new Date()).getTime() - thread.getStart().getTime()) + "ms");
				}
			}
		}
	}
	
	private void doStatus(PrintWriter pw) {
		MDRExecThread thread = null;
		synchronized(threadMap) {
			 thread = threadMap.get(getThreadKey());
		}

		if (thread == null) {
			pw.println("NOT_FOUND");
		} else {
			pw.println("RUNNING");
		}
	}

	private void doListen(PrintWriter pw) {
		MDRExecThread thread = null;
		synchronized(threadMap) {
			thread = threadMap.get(getThreadKey());
		}
		if (thread == null) {
			pw.println(getThreadKey() + " is not running!");
		}
		thread.addLog(pw);
		synchronized(thread) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				pw.println("Interuppted....");
				e.printStackTrace();
			}
		}
		synchronized(threadMap) {
			threadMap.remove(getThreadKey());
		}
	}

	private void doStop(PrintWriter pw) {
		MDRExecThread thread = null;
		synchronized(threadMap) {
			thread = threadMap.get(getThreadKey());
		}
		if (thread == null) {
			pw.println(getThreadKey() + " is not running!");
			return;
		}
		thread.terminate(pw);
	}

	private String getThreadKey() {
		return(mdr + "_" + config);
	}

	public void doStart(final PrintWriter pw) throws Exception {	
		pw.println("Starting " + getThreadKey());
		pw.flush();
		
		// Check if config is already running.
		MDRExecThread thread = null;
		synchronized(threadMap) {
			thread = threadMap.get(getThreadKey());
		}
		if (thread != null) {
			pw.println(config + "is already running ");
			pw.flush();
			if (isVerbose()) {
				doListen(pw);
			}
			return;
		}

		pw.println(getThreadKey() + " starting...");
		
		MDRExecThread start = new MDRExecThread(this);
		start.setDaemon(true);
		start.setName("OneCMDB-MDR-" + getThreadKey());
		register(start);
		start.start();
	
		if (isVerbose()) {
			doListen(pw);
		}
	}

	private boolean isVerbose() {
		if (verbose == null) {
			return(false);
		}
		return(verbose.equals("true"));
	}

	public IRfcResult update(CiBean local, CiBean base) throws Exception {
		List<CiBean> locals = new ArrayList<CiBean>();
		List<CiBean> bases = new ArrayList<CiBean>();
		locals.add(local);
		bases.add(base);
		IRfcResult result = getService().update(getToken(), locals.toArray(new CiBean[0]), bases.toArray(new CiBean[0]));
		return(result);
	}

	public void setValue(CiBean local, String alias, String value) {
		ValueBean v = local.fetchAttributeValueBean(alias, 0);
		if (v == null) {
			v = new ValueBean();
			v.setAlias(alias);
			local.addAttributeValue(v);
		}
		v.setValue(value);
	}

	public CiBean getHistoryBean() {
		return(historyBean);
	}
	
	public CiBean createHistory(CiBean configBean) throws Exception {
		CiBean bean = new CiBean();
		bean.setDerivedFrom(history);
		bean.setAlias(AliasFactory.generateAlias(history));
		bean.setTemplate(false);
		bean.addAttributeValue(new ValueBean("mdrConfigEntry", configBean.getAlias(), true));
		setValue(bean, "status", MDRHistoryState.EXECUTING);
		
		IRfcResult result = getService().update(getToken(), new CiBean[] {bean}, null);
		if (result.isRejected()) {
			throw new IllegalArgumentException("Can't create history entry <" + history + ">. Reason " + result.getRejectCause());
		}
		historyBean = getCI(history, bean.getAlias());
		return(historyBean);
	}

	public void setExecError(Throwable t) {
		this.execError = t;
	}
	
	public Throwable getExecError() {
		return(this.execError);
	}

	


	
}
