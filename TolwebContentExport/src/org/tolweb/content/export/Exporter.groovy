package org.tolweb.content.export

/**
 * Exports the content of the entire tree into a 
 * well-formed single string of XML. 
 * 
 * @author lenards 
 */
class Exporter { 
	static final Long LIFE_ON_EARTH = 1
	static final String NON_COMMERCIAL = 'nc'
	Fetcher fetcher
	Long rootNodeId
	String licenseType 
	
	/**
	 * Constructs an instance with the given host & services 
	 * information. 
	 * 
	 * If rootNodeId is not set, LIFE_ON_EARTH is assumed. 
	 */
	public Exporter(HostInfo hostInfo, ServicesInfo servInfo) {
		fetcher = new Fetcher(hostInfo: hostInfo, servicesInfo: servInfo)
		rootNodeId = LIFE_ON_EARTH
		licenseType = NON_COMMERCIAL
	}
	
	/**
	 * Fetches the tree structure and gets page content associated with 
	 * nodes that are under the proper license. 
	 * 
	 * @return a well-formed XML string of all valid content for the entire 
	 * tree of life
	 */
	String export() {
		def tree = new XmlParser().parseText(fetcher.fetchTreeStructure(rootNodeId))
		def nodes = tree.depthFirst().NODE.findAll { it['@HASPAGE'] == '1' }
		def docStart = ""
		def body = ""
		(0..<nodes.size()).each {
			def id = nodes[it].'@ID'
			String content = fetcher.fetchContent(new Long(id), licenseType)
			ResponseFinder respFinder = new ResponseFinder(content)
			TaxonFinder finder = new TaxonFinder(content)
			ContentFinder cfinder = new ContentFinder(content)
			if (cfinder.hasContent) { // if there's no export-worth content, skip it
				if (respFinder.documentHeading != "") { 
					docStart = respFinder.documentHeading
				}
				body += finder.taxonText ?: ""
			}
		}
		return "${docStart}${body}${ResponseFinder.CLOSING_TAG}"
	}
}