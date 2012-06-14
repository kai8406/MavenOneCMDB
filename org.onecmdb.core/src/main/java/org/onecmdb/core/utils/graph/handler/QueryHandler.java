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
package org.onecmdb.core.utils.graph.handler;

import java.awt.IllegalComponentStateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.RFCSummaryDecorator;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.graph.expression.AttributeExpression;
import org.onecmdb.core.utils.graph.expression.AttributeValueExpression;
import org.onecmdb.core.utils.graph.expression.ItemExpression;
import org.onecmdb.core.utils.graph.expression.OffspringExpression;
import org.onecmdb.core.utils.graph.expression.RFCExpression;
import org.onecmdb.core.utils.graph.expression.RFCRelationExpression;
import org.onecmdb.core.utils.graph.expression.RelationExpression;
import org.onecmdb.core.utils.graph.expression.ItemSecurityExpression;
import org.onecmdb.core.utils.graph.expression.TransactionExpression;
import org.onecmdb.core.utils.graph.expression.TransactionRelationExpression;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.OrderInfo;
import org.onecmdb.core.utils.graph.query.constraint.AttributeSourceRelationConstraint;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemIdConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemNotConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RFCTargetConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRFCSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemTransactionSelector;
import org.onecmdb.core.utils.graph.query.selector.RFCItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.TransactionRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.wsdl.TransactionBean;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;

public class QueryHandler {
	private IModelService msvc;
	private ICcb ccb;
	private ISession session;
	private Log log = LogFactory.getLog(this.getClass());
	//private boolean newSortPageAlgorithm; 

	class QueryResult {
		public List matches;
		public int totalCount;
	}
	
	class QueryExpression {
		public DetachedCriteria criteria;
		public boolean empty;
	}
	
	public QueryHandler(ISession session) {
		this.session = session;
		this.msvc = (IModelService)session.getService(IModelService.class);
		this.ccb = (ICcb)session.getService(ICcb.class);

	}
	
	
	public Graph execute3(GraphQuery query) {
		if (log.isDebugEnabled()) {
			log.debug(toXML(query, 0));
		}
		
		Graph result = new Graph();
		OneCmdbBeanProvider beanProvider = new OneCmdbBeanProvider();
		beanProvider.setModelService((IModelService)session.getService(IModelService.class));
		if (query.fetchSelectors().size() == 0) {
			throw new IllegalArgumentException("No Selectors specified in query!");
		}
		for (ItemSelector sel : query.fetchOrderdItemSelectors()) {
			if (sel.isExcludedInResultSet()) {
				continue;
			}
			GraphQuery nQ = new GraphQuery();
			
			// Resolve dependecies...
			ItemSelector newSelector = buildGraph2(nQ, query, sel);
			
			// Execute query..
			QueryResult qRes = executeQuery(nQ, newSelector);
			
			
			// Create Result Template
			Template template = new Template();
			template.setId(sel.getId());
			if (newSelector.hasTemplate()) {
				template.setTemplate(beanProvider.convertCiToBean(newSelector.fetchTemplate()));
			}
			template.setTotalCount(qRes.totalCount);
			result.addNodes(template);
			
			// Populate items...
			List<Long> mIds = new ArrayList<Long>();
			List<ICi> cis = new ArrayList<ICi>();
			for (Object match : qRes.matches) {
				if (match instanceof ICi) {
					cis.add((ICi)match);
					mIds.add(((ICi)match).getId().asLong());
				} else 	if (match instanceof IRFC) {
					template.addRFC(convert(ccb, (IRFC)match));
					mIds.add(((IRFC)match).getTargetCIId());
				} else if (match instanceof ICmdbTransaction) {
					template.addTransaction(convert(ccb, (ICmdbTransaction)match));
					mIds.add(((ICmdbTransaction)match).getId().asLong());
				}
			}
			if (cis.size() > 0) {
				template.setOffsprings(beanProvider.convertCIsToBeans(cis));
			}
			// Update Relation ids.
			for (ItemRelationSelector rel : query.fetchRelationSelectors()) {
				if (rel.getSource().equals(sel.getId())) {
					rel.setSourceRange(mIds);
				}
				if (rel.getTarget().equals(sel.getId())) {
					rel.setTargetRange(mIds);
				}
			}
		}
		
		for (ItemRelationSelector rel : query.fetchRelationSelectors()) {
			if (rel instanceof RFCItemRelationSelector) {
				continue;
			}
			if (rel instanceof TransactionRelationSelector) {
				continue;
			}
			if (rel.isExcludedInResultSet()) {
				continue;
			}
			QueryResult subRes = null;
			subRes = executeQuery(query, rel);
			Template temp = new Template();
			temp.setId(rel.getId());
			if (rel.hasTemplate()) {
				temp.setTemplate(beanProvider.convertCiToBean(rel.fetchTemplate()));
			}
			temp.setTotalCount(subRes.totalCount);
			List<ICi> cis = new ArrayList<ICi>();
			for (Object match : subRes.matches) {
				if (match instanceof IRFC) {
					temp.addRFC(convert(ccb, (IRFC)match));
				} else if (match instanceof ICi) {
					temp.addOffspring(beanProvider.convertCiToBean((ICi)match));
				} else if (match instanceof ICmdbTransaction) {
					temp.addTransaction(convert(ccb, (ICmdbTransaction)match));
				}
			}
			result.addEdges(temp);
		}
		return(result);
	}
	
