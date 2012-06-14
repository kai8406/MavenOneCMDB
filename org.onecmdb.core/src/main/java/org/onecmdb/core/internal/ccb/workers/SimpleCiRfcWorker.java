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
package org.onecmdb.core.internal.ccb.workers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//import javax.resource.cci.LocalTransaction;

import oracle.toplink.queryframework.ModifyQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IPolicyService;
import org.onecmdb.core.IPolicyTrigger;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IReference;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.CiModifiable;
import org.onecmdb.core.internal.ccb.IRfcWorker;
import org.onecmdb.core.internal.ccb.rfc.RFC;
import org.onecmdb.core.internal.ccb.rfc.RFCAddAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDisplayNameExpression;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyIsTemplate;
import org.onecmdb.core.internal.ccb.rfc.RFCMoveCi;
import org.onecmdb.core.internal.ccb.rfc.RFCNewCi;
import org.onecmdb.core.internal.ccb.rfc.RFCNewReference;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.ObjectConverter;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.RelationItem;
import org.onecmdb.core.internal.storage.IDaoReader;
import org.onecmdb.core.tests.profiler.Profiler;


public class SimpleCiRfcWorker implements IRfcWorker {

	private String RFC_NOT_SUPPORTED = "org.onecmdb.ccb.error.rfcnotsupported";

	private IPolicyService policyService;

	private IDaoReader reader;

	private Log log = LogFactory.getLog(this.getClass()); 
	
	// Do not inherite attribute, only when a value is set. 
	private boolean inheriteAttribute = false;
	
	public void setPolicyService(IPolicyService service) {
		this.policyService = service;
	}

	public void setDaoReader(IDaoReader reader) {
		this.reader = reader;
	}
	
	/**
	 * Will handle all RFC for now.
	 */
	public boolean handleRfc(IRFC rfc) {
		if (rfc.getClass().equals(RFCNewCi.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCModifyAlias.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCModifyIsTemplate.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCModifyDisplayNameExpression.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCModifyDescription.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCModifyDerivedAttributeValue.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCNewReference.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCMoveCi.class)) {
			return (true);
		}
		if (rfc.getClass().equals(CiModifiable.class)) {
			return (true);
		}
		if (rfc.getClass().equals(RFCDestroy.class)) {
			return (true);
		}
		return (false);
	}

	public IRfcResult perform(IRFC rfc, IObjectScope scope) {
		IRfcResult result = null;
		try {
			Profiler.start("perform(" + rfc.getClass().getSimpleName() + ")");
			result = internalPerform(rfc, scope);
			Profiler.stop();
			return(result);
		} finally {	
		}
	}
	
	private  IRfcResult internalPerform(IRFC rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
			if (rfc.getClass().equals(RFCNewCi.class)) {
				return (performNewCiRfc((RFCNewCi) rfc, scope));
			}
			if (rfc.getClass().equals(CiModifiable.class)) {
				return (performCiTemplate((CiModifiable) rfc, scope));
			}
			if (rfc.getClass().equals(RFCModifyAlias.class)) {
				return (performInternal((RFCModifyAlias) rfc, scope));
			}
			if (rfc.getClass().equals(RFCModifyIsTemplate.class)) {
				return (performInternal((RFCModifyIsTemplate) rfc, scope));
			}
			
			if (rfc.getClass().equals(RFCModifyDisplayNameExpression.class)) {
				return (performInternal((RFCModifyDisplayNameExpression) rfc, scope));
			}
			if (rfc.getClass().equals(RFCModifyDescription.class)) {
				return (performInternal((RFCModifyDescription) rfc, scope));
			}
			if (rfc.getClass().equals(RFCModifyDerivedAttributeValue.class)) {
				return (performInternal((RFCModifyDerivedAttributeValue) rfc, scope));
			}
			if (rfc.getClass().equals(RFCNewReference.class)) {
				return (performInternal((RFCNewReference) rfc, scope));
			}
			if (rfc.getClass().equals(RFCDestroy.class)) {
				return (performInternal((RFCDestroy) rfc, scope));
			}
			if (rfc.getClass().equals(RFCMoveCi.class)) {
				return (performInternal((RFCMoveCi) rfc, scope));
			}
		result.setRejectCause(RFC_NOT_SUPPORTED);
		return (result);
	}
	
	
	
