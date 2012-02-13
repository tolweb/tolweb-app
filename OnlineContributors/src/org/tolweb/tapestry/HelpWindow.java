package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.asset.ExternalAsset;

public abstract class HelpWindow extends BaseComponent {
	private ExternalAsset stylesheet;
	private ExternalAsset trhsStylesheet;
	
	public abstract boolean getIsImages();
			
	public IAsset getStylesheetToUse() {
		if (getIsImages()) {
			return getStylesheet();
		} else {
			return getTrhsStylesheet();
		}
	}
	
	public ExternalAsset getStylesheet() {
		if (stylesheet == null) {
			stylesheet = new ExternalAsset("/tree/css/tolforms.css", null);
		}
		return stylesheet;
	}
	public void setStylesheet(ExternalAsset stylesheet) {
		this.stylesheet = stylesheet;
	}
	public ExternalAsset getTrhsStylesheet() {
		if (trhsStylesheet == null) {
			trhsStylesheet = new ExternalAsset("/tree/css/trhsform.css", null);
		}
		return trhsStylesheet;
	}
	public void setTrhsStylesheet(ExternalAsset trhsStylesheet) {
		this.trhsStylesheet = trhsStylesheet;
	}
}
