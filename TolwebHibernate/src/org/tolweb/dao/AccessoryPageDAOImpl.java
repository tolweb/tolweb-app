/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.EditComment;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Portfolio;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.PortfolioSection;
import org.tolweb.hibernate.Student;
import org.tolweb.hibernate.SupportMaterialDocument;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AccessoryPageDAOImpl extends AbstractPageContributorDAO implements AccessoryPageDAO {
	private NodeDAO nodeDAO;
	private ImageDAO imageDAO;
	private PageDAO pageDAO;
	private boolean isWorking;
	private EditHistoryDAO editHistoryDAO;
	
	private StringBuffer getNodeBasedReportQueryStringBuffer() {
	    StringBuffer sb = new StringBuffer("select page.menu, page.accessoryPageId, page.treehouseType from org.tolweb.hibernate.MappedAccessoryPage page");
	    sb.append(" join page.nodesSet as nodes join nodes.node as node where");
	    return sb;
	}
	
	public List getArticlesAndNotesForNode(MappedNode nd) {
		String queryString = "select page from org.tolweb.hibernate.MappedAccessoryPage page join page.nodesSet as nodes join nodes.node as node where " +
			"node.id=" + nd.getNodeId() + " and page.isTreehouse=0";
		return getHibernateTemplate().find(queryString);
	}
	
	public List getAccessoryPagesForNode(MappedNode nd) {
		String queryString = "select page from org.tolweb.hibernate.MappedAccessoryPage page join page.nodesSet as nodes join nodes.node as node where " +
		"node.id=" + nd.getNodeId();
		List accPages = getHibernateTemplate().find(queryString);
		return accPages;
	}
	
	public List getAccessoryPagesForNodes(String nodeIds) {
		String queryString = "select page from org.tolweb.hibernate.MappedAccessoryPage page join page.nodesSet as nodes join nodes.node as node where " +
		"node.id in (" + nodeIds + ")";
		List accPages = getHibernateTemplate().find(queryString);
		return accPages; 
	}
    
    public boolean getAccessoryPageExistsWithId(Long id) {
        String queryString = "select count(*) from org.tolweb.hibernate.MappedAccessoryPage page where page.accessoryPageId=" + id;
        Number numPages = (Number) getFirstObjectFromQuery(queryString);
        return numPages != null && numPages.intValue() > 0; 
    }
	
	public List getArticlesForNode(MappedNode nd) {
		return getBonusPagesForNode(nd, true);
	}
	
	public List getNotesForNode(MappedNode nd) {
		return getBonusPagesForNode(nd, false);
	}
	
	private List getBonusPagesForNode(MappedNode nd, boolean isArticle) {
	    StringBuffer queryStringBuffer = getNodeBasedReportQueryStringBuffer();
	    addGroupCriteria(queryStringBuffer, nd.getNodeId());
	    conditionallyAppendAnd(queryStringBuffer, true);
	    addBonusPageCriteria(queryStringBuffer);
	    conditionallyAppendAnd(queryStringBuffer, true);	    
	    addShowOnMenuCriteria(queryStringBuffer);
	    conditionallyAppendAnd(queryStringBuffer, true);	    
	    addArticlesCriteria(queryStringBuffer, isArticle);
	    return returnAuxiliaryPages(queryStringBuffer.toString());		
	}
	
	public List getTreehousesForNode(MappedNode nd) {
	    StringBuffer queryStringBuffer = getNodeBasedReportQueryStringBuffer();
	    addGroupCriteria(queryStringBuffer, nd.getNodeId());
	    conditionallyAppendAnd(queryStringBuffer, true);	    
	    addTreehouseCriteria(queryStringBuffer);
	    conditionallyAppendAnd(queryStringBuffer, true);
	    addShowOnMenuCriteria(queryStringBuffer);
	    queryStringBuffer.append(" order by page.treehouseType, page.menu asc");
	    return returnAuxiliaryPages(queryStringBuffer.toString());
	}
	
	private List returnAuxiliaryPages(String queryString) {
	    if (isWorking) {
	        return getHibernateTemplate().find(queryString);
	    } else {
	        List list;
	        try {
	            Session session = getSession();
	            Query pageQuery = session.createQuery(queryString);
	            pageQuery.setCacheable(true);
	            list = pageQuery.list();
	        } catch (Exception e) {
	            list = new ArrayList();
	            e.printStackTrace();
	        }
	        return list;
	    }
	}
	
	public void addShowOnMenuCriteria(StringBuffer queryStringBuffer) {
	    queryStringBuffer.append(" nodes.showLink=1 ");
	}
	
	public void addAccessoryPageWithId(Long id, int discriminator) {
		addObjectWithId(id + "," + discriminator, "ACCESSORY_PAGES", "id,discriminator");
	}

	public void addAccessoryPage(MappedAccessoryPage page, Contributor contributor) {
		createInitialHistory(contributor, page);
		page.setContributorId(contributor.getId());
		getHibernateTemplate().save(page);
	}

	public void saveAccessoryPage(MappedAccessoryPage page) {
		getHibernateTemplate().update(page);
	}

	public void deleteAccessoryPage(MappedAccessoryPage page) {
		getHibernateTemplate().delete(page);
	}
	
	public List getSubmittedTreehouses() {
		List returnVals = getHibernateTemplate().find("select page from org.tolweb.hibernate.MappedAccessoryPage as " + 
			" page where page.isSubmitted = 1 and page.isTreehouse=1 order by page.submissionDate desc");
		return fillOutInMemoryFieldsForAll(returnVals);
	}
    
    public List getSubmittedTreehousesForTeacher(Contributor teacher) {
        ArrayList teacherTreehouses = new ArrayList();
        List treehouses = getTreehousesSubmittedToTeacher();
        for (Iterator iter = treehouses.iterator(); iter.hasNext();) {
            MappedAccessoryPage treehouse = (MappedAccessoryPage) iter.next();
            Contributor submittedContributor = treehouse.getSubmittedContributor();
            if (Student.class.isInstance(submittedContributor)) {
                if (((Student) submittedContributor).getTeacher().getId() == teacher.getId()) {
                    teacherTreehouses.add(treehouse);
                }
            }
        }
        // add in any treehouses the teacher has submitted to the end of the list
        teacherTreehouses.addAll(getContributorSubmittedTreehouses(teacher));
        return teacherTreehouses;
    }
    
    public List getContributorSubmittedTreehouses(Contributor contr) {
        List treehouses = getHibernateTemplate().find("from org.tolweb.hibernate.MappedAccessoryPage where isSubmitted=1 and submittedContributorId=" 
                + contr.getId() + " order by submissionDate desc");
        return fillOutInMemoryFieldsForAll(treehouses);
    }
    
    public List getTreehousesSubmittedToTeacher() {
        List treehouses = getHibernateTemplate().find("from org.tolweb.hibernate.MappedAccessoryPage where isSubmittedToTeacher=1 order by submissionDate desc");
        return fillOutInMemoryFieldsForAll(treehouses);
    }
	
	public List getSubmittedArticlesAndNotes() {
		List returnVals = getHibernateTemplate().find("select page from org.tolweb.hibernate.MappedAccessoryPage as " + 
			" page where page.isSubmitted = 1 and page.isTreehouse=0 order by page.submissionDate desc");
		return fillOutInMemoryFieldsForAll(returnVals);
	}	

	public List getAccessoryPagesMatchingCriteria(Map args) {
		Collection contrIds = (Collection) args.get(AccessoryPageDAO.CONTRIBUTOR_IDS);
		Contributor contr = (Contributor) args.get(AccessoryPageDAO.CONTRIBUTOR);
		String group = (String) args.get(AccessoryPageDAO.GROUP);
		Long groupId = (Long) args.get(AccessoryPageDAO.GROUP_ID);
		String copyYear = (String) args.get(AccessoryPageDAO.COPY_YEAR);
		String title = (String) args.get(AccessoryPageDAO.TITLE);
		String id = (String) args.get(AccessoryPageDAO.ID);
		Integer bonusPages = (Integer) args.get(AccessoryPageDAO.BONUSPAGES);
		Integer treehouses = (Integer) args.get(AccessoryPageDAO.TREEHOUSES);
		Integer biographies = (Integer) args.get(AccessoryPageDAO.BIOGRAPHIES);
		Integer limit = (Integer) args.get(AccessoryPageDAO.LIMIT);
		//AccessoryPage exampleAccPage = new AccessoryPage();
		boolean seenOne = false;
		List returnValue;		
		StringBuffer queryStringBuffer = new StringBuffer("select distinct page from org.tolweb.hibernate.MappedAccessoryPage as page ");
		if (group != null || groupId != null) {
		    queryStringBuffer.append(" join page.nodesSet as nodes join nodes.node as node ");
		}
		if (contr != null || contrIds != null) {
			queryStringBuffer.append(" left join page.contributors as contributors ");
		}
		queryStringBuffer.append(" where ");
		if (group != null) {
			List nodes= nodeDAO.findNodesNamed(group);
			// If no nodes are found with the given name, we can't match anything, so return
			if (nodes.size() == 0) {
				return new ArrayList();
			}
			queryStringBuffer.append(" node.id in (");
			queryStringBuffer.append(getCommaSeparatedNodeIdsString(nodes));
			queryStringBuffer.append(")");
			seenOne = true;	
		} else if (groupId != null) {
		    addGroupCriteria(queryStringBuffer, groupId);
			seenOne = true;			    
		}
		if (bonusPages != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
		    addBonusPageCriteria(queryStringBuffer);
		} 
		if (treehouses != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
		    addTreehouseCriteria(queryStringBuffer);
		}
		if (biographies != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
		    addBiographyCriteria(queryStringBuffer);
		}
		if (contr != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
			queryStringBuffer.append("( contributors.contributorId = :contributor_id or page.contributorId = :contributor_id)");
		} else if (contrIds != null) {
			seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
			queryStringBuffer.append(getContributorIdsToSearch(contrIds));
		}
		if (copyYear != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
			copyYear = "%" + copyYear + "%";
			queryStringBuffer.append(" page.copyrightYear like :copy_year");
		}
		if (id != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
		    queryStringBuffer.append(" page.accessoryPageId = :id");
		}
		if (title != null) {
		    seenOne = conditionallyAppendAnd(queryStringBuffer, seenOne);
			title = "%" + title + "%";
			queryStringBuffer.append("(page.pageTitle like :page_title or page.menu like :page_title)");
		}
		queryStringBuffer.append(" order by page.lastEditedDate desc");
		Session session = getSession();
		try {
			Query query = session.createQuery(queryStringBuffer.toString());
			if (contr != null) {
				query.setInteger("contributor_id", contr.getId());
			}
			if (title != null) {
				query.setString("page_title", title);
			}
			if (copyYear != null) {
				query.setString("copy_year", copyYear);
			}
			if (id != null) {
			    query.setString("id", id);
			}
			if (limit != null) {
				query.setMaxResults(limit.intValue());
			}
			returnValue =  query.list();
		} catch (Exception e) {
			e.printStackTrace();
			returnValue = new ArrayList();
		}
		return fillOutInMemoryFieldsForAll(returnValue);
	}
	
	private void addGroupCriteria(StringBuffer queryStringBuffer, Long groupId) {
	    queryStringBuffer.append(" node.id=");
	    queryStringBuffer.append(groupId);
	}
	
	private void addBiographyCriteria(StringBuffer queryStringBuffer) {
	    queryStringBuffer.append(" page.isTreehouse = 1 and page.treehouseType = ");
	    queryStringBuffer.append(MappedAccessoryPage.BIOGRAPHY);
	}
	
	private void addBonusPageCriteria(StringBuffer queryStringBuffer) {
	    queryStringBuffer.append(" page.isTreehouse = 0");
	}
	
	private void addArticlesCriteria(StringBuffer queryStringBuffer, boolean isArticle) {
		if (isArticle) {
			queryStringBuffer.append(" page.isArticle = 1");
		} else {
			queryStringBuffer.append(" page.isArticle = 0");
		}
	}
	
	private void addTreehouseCriteria(StringBuffer queryStringBuffer) {
	    queryStringBuffer.append(" page.isTreehouse = 1");	    	    
	}
	
	private boolean conditionallyAppendAnd(StringBuffer queryStringBuffer, boolean seenOne) {
	    if (seenOne) {
	        queryStringBuffer.append(" and ");
	    }
	    return true;
	}
	
	public MappedAccessoryPage getAccessoryPageWithId(int id) {	
		return getAccessoryPageWithId(Long.valueOf(id));
	}
	
	public MappedAccessoryPage getAccessoryPageWithId(Long id) {
		try {
			MappedAccessoryPage page = (MappedAccessoryPage) getHibernateTemplate().load(MappedAccessoryPage.class, id);
			if (page != null) {
				return fillOutInMemoryFields(page);
			} else {
				return null;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return null;		
		}
	}	
	
	public void setNodeDAO(NodeDAO value) {
		nodeDAO = value;
	}
	
	public String getCommaSeparatedNodeIdsString(List nodes) {
		Iterator it = nodes.iterator();
		String returnString = "";
		while (it.hasNext()) {
			MappedNode node = (MappedNode) it.next();
			returnString += node.getNodeId();
			if (it.hasNext()) {
				returnString += ", ";
			}
		}
		return returnString;
	}
	
	public void clearCacheForPage(MappedAccessoryPage page) {
	    clearCacheForPage(page, false);
	}
	
	public void clearCacheForPage(MappedAccessoryPage page, boolean refreshNodes) {
	    try {
	        SessionFactory factory = getSessionFactory();
	        Long id = page.getAccessoryPageId();
	        System.out.println("just evicted acc page with id: " + id);
	        factory.evict(MappedAccessoryPage.class, id);	        
	        factory.evictCollection("org.tolweb.hibernate.MappedAccessoryPage.contributors", id);
	        factory.evictCollection("org.tolweb.hibernate.MappedAccessoryPage.nodesSet", id);
	        factory.evictCollection("org.tolweb.hibernate.MappedAccessoryPage.internetLinks", id);
	        if (refreshNodes) {
	            Iterator it = page.getNodesSet().iterator();
	            while (it.hasNext()) {
	                AccessoryPageNode node = (AccessoryPageNode) it.next();
	                nodeDAO.clearCacheForNode(node.getNode());
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	    
	}
	
	private List fillOutInMemoryFieldsForAll(List value) {
		Iterator it = value.iterator();
		while (it.hasNext()) {
			MappedAccessoryPage nextPage = (MappedAccessoryPage) it.next();
			fillOutInMemoryFields(nextPage);
		}
		return value;
	}

	/**
	 * Does the job of going through the contributorDAO in order to fill out Contributor details
	 * since the Contributors are currently stored in a different database than the accessory 
	 * pages.
	 * @param page The AccessoryPage to fix contributors on.
	 * @return
	 */
	private MappedAccessoryPage fillOutContributorData(MappedAccessoryPage page) {
	    fillOutPageContributorsData(page.getContributors());
		if (page.getSubmittedContributorId() != null && page.getSubmittedContributorId().intValue() != 0) {
			page.setSubmittedContributor(contributorDAO.getContributorWithId(page.getSubmittedContributorId().toString()));
		}
		if (page.getLastEditedContributorId() != null && page.getLastEditedContributorId().intValue() != 0) {
		    page.setLastEditedContributor(contributorDAO.getContributorWithId(page.getLastEditedContributorId().toString()));
		}
		if (page.getContributorId() > 0) {
		    page.setContributor(contributorDAO.getContributorWithId(page.getContributorId() + ""));
		}
        for (Iterator iter = page.getEditComments().iterator(); iter.hasNext();) {
            EditComment comment = (EditComment) iter.next();
            if (comment.getCommentContributorId() > 0) {
                comment.setCommentContributor(contributorDAO.getContributorWithId("" + comment.getCommentContributorId()));
            }
        }
		return page;
	}
	
	private MappedAccessoryPage fillOutInMemoryFields(MappedAccessoryPage page) {
	    fillOutContributorData(page);
	    page.getPrimaryAttachedNode();
        if (page.getStateStandardsId() != null) {
            page.setStateStandardsDocument((Document) imageDAO.getImageWithId(page.getStateStandardsId().intValue()));
        }
        if (page.getNationalStandardsId() != null) {
            page.setNationalStandardsDocument((Document) imageDAO.getImageWithId(page.getNationalStandardsId().intValue()));
        }	    
	    if (TeacherResource.class.isInstance(page)) {
	        TeacherResource resource = (TeacherResource) page;
	        // Fetch the standards documents if they exist 
	        for (Iterator iter = resource.getSupportMaterialDocuments().iterator(); iter.hasNext();) {
                SupportMaterialDocument supportDoc = (SupportMaterialDocument) iter.next();
                if (supportDoc.getDocumentId() != null) {
                    supportDoc.setDocument((Document) imageDAO.getImageWithId(supportDoc.getDocumentId().intValue()));
                }
            }
	    }
	    if (page.getPortfolio() != null) {
	        // Go through the portfolio pages and sections and fetch as necessary
	        for (Iterator iter = page.getPortfolio().getSections().iterator(); iter.hasNext();) {
                PortfolioSection currentSection = (PortfolioSection) iter.next();
                for (Iterator it = currentSection.getPages().iterator(); it
                        .hasNext();) {
                    PortfolioPage nextPage = (PortfolioPage) it.next();
                    if (!nextPage.getIsExternal()) {
                        int destinationId = nextPage.getDestinationId();
                        String pageTitle, pageType;
	                    if (nextPage.getDestinationType() == PortfolioPage.ACC_PAGE_DESTINATION) {
	                        pageTitle = getPageMenuForAccessoryPageWithId(destinationId);
	                        pageType = getPageTypeForAccessoryPageWithId(destinationId);
	                        nextPage.setTreehouseType(getPageTypeByteForAccessoryPageWithId(destinationId));
	                    } else {
	                        pageTitle = pageDAO.getGroupNameForPage(Long.valueOf(destinationId));
	                        pageType = pageDAO.getPageTypeForPageWithId(destinationId);
	                    }
                        nextPage.setPageTitle(pageTitle);
                        nextPage.setPageType(pageType);	                    
                    }
                }
            }
	    }
	    return page;
	}
	
	public byte getPageTypeByteForAccessoryPageWithId(int id) {
	    List results = getHibernateTemplate().find("select p.treehouseType from org.tolweb.hibernate.MappedAccessoryPage p where p.accessoryPageId=" + id);
	    if (results.size() > 0) {
	        Byte trType = (Byte) results.get(0);
	        return trType.byteValue();
	    } else {
	        return (byte) 0;
	    }
	}
	
	public String getPageTypeForAccessoryPageWithId(int id) {
	    byte trType = getPageTypeByteForAccessoryPageWithId(id);
        if (trType > 0) {
            return MappedAccessoryPage.getTreehouseTypeString(trType);
        } else {
            // Not a treehouse, so check to see if it is an article or note
            if (getPageIsArticle(id)) {
                return MappedAccessoryPage.ARTICLE;
            } else {
                return MappedAccessoryPage.NOTE;
            }
        }
	}
	
    public String getPageTitleForAccessoryPageWithId(int id) {
        List results = getHibernateTemplate().find("select p.pageTitle from org.tolweb.hibernate.MappedAccessoryPage p where p.accessoryPageId=" + id);
        if (results.size() > 0) {
            return (String) results.get(0);
        } else {
            return "";
        }
    }
    
    public String getPageMenuForAccessoryPageWithId(int id) {
        List results = getHibernateTemplate().find("select p.menu from org.tolweb.hibernate.MappedAccessoryPage p where p.accessoryPageId=" + id);
        if (results.size() > 0) {
            return (String) results.get(0);
        } else {
            return "";
        }
    }    
    
    public boolean getPageIsArticle(int id) {
        List results = getHibernateTemplate().find("select p.isArticle from org.tolweb.hibernate.MappedAccessoryPage p where p.accessoryPageId=" + id);
        if (results.size() > 0) {
            Boolean isArticle = (Boolean) results.get(0);
            return isArticle.booleanValue();
        } else {
            return false;
        }
    }
	
	public void setIsWorking(boolean value) {
	    isWorking = value;
	}
	
	public boolean getIsWorking() {
	    return isWorking;
	}
	
	public void reattachAccessoryPagesForNodes(Collection nodeIds, MappedNode newParentNode) {
		if (newParentNode == null || nodeIds == null || nodeIds.size() == 0) {
			// nothing to do here, move along
			return;
		}
		try {
			// we need to make sure that some pages don't get attached to the new node twice,
			// so look for those pages that are attached to both a deleted node and the new parent
			// and delete the attachment to the deleted node so that the update doesn't affect it
			String duplicateNodesSQL = "select an1.acc_page_id, an2.node_id from Acc_Pages_To_Nodes an1 join " + 
				"Acc_Pages_To_Nodes an2 on an1.acc_page_id=an2.acc_page_id and an1.node_id=" + newParentNode.getNodeId() + 
				" and an2.node_id " + StringUtils.returnSqlCollectionString(nodeIds);			
            Session session = getSession();
            Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = selectStatement.executeQuery(duplicateNodesSQL);
            while (results.next()) {
            	Long accPageId = results.getLong(1);
            	Long nodeId = results.getLong(2);
            	deleteAccPageAttachment(accPageId, nodeId);
            }
            // now check for pages that are attached to multiple deleted nodes
            String accPagesSQL = "select acc_page_id, node_id from Acc_Pages_To_Nodes where node_id " + StringUtils.returnSqlCollectionString(nodeIds);
            selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            results = selectStatement.executeQuery(accPagesSQL);
            // keep track of acc_page_ids we've already seen
            HashSet seenPageIds = new HashSet<Long>();
            while (results.next()) {
            	Long accPageId = results.getLong(1);
            	Long nodeId = results.getLong(2);
            	if (seenPageIds.contains(accPageId)) {
            		// already seen this page, so delete this attachment so it doesn't break
            		// the update later
	            	deleteAccPageAttachment(accPageId, nodeId);
            	} else {
            		seenPageIds.add(accPageId);
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	String rawSQL = "update Acc_Pages_To_Nodes set node_id=" + newParentNode.getNodeId() + " where node_id " + 
    		StringUtils.returnSqlCollectionString(nodeIds);
    	try {
    		executeRawSQLUpdate(rawSQL);
    	} catch (Exception e) {
    		System.err.println("CANNOT RE-ATTACH ACC PAGES FROM NODES " + StringUtils.returnCommaJoinedString(nodeIds) + " TO NODE: " + newParentNode);
    	}		
	}

	private void deleteAccPageAttachment(Long accPageId, Long nodeId) {
		String deleteSql = "delete from Acc_Pages_To_Nodes where acc_page_id=" + accPageId + " and node_id=" + nodeId;
		executeRawSQLUpdate(deleteSql);
	}

    /* (non-Javadoc)
     * @see org.tolweb.dao.AccessoryPageDAO#reattachAccessoryPagesForNode(org.tolweb.hibernate.MappedNode, org.tolweb.hibernate.MappedNode)
     */
    public void reattachAccessoryPagesForNode(MappedNode nodeToBeDeleted, MappedNode newParentNode) {
    	ArrayList idList = new ArrayList();
    	idList.add(nodeToBeDeleted.getNodeId());
    	reattachAccessoryPagesForNodes(idList, newParentNode);
    }
    
    public void copyAccessoryPageToDB(MappedAccessoryPage page) {
        getSession().replicate(page, ReplicationMode.OVERWRITE);
        System.out.println("foo");
    }
    
    /**
     * @return Returns the imageDAO.
     */
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
    /**
     * @param imageDAO The imageDAO to set.
     */
    public void setImageDAO(ImageDAO imgDAO) {
        this.imageDAO = imgDAO;
    }
    
    public void savePortfolio(Portfolio portfolio) {
        getHibernateTemplate().saveOrUpdate(portfolio);
    }
    public Portfolio getPortfolioWithId(Long id) {
    	return (Portfolio) getObjectWithId(Portfolio.class, id);
    }
    public void addPortfolioWithId(Long id) {
    	addObjectWithId(id, "Portfolios", "id");
    }
    /**
     * @return Returns the pageDAO.
     */
    public PageDAO getPageDAO() {
        return pageDAO;
    }
    /**
     * @param pageDAO The pageDAO to set.
     */
    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }

    public List getTreehousesForProject(ClassroomProject project) {
        List contributorIds = contributorDAO.getStudentIdsForProject(project);
        if (contributorIds != null && contributorIds.size() > 0) {
	        String contributorIdsString = "(" + StringUtils.returnCommaJoinedString(contributorIds) + ")";
	        return fillOutInMemoryFieldsForAll(getHibernateTemplate().find("select distinct page from org.tolweb.hibernate.MappedAccessoryPage page left join page.contributors as contributors where contributors.contributorId in " + contributorIdsString + " or page.contributorId in " + contributorIdsString));
        } else {
        	return new ArrayList();
        }
    }
    
    public List findTreehousesInProject(String title, String projectId, Collection studentIds) {
    	return null;
    }
    
    private String getContributorIdsToSearch(Collection contributorIds) {
    	String inString = StringUtils.returnSqlCollectionString(contributorIds);
    	String queryString = "(contributors.contributorId " + inString + " or page.contributorId " + inString + ")";
    	return queryString;
    }

	public List getMostRecentlyEditedTreehouses(Contributor contributor) {
		if (contributor != null) {
			Hashtable args = new Hashtable();
			args.put(AccessoryPageDAO.CONTRIBUTOR, contributor);
			args.put(AccessoryPageDAO.LIMIT, 2);
			List results = getAccessoryPagesMatchingCriteria(args); 
			return results;
		} else {
			return new ArrayList();
		}
	}

	public List getArticleNoteTitlesIdsForContributor(Contributor contr) {
		return getAccPageTitlesIdsForContributor(contr, false);
	}
	public List getTreehouseTitlesIdsForContributor(Contributor contr) {
		return getAccPageTitlesIdsForContributor(contr, true);
	}
	public List getMostRecentlyEditedTreehouses(int num) {
		Query query = getSession().createQuery("from org.tolweb.hibernate.MappedAccessoryPage order by lastEditedDate desc");
		query.setMaxResults(num);
		return query.list();
	}
	private List getAccPageTitlesIdsForContributor(Contributor contr, boolean isTreehouse) {
		String queryString = "select p.menu, p.accessoryPageId, p.isArticle from org.tolweb.hibernate.MappedAccessoryPage p join p.contributors as contributor ";
		queryString += "where contributor.contributorId=" + contr.getId() + " and p.isTreehouse=" + isTreehouse;
		queryString += " order by p.menu";
		return getHibernateTemplate().find(queryString);
	}
	
	public List getAccessoryPagesForContributor(Contributor contr) {
		String queryString = "select p from org.tolweb.hibernate.MappedAccessoryPage p join p.contributors as contributor ";
		queryString += "where contributor.contributorId=" + contr.getId();
		queryString += " order by p.menu";
		return getHibernateTemplate().find(queryString);
	}
	
	public List getContributorIdsForTreehousesAttachedToNodeIds(Collection nodeIds) {
		return getContributorIdsForAccessoryPagesAttachedToNodeIds(nodeIds, true);
	}
	public List getContributorIdsForArticleNotesAttachedToNodeIds(Collection nodeIds) {
		return getContributorIdsForAccessoryPagesAttachedToNodeIds(nodeIds, false);		
	}
	private List getContributorIdsForAccessoryPagesAttachedToNodeIds(Collection nodeIds, boolean isTreehouse) {
		String queryString = "select distinct contributor.contributorId from org.tolweb.hibernate.MappedAccessoryPage p join p.contributors as contributor join p.nodesSet as nodes";
		queryString += " where nodes.node.nodeId " + StringUtils.returnSqlCollectionString(nodeIds) + " and p.isTreehouse=" + isTreehouse;
		queryString += " and contributor.isAuthor=1";
		return getHibernateTemplate().find(queryString);
	}

	public EditHistoryDAO getEditHistoryDAO() {
		return editHistoryDAO;
	}

	public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
		this.editHistoryDAO = editHistoryDAO;
	}

	public Collection getArticleNoteContributorIds() {
		String queryString = "select distinct contributor.contributorId from org.tolweb.hibernate.MappedAccessoryPage p join p.contributors as contributor ";
		queryString += " where p.isTreehouse=0";
		return getHibernateTemplate().find(queryString);
	}
	public int getNumPagesForContributor(int contributorId) {
		String queryString = "select count(*) from org.tolweb.hibernate.MappedAccessoryPage p join p.contributors as contributor ";
		queryString += " where contributor.contributorId=" + contributorId + " and p.isTreehouse=0";
		return ((Integer) getFirstObjectFromQuery(queryString)).intValue();
	}
	
	public void reattachAccessoryPage(Long accPageId, Long oldNodeId, Long newNodeId) {
		Object[] args = new Object[] {newNodeId, accPageId, oldNodeId};
		String fmt = "UPDATE Acc_Pages_To_Nodes SET node_id = %1$d WHERE acc_page_id = %2$d AND node_id = %3$d";
		executeRawSQLUpdate(String.format(fmt, args));    		
	}
}
