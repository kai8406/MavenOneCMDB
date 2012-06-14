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
package org.onecmdb.core.internal.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IExpression;
import org.onecmdb.core.IMetaCi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.internal.storage.IDaoReader;
import org.onecmdb.core.internal.storage.expression.OneCMDBExpression;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.xml.BeanCache;

/**
 * The basic module, ICiServie, knows about all CIs ever defined. Defining CIs
 * starts by creating offsprings from the single universially defined CI
 * <em>identified as</em> 'ROOT'
 * 
 * The IModelService makes use of the universally defined 'MODEL' CI, which is
 * an offspring off 'ROOT'. The 'MODEL' is given the notion of an container
 * which gives it the possibility to attach, other objects, i.e. not create
 * offsprings, into the model. These other objects are offsprings from other,
 * known, <em>hives</em>, fetched via other the model service itselves, or
 * other attached services.
 * 
 * The gatering of these fundamental services builds up the oneCmdb application!
 * 
 * 
 * 
 * 
 * The ITypeSercie makes
 * 
 * 
 * 
 * The application, oneCmdb, is contained by a number of modules,
 * 
 * 
 * makes use of one, and only one CI, called the root. New CIs can be dereived
 * from this root, creating offspring - - The base system, makes use of module,
 * i.e. servic
 * 
 * 
 * 
 * <ul>
 * <li>Identified by an <em>identifier</em></li>
 * <li>Makes use of a set of attributes to build up a <em>personality</em></li>
 * <li>A <em>Change Control Board</em> (CCB) is used to verify that the
 * incoming changes are sanctions, and that they can be performed. The CCB may
 * reschedule the change if appropriate.
 * <li>Changes of the internal state is controlled via versioning</li>
 * <li></li>
 * </ul>
 * 
 * Via adaptable one should expect to be able the reach a {@link IMetaCi}
 * interfacem usied to <em>carv</em> new CIs.
 * 
 * @author nogun
 * 
 */
public class ModelService implements IModelService {

	private IDaoReader daoReader;

	// private IDaoWriter daoWriter;

	private String rootAlias;

	private Log log = null;

	private HashSet<String> protecedCISet = new HashSet<String>();
	
	public ICi getRoot() {
		ICi ci = findCi(new Path<String>(this.rootAlias));
		return (ci);
		// return(daoReader.findById(this.rootId));
	}

	public void setLogger(Log log) {
		this.log = log;
	}

	public ICi getRootCi() {
		return (getRoot());
	}

	public void setRootAlias(String alias) {
		rootAlias = alias;
	}

	public void setDaoReader(IDaoReader daoReader) {
		this.daoReader = daoReader;
	}

	public void setBeanCachSize(int size) {
		BeanCache.getInstance().setMaxSize(size);
	}
	
	/*
	 * public void setDaoWriter(IDaoWriter daoWriter) { this.daoWriter =
	 * daoWriter; }
	 */

	public void init() {

		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}

		// validate that the instance is correctly instantiated
		if (this.daoReader == null) {
			throw new NullPointerException("daoReader not set");
		}

		if (this.rootAlias == null) {
			throw new NullPointerException("rootAlias not set");
		}

		// Validate that the root ci is found
		ICi ci = getRoot();
		if (ci == null) {
			log.fatal("No Root Ci is found with alias " + this.rootAlias
					+ ", please check the configuration");
		}

		// Create it in the dao.
		// daoWriter.write(bootCi);
		// Create attributes as well.
		// TODO:
		// bootCi.getBootAttributes();
	}

	public ICi findCi(IPath<String> path) {
        if (path == null) return null;
		ICi ci = daoReader.findCiByAlias(path);
		return(ci);
	}

	public ICi find(ItemId id) {
		return daoReader.findById(id);
	}
	
	
	/**
	 * 
	 * 
	 * @param root
	 * @param ci
	 * @return
	 */
	public boolean isOffspringOf(ICi root, ICi ci) {
		if (ci.equals(root)) {
			return (true);
		}
		ICi derivedFrom = ci.getDerivedFrom();
		if (derivedFrom == null) {
			return (false);
		}
		return (isOffspringOf(root, derivedFrom));
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public Set<ICi> getAllCis() {
		List list = daoReader.query(ConfigurationItem.class, new HashMap());
		// list = daoReader.sqlQuery("select {ci.*} from CI {ci}");
		return(new HashSet<ICi>(list));
	}

	
	public Set<IType> getAllComplexTypes(IPath<String> path) {
		// Problem to cast here!
		Set types = getAllTemplates(path);
		return(types);
	}	
	
	

	public Set<IType> getAllBuiltInTypes() {
		Set<IType> builtInTypes = SimpleTypeFactory.getInstance().getAllTypes();
		return(builtInTypes);
	}

	public Set<ICi> getAllTemplates(IPath<String> path) {
		ICi base = null;
		if (path != null) {
			base = findCi(path);
			if (base == null) {
				return(Collections.EMPTY_SET);
			}
		}
		
		/*
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("isBlueprint", Boolean.TRUE);
		List<ICi> list = (List<ICi>) daoReader.query(ConfigurationItem.class, crit);
		//List<ICi> list = daoReader.sqlQuery("select {ci.*} from CI {ci} where isBlueprint = 'true'");
		 */
		/*
		String hqlQuery = "from ICi ci where ci.class = ConfigurationItem ans ci.isBlueprint='true'";
		List<ICi> list = daoReader.hqlQuery(hqlQuery);
		*/
		//List<ICi> list = daoReader.sqlQuery("select {ci.*} from CI {ci} where ci.isBlueprint = 'true'");
	
		HashMap<String, Object> crit = new HashMap<String, Object>();
		crit.put("isBlueprint", Boolean.TRUE);
		List<ICi> list = (List<ICi>) daoReader.query(ConfigurationItem.class, crit);
		
		HashSet<ICi> set = new HashSet<ICi>();
		if (base == null) {
			set.addAll(list);
		} else {
			for (ICi ci : list) {
				if (isOffspringOf(base, ci) && !ci.equals(base)) {
					set.add(ci);
				}
			}
		}
		return(set);
	}

	public IType getType(String alias) {
		IType type = SimpleTypeFactory.getInstance().toType(alias);
		if (type != null) {
			return(type);
		}
		type = findCi(new Path<String>(alias));
		return(type);
	}
    public IType getType(ItemId typeId) {
        IType type = find(typeId);
        return(type);
    }

	public QueryResult query(QueryCriteria crit) {
		QueryResult result = this.daoReader.query(crit, false);
		return(result);
	}
	public int queryCount(QueryCriteria crit) {
		QueryResult result = this.daoReader.query(crit, true);
		return(result.getTotalHits());
	}

	public QueryResult evalExpression(OneCMDBExpression expr) {
		return(this.daoReader.queryExpression(expr));
	}
    
	public List queryCrtiteria(DetachedCriteria crit, PageInfo info) {
		return(this.daoReader.queryCriteria(crit, info));
	}
	public Integer queryCrtiteriaCount(DetachedCriteria crit) {
		return(this.daoReader.queryCriteriaCount(crit));
	}

	public void addProtectedCI(String ciAlias) {
		if (ciAlias == null) {
			return;
		}
		this.protecedCISet.add(ciAlias);
		
	}

	public boolean isCIProteced(String alias) {
		if (alias == null) {
			return(false);
		}
		return(protecedCISet.contains(alias));
	}
	
	
    
	
	
}
