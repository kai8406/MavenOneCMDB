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
package org.onecmdb.ui.gwt.toolkit.client.control.table;


import org.onecmdb.ui.gwt.toolkit.client.control.input.IDataControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ITableControl extends IDataControl {

	/**
	 * Returns a List<AbstractAttributeValue>
	 * 
	 * @param callback
	 */
	public void getColumns(final AsyncCallback callback);
	
	
	/**
	 * Returns a Integer.
	 * 
	 * @param callback
	 */
	public void getRowCount(final AsyncCallback callback);
	
	/**
	 * returns List<List<AbstractAttributeValue>>
	 * @param callback
	 */
	public void getRows(final AsyncCallback callback);
	
	public String getObjectName(int row, int col);
	
	// Search/Paging control.
	public void setSearchText(String text);
	public void setSortOrder(String attrAlias);
	public void setSortOrderAscending(boolean value);
	public Integer getFirstItem();
	public Integer getMaxResult();
	public void setFirstItem(Integer value);
	public void setMaxResult(Integer value);
	
	// Navigation control.
	public int getSelectScreenIndex();
	public void setOnSelectScreenIndex(int index);
	public void setTemplate(GWT_CiBean bean);
	public void setEditable(boolean b);
	public boolean isEditabel();
}
