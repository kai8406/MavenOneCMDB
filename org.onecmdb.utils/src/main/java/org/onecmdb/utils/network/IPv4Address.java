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

public class IPv4Address {
	
	
	private static final int BINARY = 1;
	private static final int DECIMAL = 2;
	private static final int HEX = 4;
	
	int ipv4Address = 0;
	
	public IPv4Address(String adr) {
		this.ipv4Address = parseIPAddress(adr);
	}
	
	public IPv4Address(int adr) {
		setIpv4Address(adr);
	}
	
	public int getIpv4Address() {
		return(ipv4Address);
	}
	
	public void setIpv4Address(int ipv4Address) {
		// TODO: validate.
		this.ipv4Address = ipv4Address;
	}

	public int parseIPAddress(String adr) {
		if (adr == null) {
			throw new IllegalArgumentException("Can't handle null ip address!");
		}
		String octets[] = adr.split("\\.");
		if (octets.length != 4) {
			throw new IllegalArgumentException("Invalid ip address '" + adr + "'. Need xxx.xxx.xxx.xxx");
		}
		int ip = 0;
		int offset = 24;
		for (int i = 0; i < octets.length; i++) {
			int octet = Integer.parseInt(octets[i]);
			if (octet < 0 || octet > 255) {
				throw new IllegalArgumentException("Invalid ip address '" + adr + "'. Octet '" + i + "' range 0..255");
			}
			ip = ip | (octet << offset);
			offset -= 8;
		}
		return(ip);
	}


	public String toBinaryString() {
		return(toString(BINARY));
	}
	
	public String toString() {
		return(toString(DECIMAL));
	}
	public String toHexString() {
		return(toString(HEX));
	}
	
	public String toAllString() {
		StringBuffer b = new StringBuffer();
		b.append(toString() + "\n");
		b.append("\t" + toHexString() + "\n");
		b.append("\t" + toBinaryString() + "\n");
		return(b.toString());
	}
	public String toString(int type) {
		StringBuffer b = new StringBuffer();
		int offset = 24;
		for (int i = 0; i < 4; i++) {
			Integer octet = (ipv4Address >> offset) & 0xff;
			switch(type) {
			case BINARY:
				b.append(asBinary(octet, 8));
				break;
			case DECIMAL:
				b.append(Integer.toString(octet));
				break;
			case HEX:
				b.append(Integer.toHexString(octet));
				break;
			}
			if (offset > 0) {
				b.append(".");
			}
			offset -= 8;
		}
		return(b.toString());
	}

	public String asBinary(int value, int bits) {
		StringBuffer b = new StringBuffer();
		for (int i = (bits-1); i >= 0; i--) {
			if (((value >> i) & 0x1) == 0x1) {
				b.append("1");
			} else {
				b.append("0");
			}
		}
		return(b.toString());
	}
	
	/**
	 * If this Address is a network mask, then it can be expressed as a CIDR /nn.
	 * @return
	 */
	public int getCIDR() {
		int cidr = 0;
		for (int i = 31; i >= 0; i--) {
			if ((this.ipv4Address & (1<<i)) == 0) {
				break;
			}
			cidr++;
		}
		return(cidr);
	}
	public static void main(String argv[]) {
		IPv4Address ip = new IPv4Address(argv[0]);
		System.out.println(ip.toString());
		System.out.println("\t" + ip.toHexString());
		System.out.println("\t" + ip.toBinaryString());
		
		IPv4Network n1 = new IPv4Network(argv[0], argv[1]);
		System.out.println(n1.toString());
		
		for (IPv4Address a : n1.getNetworkAddresses()) {
			System.out.println(a.toString() + " " + n1.isInNetwork(a) + "");
		}
		
		IPv4Network n2 = new IPv4Network("192.168.1.3", 24);
		System.out.println(n2.toString());
			
	}

	

}
