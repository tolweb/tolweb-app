package org.tolweb.base.xml;

import java.io.ByteArrayInputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tolweb.treegrow.main.FileUtils;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;

public class BaseXMLReader {
	public static Document getDocumentFromFile(String filePath) {
		String fileContents = null;
		try {
			fileContents = FileUtils.getFileContents(filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getDocumentFromString(fileContents);
	}
    public static Document getDocumentFromString(String xmlString) {
        Document doc = null;
        try {
            // Make sure it's in the correct encoding
            ByteArrayInputStream stream = new ByteArrayInputStream(xmlString
                    .getBytes(XMLConstants.CHARSET_NAME));
            doc = new SAXBuilder().build(stream);
        } catch (Exception e) {
            e.printStackTrace();
			System.out.println("INFO: [BaseXMLReader::getDocumentFromString(String)] >> argument: " + xmlString);            
        }
        return doc;
    }
    public static boolean getBooleanValue(Element nodeElement, String attributeName) {
        boolean returnValue = false;
        String attrValue = nodeElement.getAttributeValue(attributeName);
        if (StringUtils.notEmpty(attrValue)) {
            if (attrValue.equalsIgnoreCase(XMLConstants.ONE) || attrValue.equalsIgnoreCase(XMLConstants.TRUE) || attrValue.equalsIgnoreCase(XMLConstants.y)) {
                returnValue = true;
            }
        }
        return returnValue;
    }    
}
