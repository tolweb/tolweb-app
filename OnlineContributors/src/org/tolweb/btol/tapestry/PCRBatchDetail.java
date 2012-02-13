package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.Sequence;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.btol.injections.SequenceInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class PCRBatchDetail extends BaseComponent implements PCRInjectable, ProjectInjectable,
		SequenceInjectable, PageBeginRenderListener {
	public abstract PCRReaction getCurrentReaction();
	@InjectComponent("pcrJavascript")
	public abstract PCRBatchJavascript getJavascriptComponent();
	@Parameter(required = true)
	public abstract PCRBatch getBatch();
	@Parameter(required = false, defaultValue = "false")
	public abstract boolean getCanUploadImage();
	public abstract IUploadFile getUploadFile();
	@Parameter(required = false, defaultValue = "false")
	public abstract boolean getShowAddImageLink();
	public abstract int getIndex();
	public abstract Sequence getCurrentSequence();
	public abstract void setAllSequences(List<Sequence> value);
	public abstract List<Sequence> getAllSequences();
	

	public void pageBeginRender(PageEvent event) {
		setAllSequences(new ArrayList<Sequence>());
	}
	
	public String getMouseOutString() {
		return "hideTip()";
	}
	public String getCurrentSequenceMouseOverString() {
		return getJavascriptMouseOverString(getJavascriptComponent().getSequenceJavascriptName(getCurrentSequence()));
	}
	public String getForwardPrimerMouseOverString() {
		return getJavascriptMouseOverString(getJavascriptComponent().getForwardPrimerJavascriptName());
	}
	public String getReversePrimerMouseOverString() {
		return getJavascriptMouseOverString(getJavascriptComponent().getReversePrimerJavascriptName());
	}
	public String getProtocolMouseOverString() {
		return getJavascriptMouseOverString(getJavascriptComponent().getProtocolJavascriptName());
	}
	public String getReactionNotesMouseOverString() {
		if (getCurrentReaction().getHasNotes()) {
			return getJavascriptMouseOverString(getJavascriptComponent().getReactionNotesJavascriptName(getCurrentReaction()));
		} else {
			return null;
		}
	}
	public String getGeneName() {
		return getBatch().getForwardPrimer().getGene().getName();
	}
	public String getCurrentReactionBtolCode() {
		String code = getCurrentReaction().getBtolCode();
		return StringUtils.zeroFillString(code);
	}
	public boolean getHasImages() {
		return (getGelImage1ReallyExists() || 
				getGelImage2ReallyExists());
	}
	public boolean getCanUploadAnother() {
		return !getGelImage1ReallyExists() || !getGelImage2ReallyExists();
	}
	private boolean getGelImage1ReallyExists() {
		return getBatch().getHasImage1() && getGelImageUtils().getGelImageFileExists(getBatch().getImageFilename1());
	}	
	private boolean getGelImage2ReallyExists() {
		return getBatch().getHasImage2() && getGelImageUtils().getGelImageFileExists(getBatch().getImageFilename2());
	}
	public void uploadImage() {
		if (getPage().getRequestCycle().isRewinding()
				&& getUploadFile() != null && getUploadFile().getSize() > 0) {
			System.out.println("image file size is: " + getUploadFile().getSize());
			String filename = getGelImageUtils().writeGelImageFileToDisk(getUploadFile());
			if (!getGelImage1ReallyExists()) {
				getBatch().setImageFilename1(filename);
			} else {
				// the first image really existed so set it on the 2nd
				getBatch().setImageFilename2(filename);
			}
			// save the changes so the batch has an image associated with it
			getPCRBatchDAO().saveBatch(getBatch(), getProject().getId());
		}
	}
	@SuppressWarnings("unchecked")
	public List<Sequence> getCurrentReactionsSequences() {
		List results = getSequenceDAO().getSequencesForReaction(getCurrentReaction());
		// add the current sequences to the "all sequences" list so the javascript
		// gets emitted properly
		getAllSequences().addAll(results);
		return results;
	}
	private String getJavascriptMouseOverString(String varName) {
		return "if (" + varName + " && " + varName + " !='') {doTooltip(event," + varName + "); }";		
	}	
}
