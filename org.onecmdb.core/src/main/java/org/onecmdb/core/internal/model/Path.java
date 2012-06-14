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
package org.onecmdb.core.internal.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.core.IPath;
import org.onecmdb.core.IType;

/** 
 * Generic implementation of the the {@link #rg.onecmdb.core.IPath}
 * interface.
 */
public class Path<T> implements IPath<T> {
	private static final char SEPARATOR_CHAR = '/';
    private List<T> path = new ArrayList<T>(0);

    
    /** Construct a new path, with no element */
	public Path() {

	}

    /** Construct a new path, with one element */
	public Path(T element) {
		addElement(element);
	}

	public void addElement(T element) {
		if (element == null) {
			return;
		}
		path.add(element);
	}

	public void addPath(IPath<T> path) {
		this.path.addAll(path.getList());
	}

	public T getLeaf() {
		return (path.get(path.size() - 1));
	}

	public List<T> getList() {
		return Collections.unmodifiableList(this.path);
	}

	public Iterator<T> iterator() {
		return this.path.iterator();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (T elt : path) {
			if (!first) {
				sb.append(Path.SEPARATOR_CHAR);
			} else {
				first = false;
			}
			sb.append(elt.toString());
		}
		return sb.toString();
	}

    
    
    public IPath<T> getAllButLeaf() {
        Path<T> newPath = new Path<T>();
        for (Iterator<T> eltIter = this.iterator(); eltIter.hasNext();) {
            T elt = eltIter.next();
            if (eltIter.hasNext()) {
                newPath.addElement(elt);
            }
        }
        return newPath;
    }

    
    public IPath<T> getAllButRoot() {
        Path<T> newPath = new Path<T>();
        for (int i = 1; i < this.path.size(); i++) {
            newPath.addElement(this.path.get(i));
        }
        return newPath;
    }
    
    
    
    public IPath<T> getReversed() {
        if (getSize() == 1) {
            Path<T> newPath = new Path<T>();
            newPath.addElement(getRoot());
            return newPath;
        } else {
            IPath<T> newPath = getAllButRoot().getReversed();
            newPath.addElement(getRoot());
            return newPath;
        }
    }
    
    
    public T getRoot() {
        return getSize() > 0 ? this.path.get(0) : null;
    }

    public int getSize() {
        return this.path.size();
    }

    public String getPathSepearator() {
        return ""+SEPARATOR_CHAR;
    }

    public boolean isParent(IPath<T> ancestor) {
        if (getSize() == 0) {
            return true;
        }
        
        if (ancestor.getSize() >= getSize()) {
            if (ancestor.getRoot().equals(getRoot())) {
                IPath<T> rest = getAllButRoot();
                IPath<T> ancestorRest = ancestor.getAllButRoot();
                return rest.isParent(ancestorRest);
            }
        }
        return false;
    }

    
    public boolean isSibling(IPath<IType> sibling) {
        return getAllButLeaf().equals(sibling.getAllButLeaf());
        
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(Path.class))
            return false;
        
        Path other = (Path) obj;
        return this.path.equals(other.path);
        
    }
        
        
        
        
        
        
        
}