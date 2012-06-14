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
package org.onecmdb.ui.gwt.toolkit.client.view.tree;

import org.onecmdb.ui.gwt.toolkit.client.control.tree.ITreeControl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

public class ReverseTreeTable extends Composite {
	
	
	private VerticalPanel rootPanel;
	private ITreeControl control;

	public ReverseTreeTable(final ITreeControl control) {
		rootPanel = new VerticalPanel();
		rootPanel.setSpacing(6);
		
		this.control = control;
		// tree.addItem(item);
		control.getRootObject(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				rootPanel.add(new Label("ERROR: " + caught.getMessage()));
			}

			public void onSuccess(Object result) {

				if (control.isHideRoot()) {
					if (result instanceof Object[]) {
						Object objects[] = (Object[])result;
						for (int i = 0; i < objects.length; i++) {
							control.getChildren(objects[i], null, new AsyncCallback() {

								public void onFailure(Throwable caught) {
									rootPanel.add(new Label("ERROR: " + caught.getMessage()));
								}

								public void onSuccess(Object result) {
									addRootObject(result);
								}

							});
						}
					} else {
						control.getChildren(result, null, new AsyncCallback() {

							public void onFailure(Throwable caught) {
								rootPanel.add(new Label("ERROR: " + caught.getMessage()));
							}

							public void onSuccess(Object result) {
								addRootObject(result);
							}
						});
					}
				} else {
					addRootObject(result);
				}
			}
		});
		initWidget(rootPanel);
		}
	
		protected void addRootObject(Object result) {
			if (result instanceof Object[]) {
				Object objects[] = (Object[])result;
				for (int i = 0; i < objects.length; i++) {
					newTreeItem(objects[i], true);
					//tree.addItem(item);
				}
			} else {
				newTreeItem(result, true);
				//tree.addItem(item);
			}
		}

	
		protected WidgetItem newTreeItem(Object data, final boolean root) {
			
			Widget w = this.control.getWidget(data);
			final WidgetItem item = new WidgetItem(new ClickListener() {

				public void onClick(Widget sender) {
					if (sender instanceof WidgetItem) {
						final WidgetItem currentItem = (WidgetItem)sender;
						if (currentItem.getState()) {
						   // Open start populate with entries.
							ReverseTreeTable.this.control.getChildren(currentItem.getUserObject(), null, new AsyncCallback() {

					
								public void onFailure(Throwable caught) {
									currentItem.removeItems();
									currentItem.addItem(new Label("ERROR: " + caught.getMessage()));
								}

								public void onSuccess(Object result) {
									long stopCall = System.currentTimeMillis();  
									if (result instanceof Object[]) {
										currentItem.removeItems();
										int resultSize = 0;
										Object children[] = (Object[])result;
										resultSize = children.length;
										for (int i = 0; i < resultSize; i++) {
											currentItem.addItem(newTreeItem(children[i], false));
										}
									}
								}
							});
						} else {
							currentItem.removeItems();
						}
					}
				}
				
			});
			
			item.setUserObject(data);
			item.setWidget(w);
			item.setStyleName("onecmdb-reverse-tree-item");
			// Need to this here since we call the auto open and that
			// need to have the tree callback setup.
			if (root) {
				rootPanel.add(item);
				rootPanel.setCellHorizontalAlignment(item, HorizontalPanel.ALIGN_RIGHT);
				item.setStyleName("onecmdb-reverse-tree-root");
			}
			
			this.control.getChildCount(data, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(Object result) {
					if (result instanceof Integer) {
						int value = ((Integer)result).intValue();
						if (value > 0) {
							item.setChildCount(value);
						}
						
					}
				}
				
			});
			return(item);
		}
		
		class WidgetItem extends Composite {
			private HorizontalPanel widgetPanel;
			private VerticalPanel childPanel;
			private Object data;
			private ClickListener expandListener;
			private boolean open = false;

			public WidgetItem(ClickListener expandListener) {
				HorizontalPanel root = new HorizontalPanel();
				widgetPanel = new HorizontalPanel();
				childPanel = new VerticalPanel();
				root.add(childPanel);
				root.add(widgetPanel);
				root.setCellVerticalAlignment(widgetPanel, VerticalPanel.ALIGN_MIDDLE);
				this.expandListener = expandListener;
				initWidget(root);
			}
			
			public void removeItems() {
				childPanel.clear();
				childPanel.setVisible(false);
			}

			public boolean getState() {				
				return(open);
			}

			public void setChildCount(int value) {
				if (value > 0) {
					final Image image = new Image("images/plus.gif");
					image.setStyleName("onecmdb-reverse-expand");
					image.addClickListener(new ClickListener() {

						public void onClick(Widget sender) {
							open = !open;
							if (open) {
								image.setUrl("images/minus.gif");
							} else {
								image.setUrl("images/plus.gif");
							}
							expandListener.onClick(WidgetItem.this);
						}
					});
					widgetPanel.add(image);
					widgetPanel.setCellVerticalAlignment(image, VerticalPanel.ALIGN_MIDDLE);
				}
			}

			public void setUserObject(Object data) {
				this.data = data;
			}
			public Object getUserObject() {
				return(this.data);
			}
			public void addItem(Widget item) {
				childPanel.add(item);
				childPanel.setCellHorizontalAlignment(item, HorizontalPanel.ALIGN_RIGHT);
				childPanel.setVisible(true);
			}
			
			public void setWidget(Widget widget) {
				widgetPanel.add(widget);
			}
		}
}
