package org.tolweb.treegrow.page;

import java.io.Serializable;

/**
 * CopyRight is a class that holds the CopyRight information of the page.
 * <p>
 * This class hold the following details:
 * <ul>
 * <li>	CopyRight date
 * <li>	CopyRight holder
 * </ul>
 *
 */   
public class Copyright implements Serializable, AuxiliaryChangedFromServerProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4987562989781657323L;

	/**
	* Holds the copyright date details
	*/
	protected String m_Date = null;
	
	/**
	* Holds the copyright holder details
	*/
	protected String m_Holder = null;
	
	/**
	* Holds the date on which the author last changed content significantly
	*/
	protected String contentChangedDate = null;
        
	/**
	* Holds the date when the page went online first
	*/
	protected String m_FirstOnline = null;        
        
        private boolean changed;
        
        private Page page;

	/**
	* empty constructor.
	*/
	public Copyright(Page p)
	{
            page = p;
            contentChangedDate = "0000-00-00";
            m_FirstOnline = "0000-00-00";
	}

	/**
	* set the copyright details
	*
	* @param date	CopyRight date
	*	 holder	CopyRight holder
	* @return
	*/
	public void setCopyright(String Date, String Holder)
	{
		m_Date = Date;
		m_Holder = Holder;
	}
	
	/**
	* Returns the CopyRight date.
	*
	* @param
	* @return	copyright date
	*/
	public String getDate()
	{
		return m_Date;
	}
	
	/**
	* Returns the CopyRight holder.
	*
	* @param
	* @return	copyright holder
	*/
	public String getHolder()
	{
		return m_Holder;
	}
	
	/**
	* set the CopyRight date.
	*
	* @param date	copyright date
	* @return
	*/
	public void setDate(String date)
	{
		m_Date = date;
	}
	
	/**
	* set the CopyRight holder.
	*
	* @param holder	copyright holder
	* @return
	*/
	public void setHolder(String holder)
	{
		m_Holder = holder;
	}
	
        
	/**
	* gets the contentChanged date for this page
	*
	* @param
	* @return	the date on which content was changed
	*/
	public String getContentChangedDate()
	{
		return contentChangedDate;
	}
	
	/**
	* sets the contentChanged date for this page
	*
	* @param contentChangedDate the date on which content is changed
	* @return
	*/
	public void setContentChangedDate(String changedDate)
	{
		contentChangedDate = changedDate;
	}
        
	/**
	* gets information regarding when this page was firstonline
	*
	* @param
	* @return	when this page went online
	*/
	public String getFirstOnline() 	{
		return m_FirstOnline;
	}
	
	/**
	* sets information regarding when this page was firstonline
	*
	* @param online	when this page went online first
	* @return
	*/
	public void setFirstOnline(String online)
	{
		m_FirstOnline = online;
	}        
        
        /**
         * Returns whether the containing page thinks its copyright has changed
         *
         * @return Whether the containing page thinks its copyright has changed
         */
        public boolean auxiliaryChangedFromServer() {
            return page.getCopyrightChanged();
        }
        
        public boolean changedFromServer() {
            return changed;
        }
        
        public void setAuxiliaryChangedFromServer(boolean value) {
            page.setCopyrightChanged(value);
        }
        
        public void setChangedFromServer(boolean value) {
            changed = value;
        }
        
}
