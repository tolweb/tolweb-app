package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Iterator;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.treegrow.main.NodeImage;

public class ImageSearchResultsComponent extends BaseComponent {
	@SuppressWarnings("unchecked")
    public Collection getImages() {
        return (Collection) PropertyUtils.read(getPage(), "images");
    }
    
    public String getEditWindowName() {
        return "editWindow";
    }
    
    @SuppressWarnings("unchecked")
    public IPrimaryKeyConvertor getConvertor() {
        return new IPrimaryKeyConvertor() {
            public Object getPrimaryKey(Object objValue) {
                return Integer.valueOf(((NodeImage) objValue).getId());
            }

            /**
             * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
             */
            public Object getValue(Object objPrimaryKey) {
                Integer order = (Integer) objPrimaryKey;
                for (Iterator iter = getImages().iterator(); iter.hasNext();) {
                    NodeImage img = (NodeImage) iter.next();
                    Integer nextId = Integer.valueOf(img.getId());
                    if (nextId.equals(order)) {
                        return img;
                    }
                }
                return null;
            } 

        };
    }
}
