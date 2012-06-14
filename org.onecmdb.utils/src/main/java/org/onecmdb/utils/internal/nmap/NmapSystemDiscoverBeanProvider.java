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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.job.workflow.WorkflowParameter;
import org.onecmdb.core.internal.job.workflow.WorkflowProcess;
import org.onecmdb.core.internal.job.workflow.sample.CommitRfcProcess;
import org.onecmdb.core.internal.job.workflow.sample.ProcessBeanProvider;
import org.onecmdb.core.internal.job.workflow.sample.RunToolProcess;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.BeanScope;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;

public class NmapSystemDiscoverBeanProvider extends WorkflowProcess implements IBeanProvider {

	// Settable variables.
	private String hostnameTemplate;
	private String ipTemplate;
	private String nicTemplate;
	private String dnsEntryTemplate;
	private String netIfTemplate;

	
	private ICi hostnameContainer;
	private ICi ipContainer;
	private ICi nicContainer;
	private ICi netIfContainer;
	private ICi dnsEntryContainer;
	
	
	private String target;
	/*
	private String subnetMask;
	private String ip;
	*/
	
	private String nmapExecutable;

	Log log = LogFactory.getLog(this.getClass());
	
	RunToolProcess nmap;

	HashMap<String, CiBean> beanMap = new HashMap<String, CiBean>();
	List<CiBean> beans =  new ArrayList<CiBean>();
	private volatile boolean terminated = false;
	private volatile boolean nmapIsRunning = false;

	
	public List<CiBean> getBeans() {
		return(beans);
	}

