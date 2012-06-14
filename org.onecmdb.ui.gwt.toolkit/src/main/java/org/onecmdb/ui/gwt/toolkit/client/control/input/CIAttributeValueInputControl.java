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
package org.onecmdb.ui.gwt.toolkit.client.control.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIAttributeValueInputControl {
	
	private GWT_CiBean base = null;
	private GWT_CiBean local = null;
	private GWT_CiBean baseTemplate = null;
	
	private String templateAlias;
	private IAttributeFilter attributeFilter;
	private boolean isNew;
	private boolean isNewTemplate;
	private boolean isReadonly;
	private List additionLocalBeans = new ArrayList();
	private List additionBaseBeans = new ArrayList();
	
	
	public CIAttributeValueInputControl(String templateAlias, boolean isNew) {
		this.templateAlias = templateAlias;
		this.isNew = isNew;
	}
	
	public void setIsNewTemplate(boolean value) {
		this.isNewTemplate = value;
	}
	
	public void setAttributeFilter(IAttributeFilter aFilter) {
		this.attributeFilter = aFilter;
	}
	
	public CIAttributeValueInputControl(GWT_CiBean bean) {
		setBase(bean);
	}
	
	
	public GWT_CiBean getBase() {
		return base;
	}

	private void setBase(GWT_CiBean base) {
		this.base = base;
		this.local = base.copy();
		if (this.isNew) {
			this.local.setTemplate(isNewTemplate);
			this.local.setDerivedFrom(base.getAlias());
			unsetID(this.local);
			removeEmptyMultiValue(this.local);
		}
	}

	private void removeEmptyMultiValue(GWT_CiBean bean) {
		for (Iterator iter = bean.getAttributes().iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			if (!"1".equals(aBean.getMaxOccurs())) {
				List values = bean.fetchAttributeValueBeans(aBean.getAlias());
				for (Iterator vIter = values.iterator(); vIter.hasNext();) {
					GWT_ValueBean vBean = (GWT_ValueBean)vIter.next();
					if (vBean.getValue() == null || vBean.getValue().length() == 0) {
						bean.removeAttributeValue(vBean);
					}
				}
			}
		}
	}
	
	private void removeAttributeDefinitions(GWT_CiBean bean) {
		bean.removeAttributes();
	}

	private void unsetID(GWT_CiBean bean) {
		bean.setId(null);
		for (Iterator iter = bean.getAttributes().iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			aBean.setId(null);
		}
		for (Iterator iter = bean.getAttributeValues().iterator(); iter.hasNext();) {
			GWT_ValueBean vBean = (GWT_ValueBean)iter.next();
			vBean.setId(null);
		}
	}

	public GWT_CiBean getLocal() {
		return local;
	}

	private void loadBase(final AsyncCallback callback) {
		if (getBase() != null) {
			callback.onSuccess(getBase());
		}
		OneCMDBConnector.getCIFromAlias(this.templateAlias, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					setBase((GWT_CiBean)result);
					callback.onSuccess(result);
					return;
				}
				onFailure(new Exception(templateAlias + " template not found." + result));
			}
		});
	}
	
	/**
	 * Sort/Filter attributes for view's.
	 * 
	 * @param ci
	 * @return Collection<GWT_AttributeBean>
	 */
	public final void getAttributes(final AsyncCallback callback) {
		loadBase(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			public void onSuccess(Object result) {
				loadAttributes(callback);
			}
		});
	}

	protected void loadAttributes(final AsyncCallback callback) {
		if (!getBase().isTemplate()) {
			// Get Template for attribute definitions.
			OneCMDBConnector.getCIFromAlias(getBase().getDerivedFrom(), new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						baseTemplate = (GWT_CiBean)result;
						filterAttributes(callback);
						return;
					}
					callback.onFailure(new Exception("GetCIFromAlias(): Wrong DataType result!"));
				}
				
			});
			return;
		}
		baseTemplate = getBase();
		filterAttributes(callback);
	}
	
	/**
	 * Returns List<AttributeValue>  
	 * @param template
	 * @param bean
	 * @param callback
	 */
	protected void filterAttributes(AsyncCallback callback) {
		
		if (this.attributeFilter == null) {
			this.attributeFilter = new DefaultAttributeFilter();
		}
		this.attributeFilter.filterAttributes(baseTemplate, getLocal(), callback);
	}

	public void commit(final AsyncCallback callback) {
		
		if (isReadonly) {
			callback.onFailure(new Exception("Readonly view!"));
			return;
		}
		// When creating install allocate an instance name.
		if (this.isNew) {
			if (isNewTemplate) {
				// Remove all attribute definitions.
				removeAttributeDefinitions(this.local);
			} else {
				allocAlias(this.templateAlias, new AsyncCallback() {

					public void onFailure(Throwable caught) {
						callback.onFailure(caught);

					}

					public void onSuccess(Object result) {
						if (result instanceof String) {
							getLocal().setAlias((String)result);
							store(callback);
							
						}
					}
				});
				return;
			}
		} 
		store(callback);
	}
		

	private void allocAlias(String templateAlias, AsyncCallback callback) {
		OneCMDBConnector.getInstance().newInstanceAlias(OneCMDBSession.getAuthToken(), templateAlias, callback);
	}

	private void store(final AsyncCallback callback) {
		beforeStore();
		additionLocalBeans.add(getLocal());
		
		GWT_CiBean local[] = (GWT_CiBean[])additionLocalBeans.toArray(new GWT_CiBean[0]);
		GWT_CiBean base[] = null;
		
		if (!isNew) {
			additionBaseBeans.add(getBase());
			base = (GWT_CiBean[])additionBaseBeans.toArray(new GWT_CiBean[0]);
		}
		
		OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(), 
				local, 
				base, 
				new AsyncCallback() {

					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
						
					}

					public void onSuccess(Object result) {
						if (result instanceof GWT_RfcResult) {
							GWT_RfcResult rfcResult = (GWT_RfcResult)result;
							if (rfcResult.isRejected()) {
								onFailure(new Exception("ERROR: " + rfcResult.getRejectCause()));
							} else {
								callback.onSuccess(getLocal());
							}
							return;
						}
						onFailure(new Exception("ERROR: " + result));
					}
			
		});
	}

	protected void beforeStore() {
	}

	public boolean isReadonly() {
		return(isReadonly);
	}

	public void addNewBean(GWT_CiBean bean) {
		this.additionLocalBeans.add(bean);
		
	}

	public void addModifiedBean(GWT_CiBean local, GWT_CiBean base) {
		this.additionLocalBeans.add(local);
		this.additionBaseBeans.add(base);
		
		
	}

	public void delete(final AsyncCallback callback) {
		OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(), 
				null, 
				new GWT_CiBean[] {getBase()}, 
				new AsyncCallback() {

					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
						
					}

					public void onSuccess(Object result) {
						if (result instanceof GWT_RfcResult) {
							GWT_RfcResult rfcResult = (GWT_RfcResult)result;
							if (rfcResult.isRejected()) {
								onFailure(new Exception("ERROR: " + rfcResult.getRejectCause()));
							} else {
								callback.onSuccess(getLocal());
							}
							return;
						}
						onFailure(new Exception("ERROR: " + result));
					}
			
		});
			
	}
}
