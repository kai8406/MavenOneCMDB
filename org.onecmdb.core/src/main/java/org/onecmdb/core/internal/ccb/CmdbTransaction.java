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
package org.onecmdb.core.internal.ccb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.ObjectConverter;
import org.onecmdb.core.internal.storage.IDaoReader;

public class CmdbTransaction implements ICmdbTransaction {

	private ItemId id;

	private List<IRFC> rfcs = null;

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
	
	
	private transient IDaoReader reader;

	private ISession session;

	public void setDaoReader(IDaoReader reader) {
		this.reader = reader;
	}

	public CmdbTransaction() {
		this.id = new ItemId();
	}

	public void add(IRFC rfc) {
		if (this.rfcs == null) {
			this.rfcs = new ArrayList<IRFC>();
		}
		this.rfcs.add(rfc);
	}

	public void setRfc(List<IRFC> rfcs) {
		for (IRFC rfc : rfcs) {
			add(rfc);
		}
	}

	public ItemId getId() {
		return (this.id);
	}

	public List<IRFC> getRfcs() {
		if (this.rfcs != null) {
			return (this.rfcs);
		}
		if (reader != null) {
			return (reader.getRfcsForCmdbTx(getId()));
		}
		return(Collections.EMPTY_LIST);
	}

	public Long getLongId() {
		return (ObjectConverter.convertItemIdToLong(this.id));
	}

	public void setLongId(Long id) {
		this.id = ObjectConverter.convertLongToItemId(id);
	}

	public Date getBeginTs() {
		return beginTs;
	}

	public void setBeginTs(Date beginTs) {
		this.beginTs = beginTs;
	}

	public Date getEndTs() {
		return endedTs;
	}

	public void setEndTs(Date endedTs) {
		this.endedTs = endedTs;
	}

	public Date getInsertTs() {
		return insertTs;
	}

	public void setInsertTs(Date insertTs) {
		this.insertTs = insertTs;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
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

	public void setRejectCause(String message) {
		this.rejectCause = message;
	}

	public String getRejectCause() {
		return (this.rejectCause);
	}

	public ICiModifiable getTemplate(ICi ci) {
		if (ci == null) {
			return(null);
		}
		CiModifiable template = new CiModifiable();
		template.setTarget(ci);
		add(template);
		return (template);
	}

	public IAttributeModifiable getAttributeTemplate(IAttribute attribute) {
		AttributeModifiable template = new AttributeModifiable();
		template.setTarget(attribute);
		add(template);
		return (template);
	}
	
	public String toString() {
		return("<TX " +
				"status= "+ getStatus() + 
				", reject="+ getRejectCause() + 
				", start=" + getBeginTs() + 
				", stop=" + getEndTs() + 
				">");
	}

	
	
	

	public void setSession(ISession session) {
		this.session = session;
		setIssuer(session.getPrincipal().getUsername());
	}
	
	public ISession getSession() {
		return(this.session);
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
