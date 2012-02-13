/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.dao;

import java.sql.*;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.AccessoryPageContributor;
import org.tolweb.treegrow.page.InternetLink;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AccessoryPageDAOTest extends ApplicationContextTestAbstract {
	private AccessoryPageDAO dao;
	private AccessoryPageDAO publicDao;
	private NodeDAO nodeDao;
	private ContributorDAO contributorDao;
	private AccessoryPagePusher pusher;
	private Contributor david;
    
	public AccessoryPageDAOTest(String name) {
		super(name);
		dao = (AccessoryPageDAO) context.getBean("workingAccessoryPageDAO");
		publicDao = (AccessoryPageDAO) context.getBean("publicAccessoryPageDAO");
		nodeDao = (NodeDAO) context.getBean("workingNodeDAO");
		contributorDao = (ContributorDAO) context.getBean("contributorDAO");
		pusher = (AccessoryPagePusher) context.getBean("accessoryPagePusher");
		david = contributorDao.getContributorWithEmail("beetle@ag.arizona.edu");
java.io.PrintWriter w =
			 new java.io.PrintWriter
			 (new java.io.OutputStreamWriter(System.out));
		 DriverManager.setLogWriter(w);     
	}

	/*public void testFindAccessoryPage() {
		MappedAccessoryPage accPage = dao.getAccessoryPageWithId(1);
		assertEquals(accPage.getAccessoryPageId(), new Long(1));
		assertEquals(accPage.getPageTitle(), "List of Carabid Tribes");
		assertEquals(accPage.getMenu(), "List of Carabid Tribes");
		assertEquals(accPage.getStatus(), "Under Construction");
		String text = "<P> Go to: <a href=\"/tree?group=Carabidae&contgroup=Adephaga\">Carabidae</a> <a href=\"/tree?group=Paussinae&contgroup=Carabidae\">Paussinae</a>            <a href=\"/tree?group=Nebriitae&contgroup=Carabidae\">Nebriitae</a> <a href=\"/tree?group=Carabitae&contgroup=Carabidae\">Carabitae</a>            <a href=\"/tree?group=Cicindelitae&contgroup=Carabidae\">Cicindelitae</a> <a href=\"/tree?group=Carabidae_Conjunctae&contgroup=Carabidae\">Carabidae            Conjunctae</a> <a href=\"/tree?group=Harpalinae&contgroup=Carabidae_Conjunctae\">Harpalinae</a> <a href=\"/tree?group=Lebiomorpha&contgroup=Harpalinae\">Lebiomorpha</a>            <a href=\"/tree?group=Brachinitae&contgroup=Carabidae_Conjunctae\">Brachinitae</a>          <HR>                  This is an alphabetical list of different carabid tribes         <P>          <UL>           <LI> <a href=\"/tree?group=Amarotypini&contgroup=Carabidae\">Amarotypini</a>            <LI> <a href=\"/tree?group=Amblytelini&contgroup=Carabidae_Conjunctae\">Amblytelini</a>            <LI> <a href=\"/tree?group=Amorphomerini&contgroup=Harpalinae\">Amorphomerini</a>            <LI> <a href=\"/tree?group=Anthiini&contgroup=Harpalinae\">Anthiini</a>            <LI> <a href=\"/tree?group=Apotomini&contgroup=Carabidae_Conjunctae\">Apotomini</a>            <LI> <a href=\"/tree?group=Bascanini&contgroup=Harpalinae\">Bascanini</a>            <LI> <a href=\"/tree?group=Bembidiini&contgroup=Carabidae_Conjunctae\">Bembidiini</a>            <LI> <a href=\"/tree?group=Brachinini&contgroup=Brachinitae\">Brachinini</a>            <LI> <a href=\"/tree?group=Broscini&contgroup=Carabidae_Conjunctae\">Broscini</a>            <LI> <a href=\"/tree?group=Calophaenini&contgroup=Lebiomorpha\">Calophaenini</a>            <LI> <a href=\"/tree?group=Carabini&contgroup=Carabitae\">Carabini</a>            <LI> <a href=\"/tree?group=Catapieseini&contgroup=Harpalinae\">Catapieseini</a>            <LI> <a href=\"/tree?group=Ceroglossini&contgroup=Carabitae\">Ceroglossini</a>            <LI> <a href=\"/tree?group=Chaetogenyini&contgroup=Harpalinae\">Chaetogenyini</a>            <LI> <a href=\"/tree?group=Chlaeniini&contgroup=Harpalinae\">Chlaeniini</a>            <LI> <a href=\"/tree?group=Cicindelini&contgroup=Cicindelitae\">Cicindelini</a>            <LI> <a href=\"/tree?group=Cicindini&contgroup=Carabidae\">Cicindini</a>            <LI> <a href=\"/tree?group=Clivinini&contgroup=Carabidae\">Clivinini</a>            <LI> <a href=\"/tree?group=Cnemalobini&contgroup=Harpalinae\">Cnemalobini</a>            <LI> <a href=\"/tree?group=Collyridini&contgroup=Cicindelitae\">Collyridini</a>            <LI> <a href=\"/tree?group=Crepidogastrini&contgroup=Brachinitae\">Crepidogastrini</a>            <LI> <a href=\"/tree?group=Ctenodactylini&contgroup=Harpalinae\">Ctenodactylini</a>            <LI> <a href=\"/tree?group=Ctenostomatini&contgroup=Cicindelitae\">Ctenostomatini</a>            <LI> <a href=\"/tree?group=Cuneipectini&contgroup=Harpalinae\">Cuneipectini</a>            <LI> <a href=\"/tree?group=Cychrini&contgroup=Carabitae\">Cychrini</a>            <LI> <a href=\"/tree?group=Cyclosomini&contgroup=Lebiomorpha\">Cyclosomini</a>            <LI> <a href=\"/tree?group=Cymbionotini&contgroup=Carabidae_Conjunctae\">Cymbionotini</a>            <LI> <a href=\"/tree?group=Dryptini&contgroup=Harpalinae\">Dryptini</a>            <LI> <a href=\"/tree?group=Elaphrini&contgroup=Carabidae\">Elaphrini</a>            <LI> <a href=\"/tree?group=Galeritini&contgroup=Harpalinae\">Galeritini</a>            <LI> <a href=\"/tree?group=Gehringiini&contgroup=Carabidae\">Gehringiini</a>            <LI> <a href=\"/tree?group=Graphipterini&contgroup=Lebiomorpha\">Graphipterini</a>            <LI> <a href=\"/tree?group=Harpalini&contgroup=Harpalinae\">Harpalini</a>            <LI> <a href=\"/tree?group=Helluonini&contgroup=Harpalinae\">Helluonini</a>            <LI> <a href=\"/tree?group=Hexagoniini&contgroup=Harpalinae\">Hexagoniini</a>            <LI> <a href=\"/tree?group=Hiletini&contgroup=Carabidae\">Hiletini</a>            <LI> <a href=\"/tree?group=Idiomorphini&contgroup=Harpalinae\">Idiomorphini</a>            <LI> <a href=\"/tree?group=Lachnophorini&contgroup=Lebiomorpha\">Lachnophorini</a>            <LI> <a href=\"/tree?group=Lebiini&contgroup=Lebiomorpha\">Lebiini</a>            <LI> <a href=\"/tree?group=Licinini&contgroup=Harpalinae\">Licinini</a>            <LI> <a href=\"/tree?group=Loricerini&contgroup=Carabidae\">Loricerini</a>            <LI> <a href=\"/tree?group=Manticorini&contgroup=Cicindelitae\">Manticorini</a>            <LI> <a href=\"/tree?group=Masoreini&contgroup=Lebiomorpha\">Masoreini</a>            <LI> <a href=\"/tree?group=Megacephalini&contgroup=Cicindelitae\">Megacephalini</a>            <LI> <a href=\"/tree?group=Melaenini&contgroup=Carabidae_Conjunctae\">Melaenini</a>            <LI> <a href=\"/tree?group=Metriini&contgroup=Paussinae\">Metriini</a>            <LI> <a href=\"/tree?group=Migadopini&contgroup=Carabidae\">Migadopini</a>            <LI> <a href=\"/tree?group=Morionini&contgroup=Harpalinae\">Morionini</a>            <LI> <a href=\"/tree?group=Nebriini&contgroup=Nebriitae\">Nebriini</a>            <LI> <a href=\"/tree?group=Notiokasiini&contgroup=Nebriitae\">Notiokasini</a>            <LI> <a href=\"/tree?group=Notiophilini&contgroup=Nebriitae\">Notiophilini</a>            <LI> <a href=\"/tree?group=Nototylini&contgroup=Carabidae\">Nototylini</a>            <LI> <a href=\"/tree?group=Odacanthini&contgroup=Lebiomorpha\">Odacanthini</a>            <LI> <a href=\"/tree?group=Omophronini&contgroup=Carabidae\">Omophronini</a>            <LI> <a href=\"/tree?group=Oodini&contgroup=Harpalinae\">Oodini</a>            <LI> <a href=\"/tree?group=Opisthiini&contgroup=Nebriitae\">Opisthiini</a>            <LI> <a href=\"/tree?group=Orthogoniini&contgroup=Harpalinae\">Orthogoniini</a>            <LI> <a href=\"/tree?group=Ozaenini&contgroup=Paussinae\">Ozaenini</a>            <LI> <a href=\"/tree?group=Pamborini&contgroup=Carabitae\">Pamborini</a>            <LI> <a href=\"/tree?group=Panagaeini&contgroup=Harpalinae\">Panagaeini</a>            <LI> <a href=\"/tree?group=Patrobini&contgroup=Carabidae_Conjunctae\">Patrobini</a>            <LI> <a href=\"/tree?group=Paussini&contgroup=Paussinae\">Paussini</a>            <LI> <a href=\"/tree?group=Peleciini&contgroup=Harpalinae\">Peleciini</a>            <LI> <a href=\"/tree?group=Perigonini&contgroup=Lebiomorpha\">Perigonini</a>            <LI> <a href=\"/tree?group=Physocrotaphini&contgroup=Harpalinae\">Physocrotaphini</a>            <LI> <a href=\"/tree?group=Platynini&contgroup=Harpalinae\">Platynini</a>            <LI> <a href=\"/tree?group=Pogonini&contgroup=Carabidae_Conjunctae\">Pogonini</a>            <LI> <a href=\"/tree?group=Promecognathini&contgroup=Carabidae\">Promecognathini</a>            <LI> <a href=\"/tree?group=Pseudomorphini&contgroup=Harpalinae\">Pseudomorphini</a>            <LI> <a href=\"/tree?group=Psydrini&contgroup=Carabidae_Conjunctae\">Psydrini</a>            <LI> <a href=\"/tree?group=Pterostichini&contgroup=Harpalinae\">Pterostichini</a>            <LI> <a href=\"/tree?group=Rhysodini&contgroup=Carabidae\">Rhysodini</a>            <LI> <a href=\"/tree?group=Scaritini&contgroup=Carabidae\">Scaritini</a>            <LI> <a href=\"/tree?group=Siagonini&contgroup=Carabidae\">Siagonini</a>            <LI> <a href=\"/tree?group=Trechini&contgroup=Carabidae_Conjunctae\">Trechini</a>            <LI> <a href=\"/tree?group=Zabrini&contgroup=Harpalinae\">Zabrini</a>            <LI> <a href=\"/tree?group=Zolini&contgroup=Carabidae_Conjunctae\">Zolini</a>            <LI> <a href=\"/tree?group=Zuphiini&contgroup=Harpalinae\">Zuphiini</a>          </UL>";
		assertEquals(accPage.getText(), text);
		assertEquals(accPage.getReferences(), "these are some refs");
		
		Set contributors = accPage.getContributors();
		assertEquals(1, contributors.size());
		AccessoryPageContributor contr = (AccessoryPageContributor) contributors.iterator().next();
		assertEquals(contr.getContributorId(), 344);
		assertEquals(contr.getIsCopyOwner(), true);
		assertEquals(contr.getIsAuthor(), false);
		assertEquals(contr.getIsContact(), false);
		assertEquals(contr.getOrder(), 0);
		assertEquals(contr.getContributor().getName(), "David R. Maddison");
		System.out.println("accessory page is: " + accPage);
	}
	
	public void testQuerying() {
		Hashtable args = new Hashtable();
		args.put(AccessoryPageDAO.CONTRIBUTOR, david);
		List results = dao.getAccessoryPagesMatchingCriteria(args);
		assertEquals(results.size(), 11);
		
		args.put(AccessoryPageDAO.GROUP, "Eubacteria");
		results = dao.getAccessoryPagesMatchingCriteria(args);
		assertEquals(results.size(), 1);
		
		args.clear();
		args.put(AccessoryPageDAO.CONTRIBUTOR, david);
		args.put(AccessoryPageDAO.TITLE, "Bembidion zephyrum/B. levettei");
		results = dao.getAccessoryPagesMatchingCriteria(args);
		assertEquals(results.size(), 1);
		AccessoryPageContributor contr = (AccessoryPageContributor) ((MappedAccessoryPage) results.get(0)).getContributors().iterator().next();
		System.out.println("contributor is: " + contr);
		assertEquals(contr.getContributorId(), 344);
		
		args.clear();
		args.put(AccessoryPageDAO.COPY_YEAR, "2000");
		results = dao.getAccessoryPagesMatchingCriteria(args);
		assertEquals(results.size(), 1);
		
		args.clear();
		args.put(AccessoryPageDAO.ID, "39");
		results = dao.getAccessoryPagesMatchingCriteria(args);
		assertEquals(results.size(), 1);
		MappedAccessoryPage page = (MappedAccessoryPage) results.get(0);
		assertEquals(page.getAccessoryPageId(), new Long(39));
	}
	
	public void testFieldsMatching() {
		String title = "title";
		String menu = "menu";
		String refs = "refs";
		String status = "Complete";
		String copyDate = "1996";
		String copyHolder = "holder";
		String text = "this is some text about my page";
		String acks = "some acks";
		String notes = "some notes";
		byte usePermission = NodeImage.EVERYWHERE_USE;
		byte treehouseType = MappedAccessoryPage.GAME;
		boolean isSubmitted = true, isTreehouse = true;
		AccessoryPageContributor contr = new AccessoryPageContributor();
		contr.setContributorId(344);
		contr.setIsAuthor(true);
		SortedSet contrs = new TreeSet();
		contrs.add(contr);
		int contributorId = 664;
		AccessoryPageContributor contr2 = new AccessoryPageContributor();
		contr2.setContributorId(contributorId);
		contr2.setIsAuthor(false);
		contr2.setIsCopyOwner(true);
		contrs.add(contr2);
		
		SortedSet links = new TreeSet();
		InternetLink link = new InternetLink();
		link.setSiteName("Fark.com");
		link.setUrl("http://www.fark.com");
		link.setComments("THis is a site that's not news, it's fark.com.");
		link.setOrder(0);
		links.add(link);
		
		InternetLink link2 = new InternetLink();
		link2.setSiteName("Tree of Life Web Project");
		link2.setUrl("http://tolweb.org");
		link2.setComments("An online project to document the phylogeny of all of earths living species");
		link2.setOrder(1);
		links.add(link2);
		
		SortedSet nodesSet = constructLifeNodesSet();
		
		Date submissionDate = new Date();
		Long submissionContributorId = new Long(663);		
		
		MappedAccessoryPage accPage = new MappedAccessoryPage();
		accPage.setTreehouseType(treehouseType);
		accPage.setNotes(notes);
		accPage.setAcknowledgements(acks);
		accPage.setCopyright(copyHolder);
		accPage.setIsSubmitted(isSubmitted);
		accPage.setIsTreehouse(isTreehouse);
		accPage.setUsePermission(usePermission);
		accPage.setPageTitle(title);
		accPage.setMenu(menu);
		accPage.setReferences(refs);
		accPage.setStatus(status);
		accPage.setContributors(contrs);
		accPage.setCopyrightYear(copyDate);
		accPage.setText(text);
		accPage.setInternetLinks(links);
		accPage.setUsePermission(NodeImage.EVERYWHERE_USE);
		accPage.setNodesSet(nodesSet);
		accPage.setSubmissionDate(submissionDate);
		accPage.setSubmittedContributorId(submissionContributorId);
		accPage.setSubmittedContributor(contributorDao.getContributorWithId(submissionContributorId.toString()));
		accPage.setIsAdvancedLevel(true);
		accPage.setIsArtAndCulture(true);
		accPage.setIsBeginnerLevel(false);
		accPage.setIsGame(false);
		accPage.setIsIntermediateLevel(true);
		accPage.setIsInvestigation(true);
		accPage.setIsStory(true);
		accPage.setIsTeacherResource(false);
		accPage.setIsBiography(true);
		accPage.setLastEditedDate(new Date());
		accPage.setCreationDate(new Date());
		accPage.setComments("some comments");
		accPage.setScientificName("xxxxxxxxxxxxxx    ");
		accPage.setLastEditedContributorId(new Long(1000));
		
		Contributor contributor = new Contributor();
		contributor.setId(664);
		
		dao.addAccessoryPage(accPage, contributor);
		Long id = accPage.getAccessoryPageId();
		System.out.println("id of new accessory page is: " + id);
		MappedAccessoryPage foundPage = dao.getAccessoryPageWithId(id);
		verifyValuesOnPages(foundPage, accPage);
		
		MappedNode euks = (MappedNode) nodeDao.findNodesExactlyNamed("Eukaryotes").get(0);
		AccessoryPageNode node = new AccessoryPageNode();
		node.setNode(euks);
		node.setIsPrimaryAttachedNode(false);
		node.setShowLink(true);
		foundPage.addToNodesSet(node);
		dao.saveAccessoryPage(foundPage);
		
		MappedAccessoryPage accPage3 = dao.getAccessoryPageWithId(id);
		assertEquals(accPage3.getNodesSet().size(), 2);
		
		testPushing(accPage3);
	}
	
	public void testManipulatingContributors() {
	    Hashtable args = new Hashtable();
		args.put(AccessoryPageDAO.CONTRIBUTOR, david);
		args.put(AccessoryPageDAO.TITLE, "Bembidion zephyrum/B. levettei");
		List results = dao.getAccessoryPagesMatchingCriteria(args);
		MappedAccessoryPage page = (MappedAccessoryPage) results.get(0);
		Set contributors = page.getContributors();
		assertEquals(contributors.size(), 1);
		AccessoryPageContributor contr = (AccessoryPageContributor) contributors.iterator().next();
		assertEquals(contr.getContributorId(), david.getId());
		contributors.clear();
		AccessoryPageContributor newDavidContr = new AccessoryPageContributor();
		newDavidContr.setContributorId(344);
		newDavidContr.setOrder(0);
		contributors.add(newDavidContr);
		AccessoryPageContributor newContr = new AccessoryPageContributor();
		newContr.setContributorId(663);
		newContr.setOrder(1);
		contributors.add(newContr);
		dao.saveAccessoryPage(page);
		args.clear();
		args.put(AccessoryPageDAO.TITLE, "Bembidion zephyrum/B. levettei");
		results = dao.getAccessoryPagesMatchingCriteria(args);
		page = (MappedAccessoryPage) results.get(0);
		contributors = page.getContributors();
		assertEquals(contributors.size(), 2);
	}
	
	public void testSubmittedPages() {
		List list = dao.getSubmittedAccessoryPages();
		assertEquals(list.size(), 1);
		assertTrue(((MappedAccessoryPage) list.get(0)).getIsSubmitted());
	}*/
	
	public void testPushing(MappedAccessoryPage pageToPush) {
		pusher.pushAcessoryPageToDB(pageToPush, publicDao);
		MappedAccessoryPage publicPage = publicDao.getAccessoryPageWithId(pageToPush.getAccessoryPageId());
		verifyValuesOnPages(pageToPush, publicPage);
	}
	
	private void verifyValuesOnPages(MappedAccessoryPage foundPage, MappedAccessoryPage accPage) {
	    assertEquals(foundPage.getTreehouseType(), accPage.getTreehouseType());
		assertEquals(foundPage.getNotes(), accPage.getNotes());
		assertEquals(foundPage.getAcknowledgements(), accPage.getAcknowledgements());
		assertEquals(foundPage.getCopyright(), accPage.getCopyright());
		assertEquals(foundPage.getIsSubmitted(), accPage.getIsSubmitted());
		assertEquals(foundPage.getIsTreehouse(), accPage.getIsTreehouse());
		assertEquals(foundPage.getUsePermission(), accPage.getUsePermission());
		assertEquals(foundPage.getPageTitle(), accPage.getPageTitle());
		assertEquals(foundPage.getMenu(), accPage.getMenu());
		assertEquals(foundPage.getReferences(), accPage.getReferences());
		assertEquals(foundPage.getStatus(), accPage.getStatus());
		assertEquals(foundPage.getContributorId(), accPage.getContributorId());
		assertEquals(foundPage.getContributors().size(), accPage.getContributors().size());
		assertEquals(foundPage.getCopyrightYear(), accPage.getCopyrightYear());
		assertEquals(foundPage.getText(), accPage.getText());
		assertEquals(foundPage.getUsePermission(), accPage.getUsePermission());
		
		AccessoryPageContributor c1 = (AccessoryPageContributor) foundPage.getContributors().iterator().next();
		AccessoryPageContributor c2 = (AccessoryPageContributor) accPage.getContributors().iterator().next();
		assertEquals(c1.getContributorId(), c2.getContributorId());
		assertEquals(c1.getIsAuthor(), c2.getIsAuthor());	
		
		assertEquals(foundPage.getInternetLinks().size(), accPage.getInternetLinks().size());
		Iterator it = foundPage.getInternetLinks().iterator();
		Iterator it2 = accPage.getInternetLinks().iterator();
		while (it.hasNext()) {
			InternetLink foundLink = (InternetLink) it.next();
			InternetLink otherLink = (InternetLink) it2.next();
			assertEquals(foundLink.getSiteName(), otherLink.getSiteName());		
			assertEquals(foundLink.getUrl(), otherLink.getUrl());		
			assertEquals(foundLink.getComments(), otherLink.getComments());			
		}

		//assertEquals(foundPage.getPrimaryAttachedNode().getNodeId(), accPage.getPrimaryAttachedNode().getNodeId());
		checkDates(foundPage.getSubmissionDate(), accPage.getSubmissionDate());
		checkDates(foundPage.getCreationDate(), accPage.getCreationDate());
		checkDates(foundPage.getLastEditedDate(), accPage.getLastEditedDate());
		assertEquals(foundPage.getIsAdvancedLevel(), accPage.getIsAdvancedLevel());
		assertEquals(foundPage.getIsArtAndCulture(), accPage.getIsArtAndCulture());
		assertEquals(foundPage.getIsBeginnerLevel(), accPage.getIsBeginnerLevel());
		assertEquals(foundPage.getIsGame(), accPage.getIsGame());
		assertEquals(foundPage.getIsIntermediateLevel(), accPage.getIsIntermediateLevel());
		assertEquals(foundPage.getIsInvestigation(), accPage.getIsInvestigation());
		assertEquals(foundPage.getIsStory(), accPage.getIsStory());
		assertEquals(foundPage.getIsTeacherResource(), accPage.getIsTeacherResource());
		assertEquals(foundPage.getIsBiography(), accPage.getIsBiography());
		assertEquals(foundPage.getLastEditedContributorId(), accPage.getLastEditedContributorId());
		assertEquals(foundPage.getSubmittedContributorId(), accPage.getSubmittedContributorId());
		assertEquals(foundPage.getComments(), accPage.getComments());
		assertEquals(foundPage.getScientificName(), accPage.getScientificName());
		if (foundPage.getSubmittedContributor() != null) {
			assertEquals(foundPage.getSubmittedContributor().getName(), accPage.getSubmittedContributor().getName());
		}
	}
	
	/*public void testNodeOrder() {
	    MappedAccessoryPage pg = constructGenericAccessoryPage();
	    dao.addAccessoryPage(pg, david);
	    MappedAccessoryPage savedPg = dao.getAccessoryPageWithId(pg.getAccessoryPageId());
	    Iterator it = savedPg.getNodesSet().iterator();
	    // verify euks is first, then life
	    AccessoryPageNode nd = (AccessoryPageNode) it.next();
	    assertEquals(nd.getNode().getName(), "Eukaryotes");
	    nd = (AccessoryPageNode) it.next();
	    assertEquals(nd.getNode().getName(), "Life on Earth");
	    nd = (AccessoryPageNode) it.next();
	    assertEquals(nd.getNode().getName(), "Viruses");
	}*/
	
	public void testPushing() {
	    AccessoryPageContributor contributor = new AccessoryPageContributor();
	    contributor.setContributor(david);
	    contributor.setOrder(0);
	    MappedAccessoryPage pg = constructGenericAccessoryPage();
	    pg.addToContributors(contributor);
	    dao.addAccessoryPage(pg, david);
	    System.out.println("just added it into working.  going to push\n\n\n");
	    testPushing(pg);
	}
	
	private MappedAccessoryPage constructGenericAccessoryPage() {
	    MappedAccessoryPage pg = new MappedAccessoryPage();
	    pg.setPageTitle("new page");
	    pg.setMenu("menu");
	    pg.setCopyrightYear("2000");
	    pg.setText("");
	    pg.setReferences("");
	    pg.setNodesSet(constructMoreThanLifeNodesSet());
	    pg.setInternetLinks(constructInternetLinksSet());
	    return pg;
	}
	
	private SortedSet constructLifeNodesSet() {
		SortedSet nodesSet = new TreeSet();
		MappedNode result = (MappedNode) nodeDao.findNodesExactlyNamed("Life").get(0);
		AccessoryPageNode node = new AccessoryPageNode();
		node.setNode(result);
		node.setIsPrimaryAttachedNode(true);
		node.setShowLink(true);
		nodesSet.add(node);
		return nodesSet;
	}
	
	private SortedSet constructInternetLinksSet() {
	    SortedSet linksSet = new TreeSet();
	    InternetLink link = new InternetLink();
	    link.setLinkId(new Long(1));
	    link.setOrder(0);
	    link.setSiteName("foo");
	    link.setUrl("www.foo.com");
	    linksSet.add(link);
	    link = new InternetLink();
	    link.setLinkId(new Long(2));
	    link.setOrder(1);
	    link.setSiteName("bar");
	    link.setUrl("www.bar.com");
	    linksSet.add(link);
	    return linksSet;
	}
	
	private SortedSet constructMoreThanLifeNodesSet() {
	    SortedSet nodes = constructLifeNodesSet();
	    AccessoryPageNode euks = new AccessoryPageNode();
	    euks.setNode((MappedNode) nodeDao.findNodesExactlyNamed("Eukaryotes").get(0));
	    euks.setIsPrimaryAttachedNode(false);
	    euks.setShowLink(true);
	    nodes.add(euks);	   
	    AccessoryPageNode viruses = new AccessoryPageNode();
	    viruses.setNode((MappedNode) nodeDao.findNodesExactlyNamed("Viruses").get(0));
	    viruses.setIsPrimaryAttachedNode(false);
	    viruses.setShowLink(true);	    
	    nodes.add(viruses);
	    return nodes;
	}
}
