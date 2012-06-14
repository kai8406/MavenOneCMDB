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
import org.onecmdb.ui.gwt.modeller.client.control.TemplateAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIAttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.NewCIScreen;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AddTemplateScreen extends NewCIScreen implements ButtonCallback {
	
	public void onLoadComplete(Object sender) {
	}

	//protected AttributeValidatePanel vp;
	protected VerticalPanel vPanel;
	protected GWT_CiBean template;
	

	//protected AttributeValue alias = new AttributeValue("Alias", "xs:string", false, true, false);
	//protected AttributeValue displayName = new AttributeValue("Display Name Expr", "xs:string", false, true, false);
	//protected AttributeValue description = new AttributeValue("Description", "xs:string", false, true, false);
	
	TemplateAttributeFilter aFilter = new TemplateAttributeFilter();
	
	public AddTemplateScreen() {
		super();
		setTitleText("New Template");
		aFilter.setIsNew(true);
	}

	
	/*
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
	
	public void load() {
		if (this.template == null) {
			setErrorText("No template to inherit from is set!");
			return;
		}
		TextAttributeControl descCtrl = new TextAttributeControl("description", 
				false, 
				false, 
				TextAttributeControl.TEXT_AREA_TYPE,
				new Integer(5),
				null); 
		
		description.setCtrl(descCtrl);

		setErrorText("");
		control = new AttributeValueInputControl();
		control.addAttributeValue(alias);
		control.addAttributeValue(displayName);
		control.addAttributeValue(description);
		
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Add");
		bPanel.addCancelButton("Cancel");
		
		bPanel.setCallback(this);
		vp = new AttributeValidatePanel(control);
		vp.load();
		vp.add(bPanel);
		
		vPanel.clear();
		vPanel.add(vp);
	}	
	*/
	
	
	
	protected CellPanel getMainPanel() {
		vPanel = new VerticalPanel();
		return(vPanel);
	}


	public void save() {
		//this.control.getLocal().setDescription(aFilter.getDescription().getStringValue());
		this.control.getLocal().setAlias(aFilter.getAlias().getStringValue());
		this.control.getLocal().setDisplayNameExpression(aFilter.getDisplayName().getStringValue());
		
		super.save();
		/*
		GWT_CiBean newTemplate = new GWT_CiBean();
		newTemplate.setTemplate(true);
		newTemplate.setDerivedFrom(template.getAlias());
		newTemplate.setDescription(description.getStringValue());
		newTemplate.setAlias(alias.getStringValue());
		newTemplate.setDisplayNameExpression(displayName.getStringValue());
		
		// Call update of attribute.
		OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(),
				new GWT_CiBean[] {newTemplate},
				null,
				new AsyncCallback() {

					public void onFailure(Throwable caught) {
						setErrorText("ERROR:" +  caught);
						
					}

					public void onSuccess(Object result) {
						if (result instanceof GWT_RfcResult) {
							GWT_RfcResult rfcResult = (GWT_RfcResult)result;
							if (!rfcResult.isRejected()) {
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
		*/
	}

	
	
	
	
	protected CIAttributeValueInputControl getControl() {
		this.control = new CIAttributeValueInputControl(templateAlias, isNew());
		this.control.setIsNewTemplate(true);
		return(this.control);
	
	}

	public IAttributeFilter getAttributeFilter() {
		return(aFilter);
	}

	/*
	public boolean validate() {
		return(vp.validate());
	}
	*/

	public void close() {
		History.back();
	}

	public void clear() {
	}
	
}
