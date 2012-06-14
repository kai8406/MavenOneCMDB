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
package org.onecmdb.core.tests.profiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;


/**
 * This static class is used for profiling of the system. The application
 * calls the start(String)/stop() on places where profiling is wanted. The
 * start()/stop() is thread sensative. That means if different threads class
 * start()/stop() they will not interfere with another. It is also aware of
 * the caller stack, that means if a application class start() start() stop()
 * stop() it will match the first stop to to the second start() and when the
 * stack of start()/stop() is empty a record is written to the profile file.
 * The output is generated to a file called profiler.log unless other is
 * specifified in the config file.
 */
public class Profiler {

    private static Hashtable entryTable = new Hashtable();
    private static boolean profile = false;
    private static File profileFile = null;
    private static boolean profileError = false;
    private static boolean firstTime = true;

    /**
     * Check if profiling is configured to be on.
     */
    public synchronized static boolean isOn() {
        return (profile);
    }

    /**
     * Set on/off on profiler.
     */
    public static void useProfiler(boolean on) {
        profile = on;
    }

    /**
     * Set profile file...
     */
    public static void setProfileFile(String file) {
        profileFile = new File(file);
    }

    /**
     * Record a start point.
     */
    public synchronized static void start(String id) {

        if (!isOn() || profileError) {

            return;
        }

        // Allocate a new ProfileData.
        String threadName = Thread.currentThread().getName();
        Vector v = (Vector) entryTable.get(threadName);

        if (v == null) {
            v = new Vector();
            v.add(new Stack());
            entryTable.put(threadName, v);
        }

        ProfileData profileData = new ProfileData();
        profileData.name = id;
        profileData.start = System.currentTimeMillis();
        profileData.startMem = Runtime.getRuntime().totalMemory();
        push(v, profileData);
    }

    /**
     * Record a stop point.
     */
    public synchronized static void stop() {
        stop(null);
    }

    /**
     * Record a stop point.
     */
    public synchronized static void stop(String msg) {
        if (!isOn() || profileError) {

            return;
        }

        String threadName = Thread.currentThread().getName();
        Vector v = (Vector) entryTable.get(threadName);

        if (v == null) {

            // OOps call a end without a start....
            return;
        }

        pop(v, msg);
    }

    /**
     * Internal function to handle the stack of the start()/stop() class..
     */
    private synchronized static void push(Vector v, ProfileData data) {

        Stack s = (Stack) v.firstElement();
	if (!s.isEmpty()) {
	    ProfileData parent = (ProfileData)s.peek();
	    parent.calls++;
	}
        s.push(data);
        v.add(data);
    }

    /**
     * Internal function to handle the stack of the start()/stop() class..
     */
    private synchronized static void pop(Vector v, String msg) {

        Stack s = (Stack) v.firstElement();
        ProfileData data = (ProfileData) s.pop();
        data.stop = System.currentTimeMillis();
        data.stopMem = Runtime.getRuntime().totalMemory();

        if (msg != null) {
            data.name += ":" + msg;
        }

        if (s.isEmpty()) {
            logProfileData(v);

            // Remove from table..
            String threadName = Thread.currentThread().getName();
            entryTable.remove(threadName);
        }
    }

    /**
     * Internal function for writing a profile entry to a file.
     */
    private synchronized static void logProfileData(Vector v) {

        // Open file file for append.
	if (profileFile == null) {
	    profileFile = new File("profile.log");
	}

        // Write entry...
        PrintStream outf = null;

        try {
            outf = new PrintStream(new FileOutputStream(profileFile.getPath(),
                                                        profileFile.exists()));



            if (firstTime) {
                outf.println("###############  Start profiling " + new Date());
		outf.println("##ThreadName;Levels;" + getHeader());
		firstTime = false;
	    }

	    outf.print(Thread.currentThread().getName() + ";" + (v.size() - 1)
		       + ";");

            for (int i = 1; i < v.size(); i++) {
            	String prefix = "";
            	for (int j = 1; j < i; j++) {
            		prefix += " ";
            	}
                outf.println(prefix + (v.get(i)).toString());
            }

            //outf.println("");
            outf.close();
        } catch (IOException e) {
            System.err.println(
                    "Can't open profile file... " + profileFile.getPath() + ":" + e);
            System.err.println("No profiling will be made...");
            profileError = true;
        } finally {

            // Close file...
            if (outf != null) {
                outf.close();
            }
        }
    }

    private static String getHeader() {
	return ("Name;Calls;StartTime;StopTime;DeltaTime;StartMem;StopMem;DeltaMem");
    }

    /**
     * Internal class to store variables.
     */
    static class ProfileData {

	public String name;
	public long start;
	public long stop;
	public long startMem;
	public long stopMem;
	public int  calls = 0;
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String toString() {

	    return (name + ";" + calls + ";" + start + ";" + stop + ";" + (stop - start) + ";"
		    + startMem + ";" + stopMem + ";" + (stopMem - startMem) + ";");
	}

    }

}

