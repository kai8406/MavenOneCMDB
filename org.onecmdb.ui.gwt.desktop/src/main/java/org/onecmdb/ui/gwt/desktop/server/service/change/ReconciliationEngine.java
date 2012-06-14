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
package org.onecmdb.ui.gwt.desktop.server.service.change;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.tomcat.util.buf.CharChunk.CharInputChannel;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias;
import org.onecmdb.core.internal.ccb.rfc.RFCNewCi;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.xml.BeanCompare;
import org.onecmdb.core.utils.xml.BeanRFCGenerator;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.IBeanScope;
import org.onecmdb.core.utils.xml.RfcContainer;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeAttribute;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeCI;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeItem;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeValue;

public class ReconciliationEngine implements IBeanScope {

	private ICIMDR local;
	private ICIMDR base;
	private ICIMDR remote;
	private String token;
	private BeanCompare cmp;
	private HashMap<String, CiBean> localBeanMap = new HashMap<String, CiBean>();
	private HashSet<String> processedBeans = new HashSet<String>();
	// The resulting ChnageItems...
	List<ChangeItem> result = new ArrayList<ChangeItem>();
	
	// Statistics
	private HashSet<String> remoteBeansReferenced = new HashSet<String>();
	private HashSet<String> simpleTypesUsed = new HashSet<String>();
	private HashSet<String> externalReferences = new HashSet<String>();
	private Graph remoteResult;
	private HashMap<String, CiBean> remoteBeanCache = new HashMap<String, CiBean>();
	
	class RFCTarget {
		public CiBean bean;
		public AttributeBean attr;
		public ValueBean value;
	}
	
	public ReconciliationEngine(String token, ICIMDR local, ICIMDR base, ICIMDR remote) {
		this.local = local;
		this.base = base;
		this.remote = remote;
		this.token = token;
		
		BeanRFCGenerator gen = new BeanRFCGenerator();
		gen.setScope(this);
		gen.setRfcContainer(new RfcContainer());
		this.cmp = new BeanCompare();
		this.cmp.setRfcGenerator(gen);
	}
	
	
	
	public List<ChangeItem> getResult() {
		return result;
	}



	private List<CiBean> getBeans(ICIMDR mdr) {
		// Fetch all templates...
		GraphQuery query = new GraphQuery();
		ItemOffspringSelector templates = new ItemOffspringSelector("template", "Root");
		templates.setMatchTemplate(true);
		templates.setPrimary(true);
		
		/*
		ItemOffspringSelector references = new ItemOffspringSelector("reference", "Reference");
		references.setMatchTemplate(true);
		references.setPrimary(true);
		*/
		
		ItemOffspringSelector instances = new ItemOffspringSelector("instance", "Root");
		instances.setMatchTemplate(false);
		instances.setPrimary(true);
		
		query.addSelector(templates);
		query.addSelector(instances);
		//query.addSelector(references);
	
		Graph g = mdr.query(token, query);
		
		List<CiBean> beans = new ArrayList<CiBean>();
		for (Template t : g.getNodes()) {
			if (t.getOffsprings() == null) {
				continue;
			}
			for (CiBean bean : t.getOffsprings()) {
				beans.add(bean);
			}
		}
		return(beans);
	}
	
	
	protected List<IRFC> compare() {
		List<CiBean> beans = getBeans(local);
		
		if (beans.size() > 0) {
			GraphQuery remoteQ = new GraphQuery();
			List<String> aliases = new ArrayList<String>();
			for (CiBean bean: beans) {
				if (localBeanMap.containsKey(bean.getAlias())) {
					// Error, duplicated alias...
					getResult().add(toChanges(bean, ChangeItem.STATUS_ERROR_DUPLICATED, ""));
				}
				localBeanMap.put(bean.getAlias(), bean);

				// Query remote ...
				aliases.add(bean.getAlias());
			}
			ItemAliasSelector sel = new ItemAliasSelector("alias", "Root");
			sel.setAliases(aliases);
			sel.setPrimary(true);
			remoteQ.addSelector(sel);
			// Query remote...
			if (remote != null) {
				remoteResult = remote.query(token, remoteQ);
				remoteResult.buildMap();
			}
		}
		
		//result.addAll(compareBeans(g.fetchNode(templates.getId()).getOffsprings()));

		// Process all beans...
		for (CiBean bean : localBeanMap.values()) {
			processBean(bean.getAlias());
		}
		
		// Check for deletes
		List<CiBean> baseBeans = getBeans(base);
		for (CiBean bean: baseBeans) {
			if (localBeanMap.containsKey(bean.getAlias())) {
				continue;
			}
			// Check if the remote contains this.
			CiBean remoteBean = remote.getCI(token, bean.getAlias());
			if (remoteBean != null) {
				this.cmp.getRfcGenerator().removeCi(bean);
			}
		}
		
		List<IRFC> rfcs = cmp.getRfcGenerator().getRfcContainer().getOrderedRfcs();
		
		return(rfcs);
	}
	
