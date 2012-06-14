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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class MainController implements Controller {

    private IOneCmdbContext onecmdb;
    
    //{{{ bean suport 
    
    /**
     * WARNING: Used to satisfy spring only
     */
    public void setOneCmdb(IOneCmdbContext onecmdb) {
         this.onecmdb = onecmdb; 
    
    }
    
    //}}}
    
    
    
    public ModelAndView handleRequest(
            HttpServletRequest request, 
            HttpServletResponse response) 
    throws Exception 
    {
        
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("date", new Date());
        
        // are we logged in or not?
        
        
        
        data.put("view", "login");
        ISession session = this.onecmdb.createSession();
        IModelService cisvc = (IModelService)session.getService(IModelService.class);
        
        ICi root = cisvc.getRoot();


        
        IType type;
        
        //root.getOffspringPath();
        
        
        
        data.put("ci", root);

        System.out.println(root.getId());
        System.out.println(root.getOffsprings());
        
        
        
        //ISession session = onecmdb.createSession("user", "user");
        Date date = new java.util.Date(); 
        return new ModelAndView("welcome", "model", data);

    
    
    }


}
