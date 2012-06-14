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
package org.onecmdb.core.utils.wsdl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IJobService;
import org.onecmdb.core.IJobStartResult;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IUpdateService;
import org.onecmdb.core.internal.authentication.OneCMDBUser;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.ccb.RFCSummaryDecorator;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.utils.ImportBeanProvider;
import org.onecmdb.core.utils.MemoryBeanProvider;
import org.onecmdb.core.utils.OnecmdbUtils;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.handler.QueryHandler;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.xml.BeanCache;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;
import org.onecmdb.core.utils.xpath.commands.AuthCommand;
import org.onecmdb.core.utils.xpath.commands.QueryCommand;
import org.onecmdb.core.utils.xpath.generator.XMLContentGenerator;

public class OneCMDBWebServiceImpl implements IOneCMDBWebService {

	private static final String DATA_PATH = "data/instance";
	private IOneCmdbContext onecmdb;
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * Spring injections.
	 * @param onecmdb
	 */
	public void setOneCmdb(IOneCmdbContext onecmdb) {
		this.onecmdb = onecmdb;
	}
	
	public IOneCmdbContext getOneCmdb() {
		return this.onecmdb;
	}

	/**
	 * Authenticate a user with password.
	 * 
	 * @return a sesion token, used for all operations.
	 * @throws IllegalAccessException 
	 */
	public String auth(String username, String pwd) throws IllegalAccessException {
		//ServiceInvocationHandler
		log.info("WSDL: auth(" + username + ", ******)");
		AuthCommand cmd = new AuthCommand(this.onecmdb);
		if (pwd == null) {
			pwd = "";
		}
		cmd.setUser(username);
		cmd.setPwd(pwd);
		String token = cmd.getToken();
		log.info("WSDL: user " + username +" granted token " + token);
		return(token);
	}
	
	

	/**
	 * Query OneCMDB with a valid auth token.
	 */
	public CiBean[] query(String authToken, String path, String attributes) {
		long start = System.currentTimeMillis();
		log.info("WSDL: query(" + authToken + ", " + path + ", " + attributes + ")");
		
		// Create command.
		QueryCommand cmd = new QueryCommand(this.onecmdb);
		cmd.setAuth(authToken);
		cmd.setPath(path);
		cmd.setOutputAttributes(attributes);
		
		// Create parser.
		XMLContentGenerator generator = new XMLContentGenerator();
		generator.setCommand(cmd);
		
		// Generate beans according to command.
		List<CiBean> beans = generator.getBeans();
		
		long stop = System.currentTimeMillis();
		log.info("WSDL: query completed in " + (stop-start) + "ms returned + " + beans.size() + " objects");
	
		return(beans.toArray(new CiBean[0]));
	}
	
	public int searchCount(String auth, QueryCriteria criteria) {
		long start = System.currentTimeMillis();
		log.info("WSDL: searchCount(" + auth + ", " + criteria + ")");
		
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		IModelService mService = (IModelService)session.getService(IModelService.class);
		int count = mService.queryCount(criteria);
		
		long stop = System.currentTimeMillis();
		log.info("WSDL: searchCount completed in " + (stop-start) + "ms returned + " + count);
		
		return(count);
	}
	
	public CiBean[] search(String auth, QueryCriteria criteria) {
		long start = System.currentTimeMillis();
		log.info("WSDL: search(" + auth + ", " + criteria + ")");
	
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		IModelService mService = (IModelService)session.getService(IModelService.class);
		QueryResult<ICi> result = mService.query(criteria);
		ArrayList<CiBean> resultList = new ArrayList<CiBean>();
		OneCmdbBeanProvider converter = new OneCmdbBeanProvider();
	
		long stop = System.currentTimeMillis();
		log.info("WSDL: search completed in " + (stop-start) + "ms returned + " + result.size() + " objects");
		start = stop;
		
		for (ICi ci : result) {
			// Convert ci to cibean.
			CiBean bean = converter.convertCiToBean(ci); 
			resultList.add(bean);
		}
		
		stop = System.currentTimeMillis();
		log.info("WSDL: search convert completed in " + (stop-start) + "ms returned + " + result.size() + " beans");
		log.info("WSDL: " + BeanCache.getInstance().getStatistics());
			
		return(resultList.toArray(new CiBean[0]));
	}

