package com.github.venomousinc.homebrew.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.venomousinc.homebrew.calendar.data.CalendarDay;
import com.github.venomousinc.homebrew.calendar.data.CalendarEvent;
import com.github.venomousinc.homebrew.calendar.data.CalendarPair;
import com.github.venomousinc.homebrew.calendar.data.extra.DefaultEventData;
import com.github.venomousinc.homebrew.calendar.data.extra.DiscordEventData;
import com.github.venomousinc.homebrew.calendar.data.extra.EventData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author VenomousInc
 * @since 16/02/2020
 */
public class MSGCalendar {

    /**
     * When creating a new Subtype of EventData please register it using:
     * {@link ObjectMapper#registerSubtypes(Class[])}
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerSubtypes(DiscordEventData.class, DefaultEventData.class, EventData.class);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger( MSGCalendar.class );

    public static void main(String[] args) {
        LOGGER.info("Testing Homebrew MSG Calendar");

        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setAlert(calendarEvent.CREATED_ON + 100000);
        calendarEvent.save();
        calendarEvent.setName("Updating");
        calendarEvent.setStart(Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli());
        calendarEvent.setEnd(Instant.now().plus(5, ChronoUnit.HOURS).toEpochMilli());
        calendarEvent.setData(new DiscordEventData().setMentionEveryone(true));
        calendarEvent.save();

        LOGGER.info(buildDurationString(calendarEvent.getTimeBetween(), '`'));
        LOGGER.info(buildDurationString(calendarEvent.getTimeUntil(), '`'));
        LOGGER.info(buildDurationString(calendarEvent.getStartsIn(), '`'));
        LOGGER.info(buildDurationString(calendarEvent.getDuration(), '`'));
    }

    @Nullable
    public static ArrayList<CalendarDay> getCalendarDays() {
        return CalendarDay.getCalendarDays();
    }

    @Nullable
    public static CalendarDay getCalendarDay(long epochMs) {
        LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.of("UTC"));
        LOGGER.debug("Getting Calendar Day from epoch MS: {} -> {}", epochMs, localDate.toString());
        return CalendarDay.of(localDate);
    }

    @Nullable
    public static CalendarPair getCalendarEvent(final String uniqueID) {
        ArrayList<CalendarDay> calendarDays = getCalendarDays();

        if(calendarDays != null && calendarDays.size() > 0) {
            final Optional<CalendarPair> optionalCalendarPair = calendarDays.stream()
                    .filter(calendarDay -> calendarDay.getEvent(uniqueID) != null)
                    .map(calendarDay -> new CalendarPair(calendarDay, calendarDay.getEvent(uniqueID)))
                    .findFirst();
            if(optionalCalendarPair.isPresent()) {
                return optionalCalendarPair.get();
            }
        }

        return null;
    }

    @Nullable
    public static CalendarPair deleteCalendarItem(final String uniqueID) {
        final CalendarPair calendarPair = getCalendarEvent(uniqueID);
        if(calendarPair != null && calendarPair.DAY.removeEvent(calendarPair.EVENT) != null) {
            return calendarPair;
        }
        return null;
    }

    /**
     * No surrounding Character for the Digits.
     *
     * @see #buildDurationString(Duration, Character)
     * @param duration A Duration of time
     * return Duration.ofSeconds(61) returns `1` Minute `1` Second
     */
    public static String buildDurationString(@NotNull Duration duration) {
        return buildDurationString(duration, null);
    }

    /**
     * Builds a String based on a duration. Supports: Year, Day, Hour, Second, Millisecond (if no other cases)
     * @param duration A Duration of time
     * @param surrounding E.G: `50` Seconds, null for no surrounding
     * @return Duration.ofSeconds(61) returns `1` Minute `1` Second
     */
    public static String buildDurationString(@NotNull Duration duration, @Nullable Character surrounding) {
        StringBuilder sb = new StringBuilder();

        long years = duration.toDays() / 365;
        long days = duration.toDays() % 365;
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        final String surroundFormat = "%c%s%c";

        if(years > 0) {
            sb.append(surrounding != null
                    ? String.format(surroundFormat, surrounding, years, surrounding)
                    : years).append(" Year").append(years > 1 ? "s " : ' ');
        }
        if(days > 0) {
            sb.append(surrounding != null
                    ? String.format(surroundFormat, surrounding, days, surrounding)
                    : days).append(" Day").append(days > 1 ? "s " : ' ');
        }
        if(hours > 0) {
            sb.append(surrounding != null
                    ? String.format(surroundFormat, surrounding, hours, surrounding)
                    : hours).append(" Hour").append(hours > 1 ? "s " : ' ');
        }
        if(minutes > 0) {
            sb.append(surrounding != null
                    ? String.format(surroundFormat, surrounding, minutes, surrounding)
                    : minutes).append(" Minute").append(minutes > 1 ? "s " : ' ');
        }
        if(seconds > 0) {
            sb.append(surrounding != null
                    ? String.format(surroundFormat, surrounding, seconds, surrounding)
                    : seconds).append(" Second").append(seconds > 1 ? "s " : ' ');
        }

        if(sb.length() == 0) {
            final long millis = duration.toMillis();
            sb.append(surrounding != null
                    ? String.format(surroundFormat, surrounding, millis, surrounding)
                    : millis).append(" Milliseconds");
        }

        return sb.toString().trim();
    }

}