	private IRfcResult performInternal(RFCModifyDescription rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();
		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find Target item to set description #"
					+ rfc);
			return (result);
		}
		if (!(ci instanceof ConfigurationItem)) {
			result.setRejectCause("Target CI must be of class "
					+ ConfigurationItem.class);
			return (result);
		}
		ConfigurationItem item = (ConfigurationItem) ci;
		item.setDescription(rfc.getDescription());

		rfc.setTarget(ci);
		rfc.setTargetAlias(ci.getAlias());
		scope.addModifiedICi(ci);
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);
	}

	private IRfcResult performInternal(RFCModifyIsTemplate rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();
		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find target CI to modify template #"
					+ rfc);
			return (result);
		}
		if (!(ci instanceof ConfigurationItem)) {
			result.setRejectCause("Target CI must be of class "
					+ ConfigurationItem.class);
			return (result);
		}
		ConfigurationItem item = (ConfigurationItem) ci;
		
		// Change from template to instance. No offsprings are allowed.
		if (item.isBlueprint() && !rfc.isNewTemplate()) {
			// Can not have any offsprings.
			if (scope.getOffspringForCi(item).size() > 0) {
				result.setRejectCause("Item '" + item.getAlias() +"' can not have any offsprings "
						+ ConfigurationItem.class);
				return (result);
			}
		}
	
		item.setIsBlueprint(rfc.isNewTemplate());
		// Need to retrive the derivedFrom.
		
		if (item.getDerivedFromId() != null) {
			// ROOT Object.
			
			ICi parent = scope.getICiById(new ItemId(item.getDerivedFromId()));
			if (parent == null) {
				result.setRejectCause("Can't find derived from id : "
						+ item.getDerivedFromId());
				return (result);
			}
			deriveAttributes(rfc, scope, parent, item, rfc.isNewTemplate());
		}
		rfc.setTarget(ci);
		rfc.setTargetAlias(ci.getAlias());

		scope.addModifiedICi(ci);
	
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);

	}

	private void deriveAttributes(RFC rfc,
		IObjectScope scope, 
		ICi parent, ConfigurationItem item, 
		boolean asTemplate) {
		
		
		HashMap<String, List<IAttribute>> attrMap = new HashMap<String, List<IAttribute>>();
		HashMap<String, List<IAttribute>> existingAttrMap = new HashMap<String, List<IAttribute>>();
		HashMap<String, List<IAttribute>> attrValueMap = new HashMap<String, List<IAttribute>>();
		
		for (IAttribute existingAttr : scope.getAttributesForCi(item)) {
			List<IAttribute> list = existingAttrMap.get(existingAttr.getAlias());
			if (list == null) {
				list = new ArrayList<IAttribute>();
				existingAttrMap.put(existingAttr.getAlias(), list);
			}
			list.add(existingAttr);
		}
		
		// Map all attribute avaliable from parent.
		for (IAttribute attr: scope.getAttributesForCi(parent)) {
			List<IAttribute> list = attrMap.get(attr.getAlias());
			if (list == null) {
				list = new ArrayList<IAttribute>();
				attrMap.put(attr.getAlias(), list);
			}
			
			
			
			list.add(attr);
			
			// Check if this attribute have default values.
			if (!attr.isNullValue()) {
				List<IAttribute> valueList = attrValueMap.get(attr.getAlias());
				if (valueList == null) {
					valueList = new ArrayList<IAttribute>();
					attrValueMap.put(attr.getAlias(), valueList);
				}
				valueList.add(attr);
			}
		}
		
		for (String alias : attrMap.keySet()) {
			List<IAttribute> defaultValues = attrValueMap.get(alias);
			
			int attrAdded = 0;
			
			List<IAttribute> existingAttributes = existingAttrMap.get(alias);
			if (existingAttributes != null) {
				attrAdded += existingAttributes.size();
				
				// Update default value...
				if (defaultValues != null) {
					if (defaultValues.size() == existingAttributes.size()) {
						for (int i = 0; i < defaultValues.size(); i++) {
							IAttribute def = defaultValues.get(i);
							IAttribute itemValue = existingAttributes.get(i);
							if (itemValue.isNullValue()) {
								IValue v = def.getValue();
								if (v != null) {
									RFCModifyAttributeValue attributeRfc = new RFCModifyAttributeValue();								
									attributeRfc.setNewValue(v.getAsString());
									attributeRfc.setTarget(itemValue);
									//attributeRfc.setAlias(attr.getAlias());
									rfc.addFirst(attributeRfc);
								}
							}
						}
					}
				}
			}
		
			List<IAttribute> attr = attrMap.get(alias);
			IAttribute attrDef = attr.get(0);
			
			int minOccurs = attrDef.getMinOccurs();
		
			if (defaultValues != null) {
				for (IAttribute defaultAttr : defaultValues) {
					
					// Check so we don't add too many attributes.
					if (attrAdded >= attrDef.getMaxOccurs()) {
						break;
					}
					
					RFCAddAttribute attributeRfc = new RFCAddAttribute();
					attributeRfc.setDerivedAttributeId(defaultAttr.getId().asLong());
					//attributeRfc.setAlias(attr.getAlias());
					rfc.addFirst(attributeRfc);
					attrAdded++;
				}
			}
			
			
			// On Templates we always propagte down atleast one attribute.
			if (asTemplate && minOccurs == 0) {
				minOccurs = 1;
			}
			
			for (int i = attrAdded; i < minOccurs; i++) {
				RFCAddAttribute attributeRfc = new RFCAddAttribute();
				attributeRfc.setDerivedAttributeId(attrDef.getId().asLong());
				//attributeRfc.setAlias(attr.getAlias());
				rfc.addFirst(attributeRfc);
			}
		}
	}
	
	
	private IRfcResult performInternal(RFCMoveCi rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();
		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find target CI to modify template #"
					+ rfc);
			return (result);
		}
		
		if (!(ci instanceof ConfigurationItem)) {
			result.setRejectCause("Target CI must be of class "
					+ ConfigurationItem.class);
			return (result);
		}
		
		ConfigurationItem item = (ConfigurationItem) ci;
	
		// Fetch new parent.
		ICi newParent = null;
		if (rfc.getToId() != null) {
			newParent = scope.getICiById(new ItemId(rfc.getToId()));
			if (newParent == null) {
				result.setRejectCause("Can't move '" + item.getAlias() + 
						"' no destination template id '"
						+ rfc.getToId() + "' found!");
				return (result);
			}
	
		} else if (rfc.getToAlias() != null) {
			newParent = scope.getICiFromAlias(rfc.getToAlias());
			if (newParent == null) {
				result.setRejectCause("Can't move '" + item.getAlias() + 
						"' no destination template alias '"
						+ rfc.getToAlias() + "' found!");
				return (result);
			}
		} else {
			result.setRejectCause("Can't move '" + item.getAlias() + "' no template destination specified!");
			return(result);
		}
		
		// Check that the new parent is derived from old parent.
		if (item.getDerivedFromId() == null) {
			result.setRejectCause("Can't move ROOT item!");
			return(result);
		}
		boolean moveEveryware = true;
		if (!moveEveryware) {
			ICi oldParent = scope.getICiById(new ItemId(item.getDerivedFromId()));
			if (!isChildOf(scope, (ConfigurationItem)oldParent , (ConfigurationItem)newParent)) {
				result.setRejectCause("Can't move item '" + item.getAlias() + 
						"' new parent '" + newParent.getAlias() +"'" +
						" is not a child of '" + oldParent.getAlias() + "'");
				return(result);
			}
		}
		
		// ok derive new attributes.
		deriveAttributes(rfc, scope, newParent, item, item.isBlueprint());
		
		// Update CI
		item.setDisplayNameExpression(newParent.getDisplayNameExpression());
		item.setTemplatePath(newParent.getTemplatePath() + "/" + item.getId().asLong());
		item.setDescription(newParent.getDescription());
		item.setDerivedFromId(newParent.getId().asLong());
		scope.addModifiedICi(item);
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}
		
		return(result);
	}
	
	private boolean isChildOf(IObjectScope scope, ConfigurationItem parent, ConfigurationItem child) {
		if (parent == null || child == null) {
			return(false);
		}
		
		if (child.getDerivedFromId().equals(parent.getId().asLong())) {
			return(true);
		}
		
		if (child.getDerivedFromId() == null) {
			return(false);
		}
		
		ICi ci = scope.getICiById(new ItemId(child.getDerivedFromId()));
		return(isChildOf(scope, parent, (ConfigurationItem)ci));
	}

	private IRfcResult performInternal(RFCDestroy rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find Target Ci to destroy #" + rfc);
			return (result);
		}
		if (!(ci instanceof IAttribute)) {
			if (ci.getDerivedFrom() == null) {
				result.setRejectCause("Not allowed to delete Root object");
				return(result);
			}
			// Validate if the CI is proteced...
			IModelService svc = (IModelService) scope.getSession().getService(IModelService.class);
			if (svc != null) {
				if (svc.isCIProteced(ci.getAlias())) {
					result.setRejectCause("Alias '" + ci.getAlias() + "' is protected from deleting (System object)");
					return(result);
				}
			}
		}
		scope.addDestroyedICi(ci);

		// Check that we don't destroy an inherited attribute,
		// unless the entire ci is removed.
		if (ci instanceof IAttribute) {
			IAttribute a = (IAttribute)ci;
			ICi derivedFrom = ci.getDerivedFrom();
			if (derivedFrom != null) {
				ICi owner = a.getOwner();
				
				
				if (!scope.isDestroyed(owner)) {
					// Can only destroy until minOccurs, unless derivedFrom is also destroyed.
					if (!scope.isDestroyed(derivedFrom)) {
						int minOccurs = a.getMinOccurs();
						List<IAttribute> list = owner.getAttributesWithAlias(a.getAlias());
						if (list.size() <= minOccurs) {
							result.setRejectCause("Can't remove a derived attribute '" + ci.getAlias() + "' minOccurs violation " + minOccurs
									 + " in item '" + owner.getAlias() + "'");
							return(result);
						}
					}
				}
			}
		} else {
			// Validate that that the template is not used.
			if (ci.isBlueprint()) {
				{
					// Check if this is used, remove all reference to it.
					HashMap<String, Object> crit = new HashMap<String, Object>();
					crit.put("typeName", ci.getUniqueName());    	
					crit.put("derivedFromId", null);

					List list = scope.getDaoReader().query(BasicAttribute.class, crit);
					for (IAttribute a : (List<IAttribute>)list) {
						RFCDestroy destroyAttr = new RFCDestroy();
						destroyAttr.setTarget(a);
						rfc.addFirst(destroyAttr);
					}
				}
		    	{
		        	// Check if this reference type is removed.
		    		HashMap<String, Object> crit = new HashMap<String, Object>();
		    		crit.put("referenceTypeName", ci.getUniqueName());    	
		    		crit.put("derivedFromId", null);

		    		List list = scope.getDaoReader().query(BasicAttribute.class, crit);
		    		for (IAttribute a : (List<IAttribute>)list) {
		    			RFCDestroy destroyAttr = new RFCDestroy();
		    			destroyAttr.setTarget(a);
		    			rfc.addFirst(destroyAttr);
		    		}
		    	}
			}
			
			// Update attributes that references this....
			List<ICi> referres = scope.getReferrer(ci);
			for (ICi referrer : referres) {
				// Destory that also.
				RFCDestroy destroy = new RFCDestroy();
				destroy.setTarget(referrer);
				rfc.addFirst(destroy);
				
				// Update attribute to null.
				List<IAttribute> attributes = scope.getAttributeForReference(referrer);
				for (IAttribute attr : attributes) {
					RFCModifyAttributeValue modValue = new RFCModifyAttributeValue();
					modValue.setTarget(attr);
					modValue.setNewValue(null);
					modValue.setValueAsLong(null);
					rfc.add(modValue);
				}
			}
		}
			
		// Need to destory all offsprings and all attributes.
		for (ICi offspring : scope.getOffspringForCi(ci)) {
			RFCDestroy destroy = new RFCDestroy();
			destroy.setTarget(offspring);
			rfc.addFirst(destroy);
		}

		for (IAttribute attribute : ci.getAttributes()) {
			// Destory reference also.
			ICi reference = attribute.getReference();
			if (reference != null) {
				RFCDestroy destroyRefernce = new RFCDestroy();
				destroyRefernce.setTarget(reference);
				rfc.addFirst(destroyRefernce);
			}

			RFCDestroy destroy = new RFCDestroy();
			destroy.setTarget(attribute);
			rfc.addFirst(destroy);
		}
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			result = (RfcResult) policy.runValidation(scope, rfc, ci);
		}
		
		if (!result.isRejected()) {
			// Remeber the reference to the destroy.
			rfc.setDestroyedAlias(ci.getAlias());
			rfc.setDestroyedId(ci.getId().asLong());
			rfc.setWasCi(true);
			
			if (ci instanceof IAttribute) {
				rfc.setWasCi(false);
				// When searching the owner ci this destory will be visable.
				rfc.setTarget(((IAttribute)ci).getOwner());
			} else {
				// When searching the derivedFrom ci this destory will be visable.
				rfc.setTarget(ci.getDerivedFrom());
			}
		}
		return (result);
	}

	private IRfcResult performInternal(RFCNewReference rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();

		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find Target Ci #" + rfc);
			return (result);
		}
		String targetDn = rfc.getReferenceTarget();
		ItemId id = ObjectConverter.convertUniqueNameToItemId(scope.getDaoReader(), targetDn);
		if (id == null) {
			log.info("targetDn:" + targetDn + "will skip to create this.");
			return(result);
			
		}
		// Actullay create a new Ci object...
		ConfigurationItem item = new ConfigurationItem();
		item.setDaoReader(reader);
		item.setItemId((new ItemId()).asLong());
		item.setDerivedFrom(ci);
		item.setDisplayNameExpression(ci.getDisplayNameExpression());
		item.setTemplatePath(ci.getTemplatePath() + "/" + item.getId().asLong());
		item.setAlias(ci.getAlias() + "-" + item.getId().toString());
		item.setDescription(ci.getDescription());
		scope.addNewICi(item);

		rfc.setTarget(item);
		rfc.setTargetAlias(item.getAlias());

		ItemId aId = ObjectConverter.convertLongToItemId(rfc
				.getSourceAttributeId());
		if (aId == null) {
			result.setRejectCause("No source attribute id set!");
			return (result);
		}
		ICi source = scope.getICiById(aId);
		if (source == null) {
			result.setRejectCause("Can't find source attribute id "
					+ aId.toString());
			return (result);
		}
		if (!(source instanceof BasicAttribute)) {
			result.setRejectCause("Source in a relation must be an attribute");
			return (result);
		}
		
		BasicAttribute ba = (BasicAttribute) source;
		// TODO: Check if we have a value set here.
		ba.setValueAsString(item.getAsString());
		ba.setValueAsLong(item.getId().asLong());
		scope.addModifiedICi(source);
		
		
		

		RFCModifyIsTemplate templateRFC = new RFCModifyIsTemplate();
		templateRFC.setNewTemplate(false);
		rfc.add(templateRFC);
		
		
		RFCModifyDerivedAttributeValue modRfc = new RFCModifyDerivedAttributeValue();
		modRfc.setAlias("target");
		modRfc.setValue(targetDn);
		
		modRfc.setValueAsLong(id.asLong());
		rfc.add(modRfc);

		// Inserter add the source ci also.
		ICi sourceCi = scope.getAttributeOwner(ba);
		RFCModifyDerivedAttributeValue sourceRfc = new RFCModifyDerivedAttributeValue();
		sourceRfc.setAlias("source");
		sourceRfc.setValue(sourceCi.getAsString());
		sourceRfc.setValueAsLong(sourceCi.getId().asLong());
		rfc.add(sourceRfc);

		item.setTargetId(id.asLong());
		item.setSourceId(sourceCi.getId().asLong());
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);
	}

	private IRfcResult performInternal(RFCModifyDerivedAttributeValue rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();
		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find target CI " + rfc);
			return (result);
		}
		int index = rfc.getIndex();
		int attributeOffset = 0;
		String alias = rfc.getAlias();
		boolean found = false;
		for (IAttribute a : scope.getAttributesForCi(ci)) {
			if (a.getAlias().equals(alias)) {

				// Need to keep track of offset.
				if (attributeOffset == index) {
					BasicAttribute ba = (BasicAttribute) a;
					RFCModifyAttributeValue modRfc = new RFCModifyAttributeValue();
					modRfc.setTarget(ba);
					modRfc.setTargetAlias(ba.getAlias());
					if (rfc.getValueAsAlias() != null) {
						modRfc.setNewValueAsAlias(rfc.getValueAsAlias());
					} else {
						modRfc.setNewValue(rfc.getValue());
					}
					modRfc.setValueAsDate(rfc.getValueAsDate());
					modRfc.setValueAsLong(rfc.getValueAsLong());
					
					rfc.addFirst(modRfc);
					return (result);
				}
				attributeOffset++;
				found = true;
			}
		}
		if (!found && !ci.isBlueprint()) {
			ICi parent = scope.getICiById(new ItemId(((ConfigurationItem)ci).getDerivedFromId()));
			if (parent != null) {
				Set<IAttribute> aList = scope.getAttributesForCi(parent);
				for (IAttribute a : aList) {
					if (a.getAlias().equals(rfc.getAlias())) {
						found = true;
						break;
					}
				}
			}
		}
		
		if (!found) {
			result.setRejectCause("CI '" + ci.getAlias() + "' has no attribute with alias '" + rfc.getAlias() + "'");
			return(result);
		}
		// Try to add the attribute.
		RFCAddAttribute addRFC = new RFCAddAttribute();
		addRFC.setAlias(alias);
		addRFC.setTarget(ci);
		RFCModifyAttributeValue modRfc = new RFCModifyAttributeValue();
		if (rfc.getValueAsAlias() != null) {
			modRfc.setNewValueAsAlias(rfc.getValueAsAlias());
		} else {
			modRfc.setNewValue(rfc.getValue());
		}
		modRfc.setValueAsDate(rfc.getValueAsDate());
		modRfc.setValueAsLong(rfc.getValueAsLong());

		addRFC.add(modRfc);

		rfc.add(addRFC);
	
		rfc.setTargetAlias(ci.getAlias());

		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);
	}

	private IRfcResult performInternal(RFCModifyDisplayNameExpression rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();

		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find Target Ci #" + rfc);
			return (result);
		}
		((ConfigurationItem) ci).setDisplayNameExpression(rfc
				.getNewDisplayNameExpression());
		rfc.setTarget(ci);
		rfc.setTargetAlias(ci.getAlias());

		scope.addModifiedICi(ci);
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);
	}

	private IRfcResult performInternal(RFCModifyAlias rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();

		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find Target Ci #" + rfc);
			return (result);
		}
		
		if (rfc.getNewAlias() == null) {
			result.setRejectCause("Alias can not be null Ci #" + rfc);
			return(result);
		}
		if (ci.getClass().equals(ConfigurationItem.class)) {
			IModelService svc = (IModelService) scope.getSession().getService(IModelService.class);
			if (svc != null) {
				if (svc.isCIProteced(ci.getAlias())) {
					result.setRejectCause("Alias '" + ci.getAlias() + "' is protected (System defined object)");
					return(result);
				}
			}
		}
		
		// Validate alias name schema
		String alias = rfc.getNewAlias().trim();
		String answer = acceptAlias(alias);
		if (answer != null) {
			result.setRejectCause(answer);
			return(result);
		}
		// Check if we reset the same alias name.
		if (!alias.equals(ci.getAlias())) {
			// Special handling for alias names..
			if (ci.getClass().equals(ConfigurationItem.class)) {
				// Must be unique, atlest for now, could imagine with path.
				ICi existCi = scope.getICiFromAlias(alias);
				if (existCi != null) {
					result.setRejectCause("Alias '" + alias  + "' is already used, '" + existCi.toString() + "'");
					return(result);
				}
				/*
				// Need to modify all attributes using this as a type.
				HashMap<String, Object> types = new HashMap<String, Object>();
				types.put("typeName", ci.getAlias());
				List<? extends ICi> typeAttributes = scope.getDaoReader().query(BasicAttribute.class, types);
				for (ICi attr : typeAttributes) {
					if (attr instanceof BasicAttribute) {
						((BasicAttribute)attr).setTypeName(ci.getAlias());
						scope.addModifiedICi(attr);
					}
				}
				HashMap<String, Object> refTypes = new HashMap<String, Object>();
				refTypes.put("referenceTypeName", ci.getAlias());
				List<? extends ICi> refTypeAttributes = scope.getDaoReader().query(BasicAttribute.class, refTypes);
				for (ICi attr : typeAttributes) {
					if (attr instanceof BasicAttribute) {
						((BasicAttribute)attr).setReferenceTypeName(ci.getAlias());
						scope.addModifiedICi(attr);
					}
				}
				*/
			} else if (ci instanceof IAttribute) {
				// Can only change alias on the root attribute definition.
				ICi owner = scope.getAttributeOwner((IAttribute)ci);
				IAttribute theAttr = (IAttribute)ci;
				Long parentID = ((BasicAttribute)theAttr).getDerivedFromId();
				if (parentID != null) {
					// Check if the parent has change, name...
					ICi parentAttr = scope.getICiById(new ItemId(parentID));
					if (parentAttr == null || !parentAttr.getAlias().equals(rfc.getNewAlias())) {
						result.setRejectCause("Attribute '" + theAttr.getAlias() + 
								"' is not defined on CI '" + owner.getAlias() + 
						"'. Can only modify attribute alias where it's defined!");
						return(result);
					}
				}
				// Validate that the alias is unique inside the configuration item.
				for (IAttribute a : scope.getAttributesForCi(owner)) {
					if (alias.equals(a.getAlias())) {
						result.setRejectCause("Attribute alias '" + alias +"' already used in CI '" + owner.getAlias() +"'");
						return(result);
					}
				}
				
				// Propagate the alias modify to all children.
				for (ICi offspringAttr: theAttr.getOffsprings()) {
					RFCModifyAlias modAttrAlias = new RFCModifyAlias();
					modAttrAlias.setNewAlias(rfc.getNewAlias());
					modAttrAlias.setTarget(offspringAttr);
					rfc.addFirst(modAttrAlias);
				}
			}
		}
		
		((ConfigurationItem) ci).setAlias(alias);
		rfc.setTarget(ci);
		rfc.setTargetAlias(ci.getAlias());

		scope.addModifiedICi(ci);

		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);
	}
	
	/**
	 * Validate alias name.
	 * 
	 * <li>Can not start with numeric.</li>
	 * <li>Can not contain space</li>
	 * <li>Can not contain special characters</li>
	 * @param alias
	 * @return
	 */
	private String acceptAlias(String alias) {
		
		Character c = alias.charAt(0);
		if (!Character.isLetter(c)) {
			return("First char in alias '" + alias + "' must be a letter");
		}
		
		
		int index = alias.indexOf(' ');
		if (index > 0) {
			return("Alias '" + alias + "' can not contain spaces");
		}
		index = alias.indexOf(':');
		if (index > 0) {
			return("Alias '" + alias + "' can not contain ':' characters");
		}
		
		return(null);
	}
	
	
	
	private IRfcResult performCiTemplate(CiModifiable rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Ci #" + rfc.getId() + " is not found!");
			return (result);
		}
		rfc.setTarget(ci);

		// scope.mapRfcToCi(rfc, ci);

		return (result);
	}

	private IRfcResult performNewCiRfc(RFCNewCi rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();

		// Require need's to be there.
		ICi ci = scope.getCIFromRFC(rfc);
		if (ci == null) {
			result.setRejectCause("Can't find Target Ci #" + rfc);
			return (result);
		}
		if (!ci.isBlueprint()) {
			result.setRejectCause("Item '" + ci.getAlias() + "' must be a template to derive from");
			return (result);
		}
		
		// Actually create a new Ci object...
		ConfigurationItem item = new ConfigurationItem();
		item.setDaoReader(reader);
		item.setItemId((new ItemId()).asLong());
		item.setDerivedFrom(ci);
		item.setDisplayNameExpression(ci.getDisplayNameExpression());
		item.setTemplatePath(ci.getTemplatePath() + "/" + item.getId().asLong());
		// need to start with no numeric to please xml.
		item.setAlias(ci.getAlias() + "-" + item.getId().toString());
		item.setDescription(ci.getDescription());
		
		// Set the security group for this ci.
		if (rfc.getGroup() != null) {
			ICi group = scope.getICiFromAlias(rfc.getGroup());
			if (group == null) {
				result.setRejectCause("Security Group alias '" + rfc.getGroup() + "' is not found!");
				return(result);
			}
			item.setGid(group.getId().asLong());
		}
		
		// The scope is responsible to validate security...
		scope.addNewICi(item);
		
		scope.addOffspringToCi(ci, item);

		rfc.setTarget(item);

		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ci);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ci));
		}

		return (result);
	}
}
