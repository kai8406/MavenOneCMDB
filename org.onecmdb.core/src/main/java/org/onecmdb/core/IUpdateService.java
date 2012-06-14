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
package org.onecmdb.core;

public interface IUpdateService extends IService {
	/**
	 * Will check the latest known update status. 
	 * Will not query the update server for info.
	 * @return
	 */
	public boolean isUpdateAvaliable();
	
	/**
	 * Retrive the latest update info. The returning string contains info
	 * on what and how to retrive the update, it prefably formatted as html
	 * which then can be displayed in a window.
	 * 
	 * Will ot query the update server.
	 * @return
	 */
	public String getLatestUpdateInfo();
	
	/**
	 * Will query the update server to reciev the latest update info.
	 * Will block untill the update request is finished.
	 * 
	 * After this call to isUpdateAvailable()/getLatsetUpdateInfo() 
	 * can be performed.
	 * 
	 * @return
	 */
	public void checkForUpdate() throws IllegalArgumentException;
	
}
