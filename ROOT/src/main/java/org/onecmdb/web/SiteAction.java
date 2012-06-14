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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.acegisecurity.AuthenticationException;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.utils.HashCodeUtil;
import org.springframework.validation.BindException;


/**
 * Actions are create via parameters passed into the system.
 * 
 * In subclasses
 * @author nogun
 *
 */
public class SiteAction implements Cloneable {
    private ItemId _actionId;
    private String _name;
    
    private SiteCommand _command;
    private String _displayName;
    private Map<String, Boolean> _predicates = new HashMap<String, Boolean>();

    /**
     * A map used to ask if this actions supports a specific feature
     * @return
     */
    public final Map<String, Boolean> getPredicates() {
        return this._predicates ;
        
    }
    
    public final void setDisplayName(String name) {
        this._displayName = name;
    }
    public final String getDisplayName() {
        return this._displayName != null ? this._displayName : getName();
    }
    
    
    // {{{ keeps a hierarchy of the actions
    private Map<String, SiteAction> subActionMap = new TreeMap<String,SiteAction>();
    // }}}
    
    
    private Map<String, Object> params = new HashMap<String, Object>(0) {
        @Override
        public Object put(String key, Object value) {
            return super.put(key, value);
        }
    };
    
    /** 
     * <p>A map, which guarantees all values put are of type 
     * <code>IValue</code>.</p>
     * 
     * <p>Also makes it possible to revert values, see {@link #revert}.</p>
     */
    public class IValueMap extends LinkedHashMap<String, Object /*IValue */> {
        private static final long serialVersionUID = -8681593439719854723L;
        private Map<String, Object> _revert = new HashMap<String, Object>();

        @Override
        public Object /* IValue */ put(String key, Object value) {
            if ( ! (value instanceof IValue) ) {
                IModelService modelsvc = (IModelService) getCommand().getSession().getService(IModelService.class);
                IType xsstring = modelsvc.getType("xs:string"); 
                String stringValue = (value instanceof String) 
                    ? (String) value
                    : (value != null) ? value.toString() : null;

                    value = xsstring.parseString(stringValue);
            }
            Object prev = get(key);
            if (prev != null) {
                _revert .put(key, prev);
            }
            
            return super.put(key,  value);
        }
        
        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            super.putAll(m);
        }

        
        @Override
        public Object remove(Object key) {
            _revert.remove(key);
            return super.remove(key);
        }
        
        @Override
        public void clear() {
            _revert.clear();
            super.clear();
        }
        
        /** 
         * Revert to the previous value for the selected key, as long as the
         * entry hasn't been removed, merely overwritten via {@link #put}
         * @param key
         */
        public void revert(String key) {
            Object prev = _revert.get(key);
            if (prev != null) {
                put(key, prev);
            }
            
        }
        
    };
    
    private IValueMap  _formParams = new IValueMap(); 
    
    /** 
     * Reset all parameter known by the action with a new ones
     * @param params The new map of parameters to be known by this action.
     */
    public final void setParams(Map<String, Object> params) {
        this.params.clear();
        this.params.putAll(params);
    }
    
    
    public void setFormParams(Map<String, Object /*IValue*/> formParams) {
        this._formParams.clear();
        this._formParams.putAll(formParams);
    }

    
    
    /**
     * Put a new parameter to this action.
     * @param _name
     * @param value
     */
    public final void addParam(String name, String value) {
        this.params.put(name, value);
    }

    /**
     * 
     * @return
     */
    public final Map<String, Object> getParams() {
        return this.params ;
    }
    
    public SiteAction(String text) {
        this._actionId = new ItemId();
        this._name = text;
    }

    public final ItemId getId() {
        return _actionId;
    }
    

    public final Map<String,SiteAction> getSubActionMap() {
        return subActionMap;
    }

    public final void addSubAction(SiteAction action) {
        subActionMap.put(action.getName(), action);
    }


    public final String getName() {
        return _name;
    }

    /** 
     * Every action has a <em>path</em> 
     * @return The path this action is 
     */
