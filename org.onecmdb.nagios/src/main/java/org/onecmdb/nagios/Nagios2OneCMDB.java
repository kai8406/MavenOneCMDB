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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.modelmbean.XMLParseException;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.jgroups.util.GetNetworkInterfaces1_4;
import org.onecmdb.core.utils.MemoryBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.utils.wsdl.AbstractCMDBCommand;
import org.onecmdb.utils.wsdl.CMDBChangeUpload;

/**
 * Convert Nagios XML description to OneCMDB Template/Instance Model.
 * Special handling:
 * 	use == Derived From
 *  name == Template name.
 *  register==0 --> No instance only template
 * @author niklas
 *
 */
public class Nagios2OneCMDB extends AbstractCMDBCommand {

	class NagiosConfigEntry {
		public String id;
		public String value;
		public String description;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		
	}
	
	class NagiosConfig {
		public String type;
		Map<String, NagiosConfigEntry> entries = new HashMap<String, NagiosConfigEntry>();
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Map<String, NagiosConfigEntry> getEntries() {
			return entries;
		}
		public void setEntries(Map<String, NagiosConfigEntry> entries) {
			this.entries = entries;
		}
		
		public void addEntry(NagiosConfigEntry entry) {
			this.entries.put(entry.getId(), entry);
		}
		
		public String getValueForAttr(String name) {
			NagiosConfigEntry entry = this.entries.get(name);
			if (entry == null) {
				return(null);
			}
			return(entry.getValue());
		}
		
	}
	
	private String input;
	private String output;
	
	private static String ARGS[][] = {
		{"input", "Input file/dirctory. If directory files with .cfg are tried.", null},
		{"output", "Output file, - stdout", "-"},
	};
	
