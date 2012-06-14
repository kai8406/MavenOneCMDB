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
package org.onecmdb.core.tests.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.onecmdb.core.IReference;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueSelector;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;


/**
 * Main OneCMDB core test.
 * 
 */
public class TestOneCmdbCore extends AbstractOneCmdbTestCase {

	
	
	public TestOneCmdbCore() {
		this(new OneCMDBTestConfig());
	}
	
	public TestOneCmdbCore(OneCMDBTestConfig config) {
		super(config);
	}


	public String dumpOffsprings(ICi ci, int level) {
		StringBuffer buffer = new StringBuffer();
		String isBlueprint = "I";
		if (ci.isBlueprint()) {
			isBlueprint = "B";
		}
		buffer.append(ci.getAlias() + ":" + isBlueprint);
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
	
	public void testDeleteReferences() {
		ICi target = testUtils.createTemplate(this.ciRoot, "Target");
		ICi source = testUtils.createTemplate(this.ciRoot, "Source");
			
		IAttribute a = testUtils.newAttribute(source, "toTarget", target, this.ciRelationRoot, 1, 1);
		
		// Create instances
		ICi t1 = testUtils.createInstance(target, "T1");
		ICi s1 = testUtils.createInstance(source, "S1");
		
		testUtils.setValue(s1, "toTarget", t1);
		
		System.out.println(s1.toString());
		
		List<IAttribute> attrs = s1.getAttributesWithAlias("toTarget");
		
		for (IAttribute attr : attrs) {
			BasicAttribute ba = (BasicAttribute)attr;
			System.out.println("ValueAsLong=" + ba.getValueAsLong());
			System.out.println("ValueAsString=" + ba.getValueAsString());
		}
		
		testUtils.destroyCi(t1);
	
		attrs = s1.getAttributesWithAlias("toTarget");
		
		for (IAttribute attr : attrs) {
			BasicAttribute ba = (BasicAttribute)attr;
			System.out.println("ValueAsLong=" + ba.getValueAsLong());
			System.out.println("ValueAsString=" + ba.getValueAsString());
		}
		
		System.out.println(s1.toString());
		//System.exit(-1);
	}
	
	
	
	public void testGUICreateTemplate() {
		List<IAttribute> rootIcons = this.ciRoot.getAttributesWithAlias("icon");
		Assert.assertEquals(1, rootIcons.size());
		IAttribute rootIcon = rootIcons.get(0);
	
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		{
			// Update root icon.
			ICmdbTransaction tx = ccb.getTx(session);
			{
				IAttributeModifiable aMod = tx.getAttributeTemplate(rootIcon);
				aMod.setValue(SimpleTypeFactory.STRING.parseString("resource"));
		
			}
			ITicket ticket = ccb.submitTx(tx);
	
			IRfcResult result = ccb.waitForTx(ticket);
			Assert.assertEquals(null, result.getRejectCause());
		}
		
		
		{
			// Create an empty instance.
			ICmdbTransaction tx = ccb.getTx(session);
			ICiModifiable instance = null;
			{
				ICiModifiable rootTemplate = tx.getTemplate(this.ciRoot);
				instance = rootTemplate.createOffspring();
				instance.setAlias("Testing");
				instance.setIsBlueprint(false);
			}
			ITicket ticket = ccb.submitTx(tx);
	
			IRfcResult result = ccb.waitForTx(ticket);
			Assert.assertEquals(null, result.getRejectCause());
		}
		// Reload Testing CI.
		ICi test = this.testUtils.findAlias("Testing");
		Assert.assertNotNull(test);
		
		List<IAttribute> icons = test.getAttributesWithAlias("icon");
		Assert.assertEquals(1, icons.size());
		IAttribute icon = icons.get(0);
		
		{
			ICmdbTransaction tx = ccb.getTx(session);
			ICiModifiable instance = null;
			{
				instance = tx.getTemplate(test);
				
				// Set icon.
				IAttributeModifiable aMod = tx.getAttributeTemplate(icon);
				aMod.setValue(SimpleTypeFactory.STRING.parseString("resource"));
				
				// Convert to template.
				instance.setIsBlueprint(true);
			}
			ITicket ticket = ccb.submitTx(tx);
	
			IRfcResult result = ccb.waitForTx(ticket);
			Assert.assertEquals(null, result.getRejectCause());
		}
	}
	
	public void testGetAllComplexTypes() {
		IModelService model = (IModelService)this.session.getService(IModelService.class);
		Set<IType> allTypes = model.getAllComplexTypes(null);
		for (IType type : allTypes) {
			System.out.println("\t" + type.getDisplayName() + ":" + type.getClass().getSimpleName());
		}
	}
	
	public void testAttributeDisplayNameExpression() {
		OnecmdbTestUtils testUtils = new OnecmdbTestUtils(this.session);
		
		ICi test = testUtils.createTemplate(this.ciRoot, "Test");
		IAttribute a = testUtils.newAttribute(test, "name", SimpleTypeFactory.STRING, null, 1, 1);
		
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		
		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			IAttributeModifiable aTemplate = tx.getAttributeTemplate(a);
			aTemplate.setDisplayNameExpression("Name is the one to change");
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(null, result.getRejectCause());
		
		
		ICi inst = testUtils.createInstance(test, "test-1");
		IAttribute iA = inst.getAttributesWithAlias("name").get(0);
		Assert.assertEquals("Name is the one to change", iA.getDisplayName());
		
		a = test.getAttributesWithAlias("name").get(0);
		Assert.assertEquals("Name is the one to change", a.getDisplayName());	

		
		
	}
	
	public void testGetOffspringsTypes() {
		OnecmdbTestUtils testUtils = new OnecmdbTestUtils(this.session);
	
		ICi parent = testUtils.createTemplate(this.ciRoot, "Parent");
		ICi child1 = testUtils.createTemplate(parent, "Child1");
		ICi child2 = testUtils.createTemplate(parent, "Child2");
		ICi childI1 = testUtils.createInstance(child1, "IChild1");
		ICi childI2 = testUtils.createInstance(child2, "IChild2");
		
		Set<IType> set = parent.getAllOffspringTypes();
		Assert.assertEquals(2, set.size());
		
	}
	
	public void testGenerateXml()  {
		// OneCmdb Provider.
		IModelService model = (IModelService) session.getService(IModelService.class);
		OneCmdbBeanProvider onecmdbProvider = new OneCmdbBeanProvider();
		onecmdbProvider.setModelService(model);
		
		// Generate Xml file.
		XmlGenerator gen = new XmlGenerator();
		gen.setOutput("testGenerateModel.xml");
		gen.setBeans(onecmdbProvider.getBeans());
		try {
			gen.process();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		// Read Xml back.
		XmlParser fileProvider = new XmlParser();
		try {
			fileProvider.setURL(new File("testGenerateModel.xml").toURL().toExternalForm());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}

		// Validate that we don't have any empty references
		
		{
			BeanScope scope = new BeanScope();
		
			scope.setBeanProvider(fileProvider);
			scope.setValidation(true);
			scope.process();
			// Other this to ask the scope for, used to verify.
			scope.getDuplicatedBeans();
			scope.getReposiotryBeanUsed();
			scope.getSimpleTypesUsed();
			for (String unresolved : scope.getUnresolvedAliases()) {
				System.out.println("Unresolved alias:" + unresolved);
			}
			Assert.assertEquals(0, scope.getUnresolvedAliases().size());
		
		}
	
		{
			// Compare two provides, and generate RFC for the diff.
			// Will not support delete.
			// TODO: rename BeanScope to something else.
			BeanScope scope = new BeanScope();
			scope.setBeanProvider(fileProvider);		
			scope.setRemoteBeanProvider(onecmdbProvider);
			
			scope.process();
			List<IRFC> list = scope.getRFCs();
			Assert.assertEquals(0, list.size());
		}
	}

	
	public void testDisplayNameInheritange() {
		Assert.assertNotNull(ciRoot);
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		String path = this.getClass().getPackage().getName().replace('.', '/');
		try {
			utils.importXml("res:" + path + "/DisplayNameInherite.xml");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("" + e);
		}
		ICi item = utils.findAlias("Item");
		Assert.assertNotNull(item);
		ICi child1 = utils.findAlias("Child1");
		Assert.assertNotNull(child1);
		ICi child2 = utils.findAlias("Child2");
		Assert.assertNotNull(child2);
		ICi child3 = utils.findAlias("Child3");
		Assert.assertNotNull(child3);
		
		System.out.println(utils.dumpOffsprings(item,0));
		
		Assert.assertEquals("Item.Category/Item", item.getDisplayName());
		Assert.assertEquals("Child1.Category/Child1", child1.getDisplayName());
		Assert.assertEquals("Child1.Category/Child2", child2.getDisplayName());
		Assert.assertEquals("Child1.Category/Child3", child3.getDisplayName());
						
	}


	
	public void testDescriptionInheritage() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		ICi ci = utils.createTemplate(ciRoot, "Test");
		IAttribute a = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		a = (IAttribute) utils.setDescription(a, "My description");
		Assert.assertEquals("My description", a.getDescription());
		
		ICi child = utils.createTemplate(ci, "Child1");
		List<IAttribute> list = child.getAttributesWithAlias("a1");
		Assert.assertEquals(1, list.size());
		IAttribute cA = list.get(0);
		Assert.assertEquals("My description", cA.getDescription());
			
	}
	
	
	
	public void testDestoryUsedType() {
		
	}
	
	public void testDestroyUntilMinOccursAttribute() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		ICi ci = utils.createTemplate(ciRoot, "CI1");
		IAttribute a = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 3);
		
