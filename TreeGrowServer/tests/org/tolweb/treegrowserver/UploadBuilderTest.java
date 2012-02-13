/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import org.jdom.Document;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.dao.DownloadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadBuilderTest extends ApplicationContextTestAbstract {
	private UploadBuilder uploadBuilder;
	private DownloadBuilder downloadBuilder;
	private ServerXMLReader serverXMLReader;
	private DownloadDAO downloadDAO;
	
	public UploadBuilderTest(String name) {
		super(name);
		uploadBuilder = (UploadBuilder) context.getBean("uploadBuilder");
		downloadBuilder = (DownloadBuilder) context.getBean("downloadBuilder");
		serverXMLReader = (ServerXMLReader) context.getBean("serverXMLReader");
		downloadDAO = (DownloadDAO) context.getBean("downloadDAO");
	}
	
	public void testBuildUpload() throws Exception {
		Contributor danny = new Contributor();
		danny.setId(664);
		// Build a download -- get the document and serialize it to string
		// Then try and upload the same thing
		Document doc = downloadBuilder.buildDownload(new Long(1), 1, true, true, danny, null, false, false);
		String docString = printOutDocument(doc);
		Document uploadResult = uploadBuilder.buildUpload(docString);
		// Delete the download
		int downloadId = serverXMLReader.fetchDownloadId(doc.getRootElement());
		downloadDAO.deleteDownload(downloadDAO.getDownloadWithId(new Long(downloadId)));
		assertEquals(uploadResult.getRootElement().getName(), XMLConstants.UPLOADRESULTS);
	}
}
