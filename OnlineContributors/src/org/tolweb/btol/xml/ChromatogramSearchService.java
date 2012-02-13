package org.tolweb.btol.xml;

import java.util.Collection;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.btol.injections.ChromatogramInjectable;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.XMLConstants;


public abstract class ChromatogramSearchService extends SequenceNameService implements IExternalPage, GeneInjectable,
		ChromatogramInjectable {
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String extractionName = getRequest().getParameterValue(RequestParameters.EXTRACTION);
		String geneName = getRequest().getParameterValue(RequestParameters.GENE);
		String taxon = getRequest().getParameterValue(RequestParameters.TAXON);
		String batchName = getRequest().getParameterValue(RequestParameters.NAME);
		Long projectId = getProjectIdOrDefault();
		try {
			outputResultsDocument(getChromatogramSearcher().getChromatogramFilenames(extractionName, geneName, batchName, taxon, projectId));
		} catch (RuntimeException e) {
			Element errorElement = new Element(XMLConstants.ERROR);
			errorElement.addContent(new CDATA(e.getMessage()));
			setResultDocument(new Document(errorElement));			
		}
	}

	@SuppressWarnings("unchecked")
	private void outputResultsDocument(Collection returnVals) {
		Element rootElement = new Element(XMLConstants.SUCCESS.toLowerCase());
		Document doc = new Document(rootElement);
		rootElement.setAttribute(XMLConstants.COUNT.toLowerCase(), returnVals.size() + "");
		setResultDocument(doc);
	}
}
