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

import org.onecmdb.core.internal.model.ItemId;

public interface IReferenceService extends IService {

	ICi getRootReference();

	/**
	 * <p>Retrieve all CIs that are <em>pointing to</em> the given CI.
	 * This method is a shortcut: another way is to use getReferrers(),
	 * and use the methods from IReference to navigate the reference.</p>>
	 * 
	 * @param ci a ICi
	 * @return Set of ICi that are pointing to the given ICi
	 */
	Set<ICi> getOriginCiReferrers(ICi ci);

	/**
	 * Retrieve all CIs that are "pointing to" the given CI using a specific type of reference.<br>
	 * This method is a short-cut: another way is to use getReferrers()<br>
	 * and use the methods from IReference to navigate the reference.<br>
	 * 
	 * @param ci a ICi
	 * @param refType The reference type to include, or <code>null</code> for
     * all.
	 * @return Set of ICi that are pointing to the given ICi using references of the given type
	 */
	Set<ICi> getOriginCiReferrers(ICi ci, ICi refType);

	/**
	 * Retrieve all references that point to the specified CI.
	 * 
	 * @param ci
	 * @return
	 */
	Set<IReference> getReferrers(ICi ci);
	
		
	/**
	 * Retrieve the <em>reference type</em> between a CI and a referrer.
	 * 
	 * @param ci
	 * @param referrer
	 * @return
	 */
	IType getReferrerType(ICi ci, ICi referrer);
	
	/**
	 * Retrieve all known reference types the system, that are offsprings of a 
     * certain <em>base path</em>.
	 * If basePath is null, all references are retrieved.
	 * 
	 * @return
	 */
	Set<IType> getAllReferences(IPath<String> basePath);
	
	/**
	 * Find the reference type with a specific id.
	 *  
	 * @param id
	 * @return null if not exists.
	 */
	IType getRefType(ItemId id);
	
	/**
	 * Find the reference type with a specific alias.
	 *  
	 * @param alias
	 * @return null if not found.
	 */
	IType getRefType(String alias);
	

}
