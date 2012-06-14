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
package org.onecmdb.ui.gwt.desktop.server.command.exec;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
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
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.window.mdr.MDRConfigureWindow;
import org.onecmdb.ui.gwt.desktop.server.command.ExecCommand;
import org.onecmdb.ui.gwt.desktop.server.service.model.ShellMapper;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;

public class MDRExecThread extends Thread {

	private ExecCommand cmd;
	Log logger = LogFactory.getLog(this.getClass());
	private List<PrintWriter> logs = new ArrayList<PrintWriter>();
	private boolean terminate;
	private JavaExec exec;
	StreamHandler streamHandler = null;
	private Date createDate;

	public MDRExecThread(ExecCommand cmd) {
		this.cmd = cmd;
		this.createDate = new Date();
	}
	
	public void run() {
		CiBean historyBean = null;
		String stdErr = null;
		String stdOut = null;
		ExecResult result = null;
		String prgPath = null;
		
		try {
			CiBean[] beans = getMDRBeans();
			CiBean mdrBean = beans[0];
			CiBean configBean = beans[1];
			/*
			final CiBean mdrBean = cmd.getCI("Ci", cmd.getMdr());
				
			//final CiBean mdrBean = cmd.getCI("Ci", configBean.toStringValue("mdrRepository"));
			if (mdrBean == null) {
				throw new IllegalArgumentException("No MDR with name '" + cmd.getMdr() + "' found!");
			}
			log("MDR '" + cmd.getMdr() + "' loaded...");
			
			// Fetch Config.
			Collection<CiBean> configBeans = cmd.queryCI("MDR_ConfigEntry", "name", cmd.getConfig());
			if (configBeans.size() != 1) {
				throw new IllegalArgumentException("Found " + configBeans.size() + " config CI's with name <" + cmd.getConfig() + "> is not found.");
			}

			final CiBean configBean = configBeans.iterator().next();
			*/
			log("MDR Config loaded...");
			 
			
			historyBean = cmd.createHistory(configBean);

			log("History created [" + historyBean.getAlias() + "] ...");

			final HashMap<String, String> params = new HashMap<String, String>();
			log("Initilize Params START...");
			for (ValueBean v : configBean.getAttributeValues()) {
				String value = v.getValue();
				if (params.containsKey(v.getAlias())) {
					String oldValue = params.get(v.getAlias());
					value = oldValue + "," + value;
				}
				params.put(v.getAlias(), value);
				log("\t" + v.getAlias() + "=" + value);
			}
			params.put("mdr_history", historyBean.getAlias());
			params.put("onecmdb_token", cmd.getToken());

			log("Initilize Params END...");

			log("Exec Thread Started...");
	
			// Create exec.
			synchronized(this) {
				if (this.terminate) {
					throw new InterruptedException("Terminate...");
				}
				exec = new JavaExec(this);
				Properties p = ShellMapper.getShellProperties();
				if (p != null) {
					exec.setShellMap(p);
				}
			}
			exec.setProgramArgs(params);

			String mdrName = mdrBean.toStringValue("name");
			String configProgram = configBean.toStringValue("program");

			String startPath = cmd.getRoot() + "/" + mdrName;
			File startFile = new File(startPath);
			log("Start path set to '" + startPath +"'");

			if (!startFile.exists() || !startFile.isDirectory()) {
				throw new IllegalArgumentException("No 'program' specified in config!");
			}

			if (configProgram.length() == 0) {
				throw new IllegalArgumentException("No 'program' specified in config!");
			}


			prgPath = startPath + "/" + configProgram;

			log("Program path set to '" + prgPath +"'");

			exec.setProgramPath(prgPath);
			exec.setStartDir(startPath);
			// Direct in/out/err...
			
			String logPath = startPath + "/logs/";
			File logFile = new File(logPath);;
			if (!logFile.exists()) {
				logFile.mkdirs();
			}
			File prgFile = new File(prgPath);
			String prgName = prgFile.getName();

			// Time
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd-HHmmss");
			String dateStr = fmt.format(new Date());
			stdErr = logPath + "stderr-" + prgName + "-" + dateStr; 
			stdOut = logPath + "stdout-" + prgName + "-" + dateStr; 
			synchronized(this) {
				if (this.terminate) {
					throw new InterruptedException("Terminate...");
				}
				streamHandler = new StreamHandler(this, mdrBean.toStringValue("name") + "/" + configBean.toStringValue("program"));
			}
	
			streamHandler.setStderr(new FileWriter(new File(stdErr)));
			streamHandler.setStdout(new FileWriter(new File(stdOut)));
			streamHandler.setAutoClose(true);
			//streamHandler.setStdin(writer);

			log("Direct stdout to " + stdOut);
			log("Direct stderr to " + stdErr);

			exec.setStreamHandler(streamHandler);
	
			// Do exec.
			log("Start Exec...");
			long start = System.currentTimeMillis();
			result = exec.doExec();
			long stop = System.currentTimeMillis();
			log("Exec Ended [" + (stop-start) + "ms], result=" + result.toString());
		} catch (Throwable t) {
			log("Exec Exception: ");
			log(t);

			result = new ExecResult();
			result.setMessage("Internal Exception: " + t.getMessage());
			result.setRc(ExecResult.ERROR_EXCEPTION_INIT);
			cmd.setExecError(t);
		} finally {
			if (streamHandler != null) {
				streamHandler.terminate();
			}
			if (historyBean != null) {
				try {
					log("Update History....");
					// Reload historyBean:
					CiBean local = cmd.getCI(historyBean.getDerivedFrom(), historyBean.getAlias());
					CiBean base = local.copy();
					cmd.setValue(local, "exitCode", "" + result.getRc());
					cmd.setValue(local, "execMessage", result.getMessage());
					cmd.setValue(local, "stderr", stdErr);
					cmd.setValue(local, "stdout", stdOut);
					if (result.getRc() != 0) {
						cmd.setValue(local, "status", MDRHistoryState.FAILED);
					} else {
						String status = local.toStringValue("status");
						if (status == null || status.length() == 0 || status.equals(MDRHistoryState.EXECUTING)) {
							cmd.setValue(local, "status", MDRHistoryState.READY);
						}
					}
					IRfcResult rfcResult = cmd.update(local, base);
					if (rfcResult.isRejected()) {
						log("History updated was rejected, cause " + rfcResult.getRejectCause());
					} else {
						log("History update complete");
					}

				} catch (Throwable t) {
					logger.error("Can't update history " + historyBean.getAlias() + " for " + prgPath);
					log("History update failed");
					log(t);
					cmd.setExecError(t);
				}
			}
			log("Execution Completed");
			cmd.unregister(this);
		}
	}
	
