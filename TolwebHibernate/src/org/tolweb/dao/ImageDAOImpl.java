/*
 * ImageDAOImpl.java
 *
 * Created on May 3, 2004, 10:43 AM
 */

package org.tolweb.dao;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.misc.ImageUtils;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.Keywords;
import org.tolweb.treegrow.main.Languages;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;


/**
 *
 * @author  dmandel
 */
public class ImageDAOImpl extends AbstractNodeAttachedDAO implements ImageDAO {
	private ContributorDAO contributorDao;
	private ImageUtils imageUtils;
    private PageDAO workingPageDAO;
    private PageDAO publicPageDAO;
	
    public void addImage(NodeImage img, Contributor contributor, boolean assignCopyright) {
        try {
            Date now = new Date();
            img.setContributor(contributor);
            img.setLastEditedContributor(contributor);
            if (assignCopyright) {
                img.setCopyrightOwnerContributor(contributor);
            }
            img.setCreationDate(now);
            img.setLastEditedDate(now);
            if (img.getKeywords() != null) {
                img.setKeywords(new Keywords());
            }
            if (img.getLanguages() != null) {
                img.setLanguages(new Languages());
            }
            getHibernateTemplate().save(img);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    public void saveImage(NodeImage img) {
    	// ensure that if there is a copyright URL provided, it has the proper URL form. 
    	ensureUrlsValid(img);
        getHibernateTemplate().saveOrUpdate(img);        
    }

    /**
     * Verifies that the copyright URL has the valid prefix so that it is properly formed. 
     * 
     * Users often supply the URL without the protocol prefix, so the values stored in 
     * the database are malformed, valid URLs.  This ensures that the anchor data is in 
     * a valid form 
     * 
     * @param img media file to verify copyright URL for 
     */
	private void ensureUrlsValid(NodeImage img) {
		if (img != null) {
    		if (StringUtils.notEmpty(img.getCopyrightUrl()) && !hasHttpPrefix(img.getCopyrightUrl())) {
    			img.setCopyrightUrl(HTTP_PREFIX + img.getCopyrightUrl());
    		}
    		if (StringUtils.notEmpty(img.getSourceCollectionUrl()) && !hasHttpPrefix(img.getSourceCollectionUrl())) {
    			img.setSourceCollectionUrl(HTTP_PREFIX + img.getSourceCollectionUrl());
    		}
    	}
	}
	
    public void deleteImage(NodeImage img) {
        getHibernateTemplate().delete(img);
    }
    
    public NodeImage getImageWithId(int id) {
    	try {
			return (NodeImage) getHibernateTemplate().load(NodeImage.class, Integer.valueOf(id));
    	} catch(Exception e) {
    	    e.printStackTrace();
    		return null;
    	}
    }
    
    public String getAltTextForImageWithId(int id) {
        return returnSingleStringResult("select i.altText from org.tolweb.treegrow.main.NodeImage i where i.id=" + id);
    }
    
    public String getImageLocationWithId(int id) {
        return returnSingleStringResult("select i.location from org.tolweb.treegrow.main.NodeImage i where i.id=" + id);
    }
    
    private String returnSingleStringResult(String hql) {
        List returnList = getHibernateTemplate().find(hql);
        if (returnList.size() == 1) {
            return (String) returnList.get(0);
        } else {
            return null;
        }        
    }
    
    public String generateAndSaveVersion(ImageVersion version) {
        if (version != null) {
	        String url = getImageUtils().getVersionUrl(version);
	        saveImageVersion(version);
	        return url;
        } else {
            return "";
        }
    }
    
    public ImageVersion getMasterVersion(int imageId) {
        return ((ImageVersion) getFirstObjectFromQuery("select v from org.tolweb.treegrow.main.ImageVersion v where v.image.id=" + imageId + " and v.isMaster=1"));
    }
    
    public int getMasterVersionHeight(int imageId) {
        try {
            return ((Number) getFirstObjectFromQuery("select v.height from org.tolweb.treegrow.main.ImageVersion v where v.image.id=" + imageId + " and v.isMaster=1")).intValue();
        } catch (RuntimeException e) {
            return 100;
        }
    }
    
    public int getMaxAllowedVersionHeight(int imageId) {
        List returnList = doMaxHeightQuery("select v.height", imageId);
        if (returnList.size() > 0) {
            return ((Number) returnList.get(0)).intValue();
        } else {
            logger.info("Image without a maxheight set is: " + imageId);
            return 100;
        }
    }
    
    public Dimension getMaxAllowedVersionDimensions(int imageId) {
    	List returnList = null;
    	try {
    		returnList = doMaxHeightQuery("select v.width, v.height", imageId);
    	} catch (Exception e) {
    		returnList = new ArrayList();
    	}
    	if (returnList.size() > 0) {
    		Object[] dims = (Object[]) returnList.get(0);
    		int width = ((Number) dims[0]).intValue();
    		int height = ((Number) dims[1]).intValue();
    		return new Dimension(width, height);
    	} else {
    		return new Dimension(100, 100);
    	}
    }
    
    public ImageVersion getMaxAllowedVersion(NodeImage image) {
        List returnList = doMaxHeightQuery("select v", image.getId());
        if (returnList.size() > 0) {
            return  (ImageVersion) returnList.get(0);
        } else {
            logger.info("Image without a maxheight set is: " + image.getId());
            return null;
        }        
    }
    
    private List doMaxHeightQuery(String selectString, int imageId) {
        List returnList = getHibernateTemplate().find(selectString + " from org.tolweb.treegrow.main.ImageVersion v where v.image.id=" + imageId + " and v.isMaxSize=1");
        return returnList;
    }
    
    /**
     * Returns the url for the image version with a given height.  If the
     * height is invalid or doesn't exist, then try to pick appropriate
     * version
     */
    public String getImageVersionLocationForSizedImageWithId(int id, int height) {
        ImageVersion version = null;
        int maxHeight = getMaxAllowedVersionHeight(id); 
        // if it's a bogus value for height, choose the max allowable
        if (height <= 0 || height > maxHeight) {
            height = maxHeight;
        }
        version = getImageVersionWithHeight(id, height);
        if (version == null) {
            height = getImageUtils().getClosestAutogeneratedVersionHeight(height);
            version = getImageVersionWithHeight(id, height);
        }
        if (version != null && StringUtils.notEmpty(version.getFileName())) {
            return getImageUtils().getVersionUrl(version);
        } else {
            // Needs to be generated
            return generateAndSaveVersion(version);
        }
    }
    
    public String getThumbnailUrlForImageWithId(int id) {
        return getImageVersionLocationForSizedImageWithId(id, imageUtils.getDefaultThumbnailHeight());
    }
    
    /**
     * Returns the image version with the given height.  If more than one exists, pick
     * the non-autogenerated version
     * @param imageId
     * @param height
     * @return
     */
    public ImageVersion getImageVersionWithHeight(int imageId, int height) {
        String queryString = "from org.tolweb.treegrow.main.ImageVersion v where v.image.id=" + imageId + " and v.height=" + height;
        List returnList = getHibernateTemplate().find(queryString);
        ImageVersion version = null;
        if (returnList.size() > 1) {
            // Grab the first
            ImageVersion first = (ImageVersion) returnList.get(0);
            if (first.getIsGenerated()) {
                version = (ImageVersion) returnList.get(1);
            } else {
                version = first;
            }
        } else {
            if (returnList.size() == 1) {
                version = (ImageVersion) returnList.get(0);
            }
        }
        return version;
    }
    
    public Set getDistinctImageContributorsIds() {
    	String copyContributorQuery = "select distinct i.copyrightOwnerContributor.id from org.tolweb.treegrow.main.NodeImage i where i.copyrightOwnerContributor.id != null";
    	String contributorQuery = "select distinct i.contributor.id from org.tolweb.treegrow.main.NodeImage i where i.contributor.id != null";
    	List copyContributors = getHibernateTemplate().find(copyContributorQuery);
    	List contributors = getHibernateTemplate().find(contributorQuery);
    	Set contributorsSet = new HashSet();
    	contributorsSet.addAll(copyContributors);
    	contributorsSet.addAll(contributors);
    	return contributorsSet;
    }
    
    public int getNumImagesMatchingCriteria(Map args) {
        Integer numImages = (Integer) findImagesMatchingCriteria(args, false, -1);
        return numImages.intValue();

    }
    
    public List getImagesMatchingCriteria(Map args, int maxResults) {
        return (List) findImagesMatchingCriteria(args, true, maxResults);
    }
    
	public List getImagesMatchingCriteria(Map args) {
        return getImagesMatchingCriteria(args, -1);			 
	}
	
	public List getImagesAttachedToNode(Long nodeId) {
	    Hashtable args = new Hashtable();
	    args.put(ImageDAO.NODE_ID, nodeId);
	    return (List) findImagesMatchingCriteria(args, true, -1);
	}
	
	public List getImagesAttachedToNodes(String nodeIds) {
	    List returnImages = getHibernateTemplate().find("select i from org.tolweb.treegrow.main.NodeImage i join i.nodesSet as nodes where nodes.nodeId in (" + nodeIds + ")"); 
	    return returnImages;
	}
	
	private Object findImagesMatchingCriteria(Map args, final boolean returnImages, final int maxResults) {
	    return findImagesMatchingCriteria(args, returnImages, maxResults, NodeImage.class);
	}
	
	/**
	 * Method that conditionally returns images based on the search or returns the number
	 * of images that would be returned by the search if it were run
	 * @param args The criteria to search on
	 * @param returnImages Whether to actually return the images or to return a count
	 * @param maxResults If greater than 0, the max number of images to return
	 * @return
	 */
	private Object findImagesMatchingCriteria(Map args, final boolean returnImages, final int maxResults, final Class mediaClass) {
		final String filename = (String) args.get(ImageDAO.FILENAME);
		final Integer imgId = (Integer) args.get(ImageDAO.IMAGE_ID);
		final String sciName = (String) args.get(ImageDAO.SCIENTIFIC_NAME);
		final String group = (String) args.get(ImageDAO.GROUP);
		final Long nodeId = (Long) args.get(ImageDAO.NODE_ID);
		final Contributor contr  = (Contributor) args.get(ImageDAO.CONTRIBUTOR);
		final Contributor copyOwnerContr = (Contributor) args.get(ImageDAO.COPYOWNER_CONTRIBUTOR);
		final String copyOwner = (String) args.get(ImageDAO.COPYOWNER);
		final String anyText = (String) args.get(ImageDAO.ANY_DATA);
		final Byte usePermission = (Byte) args.get(ImageDAO.IMAGE_USE);
		final Boolean searchAncestors = (Boolean) args.get(ImageDAO.SEARCH_ANCESTORS);
		final String refs = (String) args.get(ImageDAO.REFERENCE);
		final String creator = (String) args.get(ImageDAO.CREATOR);
		final String acks = (String) args.get(ImageDAO.ACKS);
		final String identifier = (String) args.get(ImageDAO.IDENTIFIER);
		final String collector = (String) args.get(ImageDAO.COLLECTOR);
		final String location = (String) args.get(ImageDAO.LOCATION);
		final String credits = (String) args.get(ImageDAO.CREDITS);
		final String imageType = (String) args.get(ImageDAO.IMAGE_TYPE);
		final String behavior = (String) args.get(ImageDAO.BEHAVIOR);
		final String voucherNumber = (String) args.get(ImageDAO.VOUCHER_NUMBER);
		final String voucherCollection = (String) args.get(ImageDAO.VOUCHER_COLLECTION);
		final String stage = (String) args.get(ImageDAO.STAGE);
		final String bodyPart = (String) args.get(ImageDAO.BODY_PART);
		final String view = (String) args.get(ImageDAO.VIEW);
		final String alive = (String) args.get(ImageDAO.CONDITION);
		final String collection = (String) args.get(ImageDAO.COLLECTION);
		final String sex = (String) args.get(ImageDAO.SEX);
		final String type = (String) args.get(ImageDAO.TYPE);
		final Boolean isBodyparts = (Boolean) args.get(ImageDAO.IS_BODYPARTS);
		final Boolean isSpecimens = (Boolean) args.get(ImageDAO.IS_SPECIMENS);
		final Boolean isUltrastructure = (Boolean) args.get(ImageDAO.IS_ULTRASTRUCTURE);
		final Boolean isHabitat = (Boolean) args.get(ImageDAO.IS_HABITAT);
		final Boolean isEquipment = (Boolean) args.get(ImageDAO.IS_EQUIPMENT);
		final Boolean isPeople = (Boolean) args.get(ImageDAO.IS_PEOPLE);
		final Boolean onlyPodcasts = (Boolean) args.get(ImageDAO.ONLY_PODCASTS);
		
		HibernateCallback callback = new HibernateCallback() {
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria imgsCriteria = session.createCriteria(mediaClass);
				imgsCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				boolean anyNodes = true;
				if (group != null || nodeId != null) {
				    if (searchAncestors == null || !searchAncestors.booleanValue()) {
						anyNodes = addCriteriaForNodesSet(imgsCriteria, group, nodeId);
				    } else {
				        anyNodes = addCriteriaForNodesAncestorsSet(imgsCriteria, group);
				    }
				}
				// No groups with that name so there couldn't possibly be any images attached to
				// those nodes
				if (!anyNodes) {
				    if (returnImages) {
				        return new ArrayList();
				    } else {
				        return Integer.valueOf(0);
				    }
				}
				if (StringUtils.notEmpty(credits)) {
				    addCreditsCriteria(credits, imgsCriteria);
				}
				if (StringUtils.notEmpty(sex)) {
				    addSexCriteria(sex, imgsCriteria);
				}
				if (StringUtils.notEmpty(type)) {
				    addTypeCriteria(type, imgsCriteria);
				}
				addBooleanMatchIfNotNull(isBodyparts, "isBodyParts", imgsCriteria);
				addBooleanMatchIfNotNull(isEquipment, "isEquipment", imgsCriteria);
				addBooleanMatchIfNotNull(isHabitat, "isHabitat", imgsCriteria);
				addBooleanMatchIfNotNull(isPeople, "isPeopleWorking", imgsCriteria);
				addBooleanMatchIfNotNull(isSpecimens, "isSpecimen", imgsCriteria);
				addBooleanMatchIfNotNull(isUltrastructure, "isUltrastructure", imgsCriteria);
				addBooleanMatchIfNotNull(onlyPodcasts, "considerForPodcast", imgsCriteria);
				addStringMatchIfNotNull(filename, "location", imgsCriteria);
				addStringMatchIfNotNull(refs, "references", imgsCriteria);
				addStringMatchIfNotNull(creator, "creator", imgsCriteria);
				addStringMatchIfNotNull(acks, "acknowledgements", imgsCriteria);
				addStringMatchIfNotNull(identifier, "identifier", imgsCriteria);
				addStringMatchIfNotNull(collector, "collector", imgsCriteria);
				addStringMatchIfNotNull(location, "geoLocation", imgsCriteria);
				addStringMatchIfNotNull(sciName, "scientificName", imgsCriteria);
				addStringMatchIfNotNull(imageType, "imageType", imgsCriteria);
				addStringMatchIfNotNull(behavior, "behavior", imgsCriteria);
				addStringMatchIfNotNull(voucherNumber, "voucherNumber", imgsCriteria);
				addStringMatchIfNotNull(voucherCollection, "voucherNumberCollection", imgsCriteria);
				addStringMatchIfNotNull(stage, "stage", imgsCriteria);
				addStringMatchIfNotNull(bodyPart, "bodyPart", imgsCriteria);
				addStringMatchIfNotNull(view, "view", imgsCriteria);
				addStringMatchIfNotNull(alive, "alive", imgsCriteria);
				addStringMatchIfNotNull(collection, "collection", imgsCriteria);
				if (imgId != null) {
					imgsCriteria.add(Expression.eq("id", imgId));            
				}
				
				if (contr != null || copyOwnerContr != null) {			
					boolean all = contr != null && copyOwnerContr != null;
					if (all) {
					    Disjunction copyOwnerExpression = Expression.disjunction();
						copyOwnerExpression.add(Expression.eq("contributor.id", Integer.valueOf(contr.getId())));
						copyOwnerExpression.add(Expression.eq("copyrightOwnerContributor.id", Integer.valueOf(copyOwnerContr.getId())));
						imgsCriteria.add(copyOwnerExpression);
					} else {
						if (contr != null) {
							imgsCriteria.add(Expression.eq("contributor.id", Integer.valueOf(contr.getId())));
						} else {
							imgsCriteria.add(Expression.eq("copyrightOwnerContributor.id", Integer.valueOf(copyOwnerContr.getId())));
						}
					}
				}
				if (copyOwner != null) {
					imgsCriteria.add(getCopyrightOwnerCriterion(copyOwner));
				}
				if (usePermission != null) {
				    imgsCriteria.add(Expression.gt("usePermission", usePermission));
				}
				if (anyText != null) {
					addAnyTextCriteria(anyText, imgsCriteria);	
				}
				imgsCriteria.add(Expression.eq("isUnapproved", Boolean.valueOf(false)));
				if (returnImages) {
				    if (maxResults > 0) {
				        imgsCriteria.setMaxResults(maxResults);
				    }
					List returnList;
					try {
						returnList = imgsCriteria.list();
					} catch (Exception e) {
						e.printStackTrace();
						returnList = new ArrayList();
					}
					return returnList;
				} else {
				    imgsCriteria.setProjection(Projections.rowCount());
				    try {
				        Integer numImgs = (Integer) imgsCriteria.uniqueResult(); 
				        return numImgs;
				    } catch (Exception e) {
				        e.printStackTrace();
				        return Integer.valueOf(0);
				    }
				}
			}
		 };
		 return getHibernateTemplate().execute(callback);	    
	}
	

	
	private void addCreditsCriteria(String credits, Criteria imgsCriteria) {
	    imgsCriteria.add(
	            Expression.disjunction()
	            .add(getCopyrightOwnerCriterion(credits))
	            .add(Expression.like("reference", credits, MatchMode.ANYWHERE))
	            .add(Expression.like("creator", credits, MatchMode.ANYWHERE))
	            .add(Expression.like("acknowledgements", credits, MatchMode.ANYWHERE))
	            .add(Expression.like("identifier", credits, MatchMode.ANYWHERE))
	            .add(Expression.like("collector", credits, MatchMode.ANYWHERE))
	            .add(Expression.like("geoLocation", credits, MatchMode.ANYWHERE)));	    
	}
	
	private void addSexCriteria(String sex, Criteria imgsCriteria) {
	    if(sex.equals(NodeImage.OTHER)) {
	        imgsCriteria.add(Expression.not(Expression.like("sex", NodeImage.ALIVE, MatchMode.EXACT)));
	        imgsCriteria.add(Expression.not(Expression.like("sex", NodeImage.DEAD, MatchMode.EXACT)));
	        imgsCriteria.add(Expression.isNotNull("sex"));
	        imgsCriteria.add(Expression.not(Expression.like("sex", "", MatchMode.EXACT)));
	    } else {
	        imgsCriteria.add(Expression.like("sex", sex, MatchMode.ANYWHERE));
	    }
	}
	
	private void addTypeCriteria(String type, Criteria imgsCriteria) {
	    if (type.equals(ImageDAO.ANY_TYPE)) {
	        Disjunction disjunction = Expression.disjunction();
	        Iterator it = NodeImage.TYPES_LIST.iterator();
	        while (it.hasNext()) {
	            String nextType = (String) it.next();
	            disjunction.add(Expression.like("type", nextType, MatchMode.EXACT));
	        }
	        imgsCriteria.add(disjunction);
	    } else {
	        imgsCriteria.add(Expression.like("type", type, MatchMode.ANYWHERE));
	    }
	}

	
	private Criterion getCopyrightOwnerCriterion(String copyOwner) {
		Criterion copyOwnerExpr = Expression.like("copyrightOwner", copyOwner, MatchMode.ANYWHERE);
		Hashtable args = new Hashtable();
		args.put(ContributorDAO.NAME, copyOwner);
		List contributors = contributorDao.findContributors(args);
		Iterator it = contributors.iterator();
		ArrayList ids = new ArrayList();
		if (contributors.size() > 0) {
			while (it.hasNext()) {
				Contributor contr = (Contributor) it.next();
				ids.add(Integer.valueOf(contr.getId())); 
			}
			Criterion contribCopyOwnerExpr = Expression.in("copyrightOwnerContributor.id", ids);
			return Expression.or(copyOwnerExpr, contribCopyOwnerExpr);		
		} else {
			return copyOwnerExpr;
		}
	}
	
	private void addAnyTextCriteria(String anyText, Criteria imgsCriteria) {
		imgsCriteria.add(
			Expression.disjunction()
				.add(Expression.like("comments", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("scientificName", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("altText", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("reference", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("creator", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("identifier", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("acknowledgements", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("geoLocation", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("sex", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("stage", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("bodyPart", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("size", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("view", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("period", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("collection", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("type", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("collector", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("scientificName", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("additionalDateTimeInfo", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("season", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("alive", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("imageType", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("voucherNumber", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("behavior", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("notes", anyText, MatchMode.ANYWHERE))
				.add(Expression.like("location", anyText, MatchMode.ANYWHERE))
				.add(getCopyrightOwnerCriterion(anyText)));
	}
	
	public Integer getNumImagesForContributor(Contributor value) {
		Integer id = Integer.valueOf(value.getId());
		List result = getHibernateTemplate().find
			("select count(*) from org.tolweb.treegrow.main.NodeImage i where i.copyrightOwnerContributor.id=? or i.contributor.id=?", 
				new Object[] {id, id});
		return (Integer) result.get(0);
	}
	
	public List getImagesForContributor(Contributor value) {
		Hashtable args = new Hashtable();
		args.put(ImageDAO.CONTRIBUTOR, value);
		args.put(ImageDAO.COPYOWNER_CONTRIBUTOR, value);
		return getImagesMatchingCriteria(args);
	}
	
    /**
     * Returns the filenames for the imgs with the passed-in ids
     */
	public List getImageLocationsAndIdsForImagesWithIds(List ids) {
	    String queryString = "select i.location, i.id from org.tolweb.treegrow.main.NodeImage i where i.id in (" + 
	    	StringUtils.returnCommaJoinedString(ids) + ")"; 
        return getHibernateTemplate().find(queryString);
    }
	
	public List getVersionLocationsAndIdsForVersionsWithIds(Collection ids) {
	    String queryString = "select v.fileName, v.versionId from org.tolweb.treegrow.main.ImageVersion v where v.versionId in (" + 
    		StringUtils.returnCommaJoinedString(ids) + ")"; 
	    return getHibernateTemplate().find(queryString);	    
	}
	
	public List getImagesWithIds(Collection ids) {
		if (ids != null && !ids.isEmpty()) {
			String queryString = "from org.tolweb.treegrow.main.NodeImage i where i.id in (" + 
					StringUtils.returnCommaJoinedString(ids) + ")";
			return getHibernateTemplate().find(queryString);
		} else {
			return new ArrayList();
		}
	}
	
    public List getVersionsForImage(NodeImage image) {
        return getVersionsOrIdsForImage(image, "");
    }
    
    public List getVersionIdsForImage(NodeImage image) {
        return getVersionsOrIdsForImage(image, "select v.versionId");
    }
    
    private List getVersionsOrIdsForImage(NodeImage image, String selectPrefix) {
        return getHibernateTemplate().find(selectPrefix + "from org.tolweb.treegrow.main.ImageVersion v where v.image.id=" + image.getId() + " order by v.height desc");        
    }
    
    public List getUsableVersionsForImage(NodeImage image) {
        List versions = getVersionsForImage(image);
        Collections.sort(versions);
        int maxSizeIndex = 0;
        int i = 0;
        for (Iterator iter = versions.iterator(); iter.hasNext();) {
            ImageVersion version = (ImageVersion) iter.next();
            if (version.getIsMaxSize()) {
                maxSizeIndex = i; 
            }
            i++;
        }
        return versions.subList(maxSizeIndex, versions.size());
    }
    
    public void saveImageVersion(ImageVersion version) {
        getHibernateTemplate().saveOrUpdate(version);
    }
    
    public void deleteImageVersion(ImageVersion version) {
        getHibernateTemplate().delete(version);
        getImageUtils().deleteVersionFromFilesystem(version);
    }
    
    public ImageVersion getImageVersionWithId(Long id) {
        try {
            ImageVersion version = (ImageVersion) getHibernateTemplate().load(ImageVersion.class, id);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void clearCacheForImage(NodeImage img) {
	    try {
	        SessionFactory factory = getSessionFactory();
	        Integer id = Integer.valueOf(img.getId());
	        factory.evict(NodeImage.class, id);
	        factory.evictCollection("org.tolweb.treegrow.main.NodeImage.nodesSet", id);
	    } catch (Exception e) {
	        logger.info("Problem uncaching image ", e);
	    }
    }
	
	public void setContributorDAO(ContributorDAO value) {
		contributorDao = value;
	}
	
	public ContributorDAO getContributorDAO() {
		return contributorDao;
	}
    /**
     * @return Returns the imageUtils.
     */
    public ImageUtils getImageUtils() {
        return imageUtils;
    }
    /**
     * @param imageUtils The imageUtils to set.
     */
    public void setImageUtils(ImageUtils imageUtils) {
        this.imageUtils = imageUtils;
    }
    
    public List getImagesContributorIsEditor(Contributor contr) {
        return getHibernateTemplate().find("from org.tolweb.treegrow.main.NodeImage img left join img.permissionsSet as permissions where permissions.contributor.id=" + contr.getId());
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.ImageDAO#getSoundsMatchingCriteria(java.util.Map)
     */
    public List getSoundsMatchingCriteria(Map args) {
        return getSoundsMatchingCriteria(args, -1);
    }
    public List getSoundsMatchingCriteria(Map args, int maxResults) {
    	return (List) findImagesMatchingCriteria(args, true, maxResults, Sound.class);
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.ImageDAO#getDocumentsMatchingCriteria(java.util.Map)
     */
    public List getDocumentsMatchingCriteria(Map args) {
    	return getDocumentsMatchingCriteria(args, -1);
    }
    
    public List getDocumentsMatchingCriteria(Map args, int maxResults) {
        return (List) findImagesMatchingCriteria(args, true, maxResults, Document.class);    	
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.ImageDAO#getMoviesMatchingCriteria(java.util.Map)
     */
    public List getMoviesMatchingCriteria(Map args) {
    	return getMoviesMatchingCriteria(args, -1);
    }
    
    public List getMoviesMatchingCriteria(Map args, int maxResults) {
        return (List) findImagesMatchingCriteria(args, true, maxResults, Movie.class);    	
    }
    
    public int getMediaType(int mediaId) {
        try {
            List results = getHibernateTemplate().find("select i.class from org.tolweb.treegrow.main.NodeImage i where i.id=" + mediaId);
            int mediaType = ((Integer) results.get(0)).intValue(); 
            return mediaType;
        } catch (Exception e) {
            return NodeImage.NON_EXISTENT_MEDIA;
        }
    }
    
    public String getTitleForMediaWithId(int mediaId) {
        List results = getHibernateTemplate().find("select i.title from org.tolweb.treegrow.main.NodeImage i where i.id=" + mediaId);
        if (results != null && results.size() > 0) {
            String title = ((String) results.get(0));
            return title;
        } else {
            return null;
        }
    }
    
    public Set getUnattachedMedia() {
    	String queryString = "SELECT image_id FROM Images where image_id not in (SELECT image_id FROM Images_To_Nodes) ORDER BY image_id DESC";
    	return executeRawSQLSelectForLongs(queryString);
    }
    
	public List getPrimaryImagesForNode(MappedNode node) {
		Query query = getSession().createQuery("select i from org.tolweb.treegrow.main.NodeImage i join i.nodesSet as nodes where nodes.nodeId=" + node.getNodeId() + " and i.isPrimaryImage=true");
		query.setMaxResults(2);
		return query.list();
	} 
	public List getContributorsWithImagesAttachedToNodeIds(Collection nodeIds) {
		String queryString = "select distinct i.contributor from org.tolweb.treegrow.main.NodeImage i join i.nodesSet as nodes where nodes.nodeId " + 
			StringUtils.returnSqlCollectionString(nodeIds) + " order by i.contributor.lastName";
		return getHibernateTemplate().find(queryString);
	}
	public List getMostRecentlyEditedImages(int numImages) {
		Query query = getSession().createQuery("from org.tolweb.treegrow.main.NodeImage i order by lastEditedDate desc");
		query.setMaxResults(numImages);
		return query.list();
	}
	public List getRandomGalleryImagesForNode(MappedNode node) {
		return getRandomGalleryImagesForNode(node, org.tolweb.treegrow.main.NodeImage.IMAGE);
	}

	public List getRandomGalleryImagesForNode(MappedNode node, int mediaClass) {
		return (List) getGalleryImagesForNode(node, -1, IMAGE_GALLERY_SIZE, true, false, null, mediaClass);
	}
	public List getGalleryImagesForNode(MappedNode node, int startIndex) {
		return getGalleryImagesForNode(node, startIndex, org.tolweb.treegrow.main.NodeImage.IMAGE);
	}

	public List getGalleryImagesForNode(MappedNode node, int startIndex, int mediaClass) {
		return (List) getGalleryImagesForNode(node, startIndex, IMAGE_GALLERY_SIZE, false, false, null, mediaClass);
	}
	public int getNumGalleryImagesForNode(MappedNode node) {
		return getNumGalleryImagesForNode(node, org.tolweb.treegrow.main.NodeImage.IMAGE);
	}

	public int getNumGalleryImagesForNode(MappedNode node, int mediaClass) {
		return (Integer) getGalleryImagesForNode(node, 0, 0, false, true, null, mediaClass);
	}	
	public int getNumGalleryTitleIllustrationsForNode(MappedNode node, List<Long> versionIds) {
		return (Integer) getGalleryImagesForNode(node, 0, IMAGE_GALLERY_SIZE, false, true, versionIds, NodeImage.IMAGE);
	}
	public List<Object[]> getGalleryTitleIllustrationsForNode(MappedNode node, List<Long> versionIds, int startIndex) {
		return (List<Object[]>) getGalleryImagesForNode(node, startIndex, IMAGE_GALLERY_SIZE, false, false, versionIds, NodeImage.IMAGE);
	}
	
	public List getLatestGalleryImagesForNode(MappedNode node, int fetchSize, int mediaClass) {
		String queryString = "select distinct i from org.tolweb.treegrow.main.NodeImage i";
		queryString += " join i.nodesSet as nodes join nodes.ancestors as nodeAncestors " + 
					   "where i.class=" + mediaClass + " and nodeAncestors.nodeId=" + 
					   node.getId() + " and i.creationDate != null";
		queryString += " order by i.creationDate desc ";
		Query query = getSession().createQuery(queryString);
		query.setMaxResults(fetchSize);
		return query.list();
	}
	
	private Object getGalleryImagesForNode(MappedNode node, int startIndex, int fetchSize, boolean isRandom, boolean countOnly, 
				Collection versionIds, int mediaClass) {
		boolean hasVersionIds = versionIds != null && versionIds.size() > 0;
		String queryString = "select ";
		if (countOnly) {
			queryString += "count(distinct i)";
		} else {
			if (!hasVersionIds) {
				queryString += "distinct i";
			} else {
				queryString += "i, v.versionId"; 
			}
		}
		queryString += " from org.tolweb.treegrow.main.NodeImage i";
		if (hasVersionIds) {
			queryString += ", org.tolweb.treegrow.main.ImageVersion v";
		}
		queryString += " join i.nodesSet as nodes " + 
		"join nodes.ancestors as nodeAncestors where i.class=" + mediaClass + " and nodeAncestors.nodeId=" + node.getId();
		if (hasVersionIds) {
			queryString += " and i.id=v.image.id and v.versionId " + StringUtils.returnSqlCollectionString(versionIds);
		}
		if (isRandom) {
			queryString += " order by rand()";
		} else {
			queryString += " order by i.id desc";
		}
		Query query = getSession().createQuery(queryString);
		if (!countOnly) {
			query.setMaxResults(fetchSize);
			if (!isRandom) {
				query.setFirstResult(startIndex);
			}
			return query.list();			
		} else {
			query.setCacheable(true);
			return query.uniqueResult();
		}		
	}
	public Hashtable<Number, NodeImage> getImagesFromVersionIds(Collection versionIds) {
		String queryString = "select v.versionId, v.image from org.tolweb.treegrow.main.ImageVersion v where v.versionId " + 
			StringUtils.returnSqlCollectionString(versionIds);
		List<Object[]> images = getHibernateTemplate().find(queryString);
		Hashtable<Number, NodeImage> resultsTable = new Hashtable<Number, NodeImage>();
		for (Object[] results : images) {
			resultsTable.put((Number) results[0], (NodeImage) results[1]);
		}
		return resultsTable;
	}

	public Boolean getShowImageInfoLinkForImageId(int imgId) {
		return (Boolean) getFirstObjectFromQuery("select i.showImageInfoLink from org.tolweb.treegrow.main.NodeImage i where i.id=" + imgId);
	}
	public Boolean getShowInlineImageInfoLinkForImageId(int imgId) {
		return (Boolean) getFirstObjectFromQuery("select i.showImageInfoLinkForInlineImage from org.tolweb.treegrow.main.NodeImage i where i.id=" + imgId);
	}
	
	public void reattachImage(Long imageId, Long oldNodeId, Long newNodeId) {
		int count = attachmentExists(imageId, newNodeId);
		if (count == 0) {
			Object[] args = new Object[] {newNodeId, imageId, oldNodeId};
			String fmt = "UPDATE Images_To_Nodes SET node_id = %1$d WHERE image_id = %2$d AND node_id = %3$d";
			executeRawSQLUpdate(String.format(fmt, args));
		}
	}

	private int attachmentExists(Long imageId, Long newNodeId) {
		int count = 0;
		String exists = "SELECT count(*) FROM Images_To_Nodes WHERE image_id = %1$d AND node_id = %2$d";
		Object[] args = new Object[] {imageId, newNodeId};
		Set results = executeRawSQLSelectForIntegers(String.format(exists, args));
		if (results != null && !results.isEmpty()) {
			Integer val = (Integer)results.iterator().next();
			count = val.intValue();
		}
		return count;
	}
	
	public List getNativeAttachedImagesForNode(Long nodeId, Collection excludeIds) {
		return getNativeAttachedImagesForNode(nodeId, excludeIds, false);
	}
	
	public List getNativeAttachedImagesForNode(Long nodeId, Collection excludeIds, boolean limitImages) {
		String hql = "select i from org.tolweb.treegrow.main.NodeImage i" + 
					" join i.nodesSet as nodes where nodes.nodeId=" + 
					nodeId + " and i.sourceDbId is null " + 
					"and i.artisticInterpretation = 0 " + 
					"and (i.sourceCollectionUrl is null or i.sourceCollectionUrl = '')";
		if (limitImages) {
			hql += " and (i.isSpecimen = 1 or i.isUltrastructure = 1 or i.isBodyParts = 1" +
				   " or i.imageType = 'Photograph' or i.imageType = 'Drawing/Painting')";
		}
		if (excludeIds != null && !excludeIds.isEmpty()) {
			hql += " and i.id not " + StringUtils.returnSqlCollectionString(excludeIds);
		}
		Query query = getSession().createQuery(hql);
		return query.list();		
	}
	public List getNativeAttachedImagesForNode(Long nodeId) {
		return getNativeAttachedImagesForNode(nodeId, null);
	}
	
    public ForeignDatabase getForeignDatabaseWithId(Long id) {
    	return (ForeignDatabase) getObjectWithId(ForeignDatabase.class, id);
    }
    
    public ForeignDatabase getForeignDatabaseWithName(String name) {
    	String queryString = "from org.tolweb.hibernate.ForeignDatabase where name=?";
    	return (ForeignDatabase)getFirstObjectFromQuery(queryString, name);
    }	
	
    /**
     * @return Returns the publicPageDAO.
     */
    public PageDAO getPublicPageDAO() {
        return publicPageDAO;
    }

    /**
     * @param publicPageDAO The publicPageDAO to set.
     */
    public void setPublicPageDAO(PageDAO publicPageDao) {
        this.publicPageDAO = publicPageDao;
    }

    /**
     * @return Returns the workingPageDAO.
     */
    public PageDAO getWorkingPageDAO() {
        return workingPageDAO;
    }

    /**
     * @param workingPageDAO The workingPageDAO to set.
     */
    public void setWorkingPageDAO(PageDAO workingPageDao) {
        this.workingPageDAO = workingPageDao;
    }
}
