package org.tolweb.treegrow.main;

import java.net.*;
import java.io.*;

/**
 * Reads an image from a url and saves it as a local file
 */
public class ReadImage
{
	protected String m_ImageString = null;
	protected String m_FileName = null;

        /**
         * Constructs the ReadImage object
         *
         * @param str The url where the image is located
         * @param file The local file to write the image to
         */
        public ReadImage(String str, String file)
	{
		m_ImageString = str;
		m_FileName = file;
	}
	
	public int read()
	{
		URL url = null;
		try
		{
			url = new URL(m_ImageString);
			int success = saveImage(url);
			return success;
		}
		catch (MalformedURLException e1)
		{
			return -1;
		}
	}
	
	public int read(String htpath)
	{
		URL url = null;
		try
		{
			url = new URL(htpath+m_ImageString);
			int success = saveImage(url);
			return success;
		}
		catch (MalformedURLException e1)
		{
			return -1;
		}
	}


	private int saveImage(URL url)
	{
		String fileName = m_FileName;

		FileOutputStream out = null;
		InputStream is = null;

		try 
		{
			out = new FileOutputStream(fileName);
		} 
		catch (Exception e)
		{
			return -1;
		}

		try
		{
			//is = url.openStream();
                    byte[] bytes = HttpRequestMaker.makeHttpRequest(url.toString());
                    if (bytes == null) {
                        return -1;
                    }
                    out.write(bytes);
                    out.flush();
                    out.close();
		}
		catch (IOException e)
		{
			return -1;
		}
                return 1;
	}
}
