package org.tolweb.tapestryenhancements;

import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IScript;
import org.apache.tapestry.PageRenderSupport;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.form.FormConstants;
import org.tolweb.tapestry.injections.MiscInjectable;

/**
 * Implements a component that submits its enclosing form via a JavaScript link. [<a
 * href="../../../../../ComponentReference/LinkSubmit.html">Component Reference</a>]
 * 
 * @author Richard Lewis-Shell
 * @version $Id$
 */

public abstract class PopupLinkSubmit extends CustomSubmit implements MiscInjectable {
    public abstract String getWidth();
    public abstract String getHeight();
    public abstract String getWindowName();	
  /**
   * The name of an {@link org.apache.tapestry.IRequestCycle}attribute in which
   * the current submit link is stored. LinkSubmits do not nest.
   */

  public static final String ATTRIBUTE_NAME = "org.tolweb.tapestryenhancements.PopupLinkSubmit";

  public static final String ATTRIBUTE_NAME_SCRIPT = "org.tolweb.tapestryenhancements.PopupLinkSubmitScript";

  /**
   * The name of an {@link org.apache.tapestry.IRequestCycle}attribute in which
   * the link submit component that generates the javascript function is stored.
   * The function is only required once per page (containing a form with a
   * non-disabled LinkSubmit)
   */
  public static final String ATTRIBUTE_FUNCTION_NAME = "org.tolweb.tapestryenhancements.PopupLinkSubmit_function";

  /**
   * Checks the submit name ({@link FormConstants#SUBMIT_NAME_PARAMETER}) to
   * see if it matches this LinkSubmit's assigned element name.
   */
  protected boolean isClicked(IRequestCycle cycle, String name) {
    String value = cycle.getParameter(FormConstants.SUBMIT_NAME_PARAMETER);

    return name.equals(value);
  }

  public ILink getLink(IRequestCycle cycle) {
    return getTapestryHelper().getPageServiceLink(getPopupPage());
  }

  public abstract Object getPopupParameters();

  public abstract String getPopupPage();

  /**
   * @see org.apache.tapestry.form.AbstractFormComponent#renderFormComponent(org.apache.tapestry.IMarkupWriter,
   *      org.apache.tapestry.IRequestCycle)
   */
  @Override 
  @SuppressWarnings("unchecked")
  protected void renderFormComponent(IMarkupWriter writer, IRequestCycle cycle) {
    boolean disabled = isDisabled();

    if (!disabled) {
      PageRenderSupport pageRenderSupport = TapestryUtils.getPageRenderSupport(cycle,
          this);

      if (cycle.getAttribute(ATTRIBUTE_NAME_SCRIPT) == null) {
        BodyBuilder builder = new BodyBuilder();

        builder.addln("function {0}(form, elementId, url)", "submitPopupLink");
        builder.begin();
        builder.addln("var form = document.getElementById(form);");       
        builder.addln("form.events.submit(elementId);");
        builder.addln("aWindow = window.open(url, '" + getWindowName() 
            + "', 'resizable,height=" + getHeight() + ",width=" + getWidth() + "', false);");
        builder.addln("aWindow.focus();");
        builder.end();

        pageRenderSupport.addBodyScript(builder.toString());
        cycle.setAttribute(ATTRIBUTE_NAME_SCRIPT, this);
      }

      IForm form = getForm();

      String slink = getLink(cycle).getURL(null, true);
      Map symbols = new HashMap();
      symbols.put("form", form);
      symbols.put("name", getName());
      symbols.put("popupLink", slink);

      getScript().execute(cycle, pageRenderSupport, symbols);

      writer.begin("a");
      writer.attribute("href", (String) symbols.get("href"));

    }

    renderBody(writer, cycle);

    if (!disabled)
      writer.end();
  }

  public abstract IScript getScript();

  /**
   * @see org.apache.tapestry.AbstractComponent#prepareForRender(org.apache.tapestry.IRequestCycle)
   */
  protected void prepareForRender(IRequestCycle cycle) {
    IComponent outer = (IComponent) cycle.getAttribute(ATTRIBUTE_NAME);

    if (outer != null)
      throw new ApplicationRuntimeException("Link Submits May Not Nest", this, getLocation(), null);

    cycle.setAttribute(ATTRIBUTE_NAME, this);
  }

  /**
   * @see org.apache.tapestry.AbstractComponent#cleanupAfterRender(org.apache.tapestry.IRequestCycle)
   */
  protected void cleanupAfterRender(IRequestCycle cycle) {
    cycle.removeAttribute(ATTRIBUTE_NAME);
  }

  /**
   * Links can not take focus, ever.
   */
  protected boolean getCanTakeFocus() {
    return false;
  }

  /**
   * Returns true; the LinkSubmit's body should render during a rewind, even if
   * the component is itself disabled.
   */
  protected boolean getRenderBodyOnRewind() {
    return true;
  }

}
