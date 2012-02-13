/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

/**
 * @author dmandel
 *
 * Interface used to set the edited object properly before
 * rendering the page -- for object editors (images, title illustrations, articles/notes)
 */
public interface EditIdPage {
    public void setEditedObjectId(Long id);
    public Long getEditedObjectId();
}
