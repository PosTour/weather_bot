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

@FeignClient(value = "phenomFeignClient", url = "${url.weather}")
public interface PhenomFeignClient {
    @PostMapping(value = "/user/signup")
    ResponseEntity<Void> signUp(UserDto userDto);

    @PostMapping(value = "/phenom/add")
    ResponseEntity<Void> addPhenom(CreationPhenomDto creationPhenomDto);

    @GetMapping(value = "/phenom/all/by/{chat_id}")
    List<ViewPhenomDto> getAllPhenomsByChatId(@PathVariable ("chat_id") long chatId);

    @DeleteMapping(value = "/phenom/delete/{id}")
    ResponseEntity<Void> deletePhenom(@PathVariable ("id") UUID id);
}

