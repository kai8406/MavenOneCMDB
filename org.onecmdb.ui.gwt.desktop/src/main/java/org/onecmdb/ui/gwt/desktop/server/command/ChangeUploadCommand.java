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
package org.onecmdb.ui.gwt.desktop.server.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.poi.util.IOUtils;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeItem;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeRecord;
import org.onecmdb.ui.gwt.desktop.client.service.change.IChangeService;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;
import org.onecmdb.ui.gwt.desktop.server.service.change.ChangeServiceImpl;
import org.onecmdb.ui.gwt.desktop.server.service.change.ChangeStoreImpl;
import org.onecmdb.ui.gwt.desktop.server.service.change.IChangeStore;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.Transform;

import com.sun.tools.xjc.ModelLoader;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class ChangeUploadCommand extends AbstractOneCMDBCommand {
	private String token;
	private String history;
	private String group;
	
	private CIModel mdrRepositoryModel;
	private CIModel mdrConfigModel;
	private CIModel mdrHistoryModel;
	private CIModel mdrHistoryModelCopy;
	private boolean historyStored;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	
	private void commitContent(List<String> mdrEntries) {
		IChangeService store = (IChangeService) ServiceLocator.getService(IChangeService.class);
		if (store == null) {
			store = new ChangeServiceImpl();
		}
	
		Transform.updateModel(this.mdrHistoryModel, "files", mdrEntries);
		
		
		try {
			if ("true".equals(this.mdrConfigModel.getValueAsString("autoCommit"))) {
				
				ChangeRecord record =  store.loadChanges(token, mdrRepositoryModel, mdrConfigModel, mdrHistoryModel, null);
				List<ChangeItem> changes = record.getChangeItems();
				
				storeHistory();
				loadMDRInfo(true);
				
				StoreResult result = store.commit(token, this.mdrRepositoryModel, this.mdrConfigModel, this.mdrHistoryModel, changes);
				historyStored = true;
				/*
				updateBean(this.mdrHistory, "txid", result.getTxId());
				updateBean(this.mdrHistory, "added", result.getAdded());
				updateBean(this.mdrHistory, "deleted", result.getDelted());
				updateBean(this.mdrHistory, "modified", result.getModfied());
				updateBean(this.mdrHistory, "rejected", result.isRejected());
				updateBean(this.mdrHistory, "rejectCause", result.getRejectCause());
				updateBean(this.mdrHistory, "status", result.isRejected() ? "REJECTED" : "COMITTED");
				//updateBean(this.mdrHistory, "commitStart", result.getStart());
				//updateBean(this.mdrHistory, "commitStop", result.getStop());
				*/	
			} else {
				Transform.updateModel(this.mdrHistoryModel, "status", MDRHistoryState.READY);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Transform.updateModel(this.mdrHistoryModel, "error", e.toString());
			Transform.updateModel(this.mdrHistoryModel, "status", MDRHistoryState.FAILED);
		}
	}

	
	private void loadMDRInfo(boolean force) throws Exception {
		// Query 
		if (!force) {
			if (this.mdrConfigModel != null && this.mdrRepositoryModel != null) {
				return;
			}
		}
		GraphQuery q = new GraphQuery();
		
		ItemAliasSelector hist = new ItemAliasSelector("history", MDRHistoryState.getHistoryTemplate());
		hist.setPrimary(true);
		hist.setAlias(history);
		ItemOffspringSelector config = new ItemOffspringSelector("config", "MDR_ConfigEntry");
		ItemOffspringSelector mdr = new ItemOffspringSelector("mdr", "MDR_Repository");
		
		ItemRelationSelector history2config = new ItemRelationSelector("h2c", "Reference", config.getId(), hist.getId());
		ItemRelationSelector config2mdr = new ItemRelationSelector("c2m", "Reference", mdr.getId(), config.getId());
		
		q.addSelector(hist);
		q.addSelector(config);
		q.addSelector(mdr);
		q.addSelector(history2config);
		q.addSelector(config2mdr);
		
		
		Graph result = getService().queryGraph(token, q);
		
		// Fetch config and mdr.
		Template configNode = result.fetchNode(config.getId());
		CiBean c = configNode.getOffsprings().get(0);
		this.mdrConfigModel = new Transform().convert(getCIMDR(), token, configNode.getTemplate(), c); 
		
		Template mdrNode = result.fetchNode(mdr.getId());
		CiBean m = mdrNode.getOffsprings().get(0);
		this.mdrRepositoryModel = new Transform().convert(getCIMDR(), token, mdrNode.getTemplate(), m);
		
		Template historyNode = result.fetchNode(hist.getId());
		CiBean v = historyNode.getOffsprings().get(0);
		
		this.mdrHistoryModel = new Transform().convert(getCIMDR(), token, historyNode.getTemplate(), v);
		this.mdrHistoryModelCopy = this.mdrHistoryModel.copy();
		historyStored = false;
	}
	
	
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
		throws Throwable {

		try {
			loadMDRInfo(false);

			resp.setContentType("text/html");

			getFileItem(req);

			resp.getWriter().write("OK token=" + token + ", history=" + history);
		} catch (Throwable t) {
			Transform.updateModel(this.mdrHistoryModel, "exception", t.toString());
			throw t;
		} finally {
		
			storeHistory();
		}
	}

	private void storeHistory() throws Exception {
		if (historyStored) {
			return;
		}
		CiBean history = new Transform().convert(getCIMDR(), token, mdrHistoryModel);
		CiBean copy = new Transform().convert(getCIMDR(), token, mdrHistoryModelCopy);
			
		IRfcResult result = getService().update(token, new CiBean[] {history}, new CiBean[] {copy});
		System.out.println("Store Entry History: " + (result.isRejected() ? "REJECTED" : "COMITTED") + ", cause:" + result.getRejectCause() + "");
		historyStored = true;
	}
	
	
	
	private void getFileItem(HttpServletRequest request) throws FileUploadException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if (!isMultipart) {
			throw new IllegalArgumentException("Not multipart...");
		}
		
		
			ServletFileUpload upload = new ServletFileUpload();
			
			List<String> mdrEntries = new ArrayList<String>();
			
			// Parse the request
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					System.out.println("Form field " + name + " with value "
							+ Streams.asString(stream) + " detected.");
				} else {
					System.out.println("File field " + name + " with file name "
							+ item.getName() + " detected.");
					// Process the input stream
				}
				String mdrEntry = handleInput(name, stream);
				mdrEntries.add(mdrEntry);
			}
			commitContent(mdrEntries);
	}
	
	private String handleInput(String name, InputStream stream) throws IOException {
		String mdrName = mdrRepositoryModel.getValueAsString("name");
		
		String mdrHome = ConfigurationFactory.get(Config.MDR_HOME);
		if (mdrHome == null) {
			mdrHome = "MDR";
		}
		SimpleDateFormat fmt1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat fmt2 = new SimpleDateFormat("HHmmss");
		
		String date = fmt1.format(new Date());
		String time = fmt2.format(new Date());
		
		File root = ContentParserFactory.get().getRootPath();
		String mdrPath = mdrHome + "/" + mdrName + "/history/" + date;
		String fileName = time + "_" + name;
		File path = new File(root, mdrPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File outputFile = new File(path, fileName);
		
		// Save file.
		IOUtil.copyCompletely(stream, new FileOutputStream(outputFile));
		
		return(mdrPath + "/" + fileName);
	}
	
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void transfer(OutputStream out) throws Throwable {
		// TODO Auto-generated method stub
		
	}
}
