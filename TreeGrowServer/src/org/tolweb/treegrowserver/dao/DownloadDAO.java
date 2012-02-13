/*
 * Created on Nov 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.Collection;
import java.util.List;

import org.jdom.Element;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.Download;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DownloadDAO {
	public List getNodesAreDownloaded(List nodeIds);
    public boolean getNodeIsDownloaded(Long nodeId);
    public Download getOpenDownloadForNode(Long nodeId);
    public Element getNodeIsLocked(Long nodeId);
	public Element getNodeIsLocked(MappedNode nd);
    public Element getNodeIsLocked(MappedNode nd, Contributor contr);
    public Element getNodeIsLocked(Long nodeId, Contributor contr);
    public String getMessageForLockedElement(Element element);
    public void createNewDownload(Download download);
    public void saveDownload(Download download);
    public void deleteDownload(Download download);
    public Download getDownloadWithId(Long id);
    public boolean getDownloadIsActive(Long id);
    public void reassignDownloadRootNodesForDeletedNodes(Collection deletedNodeIds, Long newRootNodeId);
    /**
     * Returns a list of download ids that are active, have nodes
     * that are contained in the node ids to check, and are checked out
     * by the contributor passed-in.
     * @param nodeIds The ids to check
     * @return A list of 2-object arrays.  The first object is the download id and the second is the
     * 			contributor id for that download.
     */
    public List getActiveDownloadsForNodeIds(Collection nodeIds);
    public List getActiveDownloadsForUser(Contributor contr);
    /**
     * Returns a list of node ids from the download that have pages
     */
    public List getNodeIdsWithPages(Long downloadId);
    /**
     * return a collection of any node ids that have been deleted
     * in the passed-in collection
     * @param nodeIds
     * @return
     */
    public Collection getDeletedNodeIds(Collection nodeIds);
}
