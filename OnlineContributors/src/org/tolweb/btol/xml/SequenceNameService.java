package org.tolweb.btol.xml;

import org.apache.tapestry.IRequestCycle;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.XMLConstants;

public abstract class SequenceNameService extends ProjectXMLPage implements TreeGrowServerInjectable, PCRInjectable {
	private static final String DRM_CODE = "DNA";
	private static final String BTOL_CODE = "BP";

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String code = getRequest().getParameterValue(RequestParameters.CODE);
		SpecimenExtraction extraction = null;
		Long projectId = getProjectIdOrDefault();
		boolean negativeControl = false;
		if (code.startsWith(BTOL_CODE)) {
			// the code is a code for a PCRReaction, which has a reference to the extraction,
			// so get the reaction
			PCRReaction reaction = getPCRReactionFromCode(code);
			if (reaction != null) {
				if (reaction.getIsNegativeControl()) {
					negativeControl = true;
				} else {				
					extraction = reaction.getExtraction();
				}
			}
		} else {
			// 
			// it's a DRM code, so it's a straight extraction code
			SourceCollection drmCollection = getSpecimenExtractionDAO().getSourceCollectionWithCode(DRM_CODE);
			// chop off the first part
			code = code.substring(DRM_CODE.length());
			extraction = getSpecimenExtractionDAO().getExtractionWithCode(code, drmCollection, projectId);
		}
		boolean found = extraction != null;
		if (found || negativeControl) {
			Document doc = new Document();
			String elementName = XMLConstants.SEQUENCE.toLowerCase();
			String plural = elementName+"s";
			Element rootElement = new Element(plural);
			doc.setRootElement(rootElement);			
			Element sequenceElement = new Element(XMLConstants.SEQUENCE.toLowerCase());
			String sequenceCode = "";
			if (found) {
				sequenceCode = extraction.getSequenceCode();
			} else {
				sequenceCode = "Negative Control";
			}
			sequenceElement.addContent(new CDATA(sequenceCode));
			rootElement.addContent(sequenceElement);
			setResultDocument(doc);
		} else {
			setResultDocument(getServerXMLWriter().getNotFoundDocument());
		}
	}
	
	protected PCRReaction getPCRReactionFromCode(String code) {
		if (code.startsWith(BTOL_CODE)) {
			// strip off the BP portion of the number			
			code = code.substring(BTOL_CODE.length());
			PCRReaction reaction = getPCRReactionDAO().getReactionWithCode(code);
			return reaction;
		} else {
			return null;
		}
	}
	
}
