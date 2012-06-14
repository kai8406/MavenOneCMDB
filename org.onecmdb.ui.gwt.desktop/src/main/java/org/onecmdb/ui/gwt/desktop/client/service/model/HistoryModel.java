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
package org.onecmdb.ui.gwt.desktop.client.service.model;

import java.util.Date;

public class HistoryModel extends ModelItem {

	
	
	
	@Override
	public HistoryModel copy() {
		HistoryModel copy = new HistoryModel();
		super.copy(copy);
		return(copy);
	}

	public void setTxId(String txid) {
		set("txid", txid);
		
	}

	public void setIssuer(String issuer) {
		set("issuer", issuer);
	}

	public void setSummary(String summary) {
		set("summary", summary);
	}

	public void setTs(Date ts) {
		set("ts", ts);
	}
	
	public String getTxId() {
		return(get("txid"));
		
	}

	public String getIssuer() {
		return(get("issuer"));
	}

	public String getSummary() {
		return(get("summary"));
	}

	public Date getTs() {
		return(get("ts"));
	}

	public void setRfcId(String id) {
		set("rfcid", id);
		
	}
	public String getRfcId() {
		return(get("rfcid"));
	}
}
