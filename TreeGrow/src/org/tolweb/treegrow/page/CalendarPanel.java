/*
 * CalendarPanel.java
 *
 * Created on August 6, 2003, 12:55 PM
 */

package org.tolweb.treegrow.page;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * JPanel subclass used to show three dropdowns to select a date
 */
public class CalendarPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8655373366324764272L;
	public static final String NO_SELECTION_STRING = "-";
    private static Vector years, months, days;
    public JComboBox yearBox, monthBox, dayBox;

    static {
        years = new Vector();

        int currentYear = new GregorianCalendar().get(Calendar.YEAR);
        years.add(NO_SELECTION_STRING);
        for (int i = 1995; i <= currentYear + 1; i++) { 
            years.add("" + i);
        }

        months = new Vector(Arrays.asList(new DateFormatSymbols().getMonths()));
        months.remove(months.size() - 1);
        months.add(0, NO_SELECTION_STRING);
        
        days = new Vector();
        
        days.add(NO_SELECTION_STRING);
        for (int j = 1; j <= 31; j++) {
            days.add("" + j);
        }
    } 
    
    public CalendarPanel() {
        this(null);
    }
    
    public CalendarPanel(ActionListener listener) {
        setLayout(new BorderLayout());
        JPanel holderPanel = new JPanel();
        yearBox = new JComboBox(years);
        yearBox.setMaximumRowCount(13);
        monthBox = new JComboBox(months);
        monthBox.setMaximumRowCount(13);
        dayBox = new JComboBox(days);
        dayBox.setMaximumRowCount(13);
        if (listener != null) {
            yearBox.addActionListener(listener);
            monthBox.addActionListener(listener);
            dayBox.addActionListener(listener);
        }

        // Subject to change if a European coder comes along
        holderPanel.add(monthBox);
        holderPanel.add(dayBox);
        holderPanel.add(yearBox);
        add(BorderLayout.WEST, holderPanel);
    }

    public String getDateString() {
        String yearString = (String) yearBox.getSelectedItem();
        if (yearString.equals(NO_SELECTION_STRING)) {
            yearString = "0000";
        }

        String monthString = handleDayMonthStrings(months.indexOf(monthBox.getSelectedItem()) + "");

        String dayString = handleDayMonthStrings((String) dayBox.getSelectedItem());

        String returnString = yearString + NO_SELECTION_STRING + monthString + NO_SELECTION_STRING + dayString;
        return returnString;
    }
    
    public void setSelectedDate(String dateString) {
        if (dateString != null) {
            String year = dateString.substring(0, 4);
            String month = dateString.substring(5, 7);
            String day = dateString.substring(8);

            yearBox.setSelectedIndex(0);//force it to reset
            yearBox.setSelectedItem(year);

            int index = new Integer(month).intValue();
            monthBox.setSelectedIndex(index);

            index = new Integer(day).intValue();
            dayBox.setSelectedIndex(index);       
        }
    }

    private String handleDayMonthStrings(String value) {
        if (value.equals(NO_SELECTION_STRING)) {
            value = "00";
        } else {
            int intVal = new Integer(value).intValue();
            if (intVal < 10) {
                value = "0" + value;
            }
        }
        return value;
    }
    
    public static Vector getYears() {
        return years;
    }
}
