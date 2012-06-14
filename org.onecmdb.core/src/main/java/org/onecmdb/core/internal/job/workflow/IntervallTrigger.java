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
package org.onecmdb.core.internal.job.workflow;

public class IntervallTrigger extends Trigger {
	private long startDelay;
	private long repeateIntervall;
	private long repeateCount;
	
	
	
	public long getRepeateCount() {
		return repeateCount;
	}

	public void setRepeateCount(long repeateCount) {
		this.repeateCount = repeateCount;
	}

	public long getRepeateIntervall() {
		return repeateIntervall;
	}
	
	public void setRepeateIntervall(long repeateIntervall) {
		this.repeateIntervall = repeateIntervall;
	}
	
	public long getStartDelay() {
		return startDelay;
	}
	
	public void setStartDelay(long startDelay) {
		this.startDelay = startDelay;
	}
	
}
