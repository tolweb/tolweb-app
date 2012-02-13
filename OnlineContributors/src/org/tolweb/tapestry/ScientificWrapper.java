package org.tolweb.tapestry;

public abstract class ScientificWrapper extends Wrapper {
	public String getStandardIncludes() {
		String standardIncludes = super.getStandardIncludes();
		standardIncludes += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/tree/css/tol.css\" />";		
		// add some other head stuff in here
		standardIncludes += "<style type=\"text/css\" media=\"all\">\n";
		standardIncludes += "<!--\n";
		standardIncludes += "body	{\n";
		standardIncludes += "	background-image: url(/tree/img/CoreGlobeBg.gif);\n";
		standardIncludes += "	}\n";
		standardIncludes += "-->\n";
		standardIncludes += "</style>\n";
		standardIncludes += "<style type=\"text/css\" media=\"print\">\n";
		standardIncludes += "<!--\n";
		standardIncludes += "body	{\n";
		standardIncludes += "	background-image: none;\n";
		standardIncludes += "	}\n";
		standardIncludes += "-->\n";
		standardIncludes += "</style>\n";
		return standardIncludes;
	}
}
