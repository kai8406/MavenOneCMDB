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
package org.onecmdb.core.internal.storage.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.ccb.AttributeModifiable;
import org.onecmdb.core.internal.ccb.CiModifiable;
import org.onecmdb.core.internal.ccb.CmdbTransaction;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.ccb.rfc.RFC;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.storage.IDaoReader;
import org.onecmdb.core.internal.storage.IDaoWriter;
import org.onecmdb.core.internal.storage.expression.OneCMDBExpression;
import org.onecmdb.core.tests.profiler.Profiler;
import org.springframework.dao.ConcurrencyFailureException;

public class HibernateDao2  {//implements IDaoReader2, IDaoWriter2 {

	private SessionFactory sf;

		
	private String namespace = "oneCMDB";

	private volatile boolean sessionLocked;

	private Log log = LogFactory.getLog(this.getClass());

	
	private TestCache aliasCache = new TestCache();
	private TestCache idCache = new TestCache();
	
	public void setSessionFactory(SessionFactory sf) {
		this.sf = sf;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return (this.namespace);
	}


	public void destory() {
		log.info("HIBERNATE DAO: Shutdown started");
		sf.close();
		log.info("HIBERNATE DAO: Shutdown completed");
	}

	/*
	public void setInterceptor(DaoReaderInterceptor interceptor) {
		if (interceptor instanceof DaoReaderInterceptor) {
			((DaoReaderInterceptor) interceptor).setDaoReader(this);
		}
	}
	*/

	/**
	 * Retrive a session. There will only exists only ONE open session per
	 * application. This means that the closeSession() MUST be called once a
	 * getSession() has been called, else the system will be blocked!
	 * 
	 * @return
	 */
	private Session getSession(ISession s) {
		// Lock here
		synchronized (sf) {
			while (sessionLocked) {
				try {
					sf.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new ConcurrencyFailureException(
							"Session wait got interrupted", e);
				}
			}
			
			// Create Session interceptor...
			DaoSessionReaderInterceptor inter = new DaoSessionReaderInterceptor();
			//inter.setDaoReader(this);
			inter.setSession(s);
			
			Session session = sf.openSession(inter);
			
			//sessionLocked = true;
			return (session);
		}

	}

	private void closeSession(Session session) {
		synchronized (sf) {
			try {
				session.close();
			} finally {
				// Relese lock...
				//sessionLocked = false;
				sf.notifyAll();
			}
		}
	}

	private Map<ItemId, ICi> mapdb = new HashMap<ItemId, ICi>();

	private Queue<ICmdbTransaction> txqueue = new LinkedList<ICmdbTransaction>();


	

	/**
	 * Transaction handling....
	 */
	public void storeTransaction(ICmdbTransaction cmdbtx) {
		/*
		 * Session session = getSession(); Transaction tx = null; try {
		 * 
		 * tx = session.beginTransaction(); for (IRFC rfc : cmdbtx.getRfcs()) {
		 * session.save(rfc); } session.save(cmdbtx); tx.commit(); } catch
		 * (HibernateException he) { if (tx!=null) { tx.rollback(); } throw he; }
		 * finally { closeSession(session); }
		 */
	}

	/*
	public ICmdbTransaction getTransaction(ItemId id) {
		Session session = getSession();
		try {
			Object o = session.get(CmdbTransaction.class, ((ItemId) id)
					.asLong());
			if (o instanceof ICmdbTransaction) {
				return ((ICmdbTransaction) o);
			}
			return (null);
		} finally {
			closeSession(session);
		}
	}
	*/
	
	public void rejectTransaction(ICmdbTransaction cmdbTx) {
		idCache.clear();
		aliasCache.clear();
		
		Session session = getSession(cmdbTx.getSession());
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			cmdbTx.setEndTs(new Date());
			storeTx(session, cmdbTx);
			tx.commit();
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}
			throw he;
		} finally {
			closeSession(session);
		}

	}

	private void storeTx(Session session, ICmdbTransaction cmdbTx) {
		storeRFCs(session, cmdbTx, cmdbTx.getRfcs());
		session.save(cmdbTx);
	}

	private void storeRFCs(Session session, ICmdbTransaction tx, List<IRFC> rfcs) {
		for (IRFC rfc : rfcs) {
			// Update tx id.
			rfc.setTxId(tx.getId().asLong());
			// Don't save Attribute/Ci modifiable.
			if (!(
				  rfc.getClass().equals(CiModifiable.class) 
					|| 
				  rfc.getClass().equals(AttributeModifiable.class)
				  )) {
					session.save(rfc);
			}
			if (rfc.getRfcs().size() > 0) {
				storeRFCs(session, tx, rfc.getRfcs());
			}
		}
	}

	public void commitTransaction(IObjectScope scope, ICmdbTransaction cmdbTx) {
		idCache.clear();
		aliasCache.clear();
		
		Session session = null;
		Transaction tx = null;
		Profiler.start("commitTx()");
		try {
			session = getSession(cmdbTx.getSession());
			tx = session.beginTransaction();
			for (ICi item : scope.getDestroyedICis()) {
				session.delete(item);
			}
			for (ICi item : scope.getNewICis()) {
				session.save(item);
			}
			for (ICi item : scope.getModifiedICis()) {
				session.update(item);
			}
			cmdbTx.setEndTs(new Date());
			storeTx(session, cmdbTx);// session.update(cmdbTx);
			tx.commit();
			
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}
			throw he;
		} finally {
			closeSession(session);
			
			Profiler.stop("commitTx()");
		}
	}
	
	public List queryCriteria(ISession s, DetachedCriteria detachedCrit, PageInfo info) {
		Session session = getSession(s);
		List result = Collections.EMPTY_LIST;
		try {
			Profiler.start("QueryCriteria():");
			Criteria criteria = detachedCrit.getExecutableCriteria(session);
			criteria.addOrder(Order.asc("alias"));
			if (info != null) {
				if (info.getFirstResult() != null) {
					criteria.setFirstResult(info.getFirstResult());
				}
				if (info.getMaxResult() != null) {
					criteria.setMaxResults(info.getMaxResult());
				}
			}
			result = criteria.list();
		} finally {
			Profiler.stop();
			closeSession(session);
		}
		return(result);
	}
}
