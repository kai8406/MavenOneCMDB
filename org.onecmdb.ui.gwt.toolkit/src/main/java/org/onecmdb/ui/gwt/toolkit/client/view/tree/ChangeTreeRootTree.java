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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Used to insert one tree into another tree.
 *
 */
public class ChangeTreeRootTree extends CITreeWidget {

	private TreeItem triggerItem;

	public ChangeTreeRootTree(Tree tree, ITreeControl ctrl) {
		super();
		this.tree = tree;
		this.control = ctrl;
		this.tree.addTreeListener(this);
	}

	public void setTriggerItem(TreeItem listCIItem) {
		this.triggerItem = listCIItem;
		
	}

	
	public void onTreeItemSelected(TreeItem item) {
		if (item.getUserObject() != null) {
			super.onTreeItemSelected(item);
		}
	}

	
	public void onTreeItemStateChanged(TreeItem item) {
		if (item.equals(triggerItem)) {
			// Load root object....
			loadRootObject(item);
			return;
		}
		if (item.getUserObject() != null) {
			super.onTreeItemStateChanged(item);
		}
	}
	
	
	protected void loadRootObject(final TreeItem item) {
		control.getRootObject(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				item.removeItems();
				item.addItem(new TreeItem(new Label("ERROR: " + caught.getMessage())));
			}

			public void onSuccess(Object result) {
				item.removeItems();
				if (result instanceof Object[]) {
					Object objects[] = (Object[])result;
					for (int i = 0; i < objects.length; i++) {
						TreeItem childItem = newTreeItem(objects[i], false);
						item.addItem(childItem);
					}
				} else {
					TreeItem childItem = newTreeItem(result, false);
					item.addItem(childItem);
				}
			}
		});
	}
}
