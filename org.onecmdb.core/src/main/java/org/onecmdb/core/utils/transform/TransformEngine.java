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
package org.onecmdb.core.utils.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox.KeySelectionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.MemoryBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;

/**
 * <code>TransformEngine</code> transforms a data set into CI/attributes.
 * 
 *
 */
public class TransformEngine {
	
	
	private HashMap<String, CiBean> templateCache = new HashMap<String, CiBean>();
	private TransformReport report = new TransformReport();
	private IOneCMDBWebService webService = null;
	private String token;
	private Log log = LogFactory.getLog(this.getClass());
	private boolean validate;
	private int queryCount;
	private HashMap<String, AliasData> aliasCache = new HashMap<String, AliasData>();
	private float queryAvg = 0.0F;
	private int reuseCI;
	private HashMap<String, HashSet<CiBean>> dataSetMap = new HashMap<String, HashSet<CiBean>>(); 
	
	/**
	 * Static entry for the transformation. The bean provider needs to contain all<br>
	 * templates alias defined in the DataSet <code>IInstanceSelector</code>.<br>
	 * <br>
	 * All attributes names defined in <code>IAttributeSelection</code> will also be validated<br> 
	 * against the template's attribute alias returned from the bean provider.<br>
	 * @param beanProvider
	 * @param dataSet
	 * @return
	 * @throws IOException
	 */
	public IBeanProvider transform(IBeanProvider beanProvider, DataSet dataSet) throws IOException {
		MemoryBeanProvider beanSet = new MemoryBeanProvider();
		report.startReport();
		debugDataSet(dataSet, 0);
		// Generate Instances from dataSet.
		try {
			generateInstances(beanSet, dataSet, beanProvider);
			return(beanSet);
		} catch (IOException t) {
			report.addError("Exception:", t);
			throw t;
		} finally {
			report.stopReport();
			log.info("WSDL query avg: " + queryAvg + "ms [matched " + reuseCI + "]");
		}
	}
	
	private void debugDataSet(DataSet dataSet, int level) {
		System.out.println(getTab(level) + dataSet.getName());
		if (dataSet.getInstanceSelector() instanceof ForwardInstanceSelector) {
			for (IAttributeSelector aSelector : dataSet.getAttributeSelector()) {
				if (aSelector instanceof ComplexAttributeSelector) {
					DataSet forwardDataSet = ((ComplexAttributeSelector)aSelector).getDataSet();
					debugDataSet(forwardDataSet, level+1);
				}
			}
		}
	}

	private String getTab(int level) {
		String tab = "";
		for (int i = 0; i < level; i++) {
			tab += "\t";
		}
		return(tab);
	}

	public Object getReport() {
		return(this.report);
	}
	
	private CiBean generateInstance(MemoryBeanProvider resultSet, IInstance instance, IBeanProvider beanProvider) throws IOException {
		CiBean template = getTemplate(beanProvider, instance);
		log.debug("FOUND Template:" + template.getAlias());
		// Generate templates.
		AliasData aliasData = getAliasKey(resultSet, template, instance, beanProvider);
		
		CiBean bean = resultSet.getBean(aliasData.alias); 
		if (bean == null) {
			log.debug("New instance: " + template.getAlias() + "," + aliasData.alias);
			bean = new CiBean();
			bean.setAlias(aliasData.alias);
			if (aliasData.found != null) {
				bean.setDerivedFrom(aliasData.found.getDerivedFrom());
				bean.setId(aliasData.found.getId());
			} else {
				bean.setDerivedFrom(template.getAlias());
			}
			resultSet.addBean(bean);
		} else {
			log.debug("Reuse instance: " + template.getAlias() + "," + aliasData.alias);
		}
		if (aliasData.isProcessed) {
			return(bean);
		}
		for (IAttributeSelector attrSelector : instance.getDataSet().getAttributeSelector()) {
			if (attrSelector.getName().equals("internal_alias")) {
				continue;
			}
			// Validate the the attribute selector match an attribute.
			AttributeBean attributeDefinition = template.getAttribute(attrSelector.getName());
			if (attributeDefinition == null) {
				throw new IOException("Attribute Selector " +
						"<" + instance.getDataSet().getName() + "/" + attrSelector.getName() + "> " +
						"dont't match any attribute in template <" + template.getAlias() + ">");
			}
			IAttributeValue attributeValue = attrSelector.getAttribute(instance);
			
			if (attributeValue == null) {
				continue;
			}
			if (attributeValue.isEmpty()) {
				continue;
			}
			if (attributeValue.isPrimitive()) {
				if (attributeValue.getText() != null) {
					ValueBean vBean = new ValueBean();
					vBean.setAlias(attributeValue.getName());
					vBean.setValue(attributeValue.getText());
					vBean.setComplexValue(attributeValue.isComplex());
					updateValue(bean, attributeDefinition, vBean);
				}
				//bean.addAttributeValue(vBean);
			} else {
				Set<CiBean> refBeans = generateInstances(resultSet, attributeValue.getDataSet(), beanProvider);
				for (CiBean refBean : refBeans) {
					ValueBean vBean = new ValueBean();						
					vBean.setAlias(attributeValue.getName());
					vBean.setValue(refBean.getAlias());
					vBean.setComplexValue(true);
					updateValue(bean, attributeDefinition, vBean);
					//bean.addAttributeValue(vBean);
				}
			}
		}
		aliasData.isProcessed = true;

		return(bean);
	}
	
