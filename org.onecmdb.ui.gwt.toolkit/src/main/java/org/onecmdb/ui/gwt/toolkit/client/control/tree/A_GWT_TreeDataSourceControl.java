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
package org.onecmdb.ui.gwt.toolkit.client.control.tree;


import org.onecmdb.ui.gwt.toolkit.client.control.input.AbstractDataControl;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TreeListener;

public abstract class A_GWT_TreeDataSourceControl extends AbstractDataControl implements ITreeControl  {
	private TreeListener listener;
	private boolean showSearch = true;
	private boolean hideRoot = false;
	private boolean rootState = false;
	private boolean reverse = false;
	private ClickListener clickListener = null;
	
	public TreeListener getTreeListener() {
		return(this.listener);
	}

	public void setTreeListener(TreeListener l) {
		this.listener = l;
	}
	
	public boolean showSearch() {
		return(this.showSearch);
	}
	
	public void setShowSearch(boolean showSearch) {
		this.showSearch = showSearch;
	}
	
	public void setHideRoot(boolean b) {
		this.hideRoot = b;
		
	}

	public boolean isHideRoot() {
		return(this.hideRoot);
	}

	public boolean isRootState() {
		return rootState;
	}

	public void setRootState(boolean rootState) {
		this.rootState = rootState;
	}
	
	public void setReverse(boolean b) {
		this.reverse = b;
	}
	public boolean isReverse() {
		return(this.reverse);
	}

	public ClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(ClickListener clickListener) {
		this.clickListener = clickListener;
	}

	
	
}
