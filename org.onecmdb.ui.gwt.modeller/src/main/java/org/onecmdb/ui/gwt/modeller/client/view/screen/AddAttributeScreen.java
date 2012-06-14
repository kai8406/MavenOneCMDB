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

import org.gwtiger.client.widget.panel.ButtonCallback;
import org.gwtiger.client.widget.panel.ButtonPanel;
import org.onecmdb.ui.gwt.modeller.client.model.TemplateCache;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.IEventListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddAttributeScreen extends OneCMDBBaseScreen implements ButtonCallback, LoadListener {

	protected GWT_CiBean template;
	protected AttributeValidatePanel vp;
	protected VerticalPanel vPanel;

	protected AttributeValue alias = new AttributeValue("Alias", "xs:string", false, true, false);
	protected AttributeValue displayName = new AttributeValue("Display Name", "xs:string", false, true, false);
	protected AttributeValue description = new AttributeValue("Description", "xs:string", false, false, false);

	protected AttributeValue complexSelector = new AttributeValue("Complex Type", "xs:boolean", false, false, false);
	protected AttributeValue simpleType = new AttributeValue("Type", "xs:string", false, false, false);
	protected AttributeValue complexType = new AttributeValue("Type", "Root", true, true, false);
	protected AttributeValue refType = new AttributeValue("Reference Type", "Reference", true, false, false);
	protected AttributeValue maxOccurs = new AttributeValue("Max Occurs", "xs:string", false, true, false);
	protected AttributeValue minOccurs = new AttributeValue("Min Occurs", "xs:string", false, true, false);
	protected AttributeValueInputControl control;
	
	
	public AddAttributeScreen() {
		super();
		setTitleText("Add attribute");
		vPanel = new VerticalPanel();
		dockPanel.add(vPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		initWidget(dockPanel);
	}
	
	
	public void load() {
		if (this.template == null) {
			setErrorText("No template to add attribute is set!");
			return;
		}
		setErrorText("");
	
		TextAttributeControl descCtrl = new TextAttributeControl("description", 
				false, 
				false, 
				TextAttributeControl.TEXT_AREA_TYPE,
				new Integer(5),
				null); 
		
		TextAttributeControl simpleTypeCtrl = new TextAttributeControl("simpleType", 
				false, 
				false, 
				TextAttributeControl.TEXT_ENUM_TYPE,
				new Integer(1),
				OneCMDBUtils.getSimpleTypesAsList()
		);
	
		AttributeControl complexTypeCtrl = new AttributeControl();
		complexTypeCtrl.setSelectTemplate(true);
		AttributeControl refTypeCtrl = new AttributeControl();
		refTypeCtrl.setSelectTemplate(true);
		
		AttributeControl complexSelectorCtrl = new AttributeControl();
		complexSelectorCtrl.setEventListener(new IEventListener() {

			public void onEvent(Object listener, Object sender) {
				if (listener instanceof ClickListener) {
					updateTypeSelectors();
				
				}
			}
		});
		
		complexType.setCtrl(complexTypeCtrl);
		refType.setCtrl(refTypeCtrl);
		
		complexSelector.setCtrl(complexSelectorCtrl);
		simpleType.setCtrl(simpleTypeCtrl);
		description.setCtrl(descCtrl);
		
		control = new AttributeValueInputControl();
		control.addAttributeValue(alias);
		control.addAttributeValue(displayName);
		control.addAttributeValue(description);
		control.addAttributeValue(complexSelector);
		control.addAttributeValue(simpleType);
		control.addAttributeValue(complexType);
		control.addAttributeValue(refType);
		control.addAttributeValue(minOccurs);
		control.addAttributeValue(maxOccurs);
		addDefaultValue(control);
		ButtonPanel bPanel = getButtonPanel();
		
		vp = new AttributeValidatePanel(control);
		vp.addLoadListener(this);
		vp.load();
		vp.add(bPanel);
		
		vPanel.clear();
		vPanel.add(vp);
		
			
	}

	
	protected void addDefaultValue(AttributeValueInputControl control2) {
		
		
	}


	protected ButtonPanel getButtonPanel() {
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Add");
		bPanel.addCancelButton("Cancel");
		bPanel.setCallback(this);
		return(bPanel);
	}


	private void updateTypeSelectors() {
		if (complexSelector.getStringValue().equals("true")) {
			if (simpleType.getWidget() instanceof Widget) {
				((Widget)simpleType.getWidget()).setVisible(false);
			}
			if (complexType.getWidget() instanceof Widget) {
				((Widget)complexType.getWidget()).setVisible(true);
			}
			if (refType.getWidget() instanceof Widget) {
				((Widget)refType.getWidget()).setVisible(true);
			}
		
		} else {
			if (simpleType.getWidget() instanceof Widget) {
				((Widget)simpleType.getWidget()).setVisible(true);
			}
			if (complexType.getWidget() instanceof Widget) {
				((Widget)complexType.getWidget()).setVisible(false);
			}
			if (refType.getWidget() instanceof Widget) {
				((Widget)refType.getWidget()).setVisible(false);
			}
		}
	}
	
	public void load(final String objectType, Long objectId) {
		TemplateCache.load(objectType, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("Can't load alias " + objectType + " ERROR:" + caught.toString());
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					template = (GWT_CiBean)result;
					load();
					return;
				}
				setErrorText("Can't load alias " + objectType + " Unknown object:" + result);
			}
		});
	}
	
	protected void fillValues(GWT_AttributeBean aBean) {
		aBean.setAlias(alias.getStringValue());
		aBean.setDisplayName(displayName.getStringValue());
		aBean.setDescription(description.getStringValue());
		aBean.setComplexType(complexSelector.getStringValue().equals("true"));
		if (aBean.isComplexType()) {
			aBean.setType(complexType.getStringValue());
			aBean.setRefType(refType.getStringValue());
		} else {
			aBean.setType(simpleType.getStringValue());
		}
		aBean.setMaxOccurs(maxOccurs.getStringValue());
		aBean.setMinOccurs(minOccurs.getStringValue());
	}
		
	public void save() {
		GWT_AttributeBean aBean = new GWT_AttributeBean();
		fillValues(aBean);
		GWT_CiBean copy = this.template.copy();
		copy.addAttribute(aBean);
		
		
		// Call update of attribute.
		OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(),
				new GWT_CiBean[] {copy},
				null,
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

	public boolean validate() {
		if (complexSelector.getStringValue().equals("true")) {
			complexType.getCtrl().setRequiered(true);
			refType.getCtrl().setRequiered(true);
			simpleType.getCtrl().setRequiered(false);
		} else {
			complexType.getCtrl().setRequiered(false);
			refType.getCtrl().setRequiered(false);
			simpleType.getCtrl().setRequiered(true);
		}
		return(vp.validate());
	}

	public void close() {
		History.back();
	}

	public void clear() {
	}


	public void onLoadComplete(Object sender) {
		updateTypeSelectors();
	}


	public void onLoadFailure(Object sender, Throwable caught) {
	}


	public void onLoadStart(Object sender) {
	}
	
}
