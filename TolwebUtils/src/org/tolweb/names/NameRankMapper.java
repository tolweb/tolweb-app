package org.tolweb.names;

/**
 * Class that infers node ranks from a name
 * @author dmandel
 *
 */
public class NameRankMapper {
	
	public Rank getRankForName(String name) {
		Rank rank = Rank.OTHER;
		if (name != null) {
			if (name.endsWith("oidea")) {
				rank = Rank.SUPERFAMILY;
			} else if (name.endsWith("idae")) {
				rank = Rank.FAMILY;
			} else if (name.endsWith("inae")) {
				rank = Rank.SUBFAMILY;
			} else if (name.endsWith("ini")) {
				rank = Rank.TRIBE;
			}
		} 
		return rank;
	}
}
