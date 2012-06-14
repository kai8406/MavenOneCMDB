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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.onecmdb.core.ErrorObject;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.Multiplicity;
import org.onecmdb.core.internal.OneCmdb;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.web.SiteCommand.MemoryObject;
import org.springframework.validation.BindException;

public class EditCiAction extends CiSiteAction implements FormAction {

    public EditCiAction() {
        this("editci");
        setDisplayName("Edit");
    }
    protected EditCiAction(String name) {
        super(name);
    }

    
    @Override
    protected void handleNavigationalChange(BindException errors) {
        ICi ci = getCi();
        if (ci == null) {
            errors.reject("REJECT", new String[] {"No CI yet bound!"}, "No CI yet bound!" );
            return;
        }
        
        IModelService modelsvc = (IModelService) getCommand().getSession()
            .getService(IModelService.class);
        
        IType xsstring = modelsvc.getType("xs:string");
        IType xsbool   = modelsvc.getType("xs:boolean");
        
        for (IAttribute attr : ci.getAttributes()) {
            String key = "ATTR"+attr.getId();
            if ( !getFormParams().containsKey(key) ) {
                IValue v = attr.getValue();
                setFormParam(attr, v);
            }
        }

        // there are some special treatments

        {
            String expr = ci.getAlias();
            IValue v = xsstring.parseString(expr);
            String key = "ALIAS";
            if (!getFormParams().containsKey(key)) {            
            getFormParams().put(key, v != null ? v : xsstring.getNullValue());
            }
        }
        {
            String expr = ci.getDisplayNameExpression();
            IValue v = xsstring.parseString(expr);
            String key = "DISPEXPR";
            if (!getFormParams().containsKey(key)) {            
            getFormParams().put(key, v != null ? v : xsstring.getNullValue());
            }
        }
        {
            String expr = ci.getDescription();
            IValue v = xsstring.parseString(expr);
            String key = "DESCR";
            if (!getFormParams().containsKey(key)) {            
                getFormParams().put(key, v != null ? v : xsstring.getNullValue());
            }
        }

        
        {
            boolean expr = ci.isBlueprint();
            IValue v = xsbool.parseString(""+expr);
            String key = "BLUEPRINT";
            if (!getFormParams().containsKey(key)) {            
                getFormParams().put(key, v != null ? v : xsbool.getNullValue());
            }
        }
    }
    
//  M+ (can we use the stored object to fill in values
    public boolean isRecallable() {
        MemoryObject mem =  (MemoryObject) getCommand().getGlobals().get("mem");
        if (mem != null) {
            ICi ci = getCi();
            ICi rclci = mem.getCi();
            if (ci != null && rclci != null) {
                IPath<IType> ciPath = ci.getOffspringPath();
                IPath<IType> rclciPath = rclci.getOffspringPath();
                return (rclciPath.isParent(ciPath) || rclciPath.isSibling(ciPath));
            }
        }
        return false;
    }
    
    
    
    public void validate(BindException errors) {
        for (IAttribute attr : getCi().getAttributes()) {
            String key = "ATTR"+attr.getId();
            if ( getFormParams().containsKey(key) ) {
                IValue       v = (IValue) getFormParams().get(key);
                IValue current = attr.getValue();
                if ( (v != null && !v.equals(current)) 
                     || (current != null && !current.equals(v)) ) 
                { 
                    IType type = attr.getValueType();
                    if (type == null) {
                        OneCmdb.getLogger(EditCiAction.class).warn("Cannot validate. No type attached on attribute '" + attr.getAlias() +"'");
                    } else {
                        ErrorObject error = type.validate(v);
                    }
                }
            }
        }
        if (getFormParams().containsKey("ALIAS")) {
            IValue alias = (IValue) getFormParams().get("ALIAS");
            String s = alias.getAsString();
            if ( s == null || "".equals(s) ) {
                errors.rejectValue("action.formParams[ALIAS]", 
                        "ERROR_CANNOTBEEMPTY", new String[] { "Cannot be empty" }, "{0}");
            } else if (s.indexOf(' ') != -1) {
                errors.rejectValue("action.formParams[ALIAS]", 
                        "ERROR_CANNOTCONTAINSPACES", new String[] {"Cannot contain spaces" }, "{0}");
            }
        }
    }
    
