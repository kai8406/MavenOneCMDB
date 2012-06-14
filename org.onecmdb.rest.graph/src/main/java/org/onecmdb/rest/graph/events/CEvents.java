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
package org.onecmdb.rest.graph.events;

public class CEvents {
	public static final int ITEM_MODIFIED = 1;
	public static final int RELATION_MODIFIED = 2;
	public static final int ITEM_SELECTED = 3;
	public static final int UPDATE_GRAPH = 4;
	
	public static final int REDRAW_GRAPH = 5;
	public static final int RELATION_ITEM_SELECTED = 6;
	public static final int STATUS_MSG = -1;

	
	public static final int NODE_SELECTED = 10;
	public static final int NODE_UNSELECTED = 11;
	
	public static final int RELATION_NODE_SELECTED = 20;
	public static final int RELATION_NODE_UNSELECTED = 21;
	
	public static final int INSTANCE_ITEM_SELECTED = 30;
	public static final int INSTANCE_SET_LAYOUT = 31;
}
