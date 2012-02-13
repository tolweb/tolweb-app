package org.tolweb.hivemind;

import java.io.InputStream;

import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.IUploadFile;
import org.jdom.Document;
import org.tolweb.tapestry.IImageCallback;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 * Interface for doing common image database
 * and file manipulations.  Also has helper methods to 
 * determine various edit form values based on request
 * parameters, cookies, etc.
 * @author  dmandel
 */
public interface ImageHelper {
	public IImageCallback getEditCallback();
	public IImageCallback getDeleteCallback(final Long editedObjectId, final String searchPageName);
	public IImageCallback getCopyDataCallback();
	public IImageCallback getTillusCallback(final Long editedObjectId, final String pageName);
	public void deleteImage(NodeImage img);
	public void writeOutImage(NodeImage img, IUploadFile file);
	public void saveAndWriteOutImage(NodeImage img, IUploadFile file, Long nodeId);
	public void saveAndWriteOutImageStream(NodeImage img, InputStream stream, String filename, Long nodeId);
	public void saveAndWriteOutImageStream(NodeImage img, InputStream stream, String filename, Long nodeId, Contributor contr);
	public void writeOutImageVersion(ImageVersion version, IUploadFile file);
	public Object[] saveAndWriteOutZipFile(IUploadFile file, Document doc, Contributor contr);
	public IRender getRedirectDelegate(final String url);
	public String getEditUrlForMedia(NodeImage media, IRequestCycle cycle, Contributor contr);
	public String getEditUrlForMedia(NodeImage media, IRequestCycle cycle, Contributor contr, boolean someBool);
	public boolean getContributorShouldUseSimpleMedia();
	public boolean getContributorShouldUseSimpleMedia(Contributor contr);
}
