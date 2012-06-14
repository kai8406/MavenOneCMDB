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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIDisplayNameWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ReferenceTreeControl extends A_GWT_TreeDataSourceControl {

	protected GWT_CiBean root;

	
	public void getRootObject(AsyncCallback callback) {
		callback.onSuccess(root);
	}
	
	public void getChildCount(Object o, final AsyncCallback callback) {
		if (o instanceof GWT_CiBean) {
			GWT_CiBean bean = (GWT_CiBean)o;
			getReferenceMap(bean, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(Object result) {
					if (result instanceof HashMap) {
						HashMap map = (HashMap)result;		
						Integer count = new Integer(map.size());
						callback.onSuccess(count);
					}
				}
			});
			return;
		}
		
		if (o instanceof ReferenceNode) {
			ReferenceNode node = (ReferenceNode)o;
			callback.onSuccess(node.getTargetCount());
		}
	}

	public void getChildren(Object o, Integer firstItem, final AsyncCallback callback) {
		System.out.println("Fetch children for '" + o + "'");
		if (o instanceof GWT_CiBean) {
			final GWT_CiBean bean = (GWT_CiBean)o;
			getReferenceMap(bean, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(Object result) {
					if (result instanceof HashMap) {
						HashMap map = (HashMap)result;
						// Load all refTypes beans
						List resultList = new ArrayList();
						loadReferenceNodes(bean, map, resultList, callback);
					}
					
				}
				
			});
			return;
		}
		
		if (o instanceof ReferenceNode) {
			ReferenceNode ref = (ReferenceNode)o;
			List targetAlias = new ArrayList();
			targetAlias.addAll(ref.getTargetAlias());
			List targets = new ArrayList();
			int first = (firstItem == null) ? 0 : firstItem.intValue();
			loadTargetNodes(targetAlias, targets, first, 0, callback);
			return;
		}
		
	}
	
	public boolean showSearch() {
		return(false);
	}

	protected void getReferenceMap(final GWT_CiBean bean, final AsyncCallback callback) {
		getAttributes(bean, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof List) {
					HashMap map = new HashMap();
					for (Iterator iter = ((List)result).iterator(); iter.hasNext();) {
						GWT_AttributeBean aBean = (GWT_AttributeBean) iter.next();
						if (aBean.getRefType() != null) {
							List to = (List)map.get(aBean.getRefType());
							if (to == null) {
								to = new ArrayList();
								map.put(aBean.getRefType(), to);
							}
							addReferenceValues(to, bean, aBean);
						}
					}
					callback.onSuccess(map);
				}
				
			}
			
		});
	
	}
	
	protected void getAttributes(GWT_CiBean bean, final AsyncCallback callback) {
		if (bean.isTemplate()) {
			callback.onSuccess(bean.getAttributes());
			return;
		}
		OneCMDBConnector.getCIFromAlias(bean.getDerivedFrom(), new AsyncCallback() {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					callback.onSuccess(((GWT_CiBean)result).getAttributes());	
				}
			}
		});
	}

	protected void addReferenceValues(List to, GWT_CiBean bean, GWT_AttributeBean aBean) {
		to.add(aBean.getType());
	}
	
	private void loadReferenceNodes(final GWT_CiBean source, final HashMap map, final List resultList, final AsyncCallback callback) {
		if (map.keySet().iterator().hasNext()) {
			String alias = (String)map.keySet().iterator().next();
			System.out.println("Load Reference " + alias + " map size=" + map.size());
			OneCMDBConnector.getCIFromAlias(alias, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
					
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						GWT_CiBean ref = (GWT_CiBean)result;
						List types = (List)map.get(ref.getAlias());
						map.remove(ref.getAlias());
						resultList.add(new ReferenceNode(source, ref, types));
						System.out.println("Loaded Reference " + ref.getAlias() + " map size=" + map.size());
						if (map.isEmpty()) {
							callback.onSuccess(resultList.toArray());
							return;
						}
						loadReferenceNodes(source, map, resultList, callback);
					}
				}
				
			});
		}
		
	}

	private void loadTargetNodes(final List targetAlias, final List targets, final int first, final int current, final AsyncCallback callback) {
		if (targetAlias.size() > 0) {
			// End of list.
			if ((first + current) >= targetAlias.size()) {
				callback.onSuccess(targets.toArray());
				return;
			}
			if (current >= getMaxResult().intValue()) {
				callback.onSuccess(targets.toArray());
				return;
			}
			String alias = (String)targetAlias.get(first + current);
			
			OneCMDBConnector.getCIFromAlias(alias, new AsyncCallback() {

				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						GWT_CiBean target = (GWT_CiBean)result;
						targets.add(target);
						loadTargetNodes(targetAlias, targets, first, current+1, callback);
						/*
						//targetAlias.remove(0);
						if (targetAlias.isEmpty()) {
							callback.onSuccess(targets.toArray());
							return;
						}
						*/
						
					}
				}
				
			});
		}
	}
	
	

	public Widget getWidget(Object data) {
		if (data instanceof GWT_CiBean) {
			GWT_CiBean bean = (GWT_CiBean)data;
			HorizontalPanel hpanel =  new HorizontalPanel();
			hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
			hpanel.add(new CIDisplayNameWidget(bean, getClickListener()));
			return(hpanel);
		}
		if (data instanceof ReferenceNode) {
			GWT_CiBean bean = ((ReferenceNode)data).getReference();
			HorizontalPanel hpanel =  new HorizontalPanel();
			hpanel.add(new Image(OneCMDBUtils.getIconForCI(bean)));
			hpanel.add(new CIDisplayNameWidget(bean));
			return(hpanel);
		}
		return(new Label("....."));
		
	}

	public void setRoot(GWT_CiBean currentItem) {
		this.root = currentItem;
	}
	
	class ReferenceNode {
		GWT_CiBean source;
		GWT_CiBean reference;
		List targets;
		
		public ReferenceNode(GWT_CiBean source, GWT_CiBean ref, List targets) {
			this.source = source;
			this.reference = ref;
			this.targets = targets;
		}

		public Integer getTargetCount() {
			return(new Integer(this.targets.size()));
		}
		
		public List getTargetAlias() {
			return(targets);
		}
		
		public GWT_CiBean getReference() {
			return(this.reference);
		}
	}
}
