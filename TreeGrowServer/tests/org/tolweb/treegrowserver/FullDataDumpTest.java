/*
 * Created on Nov 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.io.FileOutputStream;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.XMLWriter;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FullDataDumpTest extends ApplicationContextTestAbstract {
	private DownloadBuilder downloadBuilder;
	
	public FullDataDumpTest(String name) {
		super(name);
		downloadBuilder = (DownloadBuilder) context.getBean("publicDownloadBuilder");
	}
	
	public void testFullDataDump() {
		//Document doc = downloadBuilder.buildFullDataDumpDocument();
		//writeOutDocument(doc);
	}
	
	private void writeOutDocument(Document doc) {
		try {
			FileOutputStream out = new FileOutputStream("tolskeletaldump.xml");
	        XMLOutputter serializer = XMLWriter.getXMLOutputter();
	        try {
	        	serializer.output(doc, out);
	        	out.flush();
	        	out.close();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
