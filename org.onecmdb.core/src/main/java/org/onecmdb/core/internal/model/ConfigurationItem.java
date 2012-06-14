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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onecmdb.core.ErrorObject;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueSelector;
import org.onecmdb.core.internal.storage.IDaoReader;

/**
 * <p>The basic, underlying, data container used in the system.</p> 
 */
public class ConfigurationItem implements ICi {

    private ItemId id;
	private String displayName;
	private ItemId derivedFrom;
	private boolean isBlueprint;
	private String description;
	private String templatePath;
	private Long gid;
	private Date lastModified;
	private Date createTime;
	
	
	protected transient IDaoReader daoReader;
	

	/**
	 * Offspring path
	 */
	private String alias;

	// {{{ springified

	public ConfigurationItem() {
		this.id = new ItemId();
	}

	public void setItemId(long id) {
		this.id = new ItemId(id);
	}

	public void setDaoReader(IDaoReader reader) {
		this.daoReader = reader;
	}
	
	/**
	 * Copy internal state from the item passed in.
	 * 
	 * @param item
	 */
	protected void copy(ConfigurationItem item) {
		this.id = item.id;
		
		this.displayName = item.displayName;
		this.derivedFrom = item.derivedFrom;
		this.isBlueprint = item.isBlueprint;
		this.description = item.description;
		this.alias = item.alias;

		this.daoReader = item.daoReader;

	}
	/**
	 * <p>An expression, describing how this configuration item, should be
	 * presented to others others. The expression is built up via ordinary text
	 * and tokens, <em>pointing</em> to attributes owned by this configuration
	 * item.</p> 
     * <blockquote>
     * Date is ${date} 
     * </blockquote> 
	 * 
	 * @param expession
	 */
	public final void setDisplayNameExpression(String expr) {
		this.displayName = expr;
	}

	public final String getDisplayNameExpression() {
		return (this.displayName);
	}

	// }}}

	protected ConfigurationItem(ItemId id) {
		this.id = id;
	}

	protected ConfigurationItem(String a, String b) {
	}

	/**
	 * Fetch all attributes as an iterator, which gives the possibility to
	 * retreive the attributes in a lazy manner.
	 * 
	 * @return
	 */
	public Set<IAttribute> getAttributes() {
		if (this.daoReader == null) {
			return (Collections.emptySet());
		}
		return (this.daoReader.getAttributesFor(this.getId()));
	}

    
    /**
     * Search for, and return the attribute with a certain id.
     * 
     * @param attrId  Identifier for the attribute to search for
     * @return The attribute, as reference, or <code>null</code> in case it
     *         does not exist
     */
    public IAttribute getAttribute(ItemId attrId) {
    	return(getAttributeWithId(attrId));
    }
    
    public IAttribute getAttributeWithId(ItemId attrId) {
        IAttribute attr = daoReader.findAttributeById(attrId);
    	return(attr);
        /*
        for (IAttribute attr : this.getAttributes()) {
            if (attr.getId().equals(attrId)) {
                return attr;
            }
        }
        return null;
        */
    }
    
	
	public List<IAttribute> getAttributesWithAlias(String alias) {
	 	HashMap<String, Object> crit = new HashMap<String, Object>();
    	crit.put("ownerId", id.asLong());
    	crit.put("alias", alias);
    	List list = daoReader.query(BasicAttribute.class, crit);
   		return(list);
    	/*
   		List<IAttribute> list = new ArrayList<IAttribute>();
		for (IAttribute attr : this.getAttributes()) {
			if (alias.equals(attr.getAlias())) {
				list.add(attr);
			}
		}
		return(list);
		*/
	}

	public Set<IAttribute> getAttributeDefinitions() {
	 	// Query this ci for attribute that have no derivedFrom.
		HashMap<String, Object> crit = new HashMap<String, Object>();
    	crit.put("ownerId", id.asLong());    	
    	crit.put("derivedFromId", null);
    	List list = daoReader.query(BasicAttribute.class, crit);
    	
    	// The result map.
    	Set<IAttribute> resultSet = new HashSet<IAttribute>();
    	for (IAttribute a : (List<IAttribute>)list) {
    		resultSet.add(a);
    	}
    	
    	ICi parent = getDerivedFrom();
    	if (parent != null) {
    		resultSet.addAll(parent.getAttributeDefinitions());
    	}
   		return(resultSet);
    	 
	}
	
	public IAttribute getAttributeDefinitionWithAlias(String alias) {
		// Query datasource
		HashMap<String, Object> crit = new HashMap<String, Object>();
    	crit.put("ownerId", id.asLong());
    	crit.put("alias", alias);
     	crit.put("derivedFromId", null);
        List list = daoReader.query(BasicAttribute.class, crit);
   		
        // Found it?
        if (list.size() == 1) {
   			return((IAttribute)list.get(0));
   		}
       	
        // Ask parent.
        ICi parent = getDerivedFrom();
        if (parent != null) {
    		return(parent.getAttributeDefinitionWithAlias(alias));
    	}
   		
        // Not found
        return(null);
	}
	

