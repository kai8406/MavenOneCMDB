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
package org.onecmdb.core;

import java.util.Date;
import java.util.List;

import org.onecmdb.core.internal.model.ItemId;

/**
 * The CmdbTransaction defines a "unit of work" for a client. It contains one or
 * more RFC to be preformed in the same transaction. Each transaction is
 * performed in sequence,
 * 
 * 
 */
public interface ICmdbTransaction {

	public static int REGISTERED_STATE = 0x01;

	public static int PROCESSING_STATE = 0x02;

	public static int COMMITED_STATE = 0x04;

	public static int REJECTED_STATE = 0x08;

	public ItemId getId();

	public void add(IRFC rfc);

	public void setRfc(List<IRFC> rfcs);

	public List<IRFC> getRfcs();

	public Date getBeginTs();

	public void setBeginTs(Date beginTs);

	public Date getEndTs();

	public void setEndTs(Date endedTs);

	public Date getInsertTs();

	public void setInsertTs(Date insertTs);

	public String getIssuer();

	public String getName();

	public void setName(String name);

	public int getStatus();

	public void setStatus(int status);

	public void setRejectCause(String message);

	public String getRejectCause();

	public ICiModifiable getTemplate(ICi ci);

	public IAttributeModifiable getAttributeTemplate(IAttribute attribute);

	public ISession getSession();
	
	public Integer getCiModified();
	public void setCiModified(Integer ciModified);
	public Integer getCiDeleted();
	public void setCiDeleted(Integer ciDeleted);
	public Integer getCiAdded();
	public void setCiAdded(Integer ciAdded);
	

}
