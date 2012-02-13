/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.tolweb.hibernate.FeatureGroup;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.ReorderHelper;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.TextSection;

public class PageDAOImpl extends AbstractPageContributorDAO implements PageDAO {
    private ImageDAO imgDAO;
    private EditHistoryDAO editHistoryDAO;
    private NodeDAO miscNodeDAO;
    private NodeDAO workingNodeDAO;
    private ReorderHelper reorderHelper;
    private static final int A_REALLY_HIGH_NUMBER = 100000000;
    // the page-id for Life on Earth will be always be 1 
    private static final Long LIFE_ON_EARTH = Long.valueOf(1);
    private static Comparator<Object[]> imageIdComparator;
    
    public MappedPage getPageWithId(Long id) {
        try {
	        MappedPage pg = (MappedPage) getHibernateTemplate().load(MappedPage.class, id);
            //MappedPage pg = (MappedPage) getHibernateTemplate().find("from org.tolweb.hibernate.MappedPage p where p.pageId=" + id).get(0); 
	        fillOutInMemoryValues(pg);
	        return pg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List getPageIdsForContributor(Long id) {
    	String queryString = "select pc.page_id from org.tolweb.treegrow.page.PageContributor pc where pc.contributor_id=" + id;
    	return getHibernateTemplate().find(queryString);
    }
    
    public void addPageWithId(Long id, Long nodeId) {
		Session session = getSession();
		try {
			Statement insertStatement = session.connection().createStatement();
			// Temporarily set the node to life -- just for the insert so Hibernate doesn't complain
			
			//String sqlString = "insert into PAGES (page_id,node_id) values (" + id + "," + nodeId + ")";
			String sqlString = "insert into PAGES (page_id,node_id) values (" + id + ",1)";
			int numInserted = insertStatement.executeUpdate(sqlString);
			System.out.println("just inserted page! : " + sqlString + " how many rows affected? " + numInserted);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void addTextSectionWithId(Long id) {
        Session session = getSession();
        try {
            String insertSectionSql = "insert into SECTIONS (section_id) values(" + id + ")";
            Statement insertStatement = session.connection().createStatement();
            insertStatement.executeUpdate(insertSectionSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean getNodeHasPage(MappedNode nd) {
        return (nd != null) ? getNodeHasPage(nd.getNodeId()) : false;
    }
    
    public boolean getNodeHasPage(Long nodeId) {
        List list = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.MappedPage p where p.mappedNode.nodeId=" + nodeId); 
        if (list == null || list.size() == 0) {
            return false;
        } else {
            Integer intgr =  (Integer) list.get(0);
            if (intgr.intValue() > 0) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    public Collection getNodeIdsWithPages(Collection nodeIds) {
    	Query query = getSession().createQuery("select p.mappedNode.nodeId from org.tolweb.hibernate.MappedPage p where p.mappedNode.nodeId " + 
                StringUtils.returnSqlCollectionString(nodeIds));
    	query.setCacheable(true);
        List list = query.list();
        return list;
    }
    
    public MappedPage getPageNodeIsOn(MappedNode nd) {
        MappedPage pg = (MappedPage) getPageNodeIsOn(nd, false);
        fillOutInMemoryValues(pg);
        return pg;
    }
    
    public Long getPageIdNodeIsOn(MappedNode nd) {
        return (Long) getPageNodeIsOn(nd, true);
    }
    
    public Long getPageIdNodeIdIsOn(Long nodeId) {
        return (Long) ((List) getHibernateTemplate().find("select n.pageId from org.tolweb.hibernate.MappedNode n where n.nodeId=" + nodeId)).get(0);
    }
    
    public String getGroupNameForPage(Long pgId) {
        List list = getHibernateTemplate().find("select p.groupName from org.tolweb.hibernate.MappedPage p where p.pageId=" + pgId); 
        if (list != null && list.size() > 0) {
            return (String) list.get(0);
        } else {
            return "";
        }
    }
    
    public Object[] getGroupNameAndNodeIdForPage(Long pgId) {
        return (Object[]) getFirstObjectFromQuery("select p.groupName, p.mappedNode.nodeId from org.tolweb.hibernate.MappedPage p where p.pageId=" + pgId);
    }
    
    private Object getPageNodeIsOn(MappedNode nd, boolean onlyId) {
        String idString = onlyId ? "select p.pageId " : "";
        List list = getHibernateTemplate().find(idString + "from org.tolweb.hibernate.MappedPage p where p.pageId=" + nd.getPageId());
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    public MappedNode getNodeForPageNodeIsOn(MappedNode nd) {
        Long id = nd.getPageId();
        List list;
        try {
            Session session = getSession();
            Query pageQuery = session.createQuery("select n from org.tolweb.hibernate.MappedNode n, org.tolweb.hibernate.MappedPage p where p.pageId=" + id + " and n.nodeId=p.mappedNode.nodeId");
            pageQuery.setCacheable(true);
            list = pageQuery.list();
        } catch (Exception e) {
            list = new ArrayList();
            e.printStackTrace();
        }        
        if (list != null && list.size() > 0) {
            return (MappedNode) list.get(0);
        } else {
            return null;
        }
    }
    
    public MappedPage getPageForNode(MappedNode nd) {
        MappedPage pg = (MappedPage) getPageForNode(nd, false);
        if (pg != null) {
            fillOutInMemoryValues(pg);
        }
        return pg;
    }
    
    public Long getPageIdForNode(MappedNode nd) {
        return (Long) getPageForNode(nd, true);
    }
    
    public Long getPageIdForNodeId(Long nodeId) {
        return (Long) getPageForNode(nodeId, true);
    }
    
    /**
     * Method that checks to see if this node has a page.  If it does,
     * return that page, else return the page that this node sits on.
     * @param nd
     * @return
     */
    public MappedPage getPage(MappedNode nd) {
    	if (getNodeHasPage(nd)) {
    		return getPageForNode(nd);
    	} else {
    		return getPageNodeIsOn(nd);
    	}
    }
    
    public Long getPageId(Long nodeId) {
    	if (getNodeHasPage(nodeId)) {
    		return getPageIdForNodeId(nodeId);
    	} else {
    		return getPageIdNodeIdIsOn(nodeId);
    	}
    }
    public Long getRootNodeIdOnPage(Long nodeId) {
    	if (getNodeHasPage(nodeId)) {
    		return nodeId;
    	} else {
    		Long pageId = getPageId(nodeId);
    		return getNodeIdForPage(pageId);
    	}
    }
    
    public Long getNodeIdForPage(Long pgId) {
        List results = getHibernateTemplate().find("select p.mappedNode.nodeId from org.tolweb.hibernate.MappedPage p where p.pageId=" + pgId);
        if (results != null && results.size() > 0) {
            return (Long) results.get(0);
        } else {
            return null;
        }
    }
    
    private Object getPageForNode(MappedNode nd, boolean onlyId) {
        return getPageForNode(nd.getNodeId(), onlyId);
    }
    
    protected Object getPageForNode(Long nodeId, boolean onlyId) {
        String idString = onlyId ? "select p.pageId " : "";
        List list;
        try {
            Session session = getSession();
            Query pageQuery = session.createQuery(idString + "from org.tolweb.hibernate.MappedPage p where p.mappedNode.nodeId=" + nodeId);
            pageQuery.setCacheable(!getIsWorking());
            list = pageQuery.list();
        } catch (Exception e) {
            list = new ArrayList();
            e.printStackTrace();
        }        
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }        
    }
    
    public List getChildPageNamesAndIds(Long pgId) {
        return getChildPages(pgId, true);
    }
    
    public List getChildPages(MappedPage pg) {
        return getChildPages(pg.getPageId(), false);
    }
    
    protected List getChildPages(Long pgId, boolean onlyNamesIds) {
        String selectString = onlyNamesIds ? "select p.groupName, p.pageId, p.mappedNode.nodeId " : "select p ";
        List list = getHibernateTemplate().find(selectString + "from org.tolweb.hibernate.MappedPage p join p.mappedNode as node where p.parentPageId=" + pgId + " order by node.orderOnPage");
        if (!onlyNamesIds) {
            // If we are returning the actual page objects, fill out the contributor info
            fillOutInMemoryValuesForAll(list);
        }
        return list;        
    }
    
    public List getAncestorPageNames(Long pgId) {
    	// if we are not given a valid page-id to query with, then simply give them null
    	if (pgId == null) {
    		return null; // YES, LET THEM EAT NULL!!!
    	}
    	
        List ancestors = getHibernateTemplate().find(
        		"select a.ancestorId from org.tolweb.hibernate.PageAncestor a where a.pageId=" + 
        		pgId + " and a.ancestorId !=" + pgId);
        
        ResultSet results;
		try {
			if (!ancestors.isEmpty()) {
			    Session session = getSession();
				Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				String sqlString = "select distinct mappedpage0_.cladename as x0_0_, mappedpage1_.cladename as x1_0_, mappedpage0_.node_id as x3_0_, " + 
		        "count(pageancest2_.page_id) as x2_0_ from PAGES mappedpage0_, PAGES mappedpage1_, PAGEANCESTORS pageancest2_ where " + 
		        "(pageancest2_.page_id in(" + StringUtils.returnCommaJoinedString(ancestors) + "))and" + 
		        "(mappedpage0_.page_id=pageancest2_.page_id )and(mappedpage0_.parent_page_id=mappedpage1_.page_id ) " + 
				"group by mappedpage0_.cladename, mappedpage1_.cladename order by x2_0_ desc";
				results = selectStatement.executeQuery(sqlString);
				ancestors = new ArrayList();
				while (results.next()) {
				    ancestors.add(new Object[] {results.getString(1), results.getString(2).replace(' ', '_'), Long.valueOf(results.getLong(3))});
				}
			} else {
				if (!LIFE_ON_EARTH.equals(pgId)) {
					System.out.println("INFO: [PageDAOImpl::getAncestorPageNames(Long)] >> page-id " + 
						pgId + " does not have ancestors. The query for them returns empty list. ");
				}
			}
			// Special case of life since its parent is null
			ancestors.add(new Object[] {"Life on Earth", "", Long.valueOf(1L)});
			return ancestors;			
		} catch (Exception e) {
            try {
                throw new Exception("page id missing ancestors is: " + pgId, e);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
		}
        return null;
    }
    
    public List getNodeIdsOnPage(MappedPage page) {
        return getNodeIdsOnPage(page, false);
    }
    
    public List getNodeIdsOnPage(MappedPage page, boolean onlyWithoutPages) {
    	if (onlyWithoutPages) {
    		// need to actually fetch the nodes to figure out which ones
    		List nodes = getNodesOnPage(page, false, false, false);
    		ArrayList<MappedNode> iteratedNodes = new ArrayList<MappedNode>(nodes);
    		List<Long> nodeIds = new ArrayList<Long>();
    		for (MappedNode node : iteratedNodes) {
				if (!getNodeHasPage(node.getNodeId())) {
					nodeIds.add(node.getNodeId());
				}				
			}
    		return nodeIds;
    	} else {
    		return getNodesOnPage(page, false, true, false);
    	}
    }
    
    public List getNodeIdsOnPage(Long pgId) {
    	return getNodesOnPage(pgId, false, true, false, null);
    }
    
    public List getNodeIdsOnPageWithIncompleteSubgroups(Long pgId) {
    	return getNodesOnPage(pgId, false, true, false, "node.hasIncompleteSubgroups=1");
    }
    
    public List getNodesOnPage(MappedPage page) {
        return getNodesOnPage(page, false, false, false);
    }
    
    public List getNodesOnPage(MappedPage page, boolean onlyNamed) {
        return getNodesOnPage(page, onlyNamed, false, false);
    }
    
    public List getOrderedByParentNodesOnPage(MappedPage page, boolean fetchHasPage) {
        List nodes = getNodesOnPage(page, false, false, true);
        // if looking for tuning, make this a batch fetch query
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            nextNode.setHasPage(getNodeHasPage(nextNode.getNodeId()));
        }
        return nodes;
    }
    
    public List getNodesOnPage(MappedPage page, boolean onlyNamed, boolean onlyIds, boolean orderByOrderOnParent) {
        return getNodesOnPage(page.getPageId(), onlyNamed, onlyIds, orderByOrderOnParent, null);
    }
    
    public List getNodesOnPage(Long pageId, boolean onlyNamed, boolean onlyIds, boolean orderByOrderOnParent, String additionalQuery) {
        String selectPrefix = "";
        if (onlyIds) {
            selectPrefix = "select node.nodeId ";
        }
        String selectString = selectPrefix + "from org.tolweb.hibernate.MappedNode node where node.pageId=" + pageId;
        if (onlyNamed) {
            // in this case the onlyNamed is because we are fetching for otherNames editing purposes
            // on working, so go ahead and order by their vertical order on the page
            selectString += " and node.name != '' order by node.orderOnPage";
        } else if (orderByOrderOnParent) {
            selectString += " order by node.orderOnParent";
        } else if (StringUtils.notEmpty(additionalQuery)) {
        	selectString += " and " + additionalQuery;
        }
        return getHibernateTemplate().find(selectString);
    }  
    public int getNumPicsForImageGallery(MappedPage page) {
    	List<Long> versionIds = getTillusVersionIds();
    	return getImageDAO().getNumGalleryTitleIllustrationsForNode(page.getMappedNode(), versionIds);
    }
    public List<Object[]> getPicsForImageGallery(MappedPage page, int numImages, int startIndex) {
    	// this query returns the image and the version id.  need to find
    	// the page name and node id that this version is a title illustration on
    	List<Object[]> results = getImageDAO().getGalleryTitleIllustrationsForNode(page.getMappedNode(), getTillusVersionIds(), startIndex);
    	Hashtable<Long, NodeImage> versionIdToImage = new Hashtable<Long, NodeImage>();
    	List<Long> versionIds = new ArrayList<Long>();
    	for (Object[] objects : results) {
    		// associate the version id with the image so we can do a lookup later
			versionIdToImage.put((Long) objects[1], (NodeImage) objects[0]);
			versionIds.add((Long) objects[1]);
		}
    	List<Object[]> pageInfoRows = getVersionIdsAndPageInfoFromVersionIds(versionIds);
    	List<Object[]> returnList = new ArrayList<Object[]>();
    	for (Object[] objects : pageInfoRows) {
			// first object is the version id so use that to do a lookup
    		NodeImage image = versionIdToImage.get(objects[0]);
    		String groupName = (String) objects[1];
    		Long nodeId = (Long) objects[2];
    		returnList.add(new Object[] {image, groupName, nodeId});
		}

    	Collections.sort(returnList, getImageIdComparator());
    	return returnList; 
    }
    private static Comparator<Object[]> getImageIdComparator() {
    	if (imageIdComparator == null) {
        	imageIdComparator = new Comparator<Object[]>() {
    			public int compare(Object[] o1, Object[] o2) {
    				NodeImage img1 = (NodeImage) o1[0];
    				NodeImage img2 = (NodeImage) o2[0];
    				return ((Integer) img1.getId()).compareTo(img2.getId());
    			}
        		
        	};    		
    	}
    	return imageIdComparator;
    }
    private List<Object[]> getVersionIdsAndPageInfoFromVersionIds(List<Long> versionIds) {
    	String queryString = getTillusPageInfoQueryStringPrefix(false);
    	queryString += " t.versionId " + StringUtils.returnSqlCollectionString(versionIds);
    	return getHibernateTemplate().find(queryString);
    }
    public List<Long> getTillusVersionIds() {
    	String queryString = "select t.versionId from org.tolweb.hibernate.MappedPage p join p.titleIllustrations as t";
    	Query query = getSession().createQuery(queryString);
    	query.setCacheable(true);
    	return query.list();
    }

	public List<Object[]> getRandomPicsForImageGallery(MappedPage page, int numImages) {
    	Hashtable<Number, Object[]> versionIdsToPageInfo = getVersionIdsAndPageInfo(page, true, numImages, true);
    	Hashtable<Number, NodeImage> images = getImageDAO().getImagesFromVersionIds(versionIdsToPageInfo.keySet());
    	List<Object[]> returnList = new ArrayList<Object[]>();
    	for (Number versionId : versionIdsToPageInfo.keySet()) {
    		Object[] pageInfo = versionIdsToPageInfo.get(versionId);
			returnList.add(new Object[] {images.get(versionId), pageInfo[0], pageInfo[1]});
		}
    	return returnList;
    }
    private String getTillusPageInfoQueryStringPrefix(boolean includeAncestor) {
    	String queryString = "select t.versionId, p.groupName, p.mappedNode.nodeId from ";
    	if (includeAncestor) {
    		queryString += "org.tolweb.hibernate.PageAncestor a, ";
    	}
    	queryString += "org.tolweb.hibernate.MappedPage p join p.titleIllustrations as t where ";
    	return queryString;
    }
    private Hashtable<Number, Object[]> getVersionIdsAndPageInfo(MappedPage page, boolean includeCurrentPage, 
    		int maxResults, boolean isRandom) {
    	String selectString = getTillusPageInfoQueryStringPrefix(true);
    	selectString += "a.ancestorId=" + page.getPageId() + " and p.pageId=a.pageId ";
        if (!includeCurrentPage) {
            selectString += " and p.pageId!=" + page.getPageId();
        }
        if (isRandom) {
        	selectString += "order by rand()";
        }
        Query query = getSession().createQuery(selectString);
        query.setMaxResults(maxResults);
        List<Object[]> results = query.list();
        Hashtable<Number, Object[]> versionIdsToPageInfo = new Hashtable<Number, Object[]>();
        //System.out.println("results are: " + results);
        for (Object[] nextArr : results) {
            // Construct the hashtable that takes a version id and associates it with a page group name
            // so that when we fetch the img location we have the group name
            Object[] groupAndNodeId = new Object[2];
            groupAndNodeId[0] = nextArr[1];
            groupAndNodeId[1] = nextArr[2];
            versionIdsToPageInfo.put((Long) nextArr[0], groupAndNodeId);
        }    
        return versionIdsToPageInfo;
    }
    
    /**
     * Returns a list of object arrays with the first element being the img loc, the
     * 2nd element being the group name of the page to link to and the 3rd element
     * being the node id of the page to link to
     */
    public List getRandomPicsForPage(MappedPage page, boolean includeCurrentPage) {
    	Hashtable<Number, Object[]> versionIdsToGroupNames = getVersionIdsAndPageInfo(page, includeCurrentPage, 6, true);
    	ArrayList<Object[]> returnList = new ArrayList<Object[]>();
        if (versionIdsToGroupNames.size() > 0) {
	        List imgLocs = getImageDAO().getVersionLocationsAndIdsForVersionsWithIds(versionIdsToGroupNames.keySet());
	        Iterator it2 = imgLocs.iterator();
	
	        while (it2.hasNext()) {
	            Object[] nextPair = (Object[]) it2.next();
	            Object[] toAdd = new Object[3];
	            // set the first object to be the image location
	            toAdd[0] = nextPair[0];
	            Number nextPairOne = (Number) nextPair[1];
	            Object[] groupAndNodeId = (Object[]) versionIdsToGroupNames.get(nextPairOne);
	            // set the second object to be the group name
	            toAdd[1] = groupAndNodeId[0];
	            // and the third to be the node id
	            toAdd[2] = groupAndNodeId[1];
	            returnList.add(toAdd);
	        }
        }
        return returnList;
    }

    public List getRandomPicsForPage(MappedPage page) {
        return getRandomPicsForPage(page, true);
    }    
	
	public void fillOutInMemoryValues(MappedPage page) {
	    fillOutPageContributorsData(page.getContributors());
	    Iterator it = page.getTitleIllustrations().iterator();
	    while (it.hasNext()) {
	        TitleIllustration nextIllustration = (TitleIllustration) it.next();
	        ImageVersion version = getImageDAO().getImageVersionWithId(nextIllustration.getVersionId());
//	        if (version == null) {
//	            System.out.println("DEBUG INFO : " + 
//	            		(getIsWorking() ? "(Working DAO)" : "") + 
//	            		"null image version is: !!!!!!!!!" + 
//	            		nextIllustration.getVersionId().intValue());
//	        }
	        nextIllustration.setVersion(version);
	    }
	}
	
	public void fillOutInMemoryValuesForAll(Collection pgs) {
	    Iterator it = pgs.iterator();
	    while (it.hasNext()) {
	        MappedPage page = (MappedPage) it.next();
	        fillOutInMemoryValues(page);
	    }	    
	}
	
	public void setImageDAO(ImageDAO value) {
	    imgDAO = value;
	}
	public ImageDAO getImageDAO() {
		return imgDAO;
	}
	
	public void clearCacheForPage(MappedPage page) {
	    try {
	        SessionFactory factory = getSessionFactory();
	        Long id = page.getPageId();
	        System.out.println("\n\n\nevicting page with id: " + id + "\n\n\n"); 
	        factory.evict(MappedPage.class, id);	        
	        factory.evictCollection("org.tolweb.hibernate.MappedPage.contributors", id);
	        for (Iterator iter = page.getTitleIllustrations().iterator(); iter.hasNext();) {
                TitleIllustration illus = (TitleIllustration) iter.next();
                factory.evict(TitleIllustration.class, illus.getId());
            }
	        factory.evictCollection("org.tolweb.hibernate.MappedPage.textSections", id);
	        factory.evictCollection("org.tolweb.hibernate.MappedPage.titleIllustrations", id);
	        factory.evictQueries();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	    
	}

	/* (non-Javadoc)
	 * @see org.tolweb.dao.PageDAO#savePage(org.tolweb.hibernate.MappedPage)
	 */
	public void savePage(MappedPage pg) {
		getHibernateTemplate().saveOrUpdate(pg);
	}
    
    public void addPage(MappedPage pg, Contributor contr) {
    	createInitialHistory(contr, pg);
        // make sure it has the initial text sections and a valid copyright date
        checkForNecessarySections(pg);
        // set the copyright year to this year
        pg.setCopyrightDate(new GregorianCalendar().get(Calendar.YEAR) + "");
        // we've just added a page, so it's "authorless" and the license should 
        // be set to the ToL default for pages (ContributorLicenseInfo.LICENSE_DEFAULT)
        pg.setUsePermission(ContributorLicenseInfo.LICENSE_DEFAULT);
        savePage(pg);
    }
    public MappedPage addPageForNode(MappedNode node, Contributor contributor, boolean writeAsList) {
    	// check to make sure that a page doesn't already exist!
    	Long pageId = getPageIdForNode(node);
    	if (pageId != null) {
    		return getPageForNode(node);
    	}
        MappedPage newPage = new MappedPage();
        newPage.setMappedNode(node);
        newPage.setWriteAsList(writeAsList);
        MappedPage parentPage = getPageNodeIsOn(node);
        newPage.setParentPageId(parentPage.getPageId());
        addPage(newPage, contributor);
        // set the ancestors for this new page
        try {
            Set parentPageAncestors = getAncestorPageIds(parentPage.getPageId());
            parentPageAncestors.add(newPage.getPageId());
            resetAncestorsForPage(newPage.getPageId(), parentPageAncestors);
            // need to check anyone who had parent page as an ancestor
            // and insert the new page as an ancestor
            //insertNewAncestorForPages(parentPage, newPage);
            
            
            // get all of the nodes on the other page and check whether they are a 
            // descendant of the new page node.  if they are, set their pageId
            // to be the newly created page
            List nodes = getNodesOnPage(parentPage, false);
            Long newPageId = newPage.getPageId();
            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                MappedNode nextNode = (MappedNode) iter.next();
                if (!nextNode.getNodeId().equals(node.getNodeId()) && 
                        getMiscNodeDAO().getNodeIsAncestor(nextNode.getNodeId(), node.getNodeId())) {
                    nextNode.setPageId(newPageId);
                    getWorkingNodeDAO().saveNode(nextNode);
                    pageId = getPageIdForNode(nextNode);
                    if (pageId != null) {
                    	updateParentPageIdForPage(pageId, newPageId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newPage;    	
    }
    public MappedPage addPageForNode(MappedNode node, Contributor contributor) {
    	return addPageForNode(node, contributor, false);
    }    
    
    /*private void checkIncompleteSubgroupsStatusForPage(MappedPage page) {
        boolean hasIncompleteSubgroups = page.getMappedNode().getHasIncompleteSubgroups();
        if (!hasIncompleteSubgroups) {
            List incompleteSubgroupNodes = getIncompleteSubgroupsNodesOnPage(page);
            for (Iterator iter = incompleteSubgroupNodes.iterator(); iter
                    .hasNext();) {
                MappedNode nextNode = (MappedNode) iter.next();
                if (nextNode.getHasIncompleteSubgroups()) {
                    // check to see if it has a page.  if it does, ignore it
                    // because it is the root of a page and the incomplete
                    // subgroups flag affects that page not the one it's on
                    if (!getNodeHasPage(nextNode)) {
                        hasIncompleteSubgroups = true;
                        break;
                    }
                }
            }
        }
        updateHasIncompleteSubgroupsForPage(page.getPageId(), hasIncompleteSubgroups);
    }*/
       
    public void addNewAncestorsForPages(List descendantPageIds, Set pageAncestorIds) {
    	String baseInsertString = "insert into PAGEANCESTORS (page_id, ancestor_id) values ";
    	String valuesString = StringUtils.buildAncestorSqlString(descendantPageIds, pageAncestorIds);
    	executeRawSQLUpdate(baseInsertString + valuesString);
    }
    
    public List getDescendantPageIds(Collection pageIds) {
    	return getHibernateTemplate().find("select distinct pa.pageId from org.tolweb.hibernate.PageAncestor pa where pa.ancestorId " + StringUtils.returnSqlCollectionString(pageIds));
    }
    public List getDescendantPageIds(Long pageId) {
    	return getDescendantPageIds(Arrays.asList(new Object[] {pageId}));
    }
    /**
     * Returns a list of object arrays that contain:
     * (1) cladename
     * (2) page status
     * (3) author ids
     * (4) last revision date
     * (5) initial publication date
     * (6) num title illustrations
     * @param rootPageId
     * @return
     */    
    public List<Object[]> getDescendantPageInfo(Long rootPageId) {
    	String queryString = "select p.groupName, p.status, elements(c), p.contentChangedDate, p.firstOnlineDate, elements(ti) from " + 
    		"org.tolweb.hibernate.MappedPage p join p.ancestors as an left join p.contributors as c join p.textSections as t " + 
    		"left join p.titleIllustrations as ti where an.pageId=" + rootPageId + " and t.text is not null and t.text!='' group by p.groupName"; 
    	return getHibernateTemplate().find(queryString);
    }
    public List<MappedPage> getDescendantPagesWithInfo(Long rootPageId) {
    	String queryString = "select distinct p from " + 
		"org.tolweb.hibernate.MappedPage p join p.ancestors as an join p.textSections as t " + 
		"where an.pageId=" + rootPageId + " and t.text is not null and t.text!='' and t.heading!='References'";
    	List pages = getHibernateTemplate().find(queryString);
    	fillOutInMemoryValuesForAll(pages);
    	return pages;
    }
    public void deleteAncestorPagesNotInBranch(Collection pageIds) {
    	String sqlInString = StringUtils.returnSqlCollectionString(pageIds);
    	executeUpdateQuery("delete from org.tolweb.hibernate.PageAncestor where pageId " + sqlInString + " and ancestorId not " + sqlInString);
    }

    public void insertNewAncestorForPages(MappedPage parentPage, MappedPage newPage) {
        insertNewAncestorForPages(parentPage.getPageId(), newPage.getPageId());
    }
    
    public void insertNewAncestorForPages(Long parentPageId, Long pageId) {
        Set pagesToIgnore = new HashSet();
        pagesToIgnore.add(parentPageId);
        pagesToIgnore.addAll(getChildrenPageIds(parentPageId));
        String selectString = "select page_id from PAGEANCESTORS where ancestor_id="
                + parentPageId
                + " and page_id not " + StringUtils.returnSqlCollectionString(pagesToIgnore);
        Set pageIdsWithOldPageAsAncestor = executeRawSQLSelectForLongs(selectString);
        if (pageIdsWithOldPageAsAncestor.size() > 0) {
            String insertString = "insert into PAGEANCESTORS (page_id, ancestor_id) values ";
            for (Iterator iter = pageIdsWithOldPageAsAncestor.iterator(); iter
                    .hasNext();) {
                Long nextPageId = (Long) iter.next();
                insertString += " (" + nextPageId + "," + pageId + ")";
                if (iter.hasNext()) {
                    insertString += ",";
                }
            }
            executeRawSQLUpdate(insertString);
        } else {
            try {
                throw new RuntimeException("page with no ancestors is: " + parentPageId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Returns a list of page ids that are the children of this page
     * @param pageId
     * @return
     */
    private List getChildrenPageIds(Long pageId) {
        return getHibernateTemplate().find("select p.pageId from org.tolweb.hibernate.MappedPage p where p.parentPageId=" + pageId);
    }

    /**
     * Checks to make sure there is 1 section for each immutable text 
     * section.  If there isn't, then a text section is added with the
     * missing immutable section.
     */
    private void checkForNecessarySections(MappedPage pg) {
        MappedTextSection discussion;//, references, information;
        
        createAndAddSectionIfAbsent(pg, TextSection.INTRODUCTION);
        createAndAddSectionIfAbsent(pg, TextSection.CHARACTERISTICS);        
        if (!pg.getMappedNode().getIsLeaf()) {
            discussion = checkForSectionNamed(pg, TextSection.DISCUSSION); 
            if (discussion == null) {
                createAndAddSectionIfAbsent(pg, TextSection.DISCUSSION);
            }
        }
    }
    
    private MappedTextSection createAndAddSectionIfAbsent(MappedPage page, String name) {
        MappedTextSection result = checkForSectionNamed(page, name);
        if (result == null) {
            result = new MappedTextSection();
            result.setHeading(name);
            result.setText("");
            getReorderHelper().addToSet(page.getTextSections(), result);
        }
        return result;
    }

    public MappedTextSection checkForSectionNamed(MappedPage page, String name) {
        Iterator it = page.getTextSections().iterator();
        while (it.hasNext()) {
            MappedTextSection next = (MappedTextSection) it.next();
            if (next.getHeading().equals(name)) {
                return next;
            }
        }
        return null;
    }    
	
	public void deletePage(MappedPage pg) {
		Long editHistoryId = pg.getEditHistoryId();
        getEditHistoryDAO().deleteHistoryWithId(editHistoryId);
        // Also need to delete the page ancestors for the page and all ancestor references to it
        executeUpdateQuery("delete from org.tolweb.hibernate.PageAncestor where pageId=" + pg.getPageId() + " or ancestorId=" + pg.getPageId());
        getHibernateTemplate().delete(pg);        
	}
    
    public void deletePageAndReassignNodes(MappedPage pg) {
        Long parentPageId = pg.getParentPageId();
        List nodesOnPage = getNodesOnPage(pg);
        for (Iterator iter = nodesOnPage.iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            nextNode.setPageId(parentPageId);
            getWorkingNodeDAO().saveNode(nextNode);
        }
        deletePage(pg);
        // update the incomplete subgroups status for the page as nodes likely changed
        // pages
        //checkIncompleteSubgroupsStatusForPage(getPageWithId(parentPageId));
    }
	
    public Set getAncestorPageIds(Long pageId) {
        Set ancestors = executeRawSQLSelectForLongs("select ancestor_id from PAGEANCESTORS where page_id=" + pageId);
        return ancestors;
    }
    
    public void resetAncestorsForPage(Long pageId, Collection ancestors) {
        Session session = null;
        try {
          session = getSession();
          Statement deleteStatement = session.connection().createStatement();
          String deleteString = "delete from PAGEANCESTORS where page_id=" + pageId;
          deleteStatement.executeUpdate(deleteString);
          String insertString = "insert into PAGEANCESTORS (page_id,ancestor_id) values ";
          Statement insertStatement = session.connection().createStatement();
          String valuesList = "";
          for (Iterator iter = ancestors.iterator(); iter.hasNext();) {
            Long nextAncestorId = (Long) iter.next();
            if (getPageExistsWithId(nextAncestorId)) {
                valuesList += "(" + pageId + "," + nextAncestorId + ")";
                if (iter.hasNext()) {
                    valuesList += ",";
                }
            }
          }
          String sql = insertString + valuesList;
          insertStatement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.PageDAO#getPageExistsWithId(java.lang.Long)
     */
    public boolean getPageExistsWithId(Long id) {
        List results = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.MappedPage p where p.pageId=" + id);
        if (results != null) {
            Integer count = (Integer) results.get(0);
            return count.intValue() == 1;
        } else {
            return false;
        }
    }
    
    public boolean getTextSectionExistsWithId(Long id) {
        List results = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.MappedTextSection s where s.textSectionId=" + id);
        if (results != null) {
            Integer count = (Integer) results.get(0);
            return count.intValue() == 1;
        } else {
            return false;
        }        
    }
    
    public int getTitleIllustrationBranchDefaultHeight(MappedPage page) {
        try {
            Set pageIds = getAncestorPageIds(page.getPageId());
            String sqlString = "select P.page_id, P.tillus_branch_default from PAGES P where P.tillus_branch_default is not NULL and P.page_id in ("
                + StringUtils.returnCommaJoinedString(pageIds) + ")";
            Session session = getSession();
            Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet results = selectStatement.executeQuery(sqlString);
			System.out.println("branch default query was: " + sqlString);
			Hashtable pageIdToBranchDefault = new Hashtable();
			while (results.next()) {
			    pageIdToBranchDefault.put(Integer.valueOf(results.getInt(1)), Integer.valueOf(results.getInt(2)));
			}
			if (pageIdToBranchDefault.size() > 0) {
			    // Get them in descending order since the page id that has the most ancestors is the closest branch default
			    sqlString = "SELECT page_id, count(ancestor_id) C FROM PAGEANCESTORS where page_id in (" + StringUtils.returnCommaJoinedString(pageIdToBranchDefault.keySet()) + ") group by page_id order by C desc";
			    results = selectStatement.executeQuery(sqlString);
			    if (results.next()) {
			        int pageId = results.getInt(1);
			        return ((Integer) pageIdToBranchDefault.get(Integer.valueOf(pageId))).intValue();
			    }
			}
			return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    
    public List getTitleIllustrationsPointingAtVersion(Long versionId) {
        ArrayList singleList = new ArrayList();
        singleList.add(versionId);
        return getTitleIllustrationsPointingAtVersionIds(singleList); 
    }
    
    public List getTitleIllustrationsPointingAtVersionIds(Collection versionIds) {
        return getHibernateTemplate().find("from org.tolweb.hibernate.TitleIllustration t where t.versionId in (" + StringUtils.returnCommaJoinedString(versionIds) + ")");
    }
    
    public void deleteTitleIllustrationsPointingAtVersionIds(List versionIds) {
        List illsToDelete = getTitleIllustrationsPointingAtVersionIds(versionIds);
        getHibernateTemplate().deleteAll(illsToDelete);
    }
    
    public void saveTitleIllustration(TitleIllustration ill) {
        getHibernateTemplate().saveOrUpdate(ill);
    }    
    
    public void setFirstOnlineDateForPageWithId(Long pageId, Date date) {
        try {
            Statement updateStatement = getSession().connection().createStatement();
            SimpleDateFormat dateFormat;
            dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
            String dateString = dateFormat.format(date);
            String sql = "update PAGES set page_firstonline='" + dateString + "' where page_id=" + pageId;
            System.out.println("sql string is: " + sql);
            updateStatement.executeUpdate(sql);
        } catch (DataAccessResourceFailureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HibernateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public String getPageTypeForPageWithId(int id) {
        List results = getHibernateTemplate().find("select n.isLeaf from org.tolweb.hibernate.MappedPage p join p.mappedNode as n where p.id=" + id);
        if (results.size() > 0) {
            Boolean isLeaf = (Boolean) results.get(0);
            if (isLeaf.booleanValue()) {
                return "Leaf";
            } else {
                return "Branch";
            }
        } else {
            return "";
        }
    }
    
    public int getNumAncestorsForPage(Long pgId) {
        List results = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.PageAncestor a where a.pageId=" + pgId);
        if (results.size() > 0) {
            int returnValue = ((Number) results.get(0)).intValue();
            if (returnValue == 0) {
                returnValue = A_REALLY_HIGH_NUMBER;
            }
            return returnValue;
        } else {
            // this is a really high number.  if we can't find any ancestors it's
            // got to show up at the end of a list (since this method is currently only used for sorting)
            return A_REALLY_HIGH_NUMBER;
        }
    }
    
    public Long getParentPageIdForPage(Long pgId) {
        List results = getHibernateTemplate().find("select p.parentPageId from org.tolweb.hibernate.MappedPage p where p.pageId=" + pgId);
        if (results.size() > 0) {
            return (Long) results.get(0);
        } else {
            return Long.valueOf(0);
        }
    }
    public List<Object[]> getNodeIdsAndEditHistoryIds() {
    	String queryString = "select n.nodeId, p.editHistoryId from org.tolweb.hibernate.MappedPage p join p.mappedNode as n";
    	return getHibernateTemplate().find(queryString);
    }
    public List getEditHistoryIdsForPageIds(Collection pageIds) {
        return getHibernateTemplate().find("select p.editHistoryId from org.tolweb.hibernate.MappedPage p where p.pageId " + StringUtils.returnSqlCollectionString(pageIds));
    }    
    public int getNumPagesWithIds(Collection ids) {
        String idsString = "(" + StringUtils.returnCommaJoinedString(ids) + ")";
        List results = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.MappedPage p where p.pageId in " + idsString);
        if (results != null && results.size() > 0) {
            return ((Number) results.get(0)).intValue();
        } else {
            return -1;
        }
    }
    public void copyPageToDB(MappedPage page) {
        getSession().replicate(page, ReplicationMode.OVERWRITE);
    }
    
    public MappedTextSection getTextSectionWithId(Long id) {
        return (MappedTextSection) getHibernateTemplate().load(MappedTextSection.class, id);
    }
    public void saveTextSection(MappedTextSection value) {
        getHibernateTemplate().saveOrUpdate(value);
    }
    public void deleteTextSection(MappedTextSection value) {
        getHibernateTemplate().delete(value);
    }
    public void updateHasIncompleteSubgroupsForPage(Long pageId, boolean value) {
        executeUpdateQuery("update org.tolweb.hibernate.MappedPage set hasIncompleteSubgroups=" + value + " where pageId=" + pageId);
    }
    public boolean getPageLeadsToCompletePage(Long pageId) {
    	String queryString = "select count(*) from PAGES p, PAGEANCESTORS pa where pa.ancestor_id=" + pageId + " and p.page_id=pa.page_id and " +
    		"(p.status='Complete' or p.status='Peer Reviewed' or p.status='ToL Reviewed')";
    	Set results = executeRawSQLSelectForLongs(queryString);
    	return results != null && results.size() > 0 && ((Number) results.iterator().next()).intValue() > 0;
    }
    public List getGroupNamesContributorOwns(Contributor contr) {
    	String queryString = "select p.groupName from org.tolweb.hibernate.MappedPage p join p.contributors as contributor where contributor.contributorId=" + 
    		contr.getId() + " and p.groupName is not null order by p.groupName";
    	return getHibernateTemplate().find(queryString);
    }
    public List getPagesForContributor(Contributor contr) {
    	return getPagesForContributor(contr.getId());
    }
    public List getPagesForContributor(int contrId) {
    	String queryString = "select p from org.tolweb.hibernate.MappedPage p join p.contributors as contributor " + 
    		"where contributor.contributorId=" + contrId;
    	return getHibernateTemplate().find(queryString);    	
    }
    /**
     * Returns the max order on page of all the nodes that live on this page
     * @param pageId
     * @return
     */
    public int getMaxOrderOnPage(Long pageId) {
    	Number maxOrderOnPage = (Number) getFirstObjectFromQuery("select max(n.orderOnPage) from org.tolweb.hibernate.MappedPage p join p.mappedNode as n where p.id=" + pageId);  
    	return maxOrderOnPage.intValue();
    }
    public void updateParentPageIdForPage(Long pageId, Long newParentPageId) {
    	executeUpdateQuery("update org.tolweb.hibernate.MappedPage set parentPageId=" + newParentPageId + " where pageId=" + pageId);
    }
    public void updateContentChangedDateForPage(Long pageId, Date lastEditedDate) {
    	Hashtable<String, Object> args = new Hashtable<String, Object>();
    	args.put("date", lastEditedDate);
    	executeUpdateQuery("update org.tolweb.hibernate.MappedPage set contentChangedDate=:date where pageId=" + pageId, args);
    }  
    public Collection getPageContributorIds() {
    	String queryString = "select distinct(contributor.contributorId) from org.tolweb.hibernate.MappedPage p join p.contributors as contributor";
    	return getHibernateTemplate().find(queryString);
    }
    public int getNumPagesForContributor(int contributorId) {
    	String queryString = "select count(*) from org.tolweb.hibernate.MappedPage p join p.contributors as contributor where contributor.contributorId=" + contributorId;
    	return ((Integer) getFirstObjectFromQuery(queryString)).intValue();
    }
	public List<Object[]> getPageIdsAndNodeIdsForPages(Collection ancestorIds) {
		return getHibernateTemplate().find("select p.id, n.nodeId from org.tolweb.hibernate.MappedPage p join p.mappedNode as n where p.id " + 
				StringUtils.returnSqlCollectionString(ancestorIds));
	}

	public List<Long> getPageIdsForNodeIds(Collection ancestorIds) {
		if (ancestorIds == null || ancestorIds.isEmpty()) {
			return new ArrayList<Long>();
		}
		return getHibernateTemplate().find("select p.id from org.tolweb.hibernate.MappedPage p join p.mappedNode as n where  n.nodeId " + 
				StringUtils.returnSqlCollectionString(ancestorIds));
	}
	
	public List<Object[]> getContributorsDatesAndPagesRecentlyChanged(Long pageId, Date lastChangedDate) {
		String rawSql = "select h.lastEditedContributorId, h.lastEditedDate, p.cladename, p.page_id from PAGES p " + 
			" join EditHistories h on p.editHistoryId=h.id join PAGEANCESTORS pa on pa.page_id=p.page_id " + 
			"where pa.ancestor_id=" + pageId + " and h.lastEditedDate >='" + 
			StringUtils.getMySqlDateString(lastChangedDate) + "' order by h.lastEditedDate desc";
        ResultSet results;
        Session session = null;
        List<Object[]> returnList = new ArrayList<Object[]>();
        try {
            session = getSession();
            Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            results = selectStatement.executeQuery(rawSql);
            
            while (results.next()) {
            	int contrId = results.getInt(1);
            	Timestamp lastEditedDate = results.getTimestamp(2);
            	String cladename = results.getString(3); 
            	pageId = results.getLong(4);            	
            	String contrName = getContributorDAO().getNameForContributorWithId(contrId);
            	returnList.add(new Object[] {contrName, lastEditedDate, cladename, pageId});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }		
		// TODO Auto-generated method stub
		return returnList;
	}
	
	/**
	 * Returns the number of branch pages with a status of complete or better descendant from pageId.
	 * @param pageId
	 * @return a number representing the number of complete or better branch pages descendant from pageId
	 */
    public int getBranchPagesLeadToCompletePages(Long pageId) {
    	String queryString = "SELECT COUNT(*) FROM PAGES p, PAGEANCESTORS pa, NODES n WHERE pa.ancestor_id = " + 
    		pageId + " AND p.page_id = pa.page_id AND p.node_id = n.node_id AND n.node_Leaf = 0 AND " +
    		"(p.status='Complete' or p.status='Peer Reviewed' or p.status='ToL Reviewed')";
    	Set results = executeRawSQLSelectForIntegers(queryString);
    	return (results != null && results.size() > 0) ? ((Number) results.iterator().next()).intValue() : 0;
    }
    
    /**
     * Returns the number of branch pages with a status of under-construction descendant from pageId.
     * @param pageId
     * @return a number representing the number of under-construction branch pages descendant from pageId.
     */
    public int getBranchPagesLeadToUnderConstructionPages(Long pageId) {
    	String queryString = "SELECT COUNT(*) FROM PAGES p, PAGEANCESTORS pa, NODES n WHERE pa.ancestor_id = " + 
    		pageId + " AND p.page_id = pa.page_id AND p.node_id = n.node_id AND n.node_Leaf = 0 AND " +
    		"(p.status='Under Construction')";
    	Set results = executeRawSQLSelectForLongs(queryString);
    	return (results != null && results.size() > 0) ? ((Number) results.iterator().next()).intValue() : 0;
    }    
    
    /**
     * Returns the number of branch pages with a status of temporary descendant from pageId.
     * @param pageId
     * @return a number representing the number of temporary branch pages descendant from pageId.
     */
    public int getBranchPagesLeadToTemporaryPages(Long pageId) {
    	String queryString = "SELECT COUNT(*) FROM PAGES p, PAGEANCESTORS pa, NODES n WHERE pa.ancestor_id = " + 
    		pageId + " AND p.page_id = pa.page_id AND p.node_id = n.node_id AND n.node_Leaf = 0 AND " +
    		"(p.status='Temporary' or p.status='Skeletal')";
    	Set results = executeRawSQLSelectForLongs(queryString);
    	return (results != null && results.size() > 0) ? ((Number) results.iterator().next()).intValue() : 0;
    }
    
	/**
	 * Returns the number of leaf pages with a status of complete or better descendant from pageId.
	 * @param pageId
	 * @return a number representing the number of complete or better leaf pages descendant from pageId
	 */    
    public int getLeafPagesLeadToCompletePages(Long pageId) {
    	String queryString = "SELECT COUNT(*) FROM PAGES p, PAGEANCESTORS pa, NODES n WHERE pa.ancestor_id = " + 
    		pageId + " AND p.page_id = pa.page_id AND p.node_id = n.node_id AND n.node_Leaf = 1 AND " +
    		"(p.status='Complete' or p.status='Peer Reviewed' or p.status='ToL Reviewed')";
    	Set results = executeRawSQLSelectForLongs(queryString);
    	return (results != null && results.size() > 0) ? ((Number) results.iterator().next()).intValue() : 0;
    }
    
    /**
     * Returns the number of leaf pages with a status of under-construction descendant from pageId.
     * @param pageId
     * @return a number representing the number of under-construction leaf pages descendant from pageId.
     */    
    public int getLeafPagesLeadToUnderConstructionPages(Long pageId) {
    	String queryString = "SELECT COUNT(*) FROM PAGES p, PAGEANCESTORS pa, NODES n WHERE pa.ancestor_id = " + 
    		pageId + " AND p.page_id = pa.page_id AND p.node_id = n.node_id AND n.node_Leaf = 1 AND " +
    		"(p.status='Under Construction')";
    	Set results = executeRawSQLSelectForLongs(queryString);
    	return (results != null && results.size() > 0) ? ((Number) results.iterator().next()).intValue() : 0;
    }
    
    /**
     * Returns the number of leaf pages with a status of temporary descendant from pageId.
     * @param pageId
     * @return a number representing the number of temporary leaf pages descendant from pageId.
     */    
    public int getLeafPagesLeadToTemporaryPages(Long pageId) {
    	String queryString = "SELECT COUNT(*) FROM PAGES p, PAGEANCESTORS pa, NODES n WHERE pa.ancestor_id = " + 
    		pageId + " AND p.page_id = pa.page_id AND p.node_id = n.node_id AND n.node_Leaf = 1 AND " +
    		"(p.status='Temporary' or p.status=\'Skeletal\')";
    	Set results = executeRawSQLSelectForLongs(queryString);
    	return (results != null && results.size() > 0) ? ((Number) results.iterator().next()).intValue() : 0;
    }
    
	public void reattachPage(Long pageId, Long oldNodeId, Long newNodeId) {
		Object[] args = new Object[] {newNodeId, pageId, oldNodeId};
		String fmt = "UPDATE PAGES SET node_id = %1$d WHERE page_id = %2$d AND node_id = %3$d";
		executeRawSQLUpdate(String.format(fmt, args));
	}    
    
	public MappedPage getRandomPage() {
		final String queryString = "select mpage.pageId from org.tolweb.hibernate.MappedPage as mpage " +
								   "order by rand()";
        Long pageId = (Long) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
            	Query query = session.createQuery(queryString);
            	query.setMaxResults(1);
            	return query.uniqueResult();
            }
        });
        
        return getPageWithId(pageId);
	}
	
    /**
     * @return Returns the editHistoryDAO.
     */
    public EditHistoryDAO getEditHistoryDAO() {
        return editHistoryDAO;
    }

    /**
     * @param editHistoryDAO The editHistoryDAO to set.
     */
    public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
        this.editHistoryDAO = editHistoryDAO;
    }

    /**
     * @return Returns the reorderHelper.
     */
    public ReorderHelper getReorderHelper() {
        return reorderHelper;
    }

    /**
     * @param reorderHelper The reorderHelper to set.
     */
    public void setReorderHelper(ReorderHelper reorderHelper) {
        this.reorderHelper = reorderHelper;
    }

    /**
     * @return Returns the nodeDAO.
     */
    public NodeDAO getMiscNodeDAO() {
        return miscNodeDAO;
    }

    /**
     * @param nodeDAO The nodeDAO to set.
     */
    public void setMiscNodeDAO(NodeDAO nodeDAO) {
        this.miscNodeDAO = nodeDAO;
    }

    /**
     * @return Returns the workingNodeDAO.
     */
    public NodeDAO getWorkingNodeDAO() {
        return workingNodeDAO;
    }

    /**
     * @param workingNodeDAO The workingNodeDAO to set.
     */
    public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
        this.workingNodeDAO = workingNodeDAO;
    }
}
