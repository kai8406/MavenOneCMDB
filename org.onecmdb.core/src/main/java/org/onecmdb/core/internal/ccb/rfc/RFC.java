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
package org.onecmdb.core.internal.ccb.rfc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.ObjectConverter;
import org.onecmdb.core.internal.storage.IDaoReader;

public abstract class RFC implements IRFC {
	// Id to this RFC.
	private Long id;

	// The ID where the source object is located.
	private transient IRFC parent;

	private Long parentId;

	// Indicating that the sourceId is a RFC or a ICo.
	// private boolean sourceIsRfc = true;

	// The id of the transaction this rfc belongs to.
	private Long txId;

	// The target ICi id(CI or Attribute). Could be the same as the source ICi.
	private Long targetId;
	
	// The target CI.
	private Long targetCIId;

	// Another way to point out the target.
	private String targetAlias;

	// Timestamp when this rfc has been processed.
	private Date ts;

	private transient List<IRFC> rfcs = new ArrayList<IRFC>();

	private IDaoReader daoReader;

	public RFC() {
		this.id = ObjectConverter.convertItemIdToLong(new ItemId());
	}

	public void setDaoReader(IDaoReader daoReader) {
		this.daoReader = daoReader;
	}
	
	public void setTs(Date date) {
		this.ts = date;
	}

	public Date getTs() {
		return (this.ts);
	}

	public void setParent(IRFC rfc) {
		this.parent = rfc;
		this.parentId = rfc.getId();
	}

	public IRFC getParent() {
		if (this.parent == null) {
			// Need yo lookup through daoReader!
			if (this.parentId == null) {
				return (null);
			}
			// TODO:....
		}
		return (this.parent);
	}

	/*
	 * public void setSource(ICi ci) { this.sourceId =
	 * ObjectConverter.convertItemIdToLong(ci.getId()); this.sourceIsRfc =
	 * false; }
	 */
	public void setTargetAlias(String alias) {
		this.targetAlias = alias;
	}

	public String getTargetAlias() {
		return (this.targetAlias);
	}

	public void setTarget(ICi ci) {
		this.targetId = ObjectConverter.convertItemIdToLong(ci.getId());
		this.targetCIId = targetId;
		if (ci instanceof BasicAttribute) {
			this.targetCIId = ((BasicAttribute)ci).getOwnerId();
		}
	}
	
	public ICi getTarget() {
		if (this.targetId == null) {
			return(null);
		}
		if (this.daoReader == null) {
			return(null);
		}
		ICi ci = this.daoReader.findById(new ItemId(this.targetId));
		return(ci);
	}
	
	public void setTargetId(Long id) {
		this.targetId = id;
	}

	public Long getTargetId() {
		return (this.targetId);
	}
	
	public void setTxId(Long id) {
		this.txId = id;
	}

	public Long getTxId() {
		return (this.txId);
	}

	/**
	 * Common method to identify the item.
	 * Used fro summary.
	 * @return
	 */
	public String getTargetInfo() {
		if (getTargetAlias() != null) {
			return(getTargetAlias());
		}
		if (getTargetId() != null) {
			return("" + getTargetId());
		}
		return("");
	}
	
	/*
	 * public ItemId getSource() { return
	 * ObjectConverter.convertLongToItemId(this.sourceId); }
	 */

	public ItemId getItemId() {
		return ObjectConverter.convertLongToItemId(this.id);
	}

	public void add(IRFC rfc) {
		rfc.setParent(this);
		this.rfcs.add(rfc);
	}

	public void addFirst(IRFC rfc) {
		rfc.setParent(this);
		this.rfcs.add(0, rfc);
	}

	public void setRfcs(List<IRFC> rfcs) {
		for (IRFC rfc : rfcs) {
			add(rfc);
		}
		// this.rfcs = rfcs;
	}

	public List<IRFC> getRfcs() {
		return (this.rfcs);
	}

	public String getSummary() {
		return ("UNKOWN: OVERIDE in " + this.getClass());
	}

	/**
	 * {{{ Hibernate setter/Getters!!! Should/Could be done with Hibernate
	 * PropertyAccessor's
	 * 
	 */

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return (this.id);
	}

	public void setParentId(Long id) {
		this.parentId = id;
	}

	public Long getParentId() {
		return (this.parentId);
	}

	public void setTargetCIId(Long id) {
		this.targetCIId = id;
	}

	public Long getTargetCIId() {
		return (this.targetCIId);
	}
	

	/*
	 * Hibernate }}}
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("id=<" + this.id + ">");

		buffer.append(" target=");
		if (getTargetId() != null) {
			buffer.append("<id:" + getTargetId() + ">");
		} else if (getTargetAlias() != null) {
			buffer.append("<alias:" + getTargetAlias() + ">");
		} else if (getParent() != null) {
			buffer.append("<parent:" + getParent().getId() + ">");
		} else {
			buffer.append("<unknown!>");
		}
		buffer.append(" txid=<" + txId + ">");
		return (buffer.toString());
	}

}
