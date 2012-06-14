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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tuple.IdentifierProperty;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IValue;
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
import org.onecmdb.core.utils.xml.BeanCache;
import org.springframework.dao.ConcurrencyFailureException;

public class HibernateDao implements IDaoReader, IDaoWriter {

	private SessionFactory sf;

		
	private String namespace = "oneCMDB";

	private volatile boolean sessionLocked;

	private Log log = LogFactory.getLog(this.getClass());

	private int flushCount = 20;
	
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


	public int getFlushCount() {
		return flushCount;
	}

	public void setFlushCount(int flushCount) {
		this.flushCount = flushCount;
	}

	public void destory() {
		log.info("HIBERNATE DAO: Shutdown started");
		/*
		 * No need to do anything here, Spring shutdowns the SessionFactory for
		 * us... Will be a problem if we are using In Memory HSQL-DB because we
		 * nee to send SHUTDOWN to it to actually close it!!!! We can't do it
		 * here because spring has shutdown the sf factory for us!
		 */
		if (false) {
			Session session = getSession();
			try {
				try {
					session.connection().createStatement().execute("SHUTDOWN");
				} catch (HibernateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("Can't create statement 'SHUTDOWN' to DB", e);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("Can't create statement 'SHUTDOWN' to DB", e);
				}
			} finally {
				closeSession(session);
			}
		} else {
			sf.close();
		}
		log.info("HIBERNATE DAO: Shutdown completed");
	}

	public void setInterceptor(DaoReaderInterceptor interceptor) {
		if (interceptor instanceof DaoReaderInterceptor) {
			((DaoReaderInterceptor) interceptor).setDaoReader(this);
		}
	}

