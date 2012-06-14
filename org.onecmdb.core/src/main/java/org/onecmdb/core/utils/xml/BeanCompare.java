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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.TreeBag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

public class BeanCompare {
	
	BeanRFCGenerator rfcGenerator;
	private Log log = LogFactory.getLog(this.getClass()); 
	
	public BeanRFCGenerator getRfcGenerator() {
		return rfcGenerator;
	}

	public void setRfcGenerator(BeanRFCGenerator rfcGenerator) {
		this.rfcGenerator = rfcGenerator;
	}

	
	public void compare(CiBean base, CiBean remote, CiBean local) {
		// Handle correct sequence of rfc.
		if (local != null) {
			// Check if root. 
			if (local.getDerivedFrom() == null || local.getDerivedFrom().equals("null")) {
				return;
			}
			this.rfcGenerator.getScope().referenceBean(local, "DerivedFrom", local.getDerivedFrom());
			this.rfcGenerator.getScope().processBean(local.getDerivedFrom());
		}
		
		if (base == null) {
			// Two way compare.
			// Compare with remote only.
			if (remote == null && local != null) {
				// Case 8.
				// Add Bean..
				this.rfcGenerator.addCi(local);
			}
			if (remote != null && local != null) {
				// Case 9,10
				// Compare local <--> remote
				compare(local, remote, false);
			}
		
		} else {
			// Three way compare
			if (remote == null && local != null) {
				// Case 1,2
				// Removed from remote, exists in base and local.
				// Will cause conflict here if it's changed.
				// Compare local <--> base
				// What to do....
				// Add it again...
				
				if (local.getAlias().equals(base.getAlias())) {
					this.rfcGenerator.addCi(local);
				} else {
					compare(local, base, true);
				}
			}
			if (remote != null && local != null) {
				// Case 3,4
				// Three way compare.
				compare(local, base, true);
			}
			
			if (remote != null && local == null) {
				// Case 7
				// Remove
				this.rfcGenerator.removeCi(remote);
			}
		}
	}
	
	
	/*
	 * Compare two beans. The left bean is the one that
	 * should be the end result.
	 */
	public void compare(CiBean left, CiBean right, boolean doDelete) {
		// DerivedFrom.
		if (!isObjectEqual(left.getDerivedFrom(), right.getDerivedFrom())) {
			// Not supported!
		}
		
		// DisplayNameExpression.
		if (left.getDisplayNameExpression() != null) {
			if (!isObjectEqual(left.getDisplayNameExpression(), right.getDisplayNameExpression())) {
				// Update id.
				left.setId(right.getId());
				rfcGenerator.modifyDisplayNameExpr(left);

			}
		}	
	
		if (left.getDescription() != null) {

			// Description.
			if (!isObjectEqual(left.getDescription(), right.getDescription())) {
				// Update id.
				left.setId(right.getId());

				rfcGenerator.modifyDescrption(left);
			}
		}
		
		// isBlueprint.
		if (!isObjectEqual(left.isTemplate(), right.isTemplate())) {
			// Update id.
			left.setId(right.getId());
			
			rfcGenerator.modifyTemplate(left);
		}
		
		// Attributes.
		for (AttributeBean leftAtt : left.getAttributes()) {
			if (leftAtt.isDerived()) {
				continue;
			}
			AttributeBean rightAtt = right.getAttribute(leftAtt.getAlias());
			compare(left, leftAtt, rightAtt);
		}
		
		if (doDelete) {
			// Handle delete.
			for (AttributeBean rightAtt : right.getAttributes()) {
				if (rightAtt.isDerived()) {
					continue;
				}
				AttributeBean leftAtt = left.getAttribute(rightAtt.getAlias());
				compare(left, leftAtt, rightAtt);
			}
		}
		
		// Attribute values.
		Map<String, List<ValueBean>> leftValue = getValueMap(left);
		Map<String, List<ValueBean>> rightValue = getValueMap(right);
		
		// Compare values.			
		for (String key : leftValue.keySet()) {
			List<ValueBean> leftList = leftValue.get(key);
			List<ValueBean> rightList = rightValue.get(key);			
			compare(left, leftList, rightList);
		}
		
		if (doDelete) {
			// Delete.
			for (String key : rightValue.keySet()) {
				List<ValueBean> leftList = leftValue.get(key);
				if (leftList != null) {
					continue;
				}
				List<ValueBean> rightList = rightValue.get(key);				
				compare(left, leftList, rightList);
			}
		}
	}
	
