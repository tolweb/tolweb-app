/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;


/**
 * Class used to store metadata about files.
 */
public class FileMetadata {
    String fileName, filePath, uploadDate, modifiedDate, downloadDate, rootNodeName, editingBatchId;
    int downloadId, rootNodeId;
    boolean changedFromServer = false;

    public FileMetadata(String nodeName, String file, String path, String upload, String modified, String download, String batchId, int id, int root, boolean changed) {
        rootNodeName = nodeName;
        fileName = file;
        filePath = path;
        uploadDate = upload;
        modifiedDate = modified;
        downloadDate = download;
        editingBatchId = batchId;
        downloadId = id;
        rootNodeId = root;
        changedFromServer = changed;
    }
    
    public FileMetadata(String rootNodeName, String file, String path, String upload, String modified, String download, String batchId, int id) {
        this(rootNodeName, file, path,  upload, modified, download, batchId, id, -1, false);       
    }
    
    public FileMetadata(String file, String path, String upload, String modified, String download, int id) {
        this(file, file, path, upload, modified, download, null, id);
    }
    
    public boolean isLocal() {
        return filePath != null;
    }
}