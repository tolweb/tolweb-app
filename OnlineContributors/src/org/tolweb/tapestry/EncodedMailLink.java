/*
 * This is where a license would go if I cared.
 */
package org.tolweb.tapestry;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel@tolweb.org  
 * Tapestry component that enccodes email addresses and emits javascript links
 * 
 */
public abstract class EncodedMailLink extends AbstractComponent implements PageInjectable, BaseInjectable {
    public abstract String getEmail();
    public abstract String getLinkText();
    
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        if (StringUtils.notEmpty(getEmail()) && StringUtils.notEmpty(getLinkText())) {
            String linkString = getTextPreparer().getEncodedMailLinkString(getEmail(), getLinkText());
        	writer.printRaw(linkString);
        } else if (StringUtils.notEmpty(getLinkText())) {
            writer.printRaw(getLinkText());
        }
    }
    

}
