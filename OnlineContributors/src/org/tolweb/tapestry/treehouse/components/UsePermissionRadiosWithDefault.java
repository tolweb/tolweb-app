package org.tolweb.tapestry.treehouse.components;

import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.tapestry.UsePermissionComponent;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.UsePermissable;

@ComponentClass
public abstract class UsePermissionRadiosWithDefault extends UsePermissionComponent {
	@InjectState("image")
	public abstract NodeImage getImage();
	@Parameter(defaultValue = "'radiogroup'")
	public abstract String getUlClass();
	
	public UsePermissable getPermissableObject() {
		return getImage();
	}
	public String getOpenUseDivString() {
		return "<div id=\"morechoices1\" class=\"hide boxtocblue\">";
	}
	public String getCloseUseDivString() {
		return "</div>";
	}	
}
