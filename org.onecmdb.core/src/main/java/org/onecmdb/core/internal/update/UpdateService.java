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
package org.onecmdb.core.internal.update;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IUpdateService;

public class UpdateService implements IUpdateService {
	Log log = LogFactory.getLog(this.getClass());
	private ISession session;
	private int intervall = 24*7;
	private String updateURL = "http://www.onecmdb.org/update/check.php";
	private CheckForUpdate updateThread;
	
	public ISession getSession() {
		return session;
	}

	public void setSession(ISession session) {
		this.session = session;
	}
	
	public CheckForUpdate getUpdateThread() {
		return updateThread;
	}

	public int getIntervall() {
		return intervall;
	}

	public void setIntervall(int intervall) {
		this.intervall = intervall;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	public void close() {
		updateThread.setTerminate(true);
		log.info("Update Service Stopped");
	}

	public void init() {
		synchronized(this) {
			if (updateThread == null) {
				updateThread = new CheckForUpdate();
				updateThread.setDaemon(true);
				updateThread.setRootURL(getUpdateURL());
				updateThread.setIntervall(this.intervall*60*60*1000);
				updateThread.setSession(session);
				updateThread.start();
			}
		}
		log.info("Update Service Started");
	}

	public static void main(String argv[]) {
		UpdateService service = new UpdateService();
		service.setUpdateURL("http://localhost/cmdb/check/check.php");
		service.setIntervall(10*1000);
		service.init();
		try {
			service.getUpdateThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getUpdateInfo() {
		if (updateThread == null) {
			return(null);
		}
		return(updateThread.getLastResponse());
	}
	
	/**
	 * A update is availabe if the response contains more than
	 * 10 characters. 
	 * The response is a instruction on where to recive the update.
	 */
	public boolean isUpdateAvaliable() {
		if (updateThread == null) {
			return(false);
		}
		String msg = updateThread.getLastResponse();
		if (msg == null) {
			return(false);
		}
		return(msg.length() > 10);
	}

	public void checkForUpdate() throws IllegalArgumentException {
		if (updateThread == null) {
			return;
		}
		try {
			updateThread.getUpdateInfo();
		} catch (Throwable e) {
			throw new IllegalArgumentException("Can not invoke update service : ", e);
		}
	}

	public String getLatestUpdateInfo() {
		if (updateThread == null) {
			return("");
		}
		return(updateThread.getLastResponse());
	}	
	
}
