package org.onecmdb.ui.gwt.desktop.client;

import org.onecmdb.ui.gwt.desktop.client.widget.FileUploadWidget;

import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CMDBApplication implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  Viewport v = new Viewport();  
	  v.setLayout(new FillLayout());  
	  v.add(new BorderLayoutTest());  
	  RootPanel.get().add(v);  
	  
  }
  
  
}
