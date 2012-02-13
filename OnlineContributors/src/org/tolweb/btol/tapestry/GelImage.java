package org.tolweb.btol.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class GelImage extends BaseComponent implements PCRInjectable {
	@Parameter(required = true)
	public abstract PCRBatch getBatch();
	@Parameter(required = false, defaultValue = "false")
	public abstract boolean getUseSecondImage();
	
	public boolean getShowImg() {
		if (StringUtils.notEmpty(getImgUrl())) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getImgUrl() {
		if (getBatch() != null) {
			if (getUseSecondImage()) {
				return getGelImageUtils().getPCRBatchGelImage2Url(getBatch());
			} else {
				return getGelImageUtils().getPCRBatchGelImage1Url(getBatch()); 
			}
		} else {
			return null;
		}
	}
}
