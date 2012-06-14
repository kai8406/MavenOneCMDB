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

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;

public class DebugCheckBoxSelectionModel<M extends ModelData> extends CheckBoxSelectionModel<M> {

	@Override
	protected void handleMouseDown(GridEvent e) {
		System.out.println("Handle Mouse Down...." + selected.size());
		super.handleMouseDown(e);
	}

	@Override
	protected void doMultiSelect(List models, boolean keepExisting,
			boolean supressEvent) {
		/*
		if (!keepExisting) {
			System.out.println("Break Here...");
			if (models.size() == 1) {
				System.out.println("One object break;");
			} else {
				System.out.println("many object break;");
				
			}
		}
		*/
		System.out.println("doMultiSelect....models=" + models + ",keep=" + keepExisting);
		super.doMultiSelect(models, keepExisting, supressEvent);
	}

	@Override
	public void select(int index) {
		System.out.println("Select index=" + index + ".. Call...");
		super.select(index);
	}

	
	
	public void select(int start, int end) {
		System.out.println("Select(" + start + "," + end + ")");
		super.select(start, end);
	}
	
	
}
