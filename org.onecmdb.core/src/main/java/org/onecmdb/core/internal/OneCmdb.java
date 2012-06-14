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
package org.onecmdb.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.tests.profiler.Profiler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Configuration:
 * <ul>
 * <li>A notification framework</li>
 * </ul>
 * 
 */
public class OneCmdb implements IOneCmdbContext, ApplicationContextAware {


	private List<IService> services;

	private ApplicationContext applicationContext;

	private Log log = LogFactory.getLog(this.getClass());

	private Long sessionTimeout = null;
	private Long scanIntervall = null;

	private SessionCleanerThread sessionCleaner;
	
	// {{{ ---| BEAN properties, to satisfy spring |---

	public OneCmdb() {
	}


	public void setServices(List<IService> services) {
		this.services = services;
	}

	public void setProfiler(boolean value) {
		Profiler.useProfiler(value);
	}
	
	
	// }}}

	public Long getSessionTimeout() {
		return sessionTimeout;
	}


	public void setSessionTimeout(Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}


	public Long getScanIntervall() {
		return scanIntervall;
	}


	public void setScanIntervall(Long scanIntervall) {
		this.scanIntervall = scanIntervall;
	}


	public void init() {
		// Initialize all Services.
		for (IService service : services) {
			service.init();
		}      
        
		// Start the session cleaner thread.
		sessionCleaner = new SessionCleanerThread();
		if (scanIntervall != null) {
			sessionCleaner.setScanIntervall(scanIntervall);
		}
		if (sessionTimeout != null) {
			sessionCleaner.setTimeout(sessionTimeout);
		}
		// Release the thread.
		sessionCleaner.start();
		
        log.info("OneCMDB successfully initialized {");
        log.info(" CWD=" + System.getProperty("user.dir"));
        log.info("}");
        
        
	}
	
	public static Log getLogger(Class module) {
		return LogFactory.getLog(module);
	}
	
	public ISession createSession() {
        ISession session = (ISession) applicationContext.getBean("session");
		return (session);
	}


	public IService getService(ISession session, Class<? extends IService> type) {

        
        
        for (final IService svc : this.services) {
			if (type.isAssignableFrom(svc.getClass())) {
				// How to handle Session object, all services might need to know
				// which session is executing the call?

			    return svc;
			}
		}
		return null;
	}

	public void close() {

		
		log.debug("Closing OneCMDB...");
		
		// Close session cleaner.
		if (this.sessionCleaner != null) {
			this.sessionCleaner.terminate();
		}
		
		for (IService svc : this.services) {
			try {
				svc.close();
			} catch (Throwable t) {
				// To be sure to close as much as possible.
				log.error("Unable to close service <" + svc + ">", t);
			}
		}

		log.info("OneCMDB closed.");

	}


	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
        
        if (this.applicationContext == null)
            this.applicationContext = applicationContext;
	}

	/**
	 * Class to handle timeout of sessions.
	 */
	class SessionTS {
		private ISession session;
		private long ts;
		
		public SessionTS(ISession session) {
			this.session = session;
			updateTS();
		}
		
		public void updateTS() {
			this.ts = System.currentTimeMillis();
		}
		
		public long getTS() {
			return(this.ts);
		}
		
		public ISession getSession() {
			return(this.session);
		}
	}
	
	class SessionCleanerThread extends Thread {
		// Time in ms before a session is logout.
		// Default is 4 hour.
		private long sessionTimeout = 4*60*60*1000;
		private volatile boolean terminate = false;
		private Log log = LogFactory.getLog(this.getClass());
		// How often to scan it.
		// Default every 10 minute.
		private long scanIntervall = 10*60*1000; // Default 1 minute.
		
		public SessionCleanerThread() {
		}
		
		public void setTimeout(long ts) {
			if (ts < 1000) {
				log.info("Session timeout " + ts + " to short!");
				return;
			}
			this.sessionTimeout = ts;
		}
		
		public void setScanIntervall(long intervall) {
			this.scanIntervall = intervall;
		}
		
		public void terminate() {
			this.terminate = true;
			synchronized(this)  {
				this.notifyAll();
			}
		}
		public void run() {
			log.info("Session Cleaner Start: sessionTimeout=" + 
					this.sessionTimeout +", scanIntervall=" + this.scanIntervall);
			
			while (!terminate) {
				try {
					long currentTs = System.currentTimeMillis();

					// Terminated sessions.
					List<Object> timeouted = new ArrayList<Object>();
					for (Object key : sessionMap.keySet()) {
						SessionTS ts = sessionMap.get(key);

						if ((ts.getTS() + this.sessionTimeout) < currentTs) {
							// Log out.
							timeouted.add(key);	
						}
					}

					for (Object key : timeouted) {
						SessionTS ts = sessionMap.get(key);
						log.info("Session token <" + key + "> timed out, inactive for (" + (currentTs - ts.getTS()) + "ms)");
						removeSession(key);
					}
				} catch (Throwable t) {
					log.error("Session cleaner encountered exception:", t);
				} finally {
					synchronized(this) {
						try {
							this.wait(scanIntervall);
						} catch (InterruptedException e) {
							log.error("Session cleaner got interrupted!.");
						}
					}
				}
			}
			log.info("Session Cleaner Terminated");
		}
	}
	/**
	 * Session handling.
	 */
	HashMap<Object, SessionTS> sessionMap = new HashMap<Object, SessionTS>();
	
	public void addSession(Object token, ISession session) {
		sessionMap.put(token, new SessionTS(session));
	}

	public ISession getSession(Object token) {
		SessionTS session = sessionMap.get(token);
		if (session == null) {
			return(null);
		}
		session.updateTS();
		return(session.getSession());
	}

	public void removeSession(Object token) {
		SessionTS session = sessionMap.remove(token);
		if (session != null) {
			session.getSession().logout();
		}
	}
}
