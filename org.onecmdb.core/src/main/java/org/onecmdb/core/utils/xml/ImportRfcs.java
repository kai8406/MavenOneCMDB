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
package org.onecmdb.core.utils.xml;

import java.util.List;

import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;

public class ImportRfcs {

	private ISession session;

	private List<IRFC> rfcs;

	public void setSession(ISession session) {
		this.session = session;
	}

	public void setRfcs(List<IRFC> rfcs) {
		this.rfcs = rfcs;
	}

	public void run() {
		if (session == null) {
			throw new IllegalArgumentException("No session set to BeanScope!");
		}
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		tx.setRfc(rfcs);
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			System.out.println("REJECTED: " + result.getRejectCause());
		} else {
			System.out.println("COMMITED TICKET_ID=" + ticket);
		}
	}

}
