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
 * <p>
 * This interface can be retrieved from the CCB to request for changes on an
 * attribute. The actual changes occurs inside the CCB, and have no effect on
 * the in memory attribute.</p>
 * 
 * <p>All changes to an attribute or ci's is grouped in a transaction, which is
 * sent to the CCB. If one change is rejected the entire change transaction is
 * rolled backed, and no changes to the backend DataStore nor to the in memory
 * objects is performed.</p>
 * 
 * <p>Once a request for change has been performed the in-memory objects are not
 * up to date. This requires a refresh from the backend data store. Currently
 * there exists no notification mechanism that automatically flushes all stale 
 * in-memory objects.</p>
 * 
 * @see ICcb
 * @see ICmdbTransaction
 * 
 */
public interface IAttributeModifiable extends ICiModifiable {

	/**
	 * Update the value of the attribute.
	 * @param value Ne value to be set
	 */	
	public void setValue(IValue value);

	/**
	 * Update the type of this attribute.
	 * <br>
	 * WARNING: All values associated with this attribute will be nullified
	 *   
	 * 
	 * @param type
	 */
	public void setValueType(IType type);

	/**
	 * Update the reference type of this attribute.
	 * <br>
	 * WARNING: All values associated with this attribute will be nullified
	 *   
	 * 
	 * @param type
	 */
	public void setReferenceType(IType type);
	
	/**
	 * Update the maxOccurs policy.	  
	 * <br>
	 * WARNING: If the attribute have more values than maxOccurs
	 * the operation will be rejected.
	 * 
	 * @param maxOccurs -1 is unbound.
	 */
	public void setMaxOccurs(int maxOccurs);
	
	/**
	 * Update the minOccurs policy.	  
	 * 
	 * @param maxOccurs -1 (unbound) is not a valid value
	 */
	public void setMinOccurs(int minOccurs);
	

}
