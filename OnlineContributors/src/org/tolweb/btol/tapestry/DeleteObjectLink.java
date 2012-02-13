package org.tolweb.btol.tapestry;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;

@ComponentClass
public abstract class DeleteObjectLink extends BaseComponent {
    @Parameter
    public abstract Object getIdToDelete();
}
