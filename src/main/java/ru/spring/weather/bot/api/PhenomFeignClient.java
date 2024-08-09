package ru.spring.weather.bot.api;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "phenomFeignClient", url = "")
public interface PhenomFeignClient {
}

