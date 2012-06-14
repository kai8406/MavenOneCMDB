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
package org.onecmdb.nagios;

import java.net.URL;

import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.transform.ComplexAttributeSelector;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.DataSetSelector;
import org.onecmdb.core.utils.transform.DefaultAttributeValueSelector;
import org.onecmdb.core.utils.transform.ForwardInstanceSelector;
import org.onecmdb.core.utils.transform.TransformEngine;
import org.onecmdb.core.utils.transform.matcher.RegExprMatcher;
import org.onecmdb.core.utils.transform.xml.XMLDataSource;
import org.onecmdb.core.utils.transform.xml.XPathAttributeSelector;
import org.onecmdb.core.utils.transform.xml.XPathComplexAttributeSelector;
import org.onecmdb.core.utils.transform.xml.XPathInstanceSelector;
import org.onecmdb.core.utils.wsdl.WSDLBeanProvider;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.utils.wsdl.AbstractCMDBCommand;

public class TestTransform extends AbstractCMDBCommand {

	
	public static void main(String argv[]) {
		TestTransform trans = new TestTransform();
		start(trans, new String[0][0], argv);
	}

	@Override
	public void process() throws Exception {
		DataSet linux = new DataSet();
		linux.setName("linux");
		linux.setInstanceSelector(new XPathInstanceSelector("linux", "NAGIOS_Host_linux-server", "/Tree/system[os2system/os/Family = 'Linux']"));
		linux.addAttributeSelector(new XPathAttributeSelector("host_name", "Hostname", true));
		linux.addAttributeSelector(new XPathAttributeSelector("address", "IPAddress", true));
		
		DataSetSelector sel = new DataSetSelector();
		sel.setName("select_host");
		RegExprMatcher matcher = new RegExprMatcher("linux", 
				new XPathAttributeSelector("expr", "os2system/os/Family", false),
				linux, true);
		sel.setInstanceSelector(new XPathInstanceSelector("selector", "NAGIOS_Host", "."));
		sel.addDataSetMatcher(matcher);
		
		
		DataSet tcp = new DataSet();		
		tcp.setName("tcp_service");
		tcp.setInstanceSelector(
				new XPathInstanceSelector("tcp_service", 
						"NAGIOS_Service_generic-service", 
						"/Tree//appl[Protocol='tcp']"));
		tcp.addAttributeSelector(
				new XPathComplexAttributeSelector("host_name", sel, true, "../../../.."));
		tcp.addAttributeSelector(
				new DefaultAttributeValueSelector("check_command", false, "NAGIOS_I_command_check_tcp", true));
		tcp.addAttributeSelector(new XPathAttributeSelector("service_description", "Port", true));
		DataSet fw = new DataSet();
		fw.setName("fw");
		fw.setInstanceSelector(new ForwardInstanceSelector());
		fw.addAttributeSelector(new ComplexAttributeSelector("fw-linux", linux));
		//fw.addAttributeSelector(new ComplexAttributeSelector("fw-sel", sel));
		fw.addAttributeSelector(new ComplexAttributeSelector("fw-tcp", tcp));
		
		
		XMLDataSource src = new XMLDataSource();
		src.addURL(new URL("http://localhost:8080/onecmdb-desktop/onecmdb/export?name=cmdb2nagios.cfg"));
		
		fw.setDataSource(src);
		WSDLBeanProvider provider = new WSDLBeanProvider(getService(), getToken());
		TransformEngine engine = new TransformEngine();
		engine.setWebService(getService());
		engine.setToken(getToken());
		
		
		IBeanProvider result = engine.transform(provider, fw);
		
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(result.getBeans());
		gen.transfer(System.out);
	}
}
