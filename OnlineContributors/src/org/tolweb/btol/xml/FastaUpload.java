package org.tolweb.btol.xml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.btol.Chromatogram;
import org.tolweb.btol.Sequence;
import org.tolweb.btol.injections.ChromatogramInjectable;
import org.tolweb.btol.injections.SequenceInjectable;
import org.tolweb.treegrow.main.RequestParameters;

public abstract class FastaUpload extends ProjectXMLPage implements ChromatogramInjectable, SequenceInjectable {

	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String fasta = getRequest().getParameterValue(RequestParameters.FAS);
		String contributorId = getRequest().getParameterValue(RequestParameters.CONTRIBUTOR_ID);
		String chroFilenamesString = getRequest().getParameterValue(RequestParameters.FILENAME);
		String[] filenames = chroFilenamesString.split(",");
		List filenamesList = Arrays.asList(filenames); 
		List chromatograms = getChromatogramBatchDAO().getChromatogramsWithFilenames(filenamesList, getProjectIdOrDefault());
		Sequence sequence = new Sequence();
		sequence.setChromatograms(new HashSet<Chromatogram>(chromatograms));
		try {
			sequence.setContributorId(Integer.parseInt(contributorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sequence.setFasta(fasta);
		getSequenceDAO().saveSequence(sequence);
		setResultDocument(getSuccessDocument());
	}
}
