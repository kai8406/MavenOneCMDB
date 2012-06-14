/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.modeller.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.onecmdb.ui.gwt.modeller.client.view.screen.TemplateBrowserScreen;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;

public class TemplateCache {
	
	private static HashMap cache = new HashMap();
	private static ArrayList changeListsners = new ArrayList();
	
	
	public static void add(String key, GWT_CiBean bean) {
		cache.put(key, bean);
	}
	
	public static void load(String key, AsyncCallback callback) {
		GWT_CiBean bean = (GWT_CiBean) cache.get(key);
		if (bean != null) {
			callback.onSuccess(bean);
			return;
		}
		OneCMDBConnector.getCIFromAlias(key, callback);
	}
	
	public static void clear() {
		cache.clear();
	}
	
	public static void remove(String key) {
		cache.remove(key);
		
		// Call listeners.
		notifyListeners();
	}
	
	protected static void notifyListeners() {
		for (Iterator iter = changeListsners.iterator(); iter.hasNext();) { 
			Object list = iter.next();
			if (list instanceof ChangeListener) {
				((ChangeListener)list).onChange(null);
			}
		}
	}
	
	public static void addChangeListener(ChangeListener listener) {
		changeListsners.add(listener);
	}

}