	public static void main(String argv[]) {
		Nagios2OneCMDB nagios2cmdb = new Nagios2OneCMDB();
		nagios2cmdb.handleArgs(ARGS, argv);
		try {
			nagios2cmdb.process();
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("ERROR:" + t.getMessage());
			System.exit(-1);
		}
		System.exit(0);
	}

	
	@Override
	public void process() {
	
		List<CiBean> result = new ArrayList<CiBean>();
		OutputStream out = System.out;
		FileInputStream in = null;
		
		
		weekDayMap = new HashSet<String>();
		weekDayMap.add("sunday");
		weekDayMap.add("monday");
		weekDayMap.add("tuesday");
		weekDayMap.add("wednesday");
		weekDayMap.add("thursday");
		weekDayMap.add("friday");
		weekDayMap.add("saturday");
	
		buildReferenceMap();
		
		
		File file = new File(input);
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.getName().endsWith(".xml")) {
					try {
						in = new FileInputStream(f);
						result.addAll(parse(in));
					} catch (Exception e) {
						throw new IllegalArgumentException("Error parsing file " + f.getPath() + "", e);
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
							}
						}				
					}
				}
			}
		} else {
			try {
				in = new FileInputStream(file);
				result.addAll(parse(in));
			} catch (Exception e) {
				throw new IllegalArgumentException("Error parsing file " + file.getPath() + "", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}				
			}
		}

		handleServiceRelations(result, serviceRelations);
		
		try {
			if (!output.equals("-")) {
				out = new FileOutputStream(output);
			}
			
			// Validate....
			for (CiBean bean : result) {
				for (ValueBean v : bean.getAttributeValues()) {
					if (v.isComplexValue()) {
						if (!beanExists(result, v.getValue())) {
							bean.removeAttributeValue(v);
							System.out.println("Missing: " + v.getValue() + " in " + bean.getAlias());
						}
					}
				}
			}
			
			XmlGenerator gen = new XmlGenerator();
			gen.setBeans(result);
			
			gen.transfer(out);
			System.out.println("Generated " + result.size() + " CI:s");
		} catch (Exception e) {
			throw new IllegalArgumentException("Error writing file " + output + "", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	private void handleServiceRelations(List<CiBean> result,
			List<ServiceRelation> sR) {
		MemoryBeanProvider prov = new MemoryBeanProvider(result.toArray(new CiBean[0]));
		System.out.println("ServideRelations counts " + sR.size());
		int resolved = 0;
		for (ServiceRelation rel : sR) {
			String value = rel.getValue();
			String values[] = value.split(",");
			
			
			if (!rel.getType().equals("servicegroup")) {
				System.out.println("TODO: Handle " + rel.getType());
				continue;
			}
			
			if (!((values.length % 2) == 0)) {
				System.out.println("Missconfigured service relation: " + value + " in object " + rel.getBean());
				continue;
			}
			System.out.println("Handle SR " + rel.getType() + ":" + rel.getBean());
			// Here we have host,service group pair.
			for (int i = 0; i < values.length; i += 2) {
				String host = values[i];
				String descr = values[i+1];
				
				// Need to find a service with this set...
				boolean found = false;
				for (CiBean bean : result) {
					
					String sDesc = bean.toStringValue("service_description");
					if (sDesc == null) {
						continue;
					}

					if (sDesc.equals(descr)) {
						for (ValueBean v : bean.fetchAttributeValueBeans("host_name")) {
							CiBean hostBean = prov.getBean(v.getValue());
							if (host.equals(hostBean.toStringValue("host_name"))) {
								System.out.println("Set " + rel.getBean().getAlias() + "[" + rel.getId().getId() + "]=" + bean);
								updateCMDBValue(rel.getBean(), rel.getId().getId(), bean.getAlias(), true);
								found = true;
								break;
							}
						}
					}

					if (found) {
						resolved++;
						break;
					}
				}
			}
		}
		System.out.println("Service Relations resolved " + resolved + "(" + sR.size() + ")");
	}


	private boolean beanExists(List<CiBean> result, String value) {
		for (CiBean bean : result) {
			if (bean.getAlias().equals(value)) {
				return(true);
			}
		}
		return(false);
	}

	private Map<String, String> referenceMap = new HashMap<String, String>();
	private Set<String> weekDayMap;
	private List<ServiceRelation> serviceRelations = new ArrayList<ServiceRelation>();
	
	public List<CiBean> parse(InputStream in) throws DocumentException {
		
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(in);
		Element root = document.getRootElement();
		
		List<CiBean> beans = new ArrayList<CiBean>();
		for (Element el : (List<Element>)root.elements()) {
			// Handle different types.
			
			NagiosConfig cfg = toNagiosConfig(el);
			
			boolean createTemplate = false;
			boolean createInstance = false;
		
			if ("0".equals(cfg.getValueForAttr("register"))) {
			
				createInstance = false;
				createTemplate = true;
			} else if (cfg.getValueForAttr("name") != null) {
				createInstance = true;
				createTemplate = true;
			} else {
				createInstance = true;
				createTemplate = false;
			}
			if (createTemplate) {
				beans.add(getBean(cfg, true));	
			}
			if (createInstance) {
				CiBean bean = getBean(cfg, false);
				if (createTemplate) {
					updateCMDBValue(bean, "useName", "true", false);
				}
				beans.add(bean);
			}
		}
		return(beans);
	}
	
	public void transform(InputStream in, OutputStream out) throws DocumentException {
		
		List<CiBean> beans = parse(in);
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(beans);
		try {
			gen.transfer(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void buildReferenceMap() {
		// Call OneCMDB to get referenceMap...
		IOneCMDBWebService service = null;
		try {
			 service = getService();
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't connect to OneCMDB [" + getUrl() + "]" , e);
		}
		
		// Load NAGIOS Templates.
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector sel = new ItemOffspringSelector("nagios", "NAGIOS");
		sel.setMatchTemplate(true);
		sel.setLimitToChild(false);
		sel.setPrimary(true);
		
		q.addSelector(sel);
		
		Graph result = service.queryGraph(getToken(), q);
		result.buildMap();
		for (CiBean bean : result.fetchAllNodeOffsprings()) {
			for (AttributeBean aBean : bean.getAttributes()) {
				if (aBean.isComplexType()) {
					String key = fromCMDBName(bean.getAlias()) + "/" + aBean.getAlias();
					String value = fromCMDBName(aBean.getType());
					referenceMap.put(key, value);
					System.out.println("ADD: " + key + "->" + value);
				}
			}
		}			
		/*
		referenceMap.put("host/parents", "host");
		referenceMap.put("host/hostgroups", "hostgroup");
		referenceMap.put("host/check_command", "command");
		referenceMap.put("host/check_period", "timeperiod");
		referenceMap.put("host/event_handler", "command");
		referenceMap.put("host/contact_groups", "contactgroup");
		referenceMap.put("host/notification_period", "timeperiod");
		
		referenceMap.put("service/host_name", "host");
		referenceMap.put("service/hostgroup_name", "hostgroup");
		referenceMap.put("service/servicegroups", "servicegroup");
		referenceMap.put("service/check_command", "command");
		referenceMap.put("service/check_period", "timeperiod");
		referenceMap.put("service/event_handler", "command");
		referenceMap.put("service/notification_period", "timeperiod");
		referenceMap.put("service/contacts", "contact");
		referenceMap.put("service/contact_groups", "contactgroup");
				
		
		
		referenceMap.put("hostgroup/members", "host");
		referenceMap.put("hostgroup/hostgroup_members", "hostgroup");
		
		referenceMap.put("servicegroup/members", "service");
		referenceMap.put("servicegroup/servicegroup_members", "servicegroup");
		*/
	}


	private NagiosConfig toNagiosConfig(Element el) {
		NagiosConfig cfg = new NagiosConfig();
		cfg.setType(el.getName());
		for (Element attr : (List<Element>)el.elements()) {
			NagiosConfigEntry entry = new NagiosConfigEntry();
			entry.setId(attr.getName());
			entry.setValue(getElementValue(attr, "value", true));
			entry.setDescription(getElementValue(attr, "description", false));
			cfg.addEntry(entry);
		}
		return(cfg);
	}

	private void updateBean(CiBean bean, NagiosConfig cfg) {
		
	}
	private CiBean getBean(NagiosConfig cfg, boolean template) {
		CiBean bean = new CiBean();
		
		// Register == 0, only template.
		if (template) {
			
			bean.setTemplate(true);
			bean.setAlias(toCMDBName(cfg.getType()) + "_" + cfg.getValueForAttr("name"));
			System.out.println("Create Template : " + bean.getAlias());
		} else {
			bean.setTemplate(false);
			bean.setAlias(getAlias(cfg));
			updateCMDBValue(bean, "register", "1", false);
		}
		
		String uses = cfg.getValueForAttr("use");
		if (uses == null) {
			bean.setDerivedFrom(toCMDBName(cfg.getType()));
		} else {
			bean.setDerivedFrom(toCMDBName(cfg.getType()) + "_" + uses);
		}
	
		updateValues(bean, cfg);
		
		return(bean);
	}
	
	private String fromCMDBName(String type) {
		String nagiosType = type.replace("NAGIOS_","");
		return(nagiosType.toLowerCase());
	}
	
	private String toCMDBName(String type) {
		String first = type.substring(0,1);
		String rest =  type.substring(1);
		
		return("NAGIOS_" + first.toUpperCase() + rest);
	}

	private String getAlias(NagiosConfig cfg) {
		String id = cfg.getValueForAttr(cfg.getType() + "_name");
		if (cfg.getType().equals("service")) {
			String host = cfg.getValueForAttr("host_name");			
			String descr = cfg.getValueForAttr("service_description");
			id = descr + (host == null ? "" : "_" + host.hashCode());
		}
		if (cfg.getType().equals("servicedependency")) {
			String dHostName = cfg.getValueForAttr("dependent_host_name");
			String dServiceDescr = cfg.getValueForAttr("dependent_service_description");
			String hostName = cfg.getValueForAttr("host_name");
			String serviceDescr = cfg.getValueForAttr("service_description");
			if (dHostName == null) {
				dHostName = "";
			}
			if (hostName == null) {
				hostName = "";
			}
			id = dHostName.hashCode() + "_" + dServiceDescr + "_" + hostName.hashCode() + "_" + serviceDescr;
		}
		id = handleSpace(id);
			
		String alias = "NAGIOS_I_" + cfg.getType() + "_" + id;
		return(alias);
	}


	private void updateValues(CiBean bean, NagiosConfig cfg) {
		updateCMDBValue(bean, "objectType", cfg.getType(), false);
		
		// Special handling for Timeperiod.
		if (cfg.getType().equals("timeperiod")) {
			doTimeperiod(bean, cfg);
			return;
		}

		
		for (NagiosConfigEntry entry : cfg.getEntries().values()) {
			String value = entry.getValue();
			
			
			// References and multivalues.
			String type = getReferenceType(cfg, entry); 
			if (type != null) {
				
				// Special handling for check_command..
				if (type.equals("command")) {
					String values[] = value.split("!", 2);
					
					updateCMDBValue(bean, entry.getId(), "NAGIOS_I_" + type + "_" + handleSpace(values[0]), true);
					
					if (values.length > 1) {
						updateCMDBValue(bean, entry.getId()+"_arg", handleSpace(values[1]), false);
					}
				} else if (type.equals("service")) {
					System.out.println("Service Relation:" + value);
					addServiceToResolve(bean, entry, cfg.getType(), value);
				} else {
					String values[] = value.split(",");
					for (String v : values) {
						v = v.trim();
						v = handleSpace(v);
						updateCMDBValue(bean, entry.getId(), "NAGIOS_I_" + type + "_" + v, true);
					}
				}
			} else {
				updateCMDBValue(bean, entry.getId(), value, false);
			}
		}
	}
	
	private void addServiceToResolve(CiBean bean, NagiosConfigEntry id, String type, String value) {
		ServiceRelation rel = new ServiceRelation();
		rel.setBean(bean);
		rel.setId(id);
		rel.setValue(value);
		rel.setType(type);
		serviceRelations .add(rel);
	}


	private String handleSpace(String value) {
		StringBuffer buf = new StringBuffer();
		if (value != null) {
			for (Character c : value.toCharArray()) {
				
				if (c.isWhitespace(c) || c.equals(':') || c.equals('/')) {
					buf.append("_");
				} else {
					buf.append(c);
				}
				/*
				if (c.isLetterOrDigit(c)) {
					buf.append(c);
				} else {
					buf.append("_");
				}
				*/
			}
		}
		return(buf.toString());
	}


	private ValueBean updateCMDBValue(CiBean bean, String alias, String value,
			boolean complex) {
		
		// Validate that attribute exists.
		
		
		ValueBean vBean = new ValueBean();
		vBean.setAlias(alias);
		vBean.setValue(value);
		vBean.setComplexValue(complex);
		
		bean.addAttributeValue(vBean);
		
		return(vBean);
	}


	/**
	 * Special handling for timeperiod.
	 * @param bean
	 * @param cfg
	 */
	private void doTimeperiod(CiBean bean, NagiosConfig cfg) {
		for (NagiosConfigEntry entry : cfg.getEntries().values()) {
			String value = entry.getValue();
			String name = entry.getId();
			
			if (name.equals("exclude")) {
				String values[] = value.split(",");
				for (String v : values) {
					updateCMDBValue(bean, entry.getId(), "NAGIOS_I_" + "timeperiod" + "_" + value, true);
				}
			} else if (name.equals("alias")) {
				updateCMDBValue(bean, entry.getId(), value, false);
			} else if (name.equals("timeperiod_name")) {
				updateCMDBValue(bean, entry.getId(), value, false);
			} else if (name.equals("use")) {
				updateCMDBValue(bean, entry.getId(), value, false);
			} else if (name.equals("use")) {
				updateCMDBValue(bean, entry.getId(), value, false);
			} else if (name.equals("name")) {
				updateCMDBValue(bean, entry.getId(), value, false);
			} else {
				// Weekdays or exceptions.
				if (weekDayMap.contains(name)) {
					updateCMDBValue(bean, "weekday", name + " " + value, false);
				} else {
					updateCMDBValue(bean, "exception", name + " " + value, false);
				}
			}
		}
	}

	private String getReferenceType(NagiosConfig cfg, NagiosConfigEntry entry) {
		String type = referenceMap .get(cfg.getType() + "/" + entry.getId());
		return(type);
	}
	

	private String getElementValue(Element sel,
			String elementName, boolean requiered) {
		
		Element el = sel.element(elementName);
		if (el == null) {
			if (requiered) {
				throw new IllegalArgumentException("Element <" + elementName + "> is missing in <" + 
						sel.getName() + "> [" + sel.getPath() + "]");
			}
			return(null);
		}
		
		String text = el.getTextTrim();
		
		return(text);
	}


	public String getInput() {
		return input;
	}


	public void setInput(String input) {
		this.input = input;
	}


	public String getOutput() {
		return output;
	}


	public void setOutput(String output) {
		this.output = output;
	}

	class ServiceRelation {
		CiBean bean;
		NagiosConfigEntry id;
		String value;
		String type;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public CiBean getBean() {
			return bean;
		}
		public void setBean(CiBean bean) {
			this.bean = bean;
		}
		public NagiosConfigEntry getId() {
			return id;
		}
		public void setId(NagiosConfigEntry id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		
	}
	
	
}
