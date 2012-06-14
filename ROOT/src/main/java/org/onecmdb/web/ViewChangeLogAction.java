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

import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.springframework.validation.BindException;

public class ViewChangeLogAction extends CiSiteAction {

    /** current page in focus */
    private Integer page = null;


    public ViewChangeLogAction() {
        super("viewchlog");
        setDisplayName("Change log");
    }

    /** 
     * <p>The (recent) changes as <em>request for chhanges</em> sorted with the 
     * newest first.</p>
     * 
     * @return A list of request for changes.
     */
    public List<IRFC> getRfcs() {
        
        ICi ci = getCi();

        ICcb ccb = (ICcb) getCommand().getSession()
            .getService(ICcb.class);
        
        RfcQueryCriteria crit = new RfcQueryCriteria();
        crit.setDescendingOrder(true);
        crit.setFetchAttributes(true);
        crit.setMaxResult(1000);
        if (page != null) crit.setFirstResult(page * 10);
        
        
        List<IRFC> rfcs = ccb.queryRFCForCi(ci, crit);
        
        
        return rfcs;
    }

    
    @Override
    protected void handleNavigationalChange(BindException errors) {
        super.handleNavigationalChange(errors);

        this.page = null;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    
    @Override
    protected void handleFormChange(FormChange change, BindException errors) {

        
        
        super.handleFormChange(change, errors);
    }
}

