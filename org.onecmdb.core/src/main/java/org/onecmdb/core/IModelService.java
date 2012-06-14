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
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.storage.expression.OneCMDBExpression;
import org.onecmdb.core.internal.storage.expression.SourceRelationExpression;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;

/**
 * <p>
 * The generic module handling all aspects of the configuration items:
 * </p>
 * <ul>
 * <li>Query for existing CIs</li>
 * <li>Query for existing  CIs</li>
 * <li>Query for existing  CIs</li>
 * <li>Query for existing  CIs</li>
 * </ul>
 */
public interface IModelService extends IService {

	/**
	 * Convenient method to retrieve the <em>root</em> whithout using the
	 * {@link IService#getRoot()} which requires a cast.
	 * 
	 * @return
	 */
	ICi getRoot();

	/**
	 * <p>Find a CI according to its <em>offspring path</em>.</p>
     * 
	 * <p>The offspring path can be as simple as the alias of a CI, for example
     * "Test", or a full path from the Root CI, like "ROOT/Ci/MyTemplate/Test".
	 * 
	 * @param offspringpath
	 *            The offspring path for the configuration item to find.
	 * @return ICi with the given offspring path, or <code>null</code> if none
     * was found.
	 */
	ICi findCi(IPath<String> offspringpath);

	/**
	 * Find, and bind, a configuration item when (only) its identifier is known.
	 * 
	 * @param id
	 *            the identifier of the configuration item to find.
	 * @return The found configuration item, or <code>null</code> in case no
	 *         item with the requested identifier was found.
	 */
	ICi find(ItemId id);

	/**
	 * Get all defined types that are offsprings of the basePath.
	 * If basePath is null all types are returned.
	 * 
	 * <br> 
	 * There exists simple types, like string,integer and complex types.
	 * <br> 
	 * All <em>templates</em> are considered to be complexType. 	
	 * @return
	 */
	Set<IType> getAllComplexTypes(IPath<String> basePath);

	/**
	 * Retrieve all built-in types. 
	 * 
	 * @return
	 */
	Set<IType> getAllBuiltInTypes();
	
	/**
	 * <p>Retrieve all CIs which are offsprings relative a specified path, and
     * marked as template.</p>
     *  
	 * <p>If  no path is passed, all templates are retrieved.</p>
	 * 
	 * 
	 * @return
	 */
	Set<ICi> getAllTemplates(IPath<String> basePath);

	/**
	 * Return all templates and instances CIs in the system.
	 * 
	 * @return
	 * @throws OutOfMemException if too many cis exists.
	 */
	Set<ICi> getAllCis();
	
	/**
	 * Determine if a CI is an offspring of another CI. If both CIs passed
	 * are equal, TRUE is returned.
	 * 
	 * @param root  
	 * @param ci 
	 * @return True in case <code>ci</code> is an offspring of 
     * <code>root</code>.
	 */
	boolean isOffspringOf(ICi root, ICi ci);

    /** 
     * Return the <em>type</em> identified by the passed alias name. 
     * @param alias 
     * @return The type represented by the alias, or <code>null</code>
     * if no type with the alias name exists.
     */
    IType getType(String alias);

    /**
     * Return the <em>type</em> identified by the passed identifier. 
     * @param id 
     * @return The type represented by the alias, or <code>null</code>
     * if no type with the alias name exists.
     */
     IType getType(ItemId typeId);
     
     /**
 	 * Query the data source according to QueryCriteria.
 	 *  
 	 * @param crit what to query for
 	 * @return QueryResult
 	 * @see QueryCriteria 
 	 */	
 	QueryResult query(QueryCriteria crit);
 	
 	/**
 	 * Count how many objects a query criteria will generates.
 	 * 
 	 * @param criteria
 	 * @return
 	 */
	int queryCount(QueryCriteria criteria);

	QueryResult evalExpression(OneCMDBExpression expr);
     
	List queryCrtiteria(DetachedCriteria crit, PageInfo info);
	Integer queryCrtiteriaCount(DetachedCriteria crit);

	public void addProtectedCI(String ciAlias);
	public boolean isCIProteced(String alias);

	
}
