package org.tolweb.btol.xml;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.multipart.MultipartDecoder;
import org.apache.tapestry.request.IUploadFile;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.btol.Chromatogram;
import org.tolweb.btol.ChromatogramBatch;
import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.injections.ChromatogramInjectable;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.XMLConstants;

public abstract class SequenceUploadService extends SequenceNameService implements IExternalPage, RequestInjectable,
		PCRInjectable, ChromatogramInjectable {
	@InjectObject("infrastructure:multipartDecoder")
	public abstract MultipartDecoder getDecoder();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String code = getRequest().getParameterValue(RequestParameters.CODE);
		String filename = getRequest().getParameterValue(RequestParameters.FILENAME);
		String batchId = getRequest().getParameterValue(RequestParameters.BATCH_ID);
		
		MultipartDecoder multipartDecoder = getDecoder();
		IUploadFile abiFile = multipartDecoder.getFileUpload(RequestParameters.FILE);
		PCRReaction reaction = getPCRReactionFromCode(code);
		ChromatogramBatch batch = getChromatogramBatchDAO().getChromatogramBatchWithId(Long.valueOf(batchId));
		if (reaction == null) {
			Element errorElement = new Element(XMLConstants.ERROR);
			errorElement.setAttribute(XMLConstants.ERRORNUM, "404");
			Document doc = new Document(errorElement);
			setResultDocument(doc);
		} else {
			String serverFilename = getGelImageUtils().writeSequenceFileToDisk(abiFile, filename);
			Chromatogram chro = new Chromatogram();
			chro.setFilename(serverFilename);
			getChromatogramBatchDAO().saveChromatogram(chro);
			reaction.addToChromatograms(chro);
			getPCRReactionDAO().saveReaction(reaction);
			batch.addToChromatograms(chro);
			getChromatogramBatchDAO().saveChromatogramBatch(batch, getProjectIdOrDefault());
			setResultDocument(getSuccessDocument());
		}
	}	
}