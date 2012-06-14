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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWT_TransactionBean implements IsSerializable {
	public static int REGISTERED_STATE = 0x01;

	public static int PROCESSING_STATE = 0x02;

	public static int COMMITED_STATE = 0x04;

	public static int REJECTED_STATE = 0x08;

	private Long id;

	private String name;

	private int status = 0;

	private String issuer;

	private Date insertTs;

	private Date beginTs;

	private Date endedTs;

	private String rejectCause;

	private Integer ciModified;
	private Integer ciDeleted;
	private Integer ciAdded;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public Date getInsertTs() {
		return insertTs;
	}
	public void setInsertTs(Date insertTs) {
		this.insertTs = insertTs;
	}
	public Date getBeginTs() {
		return beginTs;
	}
	public void setBeginTs(Date beginTs) {
		this.beginTs = beginTs;
	}
	public Date getEndedTs() {
		return endedTs;
	}
	public void setEndedTs(Date endedTs) {
		this.endedTs = endedTs;
	}
	public String getRejectCause() {
		return rejectCause;
	}
	public void setRejectCause(String rejectCause) {
		this.rejectCause = rejectCause;
	}
	public Integer getCiModified() {
		return ciModified;
	}
	public void setCiModified(Integer ciModified) {
		this.ciModified = ciModified;
	}
	public Integer getCiDeleted() {
		return ciDeleted;
	}
	public void setCiDeleted(Integer ciDeleted) {
		this.ciDeleted = ciDeleted;
	}
	public Integer getCiAdded() {
		return ciAdded;
	}
	public void setCiAdded(Integer ciAdded) {
		this.ciAdded = ciAdded;
	}

}
