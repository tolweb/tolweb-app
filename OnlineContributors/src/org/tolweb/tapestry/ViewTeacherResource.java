/*
 * Created on Jul 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.tolweb.hibernate.TeacherResource;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ViewTeacherResource extends ViewTreehouse {
    public TeacherResource getTeacherResource() {
        return (TeacherResource) getTreehouse();
    }
}
