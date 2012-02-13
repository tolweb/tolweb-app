package org.tolweb.tapestry;

import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class TextSectionAutoSave extends AutoSave implements PageInjectable {
    protected Object getObjectWithId(Long id) {
        return getWorkingPageDAO().getTextSectionWithId(id);
    }    
    protected void saveObject(Object toSave) {
        getWorkingPageDAO().saveTextSection((MappedTextSection) toSave);
    }
}
