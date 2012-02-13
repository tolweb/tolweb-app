package org.tolweb.content.services;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.tolweb.dao.ApplicationContextTestAbstract;

public class PageContentServiceTest extends ApplicationContextTestAbstract {
	private Document serviceOutput; 
	
	private static final String SERVICE_URL = "http://dev.tolweb.org/onlinecontributors/app?page=content/ToLPageContentService&service=external&nodeId=";
	
	public PageContentServiceTest(String name) {
		super(name);
		SAXBuilder saxb = new SAXBuilder();
		serviceOutput = null;
		try { 
			serviceOutput = saxb.build(SERVICE_URL + 15093);
		} catch (Exception jdome) {
			System.out.println("something really bad happened - maybe you've got bad karma");
		}		
	}
	
	public void testServiceOutput() {

		
		assertNotNull(serviceOutput);
		System.out.println("serviceOutput is not null? " + (serviceOutput != null));
	}
	
	public void testDocumentStructure() {
/*
org.jdom.Element titleNode = 
    (org.jdom.Element) XPath.selectSingleNode(jdomDocument, "/catalog//journal//article[@date='January-2004']/title");
 */	
		Element rootNode = null;
		Element pageNode = null;
		Attribute pageIdAttr = null;
		Element nameText = null;
		System.out.println("serviceOutput is not null? " + (serviceOutput != null));
		try {
			rootNode = (Element)XPath.selectSingleNode(serviceOutput, "/success");
			pageNode = (Element)XPath.selectSingleNode(serviceOutput, "/success/pages/page");
			pageIdAttr = (Attribute)(XPath.selectSingleNode(serviceOutput, "/success/pages/page/@id"));
			nameText = (Element)XPath.selectSingleNode(serviceOutput, "//name");
			
		} catch (Exception bad) {
			System.out.println("something really bad happened - maybe you've got bad karma");
			
			System.out.println(bad.toString());
		}
		
		System.out.println(rootNode != null ? rootNode.toString() : "(null)");
		System.out.println(pageNode != null ? outputElement(pageNode) : "(null)");
		System.out.println(pageIdAttr != null ? pageIdAttr.toString() : "(null)");
		System.out.println(nameText != null ? nameText.toString() + " - " + nameText.getText() : "(null)");
		System.out.println(serviceOutput != null ? serviceOutput.toString() : "service null - wth?");
		
		
	}

	private String outputElement(Element el) {
		XMLOutputter xout = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
		return (xout.outputString(el));		
	}
}
