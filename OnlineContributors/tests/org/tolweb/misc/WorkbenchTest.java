package org.tolweb.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.FilteredNodeDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.NodePusher;
import org.tolweb.dao.PageDAO;

public class WorkbenchTest extends ApplicationContextTestAbstract {
	private NodeDAO nodeDAO;
	private NodeDAO miscNodeDAO;
	private PageDAO pageDAO;
	private ImageDAO imageDAO;
	private FilteredNodeDAO filteredNodeDAO;
	private NodePusher nodePusher;
	
	public WorkbenchTest(String name) {
		super(name);
		nodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		pageDAO = (PageDAO)context.getBean("workingPageDAO");
		miscNodeDAO = (NodeDAO)context.getBean("nodeDAO");
		filteredNodeDAO = (FilteredNodeDAO)context.getBean("workingFilteredNodeDAO");
		nodePusher = (NodePusher)context.getBean("nodePusher");
		imageDAO = (ImageDAO)context.getBean("imageDAO");
	}

	// /data/1.XLive/contributorsimages/danny.blue.250.jpg
	public void test_writing_image_out() {
		try {
			FileInputStream fis = new FileInputStream(new File("/home/dmandel/documents/Danny.blue.250.jpg"));
			assertNotNull(fis);
			String path = "/data/1.XLive/contributorsimages/danny.blue.250.jpg";
			File f = new File("/data/1.XLive/contributorsimages/danny.txt");
			System.out.println("can write? " + f.canWrite());
			FileOutputStream fos = null;
			FileWriter fw = null;
			try {
				//fos = new FileOutputStream(f);
				fw = new FileWriter(f);
				fw.write("work...work...work...work");
				fw.flush();
			} catch (IOException ioe) {
				System.out.println("wth?");
				ioe.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			writeOutStream(fis, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
    protected String writeOutStream(InputStream stream, String path) {
        InputStream fis = stream; 
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(path));
            byte[] buffer = new byte[1024];  
            while (true) {            
                int length = fis.read(buffer);  
                if (length <  0) {
                    break;
                }
                fos.write(buffer, 0, length);               
            }        
        } catch (IOException ioe) {
        	fail();
            ioe.printStackTrace();
            path = "";
        }  finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException ioe) {}
            }   
            if (fos != null) {
                try { fos.close(); } catch (IOException ioe) {}
            }
        }
        return path;    	
    }	
	
//	public void test() {
//		String s = null; 
//		s += "Andy";
//		System.out.println(s);
//	}
	
//	public void testBasicNodeFetch() { 
//		MappedNode nd = nodeDAO.getNodeWithId(Long.valueOf(56976), false);
//		assertNotNull(nd);
//	}
//	
//	public void test_native_attached_images_method() {
//		List images = imageDAO.getNativeAttachedImagesForNode(Long.valueOf(543));
//		assertNotNull(images);
//		assertTrue(images.size() > 0);
//		assertEquals(5, images.size());
//		images = imageDAO.getNativeAttachedImagesForNode(Long.valueOf(2400));
//		assertNotNull(images);
//		assertTrue(images.size() > 0);
//		assertEquals(2, images.size());
//	}
//	
//	// TODO move this code into NodeHelper as getAncestorsFor(nd) 
//	public void testAncestorsTreewalk() {
//		MappedNode piperales = nodeDAO.getNodeWithId(20674L);
//		List<Long> ids = new ArrayList<Long>();
//		traverseTree(piperales, ids);
//		System.out.println("piperales ancestors: " + ids);
//		for (int i = ids.size() - 1; i >= 0; i--) {
//			MappedNode mnode = nodeDAO.getNodeWithId(ids.get(i));
//			System.out.println("ancestor: " + mnode);
//		}
//		List<Long> helperIds = NodeHelper.getAncestorIdsForNode(piperales, nodeDAO);
//		assertEquals(ids, helperIds);
//	}
//	
//	public void traverseTree(MappedNode root, List<Long> ids) {
//		if (root != null && !root.getNodeId().equals(Long.valueOf(1))) {
//			Long parentNodeId = root.getParentNodeId();
//			ids.add(parentNodeId);
//			traverseTree(nodeDAO.getNodeWithId(parentNodeId), ids);
//		}
//	}
	
