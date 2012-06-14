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
package org.onecmdb.core.example;

import java.io.Serializable;

/**
 * Description of MaxMinAvg
 * 
 * User: niklas Date: Jun 2, 2003 Time: 12:49:07 PM
 */
public class MaxMinAvg implements Serializable {
	static final long serialVersionUID = 100000L;

	double max = Long.MIN_VALUE;

	double min = Long.MAX_VALUE;

	double avg = 0;

	long added = 0;

	double totalValue = 0;

	private String unit = "";

	private double lastValue = 0;

	public void addValue(double l) {
		avg = avg * added;
		added++;
		avg = (avg + l) / (double) added;

		if (l < min) {
			min = l;
		}
		if (l > max) {
			max = l;
		}
		totalValue += l;
		lastValue = l;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getTotal() {
		return (totalValue);
	}

	public double getLastValue() {
		return (lastValue);
	}

	public double getAvg() {
		return (avg);
	}

	public double getMax() {
		return (max);
	}

	public double getMin() {
		return (min);
	}

	public long getAdded() {
		return (added);
	}

	public String toString() {
		if (added == 0) {
			return ("No values added");
		}
		return ("Avg=" + avg + "[" + min + "," + max + "]" + unit
				+ ", TotalSum=" + totalValue + unit + ", AddedValus=" + added
				+ ", (Max-Min)=" + (max - min) + unit);
	}
}
