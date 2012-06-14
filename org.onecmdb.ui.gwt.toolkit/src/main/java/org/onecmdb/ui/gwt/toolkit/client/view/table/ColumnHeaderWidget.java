package org.onecmdb.ui.gwt.toolkit.client.view.table;

import org.onecmdb.ui.gwt.toolkit.client.control.input.AbstractAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.IOrderListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.ISourcesOrderEvents;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.OrderEvent;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.OrderListenerCollection;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

public class ColumnHeaderWidget extends Composite implements ISourcesOrderEvents {
	OrderListenerCollection listeners = new OrderListenerCollection();
	private boolean asc = false;
	Image sortImage = new Image();
	private HTML html;
	private AbstractAttributeValue aValue;
	
	public ColumnHeaderWidget(final AbstractAttributeValue aValue) { 
		this.aValue = aValue;
		html = new HTML(aValue.getDisplayName(), false);
		
		if (true) {
			html.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					setAscending(!getAscending());
					listeners.fireOnOrderEvent(new OrderEvent(ColumnHeaderWidget.this, aValue, getAscending()));
				}
			});
			initWidget(html);
			return;
		}
		HorizontalPanel hPanel = new HorizontalPanel();
		Label name = new Label(aValue.getDisplayName(), false);
		hPanel.setWidth("100%");
		hPanel.add(name);
		hPanel.setCellWidth(name, "100%");
		
		if (aValue.isSortable()) {
			hPanel.add(sortImage);
			hPanel.setCellVerticalAlignment(sortImage, HorizontalPanel.ALIGN_MIDDLE);
					
			sortImage.setVisible(false);
			name.addMouseListener(new MouseListener() {

				public void onMouseDown(Widget sender, int x, int y) {
					// TODO Auto-generated method stub
					
				}

				public void onMouseEnter(Widget sender) {
					
					
				}

				public void onMouseLeave(Widget sender) {
					// TODO Auto-generated method stub
					
				}

				public void onMouseMove(Widget sender, int x, int y) {
					// TODO Auto-generated method stub
					
				}

				public void onMouseUp(Widget sender, int x, int y) {
					// TODO Auto-generated method stub
					
				}
				
			});
			name.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					setAscending(!getAscending());
					listeners.fireOnOrderEvent(new OrderEvent(ColumnHeaderWidget.this, aValue, getAscending()));
				}
			});
		}
		initWidget(hPanel);
	
	}
			
	private boolean getAscending() {
		return(this.asc);
	}
	
	private void setAscending(boolean v) {
		System.out.println("Set Ascending = " + v);
		this.asc = v;
		if (this.asc) {
			html.setHTML(aValue.getDisplayName() + "<img src=\"images/sort_asc.gif\">");
			//sortImage.setUrl("images/sort_asc.gif");
		} else {
			//sortImage.setUrl("images/sort_desc.gif");
			html.setHTML(aValue.getDisplayName() + "<img src=\"images/sort_desc.gif\">");
		}
		//setSortVisable(true);
	}
	
	public void setSortVisable(boolean value) {
		if (!value) {
			html.setHTML(aValue.getDisplayName());
			//sortImage.setVisible(value);
		}
	}
	
	public void addOrderListener(IOrderListener listener) {
		listeners.add(listener);
	}

	public void removeOrderListener(IOrderListener listener) {
		listeners.remove(listener);
	}
	
	

}
