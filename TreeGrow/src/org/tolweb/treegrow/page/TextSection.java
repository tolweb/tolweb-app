package org.tolweb.treegrow.page;

import java.io.*;
import java.util.*;
import org.tolweb.treegrow.main.*;

/**
 * TextSection is a class that can hold information regarding the contents of a page
 * <p>
 * This class hold the following details.
 * <ul>
 * <li>	Heading
 * <li>	Text
 * <li> Order   - information as to the order in which this author is displayed on the page
 * </ul>
 */
public class TextSection extends OrderedObject implements AuxiliaryChangedFromServerProvider, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6950283523169936542L;
	public static final String INTRODUCTION = "Introduction";
    public static final String CHARACTERISTICS = "Characteristics";
    public static final String DISCUSSION = "Discussion of Phylogenetic Relationships";
    
    private static ArrayList immutableNames;
    
    static {
        immutableNames = new ArrayList();
        immutableNames.add(INTRODUCTION);
        immutableNames.add(CHARACTERISTICS);
        immutableNames.add(DISCUSSION);
    }
    
    /**
     * Returns the list of TextSection names that cannot be modified
     *
     * @return An ArrayList containing Strings that cannot be changed
     */
    public static ArrayList getImmutableNames() {
        return immutableNames;
    }
    
	/**
	* Holds the heading of this text section
	*/
	protected String m_Heading = null;

	/**
	* Holds the text of this text section
	*/
	protected String m_Text = null;

	/**
	* Holds information as to whether the details of this text section has been changed or not.
	* true  - change has been made.
	* false - no change has been made.
	*/
	protected boolean m_Changed = false;
        
    /**
     * The page this text section is associated with
     */
    protected Page page;

	/**
	* Constructor for this class.
	*
	* @param heading	heading for this text section
	*	 text		text of this text section
	*/
	public TextSection(String heading, String text, Page p) {
		m_Heading = heading;
		m_Text = text;
		page = p;
	}

	/**
	* Constructor for this class.
	*
	* @param heading	heading for this text section
	*	 text		text of this text section
	*/
	public TextSection(String heading, String text)
	{
            this(heading, text, null);
	}

	/**
	* empty constructor.
	*/
	public TextSection(Page p) {
            page = p;
            m_Heading = "<<< New Text Section >>>";
	}
	
	public TextSection() {
	    this(null);
	}

	/**
	* sets the values of this text section
	*
	* @param heading	heading for this text section
	*	 text		text of this text section
	* @return
	*/
	public void setTextSection(String Heading, String Text)
	{
		m_Heading = Heading;
		m_Text = Text;
	}
	
	/**
	* sets the text of this text section
	*
	* @param text		text of this text section
	* @return
	*/
	public void setText(String text)
	{
		m_Text = text;

	}

	/**
	* gets the heading of this text section
	*
	* @hibernate.property column="section_title"
	* @param
	* @return	heading of this text section
	*/
	public String getHeading()
	{
		return m_Heading;
	}

	/**
	* sets the heading of this text section
	*
	* @param heading	heading for this text section
	* @return
	*/
	public void setHeading(String Heading)
	{
		m_Heading = Heading;
	}

	/**
	* gets the text of this text section
	*
	* @hibernate.property column="section_text"
	* @param
	* @return	text of this text section
	*/
	public String getText()
	{
		return m_Text;
	}

	/**
	* set the value as to whether this text section was changed.
	* This function is called when any one of the text detail is changed.
	*
	* @param value	whether this text section was changed.
	* @return
	*/
	public void setChangedFromServer(boolean value) {
		m_Changed = value;
	}
	
	/**
	* get the value as to whether this text section was changed.
	*
	* @param
	* @return	whether this text section was changed
	*/
	public boolean changedFromServer()
	{
		return m_Changed;
	}
        
        /**
         * Returns whether the page thinks its text sections have changed
         *
         * @return Whether the page thinks its text sections have changed
         */
        public boolean auxiliaryChangedFromServer() {
            return page.getListChanged();
        }
        
        /**
         * Sets the page to think its text sections have changed (or not)
         *
         * @param value The new value for the page's text sections changed
         */
        public void setAuxiliaryChangedFromServer(boolean value) {
            page.setListChanged(value);
        }
}
