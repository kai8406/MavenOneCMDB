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

import java.util.List;

/**
 * <p>A path keeps a notion of <em>elements</em>, ordered according to its 
 * insertion order.</p>
 * 
 * @param <T>
 *            The actual element represented by the path
 */
public interface IPath<T> extends Iterable<T> {

	/**
	 * Express the path as a list, which a number of utility framework desires
	 * ;-)
	 * 
	 * @return A list representing the elements in this path. One must not 
     * expect the returned list to be mutable.
	 */
	List<T> getList();

	/** 
     * Add a single element to the end of this path 
     *
     * @param element The element to add.
     */
	void addElement(T element);

	/** 
     * Append a specific path to this path. 
     *
     * @param path The path to append
     */
	void addPath(IPath<T> path);


	/** 
	 * Return the <em>first</em> element from this path *
	 * @return The first element in this path 
	 */
	T getRoot();

    /** 
     * Return the <em>last</em> element from this path *
     * @return The last element in this path 
     */
	T getLeaf();


    /** 
     * Extract all elements, except the first one, the root, out of this path.
     *  
     * @return A <b>new</b> path consisting of all elements, except the very
     * first one, from this path.
     */
    IPath<T> getAllButRoot();
    
    /** 
     * Extract all elements, except the last one, out of this path.
     *  
     * @return A <b>new</b> path consisting of all elements, except the very
     * last one, from this path.
     */
    IPath<T> getAllButLeaf();
    

    /**
     * Reverse the order of elements in this path.
     * @return A <b>new</b> path consisting of all elements, in reversed order,
     * as this path.
     */
    IPath<T> getReversed();

    /** 
     * Return the number of individual path elements contained by this path.
     * @return Number of elements in this path
     */
    int getSize();
    
    /** 
     * Construct a string representation of this path, normally 
     * consisting of each path element separated by a special 
     * path separator. 
     * @return
     */
    String toString();    

    /** 
     * The sequence of chars separating each path element, when the
     * path is seen as a string. 
     *
     * @return The sequence separating path elements
     * 
     * @see #toString()
     */
    String getPathSepearator();

    /** 
     * Test if this path is a <em>parent</em> path of another path, i.e. 
     * the passed one may be longer, but both must have the same beginning. 
     * @param ancestor The path to test
     * @return
     */
    boolean isParent(IPath<T> ancestor);

    /** 
     * Test if this path is a (direct) sibling to another path, i.e. all but
     * the leaves are the same.
     * @param ciPath
     * @return
     */
    boolean isSibling(IPath<IType> sibling);
    
}

