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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.ccb.rfc.RFCAddAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCNewCi;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

/**
 * Old comparer.
 * 
 * @deprecated use BeanCompare
 *
 */
public class BeanToRfcWorker {

	private Log log = LogFactory.getLog(this.getClass());
	
	private boolean noUpdate;

	/**
	 * If true on new ci wil be created.
	 * @param noUpdate
	 */
	public void setNoUpdate(boolean noUpdate) {
		this.noUpdate = noUpdate;
		
	}
	public void cmpBean(CiBean remoteBean, CiBean localBean, BeanScope scope, RfcContainer rfcContainer) {
	
		// Handle correct sequence!
		if (localBean != null) {
			// Skip root object. 
			if (localBean.getDerivedFrom() == null || localBean.getDerivedFrom().equals("null")) {
				return;
			}
			scope.processBean(localBean.getDerivedFrom());
		}

		// DELETE
		if (remoteBean != null && localBean == null) {
			RFCDestroy destoryRfc = new RFCDestroy();
			destoryRfc.setTargetAlias(remoteBean.getAlias());
			rfcContainer.addDestory(destoryRfc);
			return;
		}
		// NEW
		if (remoteBean == null) {
			// Set Status on bean.
			RFCNewCi newRfc = new RFCNewCi();
			if (localBean.getDerivedFrom() == null) {
				throw new IllegalArgumentException("No derived from set on alias=´'" + localBean.getAlias() +"'");
			}
			newRfc.setTargetAlias(localBean.getDerivedFrom());
	
			if (localBean.getAlias() == null) {
				throw new IllegalArgumentException("No alias set on (derivedFrom='" + localBean.getDerivedFrom() +"')");
			}
			newRfc.setAlias(localBean.getAlias());
			if (localBean.getDisplayNameExpression() != null) {
				newRfc.setDisplayNameExpression(localBean.getDisplayNameExpression());
			}
			newRfc.setIsBlueprint(localBean.isTemplate());

			if (localBean.getDescription() != null) {
				newRfc.setDescription(localBean.getDescription());
			}

			if (localBean.isTemplate()) {
				for (AttributeBean aBean : localBean.getAttributes()) {
					if (aBean.isDerived()) {
						continue;
					}
					IRFC rfc = newAttribute(aBean, localBean, scope);
					//rfc.setTargetAlias(newBean.getAlias());
					rfcContainer.addNewAttribute(rfc);
					// need to add attributes last, to handel cyclic dependecies.
					//newRfc.add(newAttribute(aBean, scope));
				}
			}
			for (String name : localBean.fetchAttributeValueAliases()) {
				int index = 0;
				List<IRFC> rfcs = new ArrayList<IRFC>();
				for (ValueBean vBean : localBean.fetchAttributeValueBeans(name)) {
					AttributeBean aBean = localBean.getAttribute(name);
					rfcs.add(setValue(vBean, index, localBean, scope));
					//rfc.setTargetAlias(newBean.getAlias());
					index++;
				}
				if (localBean.isTemplate()) {
					rfcContainer.addNewTemplateValues(rfcs);
				} else {
					rfcContainer.addNewInstanceValues(rfcs);
				}
		
			}
			if (localBean.isTemplate()) {
				rfcContainer.addNewTemplate(newRfc);
			} else {
				rfcContainer.addNewInstance(newRfc);
			}
			//return (newRfc);
			return;
		}

		if (noUpdate) {
			return;
		}
		
		// For now can only modify value.
		
		for (String name : localBean.fetchAttributeValueAliases()) {
			int index = 0;
			
			// doCmpLog(newBean, oldBean, name);
			for (ValueBean newVBean : localBean.fetchAttributeValueBeans(name)) {
				boolean doUpdate = true;
				int oldValues = 0;
				
				// Check if value already set!.
				for (ValueBean oldVBean : remoteBean.fetchAttributeValueBeans(name)) {
					oldValues++;
				
					if (newVBean.isComplexValue()) {
						if (newVBean.getValue().equals(oldVBean.getValue())) {
							doUpdate = false;
							break;
						}
					} else {
						if (newVBean.getValue().equals(oldVBean.getValue())) {
							doUpdate = false;
							break;
						}
					}
				}
				if (!doUpdate) {
					continue;
				}

				log.debug("Modify alias '" + newVBean.getAlias() + "' index=" + index + ", value '" + newVBean.getValue() + "'");
				List<IRFC> rfcs = new ArrayList<IRFC>();	
				AttributeBean aBean = remoteBean.getAttribute(name);
				if (aBean != null && aBean.fetchMaxOccursAsInt() == 1) {
					// Do update on one attribute
					IRFC rfc = setValue(newVBean, aBean.getId(), scope);
					rfc.setTargetAlias(localBean.getAlias());
					rfcs.add(rfc);
				} else {
					// Add value
					rfcs.add(addValue(newVBean, localBean, scope));
				}
				if (localBean.isTemplate()) {
					rfcContainer.addNewTemplateValues(rfcs);
				} else {
					rfcContainer.addNewInstanceValues(rfcs);
				}
			}
		}
	}

