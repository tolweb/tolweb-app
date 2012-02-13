package org.tolweb.misc;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.tapestry.contrib.link.PopupLinkRenderer;

public class RendererFactory {
    private PopupLinkRenderer editLinkRenderer;
    private PopupLinkRenderer helpRenderer;
    private PopupLinkRenderer branchLeafEditRenderer;
    private PopupLinkRenderer viewPageRenderer;
    private PopupLinkRenderer previewLinkRenderer;
    private PopupLinkRenderer imagesSearchRenderer;
    private PopupLinkRenderer managerRenderer;
    private PopupLinkRenderer imageContributorRenderer;
	public static final String EDIT_TREEHOUSE_WINDOW_NAME = "editTreehouse";
    
    
    public RendererFactory() {
        try {
            FileOutputStream errorFile = new FileOutputStream("/tmp/ToLErrors.log");
            System.setErr(new PrintStream(errorFile));
        } catch (Exception e) {
            e.printStackTrace();
        }

        branchLeafEditRenderer = getLinkRenderer("editBranchLeaf", 900, 700);
        viewPageRenderer = getLinkRenderer("viewPage", 900, 700, "scrollbars=yes, resizable=yes, menubar=yes, titlebar=yes, toolbar=yes, status=yes, location=yes");
        editLinkRenderer = getLinkRenderer(EDIT_TREEHOUSE_WINDOW_NAME, 800, 500);
        helpRenderer = getLinkRenderer("fullHelpWindow", 800, 500);
        previewLinkRenderer = getLinkRenderer("previewWindow", 800, 500);
        imagesSearchRenderer = getLinkRenderer("imagesSearchWindow", 800, 500);
        managerRenderer = getLinkRendererNoFeatures("managerWindow");
        imageContributorRenderer = getLinkRenderer("imageContributorWindow", 800, 500, "scrollbars=yes, resizable=yes, menubar=yes, titlebar=yes, toolbar=yes, status=yes, location=yes");
    }

    public PopupLinkRenderer getLinkRenderer(String windowName, int width, int height) {
        return constructRenderer(windowName, true, width, height);
    }
    public PopupLinkRenderer getLinkRenderer(String windowName, int width, int height, String features) {
    	return constructRenderer(windowName, true, width, height, features);
    }
    public PopupLinkRenderer getLinkRendererNoFeatures(String windowName) {
    	return constructRenderer(windowName, false, -1, -1);
    }
    private PopupLinkRenderer constructRenderer(String windowName, boolean addFeatures, int width, int height) {
    	return constructRenderer(windowName, addFeatures, width, height, "scrollbars=yes, resizable=yes");
    }
    private PopupLinkRenderer constructRenderer(String windowName, boolean addFeatures, int width, int height, String features) {
        PopupLinkRenderer renderer = new PopupLinkRenderer();
        renderer.setWindowName(windowName);
        if (addFeatures) {
        	renderer.setFeatures("width=" + width + ", height=" + height + ", " + features);
        }
        return renderer;    	
    }

    /**
     * @return Returns the editLinkRenderer.
     */
    public PopupLinkRenderer getEditLinkRenderer() {
        return editLinkRenderer;
    }
    /**
     * @return Returns the branchLeafEditRenderer.
     */
    public PopupLinkRenderer getBranchLeafEditRenderer() {
        return branchLeafEditRenderer;
    }
    /**
     * @return Returns the viewPageRenderer.
     */
    public PopupLinkRenderer getViewPageRenderer() {
        return viewPageRenderer;
    }
    /**
     * @param viewPageRenderer The viewPageRenderer to set.
     */
    public void setViewPageRenderer(PopupLinkRenderer viewPageRenderer) {
        this.viewPageRenderer = viewPageRenderer;
    }

	public PopupLinkRenderer getPreviewLinkRenderer() {
		return previewLinkRenderer;
	}
	public void setPreviewLinkRenderer(PopupLinkRenderer previewLinkRenderer) {
		this.previewLinkRenderer = previewLinkRenderer;
	}
    /**
     * @return Returns the helpRenderer.
     */
    public PopupLinkRenderer getHelpRenderer() {
        return helpRenderer;
    }
    /**
     * @param helpRenderer The helpRenderer to set.
     */
    public void setHelpRenderer(PopupLinkRenderer helpRenderer) {
        this.helpRenderer = helpRenderer;
    }

	public PopupLinkRenderer getImagesSearchRenderer() {
		return imagesSearchRenderer;
	}

	public void setImagesSearchRenderer(PopupLinkRenderer imagesSearchRenderer) {
		this.imagesSearchRenderer = imagesSearchRenderer;
	}

	public PopupLinkRenderer getManagerRenderer() {
		return managerRenderer;
	}

	public void setManagerRenderer(PopupLinkRenderer managerRenderer) {
		this.managerRenderer = managerRenderer;
	}

	public PopupLinkRenderer getImageContributorRenderer() {
		return imageContributorRenderer;
	}

	public void setImageContributorRenderer(
			PopupLinkRenderer imageContributorRenderer) {
		this.imageContributorRenderer = imageContributorRenderer;
	}	
}
