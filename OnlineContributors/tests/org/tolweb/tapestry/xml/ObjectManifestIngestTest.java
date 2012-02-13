package org.tolweb.tapestry.xml;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.tapestry.xml.taxaimport.beans.OMAccessoryPage;
import org.tolweb.tapestry.xml.taxaimport.beans.OMContributor;
import org.tolweb.tapestry.xml.taxaimport.beans.OMMediaFile;
import org.tolweb.tapestry.xml.taxaimport.beans.OMNode;
import org.tolweb.tapestry.xml.taxaimport.beans.OMOtherName;
import org.tolweb.tapestry.xml.taxaimport.beans.OMRoot;

public class ObjectManifestIngestTest extends ApplicationContextTestAbstract {
	public ObjectManifestIngestTest(String name) {
		super(name);
	}
	
	public void testOmma() {
		runBetwixt("./omma_objectmanifest1.xml");
	}
	
	public void DONTtestCarabidae() {
		runBetwixt("./carabidae_objectmanifest4.xml");
	}
	
	public void DONTtestCarabidaeOutput() {
		try {
			System.out.println("output test started... ");
			OMRoot root = getBetwixtObjectModel("./carabidae_objectmanifest4.xml");			
			List<OMNode> nodes = root.getNodes();
			assertNotNull(nodes);
			System.out.println("size: " + nodes.size());
			List<OMNode> death = new ArrayList<OMNode>();
			for (OMNode node : nodes) {
				if (node != null) {
					if (node.getId() % 2 == 0) {
						death.add(node);
					}
				}
			}
			System.out.println("node count: " + nodes.size());
			nodes.removeAll(death);
			System.out.println("node count: " + nodes.size());

			FileWriter output = new FileWriter("/tmp/mod_carabidae.xml");
			
			// Write XML document
			BeanWriter beanWriter = new BeanWriter(output);
			beanWriter.setEndOfLine("\r\n");
			beanWriter.setIndent("\t");			
			beanWriter.enablePrettyPrint();
			beanWriter.write(root);

			output.flush();
			output.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}
	
	public void testCarabidaeOutputAsString() {
		try {
			System.out.println("output test started... ");
			OMRoot root = getBetwixtObjectModel("./carabidae_objectmanifest4.xml");			
			List<OMNode> nodes = root.getNodes();
			assertNotNull(nodes);
			System.out.println("size: " + nodes.size());
			List<OMNode> death = new ArrayList<OMNode>();
			for (OMNode node : nodes) {
				if (node != null) {
					if (node.getId() % 2 == 0) {
						death.add(node);
					}
				}
			}
			System.out.println("node count: " + nodes.size());
			nodes.removeAll(death);
			System.out.println("node count: " + nodes.size());

			StringWriter output = new StringWriter();
			
			// Write XML document
			BeanWriter beanWriter = new BeanWriter(output);
			beanWriter.setEndOfLine("\r\n");
			beanWriter.setIndent("\t");			
			beanWriter.enablePrettyPrint();
			beanWriter.write(root);

			output.flush();
			output.close();
			System.out.println(output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}
	
	public OMRoot getBetwixtObjectModel(String filename) throws Exception {
		InputStream xtnodes = getClass().getResourceAsStream(filename);
		BeanReader beanReader = new BeanReader();
		beanReader.registerBeanClass(OMRoot.class);
		
		return (OMRoot)beanReader.parse(xtnodes);
	}
	
	public void runBetwixt(String filename) {
		try {
			System.out.println("... ");
			OMRoot root = getBetwixtObjectModel(filename);
			System.out.println("key: " + root.getId());
			System.out.println("basal: " + root.getBasalNodeId());
			System.out.println("ns: " + root.getXmlNamespace());
			if (root != null) {
				List<OMNode> nodes = root.getNodes();
				assertNotNull(nodes);
				System.out.println("size: " + nodes.size());
				for (OMNode node : nodes) {
					assertNotNull(node);
					System.out.println("id: " + node.getId() + " name: " + node.getName());
					System.out.println("page: " + node.getPage());
					assertNotNull(node.getMediafiles());
					if (node.getMediafiles() != null) {
						for (OMMediaFile mediafile : node.getMediafiles()) {
							System.out.println("\t\tid: " + mediafile.getId() + " type: " + mediafile.getType());
						}
					}
					assertNotNull(node.getContributors() == null);
					for (OMContributor contributor : node.getContributors()) {
						System.out.println("\t\tid: " + contributor.getId() + " type: " + contributor.getType());						
					}
					assertNotNull(node.getOthernames() == null);
					for (OMOtherName othername : node.getOthernames()) {
						System.out.println("\t\tid: " + othername.getId() + " type: " + othername.getType());						
					}
					assertNotNull(node.getAccessorypages() == null);
					for (OMAccessoryPage accpage : node.getAccessorypages()) {
						System.out.println("\t\tid: " + accpage.getId() + " type: " + accpage.getType());
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}	
}
