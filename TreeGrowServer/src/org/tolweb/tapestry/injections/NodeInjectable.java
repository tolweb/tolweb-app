package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.FilteredNodeDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.NodePusher;
import org.tolweb.treegrow.main.XMLConstants;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

public interface NodeInjectable {
    @InjectObject("spring:nodeDAO")
    public abstract NodeDAO getMiscNodeDAO();
    @InjectObject("spring:workingNodeDAO")
    public abstract NodeDAO getWorkingNodeDAO();
    @InjectObject("spring:publicNodeDAO")
    public abstract NodeDAO getPublicNodeDAO();
    @InjectObject("spring:nodePusher")
    public abstract NodePusher getNodePusher();
    @InjectObject("spring:workingFilteredNodeDAO")
    public abstract FilteredNodeDAO getWorkingFilteredNodeDAO();
}
