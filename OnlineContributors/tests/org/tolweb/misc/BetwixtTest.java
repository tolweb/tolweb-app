package org.tolweb.misc;

import java.io.InputStream;

import org.apache.commons.betwixt.io.BeanReader;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.misc.types.LayerNode;
import org.tolweb.misc.types.MeElement;

public class BetwixtTest extends ApplicationContextTestAbstract {
	public BetwixtTest(String name) {
		super(name);
	}
	
	public void testMeDocumentLoad() {
		try {
			InputStream xtnodes = getClass().getResourceAsStream("./me.xml");
			BeanReader beanReader = new BeanReader();
			beanReader.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
			beanReader.registerBeanClass(MeElement.class);
			MeElement root = (MeElement)beanReader.parse(xtnodes);
			assertNotNull(root);
			System.out.println(root);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}
	
	public void testDocumentLoad() {
		try {
			LayerNode root = getRootNode("./text.xml");
			assertNotNull(root);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
		}
	}
	
	public LayerNode getRootNode(String filename) {
		try {
			System.out.println("Test::getRootNode [...] ");
			InputStream xtnodes = getClass().getResourceAsStream(filename);
			BeanReader beanReader = new BeanReader();
			beanReader.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
			beanReader.registerBeanClass(LayerNode.class);
			return (LayerNode)beanReader.parse(xtnodes);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured... lame");
			System.out.println(e.toString());
			return null;
		}
		
	}	
}
