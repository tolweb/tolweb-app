package org.tolweb.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

/**
 * Utility class for using the XOM framework to pretty printer strings. 
 * 
 * @author lenards
 *
 */
public class XmlPrettyPrinter {
	/**
	 * Using pretty printing to output the given string input. 
	 * 
	 * This method uses three types defined in the XOM framework.
	 * 
	 * @param input the string containing the XML to pretty print
	 * @return a string with "pretty" printing indentation and 
	 * maximum length lines to be easily read by humans
	 *  
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws IOException
	 */
	public static String prettyPrint(String input) throws SAXException, ValidityException, ParsingException, IOException {
/*
			xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			Builder parser = new Builder(xerces);
			Document doc = parser.build(new StringReader(input));

 */		
		XMLReader xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
    	Builder parser = new Builder(xerces);
        Document doc = parser.build(new StringReader(input));
        Serializer serializer = new Serializer(fileOut, "ISO-8859-1");
        serializer.setIndent(4);
        serializer.setMaxLength(80);
        serializer.setPreserveBaseURI(false);
        serializer.write(doc);
        serializer.flush();
		return fileOut.toString();
	}
}
