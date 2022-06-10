package com.github.venomousinc.homebrew.calendar.data;

import com.github.venomousinc.homebrew.calendar.data.CalendarDay;
import com.github.venomousinc.homebrew.calendar.data.CalendarEvent;

/**
 * A Paired Class, containing {@link #DAY} and {@link #EVENT}
 * Useful for returning the DAY that the EVENT is found in.
 *
 * @see CalendarDay
 * @see CalendarEvent
 * @author VenomousInc
 * @since 16/02/2020
 */
public class CalendarPair {

    public final CalendarDay DAY;
    public final CalendarEvent EVENT;

    public CalendarPair(CalendarDay day, CalendarEvent event) {
        DAY = day;
        EVENT = event;
    }

}