	public List<IAttribute> getAddableAttributes() {
		ICi derivedFrom = this.getDerivedFrom();
		if (derivedFrom == null) {
			return(Collections.emptyList());
		}
		HashMap<String, IAttribute> addableAttributes = new HashMap<String, IAttribute>();
	
		// Add what derived from think is ok.
		List<IAttribute> parentAttributes = derivedFrom.getAddableAttributes();
		for (IAttribute a : parentAttributes) {
			addableAttributes.put(a.getAlias(), a);
		}
		
		// Check my attributes.
		HashMap<String, List<IAttribute>> thisAttributeMap = new HashMap<String, List<IAttribute>>();
		for (IAttribute a : getAttributes()) {
			List<IAttribute> list = thisAttributeMap.get(a.getAlias());
			if (list == null) {
				list = new ArrayList<IAttribute>();
				thisAttributeMap.put(a.getAlias(), list);
			}
			list.add(a);
		}
		
		for (String alias : thisAttributeMap.keySet()) {
			List<IAttribute> attributes = thisAttributeMap.get(alias);
			IAttribute definition = getAttributeDefinitionWithAlias(alias);
			if ((definition.getMaxOccurs() < 0) || 
					(attributes.size() < definition.getMaxOccurs())) {
				addableAttributes.put(definition.getAlias(), definition);
			} else {
				addableAttributes.remove(definition.getAlias());
			}
		}
		
		List<IAttribute> result = new ArrayList<IAttribute>(addableAttributes.values());
		 
		
		return(result);
	}
	/**
	 * A CI is identified by its ID, which once set never changes. Note, there
	 * is no support to set the hash in this interface. Each implementation must
	 * deal with the details regarding this issue.
	 * 
	 * @param newId
	 */
	public ItemId getId() {
		return this.id;
	}

	protected IDaoReader getDaoReader() {
		return (this.daoReader);
	}

    public boolean isComplex(){
    	return true;
    }
    
	public ICi getDerivedFrom() {
		if (this.derivedFrom == null) {
			return (null);
		}
		if (getDaoReader() == null) {
			return (null);
		}
		ICi derived = getDaoReader().findById(this.derivedFrom);
		return (derived);
	}

	public void setDerivedFrom(ICi ci) {
		this.derivedFrom = ci.getId();
	}

	public Set<ICi> getOffsprings() {
		if (getDaoReader() == null) {
			return (Collections.emptySet());
		}

		return (daoReader.getOffsprings(this.getId()));
	}

	public Object getAdapter(Class type) {
		return null;
	}

	public boolean isBlueprint() {
		return (isBlueprint);
	}

	public void setIsBlueprint(boolean value) {
		this.isBlueprint = value;
	}

	public boolean getIsBlueprint() {
		return (this.isBlueprint);
	}

	/**
	 * 
	 * protected ConfigurationItem copy() { Moved to RFCCopyCi
	 * 
	 * @return
	 */
	/*
	 * protected ConfigurationItem copy() {
	 * 
	 * ConfigurationItem copy = new ConfigurationItem(); for (IAttribute a :
	 * getAttributes()) {
	 *  // {{{ strategy 1)
	 * 
	 * find the policy.... from (?)
	 *  {
	 *  } // }}}
	 *  // {{{ strategy 2)
	 * 
	 * send a message, and provide a callback, where the the new offspring can
	 * be feteched from
	 *  {
	 * 
	 * final Map<String key , IAttribute offspring > data = new HashMap<String,IAttribute>(1);
	 * 
	 * NotificationCallback callback = new NotificationCallback() { public void
	 * callback(Object callbackData) { data.put("offspring", (IAttribute)
	 * callbackData);
	 *  // let others now (should be handled more generically though) beencalled =
	 * true; synchronized(this) { notify(); } } };
	 * 
	 * 
	 * 
	 * callback.waitForRProceessed(); IAttribute newOffspring =
	 * data.get("offspring");
	 * 
	 * 
	 *  // wait for callback (at most 2 seconds)
	 *  // clone the attribute, by accessing IExtensibleAttribute ea =
	 * (IExtensibleAttribute)
	 * a.getDerivedFrom().getAdapter(IExtensibleAttribute.class);
	 * ea.createOffspring(a.getName(), a.getType(), null);
	 * 
	 * copy.addAttribute(a); } // }}}
	 *  }
	 * 
	 * 
	 * 
	 * return copy; }
	 */
	public void setAlias(String value) {
		this.alias = value;
	}

	public String getAlias() {
		return (this.alias);
	}

