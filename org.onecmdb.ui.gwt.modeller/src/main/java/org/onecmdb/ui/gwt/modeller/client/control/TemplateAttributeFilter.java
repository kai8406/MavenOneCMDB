/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.modeller.client.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.AttributeComparator;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

public class TemplateAttributeFilter extends DefaultAttributeFilter {
	
	protected AttributeValue alias = new AttributeValue("Alias", "xs:string", false, true, false);
	protected AttributeValue displayName = new AttributeValue("Display Name Expr", "xs:string", false, true, false);
	//protected AttributeValue description = new AttributeValue("Description", "xs:string", false, true, false);
	private boolean isNew;

	
	public TemplateAttributeFilter() {
		super();
		/*
		TextAttributeControl descCtrl = new TextAttributeControl("description", 
				false, 
				false, 
				TextAttributeControl.TEXT_AREA_TYPE,
				new Integer(5),
				null); 
		description.setCtrl(descCtrl);
		*/
	}

	public List filterAttributes(GWT_CiBean template, GWT_CiBean bean) {
		List attributes = super.filterAttributes(template, bean);
		
		if (isNew()) {
			displayName.setValue(template.getDisplayNameExpression());
			//description.setValue(template.getDescription());
			addSuggestions(bean.isTemplate() ? bean : template, displayName);
		} else {
			displayName.setValue(bean.getDisplayNameExpression());
			//description.setValue(bean.getDescription());
			alias.setValue(bean.getAlias());
			
			addSuggestions(bean.isTemplate() ? bean : template, displayName);
		}
		attributes.add(1, alias);
		attributes.add(2, displayName);
		//attributes.add(3, description);
		
	
		return(attributes);
	}

	private void addSuggestions(GWT_CiBean bean, AttributeValue aValue) {
		
		List suggestions = new ArrayList();
		List sortedAttributes = new ArrayList(bean.getAttributes());
		Collections.sort(sortedAttributes, new AttributeComparator());
		for (Iterator iter = sortedAttributes.iterator(); iter.hasNext(); ) {
			GWT_AttributeBean aBean = (GWT_AttributeBean) iter.next();
			suggestions.add("${" + aBean.getAlias() +"}");
		}
		TextAttributeControl suggestCtrl = new TextAttributeControl("description", 
				false, 
				false, 
				TextAttributeControl.TEXT_SUGGEST_TYPE,
				null,
				suggestions); 
		
		aValue.setCtrl(suggestCtrl);
	}

	public boolean isNew() {		
		return (isNew);
	}
	
	public void setIsNew(boolean value) {
		this.isNew = value;
	}

	public AttributeValue getAlias() {
		return alias;
	}
	
	/*
	public AttributeValue getDescription() {
		return description;
	}
	*/

	public AttributeValue getDisplayName() {
		return displayName;
	}

	
	
	
	
}
