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
package org.onecmdb.utils.wsdl;

public class SimpleArg {
	String ARGS[][];
	
	public SimpleArg(String[][] args) {
		this.ARGS = args;		
	}

	public String getArg(String name, String argv[]) throws IllegalArgumentException {
		// Check on help.
		for (int i = 0; i < argv.length; i++) {	
			if (argv[i].equals("-?") || 
				argv[i].equals("--help") ||
				argv[i].equals("--?")) {
				showHelp();
			}
		}
		for (int i = 0; i < argv.length; i += 2) {	
			boolean found = false;
			for (int j = 0; j < ARGS.length; j++) {
				if (argv[i].equals("--" + ARGS[j][0])) {
					found = true;
					break;
				} 
			}
			if (!found) {
				System.out.println("Unknown arg: " + argv[i]);
				showHelp();
			}
		
		}
		
		for (int i = 0; i < argv.length; i++) {	
			if (argv[i].equals("--" + name)) {
				return(argv[i+1]);
			}
		}
		// Check default value.
		for (int i = 0; i < ARGS.length; i++) {
			if (name.equals(ARGS[i][0])) {
				return(ARGS[i][2]);			
			}
		}
		
		// Will exit!!!.
		showHelp();		
		
		// For syntax check to be happy.
		return(null);
		
		
	}
	
	public void showHelp() {
		System.out.println("Options:");
		for (int i = 0; i < ARGS.length; i++) {
			System.out.println("--" + ARGS[i][0] + "\t" + ARGS[i][1]);
			System.out.println("\tdefaultValue=" + ARGS[i][2]);
		}
		System.exit(-2);
	}
}
