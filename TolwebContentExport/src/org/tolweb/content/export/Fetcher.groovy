package org.tolweb.content.export

import java.io.InputStream
import java.io.IOException

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.HttpException
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.params.HttpMethodParams

class Fetcher {
	HostInfo hostInfo;
	ServicesInfo servicesInfo;

	String getTreeStructureUrl(Long nodeId) {
		String.format(hostInfo.toString() + servicesInfo.treeServiceUrl, nodeId)
	}
	
	String getContentUrl(Long nodeId, String licenseCode) {
		String.format(hostInfo.toString() + servicesInfo.contentServiceUrl, licenseCode, nodeId)
	}
	
	private String coreFetch(String url) {
		HttpClient client = new HttpClient()
		
		GetMethod method = new GetMethod(url)

	    method.params.setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(5, false))
		
		try {
			int statusCode = client.executeMethod(method)
			
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("Method call failed: " + method.statusLine)
				throw new HttpException("Calling ${url} via HTTP GET results in an unexpected status code: ${statusCode}")
			}
			
			return slurp(method.responseBodyAsStream)
		} catch (HttpException httpEx) {
			System.out.println("Fatal protocol violation: " + httpEx.message)
			httpEx.printStackTrace()
			return null
		} catch (IOException ioe) {
		    System.err.println("Fatal transport error: " + ioe.message)
		    ioe.printStackTrace();
		    return null
		} finally {
			method.releaseConnection()
		}		
	}
	
	String fetchTreeStructure(Long nodeId) {
		coreFetch(getTreeStructureUrl(nodeId))
	}
	
	String fetchContent(Long nodeId, String licenseCode) {
		coreFetch(getContentUrl(nodeId, licenseCode))
	}
	
	public static String slurp(InputStream input) throws IOException {
		StringBuilder out = new StringBuilder();
	    byte[] b = new byte[4096];
	    for (int n; (n = input.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    out.toString();
	}
}