    public void apply(BindException errors) {
        // clean up...
        
        ISession session = getCommand().getSession();
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        
        ICmdbTransaction tx = ccb.getTx(session);
        ICi ci = getCi();
        for (IAttribute attr : ci.getAttributes()) {
            String attrKey = "ATTR"+attr.getId();
            if (getFormParams().containsKey(attrKey)) {
                IValue newValue = (IValue) getFormParams().get(attrKey);
                IAttributeModifiable tpl = tx.getAttributeTemplate(attr);

                if (attr.getMaxOccurs() != 1 && newValue == null) {
                    // TODO: think of
                    //tpl.delete();
                    
                    
                } else {
                    IValue currentValue = attr.getValue();
                    if ( (newValue != null && !newValue.equals(currentValue)) 
                            || (currentValue != null && !currentValue.equals(newValue)) ) { 

                        tpl.setValue(newValue);

                    }
                }
                
            }
        } 

        final ICiModifiable tpl = tx.getTemplate(ci);
        {
            String attrKey = "ALIAS";
            if (getFormParams().containsKey(attrKey)) {
                IValue newValue = (IValue) getFormParams().get(attrKey);
                tpl.setAlias(newValue.getAsString());
            }
        }
        {
            String attrKey = "DISPEXPR";
            if (getFormParams().containsKey(attrKey)) {
                IValue newValue = (IValue) getFormParams().get(attrKey);
                tpl.setDisplayNameExpression(newValue.getAsString());
            }
        }
        {
            String attrKey = "DESCR";
            if (getFormParams().containsKey(attrKey)) {
                IValue newValue = (IValue) getFormParams().get(attrKey);
                tpl.setDescription(newValue.getAsString());
            }
        }
        { // toggle the blueprint status?
            String attrKey = "BLUEPRINT";
            if (getFormParams().containsKey(attrKey)) {
                IModelService modelsvc = (IModelService) getCommand().getSession()
                    .getService(IModelService.class);

                IType xsbool   = modelsvc.getType("xs:boolean");
                
                IValue newValue = (IValue) getFormParams().get(attrKey);
                if (newValue == null) {
                    newValue = xsbool.parseString("false");
                }
                if (!newValue.getValueType().equals(xsbool)) {
                    newValue = xsbool.parseString(newValue.getAsString());
                }
                
                IValue oldValue; {
                    boolean b = ci.isBlueprint();
                    oldValue = xsbool.parseString(""+b);
                }
                if (!oldValue.equals(newValue)) {
                    // toggle the value
                    tpl.setIsBlueprint((Boolean) newValue.getAsJavaObject());
                }    
            }
        }

        
        
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket);
        
