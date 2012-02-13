package org.tolweb.tapestry;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.request.IUploadFile;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ImageZipUpload extends AbstractContributorPage implements UserInjectable {
	public abstract IUploadFile getZipFile();
	public abstract IUploadFile getXmlFile();
	public abstract String getError();
	public abstract void setError(String value);
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();	
    
    @SuppressWarnings("unchecked")
    public void doUpload(IRequestCycle cycle) {
        IUploadFile zipFile = getZipFile();
        IUploadFile xmlFile = getXmlFile();
        String error = null;
        Document doc = null;
        if (zipFile == null || xmlFile == null || zipFile.getSize() == 0 || xmlFile.getSize() == 0) {
            error = "You must provide both a zip file containing your images and an xml file which annotates your images.";
        } else {
            try {
                doc = getDocumentFromUploadFile(xmlFile);
            } catch (JDOMException e) {
                error = "XML Processing Error: " + e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error = "Input Error: " + e.getMessage();
                e.printStackTrace();
            }
        }
        Contributor contr = getContributor();
        if (error == null) {
            Object[] results = getImageHelper().saveAndWriteOutZipFile(zipFile, doc, contr);
            ImageZipUploadResults resultsPage = (ImageZipUploadResults) cycle.getPage("ImageZipUploadResults");
            List images = (List) results[0];
            resultsPage.doActivate(cycle, images, (Set) results[1], (Set) results[2]);             
        } else {
            setError(error);
        }
    }
    
    protected Document getDocumentFromUploadFile(IUploadFile xmlFile) throws JDOMException, IOException {
    	InputStream fileStream = xmlFile.getStream();
    	int lastByteRead;
    	byte[] bytes = new byte[1000000];
    	int i = 0;
    	while ((lastByteRead = fileStream.read()) != -1) {
    		bytes[i++] = (byte) lastByteRead;
    	}
    	fileStream.close();
    	String xmlString = new String(bytes);
    	bytes = null;
    	xmlString = xmlString.trim();
    	xmlString = xmlString.replace((char)0, ' ');
        return new SAXBuilder().build(new StringReader(xmlString));
    }
}
