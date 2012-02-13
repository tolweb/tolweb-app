package org.tolweb.tapestry.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.commons.betwixt.io.BeanReader;
import org.tolweb.attributes.PositionConfidence;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.xml.taxaimport.TaxaImportCheck;
import org.tolweb.tapestry.xml.taxaimport.TaxaImportHelper;
import org.tolweb.tapestry.xml.taxaimport.beans.XTNode;
import org.tolweb.tapestry.xml.taxaimport.beans.XTRoot;
import org.tolweb.tapestry.xml.taxaimport.beans.XTSourceInformation;
import org.tolweb.treegrow.main.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLTaxaImportTest extends ApplicationContextTestAbstract {
	public XMLTaxaImportTest(String name) {
		super(name);
	}

	public void testXPath() {
		try {
			XMLReader xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			Builder parser = new Builder(xerces);
			InputStream inputDoc = getClass().getResourceAsStream("./wombats.xml");
			String inputText = "";
			try {
				inputText = slurp(inputDoc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Document doc = parser.build(new StringReader(inputText));
			Nodes nodes = doc.query("//node/name");
			List<String> names = new ArrayList<String>();
			if (nodes != null && nodes.size() > 0) {
				for (int i = 0; i < nodes.size(); i++) {
					Node n = nodes.get(i);
					System.out.println("node: " + n + " value: " + n.getValue().trim());
					names.add(n.getValue().trim());
				}
			} else {
				System.out.println("no nodes... ");
			}
			nodes = doc.query("//othername/name");
			List<String> othernames = new ArrayList<String>();
			if (nodes != null && nodes.size() > 0) {
				for (int i = 0; i < nodes.size(); i++) {
					Node n = nodes.get(i);
					System.out.println("othername: " + n + " value: " + n.getValue().trim());
					othernames.add(n.getValue().trim());
				}
			} else {
				System.out.println("no nodes... ");
			}			
			
			Set<String> dupes = new TreeSet<String>();
			boolean nohomonyms = TaxaImportCheck.performHomonymyCheck(names, dupes);
			System.out.println("no homonyms? " + nohomonyms);
		} catch (SAXException ex) {
			System.out.println(ex.getMessage());
		} catch (ParsingException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println("Due to an IOException, the parser could not print ");
		}		
	}
	
	public XTRoot getRootNode(String filename) {
		try {
			System.out.println("... ");
			InputStream xtnodes = getClass().getResourceAsStream(filename);
//			InputStream test = getClass().getResourceAsStream(filename);
//			String output = slurp(test);
//			System.out.println("file: \n" + ((output != null) ? output.length() : "<null>"));
//			System.out.println(output);
			BeanReader beanReader = new BeanReader();
			//beanReader.registerBeanClass(XTGeographicDistribution.class);
			beanReader.registerBeanClass(XTRoot.class);
			return (XTRoot)beanReader.parse(xtnodes);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
			return null;
		}
		
	}
	
	public void testHeteroteuthisBetwixt() {
		try {
			XTRoot root = getRootNode("./heteroteuthis_mod.xml");
			assertNotNull(root);
			assertEquals(root.getVersion(), "1.0");
			if (root != null) {
				assertTrue(!root.hasSingleRootNode());
				List<XTNode> nodes = root.getNodes();
				assertNotNull(nodes);
				checkAttributes(nodes);
				assertNotNull(nodes.get(3));
				checkAttributes(nodes.get(3).getNodes());
				//checkCopyOp(nodes.get(3).getNodes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());			
		}
	}

	private void checkCopyOp(List<XTNode> xtnodes) {
		List<MappedNode> mnodes = new ArrayList<MappedNode>();
		for (XTNode xtnode : xtnodes) {
			MappedNode mnode = new MappedNode();
			TaxaImportHelper.copyNodeData(mnode, xtnode, false, false);
			assertEquals(mnode.getName(), xtnode.getName());
			assertTrue(mnode.getAuthorityDate().equals(Integer.parseInt(xtnode.getAuthDate())));
			assertEquals(new Boolean(mnode.getIsLeaf()), xtnode.getLeaf());
			assertEquals(mnode.getIsNewCombination(), xtnode.getIsNewCombination().booleanValue());
			assertEquals(mnode.getItalicizeName(), xtnode.getItalicizeName().booleanValue());
			assertTrue(mnode.getCombinationDate().equals(Integer.parseInt(xtnode.getCombinationDate())));
			assertEquals(mnode.getNameAuthority(), xtnode.getAuthority());
			assertEquals(StringUtils.isEmpty(mnode.getCombinationAuthor()), StringUtils.isEmpty(xtnode.getCombinationAuthor()));
			int confidence = PositionConfidence.toInt(PositionConfidence.fromString(xtnode.getConfidence()));
			assertEquals(mnode.getConfidence(), confidence);
			assertEquals(mnode.getDescription(), xtnode.getDescription());
			assertEquals((mnode.getExtinct() == MappedNode.EXTINCT), xtnode.getExtinct().booleanValue());
		}
	}
	
	private void checkAttributes(List<XTNode> nodes) {
		for (XTNode node : nodes) {
			System.out.println("\n\tnode-name: " + node.getName());
			System.out.println("\t\tnew-combo: " + node.getIsNewCombination());
			System.out.println("\t\tcombo-date: " + node.getCombinationDate());
			System.out.println("\t\tconfidence: " + node.getConfidence());
			System.out.println("\t\thas-page: " + node.getHasPage());
			assertNotNull(node.getIsNewCombination());
			assertNotNull(node.getConfidence());
		}
	}
	
	
	
	public void ftestHaliplusBetwixt() {
		try {
			XTRoot root = getRootNode("./haliplus.xml");
			assertNotNull(root);
			if (root != null) {
				if (root.hasSingleRootNode()) {
					XTNode node = root.getNode();
					assertNotNull(node);
				} else {
					List<XTNode> nodes = root.getNodes();
					assertNotNull(nodes);
					for (XTNode node : nodes) {
						System.out.println("\n\tnode-name: " + node.getName());
						System.out.println("\tauthority: " + node.getAuthority());
						System.out.println("\tauth-date: " + node.getAuthDate());
						System.out.println("\tnode-rank: " + node.getRank());						
						assertNotNull(node.getName());
						assertNotNull(node.getAuthority());
//						assertNotNull(node.getAuthDate());
						assertNotNull(node.getRank());
					}
				}
//				assertEquals("Migadopinae", node.getName());
//				assertEquals("Chaudoir", node.getAuthority());
//				assertEquals("1861", node.getAuthDate());
//				assertEquals("Subfamily", node.getRank());
//				
//				assertNotNull(node.getNodes());
//				assertTrue(!node.getNodes().isEmpty());
//				System.out.println("child nodes: " + node.getNodes().size());
//				
//				XTSourceInformation srcInfo = node.getSourceInformation();
//				assertNotNull(srcInfo);
//				assertEquals("LorenzCarabCat", srcInfo.getSourceId());
//				assertEquals("356751", srcInfo.getSourceKey());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}		
	}
	
	public void jajtestBetwixt() {
		try {
			XTRoot root = getRootNode("./Migadopine3.xml");
			assertNotNull(root);
			if (root != null) {
				XTNode node = root.getNode();
				assertNotNull(node);
				assertEquals("Migadopinae", node.getName());
				assertEquals("Chaudoir", node.getAuthority());
				assertEquals("1861", node.getAuthDate());
				assertEquals("Subfamily", node.getRank());
				
				assertNotNull(node.getNodes());
				assertTrue(!node.getNodes().isEmpty());
				System.out.println("child nodes: " + node.getNodes().size());
				
				XTSourceInformation srcInfo = node.getSourceInformation();
				assertNotNull(srcInfo);
				assertEquals("LorenzCarabCat", srcInfo.getSourceId());
				assertEquals("356751", srcInfo.getSourceKey());
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
}
