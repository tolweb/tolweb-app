package org.tolweb.btol.tapestry;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class AbstractMassUploadPage extends ProjectPage implements MiscInjectable,
		PageBeginRenderListener {
	protected static final int UPLOAD_FILE = 0;
	protected static final int USE_TEXT = 1;
	@Bean
	public abstract ValidationDelegate getDelegate();	
	public abstract IUploadFile getUploadFile();
	public abstract String getTypedText();
	public abstract void setTypedText(String value);
	public abstract int getUploadOption();
	public abstract void setUploadOption(int value);
	public abstract void setErrorMessage(String value);
	public abstract String getErrorMessage();
	
	public boolean getHasError() {
		return StringUtils.notEmpty(getErrorMessage());
	}
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			if (StringUtils.notEmpty(getTypedText())) {
				setUploadOption(USE_TEXT);
			}
		}
	}		
	public int getUploadFileOption() {
		return UPLOAD_FILE;
	}
	public int getUseTextOption() {
		return USE_TEXT;
	}
	protected String getUploadString() {
		String uploadString;
		if (getUploadOption() == UPLOAD_FILE) {
			uploadString = getTapestryHelper().getStringFromUploadFile(getUploadFile());
		} else {
			uploadString = getTypedText();
		}		
		return uploadString;
	}
}
