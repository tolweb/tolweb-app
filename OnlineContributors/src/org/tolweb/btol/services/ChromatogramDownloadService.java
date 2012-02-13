package org.tolweb.btol.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.services.LinkFactory;
import org.apache.tapestry.web.WebRequest;
import org.tolweb.btol.util.ChromatogramSearcher;
import org.tolweb.btol.util.GelImageUtils;
import org.tolweb.hivemind.ProjectHelper;
import org.tolweb.treegrow.main.FileUtils;
import org.tolweb.treegrow.main.RequestParameters;

/**
 * Tapestry engine service that creates zip files of chromatograms
 * from the database
 */
public class ChromatogramDownloadService implements IEngineService {
	private HttpServletResponse response;
	private WebRequest request;
	private LinkFactory linkFactory;
	private GelImageUtils gelImageUtils;
	private ProjectHelper projectHelper;
	private ChromatogramSearcher chromatogramSearcher;

	public String getName() {
		return "chromatogramdownload";
	}

	public void setResponse(HttpServletResponse response) {
	    this.response = response;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setRequest(WebRequest request) {
		this.request = request;
	}
	public WebRequest getRequest() {
		return request;
	}
	@SuppressWarnings("unchecked")
	public void service(IRequestCycle cycle) throws IOException {
		String extractionName = getRequest().getParameterValue(RequestParameters.EXTRACTION);
		String geneName = getRequest().getParameterValue(RequestParameters.GENE);	
		String taxon = getRequest().getParameterValue(RequestParameters.TAXON);
		String batchName = getRequest().getParameterValue(RequestParameters.NAME);
		
		Long projectId = getProjectHelper().getProjectIdOrDefault();		


		Collection<String> chromatFilenames = getChromatogramSearcher().getChromatogramFilenames(extractionName, geneName, 
				batchName, taxon, projectId);
		// here we have a list of filenames -- get the path to the real physical directory
		// of the chromatograms to create file objects
		List files = getGelImageUtils().getChromatogramFileListFromFilenames(chromatFilenames);
		
	    response.setHeader("Content-disposition", "attachment; filename=foo.zip");
	    response.setContentType("application/zip");
	    
		OutputStream responseStream = response.getOutputStream();
		FileUtils.createZipFile(files, responseStream);
	}
	public ILink getLink(boolean post, Object parameter) {
		return null;
	}

	public LinkFactory getLinkFactory() {
		return linkFactory;
	}

	public void setLinkFactory(LinkFactory linkFactory) {
		this.linkFactory = linkFactory;
	}

	public GelImageUtils getGelImageUtils() {
		return gelImageUtils;
	}

	public void setGelImageUtils(GelImageUtils gelImageUtils) {
		this.gelImageUtils = gelImageUtils;
	}

	public ProjectHelper getProjectHelper() {
		return projectHelper;
	}

	public void setProjectHelper(ProjectHelper projectHelper) {
		this.projectHelper = projectHelper;
	}

	public ChromatogramSearcher getChromatogramSearcher() {
		return chromatogramSearcher;
	}

	public void setChromatogramSearcher(ChromatogramSearcher chromatogramSearcher) {
		this.chromatogramSearcher = chromatogramSearcher;
	}

}
