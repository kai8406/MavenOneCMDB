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
package org.onecmdb.utils.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IPv4Network {
	IPv4Address networkAdr;
	IPv4Address broadcastAdr;
	IPv4Address subnetMask;
	
	public IPv4Network(IPv4Address adr, IPv4Address mask) {
		this.subnetMask = mask;
		this.networkAdr = new IPv4Address(adr.getIpv4Address() & mask.ipv4Address);
		
		int hostMask = ~mask.getIpv4Address();
		this.broadcastAdr = new IPv4Address(networkAdr.getIpv4Address() + hostMask);
		
	}
	
	public IPv4Network(String ip, String mask) {
		this(new IPv4Address(ip), new IPv4Address(mask));
	}
	
	public IPv4Network(IPv4Address addr, int cidr) {
		this(addr, computeMaskFromOffset(cidr));

	}
	
	public IPv4Network(String addr, int cidr) {
		this(new IPv4Address(addr), computeMaskFromOffset(cidr));

	}
	
	private static IPv4Address computeMaskFromOffset(int offset) {
		int mask = 0;
		for (int i = 0; i < offset; i++) {
			mask = mask | (1 << (31 - i));
		}
		return(new IPv4Address(mask));
	}

	public IPv4Network(String cidr) {
	}
	
	
	public List<IPv4Address> getNetworkAddresses() {
		List<IPv4Address> range = new ArrayList<IPv4Address>();
		for (int adr = (networkAdr.getIpv4Address()+1); adr < broadcastAdr.getIpv4Address(); adr++) { 
			range.add(new IPv4Address(adr));
		}
		return(range);
	}
	
	public IPv4Address getFirstNetworkAddress() {
		return(new IPv4Address(networkAdr.getIpv4Address()+1));
	}
	public IPv4Address getLastNetworkAddress() {
		return(new IPv4Address(broadcastAdr.getIpv4Address()-1));
	}
	
	public boolean isInNetwork(IPv4Address adr) {
		return(adr.getIpv4Address() >= getFirstNetworkAddress().getIpv4Address() &&
				adr.getIpv4Address() <= getLastNetworkAddress().getIpv4Address());
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Network Mask: " + subnetMask.toString() + " or /" + subnetMask.getCIDR());
		b.append("\n");
		b.append("Network   Addr:" + networkAdr.toString());
		b.append("\n");
		b.append("Broadcast Addr:" + broadcastAdr.toString());
		b.append("\n");
		b.append("Addresses: " + ((broadcastAdr.getIpv4Address()-1) -  networkAdr.ipv4Address));
		b.append("\n");
		return(b.toString());
	}
	
	public static void main(String argv[]) {
		IPv4Network adr = new IPv4Network("192.168.1.34", "255.255.255.0");
		System.out.println("Firts IP=" + new IPv4Address(adr.getFirstNetworkAddress().ipv4Address + 1));
		
	}
	
}