	public int historyCount(String auth, CiBean bean, RfcQueryCriteria criteria) {
		long start = System.currentTimeMillis();
		log.info("WSDL: history(" + auth + ", " + criteria + ")");
		
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		// Find the ci if provided.
		IModelService modelSvc = (IModelService) session.getService(IModelService.class);
		ICi ci = null;
		if (bean != null) {
			
			if (bean.getId() != null) {
				ci = modelSvc.find(new ItemId(bean.getId()));
				if (ci == null) {
					throw new IllegalArgumentException("CI with id <" + bean.getId() + "> not found");
				}
			} else if (bean.getAlias() != null) {
				ci = modelSvc.findCi(new Path<String>(bean.getAlias()));
				if (ci == null) {
					throw new IllegalArgumentException("CI with alias <" + bean.getAlias() + "> not found");
				}
			}
		}
		ICcb ccbSvc = (ICcb) session.getService(ICcb.class);
		int count = ccbSvc.queryRFCForCiCount(ci, criteria);

		long stop = System.currentTimeMillis();
		log.info("WSDL: historyCount completed in " + (stop-start) + "ms returned + " + count);
		
		return(count);
	}
	
	public RFCBean[] history(String auth, CiBean bean, RfcQueryCriteria criteria) {
		long start = System.currentTimeMillis();
		log.info("WSDL: history(" + auth + ", " + criteria + ")");
		
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		// Find the ci if provided.
		IModelService modelSvc = (IModelService) session.getService(IModelService.class);
		ICi ci = null;
		if (bean != null) {
			
			if (bean.getId() != null) {
				ci = modelSvc.find(new ItemId(bean.getId()));
				if (ci == null) {
					throw new IllegalArgumentException("CI with id <" + bean.getId() + "> not found");
				}
			} else if (bean.getAlias() != null) {
				ci = modelSvc.findCi(new Path<String>(bean.getAlias()));
				if (ci == null) {
					throw new IllegalArgumentException("CI with alias <" + bean.getAlias() + "> not found");
				}
			}
		}
		ICcb ccbSvc = (ICcb) session.getService(ICcb.class);
		List<IRFC> rfcs = ccbSvc.queryRFCForCi(ci, criteria);
	
		long stop = System.currentTimeMillis();
		log.info("WSDL: history completed in " + (stop-start) + "ms returned + " + rfcs.size() + " objects");
		start = stop;
		
		stop = System.currentTimeMillis();
		log.info("WSDL: history convert completed in " + (stop-start) + "ms returned + " + rfcs.size() + " beans");
		List<RFCBean> rfcBeans = convert(session, rfcs);
		return(rfcBeans.toArray(new RFCBean[0]));
	}
	
