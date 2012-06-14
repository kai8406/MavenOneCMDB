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
package org.onecmdb.core.example;

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAttributeModifiable;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IContainer;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IPath;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueProvider;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Runtime extends TestCase {

	private ICi ciRoot;

	private ICi ciRelationRoot;

	private ISession session;

	private GenericApplicationContext svrctx;

	private static class Initializer implements IValueProvider {
		private IValue initalValue;

		Initializer(IValue v) {
			this.initalValue = v;
		}

		public IValue fetchValueContent() {
			return initalValue;
		}

		public boolean isValid() {
			return false;
		}

		public Object getAdapter(Class type) {
			return null;
		}

	};

	public void setUp() {
		// Resource res = new
		// ClassPathResource("org/onecmdb/core/example/application.xml");
		Resource res = new ClassPathResource("application2.xml");
		XmlBeanFactory beanFactory = new XmlBeanFactory(res);
		svrctx = new GenericApplicationContext(beanFactory);

		// PropertyPlaceholderConfigurer cfg = new
		// PropertyPlaceholderConfigurer();
		// cfg.setLocation(new ClassPathResource("jdbc.properties"));
		// cfg.postProcessBeanFactory(beanFactory);

		final IOneCmdbContext cmdb = (IOneCmdbContext) svrctx
				.getBean("onecmdb");

		session = cmdb.createSession();
		IModelService cisvc = (IModelService) session
				.getService(IModelService.class);

		// well known name is ``root''
		ciRoot = cisvc.getRoot();

		assertNotNull(ciRoot);
		System.out.println(ciRoot.toString());
		// Dump offsprings...
		System.out.println(dumpOffsprings(ciRoot, 0));

		IReferenceService refSvc = (IReferenceService) session
				.getService(IReferenceService.class);

		ciRelationRoot = refSvc.getRootReference();
		assertNotNull(ciRelationRoot);

		System.out.println(ciRelationRoot.toString());
	}

	public void tearDown() {
		// svrctx.close();
	}

	public String dumpOffsprings(ICi ci, int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ci.toString());
		for (ICi o : ci.getOffsprings()) {
			for (int i = 0; i < level; i++) {
				buffer.append(" ");
			}
			buffer.append(dumpOffsprings(o, level + 1));
		}
		return (buffer.toString());
	}

	/**
	 * @param args
	 */
	public void xtestDisplayName() {

		Assert.assertNotNull(ciRoot);

		Assert.assertEquals("ROOT", ciRoot.getDisplayName());

		// test display name
		ConfigurationItem ci = (ConfigurationItem) ciRoot;
		ci.setDisplayNameExpression("name");
		Assert.assertEquals("name", ciRoot.getDisplayName());

		ci.setDisplayNameExpression("${name}");
		Assert.assertEquals("ROOT", ciRoot.getDisplayName());

		ci.setDisplayNameExpression("--${name}--");
		Assert.assertEquals("--ROOT--", ciRoot.getDisplayName());

	}

	/*
	 * public void testOffspring() {
	 *  // Begin
	 * 
	 * rootTemplate = session.getTemplate(ciRoot);
	 * 
	 * session.createOffspring(rootTemplate);
	 * 
	 * session.commit();
	 *  // End
	 * 
	 * ITicket ticket = session.doTransaction(new ITransactionJob() {
	 * 
	 * private ICmdbTransaction tx;
	 * 
	 * public void setICmdTransaction(ICmdbTransaction tx) { this.tx = tx; }
	 * 
	 * public String getName() { return("TestOffspring Testcase 1"); }
	 * 
	 * public void run() { rootTemplate = TemplateFactyory.getTemplate(ciRoot);
	 * 
	 * rootTemplate = ciRoot.getTemplate();
	 * 
	 * ICiModifiable rootTemplate = session.getTemplate(ciRoot); ICiModifiable
	 * template = rootTemplate.createOffspring(); template.createOffspring(); }
	 * });
	 *  // Add Validation. }
	 */

	public void testComplexType() {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ciRoot);
			ICiModifiable ipTemplate = rootTemplate.createOffspring();
			ipTemplate.setAlias("IP");

			ipTemplate.createAttribute("1", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
			ipTemplate.createAttribute("2", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
			ipTemplate.createAttribute("3", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
			ipTemplate.createAttribute("4", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		IModelService cisrvc = (IModelService) session
				.getService(IModelService.class);
		ICi ip = cisrvc.findCi(new Path<String>("IP"));

		Assert.assertNotNull(ip);
		System.out.println(ip.toString());

		// Create Instances of ip addresses.
		tx = ccb.getTx(session);
		{
			ICiModifiable ipTemplate = tx.getTemplate(ip);
			ICiModifiable ipInstance = ipTemplate.createOffspring();
			ipInstance.setAlias("IP1");
			ipInstance.setDerivedAttributeValue("1", 0, SimpleTypeFactory.UBYTE
					.parseString("192"));
			ipInstance.setDerivedAttributeValue("2", 0, SimpleTypeFactory.UBYTE
					.parseString("168"));
			ipInstance.setDerivedAttributeValue("3", 0, SimpleTypeFactory.UBYTE
					.parseString("1"));
			ipInstance.setDerivedAttributeValue("4", 0, SimpleTypeFactory.UBYTE
					.parseString("11"));
		}

		ticket = ccb.submitTx(tx);

		result = ccb.waitForTx(ticket);
		System.out.println("RESULT:" + result.toString());
		Assert.assertEquals(false, result.isRejected());

		// Find the new ip address.
		IPath<String> path = new Path<String>("IP");
		path.addElement("IP1");
		ICi ipI1 = cisrvc.findCi(path);
		Assert.assertNotNull(ipI1);
		System.out.println(ipI1.toString());
		System.out.println(dumpOffsprings(ip, 0));

		// Create a Server Blueprint containing one or many Ip Addresses.
		tx = ccb.getTx(session);
		{
			// Create a Server Blueprint that contains ONE reference to an Ip
			// Address.
			ICiModifiable rootTemplate = tx.getTemplate(ciRoot);
			ICiModifiable server = rootTemplate.createOffspring();
			server.setAlias("Server");
			// Specify minOccurs = 1, and maxOcccurs = -1
			// Also specify that we have direct link by saying null, on refType.
			server.createAttribute("ip", ip, null, 1, -1, null);
		}
		ticket = ccb.submitTx(tx);
		result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		ICi server = cisrvc.findCi(new Path<String>("Server"));
		Assert.assertNotNull(server);

		System.out.println(server.toString());

		// Create a instance of Server.
		tx = ccb.getTx(session);
		{
			// Create A Server Instance with ONE ipAdress [192.168.1.10]
			ICiModifiable serverInstance1 = tx.getTemplate(server)
					.createOffspring();
			serverInstance1.setAlias("Server1");

			// Connect the ip address to one ip address instance.
			serverInstance1.setDerivedAttributeValue("ip", 0, ipI1);
		}
		ticket = ccb.submitTx(tx);

		result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		path = new Path<String>("Server");
		path.addPath(new Path<String>("Server1"));

		ICi serverI1 = cisrvc.findCi(path);
		Assert.assertNotNull(serverI1);

		System.out.println(serverI1.toString());
	}

	public void testReferenceComplexType() {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		// Create a Blueprint that defines one IP Address.
		ICmdbTransaction tx = ccb.getTx(session);
		{
			ICiModifiable rootTemplate = tx.getTemplate(ciRoot);
			ICiModifiable ipTemplate = rootTemplate.createOffspring();
			ipTemplate.setAlias("IP");
			ipTemplate.setDisplayNameExpression("${1}.${2}.${3}.${4}");
			ipTemplate.createAttribute("1", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
			ipTemplate.createAttribute("2", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
			ipTemplate.createAttribute("3", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
			ipTemplate.createAttribute("4", SimpleTypeFactory.UBYTE, null, 1,
					1, null);
		}
		ITicket ticket = ccb.submitTx(tx);

		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		IModelService cisrvc = (IModelService) session
				.getService(IModelService.class);

		ICi ip = cisrvc.findCi(new Path<String>("IP"));
		Assert.assertNotNull(ip);
		System.out.println(ip.toString());

		// session.waitForCompletion(ticket);

		// assertEquals(false, session.isRejected());

		// Create Instances of ip addresses.
		tx = ccb.getTx(session);
		{
			ICiModifiable ipTemplate = tx.getTemplate(ip);
			ICiModifiable ipInstance = ipTemplate.createOffspring();
			ipInstance.setAlias("IP1");
			ipInstance.setDerivedAttributeValue("1", 0, SimpleTypeFactory.UBYTE
					.parseString("192"));
			ipInstance.setDerivedAttributeValue("2", 0, SimpleTypeFactory.UBYTE
					.parseString("168"));
			ipInstance.setDerivedAttributeValue("3", 0, SimpleTypeFactory.UBYTE
					.parseString("1"));
			ipInstance.setDerivedAttributeValue("4", 0, SimpleTypeFactory.UBYTE
					.parseString("11"));
		}

		ticket = ccb.submitTx(tx);

		result = ccb.waitForTx(ticket);
		System.out.println("RESULT:" + result.toString());
		Assert.assertEquals(false, result.isRejected());

		// Find the new ip address.
		Path<String> path = new Path<String>("IP");
		path.addElement("IP1");
		ICi ipI1 = cisrvc.findCi(path);
		Assert.assertNotNull(ipI1);
		System.out.println(ipI1.toString());
		System.out.println(dumpOffsprings(ip, 0));

		// Create a Server Blueprint containing one or many Ip Addresses.
		tx = ccb.getTx(session);
		{
			// Create a Server Blueprint that contains ONE reference to an Ip
			// Address.
			ICiModifiable rootTemplate = tx.getTemplate(ciRoot);
			ICiModifiable server = rootTemplate.createOffspring();
			server.setAlias("Server");
			// Specify minOccurs = 1, and maxOcccurs = -1
			// Also specify that we have direct link by saying null, on refType.
			server.createAttribute("ip", ip, null, 1, -1, null);
		}
		ticket = ccb.submitTx(tx);

		ICi server = cisrvc.findCi(new Path<String>("Server"));
		Assert.assertNotNull(server);

		System.out.println(server.toString());

		// Create a instance of Server.
		tx = ccb.getTx(session);
		{
			// Create A Server Instance with ONE ipAdress [192.168.1.10]
			ICiModifiable serverInstance1 = tx.getTemplate(server)
					.createOffspring();
			serverInstance1.setAlias("Server1");

			// Connect the ip address to one ip address instance.
			serverInstance1.setDerivedAttributeValue("ip", 0, ipI1);
		}
		ticket = ccb.submitTx(tx);

		path = new Path<String>("Server");
		path.addPath(new Path<String>("Server1"));

		ICi serverI1 = cisrvc.findCi(path);
		Assert.assertNotNull(serverI1);

		System.out.println(serverI1.toString());

	}

	public void xtestOffspringPerformance() {
		ICcb ccb = (ICcb) session.getService(ICcb.class);

		final int LEVELS = 5;
		final int LEAFS = 5;
		MaxMinAvg timer = new MaxMinAvg();

		for (int i = 0; i < 100; i++) {
			long startTime = System.currentTimeMillis();
			ICmdbTransaction tx = ccb.getTx(session);
			ICiModifiable rootTemplate = tx.getTemplate(ciRoot);
			for (int level = 0; level < LEVELS; level++) {
				ICiModifiable template = rootTemplate.createOffspring();
				for (int leafs = 0; leafs < LEAFS; leafs++) {
					template.createOffspring();
				}
			}
			ITicket ticket = ccb.submitTx(tx);
			IRfcResult result = ccb.waitForTx(ticket);

			Assert.assertEquals(null, result.getRejectCause());

			long stopTime = System.currentTimeMillis();
			timer.addValue((stopTime - startTime));
			System.out.println(i + "TEST TOOK " + timer + " ms");

		}
		System.out.println(timer.toString());
		System.out.println(dumpCi(ciRoot));

		// Test modifying a value....
		// Modifying Ci Root name.
		Set<IAttribute> attributes = ciRoot.getAttributes();
		IAttribute aAttribute = null;
		for (IAttribute a : attributes) {
			if (a.getUniqueName().equalsIgnoreCase("name")) {
				aAttribute = (IAttribute) a;
				break;
			}
		}
		final IAttribute theAttribute = aAttribute;
		Assert.assertNotNull(theAttribute);

		for (int i = 0; i < 1000; i++) {
			final int offset = i;
			long startTime = System.currentTimeMillis();
			ICmdbTransaction tx = ccb.getTx(session);
			IAttributeModifiable template = tx
					.getAttributeTemplate(theAttribute);
			template.setValue(theAttribute.getValueType().parseString(
					"ROOT" + offset));
			ITicket ticket = ccb.submitTx(tx);
			IRfcResult result = ccb.waitForTx(ticket);

			Assert.assertEquals(null, result.getRejectCause());

			long stopTime = System.currentTimeMillis();
			timer.addValue((stopTime - startTime));
			if ((i % 100) == 0) {
				System.out.println(i + "TEST TOOK " + timer);
			}
		}
		System.out.println("MODIFY VALUE TEST TOOK " + timer);
		System.out.println(dumpCi(ciRoot));
	}

	private String dumpCi(ICi ci) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ci.toString());
		buffer.append("\n");
		buffer.append("OFFSPRINGS=" + ci.getOffsprings().size());
		return (buffer.toString());
	}

	/*
	 * public void testOffsprings() {
	 * 
	 * ciRoot.toString();
	 * 
	 * 
	 * Set<IAttribute> ciRootAttrs = ciRoot.getAttributes();
	 * 
	 * 
	 * Assert.assertNotNull(ciRoot); Assert.assertTrue(ciRoot.getDerivedFrom() ==
	 * null);
	 * 
	 * 
	 * 
	 * 
	 * root.getP root.getDerivedFrom() --> null root.getOffsprings() --> {
	 *  / * Container * Association ( TextDocument String Integer )
	 * 
	 *  }
	 * 
	 * container.getOffSprings() --> { Folder Category Group Roles }
	 * 
	 * Folder.getITems(
	 * 
	 * 
	 * 
	 * 
	 * root is a CI having attributes like: createdBy: Kalle Kula
	 * 
	 * createdFoe: acme ... ...
	 * 
	 * next level of objects caintaeind in the root can now be retrivied:
	 * ^^^^^^^^^^
	 * 
	 * 
	 * root.getItems() --->
	 * 
	 * README
	 * 
	 * --> getOffSprings --> {} --> getDerivedFrom() --> TextDocument
	 * 
	 * 
	 * root.getOffspprings ---> {}
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * To relate itmes together, an association is needed
	 * 
	 * 
	 *  // create intial data model ITicket ticket = session.doTransaction(new
	 * ITransactionJob() { public int run(ICmdbTransaction tx) {
	 *  // ALT1, textual description.
	 * 
	 * new TextRFCRender(tx, new ITextInput() { public String
	 * getRFCSpecification() { StringBuffer buffer = new StringBuffer();
	 * buffer.append("<createOffspring id=\"offspring1\" from=\"id#" +
	 * RunTime.rootCi.getId().asLong() + "\"/>"); buffer.append("<createAttribute
	 * to=\"ref#offspring\" name=\"A1\" type=\"oncmdeb/string\" value=\"V1\"
	 * />"); return(buffer.toString()); } }).run();
	 * 
	 *  // ALT2: java coding style // decrations of the new JavaRFCRender(tx,
	 * new IJavaCode() { public void run(ICmdbTransaction tx) { ICiModifiable
	 * rootTemplate = getTemplate(ciRoot);
	 *  // Should be done by the AssociationService // Create a new ci with two
	 * new attributes(provide,requiers) // Will also modify the new ci's name to
	 * "Assosications" // Need to know the names of the received attributes... // //
	 * Responsable for connection two ci to each other. { ICiModifiable
	 * assocTemplate = rootTemplate.createOffspring();
	 * 
	 * assocTemplate.modifyAttributeValue("name", "Associations");
	 * assocTemplate.setDisplayName("${&require.value} ${require->${displayName}
	 * --> ${provide}"); IAttributeModifiable attributeAssocTemplate =
	 * template.createAttribute("provide", new MimeType("onecmdb/x-ciid"), new
	 * Initializer(null)); IAttributeModifiable attributeAssocTemplate =
	 * template.createAttribute("require", new MimeType("onecmdb/x-ciid"), new
	 * Initializer(null)); }
	 *  // Should be done by the TypeService // Problamatic here, eht should
	 * this do, we use types as string when // createing attributes. It could
	 * dynamically introduce more knowalge to the // String representation than
	 * just a String. Meaning that // the marsler-class is responsable to
	 * marshle the string representation // to an Java Object/XML/Serializable
	 * and back? { ICiModifiable typeTemplate = rootTemplate.createOffspring();
	 * 
	 * typeTemplate.modifyAttributeValue("name", "Types");
	 * typeTemplate.modifyDisplayName("${mime-type}"); nameAttributeTemplate =
	 * template.createAttribute("mime-type", new MimeType("onecmdb/string"), new
	 * Initializer(null)); typeTemplate =
	 * template.createAttribute("marsler-class", new MimeType("onecmdb/class"),
	 * new Initializer(null));
	 *  // Create the types we have used!!!!! ITemplate template =
	 * typeTemplate.createOffspring(); template.modifyAttribute("mime-type",
	 * "onecmdb/x-ciid"); template.modifyAttribute("marsler-class",
	 * "org.onecmdeb.internal.types.ItemIdType"); }
	 *  // JobService // The ci's are responsable to provide values accoring //
	 * to policies. { ICiModifiable providerTemplate =
	 * rootTemplate.createOffspring();
	 * serverTemplate.modifyAttributeValue("name", "Providers");
	 * 
	 * typeTemplate = template.createAttribute("provider-class", new
	 * MimeType("onecmdb/class"), new Initializer(null));
	 * 
	 *  }
	 *  {
	 *//**
		 * Create a new Server Ci with a Attribute ip Address
		 */
	/*
	 * ICiModifiable serverTemplate = rootTemplate.createOffspring();
	 * serverTemplate.modifyAttributeValue("name", "Server");
	 * 
	 * IAttributeModifiable ipAddress = serverTemplate.createAttribute("IP
	 * Address", new MimeType("text/x-ipaddress"), new Initializer("x.x.x.x"));
	 * ipAddress.createAttribute("1", new MimeType("text/plain"), new
	 * Initializer(192)); ipAddress.createAttribute("2", new
	 * MimeType("text/plain"), new Initializer(168));
	 * ipAddress.createAttribute("3", new MimeType("text/plain"), new
	 * Initializer(1)); ipAddress.createAttribute("4", new
	 * MimeType("text/plain"), new Initializer(111));
	 *  }
	 *  {
	 *//**
		 * Create
		 */
	/*
	 * }
	 *  } });
	 * 
	 * 
	 * 
	 * ICiExtensible eroot = ciRoot.getCiExtensableAdaptor(tx);
	 * 
	 * eroot.createOffspring("#newroot");
	 * 
	 * ICiExtensible eassoc = assoc.getCiExtensableAdaptor(tx);
	 * 
	 *//** connects to the provide part of an exiting ci */
	/*
	 * IAttribute provide = eassoc.createOffspring("provide", new
	 * MimeType("onecmdb/x-ciid"), new Initializer(null));
	 * 
	 *//** connects to the require part of an exiting ci */
	/*
	 * IAttribute require = eassoc.createOffspring("require", new
	 * MimeType("onecmdb/x-ciid"), new Initializer(null));
	 * 
	 * ICi container = null; ticket = eroot.createOffspring(); ICiExtensible
	 * econtainer = (ICiExtensible) assoc.getAdapter(ICiExtensible.class);
	 * IAttribute items = econtainer.createOffspring("itmes", new
	 * MimeType("onecmdb/x-ciid"), new Initializer(null) );
	 * 
	 * ICi server = null; ticket = eroot.createOffspring();
	 * 
	 * 
	 * Assert.assertEquals(eroot, server.getDerivedFrom());
	 * 
	 * ICiExtensible eserver = (ICiExtensible)
	 * server.getAdapter(ICiExtensible.class);
	 * 
	 * 
	 * final IAttribute ipaddr = eserver.createOffspring("IP Address", new
	 * MimeType("text/x-ipaddress"), new Initializer("x.x.x.x") );
	 * 
	 *  // fetch the abstract implementations for our basic objects
	 * 
	 * 
	 * 
	 *  // Assert.assertEquals(1, server.getAttributes().size());
	 * 
	 * IExtensibleAttribute eipaddr = (IExtensibleAttribute)
	 * ipaddr.getAdapter(IExtensibleAttribute.class);
	 * Assert.assertNotNull(eipaddr);
	 * 
	 * IAttribute[] ip = new IAttribute[4]; ip[0] = eipaddr.createOffspring("1",
	 * new MimeType("text/plain"), new Initializer(192)); ip[1] =
	 * eipaddr.createOffspring("2", new MimeType("text/plain"), new
	 * Initializer(168)); ip[2] = eipaddr.createOffspring("3", new
	 * MimeType("text/plain"), new Initializer(1)); ip[3] =
	 * eipaddr.createOffspring("4", new MimeType("text/plain"), new
	 * Initializer(111));
	 * 
	 * 
	 * IValueProvider vp = new IValueProvider() { { fetchValueContent(); }
	 * 
	 * public Object fetchValueContent() { IModifiableAttribute mipaddr =
	 * (IModifiableAttribute) ipaddr.getAdapter(IModifiableAttribute.class);
	 * ConfigurationItem ci = (ConfigurationItem) ipaddr; String v =
	 * ci.evaluate("${1}.${2}.${3}.${4}"); mipaddr.setValue(v); return v; }
	 * 
	 * public boolean isValid() { return false; }
	 * 
	 * public Object getAdapter(Class type) { return null; }};
	 * 
	 * 
	 * Assert.assertEquals("192.168.1.111", ipaddr.getValue());
	 * 
	 * IAttribute productsheet = eserver.createOffspring("Product Sheet", new
	 * MimeType("text/html"), new Initializer("unresolved") );
	 * 
	 * UrlProvider urlprovider = new UrlProvider("http://www.kth.se");
	 * urlprovider.attach((IModifiableAttribute)
	 * productsheet.getAdapter(IModifiableAttribute.class));
	 * 
	 * try { Thread.sleep(1000); } catch (InterruptedException e) {
	 * e.printStackTrace(); }
	 * 
	 * IAttribute productsheet = eserver.createAttribute("Product Sheet", new
	 * MimeType("attr/struct") ); //Assert.assertEquals(2,
	 * server.getAttributes().size()); System.out.println(server.toString());
	 * return 0; }
	 * 
	 * public String getName() { return("Test1 Inserttion"); }
	 * 
	 * }); }
	 */

	public void xtestInitialization() {

		IType baseType = null;
		for (ICi offspring : ciRoot.getOffsprings())
			if ("TYPE".equals(offspring.getDisplayName())) {
				baseType = (IType) offspring;
				break;
			}
		Assert.assertNotNull(baseType);

		IContainer baseContainer = null;
		for (ICi offspring : ciRoot.getOffsprings())
			if ("CONTAINER".equals(offspring.getDisplayName())) {
				baseType = (IType) offspring;
				break;
			}
		Assert.assertNotNull(baseContainer);

		IValueProvider baseProvider = null;
		for (ICi offspring : ciRoot.getOffsprings())
			if ("PROVIDER".equals(offspring.getDisplayName())) {
				baseProvider = (IValueProvider) offspring;
				break;
			}
		Assert.assertNotNull(baseProvider);

	}

}
