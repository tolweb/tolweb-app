package org.tolweb.tapestry;

import java.util.Date;

import org.apache.tapestry.annotations.InitialValue;
import org.tolweb.hibernate.EditComment;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ApproveTreehousePage extends AbstractTreehousePublicationPage implements 
		UserInjectable, BaseInjectable {
	@InitialValue("true")
	public abstract boolean getIsTeacherApproval();
    public abstract void setIsTeacherApproval(boolean value);	
    
    protected EditComment contructEditComment() {
        Contributor contr = getContributor();
        EditComment comment = new EditComment();
        String commentsString = "";
        if (getIsTeacherApproval()) {
	        commentsString = "Treehouse submitted for publication";
	        if (StringUtils.notEmpty(getComments())) {
	            commentsString += " with comments: <p>" + getComments() + "</p>";
	        }
        } else {
        	commentsString = "<p class=\"trhspublished\"><strong>Published on the Tree of Life! <br />";  
        	String publicUrl = getUrlBuilder().getURLForObject(getTreehouseToApproveOrReject());
      	  	commentsString += "View published treehouse:</strong> <a href=\"javascript: w = window.open('" + publicUrl + "', 'viewPage', 'width=900, height=700, scrollbars=yes, resizable=yes'); w.focus();\"";
      	  	commentsString += "class=\"underline\">" + getTreehouseToApproveOrReject().getPageTitle() + "</a></p>";
      	  	commentsString += "<p>&nbsp; </p>";
        }
        comment.setComment(commentsString);
        comment.setCommentContributor(contr);
        comment.setCommentDate(new Date());
        return comment;
    }
    
    public Boolean getIsApprove() {
        return true;
    }    
}
