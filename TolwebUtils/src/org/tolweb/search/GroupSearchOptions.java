package org.tolweb.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the available options when performing a Group Search 
 * 
 * @author lenards
 */
public class GroupSearchOptions {
	/** 
	 * Indicates the user expects partial matches. 
	 */
	public static final String PARTIAL_OPTION = "partially";
	/**
	 * Indicates the user only expects exact matches.
	 */
	public static final String EXACT_OPTION = "exactly";
	/**
	 * Indicates the user expects matches that start the search text.
	 */
	public static final String START_OPTION = "beginning of name";
	/**
	 * Indicates the user expects matches that end with the search text.
	 */
	public static final String END_OPTION = "end of name";
	
	/**
	 * Indicates that in a drop-down, this would be considered the default 
	 * value for the overall group of selections. 
	 */
	public static final String DEFAULT = PARTIAL_OPTION;
	
	/**
	 * A list containing all of the options for Group Search
	 */
	public static List<String> OPTIONS; 
	
	/**
	 * Construct a static instance of the options list
	 */
	static {
		OPTIONS = new ArrayList<String>();
		OPTIONS.add(PARTIAL_OPTION);
		OPTIONS.add(EXACT_OPTION);
		OPTIONS.add(START_OPTION);
		OPTIONS.add(END_OPTION);
	}
	
	/**
	 * Gets a list of options for the Group Search
	 * @return a list of string representing the Group Search options
	 */
	public static List<String> getOptionsList() {
		return OPTIONS;
	}
}