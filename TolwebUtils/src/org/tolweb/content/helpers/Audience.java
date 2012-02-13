package org.tolweb.content.helpers;

/**
 * Represents the intended audience for the content. 
 * 
 * @author lenards
 *
 */
public enum Audience {
	/**
	 * Intended for the general public learning about organisms.
	 */
	GeneralPublic, 
	/**
	 * Intended for scientists and expert individuals
	 */
	ExpertUsers;
	
	/**
	 * Converts an enum value into a string
	 * 
	 * @param aud the enum value
	 * @return a string representing 
	 */
	public static String toString(Audience aud) {
		// I hate that I can't do this an easier way
		if (aud.equals(Audience.GeneralPublic)) {
			return "General public";
		}
		if (aud.equals(Audience.ExpertUsers)) {
			return "Expert users";
		}
		return "";
	}
}
