package org.tolweb.tapestry;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;

public class BaseHTMLEditorDelegate implements IRender {
    public void render(IMarkupWriter writer, IRequestCycle cycle) {
        writer.printRaw("\n<script language=\"javascript\" type=\"text/javascript\" src=\"/tree/js/tinymce/jscripts/tiny_mce/tiny_mce.js\"></script>\n");
        writer.printRaw("<script language=\"javascript\" type=\"text/javascript\">\ntinyMCE.init({");
        doConfiguration(writer);
        writer.printRaw("</script>");
        // any page that has the html editor should NOT be indexed by search engines
        writer.printRaw("\n<meta name=\"robots\" content=\"noindex,nofollow\"/>\n");
    }
    
    protected void doConfiguration(IMarkupWriter writer) {
        writer.printRaw("mode : \"specific_textareas\",\n");
        writer.printRaw("theme_advanced_toolbar_location: \"top\",\n");        
        writer.printRaw("theme : \"default\"})\n");        
    }
}
