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
 * This interface can be retrieved from the CCB to request for changes on a CI.
 * The actual changes is taken place <em>inside</em> CCB, and have no effect on
 * the in-memory CI.</p>
 * <p>
 * All changes to an attribute or CI's is grouped in a transaction, which is
 * sent to CCB. If one change is rejected the entire change transaction is
 * rolled backed, and no changes to the backend storage nor to the in-memory
 * objects is performed.
 * </p>
 * <p>
 * Once a request for changes has been performed the in-memory objects are not up
 * to date. This requires a refresh from the backend storage. Currently there
 * exists no notification mechanism that automatically flushes stale in-memory
 * objects.
 * </p>
 * 
 * @see ICcb
 * @see ICmdbTransaction
 * @see IAttributeModifiable
 */
public interface ICiModifiable {

	
	/**
	 * <p>Create an offspring of this CI.</p>
	 * <p>The offspring will not inherit any attributes unless the 
     * {@link #setIsBlueprint()} is called.</p>
     * 
	 * <p>TODO: Change the method to accept a boolean(template|instance)?</p>
	 * 
	 */
	public ICiModifiable createOffspring();

	/**
	 * Create a new attribute on this CI.
	 * 
	 * @param initAlias
	 * @param initType
	 * @param referenceType
	 * @param minOccurs
	 * @param maxOccurs 
	 * @param initValue
	 * @return
	 */
	public IAttributeModifiable createAttribute(String initAlias,
			IType initType, IType referenceType, int minOccurs, int maxOccurs,
			IValue initValue);

	/**
	 * Set the display name expression for a CI. The name can be an expression.
     * 
	 * <p>Examples of expressions:</p>
	 * 
	 * <p><b><code>${attribute.a1}<code></b> will evaluate to the value of the 
     * attribute with alias <code>a1</code>.<br>
	 * <b><code>${id}</code></b> will show the id of the CI.</p>
	 *   
	 * @param name
	 */
	public void setDisplayNameExpression(String name);

	/**
	 * Specifies the alias name for this CI.
	 * The alias can not contain any space or special characters.
	 * 
	 * @param name
	 */
	public void setAlias(String name);
	
	/**
	 * Specifies the description for this CI.
	 * The description may contain spaces and/or special characters.
	 * 
	 * @param name
	 */
	public void setDescription(String name);
	
	/**
	 * Modify a value of an existing attribute.
     *  
	 * <p>Will add an attribute if that is permitted, according to max/min 
     * occurrence.</p>
	 * 
	 * <p>The index specifies how many attributes with the alias name there
     * should exist. Attribute's have NO knowledge of index, if a specific
     * attribute's value should be altered, use 
     * {@link IAttributeModifiable#setValue(IValue)}.</p>  
	 *  
	 * @param alias
	 * @param index
	 * @param value
	 */
	public void setDerivedAttributeValue(String alias, int index, IValue value);

	/**
	 * Add an existing attribute to this CI. The attribute must exist in the
	 * parent (derived from) CI. If a <strong>new</strong> attribute is to be
     * added, use the {@link #createAttribute()} method instead.
	 *  
	 * @param alias the alias name of the attribute to add.
	 */
	public IAttributeModifiable addAttribute(String alias);

	/**
	 * <p>Mark this CI as a template or as an instance.</p>
	 * 
	 *<p>This dictates the inherit policy for attributes./p>
	 * 
	 * 
	 * @param value
	 */
	public void setIsBlueprint(boolean value);

	/**
	 * Delete this CI permanently from OneCMDB.
	 * <p><b>WARNING</b>: All offsprings of this CI will also be deleted.</p>
	 */
	public void delete();
}
