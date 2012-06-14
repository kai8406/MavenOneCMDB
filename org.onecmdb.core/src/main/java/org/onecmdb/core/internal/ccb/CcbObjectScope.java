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
package org.onecmdb.core.internal.ccb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAuthorizationService;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.storage.IDaoReader;

public class CcbObjectScope implements IObjectScope {
	private ISession session;
	
	private IDaoReader reader;
	
	HashMap<ItemId, ICi> newMap = new HashMap<ItemId, ICi>();

	HashMap<ItemId, ICi> modifyMap = new HashMap<ItemId, ICi>();

	HashMap<ItemId, ICi> destroyMap = new HashMap<ItemId, ICi>();

	HashMap<ItemId, ICi> rfcMap = new HashMap<ItemId, ICi>();

	HashMap<String, ICi> aliasMap = new HashMap<String, ICi>();

	private HashMap<ICi, Set<IAttribute>> attributeMap = new HashMap<ICi, Set<IAttribute>>();
	private HashMap<IAttribute, ICi> attributeToOwnerMap = new HashMap<IAttribute, ICi>();
	
	private HashMap<ICi, Set<ICi>> offspringMap = new HashMap<ICi, Set<ICi>>();

	// Simple cache...
	HashMap<String, ICi> aliasCache = new HashMap<String, ICi>();
	HashMap<ItemId, ICi> idCache = new HashMap<ItemId, ICi>();

	private int ciModified = 0;
	private int ciAdded = 0;
	private int ciDeleted = 0;
	
	
	private IAuthorizationService auth;

	
	
	public CcbObjectScope(ISession session, IDaoReader reader) {
		this.reader = reader;
		this.session = session;
		
		this.auth = (IAuthorizationService) session.getService(IAuthorizationService.class);
	}

	public ISession getSession() {
		return(this.session);
	}
	
	public Collection<ICi> getNewICis() {
		return (newMap.values());
	}

	public Collection<ICi> getModifiedICis() {
		return (modifyMap.values());
	}

	public Collection<ICi> getDestroyedICis() {
		return (destroyMap.values());
	}

	public ICi getICiFromAlias(String alias) {
		ICi ci = aliasMap.get(alias);
		if (ci != null) {
			return (ci);
		}
		
		ci = aliasCache.get(alias);
		if (ci != null) {
			return(ci);
		}
		
		// Serach the dao.
		ci = reader.findCiByAlias(new Path<String>(alias));
		
		// Put in cache...
		if (ci != null) {
			aliasCache.put(alias, ci);
		}
		
		return(ci);
	}

	public ICi getICiById(ItemId id) {
		// System.out.println("FIND CI [" + id + "]");

		// Check internal maps.
		ICi ci = newMap.get(id);
		if (ci != null) {
			return (ci);
		}
		ci = modifyMap.get(id);
		if (ci != null) {
			return (ci);
		}
		
		ci = idCache.get(id);
		if (ci != null) {
			return(ci);
		}
		
		ci = reader.findById(id);
		if (ci != null) {
			idCache.put(id, ci);
		}
		return(ci);
	}

	/*
	 * public void mapRfcToCi(IRFC rfc, ICi ci) { //System.out.println("MAP
	 * RFC[" + rfc.getItemId() +"]-->ICI" + ci.getId()); rfcMap.put(rfc.getId(),
	 * ci); }
	 * 
	 * 
	 * public ICi getCIFromRfc(ItemId source) { ICi ci = rfcMap.get(source);
	 * return(ci); }
	 */

	public void addNewICi(ICi ci) {
		// Verify Permission...
		if (auth != null) {
			auth.validateCreatePermission(session, ci);
		}
		// System.out.println("NEW CI[" + ci.getId() +"]");
		// TODO: Should we thorw an Error here, or an Exception!
		// Consult Spring...
		if (modifyMap.containsKey(ci.getId())) {
			throw new IllegalAccessError("Can't add new item[" + ci.getId()
					+ "] when it exists in modify map");
		}
		((ConfigurationItem) ci).setDaoReader(this.reader);
		newMap.put(ci.getId(), ci);
		
		idCache.put(ci.getId(), ci);
		
		if (ci.getAlias() != null) {
			if (!(ci instanceof IAttribute)) {
				aliasMap.put(ci.getAlias(), ci);
			}
		}
		
		// Update ts.
		Date create = new Date();
		((ConfigurationItem)ci).setCreateTime(create);
		((ConfigurationItem)ci).setLastModified(create);
			
		if (ci instanceof IAttribute) {
			ICi owner = getAttributeOwner((IAttribute)ci);
			if (owner != null) {
				addModifiedICi(owner);			
			}
		}
	}

	public void addModifiedICi(ICi ci) {
		// Verify Permission...
		if (auth != null) {
			auth.validateWritePermission(session, ci);
		}
		if (destroyMap.containsKey(ci.getId())) {
			return;
		}
		
		// System.out.println("MODIFY CI[" + ci.getId() +"]");
		if (ci.getAlias() != null) {
			if (!(ci instanceof IAttribute)) {
				aliasMap.put(ci.getAlias(), ci);
			}
		}

		// TODO: Is this ok, or should we throw something here.
		if (newMap.containsKey(ci.getId())) {
			// Already known.
			return;
		}
		
		idCache.put(ci.getId(), ci);
		
		modifyMap.put(ci.getId(), ci);
		
		// Update ts.
		((ConfigurationItem)ci).setLastModified(new Date());
		if (ci instanceof IAttribute) {
			ICi owner = getAttributeOwner((IAttribute)ci);
			if (owner != null) {
				addModifiedICi(owner);			
			}
		}


	}

