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

import java.io.InputStream;

/**
 * An attribute is said to keep a <em>value</em>, which itself is
 * polymorphic:
 * <ul>
 * <li>A <em>primitive</em> which maps to the basic types from XML Schema.
 * <li>Another, already existing, configuration item.
 * </ul>
 * 
 * <p>When the attribute's value is asked for, the retrieval is either a 
 * configuration item, making the attribute merely a pointer. By changing the
 * value, another configuration item is pointed at.</p>
 * 
 * <p>An attribute may have nested attributes, because an attribute <em>is a</em>
 * configuration item, making it possible to make use of attributes in a way
 * familiar with C structs.</p>
 * 
 * <p>Note that this interface describes the <em>Read Only</em> side of the
 * attribute. If an attribute is to be modified use the 
 * {@link IAttributeModifable} interface instead.</p>
 * 
 * @see IAttributeModifiable
 */
public interface IAttribute extends ICi {


	/**
	 * <p>An attribute that has a value have a <em>type</em>, which can be 
     * asked for via this method.</p>
     * 
	 * <p>Two different types exists:</p>
	 * <ol>
	 * <li>Simple</li>
	 * <li>Complex</li>
	 * </ol>
     * 
	 * <p>Simple means that the actual value is stored local to the attribute,
     * while Complex defines that the attribute actually <em>points to/em>
     * another configuration item.</p>
	 * 
	 * <p> Simple types:</p>
	 * <ul>
	 * <li> String </li>
	 * <li> Boolean </li>
	 * <li> Integer </li>
	 * <li> .... </li>
	 * </ul>
	 * 
	 * <p>For a all simple types see 
     * {@link org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory}
	 * </p>
	 * 
	 * <p>All configuration items used in oneCMDB are considered to be complex
	 * types (Blueprints) and are therefore <em>referenceble</em>.</p>
	 * 
	 * <p>How the reference is handled is defined by the <em>reference type</em>
     * from  {@link IAttribute}.</p>
	 * 
	 * @return The type represented by this attributes value
	 * 
	 * @see org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory
	 * @see #getReferenceType()
	 */
	IType getValueType();

	/**
	 * <p>This describes the <em>type of reference</em> used when this attribute
     * is referencing another CI.</p>
     * 
	 * <p>The referenceType points out a CI to be the blueprint of the reference
	 * instance. This means that the reference can contain attributes that 
     * describes the reference.</p>
	 * 
	 * <p>If the refernceType is null the value is referenced directly, meaning
	 * that <b>NO</b> attribute's can be added to the reference.</p>
	 * 
	 * <p>Examples:</p>
	 * <ul>
	 * <li>referenceType == null</li>
	 * <ul>
	 * <li>CI{A.value} --> ICI(IValue)<br>
	 * </li>
	 * </ul>
	 * <li>referenceType != null</li>
	 * <ul>
	 * <li>CI{A.value} --> ReferenceCi{A.target} --> Ci(IValue)</li>
	 * </ul>
	 * </ul>
	 * 
	 * @return The type of Reference this attribute is referencing a ICi with.
	 * @see #getReference()
	 */
	IType getReferenceType();

	/**
	 * <p>Returns the CI the attribute refers to or NULL if the attribute does 
     * not refer any CI.</p>
     * 
	 * <p>An attribute <em>refers to a CI</em> if:</p>
     * <ul>
	 * <li>the attribute has a CI as value</li>
	 * <li>the attribute has a reference type</li>
     * </ul>
	 * 
	 * @return the CI instance describing the reference, or <code>null</code>
     * if no reference exists.
     * 
	 * @see #getReferenceType
	 * 
	 */
	ICi getReference();

	/**
	 * Fetch the underlying value represented by this attribute and put it into
	 * a memory object. By querying for the {@link #getType type}, an hint of
	 * the intention should be revealed.
	 * 
	 * @return The value as an object, or <code>null</code> if no value is
	 *         currently not set.
	 * 
	 */
	IValue getValue();

	/**
	 * An Attribute is always owned by a CI. 
     * @return The owner CI of this  attribute.
	 */
	ICi getOwner();

	/**
	 * Fetch an input stream of the underlying value. In case the value
	 * represents a large (in memory) value this method may be preferred
	 * compared to {@link #getValue}.
	 * 
	 * @return An input stream connected to the underlying value
	 */
	InputStream getInputStream();

	/**
	 * <p>Constraint on the minimum number of attributes bound to CI 
     * <em>implementing</em>  this attribute.</p>
     * 
	 * <p>TODO: Move this to the policy!
     * 
     * @return Number of minimum number off attributes expected to be found
     * on the CI owning this attribute.
	 */
	public int getMinOccurs();

	/**
	 * Constraints on how many attributes of this type an offspring can 
     * have, where the value <code>-1</em> means unlimited.<br>
	 * TODO: Move this to the policy!
     * @return Number of maximum attributes (inclusive), or <code>-1</code>
     * meaning unlimited.
	 */
	public int getMaxOccurs();

    
    /**
     * Return a value selector containing all values, which, for the moment,
     * can be set on this attribute.
     */
    public IValueSelector getValueSelector();
    
    /** 
     * Return a type selector containing all types, from where new values to
     * this attribute can be constructed from.
     * @return
     */
    public ITypeSelector getTypeSelector();
    
    /**
     * Determine if the value of tjhis attribute is complex. 
     * @return
     */
    public boolean isComplexValue();

    /**
     * Check if this attribute is derived.
     * @return
     */
	boolean isDerived();
    
}