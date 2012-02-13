package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class NewPasswordEmail extends BaseComponent implements BaseInjectable {
    public abstract Contributor getContributor();
    @Parameter
    public abstract boolean getIsBtol();
    @Parameter
    public abstract String getAdditionalEmail();
    @InjectObject("spring:passwordUtils")
    public abstract PasswordUtils getPasswordUtils();
    
    public String getToEmail() {
        if (getIsStudent()) {
            return ((Student) getContributor()).getTeacher().getEmail();
        } else {
            return getContributor().getEmail();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List getEmailAddresses() {
    	List addresses = new ArrayList<String>();
    	addresses.add(getToEmail());
    	
			if (StringUtils.notEmpty(getAdditionalEmail())) {
				addresses.add(getAdditionalEmail());
			}

    	return addresses;
    }
    
    public boolean getIsStudent() {
        return Student.class.isInstance(getContributor()); 
    }
    
    public Student getStudent() {
        return (Student) getContributor();
    }
    
    public String getSubject() {
        if (getIsStudent()) {
            return "Student " + getStudent().getAlias() + "'s Treehouse Password";
        } else if (getConfiguration().getIsBtol()) {
        	return "BTOL Contributor Registration";
        } else {
            return "Tree of Life Contributor Registration";
        }
    }
    public String getNewPassword() {
    	return getPasswordUtils().resetPassword(getContributor()) + "\n";
    }
}