	/**
     * Generates a display name according to displayName expression.
     * If no expression is set an empty string is returned.
     */
    public final String getDisplayName() {
        String name = "";
        if (getDisplayNameExpression() != null) {
	        name = evaluate(getDisplayNameExpression());
        }
	    return name;
        
	}

	/**
	 * The beginning of an <em>interpreted</em> expression language which may
	 * be used to extract information from this configuration item.
	 * <p>
	 * Via an expresion in the form:
	 * </p>
	 * <blockqoute>
	 * <code>[<em>text</em>]${<em>token</em>[.<em>token</em>]}[<em>text</em>]</code>...
	 * </blockqoute>
	 * <p>
	 * Text can be interpersed with attribute values from this configuration
	 * item
	 * </p>
	 * <p>
	 * The token <em>points</em> out attributes. With punctatition, nested
	 * attributes may be reached</em>
	 * 
	 * @param expr
	 *            The expression to evalaute
	 * @return A string representation of the evalaution
	 */
	public String evaluate(final String expr) {
		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile("\\$\\{([^}]*)\\}");
		int start = 0;
		Matcher m = p.matcher(expr);
		while (m.find()) {
			int end = m.start();
			sb.append(expr.substring(start, end));
			String tok = m.group(1);
			sb.append(resolve(tok));
			start = m.end();
		}
		if (start < expr.length()) {
			sb.append(this.displayName.substring(start));
		}
		return sb.toString().trim();
	}

	private String resolve(String tokens) {
		final int p = tokens.indexOf('.');
		final String tok;
		if (p != -1) {
			tok = tokens.substring(0, p);
			tokens = p + 1 < tokens.length() ? tokens.substring(p + 1) : null;
		} else {
			tok = tokens;
			tokens = null;
		}
		for (ICi ciAttr : getAttributesWithAlias(tok)) {
			if (ciAttr instanceof IAttribute) {
				IAttribute attr = (IAttribute) ciAttr;
				if (tokens == null) {
				    IValue v = attr.getValue();
				    return v != null ? v.getDisplayName() : "";
				} else {
                    IValue deref = attr.getValue();
                    if (deref == null || !(deref instanceof ConfigurationItem)) {
                        return "";
                    }
				    ConfigurationItem ci = (ConfigurationItem) deref;
				    return ci.resolve(tokens);
				}
			}
		}
		if ("alias".equals(tok)) {
		    return(this.getAlias());
		}
		if ("id".equals(tok)) {
		    return(this.getId().toString());
		}
		return "";
	}

	/**
	 * Creates a string repesentation of this configuration item by
	 * concatenating the identifier, the displayname, and every contained
	 * attributes recursivly
	 * 
	 * <pre>
	 *  
	 *  #nnnnn: displayname [{
	 *       attr : [type] value {
	 *           attr : [type] value
	 *           ...
	 *           }
	 *       }
	 *       attr    
	 *  }
	 *  
	 * </pre>
	 */

	public String toString() {
		return (toString(0, 3));
	}

	protected String toString(int level, int maxLevel) {
		StringBuffer sb = new StringBuffer(getTab(level) + getId() + ":"
				+ getAlias());
		sb.append(toStringAttributes(level + 1, maxLevel));
		return sb.toString();
	}

    
    /**
     * TODO: We must discover cycles, for now we add a max level instead.
     * @param level
     * @return
     */
    
	protected final String toStringAttributes(int level, int maxLevel) {
        if (level > maxLevel) {
            return "...";
        }
        
        Set<IAttribute> attributeDefinitions = getAttributes();
        if (level > maxLevel || attributeDefinitions.size() == 0) {
			return ("");
		}
		StringBuffer sb = new StringBuffer();

		sb.append("\n" + getTab(level) + "{");
		for (ICi attr : attributeDefinitions) {
			if (attr instanceof ConfigurationItem) {
				sb.append("\n");
				sb.append(((ConfigurationItem) attr).toString(level, maxLevel));
			}
		}
		sb.append("\n" + getTab(level) + "}");
		return (sb.toString());
	}

	/*
	 * protected final String toStringAttrbutes(int level) { if
	 * (getAttributes().size() == 0) { return ""; } StringBuffer sb = new
	 * StringBuffer(); String tab = getTab(level); for (ICi attr :
	 * getAttributes()) { if (sb.length() > 0) sb.append("\n"); sb.append(tab +
	 * attr.toString());; AbstractAttribute aa = (AbstractAttribute) attr;
	 * String nested = aa.toStringAttrbutes(level + 1); if (nested.length() != 0 ) {
	 * sb.append(" {\n"); sb.append(nested + "\n"); sb.append(tab+"}"); } }
	 * return sb.toString(); }
	 */

	protected String getTab(int level) {
		String tab = "";
		for (int i = 0; i < level; i++)
			tab += "  ";
		return tab;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !getClass().equals(obj.getClass()))
			return false;

