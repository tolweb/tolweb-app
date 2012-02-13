/*
 * Created on Jun 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.TreehouseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceViewComponent extends BaseComponent implements TreehouseInjectable {
    public MappedAccessoryPage getTeacherResource() {
        return ((ViewTreehouse) getPage()).getTreehouse();
    }
}
