package com.github.venomousinc.homebrew.calendar.data.extra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.jetbrains.annotations.Nullable;

/**
 * @author VenomousInc
 * @since 16/02/2020
 */
@JsonPropertyOrder({"providedLink"})
public class DefaultEventData {

    /**
     * An optional link, provided by the User
     */
    @Nullable
    @JsonProperty("providedLink")
    private String providedLink;

    @Nullable
    public String getProvidedLink() {
        return providedLink;
    }

    public void setProvidedLink(@Nullable String providedLink) {
        this.providedLink = providedLink;
    }
}
