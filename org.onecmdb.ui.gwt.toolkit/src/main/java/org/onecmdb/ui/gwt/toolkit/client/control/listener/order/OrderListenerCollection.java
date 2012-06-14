package org.onecmdb.ui.gwt.toolkit.client.control.listener.order;

import java.util.ArrayList;
import java.util.Iterator;

public class OrderListenerCollection extends ArrayList {
	
	public void fireOnOrderEvent(OrderEvent event) {
		for (Iterator iter = iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof IOrderListener) {
				((IOrderListener)o).onOrderEvent(event);
			}
		}
	}
}