	public List<ChangeItem> reconciliate() {
		HashMap<String, ChangeItem> items = new HashMap<String, ChangeItem>();
		List<IRFC> rfcs = compare();
		for (IRFC rfc : rfcs) {
			RFCTarget target = getBeanFromRFC(rfc);
			if (target == null) {
				continue;
			}
			CiBean bean = target.bean;
			if (bean == null) {
				continue;
			}
			if (rfc instanceof RFCNewCi) {
				if (items.containsKey(bean.getAlias())) {
					items.remove(bean);
				}
				
				ChangeItem item = toChanges(bean, ChangeItem.STATUS_NEW, "");
				items.put(bean.getAlias(), item);
				//updateBeanSummary(bean, item);
				continue;
			}
			
			if (rfc instanceof RFCDestroy && target.attr == null && target.value == null) {
				if (items.containsKey(bean.getAlias())) {
					items.remove(bean.getAlias());
				}
				ChangeItem item = toChanges(bean, ChangeItem.STATUS_DELETE, "");
				items.put(bean.getAlias(), item);
				//updateBeanSummary(bean, item);
				continue;
			}
			ChangeItem item = items.get(bean.getAlias());
			if (item == null) {
				item = toChanges(bean, ChangeItem.STATUS_MODIFIED, "");
			}
			if (item.getStatus().equals(ChangeItem.STATUS_DELETE) 
					|| item.getStatus().equals(ChangeItem.STATUS_NEW)) {
				continue;
			}
			String summary = item.get("summary", "");
			if (rfc instanceof RFCDestroy) {
				if (target.attr != null) {
					summary = summary + "<br/> Delete attribute " + target.attr.getAlias();
				} else if (target.value != null) {
					summary = summary + "<br/> Delete value " + target.value.getAlias();
				}
			} else {
				summary = summary + "<br/>" + rfc.getSummary();
			}
			item.set("summary", summary);
			items.put(bean.getAlias(), item);
		}
		for (String alias : items.keySet()) {
			CiBean bean = getBean(alias);
			if (bean != null) {
				updateBeanSummary(bean, items.get(alias));
			}
		}
		getResult().addAll(items.values());
		return(result);
			
	}
	
	private void updateBeanSummary(CiBean bean, ChangeItem item) {
		StringBuffer b = new StringBuffer();
		String summary = item.get("summary");
		if (summary != null) {
			b.append(summary);
			b.append("<br><hr><br>");
		}
		b.append("<br/>Display name : " + handleNull(bean.getDisplayName()) + "<br/>");
		b.append("Template : " +handleNull(bean.getDerivedFrom()) + "<br/><br/>");

		if (bean.isTemplate()) {
			b.append("<b>Attributes</b><br>");
			b.append("<table>");
			b.append("<tr>");
			b.append("<td><b>" + "Name" + "</b></td>");
			b.append("<td><b>" + "Alias" + "</b></td>");
			b.append("<td><b>" + "Type" + "</b></td>");
			b.append("<td><b>" + "Ref. Type" + "</b></td>");
			b.append("</tr>");
			for (AttributeBean aBean : bean.getAttributes()) {
				b.append("<tr>");
				b.append("<td>" +  handleNull(aBean.getDisplayName()) + "</td>");
				b.append("<td>" +  handleNull(aBean.getAlias()) + "</td>");
				b.append("<td>" +  handleNull(aBean.getType()) + "</td>");
				b.append("<td>" +  handleNull(aBean.getRefType()) + "</td>");
				b.append("<tr>");
			}
			b.append("</table>");
		} 
		
		// Validate Attributes.
		CiBean templateBase = null;
		CiBean templateRemote = null;
		
		if (!bean.isTemplate()) {
			// Validate Attributes.
			templateBase = getBaseBean(bean.getDerivedFrom());
			templateRemote = getRemoteBean(bean.getDerivedFrom());
		}
		b.append("<b>Values</b><br>");
		b.append("<table>");
		for (ValueBean vBean : bean.getAttributeValues()) {
			
			if (!attributeExists(templateBase, templateRemote, vBean)) {
				item.setStatus(ChangeCI.STATUS_ERROR_MISSING_ATTRIBUTE);
				b.append("<tr>");
				b.append("<td><i>" + "Not defined in template" + "</i></td>");
				b.append("<td><b>" + handleNull(vBean.getAlias()) + "</b></td>");
				b.append("<td>" + handleNull(vBean.getValue()) + "</td>");
				b.append("<tr>");
			} else {
				b.append("<tr>");
				b.append("<td><b>" + handleNull(vBean.getAlias()) + "</b></td>");
				b.append("<td>" + handleNull(vBean.getValue()) + "</td>");
				b.append("<tr>");
			}
		}
		b.append("</table>");
		
		item.set("summary", b.toString());
	}