        if(result.isRejected()) {
            errors.reject("REJECTED", new String[] { result.getRejectCause() }, "{0}" );
        } 
        else {
            forward();
            clearCache();
        }
    }
    protected void forward() {
        SiteAction current = getCommand().getAction();
        CiSiteAction action = (CiSiteAction) getCommand().getActionMap().get("viewci");
        action.clearCache();
        action.setParams(current.getParams());

        for (SiteAction h : getCommand().getHistory()) {
            if (h.equals(action)) {
                ((CiSiteAction) h).clearCache(); 
            }
        }
        
        
        getCommand().setAction("viewci");
    }

    public final void setReturnTo(String navigate) {
        if (navigate == null) {
            getParams().remove("RETURNTO");
        } else 
            getParams().put("RETURNTO", navigate);
    }
    protected final String getReturnTo() {
        return (String) getParams().get("RETURNTO");
    }


    public final void setReturnParam(String paramKey) {
        if (paramKey == null) {
            getParams().remove("RETURNPARAM");
        } else 
            getParams().put("RETURNPARAM", paramKey);
    }
    protected final String getReturnParam() {
        return (String) getParams().get("RETURNPARAM");
    }
    
    public final void setReturnHash(String returnHash) {
        if (returnHash == null) {
            getParams().remove("RETURNHASH");
        } else 
            getParams().put("RETURNHASH", returnHash);
    }

    
    
    
    /** 
     * Empty implementation. Nothing is really changed.
     */
    public void cancel(BindException errors) {
        // can we undo changes, then we sould implement such a feature
        
        ISession session = getCommand().getSession();
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        ICi ci = getCi();
        boolean submit = false;
        for (IAttribute attr : ci.getAttributes()) {
            String attrKey = "ATTR"+attr.getId();
            if (getFormParams().containsKey(attrKey)) {
                IValue newValue = (IValue) getFormParams().get(attrKey);
                IAttributeModifiable tpl = tx.getAttributeTemplate(attr);
                if (attr.getMaxOccurs() != 1 && newValue == null) {
                    tpl.delete();
                    submit = true;
                } 
                
            }
        }
        if (submit) {
            ITicket ticket = ccb.submitTx(tx);
            IRfcResult result = ccb.waitForTx(ticket);
            
            if(result.isRejected()) {
                String applyExpr = "action.formParams["+ getName() + "]";
                errors.reject("REJECTED", new String[] { result.getRejectCause() }, "{0}" );
                
            }
        }
        
        forward();
    }
    
    

    /**
     * TODO: Break up into classes (command pattern), instead of the mega switch
     */
    @Override
    protected void handleFormChange(FormChange change, BindException errors) {
        if ("addValue".equals(change.getOperation())) {
            addValue(change, errors);
        } 
        else if ("removeValue".equals(change.getOperation())) {
            removeValue(change, errors);
        }
        else if ("addAttr".equals(change.getOperation())) {
            addAttr(change, errors);
        } 
        else if ("deleteAttr".equals(change.getOperation())) {
            deleteAttr(change, errors);
        }
        else if ("rclCi".equals(change.getOperation())) {
            recallMem(change, errors);
        }
        else {
            super.handleFormChange(change, errors);
        }
    }
    
    private void addAttr(FormChange change, BindException errors) {
        ICi ci = getCi();
        ISession session = getCommand().getSession();
        IModelService modelsvc = (IModelService) session.getService(IModelService.class);
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        {
            final IValue alias;
            IValue dispExpr = change.getParamValue("dispExpr");
            if (dispExpr == null || "".equals(dispExpr.getAsString())) {
                errors.rejectValue(change.getParamExpr("dispExpr"), 
                        "ERROR_CANNOTBEEMPTY", "Cannot be empty");
                
                alias = null;
            } else {
                // derive an alias
                IType xsstring = modelsvc.getType("xs:string");

                String s = dispExpr.getAsString();
                s = s.replaceAll("\\W", " ");
                String t = "";
                for (String p : s.split(" ")) {
                    p = p.substring(0, 1).toUpperCase() +
                    (p.length() > 1 ? p.substring(1) : "");
                    t += p;
                    
                }
                s = t;
                if ("".equals(s)) {
                    errors.rejectValue(change.getParamExpr("dispExpr"), 
                            "ERROR_CANNOTBEEMPTY", "Too few letters" );
                
                    alias = null;
                } else {
                    s = s.substring(0, 1).toLowerCase() +
                    (s.length() > 1 ? s.substring(1) : "");

                    IValue prefix = change.getParamValue("prefix");
                    if (!prefix.isNullValue() && !"".equals(prefix.getAsString()) ) {
                        s = prefix.getAsString() + "_" + s;
                    }
                    alias = xsstring.parseString(s);
                }
            }
            
            IValue description = change.getParamValue("description");
            final IType  type; {
                IValue _type = change.getParamValue("type");
                String s = _type.getAsString();
                if (s.startsWith("ID:") && s.length() > 3) {
                    s = s.substring(3);
                    ItemId typeId = new ItemId(s);
                    type = modelsvc.getType(typeId);
                } else if (!s.startsWith("ID:") && s.length() > 0) {
                    type = modelsvc.getType(s);
                } else {
                    type = null;
                }
            }
            if (type == null)  {
                errors.rejectValue(change.getParamExpr("type"), 
                "ERROR_CANNOTBEEMPTY", "Cannot be empty" );
            }
            
            
            final IType reftype; {
                IReferenceService refsvc = (IReferenceService) session.getService(IReferenceService.class);
                IValue _refype = change.getParamValue("typeref");
                if (_refype == null) {
                    reftype = null;
                } else { 
                    String s = _refype.getAsString();
                    if (s.startsWith("ID:") && s.length() > 3) {
                        s = s.substring(3);
                        ItemId reftypeId = new ItemId(s);
                        reftype = refsvc.getRefType(reftypeId);
                    } else if (!s.startsWith("ID:")  && s.length() > 0) {
                        reftype = refsvc.getRefType(s);
                    } else {
                        reftype = null;
                    }
                }
            }

            
            Multiplicity mult = null;
            {
               int minI = -1; {
                   // these arrives as strings
                   IValue min = change.getParamValue("mult.min");
                   if (min == null || min.getAsJavaObject() == null || min.getAsJavaObject().equals(""))  {
                       errors.rejectValue(change.getParamExpr("mult.min"), 
                       "ERROR_CANNOTBEEMPTY", "Lower bound can not be empty" );
                   } 
                   else {
                       String minS = (String) min.getAsJavaObject();
                       try {
                           minI = Integer.parseInt(minS);
                       } catch (NumberFormatException e) {
                           errors.rejectValue(change.getParamExpr("mult.min"), 
                                   "REJECT", "Invalid number for lower bound: " + minS);
                       }
                   }
               }
               
               int maxI = -1; {
                   // these arrives as strings
                   IValue max = change.getParamValue("mult.maxInf");
                   if (max == null) { 
                       max = change.getParamValue("mult.max");
                   }
                   if (max == null || max.getAsJavaObject() == null || max.getAsJavaObject().equals(""))  {
                       errors.rejectValue(change.getParamExpr("mult.max"), 
                       "ERROR_CANNOTBEEMPTY", "Upper bound cannot be empty");
                   } 
                   else {
                       String maxS = (String) max.getAsJavaObject();
                       
                       try {
                           maxI = "n".equalsIgnoreCase(maxS) ? Multiplicity.UNBOUND : 
                               Integer.parseInt(maxS);
                       } catch (NumberFormatException e) {
                           errors.rejectValue(change.getParamExpr("multMax"), 
                                   "REJECT", "Invalid number for upper bound: " + maxS);
                       }
                   }
               }
               
               if (!errors.hasErrors()) {
                   mult = new Multiplicity(minI, maxI);
               }
            }
            
            if (errors.hasErrors()) {
                return;
            } 
            ICiModifiable tpl = tx.getTemplate(ci);
            IAttributeModifiable newAttr = tpl.createAttribute(alias.getAsString(), 
                    type, reftype, 
                    mult.getMin(), mult.getMax(), null);                
            
            newAttr.setDisplayNameExpression(dispExpr.getAsString());
            newAttr.setDescription(description.getAsString());
            
            ITicket ticket = ccb.submitTx(tx);
            IRfcResult result = ccb.waitForTx(ticket);
            
            if (result.isRejected()) {
                errors.rejectValue(change.getChangeExpr(), "REJECT", 
                        new String[] {result.getRejectCause()},
                        result.getRejectCause());
                
                return;
            }
                
            // clear the attributes...
            for (Iterator<Entry<String, Object /*IValue */>> entryIter = getFormParams().entrySet().iterator();
            entryIter.hasNext(); ) {
                change.getChangeExpr();
                Entry<String, Object /*IValue*/> entry = entryIter.next();
                if (entry.getKey().startsWith("addAttr(")) {
                    entryIter.remove();
                }
            }
            errors.rejectValue(change.getChangeExpr(), "SUCCESS", 
                    new String[] {"Attribute added"},
            "Attribute added");
            
            handleNavigationalChange(errors);
        }
    }
    private void recallMem(FormChange change, BindException errors) {
        String alias = change.getArgs().get(0);
        MemoryObject mem = (MemoryObject) getCommand().getGlobals().get("mem");
        ICi memci = mem.getCi();
        
        // put the stored CI's values
        handleRecallFrom(memci, errors);
    }
    private void deleteAttr(FormChange change, BindException errors) {
        String alias = change.getArgs().get(0);   
        ICi ci = getCi();
        
        ISession session = getCommand().getSession();
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        {
            for (IAttribute attr : ci.getAttributes()) {
                if (alias.equals(attr.getAlias())) {
                    IAttributeModifiable tpl = tx.getAttributeTemplate(attr);
                    tpl.delete();
                }
             }
        }
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket);

        if (result.isRejected()) {
            errors.rejectValue(change.getChangeExpr(), "REJECT", 
                    new String[] {result.getRejectCause()},
                    result.getRejectCause());
        }
    }
    private void removeValue(FormChange change, BindException errors) {
        ItemId attrId = new ItemId(change.getArgs().get(0));   
        ISession session = getCommand().getSession();
        ICcb ccb = (ICcb) session.getService(ICcb.class);
        ICmdbTransaction tx = ccb.getTx(session);
        {
            ICi ci = getCi();
            IAttribute attr = ci.getAttributeWithId(attrId);
            IAttributeModifiable tpl = tx.getAttributeTemplate(attr);
            tpl.delete();
        }
        ITicket ticket = ccb.submitTx(tx);
        IRfcResult result = ccb.waitForTx(ticket);

        if (result.isRejected()) {
            errors.rejectValue(change.getChangeExpr(), "REJECT", result.getRejectCause());
        }
    }
    private void addValue(FormChange change, BindException errors) {
        ISession session = getCommand().getSession();
        IModelService modelsvc = (IModelService) session.getService(IModelService.class);
        ICcb ccb = (ICcb) session.getService(ICcb.class);

        final ICi ci = getCi();
        final String attrKey = change.getArgs().get(0);
        final IAttribute attr = getAttribute(attrKey); 

        IValue newValue = null; {
            newValue = (IValue) getFormParams().get(attrKey);
            if ( !(newValue instanceof ICi) && newValue != null ) {
                String s = newValue.getAsString();
                if (s.startsWith("ID:") && s.length() > 3) {
                    s = s.substring(3);
                    ItemId typeId = new ItemId(s);
                    newValue = modelsvc.find(typeId);
                } else if (!s.startsWith("ID:")) {
                    // a simple type is what whar we should bind to...
                    
                    // must fetch the ``type'' we are adding
                    IType attrType = null; { 
                        for (IAttribute addable : ci.getAddableAttributes()) {
                            if (addable.getAlias().equals(attr.getAlias())) {
                                attrType = addable.getValueType();
                                break;
                            }
                        }
                    }
                    newValue = attrType.fromValue(newValue);
                }
            }
        }
        if (newValue == null) {
            
            errors.rejectValue(change.getChangeExpr(), "NOVALUE", 
            "A value must must be selected");

        
        } else {
        
            ICmdbTransaction tx = ccb.getTx(session);
            
            // set the value on the exisiting attribute
//                IAttributeModifiable attrtpl = tx.getAttributeTemplate(attr);
//                attrtpl.setValue(newValue);
            
            // add a new slot 
            ICiModifiable citpl = tx.getTemplate(ci);
            IAttributeModifiable newSlot = citpl.addAttribute(attr.getAlias());
            newSlot.setValue(newValue);
            
            
            
            ITicket ticket = ccb.submitTx(tx);
            IRfcResult result = ccb.waitForTx(ticket);
            if (result.isRejected()) {
                errors.rejectValue(change.getChangeExpr(), "REJECT", 
                        new String[] {result.getRejectCause()},
                        result.getRejectCause());
            } else {
                
                getFormParams().remove(attrKey);
                
            }
        }
    }
    
    
    
    
    private void handleRecallFrom(ICi memci, BindException errors) {
        IModelService modelsvc = (IModelService) getCommand().getSession()
        .getService(IModelService.class);
        IType xsstring = modelsvc.getType("xs:string");
        IType xsbool = modelsvc.getType("xs:boolean");

        final ICi ci = getCi();
        Set<String> processed = new HashSet<String>();

        // {{{ copy
        // copy all attributes compatible, from the memory stored
        // CI to the current, hold by this action
        for (IAttribute memAttr : memci.getAttributes()) {
            String attrAlias = memAttr.getAlias();
            if (processed.contains(attrAlias)) 
                continue;
            processed.add(attrAlias);
            
            List<IAttribute> memAttrs = memci.getAttributesWithAlias(attrAlias);
            List<IAttribute> attrs = ci.getAttributesWithAlias(attrAlias);
            if (memAttr.getMaxOccurs() == 1 && memAttr.getMinOccurs() == 1 && attrs.size() == 1) {
                // single valued
                IAttribute attr = attrs.get(0);
                memAttr = memAttrs.get(0);
                setFormParam(attr, memAttr.getValue());
            } else { 
                // replace all attributes already set
                Iterator<IAttribute> memAttrIter = memAttrs.iterator(); 
                Iterator<IAttribute> attrIter = attrs.iterator(); 
                
                ISession session = getCommand().getSession();
                ICcb ccb = (ICcb) session.getService(ICcb.class);
                ICmdbTransaction tx = ccb.getTx(session);

                while (memAttrIter.hasNext()) {
                    IAttribute recallAttr = memAttrIter.next();
                    final IAttributeModifiable attrtpl; {
                        if (attrIter.hasNext()) {
                            // replace the current value
                            
                            IAttribute replaceAttr = attrIter.next();
                            attrtpl = tx.getAttributeTemplate(replaceAttr);

                        } else {
                            if (canAdd(ci, recallAttr)) {
                                ICiModifiable citpl = tx.getTemplate(ci);
                                attrtpl = citpl.addAttribute(recallAttr.getAlias());
                            } else {
                                attrtpl = null;
                            }
                        }
                    }
                    if (attrtpl != null) {
                        attrtpl.setValue(recallAttr.getValue());
                    }
                }
                while (attrIter.hasNext()) {
                    IAttribute deleteAttr = attrIter.next();
                    IAttributeModifiable deltpl = tx.getAttributeTemplate(deleteAttr);
                    deltpl.delete();
                }
                ITicket ticket = ccb.submitTx(tx);
                IRfcResult result = ccb.waitForTx(ticket);
                if (result.isRejected()) {
                    errors.rejectValue(null, "REJECT", 
                            new String[] {result.getRejectCause()},
                            result.getRejectCause());
                }
            }
        }
        // }}}

        
        
        //setFormParam("ALIAS",    xsstring, memci.getAlias());
        setFormParam("DISPEXPR", xsstring, memci.getDisplayNameExpression());
        setFormParam("DESCR",    xsstring, memci.getDescription());
        setFormParam("BLUEPRINT",  xsbool, memci.isBlueprint());

            
        
        
    }
    
    private boolean canAdd(final ICi ci, final IAttribute recallAttr) {
        boolean canAdd = false;
        for (IAttribute addable : ci.getAddableAttributes()) {
            if (addable.getAlias().equals(recallAttr.getAlias())) {
                canAdd = true;
                break;
            }
        }
        return canAdd;
    }

    
    
    private void setFormParam(String key, IType type, Object value) {
        IValue v = type.parseString(value.toString());
        if (getFormParams().containsKey(key)) {            
            getFormParams().put(key, v != null ? v : type.getNullValue());
        }
        
    }

    private void setFormParam(IAttribute attr, IValue value) {
        String key = "ATTR"+attr.getId();
        if ( !getFormParams().containsKey(key) ) {
            if (value == null) {
                IModelService modelsvc = (IModelService) getCommand().getSession()
                .getService(IModelService.class);
            
                IType xsstring = modelsvc.getType("xs:string");
                IType vt = attr.getValueType();
                if (vt == null) {
                    value = xsstring.parseString("");
                } else {
                    value = vt.getNullValue();
                }                    
            }
        }
        getFormParams().put(key, value);
    }



    
    
    private IAttribute getAttribute(String attrKey) {
        ICi ci = getCi();
        for (IAttribute attr : ci.getAttributes()) {
            if (attrKey.equals("ATTR"+attr.getId())) {
                return attr;
            }
            if (attrKey.equals(attr.getAlias())) {
                return attr;
            }
        }
        for (IAttribute attr : ci.getAddableAttributes()) {
            if (attrKey.equals(attr.getAlias())) {
                return attr;
            }
        }
        return null;
    }
    private IValue getSelectValue(IAttribute attr, IValue value) {
        IModelService modelsvc = (IModelService) getCommand().getSession()
            .getService(IModelService.class);
        
        
        if (value != null) {
            String s = value.getAsString();
            if (s.startsWith("ID:") && s.length() > 3) {
                s = s.substring(3);
                ItemId typeId = new ItemId(s);
                value = modelsvc.find(typeId);
            } else if (!s.startsWith("ID:")) {
                // a simple type is what whar we should bind to...
                
                // must fetch the ``type'' we are adding
                IType attrType = null; { 
                    for (IAttribute addable : attr.getOwner().getAddableAttributes()) {
                        if (addable.getAlias().equals(attr.getAlias())) {
                            attrType = addable.getValueType();
                            break;
                        }
                    }
                }
                value = attrType.fromValue(value);
            }
        }
        return value;
    }
    
    
}
