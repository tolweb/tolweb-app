/*
 * Created on May 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.tolweb.hibernate.Document;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditDocumentData extends EditImageData {
    
    public Document getDocument() {
        return (Document) getImage();
    }
    
    public String getHelpPageName() {
        return "DocumentHelpMessagesPage";
    }
    
    public String getMediaType() {
        return ImageSearch.DOCUMENT;
    }
}
