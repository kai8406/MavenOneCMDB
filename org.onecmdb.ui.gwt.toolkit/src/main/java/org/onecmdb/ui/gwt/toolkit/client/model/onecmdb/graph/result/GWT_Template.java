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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RFCBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_TransactionBean;

public class GWT_Template extends GWT_NamedItem {
	private static final long serialVersionUID = 1L;

	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean>
	 */
	private List offsprings = new ArrayList();
	
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RFCBean>
	 */
	private List rfcs = new ArrayList();
	/**
	 * @gwt.typeArgs <org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_TransactionBean>
	 */
	private List transactions = new ArrayList();

	
	private Integer totalCount;
	private GWT_CiBean template;
	private String id;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	
	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public void setTemplate(GWT_CiBean template) {
		this.template = template;
	}
	
	public GWT_CiBean getTemplate() {
		return(this.template);
	}
	
	
	public void addOffspring(GWT_CiBean b) {
		offsprings.add(b);
	}
	
	public List getOffsprings() {
		return(offsprings);
	}
	
	public void setRFC(List rfcs) {
		this.rfcs = rfcs;
	}
	public List getRFC() {
		return(this.rfcs);
	}
	public void addRFC(GWT_RFCBean rfc) {
		this.rfcs.add(rfc);
		
	}
	
	public List getTransactions() {
		return(this.transactions);
	}
	
	public void setTransactions(List list) {
		this.transactions = list;
	}
	public void addTransaction(GWT_TransactionBean tx) {
		this.transactions.add(tx);
	}

	
	
}
