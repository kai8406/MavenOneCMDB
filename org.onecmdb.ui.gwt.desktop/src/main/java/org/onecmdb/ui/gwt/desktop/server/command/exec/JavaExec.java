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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentServiceFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;


public class JavaExec {
    Log log = LogFactory.getLog(this.getClass());
	 
	String shells = "AUTO:,BASH:/bin/bash,PERL:/usr/bin/perl,CMD:CMD /C,ACTIVEPERL:/usr/local/ActivePerl-5.6/bin/perl";

	private File startDir;
	private String program;
	private HashMap<String, String> params;
	private Process process;
	private StreamHandler streamHandler;

	private Properties shellMap = new Properties();

	private MDRExecThread control;
	
	
	public JavaExec(MDRExecThread execThread) {
		this.control = execThread;
		// Set default shell map.
		shellMap.put(".bat", "CMD /C");
		shellMap.put(".sh", "/bin/bash");
		shellMap.put(".pl", "/usr/bin/perl");
	}
	
	public Properties getShellMap() {
		return shellMap;
	}

	public void setShellMap(Properties shellMap) {
		this.shellMap = shellMap;
	}



	public void setStreamHandler(StreamHandler streamHandler) {
		this.streamHandler = streamHandler;
	}
	 public void setStartDir(String dir) {
		 this.startDir = new File(dir);
	 }
	
	 public void setProgramPath(String prg) {
		 this.program = prg;
	 }
	 
	 public void setProgramArgs(HashMap<String, String> params) {		 
		 this.params = params;
	 }
	 
	 
	 
	
	/** 
     * 
     * Start the handled program
     * @param unpackdir Directory containing the files in the Job Definition
     * @throws IOException
     */
    public ExecResult doExec() {
        ExecResult result = new ExecResult();
        
        String program = reolveProgram();
        List<String> shellArgs = resolveShell(program);
        List<String> params = reloveParams();
       
        
        List<String> cmdList = new ArrayList<String>();
        cmdList.addAll(shellArgs);
        cmdList.add(program);
        cmdList.addAll(params);
       
        liveLog("CMD-LIST : " + cmdList.toString());
        
        log.info("Startup Directory:" + startDir);
        for (int i = 0; i < cmdList.size(); i++) {
            log.info("$" + i + ": " + cmdList.get(i));
        }
        
        // Do some checks.
        if (!startDir.isDirectory()) {
            result.setRc(ExecResult.ERROR_NO_STARTUP_DIR);
            result.setMessage("Starup directory `" + startDir + "' not accessible.");
            return(result);
        }
        File f = new File(program);
        if (!f.exists()) {
        	 result.setRc(ExecResult.ERROR_NO_PROGRAM);
             result.setMessage("Program `" + program + "' is not found.");
             return(result);
        }
        if (f.isDirectory()) {
        	result.setRc(ExecResult.ERROR_NO_PROGRAM);
            result.setMessage("Program `" + program + "' is a directory.");
            return(result);
       }
       
        try {
            // let us start it now...
            Runtime runtime = Runtime.getRuntime();
            this.process = runtime.exec( cmdList.toArray(new String[0]), null, startDir );
            // handle the streams
            if (this.streamHandler != null) {
            	streamHandler.setStdin(new OutputStreamWriter(this.process.getOutputStream()));
            	streamHandler.setStdout(new BufferedReader(new InputStreamReader(this.process.getInputStream())));
            	streamHandler.setStderr(new BufferedReader(new InputStreamReader(this.process.getErrorStream())));
            }
            // according to the contract for `doExec' we should wait until the
            // the process has finished.
            
            int rc = this.process.waitFor();
            result.setRc(rc);
            this.process = null;
            
        } catch (InterruptedException e) {
            log.info("Execution interrupted. " + e.getMessage());
            result.setRc(ExecResult.ERROR_INTERRUPTED);
            result.setMessage("Execution interrupted. " + e.getMessage());
        } catch (Throwable e) {
            log.error("Failed to compleate execution. " + e.getMessage(), e);
            result.setRc(ExecResult.ERROR_EXCEPTION);
            result.setMessage("Execution <" + program + "> failed. Reason " + e.getMessage());
        } 
        return result;
    }


    private void liveLog(String msg) {
    	if (this.control != null) {
    		this.control.log(msg);
    	}
    }


	private List<String> reloveParams() {
    	List<String> args = new ArrayList<String>();
    	if (this.params == null) {
    		return(args);
    	}
		for (String arg : params.keySet()) {
			String value = params.get(arg);
			
			if (value == null) {
				continue;
			}
			value = value.trim();
			// Handling space....
			value = value.replace(' ', '_');
			
			if (isUNIX()) {
				args.add("--" + arg);
    			args.add(value);
			} else {
				args.add("/" + arg + ":" + value);
			}
		}
		return(args);
    }
	
    private List<String> resolveShell(String program) {
    	File f = new File(program);
		String ext = "";  
		int index = program.lastIndexOf(".");
		if (index > 0) {
			ext = program.substring(index);
		}
		String shell = shellMap.getProperty(ext);
		liveLog("Shell for " + ext + " resolved to '" + shell + "'");
			
		if (shell == null) {
			return(new ArrayList<String>());
		}
    	String args[] = shell.split(" ");
    	
    	List<String> shellArgs = Arrays.asList(args);
		return(shellArgs);
    }
	
	private String reolveProgram() {
		File f = new File(program);
		if (!f.exists()) {
			String ext = "";
    		// Check type of OS, eq UNIX or Windows -->.bat or .sh 
    		if (isUNIX()) {
				ext = ".sh";
    		} else {
    			ext =".bat";
    		}
    		f = new File(program + ext);
    	}
		return(f.getPath());
	}
	
	public boolean isUNIX() {
		String sep = System.getProperty("file.separator");
		return(sep.equals("/"));
	}
	/* (non-Javadoc)
     * @see com.lokomo.executor.Job#halt()
     */
    protected void halt() {
        if (this.process != null) {
            this.process.destroy();
        }
    }

}
