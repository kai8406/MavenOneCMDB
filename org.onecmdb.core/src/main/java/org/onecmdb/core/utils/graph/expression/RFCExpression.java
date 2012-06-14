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
package org.onecmdb.core.utils.graph.expression;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.onecmdb.core.internal.ccb.rfc.RFC;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCNewCi;
import org.onecmdb.core.utils.graph.query.selector.ItemRFCSelector;

public class RFCExpression extends OneCMDBExpression {
	private String type = ItemRFCSelector.RFC_ANY_TYPE;
	
	private Long txId;
	
	
	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}

	public DetachedCriteria getCriteria() {
		
		DetachedCriteria crit = null;
		if (type == null) {
			type = ItemRFCSelector.RFC_ANY_TYPE;
		}
		
		if (type.equals(ItemRFCSelector.RFC_ANY_TYPE)) {
			crit = DetachedCriteria.forClass(RFC.class);	
		} else if (type.equals(ItemRFCSelector.RFC_MODIFY_VALUE_TYPE)) {
			crit = DetachedCriteria.forClass(RFCModifyAttributeValue.class);
		} else if (type.equals(ItemRFCSelector.RFC_NEW_CI_TYPE)) {
			crit = DetachedCriteria.forClass(RFCNewCi.class);
		}
		
		
		
		//crit.addOrder( Order.desc("ts") );
		if (this.txId != null) {
			crit.add(Expression.eq("txId", this.txId));
		}
		return(crit);
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
