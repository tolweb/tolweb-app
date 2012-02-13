/*
 * UserMessageController.java
 *
 * Created on September 17, 2003, 4:18 PM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.tolweb.base.xml.BaseXMLWriter;

/**
 * Class used to store messages that are shown to the user during the running
 * of the application.  They are read in from an XML file and stored in a
 * hashtable
 */
public class UserMessageController {

    private static Hashtable messages = new Hashtable(150);

    /** Creates a new instance of UserMessageController */
    public static Hashtable buildMessageXMLFile (String fileName) {
            File file = new File(fileName);
            int defaultMsgCount = messages.size();
            int matchCount = 0;
            int mismatchCount = 0;
                                    
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    return messages;
                }
            } else {
                Element rootElmt = null;
                SAXBuilder builder = null;
                Document doc = null;
                try {
                    URL url = file.toURL();
                    builder = new SAXBuilder();
                    doc = builder.build(url);
                    rootElmt = doc.getRootElement();
                } catch(Exception e) {
                    return messages;
                }
               
                List msgList = rootElmt.getChildren(XMLConstants.MESSAGE);
                Iterator it = msgList.iterator();
                while (it.hasNext()) {
                    Element msgElem = (Element) it.next();
                    Attribute msgNameAttr = msgElem.getAttribute(XMLConstants.MESSAGENAME);
                    Element msgTextElem = msgElem.getChild(XMLConstants.MESSAGETEXT); 
                    if (messages.containsKey(msgNameAttr.getValue())) {
                        matchCount++;
                    } else {
                        mismatchCount++;
                    }
                    String text = msgTextElem.getText();
                    text = text.replace('\n', ' ');
                    text = text.replace('\r', ' ');
                    
                    messages.put(msgNameAttr.getValue(), text); // overwrite the default
                }        
            }

            if (defaultMsgCount>0  && ( mismatchCount > 0 || matchCount < defaultMsgCount)){
                //there are some new messages not represented in the file
                Element rootElmt = new Element(XMLConstants.usermessages);
                
                
                ArrayList keys = new ArrayList(messages.keySet());
                Collections.sort(keys);
                Iterator it = keys.iterator();
                while( it.hasNext() ) {
                    String name = (String )it.next();
                    String text = (String )messages.get(name); 

                    Element messageElmt = new Element(XMLConstants.MESSAGE);
                    Element msgTextElmt = new Element(XMLConstants.MESSAGETEXT);
                    messageElmt.addContent(msgTextElmt);
                    rootElmt.addContent(messageElmt);

                    messageElmt.setAttribute(XMLConstants.MESSAGENAME, name );
                    msgTextElmt.addContent(new CDATA(text));
                }
                
                try {
                    Document outDoc = new Document(rootElmt);
                    FileOutputStream out = new FileOutputStream(file);
                    XMLOutputter serializer = BaseXMLWriter.getXMLOutputter();
                    serializer.output(outDoc, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    System.out.println("problem with xml - C");
                    e.printStackTrace();
                    return messages;
                }                
            }
            return messages;
    }
    
    
    /**
     * Anything in this hashtable will get written out to the XML file if 
     * there is not already an entry for it.  Can be used to update instances
     * of the application out in the wild with revised or new error messages
     */
    static {
    }
}
