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
package org.onecmdb.ui.gwt.toolkit.client.control;

import org.onecmdb.ui.gwt.toolkit.client.IOneCMDBGWTService;
import org.onecmdb.ui.gwt.toolkit.client.IOneCMDBGWTServiceAsync;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class OneCMDBConnector {
	
	private static IOneCMDBGWTServiceAsync service;
	
	
	public static IOneCMDBGWTServiceAsync getInstance() {
		if (service == null) {
			service =  (IOneCMDBGWTServiceAsync) GWT.create(IOneCMDBGWTService.class);

		      ServiceDefTarget target = (ServiceDefTarget) service;
		      
		      // Use a module-relative URLs to ensure that this client code can find 
		      // its way home, even when the URL changes (as might happen when you 
		      // deploy this as a webapp under an external servlet container). 
		      
		      String moduleRelativeURL = GWT.getModuleBaseURL() + "onecmdb-gwt/wsdl";
		      target.setServiceEntryPoint(moduleRelativeURL);
		}
		return(service);	
	}
	
	/**
	 * 
	 * @param alias
	 * @param callback onSuccess returns a GWT_CiBean object
	 */
	public static void getCIFromAlias(String alias, final AsyncCallback callback) {
		if (alias == null || alias.length() == 0) {
			System.out.println("Empty Alias Search!!!!!");
			callback.onSuccess(null);
			return;
		}
		GWT_QueryCriteria crit = new GWT_QueryCriteria();
		crit.setCiAlias(alias);
		OneCMDBConnector.getInstance().search(OneCMDBSession.getAuthToken(), crit, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean[]) {
					GWT_CiBean beans[] = (GWT_CiBean[])result;
					if (beans.length == 1) {
						callback.onSuccess(beans[0]);
						return;
					}
					if (beans.length == 0) {
						callback.onSuccess(null);
						return;
					}
					onFailure(new Exception("More that one CI with unique alias!"));
					
				}
				onFailure(new Exception("Not a correct result object, <" + result +">"));
			}
			
		});		
	}
}
