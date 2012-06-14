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
package org.onecmdb.core.internal.reference;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IReference;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.SchemaService;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.storage.IDaoReader;

public class ReferenceService extends SchemaService implements IReferenceService {

	private String rootRelationAlias;

	private IModelService modelService;

	private IDaoReader daoReader;

	private Log log;

	// {{{ Spring initilazation setters
	public void setRootAlias(String alias) {
		rootRelationAlias = alias;
	}

	public void setLogger(Log log) {
		this.log = log;
	}

	public void setModelService(IModelService service) {
		this.modelService = service;
	}

	public void setDaoReader(IDaoReader reader) {
		this.daoReader = reader;
	}

	// }}} End Spring setters

	/**
	 * Retrive all reference ci that are linked to the ci.
	 */
	public Set<IReference> getReferrers(ICi ci) {
		if (this.daoReader == null) {
			log.fatal("No daoReader set on refrence service.");
			return(Collections.EMPTY_SET);
		}
		if (ci == null) {
			return(Collections.EMPTY_SET);
		}
		
		List<IAttribute> refs = daoReader.getTargetReference(ci);
		
		if (refs == null) {
			return (Collections.EMPTY_SET);
		}
		Set<IReference> references = new HashSet<IReference>();
		for (IAttribute refAttribute : refs) {
			ICi refCi = refAttribute.getOwner();
			if (isReferenceCi(refCi)) {
				references.add(new ReferenceItem(refCi));
			}
		}
		return (references);
	}

	/**
	 * Retrive the origin referrer Ci.
	 * 
	 * RefrerrerCi A -> REFCi target --> Ci
	 * 
	 */
	public Set<ICi> getOriginCiReferrers(ICi ci) {
		Set<ICi> resultSet = new HashSet<ICi>();
		List<IAttribute> attributes = daoReader.getAttributesReferringTo(ci);
		for (IAttribute attribute : attributes) {
			ICi owner = attribute.getOwner();
			if (isReferenceCi(owner)) {
				// It's a reference, get the target.
				ReferenceItem ref = new ReferenceItem(owner);
				ICi target = ref.getTarget();
				if (target == null) {
					continue;
				}
				if (target.equals(ci)) {
					resultSet.addAll(ref.getSourceCis());
				} else {
					resultSet.add(target);
				}
			} else {
				resultSet.add(owner);
			}
		}
		return(resultSet);
		
		/*
		Set<IReference> refs = getReferrers(ci);
		Set<ICi> originCis = new HashSet<ICi>();
		for (IReference ref : refs) {
			originCis.addAll(ref.getSourceCis());
		}
		*/
		/*
		List<IAttribute> refs = daoReader.getReferenceForTarget(((IValue) ci)
				.getAsString());
		if (refs == null) {
			return (Collections.EMPTY_SET);
		}
		Set<ICi> originCis = new HashSet<ICi>();
		for (IAttribute refAttribute : refs) {
			ICi refCi = refAttribute.getOwner();
			if (isReferenceCi(refCi)) {
				originCis.addAll(getOriginCiReferrers(refCi));
			} else {
				originCis.add(refCi);
			}
		}
		
		return (originCis);
		*/
	}
	
	public Set<ICi> getOriginCiReferrers(ICi ci, ICi refType) {
		Set<IReference> refs = getReferrers(ci);
		Set<ICi> originCis = new HashSet<ICi>();
		for (IReference ref : refs) {
			if (modelService.isOffspringOf(refType, ref)) {
				originCis.addAll(ref.getSourceCis());
			}
		}
		return(originCis);
		/*
		List<IAttribute> refs = daoReader.getReferenceForTarget(((IValue) ci)
				.getAsString());
		if (refs == null) {
			return (Collections.EMPTY_SET);
		}
		Set<ICi> originCis = new HashSet<ICi>();
		for (IAttribute refAttribute : refs) {
			ICi refCi = refAttribute.getOwner();
			if (modelService.isOffspringOf(refType, refCi)) {
				
				originCis.addAll(getOriginCiReferrers(refCi, refType));
			} else if (isReferenceCi(refCi)) {
				continue;
			} else {
				originCis.add(refCi);
			}
		}
		return (originCis);
		*/
	}

	public IType getReferrerType(ICi ci, ICi referrer) {
		List<IAttribute> refs = daoReader.getSourceReference(ci);
				
		for (IAttribute refAttribute : refs) {
			ICi refCi = refAttribute.getOwner();
			if (isReferenceCi(refCi)) {
				Set<ICi> referrers = getOriginCiReferrers(refCi);
				if (referrers.contains(referrer)) {
					return (refCi);
				}
			}
		}
		return (null);
	}

	public boolean isReferenceCi(ICi ci) {
		ICi rootReleation = getRootReference();
		return (modelService.isOffspringOf(rootReleation, ci));
	}

	public ICi getRootReference() {
		Path<String> path = new Path<String>(this.rootRelationAlias);
		ICi ci = modelService.findCi(path);
		return (ci);
	}

	public void init() {
		if (this.log == null) {
			this.log = LogFactory.getLog(this.getClass());
		}

		if (this.rootRelationAlias == null) {
			log.fatal("rootAlias not set, check configuration");
		}
		
		super.setupSchema();
		
		if (this.modelService == null) {
			log.fatal("No ModelService set, check configuration.");
		}
		ICi rootRef = this.modelService.findCi(new Path<String>(
				this.rootRelationAlias));
		if (rootRef == null) {
			log.fatal("No Root Reference Ci found with alias "
					+ this.rootRelationAlias);
		}
	}

	public void close() {
		// TODO Auto-generated method stub
	}

	/**
	 * Retrive all known references types in onecmdb.
	 * 
	 * @return
	 */
	public Set<IType> getAllReferences(IPath<String> base) {
		ICi rootRef = null;
		if (base == null) {
			base = new Path<String>(this.rootRelationAlias);
		} 
		Set set = modelService.getAllComplexTypes(base);
		return(set);
	}
	
	public IType getRefType(ItemId id) {
		if (modelService == null) {
			return(null);
		}
		IType type = modelService.find(id);
		return(type);
	}
	
	public IType getRefType(String alias) {
		if (modelService != null) {
			return(null);
		}
		IType type = modelService.findCi(new Path<String>(alias));
		return(type);
	}

}
