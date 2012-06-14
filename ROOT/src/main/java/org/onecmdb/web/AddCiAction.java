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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.springframework.validation.BindException;

public class AddCiAction extends EditCiAction implements FormAction {
    
    private final Map<ICi, CountDown> countdownMap = 
        Collections.synchronizedMap( new HashMap<ICi, CountDown>(0) );
    
    
    public AddCiAction() {
        super("addci");
        setDisplayName("Add");
    }

    private class CountDown extends Thread {

        CountDown() {
            countdownMap.put(getCi(), this);
            setName(getCi().getId().toString());
            start();
        }

        private boolean applied = false;

        synchronized  void setApplied(boolean b) {
            this.applied = b;
            notify();
        }
        
        public synchronized void run() {
            if (!applied) {
                    try {
                        wait(60000);
                    } catch (InterruptedException e) { }
            }
            if (!applied) {
                  // remove the CI
                System.out.println("Timeout!!!");
                
                ISession session = getCommand().getSession();
                IModelService model = (IModelService) session.getService(IModelService.class);
                
                ICcb ccb = (ICcb) session.getService(ICcb.class);
                ICmdbTransaction tx = ccb.getTx(session);
                {
                    ICiModifiable tpl = tx.getTemplate(getCi());
                    tpl.delete();
                }
                ITicket ticket = ccb.submitTx(tx);
                IRfcResult result = ccb.waitForTx(ticket);

                if (result.isRejected()) {
                    System.out.println("Failed to delete timed out CI!");
                }
                
                
            }
            countdownMap.remove(getCi());
        }
    };
    
    
    @Override
    protected void handleNavigationalChange(BindException errors) {
        
        // we came here by a triggering to this action
        // a  new template object is needed
        
        ICi ci = getCi();
        final ICi newCi = createOffspring(getCi(), errors);
        setCi(newCi);
        
        if (errors.hasErrors()) {
            cancel(errors);
            return;
        }
       
        super.handleNavigationalChange(errors);

    }

    public void apply(BindException errors) {
        super.apply(errors);
    }
    public void cancel(BindException errors) {
        
        ICi newCi = this.getCi();
        if (newCi != null) {
            deleteOffspring(newCi, errors);
            this.setCi((ICi) null);
        }
        SiteAction action = setReturnAction();
        String returnParam = getReturnParam();
        if (returnParam != null) {
            action.getFormParams().revert(returnParam);
        }
    }
    

    private SiteAction setReturnAction() {
        String returnTo = getReturnTo();
        if (returnTo!= null) {
            getCommand().setNavigate(returnTo);
        }        
        return getCommand().getAction(); 
    }
    
    protected void forward() {
        SiteAction action = setReturnAction();
        if (action == null) {
            getCommand().setAction("viewci");
        }
        String returnParam = getReturnParam();
        if (returnParam != null) {
            ICi newCi = getCi();
            action.getFormParams().put(returnParam, newCi);
        }
    }
    
    
   
    

    private void deleteOffspring(ICi ci,  BindException errors) {
        
        ISession session = getCommand().getSession();
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        {
            ICiModifiable tpl = tx.getTemplate(ci);
            tpl.delete();
        }
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket); 
        
        if (result.isRejected()) {
            errors.reject("REJECT", new String[] {result.getRejectCause()}, result.getRejectCause());
        }
    }
    
    
    private ICi createOffspring(ICi ci, BindException errors) {
        Set<ICi> beforeSet = ci.getOffsprings();

        ISession session = getCommand().getSession();
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        {
            ICiModifiable tpl = tx.getTemplate(ci);
            ICiModifiable offsping = tpl.createOffspring();
            offsping.setIsBlueprint(false);
        }
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket);  
        if (result.isRejected()) {
        errors.reject("REJECT", new String[] {result.getRejectCause()}, result.getRejectCause());
        return null;
        }
        /* 
         * removing the ones available before, leaves the newly created
         * CI left as single element
         */ 
        Set<ICi> newSet = ci.getOffsprings();
        newSet.removeAll(beforeSet);
        if (newSet.isEmpty()) {
            errors.reject("ADDCI", new String[] {}, "Failed to create offspring");
            return null;
        } else {
            return newSet.iterator().next();
        }
    }



    

}
