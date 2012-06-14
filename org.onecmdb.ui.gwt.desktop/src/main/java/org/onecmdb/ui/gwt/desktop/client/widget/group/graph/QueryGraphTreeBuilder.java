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
package org.onecmdb.ui.gwt.desktop.client.widget.group.graph;

import java.util.HashMap;

public class QueryGraphTreeBuilder {

	public static GWT_GraphTreeItem buildTree(GWT_GraphQuery query) {
		
		GWT_GraphTreeItem root = new GWT_GraphTreeItem();

		HashMap<String, GWT_ItemSelector> map = query.getSelectors();
		if (map == null) {
			return(root);
		}
		
		// Start with the primary and then traverse down.
		GWT_ItemOffspringSelector primary = null;
		for (GWT_ItemSelector sel : map.values()) {
			if (sel instanceof GWT_ItemOffspringSelector) {
				((GWT_ItemOffspringSelector)sel).isPrimary();
				primary = (GWT_ItemOffspringSelector) sel;
				break;
			}
		}
		if (primary == null) {
			return(root);
		}
		
		
		GWT_GraphTreeItem item = buildTree(query, null, primary);
		
		return(item);
	}

	private static GWT_GraphTreeItem buildTree(GWT_GraphQuery query,
			GWT_GraphTreeItem parent, GWT_ItemSelector current) {
		
		if (parent == null) {
			parent = new GWT_GraphTreeItem();
			parent.setSelector(current);
			parent.setGraphQuery(query);
		} else {
			// Check if we have traversed this leg.
			if (parent.hasParentSelector(current)) {
				return(parent);
			}
			// Add this as child.
			GWT_GraphTreeItem item = new GWT_GraphTreeItem();
			item.setSelector(current);
			parent.add(item);
			parent = item;
		}
		
		// Handle offspring selectors.
		if (current instanceof GWT_ItemOffspringSelector) {
			for (GWT_ItemSelector sel : query.getSelectors().values()) {
				if (sel instanceof GWT_ItemRelationSelector) {
					GWT_ItemRelationSelector rel = (GWT_ItemRelationSelector)sel;
					if (rel.getTarget().equals(current.getId())) {
						buildTree(query, parent, rel);
					}
					if (rel.getSource().equals(current.getId())) {
						buildTree(query, parent, rel);
					}
				}
			}
		}
		// Handle references.
		if (current instanceof GWT_ItemRelationSelector) {
			for (GWT_ItemSelector sel : query.getSelectors().values()) {
				if (sel instanceof GWT_ItemOffspringSelector) {
					GWT_ItemRelationSelector rel = (GWT_ItemRelationSelector)current;
					
					GWT_ItemSelector parentSelector = ((GWT_GraphTreeItem) parent.getParent()).getItemSelector();
					if (parentSelector == null) {
						continue;
					}
					
					if (rel.getTarget().equals(sel.getId())) {
						// found relation...
						if (!parentSelector.getId().equals(rel.getTarget())) {
							buildTree(query, parent, sel);
						}
					}
					if (rel.getSource().equals(sel.getId())) {
						// found relation...
						if (!parentSelector.getId().equals(rel.getSource())) {
							buildTree(query, parent, sel);
						}
					}
				}
			}
		}
		
		return(parent);
	}
	
}
