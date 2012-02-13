package org.tolweb.treegrowserver;

import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

public class XMLDownloadUploadTest extends ApplicationContextTestAbstract {
	private ServerXMLWriter xmlWriter;
	private UploadBuilder uploadBuilder;
	private ContributorDAO contributorDAO;
	
	public XMLDownloadUploadTest(String name) {
		super(name);
		xmlWriter = (ServerXMLWriter) context.getBean("serverXMLWriter");
		contributorDAO = (ContributorDAO) context.getBean("contributorDAO"); 
		uploadBuilder = (UploadBuilder) context.getBean("uploadBuilder");
	}
	
	public void testXMLReadingWriting() {
		Long bembidionId = 194L;
		StringWriter stringWriter = new StringWriter();
		DataWriter writer = xmlWriter.getDataWriterFromWriter(stringWriter);
		Contributor danny = contributorDAO.getContributorWithId(664);
		try {
			xmlWriter.startDocument(writer);
			xmlWriter.startTree(writer);
			xmlWriter.addTreeStructureXMLForNode(bembidionId, false, writer, true, false);
			xmlWriter.endTree(writer);
			xmlWriter.endDocument(writer);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		String xmlString = stringWriter.getBuffer().toString();
		System.out.println("xml string is: \n" + xmlString);
		
		// now ingest it back in
		try {
			Document doc = uploadBuilder.buildUpload(xmlString, danny, false, true);
			if (doc == null) {
				System.out.println("doc is null");
			}
	    	Format format = Format.getPrettyFormat();
	    	format.setEncoding(XMLConstants.CHARSET_NAME);
            XMLOutputter serializer = new XMLOutputter(format);
            serializer.output(doc, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