	/**
	 * Set value on a Ci, handle multiple values.
	 * @param bean
	 * @param attributeDefinition
	 * @param bean2
	 */
	private void updateValue(CiBean bean, AttributeBean def, ValueBean v) {
		// Check for existence.
		List<ValueBean> values = bean.fetchAttributeValueBeans(v.getAlias());
		for (ValueBean existingValue : values) {
			if (existingValue.getValue() != null && v.getValue() != null) {
				if (existingValue.getValue().equals(v.getValue())) {
					return;
				}
			}
		}
		
		if (values.size() == 0) {
			bean.addAttributeValue(v);
			return;
		}
		if ("1".equals(def.getMaxOccurs())) {
			// Single value...
			// Don't overwrite, if we have a value.
			if (values.get(0).getValue() == null) {
				values.get(0).setValue(v.getValue());
				return;
			}
			if (values.get(0).getValue().length() == 0) {
				values.get(0).setValue(v.getValue());
				return;
			}
		} else {
			// Multi value..
			bean.addAttributeValue(v);
		}
		
	}

	public Set<CiBean> generateInstances(MemoryBeanProvider beanSet, DataSet dataSet, IBeanProvider beanProvider) throws IOException {
		Set<CiBean> beans = new HashSet<CiBean>();
		dataSet.setReport(this.report );
		if (dataSet.getInstanceSelector() instanceof ForwardInstanceSelector) {
			for (IAttributeSelector aSelector : dataSet.getAttributeSelector()) {
				IDataSource source = dataSet.getDataSource();
				if (aSelector instanceof ComplexAttributeSelector) {
					DataSet forwardDataSet = ((ComplexAttributeSelector)aSelector).getDataSet();
					forwardDataSet.setDataSource(source);
					Set<CiBean> fBeans = generateInstances(beanSet, forwardDataSet, beanProvider);
					beans.addAll(fBeans);
					source.reset();
				}
			}
		} else {
			for (IInstance row : dataSet.getInstances()) {
				try {
					CiBean bean = generateInstance(beanSet, row, beanProvider);
					beans.add(bean);
					HashSet<CiBean> dsBeans = dataSetMap.get(dataSet.getName());
					if (dsBeans == null) {
						dsBeans = new HashSet<CiBean>();
						dataSetMap.put(dataSet.getName(), dsBeans);
					}
					dsBeans.add(bean);
					
				} catch (Throwable t) {
					IOException e = new IOException("Problem parsing row[" + row.getLocalID() +"]");
					e.initCause(t);
					throw e;
				}
			}
			
		}
		return(beans);
	}
	
	
	public Set<CiBean> getBeansForDataSet(String dsName) {
		return(dataSetMap.get(dsName));
	}
	
	private CiBean getTemplate(IBeanProvider provider, IInstance row) throws IOException {
		if (row.getDataSet().getInstanceSelector() == null) {
			throw new IOException("DataSet " + row.getDataSet().getName() + " is missing instance selector");		
		}
		
		String alias = row.getTemplate();
		
		if (alias == null) {
			throw new IOException("Template alias is null in instance selector " +
					"<" + row.getDataSet().getName() + "/" + row.getDataSet().getInstanceSelector().getName() + ">");
		}
		
		CiBean bean = templateCache.get(alias);
		if (bean == null) {
			
			bean = provider.getBean(alias);
			if (bean == null) {
				throw new IOException("Template alias <" + alias + "> specified in instance selector " +
					"<" + row.getDataSet().getName() + "/" + row.getDataSet().getInstanceSelector().getName() + "> is not found in provider!");
			}
			templateCache.put(alias, bean);
		}
		return(bean);
	}
	
