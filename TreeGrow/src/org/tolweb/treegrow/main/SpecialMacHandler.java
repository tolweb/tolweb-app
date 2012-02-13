package org.tolweb.treegrow.main;

import com.apple.mrj.*;

/**
 * Class used to handle special Macintosh menu items
 */
public class SpecialMacHandler
	implements MRJQuitHandler, MRJPrefsHandler, MRJAboutHandler {
	
	public SpecialMacHandler() {
		System.setProperty("com.apple.macos.useScreenMenubar", "true");
	
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TreeGrow");
		MRJApplicationUtils.registerAboutHandler(this);
		MRJApplicationUtils.registerPrefsHandler(this);
		MRJApplicationUtils.registerQuitHandler(this);
	}

	public void handleAbout() {
	    Controller.getController().showAboutDialog();
	}

	public void handlePrefs() {
	}
	
	public void handleQuit() {

		CloseDialog dialog = new CloseDialog();
		int success = dialog.saveConfirm();

		if(success < 1)
			throw new IllegalStateException("Not ready to quit, thanks");

	}
}

