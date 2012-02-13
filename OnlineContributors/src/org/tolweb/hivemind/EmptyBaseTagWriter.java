package org.tolweb.hivemind;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;

public class EmptyBaseTagWriter implements IRender {
    /**
     * This prevents rendering of the any base tag.
     */
    public void render(IMarkupWriter writer, IRequestCycle cycle) {
        IPage page = cycle.getPage();
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        if(page.getNamespace().getId() == null) {
            String name = page.getPageName();
            int slashx = name.lastIndexOf('/');
            if(slashx > 0)
                sb.append(name.substring(0, slashx + 1));
        }
    }
}
