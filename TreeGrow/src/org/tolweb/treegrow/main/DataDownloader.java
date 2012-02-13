package org.tolweb.treegrow.main;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import org.tolweb.treegrow.tree.*;

/**
 * Responsible for starting assorted threads and doing the http
 * requests that download the various parts of data that make up the tree
 */
public class DataDownloader {
    private static final String SERVER_PAGE_NAME = "TreeXML";
	private int activeThreads = 0; 
	private int maxThreads = 8; 
	protected Stack failedThreadsStack = new Stack();
	protected Hashtable failedThreadsHashtable = new Hashtable();
	
	protected float downloadPercentage = 0;
	protected float fileIncrementPercentage = 0;
	protected float imageIncrementPercentage = 0;
	protected String downloadStatus = null;
	protected boolean fromSearchWindow; 	//false = from the process of openening an old copy of a file
	protected boolean done;
	protected boolean restartDownload;
	private boolean cancelDownload;
	private boolean dontCheckInOnCancel;
	protected boolean errorPopped;
	// The new thread group is used to store all of the worker threads who
	// will download information about terminal nodes.  Create a new thread
	// group so that we can easily kill them if an unrecoverable error occurs.
	protected ThreadGroup newThreadGroup;
	protected NodeDetails tempDetails;
	
	private ArrayList allNodes;
	private ArrayList lockedNodes = new ArrayList();
	private int nodeID;
	private int errorNum;
	private String batchId;
	
	private Node oldSubtreeRoot;
	private Node newSubtreeRoot;
	private ArrayList nodesToFetch;
	
	private boolean isSubtreeDownload;
	
	/**
	 * Used to determine whether we want to actually open the tree
	 * editor or just have it open for programmatic usage
	 */
	private boolean showTreeEditor = true;
	
	public DataDownloader(int nodeid) {
		this (nodeid,true);
	}
	
	public DataDownloader(int nodeid, boolean fromSearchWindow) {
		nodeID = nodeid;
		this.fromSearchWindow = fromSearchWindow;
	}
	
	public DataDownloader(int nodeid, String batchid, boolean fromSearchWindow) {
		nodeID = nodeid;
		batchId = batchid;
		this.fromSearchWindow = fromSearchWindow;
	}
	
	/**
	 * Starts a full download
	 */
	public void startFullDownload() {
		downloadStatus = Controller.getController().getMsgString("ST_DOWNLOADING_CONTENT");
		isSubtreeDownload = false;
		MainDownloadHomicidalThread watcher = new MainDownloadHomicidalThread();
		watcher.start();
	}
	
	/**
	 * Starts a subtree download
	 */
	public boolean startSubtreeDownload() {
		downloadStatus = Controller.getController().getMsgString("ST_DOWNLOADING_CONTENT");
		isSubtreeDownload = true;
		SubtreeDownloadHomicidalThread watcher = new SubtreeDownloadHomicidalThread();
		watcher.start();   
		try {
			watcher.join();
		} catch (Exception e) {}
		
		return done;
	}
	
	/**
	 * When called, this method prevents the Tree Editor from being popped up
	 * during a tree download
	 */
	public void hideTreeEditor() {
		showTreeEditor = false;
	}
	
	/**
	 * Causes the file manager to not check in when the cancel button is
	 * pressed
	 */
	public void dontCheckInOnCancel() {
		dontCheckInOnCancel = true;
	}
	
	
	/**
	 * Controller thread class that runs in the background an makes sure that
	 * things are running smoothly.  If one of the threads encounters a problem,
	 * it sets a flag variable in the class so the MainDownloadHomicidalThread knows
	 * to destroy all of the currently running download threads and restart the
	 * process.
	 */
	final class MainDownloadHomicidalThread extends Thread {
		public void run() {
			Controller controller = Controller.getController();
			controller.setDownloadComplete(false);
			initFullThreadGroup();
			while (!done) {
				if (restartDownload) {
					// Save this since calling dispose usually erases this information
					String editorBatchId = controller.getEditorBatchId();
					String filename = controller.getFileName();
					stopThreads();
					if (controller.getTreeEditor() != null) {
						// Do this so we get rid of the singleton TreePanel and
						// other singleton variables that the TreeFrame works with
						controller.getTreeEditor().dispose();
					}
					controller.setEditorBatchId(editorBatchId);
					controller.setFileName(filename);
					controller.getFileManager().checkIn();
					initFullThreadGroup();
					restartDownload = false;
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				
			}
			stopThreads();
		}
	}    
	
