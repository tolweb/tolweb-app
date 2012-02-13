/*
 * Created on May 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceChooseResourceType extends AbstractTreehouseEditingPage implements UserInjectable, AccessoryInjectable {
    public abstract int getResourceType();
    
    public IPropertySelectionModel getResourceTypeModel() {
        return new IPropertySelectionModel() {
            public int getOptionCount() {
	            return 4;
	        }
	        
	        public Object getOption(int index) {
	            return Integer.valueOf(index);
	        }
	        
	        public String getLabel(int index) {
	            switch (index) {
	            	case 0: return "Lesson";
	            	case 1: return "Activity";
	            	case 2: return "Project";
	            	//case 3: return "Unit";
	            	//case 3: return "WebQuest";
	            	case 3: return "Other";
	            	default: return "";
	            }
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
    
    public void formSubmitted(IRequestCycle cycle) {
        int resourceType = getResourceType();
        getAccessoryPageHelper().initializeNewAccessoryPageInstance(getContributor(), 
                MappedAccessoryPage.TEACHERRESOURCE, true, getWorkingAccessoryPageDAO(), 
                resourceType, true);
        cycle.activate("TreehouseEditor");
    }
}
