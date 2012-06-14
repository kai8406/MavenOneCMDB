/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.toolkit.client;



import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query.GWT_GraphQuery;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.result.GWT_Graph;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IOneCMDBGWTServiceAsync {
	public void getCurrentOneCMDB_WSDL(AsyncCallback callback);
	public void setCurrentOneCMDB_WSDL(String url, AsyncCallback callback);
	public void transform(String token, String dsAlias, String dataSourceAlias, AsyncCallback callback);

	/**
	 * Basic authentication to the service.
	 * <br>
	 * 
	 * @param username
	 * @param pwd
	 * @return a authentication token, used in all other calls.
	 * @throws Exception if the login fails.
	 */
	public void auth(String username, String pwd, AsyncCallback callback); 
	
	/**
	 * Invalidate the token meaning that it can not be used any more.
	 * 
	 * @param authToken
	 */
	public void logout(String authToken, AsyncCallback callback);
	
	/**
	 * Query for history objects for a CI. All modifications to a CI
	 * will be stored as RFC. 
	 * 
	 * @param auth
	 * @param vBean
	 * @param criteria
	 * @return
	 */
	//public GWT_RFCBean[] history(String auth, GWT_CiBean vBean, GWT_RfcQueryCriteria criteria);
	
	/**
	 * Count number of hits for history Query. 
	 * 
	 * @param auth
	 * @param vBean
	 * @param criteria
	 * @return
	 */
	//public int historyCount(String auth, GWT_CiBean vBean, GWT_RfcQueryCriteria criteria);
	
	/**
	 * Search the CMDB. What to search for is controlled by the <code>QueryCriteria</code> 
	 * object. 
	 * <br>
	 * The criteria also supports paging functionality.
	 * 
	 * @param auth
	 * @param criteria 
	 * @return callback result<GWT_CiBean[]>
	 * @see GWT_QueryCriteria
	 */
	public void search(String auth, GWT_QueryCriteria criteria, AsyncCallback callback);
	
	/**
	 * Count how many hits the search criteria finds.
	 * 
	 * @param auth
	 * @param criteria
	 * @return
	 */
	public void searchCount(String auth, GWT_QueryCriteria criteria, AsyncCallback callback);
	
	
	/**
	 * Query using XPath syntax. All input arguments are simple String's.
	 *  
	 * @param auth the token received from auth()
	 * @param xPath the XPath expression.
	 * @param attributes which attribute's the result CI should contain.
	 * @return
	 */
	public void query(String auth, String xPath, String attributes, AsyncCallback callback);
	
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
	public void update(String auth, GWT_CiBean localBeans[], GWT_CiBean baseBeans[], AsyncCallback callback);
	
	
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
	public void findRelation(String auth, GWT_CiBean source, GWT_CiBean target, AsyncCallback callback);
	
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
	public void evalRelation(String auth, GWT_CiBean source, String relationPath, GWT_QueryCriteria crit, AsyncCallback callback);
	
	/**
	 * Return how many target objects this relation will evaluates to.
	 * 
	 * @param auth
	 * @param root
	 * @param relationPath
	 * @param crit
	 * @return
	 */
	public void evalRelationCount(String auth, GWT_CiBean root, String relationPath, GWT_QueryCriteria crit, AsyncCallback callback);

	public void newInstanceAlias(String token, String templateAlias, AsyncCallback callback);

	public void getRBACSession(String token, AsyncCallback callback);

	public void getAuthAccount(String token, AsyncCallback callback);

	public void startJob(String token, GWT_CiBean job, AsyncCallback startJob);
	//public void queryGraph(String token, GWT_GraphQuery g, AsyncCallback callback);
	public void cancelJob(String authToken, GWT_CiBean job, AsyncCallback asyncCallback);

	public void queryGraph(String token, GWT_GraphQuery g, AsyncCallback callback) throws Exception;
	
	public void getRelations(String authToken, String template, String root,  AsyncCallback asyncCallback);
	
}

