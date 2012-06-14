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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;



public class OneCmdbBeanProvider implements IBeanProvider {

	Log log = LogFactory.getLog(this.getClass());
	
	private IModelService modelService;

	private HashMap<String, CiBean> aliasBeanCache = new HashMap<String, CiBean>();

	private HashMap<Long, Set<IAttribute>> attributeMap = new HashMap<Long, Set<IAttribute>>();
	private HashMap<Long, Set<IAttribute>> attributeValueMap = new HashMap<Long, Set<IAttribute>>();

	private int cacheAttrDefHit;

	private int cacheAttrDefRef;

	private int cacheAttrHit;

	private int cacheAttrRef;
	
	public void setModelService(IModelService modelService) {
		this.modelService = modelService;
	}

	public List<CiBean> getBeans() {
		Set<ICi> cis = modelService.getAllCis();
		List<CiBean> beans = new ArrayList<CiBean>();
		for (ICi ci : cis) {
			CiBean bean = convertCiToBean(ci);
			beans.add(bean);
		}
		return(beans);
	}

	public CiBean getBean(String alias) {
		if (modelService == null) {
			return (null);
		}
		CiBean  bean = aliasBeanCache.get(alias);
		if (bean != null) {
			return(bean);
		}
		ICi ci = modelService.findCi(new Path<String>(alias));
		
		if (ci == null) {
			return (null);
		}
		bean = convertCiToBean(ci);
		if (bean != null) {
			aliasBeanCache.put(alias, bean);
		}
		return (bean);
	}

	public CiBean convertCiToBean(ICi ci) {
		CiBean bean = BeanCache.getInstance().get(ci);
		if (bean != null) {
			return(bean);
		}
		
		bean = new CiBean();
		ICi parent = ci.getDerivedFrom();

		bean.setAlias(ci.getAlias());
		if (parent != null) {
			bean.setDerivedFrom(parent.getAlias());
		}

		bean.setTemplate(ci.isBlueprint());
		bean.setDisplayNameExpression(ci.getDisplayNameExpression());
		bean.setDisplayName(ci.getDisplayName());
		bean.setId(ci.getId().asLong());
		bean.setDescription(ci.getDescription());
		bean.setCreateDate(ci.getCreateTime());
		bean.setLastModified(ci.getLastModified());
		ICi group = ci.getGroup();
		if (group != null) {
			bean.setGroup(group.getAlias());
		}
		
		updateBean(bean, ci);
		/*
		List<AttributeBean> aBeans = getAttributeBeans(ci);
		bean.setAttributes(aBeans);

		List<ValueBean> vBeans = getValueBeans(ci);
		bean.setAttributeValues(vBeans);
		*/
		BeanCache.getInstance().add(ci, bean);
		
		return (bean);
	}

	
	public void updateAttributeValue(CiBean bean, ICi ci) {
		Set<IAttribute> attributes = getAttributeValues(ci);
		for (IAttribute a : attributes) {
			// Set value
			ValueBean vBean = new ValueBean();
			vBean.setAlias(a.getAlias());
			vBean.setId(a.getId().asLong());
			vBean.setComplexValue(a.isComplexValue());
			if (a.getLastModified() == null) {
				vBean.setLastModified(a.getCreateTime());
			} else {
				vBean.setLastModified(a.getLastModified());
			}
			IValue value = a.getValue();
			if (value != null) {
				if (value instanceof ICi) {
					vBean.setValue(((ICi) value).getAlias());
				} else {
					vBean.setValue(value.getAsString());
				}
			}
			bean.addAttributeValue(vBean);
		}
	}
	
	public void updateAttributeDefs(CiBean bean, ICi ci) {
		Set<IAttribute> attrDefs = getAttributeDefinitions(ci);
		for (IAttribute a : attrDefs) {
			//if (a.getDerivedFrom() == null) {
			AttributeBean aBean = new AttributeBean();
			
			//if (a.isDerived()) {
			//if (!a.getOwner().equals(ci)) {
			aBean.setDerived(a.isDerived());
			//}
			aBean.setDisplayName(a.getDisplayNameExpression());
			aBean.setAlias(a.getAlias());
			aBean.setId(a.getId().asLong());
			aBean.setLastModified(a.getLastModified());
			aBean.setCreateDate(a.getCreateTime());
					
			if (a.getMaxOccurs() < 0) {
				aBean.setMaxOccurs("unbound");
			} else {
				aBean.setMaxOccurs("" + a.getMaxOccurs());
			}
			aBean.setMinOccurs("" + a.getMinOccurs());
			aBean.setDescription(a.getDescription());
			IType type = a.getValueType();
			if (type == null) {
				log.error("Attribute id='" + a.getId() + "' '" + a +"' has no type");
				continue;
			}
			// Detirmnie ComplexType!
			if (type instanceof ICi) {
				IType ref = a.getReferenceType();
				if (ref != null) {
					aBean.setRefType(ref.getAlias());
				}
				aBean.setComplexType(true);
			} else {
				aBean.setComplexType(false);
			}
			aBean.setType(type.getAlias());
			bean.addAttribute(aBean);
		}
	}
	
