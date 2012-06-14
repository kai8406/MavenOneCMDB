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
package org.onecmdb.rest.graph.test;

import java.applet.AppletStub;

import javax.swing.JApplet;

import org.omg.SendingContext.RunTime;
import org.onecmdb.rest.graph.utils.applet.AppletLaunch;

public class TestOutofMemory extends JApplet implements AppletStub {
	
	
	

	

	private AppletLaunch launch;
	private long consumtion;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() {
		super.init();
		
		System.out.println("Init");
	}

	@Override
	public void start() {
		super.start();
		
		long totalMemory = Runtime.getRuntime().totalMemory();
		
		
		launch = new AppletLaunch();
		launch.setStub((AppletStub)this);
		getContentPane().add(launch);
		
		for (int i = 0; i < 100; i++) {
			launch.init();
			launch.start();
			
			Runtime.getRuntime().gc();
			long currentMemory = Runtime.getRuntime().totalMemory();
			System.out.println("==============================================");
			System.out.println("START MEMORY: " + (currentMemory - totalMemory));
			System.out.println("==============================================");
			totalMemory = currentMemory;
		
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			launch.stop();
			
			launch.destroy();
	
			Runtime.getRuntime().gc();
			currentMemory = Runtime.getRuntime().totalMemory();
			long dm = currentMemory - totalMemory;
			
			consumtion += dm;
			System.out.println("==============================================");
			System.out.println("STOPPED MEMORY: DELTA[" + (dm) + "] TOTAL[" + consumtion +"]");
			System.out.println("==============================================");
			totalMemory = currentMemory;

			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

	@Override
	public void stop() {
		super.stop();
		System.out.println("Init");
	}

	public void appletResize(int width, int height) {
	      resize(width, height);

	}

	
}