	public CiBean getBean(String alias) {
		CiBean bean = beanMap.get(alias);
		return(bean);
	}

	
	public void run() throws Throwable {
		ISession session = (ISession)data.get("session");
		if (session == null) {
			throw new IllegalStateException("No 'session' found!");
		}
	
		IModelService modelService = (IModelService) session
		.getService(IModelService.class);

		// Validate input.
		validateTemplateExists(modelService , "hostnameTemplate", this.hostnameTemplate);
		validateTemplateExists(modelService , "ipTemplate", this.ipTemplate);
		validateTemplateExists(modelService , "dnsEntryTemplate", this.dnsEntryTemplate);
		validateTemplateExists(modelService , "netIfTemplate", this.netIfTemplate);
		validateTemplateExists(modelService , "nicTemplate", this.nicTemplate);
		
		// Run nmap.
		updateProgress("Running nmap");
		nmap = new RunToolProcess();
		nmap.setProgramPath(this.nmapExecutable);
		
		File output = File.createTempFile("NmapDiscover", ".xml");
		List<CiBean> discoverBeans = null;
		// Validate against current onecmdb.
		OneCmdbBeanProvider remoteBeanProvider = new OneCmdbBeanProvider();
		remoteBeanProvider.setModelService(modelService);
		
		try {
			String outputPath = output.getCanonicalPath();
			updateProgressPercentage(5);
			List<String> args = new ArrayList<String>();
			args.add("-sP");
			args.add(getTarget());
			args.add("-oX");
			args.add(outputPath);
			
			
			nmap.setArguments(args);
			
			nmapIsRunning = true;
			
			nmap.run();
			
			nmapIsRunning  = false;
			
			updateProgress("nmap ended");
			
			updateProgressPercentage(40);
			
			if (nmap.getOutParameter().get("ok").equals("false")) {
				String cause = (String) nmap.getOutParameter().get("cause");
				throw new IllegalStateException("Problem running nmap : " + cause);	
			}
			
			String inputFile = outputPath;

	
			TransformNmap transformNmap = new TransformNmap();
			transformNmap.setNicTemplate(nicTemplate);
			transformNmap.setIpTemplate(ipTemplate);
			transformNmap.setHostnameTemplate(hostnameTemplate);
			transformNmap.setNetIfTemplate(netIfTemplate);
			transformNmap.setDnsEntryTemplate(dnsEntryTemplate);
			
			transformNmap.setBeanProvider(remoteBeanProvider);
			
			transformNmap.setInput(inputFile);
			discoverBeans = transformNmap.transform();
			
			updateProgressPercentage(50);
			
		} finally {
			// Cleanup temp file.
			if (output != null) {
				boolean deleted = output.delete();
				if (!deleted) {
					log.warn("Temporary nmap output file '" + output.getAbsolutePath() +"' can not be deleted");
				}
			}
		}
		
		
	
		CiBean ipContainerBean = null;
		if (this.ipContainer != null) {
			ipContainerBean = new CiBean();
			ipContainerBean.setAlias(this.ipContainer.getAlias());
			ipContainerBean.setDerivedFrom(this.ipContainer.getDerivedFrom().getAlias());
			ipContainerBean.setTemplate(false);
			this.beanMap.put(ipContainerBean.getAlias(), ipContainerBean);
			this.beans.add(ipContainerBean);
		}
		
		
		CiBean nicContainerBean = null;
		if (this.nicContainer != null) {
			nicContainerBean = new CiBean();
			nicContainerBean.setAlias(this.nicContainer.getAlias());
			nicContainerBean.setDerivedFrom(this.nicContainer.getDerivedFrom().getAlias());
			nicContainerBean.setTemplate(false);
			this.beanMap.put(nicContainerBean.getAlias(), nicContainerBean);
			this.beans.add(nicContainerBean);
		}
		CiBean hostnameContainerBean = null;
		if (hostnameContainer != null) {
			hostnameContainerBean = new CiBean();
			hostnameContainerBean.setAlias(this.hostnameContainer.getAlias());
			hostnameContainerBean.setDerivedFrom(this.hostnameContainer.getDerivedFrom().getAlias());
			hostnameContainerBean.setTemplate(false);
			this.beanMap.put(hostnameContainerBean.getAlias(), hostnameContainerBean);
			this.beans.add(hostnameContainerBean);
		}
		
		
		CiBean dnsEntryContainerBean = null;
	
		if (this.dnsEntryContainer != null) {
			dnsEntryContainerBean = new CiBean();
			dnsEntryContainerBean.setAlias(this.dnsEntryContainer.getAlias());
			dnsEntryContainerBean.setDerivedFrom(this.dnsEntryContainer.getDerivedFrom().getAlias());
			dnsEntryContainerBean.setTemplate(false);
			this.beanMap.put(dnsEntryContainerBean.getAlias(), dnsEntryContainerBean);
			this.beans.add(dnsEntryContainerBean);
		}
		
		CiBean netIfContainerBean = null;
		if (netIfContainer != null) {
			netIfContainerBean = new CiBean();
			netIfContainerBean.setAlias(this.netIfContainer.getAlias());
			netIfContainerBean.setDerivedFrom(this.netIfContainer.getDerivedFrom().getAlias());
			netIfContainerBean.setTemplate(false);
			this.beanMap.put(netIfContainerBean.getAlias(), netIfContainerBean);
			this.beans.add(netIfContainerBean);
		}
			
			
		updateProgressPercentage(60);
		
		for (CiBean bean : discoverBeans) {
			if (terminated ) {
				throw new IllegalStateException("Discover system was stopped");
			}
			
			this.beanMap.put(bean.getAlias(), bean);
			this.beans.add(bean);
			
			// add it to the correct foled also.
			if (bean.getDerivedFrom().equals(this.hostnameTemplate)) {
				if (hostnameContainerBean != null) {
					hostnameContainerBean.addAttributeValue(new ValueBean("hostnames", bean.getAlias(), true));
				}
			} else if (bean.getDerivedFrom().equals(this.ipTemplate)) {
				if (ipContainerBean != null) {
					ipContainerBean.addAttributeValue(new ValueBean("ips", bean.getAlias(), true));
				}
			} else if (bean.getDerivedFrom().equals(this.nicTemplate)) {
				if (nicContainerBean != null) { 
					nicContainerBean.addAttributeValue(new ValueBean("nics", bean.getAlias(), true));
				}
			} else if (bean.getDerivedFrom().equals(this.netIfTemplate)) {
				if (netIfContainerBean != null) {
					netIfContainerBean.addAttributeValue(new ValueBean("networkInterfaces", bean.getAlias(), true));
				}
			} else if (bean.getDerivedFrom().equals(this.dnsEntryTemplate)) {
				if (dnsEntryContainerBean != null) {
					dnsEntryContainerBean.addAttributeValue(new ValueBean("dnsEntries", bean.getAlias(), true));
				}
			}
		}
		
		updateProgressPercentage(65);
		// Process beans
		ProcessBeanProvider importBeans = new ProcessBeanProvider();
		WorkflowParameter par = new WorkflowParameter();
		par.put("provider", this);
		par.put("validation", "false");
		importBeans.setInParameter(par);
		importBeans.setRelevantData(data);
		importBeans.run();
		
		updateProgressPercentage(80);
		
		// Commit
		BeanScope scope = (BeanScope) importBeans.getOutParameter().get("scope");
		List<IRFC> rfcs = scope.getRFCs();
		
		
		CommitRfcProcess commit = new CommitRfcProcess();
		WorkflowParameter par1 = new WorkflowParameter();
		System.out.println(rfcs.size() + " Rfc's generated ");
		par1.put("rfcs", rfcs);
		commit.setInParameter(par1);
		commit.setRelevantData(data);
		commit.run();
		
		updateProgressPercentage(100);
		
		String ok = (String)commit.getOutParameter().get("ok");
		String cause = (String)commit.getOutParameter().get("cause");
		
		
		if (!ok.equals("true")) {
			throw new IllegalAccessError("Can't commit changes:" + cause);
		}
		return;
	}
	
	
	
