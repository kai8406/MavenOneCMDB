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
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.StrippedCi;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;



/**
 * Manages the collection of model data, which is then propaged back to 
 * the view, which shows this data.
 * 
 * The view can query for getViews(
 * 
 * 
 * 
 * 
 * A controller to ask for a CI from the backend, and expose it in
 * the model returbed.
  
 * An action is mapped into a view, which is reflected by the ViewData
 * container.
 * 
 * The action can SiteAction 
 * 
 * 
 * 
 *
 */
public class ShowCiController extends SimpleFormController  {

    private static final String ACTION_PARAM = "_action";
    private static final String ACTION_DATA_PARAM = "_actionData";
    
    private Map<String, SiteAction> views = new HashMap<String, SiteAction>();
    {
        // the navigator (tree)
        // defines the navigational stragegies (hierarchical)
    }


    
    private IOneCmdbContext onecmdb;


    private Map actionViewMap;


    private String action;


    private ISession session;

    
    //{{{ bean suport 
    
    /**
     * WARNING: Used to satisfy spring only
     */
    public void setOneCmdb(IOneCmdbContext onecmdb) {
        this.onecmdb = onecmdb;
    }

    public IOneCmdbContext getOneCmdb() {
        return this.onecmdb;
    }
    
    public Map<String, SiteAction> getViews() {
        return views;
    }
    

