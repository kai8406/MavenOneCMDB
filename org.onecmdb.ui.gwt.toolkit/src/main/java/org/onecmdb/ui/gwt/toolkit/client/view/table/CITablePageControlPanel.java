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



import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CITablePageControlPanel extends Composite implements LoadListener {
	
	private Label firstItemNumber = new Label("0");
	private Label lastItemNumber = new Label("0");
	private Label currentRows = new Label("Loading..");
	TextBox search = new TextBox();
	private CITablePanel table;
	private Image nextImage;
	private Image prevImage;
	private Image reload;
	
	public CITablePageControlPanel(final CITablePanel table) {
		this.table = table;
		table.addLoadListener(this);
		HorizontalPanel root = new HorizontalPanel();
		root.setStyleName("onecmdb-table-page-search-panel");
		root.setWidth("100%");
		HorizontalPanel actions = new HorizontalPanel();
		root.add(actions);
		root.setCellHorizontalAlignment(actions, HorizontalPanel.ALIGN_LEFT);
		
		HorizontalPanel paging = new HorizontalPanel();		
		root.add(paging);
		root.setCellHorizontalAlignment(paging, HorizontalPanel.ALIGN_RIGHT);
			
		reload = new Image("images/reload.gif");
		
		reload.addClickListener(getSearchClickListener());
		// Enable 'return key' to start search.
		search.addKeyboardListener(new KeyboardListener() {

			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
			}

			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if (keyCode == KeyboardListener.KEY_ENTER) {
					getSearchClickListener().onClick(sender);
				}
			}

			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
			}
			
		});
		prevImage = new Image("images/prev.gif");
		nextImage = new Image("images/next.gif");
		nextImage.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				setNextPage();
				table.reload();
			}
			
		});
		
		
		prevImage.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				setPrevPage();
				table.reload();
			}
		});
		
		paging.add(prevImage);
		paging.add(firstItemNumber);
		paging.add(new Label("-"));
		paging.add(lastItemNumber); 
		paging.add(new Label("(")); 
		paging.add(currentRows);
		paging.add(new Label(")")); 
		paging.add(nextImage);
		
		actions.add(new Label("Search"));
		
		actions.add(search);
		actions.add(reload);
		
		/*
		actions.add(new Label("PageSize"));
		TextBox pageSize = new TextBox();
		pageSize.setVisibleLength(2);
		actions.add(pageSize);
		*/
		
		table.addLoadListener(this);
		
		initWidget(root);
	}
	
	private ClickListener getSearchClickListener() {
		return(new ClickListener() {
			public void onClick(Widget sender) {
				table.getTableControl().setSearchText(search.getText());
				table.getTableControl().setFirstItem(new Integer(0));
				table.reload();
			}
		});
	}

	public void update() {
		table.getTableControl().getRowCount(new AsyncCallback() {
			public void onFailure(Throwable caught) {
			}
			public void onSuccess(Object result) {
				if (result instanceof Integer) {
					int totalItems = ((Integer)result).intValue();
					int firstItem = getInt(table.getTableControl().getFirstItem());
					int pageSize = getInt(table.getTableControl().getMaxResult());
					
					int lastItem = pageSize + firstItem;
					if (lastItem >= totalItems) {
						 lastItem = totalItems;
						 enableNext(false);
					} else {
						 enableNext(true);
					}
					if (firstItem <= 0) {
						enablePrev(false);
					} else {
						enablePrev(true);
					}
					// Set the labels.
					currentRows.setText(result.toString());
					firstItemNumber.setText("" + ((totalItems > 0) ? (firstItem+1) : 0));
					lastItemNumber.setText("" + lastItem);
				}
			}
			
		});
	}
	
	private void enableNext(boolean value) {
		nextImage.setVisible(value);
	}
	private void enablePrev(boolean value) {
		prevImage.setVisible(value);
	}
	
	public void setNextPage() {
		int first = getInt(table.getTableControl().getFirstItem());
		int next = first + getInt(table.getTableControl().getMaxResult());
		table.getTableControl().setFirstItem(new Integer(next));
	}
	
	public void setPrevPage() {
		int first = getInt(table.getTableControl().getFirstItem());
		int max = getInt(table.getTableControl().getMaxResult());
		int prev = first - max;
		if (prev < 0) {
			prev = 0;
		}
		table.getTableControl().setFirstItem(new Integer(prev));
	}

	private int getInt(Integer integer) {
		if (integer == null) {
			return(0);
		}
		return (integer.intValue());
	}

	public void onLoadComplete(Object sender) {
		reload.setUrl("images/reload.gif");
		reload.setTitle("Reload");
	}

	public void onLoadFailure(Object sender, Throwable caught) {
		reload.setUrl("images/reload_error.gif");
		reload.setTitle(caught.toString());
	}

	public void onLoadStart(Object sender) {
		reload.setUrl("images/indicator.gif");
		reload.setTitle("Loading....");
		update();
		
	}
	

}
