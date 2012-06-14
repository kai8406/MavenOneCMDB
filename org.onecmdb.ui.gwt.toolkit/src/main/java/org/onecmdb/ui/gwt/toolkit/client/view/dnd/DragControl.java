/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.toolkit.client.view.dnd;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;

public class DragControl implements MouseListener {

	private Widget dragWidget;

	public DragControl(Widget dragWidget, SourcesMouseEvents drag) {
			drag.addMouseListener(this);
			this.dragWidget = dragWidget;
	}

    private AbsolutePanel getAbsoluteWidgetPanel(Widget widget) {
    	if (widget == null) {
    		return(null);
    	}
    	Widget parent = widget.getParent();
    	if (parent == null) {
    		return(null);
    	}
    	
    	if (parent instanceof AbsolutePanel) {
    		return((AbsolutePanel)parent);
    	}
    	return(getAbsoluteWidgetPanel(parent));
    }
    
	
	
	private int xOffset = 0;
	private int yOffset = 0;
	private boolean dragging = false;
	

	// Start the drag operation on the component.
    public void onMouseDown(Widget source, int x, int y) {
    	if (!dragging) {
	        DOM.setCapture(source.getElement());

	        dragging  = true;
	        
	        xOffset = x;
	        yOffset = y;
	        
    	}
    }

    // Drag the component.
    public void onMouseMove(Widget source, int x, int y) {
    	if (dragging) {
    		// System.out.println("MOUSE MOVE:" + x + "," + y);
    		
    		AbsolutePanel rootPanel = getAbsoluteWidgetPanel(dragWidget);
    		
    		
    		int newX = x + dragWidget.getAbsoluteLeft() - xOffset;
    		int newY = y + dragWidget.getAbsoluteTop() - yOffset;
    		
    		
        	
        	
        	int rootOffsetY = newY - rootPanel.getAbsoluteTop();
        	int rootOffsetX =  newX-rootPanel.getAbsoluteLeft();
        	if (rootOffsetX < 0) {
        		rootOffsetX = 0;
        	}
        	if (rootOffsetY < 0) {
        		rootOffsetY = 0;
        	}
        	rootPanel.setWidgetPosition(dragWidget, rootOffsetX, rootOffsetY);
    	}
    }

    // Drop the component.
    public void onMouseUp(Widget source, int x, int y) {
    	if (dragging) {
    			
	        DOM.releaseCapture(source.getElement());

	        dragging = false;
	        
    	}
    }
    

	public void onMouseEnter(Widget sender) {
	}

	public void onMouseLeave(Widget sender) {
	}
}
