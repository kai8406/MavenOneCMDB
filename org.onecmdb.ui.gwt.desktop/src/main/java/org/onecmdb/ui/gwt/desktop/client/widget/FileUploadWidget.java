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
package org.onecmdb.ui.gwt.desktop.client.widget;

import org.onecmdb.ui.gwt.desktop.client.fixes.MyFileUploadField;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;



import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;


public class FileUploadWidget extends Dialog {
	
	private ContentData data;
	private FormPanel upload;
	private FileUploadField file;
	private Field<String> name;
	private boolean complex = true;

	public FileUploadWidget(ContentData data) {
		this.data = data;
		setLayout(new FitLayout());
		setSize(450, 200);
	}
	
		
	public boolean isComplex() {
		return complex;
	}


	public void setComplex(boolean complex) {
		this.complex = complex;
	}


	@Override
	protected void onRender(Element parent, int index) {
		// Need to add buttons before rendering...
	    Button btn = new Button("Upload");  
	     btn.addSelectionListener(new SelectionListener<ButtonEvent>() {  

	    	 @Override  
	    	 public void componentSelected(ButtonEvent ce) {  
	    		 //file.setName(name.getValue());
	    		 if (!upload.isValid()) {  
	    			 return;  
	    		 }  
	    		
	    		 // normally would submit the form but for example no server set up to   
	    		 // handle the post  
	    		 upload.submit();  

	    		 MessageBox.info("Action", "You file was uploaded", new Listener<WindowEvent>() {

					public void handleEvent(WindowEvent be) {
						 BaseEvent submitE = new BaseEvent(name.getValue());
			    		 fireEvent(Events.Submit, submitE);
			    		 close();
			 		}
	    			 
	    		 });
	    	 }  
	     });  
	    addButton(btn);  
	    addButton(new Button("Cancel", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				close();
			}
	    }));
	    setButtons("");
		super.onRender(parent, index);
		init();
	}


	public void init() {
		setHeading("Upload File to " + data.getPath());
		upload = getUpload();
		add(upload);
		
	}
	
	public FormPanel getUpload() {
		  final FormPanel panel = new FormPanel();  
		  panel.setHeaderVisible(false);
		  //panel.setHeading("File Upload ");  
		  panel.setFrame(true);  
		  panel.setAction(GWT.getModuleBaseURL() + "onecmdb/content");  
		  panel.setEncoding(Encoding.MULTIPART);  
		  panel.setMethod(Method.POST);  
		  panel.setButtonAlign(HorizontalAlignment.CENTER);  
		  panel.setWidth(350);  
		  	if (this.complex ) {
		    TextField<String> path = new TextField<String>();  
		     path.setFieldLabel("Path"); 
		     path.setName("path");
		     path.setReadOnly(true);
		     path.setValue(data.getPath());
		     panel.add(path);  
		  
		     name = new TextField<String>();  
		     name.setFieldLabel("Name"); 
		     name.setName("name");
		     panel.add(name);  
		  	} else {
		  		HiddenField<String> path = new HiddenField<String>();
		  		path.setName("path");
		  		path.setValue(data.getPath());
		  		panel.add(path);
		  		
		  		name = new HiddenField<String>();
		  		name.setName("name");
		  		panel.add(name);
		  	}
		   
		     file = new MyFileUploadField();  
		     file.setAllowBlank(false);  
		     file.setFieldLabel("File");
		     file.setName("file");
		     file.addListener(Events.Change , new Listener<FieldEvent>() {

				public void handleEvent(FieldEvent be) {
					if (name == null) {
						return;
					}
					String value = (String) be.value;
					if (value == null) {
						return;
					}
					value = value.replace("\\", "/");
					int last = value.lastIndexOf("/");
					String v = value;
					if ( last >= 0) {
						v = value.substring(last+1);
					}
					
					name.setValue(v);
				}
		    	 
		     });
		     panel.add(file);  
		     
		     return(panel);
	}
	/*
	public FormPanel getFormPanel() {
		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "onecmdb/content");
		System.out.println("Action=" + form.getAction());
		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);

		// Create a FileUpload widget.
		FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");

		panel.add(upload);

		Button button2 = new Button("Submit", new ClickListener() {
			public void onClick(Widget sender) {
				form.submit();
			}
		});

		// Add a 'submit' button.
		panel.add(button2);

		// Add an event handler to the form.
		form.addFormHandler(new FormHandler() {

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is
				// fired. Assuming the service returned a response of type
				// text/html,
				// we can get the result text here (see the FormPanel
				// documentation for
				// further explanation).
				Window.alert(event.getResults());
			}

			public void onSubmit(FormSubmitEvent event) {
				// TODO Auto-generated method stub
				Window.alert("On Submit!");
			}
		});

		return(form);
	}
	*/
}
