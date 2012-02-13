package org.tolweb.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines the categories that FeatureGroups will be placed.
 * 
 * This is a variant of Josh Bloch's Typesafe Enum pattern. 
 * @author lenards
 *
 */
public class FeatureGroupCategory implements Comparable<FeatureGroupCategory>, Serializable {
	/** generated Serial Version ID */
	private static final long serialVersionUID = 2768857463702031954L;

	public static final FeatureGroupCategory  POPULAR       	= new FeatureGroupCategory("Popular");
	public static final FeatureGroupCategory  OBSCURE   		= new FeatureGroupCategory("Obscure");
	public static final FeatureGroupCategory  ACTIVE      		= new FeatureGroupCategory("Active");
	public static final FeatureGroupCategory  WELL_DEVELOPED	= new FeatureGroupCategory("Well-developed");
	
	private static final FeatureGroupCategory[] PRIVATE_VALUES = { POPULAR, OBSCURE, ACTIVE, WELL_DEVELOPED };
	
	public static final List<FeatureGroupCategory> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

	public static final List<String> STRING_VALUES = new ArrayList<String>();
	
	static {
		for (FeatureGroupCategory cat : VALUES) {
			STRING_VALUES.add(cat.toString());
		}
	}
	
	private final String name;
	// Ordinal of next suit to be created
	private static int nextOrdinal = 0;
	// Assign an ordinal to this suit
	private final int ordinal = nextOrdinal++;
	
	private FeatureGroupCategory(String name) { 
		this.name = name; 
	}
	
	public String toString()  { 
		return name; 
	}
	
	public int toInt() {
		return ordinal;
	}
	
	public int compareTo(FeatureGroupCategory cat) {
	    return ordinal - cat.ordinal;
	}

	public static FeatureGroupCategory getValueBy(String value) {
		for (FeatureGroupCategory cat : VALUES) {
			if (cat.toString().equals(value)) {
				return cat;
			}
		}
		return null;
	}
	
	public static FeatureGroupCategory getValueBy(int value) {
		for (FeatureGroupCategory cat : VALUES) {
			if (cat.ordinal == value) {
				return cat;
			}
		}
		return null;
	}	
}
