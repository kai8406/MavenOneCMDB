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

import java.util.Iterator;
import java.util.List;

import org.gwtiger.client.widget.field.BaseFieldLabelWidget;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AbstractAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.MultipleAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.ReferenceAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.ISelectListener;
import org.onecmdb.ui.gwt.toolkit.client.control.select.SelectMultipleDataSourceControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewCheckboxFiledWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewDateFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewDateTimeFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewIntegerFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewLabelFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewPasswordFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewSuggestTestField;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewTextAreaFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewTextFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewTextListFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewURIFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.SelectCIPopup;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.TooltipPopup;
import org.onecmdb.ui.gwt.toolkit.client.view.table.ColumnHeaderWidget;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AttributeRender implements IAttributeRender {

	
	public static final int MAX_TABLE_COLUMN_CHARS = 30;
	
	private boolean isNew;

	public Widget getColumnWidget(Object column) {
		if (column instanceof MultipleAttributeValue) {
			MultipleAttributeValue mValue = (MultipleAttributeValue)column;
			return(new MultiValueTableWidget(mValue));
		}
		if (column instanceof ReferenceAttributeValue) {
			return(new ReferenceValueWidget((ReferenceAttributeValue)column));
		}
		if (column instanceof AttributeValue) {
			// Create Header widget.
			AttributeValue attr = (AttributeValue)column;
			if (attr.isComplex()) {
				return(new CIDisplayNameWidget(attr.getStringValue()));
			} else {
				
				if (attr.getType().equals("xs:password")) {
					attr.getCtrl().setReadonly(true);
					NewPasswordFieldWidget textArea = new NewPasswordFieldWidget(attr);
					textArea.getBaseField().setWidth("5em");
					return(textArea);
				}
				
				if (attr.getType().equals("xs:boolean")) {
					if ("true".equals(attr.getStringValue())) {
						Image image = new Image("images/true.gif");
						image.setTitle("true");
						return(image);
					}
					Label l = new Label(" ");
					l.setTitle("false");
					return(l);
				}
				
				String value = attr.getStringValue();
				String concatValue = value;
				if (!attr.isNullValue()) {
					if (value.length() > MAX_TABLE_COLUMN_CHARS) {
						concatValue = value.substring(0, (MAX_TABLE_COLUMN_CHARS-3));
						concatValue += "...";
					}
				}
				Label label = new Label(concatValue, false);
				label.setTitle(value);
				return(label);
			}
		}
		return(new Label("<unknown>"));
	}
		
	public Widget getColumnHeaderWidget(Object header) {
		if (header instanceof AbstractAttributeValue) {
			
			// Create Header widget.
			AbstractAttributeValue attr = (AbstractAttributeValue)header;
		
			if (true) {
				return(new ColumnHeaderWidget(attr));
			}
			
			if (true) {
				Label name = new Label(attr.getDisplayName());
				return(name);
			}
		
			VerticalPanel panel = new VerticalPanel();
			
			if (attr.isComplex()) {
				// Icon
				CIIconWidget icon = new CIIconWidget(attr.getType());
				icon.update();
				panel.add(icon);
				panel.setCellHorizontalAlignment(icon, HorizontalPanel.ALIGN_LEFT);
			}
			
			// DisplayName.
			Label name = new Label(attr.getDisplayName());
			
			panel.add(name);
			
			panel.setCellHorizontalAlignment(name, HorizontalPanel.ALIGN_LEFT);
			return(panel);
		}
		return(new Label("<unknown>"));
	}	
	
	public Widget getWidget(Object object) {
		Widget widget = null;
		if (object instanceof MultipleAttributeValue) {
			final MultipleAttributeValue mValue = (MultipleAttributeValue)object;
			final CIMultiValueWidget multi = new CIMultiValueWidget(mValue);
			
			multi.setAddListener(new ClickListener() {

				public void onClick(Widget sender) {
					if (!mValue.isComplex()) {
						// Add a new value.
						AttributeValue newValue = mValue.newAttributeValue("");
						mValue.addAttributeValue(newValue);
						multi.addValue(getSingleValueInput(newValue));
						return;
					}
					SelectMultipleDataSourceControl ctrl = new SelectMultipleDataSourceControl(mValue);
					final SelectCIPopup popup = new SelectCIPopup("Select Multiple Instances", ctrl);
					ctrl.setSelectListener(new ISelectListener() {

						public void onSelect(Object selected) {
							if (selected instanceof List) {
								mValue.setAttributeValues((List)selected);
								multi.clearValues();
								for (Iterator iter = mValue.getAttributeValues().iterator(); iter.hasNext(); ) {
									AttributeValue aValue = (AttributeValue) iter.next();
									multi.addValue(getSingleValueInput(aValue));
								}								
								popup.hide();
							}
						}
					});
					int top = multi.getBaseField().getAbsoluteTop() + multi.getBaseField().getOffsetHeight() + 2;
					int left = multi.getBaseField().getAbsoluteLeft() + 8;
					popup.setPopupPosition(left, top);  
					popup.show();
				}
				
			});
			for (Iterator iter = mValue.getAttributeValues().iterator(); iter.hasNext(); ) {
				AttributeValue aValue = (AttributeValue) iter.next();
				if (isNew()) { 
					if (!aValue.isNullValue()) {
						multi.addValue(getSingleValueInput(aValue));
					}
				} else {
					multi.addValue(getSingleValueInput(aValue));
				}
					
			}
			
			widget = multi;
		} else if (object instanceof AttributeValue) {
			widget = getSingleValueInput((AttributeValue)object);
		}
		
		if (object instanceof AbstractAttributeValue) {
			Image image = new Image("images/eclipse/widget_closed.gif");
			ValidateHorizontalPanel hPanel = new ValidateHorizontalPanel(widget);
			hPanel.add(image);
			hPanel.add(widget);
			hPanel.setCellVerticalAlignment(image, VerticalPanel.ALIGN_MIDDLE);
			/*
			widget.setWidth("100%");
			hPanel.setCellWidth(widget, "100%");
			hPanel.setCellHorizontalAlignment(widget, HorizontalPanel.ALIGN_LEFT);
			*/
			
			new TooltipPopup(image, getAttributeTitle((AbstractAttributeValue)object));
			((AbstractAttributeValue)object).setWidget(hPanel);
			return(hPanel);
		}
		
		return(widget);
	}
	
	
	private String getAttributeTitle(AbstractAttributeValue value) {
		StringBuffer b = new StringBuffer();
		b.append("<b>Alias</b><br/>");
		b.append(value.getAlias());
		b.append("<br/>");
		b.append("<br/>");
		b.append("<b>Description</b><br>");
		b.append(value.getDescription());
		return(b.toString());
	}

	public BaseFieldLabelWidget getSingleValueInput(AttributeValue value) {
		if (value.isComplex()) {
			return(new CISingleValueWidget(value));
		}
		/*
		if (value.getCtrl().isReadonly()) {
			if (value.getCtrl() instanceof TextAttributeControl) {
				TextAttributeControl txtCtrl = (TextAttributeControl)value.getCtrl();
				if (TextAttributeControl.TEXT_AREA_TYPE.equals(txtCtrl.getTextType())) {
					NewTextAreaFieldWidget textArea = new NewTextAreaFieldWidget(value);
					return(textArea);
				}
				if (TextAttributeControl.TEXT_BOX_TYPE.equals(txtCtrl.getTextType())) {
					NewTextFieldWidget textArea = new NewTextFieldWidget(value);
					return(textArea);
				}
				if (TextAttributeControl.TEXT_PASSWORD_TYPE.equals(txtCtrl.getTextType())) {
					NewPasswordFieldWidget textArea = new NewPasswordFieldWidget(value);
					return(textArea);
				}
			}
			if ("xs:password".equals(value.getType())) {
				NewPasswordFieldWidget textArea = new NewPasswordFieldWidget(value);
				return(textArea);
			}
		
			NewLabelFieldWidget labelField = new NewLabelFieldWidget(value);
			return(labelField);
		}
		*/
		
		if (OneCMDBUtils.STRING_TYPE.equals(value.getType())) {
			// Check for special text.
			if (value.getCtrl() instanceof TextAttributeControl) {
				TextAttributeControl txtCtrl = (TextAttributeControl)value.getCtrl();
				if (TextAttributeControl.TEXT_AREA_TYPE.equals(txtCtrl.getTextType())) {
					NewTextAreaFieldWidget textArea = new NewTextAreaFieldWidget(value);
					return(textArea);
				}
				if (TextAttributeControl.TEXT_BOX_TYPE.equals(txtCtrl.getTextType())) {
					NewTextFieldWidget textArea = new NewTextFieldWidget(value);
					return(textArea);
				}
				if (TextAttributeControl.TEXT_PASSWORD_TYPE.equals(txtCtrl.getTextType())) {
					NewPasswordFieldWidget textArea = new NewPasswordFieldWidget(value);
					return(textArea);
				}
				if (TextAttributeControl.TEXT_ENUM_TYPE.equals(txtCtrl.getTextType())) {
					NewTextListFieldWidget textArea = new NewTextListFieldWidget(value);
					return(textArea);
				}
				if (TextAttributeControl.TEXT_SUGGEST_TYPE.equals(txtCtrl.getTextType())) {
					NewSuggestTestField textArea = new NewSuggestTestField(value);
					return(textArea);
				}
				
				
			}
			
			NewTextFieldWidget textField = new NewTextFieldWidget(value);
			return(textField);
			
		}
		
		if (OneCMDBUtils.BOOLEAN_TYPE.equals(value.getType())) {
			NewCheckboxFiledWidget checkBox = new NewCheckboxFiledWidget(value);
			return(checkBox);
		}
		if (OneCMDBUtils.PASSWORD_TYPE.equals(value.getType())) {
			NewPasswordFieldWidget textArea = new NewPasswordFieldWidget(value);
			return(textArea);
		}
		if (OneCMDBUtils.ANYURI_TYPE.equals(value.getType())) {
			NewURIFieldWidget textArea = new NewURIFieldWidget(value);
			return(textArea);
		}
		if (OneCMDBUtils.INTEGER_TYPE.equals(value.getType())) {
			NewIntegerFieldWidget textArea = new NewIntegerFieldWidget(value);
			return(textArea);
			
		}
		if (OneCMDBUtils.DATE_TYPE.equals(value.getType())) {
			NewDateFieldWidget textArea = new NewDateFieldWidget(value);
			return(textArea);
			
		}
		if (OneCMDBUtils.DATETIME_TYPE.equals(value.getType())) {
			NewDateTimeFieldWidget textArea = new NewDateTimeFieldWidget(value);
			return(textArea);
			
		}
	
		/*
			case "xs:string":
			case "xs:password":
			case "xs:boolean":
			case "xs:integer":
			case "xs:date":
		*/		
		
		
		NewLabelFieldWidget labelField = new NewLabelFieldWidget(value);
		return(labelField);
		
	}
	
	/**
	 * Convert input widgets to GWT_ValueBean(s) as a long list.
	 * 
	 * @return
	 */
	/*
	public List getValues() {
		List list = new ArrayList();
	
		for (Iterator iter = widgetToObject.keySet().iterator(); iter.hasNext();) {
			
			Object o = iter.next();
			if (o instanceof IBaseField) {
				IBaseField baseField = (IBaseField)o;
				MultipleAttributeValue aValue = (MultipleAttributeValue)widgetToObject.get(baseField);
				Object value = baseField.getValue();
				convertValue(list, aValue.getAttributeBean(), value);
			}
		}
		return(list);
	}
	*/

	private void convertValue(List result, GWT_AttributeBean aBean, Object value) {
		if (value instanceof String) {
			result.add(allocValue(aBean, (String)value));
		}
		if (value instanceof List) {
			for (Iterator iter = ((List)value).iterator(); iter.hasNext();) {
				convertValue(result, aBean, iter.next());
			}
		}
	}
	
	private GWT_ValueBean allocValue(GWT_AttributeBean aBean, String value) {
		System.out.println("ALLOCATE VALUE: " + aBean.getAlias() + "=" + value);
		GWT_ValueBean vBean = new GWT_ValueBean();
		vBean.setAlias(aBean.getAlias());
		vBean.setComplexValue(aBean.isComplexType());
		vBean.setValue(value);
		return(vBean);
	}

	public void setIsNew(boolean value) {
		this.isNew = value;
	}

	public boolean isNew() {
		return(this.isNew);
	}
	/*
	public Widget getTabelCellWidget(Object colObject, boolean b) {
		if (colObject instanceof MultipleAttributeValue) {
			MultipleAttributeValue aValue = (MultipleAttributeValue)colObject;
			String value = aValue.getSingleValue();
			if (value != null) {
				if (aValue.isComplex()) {
					return(new CIDisplayNameWidget(value));
				} else {
					return(new Label(aValue.getSingleValue()));
				}
			}
			return(new Label("<empty>"));
		}
		return(new Label("Unkown Object"));
	}
	*/
	

}