	private CiBean[] getMDRBeans() throws Exception {
		CiBean resultVector[] = new CiBean[2];
		
		GraphQuery query = new GraphQuery();
		ItemOffspringSelector mdrs = new ItemOffspringSelector("mdr", "MDR_Repository");
		mdrs.setPrimary(true);
		AttributeValueConstraint mdrCon = new AttributeValueConstraint();
		mdrCon.setAlias("name");
		mdrCon.setOperation(AttributeValueConstraint.EQUALS);
		String mdrName = cmd.getMdr(); 
		mdrCon.setValue(mdrName);
		mdrs.applyConstraint(mdrCon);

		ItemOffspringSelector mdrConfigs = new ItemOffspringSelector("config", "MDR_ConfigEntry");
		AttributeValueConstraint mdrConfigCon = new AttributeValueConstraint();
		mdrConfigCon.setAlias("name");
		mdrConfigCon.setOperation(AttributeValueConstraint.EQUALS);
		String configName = cmd.getConfig();
		mdrConfigCon.setValue(configName);
		mdrConfigs.applyConstraint(mdrConfigCon);

		ItemRelationSelector rel = new ItemRelationSelector("config2mdr", "Reference", "mdr", "config");

		query.addSelector(mdrs);
		query.addSelector(mdrConfigs);
		query.addSelector(rel);

		Graph result = cmd.getService().queryGraph(cmd.getToken(), query);

		Template mdrTempl = result.fetchNode(mdrs.getId());
		if (mdrTempl == null) {
			throw new IllegalArgumentException("No MDR with name '" +  cmd.getMdr() + "' found!");
		}
		if (mdrTempl.getOffsprings() == null || mdrTempl.getOffsprings().size() == 0) {
			throw new IllegalArgumentException("No MDR with name '" +cmd.getMdr() + "' found!", null);
		}
		if (mdrTempl.getOffsprings().size() != 1) {
			throw new IllegalArgumentException("More than one (" + mdrTempl.getOffsprings().size() + "' MDR with name '" + cmd.getMdr() + "' found!", null);
		}
		CiBean mdrBean = mdrTempl.getOffsprings().get(0);
		resultVector[0] = mdrBean;
		
		Template mdrConfigTempl = result.fetchNode(mdrConfigs.getId());
		if (mdrConfigTempl == null) {
			throw new IllegalArgumentException("No MDR Config with name '" + cmd.getConfig() + "' found!", null);
		}
		if (mdrConfigTempl.getOffsprings() == null || mdrConfigTempl.getOffsprings().size() == 0) {
			throw new IllegalArgumentException("No MDR Config with name '" + cmd.getConfig() + "' found!", null);
		}
		if (mdrConfigTempl.getOffsprings().size() != 1) {
			throw new IllegalArgumentException("More than one(" + 
					mdrConfigTempl.getOffsprings().size() + 
					"MDR Config with name '" + cmd.getConfig() + "' found!", null);
		}
		CiBean mdrConfigBean = mdrConfigTempl.getOffsprings().get(0);
		resultVector[1] = mdrConfigBean;
		
		return(resultVector);
	}

	public void log(Throwable t) {
		for (PrintWriter pw : this.logs) {
			synchronized (pw) {
				pw.println("Error: " + t.getMessage());
				t.printStackTrace(pw);
				pw.flush();
			}
		}
	}

	public void log(String msg) {
		for (PrintWriter pw : this.logs) {
			synchronized (pw) {
				pw.println(msg);
				pw.flush();
			}
		}
	}
	
	public void addLog(PrintWriter pw) {
		synchronized(this.logs) {
			this.logs.add(pw);
		}
	}

	public boolean isTerminate() {
		return(this.terminate);
	}

	public void terminate(PrintWriter pw) {
		this.terminate = true;
		synchronized(this) {
			if (exec != null) {
				exec.halt();
			}
			if (streamHandler != null) {
				streamHandler.terminate();
			}
		}
		
	
	}

	public Date getStart() {
		return(createDate);
	}

	public ExecCommand getCmd() {
		return(this.cmd);
	}
}
