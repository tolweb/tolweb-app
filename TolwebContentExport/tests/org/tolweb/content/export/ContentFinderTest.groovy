package org.tolweb.content.export

class ContentFinderTest extends GroovyTestCase {
	void test_no_content_present() {
		ContentFinder cf = new ContentFinder(case_none)
		assert cf.hasContent == false
	}
	
	void test_content_is_reference() {
		ContentFinder cf = new ContentFinder(case_has_ref)
		assert cf.hasContent		
	}

	void test_content_is_dataobject() {
		ContentFinder cf = new ContentFinder(case_has_do)
		assert cf.hasContent		
	}	
	
	void test_content_is_description() {
		ContentFinder cf = new ContentFinder(case_has_desc)
		assert cf.hasContent		
	}	
	
	void test_real_input_with_content() {
		ContentFinder cf = new ContentFinder(case_real_input_with_content)
		assert cf.hasContent		
	}
	
	def case_none = '''
	    <taxon>
		    <dc:identifier>tol-node-id-104581</dc:identifier>
		    <dc:source>http://tolweb.org/Calyptocarpus/104581</dc:source>
		    <dwc:ScientificName>Calyptocarpus</dwc:ScientificName>
		    <dcterms:created>2009-Jan-11 00:00:00</dcterms:created>
		    <dcterms:modified>2009-Jan-11 00:00:00</dcterms:modified>
		</taxon>
	'''

	def case_has_ref = '''
	    <taxon>
		    <dc:identifier>tol-node-id-104581</dc:identifier>
		    <dc:source>http://tolweb.org/Calyptocarpus/104581</dc:source>
		    <dwc:ScientificName>Calyptocarpus</dwc:ScientificName>
		    <dcterms:created>2009-Jan-11 00:00:00</dcterms:created>
		    <dcterms:modified>2009-Jan-11 00:00:00</dcterms:modified>
			<reference>foo</reference>
		</taxon>
	'''
	
	def case_has_do = '''
	    <taxon>
		    <dc:identifier>tol-node-id-104581</dc:identifier>
		    <dc:source>http://tolweb.org/Calyptocarpus/104581</dc:source>
		    <dwc:ScientificName>Calyptocarpus</dwc:ScientificName>
		    <dcterms:created>2009-Jan-11 00:00:00</dcterms:created>
		    <dcterms:modified>2009-Jan-11 00:00:00</dcterms:modified>
			<dataObject>foo</dataObject>
		</taxon>
	'''

	def case_has_desc = '''
	    <taxon>
		    <dc:identifier>tol-node-id-104581</dc:identifier>
		    <dc:source>http://tolweb.org/Calyptocarpus/104581</dc:source>
		    <dwc:ScientificName>Calyptocarpus</dwc:ScientificName>
		    <dcterms:created>2009-Jan-11 00:00:00</dcterms:created>
		    <dcterms:modified>2009-Jan-11 00:00:00</dcterms:modified>
	        <dc:description xml:lang="en">&lt;p&gt;The Phytomelanin Cypsela Clade (PCC)
	        contains approximately 5400 species in 460 genera classified in 11 tribes. Most
	        species are endemic to the New World. They represent approximately half of the
	        species of sunflowers in the Western Hemisphere and nearly one quarter of all
	        species in the family. Most members of the group are annual or perennial herbs but
	        shrubs and even trees are also well represented, especially in the Neotropics. Most
	        species can be found in seasonally dry habitats. Spring ephemerals are important
	        components of the desert floras of North America, whereas shrubby and arborescent
	        members are mostly found in the extensive pine-oak forests of Mexico and scattered
	        throughout the Andean forests of tropical South America and central Brazil.
	        &lt;/p&gt;</dc:description>
	    </taxon>
	'''
	
	def case_real_input_with_content = '''
	    <taxon>
		    <dc:identifier>tol-node-id-22911</dc:identifier>
		    <dc:source>http://tolweb.org/Phytomelanin+Cypsela+Clade/22911</dc:source>
		    <dwc:ScientificName>Phytomelanin Cypsela Clade</dwc:ScientificName>
		    <dcterms:created>2008-Apr-4 00:00:00</dcterms:created>
		    <dcterms:modified>2008-Apr-4 00:00:00</dcterms:modified>
		    <reference>Panero, J. L. 2007. Compositae: key to the tribes of the Heliantheae alliance.
		        Pages 391-395 &amp;lt;em&amp;gt;in&amp;lt;/em&amp;gt; Families and
		        Genera of Vascular Plants, vol. VIII, Flowering Plants, Eudicots, Asterales. Kadereit,
		        J. W., C. Jeffrey (eds.), Springer-Verlag, Berlin.</reference>
		    <reference>Panero, J. L., and V. A. Funk. 2008. The value of sampling anomalous taxa in
		        phylogenetic studies: major clades of the Asteraceae revealed. Mol. Phylogenet. Evol.
		        47: 757-782.</reference>
		    <dataObject>
		        <dc:identifier>tol-text-id-162562</dc:identifier>
		        <dataType>http://purl.org/dc/dcmitype/Text</dataType>
		        <mimeType>text/html</mimeType>
		        <agent role="author" homepage="http://www.biosci.utexas.edu/ib/faculty/panero.htm">Jose
		            L. Panero</agent>
		        <dcterms:created>2008-Apr-4 00:00:00</dcterms:created>
		        <dcterms:modified>2008-Apr-4 00:00:00</dcterms:modified>
		        <dc:title xml:lang="en">Introduction</dc:title>
		        <dc:language>en</dc:language>
		        <license>http://creativecommons.org/licenses/by-nc/3.0/</license>
		        <dcterms:rightsHolder>&lt;a
		            href="http://www.biosci.utexas.edu/ib/faculty/panero.htm"&gt;Jose L.
		            Panero&lt;/a&gt;</dcterms:rightsHolder>
		        <dcterm:bibliographicCitation xmlns:dcterm="http://purl.org/dc/terms/">Panero, Jose
		            L.2008. Phytomelanin Cypsela Clade. Version 04 April 2008 (under
		            construction).http://tolweb.org/Phytomelanin_Cypsela_Clade/22911/2008.04.04 in The
		            Tree of Life Web Project, http://tolweb.org/</dcterm:bibliographicCitation>
		        <audience>General public</audience>
		        <audience>Expert users</audience>
		        <dc:source>http://tolweb.org/Phytomelanin+Cypsela+Clade/22911</dc:source>
		        <subject>http://rs.tdwg.org/ontology/voc/SPMInfoItems#GeneralDescription</subject>
		        <dc:description xml:lang="en">&lt;p&gt;The Phytomelanin Cypsela Clade (PCC)
		            contains approximately 5400 species in 460 genera classified in 11 tribes. Most
		            species are endemic to the New World. They represent approximately half of the
		            species of sunflowers in the Western Hemisphere and nearly one quarter of all
		            species in the family. Most members of the group are annual or perennial herbs but
		            shrubs and even trees are also well represented, especially in the Neotropics. Most
		            species can be found in seasonally dry habitats. Spring ephemerals are important
		            components of the desert floras of North America, whereas shrubby and arborescent
		            members are mostly found in the extensive pine-oak forests of Mexico and scattered
		            throughout the Andean forests of tropical South America and central Brazil.
		            &lt;/p&gt;</dc:description>
		    </dataObject>
		</taxon>
	'''
}
