/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.tolweb.dao.ImageDAO;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class UserImageSearch extends ImageSearch implements ImageInjectable, PageBeginRenderListener {
    public static final int MAX_RESULTS = 100;

	static final String ANY_TYPE = "any type";
	static final String ANY_CONTENT = "any content";
	static final String ANY_SEX = "any sex";
	static final String ANY_CONDITION = "any condition";
	static final String ANY_TYPE_SPECIMENS = "any type or other specimens";
	static final String TYPE_SPECIMENS_ONLY = "type specimens only";
	public static final String SPECIMENS = "Specimen(s) (Pictures of Organisms)";
	public static final String BODYPARTS = "Body Parts"; 
	public static final String ULTRASTRUCTURE = "Ultrastructure"; 
	public static final String HABITAT = "Habitat";
	public static final String EQUIPMENT = "Equipment/Apparatus";
	public static final String PEOPLE = "People at Work";     

	public static StringPropertySelectionModel IMAGE_TYPE_SELECTION_MODEL,
			CONTENT_SELECTION_MODEL, CONDITION_SELECTION_MODEL,
			SEX_SELECTION_MODEL, TYPE_SELECTION_MODEL;

	public abstract String getCredits();
	public abstract String getLocationText();
	public abstract String getAnyText();
	public abstract String getImageTypeSelection();
	public abstract String getImageContentSelection();
	public abstract String getSpecimenConditionSelection();
	public abstract String getBehavior();
	public abstract String getSexSelection();
	public abstract String getLifeCycleStage();
	public abstract String getBodyPart();
	public abstract String getView();
	public abstract String getCollection();
	public abstract String getTypeSelection();
	public abstract String getVoucherNumber();
	public abstract String getVoucherCollection();
	public abstract String getCollector();
	public abstract String getIdentifiedBy();
	public abstract void setTooManyResults(boolean value);
	public abstract boolean getTooManyResults();

	// ------- overridden since these are hardcoded
	public abstract byte getWrapperType();
	public abstract String getReturnPageName();
	public abstract int getCallbackType();	

	@Parameter(required = false, defaultValue = "")
	public abstract String getDivClass();
	public abstract void setDivClass(String css);
	
	public String getDivStyle() {
		if (StringUtils.isEmpty(getDivClass())) {
			return "background: #f6f6f6; border: 1px solid #5d5040; padding: 25px";
		} else {
			return "";
		}
	}
	
	public void pageBeginRender(PageEvent e) {
	    if (IMAGE_TYPE_SELECTION_MODEL == null) {
	        // initialize all the selection models
	        IMAGE_TYPE_SELECTION_MODEL = getPropertySelectionFactory().createModelFromList(NodeImage.IMAGE_TYPES_LIST, ANY_TYPE);
	        CONTENT_SELECTION_MODEL = new StringPropertySelectionModel(new String[] {ANY_CONTENT,
	                SPECIMENS, BODYPARTS, ULTRASTRUCTURE, HABITAT, 
	                EQUIPMENT, PEOPLE});
	        SEX_SELECTION_MODEL = new StringPropertySelectionModel(new String[] {ANY_SEX,
	                "Female", "Male", "Other"});
	        CONDITION_SELECTION_MODEL = new StringPropertySelectionModel(new String[] {
	                ANY_CONDITION, "Live Specimen", "Dead Specimen/Model", "Fossil"});
	        TYPE_SELECTION_MODEL = getPropertySelectionFactory().createModelFromList(NodeImage.TYPES_LIST,
	                ANY_TYPE_SPECIMENS, TYPE_SPECIMENS_ONLY);
	    }
	}	
	
	@SuppressWarnings("unchecked")
	public void doSearch(IRequestCycle cycle) {
	    Hashtable args = new Hashtable();
	    addIfNotNull(getGroupName(), ImageDAO.GROUP, args);
	    addIfNotNull(getCredits(), ImageDAO.CREDITS, args);
	    addIfNotNull(getLocationText(), ImageDAO.LOCATION, args);
	    addIfNotNull(getAnyText(), ImageDAO.ANY_DATA, args);
	    addIfNotNull(getImageTypeSelection(), ImageDAO.IMAGE_TYPE, args, ANY_TYPE);
	    addImageContentSelection(args);
	    addConditionSelection(args);
	    addIfNotNull(getBehavior(), ImageDAO.BEHAVIOR, args);
	    addIfNotNull(getSexSelection(), ImageDAO.SEX, args, ANY_SEX);
	    addIfNotNull(getLifeCycleStage(), ImageDAO.STAGE, args);
	    addIfNotNull(getBodyPart(), ImageDAO.BODY_PART, args);
	    addIfNotNull(getView(), ImageDAO.VIEW, args);
	    addIfNotNull(getCollection(), ImageDAO.COLLECTION, args);
	    addTypeSelection(args);
	    addIfNotNull(getVoucherNumber(), ImageDAO.VOUCHER_NUMBER, args);
	    addIfNotNull(getVoucherCollection(), ImageDAO.VOUCHER_COLLECTION, args);
	    addIfNotNull(getCollector(), ImageDAO.COLLECTOR, args);
	    addIfNotNull(getIdentifiedBy(), ImageDAO.IDENTIFIER, args);
	    args.put(ImageDAO.SEARCH_ANCESTORS, Boolean.valueOf(true));
	    String mediaTypeSelection = getMediaTypeSelection();	    
	    ImageDAO dao = getImageDAO();
	    int numMatches = dao.getNumImagesMatchingCriteria(args); 
	    if (numMatches > MAX_RESULTS && !mediaTypeSelection.equals(SOUND) && 
	    		!mediaTypeSelection.equals(DOCUMENT) && !mediaTypeSelection.equals(MOVIE)) {
	        setTooManyResults(true);
	        //setNumResults(numMatches);
	    } else if (numMatches == 0) {
	        setNoResults(true);
	        return;	        
	    }
	    List resultImages = null;
	    if (mediaTypeSelection.equals(MOVIE)) {
	    	resultImages = dao.getMoviesMatchingCriteria(args, MAX_RESULTS);
	    } else if (mediaTypeSelection.equals(DOCUMENT)) {
	    	resultImages = dao.getDocumentsMatchingCriteria(args, MAX_RESULTS);
	    } else if (mediaTypeSelection.equals(SOUND)) {
	    	resultImages = dao.getSoundsMatchingCriteria(args, MAX_RESULTS);
	    } else {
	    	resultImages = dao.getImagesMatchingCriteria(args, MAX_RESULTS);
	    }
	    if (resultImages == null) {
	        setNoResults(true);
	        return;
	    }
	    // Check again since there could have possibly been duplicates in the count query
	    //if (resultImages.size() < 100) {
	    //    setTooManyResults(false);
	    //}
	    ImageSearchResults results = (ImageSearchResults) cycle.getPage("UserImageSearchResults");
		results.setImages(resultImages);
		results.setCallbackType(getCallbackType());
		results.setWrapperType(AbstractWrappablePage.SEARCH_WRAPPER);
		results.setDontShowFilename(true);
		setSearchResultsVariables(results);
		cycle.activate(results);
		return;
	}
	
	@SuppressWarnings("unchecked")
	private void addConditionSelection(Hashtable args) {
	    String condition = getSpecimenConditionSelection();
	    if (condition != null) {
	        String conditionString = null;
	        if (condition.indexOf("Live") != -1) {
	            conditionString = NodeImage.ALIVE;
	        } else if (condition.indexOf("Dead") != -1) {
	            conditionString = NodeImage.DEAD;
	        } else if (condition.indexOf("Fossil") != -1) {
	            conditionString = NodeImage.FOSSIL;
	        }
	        addIfNotNull(conditionString, ImageDAO.CONDITION, args);
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void addTypeSelection(Hashtable args) {
	    String selection = getTypeSelection();
	    if (StringUtils.notEmpty(selection) && !selection.equals(ANY_TYPE_SPECIMENS)) {
	        if (selection.equals(TYPE_SPECIMENS_ONLY)) {
	            addIfNotNull(ImageDAO.ANY_TYPE, ImageDAO.TYPE, args);
	        } else {
	            addIfNotNull(selection, ImageDAO.TYPE, args);
	        }
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void addImageContentSelection(Hashtable args) {
	    String contentSelection = getImageContentSelection();
	    if (StringUtils.notEmpty(contentSelection)) {
	    	Boolean boolVal = Boolean.valueOf(true);
	        if (contentSelection.equals(BODYPARTS)) {
	            addIfNotNull(boolVal, ImageDAO.IS_BODYPARTS, args);
	        } else if (contentSelection.equals(EQUIPMENT)) {
	            addIfNotNull(boolVal, ImageDAO.IS_EQUIPMENT, args);
	        } else if (contentSelection.equals(HABITAT)) {
	            addIfNotNull(boolVal, ImageDAO.IS_HABITAT, args);
	        } else if (contentSelection.equals(PEOPLE)) {
	            addIfNotNull(boolVal, ImageDAO.IS_PEOPLE, args);
	        } else if (contentSelection.equals(SPECIMENS)) {
	            addIfNotNull(boolVal, ImageDAO.IS_SPECIMENS, args);
	        } else if (contentSelection.equals(ULTRASTRUCTURE)) {
	            addIfNotNull(boolVal, ImageDAO.IS_ULTRASTRUCTURE, args);
	        } 
	    }
	    addIfNotNull(getImageContentSelection(), ImageDAO.IMAGE_CONTENT, args, ANY_CONTENT);	    
	}
	
	/**
	 * Overridden to check and make sure that the value doesn't get added
	 * if it is set to a default-value
	 * @param value
	 * @param key
	 * @param args
	 * @param defaultValue
	 */
	@SuppressWarnings("unchecked")
	public void addIfNotNull(String value, Object key, Hashtable args, String defaultValue) {
	    if (StringUtils.notEmpty(value) && !value.equals(defaultValue)) {
	        super.addIfNotNull(value, key, args);
	    }
	}
	
	public void setInstructions(String value) {}
	public String getInstructions() {
	    if (getTooManyResults()) {
	        return "Your search found more than 100 results.  The first 100 are viewable." +
	        	" To get more specific results, please use a more restrictive search criteria.";
	    } else {
	        return "";
	    }
	}
	
	public String getLinkString() {
	    return "";
	}
	
	public String getSearchPageName() {
	    return "ImageSearchPage";
	}
	public boolean getShowMediaTypeSelection() {
		return true;
	}
	public void setShowMediaTypeSelection(boolean value) {}
}
