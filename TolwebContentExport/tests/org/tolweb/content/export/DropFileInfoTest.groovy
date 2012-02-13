package org.tolweb.content.export

import java.util.Properties
import java.io.File
import java.io.IOException

class DropFileInfoTest extends AbstractApplicationTestContext {
	DropFileInfo dfi 
	
	void setUp() {
		dfi = (DropFileInfo)context.getBean("dropFileInfo")
	}
	
	void test_initialized() {
		assert dfi
		assert dfi.fileName
		assert dfi.location
		
		Properties properties = new Properties();
		ResourceBundle bundle
		try {
			bundle = ResourceBundle.getBundle("application")
		} catch (Exception e) { 
			fail()
		}
		
		assert getClass().getClassLoader().getResourceAsStream("application.properties")
		
		def dropfilename = bundle.getObject("dropfilename")?.toString()
		def droplocation = bundle.getObject("droplocation")?.toString()
				
		if (!droplocation?.endsWith(File.separator)) {
			droplocation += File.separator
		}
		
		assertEquals dropfilename, dfi.fileName
		assertEquals droplocation, dfi.location
		assertEquals droplocation + dropfilename, dfi.fullPath
	}
	
	void tearDown() {
		dfi = null
	}
}