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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AbstractDataControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultTableAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIInheritanceTableControl extends AbstractDataControl implements ITableControl {

	protected GWT_CiBean template;
	private HashMap rowMap = new HashMap();
	private int onSelectScreenIndex = -1;
	private IAttributeFilter attributeFilter;
	private String targetTemplate;
	
	
	public CIInheritanceTableControl() {
	}
	
	public CIInheritanceTableControl(String targetTemplate) {
		this.targetTemplate = targetTemplate;
	}

	public String getTemplateAlias() {
		return(this.targetTemplate);
	}
	
	public void setTemplate(GWT_CiBean template) {
		this.template = template;
		this.targetTemplate = template.getAlias();
	}
	
	public void setAttributeFilter(IAttributeFilter aFilter) {
		this.attributeFilter = aFilter;
	}
	
	public void getColumns(final AsyncCallback callback) {
		if (this.template != null) {
			fetchColumns(callback);
			return;
		}
		if (this.targetTemplate != null) {
			OneCMDBConnector.getCIFromAlias(this.targetTemplate, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						setTemplate((GWT_CiBean)result);
						fetchColumns(callback);
						return;
					}
				}
				
			});
		}
	}
	
	protected void fetchColumns(final AsyncCallback callback) {
		getAttributeFilter().filterAttributes(template, template, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			public void onSuccess(Object result) {
				callback.onSuccess(result);
			}
		});
	}

	
	
	public IAttributeFilter getAttributeFilter() {
		if (this.attributeFilter == null) {
			this.attributeFilter = new DefaultTableAttributeFilter();
		}
		return(this.attributeFilter);
	}
	
	public void getRowCount(final AsyncCallback callback) {
		System.out.println("Load Row Count.");
		
		OneCMDBConnector.getInstance().searchCount(OneCMDBSession.getAuthToken(), 
				getCriteria(), 
				new AsyncCallback() {

					public void onFailure(Throwable caught) {
						if (callback != null) {
							callback.onFailure(caught);
						}
					}

					public void onSuccess(Object result) {
						if (result instanceof Integer) {
							
							if (callback != null) {
								callback.onSuccess(result);
							}
						}
					}
		});
	}

	/**
	 * Return Object is as follow:
	 * List of List of AttributeValue.
	 * 
	 * @param callback 
	 */
	public void getRows(final AsyncCallback callback) {
		if (this.template != null) {
			fetchRows(callback);
			return;
		}
		if (this.targetTemplate != null) {
			OneCMDBConnector.getCIFromAlias(this.targetTemplate, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						setTemplate((GWT_CiBean)result);
						fetchRows(callback);
						return;
					}
				}
				
			});
		}
	}
	
	protected void fetchRows(final AsyncCallback callback) {
		System.out.print("Load Rows: " + getCriteria().toString());
		final long start = System.currentTimeMillis();
		
		
		
		OneCMDBConnector.getInstance().search(OneCMDBSession.getAuthToken(), 
				getCriteria(), 
				new AsyncCallback() {

					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					public void onSuccess(Object result) {
						System.out.println("Loaded Data in " + (System.currentTimeMillis() - start));
						Object rows = convertResultToRows(result);
						callback.onSuccess(rows);
					}
			
		});
	}

	protected List filterRow(GWT_CiBean bean) {
		List arrayList = getAttributeFilter().filterAttributes(template, bean);
		return(arrayList);
	}
	
	protected Object convertResultToRows(Object result) {
		List rows = new ArrayList();
		if (result instanceof GWT_CiBean[]) {
			GWT_CiBean beans[] = (GWT_CiBean[])result;
			rowMap.clear();
			for (int row = 0 ; row < beans.length; row++) {
				
				GWT_CiBean bean = beans[row];
				
				rowMap.put(new Integer(row+1), bean);
				
				List col = filterRow(bean);
				rows.add(col);
			}
		}
		return(rows);
		
	}
	
	protected GWT_QueryCriteria getCriteria() {
		GWT_QueryCriteria crit = super.getDataControlCriteria();
		crit.setOffspringOfAlias(this.targetTemplate);
		crit.setMatchCiInstances(true);
		// Match all instances.
		crit.setOffspringDepth(new Integer(-1));
		
		return(crit);
	}

	public GWT_CiBean getObject(int row) {
		GWT_CiBean bean = (GWT_CiBean) rowMap.get(new Integer(row));
		if (bean == null) {
			return(null);
		}
		return(bean);
	}
	
	public String getObjectName(int row, int col) {
		GWT_CiBean bean = (GWT_CiBean) rowMap.get(new Integer(row));
		if (bean == null) {
			return(null);
		}
		return(bean.getAlias());
	}
	
	public void setOnSelectScreenIndex(int onSelectScreenIndex) {
		this.onSelectScreenIndex = onSelectScreenIndex;
	}
	
	public int getSelectScreenIndex() {
		return(this.onSelectScreenIndex );
	}


}
