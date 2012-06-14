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
package org.onecmdb.core.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.rfc.RFC;
import org.onecmdb.core.internal.job.workflow.WorkflowRelevantData;
import org.onecmdb.core.internal.job.workflow.sample.UrlImportProcess;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.Path;

public class OnecmdbTestUtils {
	
	private ISession session;

	public OnecmdbTestUtils(ISession session) {
		this.session = session;		
	}
	
	public ICi createTemplate(ICi blueprint, String alias) {
		ICi ci = createTemplate(blueprint, alias, false);
		return(ci);
	}

	public ICi createTemplate(ICi blueprint, String alias, boolean reject) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		ICiModifiable instance = null;
		{
			ICiModifiable rootTemplate = tx.getTemplate(blueprint);
			instance = rootTemplate.createOffspring();
			if (alias != null) {
				instance.setAlias(alias);
			}
			instance.setIsBlueprint(true);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		if (reject) {
			Assert.assertNotNull(result.getRejectCause());
		} else {
			Assert.assertEquals(null, result.getRejectCause());
		}
		ICi ci = null;
		if (!result.isRejected()) {
			IModelService cisrvc = (IModelService) session
			.getService(IModelService.class);
	
			if (instance instanceof IRFC) {
				Long id = ((IRFC)instance).getTargetId();
				if (id != null) {
					ci = cisrvc.find(new ItemId(id));
				}
			} else {
				ci = cisrvc.findCi(new Path<String>(alias));
			}
			Assert.assertNotNull(ci);
		}
		return (ci);
	}
	
	
	public ICi createInstance(ICi ci, String alias, boolean reject) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		ICmdbTransaction tx = ccb.getTx(session);
		ICiModifiable ciInstance;
		{
			ICiModifiable rootTemplate = tx.getTemplate(ci);
			ciInstance = rootTemplate.createOffspring();
			ciInstance.setIsBlueprint(false);
			if (alias != null) {
				ciInstance.setAlias(alias);
			}
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		
	
		if (reject) {
			Assert.assertNotNull(result.getRejectCause());
		} else {
			Assert.assertEquals(null, result.getRejectCause());
		}
		
		ICi newCi = null;
		if (!result.isRejected()) {
			if (ciInstance instanceof IRFC) {
				IModelService mSvc = (IModelService)session.getService(IModelService.class);
				Long id = ((IRFC)ciInstance).getTargetId();
				newCi = mSvc.find(new ItemId(id));
			}
		}
		return(newCi);
		
	}
	
	public ICi createInstance(ICi ci, String alias) {
		ICi newCi = createInstance(ci, alias, false);
		return(newCi);
	}

	public void setValue(ICi source, String aName, IValue value) {
		setValue(source, aName, value, false);
	}

	public void setValue(ICi source, String aName, IValue value, boolean reject) {
		IAttribute theAttribute = null;
		for (IAttribute a : source.getAttributes()) {
			if (a.getAlias().equals(aName)) {
				theAttribute = a;
				break;
			}
		}
		setValue(theAttribute, value, reject);
	}

	public IAttribute setValue(IAttribute theAttribute, IValue value) {
		return(setValue(theAttribute, value, false));
	}	
	
	public IAttribute setValue(IAttribute theAttribute, IValue value, boolean reject) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		Assert.assertNotNull(theAttribute);
		ICmdbTransaction tx = ccb.getTx(session);
		{
			IAttributeModifiable aTemplate = tx
					.getAttributeTemplate(theAttribute);
			aTemplate.setValue(value);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		if (reject) {
			Assert.assertNotNull("Change was commit ok, but was flaged as to be rejected!", result.getRejectCause());
		} else {
			Assert.assertEquals(null, result.getRejectCause());
		}
		IModelService model = (IModelService) session.getService(IModelService.class);
		IAttribute reload = (IAttribute) model.find(theAttribute.getId());
		return(reload);
	}

	public IValue getValue(ICi ci, String aName) {
		for (IAttribute a : ci.getAttributes()) {
			if (a.getAlias().equals(aName)) {
				return (a.getValue());
			}
		}
		Assert.assertNotNull(null);
		return (null);
	}

