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
package org.onecmdb.core.tests.jobs.syncronization;

import java.util.List;

import junit.framework.Assert;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.tests.wsdl.AWebServiceTestCase;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.XmlParser;

public class TestSyncronization extends AWebServiceTestCase {
	
	public void testSync() {
		XmlParser parser = new XmlParser();
		parser.setURL("classpath:" + this.getClass().getPackage().getName().replace('.', '/') + "/SyncModel.xml");
		List<CiBean> beans = parser.getBeans();
		IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
		Assert.assertEquals(null, result.getRejectCause());
		
		cmdbService.reschedualeTrigger(token, new CiBean(null, "directory-change-trigger-test1", false));
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
