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

import java.util.ArrayList;
import java.util.Collection;

import org.onecmdb.core.example.MaxMinAvg;

public class ProfileData {
	public String name;
	public long start;
	public long stop;
	public long dt;
	public long startMem;
	public long stopMem;
	public long dm;
	public int  calls = 0;
	
	MaxMinAvg avgCalls = new MaxMinAvg();
	MaxMinAvg avgDt = new MaxMinAvg();
	public MaxMinAvg avgStart = new MaxMinAvg();
	public MaxMinAvg avgStop = new MaxMinAvg();
	MaxMinAvg avgDm = new MaxMinAvg();
	MaxMinAvg avgMem = new MaxMinAvg();
	
	long used = 0;
	
	private ArrayList sons = new ArrayList();
	private ProfileData parent = null;
	
	public void setParent(ProfileData data) {
		this.parent = data;
	}
	public ProfileData getParent() {
		return(this.parent);
	}
	public void addSonData(ProfileData data) {
		this.sons.add(data);
		data.setParent(this);
	}
	
	
	public Collection getSons() {
		return(this.sons);
	}
	
	
	public void addSame(ProfileData data) {
		avgCalls.addValue(data.calls);
		avgDt.addValue(data.dt);
		avgDm.addValue(data.dm);
		avgStart.addValue(data.start);
		avgStop.addValue(data.stop);
		
		avgMem.addValue(data.stopMem);
		used++;
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProfileData)) return false;
		
		final ProfileData profileData = (ProfileData) o;
		
		if (name != null ? !name.equals(profileData.name) : profileData.name != null) return false;
		
		return true;
	}
	
	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}
	
}