//    private boolean isBetaSite(String host) {
//    	return isDevelopment(host, "beta");
//    }
//
//    private boolean isDevSite(String host) {
//    	return isDevelopment(host, "dev");
//    }    
//    
//    private boolean isDevelopment(String host, String devPrefix) {
//    	return host != null && (isNotFauxDevelopment(host, devPrefix));
//    }
//    
//    private boolean isNotFauxDevelopment(String host, String devPrefix) {
//    	return host.indexOf(devPrefix) != -1 && host.indexOf(devPrefix+".working") == -1;
//    }
//    
//    public void testBetaHelper() {
//    	assertEquals(true, isBetaSite("beta.tolweb.org"));
//    	assertEquals(false, isBetaSite("beta.working.tolweb.org"));
//    	assertEquals(true, isBetaSite("working.beta.tolweb.org"));
//    	assertEquals(false, isBetaSite("working.tolweb.org"));
//    	assertEquals(false, isBetaSite("tolweb.org"));
//    	assertEquals(false, isBetaSite("newsystem.tolweb.org"));
//    	assertEquals(true, isBetaSite("beta"));
//    	assertEquals(true, isBetaSite("betabetabetabeta"));
//    	assertEquals(false, isBetaSite(""));
//    	assertEquals(false, isBetaSite(null));
//    }
//
//    public void testDevHelper() {
//    	assertEquals(true, isDevSite("dev.tolweb.org"));
//    	assertEquals(false, isDevSite("dev.working.tolweb.org"));
//    	assertEquals(true, isDevSite("working.dev.tolweb.org"));
//    	assertEquals(false, isDevSite("working.tolweb.org"));
//    	assertEquals(false, isDevSite("tolweb.org"));
//    	assertEquals(false, isDevSite("newsystem.tolweb.org"));
//    	assertEquals(true, isDevSite("dev"));
//    	assertEquals(true, isDevSite("devdevdevdev"));
//    	assertEquals(false, isDevSite(""));
//    	assertEquals(false, isDevSite(null));    	
//    }    
    
//	public void testNodePusher() {
//		MappedNode life = nodeDAO.getNodeWithId(1L);
//		assertNotNull(life);
//		try {
//			nodePusher.pushNodeToDB(life, miscNodeDAO);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public void testEolClassLoad() {
//		ContentLicenseClass licClass = ContentLicenseClass.createContentLicenseClass("nc");
//		Long nodeId = 8221L;
//		ContentParameters cparams = new ContentParameters(nodeId, new Date(System.currentTimeMillis()), licClass);
//		ContentPreparer contentPrep = new EolContentPreparer2(cparams, pageDAO, nodeDAO, imageDAO);
//		assertNotNull(contentPrep);
//	}
	
//	public void test_format_string() {
//		String format = String.format("%1$s %4$s%2$s %3$d%5$s", 
//				"Vombatidea", "Bolton", new Integer(1789), "(", ")");
//		System.out.println(format); 
//	}
	
//	public void testFoo() {
//		HtmlPreparer prep = new HtmlPreparer();
//		assertNotNull(prep);
//		boolean isInstance = HTMLAnchorElementImpl.class.isInstance(prep);
//		assertFalse(isInstance);
//	}
//	
//	public void testFilterOtherNames() { 
//		List<String> outputLog = new ArrayList<String>();
////		TaxaImportCheck.filterOtherNames(16248L, miscNodeDAO, nodeDAO, nodePusher, outputLog);
//		MappedNode node = nodeDAO.getNodeWithId(119157L, true);
//		SortedSet otherNames = node.getSynonyms();
//		Set<String> filterSet = new HashSet<String>();
//		for (Iterator itr = new ArrayList(otherNames).iterator(); itr.hasNext(); ) {
//			MappedOtherName moname = (MappedOtherName)itr.next();
//			String comboKey = moname.getName() + moname.getAuthority() + moname.getAuthorityYear();
//			boolean added = filterSet.add(comboKey);
//			if (!added) {
//				otherNames.remove(moname);
//			}
//		}
//		node.setSynonyms(otherNames);
//		nodeDAO.saveNode(node);
//		try {
//			nodePusher.pushNodeToDB(node, nodeDAO);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	// push to misc - example for a "fix page" for this
//	public void testNodePush() {
//		MappedNode piperales = nodeDAO.getNodeWithId(20674L, true);
//		assertNotNull(piperales);
//		try {
//			nodePusher.pushNodeToDB(piperales, miscNodeDAO);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		MappedNode laurales  = nodeDAO.getNodeWithId(20672L, true);
//		try {
//			nodePusher.pushNodeToDB(laurales, miscNodeDAO);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	private class SimpleErrorHandler implements ErrorHandler {
//	    public void warning(SAXParseException e) throws SAXException {
//	        System.out.println(e.getMessage());
//	    }
//
//	    public void error(SAXParseException e) throws SAXException {
//	        System.out.println(e.getMessage());
//	    }
//
//	    public void fatalError(SAXParseException e) throws SAXException {
//	        System.out.println(e.getMessage());
//	    }
//	}	
//	
//	public void festEOLInstanceDocuments() {
//		try {
//			SAXParserFactory factory = SAXParserFactory.newInstance();
//			factory.setValidating(false);
//			factory.setNamespaceAware(true);
//		
//			SAXParser saxparser = factory.newSAXParser();
//			XMLReader reader = saxparser.getXMLReader();
//			reader.setErrorHandler(new SimpleErrorHandler());
//			
//			Builder parser = new Builder(reader);
//			Document doc = parser.build(getClass().getResourceAsStream("./arachnologist.xml"));
//			Nodes nodes = doc.query("");
//			System.out.println("document is well-formed.");
//		} catch (ParsingException ex) {
//			System.out.println("document is not well-formed.");
//			System.out.println(ex.getMessage());
//			System.out.println(" at line " + ex.getLineNumber() + ", column " + ex.getColumnNumber());
//		} catch (IOException ex) { 
//			System.out.println("Due to an IOException, the parser could not check " + "./arachnologist.xml"); 
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("exception occured... lame");
//			System.out.println(e.toString());
//		}
//		try {
//			// turn validation on
//			SAXReader reader = new SAXReader(true);
//			// request XML Schema validation
//			reader.setFeature("http://apache.org/xml/features/validation/schema", true);
//			
//			InputStream instanceDoc = getClass().getResourceAsStream("./arachnologist.xml");
//			org.dom4j.Document document = reader.read(instanceDoc);
//			System.out.println(document.asXML());
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			System.out.println("exception occured... " + ex.getClass().toString());
//			System.out.println(ex.toString());				
//		}		
//	}
	
