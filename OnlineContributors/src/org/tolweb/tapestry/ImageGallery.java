package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.ITableModel;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.ImageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ImageGallery extends AbstractBranchOrLeafPage implements IExternalPage, PageBeginRenderListener, 
		NodeInjectable, ImageInjectable {
	public abstract void setNodeId(Long value);
	@Persist("client")
	public abstract int getMediaClassInt();
	public abstract void setMediaClassInt(int value);
	@Persist("client")
	public abstract Long getNodeId();
	public abstract Object getImage();
	public abstract int getIndex();
	public abstract void setParentPageNodeId(Long value);
	public abstract void setParentPageName(String value);
	@Persist("client")
	public abstract boolean getShowRandom();
	@Persist("client")
	public abstract boolean getShowOnlyTillus();
	@SuppressWarnings("unchecked")
	public abstract void setCurrentTableRows(Iterator value);
	@SuppressWarnings("unchecked")
	public abstract Iterator getCurrentTableRows();
	public abstract void setHasMore(boolean value);
	public abstract boolean getHasMore();
	private static final int IMAGES_PER_ROW = 4;
	private ImageGalleryTableModel tableModel;
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		getTableView().reset();
		setupRequestCycleAttributes();
		if (parameters == null || parameters.length == 0) {
			gotoErrorPage();
			return;
		}
		Long nodeId = ((Number) parameters[0]).longValue();
		setNodeId(nodeId);
		setupNodeFromNodeId();		
		if (getNode() == null || getTolPage() == null) {
			gotoErrorPage();
		}
		if (parameters.length > 1) {
			setMediaClassInt(((Number) parameters[1]).intValue());
		}
		setupParentPageProperties();
	}
	public void pageBeginRender(PageEvent event) {
		super.pageBeginRender(event);
		setupNodeFromNodeId();
		if (!event.getRequestCycle().isRewinding()) {
			getImagesToShow();			
		}
	}
	private void setupNodeFromNodeId() {
		MappedNode node = getNodeDAO().getNodeWithId(getNodeId());
		if (node == null) {
			// prevent npes.  if for any reason we don't have a node id, choose life
			node = getNodeDAO().getRootNode();
		}
		setNode(node);	
		setTolPage(getPageDAO().getPage(node));		
	}

	public void reloadImages(IRequestCycle cycle) {
		setupParentPageProperties();
	}
	private void setupParentPageProperties() {
		Long parentPageId = getPageDAO().getParentPageIdForPage(getTolPage().getPageId());
		setParentPageNodeId(getPageDAO().getNodeIdForPage(parentPageId));
		setParentPageName(getPageDAO().getGroupNameForPage(parentPageId));	
	}
	public String getTitle() {
		return getGroupName() + " " + getCapitalizedMediaType();
	}
	public String getMediaType() {
		return NodeImage.getWordForMediaType(getMediaClassInt()) + "s";
	}
	public String getCapitalizedMediaType() {
		return StringUtils.capitalizeString(getMediaType());
	}
	public boolean getNoImage() {
		return getCurrentTableRows() == null || !getCurrentTableRows().hasNext();
	}
	public boolean getShowMoreLink() {
		return getCurrentTableRows() != null && getHasMore();
	}
	public String getOpenTableRow() {
		return getIndex() % IMAGES_PER_ROW == 0 ? "<tr>" : null;
	}
	public String getCloseTableRow() {
		return (getIndex() % IMAGES_PER_ROW == IMAGES_PER_ROW - 1 || getIsLast()) ? "</tr>" : null;
	}
	public String getCaptionString() {
		NodeImage image = getCurrentImageToShow();		
		if (getMediaClassInt() == NodeImage.MOVIE) {
			return image.getTitle();
		} else {
			String sciName = image.getScientificName();
			if (StringUtils.notEmpty(sciName)) {
				return sciName;
			} else if (image.getNodesSet().size() > 0) {
				return ((MappedNode) image.getNodesSet().iterator().next()).getName();
			} else {
				return "";
			}			
		}
	}
	public String getCopyrightString() {
		return getTextPreparer().getCopyrightOwnerString(getCurrentImageToShow(), false, false, false);
	}
	public ImageGalleryTableModel getModel() {
		if (tableModel == null) {
			tableModel = new ImageGalleryTableModel();
		}
		return tableModel;
	}
	private boolean getIsLast() {
		return !getCurrentTableRows().hasNext();
	}
	public ITableModel getTableModel() {
		return getTableView().getTableModel();
	}
	public String getImagesTillusString() {
		if (getShowOnlyTillus()) {
			return "title illustrations";
		} else {
			return getCapitalizedMediaType();
		}
	}
	@SuppressWarnings("unchecked")
	public Iterator getImagesToShow() {
		Iterator it;
		if (getShowOnlyTillus()) {
			List<Object[]> images;
			if (getShowRandom()) {
				images = getPageDAO().getRandomPicsForImageGallery(getTolPage(), ImageDAO.IMAGE_GALLERY_SIZE);
				setHasMore(images.size() == ImageDAO.IMAGE_GALLERY_SIZE);				
				it = images.iterator();
			} else {
				it = getTableModel().getCurrentPageRows();
			}			
		} else {
			if (getShowRandom()) {
				List randomImages = getImageDAO().getRandomGalleryImagesForNode(getNode(), getMediaClassInt());
				setHasMore(randomImages.size() == ImageDAO.IMAGE_GALLERY_SIZE);
				it = randomImages.iterator();
			} else {
				it = getTableModel().getCurrentPageRows();
			}			
		}
		setCurrentTableRows(it);
		return it;
	}
	public NodeImage getCurrentImageToShow() {
		if (getShowOnlyTillus()) {
			Object[] imageInfo = (Object[]) getImage();
			return (NodeImage) imageInfo[0];
		} else {
			return (NodeImage) getImage();
		}
	}
	public String getTillusGroupName() {
		return (String) ((Object[]) getImage())[1];
	}
	public Long getTillusNodeId() {
		return (Long) ((Object[]) getImage())[2];
	}
	public TableView getTableView() {
		return ((TableView) getComponent("tableView"));
	}
	private class ImageGalleryTableModel implements IBasicTableModel {
		public int getRowCount() {
			int numImages = 0;
			if (getShowOnlyTillus()) {
				numImages = getPageDAO().getNumPicsForImageGallery(getTolPage());
			} else {
				numImages = getImageDAO().getNumGalleryImagesForNode(getNode(), getMediaClassInt());
			}
			return numImages;
		}
		@SuppressWarnings("unchecked")
		public Iterator getCurrentPageRows(int nFirst, int nPageSize, ITableColumn objSortColumn, boolean bSortOrder) {
			if (getShowOnlyTillus()) {
				return getPageDAO().getPicsForImageGallery(getTolPage(), ImageDAO.IMAGE_GALLERY_SIZE, nFirst).iterator();
			} else {
				return getImageDAO().getGalleryImagesForNode(getNode(), nFirst, getMediaClassInt()).iterator();				
			}
		}
		
	}
	public boolean getIsImages() {
		return getMediaClassInt() == NodeImage.IMAGE;
	}
	public boolean getIsMovies() {
		return getMediaClassInt() == NodeImage.MOVIE;
	}
}