	public Graph execute2(GraphQuery query) {
		if (log.isDebugEnabled()) {
			log.debug(toXML(query, 0));
		}
		
		Graph result = new Graph();
		OneCmdbBeanProvider beanProvider = new OneCmdbBeanProvider();

		ItemSelector primarySelector = query.fetchPrimarySelectors();
		if (primarySelector == null) {
			throw new IllegalArgumentException("No primary Selector found!");
		}
		// Setup dependencies...
		GraphQuery nQ = new GraphQuery();
		ItemSelector newSelector = buildGraph2(nQ, query, primarySelector);
	
		QueryResult qRes = executeQuery(nQ, newSelector);
		
		Template primaryTemplate = new Template();
		primaryTemplate.setId(primarySelector.getId());
		if (newSelector.hasTemplate()) {
			primaryTemplate.setTemplate(beanProvider.convertCiToBean(newSelector.fetchTemplate()));
		}
		primaryTemplate.setTotalCount(qRes.totalCount);
		if (primarySelector.reference()) {
			result.addEdges(primaryTemplate);
		} else {
			result.addNodes(primaryTemplate);
		}

		
		List<Long> mIds = new ArrayList<Long>();
		for (Object match : qRes.matches) {
			if (match instanceof ICi) {
				primaryTemplate.addOffspring(beanProvider.convertCiToBean((ICi)match));
				mIds.add(((ICi)match).getId().asLong());
			} else 	if (match instanceof IRFC) {
				primaryTemplate.addRFC(convert(ccb, (IRFC)match));
				mIds.add(((IRFC)match).getTargetCIId());
			} else if (match instanceof ICmdbTransaction) {
				primaryTemplate.addTransaction(convert(ccb, (ICmdbTransaction)match));
				mIds.add(((ICmdbTransaction)match).getId().asLong());
			}
		}
			

		// Update all references with this targets....
		for (ItemRelationSelector rel : query.fetchRelationSelectors()) {
			if (rel.getSource().equals(primarySelector.getId())) {
				rel.setSourceRange(mIds);
			}
			if (rel.getTarget().equals(primarySelector.getId())) {
				rel.setTargetRange(mIds);
			}
		}
		
		for (ItemSelector sel : query.fetchSelectors()) {
			if (sel.equals(primarySelector)) {
				continue;
			}
			QueryResult subRes = null;
			if (sel.reference()) {
				if (sel instanceof RFCItemRelationSelector) {
					continue;
				}
				if (sel instanceof TransactionRelationSelector) {
					continue;
				}
				ItemRelationSelector rel = (ItemRelationSelector)sel;
				subRes = executeQuery(query, sel);
			} else {
				GraphQuery subQuery = new GraphQuery();
				sel = buildGraph2(subQuery, query, sel);
				subRes = executeQuery(subQuery, sel);
			}
			 
			
			Template temp = new Template();
			temp.setId(sel.getId());
			if (sel.hasTemplate()) {
				temp.setTemplate(beanProvider.convertCiToBean(sel.fetchTemplate()));
			}
			temp.setTotalCount(subRes.totalCount);
			
			for (Object match : subRes.matches) {
				if (match instanceof IRFC) {
					temp.addRFC(convert(ccb, (IRFC)match));
				} else if (match instanceof ICi) {
					temp.addOffspring(beanProvider.convertCiToBean((ICi)match));
				} else if (match instanceof ICmdbTransaction) {
					temp.addTransaction(convert(ccb, (ICmdbTransaction)match));
				}
			}
			
			if (sel.reference()) {
				result.addEdges(temp);
			} else {
				result.addNodes(temp);
			}
		}
		
		return(result);
	}
	
	private TransactionBean convert(ICcb ccb2, ICmdbTransaction match) {
		TransactionBean bean = new TransactionBean();
		bean.setId(match.getId().asLong());
		bean.setBeginTs(match.getBeginTs());
		bean.setEndedTs(match.getEndTs());
		bean.setCiAdded(match.getCiAdded());
		bean.setCiDeleted(match.getCiDeleted());
		bean.setCiModified(match.getCiModified());
		bean.setIssuer(match.getIssuer());
		
		return(bean);
	}

