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

import org.onecmdb.core.utils.HashCodeUtil;

/**
 * An unmutable object representing <em>multiplicity</em>.
 */
public class Multiplicity {

    /** the value representing an unbound upper limit */
    public final static int UNBOUND = -1;
    
    /** multiplicity representing (0,0) */
    public final static Multiplicity ZERO = new Multiplicity(0,0);

    /** multiplicity representing (1,1) */
    public final static Multiplicity ONE = new Multiplicity(1,1);

    /** multiplicity representing (0,1) */
    public final static Multiplicity OPTIONAL = new Multiplicity(0,1);

    /** multiplicity representing (0,unbound) */
    public final static Multiplicity STAR = new Multiplicity(0,UNBOUND);

    /** multiplicity representing (1,unbound) */
    public final static Multiplicity PLUS = new Multiplicity(1,UNBOUND);
    
    
    private final int lbound, ubound;
    
    /** 
     * Construct a new multiplicity with a lower and upper limit, inclusive. 
     */
    public Multiplicity(int min, int max) {
        if (min < 0) {
            throw new IllegalArgumentException("Lower bound: "  + min);
        }
        if  (max < 0 && max != UNBOUND ) {
            throw new IllegalArgumentException("Upper bound: "  + max);
        }
        
        this.lbound = min; 
        this.ubound = max;
    }


    /**
     * Lower bound, inclusive.
     * @return
     */
    public int getMin() {
        return this.lbound;
    }
    /** 
     * 
     * @return Upper bound, inclusive, or {@link #UNBOUND} when upper 
     * limit is unbound.
     */
    public int getMax() {
        return this.ubound;
    }
    
    /** 
     * This multiplicity in UML notation, i.e. <code>m..n</code>.
     */
    public String toString() {
        return lbound  + ".." + ((ubound == UNBOUND) ? "n" : ubound);
       
    }

    /**
     * @return true if the multiplicity is (0,0)
     */
    public boolean isZero() {
        return equals(ZERO);
    }
    /**
     * @return true if the multiplicity is (1,1)
     */
    public boolean isOne() {
        return equals(ONE);
    }
    /**
     * @return true if the multiplicity is (0,1) or (1,1) 
     */
    public boolean isOptional() {
        return equals(OPTIONAL) || equals(ONE);
    }
    /**
     * @return true if the multiplicity is (0,unbound)
     */
    public boolean isStar() {
        return equals(STAR);
    }
    /**
     * @return true if the multiplicity is (1,unbound)
     */
    public boolean isPlus() {
        return equals(PLUS);
    }

    
    /**
     * @return true if the multiplicity has an unbound upper limit 
     * (?, {@link #UNBOUND})
     */
    public boolean isUnbound() {
        return ubound == UNBOUND;
    }

    /** 
     * 
     * @param m The multiplicity to test
     * @return true if this multiplicity contains the passed one entirely
     */
    public boolean contains(Multiplicity m) {
        return lbound <= m.lbound && (ubound == UNBOUND 
               || (m.ubound != UNBOUND && m.ubound <= ubound));
    }
    
    
    public int hashCode() {
        int h = HashCodeUtil.SEED;
        h = HashCodeUtil.hash(h, lbound);
        h = HashCodeUtil.hash(h, ubound);
        return h;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        
        Multiplicity other = (Multiplicity) obj;
        return lbound == other.lbound && ubound == other.ubound;  
        
    }
}
