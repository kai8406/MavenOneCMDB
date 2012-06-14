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
package org.onecmdb.rest.graph.prefuse.controls;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.util.ui.UILib;

public class FlyInOutZoomControl extends ControlAdapter {
	
	 private int m_button_zoom_out = RIGHT_MOUSE_BUTTON;
	 private int m_button_zoom_in = LEFT_MOUSE_BUTTON;
	 private long m_duration = 2000;  
	/**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
    	Display display = (Display)e.getComponent();
    	if ( !display.isTranformInProgress() && 
        	  UILib.isButtonPressed(e, m_button_zoom_in) &&
        	  e.getClickCount() == 2)
        {
    		
    		Point2D center = new Point2D.Double((double)e.getX(), (double)e.getY());
    		double scale = 3.0;
    		display.animatePanAndZoomTo(center, scale, m_duration);
    	    /*   
            Visualization vis = display.getVisualization();
            Rectangle2D bounds = vis.getBounds(m_group);
            GraphicsLib.expand(bounds, m_margin + (int)(1/display.getScale()));
            DisplayLib.fitViewToBounds(display, bounds, m_duration);
            DisplayLib.fitViewToBounds(display, bounds, center, duration)
        	*/
        }
    }

}