//    public final IPath<SiteAction> getActionPath() {
//        Path<SiteAction> path = new Path<SiteAction>();
//        path.addElement(this);
//        return path;
//    }

    
    
    @Override
    public final  int hashCode() {
        int h = HashCodeUtil.SEED;
        h = HashCodeUtil.hash(h, getName());
        h = HashCodeUtil.hash(h, getParams());
        return h;
    } 
    /**
     * Compares this action with another. Two actions are considered equal when
     * its _name and its parameters, that is {@link #getParams()} matches.
     */
    public final boolean equals(Object o) {
        if (o == null) return false;
        if ( !(o instanceof SiteAction) ) return false;
        
        SiteAction other = (SiteAction) o;
        if ( !getName().equals(other.getName()) ) return false;
        return getParams().equals(other.getParams());
    }

    
    /** 
     * Called when an onNavigationalChange occurs. Override
     * in subclasses.
     * @param errors TODO
     */
    protected void handleNavigationalChange(BindException errors) {
        
    }
    
    /** 
     * Return this actions <em>form parameters</em>, which should be seen as
     * a <em>temporary</em>. These parameters are rebound on every submission
     * until the form is <em>applied</em>. 
     * @return A map of values. The object stored is always a IValue.
     */
    public IValueMap /* IValue */ getFormParams() {
        return _formParams;
    }

    
    /**
     * <p>Produces a clone of this object, by <em>coping</em> only the relevant
     * parts:</p>
     * <ul>
     * <li>The _name</li>
     * <li>The currently set form parameters</li>
     * </ul>
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public final Object clone() {
        SiteAction clone = null;;
        try {
            clone = (SiteAction) super.clone();
            clone.params = new HashMap<String, Object>(getParams().size());
            for (Entry<String, Object> entry : getParams().entrySet()) {
                clone.params.put(entry.getKey(), entry.getValue());
            }
            
            clone._formParams = new IValueMap();
            for (Entry<String, Object /*IValue*/> entry :getFormParams().entrySet() ) {
                IValue v = (IValue) entry.getValue();
                clone._formParams.put(entry.getKey(), v);
            }
            
            //clone.predicates = new HashMap<String, Boolean>();
            //clone.predicates.putAll(_predicates);
            
            
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
    
    
    /** 
     * Callback used when a navigational change to this action occurs. Should
     * take care of initialization, etc.
     */
    public final void onNavigationalChange(BindException errors) {
        getFormParams().clear();
        handleNavigationalChange(errors);
    }
    

    
    
    
    @Override
    public String toString() {
        return getName() + getParams();
    }

    
    /** 
     * Called once, to initiate this action, when a new form is created, i.e.
     * a new session is opened. 
     * @param site
     * @param errors
     */
    public void onNewForm(SiteCommand site,  BindException errors) {
        this._command = site;
    }
    
    /**
     * Fetch the site _command associated with this action
     * @return
     */
    protected final SiteCommand getCommand() {
        return this._command;
    }

    /**
     * Whenever a <em>change form</em> is triggered this method is called, which 
     * actually forwards to the abstract method 
     * {@link #handleFormChange(FormChange, BindException)}. 
     * 
     * @param change
     * @param errors
     * @see #handleFormChange(FormChange, BindException)
     */
    public final void onChangeForm(FormChange change, BindException errors) {
            handleFormChange(change, errors);
    }

    /** 
     * <p>Used to actually act on an {@link #onChangeForm} event, and is supposed
     * to be overridden by subclasses.</p>
     * 
     * <p>This is kind a way to implement actions within an action, to be able
     * to process a longer action, for example, a wizard.</p>
     * 
     * @param change The event triggering the form change
     * @param errors
     */
    protected void handleFormChange(final FormChange change, 
            final BindException errors) {
        /* 
         * TODO: break apart into something like a command pattern, to reflect
         * the sub action characteristics.
         */
        if ("login".equals(change.getOperation())) {
            try {
                getSession().login();
            } catch (AuthenticationException e) {
                errors.rejectValue(change.getChangeExpr(), "LOGIN_FAILED", "Login Failed");
            }
            
            getCommand().reset();
            
        } else if ("logout".equals(change.getOperation())) {
            getSession().logout();
            

             getCommand().reset();

            
        }
        else if ("deleteCi".equals(change.getOperation())) {
            deleteCi(change, errors);
        }
        else if ("deleteMarked".equals(change.getOperation())) {
            deleteMarked(change, errors);
        }
    }

    /** Deletes a group a selected CIs, found in the CIMARK parameters */
    private void deleteMarked(FormChange change, BindException errors) {
        ISession session = getCommand().getSession();
        IModelService model = (IModelService) session.getService(IModelService.class);

        IType xsboolean = model.getType("xs:boolean");

        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        for (String name  : getFormParams().keySet()) {
            if (name.startsWith("CIMARK")) {
                IValue value = (IValue) getFormParams().get(name);
                if (value != null ) {
                    value = xsboolean.fromValue(value);
                    if ((Boolean) value.getAsJavaObject()) {
                        String lex = name.substring("CIMARK".length());
                        ItemId ciid = new ItemId(lex);
                        ICi ci = model.find(ciid);
                        if (ci != null) {
                            ICiModifiable tplci = tx.getTemplate(ci);
                            tplci.delete();
                        }
                    }
                }
            }
        }
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket);
        if (result.isRejected()) {
            errors.rejectValue(change.getChangeExpr(), "REJECT", 
                    new String[] { result.getRejectCause()},
                    result.getRejectCause());
        }
    }

    /** 
     * Processes a CI deletion
     * @param change
     * @param errors
     */
    private void deleteCi(FormChange change, BindException errors) {
        ItemId ciId = new ItemId(change.getArgs().get(0));   
        ISession session = getCommand().getSession();
        IModelService model = (IModelService) session.getService(IModelService.class);
        ICi ci = model.find(ciId);
        
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        {
            ICiModifiable tpl = tx.getTemplate(ci);
            tpl.delete();
        }
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket);
        if (result.isRejected()) {
            errors.rejectValue(change.getChangeExpr(), "REJECT", 
                    new String[] { result.getRejectCause()},
                    result.getRejectCause());
        }
    }


    /** before the form is shown, this method is called */
    public void process() {
        // do nothing
    }

    protected ISession getSession() {
        return getCommand().getSession();
    }

   
    
    

    



}
