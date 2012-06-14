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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CITreeWidget extends Composite implements TreeListener {

	
	protected ITreeControl control;
	protected Tree tree;
	private TreeItem currentItem;
	private QueryCriteriaWidget search;
	

	public CITreeWidget(final ITreeControl control) {
		
		this.control = control;
		
		tree = new Tree();
		
		tree.addTreeListener(this);
		
		TreeItem item = new TreeItem("Populating....");
		//tree.addItem(item);
		control.getRootObject(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				tree.addItem(new TreeItem(new Label("ERROR: " + caught.getMessage())));
			}

			public void onSuccess(Object result) {
				tree.clear();
				if (control.isHideRoot()) {
					if (result instanceof Object[]) {
						Object objects[] = (Object[])result;
						for (int i = 0; i < objects.length; i++) {
							control.getChildren(objects[i], null, new AsyncCallback() {

								public void onFailure(Throwable caught) {
									tree.addItem(new TreeItem(new Label("ERROR: " + caught.getMessage())));
								}

								public void onSuccess(Object result) {
									addRootObject(result);
								}
								
							});
						}
					} else {
						control.getChildren(result, null, new AsyncCallback() {

							public void onFailure(Throwable caught) {
								tree.addItem(new TreeItem(new Label("ERROR: " + caught.getMessage())));
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
	
			
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setVerticalAlignment(vPanel.ALIGN_TOP);
		search = new QueryCriteriaWidget(this);
		vPanel.add(search);
		vPanel.setCellWidth(search, "100%");
		if (!control.showSearch()) {
			search.setVisible(false);
		}
		vPanel.add(tree);
		vPanel.setCellWidth(tree, "100%");
		vPanel.setCellHeight(tree, "100%");
		vPanel.setCellVerticalAlignment(tree, VerticalPanel.ALIGN_TOP);
		initWidget(vPanel);
	}
	
	
	
	protected void addRootObject(Object result) {
		if (result instanceof Object[]) {
			Object objects[] = (Object[])result;
			for (int i = 0; i < objects.length; i++) {
				TreeItem item = newTreeItem(objects[i], true);
				//tree.addItem(item);
			}
		} else {
			TreeItem item = newTreeItem(result, true);
			//tree.addItem(item);
		}
	}


	protected CITreeWidget() {
	}


	public ITreeControl getTreeControl() {
		return(this.control);
	}
	
	public void reload() {
		if (currentItem == null) {
			currentItem = tree.getItem(0);
		}
		// Reload root.
		currentItem.setState(false, true);
		currentItem.setState(true, true);
		
	}

	protected TreeItem newTreeItem(Object data, final boolean root) {
		
		Widget w = this.control.getWidget(data);
		final TreeItem item = new TreeItem();
		
		item.setUserObject(data);
		item.setWidget(w);
		
		// Need to this here since we call the auto open and that
		// need to have the tree callback setup.
		if (root) {
			tree.addItem(item);
		}
		
		this.control.getChildCount(data, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(Object result) {
				if (result instanceof Integer) {
					int value = ((Integer)result).intValue();
					if (value > 0) {
						TreeItem countItem = new TreeItem("Populating....");
						countItem.setUserObject(result);	
						item.addItem(countItem);
						// Always open root item.
						
						if (root && control.isRootState()) {
							System.out.println("Default open root Tree Item! '" + item.getUserObject() +"'");
							item.setState(true, true);
						}
						
					}
					
				}
			}
			
		});
		return(item);
	}
	
	
	public void onTreeItemSelected(TreeItem item) {
		if (CITreeWidget.this.control.getTreeListener() != null) {
			CITreeWidget.this.control.getTreeListener().onTreeItemSelected(item);
		}
	}

	public void onTreeItemStateChanged(final TreeItem item) {
		final long start = System.currentTimeMillis();
		if (item.getState()) {
			if (item.getUserObject() instanceof GroupData) {
				GroupData gData = (GroupData)item.getUserObject();
				updateChildren(item, gData.getUserData(), gData.getFirstItem());
				return;
			}
			currentItem = item;
			// Open start populate with entries.
			this.control.getChildCount(item.getUserObject(), new AsyncCallback() {

				public void onFailure(Throwable caught) {
					item.removeItems();
					item.addItem(new TreeItem(new Label("ERROR: " + caught.getMessage())));
				}

				public void onSuccess(Object result) {
					if (result instanceof Integer) {
						Integer totalCount = (Integer)result;
						Integer maxResult = CITreeWidget.this.control.getMaxResult();
						
						if (maxResult != null && (totalCount.intValue() > maxResult.intValue())) {
							// Start adding groups...
							int groups = totalCount.intValue() / maxResult.intValue();
							int rest = totalCount.intValue() % maxResult.intValue();
							if (rest > 0) {
								groups++;
							}
							item.removeItems();
							for (int i = 0; i < groups; i++) {
								TreeItem groupItem = new TreeItem();
								int start = 1;
								int stop = maxResult.intValue();
								if (i > 0) {
									 start = (i*maxResult.intValue()) + 1;
									 stop = ((i+1)*maxResult.intValue());
									 if (stop > totalCount.intValue()) {
										 stop = totalCount.intValue();
									 }
								}
								groupItem.setText("[" + start + ".." + stop +"]");
								groupItem.setUserObject(new GroupData(item.getUserObject(), new Integer(start-1)));
								groupItem.addItem(new TreeItem("Loading..."));
								item.addItem(groupItem);
								
							}
						} else {
							updateChildren(item, item.getUserObject(), null);
						}
					}
				}
			});
			
			} else {
			//item.removeItems();
			//updateCountItem(item);
		}
		
		if (CITreeWidget.this.control.getTreeListener() != null) {
			CITreeWidget.this.control.getTreeListener().onTreeItemStateChanged(item);
		}
	
	}

	protected void updateChildren(final TreeItem item, Object data, final Integer firstItem) {
		
		CITreeWidget.this.control.getChildren(data, firstItem, new AsyncCallback() {

			
			public void onFailure(Throwable caught) {
				item.removeItems();
				item.addItem(new TreeItem(new Label("ERROR: " + caught.getMessage())));
			}

			public void onSuccess(Object result) {
				long stopCall = System.currentTimeMillis();  
				
				if (result instanceof Object[]) {
					/*
					boolean useSearch = false;
					Integer totalCount = new Integer(0);
					if (count instanceof Integer) {
						totalCount = (Integer)count;
					}
					if (totalCount.intValue() > control.getMaxResult().intValue()) {
							showSearch(true);
							useSearch = true;
					}
					*/
					item.removeItems();
					/*
					if (useSearch) {
						TreeItem searchItem = new TreeItem();
						searchItem.setHTML("<font size=\"xx-small\"><b>NOTE: Showing " + control.getMaxResult() + " of " + totalCount +", use search!</b></font>");
						searchItem.setUserObject(totalCount);
						item.addItem(searchItem);
					}
					*/
					int resultSize = 0;
					Object children[] = (Object[])result;
					resultSize = children.length;
					for (int i = 0; i < resultSize; i++) {
						item.addItem(newTreeItem(children[i], false));
					}
					long stopUI = System.currentTimeMillis();
					//OneCMDBSession.addCallStat("Tree.getChildren()", resultSize, start, stopCall, stopUI);
				}
			}
		});

	}

	protected void updateCountItem(final TreeItem parent) {
			
			this.control.getChildCount(parent.getUserObject(), new AsyncCallback() {

				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(Object result) {
					if (result instanceof Integer) {
						int value = ((Integer)result).intValue();
						if (value > 0) {
							TreeItem countItem = new TreeItem("Populating....");
							countItem.setUserObject(result);	
							parent.addItem(countItem);
						}
						
					}
				}
				
			});
		}
	



	protected void showSearch(boolean b) {
		search.setVisible(true);
	}


	class GroupData {
		private Object data;
		private Integer firstResult;

		public GroupData(Object data, Integer firstResult) {
			this.data = data;
			this.firstResult = firstResult;
		}

		public Object getUserData() {
			return(this.data);
		}

		public Integer getFirstItem() {
			return(this.firstResult);
		}
		
		
	}
}
