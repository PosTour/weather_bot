package ru.spring.weather.bot.dto;

import java.util.UUID;

public record ViewPhenomDto(
        UUID id,
        String city,
        String type,
        long chatId
) {}
