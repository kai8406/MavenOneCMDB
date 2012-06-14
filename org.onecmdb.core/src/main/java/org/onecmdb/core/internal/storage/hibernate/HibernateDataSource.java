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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IDataSourceWriter;
import org.onecmdb.core.IExpression;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.storage.expression.AttributeExpression;
import org.onecmdb.core.internal.storage.expression.ItemExpression;
import org.onecmdb.core.internal.storage.expression.OffspringExpression;
import org.onecmdb.core.tests.profiler.Profiler;

public class HibernateDataSource implements IDataSourceWriter {
	
	private SessionFactory sf;
	public static final int JDBC_TIMEOUT_IN_SEC = 2;
	
	public void setSessionFactory(SessionFactory sf) {
		this.sf = sf;
	}

	
	public List evaluate(IExpression e) {
		Profiler.start("EvaluateList(" + e.toString() +")");
		try {
			Session s = sf.openSession();
			
			Criteria criteria = createHibernateCriteria(s, e);
			
			if (e.getPageInfo() != null) {
				criteria.setMaxResults(e.getPageInfo().getPageSize());
				criteria.setFirstResult(e.getPageInfo().getPage() * e.getPageInfo().getPageSize());
			}
			
			criteria.setTimeout(JDBC_TIMEOUT_IN_SEC);
			
			if (e.getOrderInfo() != null) {
				criteria.addOrder((Order)e.getOrderInfo());
			}
			
			List result = criteria.list();
			return(result);
		} finally {
			Profiler.stop();
		}
	}


	private Criteria createHibernateCriteria(Session s, IExpression e) {
		if (e instanceof OffspringExpression) {
			
			Criteria crit = s.createCriteria(ConfigurationItem.class);
			
			if (e.getArgument() instanceof ICi) {
				ICi parent = (ICi)e.getArgument();
				crit.add(Restrictions.eq("derivedFrom", parent.getUniqueName()));
			}
			return(crit);
		}
		
		if (e instanceof AttributeExpression) {
			Criteria crit = s.createCriteria(ConfigurationItem.class);
		}
		
		if (e instanceof ItemExpression) {
			Criteria crit = s.createCriteria(ConfigurationItem.class);
		}
		return(null);
	}


	public void addModifiedObject(Object o) {
		// TODO Auto-generated method stub
		
	}


	public void addDeleteObject(Object o) {
		// TODO Auto-generated method stub
		
	}


	public void setModifiedObject(List objects) {
		// TODO Auto-generated method stub
		
	}


	public void setDeleteObject(List objects) {
		// TODO Auto-generated method stub
		
	}


	public Object beginTx() {
		// TODO Auto-generated method stub
		return null;
	}


	public void endTx(Object tx) {
		// TODO Auto-generated method stub
		
	}


	public Object newObject(Object template) {
		// TODO Auto-generated method stub
		return null;
	}


	public Object evaluateToUnique(IExpression expr) {
		// TODO Auto-generated method stub
		return null;
	}


	public long evaluateTotalCount(IExpression expr) {
		// TODO Auto-generated method stub
		return 0;
	}
	

	
}
