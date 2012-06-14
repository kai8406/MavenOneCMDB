/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.core.tests.performance;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerateHardware {
	int N_SYSTEMS_PER_SWITCH = 16;
	int N_SYSTEMS_PER_ROUTER = 16;
	
	List<CI> items = new ArrayList<CI>();
	
	public static void main(String argv[]) {
		int servers = Integer.parseInt(argv[0]);
		int networks = Integer.parseInt(argv[1]);
		String file = null;
		if (argv.length > 2) {
			file = argv[2];
		}
		new GenerateHardware().generate(file, servers, networks);
	}
	
	public void generate(String file, int servers, int networks) {
		int nServersPerNetwork = servers / networks;
		CIRouter root = new CIRouter("Internet", "ROOT");
		new CINetwork("Internet");
		for (int i = 0; i < networks; i++) {
			CIRouter r = getRouter("LAN-" + i, "NET-" + i +".ROOT", 0, nServersPerNetwork);
			root.addSystem(r);
			new CINetwork("LAN-" + i);
		}
		System.out.println(root.toString(0));
		
		for (CI ci : items) {
			System.out.println(ci.toXML());
		}
		if (file != null) {
			try {
				PrintWriter pWriter = new PrintWriter(file);
				pWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				pWriter.println("<onecmdb>");
				pWriter.println("<instances>");
				for (CI ci : items) {
					pWriter.println(ci.toXML());
				}
				pWriter.println("</instances>");
				pWriter.println("</onecmdb>");
				
				pWriter.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	}
	
	public CIRouter getRouter(String network, String path, int level, int servers) {
		level++;
		path = path + "." + level;
		CIRouter r = new CIRouter(network, path);
		int nSwitches = (servers + N_SYSTEMS_PER_SWITCH/2) / N_SYSTEMS_PER_SWITCH;
		int nRouters = nSwitches / N_SYSTEMS_PER_ROUTER;
		if (nRouters > 0 && !(servers <= N_SYSTEMS_PER_SWITCH * N_SYSTEMS_PER_ROUTER)) {
			int serverOffset = servers;
			for (int i = 0; i < (nRouters+1); i++) {
				int remainingServers = (serverOffset < (N_SYSTEMS_PER_SWITCH * N_SYSTEMS_PER_ROUTER)) ? serverOffset : (N_SYSTEMS_PER_SWITCH * N_SYSTEMS_PER_ROUTER);
				r.addSystem(getRouter(network, path, (level+1), remainingServers));
				serverOffset -= (N_SYSTEMS_PER_SWITCH * N_SYSTEMS_PER_ROUTER);
			}
		} else {
			// Add all switches to the same router.
			int serverOffset = servers; 
			for (int i = 0; i < nSwitches; i++) {
				int remainingServers = (serverOffset < N_SYSTEMS_PER_SWITCH) ? serverOffset : N_SYSTEMS_PER_SWITCH;
				r.addSystem(getSwitch(network, path + "-" + i, level+1, remainingServers)); 
				serverOffset -= N_SYSTEMS_PER_SWITCH;
			}
		}
		return(r);
	}
	
	
	private CISwitch getSwitch(String network, String path, int level, int servers) {
		path = path + "." + level;
		CISwitch s = new CISwitch(network, path);
		
		for (int i = 0; i < servers; i++) {
			s.addSystem(new CIServer(network ,path + "." + i));
		}
		return(s);	
	}

	class CI {
		protected String name;
		
		public CI(String name) {
			this.name = name;
			items.add(this);
		}
		public StringBuffer toXML() {
			return(new StringBuffer());
		}	
	}
	class CINetwork extends CI {
		public CINetwork(String name) {
			super(name);
		}
		
		public StringBuffer toXML() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\t<Network alias=\"" + name + "\">");
			buffer.append("\n");
			buffer.append("\t\t<A_Name>" + name +"</A_Name>");
			buffer.append("\n");
			buffer.append("\t</Network>");
			return(buffer);
		}
	}
	class CISystem extends CI {
		
		protected String network;
		
		public CISystem(String network, String name) {
			super(name);
			this.network = network;
			
		}
		protected String getLevel(int level) {
			String pad = "";
			for (int i = 0; i < level; i++) {
				pad += "\t";
			}
			return(pad);
		}
		public StringBuffer toString(int level) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(getLevel(level) + name);
			return(buffer);
		}
		public String toString() {
			return(toString(0).toString());
		}
		public String getName() {
			return(name);
		}
	}
	
	class CINetworkDevice extends CISystem {
		
		public CINetworkDevice(String network, String name) {
			super(network, name);
		}

		List<CISystem> systems = new ArrayList<CISystem>();
		protected CINetworkDevice uplink;	
		public void addSystem(CISystem system) {
			systems.add(system);
			if (system instanceof CINetworkDevice) {
				((CINetworkDevice)system).setUplink(this);
			}
		}
		
		public void setUplink(CINetworkDevice device) {
			this.uplink = device;
		}
		
		public StringBuffer toString(int level) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(super.toString(level));
			buffer.append("\n");
			for (CISystem system : systems) {
				buffer.append(getLevel(level+1));
				buffer.append(system.toString(level+1));
				buffer.append("\n");
			}
			return(buffer);
		}
	}
	
	class CIRouter extends CINetworkDevice {

		public CIRouter(String network, String name) {
			super(network, "Router-" + name);
			// TODO Auto-generated constructor stub
		}
		public StringBuffer toXML() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\t<Router16Port alias=\"" + name + "\">");
			buffer.append("\n");
			buffer.append("\t\t<A_Name>" + name +"</A_Name>");
			buffer.append("\n");
			buffer.append("\t\t<BD_Network><ref alias=\"" + network + "\"/></BD_Network>");
			buffer.append("\n");
			if (uplink != null) {
				buffer.append("\t\t<X_Uplink><ref alias=\"" + uplink.getName() + "\"/></X_Uplink>");
				buffer.append("\n");
			}
			int port = 1;
			for (CISystem system : systems) {
				buffer.append("\t\t<P_Port" + port+ "><ref alias=\"" + system.getName() + "\"/></P_Port" + port + ">");
				buffer.append("\n");
				port++;
			}
			buffer.append("\t</Router16Port>");
			return(buffer);
		}
	
	}
	
	class CISwitch extends CINetworkDevice {

		public CISwitch(String network, String name) {
			super(network, "Switch-" + name);
			// TODO Auto-generated constructor stub
		}
		
		public StringBuffer toXML() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\t<Switch16Port alias=\"" + name + "\">");
			buffer.append("\n");
			buffer.append("\t\t<A_Name>" + name +"</A_Name>");
			buffer.append("\n");
			buffer.append("\t\t<BD_Network><ref alias=\"" + network + "\"/></BD_Network>");
			buffer.append("\n");
			if (uplink != null) {
				buffer.append("\t\t<X_Uplink><ref alias=\"" + uplink.getName() + "\"/></X_Uplink>");
				buffer.append("\n");
			}
			int port = 1;
			for (CISystem system : systems) {
				buffer.append("\t\t<P_Port" + port+ "><ref alias=\"" + system.getName() + "\"/></P_Port" + port + ">");
				buffer.append("\n");
				port++;
			}
			buffer.append("\t</Switch16Port>");
			return(buffer);
		}
		
	}
	
	class CIServer extends CISystem {

		public CIServer(String network, String name) {
			super(network, "Server-" + name);
		}
		
		public StringBuffer toXML() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\t<Server alias=\"" + name + "\">");
			buffer.append("\n");
			buffer.append("\t\t<A_Name>" + name +"</A_Name>");
			buffer.append("\n");
			buffer.append("\t\t<BD_Network><ref alias=\"" + network + "\"/></BD_Network>");
			buffer.append("\n");
			buffer.append("\t</Server>");
			return(buffer);
		}
	}
	
}
