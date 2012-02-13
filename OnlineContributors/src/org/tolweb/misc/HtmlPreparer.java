package org.tolweb.misc;

import java.io.CharArrayReader;

import org.apache.html.dom.HTMLAnchorElementImpl;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLLIElementImpl;
import org.apache.html.dom.HTMLUListElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.jdom.Element;
import org.tolweb.content.helpers.PageContentElements;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

public class HtmlPreparer {

	public static Element parseHtmlList(String rawHtml) {
		CharArrayReader charReader = new CharArrayReader(rawHtml.toCharArray());
		InputSource source = new InputSource(charReader);
		HTMLDocument doc = new HTMLDocumentImpl();
		DOMFragmentParser parser = new DOMFragmentParser();
		DocumentFragment fragment = doc.createDocumentFragment();
		try {
		parser.parse(source, fragment);
		} catch (Exception e) {
			return new Element(PageContentElements.ROOT_ERROR);
		}
		Element test = new Element("test");
		print(fragment, test);
		
		return test;
	}
	
    public static void print(Node node, Element root) {
        //System.out.println(indent+node.getClass().getName());
    	//String typeName = node.getClass().getName();
    	Element curr = null;
    	if (HTMLUListElementImpl.class.isInstance(node)) {
    		Element link = new Element("list");
    		root.addContent(link);
    		curr = link;
    	} else if (HTMLLIElementImpl.class.isInstance(node)) {
    		Element link = new Element("link");
    		root.addContent(link);
    		curr = link;
    	} else if (HTMLAnchorElementImpl.class.isInstance(node)) {
    		HTMLAnchorElementImpl ahref = (HTMLAnchorElementImpl)node;
    		root.setAttribute("link", ahref.getHref());
    		Node linkText = node.getNextSibling();
    		if (linkText != null) {
    			TextImpl txt = (TextImpl)linkText;
    			root.setAttribute("link-text", txt.getData());
    		}
    	} else if (TextImpl.class.isInstance(node)) {
    		TextImpl txt = (TextImpl)node;
    		root.setText(txt.toString());
    	} 
    	
    	if (curr == null){ 
    		curr = root;
    	}
    	
        Node child = node.getFirstChild();
        while (child != null) {
            print(child, curr);
            child = child.getNextSibling();
        }
    }	
/*
 org.apache.html.dom.HTMLUListElementImpl
  org.apache.html.dom.HTMLLIElementImpl
   org.apache.html.dom.HTMLAnchorElementImpl
    org.apache.xerces.dom.TextImpl
   org.apache.xerces.dom.TextImpl
 */    
}
