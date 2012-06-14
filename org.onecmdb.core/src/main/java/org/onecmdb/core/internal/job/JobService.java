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
package org.onecmdb.core.internal.job;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICCBListener;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IJobService;
import org.onecmdb.core.IJobStartResult;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.IService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IValueProvider;
import org.onecmdb.core.internal.SchemaService;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.utils.IBeanProviderSource;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.IBeanProviderConfig;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;

public class JobService extends SchemaService implements IJobService, ICCBListener {

	private Log log;

	private IModelService modelService;

	private BeanScope initScope;

	private ISession initSession;

	private String rootJobAlias = "Job";

	// Reference aliasas.
	private String rootTriggerReferenceAlias ="JobTriggerReference";
	

	private String rootTriggerAlias = "JobTrigger";
	private String rootCronTriggerAlias = "JobCronTrigger";
	private String rootIntervallTriggerAlias = "JobIntervallTrigger";
	private String rootManualTriggerAlias = "JobManualTrigger";

	private String rootProcessAlias = "JobProcess";
	
	
	
	private JobSchedulare schedulare;

	// / {{{ Spring IOC
	public void setRootAlias(String alias) {
		this.rootJobAlias = alias;
	}
	
	public void setRootTriggerAlias(String alias) {
		this.rootTriggerAlias = alias;
	}

	public void setRootTriggerReferenceAlias(String alias) {
		this.rootTriggerReferenceAlias = alias;
	}


	public void setRootCronTriggerAlias(String rootCronTriggerAlias) {
		this.rootCronTriggerAlias = rootCronTriggerAlias;
	}

	public void setRootIntervallTriggerAlias(String rootIntervallTriggerAlias) {
		this.rootIntervallTriggerAlias = rootIntervallTriggerAlias;
	}
	
	public void setRootManualTriggerAlias(String rootManualTriggerAlias) {
		this.rootManualTriggerAlias = rootManualTriggerAlias;
	}
	
	public void setRootProcessAlias(String rootProcessAlias) {
		this.rootProcessAlias = rootProcessAlias;
	}

	public void setRootJobAlias(String rootJobAlias) {
		this.rootJobAlias = rootJobAlias;
	}

	public void setModelService(IModelService modelService) {
		this.modelService = modelService;
	}

	public void setRootJobPath(String path) {
		this.rootJobPath = path;
	}


	public void setInitScope(BeanScope scope) {
		this.initScope = scope;
	}

	public void setInitSession(ISession session) {
		this.initSession = session;
	}

	// }} End Spring IOC

