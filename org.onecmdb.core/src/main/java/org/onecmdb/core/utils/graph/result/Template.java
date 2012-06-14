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
package org.onecmdb.core.utils.graph.result;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.wsdl.TransactionBean;

public class Template extends NamedItem {
	private static final long serialVersionUID = 1L;

	private List<CiBean> offsprings = new ArrayList<CiBean>();
	private List<RFCBean> rfcs = new ArrayList<RFCBean>();
	private List<TransactionBean> transactions = new ArrayList<TransactionBean>();
	private Integer offspringCount;
	private CiBean template;
	private String id;

	private Integer totalCount;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTemplate(CiBean template) {
		this.template = template;
	}
	
	public CiBean getTemplate() {
		return(this.template);
	}
	
	public void addOffspring(CiBean b) {
		offsprings.add(b);
	}
	
	public void setOffsprings(List<CiBean> offsprings) {
		this.offsprings = offsprings;
	}
	
	public List<CiBean> getOffsprings() {
		return(offsprings);
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void addRFC(RFCBean rfc) {
		this.rfcs.add(rfc);
	}
	
	public void setRFC(List<RFCBean> rfcs) {
		this.rfcs = rfcs;
	}
	public List<RFCBean> getRFC() {
		return(this.rfcs);
	}
	
	public List<TransactionBean> getTransactions() {
		return(this.transactions);
	}
	
	public void setTransactions(List<TransactionBean> list) {
		this.transactions = list;
	}
	
	public void addTransaction(TransactionBean bean) {
		this.transactions.add(bean);
	}

	
}
