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
package org.onecmdb.ui.gwt.desktop.client.fixes.combo;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class AdaptableTriggerField<M> extends TriggerField<M> {

	private AdaptableMenu menu;
	private BaseEventPreview focusPreview;
	
	public AdaptableTriggerField(Component comp, String style) {
		if (!(comp instanceof IValueComponent)) {
			throw new IllegalArgumentException("Component must implement IValueComponent");
		}
		
		setTriggerStyle("x-form-date-trigger");
		setAutoValidate(false);
		
		this.menu = new AdaptableMenu(comp, style);
		
		menu.addListener(Events.Select, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent ce) {
	          focusValue = value;
	          value = getValue();
	          fireChangeEvent(focusValue, value);
	          
	          // Simulate enter key.
	          KeyEnterEvent fe = new KeyEnterEvent(AdaptableTriggerField.this);
	          fireEvent(Events.SpecialKey, fe);
	         
	          AdaptableTriggerField.this.menu.hide();
	          el().blur();
	        }
	      });
	      menu.addListener(Events.Hide, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent be) {
	        	KeyEscEvent fe = new KeyEscEvent(AdaptableTriggerField.this);
	 	        fireEvent(Events.SpecialKey, fe);
	 	        	
	          //focus();
	        }
	      });
	}
	
	  @Override
	public M getValue() {
		  Object v = ((IValueComponent) menu.getComponent()).getValue();
		  return((M)v);
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
	public void setValue(M value) {
		super.setValue(value);
		 ((IValueComponent) menu.getComponent()).setValue(this.value);
	  }

	@Override
	  public void setRawValue(String value) {
		  if (rendered) {
			  getInputEl().setValue(value == null ? "" : value);
			  //getInputEl().setValue("Click to edit");
		  }
	  }

	  protected void expand() {
		 // Update value on component.
		 ((IValueComponent) menu.getComponent()).setValue(this.value);
	   
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
	    if (el() != null) {
	      super.onRender(target, index);
	      return;
	    }
	    input = new El(DOM.createInputText());
	    input.disable();
	    wrap = new El(DOM.createDiv());
	    wrap.dom.setClassName("x-form-field-wrap");

	    input.addStyleName(fieldStyle);

	    trigger = new El(DOM.createImg());
	    trigger.dom.setClassName("x-form-trigger " + triggerStyle);
	    trigger.dom.setPropertyString("src", GXT.BLANK_IMAGE_URL);
	    wrap.dom.appendChild(input.dom);
	    wrap.dom.appendChild(trigger.dom);
	    setElement(wrap.dom, target, index);

	    if (isHideTrigger()) {
	      trigger.setVisible(false);
	    }

	    super.onRender(target, index);

	    triggerListener = new EventListener() {
	      public void onBrowserEvent(Event event) {
	        if (!disabled) {
	          FieldEvent ce = new FieldEvent(AdaptableTriggerField.this);
	          ce.event = event;
	          ce.type = DOM.eventGetType(event);
	          ce.stopEvent();
	          onTriggerEvent(ce);
	        }
	      }
	    };
	    DOM.sinkEvents(wrap.dom, Event.FOCUSEVENTS);
	    DOM.sinkEvents(trigger.dom, Event.ONCLICK | Event.MOUSEEVENTS);

	    if (width == null) {
	      setWidth(150);
	    }
	   
	    focusPreview = new BaseEventPreview();

	    new KeyNav<FieldEvent>(this) {
	      public void onDown(FieldEvent fe) {
	        AdaptableTriggerField.this.onDown(fe);
	      }
	    };
	  
	  }

	  /*
	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	    focusPreview = new BaseEventPreview();

	    new KeyNav<FieldEvent>(this) {
	      public void onDown(FieldEvent fe) {
	        AdaptableTriggerField.this.onDown(fe);
	      }
	    };
	  }
		*/
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
