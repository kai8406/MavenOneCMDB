/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.onecmdb.ui.gwt.desktop.client.desktop;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.i18n.client.DateTimeFormat;

public class Plant extends BaseModelData {

  private DateTimeFormat df = DateTimeFormat.getFormat("MM/dd/y");
  
  public Plant() {
    
  }
  
  public Plant(String name, String light, double price, String available, boolean indoor) {
    setName(name);
    setLight(light);
    setPrice(price);
    setAvailable(df.parse(available));
    setIndoor(indoor);
  }
  
  public Date getAvailable() {
    return get("available");
  }

  public void setAvailable(Date available) {
    set("available", available);
  }

  public boolean isIndoor() {
    return (Boolean)get("indoor");
  }

  public void setIndoor(boolean indoor) {
    set("indoor", indoor);
  }

  public String getLight() {
    return get("light");
  }

  public void setLight(String light) {
    set("light", light);
  }

  public String getName() {
    return get("name");
  }

  public void setName(String name) {
    set("name", name);
  }

  public double getPrice() {
    return (Double)get("price");
  }

  public void setPrice(double price) {
    set("price", price);
  }

}
