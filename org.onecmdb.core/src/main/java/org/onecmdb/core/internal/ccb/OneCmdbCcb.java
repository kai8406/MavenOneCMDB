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
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICCBListener;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.storage.IDaoReader;
import org.onecmdb.core.internal.storage.IDaoWriter;
import org.onecmdb.core.utils.xml.BeanCache;
import org.springframework.context.MessageSource;

public class OneCmdbCcb implements ICcb {

	private IDaoWriter writer;

	private IDaoReader reader;

	private List<IRfcWorker> rfcWorkers;

	private MessageSource messageSource;

	private ICmdbTransaction bootUp;

	private boolean syncronicMode = true;
	
	private ISession session;

	private List<ICCBListener> ccbListeners = new ArrayList<ICCBListener>();
	
	private Log log = null;

	
	
	public ISession getSession() {
		return session;
	}

	public void setSession(ISession session) {
		this.session = session;
	}

	public void setRfcWorkers(List<IRfcWorker> workers) {
		this.rfcWorkers = workers;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setSyncronic(boolean value) {
		this.syncronicMode = value;
	}

	public void setDaoReader(IDaoReader reader) {
		this.reader = reader;
	}

	public void setDaoWriter(IDaoWriter writer) {
		this.writer = writer;
	}

	public void setStartupTransaction(ICmdbTransaction boot) {
		this.bootUp = boot;
	}
	
	public void addChangeListener(ICCBListener listener) {
		if (ccbListeners.contains(listener)) {
			return;
		}
		ccbListeners.add(listener);
	}

	public boolean removeChangeListener(ICCBListener listener) {
		boolean exists = ccbListeners.remove(listener);
		return(exists);
	}

	public void init() {
		
		getLogger().info("OneCMDB CCB startup.");
		
		
		ICmdbTransaction trans = reader.getTransaction(bootUp.getId());
		if (trans == null) {
			getLogger().info("Start boot up sequence");
			debug(dumpTransaction(bootUp));
			getSession().login();
			if (bootUp instanceof CmdbTransaction) {
				((CmdbTransaction)bootUp).setSession(getSession());
			}
			rfc(bootUp);
			
			getSession().logout();
			
			// writer.storeTransaction(bootUp);
			// processTransaction(bootUp);
		} else {
			// Already Started.
			getLogger().info("Boot up sequence already performed.");
		}
		
		getLogger().info("OneCMDB CCB startup completeted.");
	}

	
	
	public ICmdbTransaction getTx(ISession session) {
		// TODO: Validate credentials.
		CmdbTransaction tx = new CmdbTransaction();
	
		// Only here we can set the Issue's session.
		tx.setSession(session);
		
		
		return (tx);
	}

	public ITicket submitTx(ICmdbTransaction tx) {
		ITicket ticket = this.rfc(tx);
		return (ticket);
	}

	/**
	 * Persite the transaction/ and all it's rfc's.
	 */
	private ITicket rfc(ICmdbTransaction tx) {
		
		// debug(dumpTransaction(tx));
		//updateTxId(tx, tx.getRfcs());
		if (syncronicMode) {
			tx.setInsertTs(new Date());
			tx.setStatus(ICmdbTransaction.REGISTERED_STATE);
			// For now same thread thread
			processTransaction(tx);
		} else {
			tx.setInsertTs(new Date());
			tx.setStatus(ICmdbTransaction.REGISTERED_STATE);
			writer.storeTransaction(tx);
		}

		return (new CmdbTransactionTicket(tx.getId()));
	}

	private IRfcWorker getRfcWorker(IRFC rfc) {
		if (this.rfcWorkers == null) {
			return (null);
		}
		for (IRfcWorker worker : this.rfcWorkers) {
			if (worker.handleRfc(rfc)) {
				return (worker);
			}
		}
		return (null);
	}
	
	/*
	private void updateTxId(ICmdbTransaction tx, List<IRFC> rfcs) {
		for (IRFC rfc : rfcs) {
			// System.out.println("SET TX[" + tx.getId()+ " to RFC[" +
			// rfc.toString() + "]");
			rfc.setTxId(tx.getId().asLong());
			updateTxId(tx, rfc.getRfcs());
		}
	}
	*/
	
	private void processTransaction(ICmdbTransaction tr) {
		tr.setBeginTs(new Date());
		log.info("PROCESS TX START - " + tr);
		IObjectScope scope = new CcbObjectScope(tr.getSession(), this.reader);
		try {
			IRfcResult result = processRfcs(tr, tr.getRfcs(), scope);
			if (result.isRejected()) {
				tr.setStatus(ICmdbTransaction.REJECTED_STATE);
				tr.setRejectCause(result.getRejectCause());
			} else {
				tr.setStatus(ICmdbTransaction.COMMITED_STATE);
			}
		} catch (Throwable t) {
			log.error("CMDB TX Internal Error:", t);
			t.printStackTrace();
			tr.setStatus(ICmdbTransaction.REJECTED_STATE);
			tr.setRejectCause("Internal Error: " + t.toString());
		} finally {
			tr.setEndTs(new Date());
			if (tr.getStatus() == ICmdbTransaction.REJECTED_STATE) {
				log.info("PROCESS RFC REJECTED - " + tr);;

				writer.rejectTransaction(tr);
			} else {
				log.info("PROCESS RFC DONE - " + tr);;
				tr.setCiModified(scope.getCiModified());
				tr.setCiAdded(scope.getCiAdded());
				tr.setCiDeleted(scope.getCiDeleted());
				
				writer.commitTransaction(scope, tr);
				
				fireChangeEvent(scope);
				
				
				// TODO: handle this through notification.
				// Easier to do this by a static call!
				BeanCache.getInstance().invalidate(scope);
		
			}
			log.info("PROCESS TX END - " + tr);;
		}

	}

	private void fireChangeEvent(IObjectScope scope) {
		for (ICCBListener listener: ccbListeners) {
			listener.onChange(scope);
		}
	}

	private IRfcResult processRfcs(ICmdbTransaction tx, List<IRFC> rfcs,
			IObjectScope scope) {
		for (IRFC rfc : rfcs) {
			// Check if set, could be a potential problem
			// to modify ts, but the tx will hold 
			// excatly when this rfc was performed and
			// that can not be set by the client.
			if (rfc.getTs() == null) {
				rfc.setTs(new Date());
			}
			
			rfc.setTxId(tx.getId().asLong());
			
			IRfcWorker worker = getRfcWorker(rfc);
			/*
			System.out.println("PROCESS RFC[" + rfc + "] --> WORKER[" + worker
					+ "]");
			*/
			if (worker == null) {

				RfcResult result = new RfcResult();
				result.setRejectCause("ERROR CODE: No worker found for rfc["
						+ rfc.getClass().getSimpleName() + "]");
				/*
				 * String message = messageSource.getMessage(
				 * "org.onecmdb.ccb.error.norfcworker", new Object[]
				 * {rfc.getClass().getName()}, "No RFC Worker found for class
				 * {0}", Locale.getDefault() )
				 */
				return (result);
			}

			IRfcResult result = worker.perform(rfc, scope);
			if (result.isRejected()) {
				log.warn("\tREJECTED : RFC[" + rfc + "]  : "
						+ result.getRejectCause());
				
				return (result);
			}
			log.debug("\tPROCESSED RFC[" + rfc + "] TXID["
					+ rfc.getTxId() + "]");
			// An RFC can generet new RFC's, need to update TXid.
			if (rfc.getRfcs().size() > 0) {
				//updateTxId(tx, rfc.getRfcs());
				result = processRfcs(tx, rfc.getRfcs(), scope);
				if (result.isRejected()) {
					return (result);
				}
			}
		}
		return (new RfcResult());

	}

	private void debug(String msg) {
		getLogger().debug(msg);
	}

	private String dumpTransaction(ICmdbTransaction tx) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TX[" + tx.getId() + "]");
		buffer.append(" name=" + tx.getName());
		buffer.append("\n");
		buffer.append(" status=" + tx.getStatus());
		buffer.append("\n");
		buffer.append(" issuer=" + tx.getIssuer());
		buffer.append("\n");
		buffer.append(" inserted=" + tx.getInsertTs());
		buffer.append("\n");
		buffer.append(" begined=" + tx.getBeginTs());
		buffer.append("\n");
		buffer.append(" ended=" + tx.getEndTs());
		buffer.append("\n");
		buffer.append("---- RFCS[" + tx.getRfcs().size() + "]----");
		buffer.append("\n");
		buffer.append(dumpRfcs(tx.getRfcs(), " "));
		buffer.append("---- RFCS ----");
		buffer.append("\n");
		return (buffer.toString());
	}

