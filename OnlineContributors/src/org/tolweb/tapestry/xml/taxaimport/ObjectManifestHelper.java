package org.tolweb.tapestry.xml.taxaimport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.lang.Validate;
import org.tolweb.dao.MetaNodeDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.ObjectManifestLogDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.ObjectManifestRecord;
import org.tolweb.misc.BtolMetaNode;
import org.tolweb.misc.MetaNode;
import org.tolweb.misc.MetaNodeTuple;
import org.tolweb.tapestry.xml.taxaimport.preparers.ObjectManifestPreparer;
import org.tolweb.treegrow.main.Contributor;

public class ObjectManifestHelper {
	public static final String ROOT_NODE_ID_NULL_MESSAGE = "The 'rootNodeId' must have a value.  Can't create an object manifest with specifying the root (or basal) node.";
	public static final String DAO_NULL_MESSAGE = "Must provide a DAO reference.";
	public static final String DOC_NULL_MESSAGE = "Must provide a reference to a valid nu.xom.Document object.";
	public static final String USER_NULL_MESSAGE = "Must provide a Contributor reference.";
	
	public static Document createObjectManifest(Long rootNodeId, NodeDAO workingNodeDAO, MetaNodeDAO metaNodeDAO) {
		Validate.notNull(rootNodeId, ROOT_NODE_ID_NULL_MESSAGE);
		Validate.notNull(workingNodeDAO, DAO_NULL_MESSAGE);
		Validate.notNull(metaNodeDAO, DAO_NULL_MESSAGE);
		
		long start = System.currentTimeMillis();
		Set ancestorIds = workingNodeDAO.getAncestorsForNode(rootNodeId);
		System.out.println("ancestors: " + ancestorIds.toString());
		Set<Long> workingIds = new HashSet<Long>();
		determineCladeIds(rootNodeId, workingIds, workingNodeDAO);
		if (workingNodeDAO.getNodeExistsWithId(rootNodeId)) {
			workingIds.add(rootNodeId);
		}
		System.out.println("processing clade node ids took: " + (System.currentTimeMillis() - start));
		System.out.println("number of node ids: " + workingIds.size());
		
		return buildObjectManifest(rootNodeId, workingIds, workingNodeDAO, metaNodeDAO, false, false);
	}

	public static Document createObjectManifest(Long basalNodeId, Set<Long> descIds, boolean includeInactiveNodes, NodeDAO workingNodeDAO, MetaNodeDAO metaNodeDAO) {
		return buildObjectManifest(basalNodeId, descIds, workingNodeDAO, metaNodeDAO, includeInactiveNodes, true);
	}
	
	public static void saveObjectManifest(Document doc, Long basalNodeId, Contributor user, ObjectManifestLogDAO logDAO) {
		Validate.notNull(doc, DOC_NULL_MESSAGE);
		Validate.notNull(user, USER_NULL_MESSAGE);
		Validate.notNull(logDAO, DAO_NULL_MESSAGE);
		
		UUID manifestKey = UUID.randomUUID();
		doc.getRootElement().addAttribute(new Attribute("id", manifestKey.toString()));
		ObjectManifestRecord record = new ObjectManifestRecord();
		record.setBasalNodeId(basalNodeId);
		record.setKeyValue(manifestKey.toString());
		record.setManifest(doc.toXML());
		record.setUpdatedBy((user != null) ? user.getEmail() : "[user not logged in]");
		logDAO.createObjectManifestRecord(record);		
	}
	
	@SuppressWarnings("unchecked")
	protected static void determineCladeIds(Long rootNodeId, Set<Long> ids, NodeDAO dao) {
		if (rootNodeId != null) {
			List childIds = dao.getChildrenNodeIds(rootNodeId);
			ids.addAll(childIds);
			for (Iterator itr = childIds.iterator(); itr.hasNext(); ) {
				Long childId = (Long)itr.next();
				determineCladeIds(childId, ids, dao);
			}
		}
	}	
	
	protected static Document buildObjectManifest(Long basalNodeId, Set<Long> workingIds, NodeDAO workingNodeDAO, MetaNodeDAO metaNodeDAO, boolean includeInactiveNodes, boolean includeNodesWithoutAttachments) {
		System.out.println("processing manifest data...");
		int total = workingIds.size();
		int i = 1;
		long processStart = System.currentTimeMillis();
		HashMap<Long, MetaNodeTuple> map = new HashMap<Long, MetaNodeTuple>();
		for (Long id : workingIds) {
			System.out.println("\tprocessing id: " + id + " number " + i++ + " of " + total);
			long currentItr = System.currentTimeMillis();
			MappedNode nd = workingNodeDAO.getNodeWithId(id, includeInactiveNodes);
			long currentTime = System.currentTimeMillis();
			MetaNode mnode = metaNodeDAO.getMetaNode(nd);
			System.out.println("\t fetching meta node element took: " + (System.currentTimeMillis() - currentTime));
			//currentTime = System.currentTimeMillis();
			//### fetching btol data at the same time was taking an insane amount of time and have to omitted from the process
			//BtolMetaNode bnode = getBtolMetaNodeDAO().getMetaNodeForBtol(nd);
			//System.out.println("\t fetching btol meta node element took: " + (System.currentTimeMillis() - currentTime));
			MetaNodeTuple tuple = new MetaNodeTuple(mnode, new BtolMetaNode());
			map.put(id, tuple);
			System.out.println("\t iteration took: " + (System.currentTimeMillis() - currentItr));
		}
		
		System.out.println("data ready... starting xml preparation:");
		System.out.println("\tprocessing took: " + (System.currentTimeMillis() - processStart));
		ObjectManifestPreparer obPrep = new ObjectManifestPreparer(basalNodeId, map);
		obPrep.setIncludeNodesWithoutAttachments(includeNodesWithoutAttachments);
		Element root = obPrep.toElement();
		Document doc = new Document(root);
		doc.setRootElement(root);

		return doc;
	}	
}
