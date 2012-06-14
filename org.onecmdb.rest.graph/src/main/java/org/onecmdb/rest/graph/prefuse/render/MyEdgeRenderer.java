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
package org.onecmdb.rest.graph.prefuse.render;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class MyEdgeRenderer extends EdgeRenderer {

	public MyEdgeRenderer(int edgeTypeCurve, int edgeArrowForward) {
		super(edgeTypeCurve, edgeArrowForward);
	}

	@Override
	protected Shape getRawShape(VisualItem item) {
	      EdgeItem   edge = (EdgeItem)item;
	        VisualItem item1 = edge.getSourceItem();
	        VisualItem item2 = edge.getTargetItem();
	        
	        int type = m_edgeType;
	        
	        getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
	                        m_xAlign1, m_yAlign1);
	        getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
	                        m_xAlign2, m_yAlign2);
	        m_curWidth = (float)(m_width * getLineWidth(item));
	        
	        // create the arrow head, if needed
	        EdgeItem e = (EdgeItem)item;
	        if ( e.isDirected() && m_edgeArrow != Constants.EDGE_ARROW_NONE ) {
	            // get starting and ending edge endpoints
	            boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
	            Point2D start = null, end = null;
	            start = m_tmpPoints[forward?0:1];
	            end   = m_tmpPoints[forward?1:0];
	            
	            // compute the intersection with the target bounding box
	            VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
	            int i = GraphicsLib.intersectLineRectangle(start, end,
	                    dest.getBounds(), m_isctPoints);
	            if ( i > 0 ) end = m_isctPoints[0];
	            
	            // create the arrow head shape
	            AffineTransform at = getArrowTrans(start, end, m_curWidth);
	            m_curArrow = at.createTransformedShape(m_arrowHead);
	            
	            // update the endpoints for the edge shape
	            // need to bias this by arrow head size
	            Point2D lineEnd = m_tmpPoints[forward?1:0]; 
	            lineEnd.setLocation(0, -m_arrowHeight);
	            at.transform(lineEnd, lineEnd);
	        } else {
	            m_curArrow = null;
	        }
	        
	        // create the edge shape
	        Shape shape = null;
	        double n1x = m_tmpPoints[0].getX();
	        double n1y = m_tmpPoints[0].getY();
	        double n2x = m_tmpPoints[1].getX();
	        double n2y = m_tmpPoints[1].getY();
	        switch ( type ) {
	            case Constants.EDGE_TYPE_LINE:          
	                m_line.setLine(n1x, n1y, n2x, n2y);
	                shape = m_line;
	                break;
	            case Constants.EDGE_TYPE_CURVE:
	                getCurveControlPoints(edge, m_ctrlPoints,n1x,n1y,n2x,n2y);
	                m_cubic.setCurve(n1x, n1y,
	                                m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(),
	                                m_ctrlPoints[1].getX(), m_ctrlPoints[1].getY(),
	                                n2x, n2y);
	                shape = m_cubic;
	                break;
	            default:
	                throw new IllegalStateException("Unknown edge type");
	        }
	        
	        // return the edge shape
	        return shape;

	}

	@Override
	protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp,
			double x1, double y1, double x2, double y2) {
		// check if the same object...
		if (eitem.getSourceItem().equals(eitem.getTargetItem())) {
			  cp[0].setLocation(x1-80, y1+80);
			  cp[1].setLocation(x1+80, y1+80);
		      /*
			  cp[0].setLocation(x1+2*dx/3,y1);
		      cp[1].setLocation(x2-dx/8,y2-dy/8);
		      */
		      return;
		 
		}
		// TODO Auto-generated method stub
		super.getCurveControlPoints(eitem, cp, x1, y1, x2, y2);
	}
	
	
	

}