	/**
	 * Creates the thread group that all threads created during a full download
	 * will belong to
	 */
	private void initFullThreadGroup() {
		activeThreads = 0;
		failedThreadsStack.clear();
		failedThreadsHashtable.clear();
		
		FullDownloadControllerThread downloadThread = new FullDownloadControllerThread();
		newThreadGroup = new ThreadGroup("XMLFullDownloadGroup");
		Thread actualDownloadThread = new Thread(newThreadGroup, downloadThread);
		errorPopped = false;
		actualDownloadThread.start();    
	}
	
	/**
	 * Stops all the threads that are part of the thread group created by 
	 * the download
	 */
	private void stopThreads() {
		Thread[] threads = new Thread[newThreadGroup.activeCount() + 10];
		newThreadGroup.enumerate(threads);
		for (int i = 0; i < threads.length; i++) {
			if (threads[i] != null) {
				threads[i].stop();
			}
		}    
	}
	
	/**
	 * Controller thread class that runs in the background an makes sure that
	 * things are running smoothly.  If one of the threads encounters a problem,
	 * it sets a flag variable in the class so the MainDownloadHomicidalThread knows
	 * to destroy all of the currently running download threads and restart the
	 * process.
	 */
	final class SubtreeDownloadHomicidalThread extends Thread {
		public void run() {
			Controller controller = Controller.getController();
			controller.setDownloadComplete(false);
			controller.getTreeEditor().setWaitCursors();
			initSubtreeThreadGroup();
			while (!done) {
				if (restartDownload) {
					stopThreads();
					
					initSubtreeThreadGroup();
					restartDownload = false;
				} else if (cancelDownload) {
					cancelSubtreeDownload();
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				
			}  
		}
	}            
	
	/**
	 * Creates the thread group that all threads created during a subtree 
	 * download will belong to
	 */
	private void initSubtreeThreadGroup() {
		activeThreads = 0;
		failedThreadsStack.clear();
		failedThreadsHashtable.clear();
		
		SubtreeDownloadControllerThread downloadThread = new SubtreeDownloadControllerThread();
		newThreadGroup = new ThreadGroup("XMLSubtreeDownloadGroup");
		Thread actualDownloadThread = new Thread(newThreadGroup, downloadThread);
		errorPopped = false;
		actualDownloadThread.start();    
	}
	
	/**
	 * Does the UI stuff and data structure stuff to return the tree to its
	 * previous state before the subtree download was launched
	 */
	private void cancelSubtreeDownload() {
		Controller controller = Controller.getController();
		stopThreads();
		try {
			Thread.sleep(250);
		} catch (Exception e) {}
		
		TreePanel treePanel = TreePanel.getTreePanel();
		Tree tree = treePanel.getTree();
		if (allNodes != null) {
			treePanel.removeNewNodes(allNodes);
			Iterator it = allNodes.iterator();
			while(it.hasNext()) {
				tree.removeNode((Node) it.next());
			}                
		}
		
		// Call the cancel script on the server -- this ensures
		// that the DownloadNodes table gets cleared out and that
		// the download is actually cleaned up on the server side
		try  {
			Thread.sleep(500);
			Controller.getController().getFileManager().checkInSubtree(newSubtreeRoot.getId());
			//URL url = new URL(controller.getCGIPath() + "cancelSubtreeDownload.pl?download_id=" + controller.getDownloadId() + "&root_node_id=" + newSubtreeRoot.getId());
			//new SAXBuilder().build(url);
		} catch (Exception e) {
		}
		
		if (newSubtreeRoot != null) {
			Node rootParent = newSubtreeRoot.getParent();
			int index = rootParent.indexOfChild(newSubtreeRoot);
			rootParent.removeChild(newSubtreeRoot);
			tree.removeNode(newSubtreeRoot);
			TreePanelUpdateManager.getManager().zombieNode(newSubtreeRoot);
			rootParent.addToChildren(index, oldSubtreeRoot);
			TreePanelUpdateManager.getManager().unZombieNode(oldSubtreeRoot);
			tree.addNode(oldSubtreeRoot);
		}
		
		treePanel.rebuildTree();
		treePanel.repaint();
		controller.setDownloadComplete(true);
		controller.setStatusMessage("Download Cancelled");
		
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		controller.setStatusMessage("");
		
		done = true;    
	}
	
	
	/**
	 * Checks to see if a critical error occurred during the XML download 
	 * associated with the passed-in reader.  If there was, a dialog box is
	 * presented to the user asking them if they would like to download again.
	 * If yes is chosen, then all worker threads are killed and a new download
	 * process is started.
	 *
	 * @param xmlReader The XMLReader to check an error on
	 * @return true If there was an error on the reader, false otherwise.
	 */
	public boolean checkXMLError(TreeGrowXMLReader xmlReader) {
		return checkXMLError(xmlReader, null, false);
	}
	
