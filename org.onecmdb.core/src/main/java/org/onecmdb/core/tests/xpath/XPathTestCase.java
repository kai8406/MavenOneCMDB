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
package org.onecmdb.core.tests.xpath;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.dialect.MySQLInnoDBDialect;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.utils.xpath.commands.AuthCommand;
import org.onecmdb.core.utils.xpath.commands.CreateCommand;

public class XPathTestCase extends AbstractOneCmdbTestCase {

	
	
	@Override
	protected String getProviderResource() {
		return("org/onecmdb/core/tests/xpath/provider.xml");
	}

	private String authenticat(String user, String pwd) throws IllegalAccessException {
		AuthCommand cmd = new AuthCommand(getCmdbContext());
		cmd.setUser("test");
		cmd.setPwd("1qaz1qaz");
		
		String token = cmd.getToken();
		return(token);
	}
	
	public void xtestAuth() {
		String token = null;
		try {
			token = authenticat("test", "1qaz1qaz");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(token);
	}
	
	public void testCreate() {
		
		String token = null;
		try {
			token = authenticat("test", "1qaz1qaz");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.toString());
		}
		
	
		
		
		{ // 1) Test New Template Offspring
			
			{ // 1.1) Simple Offspring.
				CreateCommand cmd = new CreateCommand(getCmdbContext());
				cmd.setAuth(token);
				
				cmd.setPath("/template/Router/offspring/Netgear");
				try {
					cmd.transfer(System.out);
				} catch (Throwable t) {
					Assert.fail("Create template offspring: " + t.toString());
				}
				
				// Validate that the new template exists.
				ICi router = testUtils.getModelService().findCi(new Path<String>("Router"));
				Assert.assertNotNull(router);
				
				ICi netgear = testUtils.getModelService().findCi(new Path<String>("Netgear"));
				Assert.assertNotNull(netgear);
				Assert.assertEquals(router, netgear.getDerivedFrom());
			}
		
			
			{ // 1.2 Offspring with setttings.
				CreateCommand cmd = new CreateCommand(getCmdbContext());
				cmd.setAuth(token);
				cmd.setPath("/template/Ci/offspring/Port");
				cmd.setInputAttributes("description=A Network port.");
				try {
					cmd.transfer(System.out);
				} catch (Throwable t) {
					Assert.fail("Create template adn set description: " + t.toString());
				}
			}
			
		}
		
		
		{ // 2) Create new attribute.
			{ // 2.1 Singel value attribute
				CreateCommand cmd = new CreateCommand(getCmdbContext());
				cmd.setAuth(token);
				
				cmd.setPath("/template/Netgear/attribute/version");
				cmd.setInputAttributes("defaultValue=1.2.3");
				try {
					cmd.transfer(System.out);
				} catch (Throwable t) {
					Assert.fail("Create template attribute: " + t.toString());
				}
				// Validating...
				ICi ci = testUtils.findAlias("Netgear");
				Assert.assertNotNull(ci);
				
				IAttribute definition = ci.getAttributeDefinitionWithAlias("version");
				
				// Must be there
				Assert.assertNotNull(definition);
				
				// Check owner.
				Assert.assertEquals(ci, definition.getOwner());
				
				// Check default type.
				Assert.assertEquals(SimpleTypeFactory.STRING, definition.getValueType());
			}
			
			
			
		}
		
		
		
		{ // 2) Create new multi-value attribute
			{ 
				CreateCommand cmd = new CreateCommand(getCmdbContext());
				cmd.setAuth(token);
				cmd.setPath("/template/Netgear/attribute/port");
				cmd.setInputAttributes("policy/maxOccurs=4;policy/minOccurs=0;defaultValue=0");
				try {
					cmd.transfer(System.out);
				} catch (Throwable t) {
					Assert.fail("Create template attribute: " + t.toString());
				}
				//	Validating...
				ICi ci = testUtils.findAlias("Netgear");
				Assert.assertNotNull(ci);
				
				IAttribute definition = ci.getAttributeDefinitionWithAlias("port");
				
				// Must be there
				Assert.assertNotNull(definition);
				
				// Check owner.
				Assert.assertEquals(ci, definition.getOwner());
				
				// Check default type.
				Assert.assertEquals(SimpleTypeFactory.STRING, definition.getValueType());
				
				// Check Policy
				Assert.assertEquals(0, definition.getMinOccurs());
				Assert.assertEquals(4, definition.getMaxOccurs());
				
				// Check how many attributes there exists.
				List<IAttribute> attributes = ci.getAttributesWithAlias("port");
			}
			
			{ // 2.2 Set default value.
				// NOTE: One default value entry (can be null) is always defined on the template.
				CreateCommand cmd = new CreateCommand(getCmdbContext());
				cmd.setAuth(token);
				for (int i = 1; i < 4; i ++) {
					cmd.setPath("/template/Netgear/attribute/port/defaultValue/n" + i);
					cmd.setInputAttributes("n" + i + "=" + i);
					try {
						cmd.transfer(System.out);
					} catch (Throwable t) {
						Assert.fail("Create template attribute: " + t.toString());
					}
				}
				// Validating.
				ICi ci = testUtils.findAlias("Netgear");
				List<IAttribute> attributes = ci.getAttributesWithAlias("port");
			}
		}
		
		{ // 3. Create instance.
			{ 
				CreateCommand cmd = new CreateCommand(getCmdbContext());
				cmd.setAuth(token);
				cmd.setPath("/instance/Netgear/n1");
				try {
					cmd.transfer(System.out);
				} catch (Throwable t) {
					Assert.fail("Create template attribute: " + t.toString());
				}
				//	Validating...
				ICi ci = testUtils.findAlias("n1");
				Assert.assertNotNull(ci);
				
				// Check default value for version
				List<IAttribute> versionAttributes = ci.getAttributesWithAlias("version");
				Assert.assertEquals(1, versionAttributes.size());
				Assert.assertEquals("1.2.3", versionAttributes.get(0).getValue().getAsString());
				
				// Check default value for port (multi-value)
				List<IAttribute> portAttributes = ci.getAttributesWithAlias("port");
				Assert.assertEquals(4, portAttributes.size());
				
				// Check default values.
				for (IAttribute attr : portAttributes) {
					System.out.println("attr.getValue()=" + attr.getValue());
					System.out.println("attr.getValue().getAsString()=" + attr.getValue().getAsString());
					System.out.println("attr.getValue().getDisplayName()=" + attr.getValue().getDisplayName());
					boolean found = false;
					for (int i = 0; i < 4; i++) {
						if (attr.getValue().getDisplayName().equals("" + i)) {
							found=true;
							break;
						}
					}
					if (!found) {
						Assert.assertEquals("Default value not correct<0|1|2|3>", attr.getValue().getAsString());
					}
					
				}
			}
			
		}
		
	}
	
	public void testUpdate() {		
	}
	
	
	public void testDelete() {
		
	}
	
	public void xtestQuery() {
		
	}



	
}
