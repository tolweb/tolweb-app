/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.List;
import java.util.Vector;

import org.hibernate.SessionFactory;

import org.apache.xmlrpc.XmlRpcClientLite;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TreeGif;
import org.tolweb.treegrow.main.XMLConstants;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeGifDAOImpl extends BaseDAOImpl implements TreeGifDAO {
    private String workingURL, URL;
    private PageDAO publicPageDao, workingPageDao;

    public boolean getTreeGifExistsForPage(MappedPage page) {
        List gifs = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.TreeGif as t where t.pageId=" + page.getPageId());
        if (gifs != null && gifs.size() > 0) {
            return ((Number) gifs.get(0)).intValue() > 0;
        } else {
            return false;
        }
    }

    public TreeGif getTreeGifForPage(MappedPage pg, boolean regenerate, boolean isWorking) {
        if (!regenerate && !isWorking) {
            //System.out.println("\n\nnot regenerate and not working\n\n");
            List gifs;
            try {
                gifs = getHibernateTemplate().find("from org.tolweb.hibernate.TreeGif as t where t.pageId=" + pg.getPageId());
                //System.out.println("\n\nexecuted query: " + gifs);
    	        if (gifs != null && gifs.size() > 0) {
    	            return (TreeGif) gifs.get(0);
    	        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Use the xml rpc stuff setup to call the perl script on tolweb.org to get the img generated
        return doRpcCall(pg, isWorking);
    }
    
    public TreeGif getNewTreeGifForPage(MappedPage pg) {
        return (TreeGif) getFirstObjectFromQuery("from org.tolweb.hibernate.TreeGif as t where t.pageId=" + pg.getPageId());
    }
    
    public TreeGif getTreeGifForPage(MappedPage pg) {
        return getTreeGifForPage(pg, false, false);
    }
    
    public void saveTreeGif(TreeGif treeGif) {
    	getHibernateTemplate().saveOrUpdate(treeGif);
    }
    
    /**
     * Not used anymore.  This calls the old perl treeDrawing stuff.
     * @param pg
     * @param isWorking
     * @deprecated
     * @return
     */
    private TreeGif doRpcCall(MappedPage pg, boolean isWorking) {
        //System.out.println("doing rpc call");
        Vector retVal;
        try {
            String serverUrl;
            if (isWorking) {
                serverUrl = getWorkingURL();
            } else {
                serverUrl = getURL();
            }
            XmlRpcClientLite xmlrpc = new XmlRpcClientLite(serverUrl);
            String methodName = "TreeGifFacade.facade_draw_tree_gif";
            Vector params = new Vector();
            MappedNode nd = pg.getMappedNode();
            PageDAO dao;
            if (isWorking) {
                dao = getWorkingPageDAO();
            } else {
                dao = getPublicPageDAO();
            }
            MappedNode parentNode = dao.getNodeForPageNodeIsOn(nd);
            params.add(nd.getActualPageTitle(false, false, true));
            params.add(parentNode.getActualPageTitle(false, false, true));
            params.add(Integer.valueOf(nd.getNodeId().intValue()));
            if (pg.getStatus().equals(MappedPage.COMPLETE) || pg.getStatus().equals(MappedPage.PEERREVIEWED) || 
                    pg.getStatus().equals(MappedPage.UNDERCONSTRUCTION) || pg.getStatus().equals(XMLConstants.COMPLETE) ||
                    pg.getStatus().equals(XMLConstants.PEER_REVIEWED) || pg.getStatus().equals(XMLConstants.UNDER_CONSTRUCTION) ||
                    pg.getStatus().equals(XMLConstants.TOL_REVIEWED)) {
                params.add("Black");
            } else {
                params.add("Gray");
            }
            // refresh parameter
            if (isWorking) {
                params.add(Integer.valueOf(1));
            } else {
                params.add(Integer.valueOf(0));
            }
            retVal = (Vector) xmlrpc.execute( methodName, params );
            TreeGif newGif = new TreeGif();
            newGif.setName((String) retVal.get(0));
            newGif.setWidth((Integer) retVal.get(1));
            newGif.setHeight((Integer) retVal.get(2));
            Object otherObj = retVal.get(3);
            if (String.class.isInstance(otherObj)) {
                newGif.setMapString((String) retVal.get(3));    
            } else if (otherObj instanceof byte[]) {
                newGif.setMapString(new String((byte[]) otherObj));
            } else {
                newGif.setMapString("");
            }
            return newGif;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public PageDAO getPublicPageDAO() {
        return publicPageDao;
    }
    public void setPublicPageDAO(PageDAO pageDao) {
        this.publicPageDao = pageDao;
    }
    public PageDAO getWorkingPageDAO() {
        return workingPageDao;
    }
    public void setWorkingPageDAO(PageDAO pageDao) {
        this.workingPageDao = pageDao;
    }    
    public String getURL() {
        return URL;
    }
    public void setURL(String baseURL) {
        this.URL = baseURL;
    }
    public String getWorkingURL() {
        return workingURL;
    }
    public void setWorkingURL(String workingBaseURL) {
        this.workingURL = workingBaseURL;
    }
    public void clearCacheForTreeGif(TreeGif treeGif) {
	    try {
	        SessionFactory factory = getSessionFactory();
	        Long id = treeGif.getId();
	        factory.evict(TreeGif.class, id);
	        getHibernateTemplate().delete(treeGif);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	                
    }
}
