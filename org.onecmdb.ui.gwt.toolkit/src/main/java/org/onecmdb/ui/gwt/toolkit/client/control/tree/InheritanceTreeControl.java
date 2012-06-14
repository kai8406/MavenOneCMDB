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
package org.onecmdb.ui.gwt.toolkit.client.control.tree;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InheritanceTreeControl extends A_GWT_TreeDataSourceControl {

	private String rootAlias;
	private GWT_QueryCriteria queryCriteria;
	private Boolean filterInstances;

	public InheritanceTreeControl() {
		this("Root");
	}
	
	public InheritanceTreeControl(String rootAlias) {
		this.rootAlias = rootAlias;
	}
	
	public void setFilterInstances(Boolean value) {
		this.filterInstances = value;
	}
	/*
	 * (non-Javadoc)
	 * @see org.onecmdb.ui.gwt.itil.client.onecmdb.utils.GWT_TreeDataSourceControl#getChildCount(java.lang.Object, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	public void getChildCount(Object o, final AsyncCallback callback) {
		// Validate object
		if (!(o instanceof GWT_CiBean)) {
			callback.onFailure(new Exception("getChildCount(Object data): Not a correct data object!"));
			return;
		}
		
		final GWT_CiBean bean = (GWT_CiBean)o;
		if (!bean.isTemplate()) {
			callback.onSuccess(new Integer(0));
			return;
		}
		
		GWT_QueryCriteria crit = getCriteria();
		crit.setOffspringOfAlias(bean.getAlias());
		if (filterInstances != null) {
			if (filterInstances.booleanValue()) {
				crit.setMatchCiTemplates(true);
			} 
		}
		
		OneCMDBConnector.getInstance().searchCount(OneCMDBSession.getAuthToken(), crit, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			public void onSuccess(Object result) {
				// Result is on instances
				if (result instanceof Integer) {
					callback.onSuccess(result);
				}
			}
		});		
	}

	/*
	 * (non-Javadoc)
	 * @see org.onecmdb.ui.gwt.itil.client.onecmdb.utils.GWT_TreeDataSourceControl#getChildren(java.lang.Object, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	public void getChildren(Object o, Integer firstItem, final AsyncCallback callback) {
		// Validate object
		if (!(o instanceof GWT_CiBean)) {
			callback.onFailure(new Exception("getChild(Object data): Not a correct data object!"));
			return;
		}
		
		final GWT_CiBean bean = (GWT_CiBean)o;
		
		GWT_QueryCriteria crit = getCriteria();
		crit.setOffspringOfAlias(bean.getAlias());
		if (firstItem != null) {
			crit.setFirstResult(firstItem);
		}
		if (filterInstances != null) {
			if (filterInstances.booleanValue()) {
				crit.setMatchCiTemplates(true);
			} 
		}
	
		/*
		crit.setMatchCiTemplates(true);
		crit.setMatchCiInstances(false);
		*/
		OneCMDBConnector.getInstance().search(OneCMDBSession.getAuthToken(), crit, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			public void onSuccess(Object result) {
				
				if (result instanceof GWT_CiBean[]) {
					
					callback.onSuccess(result);
					
					if (false) {
						final GWT_CiBean templates[] =  (GWT_CiBean[])result;

						// Check how many instances.
						OneCMDBConnector.getInstance().search(OneCMDBSession.getAuthToken(), getCriteria(), new AsyncCallback() {

							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							public void onSuccess(Object result) {
								if (result instanceof GWT_CiBean[]) {
									GWT_CiBean instances[] =  (GWT_CiBean[])result;
									// Need to do it myself!!!
									Object set[] = new Object[templates.length + instances.length];
									for (int i = 0; i < templates.length; i++) {
										set[i] = templates[i];
									}
									for (int i = 0; i < instances.length; i++) {
										set[i+templates.length] = instances[i];
									}
									callback.onSuccess(set);
								}

							}
						});
					}
				}
			}
		});		
	}
	/*
	 * (non-Javadoc)
	 * @see org.onecmdb.ui.gwt.itil.client.onecmdb.utils.GWT_TreeDataSourceControl#getRootObject(com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	public void getRootObject(final AsyncCallback callback) {
		
		GWT_QueryCriteria crit = new GWT_QueryCriteria();
		crit.setCiAlias(this.rootAlias);
		
		OneCMDBConnector.getInstance().search(OneCMDBSession.getAuthToken(), crit, callback);		
	}

	public Widget getWidget(Object data) {
		if (!(data instanceof GWT_CiBean)) {
			return(new Label("getChildCount(Object data): Not a correct data object!"));
		}
		GWT_CiBean bean = (GWT_CiBean)data;
		final HorizontalPanel hpanel =  new HorizontalPanel();
		hpanel.add(new CIIconDisplayNameWidget(bean, getClickListener()));
		//hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
		//hpanel.add(new CIDisplayNameWidget(bean, getClickListener()));
		getInstanceCount(bean, new AsyncCallback() {

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Object result) {
				if (result instanceof Integer) {
					HTML instances = new HTML("[" + result + "]");
					instances.setStyleName("onecmdb-tree-counter-decoration");
					instances.setTitle("Total number of instances for this template");
					hpanel.add(instances);
				}
			}
			
		});
		return(hpanel);
	}

	private void getInstanceCount(GWT_CiBean bean, AsyncCallback callback) {
		GWT_QueryCriteria crit = new GWT_QueryCriteria();
		crit.setOffspringOfAlias(bean.getAlias());
		crit.setMatchCiInstances(true);
		// Match all instances.
		crit.setOffspringDepth(new Integer(-1));
		OneCMDBConnector.getInstance().searchCount(OneCMDBSession.getAuthToken(), 
				crit, callback); 
		
	}

	public GWT_QueryCriteria getCriteria() {
		GWT_QueryCriteria crit = super.getDataControlCriteria();
		// Reset max/min...
		
		return(crit);
	}

}
