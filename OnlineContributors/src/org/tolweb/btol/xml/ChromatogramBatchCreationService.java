package org.tolweb.btol.xml;

import java.util.Date;

import org.apache.tapestry.IRequestCycle;
import org.jdom.Document;
import org.tolweb.btol.ChromatogramBatch;
import org.tolweb.btol.injections.ChromatogramInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;

public abstract class ChromatogramBatchCreationService extends ProjectXMLPage 
		implements RequestInjectable, ChromatogramInjectable {
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		// create a new batch with the passed name and description
		String name = getRequest().getParameterValue(RequestParameters.NAME);
		String description = getRequest().getParameterValue(RequestParameters.DESCRIPTION);
		String contributorId = getRequest().getParameterValue(RequestParameters.CONTRIBUTOR_ID);
		ChromatogramBatch batch = new ChromatogramBatch();
		batch.setName(name);
		batch.setDescription(description);
		if (StringUtils.notEmpty(contributorId) && StringUtils.getIsNumeric(contributorId)) {
			batch.setContributorId(Integer.parseInt(contributorId));
		}
		batch.setCreationDate(new Date());
		getChromatogramBatchDAO().saveChromatogramBatch(batch, getProjectIdOrDefault());
		Document successDocument = getSuccessDocument();
		successDocument.getRootElement().setAttribute(XMLConstants.ID, batch.getId().toString());
		setResultDocument(successDocument);
	}
}
