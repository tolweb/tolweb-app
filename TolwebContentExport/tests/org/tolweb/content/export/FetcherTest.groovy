package org.tolweb.content.export 

import org.apache.commons.httpclient.HttpException

class FetcherTest extends AbstractApplicationTestContext {
	Fetcher fetcher

	def ommaOutput = '''<?xml version="1.0" standalone="yes"?>

<TREE>
  <NODE ID="9042" HASPAGE="1">
    <NODES>
      <NODE ID="9043" HASPAGE="0"></NODE>
      <NODE ID="29714" HASPAGE="0"></NODE>
      <NODE ID="9045" HASPAGE="0"></NODE>
      <NODE ID="9044" HASPAGE="0"></NODE>
      <NODE ID="29715" HASPAGE="0"></NODE>
      <NODE ID="29716" HASPAGE="0"></NODE>
      <NODE ID="29717" HASPAGE="0"></NODE>
      <NODE ID="29718" HASPAGE="0"></NODE>
      <NODE ID="29719" HASPAGE="0"></NODE>
      <NODE ID="29720" HASPAGE="0"></NODE>
      <NODE ID="29721" HASPAGE="0"></NODE>
      <NODE ID="29722" HASPAGE="0"></NODE>
      <NODE ID="29723" HASPAGE="0"></NODE>
      <NODE ID="29724" HASPAGE="0"></NODE>
      <NODE ID="29725" HASPAGE="0"></NODE>
    </NODES>
  </NODE>
</TREE>'''
	
	void setUp() {
		HostInfo hi = (HostInfo) context.getBean("hostInfo")
		ServicesInfo si = (ServicesInfo) context.getBean("servicesInfo")
		fetcher = new Fetcher(hostInfo: hi, servicesInfo: si)
	}
	
	void test_initialized() {
		assert fetcher
		assertEquals "http://beta.tolweb.org/", fetcher.hostInfo.toString()
		assertEquals "webservices/content/%1\$s/%2\$d", fetcher.servicesInfo.contentServiceUrl
		assertEquals "webservices/treestructure/minimal/%1\$d", fetcher.servicesInfo.treeServiceUrl
		assertEquals "http://beta.tolweb.org/webservices/content/nc/8221", 
						String.format(fetcher.hostInfo.toString() + 
								fetcher.servicesInfo.contentServiceUrl, "nc", 8221)
		assertEquals "http://beta.tolweb.org/webservices/treestructure/minimal/8221", 
						fetcher.getTreeStructureUrl(8221)
	}
	
	void test_basic_functionality() {
		assertEquals "http://beta.tolweb.org/webservices/treestructure/minimal/8221", 
						fetcher.getTreeStructureUrl(8221)

		String output = fetcher.fetchTreeStructure(9042)
		output = output.trim()
		println output
		assert output
		assertEquals ommaOutput, output
	}
	
	void test_xml_slurper_functionality() {
		
		def tree = new XmlParser().parseText(fetcher.fetchTreeStructure(15994))
		println 'root node size: ' + tree.NODE.size()
		println 'all nodes: ' + tree.depthFirst().size()
		println 'root node data: ' + tree.NODE[0]
		println 'root node attribute HASPAGE: ' + tree.NODE[0].@HASPAGE
		println 'nodes data: ' + tree.NODE[0].NODES
		println 'nodes first child: ' + tree.NODE[0].NODES.NODE[0]
		println 'all children: ' + tree.NODE[0].'*'.NODE
		println 'all children: ' + tree.NODE.'*'.NODE
		println '-------------------------------------'
		
		//println nodes
		def nodes = tree.depthFirst().NODE.findAll { it['@HASPAGE'] == '1' } 
		//println nodes.class.toString()
		//println nodes.size()
		println "...."
		(0..<nodes.size()).each {
			//println 'ID=' + nodes[it].'@ID'
		}
		
	}

	void test_service_404() {
		def url = fetcher.hostInfo.toString() + "webservices/content/al/8221"
		println "url: ${url}"
		def output = null
		output = fetcher.coreFetch(url)
		assertNull output
		
	}
	
	// this slows down the overall process of running all tests... 
	// remove the "off_" prefix when you wish to include it
	void off_test_fetch_large_group() {
		String largeGroup = fetcher.fetchTreeStructure(8875)
		largeGroup = largeGroup.trim()
		assert largeGroup.endsWith("</TREE>")
	}
	
	void tearDown() {
		fetcher = null
	}
	
}