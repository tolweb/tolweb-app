package org.tolweb.tapestry;

import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.main.Contributor;

public abstract class EditTextSection extends AbstractPageEditingPage {
    public abstract void setTextSection(MappedTextSection value);
    public abstract MappedTextSection getTextSection();
    
    /**
     * Overridden here since we don't actually need to save the page since the 
     * TextSections are first-class objects now.
     */
    protected void savePage() {
        getDAO().saveTextSection(getTextSection());        
    }    
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        Number textSectionId = (Number) parameters[3];
        setTextSectionId(Long.valueOf(textSectionId.intValue()));        
    }
    
    public void setTextSectionId(Long value) {
        MappedTextSection section = getDAO().getTextSectionWithId(value);
        setTextSection(section);
    }
    
    public Long getTextSectionId() {
        return getTextSection().getTextSectionId();
    }
    
    public IRender getDelegate() {
        Contributor contr = getContributor();
        if (contr == null || !contr.getDontUseEditor()) {
            return new ArticleNoteHTMLEditorDelegate(getNode().getNodeId());
        } else {
            return null;
        }
    }
}
