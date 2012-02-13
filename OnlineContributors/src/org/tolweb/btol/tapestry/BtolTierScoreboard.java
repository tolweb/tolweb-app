package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.btol.tapestry.selection.TierFilterSelectionModel;
import org.tolweb.hibernate.MappedNode;

/**
 * Displays statistics about the number of specimen in representing the presence of variations tiers.
 * @author lenards
 */
@ComponentClass
public abstract class BtolTierScoreboard extends BaseComponent {
	private int tier0Score;
	private int tier1Score;
	private int tier2Score;
	private int tier3Score;

    @Parameter
	public abstract MappedNode getRootNode();
    @Parameter
    public abstract Hashtable<Long, List<MappedNode>> getNodes();	
	
    @Parameter
    public abstract Integer getTierSelection();
    
	public int getTier0Score() {
		return tier0Score;
	}
	public void setTier0Score(int tier0Score) {
		this.tier0Score = tier0Score;
	}
	public int getTier1Score() {
		return tier1Score;
	}
	public void setTier1Score(int tier1Score) {
		this.tier1Score = tier1Score;
	}
	public int getTier2Score() {
		return tier2Score;
	}
	public void setTier2Score(int tier2Score) {
		this.tier2Score = tier2Score;
	}
	public int getTier3Score() {
		return tier3Score;
	}
	public void setTier3Score(int tier3Score) {
		this.tier3Score = tier3Score;
	}

	public boolean getShowTier0() {
		return showTier(TierFilterSelectionModel.TIER_0);
	}

	public boolean getShowTier1() {
		return showTier(TierFilterSelectionModel.TIER_1);
	}
	
	public boolean getShowTier2() {
		return showTier(TierFilterSelectionModel.TIER_2);
	}
	
	public boolean getShowTier3() {
		return showTier(TierFilterSelectionModel.TIER_3);
	}
	
	private boolean showTier(Integer tier) {
		Integer selection = getTierSelection(); 
		return selection != TierFilterSelectionModel.MT_GENOME_COMPLETE && 
			(selection >= tier || selection == TierFilterSelectionModel.SHOW_ALL_TIERS);
	}
	
	@Override
	protected void renderComponent(IMarkupWriter arg0, IRequestCycle arg1) {
		clearScores();
		determineScores();
		super.renderComponent(arg0, arg1);
	}
	
	private void clearScores() {
		setTier0Score(0);
		setTier1Score(0);
		setTier2Score(0);
		setTier3Score(0);
	}
	
	private void determineScores() {
		List<MappedNode> nodes = new ArrayList<MappedNode>(); 
		transverseNodes(getRootNode(), nodes);
		for (MappedNode mnode : nodes) {
			int tier = mnode.getAdditionalFields().getTier();
			if (tier == 0) {
				setTier0Score(getTier0Score()+1);
			} else if (tier == 1) {
				setTier1Score(getTier1Score()+1);
			} else if (tier == 2 ) {
				setTier2Score(getTier2Score()+1);
			} else if (tier == 3) {
				setTier3Score(getTier3Score()+1);
			}
		}
		
	}
	
	private void transverseNodes(MappedNode root, List<MappedNode> nodes) {
		if (root == null) {
			return;
		} else {
			if (getNodes().containsKey(root.getNodeId())) {
				List<MappedNode> children = getNodes().get(root.getNodeId());
				if (children != null && children.size() > 0) {
					for (MappedNode mnode : children) {
						nodes.add(mnode);
						transverseNodes(mnode, nodes);
					}
				}
			}
		}
	}
}
