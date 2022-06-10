package com.github.venomousinc.homebrew.calendar.data;

import com.fasterxml.jackson.annotation.*;
import com.github.venomousinc.homebrew.calendar.MSGCalendar;
import com.github.venomousinc.homebrew.calendar.data.extra.EventData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

/**
 * A Calendar Event Item, such as someones Birthday, or what time dinner is ready.
 * @author VenomousInc
 * @since 16/02/2020
 */
@JsonPropertyOrder({
        "UNIQUE_ID",
        "active",
        "CREATED_ON",
        "alert",
        "start",
        "end",
        "name",
        "description",
        "data"
})
public class CalendarEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger( CalendarEvent.class );

    @JsonProperty("UNIQUE_ID") private final String UNIQUE_ID;
    /**
     * Has this Calendar Event been previously pushed?
     */
    @JsonProperty("active") private boolean active = true;
    /**
     * The Epoch Millisecond that this event was created.
     */
    @JsonProperty("CREATED_ON") public final long CREATED_ON;
    /**
     * The Epoch Millisecond time to Alert the Calendar User(s)
     */
    @JsonProperty("alert") private long alert = -1;
    /**
     * The Epoch Millisecond time that the event starts
     */
    @JsonProperty("start") private long start = -1;
    /**
     * The Epoch Millisecond time that the event ends
     */
    @JsonProperty("end") private long end = -1;
    /**
     * The name of this Calendar Item, such as: "John Does Birthday"
     */
    @JsonProperty("name") private String name = null;
    /**
     * The description of this Calendar Event, such as: "Don't forget the Birthday Card!"
     */
    @JsonProperty("description") private String description = null;
    /**
     * Useful for storing extra data, such as Discord Guild, Channel, and Message ID.
     */
    @JsonProperty("data") private EventData data = null;

    public CalendarEvent() {
        this.CREATED_ON = System.currentTimeMillis();
        UNIQUE_ID = UUID.randomUUID().toString();
    }

    @JsonCreator
    public CalendarEvent(@JsonProperty("CREATED_ON") final long CREATED_ON, @JsonProperty("UNIQUE_ID") final String UNIQUE_ID) {
        this.CREATED_ON = CREATED_ON;
        this.UNIQUE_ID = UNIQUE_ID;
    }

    @JsonGetter("UNIQUE_ID")
    public String getUniqueID() {
        return UNIQUE_ID;
    }

    @JsonGetter("active")
    public boolean isActive() {
        return active;
    }

    @JsonIgnore
    public boolean isInactive() {
        return !active;
    }

    @JsonSetter("active")
    public CalendarEvent setActive(boolean active) {
        this.active = active;
        return this;
    }

    @JsonIgnore
    public long getAnnouncementTime() {
        return getAlert() != -1 ? getAlert() : getStart();
    }

    /**
     * Use {@link #getAnnouncementTime()} for safer fallback to {@link #getStart()}
     * @see #alert
     * @return alert (can be -1)
     */
    @JsonGetter("alert")
    public long getAlert() {
        return alert;
    }

    @JsonSetter("alert")
    public CalendarEvent setAlert(long alert) {
        this.alert = alert;
        if(getStart() == -1)
            setStart(alert);
        return this;
    }

    @JsonGetter("start")
    public long getStart() {
        return start;
    }

    @JsonSetter("start")
    public CalendarEvent setStart(long start) {
        this.start = start;
        if(getEnd() == -1)
            setEnd(start);
        return this;
    }

    @JsonGetter("end")
    public long getEnd() {
        return end;
    }

    @JsonSetter("end")
    public CalendarEvent setEnd(long end) {
        this.end = end;
        return this;
    }

    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonSetter("name")
    public CalendarEvent setName(String name) {
        this.name = name;
        return this;
    }

    @JsonGetter("description")
    public String getDescription() {
        return description;
    }

    @JsonSetter("description")
    public CalendarEvent setDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonGetter("data")
    public EventData getData() {
        return data;
    }

    @JsonSetter("data")
    public CalendarEvent setData(EventData data) {
        this.data = data;
        return this;
    }

    /**
     * Time between the event {@link #CREATED_ON} and the {@link #getAnnouncementTime()}
     * @return
     */
    @JsonIgnore public Duration getTimeBetween() {
        return Duration.ofMillis(Math.abs(CREATED_ON - getAnnouncementTime()));
    }

    /**
     * Time Until {@link #getAnnouncementTime()} not {@link #getStart()}
     * @return Time until the Announcement.
     */
    @JsonIgnore public Duration getTimeUntil() {
        return Duration.between(Instant.now(), Instant.ofEpochMilli(getAnnouncementTime()));
    }

    /**
     * From {@link Instant#now} until {@link #getStart()} Epoch Millis
     * @return how long until the event starts
     */
    @JsonIgnore public Duration getStartsIn() {
        return Duration.between(Instant.now(), Instant.ofEpochMilli(getStart()));
    }

    /**
     * From {@link Instant#now} until {@link #getEnd()} Epoch Millis
     * @return how long until the event ends
     */
    @JsonIgnore public Duration getEndsIn() {
        return Duration.between(Instant.now(), Instant.ofEpochMilli(getEnd()));
    }

    /**
     * The events length, aka
     * time between {@link #getStart()} and {@link #getEnd()}
     * @return The duration of the Event (start to end)
     */
    @JsonIgnore public Duration getDuration() {
        return Duration.ofMillis(Math.abs(getStart() - getEnd()));
    }

    /**
     * Save / Update this event. <b>Warning:</b> this will not remove old instances if the CalendarDay is no longer the same!
     * @see CalendarPair
     * @return {@link CalendarPair} or null
     */
    @JsonIgnore
    @Nullable
    public CalendarPair save() {
        CalendarDay calendarDay = MSGCalendar.getCalendarDay(getAnnouncementTime());
        if(calendarDay != null) {
            CalendarEvent calendarEvent = calendarDay.getEvent(this);

            if(calendarEvent != null) {
                calendarDay.getEvents().remove(calendarEvent);
            }

            calendarDay.getEvents().add(this);
            LOGGER.debug("{} Calendar Item: {}", calendarEvent == null ? "Adding" : "Updating", this.getUniqueID());
            calendarDay.save();
            return new CalendarPair(calendarDay, this);
        }
        return null;
    }

    public String toPrettyPrint() {
        return String.format(
                "# CalendarEvent #%n UNIQUE_ID: %s%n active: %s%n CREATED_ON: %s%n alert: %s%n start: %s%n end: %s%n name: %s%n description: %s%n data: %s", this.UNIQUE_ID, this.active, this.CREATED_ON, this.alert, this.start, this.end, this.name, this.description, this.data);
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "UNIQUE_ID='" + UNIQUE_ID + '\'' +
                ", active=" + active +
                ", CREATED_ON=" + CREATED_ON +
                ", alert=" + alert +
                ", start=" + start +
                ", end=" + end +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", data=" + data +
                '}';
    }
}
