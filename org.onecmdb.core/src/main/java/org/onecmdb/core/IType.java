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
import java.util.Set;

import org.onecmdb.core.internal.model.ItemId;

/**
 * A <em>type</em> defines the expected behavior and aspects to an entity
 * which is related to it, i.e. by investigating the type, one should be able to
 * understand how to use the related entity.
 */
public interface IType {

    /**
     * A type is identified by its ID (or <em>hash</em>), which once set never
     * changes. Note that there is no support to set the in any interface. Each
     * implementation must deal with the details regarding this issue.
     * 
     * @param newId
     */
    ItemId getId();
    
	/**
	 * Describes this type in a unambiguous way, in a user understandable
	 * representation.
	 * 
	 * @return The string representation of this type.
	 */
	public String toString();

    /**
     * Is this type is regarded as a <em>blueprint</em>. Blueprints can
     * create/have offsprings. If the CI is not a blueprint it is regarded as a
     * <em>instance</em>.
     * 
     * @return a boolean indicating the types intention
     */
    boolean isBlueprint();
    
    
	/**
	 * This is a unique name within the scope of a <em>namespace<em>
	 * Define the difference between the getAlias() and getUniqueName()!!!! 
	 * @return
	 */
	public String getUniqueName();

    /**
     * The alias name is a more human readable representation of an id. An id
     * can NEVER change. An alias can!
     * 
     * @return A human understandable name (unique?) representation of this 
     * configuration item.
     * 
     * @see #getDisplayName
     */
    String getAlias();

    
    /**
     * <p>A display name is supposed to be used to present a textual name for 
     * this configuration item. Implementations may use some kind of expression
     * to actually generate this value.</p>
     * 
     * @return A string representing the name, which must not be
     *         <code>null</code>, nor the empty string!
     * @see #getDisplayNameExpression()
     */
    String getDisplayName();

    
    
	/**
	 * An identifier of an icon, representing this type. The identifier should,
	 * in its easiest form, be a simple identifier, which is resolved to an
	 * actual resource when needed. In case the identifier can be interpreted as
	 * an URL, no resolving is needed.
	 * 
	 * @return An icon identifier as a simple name, a full URL, or 
     * <code>null</code> if no icon is set.
	 * 
	 */
	public String getIcon();

	/**
	 * Retrieve the description of this IType. The String is for the moment not
	 * formatted in any way.
	 */ 
	String getDescription();

	/**
	 * Parses, that is <em>wraps</em> (converts), a string into a 
     * <strong>new</strong> value.
	 * 
	 * @param s
	 *            The string to be converted. Passing <code>null</code> means
	 *            <em>reset</em>.
	 * @return A new value represented by the string passed
	 * @throws May
	 *             parse a runtime exception in case of an error, presumable a
	 *             {@link Pars....}
	 */
	public IValue parseString(String s);

	public IValue parseInputStream(InputStream in);
    
	public IValueSelector getValueSelector();

	/**
	 * Used to cast an exiting value into a <strong>new</strong> value
	 * represented by this type.
	 * 
	 * @param string
	 */
	public IValue fromValue(IValue value);

	/**
	 * Validate if a specific value can be used as a value this type
	 * 
	 * @param v
	 * @return An error object describing the validation error, or
	 *         <code>null</code> if no errors exists.
	 */
	public ErrorObject validate(IValue v);


	/**
     * Constructs the <em>empty</em> value for this type.
     * @return The value representing <em>empty</em> for this type. 
	 */
    IValue getNullValue();

    
    /**
     * Fetch the <em>path</em> this configuration item was created through,
     * more or less by using {@link #getDerivedFrom()} until the root is hit.
     * 
     * <p>The <em>top</em> element should be considered the first item in the
     * path, and the last, the leaf, is this item itself.</p>
     */
    IPath<IType> getOffspringPath();
    
    
    /**
     * Retrieve all offspring types that are based on this type, meaning we 
     * fetch all descendants, this type excluded.
     * 
     * <p><b>Note:</b> For now, the actual position in the type hierarchy is 
     * lost using this method. In case this is important one can always use 
     * {@link ICi#getOffspringPath()} to retrieve the path/level a type is
     * located.
     * 
     * 
     * @return an empty set if no offsprings
     */
	Set<IType> getAllOffspringTypes();
}
