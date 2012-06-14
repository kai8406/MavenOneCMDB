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
package org.onecmdb.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.storage.expression.OrderExpression;
import org.onecmdb.core.internal.storage.expression.SourceRelationExpression;
import org.onecmdb.core.internal.storage.expression.SourceTemplateRelationExpression;
import org.onecmdb.core.internal.storage.expression.TemplateRelationExpression;

/**
 * Simplify create/modification/retriving of ci's and attributes.
 *
 */
public class OnecmdbUtils {

	private ISession session;
	private static Log log = LogFactory.getLog(OnecmdbUtils.class);
	
	public OnecmdbUtils(ISession session) {
		this.session = session;
	}

	public ICi newInstance(ICi ci) {
		return(newInstance(ci, null));
	}	
	
	public ICi newInstance(ICi ci, String alias) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		Set<ICi> beforeSet = ci.getOffsprings();
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ci);
			ICiModifiable ipTemplate = rootTemplate.createOffspring();
			ipTemplate.setIsBlueprint(false);
			if (alias != null) {
				ipTemplate.setAlias(alias);
			}
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		if (result.isRejected()) {
			log.error("Reject create instance of ci '" + ci.getAlias() +"' cause " + result.getRejectCause());
			return(null);
		}

		// How can we retrive the new item!!!
		// The target id is stored in ipTemplate.
		Set<ICi> afterSet = ci.getOffsprings();
		ICi newCi = null;
		for (ICi offspringCi : afterSet) {
			if (!beforeSet.contains(offspringCi)) {
				newCi = offspringCi;
				break;
			}
		}
		
		return (newCi);
	}

	public void setValue(ICi source, String aName, IValue value) {
		IAttribute theAttribute = null;
		for (IAttribute a : source.getAttributes()) {
			if (a.getDisplayName().equals(aName)) {
				theAttribute = a;
				break;
			}
		}
		setValue(theAttribute, value);
	}

	public IAttribute setValue(IAttribute theAttribute, IValue value) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		{
			IAttributeModifiable aTemplate = tx
					.getAttributeTemplate(theAttribute);
			aTemplate.setValue(value);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		IModelService model = (IModelService) session.getService(IModelService.class);
		IAttribute reload = (IAttribute) model.find(theAttribute.getId());
		return(reload);
	}
	
	/**
	 * Expression looks like this.
	 * | delimiter between ci's
	 * <> direction command
	 * $attr{attAlias} reference to the attribute's value in current ci.
	 * $template{alias}.$attr{attAlias} reference to an ci instance with attAlias poniting to current ci. 
	 * 
	 * @param origin
	 * @param path
	 * @return
	 */
	public Set<IValue> evaluate(IValue current, String path) {
		Set<IValue> values = new HashSet<IValue>();
		
		if (path == null || path.trim().equals("")) {
			values.add(current);
			return(values);
		}
		String expressions[] = path.split("\\|",2);
		String expression = expressions[0];
		
		Set<IValue> next = new HashSet<IValue>(); 
		
		if (expression.startsWith(">")) {
			// Must be $attr
			if (!expression.startsWith(">$attr{")) {
				throw new IllegalArgumentException("Expressson '" + expression +"' not valid. < must be followed by $attr{attAlias}");
			}
			
			String attrAlias = expression.substring(">$attr{".length(), expression.length()-1);
			log.debug(">" + attrAlias);
			
			if (!(current instanceof ICi)) {
				throw new IllegalArgumentException("Error in expression, trying to operate '" + expression + "' on a simple value '" + current + "'");
			}
			List<IAttribute> attrs = ((ICi)current).getAttributesWithAlias(attrAlias);
			
			// Will now add all values.
			for (IAttribute a : attrs) {
				IValue value  = a.getValue();
				if (value != null) {
					next.add(value);
				}
			}
		
		} else if (expression.startsWith("<")) {
			// Must be $attr
			if (!expression.startsWith("<$template{")) {
				throw new IllegalArgumentException("Expressson '" + expression +"' not valid. < must be followed by $template{alias}.$attr[attrAlis}");
			}
			String templates[] = expression.split("\\{", 3);
			int offset = templates[1].indexOf('}');
			String template =  templates[1].substring(0, offset);
			
			String attrAlias = null;
			
			if (templates.length > 2) {
				offset = templates[2].indexOf('}');
				attrAlias = templates[2].substring(0, offset);
			}
			
			log.debug("<" + template + "." + attrAlias);
			
			if (!(current instanceof ICi)) {
				throw new IllegalArgumentException("Error in expression, trying to operate '" + expression + "' on a simple value '" + current + "'");
			}
			IModelService modelSvc = (IModelService)this.session.getService(IModelService.class);
			ICi templateCi = modelSvc.findCi(new Path<String>(template));
			if (templateCi == null) {
				throw new IllegalArgumentException("Template '" + template + "' is not found");
			}
			
			List<ICi> candidates = getReferrersOfType((ICi) current, templateCi);
			// Add all candidates.
			// TODO: check attribute alias
			for (ICi candidate : candidates) {
				next.add(candidate);
			}
		}
		if (expressions.length < 2) {
			return(next);
		}
		String rest = expressions[1];
		Set<IValue> result = new HashSet<IValue>();
		for (IValue value : next) {
			result.addAll(evaluate(value, rest));
		}
		return(result);
	}
	
	public QueryResult evaluate(IValue current, String path, QueryCriteria crit, boolean count) {
		QueryResult result = new QueryResult();
		if (path == null || path.trim().equals("")) {
			return(result);
		}
		
		String expressions[] = path.split("\\|",2);
		String expression = expressions[0];
		
		QueryResult next = new QueryResult(); 
		
		if (expression.startsWith(">$attr")) {
			// Must be $attr
			
			String attrAlias = expression.substring(">$attr{".length(), expression.length()-1);
			log.debug(">" + attrAlias);
			
			if (!(current instanceof ICi)) {
				throw new IllegalArgumentException("Error in expression, trying to operate '" + expression + "' on a simple value '" + current + "'");
			}
			List<IAttribute> attrs = ((ICi)current).getAttributesWithAlias(attrAlias);
			
			// Will now add all values.
			for (IAttribute a : attrs) {
				IValue value  = a.getValue();
				if (value != null) {
					next.add(value);
				}
			}
		
		} else if (expression.startsWith("<$template{")) {
			// Must be $attr
			String templates[] = expression.split("\\{", 3);
			int offset = templates[1].indexOf('}');
			String template =  templates[1].substring(0, offset);
			
			String attrAlias = null;
			
			if (templates.length > 2) {
				offset = templates[2].indexOf('}');
				attrAlias = templates[2].substring(0, offset);
			}
			
			log.debug("<" + template + "." + attrAlias);
			
			if (!(current instanceof ICi)) {
				throw new IllegalArgumentException("Error in expression, trying to operate '" + expression + "' on a simple value '" + current + "'");
			}
			
			IModelService modelSvc = (IModelService)this.session.getService(IModelService.class);
				
			SourceRelationExpression expr = new SourceRelationExpression();
			
			if (!template.equals("*")) {
				ICi templateCi = modelSvc.findCi(new Path<String>(template));
				if (templateCi == null) {
					throw new IllegalArgumentException("Template '" + template + "' is not found");
				}
				
				expr.setSourceTemplateId(templateCi.getId().asLong());
				if (crit != null) {
					if (crit.getOffspringDepth() != null) {
						expr.setSourceTemplatePathString(templateCi.getTemplatePath());
						expr.setSourceTemplateId(null);
					}
				}
			}
				
			expr.setTargetId(((ICi)current).getId().asLong());
			expr.setCount(count);
			if (expressions.length < 2 && crit != null) {
				expr.setMaxResult(crit.getMaxResult());
				expr.setFirstResult(crit.getFirstResult());
				expr.setTextMatch(crit.getText());
				expr.setTextMatchAlias(crit.isTextMatchAlias());
				expr.setTextMatchDescription(crit.isTextMatchDescription());
				expr.setTextMatchValue(crit.isTextMatchValue());
				
				if (crit.getOrderAttAlias() != null) {
					OrderExpression order = new OrderExpression();
					order.setAttrType(crit.getOrderType());
					order.setAttrAlias(crit.getOrderAttAlias());
					order.setAscending(crit.isOrderAscending());
					expr.setOrder(order);
				}
			}
			next = modelSvc.evalExpression(expr);
		} else if (expression.startsWith("<$referenceTemplate")) { 
			// Find reference CI for a ci.
			if (!(current instanceof ICi)) {
				throw new IllegalArgumentException("Error in expression, trying to operate '" + expression + "' on a simple value '" + current + "'");
			}
			IModelService modelSvc = (IModelService)this.session.getService(IModelService.class);
			
			TemplateRelationExpression expr = new TemplateRelationExpression();
			expr.setTargetId(((ICi)current).getId().asLong());
			
			expr.setCount(count);
			if (expressions.length < 2 && crit != null) {
				expr.setMaxResult(crit.getMaxResult());
				expr.setFirstResult(crit.getFirstResult());
				expr.setTextMatch(crit.getText());
				expr.setTextMatchAlias(crit.isTextMatchAlias());
				expr.setTextMatchDescription(crit.isTextMatchDescription());
				expr.setTextMatchValue(crit.isTextMatchValue());
				
				if (crit.getOrderAttAlias() != null) {
					OrderExpression order = new OrderExpression();
					order.setAttrType(crit.getOrderType());
					order.setAttrAlias(crit.getOrderAttAlias());
					order.setAscending(crit.isOrderAscending());
					expr.setOrder(order);
				}
			}
			next = modelSvc.evalExpression(expr);
			
		} else if (expression.startsWith("<$referenceSource{")) { 
			// Find reference CI for a ci.
			if (!(current instanceof ICi)) {
				throw new IllegalArgumentException("Error in expression, trying to operate '" + expression + "' on a simple value '" + current + "'");
			}
			String templates[] = expression.split("\\{", 3);
			int offset = templates[1].indexOf('}');
			String template =  templates[1].substring(0, offset);

			IModelService modelSvc = (IModelService)this.session.getService(IModelService.class);
			
			ICi referenceTemplateCi = modelSvc.findCi(new Path<String>(template));
			if (referenceTemplateCi == null) {
				throw new IllegalArgumentException("Reference Template '" + template + "' is not found");
			}
			
			SourceTemplateRelationExpression expr = new SourceTemplateRelationExpression();
			expr.setTargetId(((ICi)current).getId().asLong());
			expr.setReferenceTemplateId(referenceTemplateCi.getId().asLong());
			
			expr.setCount(count);
			if (expressions.length < 2 && crit != null) {
				expr.setMaxResult(crit.getMaxResult());
				expr.setFirstResult(crit.getFirstResult());
				expr.setTextMatch(crit.getText());
				expr.setTextMatchAlias(crit.isTextMatchAlias());
				expr.setTextMatchDescription(crit.isTextMatchDescription());
				expr.setTextMatchValue(crit.isTextMatchValue());
				
				if (crit.getOrderAttAlias() != null) {
					OrderExpression order = new OrderExpression();
					order.setAttrType(crit.getOrderType());
					order.setAttrAlias(crit.getOrderAttAlias());
					order.setAscending(crit.isOrderAscending());
					expr.setOrder(order);
				}
			}
			next = modelSvc.evalExpression(expr);
		} else {
			if (expression.startsWith(">")) {
				throw new IllegalArgumentException("Expressson '" + expression +"' not valid. < must be followed by $attr{attAlias}");
			}
			if (expression.startsWith("<")) {
				throw new IllegalArgumentException("Expressson '" + expression +"' not valid. < must be followed by $template{alias}.$attr[attrAlis}");
			}
			
			throw new IllegalArgumentException("Illegal Expressson '" + expression +"'");
		}
		
		if (expressions.length < 2) {
			return(next);
		}
		String rest = expressions[1];
		for (Iterator iter = next.iterator(); iter.hasNext();) {
			Object value = iter.next();
			if (value instanceof IValue) {
				result.addAll(evaluate((IValue)value, rest, crit, count));
			}
		}
		return(result);
	}
	
	/**
	 * Return the firts occurence of an attribute with alias.
	 * 
	 * @param ci
	 * @param attrAlias
	 * @return null if not found or empty.
	 */
	public IAttribute getFirstAttribute(ICi ci, String attrAlias) {
		List<IAttribute> list = ci.getAttributesWithAlias(attrAlias);
		if (list == null) {
			return(null);
		}
		if (list.size() == 0) {
			return(null);
		}
		
		return(list.get(0));
	}
	
	/**
	 * Could/Should be moved to IReferenceService!
	 */
	public List<ICi> getReferrersOfType(ICi ci, ICi template) {
		IReferenceService refSvc = (IReferenceService)this.session.getService(IReferenceService.class);
		IModelService modelSvc = (IModelService)this.session.getService(IModelService.class);
		List<ICi> list = new ArrayList<ICi>();
		Set<ICi> refs = refSvc.getOriginCiReferrers(ci);
		for (ICi ref : refs) {
			if (modelSvc.isOffspringOf(template, ref)) {
				list.add(ref);
			}
		}
		return(list);
	}

	public void deleteCi(ICi ci) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable modifiable = tx.getTemplate(ci);
			modifiable.delete();
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		
	}
	
	
}
