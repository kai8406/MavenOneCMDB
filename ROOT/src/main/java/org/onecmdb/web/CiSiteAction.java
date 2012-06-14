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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IReference;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.OneCmdb;
import org.onecmdb.core.internal.model.ItemId;
import org.springframework.validation.BindException;

/** 
 * An action acting on an underlying configuration item. A number of convient 
 * number of methods to ask for things on the underlying CI can be found, 
 * for example, {@link #getAttributeMap()}.
 * @author nogun
 */
public abstract class CiSiteAction extends SiteAction {

    public CiSiteAction(String name) {
        super(name);
        getPredicates().put("hasCi", true);
    }

    
    
    private transient ICi cachedCi = null;
    
    
    /** 
     * Put the CI's identifier as an parameter of for this action.
     * @param id
     */
    public void setCi(ItemId id) {
         addParam("ci", id != null ? id.toString() : null);
    }
    
    public void setCi(ICi ci) {
        setCi(ci != null ? ci.getId() : null);
    }
    
    
    /**
     * Supposed to be called when the bound CI is changed.
     * @param newCi
     */
    private void onNotification(ICi newCi) {
        for (Iterator<String> iter = CiSiteAction.this.getPredicates().keySet().iterator();
        iter.hasNext(); ) {
            String key = iter.next();
            if (key.startsWith("derivedFrom(")) {
                iter.remove();
            }
        }
        if (newCi != null) {
            IPath<IType> path = newCi.getOffspringPath().getAllButLeaf();
            for (IType p : path ) {
                getPredicates().put("derivedFrom("+ p.getAlias()+")", true);
            }
        }
    }
    
    
    /**
     * Fetch the CI <em>bound</em> to this action
     * @return The CI bound, or <code>null</code> in case no CI is currently
     * bound.
     */
    public final ICi getCi() {
       Object ciid = getParams().get("ci");
       if (ciid == null) {
           return null;
       }
       
        ItemId id = new ItemId(""+ciid);
        if (cachedCi == null || !cachedCi.getId().equals(id)) {
            ISession session = getSession();
            IModelService cisvc = (IModelService) session.getService(IModelService.class);
            cachedCi = cisvc.find(id);
            onNotification(cachedCi);
        
        }
        return cachedCi;
    }

    /** 
     * Makes sure to clear any cache, forcing us to reread objects used 
     */
    protected final void clearCache() {
        this.cachedCi = null;
    }

    
    /**
     * All references pointing to, i.e. inbound, relations, for the currently
     * managed configuration item. 
     * @return The set of references pointing inward, on to the currently
     *  managed Configuration Item.
     */
    public Set<IReference> getInboundReferences() {
        ICi ci = getCi();
        if (ci == null) {
            OneCmdb.getLogger(CiSiteAction.class).warn("No CI currently available!");
            return Collections.emptySet();
        }
        
        IReferenceService refsvc = (IReferenceService) this.getCommand().getSession()
        .getService(IReferenceService.class);

        Set<IReference> refs = refsvc.getReferrers(ci);
        
        return refs;
    }

    
    /** 
     * All attributes collected per alias name into seperate lists
     */
    public List<BeanList<IAttribute>> getAttributes() {
        return new ArrayList<BeanList<IAttribute>>(getAttributeMap().values());
    }
    
    /** 
     * Fetch all attributes <em>keyed</em> after its alias. The key's value
     * is a list of contained attributes. This mechanism treats single valued
     * attributes and  multi valued dittos the same. To find out if the 
     * attribute is multi valued, the attributes <em>meta data</em>, or 
     * policy should be investigated, or simply check the size of the list.
      */
    public Map<String, BeanList<IAttribute>> getAttributeMap() {
        ICi ci = getCi();
        return CiUtils.getAttributeMap(ci);
    }
    
    
    /** 
     * @return Attributes <em>keyed<em> after their category. 
     */
    public Map<String,List<BeanList<IAttribute>>> getCategorizedAttributes() {
        
        final Map<String, BeanList<IAttribute>> all = getAttributeMap();
        
        // now start categorizing....
        
        BeanList<IAttribute> cats = all.get("_categories");
        if (cats != null) {
            all.remove("_categories"); // currently we don't use the idea of
            // letting a specific attribute dicatate the categories. Instead
            // we use a hard coded categories, see below. 
        }
        // ('a b c d) ;
        
      
        final Map<String, List<BeanList<IAttribute>>> categorized = new HashMap
        <String,List<BeanList<IAttribute>>>();
        
       for ( String key : all.keySet() ) {
           final String cat;
           if ("icon".equals(key)) {
               cat = "idents";
           } else {
               cat = "uncategorized";
           }
           List<BeanList<IAttribute>> members = categorized.get(cat);
           if (members == null)
           {
               members = new ArrayList<BeanList<IAttribute>>();
               categorized.put(cat, members);
           }
           members.add(all.get(key));
       }
       return categorized;
    }
    
    
    
    @Override
    public void onNewForm(SiteCommand site, BindException errors) {
        super.onNewForm(site, errors);
    }
    
    
    
}
