package org.onecmdb.ui.gwt.toolkit.client.control.listener.order;

import org.onecmdb.ui.gwt.toolkit.client.control.input.AbstractAttributeValue;

import com.google.gwt.user.client.ui.Widget;

public class OrderEvent {
	private boolean ascending;
	private Object data;
	private Widget sender;
	
	public OrderEvent(Widget sender, Object value, boolean asc) {
		this.data = value;
		this.ascending = asc;
		this.sender = sender;
	}
	
	public boolean isAscending() {
		return ascending;
	}
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}

	public Widget getSender() {
		return sender;
	}

	public void setSender(Widget sender) {
		this.sender = sender;
	}
	
	
	
}
