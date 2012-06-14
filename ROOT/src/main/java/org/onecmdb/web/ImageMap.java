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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ImageMap {
    private List<Area> areas = new ArrayList<Area>();
    private Object _fillcookie;

    /** Add an area to this image map. */
    public synchronized void addArea(Area area) {
        areas.add(area);
    }

    /** 
     * Search for the first area here the passed point is within 
     * @param p The point to test 
     * @return The first area matched or <code>null</code> if no area exists
     * where the point can not be found in any area. 
     */ 
    public synchronized Area getArea(Point p ) {
        for (Area area : areas) {
            if (area.getBounds().contains(p)) {
                return area;
            }
        }
        return null;
    }
    
    /** 
     * Query if the image map is in <em>filling mode</em>
     * 
     * @return A boolean indication filling status.
     */
    public synchronized boolean isFillng() {
        return _fillcookie != null;
    }

    /** 
     * To be called before one starts the populating of this image map (a way
     * of preventing race conditions) */
    public synchronized Object startFilling() {
        if (_fillcookie != null) {
            throw new IllegalStateException("Already filling");
        }
        _fillcookie = new Object();
        return _fillcookie;
    }

   /** 
    * To be called when population of data is ready, indicating that this this
    * image map can be used by other parties. The other parties will ge a 
    * notification (via {@link #notifyAll}) in case the are waiting for it to be 
    * ready (using {@link #wait}) */
    public synchronized void stopFilling(Object cookie) {
        if (cookie.equals(_fillcookie)) {
            _fillcookie = null;
            notifyAll();
        }
    }


}
