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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IJobService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;

/**
 * 
 * Every new _session in the web application will be given a new instance of  
 * this class, and is responsible to keep track of the user's _session data.</p>
 * 
 * <p><b>Note:</b> Even though the web application is deployed as a servlet
 * application, the servlet _session mechanism is hidden by the use of this
 * class.</p> 

 * 
 * The command object represented by this class, site, and updated via 
 * submissions from the view. 
 * 
 * <p>Each _session will have a single instance of this object, and will
 * operate as a session state object.</p>
 */
public class SiteCommand {
    
    private SiteAction _previous;
    private SiteAction _action;
    private ISession _session;
    private SiteAction _rootAction;

    private String mode = "design";
    
    private final List<SiteAction> _history = new ArrayList<SiteAction>();
    
    /** all actions recognized. The values should be considered templates, which 
     * are cloned when the user <em>enters</em> a actions.
     */
    private final Map<String,SiteAction> _actionMap = new HashMap<String,SiteAction>();
    
    private boolean debug = false;
    private String _navigate;
    
    /** the controller <em>in charge</em> */
    private SiteController _controller;

    
    public class MemoryObject {
        private ItemId _id;
        volatile private ICi _ci;
        
        public void setId(ItemId id) {
            _id = id;
            _ci = null;
        }

        public void setCi(ICi ci) {
            setId(ci.getId());
            _ci = ci;
        }
        
        public ItemId getId() {
            return _id;
        }

        public ICi getCi() {
            if (_ci == null && _id != null) {
                IModelService modelsvc = (IModelService) getSession().getService(IModelService.class);
                setCi(modelsvc.find(_id));
            }
            return _ci;
        }

    }
    
    

    /** default no-arg constructor */
    public SiteCommand() {
        super();
    }
    
    
    
    public void setDebugEnabled(boolean b) {
        this.debug = b;
    }
    public boolean isDebugEnabled() {
        return this.debug;
    }
    
    

    /** Recursively adds all actions passed in */
    private void addAction(final SiteAction action) {
        if ( !_actionMap.containsKey(action.getName()) ) {
            _actionMap.put(action.getName(), action);
        }
        for (SiteAction subaction : action.getSubActionMap().values()) {
            if ( !_actionMap.containsKey(subaction.getName()) ) {
                addAction(subaction);
            }
        }
    }
    
    public final String getVersion() {
        return "CORE v"  + org.onecmdb.core.Version.getVersionString() 
                + "; GUI v" + org.onecmdb.web.Version.getVersionString();
        
    }
    
    public final long getCurrenttimeMillis() {
        return System.currentTimeMillis();
    }
    

    private void setSession(ISession session) {
        if (_session == null)
            _session = session;
        else {
            throw new IllegalStateException("Already have a session");
        }
    }
    
    public void setRootAction(SiteAction action) {
        _rootAction = action;
    }
    
    public SiteAction getRootAction() {
        return _rootAction;
    }

    public ISession getSession() {
        return _session;
    }

    /** 
     * Set the action to use, using the name to look up the actual one. When
     * found (in the action map) it is cloned, to make sure
     * the user is given an action holding no previous state. 
     * @param actionName The name of the action to use
     * @throws IllegalArgumentException in case the action with the name can
     * not be found.
     */
    public void setAction(String actionName) {
        SiteAction actionTemplate = _actionMap.get(actionName);
        if (actionTemplate == null) {
            throw new IllegalArgumentException("Unknown action: " + actionName);
        }
        
        SiteAction newAction = (SiteAction) actionTemplate.clone(); 
        setAction(newAction);
    }
    
    private void setAction(SiteAction newAction) {
        updatePrevious();
        _action = newAction;
    }

    /** current _action */
    public SiteAction getAction() {
        return _action;
    }
    /** the previously used _action */
    public void updatePrevious() {
        if (_action != null && !_action.equals(_previous) ) { 
            _previous = (SiteAction) _action;
        }
    }
    public SiteAction getPrevious() {
        return _previous;
    }
    
    /** 
     * All actions defined for this command, i.e. session, considered 
     * templates. 
     */
    public Map<String,SiteAction> getActionMap() {
        return _actionMap;
    }
    
    /** make sure we update the _history */
    public final void setSubmit(String submit) {
        setAction(getAction());
    }


    SiteAction getHistory(String hash) {
        SiteAction history = null;
        if (hash.startsWith("#")) {
            String number = hash.substring(1);
            int abs = Integer.parseInt(number);
            history = getHistory().get(abs);
        }
        return history;
    }
    
