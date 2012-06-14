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
package org.onecmdb.core.internal.policy;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributePolicy;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.utils.ClassInjector;

public class AttributePolicy extends ConfigurationItem implements IAttributePolicy {

	protected String attributeAliasPattern = ".*";
	protected boolean propagateValueChangeToTemplates = false;
	protected boolean valueUniqueInInstance = false;
	protected boolean valueUniqueOnAllInstances = false;
	protected boolean allowValueChange = true;
	protected boolean propagateValueChangeToInstances = false;
	protected boolean propagateInitialValueChange = true;
	protected String valuePattern = null;
	
	protected boolean allowTypeChange = true;
	protected boolean allowReferenceTypeChange = true;
	
	 
	private Log log = LogFactory.getLog(this.getClass());

	interface IAttributeRfcPolicyValidator {
		public IRfcResult validate(IObjectScope scope, IRFC rfc, IAttribute a);
	}
	
	private HashMap<Class, IAttributeRfcPolicyValidator> validatorMap = new HashMap<Class, IAttributeRfcPolicyValidator>();
	
	
	public AttributePolicy(ICi ci) {
		super.copy((ConfigurationItem)ci);
		ClassInjector inject = new ClassInjector();
		inject.injectAttributes(this, ci);
		
		setupValidatorMap();
	}
	
	
	private void setupValidatorMap() {
		validatorMap.put(RFCModifyAttributeValue.class, getModifyValueValidator());
	}


