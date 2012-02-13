package org.tolweb.misc;

import java.util.StringTokenizer;

/**
 * Found at:
 * http://www.gjt.org/servlets/JCVSlet/show/gjt/com/tinyplanet/docwiz/PeekStringTokenizer.java/1.3
 * 
 * @author dmandel
 * 
 */
public class PeekStringTokenizer extends StringTokenizer {
	protected String _nextToken = null;

	public PeekStringTokenizer(String str, String delimiters) {
		super(str, delimiters);
		initNextToken();
	}

	public PeekStringTokenizer(String str) {
		super(str);
		initNextToken();
	}

	private void initNextToken() {
		if (super.hasMoreTokens()) {
			_nextToken = super.nextToken();
		} else {
			_nextToken = null;
		}
	}

	public String nextToken() {
		String previousToken = _nextToken;
		initNextToken();
		return previousToken;
	}

	public boolean hasMoreTokens() {
		return super.hasMoreTokens() || _nextToken != null;
	}

	public String peek() {
		return _nextToken;
	}

	public static void main(String[] args) {
		PeekStringTokenizer test = new PeekStringTokenizer("This is a test");
		System.out.println("peeking: " + test.peek());
		while (test.hasMoreTokens()) {
			System.out.println("next token is: " + test.nextToken());
		}
	}
}