	private QueryResult executeQuery(GraphQuery query, ItemSelector selector) {
		QueryResult result = new QueryResult();
		if (log.isDebugEnabled()) {
			log.debug(toXML(query, 0));
		}
		long t1 = System.currentTimeMillis();
		
		QueryExpression expr = getExpression(query, selector);
		if (expr.empty) {
			log.debug("Selector:" + selector.getTemplateAlias() + 
					" Empty !");
		
			result.matches = Collections.EMPTY_LIST;
			result.totalCount = 0;
			return(result);
		}
		
		
		DetachedCriteria crit = expr.criteria;
		QueryExpression exprCopy = getExpression(query, selector);
		DetachedCriteria critCopy = exprCopy.criteria;
		
		// Special handling here for RFC...
		if (selector instanceof ItemRFCSelector) {
			crit.addOrder( Order.desc("ts") );
		}
		if (selector instanceof ItemTransactionSelector) {
			crit.addOrder( Order.desc("endTs") );
		}
		boolean attributeOrder = false;
		if (selector instanceof ItemOffspringSelector) {
			if (selector.getOrderInfo() != null) {
					OrderInfo info = selector.getOrderInfo();
					if (info.getCiAttr() != null) {
						if (info.getCiAttr().equals("displayName")) {
							DetachedCriteria newCrit = getDisplayNameOrder(selector.getTemplateAlias(), crit, info);
							if (newCrit != null) {
								crit = newCrit;
								attributeOrder = true;
							} else {
								info.setCiAttr("displayNameExpression");
							}
						}
						if (!attributeOrder) {
							if (info.isDescenden()) {
								crit.addOrder(Order.desc(info.getCiAttr()));
							} else {
								crit.addOrder(Order.asc(info.getCiAttr()));
							}
						}
					} else {
						crit = getAttributeOrderCriteria(selector.getTemplateAlias(), crit, critCopy, info);
						attributeOrder = true;
					}
				} else {
					crit.addOrder(Order.desc("lastModified"));
				}
		} 
		List matches = null;
		matches = this.msvc.queryCrtiteria(crit, selector.getPageInfo());
		
		// Handle attribute order....
		if (attributeOrder) {
			final OrderInfo info = selector.getOrderInfo();
			List ciMatches = new ArrayList();
			if (info.getAttrType().equals("complex")) {
				if (false) {
					Collections.sort(matches, new Comparator<IAttribute>() {

						public int compare(IAttribute o1, IAttribute o2) {
							try {
								IValue v1  = o1.getValue();
								IValue v2 = o2.getValue();
								String n1 = v1.getDisplayName();
								String n2 = v2.getDisplayName();
								if (info.isDescenden()) {
									return(n2.compareTo(n1));
								} else {
									return(n1.compareTo(n2));
								}
							} catch (Throwable t) {
								return(0);
							}
						}
						
					});
					for (Object o : matches) {
						if (o instanceof IAttribute) {
							ciMatches.add(((IAttribute)o).getOwner());
						}
					}						
			
				} else {
					List<Long> targetIds = new ArrayList<Long>();
					List<Long> targetAttrOwnerIds = new ArrayList<Long>();
					for (Object o : matches) {
						if (o instanceof BasicAttribute) {
							targetAttrOwnerIds.add(((BasicAttribute)o).getOwnerId());
						}
					}
					if (targetAttrOwnerIds.size() > 0) {
						crit = getBackComplexAttributeOrder(targetAttrOwnerIds, critCopy, info);
						matches = this.msvc.queryCrtiteria(crit, selector.getPageInfo());
						Collections.sort(matches, new Comparator<IAttribute>() {

							public int compare(IAttribute o1, IAttribute o2) {
								try {
									IValue v1  = o1.getValue();
									IValue v2 = o2.getValue();
									String n1 = v1.getDisplayName();
									String n2 = v2.getDisplayName();
									if (info.isDescenden()) {
										return(n2.compareTo(n1));
									} else {
										return(n1.compareTo(n2));
									}
								} catch (Throwable t) {
									return(0);
								}
							}

						});
						for (Object o : matches) {
							if (o instanceof IAttribute) {
								ciMatches.add(((IAttribute)o).getOwner());
							}
						}						
					}
				}
			} else {
				// here we receives IAttribute in the correct order..
				// Need to fetch it's parent.
				for (Object o : matches) {
					if (o instanceof IAttribute) {
						ciMatches.add(((IAttribute)o).getOwner());
					}
				}						
			}
			matches = ciMatches;
		}
		
		long t2 = System.currentTimeMillis();
		
		int totalCount = -1;
	
		if (selector.getPageInfo() != null) {
			// Need to do this again else the criteria is already used...
			
			
			// Don't count null values.
			if (attributeOrder && selector.getOrderInfo().getAttrType().equals("complex")) {
				ItemAndGroupConstraint and = new ItemAndGroupConstraint();
				ItemNotConstraint not = new ItemNotConstraint();
				AttributeValueConstraint aV = new AttributeValueConstraint();
				aV.setOperation(AttributeValueConstraint.IS_NULL);
				aV.setAlias(selector.getOrderInfo().getAttrAlias());
				not.applyConstraint(aV);
				and.add(not);
				
				ItemSelector prim = query.fetchPrimarySelectors();
				if (prim.fetchConstraint() != null) {
					and.add(prim.fetchConstraint());
				}
				prim.applyConstraint(and);
				
			}
			QueryExpression expr2 = getExpression(query, selector);
			totalCount = this.msvc.queryCrtiteriaCount(expr2.criteria);
		}
	
		long t3 = System.currentTimeMillis();
		
		log.info("Selector:" + selector.getTemplateAlias() + 
				" Found " + matches.size() + "items, time:" + (t2-t1) + 
				"ms, totalCount=" + totalCount + ", time=" + (t3-t2) + "ms");
		
		
		result.matches = matches;
		result.totalCount = totalCount;
		
		return(result);
	}
	
	
	private DetachedCriteria getDisplayNameOrder(String template, DetachedCriteria ciCrit, OrderInfo info) {
		ICi templateCI = this.msvc.findCi(new Path(template));
		String expr = templateCI.getDisplayNameExpression();
		// Find first ${name}
		if (expr == null) {
			return(null);
		}
		int startIndexOf = expr.indexOf("${");
		if (startIndexOf < 0) {
			return(null);
		}
		int stopIndexOf = expr.indexOf("}");
		if (stopIndexOf < 0) {
			return(null);
		}
		String attrName = expr.substring(startIndexOf+2, stopIndexOf);
		OrderInfo nInfo = new OrderInfo();
		nInfo.setAttrAlias(attrName);
		nInfo.setAttrType("valueAsString");
		nInfo.setDescenden(info.isDescenden());
		return(getAttributeOrderCriteria(template, ciCrit, null, nInfo));
	}
	
	private DetachedCriteria getAttributeOrderCriteria(String template, DetachedCriteria ciCrit, DetachedCriteria ci2Crit,
			OrderInfo info) {
		
		if (info.getAttrType().equals("complex")) {
			return(getComplexAttributeOrderCriteria2(template, ciCrit, ci2Crit, info));
		}
		
		DetachedCriteria crit = DetachedCriteria.forClass(BasicAttribute.class);
		DetachedCriteria ciIdProjection = ciCrit.setProjection(Projections.property("longId"));
		crit.add(Property.forName("ownerId").in(ciIdProjection));
		crit.add(Expression.eq("alias", info.getAttrAlias()));
		if (info.isDescenden()) {
			crit.addOrder(Order.desc(info.getAttrType()));
		} else {
			crit.addOrder(Order.asc(info.getAttrType()));
		}
		return(crit);
	}
	
