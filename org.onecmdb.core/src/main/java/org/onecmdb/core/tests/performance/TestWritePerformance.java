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
package org.onecmdb.core.tests.performance;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.example.MaxMinAvg;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class TestWritePerformance extends TestCase {
	protected ISession session;

	protected ICi ciRoot;

	protected ICi ciRelationRoot;

	public static void main(String argv[]) {
		junit.textui.TestRunner.run(TestWritePerformance.class);		
	}
	
	public abstract void setUp();
	

	
	public void testSetComplexAttributeValue() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		
		ICi refTemplate = utils.createTemplate(ciRelationRoot, "TestReference");
		ICi targetTemplate = utils.createTemplate(ciRoot, "Status");
		ICi down = utils.createInstance(targetTemplate, "down");
		ICi up = utils.createInstance(targetTemplate, "up");
		
		
		IModelService model = (IModelService)this.session.getService(IModelService.class);
		
		ICi sourceTemplate = utils.createTemplate(ciRoot, "Test");
		utils.newAttribute(sourceTemplate, "status", targetTemplate, refTemplate,1 ,1);
		
		
		ICi source = utils.createInstance(sourceTemplate, "source");
		
		// Get the source attribute.
		List<IAttribute> list = source.getAttributesWithAlias("status");
		Assert.assertEquals(1, list.size());
		IAttribute status = list.get(0);
		
		
		long tsStart = System.currentTimeMillis();
		int COUNT = 150;
		
		IValue currentStatus = (IValue)down;
		MaxMinAvg avgSet = new MaxMinAvg();
		
		for (int i = 0; i < COUNT; i++) {
			long start = System.currentTimeMillis();
			setValue(status, currentStatus);
			long stop = System.currentTimeMillis();
			long ts = (stop - start);
			IAttribute reload = (IAttribute) model.find(status.getId());
		
			IValue reloadValue = reload.getValue();
			Assert.assertEquals(currentStatus, reloadValue);
		
			if (currentStatus.equals(down)) {
				currentStatus = up;
			} else {
				currentStatus = down;
			}
			System.out.println("[" + i + "] TIME=" + ts + "ms");
			avgSet.addValue(ts);
		}
		
		System.out.println("Setting complex value avg:" + avgSet);
		
		// Validate references.
		Set<ICi> referernces = refTemplate.getOffsprings();
		// Only one refernce object should exists.
		Assert.assertEquals(1, referernces.size());
		
		long tsStop = System.currentTimeMillis();

		// Retrive Histroy log.
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		long start = System.currentTimeMillis();
		List<IRFC> rfcList = ccb.findRFCForCi(status);
		long stop = System.currentTimeMillis();
		System.out.println("Query RFC took " + (stop-start) + "ms");
		System.out.println("CHANGES MADE=" + rfcList.size());
		MaxMinAvg avg = new MaxMinAvg();
		
		long startTime = 0;
		long stopTime = 0;
		
		for (IRFC rfc : rfcList) {
			if (rfc instanceof RFCModifyAttributeValue) {
				stopTime = rfc.getTs().getTime();
				if (startTime != 0) {
					long ts = stopTime - startTime;
					avg.addValue(ts);
				}
				startTime = stopTime;
			}
		}
		System.out.println("SET " + COUNT + " Values took "
				+ (tsStop - tsStart) + "ms" + "AVG:" + avg);
		
		
		// Query with time intervall.
		RfcQueryCriteria crit = new RfcQueryCriteria();
		crit.setRfcClass(RFCModifyAttributeValue.class.getName());
		crit.setFromDate(new Date(tsStart));
		crit.setToDate(new Date(tsStop));
		long qCritStart = System.currentTimeMillis();
		List<IRFC> rfcs = ccb.queryRFCForCi(status, crit);
		long qCritStop = System.currentTimeMillis();
		start = 0;
		for (IRFC rfc : rfcs) {
			if (rfc instanceof RFCModifyAttributeValue) {
				stop = rfc.getTs().getTime();
				if (start != 0) {
					long ts = stop - start;
					avg.addValue(ts);
				}
				start = stop;
			}
		}
		
		System.out.println("Query RFC Crit took " + (qCritStop - qCritStart) + "ms");
		System.out.println("CHANGES MADE=" + rfcs.size());
		System.exit(-1);
		
		
	}
	public void testOveriderManyInstances() {
		ICi t1 = createBlueprint("Bp1", ciRoot);
		ICi t2 = createBlueprint("Bp2", ciRoot);
		ICi t3 = createBlueprint("Bp3", ciRoot);
		
		ICi t2Ref = createBlueprint("T2Ref", ciRelationRoot);
		System.out.println("T2REF:" + t2Ref.toString());
		
		ICi t3Ref = createBlueprint("T3Ref", ciRelationRoot);
		
		ICi t2i1 = createOffspring(t2);
		ICi t2i2 = createOffspring(t2);
	
		ICi t3i1 = createOffspring(t3);
		
		
		for (int i = 0; i < 10; i++) {
			newAttribute(t1, "a" + i, SimpleTypeFactory.STRING, null, 1,
					1);
		}
		IAttribute refT2a = newAttribute(t1, "ref-t2", t2, t2Ref, 1,
				1);
		IAttribute refT3a = newAttribute(t1, "ref-t3", t3, t3Ref, 1,
				1);
		
		setValue(refT2a, t2i1);
		setValue(refT3a, t3i1);
		

		ICcb ccb = (ICcb) session.getService(ICcb.class);

		long startTime = System.currentTimeMillis();
		int COUNT = 100;
		for (int i = 0; i < COUNT; i++) {
			long start = System.currentTimeMillis();
			ICmdbTransaction tx = ccb.getTx(session);
			{
				ICiModifiable rootTemplate = tx.getTemplate(t1);
				ICiModifiable template = rootTemplate.createOffspring();
				template.setAlias("Offspring-" + i);
				template.setIsBlueprint(false);
				template.setDerivedAttributeValue("ref-t2", 0, t2i2);
				template.setDerivedAttributeValue("a1", 0, SimpleTypeFactory.STRING.parseString("v1"));
			}
			ITicket ticket = ccb.submitTx(tx);

			IRfcResult result = ccb.waitForTx(ticket);
			Assert.assertEquals(false, result.isRejected());
			long stop = System.currentTimeMillis();
			System.out.println("ADDED " + i + " time=" + (stop-start) + "ms");
		}
		long stopTime = System.currentTimeMillis();
		System.out.println("CREATED " + COUNT + " Instances in "
				+ (stopTime - startTime) + "ms");
		
		Set<ICi> ref2Instance = t2Ref.getOffsprings();
		Set<ICi> ref3Instance = t3Ref.getOffsprings();
		System.out.println("ref2Instancs = " + ref2Instance.size());
		Assert.assertEquals(1, ref3Instance.size());
		
		
		IModelService model = (IModelService)this.session.getService(IModelService.class);
		
		OneCmdbBeanProvider beanProvider = new OneCmdbBeanProvider();
		beanProvider.setModelService(model);
		// Read..
		for (int i = 0; i < COUNT; i++) {
			// Fetch everything about the ci.
			long start = System.currentTimeMillis();
			CiBean bean = beanProvider.getBean("Offspring-" + i);
			long stop = System.currentTimeMillis();
			
			System.out.println("Load Bean: " + (stop-start));
			
				
			start = stop;
			ICi ci = model.findCi(new Path("Offspring-" + i));
			stop = System.currentTimeMillis();
			
			System.out.println("LoadAlias;" + (stop-start));
			start = stop;
			ICi ci1 =model.find(ci.getId());
			stop = System.currentTimeMillis();
			System.out.println("LoadID;" + (stop-start));
			
			start = stop;			
			ICi parent = ci.getDerivedFrom();
			stop = System.currentTimeMillis();
			System.out.println("GetDerivedFrom;" + (stop-start));
			
			start = stop;
			Set<IAttribute> attributes = ci.getAttributes();
			stop = System.currentTimeMillis();
			System.out.println("LoadAttributes(" + attributes.size() + ");" + (stop-start));
			
		}
	}

	public void testSimpleManyInstances() {
		ICi t1 = createBlueprint("Bp1", ciRoot);
		ICi t2 = createBlueprint("Bp2", ciRoot);
		ICi t3 = createBlueprint("Bp3", ciRoot);
		
		ICi t2Ref = createBlueprint("T2Ref", ciRelationRoot);
		System.out.println("T2REF:" + t2Ref.toString());
		
		ICi t3Ref = createBlueprint("T3Ref", ciRelationRoot);
		
		ICi t2i1 = createOffspring(t2);
		ICi t3i1 = createOffspring(t3);
		
		
		for (int i = 0; i < 10; i++) {
			newAttribute(t1, "a" + i, SimpleTypeFactory.STRING, null, 1,
					1);
		}
		IAttribute refT2a = newAttribute(t1, "ref-t2", t2, t2Ref, 1,
				1);
		IAttribute refT3a = newAttribute(t1, "ref-t3", t3, t3Ref, 1,
				1);
		
		setValue(refT2a, t2i1);
		setValue(refT3a, t3i1);
		

		ICcb ccb = (ICcb) session.getService(ICcb.class);

		long startTime = System.currentTimeMillis();
		int COUNT = 100;
		for (int i = 0; i < COUNT; i++) {
			ICmdbTransaction tx = ccb.getTx(session);
			{
				ICiModifiable rootTemplate = tx.getTemplate(t1);
				ICiModifiable ipTemplate = rootTemplate.createOffspring();
				ipTemplate.setIsBlueprint(false);
			}
			ITicket ticket = ccb.submitTx(tx);

			IRfcResult result = ccb.waitForTx(ticket);
			Assert.assertEquals(false, result.isRejected());

		}
		long stopTime = System.currentTimeMillis();
		System.out.println("CREATED " + COUNT + " Instances in "
				+ (stopTime - startTime) + "ms");
		
		Set<ICi> ref2Instance = t2Ref.getOffsprings();
		Set<ICi> ref3Instance = t3Ref.getOffsprings();
		Assert.assertEquals(1, ref2Instance.size());
		Assert.assertEquals(1, ref3Instance.size());
	}

	public void testSetAttributeValue() {
		ICi blueprint = createOffspring(ciRoot);
		IAttribute a = newAttribute(blueprint, "time",
				SimpleTypeFactory.INTEGER, null, 1, 1);
		long ts = 0;

		long tsStart = System.currentTimeMillis();
		int COUNT = 400;
		
		for (int i = 0; i < COUNT; i++) {
			long start = System.currentTimeMillis();
			IValue v = SimpleTypeFactory.INTEGER.parseString("" + ts);
			setValue(a, v);
			long stop = System.currentTimeMillis();
			ts = (stop - start);
		}
		long tsStop = System.currentTimeMillis();

		// Retrive Histroy log.
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		long start = System.currentTimeMillis();
		List<IRFC> list = ccb.findRFCForCi(a);
		long stop = System.currentTimeMillis();
		System.out.println("Query RFC took " + (stop-start) + "ms");
		
		System.out.println("CHANGES MADE=" + list.size());
		MaxMinAvg avg = new MaxMinAvg();
		for (IRFC rfc : list) {
			if (rfc instanceof RFCModifyAttributeValue) {
				String newValue = ((RFCModifyAttributeValue)rfc).getNewValue();
				if (newValue != null) {
					avg.addValue(Double.parseDouble(newValue));
				}
			}
		}
		System.out.println("SET " + COUNT + " Values took "
				+ (tsStop - tsStart) + "ms" + "AVG:" + avg);

	}

	/**
	 * Helper classes.
	 */
	private ICi createBlueprint(String alias, ICi blueprint) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(blueprint);
			ICiModifiable ipTemplate = rootTemplate.createOffspring();
			ipTemplate.setAlias(alias);
			ipTemplate.setIsBlueprint(true);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		IModelService cisrvc = (IModelService) session
				.getService(IModelService.class);
		ICi ci = cisrvc.findCi(new Path<String>(alias));
		Assert.assertNotNull(ci);
		// System.out.println(ci.toString());
		return (ci);
	}

	private IAttribute newAttribute(ICi ci, String aName, IType type,
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
		Assert.assertEquals(false, result.isRejected());

		IAttribute newAttribute = null;
		for (IAttribute a : ci.getAttributes()) {
			if (a.getDisplayName().equals(aName)) {
				newAttribute = a;
				break;
			}
		}
		Assert.assertNotNull(newAttribute);
		return (newAttribute);
	}

	public ICi createOffspring(ICi ci) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		Set<ICi> beforeSet = ci.getOffsprings();
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ci);
			ICiModifiable ipTemplate = rootTemplate.createOffspring();
			ipTemplate.setIsBlueprint(false);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		// How can we retrive the new item!!!
		// The target id is stored in ipTemplate.
		Set<ICi> afterSet = ci.getOffsprings();
		ICi newCi = null;
		for (ICi offspringCi : afterSet) {
			if (!beforeSet.contains(offspringCi)) {
				newCi = offspringCi;
				break;
			}
		}

		Assert.assertNotNull(newCi);
		return (newCi);
	}

	public void setValue(ICi source, String aName, IValue value) {
		IAttribute theAttribute = null;
		for (IAttribute a : source.getAttributes()) {
			if (a.getDisplayName().equals(aName)) {
				theAttribute = a;
				break;
			}
		}
		setValue(theAttribute, value);
	}

	public void setValue(IAttribute theAttribute, IValue value) {
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		Assert.assertNotNull(theAttribute);
		ICmdbTransaction tx = ccb.getTx(session);
		{
			RFCModifyAttributeValue modValue = new RFCModifyAttributeValue();
			modValue.setTarget(theAttribute);
			if (value instanceof ICi) {
				modValue.setNewValueAsAlias(((ICi)value).getAlias());
			} else {
				modValue.setNewValue(value.getAsString());
			}
			// IAttributeModifiable aTemplate =
			// tx.getAttributeTemplate(theAttribute);
			// aTemplate.setValue(value);
			tx.add(modValue);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());
		
	}

	public IValue getValue(ICi ci, String aName) {
		for (IAttribute a : ci.getAttributes()) {
			if (a.getDisplayName().equals(aName)) {
				return (a.getValue());
			}
		}
		Assert.assertNotNull(null);
		return (null);
	}
}
