/*
 * Login.java
 *
 * Created on April 30, 2004, 2:42 PM
 */

package org.tolweb.tapestry;

import java.util.Collection;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ExternalCallback;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Student;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

// TODO: Make sure everyone that uses this has some sort of persistence mechanism since parameters are no longer persistent
/**
 *
 * @author  dmandel
 */
@ComponentClass(allowInformalParameters = false)
public abstract class Login extends BaseComponent implements UserInjectable, NodeInjectable, CookieInjectable {
    public static final String PASSWORD_SENT_PAGE = "PasswordSent";
    public static final String IMAGE_LOGIN_PAGE = "ContributingImages";
    public static final String TREEHOUSE_LOGIN_PAGE = "TreehouseContributorLogin";
    public static final String SCIENTIFIC_LOGIN_PAGE = "ScientificContributorsLogin";
    
    @Persist("client")
    public abstract String getEmail();
    public abstract String getPassword();
    public abstract void setDestinationPageName(String value);
    public abstract String getError();
    public abstract void setError(String value);
    public abstract boolean getWrongPassword();
    public abstract void setWrongPassword(boolean value);    
    @Parameter(required = true)
    public abstract String getDestinationPageName();
    @Parameter
    public abstract boolean getDynamicUpdateRegInfoDestination();
    @Parameter(defaultValue = "@org.tolweb.treegrow.main.Contributor@ANY_CONTRIBUTOR")
    public abstract byte getUserType();
    @Parameter
    public abstract Object[] getExternalPageParameters();
    public abstract void setExternalPageParameters(Object[] value);
    @Parameter
    public abstract boolean getDynamicDestination();
    @Parameter
    public abstract boolean getGoToWorkingPage();
    @Parameter
    public abstract Collection<Byte> getUserTypes();
    public abstract boolean getRememberMe();    
    @InjectObject("spring:urlBuilder")
    public abstract URLBuilder getUrlBuilder();
    
    @Bean
    public abstract ValidationDelegate getDelegate();
    
    public void loginSubmit(IRequestCycle cycle) {
        ContributorDAO dao = getContributorDAO();
        PasswordUtils pwUtils = getPasswordUtils();
        Contributor contr;
        if (getUserTypes() != null) {
        	contr = dao.getContributorWithEmail(getEmail(), getUserTypes());
        } else {
        	contr = dao.getContributorWithEmail(getEmail(), getUserType());
        }        
        String actualPageName = "";        
        if (contr == null) {
            contr = dao.getStudentWithAlias(getEmail());
        } 
        doAdditionalChecking(contr);
        if ((getDynamicDestination() || getDynamicUpdateRegInfoDestination()) && contr != null) {
            byte contributorType = contr.getContributorType();
            if (getDynamicDestination()) {
            	// TODO: when merging works, merge this in properly
            	actualPageName = getUrlBuilder().getDefaultManagerPageNameForContributor(contr);            	
				if (contributorType == Contributor.OTHER_SCIENTIST) {
	            	// need to go right to the contributor registration page
	            	setContributor(contr);
	            	ScientificContributorRegistrationOther editPage = (ScientificContributorRegistrationOther) cycle.getPage("ScientificContributorRegistrationOther");
	            	editPage.setEditedContributor(contr);
	            	editPage.setIsNonCoreContributor(true);
	            	cycle.activate(editPage);
	            	return;
	            }
            } else {
            	// here it's a user logging in to update their reg info
            	actualPageName = getUrlBuilder().getRegistrationPageNameForContributor(contr);
            	setExternalPageParameters(new Object[] {contr.getId()});
            }
        } else {
            actualPageName = getDestinationPageName();
        }
        if (contr == null) {
            setError("There is no registered user with that address");
            setPageProperties();
        } else if (!pwUtils.checkPassword(contr, getPassword())) {
            setError("The typed password is incorrect.");
            setWrongPassword(true);
            setPageProperties();
        } else if (getError() == null) {
        	setContributor(contr);
            int cookieAge = CookieAndContributorSource.DEFAULT_COOKIE_MAX_AGE;
            if (getRememberMe()) {
            	cookieAge = CookieAndContributorSource.ONE_YEAR;
            }
            getCookieAndContributorSource().loginContributor(contr, cookieAge);
            if (getExternalPageParameters() != null) {
            	if (getGoToWorkingPage()) {
            		// get the node name and id
            		String nodeIdString = (String) getExternalPageParameters()[1];
            		MappedNode node = getWorkingNodeDAO().getNodeWithId(Long.parseLong(nodeIdString));
            		String url = getUrlBuilder().getWorkingURLForObject(node);
            		throw new RedirectException(url);
            	} else {
	                Object[] parameters = getExternalPageParameters();
	                ExternalCallback callback = new ExternalCallback(actualPageName, parameters);
	                callback.performCallback(cycle);
            	}
            } else {
            	IPage page = cycle.getPage(actualPageName);
            	doAdditionalPageInitialization(page);
                cycle.activate(actualPageName);
            }
        }
    }
    /**
     * Empty method to be overridden by subclasses
     */    
    protected void doAdditionalPageInitialization(IPage page) {
    }
    /**
     * Empty method to be overridden by subclasses
     */
    protected void doAdditionalChecking(Contributor contr) {
    }
    
    private void setPageProperties() {
        try { PropertyUtils.write(getPage(), "destinationPageName", getDestinationPageName());} catch (Exception e) { e.printStackTrace(); }
        try { PropertyUtils.write(getPage(), "userType", Byte.valueOf(getUserType()));} catch (Exception e) { e.printStackTrace(); }
        try { PropertyUtils.write(getPage(), "externalPageParameters", getExternalPageParameters());} catch (Exception e) { e.printStackTrace(); } 
    }
    
    public void emailPassword(IRequestCycle cycle) {
        ContributorDAO dao = getContributorDAO();
    	Contributor contr = dao.getContributorWithEmail(getEmail());
        if (contr == null) {
            contr = dao.getStudentWithAlias(getEmail());
        }
    	PasswordSent sentPage = (PasswordSent) cycle.getPage("PasswordSent");
    	sentPage.setContributor(contr);
    	if (getUserType() == Contributor.TREEHOUSE_CONTRIBUTOR || Student.class.isInstance(contr)) {
    	    sentPage.setWrapperType(AbstractWrappablePage.LEARNING_WRAPPER);    		
    	} else {
    	    sentPage.setWrapperType(AbstractWrappablePage.DEFAULT_WRAPPER);
    	}
        cycle.activate(sentPage);    	
    }
    
    public String getDisplayNameCode() {
        return "User ID <span class=\"tiny\">(email)</span>";
    }
}
