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

import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;



public class ValueEditor extends PropertyEditorSupport {

    private IOneCmdbContext onecmdb;
    
    ValueEditor(IOneCmdbContext onecmdb) {

            this.onecmdb = onecmdb;
        
    }
     
    public void setAsText(String text) throws IllegalArgumentException {
        IModelService modelsvc = (IModelService) this.onecmdb.getService(null, IModelService.class);
        IType xsstring = modelsvc.getType("xs:string");
        IValue value = xsstring.parseString(text);
        setValue(value);
    }

    @Override
    public String getAsText() {
        IValue value = (IValue) getValue();
        return value.getAsString();
    }
    
    @Override
    public Object getValue() {
        return super.getValue();
    }
    

}