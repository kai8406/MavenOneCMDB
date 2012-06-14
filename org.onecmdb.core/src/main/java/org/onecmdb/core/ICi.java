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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.onecmdb.core.internal.model.ItemId;

/**
 * <p>The basic module, ICiServie, knows about all CIs ever defined. Defining 
 * CIs starts by creating offsprings from the single universally defined CI
 * <em>identified as</em> 'ROOT'.</p>
 * 
 * <p>The IModelService makes use of the universally defined 'MODEL' CI, which
 *  is an offspring off 'ROOT'. The 'MODEL' is given the notion of an container
 * which gives it the possibility to attach, other objects, i.e. not create
 * offsprings, into the model. These other objects are offsprings from other,
 * known, <em>hives</em>, fetched via other the model service itself, or
 * other attached services.</p>
 * 
 * <p>The gathering of these fundamental services builds up the oneCmdb 
 * application!</p>
 * 
 */
public interface ICi extends IType, IValue, IAdaptable {

	/**
	 * <p>The beginning of an <em>interpreted</em> expression language which may
	 * be used to extract information from this configuration item.</p>
     * 
	 * <p>Via an expression in the form:</p>
	 * <blockquote>
	 * <code>[<em>text</em>]${<em>token</em>[.<em>token</em>]}[<em>text</em>]</code>...
	 * </blockquote>
     * 
	 * <p>Text can be interspersed with attribute values from this configuration
	 * item</p>
     * 
	 * <p>The token <em>points</em> out attributes. With punctation, nested
	 * attributes may be reached.
	 * 
	 * @return The currently attached display name expression, as a string. 
	 */
	String getDisplayNameExpression();

	/**
	 * <p>Fetch all attribute definitions for this ci. All 
	 * attribute even attribute defined in the parent ci, that
	 * have minOccurs 0. </p>
	 * 
	 */
	Set<IAttribute> getAttributeDefinitions();

	/**
	 * <p>Fetch the attribute definitions for a alias that is 
	 * defined by this ci or it parent(s).</p>
	 * 
	 */
	IAttribute getAttributeDefinitionWithAlias(String alias);
	
	/**
	 * <p>Fetch all attributes attached to this configuration item. Note: to make
	 * use of the attributes, the client must be able to recognize the
	 * attributes, either by its <em>name</em>, or the <em>type</em>, to
	 * make any clever decision regarding the CI.</p>
	 * 
	 * @return A set view of the attached attributes. The caller must not assume
	 *         that the set returned can be modified.
	 */
	Set<IAttribute> getAttributes();
	
	
	/**
	 * <p>Retrieve all IAttribute with alias name. Even if the attribute is 
     * defined to one have one occurrences  a list is returned.</p>
	 *  
	 * @param alias The alias name to ask for.
     * 
	 * @return A list of attached attributes matching the specfied alias.
	 */
	List<IAttribute> getAttributesWithAlias(String alias);
	

    /**
     * Retrieve the attribute with a specific identifier. 
     *  
     * @param attrId The identifier for the attribute to retrivie
     * @return The attribute searched for, or <code>null</code> if it does not
     * exist.
     */
    IAttribute getAttributeWithId(ItemId attrId);
    
    
    /**
	 * <p>Fetch all avaliable attributes that can be added to this configuration
	 * item.</p>
     * 
	 * <p>The method will match how many attributes exists in the configuration
     * item with the {@link IAttribute#getMaxOccurs} on the attribute.</p> 
     *
	 * <p>Will also look in the configuration item <em>derived from</em> to 
     * find not-yet-used attributes.</p>
	 * 
	 * @return a list of attribute that can be added.
	 */
	List<IAttribute> getAddableAttributes();

	// {{{ ``design'' methods, break out to separate interface?

	/**
	 * Return the configuration item this configuration item was
	 * <em>derived</em> from, giving back the <em>inheritage</em> order,
	 * or NULL if the CI was not derived from another CI.
	 */
	ICi getDerivedFrom();

	/**
	 * All items derived from this configuration item, one level down.
	 * 
	 * @return All offsprings represented in a set. In cases when there are no
	 *         offsprings, the empty set must be returned. Callers must not
	 *         assume the set returned is modifiable.
	 */
	Set<ICi> getOffsprings();


	/**
	 * Retrieve the description of this CI. The String is for the moment not
	 * formatted in any way.
	 */ 
	String getDescription();

	/**
	 * Return the template path for this ci.
	 * @return
	 */
	String getTemplatePath();
	
	/**
	 * Return the security group this ci belongs to.
	 */
	ICi getGroup();

	boolean isDerivedFrom(ICi rootTrigger);

	IPath<String> getDerivedPath();

	public Date getCreateTime();
	public Date getLastModified();

}
