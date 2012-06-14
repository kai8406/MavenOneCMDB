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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gwtiger.client.widget.field.BaseFieldLabelWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AbstractAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IBaseField;
import org.onecmdb.ui.gwt.toolkit.client.control.input.MultipleAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.DragablePopup;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CIMultiValueWidget extends BaseFieldLabelWidget implements IBaseField {

	
	private VerticalPanel vPanel;
	private Image addImage;
	private List baseFields = new ArrayList();
	private VerticalPanel valuePanel;
	private MultipleAttributeValue mValue;
	private boolean expandState;
	private Image expandImage;
	
	public CIMultiValueWidget(final MultipleAttributeValue mValue) {
		super(mValue.getLabel());
		
		vPanel = new VerticalPanel();
		valuePanel = new VerticalPanel();
		this.mValue = mValue;
		
		//Widget hPanel = getMultiListAsDisclosure(mValue);
		//Widget hPanel = getMultiListAsPopup(mValue);
		Widget hPanel = getMultiListAsList(mValue);
		addField(hPanel);
		if (mValue.getCtrl().isReadonly()) {
			// Reset basefield style.
			getBaseField().setStyleName("mdv-form-input-readonly");
		}
	}
	
	protected Widget getMultiListAsList(final MultipleAttributeValue mValue) {
		final HorizontalPanel hPanel = new HorizontalPanel();
		CIIconWidget icon = new CIIconWidget(mValue.getType());
		icon.update();
		hPanel.setWidth("100%");
		hPanel.add(icon);
		hPanel.setCellHorizontalAlignment(icon, HorizontalPanel.ALIGN_LEFT);
		Label name = new Label(mValue.getDisplayName());
		hPanel.add(name);
		HorizontalPanel actionPanel = new HorizontalPanel();
		actionPanel.setSpacing(6);
		
			
		if (!mValue.getCtrl().isReadonly()) {
			// Add action
			if (mValue.isComplex()) {
				addImage = new Image("images/eclipse/add_multi.gif");
				addImage.setTitle("Select values to the multi valued attribute");
			} else {
				addImage = new Image("images/eclipse/add_single.gif");
				addImage.setTitle("Add one value to the multi valued attribute");
			}
			actionPanel.add(addImage);
			
			// Delete all action
			Image deleteAllImage = new Image("images/delete-value.gif");
			deleteAllImage.setTitle("Remove ALL values!");
			actionPanel.add(deleteAllImage);
			deleteAllImage.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					if (Window.confirm("Remove all values for attribute?")) {
						mValue.removeAttributeValues();
						clearValues();
					}
				}
				
			});
			actionPanel.add(deleteAllImage);
		}
	
		// Expand action
		expandImage = new Image();
		expandImage.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				setExpandState(!expandState);
			}
		});
		setExpandState(true);
	
		actionPanel.add(expandImage);
		actionPanel.setCellVerticalAlignment(expandImage, VerticalPanel.ALIGN_MIDDLE);
	
		hPanel.add(actionPanel);
		hPanel.setCellHorizontalAlignment(actionPanel, HorizontalPanel.ALIGN_RIGHT);
		vPanel.add(hPanel);
		vPanel.add(valuePanel);
		return(vPanel);
	}
	
	private void setExpandState(boolean state) {
		expandState = state;
		if (state) {
			expandImage.setUrl("images/minus.gif");
			expandImage.setTitle("Click to hide all values");
			valuePanel.setVisible(true);
		} else {
			expandImage.setUrl("images/plus.gif");
			expandImage.setTitle("Click to show all values");
			valuePanel.setVisible(false);
		}
	}

	public void addValue(final Widget widget) {
		// need to get the base widget...
		if (widget instanceof IBaseField) {
			final HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.add(((IBaseField)widget).getBaseField());
			
			
			valuePanel.add(hPanel);
			if (mValue.getCtrl().isReadonly()) {
				((IBaseField)widget).getBaseField().setStyleName("multi-value-input-readonly");
			} else {
				Image delete = new Image("images/delete-value.gif");
				delete.setTitle("Remove this value");
				delete.addClickListener(new ClickListener() {

					public void onClick(Widget sender) {
						List values = mValue.getAttributeValues();
						List remainingValues = new ArrayList();
						for (Iterator iter = values.iterator(); iter.hasNext();) {
							AttributeValue aValue = (AttributeValue) iter.next();
							if (widget.equals(aValue.getWidget())) {
								// Remove this, 
								mValue.removeAttributeValue(aValue);
								valuePanel.remove(hPanel);
								break;
							}
						}
					}
					
				});

				hPanel.add(delete);
				hPanel.setCellWidth(((IBaseField)widget).getBaseField(), "100%");
				//((IBaseField)widget).getBaseField().setStyleName("multi-value-input");
				((IBaseField)widget).getBaseField().setStyleName("multi-value-input-inside");
				hPanel.setStyleName("multi-value-input");
			}
			baseFields.add(widget);
			
		}
	}

	public void clear() {
	}
	
	public boolean validate() {
		return true;
	}

	public void addKeyboardListener(KeyboardListener listener) {
	}

	public Widget getBaseField() {
		return(this.baseField);
	}

	public void setReadonly(boolean readonly) {
		// TODO Auto-generated method stub
	}
	
	public void setAddListener(final ClickListener listener) {
		if (addImage != null) {
			addImage.addClickListener(new ClickListener() {
				public void onClick(Widget sender) {
					listener.onClick(sender);
				}
			});
		}
	}

	public void clearValues() {
		valuePanel.clear();
	}

	

}
