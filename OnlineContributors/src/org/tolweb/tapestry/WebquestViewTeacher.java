/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WebquestViewTeacher extends ViewWebquest {
    public boolean getHasTips() {
        return StringUtils.notEmpty(getWebquest().getTeacherTips());
    }
}