	public IRfcResult runValidation(IObjectScope scope, IRFC rfc, IAttribute a) {
		IRfcResult result = new RfcResult();
		String aliasPattern = getAttributeAliasPattern();
		if (aliasPattern == null) {
			log .warn("No 'attributeAliasPattern' defined in attribute policy '" + this.getAlias() + "'"); 
			return(result);
		}
		
		if (a.getAlias() == null) {
			return(result);
		}
		
		if (!a.getAlias().matches(aliasPattern)) {
			return(result);
		}
		
		// Its for us...
		IAttributeRfcPolicyValidator validator = validatorMap.get(rfc.getClass());
		if (validator != null) {
			result = validator.validate(scope, rfc, a);
		}
		return(result);
	}

	
	private IAttributeRfcPolicyValidator getAddAttributeValidator() {
		return (new IAttributeRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, IAttribute a) {
				RfcResult result = new RfcResult();
				return(result);
			}
		});
	}


	private IAttributeRfcPolicyValidator getModifyTypeValidator() {
		return (new IAttributeRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, IAttribute a) {
				RfcResult result = new RfcResult();
				
				if (!isAllowTypeChange()) {
					result
					.setRejectCause("Type changes not are allowed according to policy");
					return (result);
				}
				return(result);
			}
		});
	}
	
	private IAttributeRfcPolicyValidator getModifyReferenceTypeValidator() {
		return (new IAttributeRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, IAttribute a) {
				RfcResult result = new RfcResult();
				
				if (!isAllowReferenceTypeChange()) {
					result
					.setRejectCause("Type changes not are allowed according to policy");
					return (result);
				}
				return(result);
			}
		});
	}
	
	
	private IAttributeRfcPolicyValidator getModifyValueValidator() {
		return (new IAttributeRfcPolicyValidator() {

			public IRfcResult validate(IObjectScope scope, IRFC iRfc, IAttribute a) {
				RfcResult result = new RfcResult();
				
				if (!(iRfc instanceof RFCModifyAttributeValue)) {
					log.error("Internal error: RFCModifyAttributeValue policy validator called with wrong RFC '" + iRfc.getClass().getName() + "'");
					return(result);
				}
				
				RFCModifyAttributeValue rfc = (RFCModifyAttributeValue)iRfc;
				
				if (!isAllowValueChange()) {
					// Validate that it's not on root policy
					ICi ci = scope.getAttributeOwner(a);
					// Sanity check, allways be able to modify myself. 
					if (!ci.getId().equals(getId())) {
						result.setRejectCause("Value changes not are allowed according to policy");
						return (result);
					}
				}
				
				if (isValueUniqueOnAllInstances()) {
					if (a.getDerivedFrom() != null) {
						Set<ICi> attributes = a.getDerivedFrom().getOffsprings();
						for (ICi ci : attributes) {
							// Should always be.
							if (ci instanceof IAttribute) {
								IAttribute ia = (IAttribute) ci;
								if (((BasicAttribute) ia).getValueAsString().equals(
										(rfc).getNewValue())) {
									result.setRejectCause("Value must be unique on all instances!");
									return (result);
								}
							}
						}
					}
				}
				if (isValueUniqueInInstance()) {
					ICi ci = a.getOwner();
					for (IAttribute ciA : ci.getAttributesWithAlias(a.getAlias())) {
						if (ciA.equals(a)) {
							continue;
						}
						
						if (((BasicAttribute)ciA).getValueAsString().equals(
								((RFCModifyAttributeValue)rfc).getNewValue())) {
							result.setRejectCause("Value must be unique on instance!");
							return (result);
						}							
					}
				}
				
				// Check pattern.
				String pattern = getValuePattern();
				if (pattern != null && rfc.getNewValue() != null) {
					ICi ci = scope.getAttributeOwner(a);
					// Sanity check, allways be able to modify myself. 
					if (!ci.getId().equals(getId())) {
						
						if (!pattern.equals("")) {
							if (!rfc.getNewValue().matches(pattern)) {
								result.setRejectCause("Value '" + rfc.getNewValue() + "' don't match pattern '" + pattern + "' according to policy");
								return(result);
							}
						}
					}
				}
				
				// Modify all offsprings of this IAttribute
				if (isPropagateValueChangeToInstances()) {
					propagateValue(scope, rfc, a, false);
				}
				
				if (isPropagateValueChangeToTemplates()) {
					propagateValue(scope, rfc, a, true);
				}
				if (isPropagateInitialValueChange()) {
					propagateInitalValue(scope, rfc, a);
				}
				
				return(result);
			}
		});
	}
	


	/**
	 * Propagate value changes to template or instance offsprings.
	 * Always propagte attribute is null.
	 * @param scope
	 * @param rfc
	 * @param attr
	 */
	protected void propagateValue(IObjectScope scope, RFCModifyAttributeValue rfc, IAttribute attr, boolean templates) {
		if (attr.isBlueprint()) {
			Set<ICi> offsprings = scope.getOffspringForCi(attr);
			
			for (ICi offspring : offsprings) {
				BasicAttribute ba = (BasicAttribute)offspring;
				if (offspring.isBlueprint() == templates || ba.getValueAsString() == null) {
					RFCModifyAttributeValue setValueRfc = new RFCModifyAttributeValue();
					setValueRfc.setTarget(offspring);
					setValueRfc.setNewValueAsAlias(rfc.getNewValueAsAlias());
					setValueRfc.setNewValue(rfc.getNewValue());
					rfc.add(setValueRfc);
				}
			}
		}
	}
	
	protected void propagateInitalValue(IObjectScope scope, RFCModifyAttributeValue rfc, IAttribute attr) {
		if (attr.isBlueprint()) {
			Set<ICi> offsprings = scope.getOffspringForCi(attr);
			
			for (ICi offspring : offsprings) {
				BasicAttribute ba = (BasicAttribute)offspring;
				if (ba.getValueAsString() == null) {
					RFCModifyAttributeValue setValueRfc = new RFCModifyAttributeValue();
					setValueRfc.setTarget(offspring);
					setValueRfc.setNewValueAsAlias(rfc.getNewValueAsAlias());
					setValueRfc.setNewValue(rfc.getNewValue());
					rfc.add(setValueRfc);
				}
			}
		}
	}

	/**
	 * Getter/Setters.
	 */
	public boolean isAllowValueChange() {
		return allowValueChange;
	}

	public void setAllowValueChange(boolean allowValueChange) {
		this.allowValueChange = allowValueChange;
	}

	public String getAttributeAliasPattern() {
		return attributeAliasPattern;
	}

	public void setAttributeAliasPattern(String attributeAliasPattern) {
		this.attributeAliasPattern = attributeAliasPattern;
	}

	public boolean isPropagateValueChangeToInstances() {
		return propagateValueChangeToInstances;
	}

	public void setPropagateValueChangeToInstances(
			boolean propagateValueChangeToInstances) {
		this.propagateValueChangeToInstances = propagateValueChangeToInstances;
	}

	public boolean isPropagateValueChangeToTemplates() {
		return propagateValueChangeToTemplates;
	}

	public void setPropagateValueChangeToTemplates(
			boolean propagateValueChangeToTemplates) {
		this.propagateValueChangeToTemplates = propagateValueChangeToTemplates;
	}

	public boolean isValueUniqueInInstance() {
		return valueUniqueInInstance;
	}

	public void setValueUniqueInInstance(boolean valueUniqueInInstance) {
		this.valueUniqueInInstance = valueUniqueInInstance;
	}

	public boolean isValueUniqueOnAllInstances() {
		return valueUniqueOnAllInstances;
	}

	public void setValueUniqueOnAllInstances(boolean valueUniqueOnAllInstances) {
		this.valueUniqueOnAllInstances = valueUniqueOnAllInstances;
	}


	public boolean isAllowReferenceTypeChange() {
		return allowReferenceTypeChange;
	}


	public void setAllowReferenceTypeChange(boolean allowReferenceTypeChange) {
		this.allowReferenceTypeChange = allowReferenceTypeChange;
	}


	public boolean isAllowTypeChange() {
		return allowTypeChange;
	}


	public void setAllowTypeChange(boolean allowTypeChange) {
		this.allowTypeChange = allowTypeChange;
	}


	public String getValuePattern() {
		return valuePattern;
	}


	public void setValuePattern(String valuePattern) {
		this.valuePattern = valuePattern;
	}


	public boolean isPropagateInitialValueChange() {
		return propagateInitialValueChange;
	}


	public void setPropagateInitialValueChange(boolean propagateInitialValueChange) {
		this.propagateInitialValueChange = propagateInitialValueChange;
	}
	
	
	

	
	
}
