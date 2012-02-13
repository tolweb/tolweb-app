/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.Calendar;
import java.util.List;

import org.tolweb.hibernate.MappedPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PageDAOTest extends ApplicationContextTestAbstract {
    private PageDAO dao;
    private PageDAO publicPageDAO;
    
    public PageDAOTest(String name) {
        super(name);
        dao = (PageDAO) context.getBean("workingPageDAO");
        publicPageDAO = (PageDAO) context.getBean("publicPageDAO");
    }
    
    public void testLastEdited() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR, 2006);
    	calendar.set(Calendar.MONTH, 3);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);    	
    	List<Object[]> lastEditedPages = dao.getContributorsDatesAndPagesRecentlyChanged(43L, calendar.getTime());
    	for (Object[] objects : lastEditedPages) {
			System.out.println("contributor: " + objects[0] + ", date: " + objects[1] + " page: " + objects[2]);
		}
    }    
    
    public void testMappedPageFetch() {
    	MappedPage mpage = publicPageDAO.getPageWithId(1303L);
    	assertNotNull(mpage);
    }
 /*   
    public void testDescendantPageInfo() {
    	List<MappedPage> descendants = publicPageDAO.getDescendantPagesWithInfo(1502L);
    	for (MappedPage page : descendants) {
			System.out.println("cladename: " + page.getGroupName() + "\n status: " + page.getStatus() + "\n authors: " + getAuthorsString(page.getContributors()) + 
					"\n last edited: " + getLastEditedDate(page) + "\n num title illustrations: " + page.getTitleIllustrations().size());
			System.out.println("-----------------------------------------------\n");
		}
    } 
   */ 
    

    /*    
    private Date getLastEditedDate(MappedPage page) {
    	if (page.getContentChangedDate() != null) {
    		return page.getContentChangedDate();
    	} else {
    		return page.getFirstOnlineDate();
    	}
    }
    
    @SuppressWarnings("unchecked")
    private String getAuthorsString(Collection contributors) {
	    ArrayList names = new ArrayList();
		Iterator it = contributors.iterator();
		while (it.hasNext()) {
		    PageContributor contr = (PageContributor) it.next();
		    if (contr.getIsAuthor()) {
		        names.add(contr.getContributor().getDisplayName());
		    }
		}
		return StringUtils.returnCommaAndJoinedString(names);	    
	}    	
    public void testGetPageWithId() {
        MappedPage page = dao.getPageWithId(new Long(1));
        assertNotNull(page.getMappedNode());
        assertEquals(page.getMappedNode().getNodeId(), new Long(1));
        assertEquals(page.getPageId(), new Long(1));
        assertEquals(page.getGenBank(), "");
        assertEquals(page.getTreeBase(), "");
        //assertEquals(page.getPostTreeText(), "The rooting of the Tree of Life, and the relationships of the major lineages, are controversial.  The monophyly of Archaea is uncertain, and recent evidence for ancient lateral transfers of genes indicates that a highly complex model is needed to adequately represent the phylogenetic relationships among the major lineages of Life.  We hope to provide a comprehensive discussion of these issues on this page soon. For the time being, please refer to the papers listed in the <a href=\"#TOC3\">References</a> section. ");
        assertFalse(page.getPrintImageData());
        assertTrue(page.getPrintCustomCaption());
        //assertEquals(page.getReferences(), "Aravind, L., R. L. Tatusov, Y. I. Wolf, D. R. Walker, and E. V. Koonin.  1998.  Evidence for massive gene exchange between archaeal and bacterial hyperthermophiles.  Trends in Genetics 14:442-444.  Baldauf, S. L., J. D. Palmer, and W. F. Doolittle.  1996.  The root of the universal tree and the origin of eukaryotes based on elongation factor phylogeny.  Proceedings of the National Academy of Sciences of the United States of America 93:7749-7754.  Benachenhou, L. N., P. Forterre and B. Labedan. 1993. Evolution of glutamate dehydrogenase genes: Evidence for two paralogous protein families and unusual branching patterns of the archaebacteria in the universal tree of life. Journal Of Molecular Evolution 36:335-346.  Brinkmann, H. and H. Phillippe.  1999.  Archaea sister group of bacteria?  Indications from Tree Reconstruction Artifacts from ancient Phylogenies.  Molecular Biology and Evolution 16:817-825.  Brocks, J. J., G. A. Logan, R. Buick, and R. E. Summons.  1999.  Archean molecular fossils and the early rise of eukaryotes.  Science 285:1033-1036.  Brown, J. R.  2001.  Genomic and phylogenetic perspectives on the evolution of prokaryotes.  Systematic Biology 50:497-512.  Brown, J. R. and W. F. Doolittle.  1995. Root of the universal tree of life based on ancient aminoacyl-tRNA synthetase gene duplications.  Proceedings of the National Academy of Sciences of the United States of America 92:2441-2445.  Brown, J. R. and W. F. Doolittle.  1997.  Archaea and the prokaryote-to-eukaryote transition.  Microbiology and Molecular Biology Reviews 61:456-502.  Caetano-Anolles, G.  2002.  Evolved RNA secondary structure and the rooting of the universal tree of life.  Journal of Molecular Evolution 54: 333-345.  Cammarano, P., P. Palm, R. Creti, E. Ceccarelli, A. M. Sanangelantoni, and O. Tiboni. 1992. Early evolutionary relationships among known life forms inferred from elongation factor EF-2/EF-G sequences: Phylogenetic coherence and structure of the Archaeal domain. Journal Of Molecular Evolution 34:396-405.  Cammarano, P., R. Creti, A. M. Sanangelantoni, and P. Palm. 1999. The archaean monophyly issue: a phylogeny of translational elongation factor G(2) sequences inferred from an optimized selection of alignment positions. Journal Of Molecular Evolution 49:524-537.  Creti, R., E. Ceccarelli, M. Bocchetta, A. M. Sanangelantoni, O. Tiboni, P. Palm and P. Cammarano. 1994. Evolution of translational elongation factor (EF) sequences: Reliability of global phylogenies inferred from EF-1-alpha(Tu) and EF-2(G) proteins. Proceedings of the National Academy of Sciences of the United States of America 91:3255-3259.  Des Marais, D. J.  1999.  Astrobiology: Exploring the origins, evolution, and distribution of life in the universe.  Annual Review of Ecology and Systematics 30:397-420.   Doolittle, W. F.  1998.  You are what you eat: a gene transfer ratchet could account for bacterial genes in eukaryotic nuclear genomes.  Trends in Genetics 14:307-311.  Doolittle, W. F.  1999. Phylogenetic classification and the universal tree.  Science 284:2124-2128.  Doolittle, W. F.  1999. Lateral genomics.  Trends in Biochemical Sciences 24: M5-M8.  Doolittle, W. F.  2000.  Uprooting the tree of life.  Scientific American 282:90-95.  Doolittle, W. F. and J. R. Brown.  1994.  Tempo, mode, the progenote, and the universal root.  Proceedings of the National Academy of Sciences of the United States of America 91:6721-6728.  Feng, D.-F., G. Cho, and R.F. Doolittle.  1997.  Determining divergence times with a protein clock: Update and reevaluation.  Proceedings of the National Academy of Sciences of the United States of America 94:13028-13033.  Forterre, P.  2001.  Genomics and early cellular evolution. The origin of the DNA world.  Comptes Rendus de l'Academie des Sciences Serie III-Sciences de la Vie 324:1067-1076.  Forterre, P. and H. Philippe.  1999.  Where is the root or the universal tree of life?  BioEssays 21:871-879.   Gogarten, J. P., E. Hilario, and L. Olendzenski.  1996.  Gene duplications and horizontal gene transfer during early evolution.  Pages 267-292 in Evolution of Microbial Life (D. McL. Roberts, P. Sharp, G. Alderson, and M. Collins, eds.)  Symposium 54.  Society for General Microbiology.  Cambridge University Press, Cambridge.  Gogarten, J. P. and L. Taiz. 1992. Evolution of proton pumping ATPases: Rooting the tree of life. Photosynthesis Research 33:137-146.  Golding, G.B. and R.S. Gupta.  1995.  Protein-based phylogenies support a chimeric origin for the eukaryotic genome.  Molecular Biology and Evolution 12:1-6.  Gouy, M. and W.-H. Li. 1989. Phylogenetic analysis based on rRNA sequences supports the archaebacterial rather than the eocyte tree. Nature 339:145-147.  Gouy, M. and W.-H. Li. 1990. Archaebacterial or eocyte tree? Nature 343:419.  Gray, M. W., G. Burger, and B. F. Lang.  1999.  Mitochondrial evolution.  Science 283:1476-1481.  Gribaldo, S. and P. Cammarano.  1998.  The root of the universal tree of life inferred from anciently duplicated genes encoding components of the protein-targeting machinery.  Journal of Molecular Evolution 47:508-516.  Gupta, R. S.  1998.  Protein phylogenies and signature sequences: A reappraisal of evolutionary relationships among archaebacteria, eubacteria, and eukaryotes.  Microbiology and Molecular Biology Reviews 62:1435-1491.   Gupta, R. S.  1998.  What are archaebacteria: Life's third domain or monoderm prokaryotes related to Gram-positive bacteria? A new proposal for the classification of prokaryotic organisms.  Molecular Microbiology 29:695-707.   Gupta, R. S. and G. B. Golding. 1993. Evolution of HSP70 gene and its implications regarding relationships between archaebacteria, eubacteria, and eukaryotes. Journal of Molecular Evolution 37:573-582.  Hilario, E. and J. P. Gogarten. 1993. Horizontal transfer of ATPase genes: The tree of life becomes a net of life. Biosystems 31:111-119.  Iwabe, N., K.-I. Kuma, M. Hagesawa, S. Osawa, T. Miyata.  1989. Evolutionary relationship of archaebacteria, eubacteria, and eukaryotes inferred from phylogenetic trees of duplicated genes.  Proceedings of the Natural Academy of Sciences (USA) 86:9355-9359.  Jeffares, D. C., A. M. Poole, and D. Penny.  1998.  Relics from the RNA world.  Journal of Molecular Evolution 46:18-36.  Kandler, O. 1994. Cell wall biochemistry and three-domain concept of life. Systematic and Applied Microbiology 16:501-509.  Katz, L. A.  1998.  Changing perspectives on the origin of eukaryotes.  Trends in Ecology and Evolution 13:493-497.  Katz, L. A.  1999.  The tangled web:  gene genealogies and the origin of eukaryotes.  Am. Nat. 154 (suppl.):S137-S145.  Koonin, E. V., A. R. Mushegian, M. Y. Galperin, and D. R. Walker.  1997.  Comparison of archaeal and bacterial genomes:  computer analysis of protein sequences predicts novel functions and suggests a chimeric origin for the archaea.  Molecular Microbiology 25:619-637.  Kyrpides, N. C. and G. J. Olsen.  1999.  Archaeal and bacterial hyperthermophiles: horizontal gene exchange or common ancestry?  Trends in Genetics 15:298-299.  Lake, J. A. 1990. Archaebacterial or eocyte tree? Nature 343:418-419.  Lake, J. A., M. W. Clark, E. Hendeson, S. P. Fay, M. Oakes, A. Scheinman, J. P. Thornber and R. A. Mah. 1985. Eubacteria, halobacteria and the origin of photosynthesis: The photocytes. Proceedings of the National Academy of Sciences (USA) 82:3716-3720.  Lake, J.A., E. Henderson, M. Oakes, M.W. Clark. 1984.  Eocytes: a new ribosome structure indicates a kingdom with close relationship to eukaryotes.  Proceedings of the National Academy of Sciences (USA) 81:3786-3790.  Lake, J. A. and M. C. Rivera.  1996.  The prokaryotic ancestry of eukaryotes.  Pages 87-108 in Evolution of Microbial Life (D. McL. Roberts, P. Sharp, G. Alderson, and M. Collins, eds.)  Symposium 54.  Society for General Microbiology.  Cambridge University Press, Cambridge.  Lawson, F. S., R. L. Charlebois, and J.-A. R. Dillon.  1996.  Phylogenetic analysis of carbamoylphosphate synthetase genes: complex evolutionary history includes an internal duplication within a gene which can root the Tree of Life.  Molecular Biology and Evolution 13:970-977.  Liao, D. and P. P. Dennis. 1994. Molecular phylogenies based on ribosomal protein L11, L1, L10, and L12 sequences. Journal of Molecular Evolution 38:405-419.  Lopez, P., P. Forterre, and H. Philippe.  1999.  The root of the tree of life in the light of the covarian model.  Journal of Molecular Evolution 49:496-508.  Margulis, L.  1996.  Archaeal-eubacterial mergers in the origin of Eukarya: phylogenetic classification of life.   Proceedings of the Natural Academy of Sciences (USA) 92:1071-1076.  Martin, W.  1999.  Mosaic bacterial chromosomes: a challenge on route to a tree of genomes.  BioEssays 21:99-104.  Martin W. and M. M&uuml;ller.  1998.  The hydrogen hypothesis for the first eukaryote.  Nature 392:37-41.  McClendon, J. H.  1999.  The origin of life.  Earth-Science Reviews 47:71-93.  Moreira, D. and P. Lopez-Garcia.  1998.  Symbiosis between methanogenic archaea and delta-proteobacteria as the origin of eukaryotes: the syntrophic hypothesis.  Journal of Molecular Evolution 47:517-530.  Nealson, K. H. and P. G. Conrad.  1999.  Life: past, present and future.  Philosophical Transactions of the Royal Society of London Series B 354:1923-1939.  Pennisi, E.  1998.  Genome data shake the tree of life.  Science 280:672-674.   Pennisi, E.  1999.  Is it time to uproot the tree of life?  Science 284:1305-1307.  Penny, D. and A. Poole.  1999.  The nature of the last universal common ancestor.  Current Opinion in Genetics and Development 9:672-677.  Philippe, H. and P. Forterre.  1999.  The rooting of the universal tree of life is not reliable.  Journal of Molecular Evolution 49:509-523.   Poole, A., D. Jeffares, and D. Penny.  Early evolution: prokaryotes, the new kids on the block.  BioEssays 21:880-889.  Rasmussen, B.  2000.  Filamentous microfossils in a 3,235-million-year-old volcanogenic massive sulphide deposit.  Nature 405:676-679.  Reysenbach1, A. L. and E. Shock.  2002.  Merging genomes with geochemistry in hydrothermal ecosystems.  Science 296:1077-1082.  Ribeiro, S. and G. B. Golding. 1998.  The mosaic nature of the eukaryotic nucleus.  Molecular Biology and Evolution 15:779-788.  Rivera, M. C., R. Jain, J. E. Moore, and J. A. Lake.  1998.  Genomic evidence for two functionally distinct gene classes.   Proceedings of the National Academy of Sciences (USA) 95:6239-6244.  Syvanen, M. and C. I. Kado (eds.)  1998.  Horizontal Gene Transfer.  Chapman & Hall, London.  Tourasse, N. J. and M. Gouy.  1999.  Accounting for evolutionary rate variation among sequence sites consistently changes universal phylogenies deduced from rRNA and protein-coding genes.  Molecular Phylogenetics and Evolution 13:159-168.  Vellai, T. and G. Vida.  1999.  The origin of eukaryotes: the difference between prokaryotic and eukaryotic cells.  Proceedings of the Royal Society of London Series B 266:1571-1577.  Woese, C.  1998.  The universal ancestor.  Proceedings of the National Academy of Sciences (USA) 95:6854-6859.  Woese, C. R., O. Kandler, and M. L. Wheelis.  1990.  Towards a natural system of organisms: proposal for the domains Archaea, Bacteria, and Eucarya.  Proceedings of the National Academy of Sciences (USA) 87:4576-4579.  Wolf, Y. I.,  L. Aravind, N. V. Grishin, and E. V. Koonin.  1999.  Evolution of aminoacyl-tRNA synthetases: analysis of unique domain architectures and phylogenetic trees reveals a complex history of horizontal gene transfer events.  Genome Reserarch 9:689-710.  Wolters, J. and V. A. Erdmann. 1986. Cladistic analysis of 5S rRNA and 16S rRNA secondary and primary structure -- the evolution of eukaryotes and their relation to Archaebacteria. Journal of Molecular Evolution 24:152-166.");
        assertEquals(page.getGroupName(), "Life on Earth");
        assertEquals(page.getTitle(), "Life on Earth");
        assertEquals(page.getTextSections().size(), 1);
        MappedTextSection section = (MappedTextSection) page.getTextSections().iterator().next();
        assertEquals(section.getHeading(), "Discussion of Phylogenetic Relationships");
        assertEquals(section.getOrder(), 2);
    }
    
    public void testTextSectionOrder() {
        MappedPage page = dao.getPageWithId(new Long(3));
        assertEquals(page.getTextSections().size(), 4);
        Iterator it = page.getTextSections().iterator();
        MappedTextSection section = (MappedTextSection) it.next();
        assertEquals(section.getHeading(), "Introduction");
        section = (MappedTextSection) it.next();
        assertEquals(section.getHeading(), "Characteristics");
        section = (MappedTextSection) it.next();
        assertEquals(section.getHeading(), "Crenarchaeota aren't just for microbiologists...");
        section = (MappedTextSection) it.next();
        assertEquals(section.getHeading(), "Discussion of Phylogenetic Relationships");
    }
    
    public void testTitleIllustrationOrder() {
        MappedPage life = dao.getPageWithId(new Long(1));
        assertEquals(life.getTitleIllustrations().size(), 2);
        Iterator it = life.getTitleIllustrations().iterator();
        TitleIllustration pic = (TitleIllustration) it.next();
        assertEquals(pic.getVersionId(), new Long(3685));
        pic = (TitleIllustration) it.next();
        assertEquals(pic.getVersionId(), new Long(714));
    }
    
    public void testGetPageForNode() {
        MappedNode life = (MappedNode) nodeDao.findNodesExactlyNamed("Life On Earth").get(0);
        MappedNode euks = (MappedNode) nodeDao.findNodesExactlyNamed("Eukaryotes").get(0);
        boolean hasPage = dao.getNodeHasPage(life);
        assertTrue(hasPage);
        hasPage = dao.getNodeHasPage(euks);
        assertTrue(hasPage);
    }
    
    public void testGetChildPages() {
        MappedPage page = (MappedPage) dao.getPageWithId(new Long(1));
        List childPages = dao.getChildPages(page);
        assertEquals(childPages.size(), 2);
        Iterator it = childPages.iterator();

        MappedPage p0 = (MappedPage) childPages.get(0);
        assertEquals(p0.getGroupName(), "Eukaryotes");
        
        MappedPage p1 = (MappedPage) childPages.get(1);
        assertEquals(p1.getGroupName(), "Viruses");
    }
    
    public void testGetAncestors() {
        MappedPage eukaryotes = (MappedPage) dao.getPageWithId(new Long(276));
        List ancestors = dao.getAncestorPageNames(eukaryotes.getPageId());
        assertEquals(ancestors.size(), 1);
    }
    
    public void testPageContributors() {
        MappedPage life = dao.getPageWithId(new Long(3));
        System.out.println("life has: " + life.getContributors().size() + " contributors.");
        MappedNode euks = (MappedNode) nodeDao.findNodesExactlyNamed("Eukaryotes").get(0);
        MappedPage euksPage = dao.getPageForNode(euks);
        assertEquals(euksPage.getContributors().size(), 2);
        Contributor contr = ((PageContributor) euksPage.getContributors().iterator().next()).getContributor();
        assertEquals(contr.getName(), "David J. Patterson");
    }
    
    public void testGetNodeForPageNodeIsOn() {
        MappedNode eukaryotes = nodeDao.getNodeWithId(new Long(3));
        MappedNode shouldBeLife = dao.getNodeForPageNodeIsOn(eukaryotes);
        assertEquals(shouldBeLife.getNodeId(), new Long(1));
    }*/
}
