/*
 * Created on Nov 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.apache.tapestry.markup.UTFMarkupFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.main.XMLWriter;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class XMLPage extends BasePage implements IExternalPage, RequestInjectable {
	private String userName;
	private String password;
	private String groupName;
	
    public abstract void setDocumentString(String value);
	
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        userName = getRequest().getParameterValue(RequestParameters.USER_ID);
        password = getRequest().getParameterValue(RequestParameters.PASSWORD);
        groupName = getRequest().getParameterValue(RequestParameters.GROUP);
    }
    
    /**
     * overridden to set the content-type to text/xml
     */
    public IMarkupWriter getResponseWriter(OutputStream out) {
        return new MarkupWriterImpl("text/xml", new PrintWriter(out), new UTFMarkupFilter());
    }
    
    protected void setSuccessDocumentAsResult() {
        setResultDocument(getSuccessDocument());
    }
    
    protected Document getSuccessDocument() {
        Document doc = new Document(new Element(XMLConstants.SUCCESS));
        return doc;
    }
    
    protected void setResultDocument(Document doc) {
        setDocumentString(getSerializedDocument(doc));
    }
    
    private String getSerializedDocument(Document doc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1000000);
        XMLOutputter serializer = XMLWriter.getXMLOutputter();
        try {
        	serializer.output(doc, out);
        	out.flush();
        	out.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        String result = out.toString(); 
        return result;
    }    
	/**
	 * @return Returns the groupName.
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName The groupName to set.
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
