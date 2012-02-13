package org.tolweb.misc;

import junit.framework.TestCase;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolweb.content.helpers.PageContentElements;
//import org.tolweb.text.SpecialCharacterEscapeUtil;
//import org.tolweb.text.Symbols;

public class StringEscapeTest extends TestCase {
	public static final String NS = "http://www.eol.org/transfer/content/0.2";
	
	public static final String ESCAPED_SAMPLE1 = "&lt;p&gt;&lt;em&gt;" +
			"Notholaena ochracea&lt;/em&gt; has vibrant orange farina, a " +
			"feature that is otherwise only seen in one or two species of " +
			"African &lt;em&gt;Cheilanthes&lt;/em&gt; (&lt;em&gt;C. " +
			"welwitschii; C. mossambicensis&lt;/em&gt;&amp;mdash;in the " +
			"hemionitids) and in the rare scaly &lt;em&gt;Notholaena aurantiaca" +
			"&lt;/em&gt;. The leaves of &lt;em&gt;N. ochracea&lt;/em&gt; are " +
			"elliptic or lanceolate, and tend to be pinnate-pinnatifid, with " +
			"broad blunt leaf segments. This silhouette is distinctive, but " +
			"surprisingly reminiscent of the otherwise very dissimilar &lt;em" +
			"&gt;Cheiloplecton rigidum &lt;/em&gt;var&lt;em&gt;. lanceolatum." +
			"&lt;/em&gt;&lt;/p&gt;";
	public static final String ESCAPED_SAMPLE2 = "&lt;!-- ToL Image #START# " +
			"--&gt; &lt;a href=&quot;javascript: w = window.open(" +
			"'http://tolweb.org/media/7829', '7829', 'resizable,height=600," +
			"width=800,scrollbars=yes'); w.focus();&quot;&gt;&lt;img " +
			"src=&quot;http://tolweb.org/tree/ToLimages/amborella.100a.jpg&quot;" +
			"/&gt;&lt;/a&gt;&lt;!-- ToL Image #END# --&gt;&lt;/div&gt;&lt;p &gt;" +
			" Photo of &lt;em&gt;Amborella trichopoda&lt;/em&gt; (Amborellaceae; " +
			"photo &copy; Sangtae Kim).&lt;/p&gt;";
	public static final String ESCAPED_ESCAPED_SAMPLE1 = "Photos of &amp;lt;em" +
			"&amp;gt;Nuphar japonica&amp;lt;/em&amp;gt; sp. (Nymphaeaceae; " +
			"photo &amp;copy; Sangtae Kim), &amp;lt;em&amp;gt;";
	
	public void testSpecialCharacterEscaping() {
//		String test = "1995 " + Symbols.COPYRIGHT + " Andrew Lenards" + Symbols.DEGREE;
//		String after = SpecialCharacterEscapeUtil.escape(test);
//		assertFalse(after.contains(Symbols.COPYRIGHT.getChar()));
//		assertFalse(after.contains(Symbols.DEGREE.getChar()));
//		System.out.println("before: " + test);
//		System.out.println("after: " + after);
	}
	
	public void testExamples() {
		String example = "tol-media-id-34885: Tepatitlán \n"
				+ "tol-media-id-24373: España \n"
				+ "tol-media-id-7835: Río Lancanjá \n"
				+ "tol-media-id-20606: Die Vögel. Zweiter Band: "
				+ "Baumvögel, Papageien, Taubenvögel, Hühnervögel, "
				+ "Rallenvögel, Kranichvögel. \n"
				+ "tol-media-id-29830:  l’Asie, l’Afrique et l’Amerique";
		String output = StringEscapeUtils.escapeHtml(example);
		System.out.println("Example: \n" + output + '\n');
	}
	
	public void testXOMAutoEscapingText() {
//		String htmlText = "<div><h1>IMPORTANT</h1><span>1995 " + 
//		Symbols.COPYRIGHT + " Andrew Lenards</span></div>";
//		htmlText = SpecialCharacterEscapeUtil.escape(htmlText);
//		Element root = new Element(PageContentElements.ROOT_RESPONSE, NS);
//		Document doc = new Document(root);
//		Element dataObject = new Element("dataObject", NS);
//		Text txt = new Text(htmlText);
//		System.out.println("default inner-text: " + txt.getValue());
//		dataObject.appendChild(txt);
//		root.appendChild(dataObject);
//		System.out.println("xml: " + doc.toXML());
	}

	public void testUnescaping() {
		System.out.println("##############################");
		String actual = StringEscapeUtils.unescapeHtml(ESCAPED_SAMPLE1);
		
		System.out.println(actual);
		System.out.println("##############################");
		
		actual = StringEscapeUtils.unescapeHtml(ESCAPED_SAMPLE2);
		System.out.println(actual);
		System.out.println("##############################");
		
		actual = StringEscapeUtils.unescapeHtml(StringEscapeUtils.unescapeHtml(ESCAPED_ESCAPED_SAMPLE1));
		
		System.out.println(actual);
		System.out.println("##############################");

	}

}
