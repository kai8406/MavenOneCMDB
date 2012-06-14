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
package org.onecmdb.ui.gwt.desktop.server.service.change;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.xml.XmlParser;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeFilter;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeItem;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeRecord;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeResult;
import org.onecmdb.ui.gwt.desktop.client.service.change.IChangeService;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.server.service.CMDBRPCHandler;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.CMDBWebServiceFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ChangeServiceImpl extends RemoteServiceServlet implements IChangeService {
	private static final boolean CiBean = false;
	private IChangeStore changeStore;
	
	
	public IChangeStore getChangeStore() {
		if (changeStore == null) {
			changeStore = new ChangeStoreImpl();
		}
		return changeStore;
	}

	public void setChangeStore(IChangeStore changeStore) {
		this.changeStore = changeStore;
	}
	
	public ChangeServiceImpl() {
	}
	
	public StoreResult commit(String token, CIModel mdr, CIModel config, CIModel mdrHistory, List<ChangeItem> items) throws Exception {
		System.out.println("Commit Changes<>");
		
		CIMDRCollection localMDR = new CIMDRCollection(getMDRId(mdr, config));
		for (ContentData file : getContentFiles(mdrHistory)) {
			if (!(file instanceof ContentFile)) {
				continue;
			}
			ICIMDR local = null;
			local = (ICIMDR) ContentParserFactory.get().getAdaptor(file, ICIMDR.class);
			if (local == null) {
				throw new IllegalArgumentException("Can't adapt content " + file.getName() + " to " + ICIMDR.class.getName());
			}
			localMDR.add(local);
		}
		
		IChangeStore store = getChangeStore();
		ICIMDR baseMDR = store.getBase(localMDR);
		ICIMDR remoteMDR = store.getRemote();
		
		ReconciliationEngine engine = new ReconciliationEngine(token, localMDR, baseMDR, remoteMDR);
		
		IRfcResult result = engine.commit(items);
		System.out.println("Commit: " + result.isRejected() + " : " + result.getRejectCause());
		if (!result.isRejected()) {
			store.commit(token, localMDR, items);
		}
		CIModel mdrHistoryCopy = null;
		if (mdrHistory.getIdAsString() != null) {
			mdrHistoryCopy = mdrHistory.copy();
		}
		Transform.updateModel(mdrHistory, "txid", result.getTxId());
		Transform.updateModel(mdrHistory, "added", result.getCiAdded());
		Transform.updateModel(mdrHistory, "deleted", result.getCiDeleted());
		Transform.updateModel(mdrHistory, "modified", result.getCiModified());
		Transform.updateModel(mdrHistory, "rejected", result.isRejected());
		Transform.updateModel(mdrHistory, "rejectCause", result.getRejectCause());
		if (result.isRejected()) {
			Transform.updateModel(mdrHistory, "status", MDRHistoryState.REJECTED);			
		} else {
			// Update the last COMMITTED to OUT_OF_DATE...
			Transform.updateModel(mdrHistory, "status", MDRHistoryState.COMMITTED);			
			updateOutOfDateHistory(token, mdr, config, mdrHistory);		
		}
		
		
		CiBean localHistBean = new Transform().convert(remoteMDR, token, mdrHistory);
		CiBean localHistBeanCopy = null;
		if (mdrHistoryCopy != null) {
			localHistBeanCopy = new Transform().convert(remoteMDR, token, mdrHistoryCopy);
		}
			
		// Store history...
		IOneCMDBWebService service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		service.update(token, new CiBean[] {localHistBean}, (localHistBeanCopy == null ? null : new CiBean[] {localHistBeanCopy}));
		
		//updateBean(this.mdrHistory, "commitStart", result.getStart());
		//updateBean(this.mdrHistory, "commitStop", result.getStop());
		
		// Store history....
		
		StoreResult cRes = new StoreResult();
		cRes.setRejected(result.isRejected());
		cRes.setRejectCause(result.getRejectCause());
		cRes.setTxId(result.getTxId());
		cRes.setAdded(result.getCiAdded());
		cRes.setModfied(result.getCiModified());
		cRes.setDelted(result.getCiDeleted());
		cRes.setStart(result.getStart());
		cRes.setStop(result.getStop());
		return(cRes);
	}

	public StoreResult delete(String token, CIModel mdr, CIModel config, CIModel mdrHistory, List<ChangeItem> items) throws Exception {
		System.out.println("Delete Items");
		
		CIMDRCollection localMDR = new CIMDRCollection(getMDRId(mdr, config));
		for (ContentData file : getContentFiles(mdrHistory)) {
			if (!(file instanceof ContentFile)) {
				continue;
			}
			ICIMDR local = null;
			local = (ICIMDR) ContentParserFactory.get().getAdaptor(file, ICIMDR.class);
			if (local == null) {
				throw new IllegalArgumentException("Can't adapt content " + file.getName() + " to " + ICIMDR.class.getName());
			}
			localMDR.add(local);
		}
		
		IChangeStore store = getChangeStore();
		ICIMDR baseMDR = store.getBase(localMDR);
		ICIMDR remoteMDR = store.getRemote();
		
		ReconciliationEngine engine = new ReconciliationEngine(token, localMDR, baseMDR, remoteMDR);
		
		IRfcResult result = engine.delete(items);
		System.out.println("Delete: " + result.isRejected() + " : " + result.getRejectCause());
		if (!result.isRejected()) {
			store.reset(token, localMDR, items);
		}
		CIModel mdrHistoryCopy = null;
		if (mdrHistory.getIdAsString() != null) {
			mdrHistoryCopy = mdrHistory.copy();
		}
		Transform.updateModel(mdrHistory, "txid", result.getTxId());
		Transform.updateModel(mdrHistory, "added", result.getCiAdded());
		Transform.updateModel(mdrHistory, "deleted", result.getCiDeleted());
		Transform.updateModel(mdrHistory, "modified", result.getCiModified());
		Transform.updateModel(mdrHistory, "rejected", result.isRejected());
		Transform.updateModel(mdrHistory, "rejectCause", result.getRejectCause());
		if (result.isRejected()) {
			Transform.updateModel(mdrHistory, "status", MDRHistoryState.REJECTED);			
		} else {
			// Update the last COMMITTED to OUT_OF_DATE...
			Transform.updateModel(mdrHistory, "status", MDRHistoryState.DELETED);			
			updateOutOfDateHistory(token, mdr, config, mdrHistory);		
		}
		
		
		CiBean localHistBean = new Transform().convert(remoteMDR, token, mdrHistory);
		CiBean localHistBeanCopy = null;
		if (mdrHistoryCopy != null) {
			localHistBeanCopy = new Transform().convert(remoteMDR, token, mdrHistoryCopy);
		}
			
		// Store history...
		IOneCMDBWebService service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		service.update(token, new CiBean[] {localHistBean}, (localHistBeanCopy == null ? null : new CiBean[] {localHistBeanCopy}));
		
		//updateBean(this.mdrHistory, "commitStart", result.getStart());
		//updateBean(this.mdrHistory, "commitStop", result.getStop());
		
		// Store history....
		
		StoreResult cRes = new StoreResult();
		cRes.setRejected(result.isRejected());
		cRes.setRejectCause(result.getRejectCause());
		cRes.setTxId(result.getTxId());
		cRes.setAdded(result.getCiAdded());
		cRes.setModfied(result.getCiModified());
		cRes.setDelted(result.getCiDeleted());
		cRes.setStart(result.getStart());
		cRes.setStop(result.getStop());
		return(cRes);
	}

	
	private void updateOutOfDateHistory(String token, CIModel mdr, CIModel config, CIModel currentHistory) {
		try {

			GraphQuery q = new GraphQuery();
			ItemOffspringSelector history = new ItemOffspringSelector("history", MDRHistoryState.getHistoryTemplate());
			AttributeValueConstraint aCon = new AttributeValueConstraint();
			aCon.setAlias("status");
			aCon.setValue(MDRHistoryState.COMMITTED);
			aCon.setOperation(AttributeValueConstraint.EQUALS);
			history.applyConstraint(aCon);

			ItemAliasSelector cfg = new ItemAliasSelector("cfg", config.getDerivedFrom());
			cfg.setAlias(config.getAlias());
			cfg.setPrimary(true);

			ItemRelationSelector history2cfg = new ItemRelationSelector("history2cfg", 
					"Reference", 
					cfg.getId(), 
					history.getId());

			q.addSelector(cfg);
			q.addSelector(history);
			q.addSelector(history2cfg);

			IOneCMDBWebService service;
			service = CMDBWebServiceFactory.get().getOneCMDBWebService();
			Graph result = service.queryGraph(token, q);
			Template n = result.fetchNode(history.getId());
			if (n == null) {
				return;
			}
			if (n.getOffsprings() == null || n.getOffsprings().size() == 0) {
				return;
			}

			List<CiBean> local = new ArrayList<CiBean>();
			List<CiBean> base = new ArrayList<CiBean>();
			for (CiBean hBean : n.getOffsprings()) {
				if (hBean.getAlias().equals(currentHistory.getAlias())) {
					continue;
				}
				base.add(hBean.copy());
				ValueBean vBean = hBean.fetchAttributeValueBean("status", 0);
				if (vBean != null) {
					vBean.setValue(MDRHistoryState.OUT_OF_DATE);
				}
				local.add(hBean);
			}
			service.update(token, local.toArray(new CiBean[0]), base.toArray(new CiBean[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getMDRId(CIModel mdr, CIModel config) {
		String id = mdr.getValueAsString("name") + "/" + config.getValueAsString("name").replace(" ", "_");
		return(id);
	}
	private List<ContentFile> getContentFiles(CIModel history) {
		List<ContentFile> files = new ArrayList<ContentFile>();
		ValueModel value = history.getValue("files");
		if (value == null) {
			return(files);
		}
		if (value instanceof ValueListModel) {
			for (ValueModel vModel : ((ValueListModel)value).getValues()) {
				if (vModel.getValue() != null) {
					ContentFile file = new ContentFile(vModel.getValue());
					files.add(file);
				}	
			}
		} else {
			
			if (value.getValue() != null) {
				ContentFile file = new ContentFile(value.getValue());
				files.add(file);
			}
		}
		return(files);
	}
	
	public ChangeRecord loadChanges(String token, CIModel mdr, CIModel config, CIModel mdrHistory, ChangeFilter filter) throws CMDBRPCException {
		try {
		String id = getMDRId(mdr, config);
		System.out.println("Load Changes for MDR<" + id +">");
		
		CIMDRCollection localMDR = new CIMDRCollection(id);
		for (ContentData content : getContentFiles(mdrHistory)) {
			if (!(content instanceof ContentFile)) {
				continue;
			}
			ICIMDR local = null;
			local = (ICIMDR) ContentParserFactory.get().getAdaptor(content, ICIMDR.class);
			if (local == null) {
				throw new IllegalArgumentException("Can't adapt content " + content.getName() + " to " + ICIMDR.class.getName());
			}
			localMDR.add(local);
		}
		
		IChangeStore store = getChangeStore();
		ICIMDR baseMDR = store.getBase(localMDR);
		ICIMDR remoteMDR = store.getRemote();
		
		ReconciliationEngine engine = new ReconciliationEngine(token, localMDR, baseMDR, remoteMDR);
		
		List<ChangeItem> result = engine.reconciliate();
		ChangeRecord record = new ChangeRecord();
		record.setChangeItems(result);
		
		List<CiBean> cis = localMDR.getCI(token);
		// Produce statistics...
		int templateCount = 0;
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (CiBean ci : cis) {
			if (ci.isTemplate()) {
				templateCount++;
			} else {
				Integer count = map.get(ci.getDerivedFrom());
				if (count == null) {
					count = new Integer(0);
				}
				map.put(ci.getDerivedFrom(), new Integer(count.intValue() + 1));
			}
		}
		
		// Produce Summary text.
		StringBuffer summary = new StringBuffer();
		summary.append("<p><b>Summary</b></br>");
		summary.append(templateCount + " templates </br>");
		for (String name : map.keySet()) {
			summary.append(map.get(name) + " " + name + " instances</br>");
		}
		summary.append("</p>");
		record.setSummary(summary.toString());
		return(record);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CMDBRPCException("Load Chnages", t.getMessage(), CMDBRPCHandler.getStackTrace(t));
		}
	}
}
