package ru.spring.weather.bot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.spring.weather.bot.dto.CreationPhenomDto;
import ru.spring.weather.bot.dto.UserDto;
import ru.spring.weather.bot.dto.ViewPhenomDto;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "phenomFeignClient", url = "")
public interface PhenomFeignClient {
    @PostMapping(value = "")
    ResponseEntity<Void> signUp(UserDto userDto);

    @PostMapping(value = "")
    ResponseEntity<Void> addPhenom(CreationPhenomDto creationPhenomDto);

    @GetMapping(value = "")
    List<ViewPhenomDto> getAllPhenomsByChatId(@PathVariable("chat_id") long chatId);

    @DeleteMapping(value = "")
    ResponseEntity<Void> deletePhenom(@PathVariable UUID id);
}

