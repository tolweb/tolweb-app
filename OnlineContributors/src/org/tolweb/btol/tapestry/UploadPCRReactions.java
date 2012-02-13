package org.tolweb.btol.tapestry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.btol.injections.PrimerInjectable;
import org.tolweb.btol.util.MassPCRParser;
import org.tolweb.btol.util.MassParseException;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class UploadPCRReactions extends AbstractMassUploadPage implements PCRInjectable,
		PrimerInjectable, UserInjectable, PageBeginRenderListener, BaseInjectable {
	@InjectPage("btol/ReactionsUploaded")
	public abstract ReactionsUploaded getUploadedPage();
	@InjectPage("btol/EditPrimer")
	public abstract EditPrimer getEditPrimerPage();
	@InjectPage("btol/EditPCRProtocol")
	public abstract EditPCRProtocol getEditProtocolPage();	
	@InjectObject("spring:pcrParser")
	public abstract MassPCRParser getParser();
	public abstract void setMissingPrimerNames(Set<String> value);
	@Persist("session")
	public abstract Set<String> getMissingPrimerNames();
	public abstract void setMissingProtocolNames(Set<String> value);
	public abstract Set<String> getMissingProtocolNames();
	public abstract String getCurrentMissingName();
	public abstract void setExistingCodes(Set<String> value);
	public abstract Set<String> getExistingCodes();
	public abstract String getCurrentExistingCode();
	public abstract void setPrimerNameToRename(String value);
	/**
	 * The name of the primer that is in the uploaded text that the user
	 * selects as being in the database but a typo
	 * @return
	 */
	public abstract String getPrimerNameToRename();
	/**
	 * The typed value of the current primer name that should be renamed
	 * @return
	 */
	public abstract String getRenamedPrimerName();
	public abstract void setRenamedPrimerName(String value);

	
	public IPage uploadNewReactions() {
		String reactionsString = getUploadString();
		MassPCRParser parser = getParser();
		parser.setStringToParse(reactionsString);
		List<PCRBatch> batches = null;
		// clear persistent property -- why is this persistent?
		setMissingPrimerNames(null);
		Set<String> missingPrimerNames = new HashSet<String>();
		Set<String> missingProtocolNames = new HashSet<String>();
		Set<String> existingCodes = new HashSet<String>();
		try {
			batches = parser.getBatches(getContributor(), missingProtocolNames, missingPrimerNames,
					existingCodes, getProject().getId());
		} catch (MassParseException e) {
			setErrorMessage(e.getMessage());
			return null;
		}		
		if (batches != null && missingProtocolNames.size() == 0 
				&& missingPrimerNames.size() == 0 && 
				existingCodes.size() == 0) {
			getPCRBatchDAO().saveBatches(batches, getProject().getId());
			getUploadedPage().setBatches(batches);
			return getUploadedPage();					
		} else {
			setMissingPrimerNames(missingPrimerNames);
			setMissingProtocolNames(missingProtocolNames);
			setTypedText(reactionsString);
			setExistingCodes(existingCodes);
			return null;
		}
	}
	
	public boolean getHasMissingPrimers() {
		return getMissingPrimerNames() != null && getMissingPrimerNames().size() > 0;
	}
	public boolean getHasMissingProtocols() {
		return getMissingProtocolNames() != null && getMissingProtocolNames().size() > 0;
	}
	public boolean getHasExistingCodes() {
		return getExistingCodes() != null && getExistingCodes().size() > 0;
	}
	public IPage editMissingPrimer(String missingPrimerName) {
		return getEditPrimerPage().editNewPrimerWithName(missingPrimerName);
	}
	public IPage editMissingProtocol(String missingProtocolName) {
		return getEditProtocolPage().editNewProtocolWithName(missingProtocolName);
	}
	public void showPrimerRenameBox(String primerName) {
		setPrimerNameToRename(primerName);
	}
	public boolean getShowEditFields() {
		return getCurrentMissingName() != null && 
			getCurrentMissingName().equals(getPrimerNameToRename());
	}	
}