	private boolean attributeExists(CiBean templateBase, CiBean templateRemote,
			ValueBean bean) {
		if (templateRemote == null && templateBase == null) {
			return(true);
		}
		if (templateRemote != null) {
			if (templateRemote.getAttribute(bean.getAlias()) != null) {
				return(true);
			}
		}
		if (templateBase != null) {
			if (templateBase.getAttribute(bean.getAlias()) != null) {
				return(true);
			}
		}
		return(false);
	}



	private String handleNull(String value) {
		if (value == null) {
			return("");
		}
		return(value);
	}


	
	private RFCTarget getBeanFromRFC(IRFC rfc) {
		if (rfc instanceof RFCNewCi) {
			for (IRFC child : rfc.getRfcs()) {
				if (child instanceof RFCModifyAlias) {
					String alias = ((RFCModifyAlias)child).getNewAlias();
					RFCTarget target = new RFCTarget();
					target.bean = localBeanMap.get(alias);
					return(target);
				}
			}
			return(null);
		}
		
		if (rfc.getTargetId() != null) {
			Long id = rfc.getTargetId();
			// Try find value.
			RFCTarget found = null;
			for (CiBean bean : localBeanMap.values()) {
				for (AttributeBean a : bean.getAttributes()) {
					if (id.equals(a.getId())) {
						found = new RFCTarget();
						found.bean = bean;
						found.attr = a;
						break;
					}
				}
				if (found != null) {
					return(found);
				}
			
				for (ValueBean v : bean.getAttributeValues()) {
					if (id.equals(v.getId())) {
						found = new RFCTarget();
						found.bean = bean;
						found.value = v;
						break;
					}
				}
				if (found != null) {
					return(found);
				}
			}
			for (CiBean bean : getBeans(base)) {
				for (AttributeBean a : bean.getAttributes()) {
					if (id.equals(a.getId())) {
						found = new RFCTarget();
						found.bean = bean;
						found.attr = a;
						break;
					}
				}
				if (found != null) {
					return(found);
				}
			
				for (ValueBean v : bean.getAttributeValues()) {
					if (id.equals(v.getId())) {
						found = new RFCTarget();
						found.bean = bean;
						found.value = v;
						break;
					}
				}
				if (found != null) {
					return(found);
				}
			}
			
		}
		String alias = rfc.getTargetAlias();
		RFCTarget target = new RFCTarget();
		target.bean = getBean(alias);
		return(target);
	}

	public CiBean getBean(String alias) {
		if (alias == null) {
			return(null);
		}
		CiBean bean = localBeanMap.get(alias);
		if (bean != null) {
			return(bean);
		}
		bean = remoteBeanCache .get(alias);
		if (bean != null) {
			return(remoteBeanCache.get(alias));
		}
		bean = remote.getCI(token, alias);
		if (bean != null) {
			remoteBeanCache.put(alias, bean);
			return(bean);
		}
		bean = base.getCI(token, alias);
		if (bean != null) {
			return(bean);
		}
		return(null);
	}
	
	protected List<ChangeItem> compareBeans(List<CiBean> localBeans) {
		
		List<ChangeItem> result = new ArrayList<ChangeItem>();
		for (CiBean localBean : localBeans) {
			processBean(localBean.getAlias());
			
		}
		return(result);
	}

	private ChangeCI toChanges(CiBean bean, String status, String info) {
		
		ChangeCI change = new ChangeCI();
		change.setStatus(status);
		change.set("info", info);
		if (bean == null) {
			change.set("alias", "Unknown");
			change.set("derivedFrom", "Unknown");
		} else {
			change.set("alias", bean.getAlias());
			change.set("derivedFrom", bean.getDerivedFrom());
			change.set("type", bean.isTemplate() ? "Template" : "Instance");
		}
		change.set("include", true);
		
		/*
		if (status.equals(ChangeCI.STATUS_NEW)) {
			// Validate Attributes.
			CiBean template = getBaseBean(bean.getDerivedFrom());
			if (template == null) {
				template = getRemoteBean(bean.getDerivedFrom());
			}
			
			if (template == null) {
				// Should not exists, then we should have recived error already.
			} else {
				for (ValueBean v : bean.getAttributeValues()) {
					AttributeBean a = template.getAttribute(v.getAlias());
					if (a == null) {
						change.setStatus(ChangeCI.STATUS_ERROR_MISSING_ATTRIBUTE);
						change.setSu
					}
				}
			}
			
		}
		*/
		return(change);
	}


