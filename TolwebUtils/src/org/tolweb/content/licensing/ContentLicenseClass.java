package org.tolweb.content.licensing;

public enum ContentLicenseClass {
	CommercialWithModification, 
	CommercialWithoutModification, 
	NonCommercialWithModification,
	NonCommercialWithoutModification;
	
	public static final String NONCOMMERCIAL_WITH_MOD = "nc";
	public static final String NONCOMMERCIAL_WITHOUT_MOD = "nc-nd";
	public static final String COMMERCIAL_WITH_MOD = "c";
	public static final String COMMERCIAL_WITHOUT_MOD = "c-nd";
	
	public static final byte NONCOMMERCIAL_WITH_MOD_VALUE = 0;
	public static final byte NONCOMMERCIAL_WITHOUT_MOD_VALUE = 1;
	public static final byte COMMERCIAL_WITH_MOD_VALUE = 2;
	public static final byte COMMERCIAL_WITHOUT_MOD_VALUE = 4;
	
	public static ContentLicenseClass createContentLicenseClass(String webServiceName) {
		if(NONCOMMERCIAL_WITH_MOD.equals(webServiceName)) {
			return NonCommercialWithModification;
		} else if (NONCOMMERCIAL_WITHOUT_MOD.equals(webServiceName)) {
			return NonCommercialWithoutModification;
		} else if (COMMERCIAL_WITH_MOD.equals(webServiceName)) {
			return CommercialWithModification;
		} else if (COMMERCIAL_WITHOUT_MOD.equals(webServiceName)) {
			return CommercialWithoutModification;
		} else {
			throw new IllegalArgumentException("web service type does not map to a defined content license class");
		}
	}
	
	public static byte byteValue(ContentLicenseClass licClass) {
		switch (licClass) {
			case CommercialWithModification:
				return COMMERCIAL_WITH_MOD_VALUE;
			case CommercialWithoutModification:
				return COMMERCIAL_WITHOUT_MOD_VALUE;
			case NonCommercialWithModification:
				return NONCOMMERCIAL_WITH_MOD_VALUE;
			case NonCommercialWithoutModification:
				return NONCOMMERCIAL_WITHOUT_MOD_VALUE;			
			default:
				throw new IllegalArgumentException("type does not map to a defined content license class");
		}
	}
	
	public static ContentLicenseClass fromByte(byte value) {
		switch (value) {
			case COMMERCIAL_WITH_MOD_VALUE:
				return CommercialWithModification;
			case COMMERCIAL_WITHOUT_MOD_VALUE:
				return CommercialWithoutModification;
			case NONCOMMERCIAL_WITH_MOD_VALUE:
				return NonCommercialWithModification;
			case NONCOMMERCIAL_WITHOUT_MOD_VALUE:
				return NonCommercialWithoutModification;			
			default:
				throw new IllegalArgumentException("type does not map to a defined content license class");
		}		
	}
}
