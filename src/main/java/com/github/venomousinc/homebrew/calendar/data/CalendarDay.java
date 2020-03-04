package com.github.venomousinc.homebrew.calendar.data;


import com.fasterxml.jackson.annotation.*;
import com.github.venomousinc.homebrew.calendar.MSGCalendar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A Calendar Day, such as 2020-46
 * @author VenomousInc
 * @since 16/02/2020
 */
@JsonPropertyOrder({
        "DAY_OF_YEAR",
        "YEAR",
        "EVENTS"
})
public class CalendarDay {

    private static final Logger LOGGER = LoggerFactory.getLogger( CalendarDay.class );

    public static final File CALENDAR_FOLDER = new File("calendar");

    static {
        LOGGER.debug("Calendar - Created: {} Exists: {}", CALENDAR_FOLDER.mkdirs(), CALENDAR_FOLDER.exists());
    }

    public static final String CALENDAR_FILE_EXTENSION = "msgcal";
    /**
     * <b>1970-01-01.msgcal</b>
     * @see LocalDate#toString()
     * @see #CALENDAR_FILE_EXTENSION
     */
    public static final String CALENDAR_FILE_FORMAT = "%s.%s";

    @JsonProperty("DAY_OF_YEAR")
    public final int DAY_OF_YEAR;
    @JsonProperty("YEAR")
    public final int YEAR;
    @JsonProperty("EVENTS")
    private final ArrayList<CalendarEvent> EVENTS;

    @JsonCreator
    public CalendarDay(@JsonProperty("DAY_OF_YEAR") final int dayOfYear, @JsonProperty("YEAR") final int year,
                       @JsonProperty("EVENTS") final ArrayList<CalendarEvent> events) {
        this.DAY_OF_YEAR = dayOfYear;
        this.YEAR = year;
        EVENTS = events != null ? events : new ArrayList<>();
    }

    @JsonGetter("EVENTS")
    public ArrayList<CalendarEvent> getEvents() {
        return EVENTS;
    }

    /**
     * Converts the {@link #DAY_OF_YEAR} and {@link #YEAR} to LocalDate
     *
     * @see LocalDate
     * @return LocalDate, which can toString() (1970-01-01) the output will be in the ISO-8601 format uuuu-MM-dd.
     */
    @JsonIgnore public LocalDate getDate() {
        return Year.of(YEAR).atDay(DAY_OF_YEAR);
    }

    @JsonIgnore
    public CalendarDay save() {
        final String dateStr = getDate().toString();
        if(CALENDAR_FOLDER.exists() || CALENDAR_FOLDER.mkdirs()) {
            try {
                MSGCalendar.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(getFile(), this);
                LOGGER.info("Saving Calendar Day: `{}`", dateStr);
                return this;
            } catch (IOException e) {
                LOGGER.error("Could not save Calendar Day: `{}`", dateStr);
                LOGGER.error("Exception occurred while saving!", e);
                return null;
            }
        }
        LOGGER.error("Calendar Day could not save: `{}` Calendar Folder: {}", dateStr, CALENDAR_FOLDER.toString());
        return null;
    }

    @JsonIgnore
    public File getFile() {
        return new File(CALENDAR_FOLDER, String.format(CALENDAR_FILE_FORMAT, getDate().toString(), CALENDAR_FILE_EXTENSION));
    }

    @Nullable
    public CalendarEvent getEvent(final String uniqueId) {
        return getEvents().stream()
                .filter(dayEvent -> dayEvent.getUniqueID().equals(uniqueId))
                .findFirst().orElse(null);
    }

    @Nullable
    public CalendarEvent getEvent(CalendarEvent calendarEvent) {
        return getEvent(calendarEvent.getUniqueID());
    }

    @Nullable
    public CalendarEvent removeEvent(CalendarEvent calendarEvent) {
        if(getEvents().remove(calendarEvent)) {
            save();
            return calendarEvent;
        }
        return null;
    }

    public static ArrayList<CalendarDay> getCalendarDays() {
        if(CALENDAR_FOLDER.isDirectory()) {
            final String[] calendarFileNames = CALENDAR_FOLDER.list();
            if(calendarFileNames != null && calendarFileNames.length > 0) {
                ArrayList<CalendarDay> calendarDays = Arrays.stream(calendarFileNames)
                        .map(s -> new File(CALENDAR_FOLDER, s))
                        .map(CalendarDay::of).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new));
                LOGGER.debug("Returning ArrayList of {} CalendarDays", calendarDays.size());
                return calendarDays;
            } else {
                LOGGER.debug("No CalendarDay Files found!");
            }
        } else {
            LOGGER.debug("No Calendar Folder found!\n{}", CALENDAR_FOLDER.getAbsolutePath());
        }
        LOGGER.info("No Calendar Folder!");
        return null;
    }

    @JsonIgnore
    public static File getFile(final String timestamp) {
        return new File(CALENDAR_FOLDER, String.format(CALENDAR_FILE_FORMAT, timestamp, CALENDAR_FILE_EXTENSION));
    }

    @JsonIgnore
    @Nullable
    public static CalendarDay of(@NotNull LocalDate dateTime) {
        CalendarDay calendarDay = of(getFile(dateTime.toString()));

        if(calendarDay != null)
            return calendarDay;

        return new CalendarDay(dateTime.getDayOfYear(), dateTime.getYear(), null).save();
    }

    @Nullable
    public static CalendarDay of(@NotNull File file) {
        LOGGER.debug("CalendarDay#of({})", file.toString());
        if(file.exists() && file.canRead() && file.isFile()) {
            try {
                return MSGCalendar.OBJECT_MAPPER.readValue(file, CalendarDay.class);
            } catch (IOException e) {
                LOGGER.error("Unable to process Calendar Day!", e);
            }
        }

        return null;
    }

}
