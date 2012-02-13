/*
 * ImageDAO.java
 *
 * Created on May 3, 2004, 10:42 AM
 */

package org.tolweb.dao;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 *
 * @author  dmandel
 */
public interface ImageDAO {
	public static final int IMAGE_GALLERY_SIZE = 40;
	public static final Byte FILENAME = Byte.valueOf((byte) 0);
	public static final Byte IMAGE_ID = Byte.valueOf((byte) 1); 
	public static final Byte SCIENTIFIC_NAME = Byte.valueOf((byte) 2);
	public static final Byte GROUP = Byte.valueOf((byte) 3);
	public static final Byte CONTRIBUTOR = Byte.valueOf((byte) 4);
	public static final Byte COPYOWNER_CONTRIBUTOR = Byte.valueOf((byte) 5);
	public static final Byte COPYOWNER = Byte.valueOf((byte) 6);
	public static final Byte ANY_DATA = Byte.valueOf((byte) 7);
	public static final Byte IMAGE_USE = Byte.valueOf((byte) 8);
	public static final Byte SEARCH_ANCESTORS = Byte.valueOf((byte) 9);
	public static final Byte CREDITS = Byte.valueOf((byte) 10);
	public static final Byte LOCATION = Byte.valueOf((byte) 11);
	public static final Byte IMAGE_TYPE = Byte.valueOf((byte) 12);
	public static final Byte IMAGE_CONTENT = Byte.valueOf((byte) 13);
	public static final Byte CONDITION = Byte.valueOf((byte) 14);
	public static final Byte BEHAVIOR = Byte.valueOf((byte) 15);
    public static final Byte SEX = Byte.valueOf((byte) 16);
    public static final Byte STAGE = Byte.valueOf((byte) 17);
    public static final Byte BODY_PART = Byte.valueOf((byte) 18);
    public static final Byte VIEW = Byte.valueOf((byte) 19);
    public static final Byte COLLECTION = Byte.valueOf((byte) 20);
    public static final Byte TYPE = Byte.valueOf((byte) 21);
    public static final Byte VOUCHER_NUMBER = Byte.valueOf((byte) 22);
    public static final Byte VOUCHER_COLLECTION = Byte.valueOf((byte) 23);
    public static final Byte COLLECTOR = Byte.valueOf((byte) 24);
    public static final Byte IDENTIFIER = Byte.valueOf((byte) 25);
    public static final Byte REFERENCE = Byte.valueOf((byte) 26);
    public static final Byte CREATOR = Byte.valueOf((byte) 27);
    public static final Byte ACKS = Byte.valueOf((byte) 28);
    public static final Byte IS_BODYPARTS = Byte.valueOf((byte) 29);
    public static final Byte IS_SPECIMENS = Byte.valueOf((byte) 30);
    public static final Byte IS_ULTRASTRUCTURE = Byte.valueOf((byte) 31); 
    public static final Byte IS_HABITAT = Byte.valueOf((byte) 32);
    public static final Byte IS_EQUIPMENT = Byte.valueOf((byte) 33);
    public static final Byte IS_PEOPLE = Byte.valueOf((byte) 34);
    public static final Byte NODE_ID = Byte.valueOf((byte) 35);
    public static final Byte ONLY_PODCASTS = Byte.valueOf((byte) 36);
    public static final String ANY_TYPE = "anytype"; 
    public void addImage(NodeImage img, Contributor contributor, boolean assignCopyright);
    public void saveImage(NodeImage img);
    public void deleteImage(NodeImage img);
    public NodeImage getImageWithId(int id);
    public String getAltTextForImageWithId(int id);
    public String getImageLocationWithId(int id);
    public String getImageVersionLocationForSizedImageWithId(int id, int size);
    public String getThumbnailUrlForImageWithId(int id);
    public String generateAndSaveVersion(ImageVersion version);
    public List getImagesMatchingCriteria(Map args);
    public List getImagesMatchingCriteria(Map args, int maxResults);
    public List getSoundsMatchingCriteria(Map args);
    public List getSoundsMatchingCriteria(Map args, int maxResults);    
    public List getDocumentsMatchingCriteria(Map args);
    public List getDocumentsMatchingCriteria(Map args, int maxResults);
    public List getMoviesMatchingCriteria(Map args);
    public List getMoviesMatchingCriteria(Map args, int maxResults);
    public int getNumImagesMatchingCriteria(Map args);
    public Set getDistinctImageContributorsIds();
    public void setContributorDAO(ContributorDAO contrDao);
    public ContributorDAO getContributorDAO();
    public Integer getNumImagesForContributor(Contributor contr);
    public List getImagesForContributor(Contributor contr);
    public List getImageLocationsAndIdsForImagesWithIds(List ids);
    public List getVersionLocationsAndIdsForVersionsWithIds(Collection ids);
    public List getImagesWithIds(Collection ids);
    public List getVersionsForImage(NodeImage image);
    public List getVersionIdsForImage(NodeImage image);
    public List getUsableVersionsForImage(NodeImage image);
    public void saveImageVersion(ImageVersion version);
    public void deleteImageVersion(ImageVersion version);
    public ImageVersion getImageVersionWithId(Long id);
    public List getImagesAttachedToNode(Long nodeId);
    public Set getUnattachedMedia();
    
