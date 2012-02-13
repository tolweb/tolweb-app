package org.tolweb.search;

import java.util.Comparator;

/**
 * Defines the comparison for sorting GroupSearchResult instances in ascending order.
 * 
 * @author lenards
 *
 */
public class GroupSearchResultComparator implements Comparator<GroupSearchResult> {

	/**
	 * Returns the compare value between two instances using the ancestor count.
	 */
	public int compare(GroupSearchResult lhs, GroupSearchResult rhs) {
		return lhs.getAncestorCount() - rhs.getAncestorCount();
	}

}
