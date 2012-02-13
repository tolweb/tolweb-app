package org.tolweb.attributes;

import org.tolweb.treegrow.tree.Node;

/**
 * 
 * @author lenards
 *
 */
public enum Phylesis {
	Monophyletic,
	Monophyly_Uncertain,
	Nonmonophyletic;

	/* new Taxa Import ingestion format has, sadly, resulted in another set of text values */
	private static final String MONOPHYLETIC_INTERNAL = "monophyletic";
	private static final String NOT_MONOPHYLETIC_INTERNAL = "nonmonophyletic";
	private static final String MONOPHYLETIC_UNCERTAIN_INTERNAL = "monophyly-uncertain";
	
	public static final String TYPE_MAPPING_ERROR_MSG = "type does not map to a defined phylesis state";
	
	public String toString() {
		return this.name().toLowerCase().replace("_", "-");
	}
	
/*
	legacy type-info - notes
	
	- phylesis values are "monophyletic", "monophyly-uncertain", "nonmonophyletic"
	
	public static final int MONOPHYLETIC = 0;
    public static final int NOT_MONOPHYLETIC = 1;
    public static final int MONOPHYLY_UNCERTAIN = 2;
	public static final String MONOPHYLETIC_TEXT = "monophyletic";
    public static final String NOT_MONOPHYLETIC_TEXT = "not monophyletic";
    public static final String MONOPHYLY_UNCERTAIN_TEXT = "monophyly uncertain";	
 */	
	
	public static int toInt(Phylesis position) {
		switch(position) {
			case Monophyletic: 
				return Node.MONOPHYLETIC;
			case Monophyly_Uncertain:
				return Node.MONOPHYLY_UNCERTAIN;
			case Nonmonophyletic:
				return Node.NOT_MONOPHYLETIC;
			default: 
				throw new IllegalArgumentException(TYPE_MAPPING_ERROR_MSG);		
		}
	}
	
	public static Phylesis fromString(String position) {
		if (position.equalsIgnoreCase(Node.MONOPHYLETIC_TEXT) || position.equalsIgnoreCase(MONOPHYLETIC_INTERNAL)) {
			return Monophyletic;
		} else if (position.equalsIgnoreCase(Node.NOT_MONOPHYLETIC_TEXT) || position.equalsIgnoreCase(NOT_MONOPHYLETIC_INTERNAL)) {
			return Nonmonophyletic;
		} else if (position.equalsIgnoreCase(Node.MONOPHYLY_UNCERTAIN_TEXT) || position.equalsIgnoreCase(MONOPHYLETIC_UNCERTAIN_INTERNAL)) {
			return Monophyly_Uncertain;
		} else {
			throw new IllegalArgumentException(TYPE_MAPPING_ERROR_MSG);
		}
	}
	
	public static Phylesis fromInteger(Integer i) {
		if (i == null) {
			throw new IllegalArgumentException("Integer cannont be null");
		}
		return fromInt(i.intValue());
	}
	
	public static Phylesis fromInt(int position) {
		switch(position) {
		case Node.MONOPHYLETIC: 
			return Monophyletic;
		case Node.NOT_MONOPHYLETIC:
			return Nonmonophyletic;
		case Node.MONOPHYLY_UNCERTAIN:
			return Monophyly_Uncertain;
		default: 
			throw new IllegalArgumentException(TYPE_MAPPING_ERROR_MSG);
		}
	}
	
	
}
