package org.tolweb.tapestry.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import nu.xom.Builder;

import org.apache.commons.betwixt.io.BeanReader;
import org.dom4j.io.SAXReader;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.tapestry.xml.taxaimport.beans.XTGeographicDistribution;
import org.tolweb.tapestry.xml.taxaimport.beans.XTNode;
import org.tolweb.tapestry.xml.taxaimport.beans.XTOthername;
import org.tolweb.tapestry.xml.taxaimport.beans.XTRoot;
import org.tolweb.tapestry.xml.taxaimport.beans.XTSourceInformation;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class XMLTaxaUploadTest extends ApplicationContextTestAbstract {
	 
	public XMLTaxaUploadTest(String name) {
		super(name);
	}

	public void testDifferentStuff() {
		try {
			// turn validation on
			SAXReader reader = new SAXReader(true);
			// request XML Schema validation
			reader.setFeature("http://apache.org/xml/features/validation/schema", true);
			InputStream instanceDoc = getClass().getResourceAsStream("./Heliconius_sample.xml");
			org.dom4j.Document document = reader.read(instanceDoc);
			System.out.println(document.asXML());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("exception occured... " + ex.getClass().toString());
			System.out.println(ex.toString());				
		}
	}
	
	public void testSchemaIsOkay() {
	    // parse an XML document into a DOM tree
		// ParserConfigurationException
		org.w3c.dom.Document document = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
		    DocumentBuilder parser = docFactory.newDocumentBuilder();
		    document = parser.parse(getClass().getResourceAsStream("./Heliconius_sample.xml"));
		} catch (ParserConfigurationException pcex) {
			System.out.println("parser's not configured right...");
			pcex.printStackTrace();
		} catch (SAXException sex) {
			System.out.println("weird... a sax problem? ");
			sex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("something TOTALLY unexpected happened... ");
			ex.printStackTrace();
		}
		
	    // create a SchemaFactory capable of understanding WXS schemas
	    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	    // load a WXS schema, represented by a Schema instance
	    Source schemaFile = new StreamSource(getClass().getResourceAsStream("./taxaimport.xsd"));
	    
	    Schema schema = null;
	    try {
	    	schema = factory.newSchema(schemaFile);
	    } catch (SAXException sex) {
	    	
	    }
	    // create a Validator instance, which can be used to validate an instance document
	    Validator validator = schema.newValidator();

	    // validate the DOM tree
	    try {
	        validator.validate(new DOMSource(document));
	        System.out.println("if I made it here... I'm valid.. I guess");
	    } catch (SAXException e) {
	        // instance document is invalid!
	    	e.printStackTrace();
	    	System.out.println("I think the document isn't valid eh");
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    } catch (Exception ex) {
			System.out.println("in validation... something TOTALLY unexpected happened... ");
			ex.printStackTrace();
		}
	    
//		try {
//			  SAXParserFactory factory = SAXParserFactory.newInstance();
//			  factory.setValidating(true);
//			  factory.setNamespaceAware(true);
//
//			  SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//
//			  URL url = getClass().getResource("./taxaimport.xsd");
//			  System.out.println(url.toString());
//			  Schema schema = schemaFactory.newSchema(url);
//			  
//			  System.out.println("...........");
//			  factory.setSchema(schema);
//
//			  SAXParser parser = factory.newSAXParser();
//			  
//			  InputStream instanceDoc = getClass().getResourceAsStream("./Heliconius_sample.xml");
//			  
//			  SAXReader reader = new SAXReader(parser.getXMLReader());
//			  
//			  org.dom4j.Document doc = reader.read(instanceDoc);
//			  
//			  
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("exception occured... " + e.getClass().toString());
//			System.out.println(e.toString());			
//		}
//	    try {      
//	        XMLReader xerces = XMLReaderFactory.createXMLReader(
//	         "org.apache.xerces.parsers.SAXParser"); 
//	        xerces.setFeature(
//	          "http://apache.org/xml/features/validation/schema",
//	           true
//	        );                         
//	        System.out.println("huh...");
//	        InputStream schemaDoc = getClass().getResourceAsStream("./taxaimport.xsd");
//	        System.out.println(slurp(schemaDoc));
//	        schemaDoc = getClass().getResourceAsStream("./taxaimport.xsd");
//	        
//	        Builder parser = new Builder(xerces, true);
//	        parser.build(schemaDoc);
//	        
//	        System.out.println("taxaimport.xsd is schema valid.");
//	      }
//	      catch (SAXException ex) {
//	        System.out.println("Could not load Xerces.");
//	        System.out.println(ex.getMessage());
//	      }
//	      catch (ParsingException ex) {
//	        System.out.println("taxaimport.xsd is not schema valid.");
//	        System.out.println(ex.getMessage());
//	        ex.printStackTrace();
//	        System.out.println(" at line " + ex.getLineNumber() 
//	          + ", column " + ex.getColumnNumber());
//	      }
//	      catch (IOException ex) { 
//	        System.out.println(
//	         "Due to an IOException, Xerces could not check " 
//	         + "taxaimport.xsd"
//	        ); 
//	        ex.printStackTrace();
//	      }		
	}
	
	public void SchemaValidation() {
		try {
			InputStream instanceDoc = getClass().getResourceAsStream("./Heliconius_sample.xml");
			URL schemaDoc = getClass().getResource("./taxaimport.xsd");
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://tolweb.org/ns/taxaimport");
			factory.setSchema(schemaFactory.newSchema(schemaDoc));
			
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(new SimpleErrorHandler());

			Builder builder = new Builder(reader);
			builder.build(instanceDoc);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
		
/*

SAXParser parser = factory.newSAXParser();
XMLReader reader = parser.getXMLReader();
reader.setErrorHandler(new SimpleErrorHandler());

Builder builder = new Builder(reader);
builder.build("contacts.xml");
 */		
	}
	
	public void testLoad() {
		try {
			InputStream test = getClass().getResourceAsStream("./Heliconius_sample.xml");
			slurp(test);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}
	
	public void testBetwixt() {
		try {
			System.out.println("... ");
			InputStream xtnodes = getClass().getResourceAsStream("./Heliconius_sample.xml");
			BeanReader beanReader = new BeanReader();
			//beanReader.registerBeanClass("//source-information", XTSourceInformation.class);
			beanReader.registerBeanClass(XTRoot.class);
			
			XTRoot root = (XTRoot)beanReader.parse(xtnodes);
			if (root != null) {
				XTNode node = root.getNode();
				assertNotNull(node);
				assertEquals(new Long(72254), node.getId());
				assertEquals(new Boolean(false), node.getExtinct());
				assertEquals(0, node.getConfidence());
				assertEquals("monophyletic", node.getPhylesis());
				assertEquals(new Boolean(true), node.getLeaf());
				assertEquals(new Boolean(true), node.getHasPage());
				assertEquals(new Long(72231), node.getAncestorWithPage());
				assertEquals(new Boolean(true), node.getItalicizeName());
				assertEquals(new Boolean(false), node.getIncompleteSubgroups());
				assertEquals(new Boolean(true), node.getShowAuthority());
				assertEquals(new Boolean(false), node.getShowAuthorityContaining());
				assertEquals(new Boolean(false), node.getIsNewCombination());
				assertEquals("null", node.getCombinationDate());
				assertEquals(new Integer(2), node.getChildCount());
				assertEquals("Heliconius pachinus", node.getName());
				assertEquals("some description", node.getDescription());
				assertEquals("Salvin", node.getAuthority());
				assertEquals("name comment", node.getNameComment());
				assertEquals("comboAuthor", node.getCombinationAuthor());
				assertEquals("1871", node.getAuthDate());
				assertEquals("tribe", node.getRank());
				
				XTGeographicDistribution geoDist = node.getGeographicDistribution();
				assertNotNull(geoDist);
				assertEquals("Arizona, New Mexico", geoDist.getDescription());
				
				assertNotNull(node.getOthernames());
				assertEquals(3, node.getOthernames().size());
				assertEquals("Test 1", node.getOthernames().get(1).getName());
				assertEquals("Test 2", node.getOthernames().get(2).getName());
				XTOthername othername = node.getOthernames().get(0);
				assertEquals(new Boolean(true), othername.getIsImportant());
				assertEquals(new Boolean(false), othername.getIsPreferred());
				assertEquals(new Integer(0), othername.getSequence());
				assertEquals("1871", othername.getDate());
				assertEquals(new Boolean(true), othername.getItalicizeName());
				assertEquals("Heliconius cydno pachinus", othername.getName());
				assertEquals("Salvin", othername.getAuthority());
				System.out.println("othername comments: " + othername.getComments());
				assertEquals("Lamas (2004) views <em>pachinus</em> as a subspecies of <em>H. cydno</em>", othername.getComments());

				assertNotNull(node.getNodes());
				assertEquals(2, node.getNodes().size());
				assertEquals(new Long(1555), node.getNodes().get(0).getId());
				assertEquals("Butterfly 1", node.getNodes().get(0).getName());
				assertEquals(new Long(1557), node.getNodes().get(1).getId());
				assertEquals("Butterfly 2", node.getNodes().get(1).getName());
				
				XTSourceInformation srcInfo = node.getSourceInformation();
				assertNotNull(srcInfo);
				assertEquals("BGGGH001", srcInfo.getSourceId());
				assertEquals("TOL_TST11221", srcInfo.getSourceKey());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}

	public static String slurp (InputStream in) throws IOException {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}
	
	private class SimpleErrorHandler implements ErrorHandler {
	    public void warning(SAXParseException e) throws SAXException {
	        System.out.println(e.getMessage());
	    }

	    public void error(SAXParseException e) throws SAXException {
	        System.out.println(e.getMessage());
	    }

	    public void fatalError(SAXParseException e) throws SAXException {
	        System.out.println(e.getMessage());
	    }
	}	
}
