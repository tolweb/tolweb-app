package org.tolweb.content.export

class ServicesInfoTest extends AbstractApplicationTestContext {
	ServicesInfo si
	
	void setUp() {
		si = (ServicesInfo) context.getBean("servicesInfo")
	}
	
	void test_initialized() {
		assert si
	}
	
	void test_services_info_values() {
		assertEquals "webservices/content/%1\$s/%2\$d", si.contentServiceUrl
		assertEquals "webservices/treestructure/minimal/%1\$d", si.treeServiceUrl
		String formatted = String.format(si.contentServiceUrl, "nc", 8221)
		assertEquals "webservices/content/nc/8221", formatted
		formatted = String.format(si.treeServiceUrl, 1)
		assertEquals "webservices/treestructure/minimal/1", formatted
	}
	
	void tearDown() {
		si = null
	}
}