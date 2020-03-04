package com.github.venomousinc.homebrew.calendar;

import com.github.venomousinc.homebrew.calendar.data.CalendarEvent;
import com.github.venomousinc.homebrew.calendar.data.CalendarPair;
import com.github.venomousinc.homebrew.calendar.data.extra.DiscordEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Scanner;

/**
 * @author VenomousInc
 * @since 17/02/2020
 */
class CalendarCommandTest {

    private static final Logger LOGGER = LoggerFactory.getLogger( CalendarCommandTest.class );
    private static final long MOCK_GUILD_ID = 1L;
    private static final long MOCK_CHANNEL_ID = 2L;
    private static long mockMessageId = 0L;
    private static final long MOCK_AUTHOR_ID = 666L;
    public static void main(String[] args) {
        scannerTest();
    }

    public static CalendarEvent buildAsDiscord(final long guildId, final long channelId, final long messageId, final long authorId) {
        return new CalendarEvent().setData(
                new DiscordEventData().setGuildId(guildId).setChannelId(channelId).setOriginMessageId(messageId).setAuthorId(authorId)
        );
    }

    private static void scannerTest() {
        try(Scanner scanner = new Scanner(System.in)) {

            while(scanner.hasNext()) {
                if(scanner.hasNextInt()) {
                    CalendarEvent calendarEvent = buildAsDiscord(MOCK_GUILD_ID, MOCK_CHANNEL_ID, mockMessageId, MOCK_AUTHOR_ID);
                    calendarEvent.setStart(Instant.now().plusSeconds(scanner.nextInt()).toEpochMilli());
                    calendarEvent.save();

                    LOGGER.info("Event: {}\n{}", calendarEvent.toPrettyPrint(), MSGCalendar.buildDurationString(calendarEvent.getStartsIn()));
                    LOGGER.info("GetCalEvent: {}", MSGCalendar.getCalendarEvent(calendarEvent.getUniqueID()));
                    LOGGER.info("Data: {}", (calendarEvent.getData() instanceof DiscordEventData));
                    LOGGER.info("");
                    LOGGER.info("");
                    CalendarPair cp = MSGCalendar.deleteCalendarItem(calendarEvent.getUniqueID());
                    LOGGER.info("");
                    LOGGER.info("");
                    if(cp != null)
                        LOGGER.info("Deleted: E:{}, D:{}", cp.EVENT, cp.DAY);
                } else if(scanner.nextLine().equals("exit")) {
                    break;
                }
            }
            System.out.println("Goodbye.");
        }
    }

}