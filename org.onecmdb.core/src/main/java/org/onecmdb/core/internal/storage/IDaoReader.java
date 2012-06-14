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
package org.onecmdb.core.internal.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.storage.expression.OneCMDBExpression;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;


public interface IDaoReader {

	String getNamespace();

	
	
	Set<ICi> findByTemplate(ICi template);
	
	ICi findById(ItemId id);
	
	IAttribute findAttributeById(ItemId id);

	ICi findCiByAlias(IPath<String> path);
	
	List<? extends ICi> query(Class clazz, HashMap<String, Object> map);
	List query(String entityName, HashMap<String, Object> map);

	Set<ICi> getOffsprings(ItemId id);
	Set<ICi> getAttributeOffsprings(ItemId id);
		
	Set<IAttribute> getAttributesFor(ItemId id);

	ICmdbTransaction getTransaction(ItemId id);
	
	List<IRFC> getRfcsForCmdbTx(ItemId id);

	List<IRFC> findRFCForCi(ItemId ciid);
	
	/**
	 * Retrieve all the target attribute(s) pointing to a CI.
	 * This means that the argument CI is referenced by another CI.
	 * 
	 * @param ci
	 * @return target attribute of references.
	 */
	List<IAttribute> getTargetReference(ICi ci);
	
	/**
	 * Retrieve all the source attribute(s) pointing to a CI.
	 * This means the the argument CI is referencing another CI.
	 * @param ci
	 * @return source attribute of references.
	 */
	List<IAttribute> getSourceReference(ICi ci);
	
	/**
	 * Get all attributes that has the argument CI as it's value.
	 * 
	 * @param ci
	 * @return
	 */
	List<IAttribute> getAttributesReferringTo(ICi ci);
	
	/**
	 * Query RFC's connected to a ci. 
	 * 
	 * If the ci is null, then all rfc's matching the
	 * criteria is returned.
	 * 
	 * @param ci
	 * @param crit Selection criteria. 
	 * @return
	 * 
	 * @see RfcQueryCriteria
	 */
	QueryResult<IRFC> queryRfc(ICi ci, RfcQueryCriteria crit, boolean count);

	/**
	 * Query the data source accronding to QueryCriteria.
	 *  
	 * @param crit what to query for
	 * @param count only calculate number of hits.
	 * @return QueryResult 
	 */	
	QueryResult query(QueryCriteria crit, boolean count);

	QueryResult queryExpression(OneCMDBExpression expr);
	
	
	public List queryCriteria(DetachedCriteria detachedCrit, PageInfo info);
	public Integer queryCriteriaCount(DetachedCriteria detachedCrit);

}