	/**
	 * Construct a map of value with the alias name as the key.
	 * 
	 * @param bean
	 * @return
	 */
	private Map<String, List<ValueBean>> getValueMap(CiBean bean) {
		Map<String, List<ValueBean>> map = new HashMap<String, List<ValueBean>>();
		for (ValueBean vBean : bean.getAttributeValues()) {
			List<ValueBean> list = map.get(vBean.getAlias());
			if (list == null) {
				list = new ArrayList<ValueBean>();
				map.put(vBean.getAlias(), list);
			}
			list.add(vBean);
		}
		return(map);

	}
	/**
	 * Compare checks if obejcts is null.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	private boolean isObjectEqual(Object o1, Object o2) {
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

	public void compare(CiBean left, CiBean right) {
		// Alias.
		if (!isObjectEqual(left.getAlias(), right.getAlias())) {
			// Only supported if we have ID's on both.
			if (isObjectEqual(left.getId(), right.getId())) {
				rfcGenerator.modifyCIAlias(left);	
			}
		}
		
		// DerivedFrom.
		if (!isObjectEqual(left.getDerivedFrom(), right.getDerivedFrom())) {
			// Only supported if we have ID's on both.
			if (isObjectEqual(left.getId(), right.getId())) {
				rfcGenerator.modifyDerivedFrom(left);
			}	
		}
		
		
		// DisplayNameExpression.
		if (left.getDisplayNameExpression() != null) {
			if (!isObjectEqual(left.getDisplayNameExpression(), right.getDisplayNameExpression())) {
				// Update id.
				left.setId(right.getId());
				rfcGenerator.modifyDisplayNameExpr(left);

			}
		}	
	
		if (left.getDescription() != null) {

			// Description.
			if (!isObjectEqual(left.getDescription(), right.getDescription())) {
				// Update id.
				left.setId(right.getId());

				rfcGenerator.modifyDescrption(left);
			}
		}
		
		// isBlueprint.
		if (!isObjectEqual(left.isTemplate(), right.isTemplate())) {
			// Update id.
			left.setId(right.getId());
			
			rfcGenerator.modifyTemplate(left);
		}

	}
	
	public void compare(CiBean parent, AttributeBean left, AttributeBean right) {
		
		if (right == null) {
			// Compare...
			if (left.isDerived()) {
				return;
			}
			rfcGenerator.addAttribute(parent, left);
			return;
		}
		if (left == null) {
			if (right.isDerived()) {
				return;
			}
			rfcGenerator.removeAttribute(parent, right);
			return;
		}
		
		
		if (left.getAlias() != null) {
			if (!isObjectEqual(left.getAlias(), right.getAlias())) {
				// Update id.
				left.setId(right.getId());

				rfcGenerator.modifyAttributeAlias(parent, left);
			}
		}
		if (left.getDisplayName() != null) {
			if (!isObjectEqual(left.getDisplayName(), right.getDisplayName())) {
				// Update id.
				left.setId(right.getId());

				rfcGenerator.modifyAttributeDisplayNameExpr(parent, left);
			}
		}
		if (left.getType() != null) {
			if (!isObjectEqual(left.getType(), right.getType())) {
				// Update id.
				left.setId(right.getId());

				// Modify attribute type.
				rfcGenerator.modifyAttributeType(parent, left);
			}
		}
		if (left.getRefType() != null) {
			if (!isObjectEqual(left.getRefType(), right.getRefType())) {
				// Update id.
				left.setId(right.getId());

				// Modify Reference type.
				rfcGenerator.modifyAttributeRefType(parent, left);
			}
		}
		if (left.getMaxOccurs() != null) {
			if (!isObjectEqual(left.getMaxOccurs(), right.getMaxOccurs())) {
				// Update id.
				left.setId(right.getId());

				// Modify Max Occurs.
				rfcGenerator.modifyMaxOccurs(parent, left);
			}
		}
		if (left.getMinOccurs() != null) {
			if (!isObjectEqual(left.getMinOccurs(), right.getMinOccurs())) {
				// Update id.
				left.setId(right.getId());

				// Modify Min Occurs.
				rfcGenerator.modifyMinOccurs(parent, left);
			}
		}
		if (left.getDescription() != null) {
			if (!isObjectEqual(left.getDescription(), right.getDescription())) {
				// Update id.
				left.setId(right.getId());
	
				// Modfiy Description.
				rfcGenerator.modifyAttributeDescription(parent, left);
			}
		}
	}

	public void compare(CiBean parent, List<ValueBean> left, List<ValueBean> right) {
		if (left == null) {
			// Remove all values.
			for (ValueBean vBean : right) {
				rfcGenerator.removeValue(parent, vBean);
			}
			return;
		}
		/*
		if (right == null) {
			// Remove all values.
			int index = 0;
			List<IRFC> rfcs = new ArrayList<IRFC>();
			for (ValueBean vBean : left) {
				rfcs.add(rfcGenerator.addValueRFC(parent, vBean));
				index++;
			}
			rfcGenerator.addValues(parent, rfcs);
			return;
		}
		*/
		
