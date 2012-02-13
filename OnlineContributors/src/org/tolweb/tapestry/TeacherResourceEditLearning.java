/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.MappedAccessoryPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceEditLearning extends AbstractTreehouseEditingPage {
    
    public static final String PROGRESS_PROPERTY = "learningProgress";
    public abstract void setStateStandardsSearchSelected(boolean value);
    public abstract boolean getStateStandardsSearchSelected();
    public abstract void setNationalStandardsSearchSelected(boolean value);
    public abstract boolean getNationalStandardsSearchSelected();
    
    public int getStepNumber() {
        if (getIsWebquest()) {
            return 8;
        } else {
            return 5;
        }
    }
    
    public void pageValidate(PageEvent event) {
        
    }
    
    protected void goToOtherEditPageName(IRequestCycle cycle) {
        if (getOtherEditPageName().equals("TeacherResourceMediaSearchPage")) {
	        TeacherResourceMediaSearchPage page = (TeacherResourceMediaSearchPage) cycle.getPage("TeacherResourceMediaSearchPage");
	        if (getStateStandardsSearchSelected()) {
	            page.setCallbackType(ImageSearchResults.TR_STATE_STANDARDS_CALLBACK);
	        } else {
	            page.setCallbackType(ImageSearchResults.TR_NATIONAL_STANDARDS_CALLBACK);
	        }
	        cycle.activate(page);
        } else {
            super.goToOtherEditPageName(cycle);
        }
    }
    
    public IPropertySelectionModel getGradeLevelModel() {
        return new IPropertySelectionModel() {
            public int getOptionCount() {
	            return 9;
	        }
	        
	        public Object getOption(int index) {
	            return Integer.valueOf(index);
	        }
	        
	        public String getLabel(int index) {
	            return MappedAccessoryPage.getGradeLevelLabel(index);
	        }
	        
	        public String getValue(int index) {
	            return Integer.toString(index);
	        }
	        
	        public Object translateValue(String value) {
	            int index = Integer.parseInt(value);
	            return getOption(index);
	        }            
        };
    }
    
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}
	
	public void stateStandardsSearch(IRequestCycle cycle) {
	    setStateStandardsSearchSelected(true);
	    setOtherEditPageName("TeacherResourceMediaSearchPage");
	}
	
	public void nationalStandardsSearch(IRequestCycle cycle) {
	    setNationalStandardsSearchSelected(true);
	    setOtherEditPageName("TeacherResourceMediaSearchPage");
	}
    
    public Block getHelpBlock() {
        String blockName = "";
        if (getIsWebquest()) {
            blockName = "webquestHelpBlock";
        } else {
            blockName = "teacherResourceHelpBlock";
        }
        return (Block) getComponents().get(blockName);
    }
}
