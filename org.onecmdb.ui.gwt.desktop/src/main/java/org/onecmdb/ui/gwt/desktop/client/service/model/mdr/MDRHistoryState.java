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
package org.onecmdb.ui.gwt.desktop.client.service.model.mdr;

/**
 * States that the MDR Histroy can take:
 * <br>
 * <ul>
 * <li>CREATED</li>
 *   <ul>
 * 	 <li>EXECUTING</li>
 * 			<ul>
 * 			<li>FAILED</li>
 * 			<li>READY</li>
 * 				<ul>
 * 				<li>COMMITTED</li>
 * 					<ul>
 * 					<li>OUT_OF_DATE</li>
 * 					</ul>
 * 				<li>REJECTED</li>
 *         		</ul>
 *         </ul>
 *    </ul>
 * </ul>
 *
 */
public class MDRHistoryState {
	public static String DELETED = "DELETED";
	public static String CREATED = "CREATED";
	public static String EXECUTING = "EXECUTING";
	public static String FAILED = "FAILED";
	public static String READY = "READY";
	public static String COMMITTED = "COMMITTED";
	public static String OUT_OF_DATE = "OUT_OF_DATE";
	public static String REJECTED = "REJECTED";
	
	public static String getHistoryTemplate() {
		return("MDR_HistoryEntry");
	}
}
