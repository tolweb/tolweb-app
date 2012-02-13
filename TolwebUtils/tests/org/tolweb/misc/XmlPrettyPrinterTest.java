package org.tolweb.misc;

import java.io.IOException;

import org.xml.sax.SAXException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import junit.framework.TestCase;

public class XmlPrettyPrinterTest extends TestCase {
	
	public void test_simple_pretty_printer() {
		
		try {
			String prettyInPrint = XmlPrettyPrinter.prettyPrint("<test><foo>kkhjkjkj</foo><bar goo=\"hkjk\"/></test>");
			System.out.println(prettyInPrint);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidityException e) {
			System.out.println("WHOA! " + e.getClass().toString());
			e.printStackTrace();
		} catch (ParsingException e) {
			System.out.println("WHOA! " + e.getClass().toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("WHOA! " + e.getClass().toString());
			e.printStackTrace();
		}
	}
}
