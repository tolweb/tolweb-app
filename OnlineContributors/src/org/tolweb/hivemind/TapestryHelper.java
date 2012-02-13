package org.tolweb.hivemind;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.request.IUploadFile;
import org.tolweb.tapestry.AlphabeticalObjectArrayTableModel;
import org.tolweb.tapestry.AlphabeticalTableModel;

/**
 * Used to centralize code that deals with obscure tapestry stuff we'd
 * otherwise like not to remember
 * @author dmandel
 *
 */
public interface TapestryHelper {
	public String getExternalServiceUrl(String pageName, Object[] parameters);
	public String getPageServiceUrl(String pageName);
	public ILink getExternalServiceLink(String pageName, Object[] parameters);
	public ILink getPageServiceLink(String pageName);
	public AlphabeticalObjectArrayTableModel getAlphabeticalObjectArrayTableModel(String columns, IComponent page, Object[] data);
	public AlphabeticalTableModel getAlphabeticalTableModel(String columns, IComponent page, Object[] data);
	public String getStringFromUploadFile(IUploadFile file);
	public String getRestartServiceUrl();	
	public boolean getIsSafari();
	public String getDomainName();
}
