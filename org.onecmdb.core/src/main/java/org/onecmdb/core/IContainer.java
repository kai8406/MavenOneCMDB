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
package org.onecmdb.core;

import java.util.Set;

/**
 * <p>Containers are the base element to access objects within the system. The
 * container itself acts as a <em>shell</em> on top of the <em>contained</em>
 * elements.</p>
 * 
 * <p>A container <em>only</em>knows of one level of items, making the ownership
 * clear, and simple. In case an owned item itself is a container it may be
 * queried.</p>
 * 
 * @author nogun
 * 
 */
public interface IContainer extends ICi {

	/**
	 * Access the owned items, which by definition is a set--the same object can
	 * only occur once.
     * 
	 * <p>One should assume the collection returned is unmodifiable.</p>
	 * 
	 */
	Set<ICi> getItems();

	/**
	 * Put a new object into this container. If the object already exists,
	 * the function immediately returns.
	 * 
	 * <p>Events spawned:
	 * <ul>
	 * <li>{@link ItemPutEvent}</li>
	 * </ul>
	 */
	void putItem(ICi item);

	/** find named */
	Set<ICi> findByQuery(Class type, String string);


}
