package com.github.venomousinc.homebrew.calendar.data.extra;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.Nullable;

/**
 * @author VenomousInc
 * @since 16/02/2020
 */
@JsonPropertyOrder({
        "guildId",
        "channelId",
        "originMessageId",
        "authorId",
        "providedLink",
        "mentionEveryone"
})
public class DiscordEventData implements EventData {

    /**
     * The Discord Guild Snowflake/ID as a Long
     */
    @JsonProperty("guildId") private long guildId;
    /**
     * The Discord Channel Snowflake/ID as a Long
     */
    @JsonProperty("channelId") private long channelId;
    /**
     * The Discord Creators message Snowflake/ID as a Long
     */
    @JsonProperty("originMessageId") private long originMessageId;
    /**
     * The Discord Creator Snowflake/ID as a Long
     */
    @JsonProperty("authorId") private long authorId;
    /**
     * An optional User Provided URL
     */
    @Nullable
    @JsonProperty("providedLink")
    private String providedLink;
    /**
     * Should this event Mention @everyone?
     */
    @JsonProperty("mentionEveryone")
    private boolean mentionEveryone;

    @JsonGetter("guildId")
    public long getGuildId() {
        return guildId;
    }

    @JsonSetter("guildId")
    public DiscordEventData setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    @JsonGetter("channelId")
    public long getChannelId() {
        return channelId;
    }

    @JsonSetter("channelId")
    public DiscordEventData setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }

    @JsonGetter("originMessageId")
    public long getOriginMessageId() {
        return originMessageId;
    }

    @JsonSetter("originMessageId")
    public DiscordEventData setOriginMessageId(long originMessageId) {
        this.originMessageId = originMessageId;
        return this;
    }

    @JsonGetter("authorId")
    public long getAuthorId() {
        return authorId;
    }

    @JsonSetter("authorId")
    public DiscordEventData setAuthorId(long authorId) {
        this.authorId = authorId;
        return this;
    }

    @JsonGetter("providedLink")
    @Nullable
    public String getProvidedLink() {
        return providedLink;
    }

    @JsonSetter("providedLink")
    public DiscordEventData setProvidedLink(@Nullable String providedLink) {
        this.providedLink = providedLink;
        return this;
    }

    @JsonGetter("mentionEveryone")
    public boolean isMentionEveryone() {
        return mentionEveryone;
    }

    @JsonSetter("mentionEveryone")
    public DiscordEventData setMentionEveryone(boolean mentionEveryone) {
        this.mentionEveryone = mentionEveryone;
        return this;
    }
}
