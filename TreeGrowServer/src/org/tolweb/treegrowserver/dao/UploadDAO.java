/*
 * Created on Nov 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.treegrowserver.Upload;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface UploadDAO {
    public Upload getUploadWithId(Long id);
    public void saveUpload(Upload upload);
    /**
     * Returns a list of 5-element object array with the 
     * the 1st and 2nd elements being the contributor's first and last name,
     * the 3rd element being the date of the upload, 
     * and the 4th being the root node name of the upload, and the 5th
     * being the root node id of the upload
     * @param rootNodeId
     * @param lastChangedDate
     * @return
     */
    public List<Object[]> getContributorsDatesAndNodesRecentlyChanged(Long rootNodeId, Date lastChangedDate);
    /**
     * Returns a list of 5-element object arrays w/ the
     * first element being the root node name, the 2nd 
     * being the contributor first name, the 3rd being the 
     * contributor last name, the 4th being the upload date 
     * and 5th being the upload contents
     * @return
     */
    public List<Object[]> getNonXmlRootNameContributorNameUploadDateAndUploadContents();
}
