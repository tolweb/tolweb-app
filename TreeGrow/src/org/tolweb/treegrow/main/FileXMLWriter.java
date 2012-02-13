/*
 * FileXMLWriter.java
 *
 * Created on June 24, 2003, 9:32 AM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import org.jdom.*;
import org.jdom.output.*;

import org.tolweb.treegrow.tree.*;

/**
 * XML writer subclass that writes it's contents out to a file
 */
public class FileXMLWriter extends TreeGrowXMLWriter {
    private String fileName;
    
    public FileXMLWriter() {
        super();
    }
    
    public FileXMLWriter(String file) {
        super();
        fileName = file;
    }
    
    public FileXMLWriter(Tree t) {
        this(t, null);
    }
    
    public FileXMLWriter(Tree t, String file) {
        super(t);
        fileName = file;
    }
    
    /** Writes the given document to either a file or a URL
     *
     * @param outDuc The doc to write
     * @return true If the write was successful, false otherwise.
     *
     */
    protected boolean outputDocument(Document outDoc, Node root) {
        if (fileName == null) {
            fileName = buildOutputFileName();
        }
        String tempFilename = fileName + ".tmp";
        try {
            //write out to a temp file, in case there's a glitch (power failure, crash, etc)
            FileOutputStream out = new FileOutputStream(tempFilename);
            XMLOutputter serializer = getXMLOutputter();
            serializer.output(outDoc, out);
            out.flush();
            out.close();
            
            //move original to bkup, new to original, then delete bkup
            File original = new File(fileName);
            File backup = new File(fileName+".bkup");
            File newfile = new File(tempFilename);
            original.renameTo(backup);
            newfile.renameTo(original);
            backup.delete();
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    /** 
     * Used to indicate whether a subclass of XMLWriter is in file writing mode,
     * in which case more information is written.
     *
     * @return true
     *
     */
    protected boolean saveToFile() {
        return true;
    }
    
    /**
     * Builds the path string (minus the extension) to where the writer will 
     * write the file to
     *
     * @return the path string (minus the extension) to where the writer will 
     *         write the file to
     */
    protected String buildOutputFileBaseName() {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        return  fileManager.getDataPath() + "/" + controller.getFileName() + "/" + controller.getFileName();
    }
    
    /**
     * Builds the path string (with the extension) to where the writer will 
     * write the file to
     *
     * @return the path string (with the extension) to where the writer will 
     *         write the file to
     */
    public String buildOutputFileName() {
        return  buildOutputFileBaseName() + ".xml";
    }
}
