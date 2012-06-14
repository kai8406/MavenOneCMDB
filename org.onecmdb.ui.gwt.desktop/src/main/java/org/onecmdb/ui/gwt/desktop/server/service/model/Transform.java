/*
 * Management of Datacenter Resources
 * Lokomo OneCMDB - An Open Source Software for Configuration
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
package org.onecmdb.ui.gwt.desktop.server.service.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.HistoryModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.CIModelUtils;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;

public class Transform {
	public int cacheHit = 0;
	public int cacheMiss = 0;
	
	
	public static void update(String prefix, AttributeColumnConfig model, AttributeBean a) {
		if (a.getDisplayName() == null || a.getDisplayName().length() == 0) {
			model.setName(a.getAlias());
		} else {
			model.setName(a.getDisplayName());
		}
		model.setAlias(a.getAlias());
		model.setId(prefix + "." + CIModel.VALUE_PREFIX + a.getAlias());
		model.setComplex(a.isComplexType());
		model.setType(a.getType());
		model.setRefType(a.getRefType());
		model.setMaxOccurs(a.fetchMaxOccursAsInt());
		model.setEditable(true);
		model.setDescription(a.getDescription());
	}


	private Graph graphCache = null;
	private HashMap<String, CiBean> aliasMap = new HashMap<String, CiBean>();
	
	
	public CIModel convert(ICIMDR mdr, String token, CiBean template, CiBean bean) {
		return(convert(mdr, token, template, bean, true, true));
	}
	
	public CIModel convert(ICIMDR mdr, String token, CiBean template, CiBean bean, boolean attributes, boolean values) {
		CIModel ci = new CIModel();
		ci.setAlias(bean.getAlias());
		ci.setDisplayName(bean.getDisplayName());
		ci.setDisplayNameExpression(bean.getDisplayNameExpression());
		ci.setDerivedFrom(bean.getDerivedFrom());
		ci.setDescription(bean.getDescription());
		ci.setTemplate(bean.isTemplate());
		if (bean.getId() != null) {
			ci.setIdAsString("" + bean.getId());
		}
		ci.setLastModifiedDate(bean.getLastModified());
		ci.setCreateDate(bean.getCreateDate());
		
		if (bean.isTemplate()) {
			template = bean;
			// Set Attribute Beans.
			if (attributes) {
				for (AttributeBean attribute : template.getAttributes()) {
					AttributeModel aModel = convert(mdr, token, attribute);
					ci.addAttribute(aModel);
					//aModel.setParent(ci);
				}
			}
		}
	
		
		// Update values...
		if (values) {
			for (AttributeBean attribute : template.getAttributes()) {
				ValueModel value = null;
				if (attribute.fetchMaxOccursAsInt() == 1) {
					value = convert(mdr, token, bean.fetchAttributeValueBean(attribute.getAlias(), 0));
				} else {
					value = convert(mdr, token, ci, attribute, bean.fetchAttributeValueBeans(attribute.getAlias()));
				}
				//value.setParent(ci);
				if (value != null) {
					ci.setValue(attribute.getAlias(), value);
				}
			}
		}
		// Update Icon Path
		if (ci.isTemplate()) {
			ci.setProperty(CIModel.CI_ICON_PATH, IconMapper.getIcon(bean.toStringValue("icon"), ci.getAlias()));
		} else {
			ci.setProperty(CIModel.CI_ICON_PATH, IconMapper.getIcon(bean.toStringValue("icon"), ci.getDerivedFrom()));
		}
		
		return(ci);
	}

	private ValueModel convert(ICIMDR mdr, String token, ValueBean attribute) {
		if (attribute == null) {
			return(null);
		}
		ValueModel v = new ValueModel();
		if (attribute.getId() != null) {
			v.setIdAsString("" + attribute.getId());
		}
		v.setAlias(attribute.getAlias());
		v.setValue(attribute.getValue());
		v.setValueDisplayName(attribute.getValue());
		v.setIsComplex(attribute.isComplexValue());
		if (attribute.isComplexValue()) {
			CiBean bean = getCI(mdr, token, attribute.getValue());
			if (bean != null) {
				v.setValueDisplayName(bean.getDisplayName());
				v.set(CIModel.CI_ICON_PATH, IconMapper.getIcon(bean.toStringValue("icon"), bean.getDerivedFrom()));
			}
		}
		return(v);
	}
	
	private ValueListModel convert(ICIMDR mdr, String token, CIModel ci, AttributeBean aBean, List<ValueBean> list) {
		ValueListModel mList = new ValueListModel();
		mList.setAlias(aBean.getAlias());
		mList.setIsComplex(aBean.isComplexType());
		for (ValueBean v : list) {
			ValueModel m = convert(mdr, token, v);
			mList.addValue(m);
			//m.setParent(ci);
		}
		return(mList);
	}

	private  AttributeModel convert(ICIMDR mdr, String token, AttributeBean attribute) {
		AttributeModel model = new AttributeModel();
		if (attribute.getId() != null) {
			model.setIdAsString("" + attribute.getId());
		}
		model.setAlias(attribute.getAlias());
		model.setDisplayName(attribute.getDisplayName());
		model.setComplex(attribute.isComplexType());
		model.setDescription(attribute.getDescription());
		if (attribute.isComplexType()) {
			CiBean type = getCI(mdr, token, attribute.getType());
			if (type != null) {
				model.setComplexType(convert(mdr, token, type, type, false, false));
			}
			CiBean refType = getCI(mdr, token, attribute.getRefType());
			if (refType != null) {
				model.setRefType(convert(mdr, token, refType, refType, false, false));
			}
		} else {
			model.setSimpleType(attribute.getType());
		}
		model.setMaxOccur(attribute.getMaxOccurs());
		model.setMinOccur(attribute.getMinOccurs());
		model.setDerived(attribute.isDerived());
		
		return(model);
	}


	public List<CiBean> convert(ICIMDR mdr, String token,
			List<? extends ModelItem> local) {
		List<CiBean> beans = new ArrayList<CiBean>();
		for (ModelItem item : local) {
			if (item instanceof CIModelCollection) {
				beans.addAll(convert(mdr, token, (CIModelCollection)item));
			}
			if (item instanceof CIModel) {
				beans.add(convert(mdr, token, (CIModel)item));
			}
		}
		return(beans);
	}
	
	public List<CiBean> convert(ICIMDR mdr, String token,
			CIModelCollection collection) {
		List<CiBean> beans = new ArrayList<CiBean>();
		
		for (CIModel item : collection.getCIModels()) {
			CiBean bean = convert(mdr, token, item);
			beans.add(bean);
		}
		return(beans);
	}
	public CiBean convert(ICIMDR mdr, String token,
			CIModel ci) {
		CiBean bean = new CiBean();
		String id = ci.getIdAsString();
		Long longID = null;
		try {
			longID = Long.parseLong(id);
		} catch (Exception e) {			
		}
		bean.setId(longID);
		bean.setAlias(ci.getAlias());
		bean.setDisplayNameExpression((String)ci.get(CIModel.CI_DISPLAYNAMEEXPR));
		bean.setDescription((String)ci.get(CIModel.CI_DESCRIPTION));
		bean.setDerivedFrom((String)ci.get(CIModel.CI_DERIVEDFROM));
		bean.setTemplate(ci.isTemplate());
		List<AttributeBean> attrBeans = new ArrayList<AttributeBean>();
		List<AttributeModel> attrModels = ci.getAttributes();
		for (AttributeModel aModel : attrModels) {
			attrBeans.add(convert(mdr, token, aModel));
		}
		bean.setAttributes(attrBeans);
			
		List<ValueBean> valueBeans = new ArrayList<ValueBean>();
		for (ValueModel vModel : ci.getValues()) {
			if (vModel instanceof ValueListModel) {
				valueBeans.addAll(convert(mdr, token, (ValueListModel)vModel));
			} else {
				if (vModel != null) {
					valueBeans.add(convert(mdr, token, vModel));
				}
			}
		}
		bean.setAttributeValues(valueBeans);
		return(bean);
	
	}

	private  List<ValueBean> convert(ICIMDR mdr, String token, ValueListModel model) {
		
		List<ValueBean> beans = new ArrayList<ValueBean>();
		for (ValueModel vm : model.getValues()) {
			beans.add(convert(mdr, token, vm));
		}
		return(beans);
	}

	private  ValueBean convert(ICIMDR mdr, String token, ValueModel model) {
		ValueBean vBean = new ValueBean();
		vBean.setAlias((String)model.get(ValueModel.VALUE_ALIAS));
		vBean.setValue((String)model.get(ValueModel.VALUE_VALUE));
		String id = model.getIdAsString();
		Long longID = null;
		if (id != null && id.length() > 0) {
			try {
				longID = Long.parseLong(id);
			} catch (Exception e) {			
			}
		}
		vBean.setId(longID);
		vBean.setComplexValue((Boolean)model.get(ValueModel.VALUE_ISCOMPLEX, false));
		return(vBean);
	}


	private AttributeBean convert(ICIMDR mdr, String token,
			AttributeModel model) {
		AttributeBean aBean = new AttributeBean();
		aBean.setAlias(model.getAlias());
		aBean.setDisplayName(model.getDisplayName());
		if (model.getSimpleType() != null) {
			aBean.setType(model.getSimpleType());
			aBean.setComplexType(false);
		} else {
			if (model.getComplexType() != null) {
				aBean.setType(model.getComplexType().getAlias());
			}
			if (model.getRefType() != null) {
				//throw new IllegalArgumentException("Attribute " + aBean.getAlias() + " is missing reference type!");
				aBean.setRefType(model.getRefType().getAlias());
			}
			aBean.setComplexType(true);
		}
		aBean.setMaxOccurs(model.getMaxOccur());
		aBean.setMinOccurs(model.getMinOccur());
		aBean.setDerived(model.isDerived());
		aBean.setDescription(model.getDescription());
		
		String id = model.getIdAsString();
		Long longID = null;
		try {
			longID = Long.parseLong(id);
		} catch (Exception e) {			
		}
		aBean.setId(longID);
		return(aBean);
	}

	public static void updateModel(CIModel bean, String alias, Object value) {
		CIModelUtils.updateModel(bean, alias, value);
	}

	public CiBean getCI(ICIMDR mdr, String token, String alias) {
		if (alias == null || alias.length() == 0) {
			return(null);
		}
		// Check cache.
		if (this.graphCache != null) {
			CiBean bean = this.graphCache.findOffspringAlias(alias);
			if (bean != null) {
				cacheHit++;
				return(bean);
			}
		}
		
		// Have internal cache...
		CiBean bean = aliasMap .get(alias);
		if (bean != null) {
			cacheHit++;
			return(bean);
		}
		cacheMiss++;
		bean = mdr.getCI(token, alias);
		if (bean != null) {
			aliasMap.put(bean.getAlias(), bean);
		}
		return(bean);
	}
	
	public void setCache(Graph g) {
		this.graphCache = g;
	}

	public List<HistoryModel> transform(List<RFCBean> beans) {
		List<HistoryModel> result = new ArrayList<HistoryModel>();
		for (RFCBean rfc : beans) {
			result.add(transform(rfc));
		}
		return(result);
	}
	
	public HistoryModel transform(RFCBean bean) {
		HistoryModel model = new HistoryModel();
		model.setTxId(bean.getTransactionId().toString());
		model.setRfcId(bean.getId().toString());
		model.setIssuer(bean.getIssuer());
		model.setSummary(bean.getSummary());
		model.setTs(bean.getTs());
		
		return(model);
	}
	
}
