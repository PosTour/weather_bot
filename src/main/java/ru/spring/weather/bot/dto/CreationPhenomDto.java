package ru.spring.weather.bot.dto;

public record CreationPhenomDto(
        String city,
        String type,
        long chatId
) {}
