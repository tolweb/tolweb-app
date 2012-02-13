package org.tolweb.tapestry;

import java.util.Date;

import org.tolweb.hibernate.EditComment;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ReviseTreehousePage extends AbstractTreehousePublicationPage implements UserInjectable {
    public abstract boolean getPersonalInfoSelected();
    public abstract boolean getImageCopyrightSelected();    
    public abstract boolean getGrammarSelected();
    
    protected EditComment contructEditComment() {
        Contributor contr = getContributor();
        EditComment comment = new EditComment();
        String commentsString = "Thank you for your submission, but we need you to work some more on the treehouse before it can be published.";
        if (getPersonalInfoSelected() || getImageCopyrightSelected() || getGrammarSelected()) {
            String checkedAreas = "";
            commentsString += "Please correct the following areas:<ul>";
            if (getPersonalInfoSelected()) {
                commentsString += "<li>Personal information is currently displayed</li>";
                checkedAreas += "Personal information\n";
            }
            if (getImageCopyrightSelected()) {
                commentsString += "<li>Media copyright information is not correct</li>";
                checkedAreas += "Media copyright information\n";
            }
            if (getGrammarSelected()) {
                commentsString += "<li>Formatting/Spelling/Grammar</li>";
                checkedAreas += "Formatting/Spelling/Grammar\n";
            }
            commentsString += "</ul>";
            setCheckedAreas(checkedAreas);
        }
        if (StringUtils.notEmpty(getComments())) {
            if (contr.getIsLearningEditor()) {
                commentsString += "The treehouse editor had the following comments regarding your submission: ";
            } else {
                commentsString += "Your teacher had the following comments regarding your submission: ";
            }
            commentsString += "<p>" + getComments() + "</p>";
        }
        comment.setComment(commentsString);
        comment.setCommentContributor(contr);
        comment.setCommentDate(new Date());        
        return comment;
    }
    
    public boolean getIsTeacherApproval() {
        return false;
    }
    
    public Boolean getIsApprove() {
        return false;
    }        
}
