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

import org.springframework.validation.BindException;

public class AddIconAction extends SiteAction implements FormAction {
    
    
    public AddIconAction() {
        super("addicon");
        setDisplayName("Add Icon");
    }
    
    
    @Override
    protected void handleNavigationalChange(BindException errors) {
        
        // we came here by a triggering to this action
        // a  new template object is needed
        
        if (errors.hasErrors()) {
            cancel(errors);
            return;
        }
       
        super.handleNavigationalChange(errors);

    }

    public void apply(BindException errors) {

    
    
    }
    public void cancel(BindException errors) {
        forward();
    }
    

    protected void forward() {
        String returnTo = getReturnParam();
        if (returnTo!= null) {
            // now revert to were we came from
            
            String returnParam = getReturnParam();
            getCommand().setNavigate(getReturnParam());
            SiteAction action = getCommand().getAction();

            if (returnParam != null) {
                
                
                action.getFormParams().put(returnParam, null);
            }

            
        } else {
            SiteAction current = getCommand().getAction();
            SiteAction action = getCommand().getActionMap().get("viewci");
            //action.setParams(current.getParams());
            getCommand().setAction(action.getName());
        
        }
    }
    
   

    private String getReturnParam() {
        // TODO Auto-generated method stub
        return null;
    }


    public void validate(BindException errors) {
        // TODO Auto-generated method stub
    }

    public void setReturnTo(String navigate) {
        // TODO Auto-generated method stub
        
    }

    

}
