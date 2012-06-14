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
package org.onecmdb.core.tests.xml;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.ICi;
import org.onecmdb.core.internal.job.workflow.WorkflowParameter;
import org.onecmdb.core.internal.job.workflow.WorkflowRelevantData;
import org.onecmdb.core.internal.job.workflow.sample.CommitRfcProcess;
import org.onecmdb.core.internal.job.workflow.sample.ProcessBeanProvider;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;


public class TestXmlHandling extends AbstractOneCmdbTestCase {

	
	
	public void testSetAttribute() {
		ICi ci = testUtils.createTemplate(this.ciRoot, "Parent");
		testUtils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		ICi child = testUtils.createInstance(ci, "Child");
		
		// MOdify the ci through the xml interface.
		final CiBean bean = new CiBean();
		bean.setAlias("Child");
		bean.setDerivedFrom("Parent");
		bean.setTemplate(false);
		
		ValueBean vBean = new ValueBean();
		vBean.setValue("a1-value");
		vBean.setAlias("a1");
		bean.addAttributeValue(vBean);
		
		// Process beans.
		ProcessBeanProvider process = new ProcessBeanProvider();
		// Add session.
		WorkflowRelevantData data = new WorkflowRelevantData();
		data.put("session", this.session);
		process.setRelevantData(data);
		
		// Add Provider.
		WorkflowParameter in = new WorkflowParameter();
		in.put("provider", new IBeanProvider() {

			public List<CiBean> getBeans() {
				List<CiBean> beans = new ArrayList<CiBean>();
				beans.add(bean);
				return(beans);
			}

			public CiBean getBean(String alias) {
				for (CiBean lbean : getBeans()) {
					if (lbean.getAlias().equals(alias)) {
						return(lbean);
					}
				}
				return(null);
			}
			
			
		});
		
		process.setInParameter(in);
		
		process.run();
		
		// Need to commit the rfcs.
		CommitRfcProcess commit = new CommitRfcProcess();
		commit.setRelevantData(data);
		commit.setInParameter(process.getOutParameter());
		try {
			commit.run();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Check output..
		System.out.println(child.toString());
	}

	
}
