package org.tolweb.content.export

class ExporterTest extends AbstractApplicationTestContext {
	Exporter ex 
	
	def expectedMarsupilia = '''<?xml version="1.0"?><response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd"><taxon><dc:identifier>tol-node-id-16248</dc:identifier><dc:source>http://tolweb.org/Diprotodontia/16248</dc:source><dwc:ScientificName>Diprotodontia</dwc:ScientificName><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dataObject><dc:identifier>tol-text-id-163095</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Diprotodontia/16248</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163096</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Diprotodontia/16248</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163097</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Diprotodontia/16248</dc:source><dc:description xml:lang="en"></dc:description></dataObject></taxon><taxon><dc:identifier>tol-node-id-121589</dc:identifier><dc:source>http://tolweb.org/Vombatiformes/121589</dc:source><dwc:ScientificName>Vombatiformes Burnett 1830</dwc:ScientificName><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dataObject><dc:identifier>tol-text-id-163074</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatiformes/121589</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163075</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatiformes/121589</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163076</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Vombatiformes/121589</dc:source><dc:description xml:lang="en"></dc:description></dataObject></taxon><taxon><dc:identifier>tol-node-id-121599</dc:identifier><dc:source>http://tolweb.org/Vombatidae/121599</dc:source><dwc:ScientificName>Vombatidae Burnett 1829</dwc:ScientificName><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dataObject><dc:identifier>tol-text-id-163083</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatidae/121599</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163084</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatidae/121599</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163085</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Vombatidae/121599</dc:source><dc:description xml:lang="en"></dc:description></dataObject></taxon><taxon><dc:identifier>tol-node-id-121600</dc:identifier><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><dwc:ScientificName>Vombatus ursinus Shaw 1800</dwc:ScientificName><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dataObject><dc:identifier>tol-text-id-163086</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163087</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><dc:description xml:lang="en"></dc:description></dataObject><dataObject><dc:identifier>tol-text-id-163088</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2008-Aug-15 00:00:00</dcterms:created><dcterms:modified>2008-Aug-15 00:00:00</dcterms:modified><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Vombatus+ursinus/121600</dc:source><dc:description xml:lang="en"></dc:description></dataObject></taxon></response>'''
	def expectedOmma = '''<?xml version="1.0"?><response xmlns="http://www.eol.org/transfer/content/0.1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwc="http://rs.tdwg.org/dwc/dwcore/" xmlns:tol="http://tolweb.org/webservices/content/0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.eol.org/transfer/content/0.1 http://services.eol.org/schema/content_0_1.xsd"><taxon><dc:identifier>tol-node-id-9042</dc:identifier><dc:source>http://tolweb.org/Omma/9042</dc:source><dwc:ScientificName>Omma Newman 1839</dwc:ScientificName><dcterms:created>2006-Feb-10 00:00:00</dcterms:created><dataObject><dc:identifier>tol-text-id-27399</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2006-Feb-10 00:00:00</dcterms:created><dc:title xml:lang="en">Introduction</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Omma/9042</dc:source><dc:description xml:lang="en"></dc:description><reference>Lawrence, J. F. 1999. The Australian Ommatidae (Coleoptera: Archostemata): new species, larva and discussion of relationships. Invertebrate Taxonomy 13:369-390.</reference><reference>Ponomarenko, A. G. 1966. New beetles of the Family Cupedidae (Coleoptera) from Mesozoic deposits of Transbaikalia. Revue d'Entomologie de l'URSS 45:138-143.</reference><reference>Ponomarenko, A. G. 1997. New beetles of the Family Cupedidae from the Mesozoic of Mongolia. Ommatini, Mesocupedini, Priacmini. Paleontological Journal 31(4):389-399.</reference></dataObject><dataObject><dc:identifier>tol-text-id-27400</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2006-Feb-10 00:00:00</dcterms:created><dc:title xml:lang="en">Characteristics</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><audience>Expert users</audience><dc:source>http://tolweb.org/Omma/9042</dc:source><dc:description xml:lang="en"></dc:description><reference>Lawrence, J. F. 1999. The Australian Ommatidae (Coleoptera: Archostemata): new species, larva and discussion of relationships. Invertebrate Taxonomy 13:369-390.</reference><reference>Ponomarenko, A. G. 1966. New beetles of the Family Cupedidae (Coleoptera) from Mesozoic deposits of Transbaikalia. Revue d'Entomologie de l'URSS 45:138-143.</reference><reference>Ponomarenko, A. G. 1997. New beetles of the Family Cupedidae from the Mesozoic of Mongolia. Ommatini, Mesocupedini, Priacmini. Paleontological Journal 31(4):389-399.</reference></dataObject><dataObject><dc:identifier>tol-text-id-27401</dc:identifier><dataType>http://purl.org/dc/dcmitype/Text</dataType><mimeType>text/html</mimeType><dcterms:created>2006-Feb-10 00:00:00</dcterms:created><dc:title xml:lang="en">Discussion of Phylogenetic Relationships</dc:title><dc:language>en</dc:language><license>http://creativecommons.org/licenses/by/3.0/</license><dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/" /><audience>General public</audience><dc:source>http://tolweb.org/Omma/9042</dc:source><dc:description xml:lang="en"></dc:description><reference>Lawrence, J. F. 1999. The Australian Ommatidae (Coleoptera: Archostemata): new species, larva and discussion of relationships. Invertebrate Taxonomy 13:369-390.</reference><reference>Ponomarenko, A. G. 1966. New beetles of the Family Cupedidae (Coleoptera) from Mesozoic deposits of Transbaikalia. Revue d'Entomologie de l'URSS 45:138-143.</reference><reference>Ponomarenko, A. G. 1997. New beetles of the Family Cupedidae from the Mesozoic of Mongolia. Ommatini, Mesocupedini, Priacmini. Paleontological Journal 31(4):389-399.</reference></dataObject></taxon></response>'''
	
