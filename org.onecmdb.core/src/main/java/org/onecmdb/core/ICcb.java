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

import java.util.List;

import org.onecmdb.core.internal.ccb.RfcQueryCriteria;

/**
 * <p>The <em>Change Control Board</em> (CCB) is responsible, and take actions,
 * for change requests (RFCs) within OneCMDB. Modifications are supposed to be 
 * handled asynchronously leaving it up to the CCB implementation to perform the
 * changes in the background. For example, a change can involve a acknowledge 
 * from a third party that is not available at the time of the change 
 * request.</p>
 * 
 * <p>Changes are grouped together in <em>transactions</em>, where <em>ALL</em>
 * changes are committed or rejected. All changes is done from new objects so 
 * in-memory objects will not be affected by committed changes. This requires
 * clients to not keep caches of memory objects.</p>
 * 
 * <p>Example usage:</p>
 * 
 * <p>Create a new offspring from a CI as base:</p>
 * <pre>	
 * ISession session = ...
 * ICi ci = ...
 * ICcb ccb = (ICcb)ISession.getService(ICcb.class);
 * ICmdbTransaction tx = ccb.getTx(session);
 * ICiModifiable mod =  tx.getModifiableCi(ci);
 * mod.createOffspring();
 * ITicket ticket = ccb.submitTx(tx);
 * IRfcResult result = ccb.waitForTicket(ticket);
 * if (result.isRejetced() {
 * String cause = result.getRejectedCause();
 * return(-1);
 * }
 * </pre>
 *
 * @see ICmdbTransaction
 */
public interface ICcb extends IService {

	ICmdbTransaction getTx(ISession session);

	ITicket submitTx(ICmdbTransaction tx);

	IRfcResult waitForTx(ITicket ticket);

	// Change history methods.
	List<IRFC> findRFCForCi(ICi ci);

	ICmdbTransaction findTxForRfc(IRFC rfc);

	List<IRFC> queryRFCForCi(ICi ci, RfcQueryCriteria crit);

	int queryRFCForCiCount(ICi ci, RfcQueryCriteria criteria);
	
	void addChangeListener(ICCBListener listener);
	boolean removeChangeListener(ICCBListener listener);
	
}
