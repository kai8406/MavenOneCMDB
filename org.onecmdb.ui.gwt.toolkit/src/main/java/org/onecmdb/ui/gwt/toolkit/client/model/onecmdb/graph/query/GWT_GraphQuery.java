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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWT_GraphQuery implements IsSerializable,Serializable {
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_ItemSelector>
	 */
	private List selectors = new ArrayList();
	
	public GWT_GraphQuery() {
	}
	
	public void addSelector(GWT_ItemSelector selector) {
		this.selectors.add(selector);
	}
	
	public List getSelectors() {
		return(this.selectors);
	}
	
	public GWT_ItemSelector findPrimary() {
		for (Iterator iter = getSelectors().iterator(); iter.hasNext();) {
			GWT_ItemSelector sel = (GWT_ItemSelector) iter.next();
			if (sel.isPrimary()) {
				return(sel);
			}
		}
		return(null);
	}
}
