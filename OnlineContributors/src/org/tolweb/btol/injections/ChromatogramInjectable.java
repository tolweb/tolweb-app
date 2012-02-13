package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.ChromatogramBatchDAO;
import org.tolweb.btol.util.ChromatogramSearcher;

public interface ChromatogramInjectable {
	@InjectObject("spring:chromatogramBatchDAO")
	public ChromatogramBatchDAO getChromatogramBatchDAO();
	@InjectObject("spring:chromatogramSearcher")
	public ChromatogramSearcher getChromatogramSearcher();
}
