/*
 * Created on Jun 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceEducationStandards extends TeacherResourceViewComponent implements 
		ImageInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
    public List getStateStandardsSubjects() {
        return getTextPreparer().getNewlineSeparatedList(getTeacherResource().getStateStandardsSubjects());
    }
    
    public String getStateStandardsSubjectsString() {
        return getUlListStringWithSemicolons(getStateStandardsSubjects(), null);
    }
    
    public String getNationalStandardsSubjectsString() {
        return getUlListStringWithSemicolons(getNationalStandardsSubjects(), null);
    }
    
    @SuppressWarnings("unchecked")
    public List getNationalStandardsSubjects() {
        return getTextPreparer().getNewlineSeparatedList(getTeacherResource().getNationalStandardsSubjects());
    }
    
    public boolean getHasStateStandardsDocument() {
        return getTeacherResource().getStateStandardsDocument() != null;
    }
    
    public String getStateStandardsUrl() {
        return getImageUtils().getImageUrl(getTeacherResource().getStateStandardsDocument());
    }
    
    public boolean getHasNationalStandardsDocument() {
        return getTeacherResource().getNationalStandardsDocument() != null;
    }
    
    public String getNationalStandardsUrl() {
        return getImageUtils().getImageUrl(getTeacherResource().getNationalStandardsDocument());
    }
    
    public String getStateStandardsValue() {
    	return ((ViewTreehouse) getPage()).getPreparedText(getTeacherResource().getStateStandardsValue());
    }
    
    public String getNationalStandardsValue() {
    	String value = getTeacherResource().getNationalStandardsValue();
    	return ((ViewTreehouse) getPage()).getPreparedText(value);
    }    
    
    @SuppressWarnings("unchecked")
    private String getUlListStringWithSemicolons(List list, String liClass) {
        return getTextPreparer().getUlListStringWithSemicolons(list, liClass);
    }    
}
