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
package org.onecmdb.core.internal.ccb.workers;

import java.util.Date;

import org.onecmdb.core.IRfcResult;

/**
 * The result of an update operation.
 *
 */
public class RfcResult implements IRfcResult {

	private boolean rejected = false;
	private String cause;
	private Long txId;
	
	// Extra info...
	private String issuer;
	
	// Statistics
	private Integer ciAdded;
	private Integer ciModified;
	private Integer ciDeleted;
	private Date start;
	private Date stop;
	

	public boolean isRejected() {
		return (this.rejected);
	}
	
	public void setRejected(boolean value) {
		this.rejected = value;
	}
	
	public String getRejectCause() {
		return (this.cause);
	}

	public void setRejectCause(String cause) {
		setRejected(true);
		this.cause = cause;
	}

	public String toString() {
		if (rejected) {
			return ("REJECTED <" + this.cause + ">");
		}
		return ("COMMITED");
	}

	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}
	
	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}




	public Integer getCiAdded() {
		return ciAdded;
	}

	public void setCiAdded(Integer ciAdded) {
		this.ciAdded = ciAdded;
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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStop() {
		return stop;
	}

	public void setStop(Date stop) {
		this.stop = stop;
	}

	public String getTxIdAsString() {
		if (this.txId == null) {
			return(null);
		}
		return(this.txId.toString());
	}
	
	public void setTxIdAsString(String id) {
		if (id == null) {
			return;
		}
		this.txId = Long.parseLong(id);
	}
	
	
	

}
