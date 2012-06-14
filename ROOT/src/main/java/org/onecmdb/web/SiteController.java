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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.acegisecurity.AccessDeniedException;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IPath;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.OneCmdb;
import org.onecmdb.core.internal.model.ItemId;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;

/**
 * Main controller, and redirector, for the web application.
 *
 */
public class SiteController extends AbstractFormController {
    
    
    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
    }
    
    // {{{ spring bean support
    
    /** A reference to the backend OneCMDB system */  
    private IOneCmdbContext onecmdb;

    public SiteController() {
        super();
    }
    
    public void setOneCmdb(IOneCmdbContext onecmdb) {
        this.onecmdb = onecmdb;
    
    }
    IOneCmdbContext getOneCmdb() {
        return this.onecmdb;
    }
    
    private String siteView;
    private IPath<String> userScope;
    private IPath<String> refsBase;
    private IPath<String> tplBase;

    /**
     * The view, holding the main page layout.
     */
    public void setSiteView(String view) {
        this.siteView = view;
    }
    public String getSiteView() {
        return this.siteView;
    }

    
    /** 
     * Verifies that the controller is set up in a consistent and valid, state.
     */
    public void init() {
        if (!isSessionForm()) {
        
        }
    }
    // }}}

    
    // {{{ process flow (processed before showing the view)
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
         SiteCommand site = (SiteCommand) super.formBackingObject(request); 
         site.setController(this);
         site.init();
         
         
         return site;
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        
       super.initBinder(request, binder);
       binder.registerCustomEditor(IValue.class, new ValueEditor(this.onecmdb));
       binder.registerCustomEditor(ItemId.class, new ItemIdEditor());
    }
    
    @Override
    /**
     * Used to initiate a <em>new</em> session
     */
    protected void onBindOnNewForm(HttpServletRequest request, Object command, 
            BindException errors) 
    throws Exception 
    {
       super.onBindOnNewForm(request, command, errors);
        
        SiteCommand site = (SiteCommand) command;

        // inject state into the actions 
        for (SiteAction action : site.getActionMap().values() ) {
            action.onNewForm(site, errors);
        }
        if (site.getAction() != null) {
            // an action was bound via the query string etc.
            site.getAction().onNewForm(site, errors);
        } else {
            // default action
            site.setAction(site.getRootAction().getName());
        }
        
        /* bind any parameters (onto the command) from the request */
        onBindAndValidate(request, command, errors);
        
        /* consider this a navigational change, to let the action initialize */
        site.getAction().onNavigationalChange(errors);
    
    }  
    

    @Override
    protected void onBind(HttpServletRequest request, Object command) throws Exception {
        super.onBind(request, command);
    }

    
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
        
        super.onBindAndValidate(request, command, errors);
        SiteCommand site = (SiteCommand) command;
        
        
        // data is here bound into site, including the current action 
        SiteAction action = site.getAction();
        if (action == null) {
            // we are coming here without having an action 
            errors.addError(new ObjectError("SiteController", null, null, 
            "No Action set!"));
            return;
        }
        
        // initiate the action, via the parameters in the request
        ServletRequestDataBinder binder = new ServletRequestDataBinder(action);
        binder.bind(request);
        
    }
    
    @Override
    protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
       HashMap<String, Object> data = new HashMap<String, Object>();
       data.put("scratch", new HashMap() );
       data.put("pageUrl", request.getServletPath() );
       data.put("time", new Date());
       return data; 
    }
    
    // }}}

    // {{{ decision makings
   
   @Override
   protected boolean isFormSubmission(HttpServletRequest request) {
       
       
       HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(getFormSessionAttributeName(request)) == null) {
            return false;
        }
        return true;
   }

   private boolean isApplyFormRequest(SiteAction action , HttpServletRequest request) {
       String submit = request.getParameter("apply:" + action.getName());
       return submit != null;
   }

   private boolean isCancelFormRequest(SiteAction action , HttpServletRequest request) {
       String submit = request.getParameter("cancel:" + action.getName());
       return submit != null;
   }

   private boolean isChangeFormRequest(SiteAction action , HttpServletRequest request) {
       String sign = "change:" + action.getName();
       for (Enumeration nameEnum = request.getParameterNames();
       nameEnum.hasMoreElements(); ) {
           String name = (String) nameEnum.nextElement();
           if (name.startsWith(sign)) {
               return true;
           }
       }
       return false;
   }
   
   
    // }}}
    
   @Override
   protected boolean suppressBinding(HttpServletRequest request) {
       return false;
   } 
   
    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, 
            Object command, 
            BindException errors) 
    throws Exception 
    {
         
        final SiteCommand site = (SiteCommand) command;
        
        
        if (site.isNavigationalChange()) {
            SiteAction action = site.getAction();
            site.addHistory(action);
            action.onNavigationalChange(errors);
        }
        if (errors.hasErrors()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Data binding errors: " + errors.getErrorCount());
            }
            return showForm(request, response, errors);
        }
        else {
            SiteAction action = site.getAction();
            if (isChangeFormRequest(action, request)) {
                logger.debug("Detected action change request -> routing request to onActionSubmit");
                
                action.onChangeForm(new FormChange(action, request), errors);
                
            }           
            else if (isApplyFormRequest(action, request)) {
                logger.debug("Detected action submit request -> routing request to onActionSubmit");
                if (action instanceof FormAction) {
                    FormAction formAction = (FormAction) action;
                    formAction.validate(errors);
                    if (!errors.hasErrors()) {
                        formAction.apply(errors);
                    }
                }   
                //return showForm(request, response, errors);
            }
            else if (isCancelFormRequest(action, request)) {
                if (action instanceof FormAction) {
                    FormAction formAction = (FormAction) action;
                    formAction.cancel(errors);
                }   
                //return showForm(request, response, errors);
            }
            action.process();
            return showForm(request, errors, getSiteView());
        }
    }
        


    
    
    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, 
            BindException errors) throws Exception {

        
        return super.showForm(request, errors, getSiteView());
    }
    
    public void setDataCenter(IPath<String> path) {
        this.userScope = path;
    }
    public IPath<String> getDataCenter() {
        return userScope;
    }
    
    public void setRefsBase(IPath<String> path) {
        this.refsBase = path;
    }
    public IPath<String> getRefsBase() {
        return refsBase;
    }

    public void setTemplateBase(IPath<String> path) {
        this.tplBase = path;
    }
    public IPath<String> getTemplateBase() {
        return tplBase;
    }


    /**
     * Fetch the site command bound to a specific session.
     * @param request
     * @return
     */
    public SiteCommand getSiteCommand(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String s = getFormSessionAttributeName(request);
            SiteCommand siteCommand = (SiteCommand) session.getAttribute(s);
            return siteCommand;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected String getFormSessionAttributeName() {
        String s = super.getFormSessionAttributeName();
        return s;
    }
    
    




}