	public RfcResult update(String auth, CiBean[] local, CiBean[] base) {
		long start = System.currentTimeMillis();
		log.info("WSDL: update(" + auth + ", " + local + ", " + base + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		ImportBeanProvider importBeans = new ImportBeanProvider();
		importBeans.setValidation(false);
		importBeans.setSession(session);
		importBeans.setProvider(new MemoryBeanProvider(local));
		if (base != null) {
			importBeans.setBaseProvider(new MemoryBeanProvider(base));
		}
		
		IRfcResult result = importBeans.processProvider();
		long stop = System.currentTimeMillis();
		log.info("WSDL: update completed in " + (stop - start) + "ms result = " + result);
		return((RfcResult)result);
	}

	public void logout(String authToken) {
		log.info("WSDL: logout(" + authToken + ")");
		this.onecmdb.removeSession(authToken);
	}

	public CiBean[] evalRelation(String auth, CiBean source, String relationPath, QueryCriteria crit) {
		long start = System.currentTimeMillis();
		log.info("WSDL: evalRelation(" + auth + ", " + relationPath + ")");
		
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		if (source == null) {
			throw new IllegalArgumentException("Source can not be null!");
		}

		IModelService modelSvc = (IModelService) session.getService(IModelService.class);
		
		ICi ci = null;
		if (source.getId() != null) {
			ci = modelSvc.find(new ItemId(source.getId()));
			if (ci == null) {
				throw new IllegalArgumentException("CI with id <" + source.getId() + "> not found");
			}
		} else if (source.getAlias() != null) {
			ci = modelSvc.findCi(new Path<String>(source.getAlias()));
			if (ci == null) {
				throw new IllegalArgumentException("CI with alias <" + source.getAlias() + "> not found");
			}
		}
		
		if (ci == null) {
			throw new IllegalArgumentException("Source CI have no id or alias specified!");
		}
		
		
		// Lookup the source
		OnecmdbUtils utils = new OnecmdbUtils(session);
		//Set<IValue> set = utils.evaluate(ci, relationPath);
		QueryResult result = utils.evaluate(ci, relationPath, crit, false);
		
		// Convert Values to Beans.
		long stop = System.currentTimeMillis();
		log.info("WSDL: evalRelation completed in " + (stop-start) + "ms returned + " + result.size() + " objects");
		start = stop;
		ArrayList<CiBean> resultList = new ArrayList<CiBean>();
		OneCmdbBeanProvider converter = new OneCmdbBeanProvider();
		for (Object resCI : result) {
			if (resCI instanceof ICi) {
				// Convert ci to cibean.
				CiBean bean = converter.convertCiToBean((ICi)resCI); 
				resultList.add(bean);
			}
		}
		
		stop = System.currentTimeMillis();
		log.info("WSDL: evalRelation convert completed in " + (stop-start) + "ms returned + " + resultList.size() + " beans");
		return(resultList.toArray(new CiBean[0]));	
	}

	public int evalRelationCount(String auth, CiBean source, String relationPath, QueryCriteria crit) {
		long start = System.currentTimeMillis();
		log.info("WSDL: evalRelationCount(" + auth + ", " + relationPath + ")");
		
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		if (source == null) {
			throw new IllegalArgumentException("Source can not be null!");
		}

		ICi ci = getICI(session, source);
		
		
		// Lookup the source
		OnecmdbUtils utils = new OnecmdbUtils(session);
		//Set<IValue> set = utils.evaluate(ci, relationPath);
		QueryResult result = utils.evaluate(ci, relationPath, crit, true);
		
		// Convert Values to Beans.
		long stop = System.currentTimeMillis();
		log.info("WSDL: evalRelationCount completed in " + (stop-start) + "ms returned + " + result.size() + " objects");
		return(result.getTotalHits());
	}

	private ICi getICI(ISession session, CiBean bean) {
		IModelService modelSvc = (IModelService) session.getService(IModelService.class);
		
		ICi ci = null;
		if (bean.getId() != null) {
			ci = modelSvc.find(new ItemId(bean.getId()));
			if (ci == null) {
				throw new IllegalArgumentException("CI with id <" + bean.getId() + "> not found");
			}
		} else if (bean.getAlias() != null) {
			ci = modelSvc.findCi(new Path<String>(bean.getAlias()));
			if (ci == null) {
				throw new IllegalArgumentException("CI with alias <" + bean.getAlias() + "> not found");
			}
		}
		if (ci == null) {
			throw new IllegalArgumentException("Source CI have no id or alias specified!");
		}
		return(ci);
	}
	
	public String[] findRelation(String auth, CiBean source, CiBean target) {
		throw new IllegalArgumentException("Not implemented yet!");
	}

	public String newInstanceAlias(String auth, String templateAlias) {
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		// For now use the file system.
		File file = new File(DATA_PATH + "/" + templateAlias + "/");
		if (!file.exists()) {
			file.mkdirs();
		}
		// Make sure we are alone.
		synchronized(this) {
			String files[] = file.list();
			int offset = 0;
			if (files.length == 0) {
				// Validate offset...
				offset = 1;
			} else {
				offset = Integer.parseInt(files[0]);
			}
			
			offset = validateInstanceOffset(session, offset, templateAlias);
			
			if (files.length == 0) {
				// Create new offset.
				File index = new File(file, "" + offset);
				try {
					if (!index.createNewFile()) {
					}
				} catch (Exception e) {
					throw new IllegalArgumentException("Can't create index file path " + file.getPath());
				}
			} else {
				File newIndex = new File(file, "" + offset);
				File oldFile = new File(file, files[0]);
				oldFile.renameTo(newIndex);
			}
			return(templateAlias + "-" + offset);
		}
	}

	/**
	 * Run under synchronization
	 * @param session 
	 * @param offset
	 * @param templateAlias
	 * @return
	 */
	private int validateInstanceOffset(ISession session, int offset, String templateAlias) {
		IModelService service = (IModelService) session.getService(IModelService.class);
		QueryCriteria crit = new QueryCriteria();
		crit.setCiAlias(templateAlias + "-" + offset);
		if (service.queryCount(crit) == 0) {
			return(offset);
			
		}
		return(validateInstanceOffset(session, (offset+1), templateAlias));
	}

	public RBACSession getRBACSession(String token) {
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		return(session.getRBACSession());
	}
	
	public CiBean getAuthAccount(String token) {
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		UserDetails user = session.getPrincipal();
		if (user instanceof OneCMDBUser) {
			OneCMDBUser cmdbUser = (OneCMDBUser)user;
			ICi account = cmdbUser.getAccount();
			if (account != null) {
				CiBean bean = (new OneCmdbBeanProvider()).convertCiToBean(account);
				return(bean);
			}
		}
		return(null);
		//throw new SecurityException("Token is not a OneCMDB Account!");
	}


	
	public RFCBean[] compare(String auth, CiBean[] local,
			CiBean[] base, String[] keys) {
		long start = System.currentTimeMillis();
		log.info("WSDL: compare(" + auth + ", " + local + ", " + base + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(auth);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		ImportBeanProvider importBeans = new ImportBeanProvider();
		importBeans.setValidation(false);
		importBeans.setSession(session);
		importBeans.setProvider(new MemoryBeanProvider(local));
		if (base != null) {
			importBeans.setBaseProvider(new MemoryBeanProvider(base));
		}
		
		List<IRFC> rfcs = importBeans.compare();
		List<RFCBean> rfcBeans = convert(session, rfcs);
		long stop = System.currentTimeMillis();
		log.info("WSDL: compare completed in " + (stop - start) + "ms result = " + rfcBeans.size());
		return(rfcBeans.toArray(new RFCBean[0]));
	}

	private List<RFCBean> convert(ISession session, List<IRFC> rfcs) {
		ICcb ccbSvc = (ICcb) session.getService(ICcb.class);
		IModelService mSvc =  (IModelService) session.getService(IModelService.class);
			
		// Return this list.
		List<RFCBean> rfcBeans = new ArrayList<RFCBean>();
		for (IRFC rfc : rfcs) {
			RFCBean rfcBean = new RFCBean();
			rfcBean.setId(rfc.getId());
			rfcBean.setSummary(RFCSummaryDecorator.decorateSummary(mSvc, rfc));
			rfcBean.setTransactionId(rfc.getTxId());
			rfcBean.setTs(rfc.getTs());
			if (ccbSvc != null) {
				// Retrive the Transaction to get the issuer.
				ICmdbTransaction tx = ccbSvc.findTxForRfc(rfc);
				rfcBean.setIssuer(tx.getIssuer());
				
				
			}
			
			rfcBeans.add(rfcBean);
		}
		return(rfcBeans);
	}

	public Graph queryGraph(String token, GraphQuery q) {
		
		long start = System.currentTimeMillis();
		log.info("WSDL: QueryGraph(" + token + ", " + q.toString() + ")");
		Graph result = new Graph();
		try {
			// Update all beans.
			ISession session = onecmdb.getSession(token);
			if (session == null) {
				throw new SecurityException("No Session found! Try to do auth() first!");
			}
			long t1 = System.currentTimeMillis();
			QueryHandler handler = new QueryHandler(session);
			result = handler.execute3(q);

			long t2 = System.currentTimeMillis();
			log.info("\tWSDL: GraphQuery: result=" + result.toString() + (t2-t1) + "ms");
		
		} catch (Throwable t) {
			long stop = System.currentTimeMillis();
			log.error("WSDL{" + (stop-start) + "}: ERROR QueryGraph(" + token + ", " + q.toString() + ")", t);
			t.printStackTrace();
			throw new IllegalArgumentException(t.getMessage(), t);
		}
		long stop = System.currentTimeMillis();
		log.info("WSDL{" + (stop-start) + "}: QueryGraph(" + token + ", " + q.toString() + ") : " + result.toString());
		return(result);
	}

	public IJobStartResult cancelJob(String token, CiBean job) {
		long start = System.currentTimeMillis();
		log.info("WSDL: cancelJob(" + token + ", " + job.getAlias() + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		ICi ci = getICI(session, job);
		
		IJobService jobSvc = (IJobService)session.getService(IJobService.class);

		IJobStartResult result = jobSvc.cancelJob(session, ci);
		long stop = System.currentTimeMillis();
		log.info("WSDL: {" + (stop-start) + "} cancelJob(" + token + ", " + job.getAlias() + ")=" + result);
		
		return(result);
	}

	public void cancelTrigger(String token, CiBean trigger) {
		long start = System.currentTimeMillis();
		log.info("WSDL: cancelTrigger(" + token + ", " + trigger.getAlias() + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		ICi ci = getICI(session, trigger);
		
		IJobService jobSvc = (IJobService)session.getService(IJobService.class);

		jobSvc.cancelTrigger(session, ci);
		long stop = System.currentTimeMillis();
		log.info("WSDL: {" + (stop-start) + "} cancelTrigger(" + token + ", " + trigger.getAlias() + ")");
	}

	public void reschedualeTrigger(String token, CiBean trigger) {
		long start = System.currentTimeMillis();
		log.info("WSDL: reschedualeTrigger(" + token + ", " + trigger.getAlias() + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		ICi ci = getICI(session, trigger);
		
		IJobService jobSvc = (IJobService)session.getService(IJobService.class);

		jobSvc.reschedualeTrigger(session, ci);
		long stop = System.currentTimeMillis();
		log.info("WSDL: {" + (stop-start) + "} reschedualeTrigger(" + token + ", " + trigger.getAlias() + ")");
	}

	public IJobStartResult startJob(String token, CiBean job) {
		long start = System.currentTimeMillis();
		log.info("WSDL: startJob(" + token + ", " + job.getAlias() + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		ICi ci = getICI(session, job);
		
		IJobService jobSvc = (IJobService)session.getService(IJobService.class);

		IJobStartResult result = jobSvc.startJob(session, ci);
		long stop = System.currentTimeMillis();
		log.info("WSDL: {" + (stop-start) + "} startJob(" + token + ", " + job.getAlias() + ")=" + result);
		
		return(result);
	}

	public String getUpdateInfo(String token, boolean force) {
		long start = System.currentTimeMillis();
		log.info("WSDL: getUpdateInfo(" + token + ", " + force + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		IUpdateService updSvc = (IUpdateService)session.getService(IUpdateService.class);
		
		if (updSvc == null) {
			log.info("WSDL: getUpdateInfo(" + token + ","+ force +") - Service not available!");
			return(null);
		}
		if (force) {
			updSvc.checkForUpdate();
		}
		String result = updSvc.getLatestUpdateInfo();
		log.info("WSDL: getUpdateInfo(" + token + ","+ force +")=" + result);
		return(result);
	}

	public boolean isUpdateAvailable(String token, boolean force) {
		long start = System.currentTimeMillis();
		log.info("WSDL: isUpdateAvailable(" + token + ", " + force + ")");
		// Update all beans.
		ISession session = onecmdb.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		IUpdateService updSvc = (IUpdateService)session.getService(IUpdateService.class);
		
		if (updSvc == null) {
			log.info("WSDL: isUpdateAvailable(" + token + ","+ force +") - Service not available!");
			return(false);
		}
		if (force) {
			updSvc.checkForUpdate();
		}
		boolean result = updSvc.isUpdateAvaliable();
		log.info("WSDL: getUpdateInfo(" + token + ","+ force +")=" + result);
		return(result);
	}
}