    /**
     * Verify that this controller is set up in a descent state, should be 
     * used by spring, and its post-methods.
     */
    public void verify() {
        if (onecmdb == null) {
            throw new IllegalStateException("No backend  (OneCmdb application context) set!");
        }
        
        this.session = onecmdb.createSession();
    }
    
    
    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);

        binder.registerCustomEditor(ItemId.class, new ItemIdEditor());
        //binder.registerCustomEditor(ICi.class, new ItemEditor(onecmdb));
        
    }
    
    
    @Override
    protected void onBindOnNewForm(HttpServletRequest request, Object command, BindException errors) throws Exception {
        super.onBindOnNewForm(request, command, errors);
    }
   
    @Override
    protected boolean isFormChangeRequest(HttpServletRequest request) {
        String action = request.getParameter(ACTION_PARAM);
        if (action != null) {
            if ("submit".equals(action))
                return false;
            setAction(action);
            return true;
        }
        return false;
    
    };
    
    private void setAction(String action) {
        this.action = action;
        
    }
    @Override
    /**
     * In case there is a paramter named <code>_action</code> and its value is
     * "submit".
     */
    protected boolean isFormSubmission(HttpServletRequest request) {
        
        for (Iterator iter = request.getParameterMap().keySet().iterator(); iter.hasNext() ; ) {
            String paramName = (String) iter.next();
            Object paramValue = request.getParameter(paramName);
            System.out.println("::::" + paramName +"=" + paramValue);
        }
        
        
        String submitValue = request.getParameter(ACTION_PARAM);
        boolean b = submitValue != null;
        return b;
    }
    
    
    
    @Override
    protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {
        super.onBindOnNewForm(request, command);
    
        
        System.out.println("bindOnNewForm(): isSessionForm=" + isSessionForm());
    

        // fetch the ``root'' object to view
        
        IModelService cisvc = (IModelService)session.getService(IModelService.class);
        

        ICi root = cisvc.getRoot();
        
        
        
        
        Path<StrippedCi> ciPath = new Path<StrippedCi>();
        ciPath.addElement(new StrippedCi(root));
        ciPath.addElement(new StrippedCi(root));
        
    }
    
    @Override
    protected void onBind(HttpServletRequest request, Object command, BindException errors) throws Exception {
        super.onBind(request, command, errors);
    }
    
    @Override

    /**
     * Each submission will end up here, in this controller, using the action
     * to decide what to do, and what data to extract from middle tier.
     * 
     * Data is collected and put in the <code>viewData</code> object.
     * 
     * Actions are intentially kept in lower case.
     */
    protected void onFormChange(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
        super.onFormChange(request, response, command);

        SiteCommand viewData = (SiteCommand) command;
        
        /*
         * A number os actions. `Submit' actions changes values to the backend,
         * other prepares the `pageData' object with new values.
         * 
         * The pageData must be cleaned of old objects (actions no longer in use)
         *  once in a while, to not echust the memory.
         *  
         */

        
/*        
        if (this.action.equals("editattribute")) {
            // edit a single attribute
            ItemId attrId = new ItemId(request.getParameter(ACTION_DATA_PARAM));
            for (IAttribute attr : viewData.getCurrentCi().getAttributes()) {
                if (attr.getId().equals(attrId)) {
                    viewData.setEditAttribute(attr);
                    String key = attr.getId().toString();
                    viewData.getEditAttributeValues().put(key, attr.getValue().asString());
                    break;
                }
            }
        } else 
            if (this.action.equals("submiteditattribute")) {
                // update the single edited attribute
                IAttribute attr = viewData.getEditAttribute();
                String key = attr.getId().toString();
                
                IValue value = attr.getValue();
                String newValueString = (String) viewData.getEditAttributeValues().get(key);
                IValue newValue = attr.getValueType().parseString(newValueString);
                if (!newValue.equals(value)) { 
                	ICcb ccb = (ICcb)session.getService(ICcb.class);
                    //TODO: breakout; formalize.
                    ICmdbTransaction tx = ccb.getTx(session);
                    IAttributeModifiable tpl = tx.getAttributeTemplate(attr);
                    tpl.setValue(newValue);
                    ITicket ticket = ccb.submitTx(tx);
                    IRfcResult result = ccb.waitForTx(ticket);
                    if (result.isRejected()) {
                        // TODO: propagate back...
                        System.err.println(result.getRejectCause());
                    } else {
                        viewData.setSubviewData(null);
                    }
                }
                
                
                //formData.getNewAttribute().applyFor(formData.getCurrentCi());
                this.action = null;
                
        } else 
        if (this.action.equals("submitaddattribute")) {
            // add a new attribute according to the data from:
            
            // data for the new attribute.
            NewAttribute newAttrData = viewData.getNewAttribute();
            
            // configuration item, to which the attribute should be added
            ICi ci = viewData.getCurrentCi();
            
         	ICcb ccb = (ICcb)session.getService(ICcb.class);
            
            ICmdbTransaction tx = ccb.getTx(session);

            // \
            //  - now create the attribute according to? niklas? 
            // /
            
            ITicket ticket = ccb.submitTx(tx);
            IRfcResult result = ccb.waitForTx(ticket);
            if (result.isRejected()) {
                // TODO: propagate back...
                System.err.println(result.getRejectCause());
            } else {
                viewData.setSubviewData(null);
            }
            
            this.action = null;
            
        } else
        if (this.action.equals("edit")) {
            // prepare the view for editing; the whole view
            for (IAttribute attr : viewData.getCurrentCi().getAttributes()) {
                    String key = attr.getId().toString();
                    String currentValue = attr.getValue().asString().trim();
                    viewData.getEditAttributeValues().put(key, currentValue);
            }
        } else
        if (this.action.equals("submitedit")) {
            for (IAttribute attr : viewData.getCurrentCi().getAttributes()) {

                IValue value = attr.getValue();
                String newValueString = (String) viewData.getEditAttributeValues().get(attr.getId().toString());
                IValue newValue = attr.getValueType().parseString(newValueString);
                
                if (!newValue.equals(value)) { 
                    //TODO: breakout; formalize.
                   	ICcb ccb = (ICcb)session.getService(ICcb.class);
                    ICmdbTransaction tx = ccb.getTx(session);
                    IAttributeModifiable tpl = tx.getAttributeTemplate(attr);
                    tpl.setValue(newValue);
                    ITicket ticket = ccb.submitTx(tx);
                    IRfcResult result = ccb.waitForTx(ticket);
                    if (result.isRejected()) {
                        // TODO: propagate back...
                        System.err.println(result.getRejectCause());
                    } else {
                        viewData.setSubviewData(null);
                    }
                }
                
        }
        }
  */
            
        
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
    
        super.onBindAndValidate(request, command, errors);
    }
    
    
    
    @Override
    protected boolean suppressValidation(HttpServletRequest request) {
        super.suppressValidation(request);
        return this.action == null 
               || !this.action.startsWith("submit");
        
    }

    public void setFormChanges(Map formChangeViewMap) {
        this.actionViewMap = formChangeViewMap;
    
    }
    
    @Override
    /** we never arrive here... all is handled via form changes */
    protected void doSubmitAction(Object command) {
    
        Map<String,Object> data = new HashMap<String,Object>();
        
        // are we logged in or not?
        
        IModelService cisvc = (IModelService) session.getService(IModelService.class);
        
        ICi root = cisvc.getRoot();
        
        data.put("ci", root);

        System.out.println(root.getId());
        System.out.println(root.getOffsprings());
        

        //ISession session = onecmdb.createSession("user", "user");
        Date date = new java.util.Date(); 
    }
    
    //}}}

    
    
    
   

    
    


}
