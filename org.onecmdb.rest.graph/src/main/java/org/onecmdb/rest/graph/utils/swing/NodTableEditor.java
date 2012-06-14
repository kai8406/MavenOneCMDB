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
package org.onecmdb.rest.graph.utils.swing;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

import org.onecmdb.rest.graph.events.IEventListener;


public class NodTableEditor extends AbstractCellEditor implements TreeCellEditor {

	
	
	private Object value;
	private IEventListener listener;

	
	public IEventListener getListener() {
		return listener;
	}

	public void setListener(IEventListener listener) {
		this.listener = listener;
	}

	public Object getCellEditorValue() {
		return(this.value);
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		// TODO Auto-generated method stub
		this.value = value;
		CheckBoxTreeRenderer renderer = new CheckBoxTreeRenderer();
		renderer.setEventListsner(listener);
		return(renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true));
	}
	
	
}
