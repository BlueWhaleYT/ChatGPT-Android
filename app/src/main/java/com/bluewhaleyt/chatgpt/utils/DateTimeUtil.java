package com.bluewhaleyt.chatgpt.utils;

import java.util.Calendar;

public class DateTimeUtil {

    private Calendar calendar;
    private long time;

    public DateTimeUtil(Calendar calendar, long time) {
        this.calendar = calendar;
        this.time = time;
    }

    public boolean isToday() {
        return time == calendar.getTimeInMillis();
    }

    public boolean isYesterday() {
        var yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == yesterday.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH);
    }

    public boolean isCurrentYear() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return calendar.get(Calendar.YEAR) == currentYear;
    }

}
