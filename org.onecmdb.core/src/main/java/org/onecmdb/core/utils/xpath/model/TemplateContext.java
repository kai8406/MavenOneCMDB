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
package org.onecmdb.core.utils.xpath.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;

/**
 * Dynamic Object wrapper for a template.
 * <br>
 * <br>Path /template/<i>template-alias</i>
 *
 */
public class TemplateContext extends AbstractCacheContext implements ICmdbObjectDestruction {

	private ICi ci;

	public TemplateContext(Map<String, Object> context, ICi ci) {
		super(context);
		this.ci = ci;
	}
	
	public ICi getICi() {
		return(this.ci);
	}
	
	@Override
	public String[] getNewProperties() {
		// What can we do on the ICi, might just return that,
		// But we migth need more controll on how the objects
		// are passed up, manly for doing the setProperty().
		String properties[] = {
		"id",
		"alias",
		"displayName",
		"description",
		"icon",
		"derivedFrom",		
		"displayNameExpression",		
		"offspring",
		"attribute",
		"offsprings",
		"alloffsprings",
		"instances",
		"allinstances"
		};
		
		return(properties);
	}

	@Override
	public Object getNewProperty(String propertyName) {
		if (propertyName.equals("id")) {
			return(this.ci.getId());
		}
		if (propertyName.equals("alias")) {
			return(this.ci.getAlias());
		}
	
		if (propertyName.equals("displayName")) {
			return(this.ci.getDisplayName());
		}
		
		if (propertyName.equals("icon")) {
			return(this.ci.getIcon());
		}
	
		if (propertyName.equals("displayNameExpression")) {
			return(this.ci.getDisplayNameExpression());
		}
		
		if (propertyName.equals("description")) {
			return(this.ci.getDescription());
		}
		
		if (propertyName.equals("attributes")) {
			Set<IAttribute> attributes = this.ci.getAttributeDefinitions();
			List<AttributeContext> aContext = new ArrayList<AttributeContext>();
			for (IAttribute attribute : attributes) {
				aContext.add(new AttributeContext(this.context, attribute, this.ci));
			}
			return(aContext);
		}
		if (propertyName.equals("attribute")) {
			return(new AttributeCollectionContext(this.context, this.ci));
		}
		
		if (propertyName.equals("derivedFrom")) {
			ICi parent = this.ci.getDerivedFrom();
			if (parent == null) {
				return(null);
			}
			return(new TemplateContext(this.context, parent));
		}
		
		
		if (propertyName.equals("offsprings")) {
			Set<ICi> templateOffsprings = this.ci.getOffsprings();
			List<TemplateContext> templateOffspringContext = new ArrayList<TemplateContext>();
			for (ICi  templateOffspring : templateOffsprings) {
				if (templateOffspring.isBlueprint()) {
					templateOffspringContext.add(new TemplateContext(this.context, (ICi)templateOffspring));
				}
			}
			return(templateOffspringContext);
		}
		if (propertyName.equals("offspring")) {
			return(new TemplateCollectionContext(this.context, ci));
		}
		if (propertyName.equals("alloffsprings")) {
			// Query all templates of this template.
			QueryCriteria crit = new QueryCriteria();
			crit.setOffspringOfAlias(this.ci.getAlias());
			crit.setMatchCiTemplates(true);
			crit.setOffspringDepth(new Integer(-1));
			ISession session = (ISession) this.context.get("session");
			IModelService mSvc = (IModelService) session.getService(IModelService.class);
			QueryResult<ICi> result = mSvc.query(crit);
			List<TemplateContext> templateOffspringContext = new ArrayList<TemplateContext>();
			for (ICi templateOffspring: result) {
				if (templateOffspring.isBlueprint()) {
					templateOffspringContext.add(new TemplateContext(this.context, (ICi)templateOffspring));
				}
			}
			return(templateOffspringContext);
		}
		if (propertyName.equals("instances") || propertyName.equals("allinstances")) {
			// Query all templates of this template.
			QueryCriteria crit = new QueryCriteria();
			crit.setOffspringOfAlias(this.ci.getAlias());
			crit.setMatchCiTemplates(false);
			crit.setOffspringDepth(propertyName.equals("instances") ? new Integer(1) : new Integer(-1));
			ISession session = (ISession) this.context.get("session");
			IModelService mSvc = (IModelService) session.getService(IModelService.class);
			QueryResult<ICi> result = mSvc.query(crit);
			List<InstanceContext> instanceOffspringContext = new ArrayList<InstanceContext>();
			for (ICi templateOffspring: result) {
				
				if (!templateOffspring.isBlueprint()) {
					instanceOffspringContext.add(new InstanceContext(this.context, (ICi)templateOffspring));
				}
			}
			return(instanceOffspringContext);
		}
		
		
		
		return(null);
	}

	public void setProperty(String propertyName, Object value) {		
		log.debug("SetValue(" + propertyName +"," + value +") TODO:");
		ICmdbTransaction tx = (ICmdbTransaction) this.context.get("tx");
		if (tx == null) {
			throw new IllegalAccessError("No transaction setup!");
		}
		ICiModifiable mod = tx.getTemplate(this.ci);
		TemplateModifiableContext modContext = new TemplateModifiableContext(this.context, mod);
		modContext.setProperty(propertyName, value);
	}

	public String toString() {
		return(this.ci.getAlias());
	}

	public void destory() {
		log.debug("Destory(" + this.ci.getAlias() + ")");
		ICmdbTransaction tx = (ICmdbTransaction) this.context.get("tx");
		if (tx == null) {
			throw new IllegalAccessError("No transaction setup!");
		}
		ICiModifiable mod = tx.getTemplate(this.ci);
		mod.delete();
	}
}
