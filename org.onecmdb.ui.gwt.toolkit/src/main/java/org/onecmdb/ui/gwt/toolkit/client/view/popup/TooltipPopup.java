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
package org.onecmdb.ui.gwt.toolkit.client.view.popup;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/* Display's tooltips like in Eclipse 
.tooltip .gwt-HTML {
        border: 1px solid black;
        padding: 2px 3px 3px 3px;
        font-size: smaller;
        background-color: #ffffcc; // Pale Weak Yellow 

}
*/

public class TooltipPopup extends PopupPanel {

	/**
	 * The default css class name for the tool tip
	 */
	private static final String     DEFAULT_TOOLTIP_STYLE   = "tooltip";

	/**
	 * The default delay, in milliseconds,
	 */
	private static final int        DEFAULT_SHOW_DELAY              = 500;

	/**
	 * The delay, in milliseconds, to display the tooltip
	 */
	private int                                     showDelay;

	/**
	 * The delay, in milliseconds, to hide the tooltip, after it is
	displayed
	 */
	private int                                     hideDelay;

	/**
	 * The timer to show the tool tip
	 */
	private Timer                           showTimer;

	/**
	 * The timer to hide the tool tip
	 */
	private Timer                           hideTimer;

	private boolean useRelTop;

	private UIObject sender;

	private int relTop;

	private int relLeft;

	private HTML contents;

	/**
	 * Static factory to show a tooltip...
	 * 
	 * @param sender
	 * @param text
	 */
	public TooltipPopup(Widget sender, String text) {
		this(sender, 0, 0, text, true);
		if (!(sender instanceof SourcesMouseEvents)) {
			return;
		}
		
		((SourcesMouseEvents)sender).addMouseListener(new MouseListener() {

			public void onMouseDown(Widget sender, int x, int y) {
				// TODO Auto-generated method stub
				
			}

			public void onMouseEnter(Widget sender) {
				setRelLeft(sender.getOffsetWidth() + 16);
				setRelTop(16);
				show();
				
			}

			public void onMouseLeave(Widget sender) {
				hide();
			}

			public void onMouseMove(Widget sender, int x, int y) {
				// TODO Auto-generated method stub
				
			}

			public void onMouseUp(Widget sender, int x, int y) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	

	/**
	 * Creates a new Tool Tip with the default show delay and no auto
	hiding
	 * @param sender The widget to create the tool tip for
	 * @param relLeft The left offset from the <code>sender</code>
	 * @param relTop The top offset from the <code>sender</code>
	 * @param text The tool tip text to display
	 * @param useRelTop If true, then use the relative top offset. If not,
	then
	 *        just use the sender's offset height.
	 */
	public TooltipPopup( Widget sender, int relLeft, int relTop, final
			String text, boolean useRelTop ) {

		super( true );
		
		this.showTimer = null;
		this.hideTimer = null;

		this.sender = sender;
		this.relLeft = relLeft;
		this.relTop = relTop;
		this.useRelTop = useRelTop;
		
		this.showDelay = DEFAULT_SHOW_DELAY;
		this.hideDelay = -1;

		contents = new HTML( text );
		add( contents );

		addStyleName( DEFAULT_TOOLTIP_STYLE );
	}
	
	protected void setRelLeft(int relLeft) {
		this.relLeft = relLeft;
	}
	protected void setRelTop(int relTop) {
		this.relTop = relTop;
	}

	private void setPosition() {
		int left = getPageScrollLeft() + sender.getAbsoluteLeft() + relLeft;
		int top = getPageScrollTop() + sender.getAbsoluteTop();

		if ( useRelTop ) {
			top += relTop;
		}
		else {
			top += sender.getOffsetHeight() + 1;
		}

		setPopupPosition( left, top );
	}
	public void setTooltipText(String text) {
		contents.setHTML(text);
	}
	public String getTooltipText() {
		return(contents.getHTML());
	}

	/**
	 * Creates a new Tool Tip
	 * @param sender The widget to create the tool tip for
	 * @param relLeft The left offset from the <code>sender</code>
	 * @param relTop The top offset from the <code>sender</code>
	 * @param text The tool tip text to display
	 * @param useRelTop If true, then use the relative top offset. If not,
	then
	 *        just use the senders offset height.
	 * @param showDelay The delay, in milliseconds, before the popup is
	 *        displayed
	 * @param hideDelay The delay, in milliseconds, before the popup is
	hidden
	 * @param styleName The style name to apply to the popup
	 */
	public TooltipPopup( Widget sender, int relLeft, int relTop, final
			String text, boolean useRelTop, final int showDelay, final int
			hideDelay, final String styleName ) {

		this( sender, relLeft, relTop, text, useRelTop );

		this.showDelay = showDelay;
		this.hideDelay = hideDelay;
		removeStyleName( DEFAULT_TOOLTIP_STYLE );
		addStyleName( styleName );
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#show()
	 */
	public void show() {

		// Set delay to show if specified
		if ( this.showDelay > 0 ) {
			this.showTimer = new Timer() {

				/*
				 * (non-Javadoc)
				 * @see com.google.gwt.user.client.Timer#run()
				 */
				public void run() {

					TooltipPopup.this.showTooltip();
				}
			};
			this.showTimer.schedule( this.showDelay );
		}
		// Otherwise, show the dialog now
		else {
			showTooltip();
		}

		// Set delay to hide if specified
		if ( this.hideDelay > 0 ) {
			this.hideTimer = new Timer() {

				/*
				 * (non-Javadoc)
				 * @see com.google.gwt.user.client.Timer#run()
				 */
				public void run() {

					TooltipPopup.this.hide();
				}
			};
			this.hideTimer.schedule( this.showDelay + this.hideDelay );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#hide()
	 */
	public void hide() {

		super.hide();

		// Cancel the show timer if necessary
		if ( this.showTimer != null ) {
			this.showTimer.cancel();
		}

		// Cancel the hide timer if necessary
		if ( this.hideTimer != null ) {
			this.hideTimer.cancel();
		}
	}

	/**
	 * Show the tool tip now
	 */
	private void showTooltip() {
		setPosition();
		super.show();
	}

	/**
	 * Get the offset for the horizontal scroll
	 * @return The offset
	 */
	private int getPageScrollLeft() {
		return DOM.getAbsoluteLeft( DOM.getParent( RootPanel.getBodyElement()
		) );
	}

	/**
	 * Get the offset for the vertical scroll
	 * @return The offset
	 */
	private int getPageScrollTop() {
		return DOM.getAbsoluteTop( DOM.getParent( RootPanel.getBodyElement()
		) );
	}



} 

