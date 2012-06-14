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
package org.onecmdb.core.internal.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAuthorizationService;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.UsernamePassword;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The session may be used from several threads. Make sure it is implemented
 * thereafter.
 * 
 * <p>This implementation relies on IoC to call initialization and validation 
 * methods.</p>
 * 
 * @author nogun
 *
 */
public class Session implements ISession, ApplicationContextAware, InitializingBean {
    

	IOneCmdbContext onecmdb;

	private Log log = LogFactory.getLog(this.getClass());
    
    
    // {{{ temporary holds authentication details
    UsernamePassword _creds = new UsernamePassword();
    
    // }}}

    
    private Authentication _anonymousToken;
    
    private AuthenticationManager _manager;

    private ApplicationContext _appctx;



    final private GregorianCalendar _created = new GregorianCalendar();



    // Should not store it here....
	private Authentication authentication = null;

	private RBACSession rbacSession;

    public Session(String userId, IOneCmdbContext cmdb) {
    	_creds.setUsername(userId);
    	this.onecmdb = cmdb;
    }
    
	public Session(String userId, String password, IOneCmdbContext cmdb) {
        _creds.setUsername(userId);
        _creds.setPassword(password);
		this.onecmdb = cmdb;
	}

    /** perform validations */
    public void afterPropertiesSet() throws Exception {
    
        if (_appctx == null) {
            throw new NullPointerException("No application context available");
        }

        if (_manager == null) {
            _manager = (AuthenticationManager) _appctx.getBean("authenticationManager");
            if (_manager == null) {
                throw new NullPointerException("Authentication manager not set");
            }
        }
        
        if (_anonymousToken == null) {
            _anonymousToken = (Authentication) _appctx.getBean("anonymousToken");
            if (_anonymousToken == null) {
                throw new NullPointerException("Anonymous token not available");
            }
        }
        
    }
    
    public ISession newSession() {
    	ISession session = this.onecmdb.createSession();
    	return(session);
    }
    
    public void init() {
        logout();
    }
    

	public void setOneCmdb(IOneCmdbContext oneCmdb) {
		this.onecmdb = oneCmdb;
	}
    
	// }}} END IOC

	public IService getService(Class<? extends IService> type) {
        return onecmdb.getService(this, type);
        
	}
	
	public RBACSession getRBACSession() {
		return(rbacSession);
	}
	
	public UserDetails getPrincipal() {
		Object object = getAuthorization().getPrincipal();
		if (object instanceof UserDetails) {
			return((UserDetails)object);
		}
		throw new IllegalAccessError("No UserDetails found!");
	}
    
    
    private Authentication getAuthorization() {
    	if (this.authentication != null) {
    		return(this.authentication);
    	}
    	
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  auth == null ? _anonymousToken : auth;
    }


    public boolean isAnonymous() {
        return getAuthorization().equals(_anonymousToken);
        
    }
    
    public void login() throws AuthenticationException {       
    	Authentication auth = new UsernamePasswordAuthenticationToken(
                _creds.getUsername(), _creds.getPassword());
    	try {
    		auth = _manager.authenticate(auth);
    	} catch (AuthenticationException ae) {
    		log.info("Login FAILED for user<" + _creds.getUsername());
    		throw ae;
    	}
		
    	log.info("Login OK for user<" + _creds.getUsername() + ">");
	        
        // Should not be store here but need it.....
        this.authentication  = auth;
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        try {
        	List<String> roleNames = new ArrayList<String>();
        	
        	for (GrantedAuthority grantedAuth : getPrincipal().getAuthorities()) {
        		String name = grantedAuth.getAuthority();
        		
        		if (name != null) {
        			log.info("<" + _creds.getUsername() + "> granted role " + name);
        			if (!roleNames.contains(name)) {
        				roleNames.add(name);
        			}
        		}
        		
        	}
        	IAuthorizationService authService = (IAuthorizationService)getService(IAuthorizationService.class);
        	if (authService != null) {
        		rbacSession = authService.setupRBAC(this, roleNames);
        		log.info("<" + _creds.getUsername() + ">");
        		log.info(rbacSession.toString());
        		/*
        		System.out.println("<" + _creds.getUsername() + ">");
        		System.out.println(rbacSession.toString());
        		*/
        	}
        } catch (Throwable t) {
        		t.printStackTrace();
        }
    }
    
      
	
	public void logout() {    	
    	if (this.authentication != null) {
    		log.info("Logout user<" + getUsername() +">");
    	}
    	this.authentication = null;
        SecurityContextHolder.getContext().setAuthentication(_anonymousToken);
    }
    
    
    public Authentication getSubject() {
        return getAuthorization();
    }

    public Set<GrantedAuthority> getRoles() {
        return new HashSet<GrantedAuthority>(Arrays.asList(
                getSubject().getAuthorities()));
    }
    
    public void setAuthenticationManager(AuthenticationManager manager) {
        _manager = manager;
    }
    
    
    public String getUsername() {
        if (isAnonymous()) {
            return _creds.getUsername();
        }
        Object p = getSubject().getPrincipal();
        if (p instanceof UserDetails ) {
            UserDetails u = (UserDetails) p;
            return u.getUsername();
            
        }
        return null;
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (_appctx == null) {
            _appctx = applicationContext;
        }
        
    }

    public GregorianCalendar getDateCreated() {
        return _created;
    }

    public UsernamePassword getAuthentication() {
        return _creds;
    }
}