	protected CiBean match(ICIMDR mdr, CiBean bean) {
		CiBean result = mdr.getCI(token, bean.getAlias());
		return(result);
	}


	public CiBean getLocalBean(String alias) {
		if (this.local == null) {
			return(null);
		}
		return(this.local.getCI(token, alias));
	}
	
	public CiBean getBaseBean(String alias) {
		if (this.base == null) {
			return(null);
		}
		return(this.base.getCI(token, alias));
	}

	public CiBean getRemoteBean(String alias) {
		if (this.remote == null) {
			return(null);
		}
		if (remoteResult != null) {
			CiBean bean = this.remoteResult.findOffspringAlias(alias);
			if (bean != null) {
				return(bean);
			}
		}
		return(this.remote.getCI(token, alias));
	}


	public void processBean(String alias) {
		if (processedBeans.contains(alias)) {
			return;
		}
		CiBean local = localBeanMap.get(alias);
		if (local == null) {
			return;
		}
		processedBeans.add(alias);
		CiBean baseBean = match(base, local);
		
		CiBean remoteBean = null;
		if (remoteResult != null) {
			remoteBean = remoteResult.findOffspringAlias(local.getAlias());
		}
		// Check if we have the same parent..
		if (remoteBean != null) {
			/*
			if (!remoteBean.getDerivedFrom().equals(local.getDerivedFrom())) {
				getResult().add(toChanges(local, ChangeItem.STATUS_ERROR_PARENT_MISSMATCH, "Local <i>" + local.getDerivedFrom() + "</i> != <i>" + remoteBean.getDerivedFrom() + "</i>"));
				return;
			}
			*/
		}
		this.cmp.compare(baseBean, remoteBean, local);
	}


	public void referenceBean(CiBean source, String how, String alias) {
		CiBean bean = localBeanMap.get(alias);
		if (bean == null) {
			// validate against remotProvider...
			if (remote != null) {
				CiBean remoteBean = remote.getCI(this.token, alias);
				if (remoteBean != null) {
					remoteBeansReferenced.add(alias);
					return;
				}
			}
			// Check if primitive type...
			IType type = SimpleTypeFactory.getInstance().toType(alias);
			if (type != null) {
				simpleTypesUsed.add(alias);
			} else {
				//log.warn("Bean '" + beanName + "' is not resolved!");
				externalReferences.add(alias);
				if ("value".equals(how)) {
					getResult().add(toChanges(source, ChangeItem.STATUS_ERROR_MISSING_INSTANCE, alias + " not found(" + how + ")"));
				} else {
					getResult().add(toChanges(source, ChangeItem.STATUS_ERROR_MISSING_TEMPLATE, alias + " not found(" + how + ")"));
				}
			}
		}
	}



	public IRfcResult commit(List<ChangeItem> items) {
		List<CiBean> localBeans = local.getCI(token);
		List<CiBean> baseBeans = base.getCI(token);
		
		HashMap<String, CiBean> localBeanMap = new HashMap<String, CiBean>();
		HashMap<String, CiBean> baseBeanMap = new HashMap<String, CiBean>();
			
		for (CiBean bean : localBeans) {
			localBeanMap.put(bean.getAlias(), bean);
			// Reset id on local bean.
			bean.setId(null);
		}
		for (CiBean bean : baseBeans) {
			baseBeanMap.put(bean.getAlias(), bean);
		}
		
		
		for (ChangeItem item : items) {
			Boolean b = item.get("include");
			if (b != null && !b.booleanValue()) {
				localBeanMap.remove(item.get("alias"));
				baseBeanMap.remove(item.get("alias"));
			}
		}
		IRfcResult result = remote.update(token, localBeanMap.values().toArray(new CiBean[0]), baseBeanMap.values().toArray(new CiBean[0]));
		
		return(result);
	}
	
	public IRfcResult delete(List<ChangeItem> items) {
		List<CiBean> localBeans = local.getCI(token);
		List<CiBean> baseBeans = base.getCI(token);
		
		HashMap<String, CiBean> localBeanMap = new HashMap<String, CiBean>();
		HashMap<String, CiBean> baseBeanMap = new HashMap<String, CiBean>();
			
		for (CiBean bean : localBeans) {
			localBeanMap.put(bean.getAlias(), bean);
			// Reset id on local bean.
			bean.setId(null);
		}
		for (CiBean bean : baseBeans) {
			baseBeanMap.put(bean.getAlias(), bean);
		}
		
		
		for (ChangeItem item : items) {
			Boolean b = item.get("include");
			if (b != null && !b.booleanValue()) {
				localBeanMap.remove(item.get("alias"));
				baseBeanMap.remove(item.get("alias"));
			}
		}
		IRfcResult result = remote.update(token, new CiBean[0], baseBeanMap.values().toArray(new CiBean[0]));
		
		return(result);
	}
	
}
