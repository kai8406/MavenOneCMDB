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
package org.onecmdb.ui.gwt.toolkit.client.control.table;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIReferenceTableControl extends CIInheritanceTableControl {
	
	private String sourceCIAlias;
	private String referencePattern;
	private GWT_CiBean sourceBean = null;
	private String targetTemplate;
	
	public CIReferenceTableControl(String sourceCIAlias, String referencePattern) {
		super();
		this.sourceCIAlias = sourceCIAlias;
		this.referencePattern = referencePattern;
	}
	
	public CIReferenceTableControl(String sourceCIAlias, String referencePattern, String targetTemplate) {
		super(targetTemplate);
		this.sourceCIAlias = sourceCIAlias;
		this.referencePattern = referencePattern;
	}
	
	public void setSourceBean(GWT_CiBean source) {
		this.sourceBean = source;
	}
	
	public void getColumns(AsyncCallback callback) {
		super.getColumns(callback);
	}

	public void getRowCount(final AsyncCallback callback) {
		if (this.sourceBean == null) {
			OneCMDBConnector.getCIFromAlias(sourceCIAlias, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						sourceBean = (GWT_CiBean)result;
						fetchRowCount(callback);
					}
				}
			});
		} else {
			fetchRowCount(callback);
		}
	}

	/**
	 * SourceBean must have been fetched.
	 * @param callback
	 */
	protected void fetchRowCount(final AsyncCallback callback) {
		OneCMDBConnector.getInstance().evalRelationCount(
				OneCMDBSession.getAuthToken(), 
				sourceBean, 
				referencePattern, 
				getCriteria(),
				callback);
		
	}
	protected GWT_QueryCriteria getCriteria() {
		GWT_QueryCriteria crit = super.getDataControlCriteria();
		crit.setOffspringDepth(new Integer(-1));
		return(crit);
	}

	public void getRows(final AsyncCallback callback) {
		if (this.sourceBean == null) {
			OneCMDBConnector.getCIFromAlias(sourceCIAlias, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						sourceBean = (GWT_CiBean)result;
						CIReferenceTableControl.super.getRows(callback);
					}
				}
				
			});
		} else {
			super.getRows(callback);
		}
	}
	
	protected void fetchRows(final AsyncCallback callback) {
			OneCMDBConnector.getInstance().evalRelation(
					OneCMDBSession.getAuthToken(), 
					sourceBean, referencePattern, 
					getCriteria(),
					new AsyncCallback() {
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						public void onSuccess(Object result) {
							Object rows = convertResultToRows(result);
							callback.onSuccess(rows);
						}
					}
			);
	}

}
