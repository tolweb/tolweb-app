package org.tolweb.content.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.tolweb.dao.ApplicationContextTestAbstract;


public class ToLImgRemovalTest extends ApplicationContextTestAbstract {
	public static final int[] NODE_IDS = new int[] { 
		21997, 19418, 72904, 19557, 19833, 21994, 19934, 19420, 19750,
		19925, 19925, 19522, 72941, 27685, 19954, 19782, 19614, 21990		
	};
	
	public static final String PAGE_SERVICE = "http://tolweb.org/webservices/pagecontent/nc/";
	
	public ToLImgRemovalTest(String name) {
		super(name);
	}
	
	public void testTagRemoval() {
		for (int i : NODE_IDS) {
			System.out.print("node-id:" + i + " \t");
			try {
				String response = FetchURL.fetch(PAGE_SERVICE + i);
				response = response.toLowerCase();
				System.out.print("response length=" + response.length());
				if (response.contains("tolimg")) {
					System.out.println("ToLimg tag still exists in node-id: " + i);
				}
				if (response.indexOf("tolimg") != -1) {
					System.out.println("ToLimg tag still exists in node-id: " + i + " indexOf");
				}
				if (response.contains("tolweb.org")) {
					System.out.print("\t - sanity check passed");
				}
				if (i == 21997 || i == 21994) {
					//System.out.println(response);
				}
			} catch (Exception e) {
				System.out.println("Exception occurred re node-id: " + i);
				System.out.println(e.toString());
			}
			System.out.println();
		}
	}
}

class FetchURL {

  /**
    * Get the contents of a URL and return it as a string.
    */
  public static String fetch(String address) throws MalformedURLException, IOException {
    URL url = new URL(address);
    URLConnection connection = url.openConnection();
    return slurp(connection.getInputStream());  
  }

	public static String slurp (InputStream in) throws IOException {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}
}