	private void validateTemplateExists(IModelService modelService, String message, String template) {
		ICi t = modelService.findCi(new Path(template));
		
		if (t == null) {
			throw new IllegalStateException(message + " no template '" + template + "' found");
		}
		if (!t.isBlueprint()) {
			throw new IllegalStateException(template + " is an instance!");
		}
	}

	private String getValue(CiBean bean, String alias) {
		List<ValueBean> values = bean.fetchAttributeValueBeans(alias);
		if (values.size() == 0) {
			return(null);
		}
		if (values.size() != 1) {
			return(null);
		}
		ValueBean valueBean = values.get(0);
		if (valueBean.isComplexValue()) {
			return(valueBean.getValue());
		}
		return(valueBean.getValue());
	}

	@Override
	public void interrupt() {
		this.terminated = true;
		// teminate nMap if running.
		if (nmap != null) {
			if (nmapIsRunning) {
				nmap.interrupt();
			}
		}
	}

	public void updateProgress(String progress) {
		updateAttribute("progress", progress);
	}
	
	/**
	 * Generate input paremeter to nmap.
	 * ip/network
	 * @return
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getTarget() throws UnknownHostException {
		if (this.target == null) {
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			this.target = ipAddress +"/24";
			updateAttribute("target", this.target);
		}
		return(this.target);
	}
	
	// Setter/ Getters.
	
	
	
	public String getNmapExecutable() {
		return nmapExecutable;
	}

	public void setNmapExecutable(String nmapExecutable) {
		this.nmapExecutable = nmapExecutable;
	}
	
	/*
	public void setSubnetMask(String subnet) {
		this.subnetMask = subnet;
	}
	
	public String getSubnetMask() {
		return(this.subnetMask);
	}
	
	public void setIpAddress(String ipAddress) {
		this.ip = ipAddress;
	}
	public String getIpAddress() {
		return(this.ip);
	}
	*/
	
	public ICi getDnsEntryContainer() {
		return dnsEntryContainer;
	}

	public void setDnsEntryContainer(ICi dnsEntryContainer) {
		this.dnsEntryContainer = dnsEntryContainer;
	}

	public String getDnsEntryTemplate() {
		return dnsEntryTemplate;
	}

	public void setDnsEntryTemplate(String dnsEntryTemplate) {
		this.dnsEntryTemplate = dnsEntryTemplate;
	}

	public ICi getHostnameContainer() {
		return hostnameContainer;
	}

	public void setHostnameContainer(ICi hostnameContainer) {
		this.hostnameContainer = hostnameContainer;
	}

	public String getHostnameTemplate() {
		return hostnameTemplate;
	}

	public void setHostnameTemplate(String hostnameTemplate) {
		this.hostnameTemplate = hostnameTemplate;
	}

	public ICi getIpContainer() {
		return ipContainer;
	}

	public void setIpContainer(ICi ipContainer) {
		this.ipContainer = ipContainer;
	}

	public String getIpTemplate() {
		return ipTemplate;
	}

	public void setIpTemplate(String ipTemplate) {
		this.ipTemplate = ipTemplate;
	}

	public ICi getNetIfContainer() {
		return netIfContainer;
	}

	public void setNetIfContainer(ICi netIfContainer) {
		this.netIfContainer = netIfContainer;
	}

	public String getNetIfTemplate() {
		return netIfTemplate;
	}

	public void setNetIfTemplate(String netIfTemplate) {
		this.netIfTemplate = netIfTemplate;
	}

	public ICi getNicContainer() {
		return nicContainer;
	}

	public void setNicContainer(ICi nicContainer) {
		this.nicContainer = nicContainer;
	}

	public String getNicTemplate() {
		return nicTemplate;
	}

	public void setNicTemplate(String nicTemplate) {
		this.nicTemplate = nicTemplate;
	}
	
}