	public ICi getRootTrigger() {
		Path<String> path = new Path<String>(this.rootTriggerAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}
	
	public ICi getRootTriggerReference() {
		Path<String> path = new Path<String>(this.rootTriggerReferenceAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}

	
	public ICi getRootJob() {
		Path<String> path = new Path<String>(this.rootJobAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}

	public ICi getRootIntervallTrigger() {
		Path<String> path = new Path<String>(this.rootIntervallTriggerAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}

	public ICi getRootCronTrigger() {
		Path<String> path = new Path<String>(this.rootCronTriggerAlias);
		ICi ci = modelService.findCi(path);
		return (ci);	
	}
	public ICi getRootManualTrigger() {
		Path<String> path = new Path<String>(this.rootManualTriggerAlias);
		ICi ci = modelService.findCi(path);
		return (ci);	
	}

	public ICi getRootProcess() {
		Path<String> path = new Path<String>(this.rootProcessAlias);
		ICi ci = modelService.findCi(path);
		return (ci);	
	}
	
	public void init() {

		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		
		if (this.rootJobAlias == null) {
			log.fatal("No root job alias is defined.");
		}
		
		super.setupSchema();
		
		this.initSession.login();
		processInitScope(new InitModelProviderConfig(this.initSession));
		this.initSession.logout();
		
		setupSchedulare();
	}

	public void close() {
		log.info("JobService Closing");
		if (this.schedulare != null) {
			this.schedulare.shutdown();
		}
		log.info("JobService Closed");
	}
	
	private void setupSchedulare() {
		// Test start Schedulare.
		// TODO: Use spring IOC
		schedulare = new JobSchedulare();
		
		reschedualeTriggers(initSession);
		
		// Add events from ccb, to monitor changes in schedulare...
		/*
		ICcb ccb = (ICcb) initSession.getService(ICcb.class);
	
		ccb.addChangeListener(this);
		*/
	}
	
	public void onChange(IObjectScope scope) {
		// handle remove of triggers...
		for (ICi ci : scope.getDestroyedICis()) {
			if (ci.isDerivedFrom(getRootTrigger())) {
				schedulare.cancel(ci);
			}
		}
		for (ICi ci : scope.getModifiedICis()) {
			if (ci.isDerivedFrom(getRootTrigger())) {
				schedulare.scheduale(initSession, ci, null);
			}
		}
		for (ICi ci : scope.getNewICis()) {
			if (ci.isDerivedFrom(getRootTrigger())) {
				schedulare.scheduale(initSession, ci, null);
			}
		}
		
		
		
	}

	/**
	 * Serach for all Cron and Intervall
	 * trigger's and resceduale them. 
	 *
	 */
	private void reschedualeTriggers(ISession session) {
		
		log.info("Reschedualer: Cron triggers");
		reschedualeTrigger(session, getRootCronTrigger());

		log.info("Reschedualer: Intervall triggers");
		reschedualeTrigger(session, getRootIntervallTrigger());
	
		
	}
	
	
	public void reschedualeTrigger(ISession session, ICi trigger) {
		if (trigger == null || session == null) {
			log.warn("Reschedualer: Session and/or trigger is null!");
			return;
		}
		if (trigger.isBlueprint()) {
			for (ICi ci : trigger.getOffsprings()) {
				reschedualeTrigger(session, ci);
			}			
		} else {
			schedulare.cancel(trigger);
			schedulare.scheduale(session, trigger, null);
		}
	}
	
	/**
	 * Cancel trigger in schedualare. If trugger os template all offsprings including templates
	 * will be canceled.
	 * 
	 * @param trigger
	 */
	public void cancelTrigger(ISession session, ICi trigger) {
		if (trigger == null ) {
			log.warn("Reschedualer: no trigger set");
			return;
		}
		if (trigger.isBlueprint()) {
			for (ICi ci : trigger.getOffsprings()) {
				cancelTrigger(session, ci);
			}			
		} else {
			schedulare.cancel(trigger);
		}
	}

	
	private void processInitScope(IBeanProviderConfig config) {
		System.out.println("Process init scope:" + this.initScope);		
		if (this.initScope != null) {
			if (this.initSession == null) {
				log.fatal("No Init session set to process init scope.");
			}
			
			if (this.initScope.getBeanProvider() instanceof IBeanProviderSource) {
				((IBeanProviderSource)this.initScope.getBeanProvider()).setBeanProviderConfig(config);
			}
			
			// Set up to compare with already populated...
			IModelService modelService = (IModelService) this.initSession
			.getService(IModelService.class);
			OneCmdbBeanProvider oneCmdbBeanProvider = new OneCmdbBeanProvider();
			oneCmdbBeanProvider.setModelService(modelService);
			
			this.initScope.setRemoteBeanProvider(oneCmdbBeanProvider);
			this.initScope.process();
			
			
			for (CiBean bean : this.initScope.getBeanProvider().getBeans()) {
				modelService.addProtectedCI(bean.getAlias());
			}
			Set<String> unresolved = this.initScope.getUnresolvedAliases();
			if (unresolved.size() > 0) {
				log.error("Initial providers have unresolved items");
				for (String alias : unresolved) {
					log.error("  " + alias);
				}
				throw new IllegalArgumentException("Problem importing provider files.");
			}
			
			
			List<IRFC> rfcs = this.initScope.getRFCs();
			
			if (rfcs == null || rfcs.size() == 0) {
				log.info("No initial rfcs found...");
				return;
			}
			
			
			// Need to send the RFC to CCB.
			ICcb ccb = (ICcb) initSession.getService(ICcb.class);
			ICmdbTransaction tx = ccb.getTx(initSession);
			tx.setRfc(rfcs);
			ITicket ticket = ccb.submitTx(tx);
			IRfcResult result = ccb.waitForTx(ticket);
			if (result.isRejected()) {
				log.fatal("Can't setup initial model: "
						+ result.getRejectCause());
			} else {
				// Update Onecmdb Configurator.
				config.updateConfig();
				
			}
		}
	}
	
	private Map<IValueProvider, Set<IAttribute>> db = new HashMap<IValueProvider, Set<IAttribute>>();

	private String rootJobPath;

	public void addProvider(IValueProvider provider, IAttribute usedFor) {
		Set<IAttribute> attrs = this.db.get(provider);
		if (attrs == null) {
			attrs = new HashSet<IAttribute>(1);
			this.db.put(provider, attrs);
		}
		attrs.add(usedFor);
	}

	/**
	 * Run ONE turn over the registred providers
	 * 
	 */
	public void update() {

		for (IValueProvider provider : this.db.keySet()) {

			if (provider.isValid())
				continue;

			// Object newValue = provider.fetchValueContent();

			Set<IAttribute> attrs = this.db.get(provider);
			for (IAttribute attr : attrs) {

				ICmdbTransaction tx = null;
				// IModifiableAttribute eattr = attr.getModifiableAdaptor(tx);
				// eattr.setValue(newValue.toString());

			}
		}
	}

	public Object getAdapter(Class type) {
		// TODO Auto-generated method stub
		return null;
	}

	


	public boolean isJob(ICi ci) {
		boolean isOffspring = this.modelService.isOffspringOf(getRootJob(), ci);
		return (isOffspring);
	}

	public JobStartResult cancelJob(ISession session, ICi process) {
		JobStartResult result = new JobStartResult();
		if (process.isBlueprint()) {
			// Start all offsprings job?
			result.setRejectCause("Only instances of a job is cancable");
			return (result);
		}
		
		try {
			// Find the interactive trigger for this job.
			IReferenceService refSvc = (IReferenceService) session.getService(IReferenceService.class);
			IModelService modSvc = (IModelService) session.getService(IModelService.class);
			Set<ICi> triggers =  refSvc.getOriginCiReferrers(process, getRootTriggerReference());
			ICi manualTrigger = null;
			for (ICi ci : triggers) {
				if (modSvc.isOffspringOf(getRootManualTrigger(), ci)) {
					manualTrigger = ci;
					break;
				}
			}
			if (manualTrigger == null) {
				result.setRejectCause("Job '" + process.getAlias() + "' can not be started manual, therefor not cancable");
				return(result);
			}
			schedulare.cancel(manualTrigger);
			
		} catch (Throwable t) {
			log.error("Can't start job '" + process.getAlias() + "' :" + t.toString(), t);				
			result.setRejectCause("Exception when starting job '" + process.getAlias() + "' :" + t.toString());
		}
		return (result);
	}
		

	
	public IJobStartResult startJob(ISession session, ICi process) {
		JobStartResult result = new JobStartResult();
		try {

			if (process == null || session == null) {
				result.setRejectCause("No session or job specified!");
				return(result);
			}
			log.info("Start Job:" + process.getAlias());

			if (process.isBlueprint()) {
				// Start all offsprings job?
				result.setRejectCause("Only instances of a job is startable");
				return (result);
			}

			try {
				// Find the interactive trigger for this job.
				IReferenceService refSvc = (IReferenceService) session.getService(IReferenceService.class);
				IModelService modSvc = (IModelService) session.getService(IModelService.class);
				Set<ICi> triggers =  refSvc.getOriginCiReferrers(process, getRootTriggerReference());
				ICi manualTrigger = null;
				for (ICi ci : triggers) {
					if (modSvc.isOffspringOf(getRootManualTrigger(), ci)) {
						manualTrigger = ci;
						break;
					}
				}
				if (manualTrigger == null) {
					result.setRejectCause("Job '" + process.getAlias() + "' can not be started manual.");
					return(result);
				}
				schedulare.scheduale(session, manualTrigger, process);

			} catch (Throwable t) {
				log.error("Can't start job '" + process.getAlias() + "' :" + t.toString(), t);				
				result.setRejectCause("Exception when starting job '" + process.getAlias() + "' :" + t.toString());
			}
			return (result);
		} finally {
			log.info("Start Job Returned:" + result.toString());
		}
	}


	
}
