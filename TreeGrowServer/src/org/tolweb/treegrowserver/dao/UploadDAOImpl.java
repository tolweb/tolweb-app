/*
 * Created on Nov 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.tolweb.treegrowserver.Upload;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadDAOImpl extends HibernateDaoSupport implements UploadDAO {
    public Upload getUploadWithId(Long id) {
        Upload upload = (Upload) getHibernateTemplate().load(Upload.class, id); 
        return upload;
    }
    
    public void saveUpload(Upload upload) {
        getHibernateTemplate().saveOrUpdate(upload);
    }

	public List<Object[]> getContributorsDatesAndNodesRecentlyChanged(Long rootNodeId, Date lastChangedDate) {
		String queryString = "select contr.firstName, contr.lastName, u.uploadDate, d.rootNode.name, d.rootNode.nodeId from org.tolweb.treegrowserver.Upload u join " + 
			" u.download as d join d.contributor as contr join d.rootNode as n join n.ancestors as a where a.nodeId=" + rootNodeId + 
			" and u.uploadDate >= ? order by u.uploadDate desc";
		return getHibernateTemplate().find(queryString, lastChangedDate);
	}
    /**
     * Returns a list of 5-element object arrays w/ the
     * first element being the root node name, the 2nd 
     * being the contributor first name, the 3rd being the 
     * contributor last name, the 4th being the upload date 
     * and 5th being the upload contents
     * @return
     */	
	public List<Object[]> getNonXmlRootNameContributorNameUploadDateAndUploadContents() {
		String queryString = "select d.rootNode.name, contr.firstName, contr.lastName, u.uploadDate, u.xmlDoc from org.tolweb.treegrowserver.Upload u join " + 
		" u.download as d join d.contributor as contr join d.rootNode as n where u.xmlDoc not like '<%'order by u.uploadDate desc";		
		return getHibernateTemplate().find(queryString);
	}
}
