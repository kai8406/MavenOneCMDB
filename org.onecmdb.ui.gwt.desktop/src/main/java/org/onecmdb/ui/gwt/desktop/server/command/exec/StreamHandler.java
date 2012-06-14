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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamHandler {
	Log log = LogFactory.getLog(this.getClass());
	
	public class PipeThread extends Thread {
		Log pipeLog = LogFactory.getLog(this.getClass());
		
		private LineNumberReader reader;
		private PrintWriter writer;
		
		public PipeThread(String name) {
			setName(name);
		}
		public void setReader(Reader reader) {
			this.reader = new LineNumberReader(reader);
		}
		
		public void setWriter(Writer writer) {
			this.writer = new PrintWriter(writer);
		}
		
		
		public void run() {
			// Wait to start...
			pipeLog.info(getName() + " start.");
			control.log(getName() + " start.");

			synchronized(this) {
				if (!control.isTerminate()) {
					if (this.reader == null || this.writer == null) {
						pipeLog.info(getName() + "Will wait...");
						control.log(getName() + "Will wait...");
						if (!control.isTerminate()) {
							try {
								this.wait();
							} catch (InterruptedException e) {

							}
						}
					}
				}
			}
			pipeLog.info(getName() + " started.");
			control.log(getName() + " started.");
			
			boolean eof = false;
			
			if (this.reader == null) {
				pipeLog.info(getName() + " no reader set");
				control.log(this.getName() + " no reader set.");
				eof = true;	
			}
			
			try {
				while(!eof) {
					if (control.isTerminate()) {
						eof = true;
						continue;
					}
				
					String data;
					data = this.reader.readLine();
					if (data == null) {
						eof = true;
						continue;
					}
					writer.println(data);
					control.log(getName() + " - " + data);
				}
			} catch (IOException e) {
				pipeLog.error(getName() + " failed:", e);
			}
			
			if (autoClose) {
				pipeLog.info(getName() + " auto-close streams.");
				control.log(getName() + " auto-close streams");
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (Throwable t) {
					//
				}
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (Throwable t) {
					//
				}
			}
			pipeLog.info(getName() + " terminated.");
			control.log(getName() + " terminated");
		}
	}

	private PipeThread stdout;
	private PipeThread stderr;
	private PipeThread stdin;
	private boolean autoClose;
	private MDRExecThread control;
	
	public StreamHandler(MDRExecThread execThread, String name) {
		this.control = execThread;
		stdout = new PipeThread(name + " - [STDOUT]");
		stderr = new PipeThread(name + " - [STDERR]");
		stdin = new PipeThread(name + " - [STDIN]");
		
		stdout.setDaemon(true);
		stderr.setDaemon(true);
		stdin.setDaemon(true);
		
		// Start threads.
		stdout.start();
		stderr.start();
		stdin.start();
	}
	
	public void setStdout(Writer writer) {
		stdout.setWriter(writer);
	}
	
	public void setStderr(Writer writer) {
		stderr.setWriter(writer);
	}
	
	public void setStdin(Reader reader) {
		stdin.setReader(reader);
	}
	
	/**
	 * Set's the stdin writer, will kick of the writer thread.
	 * @param reader
	 */
	protected void setStdin(Writer writer) {
		stdin.setWriter(writer);
		synchronized(stdin) {
			stdin.notify();
		}
	}

	/**
	 * Set's the stdout reader, will kick of the reader thread.
	 * @param reader
	 */
	protected void setStdout(Reader reader) {
		stdout.setReader(reader);
		synchronized(stdout) {
			stdout.notify();
		}
	}

	/**
	 * Set's the stderr reader, will kick of the reader thread.
	 * @param reader
	 */
	protected void setStderr(Reader reader) {
		stderr.setReader(reader);
		synchronized(stderr) {
			stderr.notify();
		}
	}

	public void setAutoClose(boolean value) {
		this.autoClose = value;
		
	}

	public void terminate() {
		if (stderr != null) {
			synchronized(stderr) {
				stderr.notify();
			}
		}
		if (stdout != null) {
			synchronized(stdout) {
				stdout.notify();
			}
		}
		if (stdin != null) {
			synchronized(stdin) {
				stdin.notify();
			}
		}
	}
}
