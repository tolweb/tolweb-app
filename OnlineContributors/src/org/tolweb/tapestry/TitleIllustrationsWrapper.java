/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TitleIllustrationsWrapper extends BaseComponent {
    public IRender getDelegate() {
        return new IRender() {
            public void render(IMarkupWriter writer, IRequestCycle cycle) {
                writer.printRaw("<style type=\"text/css\" media=\"all\">@import url(/tree/css/tolforms.css);</style>");
            }
        };
    }
}
