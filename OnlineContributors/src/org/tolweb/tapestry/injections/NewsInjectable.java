package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.FeatureGroupDAO;
import org.tolweb.dao.NewsItemDAO;

public interface NewsInjectable {
	@InjectObject("spring:newsItemDAO")
	public NewsItemDAO getNewsItemDAO();
	@InjectObject("spring:featureGroupDAO")
	public FeatureGroupDAO getFeatureGroupDAO();
}
