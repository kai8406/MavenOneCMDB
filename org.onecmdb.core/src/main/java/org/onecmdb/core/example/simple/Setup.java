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
package org.onecmdb.core.example.simple;

import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Setup {
	
		private static IOneCmdbContext oneCmdb = null;

		/**
		 * The Onecmdb contex must be a singleton.
		 * 
		 * @return
		 */
		public static IOneCmdbContext getOnecmdbContext() {
			if (oneCmdb != null) {
				// Use Spring Application Context bean loader.
				String[] resources = {"onecmdb.xml"};
				ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(resources);
				
				// Retreive OneCmdb Context as a singleton.
				oneCmdb  = (IOneCmdbContext) appContext.getBean("onecmdb");
			}
			return(oneCmdb);
		
		}
		/**
		 * Create a onecmdb session.  
		 * 
		 * @param string
		 * @param string2
		 * @return
		 */
		public ISession getSession(String user, String passwd) {
			ISession session = getOnecmdbContext().createSession();
			session.getAuthentication().setUsername(user);
			session.getAuthentication().setPassword(passwd);
			session.login();
			
			return(session);
		}
		

		/**
		 * Create a Onecmdb Session specifying who you are, from there get the 
		 * service of intereset, here the IModelService.
		 * 
		 * @return
		 */
		public IModelService getModelService() {
			// Specify who you are.
			ISession session = getSession("test", "");
			// The Session contains references to all registered services.
			IModelService modelsvc = (IModelService) session.getService(IModelService.class);
			
			return(modelsvc);
		}
}
