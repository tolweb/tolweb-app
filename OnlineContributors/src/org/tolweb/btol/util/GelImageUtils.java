package org.tolweb.btol.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.request.IUploadFile;
import org.tolweb.btol.PCRBatch;
import org.tolweb.misc.ImageUtilsImpl;
import org.tolweb.treegrow.main.StringUtils;

public class GelImageUtils extends ImageUtilsImpl {
	/**
	 * url to the gel img directory on the server e.g. 
	 * http://btol.tolweb.org/gelimages/
	 */
	private String gelImageUrlPrefix;
	private String gelImageDir;
	private String sequenceUrlPrefix;
	private String sequenceDir;
	
	/**
	 * overridden so that anything that does directory operations will
	 * operate on the correct gel images directory
	 */
	public String getImagesDirectory() {
		return getGelImageDir();
	}
	public String getPCRBatchGelImage1Url(PCRBatch batch) {
		return getGelImageUrlFromFilename(batch.getImageFilename1());
	}
	public String getPCRBatchGelImage2Url(PCRBatch batch) {
		return getGelImageUrlFromFilename(batch.getImageFilename2());
	}
	private String getGelImageUrlFromFilename(String filename) {
		if (StringUtils.notEmpty(filename)) {
			return getGelImageUrlPrefix() + filename;
		} else {
			return null;
		}
	}
	public boolean getGelImageFileExists(String filename) {
		File file = new File(getGelImageDir(), filename);
		return file.exists();
	}
	public String writeGelImageFileToDisk(IUploadFile imageFile) {
		String filename = writeImageFileToDisk(imageFile, getGelImageDir());
		return stripSlashesFromFilename(filename);
	}
	public String writeSequenceFileToDisk(IUploadFile file, String filename) {
		InputStream stream = file.getStream();
        filename = StringUtils.cleanStringForFilename(filename);
        // Set up a unique filename for this image on the server
        filename = getUniqueServerFilename(getSequenceDir(), filename);
        // then write out the contents to the specified directory
		String sequenceFile = stripSlashesFromFilename(writeOutStream(stream, filename));
		return sequenceFile;
	}
	public String getGelImageUrlPrefix() {
		return gelImageUrlPrefix;
	}
	public void setGelImageUrlPrefix(String gelImagePrefix) {
		this.gelImageUrlPrefix = gelImagePrefix;
	}
	public String getGelImageDir() {
		return gelImageDir;
	}
	public void setGelImageDir(String getImageDir) {
		this.gelImageDir = getImageDir;
	}
	public String getSequenceDir() {
		return sequenceDir;
	}
	public void setSequenceDir(String sequenceDir) {
		this.sequenceDir = sequenceDir;
	}
	public String getSequenceUrlPrefix() {
		return sequenceUrlPrefix;
	}
	public void setSequenceUrlPrefix(String sequenceUrlPrefix) {
		this.sequenceUrlPrefix = sequenceUrlPrefix;
	}
	@SuppressWarnings("unchecked")
	public List<File> getChromatogramFileListFromFilenames(Collection<String> filenames) {
		String chromatDir = getSequenceDir();
		if (!chromatDir.endsWith("/")) {
			chromatDir += "/";
		}
		List<File> returnList = new ArrayList<File>();
		for (Iterator iter = filenames.iterator(); iter.hasNext();) {
			String nextFilename = (String) iter.next();
			returnList.add(new File(chromatDir + nextFilename));
		}
		return returnList;
	}
	
}
