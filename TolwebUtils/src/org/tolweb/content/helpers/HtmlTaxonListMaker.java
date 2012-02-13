package org.tolweb.content.helpers;

import java.util.List;

import org.tolweb.hibernate.MappedNode;

/**
 * An implementation of a taxon list maker that generates an HTML taxon list. 
 * 
 * @author lenards
 *
 */
public class HtmlTaxonListMaker extends TaxonListMaker {

	/**
	 * Constructs an instance of the list maker
	 * 
	 * @param basalNode the root node of the clade
	 * @param nodes the nodes of the clade
	 */
	public HtmlTaxonListMaker(MappedNode basalNode, List<MappedNode> nodes) {
		super(basalNode, nodes);
	}

	/**
	 * Applies processing to transform the node name 
	 *
	 * @param mnode the node to process
	 * @return a node name with processing applied  
	 */
	@Override
	public String processNodeName(MappedNode mnode) {
		String nodeName = mnode.getName();
		if (mnode.getExtinct() == MappedNode.EXTINCT) {
			nodeName += " &dagger;";
		}
		return nodeName;
	}

	/**
	 * Returns the closing text for an item in the list.
	 * 
	 * @return a string representing the closing text for an item 
	 */
	@Override
	public String itemTextClose() {
		return "</li>";
	}

	/**
	 * Returns the starting text for an item in the list.
	 * 
	 * @return a string representing the starting text for an item
	 */
	@Override
	public String itemTextStart() {
		return "<li>";
	}

	/**
	 * Returns the closing text for a list.
	 * 
	 * @return a string representing the closing text for a list
	 */
	@Override
	public String listTextClose() {
		return "</ul>";
	}

	/**
	 * Returns the starting text for a list.
	 * 
	 * @return a string representing the starting text for a list
	 */
	@Override
	public String listTextStart() {
		return "<ul>";
	}
}