	/**
	 * Generate unique alias key for a IInstance.<br>
	 * The key is generated from the attributes that are tagged<br>
	 * with naturalKey as true.<br>
	 * If the webService is set a query s performed to match<br>
	 * the naturalKeys values. This is performed if the <br>
	 * instances have been generated from another tool than this.<br> 
	 * <br>
	 * @param resultSet
	 * @param template
	 * @param instance
	 * @param beanProvider
	 * @return
	 * @throws IOException
	 */
	private AliasData getAliasKey(MemoryBeanProvider resultSet, CiBean template, IInstance instance, IBeanProvider beanProvider) throws IOException {
		String instanceName = instance.getDataSet().getInstanceSelector().getInstance();
		if (instanceName != null) {
			// Check that we don't have any empty value.
			if (!instanceName.equals("")) {
				return(new AliasData(instanceName, null));	
			}
		}
		String key = template.getAlias();
		
		List<IAttributeSelector> naturalKeySelectors = instance.getDataSet().getNaturalKeys();
		if (naturalKeySelectors.size() == 0) {
			return(new AliasData(key + "-" + new ItemId().asLong(), null));
			//throw new IllegalArgumentException("No natural keys defined on '" + template.getAlias() +"'!");				
		}
		boolean valid = false;
		
		GraphQuery query = new GraphQuery();
		String templ = instance.getDataSet().getInstanceSelector().getTemplate();
		ItemOffspringSelector sel = new ItemOffspringSelector("match",  templ);
		sel.setPrimary(true);
		ItemAndGroupConstraint and = new ItemAndGroupConstraint();
		sel.applyConstraint(and);
		query.addSelector(sel);
		
		String searchValue = "";
		String searchKeys = "";
		
		for (IAttributeSelector attrSelector : naturalKeySelectors) {
			IAttributeValue attribute = attrSelector.getAttribute(instance);
			if (attribute.getName().equals("internal_alias")) {
				CiBean found = null;
				String alias = attribute.getText();
				if (alias == null || alias.length() == 0) {
					alias = template.getAlias() + "-" + new ItemId().asLong();
					
				}
				if (this.webService != null) {
					// Alias query.
					GraphQuery aGraph = new GraphQuery();
					String defTemplate = instance.getDataSet().getInstanceSelector().getTemplate();
					ItemAliasSelector aSel = new ItemAliasSelector("alias", defTemplate);
					
					aSel.setAlias(alias);
					aSel.setPrimary(true);
					aGraph.addSelector(aSel);
					Graph aResult = this.webService.queryGraph(token, aGraph);
					aResult.buildMap();
					if (aResult.fetchAllNodeOffsprings().size() == 1) {
						found = aResult.fetchAllNodeOffsprings().iterator().next();
						resultSet.addBean(found);
					}
				}
				return(new AliasData(alias, found));
			}
			// In absence of valid key make it unique.
			String text = instance.hashCode() + ":" + attrSelector.hashCode();
			
			if (attribute != null) {
				if (attribute.isPrimitive()) {
					String value = attribute.getText();
					//throw new IllegalArgumentException("Natural key field '" + aDef.getColName() +"' in template '" + def.getAlias() +"' has no value in row '" + row.getName() +"'");
					//if (col.getText() == null || col.getText() == "") {
					if (value != null) {
						//throw new IllegalArgumentException("Col '" + col.getName() +"' has no text value in row '" + row.getName() +"'");
						text = attribute.getText();
					} else {
						text = "";
					}
					
					// Build up query..
					AttributeValueConstraint vConstraint = new AttributeValueConstraint();
					vConstraint.setAlias(attrSelector.getName());
					vConstraint.setOperation(AttributeValueConstraint.EQUALS);
					vConstraint.setValue(value);
					searchValue += "&" + value;
					searchKeys += "&" + attrSelector.getName();
					
					and.add(vConstraint);
					
				} else {
					Set<CiBean> refBeans = generateInstances(resultSet, attribute.getDataSet(), beanProvider);
					if (refBeans != null) {
						text = "";
						for (CiBean bean : refBeans) {
							text += ":" + bean.getAlias();
							
							ItemAliasSelector aliasSel = new ItemAliasSelector(bean.getAlias(), "Ci");
							aliasSel.setAlias(bean.getAlias());
							ItemRelationSelector aliasRel = new ItemRelationSelector("match2" + bean.getAlias(), "Reference", bean.getAlias(), "match");
							query.addSelector(aliasSel);
							query.addSelector(aliasRel);
							searchValue += "&ref{" + bean.getAlias() + "}";
							searchKeys += "&" + attrSelector.getName();
							/*
							if (bean.getId() != null) {
								AttributeValueConstraint vConstraint = new AttributeValueConstraint();
								vConstraint.setAlias(attrSelector.getName());
								vConstraint.setOperation(AttributeValueConstraint.LIKE);
								String value = "%" + bean.getIdAsString();
								vConstraint.setValue(value);
								
								searchValue += "&" + value;
								searchKeys += "&" + attrSelector.getName();
								
							}
							*/
						}
					}
				}
			}
			// Mark the key as valid.
			valid = true;
			key += "-" + getKeyForText(text);			
		}
		
		// Validate against the webService if available.
		CiBean foundCi = null;
		
		// Check for cache.
		String hashKey = template.getAlias() + "-" + searchKeys + "-" + searchValue;
		
		AliasData cached = aliasCache.get(hashKey);
		if (cached != null) {
			return(cached);
		}
		if (this.validate && this.webService != null) {
			queryCount++;
			long start = System.currentTimeMillis();
			Graph result = this.webService.queryGraph(this.token, query);
			long stop = System.currentTimeMillis();
			queryAvg  = (queryAvg*(queryCount-1) + (stop-start))/queryCount;	
			Template t = result.fetchNode(sel.getId());
			if (t.getOffsprings() != null) {
				if (t.getOffsprings().size() == 1) {
					CiBean bean = t.getOffsprings().get(0);
					key = bean.getAlias();
					foundCi = bean;
					resultSet.addBean(bean);
				}
				if (t.getOffsprings().size() > 1) {
					// Warning....
					log.warn("Natural keys<" + searchKeys + "> values<" + searchValue + "> on '" + template.getAlias() +"' is not unique for row '" + instance.getName() +"'");
				}
			}
		}
		if (!valid) {
			throw new IllegalArgumentException("Natural keys<" +naturalKeySelectors.size() + "> on '" + template.getAlias() +"' is invalid for row '" + instance.getName() +"'");
		}
		if (foundCi == null) {
			log.debug("CI key " + key + " not found");
		} else {
			log.debug("Found CI: " + foundCi.getDerivedFrom() + ": newTemplate=" + instance.getTemplate());
			// Update derivedFrom
			String orgDerivedFrom = foundCi.getDerivedFrom();
			String newDerivedFrom = instance.getTemplate();
			String defTemplate = templ;
			if (!defTemplate.equals(newDerivedFrom)) {
				if (!orgDerivedFrom.equals(newDerivedFrom)) {
					log.info("Move CI "+ foundCi.getAlias() + " from " + orgDerivedFrom + " to " + newDerivedFrom);
					foundCi.setDerivedFrom(newDerivedFrom);
				}
			}
			reuseCI++;
		}
		if (!instance.isAutoCreate() && foundCi == null) {
			String selName = instance.getDataSet().getName();
			throw new IllegalArgumentException("DateSet[" + selName+ "] : CIs of type " + instance.getDataSet().getInstanceSelector().getTemplate() + " using natural keys<" + searchKeys + "> values<" + searchValue + "> is not found i OneCMDB, marked as auto-create!");
		}
		AliasData data = new AliasData(key, foundCi);
		aliasCache.put(hashKey, data);
		return(data);
	}

