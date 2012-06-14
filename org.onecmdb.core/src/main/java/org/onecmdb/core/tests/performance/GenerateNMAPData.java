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
package org.onecmdb.core.tests.performance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.XmlGenerator;

public class GenerateNMAPData {
	static int nNetworks = 4;
	static int nHosts = 255;
	static int nPorts = 3;
	static int portOffest = 1024;
	
	public static void main(String argv[]) {
		List<CiBean> beans = new ArrayList<CiBean>();
		
		String networkStart = "10.0";
		for (int network = 0; network < nNetworks; network++) {
			String networkMask = networkStart +  "." + network;
			String networkName = networkMask + ".0-" + nHosts;
		
			CiBean netBean = new CiBean();
			netBean.setDerivedFrom("NMAP_Network");
			netBean.setTemplate(false);
			netBean.addAttributeValue(new ValueBean("name", networkName, false));
			netBean.setAlias("network-" + network);
			beans.add(netBean);
			
			for (int host = 1; host < nHosts; host++) {
				String hostName = networkMask + "." + host;
				CiBean hostBean = new CiBean();
				hostBean.setDerivedFrom("NMAP_Host");
				hostBean.setTemplate(false);
				hostBean.addAttributeValue(new ValueBean("hostname", hostName, false));
				hostBean.addAttributeValue(new ValueBean("ipv4Address", hostName, false));
				hostBean.addAttributeValue(new ValueBean("network", netBean.getAlias(), true));
				hostBean.setAlias("host-" + network + "-" + host);
				
				beans.add(hostBean);
									
				for (int port = portOffest; port < (nPorts + portOffest); port++) {
					CiBean portBean = new CiBean();
					portBean.setDerivedFrom("NMAP_TCP_Port");
					portBean.setTemplate(false);
					portBean.addAttributeValue(new ValueBean("port", "" + port, false));
					portBean.addAttributeValue(new ValueBean("host", hostBean.getAlias(), true));
					portBean.setAlias("tcpport-" + network + "-" + host + "-" + port);
					
					beans.add(portBean);
				}
			}
		}
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(beans);
		try {
			gen.generateXml(new File("d:/tmp/largeNMAPNetwork.xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