	private String dumpRfcs(List<IRFC> rfcs, String pad) {
		StringBuffer buffer = new StringBuffer();

		for (IRFC rfc : rfcs) {
			buffer.append(pad);
			buffer.append(rfc.toString() + "\n");
			if (rfc.getRfcs().size() > 0) {
				buffer.append(dumpRfcs(rfc.getRfcs(), pad + " "));
			}
		}
		return (buffer.toString());
	}

	public ICi getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	private long DEFAULT_TIMEOUT = 60000;

	public IRfcResult waitForTx(ITicket ticket) {
		if (ticket instanceof CmdbTransactionTicket) {
			long startTime = System.currentTimeMillis();
			while (true) {
				ICmdbTransaction tx = reader
						.getTransaction(((CmdbTransactionTicket) ticket)
								.getTxId());
				if (tx == null) {
					throw new IllegalStateException("Transaction ticket["
							+ ticket.toString() + "] is not registered!");
				}
				RfcResult result = new RfcResult();
				result.setTxId(tx.getId().asLong());
				result.setIssuer(tx.getIssuer());
				result.setStart(tx.getBeginTs());
				result.setStop(tx.getEndTs());
				result.setCiDeleted(tx.getCiDeleted());
				result.setCiAdded(tx.getCiAdded());
				result.setCiModified(tx.getCiModified());
				
				if (tx.getStatus() == ICmdbTransaction.COMMITED_STATE) {
					return (result);
				}
				if (tx.getStatus() == ICmdbTransaction.REJECTED_STATE) {
					result.setRejectCause(tx.getRejectCause());
					return (result);
				}

				// TODO: How to handle exceptions....
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new IllegalArgumentException(
							"INTERUPPTED: The wait fro transaction has been interrupted");
				}
				if ((System.currentTimeMillis() - startTime) > DEFAULT_TIMEOUT) {
					throw new IllegalArgumentException(
							"TIMEOUT: The transaction has not been proceesed yet");
				}

			}
		}
		throw new IllegalArgumentException("Not a correct transaction ticket["
				+ ticket.toString() + "]");
	}

	public void close() {
		getLogger().info("Closing OneCMDB...");

		for (IRfcWorker wrk : this.rfcWorkers) {

		}

		getLogger().info("OneCMDB has now been closed.");

	}

	private Log getLogger() {
		if (this.log == null) {
			this.log = LogFactory.getLog(this.getClass()); 
		}
		return(this.log);
	}

	public List<IRFC> findRFCForCi(ICi ci) {
		List<IRFC> list = reader.findRFCForCi(ci.getId());
		return (list);
	}

	public ICmdbTransaction findTxForRfc(IRFC rfc) {
		ICmdbTransaction tx = reader.getTransaction(new ItemId(rfc.getTxId()));
		return (tx);
	}

	public List<IRFC> queryRFCForCi(ICi ci, RfcQueryCriteria crit) {
		QueryResult<IRFC> rfcs = reader.queryRfc(ci, crit, false);
		return(rfcs);
	}
	
	public int queryRFCForCiCount(ICi ci, RfcQueryCriteria crit) {
		QueryResult<IRFC> rfcs = reader.queryRfc(ci, crit, true);
		return(rfcs.getTotalHits());
	}

	public void addChangeListsner(ICCBListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	
}
