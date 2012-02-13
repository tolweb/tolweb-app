/*
 * Created on Jan 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IScript;
import org.apache.tapestry.PageRenderSupport;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.form.FormConstants;
import org.apache.tapestry.form.LinkSubmit;

/**
 * @author dmandel
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class PopupThenLinkSubmit extends LinkSubmit {
    public abstract String getWidth();
    public abstract String getHeight();
    public abstract String getPopupHref();
    public abstract String getWindowName();
    
    private String getWindowOpenString() {
        return "var w = window.open('" + getPopupHref() + "', '" + getWindowName() + "','resizable,height=" + getHeight() + ",width=" + getWidth() + "'); w.focus();";
    }
    
    /**
     * The name of an {@link org.apache.tapestry.IRequestCycle} attribute in which the current
     * submit link is stored. LinkSubmits do not nest.
     */

    public static final String ATTRIBUTE_NAME = "org.apache.tapestry.form.LinkSubmit";

    /**
     * Checks the submit name ({@link FormConstants#SUBMIT_NAME_PARAMETER}) to see if it matches
     * this LinkSubmit's assigned element name.
     */
    protected boolean isClicked(IRequestCycle cycle, String name)
    {
        String value = cycle.getParameter(FormConstants.SUBMIT_NAME_PARAMETER);

        return name.equals(value);
    }

    public abstract IScript getScript();

    /**
     * @see org.apache.tapestry.form.AbstractFormComponent#renderFormComponent(org.apache.tapestry.IMarkupWriter,
     *      org.apache.tapestry.IRequestCycle)
     */
    @SuppressWarnings("unchecked")
    protected void renderFormComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        boolean disabled = isDisabled();

        IForm form = getForm();
        String name = getName();

        if (!disabled)
        {
            PageRenderSupport pageRenderSupport = TapestryUtils.getPageRenderSupport(cycle, this);

            Map symbols = new HashMap();
            symbols.put("form", form);
            symbols.put("name", name);

            getScript().execute(cycle, pageRenderSupport, symbols);

            writer.begin("a");
            writer.attribute("href", (String) symbols.get("href"));

            renderIdAttribute(writer, cycle);

            renderInformalParameters(writer, cycle);
        }
        writer.begin("a");
        writer.attribute("href", "javascript:" + getWindowOpenString() + "submitLink(document."
                + form.getName() + ",\"" + name + "\");");
        renderBody(writer, cycle);

        if (!disabled)
            writer.end();

    }

    /**
     * @see org.apache.tapestry.form.AbstractSubmit#rewindFormComponent(org.apache.tapestry.IMarkupWriter, org.apache.tapestry.IRequestCycle)
     */
    protected void rewindFormComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        super.rewindFormComponent(writer, cycle);

        renderBody(writer, cycle);
    }

    /**
     * @see org.apache.tapestry.AbstractComponent#prepareForRender(org.apache.tapestry.IRequestCycle)
     */
    protected void prepareForRender(IRequestCycle cycle)
    {
        IComponent outer = (IComponent) cycle.getAttribute(ATTRIBUTE_NAME);

        if (outer != null)
            throw new ApplicationRuntimeException("exception",
                    this, getLocation(), null);

        cycle.setAttribute(ATTRIBUTE_NAME, this);
    }

    /**
     * @see org.apache.tapestry.AbstractComponent#cleanupAfterRender(org.apache.tapestry.IRequestCycle)
     */
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        cycle.removeAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Links can not take focus, ever.
     */
    protected boolean getCanTakeFocus()
    {
        return false;
    }

    /**
     * Returns true; the LinkSubmit's body should render during a rewind, even if the component is
     * itself disabled.
     */
    protected boolean getAlwaysRenderBodyOnRewind()
    {
        return true;
    }
}