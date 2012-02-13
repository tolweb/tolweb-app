/*
 * Created on Jun 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IMarkupWriter;
import org.tolweb.misc.TextPreparer;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HTMLEditorDelegate extends BaseHTMLEditorDelegate {
    private boolean includeMediaControls;
    private Long treehouseId;
    
    public HTMLEditorDelegate() {
        this(true);
    }
    
    public HTMLEditorDelegate(boolean includeMediaControls) {
    	this(includeMediaControls, Long.valueOf(0));
    }
    
    public HTMLEditorDelegate(boolean includeMediaControls, Long treehouseId) {
        this.includeMediaControls = includeMediaControls;
        this.treehouseId = treehouseId;
    }
    
    protected void doConfiguration(IMarkupWriter writer) {  
        writer.printRaw("mode : \"specific_textareas\",\n");
        writer.printRaw("theme: \"advanced\",\n");
        writer.printRaw("plugins : \"tol, table, searchreplace, preview, spellchecker\",\n");
        writer.printRaw("content_css: \"/tree/css/tinymce.css\",\n");
        writer.printRaw("theme_advanced_layout_manager: \"RowLayout\",\n");
        writer.printRaw(getContainersLine());
        writer.printRaw("theme_advanced_containers_default_class: \"mceToolbar\",\n");
        writer.printRaw("theme_advanced_containers_default_align: \"center\",\n");
        //writer.printRaw("theme_advanced_container_top1: \"paragraphstyles, bold, italic, underline, separator, justifyleft, justifycenter, justifyright, justifyfull, separator, sub, sup, separator, charmap, hr, separator, link, unlink, anchor, textstyles\",\n");
        writer.printRaw(getTop1Line());
        //writer.printRaw("theme_advanced_container_top2 : \"bullist, numlist, outdent, indent, liststyles, separator, tablecontrols, tablestyles\",\n");
        writer.printRaw(getTop2Line());
        String imageString = includeMediaControls ? "insertimage, imagesize," : "";
        String mediaString = includeMediaControls ? "media" : "";
        writer.printRaw("theme_advanced_container_top3: \"" + imageString + " separator, noborder, blackborder, grayborder, separator, alignleft, aligncenter, alignright, floatleft, floatright, separator, inlinebottom, inlinemiddle, inlinetop, separator," + mediaString + "\",\n");
        writer.printRaw("theme_advanced_container_top4 : \"undo, redo, separator, tolvisualaid, unformat, removeformat, code, help, delete, replace, spellchecker, search\",\n");
        writer.printRaw("visual: \"false\",\n");
        writer.printRaw("valid_elements: \"+a[name|href|target|title|class|style" + TextPreparer.ESCAPE_MAILTO + "],strong/b[class|style],em/i[class|style],strike[class|style],u[class|style],p[dir|class|align|style],ol[class|style],ul[class|style],li[class|style],img[class|src|alt|title|width|height|" + TextPreparer.TOL_IMG_ID + "|" + TextPreparer.TOL_IMG_HEIGHT + "|style],sub,sup,blockquote[dir|class|style],object[width|height|class|style],param[name|value],embed[src|type|allowfullscreen|width|height],table[class|style],tr[class|style],td[dir|class|style|colspan|rowspan],th[class|style],div[dir|class|align|style],span[class|align|style|" + TextPreparer.TOL_IMG_ID + "],pre[class|align|style],address[class|align|style],h1[dir|class|align|style],h2[dir|class|align|style],h3[dir|class|align|style],h4[dir|class|align|style],h5[dir|class|align|style],h6[dir|class|align|style],hr,br\",\n");
        writer.printRaw("trim_span_elements: \"true\",\n");
        writer.printRaw("theme_advanced_toolbar_location: \"top\",\n");
        writer.printRaw("tol_searchurl: \"" + getSearchUrl() + "\",\n");  
        writer.printRaw("cleanup: \"true\",\n");
        writer.printRaw("remove_linebreaks: \"true\",\n");
        writer.printRaw("force_p_newlines: \"true\",\n");
        writer.printRaw("verify_html: \"true\",\n");
        writer.printRaw("save_callback: \"tol_clean\",\n");
        writer.printRaw(getAdditionalConfigurationLines());        
        writer.printRaw("theme_advanced_blockformats: \"p,address,pre,h3,h4,h5,h6\"})\n");        
    }
    
    public String getSearchUrl() {
        return "/onlinecontributors/app?service=external&page=TreehouseImageSearch&sp=l" + treehouseId + "&sp=T";
    }
    
    public String getContainersLine() {
        return "theme_advanced_containers: \"top1, listboxrow, top2, listboxrow2, top3, top4, mceEditor, mceElementpath\",\n";
    }
    
    public String getTop1Line() {
        return "theme_advanced_container_top1: \"bold, italic, underline, separator, justifyleft, justifycenter, justifyright, justifyfull, separator, sub, sup, separator, charmap, hr, separator, link, unlink, anchor, \",\n";
    }
    
    public String getTop2Line() {
        return "theme_advanced_container_top2 : \"unorderedlist, orderedlist, outdent, indent, separator, tablecontrols, separator, tableheader\",\n";
    }
    
    protected String getAdditionalConfigurationLines() {
        return "theme_advanced_container_listboxrow: \"paragraphstyles, textstyles\",\n" +
               "theme_advanced_container_listboxrow2: \"liststyles, tablestyles\",\n";
    }
}