	/**
	 * Checks to see if a critical error occurred during the XML download 
	 * associated with the passed-in reader.  If there was, a dialog box is
	 * presented to the user asking them if they would like to download again.
	 * If yes is chosen, then all worker threads are killed and a new download
	 * process is started.
	 *
	 * @param xmlReader The XMLReader to check an error on
	 * @param popDialogOnFailure Whether to pop the dialog box
	 * @return true If there was an error on the reader, false otherwise.
	 */
	public boolean checkXMLError(TreeGrowXMLReader xmlReader, boolean popDialogOnFailure) {
		return checkXMLError(xmlReader, null, popDialogOnFailure);
	}
	
	/**
	 * Checks to see if a critical error occurred during the XML download 
	 * associated with the passed-in reader.  If there was, a dialog box is
	 * presented to the user asking them if they would like to download again.
	 * If yes is chosen, then all worker threads are killed and a new download
	 * process is started.
	 *
	 * @param xmlReader The XMLReader to check an error on
	 * @param node The node there was an error with
	 * @param popDialogOnFailure Whether to pop the dialog box
	 * @return true If there was an error on the reader, false otherwise.
	 */
	public boolean checkXMLError(TreeGrowXMLReader xmlReader, Node node, boolean popDialogOnFailure) {
		String errdesc = xmlReader.getErrorString();
		if (errdesc != null) {
			int errorNum = xmlReader.getErrorNum();
			if (node != null && (errorNum == 10102  || errorNum == 10103)  ) { //locked or submitted
				node.setLocked(true);
				node.setLockDate( xmlReader.getLockTime() ) ;
				node.setLockUser( xmlReader.getLockUser() );
				lockedNodes.add(node);
			} else {
				
				System.out.println("errdesc : " + errdesc);
				System.out.println("errNumc : " + errorNum);
				System.out.println("url: " + xmlReader.getURL());
				//System.out.println("xml: " + xmlReader.getXML());
				
				// Make this portion synchronized so that if there is more than 1
				// error (in multiple threads, for instance), only 1 message box
				// gets popped up
				if (popDialogOnFailure) {
					if (errorNum != XMLReader.LOST_CONNECTION_MIDSTREAM) {
						System.out.println("");
						offerDownloadAgain();
					} else {
						synchronized(this) {
							String LOST_CONNECTION_MIDSTREAM_DOWNLOAD = Controller.getController().getMsgString("LOST_CONNECTION_MIDSTREAM_DOWNLOAD");
							JOptionPane.showMessageDialog(null, LOST_CONNECTION_MIDSTREAM_DOWNLOAD, "Lost connection", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}
					}
				}
			}
			return true;
		}
		return false;
	}        
	
	/**
	 * Method that pops the dialog box asking the user if they want to try
	 * a new download
	 */
	private synchronized void offerDownloadAgain() {
		try {
			throw new RuntimeException();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Controller controller = Controller.getController();
		FileManager fileManager = controller.getFileManager();
		if (!errorPopped) {
			errorPopped = true;
			Object[] options = {"Yes" ,"No"};
			String DOWNLOAD_AGAIN = controller.getMsgString("DOWNLOAD_AGAIN");
			int n = JOptionPane.showOptionDialog(controller.getTreeEditor(),
					DOWNLOAD_AGAIN,
					"Download Error",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					options,
					options[1]);            
			if (n == JOptionPane.YES_OPTION) {
				// Set the flag variable to true so the assisted suicide
				// thread knows to kill the others.
				restartDownload = true;
				controller.getCancelWindow().dispose();
				//we're going to be killed...but keep truckin' on (won't hurt anything)
			} else {
				if (!isSubtreeDownload) {
					fileManager.checkIn();
					if(controller.getTreeEditor() != null) {
						controller.getTreeEditor().dispose();
						controller.getCancelWindow().dispose();
						controller.getManagerFrame().show();
					}
				} else {
					// In the subtree case, we don't want to exit the application,
					// so just call the subtree cancel function
					controller.cancelDownload();
				}
			}
		}
	}
	
	/**
	 * Actually starts gathering the data for the tree.  Starts the appropriate
	 * threads and makes requests.
	 */
	private void gatherDataForTree() {
		//System.out.println("starting actual download");
		Controller controller = Controller.getController();
		FileManager fileManager = controller.getFileManager();
		TreePanel treePanel = TreePanel.getTreePanel();
		Tree tree = treePanel.getTree();
		//System.out.println("tree = " + tree);
		downloadPercentage = 0;
		controller.setStatusMessage(downloadStatus + (int)downloadPercentage +"%");
		if(fromSearchWindow) {
			if(controller.getManagerFrame() != null) {
				controller.getManagerFrame().getDownloadPanel().setCursor(Cursor.getDefaultCursor());
			}
			//System.out.println("just set wait cursor");
		}
		
		int numRequestsNeeded = nodesToFetch.size() ;
		if(numRequestsNeeded > 0) {
			//90% of time allocated to file downloads, the other 10% to images (see imageIncrementPercentage )
			fileIncrementPercentage = (float)80/numRequestsNeeded; 
		}
		//System.out.println("figured out increment");
		Iterator it = tree.getUnCheckedOutNodes().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			TreePanel.getTreePanel().setNodeComplete(node);
		}
		//System.out.println("done setting nodes complete");*/
		if(nodesToFetch.size() > 0) {
			int tempSize = nodesToFetch.size();
			
			/*for(int i = 0; i < tempSize ; i++) {
				//System.out.println("starting loop");
				if(controller.getTreeEditor() == null) {
					fileManager.checkIn();
					done = true;
					return;
				}
				
				//This allows only maxThreads downloads to happen at once.
				//When this number is exceeded, the containing for loop goes into a 
				// spin lock, and doesn't start up a new thread until one of the 
				//earlier ones has finished
				if(activeThreads >= maxThreads) {
					i--;
					try {
						Thread.sleep(100);
					} catch (Exception e) { }
				} else {
					activeThreads++;
					Node tempNode = (Node)(nodesToFetch.get(i));
					NodeThread nodeThread = new NodeThread(tempNode);
					//System.out.println("constructing node thread for: " + tempNode.getName());
					nodeThread.start();
				}
			}
			System.out.println("spinning on threads");
			while(activeThreads != 0) {
				if(controller.getTreeEditor() == null){
					fileManager.checkIn();
					done = true;
					return;
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) { /*not bloody likely }
			}
			System.out.println("threads done");
			
			while (!failedThreadsStack.empty()) {
				System.out.println("spinning on failed thread");
				if(controller.getTreeEditor() == null) {
					fileManager.checkIn();
					done = true;
					return;
				}
				
				Node tempNode = (Node)failedThreadsStack.pop();
				
				NodeThread nodeThread = new NodeThread(tempNode);
				nodeThread.start();
				try {                            
					nodeThread.join();
				} catch (Exception e) {
					e.printStackTrace();
					offerDownloadAgain();   
				}
				
			}
			
			failedThreadsHashtable.clear();
			failedThreadsStack.clear();
			
			
			//There may have been some locked nodes. Remove their subtrees from the tree.
			it = lockedNodes.iterator();
			while ( it.hasNext() ) {
				Node node = (Node)it.next();
				Node parent = node.getParent();
				if (parent != null && parent.getLocked()) {
					parent.removeChild(node);
					NodeGraveyard.getGraveyard().addNode(node);
				}
			}*/
			treePanel.removeGrayCircles(lockedNodes);
			TreePanelUpdateManager.getManager().rebuildTreePanels(lockedNodes);
			//now, continue....
			
			//download the images now for each of the nodes.
			//create the directory
			/*File newDir = new File(fileManager.getDataPath()+ controller.getFileName());
			if(newDir.mkdir() == false) {
				//try deleting the directory. it might already be present.
				fileManager.deleteImageFiles();
				//if it already exists, just use it.
				newDir.mkdir();
				if (newDir.exists() == false || newDir.canWrite() == false) {
					fileManager.checkIn();
					
					Vector argVec = new Vector(); 
					argVec.add(newDir);
					String IMG_DIR_ERROR = controller.getMsgString("IMG_DIR_ERROR", argVec);
					JOptionPane.showMessageDialog(
							controller.getManagerFrame(),
							IMG_DIR_ERROR,
							"Message Window",
							JOptionPane.INFORMATION_MESSAGE);
					done = true;
					return;
				}
			}
			
			ArrayList imgList = getAllImages(); 
			
			if(imgList == null) {               
				offerDownloadAgain();
			}
			tempSize = imgList.size();
			if(tempSize > 0) {
				//20% of time allocated to image downloads, the other 90% to files (see fileIncrementPercentage 
				imageIncrementPercentage = (float)20/tempSize; 
			}                            
			//System.out.println("image size is: " + tempSize);
			for(int i = 0; i < tempSize ; i++) {
				if(controller.getTreeEditor() == null) {
					checkInAndDelete();
					return;
				}
				//This allows only maxThreads downloads to happen at once.
				//When this number is exceeded, the containing for loop goes into a 
				// spin lock, and doesn't start up a new thread until one of the 
				//earlier ones has finished
				if(activeThreads >= maxThreads) {
					i--;
					try {
						Thread.sleep(100);
					} catch (Exception e) { }
				}  else {
					activeThreads++;
					Object nextObject = imgList.get(i);
					ImageThread imgThread;
					if (nextObject instanceof PageImage) {
						imgThread = new ImageThread((PageImage) nextObject);
					} else {
						imgThread = new ImageThread((NodeImage) nextObject);
					}
					imgThread.start();
					//System.out.println("i is: " + i);
				}
			}
			
			while(activeThreads != 0) {
				if(controller.getTreeEditor() == null){
					checkInAndDelete();
					return;
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) { /*not bloody likely }
			}
			
			while (!failedThreadsStack.empty()){
				if(controller.getTreeEditor() == null) {
					checkInAndDelete(); 
					return;
				}
				
				Object key = failedThreadsStack.pop();
				
				ImageThread imgThread;
				if (key instanceof NodeImage) {
					imgThread = new ImageThread((NodeImage) key);
				} else {
					imgThread = new ImageThread((PageImage) key);
				}
				imgThread.start();
				try {                            
					imgThread.join();
				} catch (Exception e) {
					e.printStackTrace();
					offerDownloadAgain();   
				}   
				
				
				
			}
			*/
			//write the data received into the new file
			FileXMLWriter writer = new FileXMLWriter();
			writer.writeXML();
			
			String ST_TRANSFER_COMPLETE = controller.getMsgString("ST_TRANSFER_COMPLETE");
			controller.setStatusMessage(ST_TRANSFER_COMPLETE);
			System.out.println("setting download complete to true");
			controller.setDownloadComplete(true);
			if (!controller.getPreferenceManager().hasOpenedFile()) {
				controller.showFirstTimeUsersDialog();
				controller.getPreferenceManager().setHasOpenedFile(true);
				controller.getPreferenceManager().writePreferencesToDisk();
			}
		} else {
			System.out.println("no nodes to fetch");
		}
	}
	
	/**
	 * If there was an error during download, check the file back in and remove
	 * it from the local system
	 */
	private void checkInAndDelete() {
		Controller controller = Controller.getController();
		FileManager fileManager = controller.getFileManager();
		fileManager.checkIn();
		fileManager.deleteImageFiles();
		done = true;
		return;            
	}
	
	/**
	 * Returns the list of all images that are on page objects.  This list is
	 * used to start ImageThreads
	 *
	 * @return A list of all images contained by pages in the download
	 */
	private ArrayList getAllImages() {
		Controller controller = Controller.getController();
		ArrayList imgList = new ArrayList();
		ArrayList nodeList = TreePanel.getTreePanel().getTree().getNodeList();
		
		/*Iterator it = nodeList.iterator();
		while ( it.hasNext() ) {
			Node node = (Node)it.next();
			
			Page page = node.getPageObject();
			if(page != null) {
				Vector imgListForPage = page.getImageList();
				Iterator imgIterator = imgListForPage.iterator();
				while ( imgIterator.hasNext() ) {     
					//ImageList is a class that stores the info about a single image - great name!
					imgList.add(imgIterator.next());
				}
			}
		}*/
		imgList.addAll(Controller.getController().getTree().getImages());
		return imgList;
	}       
	
	/**
	 * Thread controller class that starts off all other necessary threads for
	 * a tree download to occur.
	 */
	final class FullDownloadControllerThread implements Runnable {
		
		public void run() {
			Controller controller = Controller.getController();
			FileManager fileManager = controller.getFileManager();
			CheckNetConnection check = new CheckNetConnection();
			String fullFileName = fileManager.getDataPath() + controller.getFileName() + Controller.EXTENSION;
			
			if(check.isConnected() < 0) { // not connected
				//inform user that there is no net connection
				String NO_NET_CONNECTION = controller.getMsgString("NO_NET_CONNECTION");
				JOptionPane.showMessageDialog(
						controller.getTreeEditor(),
						NO_NET_CONNECTION,
						"Message Window",
						JOptionPane.INFORMATION_MESSAGE);
				done = true;
				return;
			}
			controller.openCancelWindow(this);
			String ST_DOWNLOADING_TREE = controller.getMsgString("ST_DOWNLOADING_TREE");
			controller.setStatusMessage(ST_DOWNLOADING_TREE);
			
			if(fromSearchWindow) {
				if(controller.getManagerFrame() != null) {
					controller.getManagerFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			}
			
			URL url = null;
            String CONTACTING_SERVER = controller.getMsgString("CONTACTING_SERVER");
            controller.setStatusMessage(CONTACTING_SERVER);
			try {
			    Hashtable args = new Hashtable();
			    args.put(RequestParameters.NODE_ID, nodeID + "");
			    args.put(RequestParameters.DEPTH, controller.getDepth() + "");
			    args.put(RequestParameters.VERBOSITY, "low");
			    args.put(RequestParameters.FROM_EDITOR, XMLConstants.ONE);
			    if (batchId != null) {
			        args.put(RequestParameters.BATCH_ID, batchId);
			    }
			    String urlString = HttpRequestMaker.getExternalUrlString(SERVER_PAGE_NAME, args); 
				url = new URL(urlString);                
				//System.out.println("url = " + url);
			} catch (Exception e) {e.printStackTrace();}
            String BUILDING_TREE = controller.getMsgString("BUILDING_TREE");
            controller.setStatusMessage(BUILDING_TREE);
			TreeGrowXMLReader xmlReader = new TreeGrowXMLReader(url, true);
			
			if (checkXMLError(xmlReader, true)) {
				int errorNum = xmlReader.getErrorNum();
				if (errorNum == 10102  || errorNum == 10103) { //locked or submitted
					//do nothing
				} else {                
					controller.getFileManager().checkIn();
				}
				return;
			}
			xmlReader.gatherContent();
			allNodes = xmlReader.getNodeList();
            String OPENING_TREE_EDITOR = controller.getMsgString("OPENING_TREE_EDITOR");
			controller.setStatusMessage(OPENING_TREE_EDITOR);
			if(allNodes == null || allNodes.size() == 0) {
				offerDownloadAgain();
				return;
			}
			
			controller.openTreeEditor(xmlReader, showTreeEditor);
			
			Tree tree = TreePanel.getTreePanel().getTree();
            
			createDataDirectory();
            FileXMLWriter writer = new FileXMLWriter();
            writer.writeXML();
            
            finishDownload();
            if (!controller.getPreferenceManager().hasOpenedFile()) {
                controller.showFirstTimeUsersDialog();
                controller.getPreferenceManager().setHasOpenedFile(true);
                controller.getPreferenceManager().writePreferencesToDisk();
            }
            
            if (StringUtils.notEmpty(controller.getObsoleteMessage())) {
            	controller.showObsoleteMessage();
            }
            
			if (controller.getTreeEditor() != null) {
				controller.getTreeEditor().setDefaultCursors();
			}
			done = true;
		}
		
		public void cancelDownload() {
			// Only do a check-in if the tree editor is visible.
			//
			// If the tree editor isn't visible, then this is a forced download
			// due to an undo last upload action.
			if (!dontCheckInOnCancel) {
				Controller.getController().getFileManager().checkIn();
			}
			done = true;
		}
		
	}
    
    private void createDataDirectory() {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        File newDir = new File(fileManager.getDataPath()+ controller.getFileName());
        if(newDir.mkdir() == false) {
            //try deleting the directory. it might already be present.
            fileManager.deleteImageFiles();
            //if it already exists, just use it.
            newDir.mkdir();
            if (newDir.exists() == false || newDir.canWrite() == false) {
                fileManager.checkIn();
                
                Vector argVec = new Vector(); 
                argVec.add(newDir);
                String IMG_DIR_ERROR = controller.getMsgString("IMG_DIR_ERROR", argVec);
                JOptionPane.showMessageDialog(
                        controller.getManagerFrame(),
                        IMG_DIR_ERROR,
                        "Message Window",
                        JOptionPane.INFORMATION_MESSAGE);
                done = true;
                return;
            }
        }        
    }
	
	
	/**
	 * Thread controller class that starts off all other necessary threads for
	 * a subtree download to occur.
	 */
	final class SubtreeDownloadControllerThread implements Runnable {               
		public void run() {
			Controller controller = Controller.getController();
			FileManager fileManager = controller.getFileManager();
			CheckNetConnection check = new CheckNetConnection();
			String fullFileName = fileManager.getDataPath() + controller.getFileName() + Controller.EXTENSION;
			
			if(check.isConnected() < 0) { // not connected
				//inform user that there is no net connection
				String NO_NET_CONNECTION = controller.getMsgString("NO_NET_CONNECTION");
				JOptionPane.showMessageDialog(
						controller.getTreeEditor(),
						NO_NET_CONNECTION,
						"Message Window",
						JOptionPane.INFORMATION_MESSAGE);
				done = true;
				return;
			}
			controller.openCancelWindow(this);
			String ST_DOWNLOADING_TREE = controller.getMsgString("ST_DOWNLOADING_SUBTREE");
			controller.setStatusMessage(ST_DOWNLOADING_TREE);
			
			//TreePanel.getTreePanel().setCursor(Cursor.WAIT_CURSOR);
			TreePanel treePanel = TreePanel.getTreePanel();
			Tree tree = treePanel.getTree();
			oldSubtreeRoot = tree.findNode(nodeID, tree.getNodeList());
			            
			URL url = null;
			try {
			    Hashtable args = new Hashtable();
			    args.put(RequestParameters.NODE_ID, "" + nodeID);
			    args.put(RequestParameters.DEPTH, "1");
			    args.put(RequestParameters.VERBOSITY, "low");
			    args.put(RequestParameters.FROM_EDITOR, XMLConstants.ONE);
			    args.put(RequestParameters.FETCH_SUBTREE, XMLConstants.ONE);
			    args.put(RequestParameters.DOWNLOAD_ID, "" + controller.getDownloadId());
			    if (batchId != null) {
			        args.put(RequestParameters.BATCH_ID, batchId);
			    }
			    String urlString = HttpRequestMaker.getExternalUrlString(SERVER_PAGE_NAME, args);
				url = new URL(urlString);
				System.out.println("opening subtree url: " + url);
			} catch (Exception e) {e.printStackTrace();}
			TreeGrowXMLReader xmlReader = new TreeGrowXMLReader(url, false);
			
			if (checkXMLError(xmlReader, true)) {
				int errorNum = xmlReader.getErrorNum();
				if (errorNum == 10102  || errorNum == 10103) { //locked or submitted
					String SUBTREE_CHECKED_OUT = controller.getMsgString("SUBTREE_CHECKED_OUT", new Vector(Arrays.asList(new String[] {xmlReader.getLockUser()})));
					JOptionPane.showMessageDialog(controller.getTreeEditor(), SUBTREE_CHECKED_OUT, "Subtree Checked Out", JOptionPane.ERROR_MESSAGE);
					oldSubtreeRoot.setLocked(true);
					oldSubtreeRoot.setLockUser(xmlReader.getLockUser());
				} else {                
					JOptionPane.showMessageDialog(controller.getTreeEditor(), xmlReader.getErrorString(), "Error during download", JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			Node rootParent = oldSubtreeRoot.getParent();
			int index = rootParent.indexOfChild(oldSubtreeRoot);
			rootParent.removeChild(oldSubtreeRoot);
			tree.removeNode(oldSubtreeRoot);
			TreePanelUpdateManager.getManager().zombieNode(oldSubtreeRoot);
			xmlReader.gatherContent();
			allNodes = xmlReader.getNodeList();
			newSubtreeRoot = (Node) allNodes.get(0);
			newSubtreeRoot.setCheckedOut(true);
			rootParent.addToChildren(index, newSubtreeRoot );
			Iterator it = allNodes.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				tree.addNode(node);
			}
			
			treePanel.rebuildTree();
			treePanel.addGrayCircles(allNodes);
			treePanel.repaint();
			
			/*get content for all newly checked out nodes
			HashSet toFetch = new HashSet(allNodes);
			toFetch.retainAll(tree.getCheckedOutNodes()) ;
			// Case where a terminal node has no children and hasn't yet gotten
			// it's page object, so it acts like it isn't checked out
			if (!toFetch.contains(newSubtreeRoot)) {
				toFetch.add(newSubtreeRoot);
			}
			//nodesToFetch = new ArrayList(toFetch);
			
			//controller.getTree().mergeImages(xmlReader.getImageList());            
			//gatherDataForTree();*/
			controller.mergeEditableContributors(xmlReader.getEditableContributors());
			controller.getTreeEditor().setToolbarEnabled();
			TreePanel.getTreePanel().rebuildTree();        
            finishDownload();
			done = true;
		}
		
		public void cancelDownload() {
			cancelDownload = true;
		}
	}    
	
    private void finishDownload() {
        Controller controller = Controller.getController();
        String ST_TRANSFER_COMPLETE = controller.getMsgString("ST_TRANSFER_COMPLETE");
        controller.setStatusMessage(ST_TRANSFER_COMPLETE);
        System.out.println("setting download complete to true");
        controller.setDownloadComplete(true);        
    }
    
	/**
	 * Class responsible for downloading the details for a given terminal node.
	 * This is necessary to get page information about a given node.
	 */
	final class NodeThread extends Thread { 
		private Node node = null;
		
		public NodeThread(Node n) {
			node = n;
		}
		
		public void run() {
			Controller controller = Controller.getController();
			
			String batchIdStr = batchId != null ? "&batch_id=" + batchId : "";
			try {
			    Hashtable args = new Hashtable();
			    args.put(RequestParameters.NODE_ID, "" + node.getId());
			    args.put(RequestParameters.DEPTH, XMLConstants.ZERO);
			    args.put(RequestParameters.VERBOSITY, "complete");
			    args.put(RequestParameters.DOWNLOAD_ID, "" + controller.getDownloadId());
			    args.put(RequestParameters.FROM_EDITOR, XMLConstants.ONE);
				URL url = new URL(HttpRequestMaker.getExternalUrlString(SERVER_PAGE_NAME, args));
				System.out.println("node thread url: " + url);
				TreeGrowXMLReader xmlReader = new TreeGrowXMLReader(url, node, false);
				System.out.println("created reader");
				if (checkXMLError(xmlReader, node, true)) {
					System.out.println("xml error");
					int errorNum = xmlReader.getErrorNum();
					if (errorNum == 10102  || errorNum == 10103) { //locked or submitted
						//do nothing
					} else {
						System.out.println("errorNum = " + errorNum + " error msg = " + xmlReader.getErrorString());
						if (errorNum == XMLReader.LOST_CONNECTION_MIDSTREAM) {
							synchronized(this) {
								String LOST_CONNECTION_MIDSTREAM_DOWNLOAD = Controller.getController().getMsgString("LOST_CONNECTION_MIDSTREAM_DOWNLOAD");
								JOptionPane.showMessageDialog(null, LOST_CONNECTION_MIDSTREAM_DOWNLOAD, "Lost connection", JOptionPane.ERROR_MESSAGE);
								System.exit(0);
							}
						}
						Integer timesFailed = (Integer) failedThreadsHashtable.get(node);
						if ( timesFailed == null ) {
							timesFailed = new Integer(1);
						} else {
							timesFailed = new Integer(timesFailed.intValue()+1);
						}
						
						if ( timesFailed.intValue() < 4) {
							failedThreadsStack.push(node);
							failedThreadsHashtable.put(node, timesFailed);
						} else {
							System.out.println("error mesg = " + xmlReader.getErrorString());
							offerDownloadAgain();
							//throw new RuntimeException("Exceeded number of attempts for node " + node);
						}
					}
					
					activeThreads--;
					return;
				}
				System.out.println("successfully downloaded");
				
				xmlReader.gatherContent();
				System.out.println("just gathered content");
				Controller.getController().getTree().mergeImages(xmlReader.getImageList());
				System.out.println("just did images");
				Controller.getController().mergeEditableContributors(xmlReader.getEditableContributors());                
				System.out.println("just did contributors");
				node.setDownloadComplete(true);
				System.out.println("just set complete");
				TreePanel.getTreePanel().setNodeComplete(node);
				downloadPercentage += fileIncrementPercentage;
				controller.setStatusMessage(downloadStatus + (int)downloadPercentage +"%");    
				
				activeThreads--;
				
				if(controller.getTreeEditor() == null) {
					return;
				}
				
			} catch(Exception error) {
				error.printStackTrace();
			}
		}
	}
}