	private DetachedCriteria getBackReferences(DetachedCriteria sourceCrit, List<Long> targetIds, OrderInfo info) {
		// Find references that point to targetCrit
		DetachedCriteria refCiCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		refCiCrit.add(Property.forName("sourceId").in(sourceCrit.setProjection(Projections.property("longId"))));
		refCiCrit.add(Property.forName("targetId").in(targetIds));
		return(refCiCrit);
	}
		
	private DetachedCriteria getBackReferenceCI(List<Long> sourceIds, OrderInfo info) {
		DetachedCriteria ciCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		ciCrit.add(Property.forName("longId").in(sourceIds));
		
		return(ciCrit);
	}
	/*
	private DetachedCriteria getBackReferenceCI(List<Long> refIds, DetachedCriteria sourceCrit, OrderInfo info) {
		// Find references that point to targetCrit
		DetachedCriteria refCiCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		refCiCrit.add(Property.forName("targetId").in(targetIds));
				
		// Find all attribute that points to the reference
		DetachedCriteria sAttrCrit = DetachedCriteria.forClass(BasicAttribute.class);
		sAttrCrit.add(Expression.eq("alias", info.getAttrAlias()));
		sAttrCrit.add(Property.forName("valueAsLong").in(refCiCrit.setProjection(Projections.property("longId"))));
		sourceCrit.add(Property.forName("longId").in(sAttrCrit.setProjection(Projections.property("ownerId"))));
		
		return(sourceCrit);
	}
	*/
	private DetachedCriteria getComplexAttributeOrderCriteria(String template, DetachedCriteria ciCrit,
			OrderInfo info) {
		DetachedCriteria crit = DetachedCriteria.forClass(BasicAttribute.class);
		DetachedCriteria ciIdProjection = ciCrit.setProjection(Projections.property("longId"));
		crit.add(Property.forName("ownerId").in(ciIdProjection));
		crit.add(Expression.eq("alias", info.getAttrAlias()));
		//crit.add(Expression.isNotEmpty("valueAsLong"));
		
		ICi ci = this.msvc.findCi(new Path(template));
		IAttribute attr = ci.getAttributeDefinitionWithAlias(info.getAttrAlias());
		IType type = attr.getValueType();
		String targetTemplate = type.getAlias();
		
		DetachedCriteria refCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		refCrit.add(Property.forName("longId").in(crit.setProjection(Projections.property("valueAsLong"))));
		
		DetachedCriteria refCiCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		refCiCrit.add(Property.forName("longId").in(refCrit.setProjection(Projections.property("targetId"))));
		
		return(getDisplayNameOrder(targetTemplate, refCiCrit, info));
	}

	private DetachedCriteria getComplexAttributeOrderCriteria2(String template, DetachedCriteria ciCrit, DetachedCriteria ci2Crit,
			OrderInfo info) {
		
		DetachedCriteria orderedAttr = getComplexAttributeOrderCriteria(template, ciCrit, info);
		
		// Serach for this with page info...
		return(orderedAttr);
	}
	private DetachedCriteria getBackComplexAttributeOrder(List<Long> attrOwnerId, DetachedCriteria ci2Crit,
			OrderInfo info) {
		
		DetachedCriteria targetCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		targetCrit.add(Property.forName("longId").in(attrOwnerId));
		
		DetachedCriteria refCrit = DetachedCriteria.forClass(ConfigurationItem.class);
		refCrit.add(Property.forName("targetId").in(targetCrit.setProjection(Projections.property("longId"))));	
		
		
		DetachedCriteria attrCrit = DetachedCriteria.forClass(BasicAttribute.class);
		attrCrit.add(Property.forName("valueAsLong").in(refCrit.setProjection(Projections.property("longId"))));
		attrCrit.add(Property.forName("ownerId").in(ci2Crit.setProjection(Projections.property("longId"))));
		attrCrit.add(Expression.eq("alias", info.getAttrAlias()));
		
		attrCrit.addOrder(Order.desc("longId"));
		return(attrCrit);
	}


	
	private String toXML(GraphQuery query, int tab) {
		StringBuffer b = new StringBuffer();
		b.append(getTab(tab));
		b.append("<" + query.getClass().getSimpleName() + ">");
		b.append("\n");
		b.append(getTab(tab+1));
		b.append("<selectors>");
		b.append("\n");
		for (ItemSelector sel : query.fetchSelectors()) {
			b.append(toXML(sel, tab+2));
		}
		b.append(getTab(tab+1));
		b.append("</selectors>");
		b.append("\n");
		b.append(getTab(tab));
		b.append("</" + query.getClass().getSimpleName());
		b.append("\n");
		return(b.toString());
	}
	
