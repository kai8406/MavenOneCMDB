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
package org.onecmdb.core.internal.job.workflow;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.onecmdb.core.internal.job.IEventTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DirectoryChangeTrigger extends Trigger implements IEventTrigger {

	private JobDetail jobDetail;
	private boolean interuppted = false;
	long scanIntervall = 10000;
	private String rootPath;
	Map<String, FileInfo> fileMap = new HashMap<String, FileInfo>();
	
	class FileInfo {
		long size;
		long lastModified;
		
		public FileInfo(File f) {
			this.size = f.length();
			this.lastModified = f.lastModified();
		}
		
		public boolean hasChanged(File f) {
			if (f.length() != size) {
				return(true);
			}
			if (f.lastModified() != lastModified) {
				return(true);
			}
			return(false);
		}
	}
	
	public void interrupt() {
		this.interuppted = true;
		notifyAll();
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
	

	public long getScanIntervall() {
		return scanIntervall;
	}

	public void setScanIntervall(long scanIntervall) {
		this.scanIntervall = scanIntervall;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void run() {
		while(!interuppted) {
			if (scanDirectory(new File(rootPath))) {
				startJob();
			}
			try {
				this.wait(scanIntervall);
			} catch (InterruptedException e) {
			}
		}
	}

	private boolean scanDirectory(File path) {
		boolean changed = false;
		if (path.isDirectory()) {
			File childs[] = path.listFiles();
			for (int i = 0; i < childs.length; i++) {
				if (scanDirectory(childs[i])) {
					changed = true;
				}
			}
		}
		if (updateMap(path)) {
			changed = true;
		}
		return(changed);
	}

	private boolean updateMap(File path) {
		FileInfo info = fileMap.get(path.getPath());
		if (info != null) {
			if (!info.hasChanged(path)) {
				return(false);
			}
		}
		fileMap.put(path.getPath(), new FileInfo(path));
		return(true);
	}

	private void startJob() {
		try {
			Job job = (Job) jobDetail.getJobClass().newInstance();
			job.execute(new JobExecutionContext(null, null, job));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
