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
package org.onecmdb.utils.internal.nmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.jxpath.xml.DOMParser;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class TransformNmap {

	public static void main(String argv[]) {
		TransformNmap transform = new TransformNmap();
		transform.setInput(argv[0]);
		List<CiBean> beans;
		try {
			beans = transform.transform();
		
			XmlGenerator generator = new XmlGenerator();
			generator.setOutput(argv[1]);
			generator.setBeans(beans);
			generator.process();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	private String inputFile;

	private String nicTemplate;
	private String ipTemplate;
	private String hostnameTemplate;
	private String dnsEntryTemplate;
	private String netIfTemplate;

	private IBeanProvider beanProvider;

	public void setInput(String inputFile) {
		this.inputFile = inputFile;
	}
	
	public void setBeanProvider(IBeanProvider provider) {
		this.beanProvider = provider;
	}
	
	public List<CiBean> transform() throws SAXException, IOException {

		// Parse discovery result
			DOMParser parser = new DOMParser();
			Document doc = (Document) parser.parseXML(null);
			//parser.parse(inputFile);
			//Document doc = parser.

			NodeList hostList = doc.getElementsByTagName("host");
			if (hostList == null) {
				return(Collections.EMPTY_LIST);
			}

			List<CiBean> allBeans = new ArrayList<CiBean>();

			for (int i = 0, j = 0; i < hostList.getLength(); i++) {
				System.out.println("${progress} " + i);
				Element host = (Element) hostList.item(i);
				List<CiBean> currentBeans = new ArrayList<CiBean>();
				if (host != null) {
					Element status = (Element) host.getElementsByTagName(
							"status").item(0);
					String state = status.getAttribute("state");
					
					
					ValueBean stateValue = new ValueBean();
					stateValue.setAlias("state");
					stateValue.setValue(state);
					
					
					CiBean ipBean = new CiBean();
					ipBean.setDerivedFrom(ipTemplate);
					ipBean.setTemplate(false);
					
					CiBean nicBean = new CiBean();
					nicBean.setDerivedFrom(nicTemplate);
					nicBean.setTemplate(false);
					
					CiBean netIfBean = new CiBean();
					netIfBean.setDerivedFrom(netIfTemplate);
					netIfBean.setTemplate(false);
					
					// Set state on NetIf.
					netIfBean.addAttributeValue(stateValue);
					
					String ipAddress = null;
					j++;
					NodeList addrList = host
								.getElementsByTagName("address");
					for (int a = 0; a < addrList.getLength(); a++) {
						Element el = (Element) addrList.item(a);
						String addr = el.getAttribute("addr");
						String type = el.getAttribute("addrtype");
						
						if (type.equals("mac")) {
							nicBean.setAlias("mac-" + addr.replace(":", "."));
							// Set mac address.
							nicBean.addAttributeValue(new ValueBean("mac", addr, false));
							
							// Set vendor.
							String vendor = el.getAttribute("vendor");
							nicBean.addAttributeValue(new ValueBean("vendor", vendor, false));
							
							// Connect nicBean to netif.
							netIfBean.addAttributeValue(new ValueBean("nic", nicBean.getAlias(), true));
							
							
							
						} else {
							ipBean.setDerivedFrom(ipTemplate);
							ipBean.setAlias("ip-" + addr);
							
							// Set ipAddress
							ipAddress = addr;
							ipBean.addAttributeValue(new ValueBean("ipAddress", addr, false));
							
							// Set addr type
							ipBean.addAttributeValue(new ValueBean("addrType", type, false));
							
							// Connect nicBean to netif.
							netIfBean.addAttributeValue(new ValueBean("ipAddress", ipBean.getAlias(), true));
						}
					}
					netIfBean.setAlias("netif-" + ipBean.getAlias());
					
					// Validate that the state is ok, since we retrive all ip's..
					if (state.equals("down")) {
						if (beanProvider != null) {
							CiBean remote = beanProvider.getBean(netIfBean.getAlias());
							if (remote == null) {
								continue;
							}
						}
					}
					currentBeans.add(ipBean);
					// Can be empty (loocal host)
					if (nicBean.getAlias() != null) {
						currentBeans.add(nicBean);
					}
					currentBeans.add(netIfBean);
					
					NodeList hostsNameList = host
					.getElementsByTagName("hostnames");
					for (int hs = 0; hs < hostsNameList.getLength(); hs++) {
						Element hostnames = (Element) hostsNameList
						.item(hs);
						NodeList hostnameList = hostnames
						.getElementsByTagName("hostname");
						for (int h = 0; h < hostnameList.getLength(); h++) {
							Element hostname = (Element) hostnameList
							.item(h);
							String name = hostname.getAttribute("name");
							String type = hostname.getAttribute("type");
							
							CiBean hostnameBean = new CiBean();
							hostnameBean.setAlias("c-" + name);
							hostnameBean.setTemplate(false);
							hostnameBean.setDerivedFrom(hostnameTemplate);
							
							// Set hostname
							hostnameBean.addAttributeValue(new ValueBean("hostname", name, false));
							
							// Connect this to a dnsEntry.
							CiBean dnsEntry = new CiBean();
							dnsEntry.setTemplate(false);
							dnsEntry.setDerivedFrom(dnsEntryTemplate);
							dnsEntry.setAlias(name + "." + ipAddress);
							dnsEntry.addAttributeValue(new ValueBean("ip", ipBean.getAlias(), true));
							dnsEntry.addAttributeValue(new ValueBean("hostname", hostnameBean.getAlias(), true));
							dnsEntry.addAttributeValue(new ValueBean("type", type, false));
							
							// Add it to the bean list.
							currentBeans.add(hostnameBean);
							currentBeans.add(dnsEntry);
							
						}
					}
				}
				allBeans.addAll(currentBeans);
			}
			return(allBeans);
	}

	public String getDnsEntryTemplate() {
		return dnsEntryTemplate;
	}

	public void setDnsEntryTemplate(String dnsEntryTemplate) {
		this.dnsEntryTemplate = dnsEntryTemplate;
	}


	
	public String getHostnameTemplate() {
		return hostnameTemplate;
	}

	public void setHostnameTemplate(String hostnameTemplate) {
		this.hostnameTemplate = hostnameTemplate;
	}

	public String getIpTemplate() {
		return ipTemplate;
	}

	public void setIpTemplate(String ipTemplate) {
		this.ipTemplate = ipTemplate;
	}

	public String getNicTemplate() {
		return nicTemplate;
	}

	public void setNicTemplate(String nicTemplate) {
		this.nicTemplate = nicTemplate;
	}

	public String getNetIfTemplate() {
		return netIfTemplate;
	}

	public void setNetIfTemplate(String netIfTemplate) {
		this.netIfTemplate = netIfTemplate;
	}

	
	
	
}
