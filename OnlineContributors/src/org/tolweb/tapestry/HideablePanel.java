package org.tolweb.tapestry;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.apache.tapestry.markup.UTFMarkupFilter;

/**
 * Represents a collapsable panel in the user interface.  
 * 
 * Renders a toggle button hyperlink in conjunction with a 
 * hideable div element.  Original usage in the SwapNodeConfirmation.html 
 * template. 
 * 
 * @author lenards
 *
 */
public abstract class HideablePanel extends AbstractComponent {
	public static final String ID = "hpanel";
	public static final String ONCLICK_FMT = "changeclass('%1$s', 'show', 'hide')"; 
	
	/**
	 * The value that will be concatenated at the end of the element's ID attribute name.
	 * @return an integer representing the suffix value. 
	 */
	@Parameter
	public abstract int getIdSuffix();
	public abstract void setIdSuffix(int suffix);
	
	/**
	 * The value that will serve as the "base" for the element's ID attribute name
	 * @return a string representing the base of the ID attribute
	 */
	@Parameter
	public abstract String getIdBase();
	public abstract void setIdBase(String base);
	
	/**
	 * The text to use as the hyperlink for the toggle button
	 * @return
	 */
	@Parameter
	public abstract String getToggleLinkText();
	public abstract void setToggleLinkText(String linkText);
	
	/**
	 * The actual unescaped content that will be in the body of the panel. 
	 * @return
	 */
	@Parameter
	public abstract String getPanelContent();
	public abstract void setPanelContent(String content);

	/* A sample of what we're trying to produce. 
	<a onclick="changeclass('sourcemanifestxml', 'show', 'hide')" class="edittoggle">show source node manifest</a>	
						<div id="sourcemanifestxml" class="hide">
						<pre>
							<!-- content -->
						</pre>
						</div>
	 */

	/**
	 * Handles the HTML rendering of the user interface component. 
	 */
	@Override
	protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
		// <<this might be a candidate for refactoring>>
		// stole this from an example within the application: TaxonList.java
        long currentTime = System.currentTimeMillis();
        String output = null;
       
	    ByteArrayOutputStream stream = new ByteArrayOutputStream(2000);
	    IMarkupWriter otherWriter = new MarkupWriterImpl("text/html", new PrintWriter(stream), new UTFMarkupFilter());
	    
	    otherWriter.begin("a");
	    otherWriter.attribute("onclick", String.format(ONCLICK_FMT, getIdBase()+getIdSuffix()));
	    otherWriter.attribute("class", "edittoggle");
	    otherWriter.printRaw(getToggleLinkText());
	    otherWriter.end("a");
        otherWriter.begin("div");
        otherWriter.attribute("id", getIdBase()+getIdSuffix());
        otherWriter.attribute("class", "hide");
        otherWriter.begin("pre");
        otherWriter.printRaw(StringEscapeUtils.escapeHtml(getPanelContent()));
        otherWriter.end("pre");
        otherWriter.end("div");
	    otherWriter.flush();   
	    output = stream.toString();
	    
	    writer.printRaw(output);
	    System.out.println("render component took: " + (System.currentTimeMillis() - currentTime));        
	}
}
