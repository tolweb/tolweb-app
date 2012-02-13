package org.tolweb.hivemind;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.components.TableColumnModelSource;
import org.apache.tapestry.contrib.table.model.IAdvancedTableColumnSource;
import org.apache.tapestry.contrib.table.model.ITableColumnModel;
import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.web.WebRequest;
import org.tolweb.tapestry.AlphabeticalObjectArrayTableModel;
import org.tolweb.tapestry.AlphabeticalTableModel;

public class TapestryHelperImpl implements TapestryHelper {
	private IEngineService externalService;
	private IEngineService pageService;
	private IEngineService restartService;
	private TableColumnModelSource tableColumnModelSource;
	private IAdvancedTableColumnSource tableColumnSource;
	private WebRequest request;
	private IRequestCycle requestCycle;
	
	public String getExternalServiceUrl(String pageName, Object[] parameters) {
		return getExternalServiceLink(pageName, parameters).getURL();
	}
	public ILink getExternalServiceLink(String pageName, Object[] parameters) {
		ExternalServiceParameter externalParameters = new ExternalServiceParameter(pageName, parameters);
		return getExternalService().getLink(false, externalParameters); 
	}
	public String getPageServiceUrl(String pageName) {
		return getPageServiceLink(pageName).getURL();
	}
	public ILink getPageServiceLink(String pageName) {
		return getPageService().getLink(false, pageName);		
	}
	public IEngineService getExternalService() {
		return externalService;
	}
	public void setExternalService(IEngineService externalService) {
		this.externalService = externalService;
	}
	public IEngineService getPageService() {
		return pageService;
	}
	public void setPageService(IEngineService pageService) {
		this.pageService = pageService;
	}
	public TableColumnModelSource getTableColumnModelSource() {
		return tableColumnModelSource;
	}
	public void setTableColumnModelSource(
			TableColumnModelSource tableColumnModelSource) {
		this.tableColumnModelSource = tableColumnModelSource;
	}
	public IAdvancedTableColumnSource getTableColumnSource() {
		return tableColumnSource;
	}
	public void setTableColumnSource(IAdvancedTableColumnSource tableColumnSource) {
		this.tableColumnSource = tableColumnSource;
	}
	public WebRequest getRequest() {
		return request;
	}
	public void setRequest(WebRequest request) {
		this.request = request;
	}	
	private ITableColumnModel getColumnModel(String columns, IComponent page) {
		return getTableColumnModelSource().generateTableColumnModel(getTableColumnSource(), columns, null, page);
	}
	public AlphabeticalObjectArrayTableModel getAlphabeticalObjectArrayTableModel(String columns, IComponent page, Object[] data) {
		ITableColumnModel columnModel = getColumnModel(columns, page);
        AlphabeticalObjectArrayTableModel tableModel = new AlphabeticalObjectArrayTableModel(data, columnModel);
		return tableModel;
	}
	public AlphabeticalTableModel getAlphabeticalTableModel(String columns, IComponent page, Object[] data) {
		ITableColumnModel columnModel = getColumnModel(columns, page);
        AlphabeticalTableModel tableModel = new AlphabeticalTableModel(data, columnModel);
		return tableModel;
	}
	public String getStringFromUploadFile(IUploadFile file) {
		if (file == null) {
			return null;
		} else {
			InputStream stream = file.getStream();
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(stream, "ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ioe) { }
				}
				return null;
			}
			StringBuffer contents = new StringBuffer();
			int nextChar = 0;
			try {
				while ((nextChar = reader.read()) != -1) {
					contents.append((char) nextChar);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return contents.toString();
		}
	}
	public IEngineService getRestartService() {
		return restartService;
	}
	public void setRestartService(IEngineService restartService) {
		this.restartService = restartService;
	}
	public String getRestartServiceUrl() {
		return getRestartService().getLink(false, null).getURL();
	}
	public boolean getIsSafari() {
		String userAgent = getRequest().getHeader("USER-AGENT");
		boolean isSafari = userAgent.contains("Safari");
		return isSafari;		
	}
	public String getDomainName() {
		return getRequest().getHeader("X-Forwarded-Host");
	}
	public IRequestCycle getRequestCycle() {
		return requestCycle;
	}
	public void setRequestCycle(IRequestCycle requestCycle) {
		this.requestCycle = requestCycle;
	}
}
