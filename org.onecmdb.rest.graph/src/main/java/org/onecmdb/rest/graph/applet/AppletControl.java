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
package org.onecmdb.rest.graph.applet;

import org.onecmdb.rest.graph.events.EventDispatcher;
import org.onecmdb.rest.graph.utils.applet.AppletLogger;

import prefuse.activity.ActivityManager;

public class AppletControl {

	private static Object sym = new Object();
	private static int started = 0;
	
	public static void init() {
		
		destroy();
		
		System.out.println("Applet.init()");
		
		synchronized(sym) {
			if (started > 0) {
				//throw new IllegalArgumentException("Only one graph instance allowed!");
			}
			started++;
			AppletLogger.showMessage("Graph Engine " + started + " started");
		}
	}
	
	public static void destroy() {
		System.out.println("Applet.destroy()");
		
		synchronized(sym) {
			if (started == 0) {
				return;
			}
			started--;
			AppletLogger.showMessage("Graph Engine " + started + " stopped");
			if (started == 0) {
				ActivityManager.stopThread();
				
				EventDispatcher.reset();
			}
		}
	}

	public static void start() {
		System.out.println("Applet.start()");
	}

	public static void stop() {
		System.out.println("Applet.stop()");
	}
	
}
