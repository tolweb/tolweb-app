package org.tolweb.tapestry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a page as not needing molecular permissions
 * to view it -- by default all project pages require this permission
 * @author dmandel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MolecularPermissionNotRequired {

}
