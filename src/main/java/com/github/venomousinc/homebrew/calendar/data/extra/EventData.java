package com.github.venomousinc.homebrew.calendar.data.extra;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author VenomousInc
 * @since 16/02/2020
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface EventData {
}