		ICi other = (ICi) obj;
		return getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	/**
	 * {{{ Hibernate Setter/Getters!!! Could be done by Hibernate support of
	 * PropertyAccessor
	 * 
	 * Should we instead use Hiberante support for decoration. Persistent state
	 * in Ci. longId : Long derivedFromId : Long
	 */
	public void setLongId(Long id) {
		this.id = ObjectConverter.convertLongToItemId(id);
	}

	public Long getLongId() {
		return (ObjectConverter.convertItemIdToLong(this.id));
	}

	public void setDerivedFromId(Long id) {
		this.derivedFrom = ObjectConverter.convertLongToItemId(id);
	}

	public Long getDerivedFromId() {
		return (ObjectConverter.convertItemIdToLong(this.derivedFrom));
	}

	/*
	 * Hibernate }}}
	 */

	public IValue parseString(String s) {
		IValue value = ObjectConverter
				.convertUniqueStringToIValue(daoReader, s);
		return (value);
	}
    
    public IValue getNullValue() {
        IValue value = ObjectConverter
        .convertUniqueStringToIValue(daoReader, null);
        return value;
    }
    

	public String getAsString() {
		String s = ObjectConverter.convertICiToUniqueName(daoReader, this);
		return (s);
	}

	/**
	 * This need to be in sync with the dao reader's getItemByUniqueName().
	 */
	public String getUniqueName() {
		String s = ObjectConverter.convertICiToUniqueName(daoReader, this);
		return (s);
	}

	public IValue parseInputStream(InputStream in) {
		throw new IllegalAccessError("Not implemented!");
	}

	/**
	 * Defined in the IValue interface.
	 */
	public IType getValueType() {
		return (this);
	}

	public OutputStream asOutputStream() {
		throw new IllegalAccessError("Not implemented!");
	}

	public IPath<IType> getOffspringPath() {
		IPath<IType> path = new Path<IType>();
        ICi parent = getDerivedFrom();
        if (parent != null) {
            IPath<IType> parentPath = parent.getOffspringPath();
            path.addPath(parentPath);
        }
        path.addElement(this);
        return path;
	}
    

	public String getIcon() {
	    List<IAttribute> iconAttrs = getAttributesWithAlias("icon");
		if (!iconAttrs.isEmpty()) {
			IValue v = iconAttrs.get(0).getValue();
			return v != null ? v.getAsString() : null;
		}
		return null;
	}

	public IValueSelector getValueSelector() {
		IValueSelector selector = new InstanceValueSelector(this);
		return (selector);
	}
    
    

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public IValue fromValue(IValue value) {
		return parseString(value != null ? value.getAsString() : null);

	}

	public ErrorObject validate(IValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Can not perform this.
	 */
	public Object getAsJavaObject() {
		return null;
	}

    
    public boolean isNullValue() {
        return false;
    }

	
	public Set<IType> getAllOffspringTypes() {
		// Retrive offsprings templates.
		QueryCriteria<IType> crit = new QueryCriteria<IType>();
		
		// Search for offsprings.
		crit.setOffspringOfId("" + this.getId().asLong());

		// Only search for templates.
		crit.setMatchCiTemplates(true);
		crit.setMatchCiInstances(false);
		
		
		// Only serach for Ci's
		crit.setMatchCi(true);
		
		List<IType> cis = this.daoReader.query(crit, false);
		HashSet<IType> set = new HashSet<IType>();
		set.addAll(cis);
		for (IType child: cis) {
			set.addAll(child.getAllOffspringTypes());
		}
		return(set);
		
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}


	
	/**
	 * Reference data....
	 */
	
	private Long sourceId;
	private Long targetId;
	private String sourceTemplatePath;
	private String targetTemplatePath;
	private Long sourceAttributeId;
	
	
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public String getSourceTemplatePath() {
		return sourceTemplatePath;
	}
	public void setSourceTemplatePath(String sourceTemplatePath) {
		this.sourceTemplatePath = sourceTemplatePath;
	}
	public String getTargetTemplatePath() {
		return targetTemplatePath;
	}
	public void setTargetTemplatePath(String targetTemplatePath) {
		this.targetTemplatePath = targetTemplatePath;
	}
	public Long getSourceAttributeId() {
		return sourceAttributeId;
	}
	public void setSourceAttributeId(Long sourceAttributeId) {
		this.sourceAttributeId = sourceAttributeId;
	}

	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

	public ICi getGroup() {
		if (this.gid == null) {
			return(null);
		}
		ICi group = daoReader.findById(new ItemId(this.gid));
		return(group);
	}

	public boolean isDerivedFrom(ICi parent) {
		return(parent.getDerivedPath().isParent(getDerivedPath()));
	}

	public IPath<String> getDerivedPath() {
		return(new Path<String>(this.templatePath));
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
}