		List<IRFC> rfcs = new ArrayList<IRFC>();
		int appendIndex = 0;
		  
		for (int index = 0; index < left.size(); index++) {
			ValueBean leftValue = left.get(index);
			if (leftValue.getValue() == null) {
				continue;
			}
			// get right value.
			ValueBean rightValue = getValueBean(leftValue, right);
			if (rightValue != null) {
				if (leftValue.getValue().equals(rightValue.getValue())) {
					continue;
				}
				
				// Update id.
				leftValue.setId(rightValue.getId());
		
				// Modify Value.
				rfcs.add(rfcGenerator.getModifyValueRFC(parent, leftValue));
			} else {
				int offset = appendIndex;
				if (right != null) {
					if (left.size() > 1) {
						offset += right.size();
					} else {
						AttributeBean attributeTemplate = rfcGenerator.getAttributeTemplate(parent, leftValue.getAlias());
						
						if (attributeTemplate != null) {
							// 	Check if we only can have one value.
							if (attributeTemplate.fetchMaxOccursAsInt() != 1) {
								offset += right.size();
							}	
						}
					}	
				}	
				
				rfcs.add(rfcGenerator.getSetValueRFC(parent, leftValue, offset));
				appendIndex++;
			}
			continue;

		}
		rfcGenerator.addValues(parent, rfcs);
		/*
		// Handle delete
		for (ValueBean rightValue : right) {
			// get right value.
			ValueBean leftValue = getValueBean(rightValue, left);
			if (leftValue == null) {
				rfcGenerator.removeValue(parent, rightValue);
			}
		}
		*/
	
	}

	private ValueBean getValueBean(ValueBean value, List<ValueBean> list) {
		if (list == null) {
			return(null);
		}
		for (ValueBean valueInList : list) {
			Long id = valueInList.getId();
			if (id != null) {
				if (id.equals(value.getId())) {
					return(valueInList);
				}
			}
			// Compare values.
			if (value.getValue() != null) {
				if (value.getValue().equals(valueInList.getValue())) {
					return(valueInList);
				}
			}
		}
		return(null);
	}
	
	
	

	public void compareID(List<CiBean> local, List<CiBean> base) {
		HashMap<Long, CiBean> baseMap = new HashMap<Long, CiBean>();
		HashMap<Long, CiBean> localMap = new HashMap<Long, CiBean>();
		List<CiBean> newLocalCi = new ArrayList<CiBean>();
		List<CiBean> noIDBaseCi = new ArrayList<CiBean>();
		
		fillCiIDMap(baseMap, noIDBaseCi, base);
		if (noIDBaseCi.size() != 0) {
			throw new IllegalArgumentException("Base CI must all have ID!");
		}
		
		fillCiIDMap(localMap, newLocalCi, local);
		
		// Check for new CI's that have bean removed...
		for (Long localId : localMap.keySet()) {
			CiBean baseCi = baseMap.get(localId);
			CiBean localCi = localMap.get(localId);
			if (baseCi == null) {
				log.debug("Local CI ID:" + localId + " " + localCi);
				for (Long id : baseMap.keySet()) {
					log.debug("\tBase CI ID:" + id + " " + baseMap.get(id));
				}
				throw new IllegalArgumentException("Local CI can not have an ID if not found in base!");
			}
			if (localCi.getAlias().equals(baseCi.getAlias())) {
				// Check if CI exists in remote
				if (rfcGenerator.getScope().getRemoteBean(localCi.getAlias()) == null) {
					// Add new.
					newLocalCi.add(localCi);
					continue;
				}
			}
		}
		// New
		// Need to sort them so we create them in correct order.
		List<CiBean> sorted = sortNew(newLocalCi);
		for (CiBean newCi : sorted) {
			rfcGenerator.addCi(newCi);
		}
		
		// Deleted
		List delete = getRemovedItems(baseMap, localMap);
		for (Iterator iter = delete.iterator(); iter.hasNext();) {
			CiBean ciBean = (CiBean)iter.next();
			rfcGenerator.removeCi(ciBean);
		}
		
		// Modified 
		for (Long localId : localMap.keySet()) {
			CiBean baseCi = baseMap.get(localId);
			CiBean localCi = localMap.get(localId);
			if (baseCi == null) {
				log.debug("Local CI ID:" + localId + " " + localCi);
				for (Long id : baseMap.keySet()) {
					log.debug("\tBase CI ID:" + id + " " + baseMap.get(id));
				}
				throw new IllegalArgumentException("Local CI can not have an ID if not found in base!");
			}
			// check so we donät have a alias change.
			if (localCi.getAlias().equals(baseCi.getAlias())) {
				// Check if CI exists in remote
				if (rfcGenerator.getScope().getRemoteBean(localCi.getAlias()) == null) {
					// Ignore, already handled above...
					continue;
				}
			}
			
			// Compare internal fields.
			compare(localCi, baseCi);
			// Compare Attribute definitions.
			compareIDAttribute(localCi, localCi.getAttributes(), baseCi.getAttributes());
			// Compare values.
			compareIDValue(localCi, localCi.getAttributeValues(), baseCi.getAttributeValues());
				
		}
	}
	
	class TreeItem {
		CiBean bean;
		List<CiBean> children = new ArrayList();
	}
	
	private void insert(CiBean b, HashMap<String, CiBean> map, HashSet<String> handled, List<CiBean> sorted) {
		if (handled.contains(b.getAlias())) {
			return;
		}
		CiBean parent = map.get(b.getDerivedFrom());
		if (parent != null) {
			insert(parent, map, handled, sorted);
		}
		sorted.add(b);
		handled.add(b.getAlias());
	}
	
	private List<CiBean> sortNew(List<CiBean> newLocalCi) {
		HashMap<String, CiBean> map = new HashMap<String, CiBean>();
		
		// Buuld tree
		for (CiBean b : newLocalCi) {
			map.put(b.getAlias(), b);
		}
		List<CiBean> sorted = new ArrayList<CiBean>();
		
		// Need to add them in correct order, (derived from)
		HashSet<String> handled = new HashSet<String>();
		for(CiBean b : map.values()) {
			insert(b, map,handled, sorted);
		}
		return(sorted);
	}

	private void compareIDAttribute(CiBean parent, List<AttributeBean> local, List<AttributeBean> base) {
		HashMap<Long, AttributeBean> baseMap = new HashMap<Long, AttributeBean>();
		HashMap<Long, AttributeBean> localMap = new HashMap<Long, AttributeBean>();
		List<AttributeBean> newLocalAttribute = new ArrayList<AttributeBean>();
		List<AttributeBean> noIDBaseAttribute = new ArrayList<AttributeBean>();
		
		fillAttributeIDMap(baseMap, noIDBaseAttribute, base);
		if (noIDBaseAttribute.size() != 0) {
			throw new IllegalArgumentException("Base Attribute(s) must all have ID!");
		}
		fillAttributeIDMap(localMap, newLocalAttribute, local);
		
		// New
		for (AttributeBean newAttr : newLocalAttribute) {
			rfcGenerator.addAttribute(parent, newAttr);
		}
		
		// Deleted
		List delete = getRemovedItems(baseMap, localMap);
		for (Iterator iter = delete.iterator(); iter.hasNext();) {
			AttributeBean aBean = (AttributeBean)iter.next();
			rfcGenerator.removeAttribute(parent, aBean);
		}
		
		// Modified attributes.
		for (Long localId : localMap.keySet()) {
			AttributeBean baseAttr = baseMap.get(localId);
			AttributeBean localAttr = localMap.get(localId);
			if (baseAttr == null) {
				throw new IllegalArgumentException("Local Attribute can not have an ID if not found in base!");
			}
			compare(parent, localAttr, baseAttr);
		}
	}
	
	/**
	 * Compare using id as identifers.
	 * @param local
	 * @param base
	 * @return
	 */
	private void compareIDValue(CiBean parent, List<ValueBean> local, List<ValueBean> base) {
		HashMap<Long, ValueBean> baseMap = new HashMap<Long, ValueBean>();
		HashMap<Long, ValueBean> localMap = new HashMap<Long, ValueBean>();
		List<ValueBean> newLocalValues = new ArrayList<ValueBean>();
		List<ValueBean> noIDBaseValues = new ArrayList<ValueBean>();
		
		fillValueIDMap(baseMap, noIDBaseValues, base);
		if (noIDBaseValues.size() != 0) {
			throw new IllegalArgumentException("Base Values must all have ID!");
		}
		fillValueIDMap(localMap, newLocalValues, local);
		
		// Add new values.
		for (ValueBean newValue: newLocalValues) {
			rfcGenerator.addValue(parent, newValue);
		}
		// Deleted values.
		List delete = getRemovedItems(baseMap, localMap);
		for (Iterator iter = delete.iterator(); iter.hasNext();) {
			ValueBean vBean = (ValueBean)iter.next();
			rfcGenerator.removeValue(parent, vBean);
		}
		
		// Modified values.
		for (Long localId : localMap.keySet()) {
			ValueBean baseValue = baseMap.get(localId);
			ValueBean localValue = localMap.get(localId);
			if (baseValue == null) {
				throw new IllegalArgumentException("Local value can not have an ID if not found in base!");
			}
			if (!isObjectEqual(baseValue.getValue(), localValue.getValue())) {
				rfcGenerator.modifyValue(parent, localValue);
			}
		}
	}
	
	private List getRemovedItems(HashMap<Long, ? extends Object> base, HashMap<Long, ? extends Object> local) {
		List result = new ArrayList();
		// Delete values.
		for (Long baseId : base.keySet()) {
			Object value = local.get(baseId);
			if (value == null) {
				//Remove this value.
				result.add(base.get(baseId));
			}
		}
		return(result);
	}
	
	private void fillValueIDMap(HashMap<Long, ValueBean> vMap, List<ValueBean> unIdentifiedValues, List<ValueBean> values) {
		for (ValueBean value : values) {
			Long id = value.getId(); 
			if (id == null) {
				unIdentifiedValues.add(value);
			} else {
				vMap.put(id, value);
			}
		}
	}
	private void fillCiIDMap(HashMap<Long, CiBean> ciMap, List<CiBean> unIdentifiedCI, List<CiBean> cis) {
		for (CiBean ci : cis) {
			Long id = ci.getId(); 
			if (id == null) {
				unIdentifiedCI.add(ci);
			} else {
				ciMap.put(id, ci);
			}
		}
	}
	private void fillAttributeIDMap(HashMap<Long, AttributeBean> aMap, List<AttributeBean> unIdentifiedAttributes, List<AttributeBean> attributes) {
		for (AttributeBean attribute : attributes) {
			Long id = attribute.getId(); 
			if (id == null) {
				unIdentifiedAttributes.add(attribute);
			} else {
				aMap.put(id, attribute);
			}
		}
	}

}
