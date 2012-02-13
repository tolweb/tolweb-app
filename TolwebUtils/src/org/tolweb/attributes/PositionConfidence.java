package org.tolweb.attributes;

import org.tolweb.treegrow.tree.Node;

/**
 * Defined type for the node attribute state of position confidence.  
 * Previously represented as integer-constants in the TreeGrow Node object.  
 * @author lenards
 */
public enum PositionConfidence {
	Confident, 
	Tentative, 
	IncertaeSedis;

	private static final String INCERT_OFF_INTERNAL = "confident";
	private static final String INCERT_PUTATIVE_INTERNAL = "tentative";
	private static final String INCERT_UNSPECIFIED_INTERNAL = "incertaesedis";	
	
	public static final String TYPE_MAPPING_ERROR_MSG = "type does not map to a defined position confidence state";
	
	public String toString() {
		return this.name().toLowerCase();
	}

/* legacy type-info - notes
 * int | old text constants | new taxa import xml "ingest" usage 
 * -----------------------------------------------------------
    0 = "incertae sedis off"  = confident
    1 = "incertae sedis in putative position"   = tentative
    2 = "incertae sedis position unspecified"   = incertaesedis
	public static final int INCERT_OFF = 0;
	public static final int INCERT_PUTATIVE = 1;
	public static final int INCERT_UNSPECIFIED = 2;        
*/	
	
	public static int toInt(PositionConfidence position) {
		switch(position) {
			case Confident: 
				return Node.INCERT_OFF;
			case Tentative:
				return Node.INCERT_PUTATIVE;
			case IncertaeSedis:
				return Node.INCERT_UNSPECIFIED;
			default: 
				throw new IllegalArgumentException(TYPE_MAPPING_ERROR_MSG);		
		}
	}
	
	public static PositionConfidence fromString(String position) {
		return fromString(position, false); 
	}
	
	public static PositionConfidence fromString(String position, boolean legacy) {
		if (isConfidentString(position, legacy)) {
			return Confident;
		} else if (isTenativeString(position, legacy)) {
			return Tentative;
		} else if (isIncertaeSedisString(position, legacy)) {
			return IncertaeSedis;
		} else {
			throw new IllegalArgumentException(TYPE_MAPPING_ERROR_MSG);
		}
	}
	
	private static boolean isConfidentString(String position, boolean legacy) {
		return position.equalsIgnoreCase((legacy) ? Node.INCERT_OFF_TEXT : INCERT_OFF_INTERNAL);
	}
	
	private static boolean isTenativeString(String position, boolean legacy) {
		return position.equalsIgnoreCase((legacy) ? Node.INCERT_PUTATIVE_TEXT : INCERT_PUTATIVE_INTERNAL);
	}
	
	private static boolean isIncertaeSedisString(String position, boolean legacy) {
		return position.equalsIgnoreCase((legacy) ? Node.INCERT_UNSPECIFIED_TEXT : INCERT_UNSPECIFIED_INTERNAL);
	}
	
	public static PositionConfidence fromInteger(Integer i) {
		if (i == null) {
			throw new IllegalArgumentException("Integer cannont be null");
		}
		return fromInt(i.intValue());
	}
	
	public static PositionConfidence fromInt(int position) {
		switch(position) {
		case Node.INCERT_OFF: 
			return Confident;
		case Node.INCERT_PUTATIVE:
			return Tentative;
		case Node.INCERT_UNSPECIFIED:
			return IncertaeSedis;
		default: 
			throw new IllegalArgumentException(TYPE_MAPPING_ERROR_MSG);
		}
	}	
}
