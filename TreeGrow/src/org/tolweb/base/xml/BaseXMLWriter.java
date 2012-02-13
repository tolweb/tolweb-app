package org.tolweb.base.xml;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.tolweb.treegrow.main.XMLConstants;

public class BaseXMLWriter {
    
    public static String getDocumentAsString(Document doc) {
    	XMLOutputter serializer = getXMLOutputter();
        String plainStr = serializer.outputString(doc);
        return plainStr;
    }
    public static String getBooleanString(boolean val) {
    	return val ? XMLConstants.ONE : XMLConstants.ZERO;
    }
    
    public static XMLOutputter getXMLOutputter() {
    	Format format = Format.getPrettyFormat();
    	format.setEncoding(XMLConstants.CHARSET_NAME);
    	return new XMLOutputter(format);    	
    }    
}
