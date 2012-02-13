package org.tolweb.tapestry.xml.taxaimport;

public class UnsupportedTaxaImportFormatVersionException extends Exception {
	/** */
	private static final long serialVersionUID = -7549503168721159404L;
	
	public UnsupportedTaxaImportFormatVersionException() {
		super("The specified version is not a supported Taxa Import format");
	}
	public UnsupportedTaxaImportFormatVersionException(String message) {
		super(message);
	}
}
