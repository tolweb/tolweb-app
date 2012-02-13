package org.tolweb.content.export

class HostInfoTest extends AbstractApplicationTestContext {
	HostInfo hi 
	
	void setUp() {
		hi = (HostInfo)context.getBean("hostInfo")
	}
	
	void test_initialized() {
		assert hi 
	}
	
	void test_host_info() {
		assertEquals "beta.tolweb.org", hi.hostPrefix
		assertEquals "http://", hi.protocol
		assertEquals "http://beta.tolweb.org/", hi.toString()
	}
	
	void tearDown() {
		hi = null
	}
}