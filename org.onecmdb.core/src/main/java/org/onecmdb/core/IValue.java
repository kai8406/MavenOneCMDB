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

import java.io.OutputStream;
import java.util.Comparator;

public interface IValue {

    /**
     * <p>A simple, basic, comparator, which can be used whenever value items 
     * are to be compared.</p>
     * 
     * <p>The comparison is based on the value's 
     * {@link IValue#getDisplayName}.</p>
     */
    public static final Comparator<IValue> VALUE_COMPARATOR 
        = new Comparator<IValue>() {

            public int compare(IValue o1, IValue o2) {
                String n1 = (o1 != null) ? o1.getDisplayName() : null;
                String n2 = (o2 != null) ? o2.getDisplayName() : null;
                if (n1 == n2) 
                    return 0;
                if (n1 != null)  
                    return n2 != null ? +n1.compareTo(n2) : 1;
                   
                if (n2 != null) {
                     return n1 != null ? -n2.compareTo(n1) : -1;
                }
                return 0;    
                
            }
    };
    
    
	/** The <em>type</em> of this value */
	IType getValueType();

	/**
	 * The string representation of this value.
	 * 
	 * @return A string representing this value. The empty string may be
	 *         returned when there is no <em>actual</em>value set--never
	 *         <code>null</code>!
	 */
	String getAsString();

	OutputStream asOutputStream();

	/**
	 * Returns a string representation of the <em>value</em> held by this
	 * value.
	 * 
	 * @return
	 */
	String getDisplayName();


    /** 
     * <p>Query if this value represents the <code>null</code> value, according
     * to the the type backed by this value.</p>
     * 
     * @return A boolean indicating if the this value actually should be 
     * interpreted as the <code>null</code> value.
     */
    boolean isNullValue();
    
    /**
     * Check if a IValue is a ICi.
     * 
     * @return true if the Value is a ICi
     */
    boolean isComplex();    
        

	String getIcon();
	
	/**
	 * The java object representation of this value if there exists any.
	 * If no correct java object represetation exists null is retured. 
	 * 
	 * @return a java object representaion of this value. <code>null</code> if no such exists. 
	 */
	Object getAsJavaObject();
}