	private void doCmpLog(CiBean newBean, CiBean oldBean, String name) {
		List<ValueBean> newValueBeans = newBean.fetchAttributeValueBeans(name);
		List<ValueBean> oldValueBeans = oldBean.fetchAttributeValueBeans(name);
		
		
		System.out.println(newBean.getAlias() + "." + name + "[" + newValueBeans.size() +"] <--> [" + oldValueBeans.size() + "]");
		for (ValueBean v : newValueBeans) {
			System.out.println("\tNEW - " + v);
		}
		for (ValueBean v : oldValueBeans) {
			System.out.println("\tOLD - " + v);
		}
		
	}
	
	public IRFC setValue(ValueBean vBean, Long aId, BeanScope scope) {
		log.debug("Set Value '" + vBean.getAlias() + 
							"' value='" + vBean.getValue()  
							+ "' id '" + aId);
		RFCModifyAttributeValue mod = new RFCModifyAttributeValue();
		mod.setTargetId(aId);
		if (vBean.isComplexValue()) {
			String aliasName = vBean.getValue();

			scope.referenceBean(null, "", aliasName);

			mod.setNewValueAsAlias(aliasName);
		} else {
			mod.setNewValue(vBean.getValue());
		}
		return(mod);
	}
	
	public IRFC addValue(ValueBean vBean, CiBean bean, BeanScope scope) {
		log.debug("Add Value '" + vBean.getAlias() + 
				(vBean.isComplexValue() ? 
						"' aliasValue='" + vBean.getValue() : 
							"' value='" + vBean.getValue()) 
							+ "' on ci '" + bean.getAlias());

		RFCAddAttribute add = new RFCAddAttribute();
		add.setAlias(vBean.getAlias());
		add.setTargetAlias(bean.getAlias());
		RFCModifyAttributeValue mod = new RFCModifyAttributeValue();
		add.add(mod);
		if (vBean.isComplexValue()) {
			String aliasName = vBean.getValue();

			scope.referenceBean(null, "", aliasName);

			mod.setNewValueAsAlias(aliasName);
		} else {
			mod.setNewValue(vBean.getValue());
		}
		
		return(add);
	}
	
	
	public IRFC setValue(ValueBean vBean, int index, CiBean bean, BeanScope scope) {
		
		RFCModifyDerivedAttributeValue modify = new RFCModifyDerivedAttributeValue();
		modify.setTargetAlias(bean.getAlias());
		if (vBean.isComplexValue()) {
			String aliasName = vBean.getValue();

			scope.referenceBean(bean, "", aliasName);

			modify.setValueAsAlias(aliasName);
		} else {
			modify.setValue(vBean.getValue());
		}

		modify.setIndex(index);
		modify.setAlias(vBean.getAlias());
		return (modify);
	}
	
	public IRFC newAttribute(AttributeBean aBean, CiBean bean, BeanScope scope) {
		// Make sure that the type bean is created first.
		
		log.debug("NEW Attribute '" + aBean.getAlias() + "' on ci '" + bean.getAlias());

		scope.referenceBean(bean, "Type", aBean.getType());

		if (aBean.getRefType() != null) {
			scope.referenceBean(bean, "ReferenceType", aBean.getRefType());
		}
		
		
		RFCNewAttribute newRfc = new RFCNewAttribute();
		newRfc.setTargetAlias(bean.getAlias());
		if (aBean.getDisplayName() != null) {
			newRfc.setDisplayNameExpression(aBean.getDisplayName());
		}
		newRfc.setAlias(aBean.getAlias());
		if (aBean.getDescription() != null) {
			newRfc.setDescription(aBean.getDescription());
		}
		newRfc.setValueTypeAlias(aBean.getType());
		if (aBean.getRefType() != null) {
			newRfc.setReferenceTypeAlias(aBean.getRefType());
		}
		if (aBean.getMaxOccurs() != null) {
			newRfc.setMaxOccurs(aBean.fetchMaxOccursAsInt());
		} else {
			newRfc.setMaxOccurs(1);
		}
		if (aBean.getMinOccurs() != null) {
			newRfc.setMinOccurs(aBean.fetchMinOccursAsInt());
		} else {
			newRfc.setMinOccurs(1);
		}

		return (newRfc);
	}

}
