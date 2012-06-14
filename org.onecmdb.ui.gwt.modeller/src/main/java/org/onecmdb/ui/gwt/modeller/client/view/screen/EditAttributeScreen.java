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
package org.onecmdb.ui.gwt.modeller.client.view.screen;

import org.gwtiger.client.widget.panel.ButtonPanel;
import org.onecmdb.ui.gwt.modeller.client.model.TemplateCache;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeValidatePanel;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EditAttributeScreen extends AddAttributeScreen {
	
	
	public void onLoadComplete(Object sender) {
		setTitleText("Edit Attribute");
	}

	private String attrAlias;

	public void load() {
		if (this.template == null) {
			setErrorText("No template to inherit from is set!");
			return;
		}
		if (this.attrAlias == null) {
			setErrorText("No attribute to edit from is set!");
			return;
		}
		GWT_AttributeBean aBean = this.template.getAttribute(this.attrAlias);
		if (aBean == null) {
			setErrorText("Template " + template.getDisplayName() + " has no attribute with alias "+ this.attrAlias + "!");
			return;
		}
		
		
		alias.setValue(aBean.getAlias());
		displayName.setValue(aBean.getDisplayName());
		description.setValue(aBean.getDescription());
		complexSelector.setValue(aBean.isComplexType() ? "true" : "false");
		if (aBean.isComplexType()) {
			complexType.setValue(aBean.getType());
			refType.setValue(aBean.getRefType());
		} else {
			simpleType.setValue(aBean.getType());
		}
		minOccurs.setValue(aBean.getMinOccurs());
		maxOccurs.setValue(aBean.getMaxOccurs());
		
		setErrorText("");
		super.load();
		/*
		control = new AttributeValueInputControl();
		control.addAttributeValue(alias);
		control.addAttributeValue(displayName);
		control.addAttributeValue(description);
		control.addAttributeValue(complex);
		control.addAttributeValue(simpleType);
		control.addAttributeValue(complexType);
		control.addAttributeValue(refType);
		control.addAttributeValue(minOccurs);
		control.addAttributeValue(maxOccurs);
		
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Save");
		bPanel.addCancelButton("Cancel");
		
		bPanel.setCallback(this);
		vp = new AttributeValidatePanel(control);
		vp.load();
		vp.add(bPanel);
		
		vPanel.clear();
		vPanel.add(vp);
		*/
	}	
	
	protected void addDefaultValue(AttributeValueInputControl control) {
		
		/* TODO:....
		AttributeControl attributeControl = new AttributeControl();
		
		control.addAttributeValue(
				attributeControl.allocAttributeValue(template, this.template.getAttribute(this.attrAlias)
		));
		*/
		
	}

	
	protected ButtonPanel getButtonPanel() {
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Save");
		bPanel.addCancelButton("Cancel");
		bPanel.setCallback(this);
		return(bPanel);
	}

	public void load(String objectType, Long objectId) {
		// Object type must contain "templateAlias.attAlias"
		String split[] = objectType.split("\\.");
		if (split.length == 2) {
			String template = split[0];
			this.attrAlias = split[1];
			super.load(template, objectId);
		}
	}
	
	public void save() {
		
		GWT_CiBean copy = this.template.copy();
		GWT_AttributeBean aBean = copy.getAttribute(this.attrAlias);
		fillValues(aBean);
		
		copy.addAttribute(aBean);
		
		// Call update of attribute.
		OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(),
				new GWT_CiBean[] {copy},
				new GWT_CiBean[] {template},
				new AsyncCallback() {

					public void onFailure(Throwable caught) {
						setErrorText("ERROR:" +  caught);
						
					}

					public void onSuccess(Object result) {
						if (result instanceof GWT_RfcResult) {
							GWT_RfcResult rfcResult = (GWT_RfcResult)result;
							if (!rfcResult.isRejected()) {
								// Invalidate cache...
								TemplateCache.remove(template.getAlias());
								History.back();
								return;
							}
							setErrorText(rfcResult.getRejectCause());
							return;
						}
						setErrorText("Unknown result object!");
					}
				}
		);
	}

}
