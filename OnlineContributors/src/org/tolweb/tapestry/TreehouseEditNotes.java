/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.EditComment;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditNotes extends AbstractTreehouseEditingPage implements IExternalPage, UserInjectable, TreehouseInjectable, 
		AccessoryInjectable, MiscInjectable {
    public static final String PROGRESS_PROPERTY = "notesProgress";
    private HTMLEditorDelegate commentsEditorDelegate = new HTMLEditorDelegate(false);
    
    public abstract EditComment getCurrentComment();
    public abstract String getNewComment();
    public abstract void setNewComment(String value);
    public abstract void setSubmittedComment(String value);
    public abstract boolean getEmailTeacher();
    public abstract boolean getEmailEditor();
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters != null && parameters.length > 0) {
            Number treehouseId = (Number) parameters[0];
            MappedAccessoryPage treehouse = getWorkingAccessoryPageDAO().getAccessoryPageWithId(treehouseId.intValue());
            setTreehouse(treehouse);
        }
    }
    
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}
	
	public int getStepNumber() {
	    if (getIsWebquest()) {
	        return 10;
	    } else if (getIsTeacherResource() || getIsPortfolio()) {
	        if (getIsOther() || getIsPortfolio()) {
	            return 8;
	        } else {
	            return 9;
	        }
	    } else {
	        return 7;
	    }
	}
    
    public HTMLEditorDelegate getCommentsEditorDelegate() {
        return commentsEditorDelegate;
    }
    
    public String getCommentAuthorDisplayName() {
        Contributor currentContributor = getContributor();
        Contributor contr = getCurrentComment().getCommentContributor();
        boolean isTeacher = Student.class.isInstance(currentContributor) && 
            ((Student) currentContributor).getTeacher().getId() == contr.getId();
        if (contr.getIsLearningEditor()) {
            return "ToL Learning Materials Editor";
        } else if (isTeacher) {
            return "Teacher: " + contr.getLastName();
        } else {
            return contr.getDisplayName();
        }
    }
    
    @SuppressWarnings("unchecked")
    public Collection getComments() {
        ArrayList comments = new ArrayList(getTreehouse().getEditComments());
        // reverse the sort order since they show up from oldest to newest
        Collections.reverse(comments);
        return comments;
    }
    
    public void submitComments(IRequestCycle cycle) {
        EditComment comment = new EditComment();
        comment.setComment(getNewComment());
        comment.setCommentContributor(getContributor());
        comment.setCommentDate(new Date());
        getReorderHelper().addToSet(getTreehouse().getEditComments(), comment);
        doSave();
        if (getEmailEditor() || getEmailTeacher()) {
            setSubmittedComment(getNewComment());
        }
        setSelectedAnchor("commquest");
        setNewComment(null);
    }
    
    @SuppressWarnings("unchecked")
    public List getEmailAddresses() {
        Contributor commentContributor = getContributor();
        List emails = new ArrayList();
        if (getEmailTeacher()) {
            emails.add(((Student) commentContributor).getTeacher().getEmail());
        }
        if (getEmailEditor()) {
            emails.add(getConfiguration().getLearningEditorEmail());
        }
        return emails;
    }
}
