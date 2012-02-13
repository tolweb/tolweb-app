package org.tolweb.btol.xml;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.btol.Primer;
import org.tolweb.btol.injections.PrimerInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;

/**
 * 
 * @author dmandel
 * onlinecontributors/app?service=external&page=btolxml/PrimerService&primer_name=LCO1490&key=archostemataarec00L
 */
public abstract class PrimerService extends ProjectXMLPage implements PrimerInjectable, IExternalPage, RequestInjectable {
	private static final String APP_KEY = "archostemataarec00L";
	
	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String primerName = getRequest().getParameterValue(RequestParameters.PRIMER_NAME);
		String appKey = getRequest().getParameterValue(RequestParameters.KEY);
		
		if (StringUtils.notEmpty(primerName) && isValidKey(appKey)) {
			// check 3 things: code, name, synonyms
			Primer primer = null;
			Long projectId = Long.valueOf(getProjectIdOrDefault());
			primer = getPrimerDAO().getPrimerWithCode(primerName, projectId);
			if (primer == null) {
				primer = getPrimerDAO().getPrimerWithName(primerName, projectId);
			}
			if (primer == null) {
				primer = getPrimerDAO().getPrimerWithSynonym(primerName, projectId);
			}
			Document document = new Document();
			Element root = null;
			if (primer == null) {
				root = new Element(XMLConstants.ERROR);
				root.setAttribute(XMLConstants.ERRORNUM, "404");
			} else {
				root = new Element(XMLConstants.PRIMERS);
				Element child = new Element(XMLConstants.PRIMER);
				addPrimerAttributes(primer, child);
				root.addContent(child);
			}
			document.setRootElement(root);
			setResultDocument(document);
		} else if (isValidKey(appKey)) {
			List primers = getPrimerDAO().getAllPrimersInProject(getProjectIdOrDefault());
			Document document = new Document();
			Element root = null;
			if (primers != null) {
				root = new Element(XMLConstants.PRIMERS);
				for (Iterator itr = primers.iterator(); itr.hasNext(); ) {
					Primer tmp = (Primer)itr.next();
					Element child = new Element(XMLConstants.PRIMER);
					addPrimerAttributes(tmp, child);
					root.addContent(child);
				}
			} else {
				root = new Element(XMLConstants.ERROR);
				root.setAttribute(XMLConstants.ERRORNUM, "404");				
			}
			document.setRootElement(root);
			setResultDocument(document);			
		} else {
			Document document = new Document();
			Element root = new Element(XMLConstants.ERROR);
			root.setAttribute(XMLConstants.ERRORNUM, "500");
			Element msg = new Element("message");
			msg.setText("Error: proper authorization not found.");
			root.addContent(msg);
			document.setRootElement(root);
			setResultDocument(document);
		}
	}

	private void addPrimerAttributes(Primer primer, Element child) {
		child.setAttribute(XMLConstants.name, primer.getName());
		child.setAttribute(XMLConstants.genename, primer.getGene().getName());
		String directionText = primer.getIsForward() ? "F" : "R";
		child.setAttribute(XMLConstants.direction, directionText);
		child.setAttribute(XMLConstants.SEQUENCE.toLowerCase(), primer.getSequence());
	}
	
	private boolean isValidKey(String argumentKey) {
		return APP_KEY.equals(argumentKey);
	}
}

