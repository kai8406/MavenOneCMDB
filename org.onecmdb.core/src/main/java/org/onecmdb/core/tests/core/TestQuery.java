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

import junit.framework.Assert;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;

/**
 * Test the query interface.
 *
 */
public class TestQuery extends AbstractOneCmdbTestCase {
	
	
	private ICi template;
	private ICi offspring1;
	private ICi offspring2;
	private ICi offspring3;
	private ICi offspring4;
	private ICi offspring5;

	
	public void setUp() {
	
		super.setUp();
		
		// Create things to serach for.
		
		template = testUtils.createTemplate(ciRoot, "Test");
		IAttribute a1 = testUtils.newAttribute(template, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		IAttribute a2 = testUtils.newAttribute(template, "a2", SimpleTypeFactory.STRING, null, 1, 1);
		IAttribute a3 = testUtils.newAttribute(template, "a3", SimpleTypeFactory.STRING, null, 1, 1);
		IAttribute a4 = testUtils.newAttribute(template, "a4", SimpleTypeFactory.STRING, null, 1, 1);
		testUtils.setValue(a1, SimpleTypeFactory.STRING.parseString("My test search string 1234 åland öland äland"), false);
		testUtils.setDescription(template, "This is the root ci of the TestCase.");
		testUtils.setDescription(a1, "Simple text Attribute. called a1");
		testUtils.setDescription(a2, "Simple text Attribute. called a2");
		testUtils.setDescription(a3, "Simple text Attribute. called a3");
		testUtils.setDescription(a4, "Simple text Attribute. called a4");
			
		offspring1 = testUtils.createInstance(template, "Offspring-1");
		testUtils.setValue(offspring1, "a1", SimpleTypeFactory.STRING.parseString("fisrt attribute Offspring 1234to Ci ..."), false);
		testUtils.setValue(offspring1, "a2", SimpleTypeFactory.STRING.parseString("Seconf attibute"), false);
		testUtils.setValue(offspring1, "a3", SimpleTypeFactory.STRING.parseString("Third"), false);
		testUtils.setValue(offspring1, "a4", SimpleTypeFactory.STRING.parseString("Forth"), false);
	
	
		offspring2 = testUtils.createInstance(template, "Offspring-2");
		offspring3 = testUtils.createInstance(template, "Offspring-3");
		offspring4 = testUtils.createInstance(template, "Offspring-4");
		offspring5 = testUtils.createInstance(template, "Offspring-5");
		
	}
	
	public void testWebQuery() {
		  QueryCriteria crit = new QueryCriteria();
		  crit.setText("icon");
			// Serach for ci and attributes.
			crit.setMatchCi(true);
			crit.setMatchAttribute(true);
			// Tex match on alias,description and value.
			crit.setTextMatchAlias(true);
			crit.setTextMatchDescription(true);
			crit.setTextMatchValue(true);
			
			// Match both template and instance attributes.
			crit.setMatchAttributeTemplates(true);
			crit.setMatchAttributeInstances(true); 
			
			// Always return instances
			crit.setMatchCiInstances(true);
			
			/*
	        if (getCommand().getMode().equalsIgnoreCase("DESIGN")) {
	        	// In designe mode match templates also.
	        	crit.setMatchCiTemplates(true);
	        	
	        }
	        */
			crit.setMatchCiTemplates(true);
	        
	        crit.setMaxResult(200);
	        QueryResult<ICi> qResult = testUtils.getModelService().query(crit);
	        System.out.println("Searching ... Result:" + qResult.size());
	
	}
	
	public void testMatchAttributeValueMaxResult() {
		// More than one template and instnaces
		QueryResult<ICi> total = null;
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("1234");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
					
			total = testUtils.getModelService().query(crit);
			Assert.assertEquals(6, total.size());
		}
		QueryResult<ICi> first = null;
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("1234");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
			crit.setFirstResult(0);
			crit.setMaxResult(4);
			
			first = testUtils.getModelService().query(crit);
			Assert.assertEquals(4, first.size());
		}
		QueryResult<ICi> last = null;
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("1234");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
			crit.setFirstResult(first.size());
			
			last = testUtils.getModelService().query(crit);
			Assert.assertEquals(2, last.size());
		}
		for (ICi ci : total) {
			if (first.contains(ci)) {
				continue;
			}
			if (last.contains(ci)) {
				continue;
			}
			fail("Ci '" + ci.getAlias() + "' is not part of part result!");
		}
		
	}

	public void testSimpelQuery() {
		IModelService service = testUtils.getModelService();
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("icon");
			crit.setMatchAttribute(true);
			QueryResult<ICi> result = service.query(crit);
			for (ICi ci : result) {
				System.out.println(ci.getDisplayName());
			}
		}
	}
	
	public void testMatchTemplateAttributeValueExact() {
		// Full search
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("My test search string 1234 åland öland äland");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
			crit.setMatchAttributeTemplates(true);
			
			QueryResult<ICi> result = testUtils.getModelService().query(crit);
			Assert.assertEquals(1, result.size());
			Assert.assertEquals(template, result.get(0));
			
		
		}
	}
	
	public void testMatchTemplateAttributeValuePartOf() {
		// Part of
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("åland");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
			crit.setMatchAttributeTemplates(true);
				
			QueryResult<ICi> result = testUtils.getModelService().query(crit);
			Assert.assertEquals(1, result.size());
			Assert.assertEquals(template, result.get(0));
		
		}
	}
	
	public void testMatchAttributeValuePartOf() {
		// More than one template and instnaces
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("1234");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
					
			QueryResult<ICi> result = testUtils.getModelService().query(crit);
			Assert.assertEquals(6, result.size());
		}
	}
	public void testMatchAttributeInstanceValuePartOf() {

		// More than one instances.
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("1234");
			crit.setMatchAttribute(true);
			crit.setTextMatchValue(true);
			crit.setMatchAttributeInstances(true);
			crit.setMatchAttributeTemplates(false);
			
					
			QueryResult<ICi> result = testUtils.getModelService().query(crit);
			Assert.assertEquals(5, result.size());
		}
	}
	
	public void testMatchAttributeTemplateAlias() {

		// Match template attribute alias.
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("a1");
			crit.setMatchAttribute(true);
			crit.setTextMatchAlias(true);
			crit.setMatchAttributeTemplates(true);
			crit.setMatchAttributeInstances(false);
						
			QueryResult<ICi> result = testUtils.getModelService().query(crit);
			Assert.assertEquals(1, result.size());
		}
	}
	
	public void testMatchAttributeAlias() {

		// Match template attribute alias.
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("a1");
			crit.setMatchAttribute(true);
			crit.setTextMatchAlias(true);
			crit.setMatchAttributeTemplates(false);
			crit.setMatchAttributeInstances(true);
						
			QueryResult<ICi> result = testUtils.getModelService().query(crit);
			Assert.assertEquals(5, result.size());
		}
		
	}
}
