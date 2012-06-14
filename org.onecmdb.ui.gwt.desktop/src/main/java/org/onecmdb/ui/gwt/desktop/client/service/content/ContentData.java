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
package org.onecmdb.ui.gwt.desktop.client.service.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.toplink.queryframework.DataModifyQuery;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Object describing reference to some content.<br>
 * <br>
 * Current subclasses are ContentFile and ContentFolder.<br>
 * The ContentData has a path to the object and a symbolic name.<br>
 * To reference a file use the path to specify the file,<br>
 * and optionally set a name for this content.
 *
 *
 */
public class ContentData extends BaseModel {
	/**
	 * The full path including filename to the content.
	 * @return
	 */
	public String getPath() {
		return(get("path"));
	}
	
	/**
	 * The full path including filename to the content.
	 * @return
	 */
	public void setPath(String path) {
		if (path != null) {
			path = path.replace('\\', '/');
		}
		set("path", path);
	}
	
	/**
	 * A symbolic name for this content. Is not the same as the file name.
	 * @return
	 */
	public String getName() {
		return(get("name"));
	}
	
	
	public void setName(String name) {
		set("name", name);
	}

	
	public void getChildren(AsyncCallback<List<? extends ContentData>> callback) {
		// Introduce factory...
		ContentServiceFactory.get().list("token", this, callback);		
	}
	
	public void getChildren(final AsyncCallback<List<? extends ContentData>> callback, final Set<ContentData> includeFilter) {
		Set<String> includeWithDirectories = null;
		if (includeFilter != null) {
			includeWithDirectories = new HashSet<String>();
			for (ContentData data : includeFilter) {
				addPath(data, includeWithDirectories);	
			}
		}
		final Set<String> filterSet = includeWithDirectories;
		// Introduce factory...
		ContentServiceFactory.get().list("token", this, new AsyncCallback<List<? extends ContentData>>() {

			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}

			public void onSuccess(List<? extends ContentData> arg0) {
				if (filterSet == null) {
					callback.onSuccess(arg0);
					return;
				}
				List<ContentData> result = new ArrayList<ContentData>();
				for (ContentData data : arg0) {
					if (includeData(filterSet, data)) {
						result.add(data);
					}
				}
				callback.onSuccess(result);
			}
			
		});		
		
	}

	
	private void addPath(ContentData data, Set<String> includeWithDirectories) {
		if (data == null) {
			return;
		}
		includeWithDirectories.add(data.getPath());
		addPath(data.getParent(), includeWithDirectories);
	}

	/**
	 * Must include parent directories.
	 * @param includeFilter
	 * @param data
	 * @return
	 */
	protected boolean includeData(Set<String> includeFilter, ContentData data) {
		for (String match : includeFilter) {
			if (match.equals(data.getPath())) {
				return(true);
			}
		}
		return(false);
		/*
		if (data instanceof ContentFolder) {
			result.add(data);
			continue;
		}
		if (includeFilter.contains(data)) {
			result.add(data);
		}
		*/
	}

	public boolean isDirectory() {
		return(get("directory", false));
	}

	public void setDirectory(boolean directory) {
		set("directory", directory);
	}

	public boolean isExists() {
		return(get("exists", false));
	}

	public void setExists(boolean exists) {
		set("exists", exists);
	}

	public long getSize() {
		return((long)get("size", 0));
	}

	public void setSize(long size) {
		set("size", size);
	}

	public void setLastModified(Long lastModified) {
		set("lastModified", lastModified);
	}
	
	public Long getLastModified() {
		return((Long)get("lastModified", -1L));
	}
	
	public int hashCode() {
		if (getPath() == null) {
			return(super.hashCode());
		}
		return(getPath().hashCode());
	}
	
	public boolean equals(Object o) {
		if (o == null) {
			return(false);
		}
		if (!(o instanceof ContentData)) {
			return(false);
		}
		return(o.hashCode() == this.hashCode());
	}

	public ContentData getParent() {
		String path = getPath();
		if (path == null) {
			return(null);
		}
		path = path.replace('\\', '/');
		int indexOf = path.lastIndexOf('/');
		if (indexOf < 0) {
			return(null);
		}
		String parentPath = path.substring(0, indexOf);
		ContentFolder parent = new ContentFolder();
		parent.setPath(parentPath);
		
		return(parent);
		
	}
	
	public String getLastPathEntry() {
		String path = getPath();
		if (path == null) {
			return("");
		}
		int index = path.lastIndexOf('/');
		if (index < 0) {
			return(path);
		}
		return(path.substring(index+1));
	}
	
	public String toString() {
		return(getPath());
	}
}
