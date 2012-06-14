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
 * <p>The IReference is used to specify the relations between CIs.</p>
 * <p>A IReference specifies:</p>
 * <ol>
 * <li> one or more CIs being the 'source' of the relation (for example an
 * attribute)</li>
 * <li> exactly one CI begin the target of the relation</li>
 * </ol>
 * <p>Example Usage:</p>
 * <pre>
 *  CI(Source)
 *  A.value --> IReference --> (Target)CI
 *  </pre>
 */
public interface IReference extends ICi {
	/**
	 * Retrieve the target ICi for this reference.
	 * @return
	 */
	public ICi getTarget();
	
	/**
	 * <p>Retrieve the CIs that are sources for this reference. (The actual
     * sources are the attributes that points to the the CI as a value.)</p>
	 *  
	 * @return
	 */
	public Set<ICi> getSourceCis();
	
	/**
	 * Retrieve the attributes that are using this reference to retrieve
	 * it's referenced value.
	 * 
	 * @return
	 */
	public Set<IAttribute> getSourceAttributes();
	
}
