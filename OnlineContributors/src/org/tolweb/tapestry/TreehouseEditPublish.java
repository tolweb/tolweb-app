/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditPublish extends AbstractTreehouseEditingPage implements UserInjectable, TreehouseInjectable {
    public void submitSelected(IRequestCycle cycle) {
        if (Student.class.isInstance(getContributor())) {
            cycle.activate("StudentSubmitTreehouse");
        } else {
    		TreehouseDataConfirm page = (TreehouseDataConfirm) cycle.getPage("TreehouseDataConfirm");
    		page.setTreehouse(getTreehouse());
    		cycle.activate(page);
        }
    }
    
    public int getStepNumber() {
        if (getIsWebquest()) {
            return 11;
        } else if (getIsTeacherResource() || getIsPortfolio()){
            if (getIsOther() || getIsPortfolio()) {
                return 9;
            } else {
                return 10;
            }
        } else {
            return 8;
        }
    }
}
