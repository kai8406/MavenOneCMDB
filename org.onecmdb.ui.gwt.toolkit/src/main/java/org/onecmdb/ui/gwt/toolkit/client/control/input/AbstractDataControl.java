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
package org.onecmdb.ui.gwt.toolkit.client.control.input;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;

public class AbstractDataControl implements IDataControl {
	private Integer firstItem = new Integer(0);
	private Integer maxResult = new Integer(10);
	private String searchText = null;
	private boolean editable;
	private String orderAttrAlias;
	private boolean orderAscending;
	
	public Integer getFirstItem() {
		return(this.firstItem);
	}
	
	public Integer getMaxResult() {
		return(this.maxResult);
	}

	public void setSearchText(String text) {
		if (text != null && text.length() == 0) {
			this.searchText = null;
		} else {
			this.searchText = text;
		}
	}
	
	public String getSearchText() {
		return(this.searchText);
	}

	public void setFirstItem(Integer value) {
		this.firstItem = value;
	}

	public void setMaxResult(Integer value) {
		this.maxResult = value;
	}

	public void setSortOrder(String attrAlias) {
		this.orderAttrAlias = attrAlias;
	}
	
	public void setSortOrderAscending(boolean value) {
		this.orderAscending = value;
	}
	

	protected GWT_QueryCriteria getDataControlCriteria() {
		GWT_QueryCriteria crit = new GWT_QueryCriteria();
		crit.setMaxResult((Integer)this.maxResult);
		crit.setFirstResult((Integer)this.firstItem);
		if (getSearchText() != null) {
			crit.setText(getSearchText());
			crit.setTextMatchAlias(true);
			crit.setTextMatchDescription(true);
			crit.setTextMatchValue(true);
			
		}

		if (this.orderAttrAlias != null) {
			crit.setOrderAttAlias(this.orderAttrAlias);
			crit.setOrderAscending(orderAscending);
			
		}
		return(crit);
	}

	public boolean isEditabel() {
		return(editable);
	}

	public void setEditable(boolean value) {
		this.editable = value;
		
	}
	

}
