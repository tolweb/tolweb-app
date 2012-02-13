package org.tolweb.misc;

import org.jdom.Element;
import org.tolweb.dao.ApplicationContextTestAbstract;

public class HtmlPreparerTest extends ApplicationContextTestAbstract {
	public HtmlPreparerTest(String name) {
		super(name);
	}
	
	public void testRun() {
		Element el = HtmlPreparer.parseHtmlList(TEST_INPUT.replace("\n", ""));
		System.out.println(el);
	}
	
/*
org.apache.xerces.dom.DocumentFragmentImpl
 org.apache.html.dom.HTMLUListElementImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
   org.apache.html.dom.HTMLUListElementImpl
    org.apache.html.dom.HTMLLIElementImpl
     org.apache.html.dom.HTMLAnchorElementImpl
      org.apache.xerces.dom.TextImpl
     org.apache.xerces.dom.TextImpl
    org.apache.html.dom.HTMLLIElementImpl
     org.apache.html.dom.HTMLAnchorElementImpl
      org.apache.xerces.dom.TextImpl
     org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
 */	
	
	private static final String TEST_INPUT = "<UL>" + 
			"\n" + 
			"<li><a href=\"http://www.biology.uiowa.edu/eu_tree/\">Eu-Tree</a>.  Assembling the Tree of Eukaryotic Diversity.\n" + 
			"\n" + 
			"<LI><a href=\"http://www.bio.usyd.edu.au/Protsvil/\">Protsville</a>. Protist Research Laboratory, University of Sydney, Australia.\n" + 
			"\n" + 
			"<LI><a href=\"http://protist.i.hosei.ac.jp/Protist_menuE.html\">Protist Information Server</a>. Japan Science and Technology Corporation.\n" + 
			"\n" + 
			"<ul>\n" + 
			"\n" + 
			"<LI><a href=\"http://protist.i.hosei.ac.jp/Servers/ProtistologistsE.html\">Protistologist\'s Home Pages</a>.\n" + 
			"\n" + 
			"<LI><a href=\"http://protist.i.hosei.ac.jp/PDB/Images/menuE.html\">Digital Specimen Archives</a>.\n" + 
			"\n" + 
			"</ul>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"<LI><a href=\"http://www.ucmp.berkeley.edu/alllife/eukaryotasy.html\">Eukaryota: Systematics</a>. Museum of Paleontology, University of California, Berkeley, USA. \n" + 
			"\n" + 
			"<li><a href=\"http://www.actionbioscience.org/evolution/dacks.html\">Malaria, Algae, Amoeba and You: \n" + 
			"\n" + 
			"Unravelling Eukaryotic Relationships</a>. Joel B. Dacks. ActionBioScience.org</li>\n" + 
			"\n" + 
			"<LI><a href=\"http://astrobiology.mbl.edu/priorwork-bioprospect.html\">Exploring Early Eukaryotic Evolution: Diversity and Relationships Among Novel Deep-Branching Lineages </a>.  Virginia Edgcomb, Andrew Roger, Alastair G.B. Simpson, Jeffrey Silberman and Mitchell Sogin, Marine Biological Laboratory, Woods Hole, USA.\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"<li><a href=\"http://serc.carleton.edu/microbelife/\">Microbial Life - Educational Resources</a>. Teaching and learning about the diversity, ecology and evolution of the microbial world; discover the connections between microbial life, the history of the earth and our dependence on micro-organisms. \n" + 
			"\n" + 
			"</li>\n" + 
			"\n" + 
			"<LI><a href=\"http://www.nhm.ac.uk/zoology/extreme.html\">Eukaryotes in extreme environments</a>.  Dave Roberts, the Natural History Museum, London, UK.\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"<li><a href=\"http://www.biosci.ki.se/groups/tbu/homeo.html\">The Homeobox Page</a>.   Thomas R. B?rglin\'s page about the homeobox genes which play important roles in the development of multicellular organisms.</li>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"<LI><a href=\"http://megasun.bch.umontreal.ca/protists/\">Protist Image Data</a>. Molecular Evolution and Organelle Genomics program at the University of Montreal, Canada.\n" + 
			"\n" + 
			"</UL>";
}
