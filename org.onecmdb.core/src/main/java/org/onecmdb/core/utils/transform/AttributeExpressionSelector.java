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
package org.onecmdb.core.utils.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttributeExpressionSelector extends AAttributeSelector {
	private String expression;
	private List<AAttributeSelector> selectors;
	
	
	public AttributeExpressionSelector(String name) {
		setName(name);
	}
	
	public AttributeExpressionSelector() {
	}



	public String getExpression() {
		return expression;
	}

	

	public List<AAttributeSelector> getSelector() {
		return selectors;
	}

	public void setSelector(List<AAttributeSelector> selectors) {
		this.selectors = selectors;
	}
	
	public void addSelector(AAttributeSelector sel) {
		if (this.selectors == null) {
			this.selectors = new ArrayList<AAttributeSelector>();
		}
		this.selectors.add(sel);
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public IAttributeValue getAttribute(IInstance row) throws IOException {
		TextAttributeValue value = new TextAttributeValue(this, expression);
	
		if (this.selectors != null) {
			// Build hash map.
			HashMap<String, String> map = new HashMap<String, String>();
			for (AAttributeSelector a : this.selectors) {
				IAttributeValue attrValue = a.getAttribute(row);
				map.put(a.getName(), attrValue.getText());
			}
			String expr = expression;
			for (String key : map.keySet()) {
				String replValue = map.get(key);
				if (replValue == null) {
					replValue = "";
				}
				expr = expr.replace("{" + key + "}", replValue);
			}
			value.setText(expr);
		}
		
		return(value);
	}

}
