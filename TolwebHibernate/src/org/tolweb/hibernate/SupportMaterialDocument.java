/*
 * Created on May 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SupportMaterialDocument extends AbstractSupportMaterial implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9106371291026254627L;
	private Long documentId;
    private Document document;
    /**
     * @hibernate.property column="document_id"
     * @return Returns the documentId.
     */
    public Long getDocumentId() {
        return documentId;
    }
    /**
     * @param documentId The documentId to set.
     */
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
    public void setDocument(Document doc) {
        this.document = doc;
        if (doc != null) {
            setDocumentId(Long.valueOf(doc.getId()));
        } else {
            setDocumentId(null);
        }
    }
    public Document getDocument() {
        return document;
    }
}
