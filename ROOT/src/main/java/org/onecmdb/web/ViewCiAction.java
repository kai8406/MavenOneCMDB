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

import java.util.List;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IJobService;
import org.onecmdb.core.IJobStartResult;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.ISession;
import org.springframework.validation.BindException;


public class ViewCiAction extends  CiSiteAction {
    
    public ViewCiAction() {
        super("viewci");
        setDisplayName("View");
    
    }

    
    @Override
    public void onNewForm(SiteCommand site, BindException errors) {
        super.onNewForm(site, errors);

        IModelService model = (IModelService) site.getSession().getService(IModelService.class);
        
        IPath<String> path = getCommand().getController().getDataCenter();
        ViewCiAction defaultAction = (ViewCiAction) clone();
        ICi dataCentre = model.findCi(path);
        if (dataCentre != null) {
            defaultAction.setCi(dataCentre);
        } else {
            ICi root = model.getRoot();
            defaultAction.setCi(root);
        }
        site.addHistory(defaultAction);
    }
    

    @Override
    protected void handleFormChange(FormChange change, BindException errors) {
        super.handleFormChange(change, errors);
        if ("jobctl".equals(change.getOperation())) {
            final ICi job = getCi();
            final ISession session = getCommand().getSession();
            final IJobService jobsvc = (IJobService) session.getService(IJobService.class);

            String cmd = change.getArgs().get(1);
            if ("start".equals(cmd)) {
                IJobStartResult result = jobsvc.startJob(session, job);
                if (result.isRejected()) {
                    errors.reject("REJECT", new String[] {result.getRejectCause()}, result.getRejectCause());
                } else {
                
	                int loopMax = 10;
	                String state = null;
	                do {
	                    loopMax--;
	                    try {
	                        Thread.sleep(100);
	                    } catch (InterruptedException e) {  }
	                    clearCache();
	                    ICi updatedJob = getCi();
	                    List<IAttribute> stateAttrs = updatedJob.getAttributesWithAlias("state");
	                    if (stateAttrs.size() == 1) {
	                        state = (String) stateAttrs.get(0).getValue().getAsJavaObject();
	                    }
	                } while (loopMax > 0 && "".equals("") || "IDLE".equals(state));
                }
            }
            else if ("stop".equals(cmd)) {
                IJobStartResult result = jobsvc.cancelJob(session, job);
                if (result.isRejected()) {
                    errors.reject("REJECT", new String[] {result.getRejectCause()}, result.getRejectCause());
                }
            }
        }
        clearCache();
    }
    
}