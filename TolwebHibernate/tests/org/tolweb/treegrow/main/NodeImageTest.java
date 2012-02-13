package org.tolweb.treegrow.main;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class NodeImageTest extends TestCase {
	private NodeImage media; 
	
	protected void setUp() throws Exception {
		super.setUp();
		media = new NodeImage();
	}
	
	// verify initial object state
	// 		private Boolean modificationPermitted = Boolean.valueOf(false);
	// 		private int id = -1;
    // 		private int copyrightContributorId = 0;
	
	
	
	// test the copy-constructor implemented 
	
	// test all mutators 
	
	// test generic setValue method 
	
	// test setValues(key, String) & setValues(key, bool) method 
	
	// test get word for media type 
	
	// test you can add and remove ImagePermission objects 
	
	// test you can add & remove nodes 
	
	// test TOL Image Tag correct 
	public void test_tol_image_tag_correct() {
		assertEquals("<ToLimg id=\"", NodeImage.TOL_IMG_STRING);
		assertEquals("<ToLimg id=\"-1\">", media.getToLimgString());
		
	}
	// test static structures present and contain expected values
	public void test_static_structure_present_and_contain_expected_values() {
		assertNotNull(NodeImage.TYPES_ARRAY);
		assertTrue(NodeImage.TYPES_ARRAY.length == 12);
		
		assertNotNull(NodeImage.TYPES_LIST);
		assertTrue(NodeImage.TYPES_LIST.size() == 12);
		
		assertNotNull(NodeImage.IMAGE_TYPES_ARRAY);
		assertTrue(NodeImage.IMAGE_TYPES_ARRAY.length == 3);
		
		assertNotNull(NodeImage.IMAGE_TYPES_LIST);
		assertTrue(NodeImage.IMAGE_TYPES_LIST.size() == 3);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		media = null;
	}
}
