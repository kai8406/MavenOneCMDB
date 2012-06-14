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
package org.onecmdb.core.utils.wsdl;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IJobStartResult;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.result.Graph;

/**
 * OneCMDB Web Service interface.<br>
 * <p>
 * The intension of this interface is that it should be possiable
 * to modify/query all aspects of a ci and it's attributes. 
 * </p>
 * 
 */
public interface IOneCMDBWebService {
	/**
	 * Basic authentication to the service.
	 * <br>
	 * 
	 * @param username
	 * @param pwd
	 * @return a authentication token, used in all other calls.
	 * @throws Exception if the login fails.
	 */
	public String auth(String username, String pwd) throws Exception;
	
	/**
	 * Invalidate the token meaning that it can not be used any more.
	 * 
	 * @param authToken
	 */
	public void logout(String authToken);
	
	/**
	 * Query for history objects for a CI. All modifications to a CI
	 * will be stored as RFC. 
	 * 
	 * @param auth
	 * @param vBean
	 * @param criteria
	 * @return
	 */
	public RFCBean[] history(String auth, CiBean vBean, RfcQueryCriteria criteria);
	
	/**
	 * Count number of hits for history Query. 
	 * 
	 * @param auth
	 * @param vBean
	 * @param criteria
	 * @return
	 */
	public int historyCount(String auth, CiBean vBean, RfcQueryCriteria criteria);
	
	/**
	 * Search the CMDB. What to search for is controlled by the <code>QueryCriteria</code> 
	 * object. 
	 * <br>
	 * The criteria also supports paging functionality.
	 * 
	 * @param auth
	 * @param criteria
	 * @return
	 * @see QueryCriteria
	 */
	public CiBean[] search(String auth, QueryCriteria criteria);
	
	/**
	 * Count how many hits the search criteria finds.
	 * 
	 * @param auth
	 * @param criteria
	 * @return
	 */
	public int searchCount(String auth, QueryCriteria criteria);
	
	
	/**
	 * Query using XPath syntax. All input arguments are simple String's.
	 *  
	 * @param auth the token received from auth()
	 * @param xPath the XPath expression.
	 * @param attributes which attribute's the result CI should contain.
	 * @return
	 */
	public CiBean[] query(String auth, String xPath, String attributes);
	
	/**
	 * Method to add/modify/delete ci's from the cmdb. 
	 * The function is inspired by CVS, meaning that
	 * it contains a three way compare function. 
	 * <p>
	 * The local ci's contains the ci's that should be modified or added
	 * to the cmdb.
	 * <p>
	 * The base array will handle the delete of ci's or attributes.
	 * If a ci is put in the base array and NOT in the local array, it's
	 * regarded as a delete. If a CI exists in both base and local than 
	 * deletion of attributes can be performed. By removing an attribute from
	 * the local CI and not in the base CI the attribute will be removed.
	 * 
	 * @param auth the token received from auth().
	 * @param localBeans The CI that should be modified.
	 * @param baseBeans The base CI's, used to handle delete. 
	 * @return the result of the update.
	 * @see IRfcResult
	 */
	public IRfcResult update(String auth, CiBean localBeans[], CiBean baseBeans[]);
	
	public RFCBean[] compare(String auth, CiBean localBeans[], CiBean baseBeans[], String keys[]);
	
	/**
	 * Find all relations between a source CI and a target CI.<br> 
	 * The CiBean passed as argument can be either an instance or a template.<br>
	 * <br>
	 * All posable path's between the two CI's will be returned.<br>
	 * Could image to have some control how the path should be evaluated,<br>
	 * for instance the shortest.<br>
	 * <br>
	 * If no relation exists a empty array is returned.<br>
	 * <br>
	 *   
	 * @param auth
	 * @param source
	 * @param target
	 * @return an array of relation expression between the source and target.
	 */
	public String[] findRelation(String auth, CiBean source, CiBean target);
	
	/**
	 * Fetch related CI's between the source and targets<br>
	 * specified by the relation path.<br>
	 * The relation path syntxt is a compact way to describe<br>
	 * between to CI's.
	 *  
	 * @param auth
	 * @param root
	 * @param relationPath
	 * @param crit
	 * @return
	 */
	public CiBean[] evalRelation(String auth, CiBean source, String relationPath, QueryCriteria crit);
	
	/**
	 * Return how many target objects this relation will evaluates to.
	 * 
	 * @param auth
	 * @param root
	 * @param relationPath
	 * @param crit
	 * @return
	 */
	public int evalRelationCount(String auth, CiBean root, String relationPath, QueryCriteria crit);

	/**
	 * A way to allocate a unique instance alias.
	 * 
	 * @param templateAlias
	 * @return
	 */
	public String newInstanceAlias(String token, String templateAlias);

	
	/**
	 *
	 * @param token
	 * @return
	 */
	public RBACSession getRBACSession(String token);
	
	/**
	 *
	 * @param token
	 * @return
	 */
	public CiBean getAuthAccount(String token);
	
	/**
	 * GraphQuery...
	 * 
	 * @param token
	 * @param q
	 * @return
	 */
	public Graph queryGraph(String token, GraphQuery q);
	
	
	/**
	 * Job functions...
	 */
	/**
	 * Start a manual <em>triggable<em> job. If job is currently running it will be 
     * canceled before started again.
	 * 
	 * @param ci
	 */
	public IJobStartResult startJob(String token, CiBean job);

	/**
	 * Cancel an ongoing job.
	 * 
	 * @param ci
	 */
	public IJobStartResult cancelJob(String token, CiBean job);

	
	/**
	 * Reschedule a specific trigger. If trigger is a template all offsprings 
     * will be rescheduled. Triggered jobs that are running will be canceled 
     * before rescheduling.
	 * 
	 * @param trigger
	 */
	public void reschedualeTrigger(String token, CiBean trigger);
	
	/**
	 * Cancel a specific trigger. If trigger is a template all offsprings will be
	 * canceled. 
	 * 
	 * @param trigger
	 */
	public void cancelTrigger(String token, CiBean trigger);
	
	/**
	 * Update Service
	 */
	/**
	 * Check if an update is available.
	 * The force flag indicates if a new request should be performed
	 * before the method returns.
	 */
	public boolean isUpdateAvailable(String token, boolean force);
	
	/**
	 * Get the update info data, is a text informing how to update 
	 * OneCMDB. One can assume the text to be html formatted.
	 * 
	 * The force flag indicates if a new request should be performed
	 * before the method returns.
	 */
	public String getUpdateInfo(String token, boolean force);
	
	
	
}