	private String toXML(ItemSelector selector, int tab) {
		StringBuffer b = new StringBuffer();
		b.append(getTab(tab));
		b.append("<" + selector.getClass().getSimpleName());
		b.append(" id=\"" + selector.getId() + "\"");
		b.append(" primary=\"" + selector.isPrimary() + "\"");
		b.append(">");
		b.append("\n");
		b.append(getTab(tab+1));
		b.append("<templateAlias>" + selector.getTemplateAlias() + "</templateAlias>");
		b.append("\n");
		if (selector instanceof ItemTransactionSelector) {
			b.append(getTab(tab+1));
			b.append("<txid>" + ((ItemTransactionSelector)selector).getTxId() + "</txid>");
			b.append("\n");
		}
		
		if (selector instanceof ItemAliasSelector) {
			b.append(getTab(tab+1));
			b.append("<alias>" + ((ItemAliasSelector)selector).getAlias() + "</alias>");
			b.append("\n");
		}
		if (selector instanceof ItemRelationSelector) {
			ItemRelationSelector relSel = (ItemRelationSelector)selector;
			b.append(getTab(tab+1));
			b.append("<source>" + ((ItemRelationSelector)selector).getSource() + "</source>");
			b.append("\n");
			
			b.append(getTab(tab+1));
			b.append("<target>" + ((ItemRelationSelector)selector).getTarget() + "</target>");
			b.append("\n");
	
			
			if (relSel.getSourceRange() != null) {
				b.append(getTab(tab+1));
				b.append("<sourceRange>"); 
				b.append("\n");
				for (Long id : relSel.getSourceRange()) {
					b.append(getTab(tab+1));
					b.append("<id>" + id + "</id>"); 
					b.append("\n");
				}
				b.append(getTab(tab+1));
				b.append("</sourceRange>"); 
				b.append("\n");
			}
			if (relSel.getTargetRange() != null) {
				b.append(getTab(tab+1));
				b.append("<targetRange>"); 
				b.append("\n");
				for (Long id : relSel.getTargetRange()) {
					b.append(getTab(tab+1));
					b.append("<id>" + id + "</id>"); 
					b.append("\n");
				}
				b.append(getTab(tab+1));
				b.append("</targetRange>"); 
				b.append("\n");
			}
			
		}
		b.append(getTab(tab+1));
		b.append("<constraint>");
		b.append("\n");
		b.append(toXML(selector.fetchConstraint(), tab+2));
		b.append(getTab(tab+1));
		b.append("</constraint>");
		b.append("\n");
		if (selector.getPageInfo() != null) {
			b.append(getTab(tab+1));
			b.append("<pageInfo>");
			b.append("\n");
			Integer max = selector.getPageInfo().getMaxResult();
			if (max != null) {
				b.append(getTab(tab+2));
				b.append("<maxResult>" + max + "</maxResult>");
				b.append("\n");
			}
			Integer first = selector.getPageInfo().getFirstResult();
			if (first != null) {
				b.append(getTab(tab+2));
				b.append("<firstResult>" + first + "</firstResult>");
				b.append("\n");
			}
		
			b.append(getTab(tab+1));
			b.append("</pageInfo>");
			b.append("\n");

		}
		b.append(getTab(tab));
		b.append("</" + selector.getClass().getSimpleName() + ">");
		b.append("\n");
		return(b.toString());
	}

	private String toXML(ItemConstraint constraint, int tab) {
		StringBuffer b = new StringBuffer();
		if (constraint == null) {
			return(b.toString());
		}
		b.append(getTab(tab));
		b.append("<" + constraint.getClass().getSimpleName() + ">");
		b.append("\n");
		if (constraint instanceof ItemGroupConstraint) {
			b.append(getTab(tab+1));
			b.append("<constraints>");
			b.append("\n");
			for (ItemConstraint con : ((ItemGroupConstraint)constraint).fetchConstraints()) {
				b.append(toXML(con, tab+2));
			}
			b.append(getTab(tab+1));
			b.append("</constraints>");
			b.append("\n");				
		}
		
		if (constraint instanceof ItemSecurityConstraint) {
			ItemSecurityConstraint con = (ItemSecurityConstraint)constraint;
			if (con.getGid() != null) {
				b.append(getTab(tab+1));
				b.append("<gid>" + con.getGid() + "</gid>");
				b.append("\n");
			}
			if (con.getGroupName() != null) {
				b.append(getTab(tab+1));
				b.append("<groupName>" + con.getGroupName() + "</groupName>");
				b.append("\n");
			}
		}
		
		
		if (constraint instanceof AttributeValueConstraint) {
			AttributeValueConstraint con = (AttributeValueConstraint)constraint;
			b.append(getTab(tab+1));
			b.append("<operation>" + con.getOperation() + "</operation>");
			b.append("\n");
			
			b.append(getTab(tab+1));
			b.append("<alias>" + con.getAlias() + "</alias>");
			b.append("\n");

			b.append(getTab(tab+1));
			b.append("<value>" + con.getValue() + "</value>");
			b.append("\n");
		}
		
		if (constraint instanceof RelationConstraint) {
			RelationConstraint con = (RelationConstraint)constraint;
			b.append(getTab(tab+1));
			b.append("<direction>" + con.getDirection() + "</direction>");
			b.append("\n");
		
			b.append(getTab(tab+1));
			b.append("<selector>" + con.getSelector() + "</selector>");
			b.append("\n");
		}
		
		b.append(getTab(tab));
		b.append("</" + constraint.getClass().getSimpleName() + ">");
		b.append("\n");
		return(b.toString());
	}

