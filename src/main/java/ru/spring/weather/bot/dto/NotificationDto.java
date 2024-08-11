package ru.spring.weather.bot.dto;

public record NotificationDto(
    long chatId,
    String city,
    String phenomType
) {}
