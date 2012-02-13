/*
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry.wrappers;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.asset.ExternalAsset;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.components.BlockRenderer;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.web.WebRequest;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.btol.tapestry.BtolContributorLogin;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractWrapper extends BaseComponent	implements 
		UserInjectable, CookieInjectable, ProjectInjectable, 
		BaseInjectable {
	
	private ExternalAsset externalTolCssAsset;
	
	protected IRender delegate;
	
    @InjectObject("infrastructure:request")
    public abstract WebRequest getWebRequest();
    @InitialValue("webRequest.contextPath")
    public abstract String getBaseURL();   
    @Parameter
    public abstract String getTitle();
    @Parameter
    public abstract IAsset getAdditionalStylesheet();
    @Parameter
    public abstract boolean getIncludeDojo();
	@Parameter	
	public abstract IRender getAdditionalDelegate();
	@Parameter
	public abstract String getBodyId();
	@Asset("css/tol.css")
	public abstract IAsset getCssAsset();
	@InjectObject("engine-service:restart")
	public abstract IEngineService getRestartService();
	
	public IAsset getTolCssStylesheet() {
    	if (getConfiguration().getUseExternalStylesheets()) {
    		return getExternalTolCssAsset();
    	} else {
    		return getCssAsset();
    	}		
	}

    public Block getDojoBlock() {
    	return (Block) getComponent("dojoBlock");
    }
    
    public ExternalAsset getExternalTolCssAsset() {
    	if (externalTolCssAsset == null) {
    		externalTolCssAsset = new ExternalAsset("/tree/css/tol.css", null);
    	}
    	return externalTolCssAsset;
    }
	
	public IRender getDelegate() {
		if (delegate == null) {
			delegate = new IRender() {
				public void render(IMarkupWriter writer, IRequestCycle cycle) {
					writer.printRaw(getStandardIncludes());
					if (getAdditionalDelegate() != null) {
						getAdditionalDelegate().render(writer, cycle);
					}
					if (getIncludeDojo()) {
						if (getDojoBlock() != null) {
							BlockRenderer renderer = new BlockRenderer(getDojoBlock());
							renderer.render(writer, cycle);
						}
					}
				}
			};
		}
		return delegate;
	}
    
	public ILink logout() {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (contr != null) {
			getEditHistoryDAO().clearAllLocksForContributor(contr);
			getMiscEditHistoryDAO().clearAllLocksForContributor(contr);
		}
		getCookieAndContributorSource().removeCookieValue(CookieAndContributorSource.PERMISSIONS_COOKIE);
		setContributor(null);
		setProject(null);
		getCookieAndContributorSource().removeCookieValue(CookieAndContributorSource.PROJECT_COOKIE);
		getPage().getRequestCycle().setAttribute(BtolContributorLogin.LOGOUT, true);
		//cycle.activate(getLogoutPageName());
		return getRestartService().getLink(false, null);
	}

	public String getLogoutPageName() {
		return "Home";
	}
	
	public String getStandardIncludes() {
		String headContent = "<link rel=\"stylesheet\" type=\"text/css\" media=\"print\" href=\"" + getUrlBuilder().getAssetUrlString("css/printtol.css") + "\"/>\n";
		headContent += "<script src=\"" + getUrlBuilder().getAssetUrlString("tip-n-tip/js/dw_event.js") + "\" type=\"text/javascript\"></script>\n";
		headContent += "<script src=\"" + getUrlBuilder().getAssetUrlString("tip-n-tip/js/dw_viewport.js") + "\" type=\"text/javascript\"></script>\n";
		headContent += "<script src=\"" + getUrlBuilder().getAssetUrlString("tip-n-tip/js/dw_tooltip.js") + "\" type=\"text/javascript\"></script>\n";
		headContent += "<script src=\"" + getUrlBuilder().getAssetUrlString("js/tol.js") + "\" type=\"text/javascript\"></script>\n";
		return headContent;
	}
}
