package org.tolweb.hivemind;

import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.resolver.ISpecificationResolverDelegate;
import org.apache.tapestry.spec.ComponentSpecification;
import org.apache.tapestry.spec.IComponentSpecification;

public class AppSpecRelativeSpecificationResolverDelegate implements
		ISpecificationResolverDelegate {
	private String templateExtension;

	public IComponentSpecification findPageSpecification(IRequestCycle cycle,
			INamespace namespace, String simplePageName) {
		String templateName = simplePageName + getTemplateExtension();
		Resource namespaceLocation = namespace.getSpecificationLocation();
		Resource templateResource = namespaceLocation
				.getRelativeResource(templateName);
		if (templateResource.getResourceURL() != null) {
			return setupImplicitPage(simplePageName, templateResource,
					namespaceLocation, true);
		}

		return null;
	}

	public IComponentSpecification findComponentSpecification(
			IRequestCycle cycle, INamespace namespace, String type) {
		String templateName = type + getTemplateExtension();
		Resource namespaceLocation = namespace.getSpecificationLocation();
		Resource templateResource = namespaceLocation
				.getRelativeResource(templateName);
		if (templateResource.getResourceURL() != null) {
			return setupImplicitPage(type, templateResource,
					namespaceLocation, false);
		}
		return null;
	}

	private String getTemplateExtension() {
		return templateExtension;
	}

	private IComponentSpecification setupImplicitPage(String simpleName,
			Resource resource, Resource namespaceLocation, boolean isPage) {
		String extension = isPage ? ".page" : ".jwc"; 
		Resource pageResource = namespaceLocation
				.getRelativeResource(simpleName + extension);

		IComponentSpecification specification = new ComponentSpecification();
		specification.setPageSpecification(true);
		specification.setSpecificationLocation(pageResource);
		specification.setLocation(new LocationImpl(resource));

		return specification;
	}

	public void setTemplateExtension(String extension) {
		if (extension.charAt(0) != '.')
			extension = "." + extension;

		templateExtension = extension;
	}
}
