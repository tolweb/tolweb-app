package org.tolweb.tapestry;

import java.util.Collection;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.tapestry.injections.MiscInjectable;

public abstract class SortableTable extends BaseComponent implements MiscInjectable {
	public abstract boolean getCanMove();
	@SuppressWarnings("unchecked")
    public abstract Collection getValues();
    public abstract IPrimaryKeyConvertor getConvertorParameter();
    public abstract int getIndex();
    public abstract int getMinMoveUpIndex();

    public boolean getCanMoveUp() {
        return getCanMove() && getReorderHelper().getCanMoveLeft(getValues(), getIndex(), getMinMoveUpIndex());
    }
    public boolean getCanMoveDown() {
        return getCanMove() && getReorderHelper().getCanMoveRight(getValues(), getIndex());        
    }
    public boolean getCanMoveBoth() {
        return getCanMove() && getReorderHelper().getCanMoveBoth(getValues(), getIndex(), getMinMoveUpIndex());
    }
    public boolean getCanMoveDownNotBoth() {
        return getCanMove() && 
            (!getReorderHelper().getCanMoveBoth(getValues(), getIndex(), getMinMoveUpIndex()) &&
                    (getReorderHelper().getCanMoveBoth(getValues(), getIndex(), 0)));
    }
    /**
     * Right now this is substituting for a listening on the loop iteration
     * because my listener was never getting called.
     * @return
     */
    public String getRowSpan() {
        String resultString = getCanMoveBoth() ? "2" : null;
        if (getBinding("rowSpanString") != null) {
        	getBinding("rowSpanString").setObject(resultString);        	
        }
        return resultString;
    }
    public IPrimaryKeyConvertor getConvertor() {
        if (getConvertorParameter() != null) {
            return getConvertorParameter();
        } else {
            return new OrderedObjectConvertor(getValues());
        }
    }
}
