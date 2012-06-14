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

import com.extjs.gxt.ui.client.data.BaseModel;

public class StoreResult extends BaseModel {
	private Long txId;
	private Integer added;
	private Integer modfied;
	private Integer delted;
	private Date start;
	private Date stop;

	
	
	public Long getTxId() {
		return(get("txId"));
	}

	public void setTxId(Long txId) {
		set("txId", txId);
	}

	public Integer getAdded() {
		return(get("added"));
	}

	public void setAdded(Integer added) {
		set("added", added);
	}

	public Integer getModfied() {
		return(get("modfied"));
	}

	public void setModfied(Integer modfied) {
		set("modfied", modfied);
	}

	public Integer getDelted() {
		return(get("delted"));
	}

	public void setDelted(Integer delted) {
		set("delted", delted);
	}

	public Date getStart() {
		return(get("start"));
	}

	public void setStart(Date start) {
		set("start", start);
	}

	public Date getStop() {
		return(get("stop"));
	}

	public void setStop(Date stop) {
		set("stop", stop);
	}

	public void setRejected(boolean rejected) {
		set("rejected", rejected);
	}

	public void setRejectCause(String rejectCause) {
		set("rejectCause", rejectCause);
	}
	public boolean isRejected() {
		return(get("rejected",false));
	}

	public String getRejectCause() {
		return(get("rejectCause"));
	}
}
