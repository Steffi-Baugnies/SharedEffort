package com.example.steff.sharedeffort;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateHandler {

    private String[] dayNames = new String[] {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    public Map<String, LocalDate> getWeekDates() {
        Map<String, LocalDate> weekdays = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today;

        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }

        for(int i = 0; i < 7; i++){
            weekdays.put(dayNames[i], monday.plusDays(i));
        }
        return weekdays;

    }

    public String[] getDayNames() {
        return dayNames;
    }

    public Map<String,LocalDate> getNextWeek(Map<String, LocalDate> currentWeek) {
        Map<String, LocalDate> nextWeek = new HashMap<>();
        for(int i = 0; i < currentWeek.size(); i++){
            nextWeek.put(dayNames[i], currentWeek.get(dayNames[i]).plusWeeks(1));
        }
        return nextWeek;
    }

    public Map<String,LocalDate> getPreviousWeek(Map<String, LocalDate> currentWeek) {
        Map<String, LocalDate> nextWeek = new HashMap<>();
        for(int i = 0; i < currentWeek.size(); i++){
            nextWeek.put(dayNames[i], currentWeek.get(dayNames[i]).minusWeeks(1));
        }
        return nextWeek;
    }
}
