package org.tolweb.presentation.helper;

/**
 * Models status information, or statistics, for pages 
 * (both branch and leaf page types) related to their 
 * status (Complete, Under Construction, Temporary) for 
 * each scope (Public or Working). 
 * 
 * In this context, "scope" is really referring to 
 * which database the page live in (Public vs. 
 * Working).  In other words, those in working are 
 * not published (made available to the public). 
 * 
 * Used by the PageDescendantStatusComponent
 * 
 * @see PageDescendantStatusComponent
 * 
 * @author lenards
 *
 */
public class PageStatusInfo {
	private PageScope publicScope;
	private PageScope workingScope;
	
	/**
	 * Constructs an instance of the class
	 */
	public PageStatusInfo() {
		publicScope = new PageScope();
		workingScope = new PageScope();
	}
	
	/**
	 * Gets the public page scope. 
	 * @return an instance of PageScope associated with the Public Scope
	 */
	public PageScope getPublicScope() {
		return publicScope;
	}

	/**
	 * Gets the working page scope. 
	 * @return an instance of PageScope associated with the Working Scope
	 */	
	public PageScope getWorkingScope() {
		return workingScope;
	}	
	
	/**
	 * Models the scope of the pages.  
	 * 
	 * Each scope has two types of pages that we 
	 * care about: Branch and Leaf. 
	 * 
	 * This is analogous to the database which the 
	 * page is stored (Public vs. Working). 
	 * 
	 * @author lenards
	 *
	 */
	public static class PageScope {
		private PageType branchPage;
		private PageType leafPage;
		
		/**
		 * Constructs an instance of the class.
		 */
		public PageScope() {
			branchPage = new PageType();
			leafPage = new PageType();
		}
		
		/**
		 * Gets statistics related to branch pages 
		 * for this scope
		 * @return a representation of the statistics 
		 * associated with pages of type branch page
		 */
		public PageType getBranchPage() {
			return branchPage;
		}
		
		/**
		 * gets statistics related to leaf pages for 
		 * this scope
		 * @return a representation of the statistics 
		 * associated with pages of type leaf page
		 */
		public PageType getLeafPage() {
			return leafPage;
		}
	}
	
	/**
	 * Models status statistics associated with a 
	 * page type. 
	 * 
	 * @author lenards
	 *
	 */
	public static class PageType {
		private int pageComplete;
		private int pageUnderConstruction;
		private int pageTemporary;
		
		/* default constructor utilized */ 
		
		/**
		 * Gets the number of pages with a status of complete
		 * @return an integer representing the number of pages 
		 * with complete status  
		 */
		public int getPageComplete() {
			return pageComplete;
		}
		
		/**
		 * Sets the number of pages with a status of complete 
		 * to be the argument 
		 * @param pageComplete number of pages with complete status
		 */
		public void setPageComplete(int pageComplete) {
			this.pageComplete = pageComplete;
		}
		
		/**
		 * Gets the number of pages with a status of under 
		 * construction
		 * @return an integer representing the number of 
		 * pages with under construction status
		 */
		public int getPageUnderConstruction() {
			return pageUnderConstruction;
		}
		
		/**
		 * Sets the number of pages with a status of under 
		 * construction to be the argument
		 * @param pageUnderConstruction number of pages with 
		 * under construction status
		 */
		public void setPageUnderConstruction(int pageUnderConstruction) {
			this.pageUnderConstruction = pageUnderConstruction;
		}
		
		/**
		 * Gets the number of pages with a status of temporary
		 * @return an integer representing the number of 
		 * pages with temporary status 
		 */
		public int getPageTemporary() {
			return pageTemporary;
		}
		
		/**
		 * Sets the number of pages with a status of temporary 
		 * to be the argument  
		 * @param pageTemporary number of pages with temporary
		 * status 
		 */
		public void setPageTemporary(int pageTemporary) {
			this.pageTemporary = pageTemporary;
		}
		
		/**
		 * Gets the total number of pages with all three statuses
		 * @return an integer representing the total number of pages 
		 */
		public int getPageTotal() {
			return pageComplete + pageUnderConstruction + pageTemporary;
		}
	}
}
