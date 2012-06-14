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
package org.onecmdb.ui.gwt.toolkit.client.view.table;

import java.util.List;

import org.gwtiger.client.widget.HoverGridWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListenerCollection;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.IOrderListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.ISourcesOrderEvents;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.order.OrderEvent;
import org.onecmdb.ui.gwt.toolkit.client.control.table.ITableControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeRender;
import org.onecmdb.ui.gwt.toolkit.client.view.input.IAttributeRender;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class CITablePanel extends HoverGridWidget implements IOrderListener {
	
	protected static final String STYLE_CELL = "gwtiger-tableCell";
	protected static final String STYLE_HEADER_CELL = "gwtiger-tableHeaderCell";
	private ITableControl control;
	private IAttributeRender render;
	private LoadListenerCollection loadListenerCollection = new LoadListenerCollection();

	public CITablePanel() {
		getColumnFormatter().setStyleName(0, STYLE_HEADER);
	}

	
	public ITableControl getTableControl() {
		return(this.control);
	}

	public void setTabelControl(ITableControl control) {
		this.control = control;
	}

	
	
	
	public void addHeader(int column, Widget header) {
		//hasHeader=true;		
		
		RowFormatter rowFormatter = getRowFormatter();
		rowFormatter.setStyleName(0, STYLE_HEADER);
		header.setStyleName(STYLE_HEADER_CELL);
		setWidget(0, column, header);
		
		
	}


	public void load() {
		if (getRowCount() > 0) {
			removeRow(0);
		}
		this.control.getColumns(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				loadListenerCollection.fireOnLoadFailure(CITablePanel.this, caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof List) {
					List resultList = (List)result;
					addHeader(0, "#");
					for (int col = 1; col < (resultList.size()+1); col++) {
						Object column = resultList.get(col-1);
						Widget columnWidget = render.getColumnHeaderWidget(column);
						if (columnWidget instanceof ISourcesOrderEvents) {
							((ISourcesOrderEvents)columnWidget).addOrderListener(CITablePanel.this);
						}
						addHeader(col, columnWidget);
					}
				}				
			}
		});
		
		reload();
	}
	
	protected void reload() {
		loadListenerCollection.fireOnLoadStart(this);
		
	
		
		this.control.getRows(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				loadListenerCollection.fireOnLoadFailure(CITablePanel.this, caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof List) {
					List rows = (List)result;
					Integer firstItem = control.getFirstItem();
					int rowIndex = 1;
					if (firstItem != null) {
						rowIndex = firstItem.intValue();
						rowIndex++;
					}
					System.out.println("ROW COUNT=" + rows.size());
					for (int row = 0; row < rows.size(); row++) {
						Object rowData = rows.get(row);
						setText(row+1, 0, "" + rowIndex);
						rowIndex++;
						if (((row+1) % 2) == 0) {
							getRowFormatter().setStyleName(row+1, STYLE_ROW_EVEN);
						} else {
							getRowFormatter().setStyleName(row+1, STYLE_ROW_ODD);
						}
						getCellFormatter().setStyleName(row+1, 0, STYLE_HEADER);
						if (rowData instanceof List) {
							List cols = (List)rowData;
							for (int col = 1; col < (cols.size()+1); col++) {
								Object colObject = cols.get(col-1);
								Widget widget = render.getColumnWidget(colObject);
								
								if (widget != null) {
									widget.setWidth("100%");
									widget.setStyleName(STYLE_CELL);
									if (widget instanceof IBaseField) {
										setWidget(row+1, col, ((IBaseField)widget).getBaseField());
									} else {
										setWidget(row+1, col, widget);
									}
								} else {
									setText(row+1, col, "");
								}
							}
						}
					}
					
					// Remove not visible rows.
					for (int row = (control.getMaxResult().intValue()+1); row > rows.size() ; row--) {
						if (getRowCount() > row) {
							removeRow(row);
						}
					}
				}
				loadListenerCollection.fireOnLoadComplete(CITablePanel.this);
			}
		});
		
	}

	public void addLoadListener(LoadListener listener) {
		loadListenerCollection.add(listener);
	}


	public void setAttributeRender(IAttributeRender render) {
		this.render = render;
		
	}
	
	public void onOrderEvent(OrderEvent evnt) {
		if (evnt.getData() instanceof AttributeValue) {
			if (getRowCount() > 0) {
				int cols = getCellCount(0);
				for (int i = 0; i < cols; i++) {
					Widget w = getWidget(0, i);
					if (w != null && !w.equals(evnt.getSender())) {
						if (w instanceof ColumnHeaderWidget) {
							((ColumnHeaderWidget)w).setSortVisable(false);
						}
					}
				}
			}
			GWT_AttributeBean aBean = ((AttributeValue)evnt.getData()).getAttributeBean(); 
				
			if (aBean != null) {
				
				this.control.setSortOrder(aBean.getAlias());
				this.control.setSortOrderAscending(evnt.isAscending());
			} else {
				this.control.setSortOrder(null);
			}
			reload();
		}
	}
}
