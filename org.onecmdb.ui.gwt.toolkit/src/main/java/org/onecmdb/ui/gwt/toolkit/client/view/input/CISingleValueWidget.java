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
package org.onecmdb.ui.gwt.toolkit.client.view.input;

import org.gwtiger.client.widget.field.BaseFieldLabelWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeLoader;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.ISelectListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.control.select.SelectInheritanceDataSourceControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.NullCIBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.SelectCIPopup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

public class CISingleValueWidget extends BaseFieldLabelWidget implements IBaseField {

	private AttributeValue value;

	public CISingleValueWidget(final AttributeValue value) {
		super(value.getLabel());
		
		this.value = value;
		this.value.setWidget(this);
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		/*
		CIIconWidget icon = new CIIconWidget(value.getType());
		panel.add(icon);
		*/
		super.setRequired(value.getCtrl().isRequiered());
		hPanel.add(new CIIconWidget(value.getType()));
		final CIDisplayNameWidget valueWidget = new CIDisplayNameWidget();
		valueWidget.setAlias(value.getStringValue());
		
		if (value.getCtrl().getClickListener() != null) {
			valueWidget.setListener(value.getCtrl().getClickListener());
		} 
		valueWidget.setLoadListener(new LoadListener() {

			public void onLoadComplete(Object sender) {
				if (sender instanceof GWT_CiBean) {
					value.setValueAsCI((GWT_CiBean)sender);
				}
				
			}

			public void onLoadFailure(Object sender, Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onLoadStart(Object sender) {
				// TODO Auto-generated method stub
				
			}
			
		});
		valueWidget.update();
		
		hPanel.add(valueWidget);
		hPanel.setCellWidth(valueWidget, "100%");
		hPanel.setCellHorizontalAlignment(valueWidget, HorizontalPanel.ALIGN_LEFT);
		
		if (value.getCtrl() instanceof IAttributeLoader) {
			IAttributeLoader aLoader = (IAttributeLoader)value.getCtrl();
			aLoader.load(new AsyncCallback() {

				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}

				public void onSuccess(Object result) {
					if (result instanceof GWT_CiBean) {
						valueWidget.load((GWT_CiBean)result);
						CISingleValueWidget.this.value.setValueAsCI(((GWT_CiBean)result));
						CISingleValueWidget.this.validate();
					}
				}
			});
		}
		
		if (!value.getCtrl().isReadonly()) {
			/*
			final Image delete = new Image("images/delete-value.gif");
			delete.setTitle("Reset this value");
			delete.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					valueWidget.load(null);
					CISingleValueWidget.this.value.setValueAsCI(null);
					CISingleValueWidget.this.validate();
				}
				
			});
			*/
			final Image change = new Image("images/eclipse/view_menu.gif");
			change.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					SelectInheritanceDataSourceControl ctrl = 
						new SelectInheritanceDataSourceControl(CISingleValueWidget.this.value.getType());
					ctrl.setFilterInstances(new Boolean(CISingleValueWidget.this.value.getCtrl().isSelectTemplate()));
					ctrl.setSelectInstances(CISingleValueWidget.this.value.getCtrl().isSelectTemplate());
					ctrl.setRequiered(CISingleValueWidget.this.value.getCtrl().isRequiered());
					ctrl.setShowSearch(true);
					
					String title = "Select a Instance";
					if (CISingleValueWidget.this.value.getCtrl().isSelectTemplate()) {
						title = "Select a Template";
					}
					
					final SelectCIPopup popup = new SelectCIPopup(title, ctrl);
					ctrl.setSelectListener(new ISelectListener() {

						public void onSelect(Object selected) {
							if (selected instanceof GWT_CiBean) {
								if (selected instanceof NullCIBean) {
									valueWidget.load(null);
									CISingleValueWidget.this.value.setValueAsCI(null);
								} else {
									valueWidget.load((GWT_CiBean)selected);
									CISingleValueWidget.this.value.setValueAsCI((GWT_CiBean)selected);
								}
								
								CISingleValueWidget.this.validate();
								popup.hide();
							}
						}
					});
					int top = getBaseField().getAbsoluteTop() + getBaseField().getOffsetHeight() + 2;
					int left = getBaseField().getAbsoluteLeft() + 8;
					popup.setPopupPosition(left, top);  
			
					popup.show();
				}
			});
			hPanel.add(change);
			hPanel.setCellHorizontalAlignment(change, HorizontalPanel.ALIGN_RIGHT);
		}
		
		// Create another panel to be able to set with to 100%
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(hPanel);
		addField(panel);
		if (value.getCtrl().isReadonly()) {
			panel.setStyleName("mdv-form-input-readonly");
		}
	}
	
	
	public Widget getBaseField() {
		return(this.baseField);
	}
	
	public void clear() {
	}

	public boolean validate() {
		setRequired(value.getCtrl().isRequiered());

		if (isRequired()) {
			// Need to have entered a value.
			if (value.getStringValue() == null) {
				showError("'" + getLabel() + "' is required", true);
				return(false);
			}
		}
		
		showError(false);
		return(true);
	}

	public void addKeyboardListener(KeyboardListener listener) {
	}
}
