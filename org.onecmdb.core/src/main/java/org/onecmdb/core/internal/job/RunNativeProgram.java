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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RunNativeProgram extends Thread {

	private List<String> execArgs = new ArrayList<String>();
	
	private Process process = null;

	private int exitStatus = 0;

	private CheckInputStream stdoutThread = null;

	private CheckInputStream stderrThread = null;

	public static final int EXIT_ALREADY_RUNNING = -128000;

	public static final int EXIT_CANT_START = -128001;

	public static final int EXIT_NOT_TERMINATED = -128002;

	public static final int EXIT_INTERRUPTED = -128003;

	private Log logger = null;

	private OutputStream stdin = null;

	private OutputStream stdout = null;

	private OutputStream stderr = null;


	public RunNativeProgram(String programPath, String[] args) {
		execArgs.add(programPath);
		if (args != null) {
			Collections.addAll(execArgs, args);
		}
	}

	public void setStdout(OutputStream out) {
		this.stdout = out;
	}

	public void setStderr(OutputStream out) {
		this.stderr = out;
	}

	public OutputStream getStdin() {
		return (this.stdin);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void run() {

		// ////Log.log("Start process " + nativeProgram);
		if (process != null) {
			// Already running..., Don't start it again.
			setExitStatus(EXIT_ALREADY_RUNNING);

			return;
		}

		try {
			this.process = Runtime.getRuntime().exec((String[])execArgs.toArray(new String[0]), null, null);

			// TODO:
			// Listen to stdin/stdout/stderr else there can be problems
			// exceuting shells/bat-scripts.
			stdoutThread = new CheckInputStream("stdout", process
					.getInputStream(), stdout);
			stderrThread = new CheckInputStream("stderr", process
					.getErrorStream(), stderr);

			stdin = process.getOutputStream();

			// stdin.close();

			stdoutThread.start();
			stderrThread.start();
		} catch (IOException e) {
			setExitStatus(EXIT_CANT_START);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getExitStatus() {
		return (exitStatus);
	}

	private void setExitStatus(int status) {
		exitStatus = status;
	}

	
	/**
	 * Try to kill the process.
	 *
	 */
	public void terminate() {
		if (process != null) {
			process.destroy();
		}
	}
	
	/**
	 * Wait for this process to stop. A timeout in ms must be specified. If the
	 * process hasn't terminated inside the timeout then it will be abruptly
	 * killed. If timeout is set to 0 it will wait until process ends.
	 */
	public void waitForProcess(long timeout) {
		try {
			// Check that the process has started...
			int loopCount = 0;

			while (process == null) {
				// ////Log.log("No process running....");
				if (loopCount > 15) {
					// ////Log.err("Process hasn't started yet", null);
					setExitStatus(EXIT_CANT_START);

					return;
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// ////Log.log("WaitForProcess was interuppted!");
					setExitStatus(EXIT_CANT_START);

					return;
				}

				loopCount++;
			}

			if (timeout == 0) {
				// ////Log.log("Wait unti process " + nativeProgram + "
				// terminates");
				process.waitFor();
				setExitStatus(process.exitValue());
			} else {
				// ////Log.log("Set timeout to " + timeout + "s on process " +
				// nativeProgram);
				int sleepTime = 0;
				int exitS = EXIT_NOT_TERMINATED;
				boolean finished = false;

				while (!finished) {
					try {
						Thread.sleep(1000);
						exitS = process.exitValue();
						finished = true;
					} catch (IllegalThreadStateException e) {
						sleepTime += 1000;

						if (sleepTime > timeout) {
							throw e;
						}
					}
				}

				setExitStatus(exitS);

				// ////Log.log("Process " + nativeProgram + " has terminated");
			}
		} catch (IllegalThreadStateException e) {
			// Process hans't exited...
			// Kill it and set error status.
			// ////Log.err("Process " + nativeProgram + " has not terminated.",
			// e);
			process.destroy();

			// ////Log.err("Process " + nativeProgram + " was killed.", e);
			setExitStatus(EXIT_NOT_TERMINATED);
		} catch (InterruptedException e) {
			// What to do.
			// //Log.err("waitForEnd was interrupted...", e);
			process.destroy();

			// //Log.err("Process " + nativeProgram + " was killed.", e);
			setExitStatus(EXIT_INTERRUPTED);
		} finally {
			if (stdoutThread != null) {
				stdoutThread.terminate();
				stdoutThread = null;
			}

			if (stderrThread != null) {
				stderrThread.terminate();
				stderrThread = null;
			}
			if (stdin != null) {
				try {
					stdin.close();
				} catch (IOException e) {
				}
			}
		}

		// //Log.log("Process " + nativeProgram + " exitstatus set to " +
		// getExitStatus());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param argv
	 *            DOCUMENT ME!
	 */
	public static void main(String[] argv) {
		List<String> argList = new ArrayList<String>();
		Collections.addAll(argList, argv);
		
		String program = argList.get(0);
		argList.remove(0);
		
		String[] args = new String[0];
		if (argList.size() > 0) {
			args = (String[])argList.toArray(new String[0]);
		}
		RunNativeProgram runNative = new RunNativeProgram(argv[0], args);
		runNative.setStdout(System.out);
		runNative.start();
		runNative.waitForProcess(Integer.parseInt(argv[1]));
	}

	/**
	 * Help class to scan stdin/stdout..
	 */
	class CheckInputStream extends Thread {

		private volatile boolean terminate = false;

		private InputStream input = null;

		private OutputStream output = null;

		private String name = null;

		/**
		 * Creates a new CheckInputStream object.
		 * 
		 * @param name
		 *            DOCUMENT ME!
		 * @param input
		 *            DOCUMENT ME!
		 */
		public CheckInputStream(String name, InputStream input,
				OutputStream output) {
			this.input = input;
			this.name = name;
			this.output = output;
		}

		/**
		 * DOCUMENT ME!
		 */
		public void run() {

			try {

				// Try to read input.
				StringBuffer buffer = new StringBuffer();

				while (!terminate) {
					int len = input.available();
					if (len > 0) {
						byte buf[] = new byte[len];
						//System.out.println("Avaliable " + input.available());
						int readLen= input.read(buf, 0, len);
						if (output != null) {
							output.write(buf, 0, readLen);
						}
					}
					if (len < 0) {
						terminate();
					} 
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
			} catch (IOException e) {

				// //Log.log("Read " + name + " got " + e);
			}

			// //Log.log("CheckInputStream " + name + " terminated");
		}

		/**
		 * DOCUMENT ME!
		 */
		public synchronized void terminate() {
			try {
				if (this.output != null) {
					while (input.available() > 0) {
						int len = input.available();
						byte buf[] = new byte[len];
						//System.out.println("Avaliable " + input.available());
						int readLen= input.read(buf, 0, len);
						output.write(buf, 0, readLen);
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						}
						// System.out.print(" S" + input.available());
					}
				}
			} catch (IOException e) {
			}

			// //Log.log("Terminating " + name);
			// this.interrupt(); Will crash JVM on BSDI 4.1!!!
			try {
				input.close();
			} catch (IOException e) {

				// //Log.err("Got exception closing input : " + e, e);
			}
			/*
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {

				}
			}
			*/
			terminate = true;
		}
	}
}