	public void addDestroyedICi(ICi ci) {
		// Verify Permission...
		if (auth != null) {
			auth.validateDeletePermission(session, ci);
		}

		// Remove from all maps...
		aliasMap.remove(ci.getAlias());
		modifyMap.remove(ci.getId());
		newMap.remove(ci.getId());
		destroyMap.put(ci.getId(), ci);
	
		// Update ts.
		if (ci instanceof IAttribute) {
			// To speed things up.
			
			Long id = ((BasicAttribute)ci).getOwnerId();
			if (id != null) {
				if (!destroyMap.containsKey(new ItemId(id))) {
					long start = System.currentTimeMillis();
					ICi owner = getAttributeOwner((IAttribute)ci);
					if (owner != null) {
						addModifiedICi(owner);
					}
				}
			}
		}
	}

	public Set<ICi> getOffspringForCi(ICi ci) {
	
		Set<ICi> set = offspringMap.get(ci);
		if (set == null) {
			set = new HashSet<ICi>();	
		}
		
		// Refresh from db.
		set.addAll(ci.getOffsprings());
		
		return (set);

	}

	public void addOffspringToCi(ICi parent, ICi offspring) {
		Set<ICi> set = offspringMap.get(parent);
		if (set == null) {
			set = new HashSet<ICi>();
			offspringMap.put(parent, set);
		}
		set.add(offspring);
	}
	
	public Set<IAttribute> getAttributesForCi(ICi ci) {
		if (ci == null) {
			return(Collections.EMPTY_SET);
		}
		Set<IAttribute> set = attributeMap.get(ci);
		if (set == null) {
			set = new HashSet<IAttribute>();
		}
		// Refresh from db only if the ci is not a new one.
		if (!newMap.containsKey(ci.getId())) {
			set.addAll(ci.getAttributes());
		}
		// Store these in the idMap.
		for (IAttribute a: set) {
			idCache.put(a.getId(), a);
		}
		return (set);

	}


	public void addAttributeToCi(ICi item, IAttribute ba) {
		Set<IAttribute> set = attributeMap.get(item);
		if (set == null) {
			set = new HashSet<IAttribute>();
			attributeMap.put(item, set);
		}
		set.add(ba);
		
		// add in reverse map.
		attributeToOwnerMap.put(ba, item);
	}

	
	public IAttribute getAttributeFromRFC(IRFC rfc) {
		Long itemId = rfc.getTargetId();

		if (itemId != null) {
			// Target point's to the Ci
			IAttribute attribute = reader.findAttributeById(new ItemId(itemId));
			if (attribute != null) {
				return (attribute);
			}
		}
		
		return((IAttribute)getCIFromRFC(rfc));
	}

	public ICi getCIFromRFC(IRFC rfc) {
		Long itemId = rfc.getTargetId();

		if (itemId != null) {
			// Target point's to the Ci
			ICi ci = getICiById(new ItemId(itemId));
			return (ci);
		}

		String alias = rfc.getTargetAlias();
		if (alias != null) {
			ICi ci = getICiFromAlias(alias);
			return (ci);
		}
		// Need to serach upward.
		IRFC parent = rfc.getParent();
		if (parent == null) {
			return (null);
		}
		return (getCIFromRFC(parent));
	}

	public IDaoReader getDaoReader() {
		return (this.reader);
	}

	public ICi getAttributeOwner(IAttribute attribute) {
		ICi ci = attributeToOwnerMap.get(attribute);
		if (ci != null) {
			return(ci);
		}
		if (attribute instanceof BasicAttribute) {
			Long id = ((BasicAttribute)attribute).getOwnerId();
			if (id != null) {
				ci = newMap.get(new ItemId(id));
				if (ci != null) {
					return(ci);
				}
				ci = modifyMap.get(new ItemId(id));
				if (ci != null) {
					return(ci);
				}
			}
		}
		return(attribute.getOwner());
	}

	public boolean isDestroyed(ICi ci) {
		return(destroyMap.containsKey(ci.getId()));
	}


	
	/**
	 * Statistics....
	 */
	private int getUniqueCIs(Collection<ICi> cis) {
		HashSet<Long> marked = new HashSet<Long>();
		for (ICi ci : cis) {
			if (ci instanceof BasicAttribute) {
				marked.add(((BasicAttribute)ci).getOwnerId());
			} else {
				marked.add(ci.getId().asLong());
			}
		}
		return(marked.size());
	}
	
	public int getCiModified() {
		return(getUniqueCIs(getModifiedICis()));
	}

	public int getCiAdded() {
		return(getUniqueCIs(getNewICis()));
	}

	public int getCiDeleted() {
		return(getUniqueCIs(getDestroyedICis()));
	}

	public List<IAttribute> getAttributeForReference(ICi referrer) {
		List<IAttribute> attributes = reader.getAttributesReferringTo(referrer);
		return(attributes);
	}
	
	/**
	 * Get all reference ci that has target as ci.
	 */
	public List<ICi> getReferrer(ICi ci) {
		List<IAttribute> attributes = reader.getTargetReference(ci);
		List<ICi> references = new ArrayList<ICi>();
		for (IAttribute attribute : attributes) {
			references.add(attribute.getOwner());
		}
		
		return(references);
	}
}