	private String getTab(int index) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < index; i++) {
			b.append("\t");
		}
		return(b.toString());
	}
	
	private ItemSelector buildGraph(GraphQuery q, ItemSelector selector) {
		ItemSelector nSelector = selector.clone();
		ItemAndGroupConstraint group = new ItemAndGroupConstraint();
		if (nSelector.fetchConstraint() != null) {
			group.add(nSelector.fetchConstraint());
		}
		for (ItemSelector sel : q.fetchSelectors()) {
			if (sel.reference()) {
				ItemRelationSelector rel = (ItemRelationSelector)sel;
				if (rel.getSource().equals(selector.getId())) {
					group.add(new RelationConstraint(RelationConstraint.SOURCE, rel.getId()));
				}
				if (rel.getTarget().equals(selector.getId())) {
					group.add(new RelationConstraint(RelationConstraint.TARGET, rel.getId()));
				}
			} 
		}
		nSelector.applyConstraint(group);
		return(nSelector);
	}
	
	private ItemSelector buildGraph2(GraphQuery newQ, GraphQuery orgQ, ItemSelector selector) {
		ItemSelector nSelector = selector.clone();
		ItemAndGroupConstraint group = new ItemAndGroupConstraint();
		if (nSelector.fetchConstraint() != null) {
			group.add(nSelector.fetchConstraint());
		}
		
		newQ.addSelector(nSelector);
		
		for (ItemRelationSelector rel : orgQ.fetchRelationSelectors()) {
			if (!rel.isMandatory()) {
				continue;
			}
			if (rel.getSource().equals(selector.getId())) {
				
				if (selector.excludeRelation(rel.getId())) {
					continue;
				}
				
				newQ.addSelector(rel);
				ItemSelector targetSel = orgQ.findSelector(rel.getTarget());
				if (targetSel == null) {
					throw new IllegalArgumentException("RelationSelector[" + rel.getId() + "] target id[" + rel.getTarget() + "] not found.");
				}
				if (newQ.findSelector(targetSel.getId()) == null) {
					buildGraph2(newQ, orgQ, targetSel);
					group.add(new RelationConstraint(RelationConstraint.SOURCE, rel.getId()));
				}
			}
			if (rel.getTarget().equals(selector.getId())) {
				
				if (selector.excludeRelation(rel.getId())) {
					continue;
				}
				
				newQ.addSelector(rel);
				ItemSelector sourceSel = orgQ.findSelector(rel.getSource());
				if (sourceSel == null) {
					throw new IllegalArgumentException("RelationSelector[" + rel.getId() + "] source id[" + rel.getTarget() + "] not found.");
				}
		
				if (newQ.findSelector(sourceSel.getId()) == null) {
					buildGraph2(newQ, orgQ, sourceSel);
					group.add(new RelationConstraint(RelationConstraint.TARGET, rel.getId()));
				}
			}
		}
		if (!group.fetchConstraints().isEmpty()) {
			nSelector.applyConstraint(group);
		}
		return(nSelector);
	}

	
	protected QueryExpression getExpression(GraphQuery query, ItemSelector selector) {
		// OffspringItemSelector
		// RelationItemSelector
		// Convert the selector to an expression.
		QueryExpression queryExpr = new QueryExpression();
		
		// Find Template...
		ICi template = selector.fetchTemplate();
		if (selector.hasTemplate()) {
			if (template == null) {
				ItemExpression item = new ItemExpression();
				item.setAlias(selector.getTemplateAlias());
				List<ICi> cis = msvc.queryCrtiteria(item.getCriteria(), new PageInfo());
				if (cis.size() != 1) {
					throw new IllegalArgumentException("ItemSelector's template '" + selector.getTemplateAlias() + "' not found!");
				}
				template = cis.get(0);
				selector.bindTemplate(template);
			}
		}
				
		DetachedCriteria resultCrit = null;
		
		if (selector instanceof ItemOffspringSelector) {
			OffspringExpression expr = new OffspringExpression();
			
			// Lookup for alias.
			if (template != null) {
				expr.setTemplatePath(template.getTemplatePath());
				expr.setTemplateID(template.getId().asLong());
			}
			
			expr.setMatchTemplate(((ItemOffspringSelector)selector).getMatchTemplate());
			expr.setLimitToChild(((ItemOffspringSelector)selector).isLimitToChild());
			resultCrit = expr.getCriteria();
		} else if (selector instanceof ItemAliasSelector) {
			// Validate...
			if (((ItemAliasSelector)selector).getAlias() == null && ((ItemAliasSelector)selector).getAliases() == null) {
				queryExpr.empty = true;
				return(queryExpr);
			}
			if (((ItemAliasSelector)selector).getAlias() == null) {
				if (((ItemAliasSelector)selector).getAliases().size() == 0) {
					queryExpr.empty = true;
					return(queryExpr);
				}
			}
			
			ItemExpression aliasExpr = new ItemExpression();
			aliasExpr.setAlias(((ItemAliasSelector)selector).getAlias());
			aliasExpr.setAliases(((ItemAliasSelector)selector).getAliases());
			resultCrit = aliasExpr.getCriteria();
		} else if (selector instanceof ItemRFCSelector) { 
			RFCExpression rfcExpr = new RFCExpression();
			rfcExpr.setType(selector.getTemplateAlias());
			rfcExpr.setTxId(((ItemRFCSelector)selector).getTxId());
			resultCrit = rfcExpr.getCriteria();
		} else if (selector instanceof ItemTransactionSelector) {
			TransactionExpression transactionExpr = new TransactionExpression();
			transactionExpr.setTxid(((ItemTransactionSelector)selector).getTxId());
			resultCrit = transactionExpr.getCriteria();
		} else if (selector instanceof ItemRelationSelector) {
			RelationExpression expr = new RelationExpression();
			ItemRelationSelector rel = (ItemRelationSelector)selector;
			
			if (rel.getSourceRange() != null) {
				if (rel.getSourceRange().size() == 0) {
					log.info("RelationSelector[" + rel.getId() + "] SourceRange Empty!");
					queryExpr.empty = true;
					return(queryExpr);
				}
				expr.setSourceIds(rel.getSourceRange());
			} else {
				ItemSelector sourceSel = query.findSelector(rel.getSource());
				if (sourceSel == null) {
					throw new IllegalArgumentException("RelationSelector[" + rel.getId() + "] source id[" + rel.getSource() + "] not found.");
				}
	
				QueryExpression sExpr = getExpression(query, sourceSel);
				if (sExpr.empty) {
					queryExpr.empty = sExpr.empty;
					return(queryExpr);
				}
				expr.setSource(sExpr.criteria);
			}
			if (rel.getTargetRange() != null) {
				if (rel.getTargetRange().size() == 0) {
					log.info("RelationSelector[" + rel.getId() + "] TargetRange Empty!");
					queryExpr.empty = true;
					return(queryExpr);
				}
				expr.setTargetIds(rel.getTargetRange());
			} else {
				ItemSelector targetSel = query.findSelector(rel.getTarget());
				if (targetSel == null) {
					throw new IllegalArgumentException("RelationSelector[" + rel.getId() + "] target id[" + rel.getTarget() + "] not found.");
				}
				QueryExpression tExpr = getExpression(query, targetSel);
				if (tExpr.empty) {
					queryExpr.empty = tExpr.empty;
					return(queryExpr);
				}
				expr.setTarget(tExpr.criteria);
			}
			resultCrit = expr.getCriteria();
		} else {
			throw new IllegalArgumentException("ItemSelector type [" + selector.getClass().getName() + "] is not supported!");
		}
		
		ItemConstraint constraint = selector.fetchConstraint();
		
		if (constraint != null) {
			Criterion crit = getConstraint2(query, constraint);
			if (crit == null) {
				queryExpr.empty = true;
			} else {
				resultCrit.add(crit);
			}
		}
		queryExpr.criteria = resultCrit;
		
		return(queryExpr);

	}

	private Criterion getConstraint2(GraphQuery query, ItemConstraint cons) {
		if (cons instanceof ItemGroupConstraint) {
			
			ItemGroupConstraint group = (ItemGroupConstraint)cons;
			Junction j = null;
			if (group.conjunction()) {
				j = Restrictions.conjunction();
			} else {
				j = Restrictions.disjunction();
			}
			
			for (Iterator iter = group.fetchConstraints().iterator(); iter.hasNext();) {
				ItemConstraint con = (ItemConstraint)iter.next();
				Criterion criterion = getConstraint2(query, con);
				if (group.conjunction()) {
					if (criterion == null) {
						return(null);
					}
				}
				if (criterion != null) {
					j.add(criterion);
				}
			}
			return(j);
		}
		if (cons instanceof ItemNotConstraint) {
			ItemConstraint notCons = ((ItemNotConstraint)cons).fetchConstraint();
			if (notCons == null) {
				throw new IllegalArgumentException("ItemNotGroupConstraint must containt a constraint");
			}
			Criterion notCrit = getConstraint2(query, notCons);
			return(Expression.not(notCrit));
		}
		if (cons instanceof RelationConstraint) {
			RelationConstraint rel = (RelationConstraint)cons;
			DetachedCriteria crit = null;
			String direction = null;
			ItemSelector selector = query.findSelector(rel.getSelector());
			if (!(selector instanceof ItemRelationSelector)) {
				throw new IllegalArgumentException("RelationExpression selector " + rel.getSelector() + " is not a ItemRelationSelection!");
			}
			
			RelationExpression relExpr = new RelationExpression();
			
			if (selector instanceof RFCItemRelationSelector) {
				relExpr = new RFCRelationExpression();
			} else if (selector instanceof TransactionRelationSelector) {
				relExpr = new TransactionRelationExpression();
			}
			
			ItemRelationSelector relSelector = (ItemRelationSelector)selector;
			Criterion relation = null;
			if (rel.isTarget()) {
				if (relSelector.getSourceRange() != null) {
					if (relSelector.getSourceRange().size() == 0) {
						log.info("RelationConstraint RelationSelector[" + relSelector.getId() + "] SourceRange Empty!");

						return(null);
					}
					relExpr.setSourceIds(relSelector.getSourceRange());
				} else {
					String srcId = ((ItemRelationSelector)selector).getSource();
					ItemSelector sel = (ItemSelector) query.findSelector(srcId);
					QueryExpression sExpr = getExpression(query, sel);
					if (sExpr.empty) {
						return(null);
					}
					DetachedCriteria source = sExpr.criteria; 
					relExpr.setSource(source);
				}
				relation = relExpr.getSourceCriterion();
				/*
				crit = relExpr.getSourceCriteria();
				relation = Property.forName("longId").in(crit.setProjection(Projections.property("targetId")));
				*/
			} else {
				
				if (relSelector.getTargetRange() != null) {
					if (relSelector.getTargetRange().size() == 0) {
						log.info("RelationConstraint RelationSelector[" + relSelector.getId() + "] TargetRange Empty!");
						return(null);
					}
					relExpr.setTargetIds(relSelector.getTargetRange());
					//relation = Property.forName("longId").in(relSelector.getTargetRange());
				} else {
					String trgId = ((ItemRelationSelector)selector).getTarget();
					ItemSelector sel = query.findSelector(trgId);
					QueryExpression tExpr = getExpression(query, sel);
					if (tExpr.empty) {
						return(null);
					}
					DetachedCriteria target = tExpr.criteria;
					relExpr.setTarget(target);
				}
				relation = relExpr.getTargetCriterion();
				/*
				crit = relExpr.getTargetCriteria();
				relation = Property.forName("longId").in(crit.setProjection(Projections.property("sourceId")));
				*/
			}
			log.debug(relation.toString());
			return(relation);
		}
		
		if (cons instanceof AttributeValueConstraint) {
			AttributeValueConstraint aValue = (AttributeValueConstraint)cons;
			AttributeValueExpression aExpr = new AttributeValueExpression();
			aExpr.setAlias(aValue.getAlias());
			aExpr.setOperation(aValue.getOperation());
			aExpr.setType(aValue.getValueType());
			aExpr.setStringValue(aValue.getValue());
			
			if (aExpr.isInternal()) {
				return(aExpr.getInternalCriterion());
			}
	
			DetachedCriteria attr = aExpr.getCriteria();
			return(Property.forName("longId").in(attr.setProjection(Projections.property("ownerId"))));
		}
		
		if (cons instanceof ItemSecurityConstraint) {
			ItemSecurityConstraint sCon = (ItemSecurityConstraint)cons;
			
			if (sCon.getGid() != null) {
				return(Property.forName("gid").eq(sCon.getGid()));
			}
			ItemExpression expr = new ItemExpression();
			expr.setAlias(sCon.getGroupName());
			
			DetachedCriteria gid = expr.getCriteria();
			return(Property.forName("gid").in(gid.setProjection(Projections.property("longId"))));		
		}
		
		if (cons instanceof ItemIdConstraint) {
			ItemIdConstraint idContrain = (ItemIdConstraint)cons;
			if (idContrain.getId() != null) {
				return(Restrictions.idEq(idContrain.getId()));		
			}
			if (idContrain.getAlias() != null) {
				return(Property.forName("alias").eq(idContrain.getAlias()));
			}
		}
		
		if (cons instanceof RFCTargetConstraint) {
			return(Property.forName("targetId").eq(((RFCTargetConstraint)cons).getLongId()));		
		}
		
		if (cons instanceof AttributeSourceRelationConstraint) {
			AttributeSourceRelationConstraint relACons = (AttributeSourceRelationConstraint)cons;
			AttributeValueExpression expr = new AttributeValueExpression();
			expr.setAlias(relACons.getAlias());
			DetachedCriteria crit = expr.getCriteria();
			return(Property.forName("sourceId").in(crit.setProjection(Projections.property("valueAsLong"))));
		}
		log.error("Constraint{" + cons.getClass().getSimpleName() + "] not implemented!");
		return(null);
	}
	
	private RFCBean convert(ICcb ccbSvc, IRFC rfc) {
		IModelService mSvc = (IModelService) this.session.getService(IModelService.class);
		// Return this list.
		RFCBean rfcBean = new RFCBean();
		rfcBean.setId(rfc.getId());
		rfcBean.setSummary(RFCSummaryDecorator.decorateSummary(mSvc, rfc));
		rfcBean.setTransactionId(rfc.getTxId());
		rfcBean.setTs(rfc.getTs());
		rfcBean.setTargetId(rfc.getTargetId());
		rfcBean.setTargetCIId(rfc.getTargetCIId());
		
		if (ccbSvc != null) {
			// Retrieve the Transaction to get the issuer.
			ICmdbTransaction tx = ccbSvc.findTxForRfc(rfc);
			rfcBean.setIssuer(tx.getIssuer());
		}
		return(rfcBean);
	}

	
