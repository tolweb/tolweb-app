package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.hivemind.TapestryHelper;
import org.tolweb.misc.PagePropertySetter;
import org.tolweb.misc.ReorderHelper;
import org.tolweb.misc.UsePermissionHelper;
import org.tolweb.tapestry.StringPropertySelectionModelFactory;

/**
 * Place for other beans to live that don't have a well-defined home
 * @author dmandel
 */
public interface MiscInjectable {
	@InjectObject("spring:propertySelectionModelFactory")
	public StringPropertySelectionModelFactory getPropertySelectionFactory();
	@InjectObject("spring:reorderHelper")
	public ReorderHelper getReorderHelper();
	@InjectObject("spring:usePermissionHelper")
	public UsePermissionHelper getUsePermissionHelper();
	@InjectObject("spring:pagePropertySetter")
	public PagePropertySetter getPagePropertySetter();
	@InjectObject("service:org.tolweb.tapestry.TapestryHelper")
	public TapestryHelper getTapestryHelper();
}