	/**
	 * Retrive a session. There will only exists only ONE open session per
	 * application. This means that the closeSession() MUST be called once a
	 * getSession() has been called, else the system will be blocked!
	 * 
	 * @return
	 */
	private Session getSession() {
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
			
			Session session = sf.openSession();
			
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

	public Set<ICi> findByTemplate(ICi template) {
		return null;
	}

	
	public ICi findCiByAlias(IPath<String> path) {
		if (path == null) {
			return(null);
		}
		String name = path.getLeaf();
		
		// Test cache..
		ICi ci = (ICi) aliasCache.get(name);
		if (ci != null) {
			return(ci);
		}
		
		/*
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("alias", name);
		// TODO: Fix this to select better, noe attribute with alias will also be found!
		Profiler.start("FindByAlias(" + name + ")");
		List list = this.query(ConfigurationItem.class, crit);
		Profiler.stop();
		*/
		/*
		Profiler.start("FindByAlias(" + name + ")");
		List list = this.hqlQuery("from ConfigurationItem ci where ci.class = ConfigurationItem and ci.alias = '" + name + "'");
		Profiler.stop();
		*/	
		/*
		 Profiler.start("FindByAlias(" + name + ")");
		List list = this.hqlQuery("from ConfigurationItem ci where ci.alias = '" + name + "'");
		Profiler.stop();
		*/	
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("alias", name);
		List list = query(ConfigurationItem.class, crit);
		//List list = this.sqlQuery("select {ci.*} from CI {ci} where alias = '" + name + "'");
		
		
		if (list.size() == 1) {
			ci = (ICi) list.get(0);
			
			aliasCache.put(name, ci);
			
			return(ci);
		}
	
		if (list.size() > 1) {
			log.error("Ci alias '" + name + "' found more than once " + list.size());
			return((ICi)list.get(0));
		}
		return (null);
	}
	
	public IAttribute findAttributeById(ItemId id) {
		
		if (id == null) {
			return(null);
		}
		
		Session session = getSession();
		try {
			Object o = null;
		
			IAttribute attribute = (IAttribute) idCache.get(id);
			if (attribute != null) {
				return(attribute);
			}
			Profiler.start("findAttributeById(" + id.toString() + ")");
			
			o = session.get(BasicAttribute.class, new Long(((ItemId) id).asLong()));
			
			Profiler.stop();
			
			if (o instanceof IAttribute) {				
				idCache.put(id, o);
				return ((IAttribute) o);
			}
			return (null);
		} finally {
			closeSession(session);
		}
	}

	
	public ICi findById(ItemId id) {
		
		if (id == null) {
			return(null);
		}
		
		// Check cache, testing, should use hibernates....		
		ICi ci = (ICi) idCache.get(id);
		if (ci != null) {
			return(ci);
		}
			
		
		Session session = getSession();
		try {
			Object o = null;
			
			/*
			o = session
					.get(BasicAttribute.class, ((ItemId) id).asLong());
			if (o instanceof ICi) {
				Profiler.stop();
				return ((ICi) o);
			}
			*/
			
			Profiler.start("findICiById(" + id.toString() + ")");
				
			o = session.get(ConfigurationItem.class, ((ItemId) id).asLong());
			
			Profiler.stop();
			
			if (o instanceof ICi) {
				
				idCache.put(id, o);
				
				return ((ICi) o);
			}
			
			
			Profiler.start("findIAttById(" + id.toString() + ")");
			o = session.get(BasicAttribute.class, ((ItemId) id).asLong());
			Profiler.stop();
			
			if (o instanceof ICi) {				
				idCache.put(id, o);
				return ((ICi) o);
			}
			return (null);
		} finally {
			closeSession(session);
		}
		/*
		 * ICi ci = mapdb.get(id); if (ci instanceof ConfigurationItem) {
		 * ((ConfigurationItem)ci).setDaoReader(this); } // TODO: REade from
		 * database...
		 * 
		 * return(ci);
		 */
	}

	public List queryHQL(String hql) {
		// Quering the db.
		Session session = getSession();
		try {
		     Query ciQuery = session.createQuery(hql);
		     List list = ciQuery.list();
		     return(list);
		} finally {
			closeSession(session);
		}
		
	}
	
	// Query the db about things.
	public List sqlQuery(String sql) {
		// Quering the db.
		Session session = getSession();
		try {
			 Query query = session.createSQLQuery(sql).addEntity("ci", ConfigurationItem.class);
			 query.setReadOnly(true);
		     List list = query.list();
		     return(list);
		} finally {
			closeSession(session);
		}
		
	}
	
	// Query the db about things.
	public List hqlQuery(String hql) {
		// Quering the db.
		Session session = getSession();
		try {
			 Query query = session.createQuery(hql);
			 query.setReadOnly(true);
		     List list = query.list();
		     return(list);
		} finally {
			closeSession(session);
		}
		
	}

	public List query(String entityName, HashMap<String, Object> map) {
		// Quering the db.
		Session session = getSession();
		try {
			Profiler.start("query(" + entityName +")");
			Criteria criteria = session.createCriteria(entityName);
			
			for (String key : map.keySet()) {
				Object value = map.get(key);
				if (value == null) {
					criteria.add(Expression.isNull(key));
				} else {
					criteria.add(Expression.eq(key, map.get(key)));
				}
			}
			
			List objects = criteria.list();
			return (objects);
		} finally {
			Profiler.stop();
			closeSession(session);
		}
	}
	

	public List query(Class clazz, HashMap<String, Object> map) {
		StringBuffer profileInfo = null;
		if (Profiler.isOn()) {
			profileInfo = new StringBuffer();
			profileInfo.append("query(");
			profileInfo.append(clazz.getSimpleName());
			for (String key : map.keySet()) {
				profileInfo.append(",");
				profileInfo.append(key);
				profileInfo.append("=");
				profileInfo.append(map.get(key));
			}
			profileInfo.append(")");
		}
		// Quering the db.
		Session session = getSession();
		try {
			if (Profiler.isOn()) {
				Profiler.start(profileInfo.toString());
			}
			
			Criteria criteria = session.createCriteria(clazz);
			
			for (String key : map.keySet()) {
				Object value = map.get(key);
				if (value == null) {
					criteria.add(Expression.isNull(key));
				} else {
					criteria.add(Expression.eq(key, map.get(key)));
				}
			}
			List objects = criteria.list();
			return (objects);
		} finally {
			Profiler.stop();
			closeSession(session);
		}
	}

	public Set<ICi> getAttributeOffsprings(ItemId id) {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("derivedFromId", id.asLong());		
		Profiler.start("getAttributeOffspring(" + id + ")");
		List list = query(BasicAttribute.class, crit);
		Profiler.stop("getAttributeOffspring(" + id + ")");
		return (new HashSet<ICi>(list));
	}
	
	public Set<ICi> getOffsprings(ItemId id) {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("derivedFromId", id.asLong());		
		Profiler.start("getOffspring(" + id + ")");
		List list = query(ConfigurationItem.class, crit);
		Profiler.stop();
		return (new HashSet<ICi>(list));
	}

	public Set<IAttribute> getAttributesFor(ItemId id) {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("ownerId", id.asLong());
		Profiler.start("getAttributesFor(" + id.toString() + ")");
		List list = query(BasicAttribute.class, crit);
		Profiler.stop();
		return (new HashSet<IAttribute>(list));
	}

	

	public ICmdbTransaction findTransaction() {
		return (null);
	}

	public List<IRFC> findRFCForCi(ItemId ciid) {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("targetId", ciid.asLong());
		List list = query(RFC.class, crit);
		return (list);
	}

	public void updateTransaction(ICmdbTransaction tx) {
	}

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

	public List<IRFC> getRfcsForCmdbTx(ItemId id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void flushTransaction(ICmdbTransaction cmdbTx) {
		Session session = getSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			cmdbTx.setEndTs(new Date());
			//storeTx(session, cmdbTx);
			session.save(cmdbTx);
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
	
	public void rejectTransaction(ICmdbTransaction cmdbTx) {
		idCache.clear();
		aliasCache.clear();
		
		flushTransaction(cmdbTx);
	}

	private void storeRFC(Session session, ICmdbTransaction cmdbTx) {
		int totalCount = countRFC(cmdbTx.getRfcs());
		storeRFCs(session, cmdbTx, cmdbTx.getRfcs(), 0, totalCount);
		//session.save(cmdbTx);
	}

	private int countRFC(List<IRFC> rfcs) {
		int count = 0;
		for (IRFC rfc : rfcs) {
			if (!(
				  rfc.getClass().equals(CiModifiable.class) 
					|| 
				  rfc.getClass().equals(AttributeModifiable.class)
				  )) {
	
				count++;
			}
			if (rfc.getRfcs().size() > 0) {
				count += countRFC(rfc.getRfcs());
			}
		}
		return(count);
	}

	private void storeRFCs(Session session, ICmdbTransaction tx, List<IRFC> rfcs, int count, int totalCount) {
		for (IRFC rfc : rfcs) {
			// Update tx id.
			rfc.setTxId(tx.getId().asLong());
			// Don't save Attribute/Ci modifiable.
			if (!(
				  rfc.getClass().equals(CiModifiable.class) 
					|| 
				  rfc.getClass().equals(AttributeModifiable.class)
				  )) {
	
				count++;
				if ((count % flushCount) == 0 ) {
					session.flush();
					session.clear();
					log.info("RFC: flush[" + count + "/" + totalCount + "]");
				}
				session.save(rfc);
			}
			if (rfc.getRfcs().size() > 0) {
				storeRFCs(session, tx, rfc.getRfcs(), count, totalCount);
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
			session = getSession();
			tx = session.beginTransaction();
			int count = 0;
			for (ICi item : scope.getDestroyedICis()) {
				session.delete(item);
				count++;
				if ((count % flushCount) == 0 ) {
					session.flush();
					session.clear();
					log.info("DELETE-CI: flush[" + count + "/" + scope.getDestroyedICis().size() + "]");
				}
			}
			count = 0;
			for (ICi item : scope.getNewICis()) {
				session.save(item);
				count++;
				if ((count % flushCount) == 0 ) {
					session.flush();
					session.clear();
					log.info("NEW-CI: flush[" + count + "/" + scope.getNewICis().size() + "]");
				}
			}
			count = 0;
			for (ICi item : scope.getModifiedICis()) {
				session.update(item);
				count++;
				if ((count % flushCount) == 0 ) {
					session.flush();
					session.clear();
					log.info("UPDATE-CI: flush[" + count + "/" + scope.getModifiedICis().size() + "]");
				}
			}
			cmdbTx.setEndTs(new Date());
			storeRFC(session, cmdbTx);// session.update(cmdbTx);
			tx.commit();
			
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}
			cmdbTx.setRejectCause("Store data to backend failed: " + he.toString());
			
			throw he;
		} finally {
			closeSession(session);
			flushTransaction(cmdbTx);
			Profiler.stop("commitTx()");
		}
		
		
	}

	/**
	 * TODO: Need to redo this for performance....
	 */
	public List<IAttribute> getSourceReference(ICi ci) {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("valueAsString", ci.getAsString());
		crit.put("alias", "source"); 
		List list = query(BasicAttribute.class, crit);
		return (list);
	}
	
	public List<IAttribute> getTargetReference(ICi ci) {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("valueAsString", ci.getAsString());
		crit.put("alias", "target"); 
		List list = query(BasicAttribute.class, crit);
		return (list);
	}
	
	public List<IAttribute> getAttributesReferringTo(ICi ci)  {
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("valueAsString", ci.getAsString()); 
		List list = query(BasicAttribute.class, crit);
		return (list);
	}
	
	public QueryResult<ICi> query(QueryCriteria criteria, boolean count) {
		DetachedCriteria hibCiCriteria = DetachedCriteria.forClass(ConfigurationItem.class);
		DetachedCriteria hibAttributeCriteria = DetachedCriteria.forClass(BasicAttribute.class);
			
		// Search in the db...
		if (criteria.getOffspringOfId() != null) {
			try {
				// Query for an unique id.
				Long longId = Long.parseLong(criteria.getOffspringOfId());
				hibCiCriteria.add(Expression.eq("derivedFromId", longId));
			} catch (NumberFormatException e) {
				log.warn("QueryCriteria contained not a long offspringId <" + criteria.getCiId());
				throw new IllegalArgumentException("Not a correct long ci id <" + criteria.getCiId());
			}
		} else if (criteria.getOffspringOfAlias() != null) {
			ICi ci = findCiByAlias(new Path<String>(criteria.getOffspringOfAlias()));
			if (criteria.getOffspringDepth() != null) {
				if (ci == null) {
					// Is an error, but we don't throw an exception, instead it will return empty/0 
					DetachedCriteria hibAliasCiCriteria = DetachedCriteria.forClass(ConfigurationItem.class);			
					hibAliasCiCriteria.add(Expression.eq("alias", criteria.getOffspringOfAlias()));
					DetachedCriteria idCriteria = hibAliasCiCriteria.setProjection(Projections.property("longId"));
					hibCiCriteria.add(Property.forName("derivedFromId").in(idCriteria));
				} else {
					// TODO: append %/%/% according to offspring depth. 
					hibCiCriteria.add(Expression.ilike("templatePath", ci.getTemplatePath() + "/%"));
				}
			} else {
				if (ci != null) {
					hibCiCriteria.add(Expression.eq("derivedFromId", ci.getId().asLong()));
				} else {
					hibCiCriteria.add(Expression.eq("derivedFromId", new Long(0)));
				}
			}
			
			//hibAttributeCriteria.add(Expression.eq("alias", criteria.getOffspringOfAlias()));
		}
		
		if (criteria.getCiAlias() != null) {
			hibCiCriteria.add(Expression.eq("alias", criteria.getCiAlias()));
		} else if (criteria.getCiId() != null) {
			try {
				// Query for an unique id.
				Long longId = Long.parseLong(criteria.getCiId());
				hibCiCriteria.add(Expression.eq("longId", longId));
			} catch (NumberFormatException e) {
				log.warn("QueryCriteria contained not a long ci id <" + criteria.getCiId());
				throw new IllegalArgumentException("Not a correct long ci id <" + criteria.getCiId());
			}
			/*
			if (ci == null || ci instanceof IAttribute) {
				if (count) {
					result.setTotalHits(0);
				} 
			} else {
				if (count) {
					result.setTotalHits(1);
				}
				result.add(ci);
			}
			return(result);
			*/
		}
		if (criteria.getMatchType() != null) {
			ICi type = findCiByAlias(new Path<String>(criteria.getMatchType()));
			if (type != null) {
				Disjunction orAttribute = Restrictions.disjunction();
				String path = type.getTemplatePath();
				String paths[] = path.split("/");
				if (paths.length > 1) {
					for (int i = 1; i < paths.length; i++) {
						orAttribute.add(Expression.ilike("typeName", "%#" + paths[i],  MatchMode.START));
						
					}
					DetachedCriteria typeCrit = DetachedCriteria.forClass(BasicAttribute.class);
					typeCrit.add(Expression.isNull("derivedFromId"));
					typeCrit.add(orAttribute);
					DetachedCriteria idCrit = typeCrit.setProjection(Projections.property("ownerId"));
					hibCiCriteria.add(Property.forName("longId").in(idCrit));
					if (criteria.getMatchCiPath() != null) {
						String idPath = "";
						String ciPath[] = criteria.getMatchCiPath().split("/");
						if (ciPath.length > 0) {
							for (int i = 0; i < ciPath.length; i++) {
								ICi ci = findCiByAlias(new Path<String>(ciPath[i]));
								if (ci != null) {
									idPath +=  "/" + ci.getId().asLong(); 
								}
							}
							// TODO: append %/%/% according to offspring depth. 
							hibCiCriteria.add(Expression.ilike("templatePath", idPath + "/%"));
						
						}
					}
				}
				
			}
		}
		
		if (criteria.isMatchCiTemplates() && criteria.isMatchCiInstances()) {
			// Search Both.
		} else if (criteria.isMatchCiTemplates()) {
			hibCiCriteria.add(Expression.eq("isBlueprint", Boolean.TRUE));
		} else if (criteria.isMatchCiInstances()) {
			hibCiCriteria.add(Expression.eq("isBlueprint", Boolean.FALSE));
		}
		if (criteria.isMatchAttributeTemplates() && criteria.isMatchAttributeInstances()) {
			// Search both
		} else if (criteria.isMatchAttributeTemplates()) {
			hibAttributeCriteria.add(Expression.eq("isBlueprint", Boolean.TRUE));
		} else if (criteria.isMatchAttributeInstances()) {
			hibAttributeCriteria.add(Expression.eq("isBlueprint", Boolean.FALSE));
		}
		
		if (criteria.getText() != null) {
			Disjunction orAttribute = Restrictions.disjunction();
			Disjunction orCi = Restrictions.disjunction();
			boolean orAttributeAdded = false;
			boolean orCiAdded = false;
			
			if (criteria.isTextMatchAlias()) {
				orCi.add(Expression.ilike("alias", criteria.getText(), MatchMode.ANYWHERE));
				orAttribute.add(Expression.ilike("alias", criteria.getText(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
				orCiAdded = true;
				
			}
			
			if (criteria.isTextMatchDescription()) {
				orCi.add(Expression.ilike("description", criteria.getText(), MatchMode.ANYWHERE));
				orAttribute.add(Expression.ilike("description", criteria.getText(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
				orCiAdded = true;
		
			}
			
			if (criteria.isTextMatchValue()) {
				orAttribute.add(Expression.ilike("valueAsString", criteria.getText(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
				// Enable Attribute serach....
				criteria.setMatchAttribute(true);
			}
			/*
			DetachedCriteria idCriteria = hibAttributeCriteria.setProjection(Projections.property("ownerId"));
			orCi.add(Property.forName("longId").in(idCriteria));
			*/
			
			if (orAttributeAdded) {
				if (criteria.getMatchAttributeAlias() != null) {
					hibAttributeCriteria.add(Expression.eq("alias", criteria.getMatchAttributeAlias()));
				}
				hibAttributeCriteria.add(orAttribute);
				DetachedCriteria idCriteria = hibAttributeCriteria.setProjection(Projections.property("ownerId"));
				orCi.add(Property.forName("longId").in(idCriteria));
				orCiAdded = true;
			}
			if (orCiAdded) {
				hibCiCriteria.add(orCi);
			}
		}
		
		QueryResult<ICi> result = new QueryResult<ICi>();
		/*
		if (criteria.isMatchAttribute()) {
			DetachedCriteria idCriteria = hibAttributeCriteria.setProjection(Projections.property("ownerId"));
			hibCiCriteria.add(Property.forName("longId").in(idCriteria));
		}
		*/

		
		// Search ICi.
		Session session = getSession();
		try {
			Profiler.start("QueryCi():");
			if (count) {
				Criteria hibCriteria = hibCiCriteria.getExecutableCriteria(session);
				hibCriteria.setProjection(Projections.rowCount());
				List list = hibCriteria.list();
				if (list != null && !list.isEmpty()) {
					Integer itemCount = ((Integer)list.get(0)).intValue();
					result.setTotalHits(itemCount);
				}
			} else {
				
				
				if (criteria.getOrderAttAlias() != null) {
					DetachedCriteria idCriteria = hibCiCriteria.setProjection(Projections.property("longId"));
					
					DetachedCriteria attr = DetachedCriteria.forClass(BasicAttribute.class);
					attr.add(Expression.eq("alias", criteria.getOrderAttAlias()));
					attr.add(Property.forName("ownerId").in(idCriteria));
					if (criteria.isOrderAscending()) {
						attr.addOrder(Order.asc(criteria.getOrderType()));
					} else {
						attr.addOrder(Order.desc(criteria.getOrderType()));
					}
				
					
					
					Criteria attrCriteria = attr.getExecutableCriteria(session);
					if (criteria.getMaxResult() != null) {
						attrCriteria.setMaxResults(criteria.getMaxResult());
					}

					if (criteria.getFirstResult() != null) {
						attrCriteria.setFirstResult(criteria.getFirstResult());
					}
					
					List<IAttribute> attrs = attrCriteria.list();
					for (IAttribute a : attrs) {
						result.add(a.getOwner());
					}
				} else {
					hibCiCriteria.addOrder(Order.asc("alias"));
				
					Criteria hibCriteria = hibCiCriteria.getExecutableCriteria(session);
					if (criteria.getMaxResult() != null) {
						hibCriteria.setMaxResults(criteria.getMaxResult());
					}

					if (criteria.getFirstResult() != null) {
						hibCriteria.setFirstResult(criteria.getFirstResult());
					}


					List objects = hibCriteria.list();
					result.addAll(objects);
				}
			}
		} finally {
			Profiler.stop();
				
			closeSession(session);
		}
		return(result);
	}

	public Integer queryCriteriaCount(DetachedCriteria detachedCrit) {
		Session session = getSession();
		List result = Collections.EMPTY_LIST;
		Integer itemCount = new Integer(0);
		try {
			Profiler.start("QueryCriteria():");
			Criteria criteria = detachedCrit.getExecutableCriteria(session);
			criteria.setProjection(Projections.rowCount());
			List list = criteria.list();
			if (list != null && !list.isEmpty()) {
				itemCount = ((Integer)list.get(0)).intValue();
			}
			
		} finally {
			Profiler.stop();
			closeSession(session);
		}
		return(itemCount);
		
	}
	
	public List queryCriteria(DetachedCriteria detachedCrit, PageInfo info) {
		Session session = getSession();
		List result = Collections.EMPTY_LIST;
		try {
			Profiler.start("QueryCriteria():");
			Criteria criteria = detachedCrit.getExecutableCriteria(session);
			
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
	public QueryResult queryExpression(OneCMDBExpression expr) {
		QueryResult<ICi> result = new QueryResult<ICi>();
		Session session = getSession();
		try {
			Profiler.start("QueryCi():");
			if (expr.isCount()) {
				Criteria criteria = expr.composeCriteria().getExecutableCriteria(session);
				criteria.setProjection(Projections.rowCount());
				List list = criteria.list();
				if (list != null && !list.isEmpty()) {
					Integer itemCount = ((Integer)list.get(0)).intValue();
					result.setTotalHits(itemCount);
				}
			} else {
							
				// Debug test.
				DetachedCriteria aCrit = expr.getOrderCriteria();
				if (aCrit != null) {
					Criteria criteria = aCrit.getExecutableCriteria(session);
					if (expr.getMaxResult() != null) {
						criteria.setMaxResults(expr.getMaxResult());
					}

					if (expr.getFirstResult() != null) {
						criteria.setFirstResult(expr.getFirstResult());
					}
					
					List objects = criteria.list();
					for (Object o : objects) {
						result.add(((IAttribute)o).getOwner());
					}
				} else {
					Criteria criteria = expr.composeCriteria().getExecutableCriteria(session);
					if (expr.getMaxResult() != null) {
						criteria.setMaxResults(expr.getMaxResult());
					}

					if (expr.getFirstResult() != null) {
						criteria.setFirstResult(expr.getFirstResult());
					}
					List objects = criteria.list();
					result.addAll(objects);
				}
			}
		} finally {
			Profiler.stop();
				
			closeSession(session);
		}
		return(result);
		
	}
	
	public QueryResult<ICi> queryOld(QueryCriteria criteria, boolean count) {
		DetachedCriteria hibCiCriteria = DetachedCriteria.forClass(ConfigurationItem.class);
		DetachedCriteria hibAttributeCriteria = DetachedCriteria.forClass(BasicAttribute.class);
			
		// Search in the db...
		if (criteria.getOffspringOfId() != null) {
			hibCiCriteria.add(Expression.eq("derivedFromId", criteria.getOffspringOfId()));
			hibAttributeCriteria.add(Expression.eq("derivedFromId", criteria.getOffspringOfId()));
		}
		if (criteria.isMatchCiTemplates() && criteria.isMatchCiInstances()) {
			// Search Both.
		} else if (criteria.isMatchCiTemplates()) {
			hibCiCriteria.add(Expression.eq("isBlueprint", Boolean.TRUE));
		} else if (criteria.isMatchCiInstances()) {
			hibCiCriteria.add(Expression.eq("isBlueprint", Boolean.FALSE));
		}
		if (criteria.isMatchAttributeTemplates() && criteria.isMatchAttributeInstances()) {
			// Search both
		} else if (criteria.isMatchAttributeTemplates()) {
			hibAttributeCriteria.add(Expression.eq("isBlueprint", Boolean.TRUE));
		} else if (criteria.isMatchAttributeInstances()) {
			hibAttributeCriteria.add(Expression.eq("isBlueprint", Boolean.FALSE));
		}
		
		if (criteria.getText() != null) {
			Disjunction orAttribute = Restrictions.disjunction();
			Disjunction orCi = Restrictions.disjunction();
			boolean orAttributeAdded = false;
			boolean orCiAdded = false;
			
			if (criteria.isTextMatchAlias()) {
				orCi.add(Expression.ilike("alias", criteria.getText(), MatchMode.ANYWHERE));
				orAttribute.add(Expression.ilike("alias", criteria.getText(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
				orCiAdded = true;
				
			}
			
			if (criteria.isTextMatchDescription()) {
				orCi.add(Expression.ilike("description", criteria.getText(), MatchMode.ANYWHERE));
				orAttribute.add(Expression.ilike("description", criteria.getText(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
				orCiAdded = true;
		
			}
			
			if (criteria.isTextMatchValue()) {
				orAttribute.add(Expression.ilike("valueAsString", criteria.getText(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
			}
			if (orAttributeAdded) {
				hibAttributeCriteria.add(orAttribute);
			}
			if (orCiAdded) {
				hibCiCriteria.add(orCi);
			}
		}
		
		QueryResult<ICi> result = new QueryResult<ICi>();
		if (criteria.isMatchCi()) {
			// Search ICi.
			Session session = getSession();
			// Lock taken, can not do anything else.
			try {
				Profiler.start("QueryCi():");
				Criteria hibCriteria = hibCiCriteria.getExecutableCriteria(session);
				
				if (count) {
					
					hibCriteria.setProjection(Projections.rowCount());
					List list = hibCriteria.list();
					if (list != null && !list.isEmpty()) {
						Integer itemCount = ((Integer)list.get(0)).intValue();
						result.setTotalHits(itemCount);
					}
				} else {
					if (criteria.getMaxResult() != null) {
						hibCriteria.setMaxResults(criteria.getMaxResult());
					}

					if (criteria.getFirstResult() != null) {
						hibCriteria.setFirstResult(criteria.getFirstResult());
					}

					hibCriteria.addOrder(Order.asc("alias"));
					List objects = hibCriteria.list();
					result.addAll(objects);
				}
			} finally {
				Profiler.stop();
				
				closeSession(session);
			}
		}
		if (criteria.isMatchAttribute()) {
			// Serach Attributes.
			List<ICi> cis = null;
			Session session = getSession();
			// Lock taken, can not do anything else.
			try {
				Profiler.start("QueryAttribute():");
			
				DetachedCriteria idCriteria = hibAttributeCriteria.setProjection(Projections.property("ownerId"));
				
				DetachedCriteria crit = DetachedCriteria.forClass(ConfigurationItem.class);
				crit.add(Property.forName("longId").in(idCriteria));
			
				Criteria hibCriteria = crit.getExecutableCriteria(session);
				if (count) {
					
					hibCriteria.setProjection(Projections.rowCount());
							
					List list = hibCriteria.list();
					if (list != null && !list.isEmpty()) {
						Integer itemCount = ((Integer)list.get(0)).intValue();
						result.setTotalHits(result.getTotalHits() + itemCount);
					}
				} else {
					if (criteria.getMaxResult() != null) {
						hibCriteria.setMaxResults(criteria.getMaxResult());
					}

					if (criteria.getFirstResult() != null) {
						hibCriteria.setFirstResult(criteria.getFirstResult());
					}

					hibCriteria.addOrder(Order.asc("alias"));


					cis = hibCriteria.list();
				}
			} finally {
				Profiler.stop();
				
				closeSession(session);
			}
			if (!count) {
				if (cis != null) {
					for (ICi ci : cis) {
						if (ci.isBlueprint()) {
							if (!criteria.isMatchCiTemplates()) {
								continue;
							}
						} else { 
							if (!criteria.isMatchCiInstances()) {
								continue;
							}
						}
					
						if (!result.contains(ci)) {
							result.add(ci);
						}
					}
				}
			}
		}
		return(result);
	}
	
	/**
	 * Query RFC's history for a CI.
	 * The Ci param can bu NULL meaning all changes will be 
	 * asked for.
	 */
	public QueryResult<IRFC> queryRfc(ICi ci, RfcQueryCriteria crit, boolean count) {
		Collection<IAttribute> attributes = null;
		if (crit.isFetchAttributes()) {
			if (ci != null) {
				if (crit.getAttributeAlias() != null) {
					attributes = ci.getAttributesWithAlias(crit.getAttributeAlias());
				} else {
					attributes = ci.getAttributes();
				}
			}
		}
		
		QueryResult<IRFC> result = new QueryResult<IRFC>();

		// Quering the db.		
		Session session = getSession();
		// Lock taken, can not do anything else.
		try {
			Profiler.start("QueryRfc():");
			Criteria criteria = session.createCriteria(crit.getRfcClass());
			if (ci != null) {
				if (crit.isFetchAttributes()) {

					Disjunction or = Restrictions.disjunction();
					or.add(Expression.eq("targetId", ci.getId().asLong()));
					for (IAttribute a : attributes) {
						or.add(Expression.eq("targetId", a.getId().asLong()));
					}
					criteria.add(or);
				} else {
					criteria.add(Expression.eq("targetId", ci.getId().asLong()));				
				}
			}
			if (crit.getTxId() != null) {
				criteria.add(Expression.eq("txId", crit.getTxId()));
			}
			if (crit.getFromDate() != null && crit.getToDate() != null) {
				criteria.add(Expression.between("ts", crit.getFromDate(), crit.getToDate()));
			} else if (crit.getFromDate() != null) {
				// Search to date.
				criteria.add(Expression.ge("ts", crit.getFromDate()));
			} else if (crit.getToDate() != null) {
				// Serach from date.
				criteria.add(Expression.le("ts", crit.getToDate()));
			} else {
				// No criteria on date.
			}
			
			if (count) {
				criteria.setProjection(Projections.rowCount());
				List list = criteria.list();
				if (list != null && !list.isEmpty()) {
					Integer itemCount = ((Integer)list.get(0)).intValue();
					result.setTotalHits(itemCount);
				}
			} else {
				if (crit.getMaxResult() != null) {
					criteria.setMaxResults(crit.getMaxResult());
				}
				if (crit.getFirstResult() != null) {
					criteria.setFirstResult(crit.getFirstResult());
				}
				if (crit.isDescendingOrder()) { 
					criteria.addOrder( Order.desc("ts") );
				} else {
					criteria.addOrder( Order.asc("ts") );
				}					
				
				List objects = criteria.list();
				result.addAll(objects);
			}
			
		} finally {
			Profiler.stop();
			
			closeSession(session);
		}
		
		return(result);
	}

	public void storeTransaction(ICmdbTransaction cmdbTx) {
		Session session = getSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			storeRFC(session, cmdbTx);
			tx.commit();
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}
			throw he;
		} finally {
			closeSession(session);
		
			flushTransaction(cmdbTx);
		}
		
		
	}

}
