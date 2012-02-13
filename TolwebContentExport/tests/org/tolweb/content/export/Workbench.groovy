package org.tolweb.content.export

import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory
import java.util.regex.Matcher
import java.util.regex.Pattern

class Workbench extends AbstractApplicationTestContext {
	Fetcher fetcher
	
	def sampleInput = '''
		<?xml version="1.0"?>
		<response xmlns="http://www.eol.org/transfer/content/0.1" 
				xmlns:dc="http://purl.org/dc/elements/1.1/" 
				xmlns:dcterms="http://purl.org/dc/terms/" 
				xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" 
				xmlns:tol="http://tolweb.org/webservices/content/0.1" 
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
				xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd">
			<taxon>
				<dc:identifier>tol-node-id-121599</dc:identifier>
				<dc:source>http://tolweb.org/Vombatidae/121599</dc:source>
				<dwc:ScientificName>Vombatidae Burnett 1829</dwc:ScientificName>
				<dcterms:created>2008-Aug-15 00:00:00</dcterms:created>
				<dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified>
				<tol:node-data extinct="false" confidence="confident" phylesis="monophyletic" leaf="false" />
				<dataObject>
					<dc:identifier>tol-text-id-163083</dc:identifier>
					<dataType>http://purl.org/dc/dcmitype/Text</dataType>
					<mimeType>text/html</mimeType>
					<dcterms:created>2008-Aug-15 00:00:00</dcterms:created>
					<dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified>
					<dc:title xml:lang="en">Introduction</dc:title>
					<dc:language>en</dc:language>
					<license>http://creativecommons.org/licenses/by/3.0/</license>
					<dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" />
					<audience>General public</audience>
					<audience>Expert users</audience>
					<dc:source>http://tolweb.org/Vombatidae/121599</dc:source>
					<tol:source-info tol:tol-native="true" />
					<dc:description xml:lang="en"></dc:description>
				</dataObject>
				<dataObject>
					<dc:identifier>tol-text-id-163084</dc:identifier>
					<dataType>http://purl.org/dc/dcmitype/Text</dataType>
					<mimeType>text/html</mimeType>
					<dcterms:created>2008-Aug-15 00:00:00</dcterms:created>
					<dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified>
					<dc:title xml:lang="en">Characteristics</dc:title>
					<dc:language>en</dc:language>
					<license>http://creativecommons.org/licenses/by/3.0/</license>
					<dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" />
					<audience>General public</audience>
					<audience>Expert users</audience>
					<dc:source>http://tolweb.org/Vombatidae/121599</dc:source>
					<tol:source-info tol:tol-native="true" />
					<dc:description xml:lang="en"></dc:description>
				</dataObject>
				<dataObject>
					<dc:identifier>tol-text-id-163085</dc:identifier>
					<dataType>http://purl.org/dc/dcmitype/Text</dataType>
					<mimeType>text/html</mimeType>
					<dcterms:created>2008-Aug-15 00:00:00</dcterms:created>
					<dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified>
					<dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title>
					<dc:language>en</dc:language>
					<license>http://creativecommons.org/licenses/by/3.0/</license>
					<dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" />
					<audience>General public</audience>
					<dc:source>http://tolweb.org/Vombatidae/121599</dc:source>
					<tol:source-info tol:tol-native="true" />
					<dc:description xml:lang="en"></dc:description>
				</dataObject>
			</taxon>
		</response>
	'''
	def expectedOutput = '''<?xml version="1.0"?><response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd">'''
	
	
	void setUp() {
		HostInfo hi = (HostInfo) context.getBean("hostInfo")
		ServicesInfo si = (ServicesInfo) context.getBean("servicesInfo")
		fetcher = new Fetcher(hostInfo: hi, servicesInfo: si)
	}

	void test_response_element_pluck() {
		def rootNodeId = 15994
		
		def tree = new XmlParser().parseText(fetcher.fetchTreeStructure(rootNodeId))
		def nodes = tree.depthFirst().NODE.findAll { it['@HASPAGE'] == '1' }
		
		def docStart = ""
		def body = ""
		
		(0..<nodes.size()).each {
			def id = nodes[it].'@ID'
			String content = fetcher.fetchContent(new Long(id), 'nc')
			ResponseFinder respFinder = new ResponseFinder(content)
			TaxonFinder finder = new TaxonFinder(content)
			
			if (respFinder.documentHeading != "") { 
				docStart = respFinder.documentHeading
			}
			body += finder.taxonText ?: ""
		}
		println "${docStart}${body}${ResponseFinder.CLOSING_TAG}"
	}

	void tearDown() {
		fetcher = null
	}
}