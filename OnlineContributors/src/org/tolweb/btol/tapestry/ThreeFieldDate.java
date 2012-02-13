package org.tolweb.btol.tapestry;

import java.util.Calendar;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IForm;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.PropertySelection;
import org.tolweb.btol.tapestry.selection.BoundedIntPropertySelectionModel;

@ComponentClass(allowBody = false, allowInformalParameters = false)
public abstract class ThreeFieldDate extends BaseComponent implements IFormComponent {
	private static final int FIRST_YEAR = 1990;
	private IPropertySelectionModel yearModel;
	private IPropertySelectionModel monthModel;
	private IPropertySelectionModel dayModel;
	@Parameter
	public abstract Integer getYear();
	@Parameter
	public abstract Integer getMonth();
	@Parameter
	public abstract Integer getDay();
	@Parameter
	public abstract String getDisplayName();
	@InjectComponent("monthSelection")
	public abstract PropertySelection getMonthSelection();
	
	public IPropertySelectionModel getYearModel() {
		final int currentYear = Calendar.getInstance().get(Calendar.YEAR);		
		if (yearModel == null) {
			yearModel = new IPropertySelectionModel() { 
				public int getOptionCount() {
					return currentYear - FIRST_YEAR + 2;
				}
				public Object getOption(int arg0) {
					if (arg0 == 0) {
						return null;
					} else {
						return Integer.valueOf(getYearStringFromIndex(arg0));
					}
				}
				public String getLabel(int arg0) {
					return getYearStringFromIndex(arg0);
				}
				private String getYearStringFromIndex(int index) {
					if (index == 0) {
						return "";
					} else {
						return (currentYear - (index - 1)) + "";
					}
				}
				public String getValue(int arg0) {
					return "" + arg0;
				}
				public Object translateValue(String arg0) {
					return getOption(Integer.parseInt(arg0));
				}
			};
		}
		return yearModel;
	}
	public IPropertySelectionModel getMonthModel() {
		if (monthModel == null) {
			monthModel = new BoundedIntPropertySelectionModel(12, 1, true);
		}
		return monthModel;
	}
	public IPropertySelectionModel getDayModel() {
		if (dayModel == null) {
			dayModel = new BoundedIntPropertySelectionModel(31, 1, true);
		}
		return dayModel;
	}
    public IForm getForm() {
    	return getMonthSelection().getForm();
    }
    public String getName() {
    	return getMonthSelection().getName() + "container";
    }
    public void setName(String value) {}
    public boolean isDisabled() {
    	return false;
    }
    public String getClientId() {
    	return getMonthSelection().getName() + "container";
    }
    public boolean isRequired() {
    	return false;
    }
	
}
