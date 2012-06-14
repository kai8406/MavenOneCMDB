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

import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiPolicy;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDisplayNameExpression;
import org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.utils.ClassInjector;

public class CiPolicy extends ConfigurationItem implements  ICiPolicy {	
	protected boolean allowNewAttributes = true;
	protected boolean propagateDisplayName = true;
	protected boolean allowDelete = true;
	protected boolean allowAliasChange = true;
	protected boolean propagateDescription = true;
	
	interface ICiRfcPolicyValidator {
		public IRfcResult validate(IObjectScope scope, IRFC rfc, ICi ci);
	}
	
	private HashMap<Class, ICiRfcPolicyValidator> validatorMap = new HashMap<Class, ICiRfcPolicyValidator>();

	public CiPolicy(ICi ci) {
		ClassInjector inject = new ClassInjector();
		inject.injectAttributes(this, ci);
		
		setupValidatorMap();
	}
	
	
	private void setupValidatorMap() {
		validatorMap.put(RFCNewAttribute.class, getNewAttributeValidator());
		validatorMap.put(RFCModifyDisplayNameExpression.class, getModifyDisplaynameValidator());
		validatorMap.put(RFCDestroy.class, getDestoryValidator());
		validatorMap.put(RFCModifyAlias.class, getModifyAliasValidator());
		validatorMap.put(RFCModifyDescription.class, getModifyDescriptionValidator());
	}


	public IRfcResult runValidation(IObjectScope scope, IRFC rfc, ICi ci) {
		IRfcResult result = new RfcResult();
		ICiRfcPolicyValidator validator = validatorMap.get(rfc.getClass());
		if (validator != null) {
			result = validator.validate(scope, rfc, ci);
		}
		return(result);
	}

	private ICiRfcPolicyValidator getNewAttributeValidator() {
		return (new ICiRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, ICi ci) {
				RfcResult result = new RfcResult();
				if (!isAllowNewAttributes()) {
					result.setRejectCause("Cannot create new Attribute! An existing policy disallows Attributes to be added to CI #"
									+ ci.getAlias());
					return (result);
				}
				
				return(result);
			}
		});
	}
	
	private ICiRfcPolicyValidator getModifyDisplaynameValidator() {
		return (new ICiRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, ICi ci) {
				RfcResult result = new RfcResult();
			
				if (isPropagateDisplayName()) {
					for (ICi offspring : scope.getOffspringForCi(ci)) {
						if (!offspring.isBlueprint() || offspring.getDisplayNameExpression() == null) {
							RFCModifyDisplayNameExpression modDisplay = new RFCModifyDisplayNameExpression();
							modDisplay.setNewDisplayNameExpression(((RFCModifyDisplayNameExpression)iRfc).getNewDisplayNameExpression());
							modDisplay.setTarget(offspring);
							iRfc.add(modDisplay);
						}
					}
				}
				
				return(result);
			}
		});
	}
	
	private ICiRfcPolicyValidator getModifyDescriptionValidator() {
		return (new ICiRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, ICi ci) {
				RfcResult result = new RfcResult();
				
				if (isPropagateDescription()) {
					for (ICi offspring : scope.getOffspringForCi(ci)) {
						if (offspring.getDescription() == null) {
							RFCModifyDescription modDesc = new RFCModifyDescription();
							modDesc.setDescription(((RFCModifyDescription)iRfc).getDescription());
							modDesc.setTarget(offspring);
							iRfc.add(modDesc);
						}
					}
				}
				
				return(result);
			}

		});
	}
	private ICiRfcPolicyValidator getModifyAliasValidator() {
		return (new ICiRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, ICi ci) {
				RfcResult result = new RfcResult();
				if (!isAllowAliasChange()) {
					result.setRejectCause("Cannot modify alias name according to policy '" + getAlias() + "'");
					return (result);
				}
				
				return(result);
			}
		});
	}
	
	private ICiRfcPolicyValidator getDestoryValidator() {
		return (new ICiRfcPolicyValidator() {
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, ICi ci) {
				RfcResult result = new RfcResult();
				if (!isAllowDelete()) {
					result.setRejectCause("Cannot delete ci/attributes according to policy '" + getAlias() + "'");
					return (result);
				}
				
				return(result);
			}
		});
	}
	
	private ICiRfcPolicyValidator getAddAttributeValidator() {
		return (new ICiRfcPolicyValidator() {
			
			public IRfcResult validate(IObjectScope scope, IRFC iRfc, ICi a) {
				RfcResult result = new RfcResult();
				return(result);
			}
		});
	}

	/**
	 * Getter/Setter
	 */
	public boolean isAllowNewAttributes() {
		return allowNewAttributes;
	}


	public void setAllowNewAttributes(boolean allowNewAttribute) {
		this.allowNewAttributes = allowNewAttribute;
	}


	public boolean isAllowAliasChange() {
		return allowAliasChange;
	}


	public void setAllowAliasChange(boolean allowAliasChange) {
		this.allowAliasChange = allowAliasChange;
	}


	public boolean isAllowDelete() {
		return allowDelete;
	}


	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}


	public boolean isPropagateDisplayName() {
		return propagateDisplayName;
	}


	public void setPropagateDisplayName(boolean propagetDisplayName) {
		this.propagateDisplayName = propagetDisplayName;
	}


	public boolean isPropagateDescription() {
		return propagateDescription;
	}


	public void setPropagateDescription(boolean propagateDescription) {
		this.propagateDescription = propagateDescription;
	}

	
	
	
	
	
}