	private String getKeyForText(String text) {
		return("" + text.hashCode());
	}
	
	public int getQueryCount() {
		return queryCount;
	}

	/**
	 * Generate basic templates from a DataSet.
	 * 
	 * @param set
	 * @return
	 */
	public IBeanProvider toTemplate(DataSet set) {
		MemoryBeanProvider provider = new MemoryBeanProvider();
		toTemplate(provider, set);
		return(provider);
	}
	
	public CiBean toTemplate(MemoryBeanProvider provider, DataSet set) {
		String alias = set.getInstanceSelector().getTemplate();
		CiBean template = provider.getBean(alias);
		if (template == null) {
			template = new CiBean();
			template.setTemplate(true);
			template.setAlias(alias);
			template.setDerivedFrom("Ci");
			for (IAttributeSelector aSel : set.getAttributeSelector()) {
				AttributeBean aDef = new AttributeBean();
				aDef.setAlias(aSel.getName());
				// Validate type.
				if (aSel instanceof ComplexAttributeSelector) {
					CiBean typeBean = toTemplate(provider, ((ComplexAttributeSelector)aSel).getDataSet());
					aDef.setType(typeBean.getAlias());
					aDef.setRefType("Reference");
					aDef.setComplexType(true);
				} else {
					aDef.setType("xs:string");
					aDef.setComplexType(false);
				}
				template.addAttribute(aDef);
			}
			provider.addBean(template);
		}
		return(template);
	}

	public IOneCMDBWebService getWebService() {
		return webService;
	}

	public void setWebService(IOneCMDBWebService webService) {
		this.webService = webService;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public class AliasData {
		public boolean isProcessed;
		String alias;
		CiBean found;
	
		public AliasData(String a, CiBean b) {
			this.alias = a;
			this.found = b;
		}
	}

	public void setValidate(boolean value) {
		this.validate = value;
		
	}
	
}
