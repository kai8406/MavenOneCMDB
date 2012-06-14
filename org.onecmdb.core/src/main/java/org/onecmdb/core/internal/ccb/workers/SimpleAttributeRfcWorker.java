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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import oracle.toplink.queryframework.ModifyQuery;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IPolicyService;
import org.onecmdb.core.IPolicyTrigger;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.AttributeModifiable;
import org.onecmdb.core.internal.ccb.IRfcWorker;
import org.onecmdb.core.internal.ccb.rfc.RFCAddAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeReferenceType;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeType;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyMaxOccurs;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyMinOccurs;
import org.onecmdb.core.internal.ccb.rfc.RFCMoveAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCNewReference;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.ObjectConverter;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.internal.reference.ConnectionItem;
import org.onecmdb.core.internal.storage.IDaoReader;
import org.onecmdb.core.tests.profiler.Profiler;
import org.onecmdb.core.utils.xml.BeanScope;

public class SimpleAttributeRfcWorker implements IRfcWorker {

	private IPolicyService policyService;

	private IDaoReader reader;

	
	public void setPolicyService(IPolicyService service) {
		this.policyService = service;
	}

	public void setDaoReader(IDaoReader reader) {
		this.reader = reader;		
	}

	
	public boolean handleRfc(IRFC rfc) {
		
		if (rfc instanceof RFCMoveAttribute) {
			return (true);
		}
		if (rfc instanceof RFCModifyAttributeValue) {
			return (true);
		}
		if (rfc instanceof RFCModifyAttributeType) {
			return (true);
		}
		if (rfc instanceof RFCModifyAttributeReferenceType) {
			return (true);
		}
		if (rfc instanceof RFCModifyMaxOccurs) {
			return (true);
		}
		if (rfc instanceof RFCModifyMinOccurs) {
			return (true);
		}

		if (rfc instanceof RFCNewAttribute) {
			return (true);
		}
		if (rfc instanceof RFCAddAttribute) {
			return (true);
		}

		if (rfc.getClass().equals(AttributeModifiable.class)) {
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
	
	public IRfcResult internalPerform(IRFC rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();

		if (rfc.getClass().equals(AttributeModifiable.class)) {
			return (internperform((AttributeModifiable) rfc, scope));
		}

		if (rfc instanceof RFCModifyAttributeValue) {
			return (internperform((RFCModifyAttributeValue) rfc, scope));
		}

		if (rfc instanceof RFCNewAttribute) {
			return (internperform((RFCNewAttribute) rfc, scope));
		}

		if (rfc instanceof RFCModifyAttributeType) {
			return (internperform((RFCModifyAttributeType) rfc, scope));
		}

		if (rfc instanceof RFCMoveAttribute) {
			return (internperform((RFCMoveAttribute) rfc, scope));
		}

		if (rfc instanceof RFCAddAttribute) {
			return (internperform((RFCAddAttribute) rfc, scope));
		}

		if (rfc instanceof RFCModifyAttributeReferenceType) {
			return (internperform((RFCModifyAttributeReferenceType) rfc, scope));

		}
		if (rfc instanceof RFCModifyMaxOccurs) {
			return (internperform((RFCModifyMaxOccurs) rfc, scope));
		}
		if (rfc instanceof RFCModifyMinOccurs) {
			return (internperform((RFCModifyMinOccurs) rfc, scope));
		}

		result.setRejectCause("TODO");
		return (result);
	}

	private IRfcResult internperform(RFCModifyMinOccurs rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		IAttribute a = (IAttribute) scope.getCIFromRFC(rfc);
		if (!(a instanceof BasicAttribute)) {
			result.setRejectCause("Unhandled Attribute Implementation...");
		}
		((BasicAttribute) a).setMinOccurs(rfc.getNewMinOccurs());
		rfc.setTarget(a);
		rfc.setTargetAlias(a.getAlias());

		scope.addModifiedICi(a);
		
		List<IRFC> rfcs = getOffspringMinOccurrs(scope, a, rfc.getNewMinOccurs());

		for (IRFC mValueRfc : rfcs) {
			rfc.addFirst(mValueRfc);
		}

		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(a);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, a));
		}
		return (result);
	}

	private IRfcResult internperform(RFCModifyMaxOccurs rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		IAttribute a = (IAttribute) scope.getCIFromRFC(rfc);
		if (!(a instanceof BasicAttribute)) {
			result.setRejectCause("Unhandled Attribute Implementation...");
		}
		((BasicAttribute) a).setMaxOccurs(rfc.getNewMaxOccurs());
		rfc.setTarget(a);
		rfc.setTargetAlias(a.getAlias());

		scope.addModifiedICi(a);

		List<IRFC> rfcs = getOffspringMaxOccurrs(scope, a, rfc.getNewMaxOccurs());

		for (IRFC mValueRfc : rfcs) {
			rfc.addFirst(mValueRfc);
		}
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(a);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, a));
		}
		return (result);
	}

	private IRfcResult internperform(RFCModifyAttributeReferenceType rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();
		IAttribute a = (IAttribute) scope.getCIFromRFC(rfc);
		if (!(a instanceof BasicAttribute)) {
			result.setRejectCause("Unhandled Attribute Implementation...");
		}
		
		
		// Can only change reference type on root attribute.
		ICi parent = a.getDerivedFrom();
		if (parent != null) {
			parent = scope.getICiById(parent.getId());
			if (parent == null) {
				result.setRejectCause("Can not set reference type on derived attribute '" + a.getAlias() +"' in ci '" + a.getOwner() + "'");
				return(result);
			}
		}
		
		List<IRFC> rfcs = getAttributeReferenceTypeRFC(scope, a, rfc);

		for (IRFC mValueRfc : rfcs) {
			rfc.addFirst(mValueRfc);
		}
		
		if (rfc.getNewReferenceTypeAlias() == null) {
			((BasicAttribute) a).setReferenceTypeName(null);
		} else {
			IType refType = (IType) scope.getICiFromAlias(rfc
					.getNewReferenceTypeAlias());

			if (refType == null) {
				result.setRejectCause("Refernce type alias <"
						+ rfc.getNewReferenceTypeAlias()
						+ "> is not found/supported.");
				return (result);
			}
		
			// Check that this is derived from References.
			// TODO:

			//if (modelService. refService.getRootReference().

			((BasicAttribute) a).setReferenceTypeName(ObjectConverter
					.convertTypeToString(refType));
		}
		rfc.setTarget(a);
		rfc.setTargetAlias(a.getAlias());

		scope.addModifiedICi(a);

		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(a);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, a));
		}

		return (result);
	}

	private IRfcResult internperform(RFCAddAttribute rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		
		ICi owner = (ICi) scope.getCIFromRFC(rfc);
		if (owner == null) {
			result.setRejectCause("Cannot add Attribute! CI #" + rfc.getTargetId()
					+ " targeted by the RFC does not exist!");
			return (result);
		}
		
		ConfigurationItem ci = (ConfigurationItem) owner;
		Long derivedFrom = ci.getDerivedFromId();
		BasicAttribute fAttribute = null;
		
		
		if (rfc.getDerivedAttributeId() != null) {
			fAttribute = (BasicAttribute) scope.getICiById(new ItemId(rfc.getDerivedAttributeId()));
		} else {
			if (rfc.getAlias() == null) {
				result.setRejectCause("No alias name or derived attribute id is set!");
				return(result);
			}
			
			fAttribute = getAddableAttribute(scope, rfc, ci);
			
		}
		if (fAttribute == null) {
			
			result
					.setRejectCause("Cannot add Attribute! Attribute alias '"
							+ rfc.getAlias()
							+ "' not defined in templates of" 
							+ " " +
							(ci.isBlueprint() ? "template" : "instance")
							+ "´'"
							+ ci.getAlias() + "'");
			return (result);
		}
		// Validate How many we have if needed, -1 indicates unbound.
		if (fAttribute.getMaxOccurs() >= 0) {
			int maxOccurs = 0;
			for (IAttribute a : scope.getAttributesForCi(owner)) {
				if (a.getAlias() == null) {
					continue;
				}
				if (a.getAlias().equals(fAttribute.getAlias())) {
					if (fAttribute.getMaxOccurs() == 1) {
						// Check if attribute already exists, ignore if all the same.
						if (a instanceof BasicAttribute) {
							BasicAttribute ba = (BasicAttribute)a;
							if (compare(ba, fAttribute)) {
								rfc.setTarget(ba);
								return(new RfcResult());
							}
						}
					}
					maxOccurs++;
				}
			}
			
			if (maxOccurs >= fAttribute.getMaxOccurs()) {
				
				result
						.setRejectCause("Cannot add Attribute! According to template, CI '"
								+ owner.getAlias()
								+ "' can only have a maximum of "
								+ maxOccurs
								+ " attribute(s) with alias '"
								+ fAttribute.getAlias()
								+ "'!");
				return (result);
			}
		}
	
		// Create Attribute.
		BasicAttribute ba = new BasicAttribute();
		ba.setDaoReader(reader);
		// ba.setItemId((new ItemId()).asLong());
		ba.setAlias(fAttribute.getAlias());
		ba.setComplexValue(fAttribute.isComplexValue());
		ba.setDerivedFrom(fAttribute);
		ba.setTypeName(fAttribute.getTypeName());
		ba.setReferenceTypeName(fAttribute.getReferenceTypeName());
		ba.setOwner(owner);
		ba.setMaxOccurs(fAttribute.getMaxOccurs());
		ba.setMinOccurs(fAttribute.getMinOccurs());
		ba.setIsBlueprint(owner.isBlueprint());
		ba.setDescription(fAttribute.getDescription());
		ba.setDisplayNameExpression(fAttribute.getDisplayNameExpression());
		// Set default value.
		if (true) {
			String value = null;
			if (fAttribute.getValueAsString() != null) {
				if (fAttribute.isComplexValue()) {
					if (fAttribute.getReferenceTypeName() == null) {
						value = fAttribute.getValueAsString();
					} else {
						ItemId id = ObjectConverter.convertUniqueNameToItemId(this.reader, fAttribute.getValueAsString());
						ICi ref = scope.getICiById(id);
						Set<IAttribute> refAttrs = scope.getAttributesForCi(ref);
						for (IAttribute refAttr : refAttrs) {
							if (refAttr.getAlias().equals("target")) {
								value = ((BasicAttribute)refAttr).getValueAsString();
								break;
							}

						}
					}
				} else {
					value = fAttribute.getValueAsString();
				}
			}
			
			if (value != null) {
				RFCModifyAttributeValue modValue = new RFCModifyAttributeValue();
				modValue.setNewValue(value);
				rfc.addFirst(modValue);
			}
		} else {
			ba.setValueAsString(fAttribute.getValueAsString());
		}

		rfc.setTarget(ba);
		rfc.setTargetAlias(ba.getAlias());


		scope.addNewICi(ba);
		scope.addAttributeToCi(owner, ba);
		scope.addOffspringToCi(fAttribute, ba);
		
		if (ba.isBlueprint()) {
			addAttributeToOffsprings(scope, rfc, ba, owner);
		}
		
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ba);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ba));
		}
		return (result);
	}
	
	/**
	 * Compare two attribute, to check if thay match.
	 * @param ba
	 * @param attribute
	 * @return
	 */
	private boolean compare(BasicAttribute a1, BasicAttribute a2) {
		if (!isEqual(a1.getTypeName(), a2.getTypeName())) {
			return(false);
		}
		if (!isEqual(a1.getAlias(), a2.getAlias())) {
			return(false);
		}
		if (!isEqual(a1.getReferenceTypeName(), a2.getReferenceTypeName())) {
			return(false);
		}
		if (!isEqual(a1.getMaxOccurs(), a2.getMaxOccurs())) {
			return(false);
		}
		if (!isEqual(a1.getMinOccurs(), a2.getMinOccurs())) {
			return(false);
		}
		return(true);
	}

	private boolean isEqual(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return(true);
		}
		if (o1 == null && o2 != null) {
			return(false);
		}
		if (o1 != null && o2 == null) {
			return(false);
		}
		return(o1.equals(o2));
	}

	private BasicAttribute getAddableAttribute(IObjectScope scope, RFCAddAttribute rfc, ConfigurationItem ci) {
		if (ci == null) {
			return(null);
		}
		BasicAttribute fAttribute = null;
		
		// Check if we have an own attribute.
		for (IAttribute ownAttribute: scope.getAttributesForCi(ci)) {
			BasicAttribute ba = (BasicAttribute)ownAttribute; 
			if (ownAttribute.getAlias().equals(rfc.getAlias()) && ba.getDerivedFromId() == null) {
				return(ba);
			}
		}
		Long derivedFrom = ci.getDerivedFromId();
		if (derivedFrom == null) {
			return(null);
		}
		ICi derivedFromCi = scope.getICiById(new ItemId(derivedFrom));
		return(getAddableAttribute(scope, rfc, (ConfigurationItem)derivedFromCi));
	}
	private IRfcResult internperform(AttributeModifiable rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		ICi a = scope.getCIFromRFC(rfc);
		if (a == null) {
			result.setRejectCause("Attribute #" + rfc + " is not found!");
			return (result);
		}
		if (!(a instanceof IAttribute)) {
			result.setRejectCause("Attribute #" + rfc + " is not an attribute!");
			return (result);
		}
		rfc.setTarget(a);
		return (result);
	}

	private IRfcResult internperform(RFCNewAttribute rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		ICi owner = (ICi) scope.getCIFromRFC(rfc);
		if (owner == null) {
			result.setRejectCause("Cannot create new Attribute! CI #"
					+ rfc.getTargetId()
					+ " targeted by the RFC does not exist!");
			return (result);
		}
	
		
		BasicAttribute ba = new BasicAttribute();
		ba.setDaoReader(reader);
		ba.setOwner(owner);
		ba.setIsBlueprint(owner.isBlueprint());

		rfc.setTarget(ba);
		scope.addNewICi(ba);
		scope.addAttributeToCi(owner, ba);
		
		// Progagate this attribute to children.
		addAttributeToOffsprings(scope, rfc, ba, owner);
			
		//	Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(ba);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ba));
		}
		return (result);
	}


	private void addAttributeToOffsprings(IObjectScope scope, IRFC rfc, IAttribute attr, ICi ci) {
		Set<ICi> offsprings = scope.getOffspringForCi(ci);
		for (ICi offspring : offsprings) {
			int attributeOffsprings = 0;
			if (offspring.isBlueprint()) {
				attributeOffsprings = 1;
			} else {
				attributeOffsprings = attr.getMinOccurs();
			}
			for (int i = 0; i < attributeOffsprings; i++) {
				RFCAddAttribute attributeRfc = new RFCAddAttribute();
				attributeRfc.setTarget(offspring);
				attributeRfc.setDerivedAttributeId(attr.getId().asLong());
				rfc.add(attributeRfc);
			}
			//addAttributeToOffsprings(scope, rfc, attr, offspring);
		}
	}
	
	private IRfcResult internperform(RFCModifyAttributeType rfc,
			IObjectScope scope) {
		RfcResult result = new RfcResult();
		IAttribute a = (IAttribute) scope.getCIFromRFC(rfc);
		if (!(a instanceof BasicAttribute)) {
			result.setRejectCause("Unhandled Attribute Implementation...");
		}
		
		
		ICi parent = a.getDerivedFrom();
		if (parent != null) {
			parent = scope.getICiById(parent.getId());
			if (parent == null) {
				result.setRejectCause("Can not set reference type on derived attribute '" + a.getAlias() +"' in ci '" + a.getOwner() + "'");
				return(result);
			}
		}
		
		if (rfc.getNewTypeAlias() == null) {
			result.setRejectCause("Can not set 'null' type on attribute '" + a.getAlias() +"' in ci '" + a.getOwner() + "'");
			return(result);
		}
		// Check if this type is a primitive.
		IType newType = SimpleTypeFactory.getInstance().toType(
				rfc.getNewTypeAlias());

		if (newType == null) {
			newType = (IType) scope.getICiFromAlias(rfc.getNewTypeAlias());
			if (newType == null) {
				result.setRejectCause("Type '" + rfc.getNewTypeAlias() +"' is not found!");
				return(result);
			}
			// Type must be a template.
			if (!((ICi)newType).isBlueprint()) {
				result.setRejectCause("Type '" + newType.getAlias() +"' must be a template");
				return(result);
			}
		}

		if (newType == null) {
			result.setRejectCause("Type alias <" + rfc.getNewTypeAlias()
					+ "> is not found/supported.");
			return (result);
		}
		
		/*
		 * Reset all values. Need to do this before type is changed!
		 */ 
		IType cType = ((BasicAttribute)a).getValueType();
		if (cType != null) {
			// Reset all through rfcs values.

			List<IRFC> rfcs = getResetAttributeValueRFC(scope, a, cType, newType);

			for (IRFC mValueRfc : rfcs) {
				rfc.addFirst(mValueRfc);
			}

			IValue value = a.getValue();
			if (value != null) {
				// Validate that the value is correct according to new type
				try {
					newType.parseString(value.getAsString());
				} catch (Throwable t) {
					// Need to reset the value.
					RFCModifyAttributeValue mvalue = new RFCModifyAttributeValue();
					mvalue.setTarget(a);
					mvalue.setNewValue(null);
					rfc.addFirst(mvalue);
				}
			}
			rfc.setOldTypeAlias(cType.getAlias());
		}
		
		((BasicAttribute) a).setTypeName(ObjectConverter
				.convertTypeToString(newType));
		((BasicAttribute) a).setComplexValue((newType instanceof ICi));
		rfc.setTarget(a);
		rfc.setTargetAlias(a.getAlias());

		scope.addModifiedICi(a);

		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(a);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, a));
		}
		return (result);
	}

	private List<IRFC> getOffspringMaxOccurrs(IObjectScope scope, IAttribute a, int newMaxOccurs) {
		List<IRFC> rfcs = new ArrayList<IRFC>();
		Set<ICi> set = scope.getDaoReader().getAttributeOffsprings(a.getId());
		for (ICi ci : set) {
			if (!(ci instanceof IAttribute)) {
				continue;
			}
			IAttribute attr = (IAttribute)ci;
			if (!attr.isBlueprint()) {
				continue;
			}
			// Change max...
			RFCModifyMaxOccurs modMax = new RFCModifyMaxOccurs();
			modMax.setTarget(attr);
			modMax.setNewMaxOccurs(newMaxOccurs);
			rfcs.add(modMax);
		}
		
		return(rfcs);
	}
	
	private List<IRFC> getOffspringMinOccurrs(IObjectScope scope, IAttribute a, int newMinOccurs) {
		List<IRFC> rfcs = new ArrayList<IRFC>();
		Set<ICi> set = scope.getDaoReader().getAttributeOffsprings(a.getId());
		for (ICi ci : set) {
			if (!(ci instanceof IAttribute)) {
				continue;
			}
			IAttribute attr = (IAttribute)ci;
			if (!attr.isBlueprint()) {
				continue;
			}
			// Change max...
			RFCModifyMinOccurs modMin = new RFCModifyMinOccurs();
			modMin.setTarget(attr);
			modMin.setNewMinOccurs(newMinOccurs);
			rfcs.add(modMin);
		}
		
		return(rfcs);
	}
	
	
	private List<IRFC> getResetAttributeValueRFC(IObjectScope scope, IAttribute a, IType cType, IType newType) {
		List<IRFC> rfcs = new ArrayList<IRFC>();
		Set<ICi> set = scope.getDaoReader().getAttributeOffsprings(a.getId());
		for (ICi ci : set) {
			if (!(ci instanceof IAttribute)) {
				continue;
			}
			IAttribute attr = (IAttribute)ci;
			
			// Change type...
			RFCModifyAttributeType modType = new RFCModifyAttributeType();
			modType.setTarget(attr);
			modType.setNewType(newType);
			modType.setOldType(cType);
			rfcs.add(modType);
		}
		
		return(rfcs);
		
		
	}
	
	private List<IRFC> getAttributeReferenceTypeRFC(IObjectScope scope, IAttribute a, RFCModifyAttributeReferenceType orgRfc) {
		List<IRFC> rfcs = new ArrayList<IRFC>();
		Set<ICi> set = scope.getDaoReader().getAttributeOffsprings(a.getId());
		for (ICi ci : set) {
			if (!(ci instanceof IAttribute)) {
				continue;
			}
			IAttribute attr = (IAttribute)ci;
			
			// Change type...
			RFCModifyAttributeReferenceType modType = new RFCModifyAttributeReferenceType();
			modType.setTarget(attr);
			modType.setNewReferenceTypeAlias(orgRfc.getNewReferenceTypeAlias());
			modType.setOldReferenceTypeAlias(((BasicAttribute)a).getReferenceTypeName());
			rfcs.add(modType);
		}
		
		return(rfcs);
		
		
	}

	
	private IRfcResult internperform(RFCModifyAttributeValue rfc,
			IObjectScope scope) {
		
		RfcResult result = new RfcResult();
		
		IAttribute a = (IAttribute) scope.getAttributeFromRFC(rfc);

		if (a == null) {
			result.setRejectCause("Attribute ID '" + rfc.getTargetId() + "' is not found!");
			return(result);
		}
		if (!(a instanceof BasicAttribute)) {
			result.setRejectCause("Un handled Attribute Implementation...");
			return(result);
		}
		
		rfc.setTarget(a);
		BasicAttribute ba = (BasicAttribute) a;
		
		// Validate value.
		String typeName = ba.getTypeName();
		IType type = SimpleTypeFactory.getInstance().toType(typeName);
		if (type == null) {
			ItemId id = ObjectConverter.convertUniqueNameToItemId(this.reader, typeName);
			type = scope.getICiById(id);
		}
	
		/*
		IType type = ba.getValueType();
		*/
		if (type == null) {
			result.setRejectCause("No type on attribute '" + ba.getAlias() +"' set. Can't validate value!");
			return(result);
		}
		// Validate empty strings...
		if (rfc.getNewValue() != null && rfc.getNewValue().length() == 0) {
			rfc.setNewValue(null);
		}
		
		if (rfc.getNewValueAsAlias() != null && rfc.getNewValueAsAlias().length() == 0) {
			rfc.setNewValueAsAlias(null);
		}
		
		
		IValue target = null;
		try {
			if (rfc.getNewValue() != null) {
				target = type.parseString(rfc.getNewValue());
			} else {
				if (rfc.getNewValueAsAlias() != null) {
					
					// Check if we have a "" as target value.
					if (rfc.getNewValueAsAlias().length() > 0) {
						//	Validate the the value is a child of type.
						target = (ICi) scope.getICiFromAlias(rfc
								.getNewValueAsAlias());
					}
				}
			}
			if (target instanceof ConfigurationItem) {
				ConfigurationItem ci = (ConfigurationItem)target;
				// Will throw exception on error.
				validateType(scope, ci, type);
			}
		} catch (Throwable t) {
			result.setRejectCause(
					"Attribute '" + ba.getAlias() + "'s value '" + 
					((rfc.getNewValue() == null) ? rfc.getNewValueAsAlias() : rfc.getNewValue()) + 
					"' is not compatible by type '" + type.getAlias() + "' : " 
					+ t.toString()  
			);
			return(result);		
		}

		// Remember old value.
		IValue oldValue = null;
		try {
			oldValue = ba.getValue();
		} catch (Throwable t) {
			rfc.setOldValue(ba.getValueAsString());
		}
		
		if (oldValue != null) {
			if (oldValue instanceof ICi) {
				rfc.setOldValue(((ICi)oldValue).getAlias());
			} else {
				rfc.setOldValue(oldValue.getAsString());
			}
		}
		
		// Set the value.
		if (ba.getReferenceTypeName() != null && (type instanceof ICi)) {
			// Create a connection through a reference, if type is a ICi(complex type)
			
			// If we have a reference reuse it.
			ICi reference = null;
			if (ba.getValueAsString() != null) {
				// REUSE reference.
				ItemId itemId = ObjectConverter.convertUniqueNameToItemId(scope
						.getDaoReader(), ba.getValueAsString());
				reference = scope.getICiById(itemId);
			}
			
			
			if (reference != null) {
				if (rfc.getNewValue() == null && rfc.getNewValueAsAlias() == null) {
					// Remove reference..
					RFCDestroy destroyRel = new RFCDestroy();
					destroyRel.setTarget(reference);
					rfc.add(destroyRel);
					ba.setValueAsString(null);
					ba.setValueAsLong(null);
					ba.setValueAsDate(null);
					scope.addModifiedICi(ba);
				} else { 
					// Modify target reference.
					RFCModifyDerivedAttributeValue modTarget = new RFCModifyDerivedAttributeValue();
					modTarget.setAlias("target");
					ICi targetCi = null;
					
					if (rfc.getNewValueAsAlias() != null) {
						targetCi = scope.getICiFromAlias(rfc.getNewValueAsAlias());
						if (targetCi == null) {
							result.setRejectCause("No CI with alias '" + rfc.getNewValueAsAlias() + "' found!");
							return(result);
						}
						modTarget.setValue(((IValue)targetCi).getAsString());
						modTarget.setValueAsLong(targetCi.getId().asLong());
					} else {
						modTarget.setValue(rfc.getNewValue());
						ItemId itemId = ObjectConverter.convertUniqueNameToItemId(scope
								.getDaoReader(), rfc.getNewValue());
						targetCi = scope.getICiById(itemId);
						if (targetCi == null) {
							result.setRejectCause("No CI with id '" + itemId + "' found (" + rfc.getNewValue() + ")!");
							return(result);
						}
					}
					modTarget.setTarget(reference);
					rfc.addFirst(modTarget);
	
					// Modify new target id alias on the reference...
					if (reference instanceof ConfigurationItem) {
						((ConfigurationItem)reference).setTargetId(targetCi.getId().asLong());
						scope.addModifiedICi(reference);
					}
					/*
						// Modify target reference.
						RFCModifyDerivedAttributeValue modSource = new RFCModifyDerivedAttributeValue();
						modSource.setAlias("source");
						modSource.setValue(scope.getAttributeOwner(ba).getAsString());
						modSource.setTarget(reference);
						rfc.addFirst(modSource);
					 */
				}
			} else {
				// Create new Reference object.
				if (rfc.getNewValue() != null || rfc.getNewValueAsAlias() != null) {
					// Will create a new reference ci.
					RFCNewReference ref = new RFCNewReference();
					ItemId itemId = ObjectConverter.convertUniqueNameToItemId(scope
							.getDaoReader(), ba.getReferenceTypeName());
					ref.setTargetId(ObjectConverter.convertItemIdToLong(itemId));
					ref.setSourceAttributeId(ba.getId().asLong());
					
					if (rfc.getNewValueAsAlias() != null) {
						IValue value = (IValue) scope.getICiFromAlias(rfc
								.getNewValueAsAlias());
						if (value == null) {
							ICi ci = scope.getAttributeOwner(ba);
							String ciAlias = "<empty>";
							if (ci != null) {
								ciAlias = ci.getAlias();
							}
							result.setRejectCause("Problem: CI '" + ciAlias + "':s attribute '" + ba.getAlias() + "':s reference value '"
									+ rfc.getNewValueAsAlias() + "' is not found. ");
							return(result);
						}
						ref.setReferenceTarget(value.getAsString());
					} else {
						ref.setReferenceTarget(rfc.getNewValue());
					}
					rfc.addFirst(ref);
				} else {
					ba.setValueAsString(null);
					ba.setValueAsLong(null);
					ba.setValueAsDate(null);
					scope.addModifiedICi(ba);
				}
			}
		} else {
			if (target != null && (target instanceof ICi)) {
				ba.setValueAsString(target.getAsString());
				ba.setValueAsLong(((ICi)target).getId().asLong());
			} else {
				if (target != null) {
					if (target.getAsJavaObject() instanceof Number) {
						Number n = (Number)target.getAsJavaObject();
						Long l = n.longValue();
						ba.setValueAsLong(l);
					}
					if (target.getAsJavaObject() instanceof XMLGregorianCalendar) {
						XMLGregorianCalendar cal = (XMLGregorianCalendar) target.getAsJavaObject();
						
						Calendar c = new GregorianCalendar();
						
						c.set(Calendar.YEAR, cal.getYear() >= 0 ? cal.getYear() : 1900);
						c.set(Calendar.MONTH, cal.getMonth() > 0 ? (cal.getMonth()-1) : 0);
						c.set(Calendar.DAY_OF_MONTH, cal.getDay() >= 0 ? cal.getDay() : 1);
						c.set(Calendar.HOUR, cal.getHour() >= 0 ? cal.getHour() : 0);
						c.set(Calendar.MINUTE, cal.getMinute() >= 0 ? cal.getMinute() : 0);
						c.set(Calendar.SECOND, cal.getSecond() >= 0 ? cal.getSecond() : 0);
						//c.set(Calendar.ZONE_OFFSET, cal.getTimezone());
						ba.setValueAsDate(c.getTime());
					}
				}
				ba.setValueAsString(rfc.getNewValue());
				//ba.setValueAsDate(rfc.getValueAsDate());
				//ba.setValueAsLong(rfc.getValueAsLong());
			}
			scope.addModifiedICi(ba);
		}
		
		rfc.setTargetAlias(ba.getAlias());
		
		// Call policy validation.		
		IPolicyTrigger policy = this.policyService.getPolicy(a);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, ba));
		}
		
		
		/*
		SimpleAttributePolicy policy = new SimpleAttributePolicy(
				);
		
		if (!policy.allowValueChange()) {
			result
					.setRejected("Value changes not are allowed according to policy");
			return (result);
		}

		if (policy.compareOldValue()) {
			if (!ba.getValueAsString().equals(rfc.getOldValue())) {
				result
						.setRejected("Value is in conflict, old value don't match current value");
			}
		}

		if (policy.valueUnique()) {
			if (a.getDerivedFrom() != null) {
				Set<ICi> attributes = a.getDerivedFrom().getOffsprings();
				for (ICi ci : attributes) {
					// Should always be.
					if (ci instanceof IAttribute) {
						IAttribute ia = (IAttribute) ci;
						if (((BasicAttribute) ia).getValueAsString().equals(
								rfc.getNewValue())) {
							result.setRejected("Value must be unique!");
							return (result);
						}
					}
				}
			}
		}
		*/
		
	
		
		// Modify all offsprings of this Iattribute
		// Done in policy
		// setValueToOffsprings(scope, rfc, ba);
		
		
		/*
		 * IValue value = ba.getValueType().parseString(rfc.getNewValue());
		 * setValue(this.reader, ba, value, scope, result, this.policyService);
		 */
		return (result);
	}
	
	private void validateType(IObjectScope scope, ConfigurationItem ci, IType type) {
		if (type.equals(ci)) {
			// It's ok.
			return;
		}

		Long id = ci.getDerivedFromId();
		if (id == null) {
			throw new IllegalArgumentException("Not of correct type");			
		}
		ICi parent = scope.getICiById(new ItemId(id));
		validateType(scope, (ConfigurationItem)parent, type);
	}
	
	
	/*
	 * static void setValue(IDaoReader reader, BasicAttribute ba, IValue value,
	 * IObjectScope scope, RfcResult result, IPolicyService pService) { if
	 * (value instanceof ICi) { // Create a new Connection here. if
	 * (ba.getValueAsString() != null) { reconnectConnection(ba, (ICi)value,
	 * scope, result, pService); } else { ICi connection =
	 * createConnection(reader, ba, (ICi)ba.getReferenceType(), (ICi)value,
	 * scope, result, pService); if (connection == null) { return; }
	 * ba.setValueAsString(connection.asString()); } } else {
	 * ba.setValueAsString(value.asString()); } scope.addModifiedICi(ba); }
	 * 
	 * protected static void reconnectConnection(IAttribute source, ICi target,
	 * IObjectScope scope, RfcResult result, IPolicyService pService) { ICi
	 * connection = source.getReference(); ConnectionItem item = new
	 * ConnectionItem(null, connection); item.setTarget(target, scope);
	 * scope.addModifiedICi(connection); }
	 * 
	 * protected static ICi createConnection(IDaoReader reader, ICi source, ICi
	 * referenceType, ICi target, IObjectScope scope, RfcResult result,
	 * IPolicyService pService) { // CreateOffspring will add the conection to //
	 * the object scope as new. ICi connection =
	 * SimpleCiRfcWorker.createOffspring(reader, pService, referenceType, scope,
	 * result); if (connection == null) { return(null); } // We donw need any
	 * reader here!!! ConnectionItem ref = new ConnectionItem(null, connection);
	 * ref.setTarget(target, scope);
	 * 
	 * return(connection); }
	 */

	private IRfcResult internperform(RFCMoveAttribute rfc, IObjectScope scope) {
		RfcResult result = new RfcResult();
		IAttribute a = (IAttribute) scope.getCIFromRFC(rfc);
		if (!(a instanceof BasicAttribute)) {
			result.setRejectCause("Unhandled Attribute Implementation...");
		}
		BasicAttribute ba = (BasicAttribute) a;

		ba.setOwnerId(rfc.getNewOwnerId());

		scope.addModifiedICi(ba);
	
		// Call policy if exists.
		IPolicyTrigger policy = this.policyService.getPolicy(a);
		if (policy != null) {
			return(policy.runValidation(scope, rfc, a));
		}

		return (result);
	}


}
