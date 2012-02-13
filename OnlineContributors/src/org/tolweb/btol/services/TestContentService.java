package org.tolweb.btol.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.services.LinkFactory;
import org.tolweb.dao.NodeDAO;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.ServerXMLWriter;

/**
 * 
 * @author dmandel
 * usage example: onlinecontributors/app?includeTree=1&rootNodeId=1&service=testcontent
 */
public class TestContentService implements IEngineService {
	private static final String _1 = "1";
	private static final String ID_PARAM = "rootNodeId";
	private static final String INCLUDE_TREE_STRUCTURE = "includeTree";
	private LinkFactory linkFactory;
	private NodeDAO workingNodeDAO;
	private HttpServletResponse response;
	private ServerXMLWriter serverXMLWriter;

	public void service(IRequestCycle cycle) throws IOException {
		Long rootNodeId = Long.parseLong(cycle.getParameter(ID_PARAM));
		boolean includeTreeStructureBool = false;
		String includeTreeStructure = cycle.getParameter(INCLUDE_TREE_STRUCTURE);
		if (StringUtils.notEmpty(includeTreeStructure)) {
			includeTreeStructureBool = includeTreeStructure.equals(_1);
		}
	    response.setContentType("text/xml");
	    serverXMLWriter.writeXMLToServletResponse(getResponse(), rootNodeId, includeTreeStructureBool, true);
	}

	@SuppressWarnings("unchecked")
	public ILink getLink(boolean post, Object parameter) {
		String rootNodeIdString = (String)((Object[])parameter)[0];
		Map parameters = new HashMap();
		parameters.put(ID_PARAM, rootNodeIdString);
		if (((Object[]) parameter).length > 1) {
			parameters.put(INCLUDE_TREE_STRUCTURE, _1);
		}
		return linkFactory.constructLink(this, post, parameters, false);
	}

	public LinkFactory getLinkFactory() {
		return linkFactory;
	}
	public void setLinkFactory(LinkFactory linkFactory) {
		this.linkFactory = linkFactory;
	}
	public NodeDAO getWorkingNodeDAO() {
		return workingNodeDAO;
	}
	public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
		this.workingNodeDAO = workingNodeDAO;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public ServerXMLWriter getServerXMLWriter() {
		return serverXMLWriter;
	}
	public void setServerXMLWriter(ServerXMLWriter xmlWriter) {
		this.serverXMLWriter = xmlWriter;
	}
	public String getName() {
		// TODO Auto-generated method stub
		return "testcontent";
	}
}
