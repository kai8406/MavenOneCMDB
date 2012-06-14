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

/**
 * <p>A value provider is loosely coupled to an (external) <em>resource</em>
 * from where information can be retrieved.</p>
 * 
 * <p>The provider knows the configuration items, and the attributes, where
 * information fetched should be put.</p>
 * 
 * <p>A <em>worker</em> is used to query all providers for new values, according
 * to their update schemas, and policies.</p>
 */
public interface IValueProvider extends IAdaptable {

	public abstract IValue fetchValueContent();

	/**
	 * Gives a hint if the current value is valid or not. 
	 * 
	 * @return
	 */
	public abstract boolean isValid();

}