    /**
     * <p>Set the navigational information for a request, indicating the 
     * <em>view</em> to process.</p>
     * 
     * @param navigate A string representing the navigational operation; can
     * indicate a history item using one of 
     * <ol>
     * <li>an actual number (<code>#nnn</code>)
     * <li>a relative number (<code>+/-nnn</code>)
     * </ol>
     * or a a name of an existing action.
     */
    public void setNavigate(String navigate) {
        navigate = navigate.split(",")[0];
        _navigate = navigate;
        
        
        final SiteAction action;
        if (navigate.startsWith("#")) {
            // _navigate to specific history item
            
            String number = navigate.substring(1);
            int abs = Integer.parseInt(number);
            action = getHistory().get(abs);
          
            int i = abs + 1;
            while (getHistory().size() > i) {
                getHistory().remove(i);
            }            
            
            
        } else if (navigate.startsWith("-") || navigate.startsWith("+")) {
            // _navigate relative current _action (_history not altered)
            String number = navigate;
            int rel = Integer.parseInt(number);

            int abs = 0; for (SiteAction history : getHistory()) {
                if (history.equals(getAction())) {
                    break;
                }
                abs++;
            }
            action = getHistory().get(abs + rel);

            
        } else if (!"".equals(navigate)) {
            action = (SiteAction) getActionMap().get(navigate).clone();
        
        } else {
            action = getAction();
        }
        setAction(action);
    }

    public String getNavigate() { 
        return _navigate;
    }


    // {{{ handle the navigation _history 

    
    /** 
     * indicates whether a navigational change has occurred.
     */
    public boolean isNavigationalChange() {
        return !getAction().equals(getPrevious());
    }
    

    
    public int getHistoryLength() {
        return _history.size();
    }
    public String getCurrentHistory() {
        final SiteAction current = getAction();
        int index = 0;
        for (SiteAction action : getHistory()) {
            if (action.equals(current)) {
                return "#" + index;
            }
            index++;
        }
        return "#0";
    }
    