	public void destroyCi(ICi ci) {
		destroyCi(ci, false);
	}
	
	public void destroyCi(ICi ci, boolean reject) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable ciTemplate = tx.getTemplate(ci);
			ciTemplate.delete();
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		if (reject) {
			Assert.assertEquals(true, result.isRejected());
			System.out.println("Destroy was rejected: " + result.getRejectCause());
		} else {
			if (result.isRejected()) {
				Assert.assertEquals(null, result.getRejectCause());
			}
			IModelService cisrvc = (IModelService) session
			.getService(IModelService.class);
			ICi destroyedCi = cisrvc.find(ci.getId());
			Assert.assertEquals(null, destroyedCi);
		}
	}
	

	public void addAttribute(ICi ci, String aliasName)  {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ci);
			rootTemplate.addAttribute(aliasName);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(null, result.getRejectCause());
	}
	
	public IAttribute newAttribute(ICi ci, String aName, IType type,
		IType refTyp, int min, int max) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ci);
			ICiModifiable ipTemplate = rootTemplate.createAttribute(aName,
					type, refTyp, min, max, null);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(null, result.getRejectCause());

		IAttribute newAttribute = null;
		for (IAttribute a : ci.getAttributes()) {
			if (a.getAlias().equals(aName)) {
				newAttribute = a;
				break;
			}
		}
		Assert.assertNotNull(newAttribute);
		return (newAttribute);
	}

	public ICi setDisplaynameExpression(ICi ci, String expression) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable template = tx.getTemplate(ci);
			template.setDisplayNameExpression(expression);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		// Reload ci.
		IModelService cisrvc = (IModelService) session
				.getService(IModelService.class);
		ICi reloadCi = cisrvc.find(ci.getId());

		return (reloadCi);
	}


	public ICi modifyTemplate(ICi ci, boolean isTemplate, boolean reject) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable template = tx.getTemplate(ci);
			template.setIsBlueprint(isTemplate);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		ICi reloadCi = null;
		if (reject) {
			Assert.assertNotNull(result.getRejectCause());
		} else {
			Assert.assertEquals(null, result.getRejectCause());
			// Reload ci.
			IModelService cisrvc = (IModelService) session
					.getService(IModelService.class);
			reloadCi = cisrvc.find(ci.getId());
		}
		return (reloadCi);

	}
	
	public ICi modifyTemplate(ICi ci, boolean isTemplate) {
		ICi reloaded = modifyTemplate(ci, isTemplate, false);
		return(reloaded);
	}


	public void importXml(String url) throws Throwable {
		UrlImportProcess process = new UrlImportProcess();
		WorkflowRelevantData data = new WorkflowRelevantData();
		data.put("session", this.session);
		process.setRelevantData(data);
		List<String> array = new ArrayList<String>();
		array.add(url);
		process.setImportUrl(array);
		process.run();
	}


	public ICi findAlias(String alias) {
		IModelService cisrvc = (IModelService) session
		.getService(IModelService.class);
		ICi ci = cisrvc.findCi(new Path(alias));
		return(ci);
	}


	public String dumpOffsprings(ICi ci, int level) {
		StringBuffer buffer = new StringBuffer();
		String cType = "I";
		if (ci.isBlueprint()) {
			cType = "T";
		}
		buffer.append(cType + ":<alias=" + ci.getAlias() +"><displayName=" + ci.getDisplayName() + ">");
		buffer.append("\n");
		level++;
		for (ICi o : ci.getOffsprings()) {
			for (int i = 0; i < level; i++) {
				buffer.append(" ");
			}
			buffer.append(dumpOffsprings(o, level));
		}
		return (buffer.toString());
	}


	public ICi setDescription(ICi ci, String desc) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable template = tx.getTemplate(ci);
			template.setDescription(desc);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(null, result.getRejectCause());
		
		// Reload ci.
		IModelService cisrvc = (IModelService) session
				.getService(IModelService.class);
		ICi reloadCi = cisrvc.find(ci.getId());
		return(reloadCi);
	}


	public IModelService getModelService() {
		IModelService service = (IModelService)this.session.getService(IModelService.class);
		return(service);
	}


}
