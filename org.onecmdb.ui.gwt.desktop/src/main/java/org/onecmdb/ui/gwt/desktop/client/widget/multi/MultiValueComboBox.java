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
package org.onecmdb.ui.gwt.desktop.client.widget.multi;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.google.gwt.user.client.Element;

public class MultiValueComboBox extends TriggerField<ValueListModel> {

	private MultiValueMenu menu;
	private BaseEventPreview focusPreview;
	private String type;
	private AttributeColumnConfig config;
	private CMDBPermissions permissions;

	
	public MultiValueComboBox(AttributeColumnConfig config, String type, CMDBPermissions perm) {
		setTriggerStyle("x-form-date-trigger");
		setAutoValidate(false);
		this.config = config;
		this.type = type;
		this.permissions = perm;
	}
	
	  @Override
	public ValueListModel getValue() {
		  return(this.value);
	}

	/**
	   * Returns the field's date picker.
	   * 
	   * @return the date picker
	   */
	  public MultiValueGrid getMultiValueGrid() {
		  
	    if (menu == null) {
	    	menu = new MultiValueMenu(config, permissions);
	    	menu.addListener(Events.Select, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent ce) {
	          if (!menu.getMultiValueGrid().isCancel()) {
	        	  focusValue = value;
	        	  value = menu.getMultiValueGrid().getValue();
	        	  setValue(value);
	        	  fireChangeEvent(focusValue, value);

	        	  // Simulate enter key.
	        	  MultiEnterFieldEvent fe = new MultiEnterFieldEvent(MultiValueComboBox.this);
	        	  fireEvent(Events.SpecialKey, fe);
	          }
	          menu.hide();
	          el().blur();
	        }
	      });
	      menu.addListener(Events.Hide, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent be) {
	        	focus();
	        }
	      });
	    }
	    return menu.getMultiValueGrid();
	  }

/*
	  @Override
	  public DateFieldMessages getMessages() {
	    return (DateFieldMessages) messages;
	  }

	
	  @Override
	  public PropertyEditor getPropertyEditor() {
	    return (DateTimePropertyEditor) propertyEditor;
	  }
*/
	  
	  @Override
	  public void setRawValue(String value) {
	    super.setRawValue(value);
	  }

	  public void expand() {
		
	    MultiValueGrid grid = getMultiValueGrid();
	    grid.onOpen();
	    Object v = this.value;
	    if (v instanceof ValueListModel) {
	    	grid.setValue((ValueListModel)v);
	    }
	    menu.show(wrap.dom, "tl-bl?");
	    menu.focus();
	  }

	  @Override
	  protected void onBlur(final ComponentEvent ce) {
	    Rectangle rec = trigger.getBounds();
	    if (rec.contains(BaseEventPreview.getLastClientX(), BaseEventPreview.getLastClientY())) {
	      ce.stopEvent();
	      return;
	    }
	    if (menu != null && menu.isVisible()) {
	      return;
	    }
	    hasFocus = false;
	    doBlur(ce);
	  }

	  protected void onDown(FieldEvent fe) {
	    fe.cancelBubble();
	    if (menu == null || !menu.isAttached()) {
	      expand();
	    }
	  }

	  @Override
	  protected void onFocus(ComponentEvent ce) {
	    super.onFocus(ce);
	    focusPreview.add();
	  }

	  @Override
	  protected void onKeyPress(FieldEvent fe) {
	    super.onKeyPress(fe);
	    int code = fe.event.getKeyCode();
	    if (code == 8 || code == 9) {
	      if (menu != null && menu.isAttached()) {
	        menu.hide();
	      }
	    }
	  }

	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	    getInputEl().disable();
	    focusPreview = new BaseEventPreview();

	    new KeyNav<FieldEvent>(this) {
	      public void onDown(FieldEvent fe) {
	        MultiValueComboBox.this.onDown(fe);
	      }
	    };
	  }

	  @Override
	  protected void onTriggerClick(ComponentEvent ce) {
	    super.onTriggerClick(ce);
	    if (disabled || isReadOnly()) {
	      return;
	    }

	    expand();
	  }

	  @Override
	  @SuppressWarnings("deprecation")
	  protected boolean validateValue(String value) {
		 return(true); 
	  }

	  private void doBlur(ComponentEvent ce) {
	    if (menu != null && menu.isVisible()) {
	      menu.hide();
	    }
	    super.onBlur(ce);
	    focusPreview.remove();
	  }
}
