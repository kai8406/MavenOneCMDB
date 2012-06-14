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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.internal.job.RunNativeProgram;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;

public class RunToolProcess extends WorkflowProcess {

	private RunNativeProgram start;
	private String programPath;
	private String programArguments;
	private String timeoutString;
	private OutputStream stdout = System.out;
	private OutputStream stderr = System.err;
	private InputStream stdin;
	
	Log log = LogFactory.getLog(this.getClass());
	private List<String> args;
	
	
	public String getProgramArguments() {
		return programArguments;
	}

	public void setProgramArguments(String programArguments) {
		this.programArguments = programArguments;
	}

	public String getProgramPath() {
		return programPath;
	}

	public void setProgramPath(String programPath) {
		this.programPath = programPath;
	}

	public void setArguments(List<String> args) {
		this.args = args;
	}
	
	public RunNativeProgram getStart() {
		return start;
	}

	public void setStart(RunNativeProgram start) {
		this.start = start;
	}

	public OutputStream getStderr() {
		return stderr;
	}

	public void setStderr(OutputStream stderr) {
		this.stderr = stderr;
	}

	public InputStream getStdin() {
		return stdin;
	}

	public void setStdin(InputStream stdin) {
		this.stdin = stdin;
	}

	public OutputStream getStdout() {
		return stdout;
	}

	public void setStdout(OutputStream stdout) {
		this.stdout = stdout;
	}

	public String getTimeout() {
		return timeoutString;
	}

	public void setTimeout(String timeout) {
		this.timeoutString = timeout;
	}

		
	public void run() throws Throwable {
		long timeout = 0;
		/*
		String path = (String) in.get("program.path");
		String argument = (String)in.get("program.arguments");
		String stdout = (String)in.get("program.stdout");
		String stderr = (String)in.get("program.stderr");
		InputStream stdin = (InputStream)in.get("program.stdin");
		
		String timeoutString = (String)in.get("timeout");
		*/
		
		if (timeoutString != null) {
			timeout = Long.parseLong(timeoutString);
		}
		
		// TODO: Setup stdout/stderr.
		
		
		String[] argArray = null; 
		if (this.args != null) {
			argArray = (String[]) args.toArray(new String[0]);
		} else {
			argArray = this.programArguments.split(" ");
		}
		
		StringBuffer argString = new StringBuffer();
		for (int i = 0; i < argArray.length; i++) { 
			argString.append(" " + argArray[i]);
		}
		log.info("RUN PROGRAM '" + programPath + argString.toString());
		
		start = new RunNativeProgram(programPath, argArray);
		
		start.setStdout(System.out);
		start.setStderr(System.err);
		
		OutputStream progStdin = start.getStdin();
		// Connect progStdin with user stdin
		if (this.stdin != null) {
			// Connect them
			
		}
		
		start.start();
		
		start.waitForProcess(timeout);
		
		int exitStatus = start.getExitStatus();
		out.put("exit", "" + exitStatus);
		if (exitStatus == 0) {
			out.put("ok", "true");
		} else {
			out.put("ok", "false");
			out.put("cause", "<" + this.programPath + argString.toString() + "> ExitStatus=" + exitStatus);
		}
	}

	@Override
	public void interrupt() {
		if (start != null) {
			start.terminate();
		}
	}
	
	public static void main(String argv[]) {
		RunToolProcess tool = new RunToolProcess();
		tool.setProgramPath(argv[0]);
		String args = "";
		for (int i = 1; i < argv.length; i++) {
			args += " " + argv[i];
		}
		tool.setProgramArguments(args);
		tool.setStdout(System.out);
		tool.setStdin(System.in);
		tool.setStderr(System.err);
		
		try {
			tool.run();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Output:");
		for (String key : tool.getOutParameter().keySet()) {
			System.out.println("\t" + key + "=" + tool.getOutParameter().get(key));
		}
		
	}
}
