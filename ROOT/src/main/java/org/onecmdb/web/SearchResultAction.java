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

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.springframework.validation.BindException;


public class SearchResultAction extends SiteAction {
    
    private SearchResult<ICi> result;

    public SearchResultAction() {
        super("searchresult");
        setDisplayName("Search Result");
    }


    
    
    @Override
    protected void handleNavigationalChange(BindException errors) {
        super.handleNavigationalChange(errors);
        
    
    }
   
    public SearchResult<ICi> getSearchResult() {
        return this.result;
    }

    
    @Override
    public void process() {
        super.process();

        ISession session = getCommand().getSession();
        IModelService modelsvc = (IModelService) session.getService(IModelService.class);

        String text =  (String) getCommand().getGlobals().get("searchText");
        
        this.result = new SearchResult<ICi>();
        
        QueryCriteria crit = new QueryCriteria();
		crit.setText(text);
		// Serach for ci and attributes.
		crit.setMatchCi(true);
		crit.setMatchAttribute(true);
		// Tex match on alias,description and value.
		crit.setTextMatchAlias(true);
		crit.setTextMatchDescription(true);
		crit.setTextMatchValue(true);
		
		// Match both template and instance attributes.
		crit.setMatchAttributeTemplates(true);
		crit.setMatchAttributeInstances(true); 
		
		// Always return instances
		crit.setMatchCiInstances(true);
		crit.setMatchCiTemplates(false);
        
        if (getCommand().getMode().equalsIgnoreCase("design")) {
        	// In design mode also match templates
        	crit.setMatchCiTemplates(true);
        } 
        
        crit.setMaxResult(200);
        QueryResult<ICi> qResult = modelsvc.query(crit);
        for (ICi ci : qResult) {
        	result.add(ci);
        }
      
    }
    
}
