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
package org.onecmdb.web.graphs;

public enum EGraphRelation {
    
    /** used to indicate all types */
    ALL,
    
    /** outgoing relations */
    OUTGOING, 
    
    /** incoming relations */
    INGOING;
    
    /** 
     * Return a user interface friendly text representation of this type
     * 
     * @return A string supposed to be used to represent this type in written
     * language.
     */
    public String getString() {
        String[] parts = super.toString().split("_");
        String s = "";
        for (String p: parts) {
            if (!s.equals("")) s +=" ";
            s += p.charAt(0) + p.substring(1).toLowerCase();
        }
        return s;
    }
   
    /** 
     * Wrapper for {@link #name()} to be able to use this type in a bean 
     * environment.
     * @return The literal name of this enum. 
     */
    public String getName() {
        return name();
    }
}