		List<IAttribute> attributes = ci.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
	
		ICi instance = utils.createInstance(ci, "instance1");
		attributes = instance.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
		IAttribute iAttribute = attributes.get(0);
		
		// Try to destory attribute on instance, should fail.
		utils.destroyCi(iAttribute, true);
		
		utils.addAttribute(instance, "a1");
		
		attributes = instance.getAttributesWithAlias("a1");
		Assert.assertEquals(2, attributes.size());
	
		utils.destroyCi(iAttribute);
		attributes = instance.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
	}
	
	public void testDestroyAttribute() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		ICi ci = utils.createTemplate(ciRoot, "CI1");
		IAttribute a = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		
		List<IAttribute> attributes = ci.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
	
		testUtils.destroyCi(a);
	
		
		attributes = ci.getAttributesWithAlias("a1");
		Assert.assertEquals(0, attributes.size());
		
		a = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		attributes = ci.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());

		ICi instance = utils.createInstance(ci, "instance1");
		attributes = instance.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
		IAttribute iAttribute = attributes.get(0);
		
		// Try to destory attribute on instance, should fail.
		utils.destroyCi(iAttribute, true);

		attributes = ci.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
	
		attributes = instance.getAttributesWithAlias("a1");
		Assert.assertEquals(1, attributes.size());
		
		// Try to destory attribute on instance, should fail.
		utils.destroyCi(a);

		attributes = ci.getAttributesWithAlias("a1");
		Assert.assertEquals(0, attributes.size());
	
		attributes = instance.getAttributesWithAlias("a1");
		Assert.assertEquals(0, attributes.size());
		
		
		
		
	}
	
	
	public void testRelinkReference() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		
		// Create reference type
		ICi refTp1 = utils.createTemplate(ciRelationRoot, "refT1");
		
		// Create two templates.
		ICi tp1 = utils.createTemplate(ciRoot, "TP1");
		ICi tp2 = utils.createTemplate(ciRoot, "TP2");
		
		IAttribute a = utils.newAttribute(tp1, "refToTp2", tp2, refTp1, 1, 1);
		utils.setValue(a, (IValue)tp2, false);
	
		Assert.assertEquals(1, refTp1.getOffsprings().size());
		
		utils.setValue(a, (IValue)tp2, false);
	
		Assert.assertEquals(1, refTp1.getOffsprings().size());
	
	
	}
	
	
	
	
	public void testReferences() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		
		// Create reference type
		ICi refTp1 = utils.createTemplate(ciRelationRoot, "refT1");
		
		// Create two templates.
		ICi tp1 = utils.createTemplate(ciRoot, "TP1");
		ICi tp2 = utils.createTemplate(ciRoot, "TP2");
		
		IAttribute a = utils.newAttribute(tp1, "refToTp2", tp2, refTp1, 1, 1);
		utils.setValue(a, (IValue)tp2, false);
		
		IReferenceService refSvc = (IReferenceService)this.session.getService(IReferenceService.class);
		Set<IReference> refs = refSvc.getReferrers(tp2);
		
		Assert.assertEquals(1, refs.size());
		IReference ref = refs.iterator().next();
		Assert.assertNotNull(ref);
		
		ICi target = ref.getTarget();
		Assert.assertEquals(tp2, target);
		
		Set<ICi> sources = ref.getSourceCis();
		Assert.assertEquals(1, sources.size());
		ICi source = sources.iterator().next();
		Assert.assertNotNull(source);
		Assert.assertEquals(tp1, source);
		
		Set<IAttribute> sourceAtts = ref.getSourceAttributes();
		Assert.assertEquals(1, sourceAtts.size());
		IAttribute sourceAtt = sourceAtts.iterator().next();
		Assert.assertNotNull(sourceAtt);
		Assert.assertEquals(a, sourceAtt);
	
	}
	
	public void testComplexReferences() {
		// Create a target template
		ICi target = testUtils.createTemplate(ciRoot, "Target");
		
		// Create a target instance.
		ICi targetInstance = testUtils.createInstance(target, "Target-instance");
		
		// Create a 'direct' source template. 
		ICi sourceDirectLink = testUtils.createTemplate(ciRoot, "Direct-Source");
		
		// Add link attribute to source template.
		testUtils.newAttribute(sourceDirectLink, "reference", target, null, 1, 1);
	
		// Create a source instance.
		ICi sourceDI1 = testUtils.createInstance(sourceDirectLink, "Direct-source-instance");

		// Connect the source attribute to the target instance.
		testUtils.setValue(sourceDI1, "reference", targetInstance);

		// Validate the 'direct' link.
		IValue value = testUtils.getValue(sourceDI1, "reference");
		Assert.assertEquals(targetInstance, value);
		
		// Create a 'in-direct' source template.
		ICi sourceRefLink = testUtils.createTemplate(ciRoot, "REFERNCE_LINK");
		testUtils.newAttribute(sourceRefLink, "reference", target, ciRelationRoot, 1, 1);
		
		// Create the 'in-direct' source instance.
		ICi sourceRefI1 = testUtils.createInstance(sourceRefLink, "Refernce_link_instance");
		testUtils.setValue(sourceRefI1, "reference", targetInstance);

		// Validate value.
		IValue value2 = testUtils.getValue(sourceRefI1, "reference");
		Assert.assertEquals(targetInstance, value2);

		// Get the reference service.
		IReferenceService refService = (IReferenceService) session
				.getService(IReferenceService.class);
		
		// Validate that the target has two references.
		Set<ICi> cis = refService.getOriginCiReferrers(targetInstance);
		
		Assert.assertEquals(2, cis.size());

		for (ICi ci : cis) {
			if (ci.equals(sourceRefI1)) {
				continue;
			}
			if (ci.equals(sourceDI1)) {
				continue;
			}
			Assert.assertEquals("", targetInstance.getAlias()
					+ " is has not correct referred " + ci.toString());
		}

	}

	public void testUniqueAliasName() {
		String alias = "ALIASNAME";
		ICi ci = testUtils.createTemplate(ciRoot, "ALIASNAME");
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		
		ICi shouldFail = testUtils.createTemplate(ciRoot, "ALIASNAME", true);
		Assert.assertEquals(null, shouldFail);

		
		// Set alias with space
		// Create new with same alias name.
		shouldFail = testUtils.createTemplate(ciRoot, "alias with space", true);
		Assert.assertEquals(null, shouldFail);

		// Set alias with no letter first char
		// Create new with same alias name.
		shouldFail = testUtils.createTemplate(ciRoot, "1alias", true);
		Assert.assertEquals(null, shouldFail);

		// Set alias with no letter first char
		// Create new with same alias name.
		shouldFail = testUtils.createTemplate(ciRoot, "#alias", true);
		Assert.assertEquals(null, shouldFail);

		// Create new with alias as alias name.
		ICi ok = testUtils.createTemplate(ciRoot, "alias", false);
		Assert.assertNotNull(ok);
	}
	
	public void testInheriteAttribuite() {
		ICi tp1 = testUtils.createTemplate(ciRoot, "TP1");
		IAttribute a = testUtils.newAttribute(tp1, "a1", SimpleTypeFactory.STRING, null, 1,1);
		
		ICi ip1 = testUtils.createInstance(tp1, null);
		List<IAttribute> list = ip1.getAttributesWithAlias("a1");
		Assert.assertEquals(1, list.size());
		IAttribute na = list.get(0);
		Assert.assertNull(na.getValue());
	
		testUtils.setValue(a, a.getValueType().parseString("Testing"));

		list = ip1.getAttributesWithAlias("a1");
		Assert.assertEquals(1, list.size());
		na = list.get(0);
		Assert.assertNotNull(na.getValue());
		Assert.assertEquals("Testing", na.getValue().getAsString());
		
		ICi tp2 = testUtils.createTemplate(tp1, "TP2");
		list = tp2.getAttributesWithAlias("a1");
		Assert.assertEquals(1, list.size());
		na = list.get(0);
		Assert.assertNotNull(na.getValue());
		Assert.assertEquals("Testing", na.getValue().getAsString());
	}
	
	public void testFindAllCis() {
		IModelService model = (IModelService) session.getService(IModelService.class);
		Set<ICi> all1 = model.getAllCis();
		Set<IType> types1 = model.getAllComplexTypes(null);
		Set<ICi> templates1 = model.getAllTemplates(null);
		
		ICi tp1 = testUtils.createTemplate(ciRoot, "TP1");
		
		testUtils.createInstance(tp1, null);
		
		Set<ICi> all2 = model.getAllCis();
		Set<IType> types2 = model.getAllComplexTypes(null);
		Set<ICi> templates2 = model.getAllTemplates(null);
		
		// One template and one instance.
		Assert.assertEquals(2, all2.size() - all1.size());
		Assert.assertEquals(1, types2.size() - types1.size());
		Assert.assertEquals(1, templates2.size() - templates1.size());
	}
	
	
	public void testAddableAttribute() {
		// Test with unlimit..
		{
			ICi tp1 = testUtils.createTemplate(ciRoot, "TP1");
			testUtils.newAttribute(tp1, "a1", SimpleTypeFactory.STRING, null, 0, -1);
			
			ICi i1 = testUtils.createInstance(tp1, null);
			List<IAttribute> addable = i1.getAddableAttributes();
			Assert.assertEquals(1, addable.size());
			Assert.assertEquals("a1", addable.get(0).getAlias());
			
			// Add alot of attributes.
			for (int i = 0; i < 100; i++) {
				testUtils.addAttribute(i1, "a1");
			}
			
			testUtils.addAttribute(i1, "a1");
			addable = i1.getAddableAttributes();
			Assert.assertEquals(1, addable.size());
			Assert.assertEquals("a1", addable.get(0).getAlias());
		}
		
		
		// Test with limit..
		{
			ICi tp2 = testUtils.createTemplate(ciRoot, "TP2");
			testUtils.newAttribute(tp2, "a1", SimpleTypeFactory.STRING, null, 0, 4);
			
			ICi i1 = testUtils.createInstance(tp2, null);
			List<IAttribute> addable = i1.getAddableAttributes();
			Assert.assertEquals(1, addable.size());
			Assert.assertEquals("a1", addable.get(0).getAlias());
			
			
			testUtils.addAttribute(i1, "a1");
			addable = i1.getAddableAttributes();
			Assert.assertEquals(1, addable.size());
			Assert.assertEquals("a1", addable.get(0).getAlias());
			
			testUtils.addAttribute(i1, "a1");
			testUtils.addAttribute(i1, "a1");
			testUtils.addAttribute(i1, "a1");
			
			addable = i1.getAddableAttributes();
			Assert.assertEquals(0, addable.size());
		}
		
	
	}
	
	public void testAttribute() {
		IModelService cisrvc = (IModelService) session
		.getService(IModelService.class);

		ICi tp1 = testUtils.createTemplate(ciRoot, "TP1");
		
		ICi i1 = testUtils.createInstance(tp1, null);
		
		IAttribute tpa1 = testUtils.newAttribute(tp1, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		
		List<IAttribute> iaList = i1.getAttributesWithAlias("a1");
		
		Assert.assertNotNull(iaList);
		
		Assert.assertEquals(1, iaList.size());
		
		ICi parent = tp1;
		for (int i = 0; i < 10; i++) {
			parent = testUtils.createTemplate(parent, "TP1" + i);
		}
		
		IAttribute tpa2 = testUtils.newAttribute(tp1, "a2", SimpleTypeFactory.STRING, null, 1, 1);
		// Now all should have a2...
		validateAttribute("a1", tp1);
	
		validateAttribute("a2", tp1);
		
	}
	
	private void validateAttribute(String aName, ICi ci) {
		List<IAttribute> iaList = ci.getAttributesWithAlias(aName);
		
		System.out.println("Validate attr<" + aName+ "> : " + ci.toString());
		Assert.assertNotNull(iaList);
		Assert.assertEquals(1, iaList.size());
		
		for (ICi offspring : ci.getOffsprings()) {
			validateAttribute(aName, offspring);
		}
	}
	
	public void testDestroy() {

		IModelService cisrvc = (IModelService) session
				.getService(IModelService.class);

		ICi target = testUtils.createTemplate(ciRoot, "CI1");

		ICi reloaded = cisrvc.find(target.getId());

		assertEquals(target, reloaded);

		testUtils.destroyCi(target);
		// Check that it has been removed.

		ICi destroyedCi = cisrvc.find(target.getId());
		assertEquals(null, destroyedCi);
	}
	

	public void testValueSelector() {
		// Check String Type.
		IType sType = SimpleTypeFactory.STRING;
		IValueSelector sSelector = sType.getValueSelector();
		Assert.assertEquals(true, sSelector.isInfinite());

		ICi bp = testUtils.createTemplate(ciRoot, "BP1");
		testUtils.setDisplaynameExpression(bp, "BP ${alias}");
		// Create 5 instances, flat.
		for (int i = 0; i < 5; i++) {
			testUtils.createInstance(bp, "instance-" + i);
		}
		// Convert bp to a type.
		IType bpType = (IType) bp;

		IValueSelector selector = bpType.getValueSelector();

		// Should not be infinit.
		Assert.assertEquals(false, selector.isInfinite());
		Assert.assertNotNull(selector.getSet());
		Assert.assertEquals(5, selector.getSet().size());

		ICi bp2 = testUtils.createTemplate(bp, "BP2");
		// Should be the same.
		Assert.assertEquals(5, selector.getSet().size());

		for (int i = 0; i < 5; i++) {
			testUtils.createInstance(bp2, "bp2-instance-" + i);
		}
		// Should now be 10
		Assert.assertEquals(10, selector.getSet().size());

		IType bp2Type = (IType) bp2;

		IValueSelector selector2 = bp2Type.getValueSelector();

		// Should not be infinit.
		Assert.assertEquals(false, selector2.isInfinite());
		Assert.assertNotNull(selector2.getSet());
		Assert.assertEquals(5, selector2.getSet().size());

	}


	public void testDisplayName() {
		Assert.assertNotNull(ciRoot);

		ICi target = testUtils.createTemplate(ciRoot, "TARGET");
		testUtils.setDisplaynameExpression(target, "TARGET");
		ICi targetI1 = testUtils.createInstance(target, null);
		IAttribute ta1 = testUtils.newAttribute(targetI1, "ta1", SimpleTypeFactory.STRING,
				null, 1, 1);
		testUtils.setValue(ta1, SimpleTypeFactory.STRING.parseString("T_V1"));

		ICi ci = testUtils.createTemplate(ciRoot, null);
		IAttribute ia1 = testUtils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null,
				1, 1);
		IAttribute ia2 = testUtils.newAttribute(ci, "a2", SimpleTypeFactory.STRING, null,
				1, 1);
		IAttribute complex = testUtils.newAttribute(ci, "complex", target, null, 1, 1);

		testUtils.setValue(ia1, SimpleTypeFactory.STRING.parseString("V1"));
		testUtils.setValue(ia2, SimpleTypeFactory.STRING.parseString("V2"));
		testUtils.setValue(complex, targetI1);

		ci = testUtils.setDisplaynameExpression(ci, "PLAIN");
		Assert.assertEquals("PLAIN", ci.getDisplayName());

		ci = testUtils.setDisplaynameExpression(ci, "${a1}");
		Assert.assertEquals("V1", ci.getDisplayName());

		ci = testUtils.setDisplaynameExpression(ci, "--${a1}--${a2}--");
		Assert.assertEquals("--V1--V2--", ci.getDisplayName());

		ci = testUtils.setDisplaynameExpression(ci, "--${a1}--${a2}--${complex}");
		Assert.assertEquals("--V1--V2--TARGET", ci.getDisplayName());

		targetI1 = testUtils.setDisplaynameExpression(targetI1, "TARGET ${ta1}");
		Assert.assertEquals("--V1--V2--TARGET T_V1", ci.getDisplayName());

		/*
		 * // test display name ConfigurationItem ci = (ConfigurationItem)
		 * ciRoot; ci.setDisplayName("name"); Assert.assertEquals("name",
		 * ciRoot.getDisplayName());
		 * 
		 * ci.setDisplayName("${name}"); Assert.assertEquals("ROOT",
		 * ciRoot.getDisplayName());
		 * 
		 * ci.setDisplayName("--${name}--"); Assert.assertEquals("--ROOT--",
		 * ciRoot.getDisplayName());
		 */
	}
	
}
