package org.tolweb.treegrow.main;

import com.Ostermiller.util.Browser;


/**
 * class to open the browser in a seperate thread.
 */
public class OpenBrowser extends Thread
{
	String m_Path = null;
	
        /**
         * Creates a new openBrowser that will point the browser at path
         *
         * @param path The path to point the browser to
         */
	public OpenBrowser(String path)
	{
		m_Path = path;
	}
	
	public void run()
	{
		try
		{
			if (m_Path != null)
			{
				try
				{
                                        Browser.displayURL(m_Path);
					//LaunchBrowser.openURL(m_Path);
				}
				catch (Exception e)
				{
					//browserString = null;
					String error = new String("The requested page "
						+ "could not be shown, because the web browser could not be used properly. "
						+ "There may be a problem with insufficient memory or the location of the "
						+ "web page or browser." );
				}
			}
		}
		catch(Exception err)
		{
		}
	}
}