//	public void testOtherNamesMove() {
//		MappedNode nd1 = nodeDAO.getNodeWithId(119453L, true);
//		MappedNode nd2 = nodeDAO.getNodeWithId(119454L, true);
//		nd2.copyValues(nd1, true);
//	}
	
//	public void testFetchingInactiveNodes() {
//		Set descIds = miscNodeDAO.getDescendantIdsForNode(15994L); //16248
//		List<MappedNode> handmade = new ArrayList<MappedNode>();
//		System.out.println(descIds);
//		System.out.println("Of " + descIds.size() + " - inactive nodes: ");
//		int i = 0;
//		for (Iterator itr = descIds.iterator(); itr.hasNext(); ) {
//			Long descId = (Long)itr.next();
//			MappedNode mnode = nodeDAO.getNodeWithId(descId, true);
//			MappedNode foo = filteredNodeDAO.getInactiveNodeWithId(descId);
//			
//			if (mnode != null && mnode.getStatus().equals(MappedNode.INACTIVE)) {
//				i++;
//				System.out.println("####" + mnode);
//				handmade.add(mnode);
//			} 
//
//			if (mnode == null) {
//				System.out.println("null? " + descId);
//			}
//		}
//		System.out.println("inactive count: " + i);
//		List<MappedNode> inactiveNodes = NodeHelper.getInactiveNodesForClade(15994L, miscNodeDAO, nodeDAO);
//		assertEquals(handmade, inactiveNodes);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void testNodesOnPageFetch() {
//		MappedNode mnode = nodeDAO.getNodeWithId(new Long(8221));
//		MappedPage mpage = pageDAO.getPage(mnode);
//		System.out.println("node-id: " + mnode.getId());
//		System.out.println("page-id: " + mpage.getPageId());
//		List nodes = pageDAO.getNodesOnPage(mpage, true);
//		assertNotNull(nodes);
//		System.out.println(nodes.toString());		
//		assertTrue(!nodes.isEmpty());
//
//	}
//	
//	public void testStringFormat() {
//		Long oldNodeId = new Long(9042);
//		Long newNodeId = new Long(29650);
//		Long imageId = new Long(650);
//		
//		String sample = "UPDATE Images_To_Nodes SET node_id = %1$d WHERE image_id = %2$d AND node_id = %3$d";
//		System.out.println(String.format(sample, oldNodeId, newNodeId, imageId));
//	}
//	
//	public void testDetermineOrderBy() {
//		String output = determineOrderBy("orderOnParent asc");
//		System.out.println(output);
//		assertEquals(output, "orderOnParent false");
//		
//		output = determineOrderBy("n.orderOnParent asc");
//		System.out.println(determineOrderBy("n.orderOnParent asc"));
//		assertEquals(output, "orderOnParent false");
//	}
//	
//	private String determineOrderBy(String orderByString) {
//		boolean isDesc = orderByString.endsWith("desc");
//		if (orderByString.contains(".")) {
//			orderByString = orderByString.substring(orderByString.indexOf(".")+1);
//		}
//		String[] pieces = orderByString.split(" ");
//		return pieces[0] + " " + isDesc;
//	}	
//	
//	public void testDetermineSelectProperty() {
//		String output = determineSelectProperty("select n.nodeId");
//		System.out.println(output);
//		assertEquals(output, "nodeId");
//
//		output = determineSelectProperty("select nodeId");
//		System.out.println(output);
//		assertEquals(output, "nodeId");
//
//		output = determineSelectProperty("select n.name ");
//		System.out.println(output);
//		assertEquals(output, "name");
//	}
//	
//	private String determineSelectProperty(String selectString) {
//		if (selectString.contains("select")) {
//			selectString = selectString.substring(selectString.indexOf("select")+"select".length()+1);
//		}
//		
//		if (selectString.contains(".")) {
//			selectString = selectString.substring(selectString.indexOf(".")+1);
//		}
//		return selectString.trim();
//	}	
//	
//	public void testHQLOrderByDefault() {
//		MappedNode beetles = nodeDAO.getNodeWithId(new Long(8221));
//		List l = nodeDAO.getChildrenNodes(beetles);
//		
//	}

}
