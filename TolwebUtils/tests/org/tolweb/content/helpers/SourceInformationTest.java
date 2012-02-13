package org.tolweb.content.helpers;

import junit.framework.TestCase;

import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

//import com.sun.org.apache.bcel.internal.verifier.statics.Pass1Verifier;

public class SourceInformationTest extends TestCase {
	private NodeImage mediaFile;
	private ForeignDatabase foreignDB;
	
	public void setUp() {
		// create a faux media file 
		mediaFile = new NodeImage();
		mediaFile.setId(650);
		mediaFile.setLocation("sampleFile.png");
		mediaFile.setSourceDbId(55L);
		mediaFile.setSourceCollectionUrl("http://some.other.db.com/");
		mediaFile.setSourceCollectionTitle("Some Other Database");
		
		// create a faux foreign database
		foreignDB = new ForeignDatabase();
		foreignDB.setId(55L);
		foreignDB.setName("TestCaseDatabase");
		foreignDB.setUrl("http://testcase.db.com/");
		foreignDB.setUrlFormat("http://testcase.db.com/not-smart?sql-inject=<text>");
	}
	
	public void test_getting_data_from_foreign_database() {
		// if we're w/o source collection info, we use foreign db 
		mediaFile.setSourceCollectionUrl(null);
		
		SourceInformation srcInfo = new SourceInformation(mediaFile, foreignDB);
		assertFalse(srcInfo.isTolNative());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollection()));
		assertEquals("TestCaseDatabase", srcInfo.getSourceCollection());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollectionUrl()));
		assertEquals("http://testcase.db.com/", srcInfo.getSourceCollectionUrl());
	}
	
	public void test_getting_data_from_source_collection() {
		// if we're w/o a source db id, we use source collection info
		mediaFile.setSourceDbId(null);
		
		SourceInformation srcInfo = new SourceInformation(mediaFile, foreignDB);
		assertFalse(srcInfo.isTolNative());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollection()));
		assertEquals("Some Other Database", srcInfo.getSourceCollection());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollectionUrl()));
		assertEquals("http://some.other.db.com/", srcInfo.getSourceCollectionUrl());		
	}
	
	public void test_applying_native_tol() {
		// we've got tol-native data so neither value is used
		mediaFile.setSourceCollectionUrl(null);
		mediaFile.setSourceDbId(null);
		
		SourceInformation srcInfo = new SourceInformation(mediaFile, foreignDB);
		assertTrue(srcInfo.isTolNative());		
		assertTrue(StringUtils.isEmpty(srcInfo.getSourceCollection()));
		assertTrue(StringUtils.isEmpty(srcInfo.getSourceCollectionUrl()));
		
		mediaFile.setSourceCollectionUrl("");
		mediaFile.setSourceDbId(null);
		
		srcInfo = new SourceInformation(mediaFile, foreignDB);
		assertTrue(srcInfo.isTolNative());		
		assertTrue(StringUtils.isEmpty(srcInfo.getSourceCollection()));
		assertTrue(StringUtils.isEmpty(srcInfo.getSourceCollectionUrl()));		
	}
	
	public void test_null_foreign_db() {
		foreignDB = null;
		SourceInformation srcInfo = new SourceInformation(mediaFile, foreignDB);
		assertFalse(srcInfo.isTolNative());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollection()));
		assertEquals("Some Other Database", srcInfo.getSourceCollection());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollectionUrl()));
		assertEquals("http://some.other.db.com/", srcInfo.getSourceCollectionUrl());		
	}
	
	public void test_only_media_file() {
		SourceInformation srcInfo = new SourceInformation(mediaFile);
		assertFalse(srcInfo.isTolNative());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollection()));
		assertEquals("Some Other Database", srcInfo.getSourceCollection());
		assertTrue(StringUtils.notEmpty(srcInfo.getSourceCollectionUrl()));
		assertEquals("http://some.other.db.com/", srcInfo.getSourceCollectionUrl());		
	}
	
	public void test_only_media_file_but_no_source_information() {
		mediaFile.setSourceCollectionUrl(null);
		
		SourceInformation srcInfo = new SourceInformation(mediaFile);
		assertTrue(srcInfo.isTolNative());
		assertTrue(StringUtils.isEmpty(srcInfo.getSourceCollection()));
		assertTrue(StringUtils.isEmpty(srcInfo.getSourceCollectionUrl()));		
	}
	
	public void test_null_media_file() {
		mediaFile = null;
		try {
			SourceInformation srcInfo = new SourceInformation(mediaFile);
			System.out.println(srcInfo.toString());
			fail();
		} catch (NullPointerException npe) {
			// the exception that I expect to be thrown
		} catch (Exception e) {
			fail();
		}
	}
	
	public void test_text_section_is_native() {
		SourceInformation srcInfo = new SourceInformation(new MappedTextSection());
		assertTrue(srcInfo.isTolNative());
	}
	
	public void tearDown() {
		mediaFile = null;
		foreignDB = null;		
	}
}
