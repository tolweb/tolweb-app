package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ImageDAO;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ImageZipUploadResults extends BasePage implements ImageInjectable {
	@SuppressWarnings("unchecked")
    public abstract void setImages(List value);
    @SuppressWarnings("unchecked")
    public abstract List getImages();
    @SuppressWarnings("unchecked")
    public abstract void setZipEntriesNoXML(Set value);
    @SuppressWarnings("unchecked")
    public abstract Set getZipEntriesNoXML();
    @SuppressWarnings("unchecked")
    public abstract void setXmlEntriesNoZip(Set value);
    @SuppressWarnings("unchecked")
    public abstract Set getXmlEntriesNoZip();

    @SuppressWarnings("unchecked")
    public void doActivate(IRequestCycle cycle, List images, Set zipEntries, Set xmlEntries) {
        setImages(images);
        setZipEntriesNoXML(zipEntries);
        setXmlEntriesNoZip(xmlEntries);
        cycle.activate(this);
    }

    public void viewMissingEntries(IRequestCycle cycle) {
        IPage missingEntriesPage = cycle.getPage("ImageZipUploadMissingEntries");
        PropertyUtils.write(missingEntriesPage, "zipEntriesNoXML", getZipEntriesNoXML());
        PropertyUtils.write(missingEntriesPage, "xmlEntriesNoZip", getXmlEntriesNoZip());
        cycle.activate(missingEntriesPage);
    }
    
    @SuppressWarnings("unchecked")
    public void deleteAll(IRequestCycle cycle) {
        ImageDAO dao = getImageDAO();
        for (Iterator iter = getImages().iterator(); iter.hasNext();) {
            NodeImage image = (NodeImage) iter.next();
            dao.deleteImage(image);
        }
        cycle.activate("ImagesManager");
    }
}
