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
package org.onecmdb.web;

import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.web.graphs.EGraphRelation;


public class GraphCommand {

  public EGraphRelation[] getAllRelationTypes() {
      return EGraphRelation.values();
  }
    
  private EGraphRelation relationType;
  public void setRelationType(EGraphRelation type) {
      this.relationType = type;
  }
  public EGraphRelation getRelationType() {
      return this.relationType == null ? EGraphRelation.ALL : this.relationType;
  }
  
  private ItemId ciid;
  public void setCiid(ItemId ciid) {
      this.ciid = ciid;
  }
  public ItemId getCiid() {
      return this.ciid;
  }

  private String format;
  public String getFormat() {
      return format;
  }
  public void setFormat(String format) {
      this.format = format;
  }

  private int depth = 2;
  public void setDepth(String d) {
      if ( d != null && !"".equals(d))
          this.depth = Math.max(2, Integer.parseInt(d));
  }
  public String getDepth() {
      return ""+this.depth ;
  }

  public int depth() {
      return depth;
  }
    
}