	void setUp() {
		HostInfo hi = (HostInfo) context.getBean("hostInfo")
		ServicesInfo si = (ServicesInfo) context.getBean("servicesInfo")		
		ex = new Exporter(hi, si)
	}

	String getMarsupiliaOutput() {
		expectedMarsupilia.trim().replaceAll("\n", "").replaceAll("\r", "")
	}
	
	void test_print() {
		//println getMarsupiliaOutput()
	}
	
	void test_initialization() {
		assert ex.fetcher
		assert ex.rootNodeId == new Long(1)
	}

	void test_export_logic_ensuring_only_nodes_with_pages() {
		ex.rootNodeId = new Long(16958)
		def tree = new XmlParser().parseText(ex.fetcher.fetchTreeStructure(ex.rootNodeId))
		println tree.depthFirst().NODE.getClass()
		def nodes = tree.depthFirst().NODE.findAll { it['@HASPAGE'] == '1' }
		def lst = []
		def docStart = ""
		def body = ""
		(0..<nodes.size()).each {
			def id = nodes[it].'@ID'
			 
			String content = ex.fetcher.fetchContent(new Long(id), ex.licenseType)
			if (content) {
				ResponseFinder respFinder = new ResponseFinder(content)
				TaxonFinder finder = new TaxonFinder(content)
				ContentFinder cfinder = new ContentFinder(content)
				if (cfinder.hasContent) {
					if (respFinder.documentHeading != "") { 
						docStart = respFinder.documentHeading
					}
					body += finder.taxonText ?: ""	
					lst.add(id)
				}
			}
		}
		println lst
		assert !lst.contains("133661")
	}
	
/*
	void test_small_fetch() {
		ex.rootNodeId = 9042
		def actual = ex.export()
		assertEquals expectedOmma, actual 
	}
	
	void test_medium_fetch() {
		ex.rootNodeId = 15994
		def actual = ex.export()
		assertEquals getMarsupiliaOutput(), actual		
	}
 */
	
	void off_test_large_fetch() {
		ex.rootNodeId = 15040
		def actual = ex.export()
		new File("mammalia.xml").withWriter { out ->
			out.writeLine(actual)
		}
	}
	
 	void test_mime_type() {
 		ex.rootNodeId = 133610
 		def actual = ex.export()
 		println actual
 		assert actual.indexOf("image/jpeg,image/pjpeg", 0) == -1
 		assert actual.indexOf("image/jpeg", 0) != -1
 	}
 
	void tearDown() {
		ex = null
	}
}