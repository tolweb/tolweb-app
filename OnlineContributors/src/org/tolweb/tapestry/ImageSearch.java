/*
 * Created on Jun 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ImageSearch extends AbstractSearch implements UserInjectable, ImageInjectable, MiscInjectable {	
    public static final String IMAGE = "Image";
    public static final String DOCUMENT = "Document";
    public static final String SOUND = "Sound";
    public static final String MOVIE = "Movie";
    public static final String MEDIA = "Media";
    private IPropertySelectionModel mediaTypeModel;
    
	public abstract Integer getImageId();
	public abstract String getFilename();
	public abstract String getScientificName();
	public abstract String getCopyrightOwner();
	public abstract String getAnyText();
	public abstract boolean getSearchOnlyContributorsImages();
	public abstract byte getImageUse();
	public abstract Long getEditedObjectId();
	public abstract String getContainingPageNoResultsAnchor();
	public abstract String getEditWindowName();
	public abstract String getMediaType();
	public abstract String getMediaTypeSelection();
	public abstract boolean getShowMediaTypeSelection();
	public abstract String getGroupNameProperty();
	public abstract void setGroupNameProperty(String value);
	@Parameter(required = false, defaultValue = "@org.tolweb.tapestry.AbstractWrappablePage@DEFAULT_WRAPPER")
	public abstract byte getWrapperType();
	@Parameter(required = false)
	public abstract boolean getShowPodcastsCheckbox();
	public abstract boolean getSearchOnlyPodcasts();
	public abstract void setSearchOnlyPodcasts(boolean value);
	
	protected void prepareForRender(IRequestCycle cycle) {
		super.prepareForRender(cycle);
		if (StringUtils.notEmpty(getGroupName())) {
			setGroupNameProperty(getGroupName());
		}
	}
	
	public IPropertySelectionModel getMediaTypeModel() {
		if (mediaTypeModel == null) {
			String[] choices = {"all media types", IMAGE, DOCUMENT, SOUND, MOVIE};
			mediaTypeModel = getPropertySelectionFactory().createModelFromList(Arrays.asList(choices));
		} 
		return mediaTypeModel;
	}
    
	@SuppressWarnings("unchecked")
	public void doSearch(IRequestCycle cycle) {
		String filenameStr = getFilename();
		String scientificName = getScientificName();
		String groupNameStr = getGroupNameProperty();
		String copyOwnerStr = getCopyrightOwner();
		String anyDataStr = getAnyText();
		
		ValidationDelegate delegate = (ValidationDelegate) getBeans().getBean("delegate");
		if (delegate.getHasErrors()) {
		    setPageProperties();
			return;
		}        
		Hashtable args = new Hashtable();
		byte usePermission = getImageUse();
		Contributor contributor = getContributor();
		Contributor copyOwnerContributor = contributor;
		boolean isImageEditor = contributor != null && contributor.getIsImageEditor();
		boolean restrictUse = usePermission != -1 && !isImageEditor; 

		Integer imageId = getImageId();
		addIfNotNull(imageId, ImageDAO.IMAGE_ID, args);
		String filename = StringUtils.notEmpty(filenameStr) ? filenameStr.trim() : null;
		addIfNotNull(filename, ImageDAO.FILENAME, args);        
		String sciName = StringUtils.notEmpty(scientificName) ? scientificName.trim() : null;
		addIfNotNull(sciName, ImageDAO.SCIENTIFIC_NAME, args);
		String groupName = StringUtils.notEmpty(groupNameStr) ? groupNameStr.trim() : null;
		addIfNotNull(groupName, ImageDAO.GROUP, args);
		String copyOwner = StringUtils.notEmpty(copyOwnerStr) ? copyOwnerStr.trim() : null;
		addIfNotNull(copyOwner, ImageDAO.COPYOWNER, args);
		String anyData = StringUtils.notEmpty(anyDataStr) ? anyDataStr.trim() : null;
		addIfNotNull(anyData, ImageDAO.ANY_DATA, args);
		
		if (isImageEditor || !getSearchOnlyContributorsImages() || getIsEditSearch()) {
			// If they are an editor, they can edit anyone's images so don't 
			// include them in the criteria -- or if it's an edit search need to
		    // restrict in another way
			contributor = null;
			copyOwnerContributor = null;
		}
		addIfNotNull(contributor, ImageDAO.CONTRIBUTOR, args);
		addIfNotNull(copyOwnerContributor, ImageDAO.COPYOWNER_CONTRIBUTOR, args);
	    args.put(ImageDAO.SEARCH_ANCESTORS, Boolean.valueOf(true));
	    if (getSearchOnlyPodcasts()) {
	    	args.put(ImageDAO.ONLY_PODCASTS, Boolean.valueOf(true));
	    }
		List resultMedia = null;
		try {
		    String mediaType = getMediaType();
		    // if we are allowing the user to choose, honor their selection
		    if (getShowMediaTypeSelection()) {	    	
		    	mediaType = getMediaTypeSelection();
		    }
		    ImageDAO dao = getImageDAO();
			if (mediaType.equals(DOCUMENT)) {
			    resultMedia = dao.getDocumentsMatchingCriteria(args); 
			} else if (mediaType.equals(SOUND)) {
			    resultMedia = dao.getSoundsMatchingCriteria(args);
			} else if (mediaType.equals(MOVIE)) {
			    resultMedia = dao.getMoviesMatchingCriteria(args);
			} else if (getIsOnlyMediaSearch()) {
                // just do 3 searches for now.  if we ever get a lot of 
                // media, it would probably be good to figure out how to do
                // an optimized hibernate search over 3 subclasses but I don't
                // have the energy to deal with their unhelpful user boards right now
			    resultMedia = new ArrayList();
                resultMedia.addAll(dao.getDocumentsMatchingCriteria(args));
                resultMedia.addAll(dao.getSoundsMatchingCriteria(args));
                resultMedia.addAll(dao.getMoviesMatchingCriteria(args));
            } else {
				resultMedia = dao.getImagesMatchingCriteria(args, UserImageSearch.MAX_RESULTS);			    
			}
		} catch (Exception e) {
			resultMedia = new ArrayList();
			e.printStackTrace();
		}
		PermissionChecker checker = getPermissionChecker();
		contributor = getContributor();
		if (getIsEditSearch()) {
		    for (Iterator iter = new ArrayList(resultMedia).iterator(); iter.hasNext();) {
                NodeImage img = (NodeImage) iter.next();
                if (!checker.checkEditingPermissionForImage(contributor, img)) {
                    resultMedia.remove(img);
                }
            }
		}
		
		// Now go through and restrict based on use permission -- makes more
		// sense than doing two disparate db queries
		if (restrictUse) {
		    for (Iterator iter = new ArrayList(resultMedia).iterator(); iter.hasNext();) {
                NodeImage img = (NodeImage) iter.next();
                if (img.getUsePermission() <= usePermission) {
                    int callbackType = getCallbackType();
                    boolean isPage = callbackType == ImageSearchResults.TILLUS_CALLBACK;
                    if (!checker.checkUsePermissionForImageOnPage(contributor, img, getEditedObjectId(), isPage)) {
                        resultMedia.remove(img);
                    }
                }
            }
		}
		if (resultMedia.size() == 0) {
			setNoResults(true);
			setPageProperties();
		} else {
		    cycle.forgetPage("ImageSearchResults");
			ImageSearchResults results = (ImageSearchResults) cycle.getPage("ImageSearchResults");
			results.setImages(resultMedia);
			results.setCallbackType(getCallbackType());
			results.setWrapperType(getWrapperType());
			results.setEditedObjectId(getEditedObjectId());
			results.setEditWindowName(getEditWindowName());
			results.setMediaType(getMediaType());
			results.setSearchPageName(getPage().getPageName());
			results.setAdditionalReturnPageName(getReturnPageName());
			setSearchResultsVariables(results);			
			cycle.activate(results);
			return;
		}        
	}
	
	protected void setPageProperties() {
	    super.setPageProperties();
		try {
		    PropertyUtils.write(getPage(), "mediaType", getMediaType());
		} catch (Exception e) {}
		try {
			PropertyUtils.write(getPage(), "editedObjectId", getEditedObjectId()); 
		} catch (Exception e) {}
		try {
			PropertyUtils.write(getPage(), "returnPageName", getReturnPageName()); 
		} catch (Exception e) {}
        try {
        	PropertyUtils.write(getPage(), "notHtmlEditor", 
        			Boolean.valueOf(getCallbackType() == ImageSearchResults.PAGE_IMAGE_NOEDITOR_CALLBACK)); 
        } catch (Exception e) {}
        try {
        	PropertyUtils.write(getPage(), "searchMedia", getIsOnlyMediaSearch());
        } catch (Exception e) {}
    }
	
    private boolean getIsOnlyMediaSearch() {
        return getCallbackType() == ImageSearchResults.PAGE_MEDIA_CALLBACK;
    }
	private boolean getIsEditSearch() {
	    return getCallbackType() == ImageSearchResults.EDIT_IMAGE_CALLBACK;
	}
	
	public boolean getShowS() {
	    return !MEDIA.equals(getMediaType());
	}
}