    public void setHistory(SiteAction action) {
        _history.clear();
        addHistory(action);
    }
    public void setHistory(List<SiteAction> actions) {
        _history.clear();
        for (SiteAction action : actions) {
            addHistory(action);
        }
    }
    public List<SiteAction> getHistory() {
        return _history;
    }
    public void addHistory(SiteAction action) {
        if (action.equals(getPrevious())) {
            return;
        }

        int last = _history.size();
        if (last > 0) {
        SiteAction lastAction = _history.get(last - 1);
            if (action.equals(lastAction)) {
                return;
            }
        }
        
        // the _history keeps a clone of the actual _action 
        //SiteAction copy = (SiteAction) action.clone();
        _history.add(action);

/*        if (!this.history.contains(copy)) {
            if (!this.history.isEmpty()) {
                SiteAction lastAction = this.history.get(this.history.size() -1);
                if (!lastAction.equals(copy)) {
                    this.history.add(copy);
                }
            } else {
                this.history.add(copy);
            }
        }
*/        
    }
    // }}}

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }



    
    public Set<IType> getAllTypes() {
        IModelService model = (IModelService) getSession().getService(IModelService.class);

        IPath<String> base = getController().getTemplateBase();
        
        Set<IType> types = model.getAllBuiltInTypes(); 
        types.addAll(model.getAllComplexTypes(base));
        
        return types;
    }
    
    public Set<IType> getSimpleTypes() {
        IModelService model = (IModelService) getSession().getService(IModelService.class);
        Set<IType> types = model.getAllBuiltInTypes();
        return types;
    }
    
    /** 
     * Return all types, keyed according to the implementation used; either
     * <em>complex</em>, or <em>simple</em>
     * @return
     */
    public Map<String, Set<IType>> getTypeMap() {
        final Comparator<IType> comparator = new Comparator<IType>() {
            public int compare(IType o1, IType o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }};
        
       TreeMap<String, Set<IType>> typeMap = new TreeMap<String, Set<IType>>();

       
       IModelService model = (IModelService) getSession().getService(IModelService.class);
       Set<IType> builtinTypes = model.getAllBuiltInTypes();
       for (IType type : builtinTypes) {
    	   // simple
     	   Set<IType> types ;
     	   String typeName="Built-In";
    	   types = typeMap.get(typeName);
    	   if (types == null) {
    		   types = new TreeSet<IType>(comparator);
    		   typeMap.put(typeName, types);
    	   }
           types.add(type);
       }
       
       IPath<String> base = getController().getTemplateBase();
       Set<IType> complexTypes = model.getAllComplexTypes(base);
       for (IType type : complexTypes) {
    	   // complex
    	   Set<IType> types ;
      	   String typeName="User Defined";
    	   types = typeMap.get(typeName);
    	   if (types == null) {
    		   types = new TreeSet<IType>(comparator);
    		   typeMap.put(typeName, types);
    	   }
           types.add(type);
       }
       
       return typeMap;
       
    }
    
    
    
    
    public Set<IType> getAllRefTypes() {
        IReferenceService refsvc = (IReferenceService) getSession().getService(IReferenceService.class);
        IPath<String> base = getController().getRefsBase();
        Set<IType> refs = refsvc.getAllReferences(base);
        return refs;
    }

    public void setController(SiteController controller) {
        _controller = controller;
    }
    
    protected SiteController getController() {
        return _controller;
    }
    

    
    
    // {{{ simple global storage
    private final Map<String,Object> _globalData = new HashMap<String, Object>();
    public Map<String,Object> getGlobals() {
        return _globalData;
    }
    
    // }}}

    void reset() {

        List<SiteAction> hist = getHistory();
        while (hist.size() > 1) {
            hist.remove(1);
        }            
        
        
        _globalData.put("graphData", new GraphCommand());
        
        _globalData.put("showSearch", true);
        _globalData.put("showHistory", true);
        _globalData.put("showActions", false);
        _globalData.put("showTemplateChain", true);
        
        _globalData.put("showGraph",      false);
        _globalData.put("showAttributes", true);
        _globalData.put("showReferences", true);

        
        // inititiate the MEM placeholder
        _globalData.put("mem", new MemoryObject());

        


        
        setAction(getRootAction().getName());
        
    }
    
    void init() {

        
        ISession newSession = getController().getOneCmdb().createSession();
        setSession(newSession);
        
        
        if (!getActionMap().isEmpty()) {
            throw new IllegalStateException("Already initialized");
        }
        
            
        // TODO: inject from outside

        SiteAction test = new SiteAction("test");
        addAction(test);

        SiteAction denied = new SiteAction("denied");
        denied.setDisplayName("Access Denied");
        addAction(denied);


        SiteAction homeAction = new SiteAction("instructions");
        homeAction.setDisplayName("Instructions");

        SiteAction helpAction = new SiteAction("help");
        helpAction.setDisplayName("Help");
        homeAction.addSubAction(helpAction);

        SiteAction searchResultAction =new SearchResultAction();
        addAction(searchResultAction);

        SiteAction addiconAction = new AddIconAction();
        homeAction.addSubAction(addiconAction);

        ViewCiAction viewAction = new ViewCiAction();
        addAction(viewAction);

        EditCiAction editAction = new EditCiAction();
        addAction(editAction);

        AddCiAction addCiAction = new AddCiAction();
        addAction(addCiAction);

        ViewChangeLogAction chlogAction = new ViewChangeLogAction();
        addAction(chlogAction);

        viewAction.addSubAction(viewAction);
        viewAction.addSubAction(editAction);
        viewAction.addSubAction(chlogAction);

        editAction.addSubAction(viewAction);
        editAction.addSubAction(editAction);
        editAction.addSubAction(chlogAction);

        chlogAction.addSubAction(viewAction);
        chlogAction.addSubAction(editAction);
        chlogAction.addSubAction(chlogAction);


        //homeAction.addSubAction(viewAction);
        addAction(homeAction);
        setRootAction(homeAction);


        
        
        
        
        
        
        reset();
        
        

        _globalData.put("__init", true);

        
    }
    
    public List<ICi> getJobs() {
        List<ICi> result = new ArrayList<ICi>();
        IJobService jobsvc = (IJobService) getSession().getService(IJobService.class);
        ICi jobroot = jobsvc.getRootManualTrigger();
        for (ICi trigger : jobroot.getOffsprings()) {
            List<IAttribute> procs = trigger.getAttributesWithAlias("process");
            if (procs.size() == 1) {
                IAttribute proc = procs.get(0);
                if (!proc.isNullValue()) {
                    ICi job = (ICi) proc.getValue();
                    result.add(job);
                }
            }
        }
        return result;
    }
    
}