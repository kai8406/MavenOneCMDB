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
package org.onecmdb.core.utils.wsdl;

import java.util.Date;

public class RFCBean {
	// The back end id for this RFC.
	private Long id;
	// The user commiting the change.
	private String issuer;
	// When the change occured.
	private Date ts;
	// A summary of the change.
	private String summary;
	// The back end id for the enclosing transaction.
	private Long transactionId;
	// The target Item id.
	private Long targetId;
	// The target CI id.
	private Long targetCIId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public Long getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	
	public Date getTs() {
		return ts;
	}
	
	public void setTs(Date ts) {
		this.ts = ts;
	}
	
	public Long getTargetId() {
		return targetId;
	}
	
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	
	public Long getTargetCIId() {
		return targetCIId;
	}
	
	public void setTargetCIId(Long targetCIId) {
		this.targetCIId = targetCIId;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[" + this.id + "] ");
		b.append("[" + this.ts + "] ");
		b.append("[" + this.issuer + "] - ");
		b.append(this.summary);
		return(b.toString());
	}
	
}
