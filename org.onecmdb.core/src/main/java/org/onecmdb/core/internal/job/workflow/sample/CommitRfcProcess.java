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
package org.onecmdb.core.internal.job.workflow.sample;

import java.util.List;

import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;

public class CommitRfcProcess extends WorkflowProcess {
	
	public void run() throws Throwable {
		ISession session = (ISession) data.get("session");
		List<IRFC> rfcs = (List<IRFC>)in.get("rfcs");
		
		if (rfcs == null || rfcs.size() == 0) {
			out.put("ok", "true");
			return;
		}
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		tx.setRfc(rfcs);
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		out.put("result", result);
		if (result.isRejected()) {
			out.put("ok", "false");
			out.put("cause", result.getRejectCause());
		} else {
			out.put("ok", "true");
		}
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}
}
