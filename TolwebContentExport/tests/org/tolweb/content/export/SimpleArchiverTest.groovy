package org.tolweb.content.export

class SimpleArchiverTest extends GroovyTestCase {
	SimpleArchiver sarch
	
	void setUp() {
		sarch = new SimpleArchiver(fullFilePath: '/home/dmandel/devel/tol-all-content-nc-test.xml')
		//new File('/home/dmandel/devel/tol-all-content-nc-test.xml.tar.gz').delete()		
		//new File('/home/dmandel/devel/tol-all-content-nc-test.xml.tar').delete()
	}
	
	void test_initialized() {
		assert sarch
	}
	
	void off_test_archive_creates_tarball() {
		assert sarch.create()
		
		//assert new File('/home/dmandel/devel/tol-all-content-nc-test.tar.gz').exists()
		assert new File('/home/dmandel/devel/tol-all-content-nc-test.tar').exists()
	}
	
	void off_test_file_extension_stripper() {
		def filename = 'tol-all-content-nc-test.xml.tar.gz'
		assertEquals 'tol-all-content-nc-test.xml.tar', sarch.stripFileExtension(filename)
		filename = 'tol-all-content-nc-test.xml'
		assertEquals 'tol-all-content-nc-test', sarch.stripFileExtension(filename)
		filename = 'tol-all-content-nc-test'
		assertEquals filename, sarch.stripFileExtension(filename)
	}
	
	void off_test_file_archive_extension() {
		def filename = 'tol-all-content-nc-test.xml.tar.gz'
		assertEquals 'tol-all-content-nc-test.xml.tar.tar.gz', sarch.getArchiveFileName(filename)
		filename = 'tol-all-content-nc-test.xml'
		assertEquals 'tol-all-content-nc-test.tar.gz', sarch.getArchiveFileName(filename)
		filename = 'tol-all-content-nc-test'
		assertEquals 'tol-all-content-nc-test.tar.gz', sarch.getArchiveFileName(filename)		
	}
	
	void tearDown() {
		sarch = null
	}
}