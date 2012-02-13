/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.MiscInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ImageSearchPage extends BasePage implements PageBeginRenderListener, MiscInjectable {
//    public static StringPropertySelectionModel IMAGE_TYPE_SELECTION_MODEL,
//		CONTENT_SELECTION_MODEL, CONDITION_SELECTION_MODEL, SEX_SELECTION_MODEL, 
//		TYPE_SELECTION_MODEL;
//    static final String ANY_TYPE = "any type";
//    static final String ANY_CONTENT = "any content";
//    static final String ANY_SEX = "any sex";
//    static final String ANY_CONDITION = "any condition";
//    static final String ANY_TYPE_SPECIMENS = "any type or other specimens";
//    static final String TYPE_SPECIMENS_ONLY = "type specimens only";
//    public static final String SPECIMENS = "Specimen(s) (Pictures of Organisms)";
//    public static final String BODYPARTS = "Body Parts"; 
//    public static final String ULTRASTRUCTURE = "Ultrastructure"; 
//    public static final String HABITAT = "Habitat";
//    public static final String EQUIPMENT = "Equipment/Apparatus";
//    public static final String PEOPLE = "People at Work";     
    public void pageBeginRender(PageEvent e) {
//        if (IMAGE_TYPE_SELECTION_MODEL == null) {
//            // initialize all the selection models
//            IMAGE_TYPE_SELECTION_MODEL = getPropertySelectionFactory().createModelFromList(NodeImage.IMAGE_TYPES_LIST, ANY_TYPE);
//            CONTENT_SELECTION_MODEL = new StringPropertySelectionModel(new String[] {ANY_CONTENT,
//                    SPECIMENS, BODYPARTS, ULTRASTRUCTURE, HABITAT, 
//                    EQUIPMENT, PEOPLE});
//            SEX_SELECTION_MODEL = new StringPropertySelectionModel(new String[] {ANY_SEX,
//                    "Female", "Male", "Other"});
//            CONDITION_SELECTION_MODEL = new StringPropertySelectionModel(new String[] {
//                    ANY_CONDITION, "Live Specimen", "Dead Specimen/Model", "Fossil"});
//            TYPE_SELECTION_MODEL = getPropertySelectionFactory().createModelFromList(NodeImage.TYPES_LIST,
//                    ANY_TYPE_SPECIMENS, TYPE_SPECIMENS_ONLY);
//        }
    }    
}
