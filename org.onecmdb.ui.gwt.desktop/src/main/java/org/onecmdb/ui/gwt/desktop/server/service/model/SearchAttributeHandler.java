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
package org.onecmdb.ui.gwt.desktop.server.service.model;

import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;

/**
 * Class for handle simple text search.<br>
 * Functions supported.<br>
 * #see parse()
 * 
 */
public class SearchAttributeHandler {
	
	/**
	 * Parse a search expression:<br>
	 * expr  gives like %text%<br>
	 * expr* gives like text%<br>
	 * "expr" gives eq text<br>
	 * id==expr  gives specific attribute, and expr as above.
	 * 
	 * @param search
	 * @return
	 */
	public static AttributeValueConstraint parse(String search, Integer op) {
		AttributeValueConstraint vCon = new AttributeValueConstraint();
		if (search == null) {
			return(vCon);
		}
		String alias = null;
		String expr = search;
		if (search.contains("==")) {
			String split[] = search.split("==");
			alias = split[0];
			expr = split[1];
			
			// Handle .
			int index = alias.indexOf(".");
			if (index > 0) {
				alias = alias.substring(index+1);
			}
			if (alias.startsWith(CIModel.VALUE_PREFIX)) {
				alias = alias.substring(CIModel.VALUE_PREFIX.length());
			}
			vCon.setAlias(alias);
		}
		handleExpression(vCon, expr, op);
		return(vCon);
		
	}
	protected static void handleExpression(AttributeValueConstraint vCon, String expr, Integer op) {
		expr = expr.trim();
		if (op != null && op == AttributeValueConstraint.EQUALS) {
			vCon.setValue(expr);
			return;
		}
		if (expr.startsWith("\"") && expr.endsWith("\"")) {
			vCon.setOperation(AttributeValueConstraint.EQUALS);
			expr = expr.replace('"', ' ');
			expr = expr.trim();
		} else {
			vCon.setOperation(AttributeValueConstraint.LIKE);
			if (expr.contains("*")) {
				expr = expr.replace('*', '%');
			} else {
				expr = "%" + expr + "%";
			}
		}
		vCon.setValue(expr);
	}

}