    public Dimension getMaxAllowedVersionDimensions(int imageId);    
    public int getMaxAllowedVersionHeight(int imageId);
    public int getMasterVersionHeight(int imageId);
    public ImageVersion getMasterVersion(int imageId);
    public ImageVersion getMaxAllowedVersion(NodeImage image);
    public ImageVersion getImageVersionWithHeight(int imgId, int height);
    /**
     * Returns a list of images attached to the nodes that appear in
     * the comma-separated string of node ids
     * @param nodeIds
     * @return
     */
    public List getImagesAttachedToNodes(String nodeIds);
    public void clearCacheForImage(NodeImage img);
    public List getImagesContributorIsEditor(Contributor contr);
    public int getMediaType(int mediaId);
    public String getTitleForMediaWithId(int mediaId);
    public List getPrimaryImagesForNode(MappedNode node);
    public List getContributorsWithImagesAttachedToNodeIds(Collection nodeIds);
    public List getMostRecentlyEditedImages(int numImages);
    
    public List getRandomGalleryImagesForNode(MappedNode node);
	public List getRandomGalleryImagesForNode(MappedNode node, int mediaClass);
    public List getGalleryImagesForNode(MappedNode node, int startIndex);
	public List getGalleryImagesForNode(MappedNode node, int startIndex, int mediaClass);
    public int getNumGalleryImagesForNode(MappedNode node);
	public int getNumGalleryImagesForNode(MappedNode node, int mediaClass);    
    public List<Object[]> getGalleryTitleIllustrationsForNode(MappedNode node, List<Long> versionIds, int startIndex);    
    public int getNumGalleryTitleIllustrationsForNode(MappedNode node, List<Long> versionIds);
    
    public Hashtable<Number, NodeImage> getImagesFromVersionIds(Collection versionIds);
    public Boolean getShowImageInfoLinkForImageId(int imgId);
    public Boolean getShowInlineImageInfoLinkForImageId(int imgId);
    
    public List getLatestGalleryImagesForNode(MappedNode node, int fetchSize, int mediaClass);
    
    public ForeignDatabase getForeignDatabaseWithId(Long id);
    public ForeignDatabase getForeignDatabaseWithName(String name);
    
    /**
     * Updates the cross-reference table responsible for managing the attachment of images to nodes 
     * so that the orphaned image will be reattached to the new node-id
     * 
     * Used by the custom taxa import functionality developed in February 2008
     * @author lenards
     * 
     * @param imageId the image that is being reattached
     * @param oldNodeId the node-id the image was attached to
     * @param newNodeId the new node-id to attach the image to 
     */
    public void reattachImage(Long imageId, Long oldNodeId, Long newNodeId);
    
    /**
     * Returns attached images that are native to the ToL database. 
     * 
     * We do not want to share images that were imported from other collections.  The 
     * primary consumer for this method will be the content web services. We only  
     * want to share images that are native to the ToL database. 
     * 
     * @param nodeId identity value for the node we are querying about 
     * @return a list of images attached to the node associated with the argument 
     * that are native to the ToL database
     */
	public List getNativeAttachedImagesForNode(Long nodeId);
	
	/**
     * Returns attached images that are native to the ToL database. 
     * 
     * We do not want to share images that were imported from other collections.  The 
     * primary consumer for this method will be the content web services. We only  
     * want to share images that are native to the ToL database. 
     * 
     * @param nodeId identity value for the node we are querying about 
	 * @param excludeIds identities for media to not include in the results
     * @return a list of images attached to the node associated with the argument 
     * that are native to the ToL database
	 */
	public List getNativeAttachedImagesForNode(Long nodeId, Collection excludeIds);
	
	public List getNativeAttachedImagesForNode(Long nodeId, Collection excludeIds, boolean limitImages);
}
