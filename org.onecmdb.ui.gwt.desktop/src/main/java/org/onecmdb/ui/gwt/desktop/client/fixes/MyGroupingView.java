/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.ui.gwt.desktop.client.fixes;

import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;

import com.extjs.gxt.ui.client.core.DomHelper;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.google.gwt.dom.client.Element;

public class MyGroupingView extends GroupingView {

	@Override
	protected String getGroup(Object value, ModelData m, int rowIndex,
			int colIndex, ListStore ds) {
		if (value instanceof ValueModel) {
			String text = ((ValueModel)value).getValueDisplayName();
			if (text == null) {
				text = "";
			}
			return(text);
		}
		return super.getGroup(value, m, rowIndex, colIndex, ds);
	}

	@Override
	public void focusCell(int rowIndex, int colIndex, boolean hscroll) {
		
		System.out.println("FoucsCell: " + rowIndex + "," + colIndex + "(" + hscroll + ")");
		super.focusCell(rowIndex, colIndex, true);
		
	}
	
	private boolean hasRows() {
		Element e = mainBody.dom.getFirstChildElement();
		return e != null && !e.getClassName().equals("x-grid-empty");
	}


	@Override
	protected void insertRows(ListStore store, int firstRow, int lastRow, boolean isUpdate) {
	    if (isUpdate && firstRow == 0 && lastRow == store.getCount() - 1) {
	      refresh(false);
	      return;
	    }
	    Element e = mainBody.dom.getFirstChildElement();
	    if (e != null && !hasRows()) {
	      mainBody.dom.setInnerHTML("");
	    }
	    String html = renderRows(firstRow, lastRow);
	    Element before = getRow(firstRow);
	    if (before != null) {
	      DomHelper.insertBefore((com.google.gwt.user.client.Element) before, html);
	    } else {
	      DomHelper.insertHtml("beforeEnd", mainBody.dom, html);
	    }

	    if (!isUpdate) {
	      processRows(firstRow, false);
	      focusRow(firstRow);
	  	}
	    
	  }
	
	
}
