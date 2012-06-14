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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.onecmdb.core.IValue;

public class FormChange {

    private String key;
    private String operation;
    private String[] params;

    private final SiteAction action;
    
    public FormChange(SiteAction action, HttpServletRequest request) {
        this.action = action;
        String sign = "change:" + action.getName() + ":";
        for (Enumeration nameEnum = request.getParameterNames();
        nameEnum.hasMoreElements(); ) {
            String name = (String) nameEnum.nextElement();
            if (name.startsWith(sign)) {
                this.key = name;
                
                int posArgsStart = name.indexOf('(', sign.length());
                int posArgsEnd   = name.indexOf(')', posArgsStart + 1);
                
                this.operation = name.substring(sign.length(), posArgsStart);
               
                
                String params = name.substring(posArgsStart + 1, posArgsEnd);
                
                this.params = params.split(",");
                for (int i = 0; i <this.params.length; i++ ) {
                    this.params[i] = this.params[i].trim();
                }                
               break;
            }
        }
    }

    public String getOperation() {
        return this.operation;
    }
    
    public  List<String> getArgs() {
        return Arrays.asList(this.params);
    }
        
    public String toString() {
        return this.key;
        
    }

    public IValue getParamValue(String name) {
        String key = getParamKey(name);
        return (IValue) action.getFormParams().get(key);
    }
    private String getParamKey(String name) {
        String opkey = this.key.substring( this.key.indexOf(this.operation) );
        return opkey + ":" + name;
    }
    
    public String getParamExpr(String name) {
        String key= getParamKey(name);
        return "action.formParams["+ key +"]";
    
    }
   
    /** 
     * The parameter holding the change status 
     * <pre>
     *    action:<i>operation</i>([<i>param...</i>])
     * </pre> 
     */
    String getChangeExpr() {
        StringBuffer sb = new StringBuffer(operation);
        sb.append("(");
        for (String arg : params) {
            if (sb.charAt(sb.length()-1)!='(') sb.append(",");
            sb.append(arg);
        }
        sb.append(")");
        String key = sb.toString();
       return "action.formParams['"+key+"']"; 
    }

    
    
        
        

}
