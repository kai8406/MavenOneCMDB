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
package org.onecmdb.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 
 * A list wrapper focused to be used in a bean environment, that is, there
 * exists bean style accesersors for those methods  that normally do not
 * have this kind of signature, for example there existes a {@link #getSize()}
 * to retrieve the size, which usually is gotten via 
 * {@link java.util.List#size()}.
 * 
 * @author nogun
 *
 * @param <T>
 */
public class BeanList<T> implements Iterable {
    private final List<T> list = new ArrayList<T>(0);
    private T meta;   
    
    /** 
     * Initiate with a meta object, from which relevant information 
     * can be gathered, kind of a declaration structure.
     * @param meta
     */
    BeanList(T meta) {
        this.meta = meta;
    }
    
    // {{{ summary information regarding the attribute
    
    public T getMeta() {
        return meta;
    }
    
    // }}}
    
    public void addValue(T attr) {
        this.list.add(attr);
    }
    
    public List<T> getValues() {
        return this.list;
    }
    
    public int getSize() {
        return this.list.size();
    }

    public Iterator<T> iterator() {
        return getValues().iterator();
    }
}