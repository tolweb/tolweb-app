package org.tolweb.tapestry.xml;

import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.AbstractScientificEditorPage;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class DownloadUploadXML extends AbstractScientificEditorPage implements 
		NodeInjectable, MiscInjectable, UserInjectable, TreeGrowServerInjectable {
	public static final int FLAT_STRUCTURE = 0;
	public static final int TREE_STRUCTURE = 1;
	
	public abstract void setRootGroupSelected(boolean value);
	@Persist("session")
	public abstract boolean getRootGroupSelected();
	public abstract void setRootGroup(MappedNode node);
	@Persist("session")
	public abstract MappedNode getRootGroup();
	public abstract String getRootGroupName();
	public abstract void setRootGroupName(String value);
	@Bean
	public abstract ValidationDelegate getDelegate();
	public abstract int getTreeStructureTypeSelection();
    @InjectObject("engine-service:xmldownload")
    public abstract IEngineService getXMLDownloadService();
    public abstract void setUploadSuccessful(boolean value);
    public abstract boolean getUploadSuccessful();
    public abstract void setUploadErrorString(String value);
    public abstract String getUploadErrorString();
    public abstract IUploadFile getUploadFile();
    public abstract void setUploadFile(IUploadFile file);
	
	public void chooseRootGroup() {
		setRootGroup(null);
		setRootGroupSelected(true);
	}
	public void findAndSelectRootGroup() {
		MappedNode rootNode = getWorkingNodeDAO().getFirstNodeExactlyNamed(getRootGroupName());
		if (rootNode != null) {
			setRootGroupSelected(false);
			setRootGroup(rootNode);
		} else {
			getDelegate().record("No node named " + getRootGroupName(), ValidationConstraint.CONSISTENCY);
		}
	}
	public void downloadData() {
		String redirectUrl = getXMLDownloadService().getLink(false,
				new Object[] { getRootGroup().getNodeId().toString() }).getURL();
		System.out.println("redirect url is: " + redirectUrl);
		throw new RedirectException(redirectUrl);
	}
	public void uploadNodes() {
		String uploadString = getTapestryHelper().getStringFromUploadFile(getUploadFile());
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		try {
			getUploadBuilder().buildUpload(uploadString, contr, false, true);
			setUploadSuccessful(true);
			setRootGroup(null);
			setRootGroupSelected(false);
		} catch (Exception e) {
			setUploadErrorString(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
