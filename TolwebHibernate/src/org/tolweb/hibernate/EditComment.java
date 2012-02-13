package org.tolweb.hibernate;

import java.util.Date;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.OrderedObject;

public class EditComment extends OrderedObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3958301995701485601L;
	private String comment;
    private Date commentDate;
    private int commentContributorId;
    private Contributor commentContributor;
    /**
     * @hibernate.property
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }
    /**
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    /**
     * @hibernate.property
     * @return Returns the commentContributorId.
     */
    public int getCommentContributorId() {
        return commentContributorId;
    }
    /**
     * @param commentContributorId The commentContributorId to set.
     */
    public void setCommentContributorId(int commentContributorId) {
        this.commentContributorId = commentContributorId;
    }
    /**
     * @return Returns the commentContributor.
     */
    public Contributor getCommentContributor() {
        return commentContributor;
    }
    /**
     * @param commentContributor The commentContributor to set.
     */
    public void setCommentContributor(Contributor commentContributor) {
        this.commentContributor = commentContributor;
        setCommentContributorId(commentContributor.getId());
    }
    /**
     * @hibernate.property
     * @return Returns the commentDate.
     */
    public Date getCommentDate() {
        return commentDate;
    }
    /**
     * @param commentDate The commentDate to set.
     */
    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }
    /**
     * @hibernate.property column="page_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
    public boolean equals(Object o) {
        return super.doEquals(o);
    }
    public int hashCode() {
        return super.getHashCode();
    }
}