/*
	private Criterion convertConstraint(GraphQuery query, DetachedCriteria parent, ItemConstraint cons) {
		if (cons instanceof ItemGroupConstraint) {
			ItemGroupConstraint group = (ItemGroupConstraint)cons;
			Junction j = null;
			if (group.isConjunction()) {
				j = Restrictions.conjunction();
			} else {
				j = Restrictions.disjunction();
			}
			for (Iterator iter = group.getConstraints().iterator(); iter.hasNext();) {
				ItemConstraint con = (ItemConstraint)iter.next();
				j.add(convertConstraint(query, parent, con));
			}
			return(j);
		}
		/*
		if (cons instanceof RelationConstraint) {
			RelationConstraint rel = (RelationConstraint)cons;
			DetachedCriteria crit = null;
			String direction = null;
			RelationExpression relExpr = new RelationExpression();
			ItemSelector selector = query.findSelector(rel.getSelector());
			if (!(selector instanceof ItemRelationSelector)) {
				throw new IllegalArgumentException("RelationExpression need a ItemRelationSelection!");
			}
			
			if (rel.isTarget()) {
				direction = "targetId";
				String srcId = ((ItemRelationSelector)selector).getSource();
				ItemSelector sel = query.findSelector(srcId);
				DetachedCriteria source = getExpression(query, sel);
				relExpr.setSource(source);
				crit = relExpr.getSourceCriteria();
			} else {
				direction = "sourceId";
				String trgId = ((ItemRelationSelector)selector).getTarget();
				ItemSelector sel = query.findSelector(trgId);
				DetachedCriteria target = getExpression(query, sel);
				relExpr.setTarget(target);
				crit = relExpr.getTargetCriteria();
			}
			
			parent.add(Property.forName("longId").in(crit.setProjection(Projections.property(direction))));
		}
		if (cons instanceof AttributeValueConstraint) {
			AttributeValueConstraint aValue = (AttributeValueConstraint)cons;
			AttributeValueExpression aExpr = new AttributeValueExpression();
			aExpr.setAlias(aValue.getAlias());
			aExpr.setOperation(aValue.getOperation());
			//aExpr.setType(aValue.get);
			aExpr.setStringValue(aValue.getValue());
			
			DetachedCriteria attr = aExpr.getCriteria();
			
			//DetachedCriteria ownerCI = DetachedCriteria.forClass(ConfigurationItem.class);
			
			parent.add(Property.forName("longId").in(attr.setProjection(Projections.property("ownerId"))));
		}
		return(null);
	}
		*/

	
	
}
