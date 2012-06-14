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
package org.onecmdb.core.utils.graph.query;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRFCSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemTransactionSelector;


/**
 * <GraphQuery>
 * 	<ItemSelector>
 * 		
 * 	</ItemSelector>
 * </GraphQuery>
 *
 */
public class GraphQuery  implements Serializable {
	private List selectors = new ArrayList();
	
	public void addSelector(ItemSelector selector) {
		if (this.selectors.contains(selector)) {
			return;
		}
		this.selectors.add(selector);
	}
	
	// Need this to declare the WSDL!
	public void setItemOffspringSelector(List<ItemOffspringSelector> o) {
		append(o);
	}
	public void setItemRelationSelector(List<ItemRelationSelector> o) {
		append(o);
	}
	public void setItemAliasSelector(List<ItemAliasSelector> o) {
		append(o);
	}
	
	public void setItemTransactionSelector(List<ItemTransactionSelector> o) {
		append(o);
	}
	
	public void setItemRFCSelector(List<ItemRFCSelector> o) {
		append(o);
	}
	
	
	public List<ItemOffspringSelector> getItemOffspringSelector() {
		List<ItemOffspringSelector> rels = new ArrayList<ItemOffspringSelector>();
		for (ItemSelector sel : fetchSelectors()) {
			if (sel instanceof ItemOffspringSelector) {
				rels.add((ItemOffspringSelector)sel);
			}
		}
		if (rels.size() == 0) {
			return(null);
		}
		return(rels);
	}
	
	public List<ItemRelationSelector> getItemRelationSelector() {
		List<ItemRelationSelector> rels = fetchRelationSelectors();
		if (rels.size() == 0) {
			return(new ArrayList<ItemRelationSelector>());
		}
		return(rels);
	}
	
	public List<ItemAliasSelector> getItemAliasSelector() {
		List<ItemAliasSelector> rels = new ArrayList<ItemAliasSelector>();
		for (ItemSelector sel : fetchSelectors()) {
			if (sel instanceof ItemAliasSelector) {
				rels.add((ItemAliasSelector)sel);
			}
		}
		if (rels.size() == 0) {
			return(null);
		}

		return(rels);
		
	}
	
	public List<ItemRFCSelector> getItemRFCSelector() {
		List<ItemRFCSelector> rels = new ArrayList<ItemRFCSelector>();
		for (ItemSelector sel : fetchSelectors()) {
			if (sel instanceof ItemRFCSelector) {
				rels.add((ItemRFCSelector)sel);
			}
		}
		if (rels.size() == 0) {
			return(null);
		}

		return(rels);
		
	}

	public List<ItemTransactionSelector> getItemTransactionSelector() {
		List<ItemTransactionSelector> rels = new ArrayList<ItemTransactionSelector>();
		for (ItemSelector sel : fetchSelectors()) {
			if (sel instanceof ItemTransactionSelector) {
				rels.add((ItemTransactionSelector)sel);
			}
		}
		if (rels.size() == 0) {
			return(null);
		}

		return(rels);
		
	}
	
	
	
	public void append(List<? extends ItemSelector> sels) {
		if (sels == null) {
			return;
		}
		this.selectors.addAll(sels);
	}
	public List<ItemSelector> fetchSelectors() {
		return(this.selectors);
	}

	public ItemSelector findSelector(String id) {
		for (ItemSelector sel : fetchSelectors()) {
			if (sel.getId().equals(id)) {
				return(sel);
			}
		}
		return(null);
	}
	
	public List<ItemRelationSelector> fetchRelationSelectors() {
		List<ItemRelationSelector> rels = new ArrayList<ItemRelationSelector>();
		for (ItemSelector sel : fetchSelectors()) {
			if (sel.reference()) {
				rels.add((ItemRelationSelector)sel);
			}
		}
		return(rels);
	}
	
	public List<ItemSelector> fetchOrderdItemSelectors() {
		List<ItemSelector> sels = new ArrayList<ItemSelector>();
		ItemSelector pSel = fetchPrimarySelectors();
		sels.add(pSel);
		for (ItemSelector sel : fetchSelectors()) {
			if (sel.equals(pSel)) {
				continue;
			}
			if (!sel.reference()) {
				sels.add(sel);
			}
		}
		return(sels);
	}

	public ItemSelector fetchPrimarySelectors() {
		for (ItemSelector sel : fetchSelectors()) {
			if (sel.isPrimary()) {
				return(sel);
			}
		}
		return(fetchSelectors().get(0));
		//return(null);
	}
	
	public boolean removeSelector(String id) {
		ItemSelector sel = findSelector(id);
		return(removeSelector(sel));
	}
	/**
	 * Remove the specified selector from the query.
	 * Will also remove all relation selectors that 
	 * references the selector.
	 * 
	 * @param sel
	 * @return
	 */
	public boolean removeSelector(ItemSelector sel) {
		if (sel == null) {
			return(false);
		}
		
		boolean result = selectors.remove(sel);
		
		for (ItemRelationSelector rel : fetchRelationSelectors()) {
			if (rel.getSource().equals(sel.getId())) {
				removeSelector(rel);
				continue;
			}
			if (rel.getTarget().equals(sel.getId())) {
				removeSelector(rel);
				continue;
			}
		}
		
		return(result);
		
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("GraphQuery {");
		for (ItemSelector sel : fetchSelectors()) {
			b.append(sel.toString());
			b.append(",");
		}
		b.append("}");
		return(b.toString());
	}
	
	
}
