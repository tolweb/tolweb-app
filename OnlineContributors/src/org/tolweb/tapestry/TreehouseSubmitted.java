/*
 * Created on Jul 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class TreehouseSubmitted extends BasePage implements UserInjectable, 
		TreehouseInjectable, BaseInjectable {
    
	@SuppressWarnings("unchecked")
    public List getEmailAddresses() {
        List addresses = new ArrayList();
        Contributor submittedContributor = getContributor();
        if (getIsStudent()) {
            addresses.add(((Student) submittedContributor).getTeacher().getEmail());
        }
        addresses.add(getConfiguration().getLearningEditorEmail());
        return addresses;
    }
    
    public boolean getIsStudent() {
        Contributor submittedContributor = getContributor();
        return Student.class.isInstance(submittedContributor);
    }
}
