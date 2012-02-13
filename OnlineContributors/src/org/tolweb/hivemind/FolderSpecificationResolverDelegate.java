package org.tolweb.hivemind;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.resolver.ISpecificationResolverDelegate;
import org.apache.tapestry.spec.ComponentSpecification;
import org.apache.tapestry.spec.IComponentSpecification;

public class FolderSpecificationResolverDelegate implements
		ISpecificationResolverDelegate {
	private String[] pagePaths;
	private List<Resource> pagesBaseLocations;
	private String[] componentPaths;
	private List<Resource> componentsBaseLocations;
	private boolean isInitialized = false;
	//private boolean isCacheDisabled; // not used in sample
	//private Resource pagesBaseLocation;
	//private Resource componentsBaseLocation;
	
	public FolderSpecificationResolverDelegate() {
		System.out.println("folder spec getting constructed");
	}

	/**
	 * @param componentPath
	 *            The componentPath to set.
	 */
	public void setComponentPaths(String componentPaths) {
		System.out.println("component paths set");
		this.componentPaths = componentPaths.split(",");
	}

	/**
	 * @param pagePath
	 *            The pagePath to set.
	 */
	public void setPagePaths(String pagePaths) {
		System.out.println("page paths set");		
		this.pagePaths = pagePaths.split(",");
	}

	/**
	 * @see org.apache.tapestry.resolver.ISpecificationResolverDelegate#findComponentSpecification(org.apache.tapestry.IRequestCycle,
	 *      org.apache.tapestry.INamespace, java.lang.String)
	 */
	public IComponentSpecification findComponentSpecification(
			IRequestCycle cycle, INamespace namespace, String type) {
		if (!isInitialized) {
			init(namespace);
		}
		if (namespace.isApplicationNamespace()) {
			for (Resource currentResource : componentsBaseLocations) {
				Resource componentResource = currentResource.getRelativeResource(type + ".jwc");
				if (componentResource != null) {
					cycle.getInfrastructure().getSpecificationSource().getComponentSpecification(componentResource);					
				}
			}
		}
		return null;
	}

	/**
	 * @see org.apache.tapestry.resolver.ISpecificationResolverDelegate#findPageSpecification(org.apache.tapestry.IRequestCycle,
	 *      org.apache.tapestry.INamespace, java.lang.String)
	 */
	public IComponentSpecification findPageSpecification(IRequestCycle cycle,
			INamespace namespace, String simplePageName) {
		if (!isInitialized) {
			init(namespace);
		}
		if (namespace.isApplicationNamespace()) {
			for (Resource currentResource : pagesBaseLocations) {
				Resource pageResource = currentResource.getRelativeResource(simplePageName + ".page");
				if (pageResource.getResourceURL() != null) {
					try {
						//cycle.getInfrastructure().getTemplateSource().
						IComponentSpecification spec = cycle.getInfrastructure().getSpecificationSource().getPageSpecification(pageResource);
						if (spec != null) {
							return spec;
						}
					} catch (Exception e) {}
				}
			}
		}
		Resource namespaceLocation = namespace.getSpecificationLocation();
		Resource templateResource = namespaceLocation.getRelativeResource(simplePageName + ".html");
		return setupImplicitPage(simplePageName, templateResource, namespaceLocation, true);
	}
	
	private IComponentSpecification setupImplicitPage(String simpleName,
			Resource resource, Resource namespaceLocation, boolean isPage) {
		String extension = isPage ? ".page" : ".jwc";
		Resource pageResource = namespaceLocation
				.getRelativeResource(simpleName + extension);

		IComponentSpecification specification = new ComponentSpecification();
		specification.setPageSpecification(isPage);
		specification.setSpecificationLocation(pageResource);
		specification.setLocation(new LocationImpl(resource));

		return specification;
	}	

	private void init(INamespace namespace) {
		/*isCacheDisabled = "true".equals(System
				.getProperty("org.apache.tapestry.disable-caching"));*/
		while (!namespace.isApplicationNamespace()) {
			namespace = namespace.getParentNamespace();
		}
		if (namespace.isApplicationNamespace()) {
			Resource namespaceLocation = namespace.getSpecificationLocation();
			pagesBaseLocations = new ArrayList<Resource>();
			componentsBaseLocations = new ArrayList<Resource>();
			for (String pagePath : pagePaths) {
				pagesBaseLocations.add(namespaceLocation.getRelativeResource(pagePath + "/"));
			}
			for (String componentPath : componentPaths) {
				componentsBaseLocations.add(namespaceLocation.getRelativeResource(componentPath + "/"));
			}
			isInitialized = true;
		}
	}
}
