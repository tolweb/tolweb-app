package org.tolweb.tapestry;

import java.net.URL;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class HelpMessagesPage extends BasePage implements IExternalPage {
	@SuppressWarnings("unchecked")
	private TreeMap messageMap;
	public static final String ASSET_NAME = "messagefile";
	public static final String MESSAGE_NAME = "message";
	public static final String TEXT_NAME = "text";
	public static final String NAME_NAME = "name";
	public static final String ORDER = "order";
	
	public abstract void setSelectedHelpTopic(String value);
	public abstract String getSelectedHelpTopic();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String name = (String) parameters[0];
		if (name != null) {
			setSelectedHelpTopic(name);
		}
	}
	
	@SuppressWarnings("unchecked")
	public TreeMap getMessageMap() {
		if (messageMap == null) {
			messageMap = new TreeMap();
			try {
				IAsset messages = getAsset(ASSET_NAME);
				URL xmlUrl = messages.getResourceLocation().getResourceURL();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(xmlUrl);
				Element root = doc.getRootElement();
				Iterator it = root.elementIterator(MESSAGE_NAME);
				int i = 0;
				while (it.hasNext()) {
					Element nextElement = (Element) it.next();
					String name = nextElement.attributeValue(NAME_NAME);
					messageMap.put(new HelpMessageKey(name, i), nextElement.element(TEXT_NAME).getTextTrim());
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Cant parse XML message file");
				//System.exit(-1);
			}
		}
		return messageMap;
	}
	
	@SuppressWarnings("unchecked")
	public String getHelpContentForSelectedTopic() {
		Iterator it = getMessageMap().keySet().iterator();
		while (it.hasNext()) {
			HelpMessageKey key = (HelpMessageKey) it.next();
			if (key.getKey().equals(getSelectedHelpTopic())) {
				return (String) getMessageMap().get(key);
			}
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private static class HelpMessageKey implements Comparable {
		private int order;
		private String key;
		
		public HelpMessageKey(String key, int order) {
			this.key = key;
			this.order = order;
		}
		
		
		/**
		 * @return Returns the key.
		 */
		public String getKey() {
			return key;
		}
		/**
		 * @param key The key to set.
		 */
		public void setKey(String key) {
			this.key = key;
		}
		/**
		 * @return Returns the order.
		 */
		public int getOrder() {
			return order;
		}
		/**
		 * @param order The order to set.
		 */
		public void setOrder(int order) {
			this.order = order;
		}
		
		public int compareTo(Object o) {
			HelpMessageKey other = (HelpMessageKey) o;
			if (other.getOrder() < getOrder()) {
				return 1;
			} else if (other.getOrder() == getOrder()) {
				return 0;
			} else {
				return -1;
			}
		}
		
		@Override
		public int hashCode() {
			return Integer.valueOf(getOrder()).hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return this.compareTo(obj) == 0;
		}
	}
}
