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
package org.onecmdb.core.utils.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;

public class BeanScope implements IBeanScope {

	private IBeanProvider beanProvider;

	private IBeanProvider baseBeanProvider;

	private IBeanProvider remoteBeanProvider;

	private Set<String> externalReferences = new HashSet<String>();

	private RfcContainer rfcContainer = new RfcContainer();


	private List<CiBean> processedBeanOrder = new ArrayList<CiBean>();

	private HashSet<String> processedBeans = new HashSet<String>();

	private HashMap<String, CiBean> beanMap = new HashMap<String, CiBean>();

	private String currentBean;

	private Log log = LogFactory.getLog(this.getClass());

	private HashSet<String> simpleTypesUsed = new HashSet<String>();

	private HashMap<String, CiBean> repositoryBeansUsed = new HashMap<String, CiBean>();

	private HashMap<String, List<CiBean>> duplicatedBeans = new HashMap<String, List<CiBean>>();

	private boolean validationEnabled = true;
	
	private BeanCompare compare;
	
	// {{{ Spring IOC
	public void setBeanProvider(IBeanProvider provider) {
		this.beanProvider = provider;
	}
	
	public IBeanProvider getBeanProvider() {
		return(this.beanProvider);
	}
	
	public void setBaseBeanProvider(IBeanProvider provider) {
		this.baseBeanProvider = provider;
	}

	public void setRemoteBeanProvider(IBeanProvider provider) {
		this.remoteBeanProvider = provider;
	}

	public void setValidation(boolean value) {
		this.validationEnabled = value;
	}
	
	// }}} END Spring IOC

	public void process() {
		// New comparer.
		compare = new BeanCompare();
		BeanRFCGenerator gen = new BeanRFCGenerator();
		gen.setScope(this);
		gen.setRfcContainer(rfcContainer);
		compare.setRfcGenerator(gen);
		
		List<CiBean> baseList = getBaseBeans();
		List<CiBean> beans = beanProvider.getBeans();
		// If we pass in a base list of beans then
		// we enforce more strict compare, with ID,
		// that will support modification on alias.
		boolean hasID = true;
		if (baseList.size() == 0 || beans.size() == 0) {
			hasID = false;
		}
		for (CiBean b : baseList) {
			if (b.getId() == null) {
				hasID = false;
				break;
			}
			CiBean localBean = beanProvider.getBean(b.getAlias());
			if (localBean != null) {
				if (localBean.getId() == null) {
					hasID = false;
					break;
				}
			}
		}
		
		if (hasID) {
			setValidation(false);
			compare.compareID(beans, baseList);
		} else {
			
			// Create mapping.
			
			for (CiBean bean : beans) {
				if (beanMap.containsKey(bean.getAlias())) {
					List<CiBean> list = duplicatedBeans.get(bean.getAlias());
					if (list == null) {
						list = new ArrayList<CiBean>();
						duplicatedBeans.put(bean.getAlias(), list);
					}
					list.add(bean);
					list.add(beanMap.get(bean.getAlias()));
				} else {
					beanMap.put(bean.getAlias(), bean);
				}
			}
	
			for (CiBean bean : beans) {
				currentBean = bean.getAlias();
				if (!processedBeans.contains(currentBean)) {
					processBean(currentBean);
				}
			}
			
			// Process base beans to handle delete.
			for (CiBean base : getBaseBeans()) {
				String beanName = base.getAlias();
				CiBean bean = beanMap.get(beanName);
				// If bean exists we have already processed it.
				if (bean == null) {
					compare.compare(getBaseBean(beanName), getRemoteBean(beanName), bean);
				}
			}
		}
		
		log.info("External references <validation enabled="+ validationEnabled +">");
		for (String s : externalReferences) {
			log.info("\t" + s);
		}

		dumpRfcs(rfcContainer.getOrderedRfcs(), 0);
	}

	public Set<String> getUnresolvedAliases() {
		return (externalReferences);
	}

	public List<IRFC> getRFCs() {
		return (rfcContainer.getOrderedRfcs());
	}

	private void dumpRfcs(List<IRFC> rfcs, int level) {
		if (!log.isDebugEnabled()) {
			return;
		}
		
		String tab = "";
		for (int i = 0; i < level; i++) {
			tab += " ";
		}
		for (IRFC rfc : rfcs) {
			log.debug(tab + rfc.getSummary());
			dumpRfcs(rfc.getRfcs(), level + 1);
		}
	}

	
	public void referenceBean(CiBean source, String how, String beanName) {
		if (!validationEnabled ) {
			return;
		}
		
		CiBean bean = beanMap.get(beanName);
		if (bean == null) {
			
			// validate agains removeProvider...
			if (remoteBeanProvider != null) {
				CiBean remoteBean = remoteBeanProvider.getBean(beanName);
				if (remoteBean != null) {
					repositoryBeansUsed.put(beanName, remoteBean);
					return;
				}
			}
			// Check if primitive type...
			IType type = SimpleTypeFactory.getInstance().toType(beanName);
			if (type != null) {
				simpleTypesUsed.add(beanName);
			} else {
				log.warn("Bean '" + beanName + "' is not resolved!");
				externalReferences.add(beanName);
			}
		
		}
	}

	public void processBean(String beanName) {
		log.debug("Process BEAN - " + beanName);
		if (processedBeans.contains(beanName)) {
			if (currentBean.equals(beanName)) {
				// Cyclic dependency give up.
				throw new IllegalArgumentException("Cyclic bean dependency to "
						+ beanName);
			}
			return;
		}
		CiBean bean = beanMap.get(beanName);
		if (bean == null) {
			//referenceBean(beanName);
			return;
		}
	
		log.info("Processing  " + beanName);
		
		// Add it before here to avoid cyclic loops,
		// will be detected by the currentBean.
		processedBeans.add(beanName);

		// Could we support 3 way compare?
		// Cmp base against remote.
		// if no changes.. --> cmp base local
		// if changes conflict!!!!

		// New worker.
		compare.compare(getBaseBean(beanName), getRemoteBean(beanName), bean);
		//worker.cmpBean(getRemoteBean(beanName), bean, this, rfcContainer);
		
		if (bean != null) {
			processedBeanOrder.add(bean);
		}
	}
	
	private List<CiBean> getBaseBeans() {
		if (baseBeanProvider == null) {
			return (Collections.EMPTY_LIST);
		}
		List<CiBean> baseBeans = baseBeanProvider.getBeans();
		return (baseBeans);
	}
	
	private CiBean getBaseBean(String alias) {
		if (baseBeanProvider == null) {
			return (null);
		}
		CiBean baseBean = baseBeanProvider.getBean(alias);
		return (baseBean);
	}
	
	public CiBean getLocalBean(String alias) {
		return(beanMap.get(alias));
	}
	
	public CiBean getRemoteBean(String alias) {
		if (remoteBeanProvider == null) {
			return (null);
		}
		CiBean remoteBean = remoteBeanProvider.getBean(alias);
		return (remoteBean);
	}

	public List<CiBean> getProcessedBeans() {
		return (processedBeanOrder);

	}

	public Set<String> getSimpleTypesUsed() {
		return (this.simpleTypesUsed);

	}

	public HashMap<String, CiBean> getReposiotryBeanUsed() {
		return (this.repositoryBeansUsed);
	}

	public HashMap<String, List<CiBean>> getDuplicatedBeans() {
		return (duplicatedBeans);
	}

	


}
