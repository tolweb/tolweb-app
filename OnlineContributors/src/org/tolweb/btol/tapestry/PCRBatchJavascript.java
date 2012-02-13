package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.PCRProtocol;
import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.Sequence;

public abstract class PCRBatchJavascript extends BaseComponent {
	@Parameter(required = true)
	public abstract PCRBatch getBatch();
	public abstract PCRReaction getCurrentReaction();
	@Parameter(required = true)
	public abstract List<Sequence> getSequences();
	public abstract Sequence getCurrentSequence();
	
	public String getForwardPrimerJavascriptName() {
		return "fprimer_" + getBatch().getId();
	}
	public String getReversePrimerJavascriptName() {
		return "rprimer_" + getBatch().getId();
	}
	public String getProtocolJavascriptName() {
		return "protocol_" + getBatch().getId();
	}
	public String getCurrentSequenceJavascriptName() {
		return getSequenceJavascriptName(getCurrentSequence());
	}
	public String getReactionNotesJavascriptName() {
		return getReactionNotesJavascriptName(getCurrentReaction());
	}
	public String getReactionNotesJavascriptName(PCRReaction reaction) {
		return "reactionnotes_" + reaction.getId();
	}
	public String getProtocolHtml() {
		PCRProtocol protocol = getBatch().getProtocol();
		String protocolHtml = "<table><tr><th>Type</th><td>" + protocol.getProtocolTypeString() + "</td></tr>";
		protocolHtml += "<tr><th>Taq Name</th><td>" + protocol.getTaqName() + "</td></tr>";
		protocolHtml += getStagesHtml(protocol.getA1Temp(), protocol.getC1Cycles(), 1);
		if (protocol.getStages() > 1) {
			protocolHtml += getStagesHtml(protocol.getA2Temp(), protocol.getC2Cycles(), 2);
		}
		protocolHtml += "</table>";
		return protocolHtml;
	}
	
	private String getStagesHtml(float annealingTemp, int numCycles, int stageNum) {
		return "<tr><th>Annealing Temp.</th><td>" + annealingTemp + "</td></tr>" +
			"<tr><th>Number of Cycles</th><td>" + numCycles + "</td></tr>";
	}
	public String getSequenceJavascriptName(Sequence currentSequence) {
		return "sequence_" + currentSequence.getId();
	}
	public String getFastaString() {
		return getCurrentSequence().getFasta().replaceAll("\n", "<br/>");
	}
}
