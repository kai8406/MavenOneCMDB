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
package org.onecmdb.rest.graph.prefuse.action.distortion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import prefuse.action.distortion.Distortion;

public class ZoomDistortion extends Distortion {

	double radius = 0.1;
	double zoom = 4.0;
	
	public ZoomDistortion() {
		super();
		m_distortX = false;
		m_distortY = false;
	}
	@Override
	protected double distortSize(Rectangle2D bbox, double x, double y,
			Point2D anchor, Rectangle2D bounds) {
		double absRadius = 0.0;
		if (bounds.getWidth() < bounds.getHeight()) {
			absRadius = bounds.getWidth() * radius;
		} else {
			absRadius = bounds.getHeight() * radius;
		}
		double distance = anchor.distance(new Point2D.Double(x,y));
		
		double dr = (absRadius - distance)/absRadius;
		
		if (dr < 0) {
			return(1.0);
		}
		
		double s = dr*zoom + 1;
		
		return(s);
		
	}

	@Override
	protected double distortX(double x, Point2D anchor, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	protected double distortY(double y, Point2D anchor, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		return y;
	}
	

}
