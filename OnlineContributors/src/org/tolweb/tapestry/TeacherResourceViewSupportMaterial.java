/*
 * Created on Jul 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Arrays;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.SupportMaterial;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceViewSupportMaterial extends ViewTeacherResource {
    public abstract void setSupportMaterial(SupportMaterial material);
    public abstract SupportMaterial getSupportMaterial();
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        setIsFromPreview(false);
        // Get the index of the support material and find it on the parent
        Integer index = (Integer) parameters[1];
        SupportMaterial supportMat = (SupportMaterial) Arrays.asList(getTeacherResource().getSupportMaterials().toArray()).get(index.intValue());
        setSupportMaterial(supportMat);
    }
    
    public String getSupportMaterialText() {
        return getPreparedText(getSupportMaterial().getText());
    }
}
