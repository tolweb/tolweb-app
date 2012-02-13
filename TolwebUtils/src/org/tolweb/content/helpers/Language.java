package org.tolweb.content.helpers;

/**
 * Indicates the languages supported by the media collections of the Tree  
 * of Life Web Project. 
 * 
 * @author lenards
 *
 */
public enum Language {
	/**
	 * Indicates the content is in English 
	 */
	English, 
	/**
	 * Indicates the content is in French
	 */
	French, 
	/**
	 * Indicates the content is in German
	 */
	German, 
	/**
	 * Indicates the content is in Spanish
	 */
	Spanish;
	
	/**
	 * Translates an enum value into the proper language code. 
	 * 
	 * Definitons are in the Language enum. 
	 * 
	 * @param lang the enum value
	 * @return a two character string representing the language code 
	 * associated with the enum value
	 */
	public static String toLanguageCode(Language lang) {
		switch (lang) {
		case English:
			return ENGLISH_CODE;
		case French:
			return FRENCH_CODE;
		case German:
			return GERMAN_CODE;
		case Spanish:
			return SPANISH_CODE;
		}
		return ENGLISH_CODE;
	}
	
	/**
	 * Language code for English
	 */
	public static final String ENGLISH_CODE = "en";
	/**
	 * Language code for French
	 */	
	public static final String FRENCH_CODE = "fr";
	/**
	 * Language code for German
	 */	
	public static final String GERMAN_CODE = "de";
	/**
	 * Language code for Spanish
	 */	
	public static final String SPANISH_CODE = "es";	
}