	public void updateBean(CiBean bean, ICi ci) {
		if (ci.isBlueprint()) {
			updateAttributeDefs(bean, ci);
		}
		updateAttributeValue(bean, ci);
	}
	
	private Set<IAttribute> getAttributeDefinitions(ICi ci) {
		
		Set<IAttribute> attrList = attributeMap.get(ci.getId().asLong());
		cacheAttrDefRef++;
		if (attrList != null) {
			cacheAttrDefHit++;
			return(attrList);
		}
		
		return(ci.getAttributes());
	}

	private Set<IAttribute> getAttributeValues(ICi ci) {
		Set<IAttribute> attrList = attributeValueMap.get(ci.getId().asLong());
		cacheAttrRef++;
		if (attrList != null) {
			cacheAttrHit++;

			return(attrList);
		}
		return(ci.getAttributes());
	}

	public List<ValueBean> getValueBeans(ICi ci) {
		ArrayList<ValueBean> vBeans = new ArrayList<ValueBean>();

		for (IAttribute a : ci.getAttributes()) {
			IValue value = a.getValue();
			if (value != null) {
				ValueBean vBean = new ValueBean();
				vBean.setAlias(a.getAlias());
				if (value instanceof ICi) {
					vBean.setValue(((ICi) value).getAlias());
					vBean.setComplexValue(true);
				} else {
					vBean.setValue(value.getAsString());
					vBean.setComplexValue(false);
				}
				vBeans.add(vBean);
			}
		}
		return (vBeans);
	}

	public List<AttributeBean> getAttributeBeans(ICi ci) {
		ArrayList<AttributeBean> aBeans = new ArrayList<AttributeBean>();

		Set<IAttribute> attributes = getAttributeDefinitions(ci);
		for (IAttribute a : attributes) {
			AttributeBean aBean = new AttributeBean();
			aBean.setDisplayName(a.getDisplayNameExpression());
            aBean.setAlias(a.getAlias());
			if (a.getMaxOccurs() < 0) {
				aBean.setMaxOccurs("unbound");
			} else {
				aBean.setMaxOccurs("" + a.getMaxOccurs());
			}
			aBean.setMinOccurs("" + a.getMinOccurs());

			IType type = a.getValueType();
			if (type == null) {
				log.error("Attribute id='" + a.getId() + "' '" + a +"' has no type");
				continue;
			}
			// Detrimnie ComplexType!!!!!
			if (type instanceof ICi) {
				IType ref = a.getReferenceType();
				if (ref != null) {
					aBean.setRefType(ref.getAlias());
				}
				aBean.setComplexType(true);
			} else {
				aBean.setType(type.getAlias());
				aBean.setComplexType(false);
			}
			aBeans.add(aBean);
		}

		return (aBeans);
	}

	/**
	 * Convert list<ICi> to list of CiBean<>
	 * 
	 * @param cis
	 * @return
	 */
	public List<CiBean> convertCIsToBeans(List<ICi> cis) {
		long start = System.currentTimeMillis();
		long stop = 0;
		if (false) {
			List<Long> ids = new ArrayList<Long>();
			for (ICi ci : cis) {
				// Check if inside bean cache
				CiBean bean = BeanCache.getInstance().get(ci);
				if (bean == null) {
					ids.add(ci.getId().asLong());
				}
			}

			if (ids.size() > 0) {
				// Query all attributes for these...
				DetachedCriteria crit = DetachedCriteria.forClass(BasicAttribute.class);
				crit.add(Expression.in("ownerId", ids));
				List attributes = modelService.queryCrtiteria(crit, new PageInfo());
				stop = System.currentTimeMillis();
				log.info("Convert " + cis.size() + " ci's, " + attributes.size() + " attributes loaded in " + (stop-start) + "ms");
		
				for (BasicAttribute a : (List<BasicAttribute>)attributes) {
					if (a.isBlueprint()) {
						Set<IAttribute> set = attributeMap.get(a.getOwnerId());
						if (set == null) {
							set = new HashSet<IAttribute>();
							attributeMap.put(a.getOwnerId(), set);
						}
						set.add(a);
					} else {
						Set<IAttribute> set = attributeValueMap.get(a.getOwnerId());
						if (set == null) {
							set = new HashSet<IAttribute>();
							attributeValueMap.put(a.getOwnerId(), set);
						}
						set.add(a);
					}
				}
			}
		}
		long start2 = System.currentTimeMillis();
		List<CiBean> beans = new ArrayList<CiBean>();
		for (ICi ci : cis) {
			CiBean bean = convertCiToBean(ci);
			beans.add(bean);
		}
		stop = System.currentTimeMillis();
		log.info("Convert " + cis.size() + " ci's in " + (stop-start) + "(" + (stop-start2) +")" + "ms. Attr Ref " + cacheAttrHit + "(" + cacheAttrRef + ") Def " + cacheAttrDefHit + "(" + cacheAttrDefRef + ")");
		

		return(beans);
	}

}
