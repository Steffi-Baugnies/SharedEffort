package com.example.steff.sharedeffort;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.Dictionary;

public class DateHandler {

    public void/*Dictionary <String, Date>*/ getWeekDates() {
        LocalDate today = LocalDate.now();

        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }

        LocalDate sunday = today;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        System.out.println(today);
        System.out.println(monday);
        System.out.println(sunday);
    }
}
