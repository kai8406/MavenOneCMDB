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

import java.beans.PropertyEditorSupport;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.StrippedCi;


/** 
 * Reconstruct a path (#id/#id/#id...
 * @author nogun
 *
 */
public class PathEditor extends PropertyEditorSupport {

    private final IModelService cisvc;
    
    
    public PathEditor(IModelService cisvc) {
        this.cisvc = cisvc;
    }
    
    public void setAsText(String text) {
        IPath<StrippedCi> path = new Path<StrippedCi>(); 
        ItemIdEditor idEditor = new ItemIdEditor();
        String[] parts = text.split("/");
        for (String part : parts) {
            idEditor.setAsText(part);
            ItemId id = (ItemId) idEditor.getValue();
            ICi ci = cisvc.find(id);
            StrippedCi strippedCi = new StrippedCi(ci);
            path.addElement(strippedCi);
        }
        setValue(path);
    }
    
 
    @Override
    public String getAsText() {
        IPath<StrippedCi> path = (IPath<StrippedCi>) getValue();
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (StrippedCi elt : path.getList()) {
            if (!first) {
                sb.append("/");
            } else {
                first = false;
            }
            sb.append(elt.toString());
        }
        return sb.toString();
    }
}
