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
package org.onecmdb.core.utils.xpath.commands;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.utils.xpath.IOneCMDBContentGenerator;
import org.onecmdb.core.utils.xpath.model.ICmdbObjectDestruction;

/**
 * Delete Command implementation
 *
 */
public class DeleteCommand extends AbstractPathCommand {
	// Specific arguments to Update Command.
	private String inputAttributes;
	
	// TODO: use spring to do this.
	private HashMap<String, IOneCMDBContentGenerator> generatorMap = new HashMap<String, IOneCMDBContentGenerator>();
	private HashMap<String, String> attributeMap = null;

	private ICmdbTransaction tx;
	
	public DeleteCommand(IOneCmdbContext context) {
		super(context);
	}
	

	/**
	 * How the content should ibe interperted.
	 * 
	 * For now it's always plain text.
	 * @return
	 */
	public String getContentType() {
		return("text/plain");
	}
	
	protected void setupTX() {
		// Setup the tx.
		ISession session = getCurrentSession();
		if (session == null) {
			throw new IllegalAccessError("Not logged in.");
		}
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		
		// The TX is stored in the session.
		this.tx = ccb.getTx(session);
		getDataContext().put("tx", this.tx);
	
	}
	
	protected void processTX() {
		ISession session = getCurrentSession();
		if (session == null) {
			throw new IllegalAccessError("Not logged in.");
		}
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		
		// Commit the tx.	
		if (this.tx == null) {
			throw new IllegalArgumentException("No tx setup!");
		}
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			throw new IllegalAccessError("Rejected:" + result.getRejectCause());
		}
		
	}
	
	/**
	 * Transfer the content to the stream.
	 * Execute the update.
	 * 
	 * @param out
	 */
	public void transfer(OutputStream out) {
		setupTX();
		
		Iterator<Pointer> iter = getPathPointers();
		while(iter.hasNext()) {
			Pointer p = (Pointer)iter.next();
			Object value = p.getValue();
			if (value instanceof ICmdbObjectDestruction) {
				((ICmdbObjectDestruction)value).destory();
			} else {
				throw new IllegalArgumentException("Not a valid item to delete");
			}
		}
		
		// Process the TX.
		processTX();
		
	}
}
