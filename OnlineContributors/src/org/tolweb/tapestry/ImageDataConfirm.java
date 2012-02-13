/*
 * Created on Jun 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.dao.ImageDAO;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ImageDataConfirm extends AbstractImageContributorPage implements UserInjectable, ImageInjectable {
	public abstract void setImage(NodeImage value);
	public abstract NodeImage getImage();
	public abstract void setMediaType(String value);
	@Persist("client")
	public abstract String getMediaType();
	public abstract void setFromPageEditing(boolean value);
	@Persist("client")
	public abstract boolean getFromPageEditing();
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(ImageDataConfirm.class);
	}
	
    public Long getEditedObjectId() {
        NodeImage image = getImage();
        return image != null ? Long.valueOf(image.getId()) : null;
    }
    
    public void setEditedObjectId(Long id) {
        if (id != null) {
            NodeImage image = getImageDAO().getImageWithId(id.intValue());
            if (image == null) {
                setImage(null);
                throw new PageRedirectException(this);
            }
            setImage(image);
        } else {
            setImage(new NodeImage());
        }
    }	
    
    @SuppressWarnings("unchecked")
	public void saveImage(IRequestCycle cycle) {
		ImageDAO dao = getImageDAO();
		NodeImage img = getImage();
		img.setCheckedOut(false);
		img.setOnlineCheckedOut(false);
		img.setCheckedOutContributor(null);
		img.setCheckoutDate(null);
		img.setIsUnapproved(false);
		dao.saveImage(getImage());
		if (logger.isDebugEnabled()) {
			ArrayList list = new ArrayList();
			Iterator it = img.getNodesSet().iterator();
			while (it.hasNext()) {
				Node next = (Node) it.next();
				list.add(next.getName());
			}
			String nodes= StringUtils.returnCommaAndJoinedString(list);			
			logger.debug("Contributor " + getContributor().getName() + " just saved image: " + 
				img.getLocation() + " with id: " + img.getId() + " attached to nodes: " + nodes);
		}
		IPage savedPage = cycle.getPage("ImageDataSaved");
		String mediaType = getMediaType();
		PropertyUtils.write(savedPage, "mediaType", mediaType);
		cycle.activate(savedPage);
	}
	
	public void editImage(IRequestCycle cycle) {
		EditImageData editPage = (EditImageData) cycle.getPage(getReturnPageName());
		if (getImage() != null && !getImage().getIsUnapproved()) {
			editPage.setImage(getImage());
		}
		editPage.setFromPageEditing(getFromPageEditing());
		cycle.activate(editPage);
	}
}
