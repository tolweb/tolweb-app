package org.tolweb.content.export

class ResponseFinderTest extends GroovyTestCase {
	def sampleInput1 = '''<?xml version="1.0"?><response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd"><taxon><dc:identifier>tol-node-id-121600</dc:identifier><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><dwc:ScientificName>Vombatus ursinus Shaw 1800</dwc:ScientificName><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><tol:node-data extinct="false" confidence="confident" phylesis="monophyletic" leaf="false" /><tol:othernames><tol:othername>Wombats Shaw 1800</tol:othername><tol:othername>Vombatus E. Geoffroy 1803</tol:othername></tol:othernames><dataObject><dc:identifier>tol-text-id-163086</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><tol:source-info tol:tol-native="true" /><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163087</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><tol:source-info tol:tol-native="true" /><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163088</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><tol:source-info tol:tol-native="true" /><dc:description xml:lang="en"></dc:description></dataObject></taxon></response>'''
	def expectedOutput1 = '''<?xml version="1.0"?><response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd">'''
	def expectedOutput1ProcText = '''<?xml version="1.0"?>'''
	def expectedOutput1RespText = '''<response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd">'''			
	def sampleInput2 = '''<?xml version="1.0"?>'''
	def sampleInput3 = '''
		<?xml version="1.0"?>
		<success xmlns="http://www.eol.org/transfer/content/0.1">
			<message license-class="NonCommercialWithModification">
				Sorry, requested content not available through this service.
			</message>
		</success>
	'''
	def sampleInput4 = '''
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
	def expectedOutput4 = '''<?xml version="1.0"?><response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd">'''
	def expectedOutput4ProcText = '''<?xml version="1.0"?>'''
	def expectedOutput4RespText = '''<response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd">'''
	def sampleInput5 = '''<taxon><dc:identifier>tol-node-id-121600</dc:identifier><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><dwc:ScientificName>Vombatus ursinus Shaw 1800</dwc:ScientificName><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><tol:node-data extinct="false" confidence="confident" phylesis="monophyletic" leaf="false" /><tol:othernames><tol:othername>Wombats Shaw 1800</tol:othername><tol:othername>Vombatus E. Geoffroy 1803</tol:othername></tol:othernames><dataObject><dc:identifier>tol-text-id-163086</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><tol:source-info tol:tol-native="true" /><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163087</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><tol:source-info tol:tol-native="true" /><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163088</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><tol:source-info tol:tol-native="true" /><dc:description xml:lang="en"></dc:description></dataObject></taxon>'''
	
	void test_finder_finds_response_element() {
		ResponseFinder finder = new ResponseFinder(sampleInput1)
		assertEquals expectedOutput1, "${finder.processingText}${finder.responseText}"
		assertEquals expectedOutput1, finder.documentHeading
		assertEquals expectedOutput1ProcText, finder.processingText
		assertEquals expectedOutput1RespText, finder.responseText
	}
	
	void test_finder_doesnt_find_element() {
		ResponseFinder finder = new ResponseFinder(sampleInput2)
		assertEquals "", finder.responseText
		assertEquals "", finder.documentHeading
		assertEquals "", finder.processingText
	}
	
	void test_finder_doesnt_find_element_multiline() {
		ResponseFinder finder = new ResponseFinder(sampleInput3)
		assertEquals "", finder.responseText
		assertEquals "", finder.documentHeading
		assertEquals "", finder.processingText		
	}
	
	void test_finder_find_response_element_multiline() {
		ResponseFinder finder = new ResponseFinder(sampleInput4)
		assertEquals expectedOutput4RespText, finder.responseText
		assertEquals expectedOutput4, finder.documentHeading
		assertEquals expectedOutput4ProcText, finder.processingText		
	}

	void test_finder_doesnt_find_any_taxon_elements() {
		ResponseFinder finder = new ResponseFinder(sampleInput5)
		assertEquals "", finder.responseText
		assertEquals "", finder.documentHeading
		assertEquals "", finder.processingText		
	}
	
	void test_finder_handles_empty_string() {
		ResponseFinder finder = new ResponseFinder("")
		assertEquals "", finder.responseText
		assertEquals "", finder.documentHeading
		assertEquals "", finder.processingText		
	}
	
	void test_finder_handles_null_argument() {
		ResponseFinder finder = new ResponseFinder(null)
		assertEquals "", finder.responseText
		assertEquals "", finder.documentHeading
		assertEquals "", finder.processingText		
	}
}