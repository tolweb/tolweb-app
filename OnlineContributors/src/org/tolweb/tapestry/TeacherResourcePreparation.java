/*
 * Created on Jun 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.tolweb.hibernate.TeacherResource;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourcePreparation extends
        TeacherResourceViewComponent implements BaseInjectable {
    public String getPhysicalMaterialsString() {
        TextPreparer preparer = getTextPreparer();
        return preparer.getUlListString(preparer.getNewlineSeparatedList(((TeacherResource) getTeacherResource()).getPhysicalMaterials()), null, false);
    }
}
