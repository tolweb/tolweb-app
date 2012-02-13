/*
 * Created on Jun 18, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.treegrow.main;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.JLabel;

import junit.framework.TestCase;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NodeImageTest extends TestCase {
	public void testSetValues() {
		NodeImage img = constructFilledOutImage();
		NodeImage img2 = new NodeImage();
		img2.setValues(img, true, true);
		testEquality(img, img2, true);
		img2 = new NodeImage();
		img2.setValues(img, true, false);
		testEquality(img, img2, false);	
	}
	
	private void testEquality(NodeImage img1, NodeImage img2, boolean checkAll) {
		assertEquals(img1.getCopyrightOwner(), img2.getCopyrightOwner());
		assertEquals(img1.getCopyrightEmail(), img2.getCopyrightEmail());	
		assertEquals(img1.getCopyrightUrl(), img2.getCopyrightUrl());
		assertEquals(img1.getScientificName(), img2.getScientificName());
		assertEquals(img1.getReference(), img2.getReference());
		assertEquals(img1.getAltText(), img2.getAltText());
		assertEquals(img1.getCreator(), img2.getCreator());
		assertEquals(img1.getIdentifier(), img2.getIdentifier());
		assertEquals(img1.getAcknowledgements(), img2.getAcknowledgements());
		assertEquals(img1.getGeoLocation(), img2.getGeoLocation());
		assertEquals(img1.getStage(), img2.getStage());
		assertEquals(img1.getBodyPart(), img2.getBodyPart());
		assertEquals(img1.getSize(), img2.getSize());
		assertEquals(img1.getView(), img2.getView());
		assertEquals(img1.getPeriod(), img2.getPeriod());
		assertEquals(img1.getCollection(), img2.getCollection());
		assertEquals(img1.getCollectionAcronym(), img2.getCollectionAcronym());
		assertEquals(img1.getType(), img2.getType());
		assertEquals(img1.getCollector(), img2.getCollector());		
		assertEquals(img1.getSex(), img2.getSex());		
		assertEquals(img1.getComments(), img2.getComments());
		assertEquals(img1.getCopyrightDate(), img2.getCopyrightDate());
		assertEquals(img1.getCollectionDate(), img2.getCollectionDate());
		assertEquals(img1.getAdditionalDateTimeInfo(), img2.getAdditionalDateTimeInfo());
		assertEquals(img1.getAlive(), img2.getAlive());
		assertEquals(img1.getSeason(), img2.getSeason());
		assertEquals(img1.getImageType(), img2.getImageType());
		assertEquals(img1.getBehavior(), img2.getBehavior());
		assertEquals(img1.getVoucherNumber(), img2.getVoucherNumber());
		assertEquals(img1.getNotes(), img2.getNotes());
		assertEquals(img1.getIsFossil(), img2.getIsFossil());		
		assertEquals(img1.getInPublicDomain(), img2.getInPublicDomain());
		assertEquals(img1.getIsSpecimen(), img2.getIsSpecimen());
		assertEquals(img1.getIsBodyParts(), img2.getIsBodyParts());
		assertEquals(img1.getIsUltrastructure(), img2.getIsUltrastructure());
		assertEquals(img1.getIsHabitat(), img2.getIsHabitat());
		assertEquals(img1.getIsEquipment(), img2.getIsEquipment());
		assertEquals(img1.getCopyrightContributorId(), img2.getCopyrightContributorId());
		assertEquals(img1.getUsePermission(), img2.getUsePermission());
		checkNodes(img1.getNodes(), img2.getNodes());
		assertEquals(img1.getCopyrightOwnerContributor(), img2.getCopyrightOwnerContributor());
		assertEquals(img1.getUserCreationDate(), img2.getUserCreationDate());
		checkNodes(img1.getNodesSet(), img2.getNodesSet());
		if (checkAll) {		
			assertEquals(img1.getLocation(), img2.getLocation());			
			assertEquals(img1.getId(), img2.getId());
		}
	}
	
	private void checkNodes(Collection coll1, Collection coll2) {
		assertEquals(coll1.size(), coll2.size());
		assertNotSame(coll1, coll2);
	}
	
	private NodeImage constructFilledOutImage() {
		NodeImage img = new NodeImage();
		img.setLocation("some location");
		img.setCopyrightOwner("copy owner");
		img.setCopyrightEmail("email@email.com");
		img.setCopyrightUrl("http://test");
		img.setScientificName("sci name");
		img.setReference("refs");
		img.setAltText("Alt text"); 
		img.setCreator("creator");
		img.setIdentifier("identifier");
		img.setAcknowledgements("acks");
		img.setGeoLocation("geoLoc");
		img.setStage("stage");
		img.setBodyPart("bodypart");
		img.setSize("size");
		img.setView("view");
		img.setPeriod("period");
		img.setCollection("collection");
		img.setCollectionAcronym("acr");
		img.setType("type"); 
		img.setCollector("collector");
		img.setSex("female");
		img.setComments("some comments");
		img.setCopyrightDate("copydate");
		img.setCollectionDate("colldate");
		img.setAdditionalDateTimeInfo("info");
		img.setAlive("alive"); 
		img.setSeason("season");
		img.setImageType("photo");
		img.setBehavior("behavior");
		img.setVoucherNumber("voucher num");
		img.setNotes("notes");
		img.setIsFossil(true);
		img.setInPublicDomain(true);
		img.setChangedFromServer(true);
		img.setCheckedOut(true);
		img.setOnlineCheckedOut(true);
		img.setIsSpecimen(true);
		img.setIsBodyParts(true);
		img.setIsUltrastructure(true);
		img.setIsHabitat(true);
		img.setIsEquipment(true);
		img.setId(666);
		img.setCopyrightContributorId(20);
		img.setUsePermission(NodeImage.EVERYWHERE_USE);
		img.setThumbnail(new JLabel());
		Vector nodes = new Vector();
		nodes.add(new Node(1, "Life"));
		nodes.add(new Node(2, "Eukaryotes"));
		img.setNodes(nodes);
		Vector deletedNodes = new Vector();
		deletedNodes.add(new Node(3, "Viruses"));
		img.setDeletedNodes(deletedNodes);
		Contributor contr = new Contributor();
		contr.setId(11);
		contr.setFirstName("Bob");
		img.setContributor(contr);
		contr = new Contributor();		
		contr.setId(12);
		contr.setFirstName("Joe");
		img.setCopyrightOwnerContributor(contr);
		contr = new Contributor();
		contr.setId(13);
		contr.setFirstName("Briggs");
		img.setLastEditedContributor(contr);
		contr = new Contributor();
		contr.setId(14);
		contr.setFirstName("Michael");
		img.setCheckedOutContributor(contr);
		Date date = new Date(78, 11, 22);
		img.setCreationDate(date);
		date = new Date(79, 6, 15);
		img.setUserCreationDate(date);
		date = new Date(80, 5, 3);
		img.setLastEditedDate(date);
		date = new Date(63, 11, 22);
		img.setCheckoutDate(date);
		Set set = new HashSet();
		set.add(new Node(197, "Bembidion"));
		set.add(new Node(250, "Cephalopoda"));
		img.setNodesSet(set);
		return img;
	}
}
