/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.onecmdb.ui.gwt.desktop.client;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.mvc.contoller.DesktopContoller;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;

public class DesktopApp implements EntryPoint {
	
 
	
  private Desktop desktop;
  
  public DesktopApp() {
	  initWindowMap();
  }
  
  private void initWindowMap() {
	  
		  
  }

  public void onModuleLoad() {
	  
	 
	  
	  
	  // Instanciate Dispatcher.
	  Dispatcher dispatcher = Dispatcher.get();
		
	  // Register Controller.
	  dispatcher.addController(new DesktopContoller());
	
	  dispatcher.dispatch(CMDBEvents.DESKTOP_CHECK_SESSION);
	  
	  if (true) {
		  return;
	  }
  }
  
}
