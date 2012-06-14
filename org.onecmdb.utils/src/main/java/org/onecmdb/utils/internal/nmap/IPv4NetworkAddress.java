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
package org.onecmdb.utils.internal.nmap;

public class IPv4NetworkAddress {

	private String mask;
	private int value;
	
	
	
	public IPv4NetworkAddress(String mask) {
		this.mask = mask;
		String bytes[] = this.mask.split("\\.", 4);
		// must be 4.
		value = 0;
		
		int v1 = (int)((Integer.parseInt(bytes[0]) << 24) & 0xff000000);
		int v2 = (int)((Integer.parseInt(bytes[1]) << 16) & 0x00ff0000);
		int v3 = (int)((Integer.parseInt(bytes[2]) << 8)  & 0x0000ff00);
		int v4 = (int)((Integer.parseInt(bytes[3]))       & 0x000000ff);
		
		System.out.println(Integer.toHexString(v1));
		System.out.println(Integer.toHexString(v2));
		System.out.println(Integer.toHexString(v3));
		System.out.println(Integer.toHexString(v4));
		
		this.value = v1 | v2 | v3 | v4;
		System.out.println(Integer.toHexString(this.value));
		
	}
	
	public int getBits() {
		int count = 0;
		for (int i = 31; i >= 0; i--) {
			if (isBitSet(i)) {
				count++;
			}
		}
		return(count);
	}
	
	public boolean isBitSet(int i) {
		return((value & (1 << i)) != 0);
	}
	
	
	public static void main(String argv[]) {
		IPv4NetworkAddress address = new IPv4NetworkAddress("255.255.255.0");
		System.out.println(address.getBits());
	}
	
}
